package com.example.telecom.analysis

import com.example.telecom.utils.{MyLogger, SparkUtils}
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._

object StationAnalysis extends MyLogger {
  def run(spark: SparkSession, callDF: DataFrame, smsDF: DataFrame, trafficDF: DataFrame): Unit = {
    import spark.implicits._

    // 1. 基站的通话和短信故障率
    // 通话故障率
    val callFailures = callDF
      .groupBy($"year", $"month", $"stationId")
      .agg(
        count($"callId").alias("total_call"),
        sum(when($"callStatus" === "FAILED", 1).otherwise(0)).alias("failed_call")
      )
      .withColumn("call_failure_rate", $"failed_call" / $"total_call")
      .orderBy($"year", $"month", $"stationId")
    info("Call Failure Rate Per Base Station")
    callFailures.show(1024, truncate = false)

    // 短信故障率
    val smsFailures = smsDF
      .groupBy($"year", $"month", $"stationId")
      .agg(
        count($"smsId").alias("total_sms"),
        sum(when($"sendStatus".isin("FAILED_TO_SEND", "FAILED_TO_RECEIVE"), 1).otherwise(0)).alias("failed_sms")
      )
      .withColumn("sms_failure_rate", $"failed_sms" / $"total_sms")
      .orderBy($"year", $"month", $"stationId")
    info("SMS Failure Rate Per Base Station")
    smsFailures.show(1024, truncate = false)

    // 2. 以月为单位计算每个基站的通话数量和通话时长
    val baseStationCallStats = callDF
      .groupBy($"year", $"month", $"stationId")
      .agg(
        count($"callId").alias("call_count"),
        sum("callDurationMillis").alias("total_call_duration_millis")
      )
      .orderBy($"year", $"month", $"stationId")
    info("Base Station Call count and duration summary")
    baseStationCallStats.show(1024, truncate = false)

    // 3. 以月为单位计算每个基站的短信数量和短信内容长度
    val baseStationSmsStats = smsDF
      .groupBy($"year", $"month", $"stationId")
      .agg(
        count($"smsId").alias("sms_count"),
        sum(length($"smsContent")).alias("total_sms_content_length")
      )
      .orderBy($"year", $"month", $"stationId")
    info("Base Station SMS count and length summary")
    baseStationSmsStats.show(1024, truncate = false)

    // 4. 以月为单位计算每个基站的会话数量和上行/下行流量
    val baseStationTrafficStats = trafficDF
      .groupBy($"year", $"month", $"stationId")
      .agg(
        count($"sessionId").alias("sessionCount"),
        sum($"upstreamDataVolume").alias("total_upstream_data_volume"),
        sum($"downstreamDataVolume").alias("total_downstream_data_volume")
      )
      .orderBy($"year", $"month", $"stationId")
    info("Base Station Traffic count and data volume summary")
    baseStationTrafficStats.show(1024, truncate = false)
  }
}
