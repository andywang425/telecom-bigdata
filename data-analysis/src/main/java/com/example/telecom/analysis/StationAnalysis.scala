package com.example.telecom.analysis

import com.example.telecom.utils.{MyLogger, SparkUtils}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, SparkSession}

object StationAnalysis extends MyLogger {
  def run(callDF: DataFrame, smsDF: DataFrame, trafficDF: DataFrame)(implicit spark: SparkSession): Unit = {
    import spark.implicits._

    // 1. 基站的通话和短信故障率
    // 通话故障率
    val callFailures = callDF
      .groupBy($"year", $"month", $"station_id")
      .agg(
        count($"call_id").alias("total_call"),
        sum(when($"call_status" === "FAILED", 1).otherwise(0)).alias("failed_call")
      )
      .withColumn("call_failure_rate", $"failed_call" / $"total_call")
      .orderBy($"year", $"month", $"station_id")
    info("Base Station Call Failure Rate Summary")
    callFailures.show(64, truncate = false)
    SparkUtils.saveToMySQL(callFailures, "station_call_failure_rate")
    callFailures.unpersist()

    // 短信故障率
    val smsFailures = smsDF
      .groupBy($"year", $"month", $"station_id")
      .agg(
        count($"sms_id").alias("total_sms"),
        sum(when($"send_status".isin("FAILED_TO_SEND", "FAILED_TO_RECEIVE"), 1).otherwise(0)).alias("failed_sms")
      )
      .withColumn("sms_failure_rate", $"failed_sms" / $"total_sms")
      .orderBy($"year", $"month", $"station_id")
    info("Base Station SMS Failure Rate Summary")
    smsFailures.show(64, truncate = false)
    SparkUtils.saveToMySQL(smsFailures, "station_sms_failure_rate")
    smsFailures.unpersist()

    // 2. 以月为单位计算每个基站的通话数量和通话时长
    val baseStationCallStats = callDF
      .groupBy($"year", $"month", $"station_id")
      .agg(
        count($"call_id").alias("call_count"),
        sum("call_duration_millis").alias("total_call_duration_millis")
      )
      .orderBy($"year", $"month", $"station_id")
    info("Base Station Call Summary")
    baseStationCallStats.show(64, truncate = false)
    SparkUtils.saveToMySQL(baseStationCallStats, "station_call_stats")
    baseStationCallStats.unpersist()

    // 3. 以月为单位计算每个基站的短信数量和短信内容长度
    val baseStationSmsStats = smsDF
      .groupBy($"year", $"month", $"station_id")
      .agg(
        count($"sms_id").alias("sms_count"),
        sum(length($"sms_content")).alias("total_sms_content_length")
      )
      .orderBy($"year", $"month", $"station_id")
    info("Base Station SMS Summary")
    baseStationSmsStats.show(64, truncate = false)
    SparkUtils.saveToMySQL(baseStationSmsStats, "station_sms_stats")
    baseStationSmsStats.unpersist()

    // 4. 以月为单位计算每个基站的会话数量和上行/下行流量
    val baseStationTrafficStats = trafficDF
      .groupBy($"year", $"month", $"station_id")
      .agg(
        count($"session_id").alias("session_count"),
        sum($"upstream_data_volume").alias("total_upstream_data_volume"),
        sum($"downstream_data_volume").alias("total_downstream_data_volume")
      )
      .orderBy($"year", $"month", $"station_id")
    info("Base Station Traffic Summary")
    baseStationTrafficStats.show(64, truncate = false)
    SparkUtils.saveToMySQL(baseStationTrafficStats, "station_traffic_stats")
    baseStationTrafficStats.unpersist()
  }
}
