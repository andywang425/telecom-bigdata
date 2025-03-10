package com.example.telecom.analysis

import com.example.telecom.utils.{MyLogger, SparkUtils}
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._

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
    info("Monthly session summary")
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
    info("Monthly user traffic summary")
    monthlyUserTrafficSummary.show(1024, truncate = false)

    // 3. 按月统计每种会话状态的数量
    val sessionCountByAppType = trafficDF
      .groupBy($"year", $"month", $"applicationType")
      .agg(count($"sessionId").alias("sessionCount"))
      .orderBy($"year", $"month", $"applicationType")
    info("按月统计每种会话状态的数量")
    sessionCountByAppType.show(1024, truncate = false)

    // 4. 按月统计花费在不同种类应用上的上行/下行流量
    val trafficByAppType = trafficDF
      .groupBy($"year", $"month", $"applicationType")
      .agg(
        sum($"upstreamDataVolume").alias("total_upstream_data_volume"),
        sum($"downstreamDataVolume").alias("total_downstream_data_volume")
      )
      .orderBy($"year", $"month", $"applicationType")
    info("Traffic summary by APP type")
    trafficByAppType.show(1024, truncate = false)

    // 5. 按月统计计算花费在不同网络技术上的上行/下行流量
    val trafficByNetworkTech = trafficDF
      .groupBy($"year", $"month", $"networkTechnology")
      .agg(
        sum($"upstreamDataVolume").alias("total_upstream_data_volume"),
        sum($"downstreamDataVolume").alias("total_downstream_data_volume")
      )
      .orderBy($"year", $"month", $"networkTechnology")
    info("Traffic summary by network technology")
    trafficByNetworkTech.show(1024, truncate = false)

    // 6. 计算每个月，一天中每个小时的总上行/下行流量（流量在一天内的分布情况）
    val trafficByMonthHour = trafficDF
      .groupBy($"year", $"month", $"hour")
      .agg(
        sum($"upstreamDataVolume").alias("total_upstream_data_volume"),
        sum($"downstreamDataVolume").alias("total_downstream_data_volume")
      )
      .orderBy($"year", $"month", $"hour")
    info("Monthly traffic day distribution per hour")
    trafficByMonthHour.show(1024, truncate = false)
  }
}
