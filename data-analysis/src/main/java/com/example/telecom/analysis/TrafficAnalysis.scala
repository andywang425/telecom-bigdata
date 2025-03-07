package com.example.telecom.analysis

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

object TrafficAnalysis {
  private val log = org.slf4j.LoggerFactory.getLogger(this.getClass)

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("SmsAnalysis")
      .enableHiveSupport()
      .getOrCreate()

    import spark.implicits._

    // Load Hive tables into DataFrames
    val trafficTable = spark.table("telecom_data.sms")

    val trafficDF = trafficTable.withColumn("year", year($"sessionStartTime"))
      .withColumn("month", month($"sessionStartTime"))
      .withColumn("day", day($"sessionStartTime"))
      .withColumn("hour", hour($"sessionStartTime"))

    // 1. 按月会话数量、持续时间、上行流量、下行流量统计
    val monthlySessionSummary = trafficDF
      .groupBy($"year", $"month")
      .agg(
        count("*").alias("total_sessions"),
        sum($"sessionDurationMillis").alias("total_duration"),
        sum($"upstreamDataVolume").alias("total_upstream"),
        sum($"downstreamDataVolume").alias("total_downstream")
      )

    // 2. 按月按用户会话数量、持续时间、上行流量、下行流量统计
    val monthlyUserTrafficSummary = trafficDF
      .groupBy(
        $"year", $"month", $"userNumber",
      )
      .agg(
        count("*").alias("total_sessions"),
        sum($"sessionDurationMillis").alias("total_duration"),
        sum($"upstreamDataVolume").alias("total_upstream"),
        sum($"downstreamDataVolume").alias("total_downstream")
      )

    // 3. 按月统计每种会话状态的数量
    val sessionCountByAppType = trafficDF
      .groupBy($"year", $"month", $"applicationType")
      .agg(count("*").alias("sessionCount"))

    // 4. 按月统计花费在不同种类应用上的上行/下行流量
    val trafficByAppType = trafficDF
      .groupBy($"year", $"month", $"applicationType")
      .agg(
        sum("upstreamDataVolume").alias("totalUpstreamDataVolume"),
        sum("downstreamDataVolume").alias("totalDownstreamDataVolume")
      )

    // 5. 按月统计计算花费在不同网络技术上的上行/下行流量
    val trafficByNetworkTech = trafficDF
      .groupBy($"year", $"month", $"networkTechnology")
      .agg(
        sum("upstreamDataVolume").alias("totalUpstreamDataVolume"),
        sum("downstreamDataVolume").alias("totalDownstreamDataVolume")
      )

    // 6. 计算每个月，一天中每个小时的总上行/下行流量（流量在一天内的分布情况）
    val trafficByMonthHour = trafficDF
      .groupBy($"year", $"month", $"hour")
      .agg(
        sum("upstreamDataVolume").alias("totalUpstreamDataVolume"),
        sum("downstreamDataVolume").alias("totalDownstreamDataVolume")
      )

    spark.stop()
  }
}
