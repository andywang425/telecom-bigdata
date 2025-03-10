package com.example.telecom.analysis

import com.example.telecom.utils.{MyLogger, SparkUtils}
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._

object StationAnalysis extends MyLogger {
  def run(spark: SparkSession, callDF: DataFrame, smsDF: DataFrame, trafficDF: DataFrame): Unit = {
    import spark.implicits._

    // 1. 基站的通话和短信故障率
    // A. Calculate call failure rate per base station (failed when callStatus = 'FAILED')
    val callFailures = callDF
      .groupBy("year", "month", "stationId")
      .agg(
        count($"callId").alias("totalCalls"),
        sum(when($"callStatus" === "FAILED", 1).otherwise(0)).alias("failedCalls")
      )
      .withColumn("callFailureRate", $"failedCalls" / $"totalCalls")
      .orderBy("year", "month", "stationId")
    println("Call Failure Rate Per Base Station:")
    callFailures.show()

    // B. Calculate SMS failure rate per base station
    // (failed when sendStatus in ('FAILED_TO_SEND', 'FAILED_TO_RECEIVE'))
    val smsFailures = smsDF
      .groupBy("year", "month", "stationId")
      .agg(
        count($"smsId").alias("totalSms"),
        sum(when($"sendStatus".isin("FAILED_TO_SEND", "FAILED_TO_RECEIVE"), 1).otherwise(0)).alias("failedSms")
      )
      .withColumn("smsFailureRate", $"failedSms" / $"totalSms")
      .orderBy("year", "month", "stationId")
    println("SMS Failure Rate Per Base Station:")
    smsFailures.show()

    // 2. 以月为单位计算每个基站的通话数量和通话时长
    val baseStationCallStats = callDF
      .groupBy("year", "month", "stationId")
      .agg(
        count($"callId").alias("callCount"),
        sum("callDurationMillis").alias("totalCallDurationMillis")
      )
      .orderBy("year", "month", "stationId")
    println("Base Station Call Stats:")
    baseStationCallStats.show()

    // 3. 以月为单位计算每个基站的短信数量和短信内容长度
    val baseStationSmsStats = smsDF
      .groupBy("year", "month", "stationId")
      .agg(
        count($"smsId").alias("smsCount"),
        sum(length($"smsContent")).alias("totalSmsContentLength")
      )
      .orderBy("year", "month", "stationId")
    println("Base Station SMS Stats:")
    baseStationSmsStats.show()

    // 4. 以月为单位计算每个基站的会话数量和上行/下行流量
    val baseStationTrafficStats = trafficDF
      .groupBy("year", "month", "stationId")
      .agg(
        count($"sessionId").alias("sessionCount"),
        sum("upstreamDataVolume").alias("totalUpstreamDataVolume"),
        sum("downstreamDataVolume").alias("totalDownstreamDataVolume")
      )
      .orderBy("year", "month", "stationId")
    println("Base Station Traffic Stats:")
    baseStationTrafficStats.show()
  }
}
