package com.example.telecom.analysis

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

object StationAnalysis {
  private val log = org.slf4j.LoggerFactory.getLogger(this.getClass)

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("StationAnalysis")
      .enableHiveSupport()
      .getOrCreate()

    import spark.implicits._

    // Load Hive tables into DataFrames
    val callTable = spark.table("telecom_data.call")
    val smsTable = spark.table("telecom_data.sms")
    val trafficTable = spark.table("telecom_data.traffic")

    val callDF = callTable
      .withColumn("year", year($"callStartTime"))
      .withColumn("month", month($"callStartTime"))
      .withColumn("day", day($"callStartTime"))
      .withColumn("hour", hour($"callStartTime"))
    val smsDF = smsTable.withColumn("year", year($"sendTime"))
      .withColumn("month", month($"sendTime"))
      .withColumn("day", day($"sendTime"))
      .withColumn("hour", hour($"sendTime"))
    val trafficDF = trafficTable.withColumn("year", year($"sessionStartTime"))
      .withColumn("month", month($"sessionStartTime"))
      .withColumn("day", day($"sessionStartTime"))
      .withColumn("hour", hour($"sessionStartTime"))

    // 1. 基站的通话和短信故障率
    // A. Calculate call failure rate per base station (failed when callStatus = 'FAILED')
    val callFailures = callDF
      .groupBy("year", "month", "stationId")
      .agg(
        count($"sessionId").alias("totalCalls"),
        sum(when($"callStatus" === "FAILED", 1).otherwise(0)).alias("failedCalls")
      )
      .withColumn("callFailureRate", $"failedCalls" / $"totalCalls")
      .orderBy("year", "month", "stationId")
    log.info("Call Failure Rate Per Base Station:")
    callFailures.show()

    // B. Calculate SMS failure rate per base station
    // (failed when sendStatus in ('FAILED_TO_SEND', 'FAILED_TO_RECEIVE'))
    val smsFailures = smsDF
      .groupBy("year", "month", "stationId")
      .agg(
        count($"sessionId").alias("totalSms"),
        sum(when($"sendStatus".isin("FAILED_TO_SEND", "FAILED_TO_RECEIVE"), 1).otherwise(0)).alias("failedSms")
      )
      .withColumn("smsFailureRate", $"failedSms" / $"totalSms")
      .orderBy("year", "month", "stationId")
    log.info("SMS Failure Rate Per Base Station:")
    smsFailures.show()

    // 2. 以月为单位计算每个基站的通话数量和通话时长
    val baseStationCallStats = callDF
      .groupBy("year", "month", "stationId")
      .agg(
        count($"sessionId").alias("callCount"),
        sum("callDurationMillis").alias("totalCallDurationMillis")
      )
      .orderBy("year", "month", "stationId")
    log.info("Base Station Call Stats:")
    baseStationCallStats.show()

    // 3. 以月为单位计算每个基站的短信数量和短信内容长度
    val baseStationSmsStats = smsDF
      .groupBy("year", "month", "stationId")
      .agg(
        count($"sessionId").alias("smsCount"),
        sum(length($"smsContent")).alias("totalSmsContentLength")
      )
      .orderBy("year", "month", "stationId")
    log.info("Base Station SMS Stats:")

    // 4. 以月为单位计算每个基站的会话数量和上行/下行流量
    val baseStationTrafficStats = trafficDF
      .groupBy("year", "month", "stationId")
      .agg(
        count($"sessionId").alias("sessionCount"),
        sum("upstreamDataVolume").alias("totalUpstreamDataVolume"),
        sum("downstreamDataVolume").alias("totalDownstreamDataVolume")
      )
      .orderBy("year", "month", "stationId")
    log.info("Base Station Traffic Stats:")
    baseStationTrafficStats.show()

    spark.stop()
  }
}
