package com.example.telecom

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

object TelecomDataAnalysis {
  def main(args: Array[String]): Unit = {
    // Initialize Spark session with Hive support
    val spark = SparkSession.builder()
      .appName("Telecom Data Analysis")
      .enableHiveSupport()
      .getOrCreate()

    import spark.implicits._

    // Load Hive tables into DataFrames
    val callDF = spark.table("telecom_data.call")
    val smsDF = spark.table("telecom_data.sms")
    val trafficDF = spark.table("telecom_data.traffic")

    // Create additional time-related columns (yearMonth and hour) for grouping
    val callWithYM = callDF.withColumn("yearMonth", date_format($"callStartTime", "yyyy-MM"))
    val callWithHour = callWithYM.withColumn("hour", hour($"callStartTime"))

    val smsWithYM = smsDF.withColumn("yearMonth", date_format($"sendTime", "yyyy-MM"))
    val smsWithHour = smsWithYM.withColumn("hour", hour($"sendTime"))

    val trafficWithYM = trafficDF.withColumn("yearMonth", date_format($"sessionStartTime", "yyyy-MM"))
    val trafficWithHour = trafficWithYM.withColumn("hour", hour($"sessionStartTime"))

    // ===================================================
    // Call Analysis
    // ===================================================

    // 1. Total call duration by month
    val totalCallDurationByMonth = callWithYM
      .groupBy("yearMonth")
      .agg(sum("callDurationMillis").alias("totalCallDurationMillis"))

    // 2. Total number of calls by month
    val totalCallsByMonth = callWithYM
      .groupBy("yearMonth")
      .agg(count("*").alias("totalCalls"))

    // 3. Call duration per user (using callerNumber) per month
    val callDurationPerUser = callWithYM
      .groupBy("yearMonth", "callerNumber")
      .agg(sum("callDurationMillis").alias("userCallDurationMillis"))

    // 4. Number of calls per user per month
    val callsPerUser = callWithYM
      .groupBy("yearMonth", "callerNumber")
      .agg(count("*").alias("userCallCount"))

    // 5. Number of calls in each call state (callStatus) on a monthly basis
    val callStateCounts = callWithYM
      .groupBy("yearMonth", "callStatus")
      .agg(count("*").alias("callStatusCount"))

    // 6. Total number of calls per month, per hour of the day
    val callsByMonthHour = callWithHour
      .groupBy("yearMonth", "hour")
      .agg(count("*").alias("callsCount"))

    // ===================================================
    // SMS Analysis
    // ===================================================

    // 1. Total number of SMS messages by month
    val totalSmsByMonth = smsWithYM
      .groupBy("yearMonth")
      .agg(count("*").alias("totalSms"))

    // 2. Total SMS content length by month
    val totalSmsContentLengthByMonth = smsWithYM
      .groupBy("yearMonth")
      .agg(sum(length($"smsContent")).alias("totalSmsContentLength"))

    // 3. Number of SMS sent per user per month (filtering for SENT)
    val smsSentPerUser = smsWithYM
      .filter($"sendDirection" === "SENT")
      .groupBy("yearMonth", "senderNumber")
      .agg(count("*").alias("sentSmsCount"))

    // 4. Number of incoming SMS per user per month (filtering for RECEIVED)
    val smsReceivedPerUser = smsWithYM
      .filter($"sendDirection" === "RECEIVED")
      .groupBy("yearMonth", "receiverNumber")
      .agg(count("*").alias("receivedSmsCount"))

    // 5. Length of sent SMS content per user per month
    val sentSmsContentLengthPerUser = smsWithYM
      .filter($"sendDirection" === "SENT")
      .groupBy("yearMonth", "senderNumber")
      .agg(sum(length($"smsContent")).alias("sentSmsContentLength"))

    // 6. Length of incoming SMS content per user per month
    val receivedSmsContentLengthPerUser = smsWithYM
      .filter($"sendDirection" === "RECEIVED")
      .groupBy("yearMonth", "receiverNumber")
      .agg(sum(length($"smsContent")).alias("receivedSmsContentLength"))

    // 7. Number of each SMS status on a monthly basis
    val smsStatusCounts = smsWithYM
      .groupBy("yearMonth", "sendStatus")
      .agg(count("*").alias("smsStatusCount"))

    // 8. Total number of SMS per month, per hour of the day
    val smsByMonthHour = smsWithHour
      .groupBy("yearMonth", "hour")
      .agg(count("*").alias("smsCount"))

    // ===================================================
    // Traffic Analysis
    // ===================================================

    // 1. Total number of sessions by month
    val sessionsByMonth = trafficWithYM
      .groupBy("yearMonth")
      .agg(count("*").alias("totalSessions"))

    // 2. Total session duration by month
    val sessionDurationByMonth = trafficWithYM
      .groupBy("yearMonth")
      .agg(sum("sessionDurationMillis").alias("totalSessionDurationMillis"))

    // 3. Number of sessions per user per month
    val sessionsPerUser = trafficWithYM
      .groupBy("yearMonth", "userNumber")
      .agg(count("*").alias("sessionCount"))

    // 4. Total uplink traffic per user per month
    val uplinkTrafficPerUser = trafficWithYM
      .groupBy("yearMonth", "userNumber")
      .agg(sum("upstreamDataVolume").alias("totalUpstreamDataVolume"))

    // 5. Total downstream traffic per user per month
    val downstreamTrafficPerUser = trafficWithYM
      .groupBy("yearMonth", "userNumber")
      .agg(sum("downstreamDataVolume").alias("totalDownstreamDataVolume"))

    // 6. Count the number of sessions by application type per month
    // (Assuming "applicationType" serves as a proxy for session state)
    val sessionCountByAppType = trafficWithYM
      .groupBy("yearMonth", "applicationType")
      .agg(count("*").alias("sessionCount"))

    // 7. Uplink/downlink traffic by application type per month
    val trafficByAppType = trafficWithYM
      .groupBy("yearMonth", "applicationType")
      .agg(
        sum("upstreamDataVolume").alias("totalUpstreamDataVolume"),
        sum("downstreamDataVolume").alias("totalDownstreamDataVolume")
      )

    // 8. Uplink/downlink traffic by network technology per month
    val trafficByNetworkTech = trafficWithYM
      .groupBy("yearMonth", "networkTechnology")
      .agg(
        sum("upstreamDataVolume").alias("totalUpstreamDataVolume"),
        sum("downstreamDataVolume").alias("totalDownstreamDataVolume")
      )

    // 9. Total uplink/downlink traffic for each hour of the day, for each month
    val trafficByMonthHour = trafficWithHour
      .groupBy("yearMonth", "hour")
      .agg(
        sum("upstreamDataVolume").alias("totalUpstreamDataVolume"),
        sum("downstreamDataVolume").alias("totalDownstreamDataVolume")
      )

    // ===================================================
    // Base Station Analysis
    // ===================================================

    // A. Calculate call failure rate per base station (failed when callStatus = 'FAILED')
    val callFailures = callWithYM
      .groupBy("yearMonth", "stationId")
      .agg(
        count("*").alias("totalCalls"),
        sum(when($"callStatus" === "FAILED", 1).otherwise(0)).alias("failedCalls")
      )
      .withColumn("callFailureRate", $"failedCalls" / $"totalCalls")

    // B. Calculate SMS failure rate per base station
    // (failed when sendStatus in ('FAILED_TO_SEND', 'FAILED_TO_RECEIVE'))
    val smsFailures = smsWithYM
      .groupBy("yearMonth", "stationId")
      .agg(
        count("*").alias("totalSms"),
        sum(when($"sendStatus".isin("FAILED_TO_SEND", "FAILED_TO_RECEIVE"), 1).otherwise(0)).alias("failedSms")
      )
      .withColumn("smsFailureRate", $"failedSms" / $"totalSms")

    // C. Base station call stats: number of calls and total call duration per month for each base station
    val baseStationCallStats = callWithYM
      .groupBy("yearMonth", "stationId")
      .agg(
        count("*").alias("callCount"),
        sum("callDurationMillis").alias("totalCallDurationMillis")
      )

    // D. Base station SMS stats: number of SMS messages and total SMS content length per month
    val baseStationSmsStats = smsWithYM
      .groupBy("yearMonth", "stationId")
      .agg(
        count("*").alias("smsCount"),
        sum(length($"smsContent")).alias("totalSmsContentLength")
      )

    // E. Base station traffic stats: number of sessions and total uplink/downlink traffic per month
    val baseStationTrafficStats = trafficWithYM
      .groupBy("yearMonth", "stationId")
      .agg(
        count("*").alias("sessionCount"),
        sum("upstreamDataVolume").alias("totalUpstreamDataVolume"),
        sum("downstreamDataVolume").alias("totalDownstreamDataVolume")
      )

    // ===================================================
    // Optionally: Show some results (or write them back to Hive, files, etc.)
    // ===================================================
    totalCallDurationByMonth.show()
    totalCallsByMonth.show()
    callDurationPerUser.show()
    callsPerUser.show()
    callStateCounts.show()
    callsByMonthHour.show()

    totalSmsByMonth.show()
    totalSmsContentLengthByMonth.show()
    smsSentPerUser.show()
    smsReceivedPerUser.show()
    sentSmsContentLengthPerUser.show()
    receivedSmsContentLengthPerUser.show()
    smsStatusCounts.show()
    smsByMonthHour.show()

    sessionsByMonth.show()
    sessionDurationByMonth.show()
    sessionsPerUser.show()
    uplinkTrafficPerUser.show()
    downstreamTrafficPerUser.show()
    sessionCountByAppType.show()
    trafficByAppType.show()
    trafficByNetworkTech.show()
    trafficByMonthHour.show()

    callFailures.show()
    smsFailures.show()
    baseStationCallStats.show()
    baseStationSmsStats.show()
    baseStationTrafficStats.show()

    spark.stop()
  }
}
