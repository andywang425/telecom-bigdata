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
        count("*").alias("total_calls")
      )

    log.info("按月总通话时长和数量")
    monthlyCallSummary.show(1024, truncate = false)

    // 2. 每月用户通话时长和数量（包含主被叫双方）
    val monthlyCallerCallSummary = callDF
      .withColumn("userNumber", $"callerNumber")
      .groupBy($"year", $"month", $"userNumber")
      .agg(
        count("*").as("caller_call_count") / 2,
        sum("callDurationMillis").as("caller_total_call_duration") / 2
      )
      .orderBy($"year", $"month", $"userNumber")

    log.info("每月用户通话时长和数量（主叫）")
    monthlyCallerCallSummary.show(1024, truncate = false)

    val monthlyReceiverCallSummary = callDF
      .withColumn("userNumber", $"receiverNumber")
      .groupBy($"year", $"month", $"userNumber")
      .agg(
        count("*").as("receiver_call_count") / 2,
        sum("callDurationMillis").as("receiver_total_call_duration") / 2
      )
      .orderBy($"year", $"month", $"userNumber")

    log.info("每月用户通话时长和数量（被叫）")
    monthlyReceiverCallSummary.show(1024, truncate = false)

    val monthlyUserCallSummary = monthlyCallerCallSummary
      .join(monthlyReceiverCallSummary, Seq("year", "month", "userNumber"))
      .withColumn("total_call_count", $"caller_call_count" + $"receiver_call_count")
      .withColumn("total_call_duration", $"caller_total_call_duration" + $"receiver_total_call_duration")

    log.info("每月用户通话时长和数量（主被叫方合并）")
    monthlyUserCallSummary.show(1024, truncate = false)

    // 3. 每月通话状态统计
    val monthlyCallStatus = callDF
      .groupBy($"year", $"month", $"callStatus")
      .agg(count("*").alias("call_count"))

    log.info("每月通话状态统计")
    monthlyCallStatus.show(1024, truncate = false)

    // 4. 按月每日小时通话分布统计
    val hourlyCallDistribution = callDF
      .groupBy($"year", $"month", $"hour")
      .agg(count($"callId").alias("call_count"))

    log.info("按月每日小时通话分布统计")
    hourlyCallDistribution.show(1024, truncate = false)

    spark.stop()
  }
}
