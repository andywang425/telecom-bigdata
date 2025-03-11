package com.example.telecom.analysis

import com.example.telecom.utils.MyLogger
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, SparkSession}

object TrafficAnalysis extends MyLogger {
  def run(spark: SparkSession, trafficDF: DataFrame): Unit = {
    import spark.implicits._

    // 1. 按月会话数量、持续时间、上行流量、下行流量统计
    val monthlySessionSummary = trafficDF
      .groupBy($"year", $"month")
      .agg(
        count($"sessionId").alias("total_sessions"),
        sum($"sessionDurationMillis").alias("total_duration"),
        sum($"upstreamDataVolume").alias("total_upstream"),
        sum($"downstreamDataVolume").alias("total_downstream")
      )
      .orderBy($"year", $"month")
    info("Monthly session count, duration and data volume summary")
    monthlySessionSummary.show(1024, truncate = false)

    // 2. 按月按用户会话数量、持续时间、上行流量、下行流量统计
    val monthlyUserTrafficSummary = trafficDF
      .groupBy($"year", $"month", $"userNumber")
      .agg(
        count($"sessionId").alias("total_sessions"),
        sum($"sessionDurationMillis").alias("total_duration"),
        sum($"upstreamDataVolume").alias("total_upstream"),
        sum($"downstreamDataVolume").alias("total_downstream")
      )
      .orderBy($"year", $"month", $"userNumber")
    info("Monthly session count, duration and data volume per user summary")
    monthlyUserTrafficSummary.show(1024, truncate = false)

    // 3. 按月统计不同种类应用的会话数量和上行/下行流量
    val monthlyDataVolumeByAppType = trafficDF
      .groupBy($"year", $"month", $"applicationType")
      .agg(
        count($"sessionId").alias("session_count"),
        sum($"upstreamDataVolume").alias("total_upstream_data_volume"),
        sum($"downstreamDataVolume").alias("total_downstream_data_volume")
      )
      .orderBy($"year", $"month", $"applicationType")
    info("Monthly session count and data volume summary per APP summary")
    monthlyDataVolumeByAppType.show(1024, truncate = false)

    // 4. 按月统计不同网络技术上的会话数量和上行/下行流量
    val trafficByNetworkTech = trafficDF
      .groupBy($"year", $"month", $"networkTechnology")
      .agg(
        count($"sessionId").alias("session_count"),
        sum($"upstreamDataVolume").alias("total_upstream_data_volume"),
        sum($"downstreamDataVolume").alias("total_downstream_data_volume")
      )
      .orderBy($"year", $"month", $"networkTechnology")
    info("Monthly session count and data volume per network technology summary")
    trafficByNetworkTech.show(1024, truncate = false)

    // 5. 计算每个月，一天中每个小时的会话数量和总上行/下行流量（流量在一天内的分布情况）
    val hourlyTrafficDistributionByStation = trafficDF
      .groupBy($"year", $"month", $"hour")
      .agg(
        count($"sessionId").alias("session_count"),
        sum($"upstreamDataVolume").alias("total_upstream_data_volume"),
        sum($"downstreamDataVolume").alias("total_downstream_data_volume")
      )
      .orderBy($"year", $"month", $"hour")
    info("Monthly (and hourly) traffic day distribution per station")
    hourlyTrafficDistributionByStation.show(1024, truncate = false)
  }
}
