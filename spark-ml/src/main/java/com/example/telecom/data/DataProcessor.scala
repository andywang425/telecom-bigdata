package com.example.telecom.data

import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, SparkSession}

object DataProcessor {
  // 通用时间分桶方法
  private def addTimeBucket(timeCol: String)(df: DataFrame): DataFrame = {
    df.withColumn("hour", hour(col(timeCol)))
  }

  // 通用聚合方法
  private def aggregateUsage(spark: SparkSession, dataType: String)(df: DataFrame): DataFrame = {
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

  // 通话数据处理
  def processCallData(implicit spark: SparkSession): DataFrame = {
    import spark.implicits._

    spark.table("telecom_data.call")
      .filter($"callDirection" === "OUTGOING")
      .transform(addTimeBucket("callStartTime"))
      .withColumnRenamed("callerNumber", "phone")
      .transform(aggregateUsage(spark, "call"))
  }

  // 短信数据处理
  def processSmsData(implicit spark: SparkSession): DataFrame = {
    import spark.implicits._

    spark.table("telecom_data.sms")
      .filter($"sendDirection" === "SENT")
      .transform(addTimeBucket("sendTime"))
      .withColumnRenamed("senderNumber", "phone")
      .transform(aggregateUsage(spark, "sms"))
  }

  // 流量数据处理（无需过滤）
  def processTrafficData(implicit spark: SparkSession): DataFrame = {
    spark.table("telecom_data.traffic")
      .transform(addTimeBucket("sessionStartTime"))
      .withColumnRenamed("userNumber", "phone")
      .transform(aggregateUsage(spark, "session"))
  }

  // 通用数据合并方法
  def mergeDataFrames(dataFrames: DataFrame*): DataFrame = {
    dataFrames.reduce((df1, df2) =>
      df1.join(df2, Seq("phone"), "outer")
    ).na.fill(0)
  }
}
