package com.example.telecom.data

import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, SparkSession}

object DataProcessor {
  /**
   * 添加时间分桶列（小时）
   *
   * @param timeCol 时间列（TIMESTAMP）
   * @param df      DataFrame
   */
  private def addTimeBucket(timeCol: String)(df: DataFrame): DataFrame = {
    df.withColumn("hour", hour(col(timeCol)))
  }

  /**
   * 按用户统计各种电信数据在一天中三个时段内的分布
   *
   * @param dataType 电信数据类型（call, sms, traffic）
   * @param df       电信数据 DataFrame
   */
  private def aggregateUsage(dataType: String)(df: DataFrame)(implicit spark: SparkSession): DataFrame = {
    import spark.implicits._

    val cols = List((6, 12), (12, 20), (20, 6)).zipWithIndex.map {
      case ((start, end), idx) =>
        sum(when(
          if (end > start) $"hour" >= start && $"hour" < end
          else $"hour" >= start || $"hour" < end, 1
        ).otherwise(0)).alias(s"${dataType}_count_${idx + 1}")
    }
    df.groupBy("phone").agg(cols.head, cols.tail: _*)
  }

  /**
   * 通话数据处理
   */
  private def processCallData(implicit spark: SparkSession): DataFrame = {
    import spark.implicits._

    spark.table("telecom_data.call")
      .filter($"call_direction" === "OUTGOING")
      .transform(addTimeBucket("call_start_time"))
      .withColumnRenamed("caller_number", "phone")
      .transform(aggregateUsage("call"))
  }

  /**
   * 短信数据处理
   */
  private def processSmsData(implicit spark: SparkSession): DataFrame = {
    import spark.implicits._

    spark.table("telecom_data.sms")
      .filter($"send_direction" === "SENT")
      .transform(addTimeBucket("send_time"))
      .withColumnRenamed("sender_number", "phone")
      .transform(aggregateUsage("sms"))
  }

  /**
   * 流量数据处理
   */
  private def processTrafficData(implicit spark: SparkSession): DataFrame = {
    spark.table("telecom_data.traffic")
      .transform(addTimeBucket("session_start_time"))
      .withColumnRenamed("user_number", "phone")
      .transform(aggregateUsage("session"))
  }

  /**
   * 使用 phone 列连接多个 DataFrame
   */
  private def mergeDataFrames(dataFrames: DataFrame*): DataFrame = {
    dataFrames.reduce((df1, df2) =>
      df1.join(df2, "phone", "outer")
    ).na.fill(0)
  }

  /**
   * 读取并处理通话、短信和流量数据
   *
   * @return 合并后的 DataFrame
   */
  def run(implicit spark: SparkSession): DataFrame = {
    val callData = processCallData
    val smsData = processSmsData
    val trafficData = processTrafficData

    mergeDataFrames(callData, smsData, trafficData)
  }
}
