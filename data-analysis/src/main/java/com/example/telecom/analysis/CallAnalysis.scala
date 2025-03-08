package com.example.telecom.analysis

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

object CallAnalysis {
  private val log = org.slf4j.LoggerFactory.getLogger(this.getClass)

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("CallAnalysis")
      .enableHiveSupport()
      .getOrCreate()

    import spark.implicits._

    // Load Hive tables into DataFrames
    val callTable = spark.table("telecom_data.call")

    val callDF = callTable
      .withColumn("year", year($"callStartTime"))
      .withColumn("month", month($"callStartTime"))
      .withColumn("day", day($"callStartTime"))
      .withColumn("hour", hour($"callStartTime"))

    // 1. 按月总通话时长和数量
    val monthlyCallSummary = callDF
      .groupBy($"year", $"month")
      .agg(
        sum($"callDurationMillis").alias("total_duration_millis"),
        count($"callId").alias("total_calls")
      )
      .orderBy($"year", $"month")

    log.info("Monthly call summary")
    monthlyCallSummary.show(1024, truncate = false)

    // 2. 每月用户通话时长和数量（包含主被叫双方）
    val monthlyCallerCallSummary = callDF
      .withColumn("userNumber", $"callerNumber")
      .groupBy($"year", $"month", $"userNumber")
      .agg(
        count($"callId").divide(2).alias("caller_call_count"),
        sum("callDurationMillis").divide(2).alias("caller_total_call_duration")
      )
      .orderBy($"year", $"month", $"userNumber")

    log.info("Monthly caller call summary")
    monthlyCallerCallSummary.show(1024, truncate = false)

    val monthlyReceiverCallSummary = callDF
      .withColumn("userNumber", $"receiverNumber")
      .groupBy($"year", $"month", $"userNumber")
      .agg(
        count($"callId").divide(2).alias("receiver_call_count"),
        sum("callDurationMillis").divide(2).alias("receiver_total_call_duration")
      )
      .orderBy($"year", $"month", $"userNumber")

    log.info("Monthly receiver call summary")
    monthlyReceiverCallSummary.show(1024, truncate = false)

    val monthlyUserCallSummary = monthlyCallerCallSummary
      .join(monthlyReceiverCallSummary, Seq("year", "month", "userNumber"))
      .withColumn("total_call_count", $"caller_call_count" + $"receiver_call_count")
      .withColumn("total_call_duration", $"caller_total_call_duration" + $"receiver_total_call_duration")

    log.info("Monthly user call summary")
    monthlyUserCallSummary.show(1024, truncate = false)

    // 3. 每月通话状态统计
    val monthlyCallStatus = callDF
      .groupBy($"year", $"month", $"callStatus")
      .agg(count("*").alias("call_count"))
      .orderBy($"year", $"month", $"callStatus")

    log.info("Monthly call status summary")
    monthlyCallStatus.show(1024, truncate = false)

    // 4. 按月每日小时通话分布统计
    val hourlyCallDistribution = callDF
      .groupBy($"year", $"month", $"hour")
      .agg(count($"callId").alias("call_count"))
      .orderBy($"year", $"month", $"hour")

    log.info("Hourly call distribution summary")
    hourlyCallDistribution.show(1024, truncate = false)

    spark.stop()
  }
}
