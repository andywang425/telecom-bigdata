package com.example.telecom.analysis

import com.example.telecom.utils.{MyLogger, SparkUtils}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, SparkSession}

object TrafficAnalysis extends MyLogger {
  def run(trafficDF: DataFrame)(implicit spark: SparkSession): Unit = {
    import spark.implicits._

    // 1. 按月会话数量、持续时间、上行流量、下行流量统计
    val monthlyTrafficSummary = trafficDF
      .groupBy($"year", $"month")
      .agg(
        count($"session_id").alias("total_sessions"),
        sum($"session_duration_millis").alias("total_duration"),
        sum($"upstream_data_volume").alias("total_upstream"),
        sum($"downstream_data_volume").alias("total_downstream")
      )
      .orderBy($"year", $"month")
    info("Monthly session count, duration and data volume summary")
    monthlyTrafficSummary.show(64, truncate = false)
    SparkUtils.saveToMySQL(monthlyTrafficSummary, "traffic_summary")
    monthlyTrafficSummary.unpersist()

    // 2. 按月按用户会话数量、持续时间、上行流量、下行流量统计
    val monthlyUserTrafficSummary = trafficDF
      .groupBy($"year", $"month", $"user_number")
      .agg(
        count($"session_id").alias("total_sessions"),
        sum($"session_duration_millis").alias("total_duration"),
        sum($"upstream_data_volume").alias("total_upstream"),
        sum($"downstream_data_volume").alias("total_downstream")
      )
      .orderBy($"year", $"month", $"user_number")
    info("Monthly session count, duration and data volume per user summary")
    monthlyUserTrafficSummary.show(64, truncate = false)
    SparkUtils.saveToMySQL(monthlyUserTrafficSummary, "traffic_user")
    monthlyUserTrafficSummary.unpersist()

    // 3. 按月统计不同种类应用的会话数量和上行/下行流量
    val monthlyTafficByAppType = trafficDF
      .groupBy($"year", $"month", $"application_type")
      .agg(
        count($"session_id").alias("session_count"),
        sum($"upstream_data_volume").alias("total_upstream_data_volume"),
        sum($"downstream_data_volume").alias("total_downstream_data_volume")
      )
      .orderBy($"year", $"month", $"application_type")
    info("Monthly session count and data volume summary per APP summary")
    monthlyTafficByAppType.show(64, truncate = false)
    SparkUtils.saveToMySQL(monthlyTafficByAppType, "traffic_by_app")
    monthlyTafficByAppType.unpersist()

    // 4. 按月统计不同网络技术上的会话数量和上行/下行流量
    val trafficByNetworkTech = trafficDF
      .groupBy($"year", $"month", $"network_technology")
      .agg(
        count($"session_id").alias("session_count"),
        sum($"upstream_data_volume").alias("total_upstream_data_volume"),
        sum($"downstream_data_volume").alias("total_downstream_data_volume")
      )
      .orderBy($"year", $"month", $"network_technology")
    info("Monthly session count and data volume per network technology summary")
    trafficByNetworkTech.show(64, truncate = false)
    SparkUtils.saveToMySQL(trafficByNetworkTech, "traffic_by_network_tech")
    trafficByNetworkTech.unpersist()

    // 5. 计算每个月，一天中每个小时的会话数量和总上行/下行流量（流量在一天内的分布情况）
    val hourlyTrafficDistribution = trafficDF
      .groupBy($"year", $"month", $"hour")
      .agg(
        count($"session_id").alias("session_count"),
        sum($"upstream_data_volume").alias("total_upstream_data_volume"),
        sum($"downstream_data_volume").alias("total_downstream_data_volume")
      )
      .orderBy($"year", $"month", $"hour")
    info("Monthly (and hourly) traffic day distribution")
    hourlyTrafficDistribution.show(64, truncate = false)
    SparkUtils.saveToMySQL(hourlyTrafficDistribution, "traffic_day_distribution")
    hourlyTrafficDistribution.unpersist()
  }
}
