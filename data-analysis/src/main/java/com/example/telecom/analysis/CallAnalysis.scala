package com.example.telecom.analysis

import com.example.telecom.utils.{MyLogger, SparkUtils}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, SparkSession}

object CallAnalysis extends MyLogger {
  def run(spark: SparkSession, callDF: DataFrame): Unit = {
    import spark.implicits._

    // 1. 按月总通话时长和数量
    val monthlyCallSummary = callDF
      .groupBy($"year", $"month")
      .agg(
        sum($"callDurationMillis").alias("total_duration_millis"),
        count($"callId").alias("total_calls")
      )
      .orderBy($"year", $"month")

    info("Monthly call summary")
    monthlyCallSummary.show(1024, truncate = false)
    SparkUtils.saveToMySQL(monthlyCallSummary, "monthly_call_summary")
    monthlyCallSummary.unpersist()

    // 2. 每月用户通话时长和数量（包含主被叫双方）
    val monthlyCallerCallSummary = callDF
      .withColumn("userNumber", $"callerNumber")
      .groupBy($"year", $"month", $"userNumber")
      .agg(
        count($"callId").divide(2).alias("caller_call_count"),
        sum($"callDurationMillis").divide(2).alias("caller_total_call_duration")
      )
      .orderBy($"year", $"month", $"userNumber")

    info("Monthly caller call summary")
    monthlyCallerCallSummary.show(1024, truncate = false)

    val monthlyReceiverCallSummary = callDF
      .withColumn("userNumber", $"receiverNumber")
      .groupBy($"year", $"month", $"userNumber")
      .agg(
        count($"callId").divide(2).alias("receiver_call_count"),
        sum($"callDurationMillis").divide(2).alias("receiver_total_call_duration")
      )
      .orderBy($"year", $"month", $"userNumber")

    info("Monthly receiver call summary")
    monthlyReceiverCallSummary.show(1024, truncate = false)

    val monthlyUserCallSummary = monthlyCallerCallSummary
      .join(monthlyReceiverCallSummary, Seq("year", "month", "userNumber"))
      .withColumn("total_call_count", $"caller_call_count" + $"receiver_call_count")
      .withColumn("total_call_duration", $"caller_total_call_duration" + $"receiver_total_call_duration")

    info("Monthly user call summary")
    monthlyUserCallSummary.show(1024, truncate = false)
    SparkUtils.saveToMySQL(monthlyUserCallSummary, "monthly_user_call_summary")
    monthlyCallerCallSummary.unpersist()
    monthlyReceiverCallSummary.unpersist()
    monthlyUserCallSummary.unpersist()

    // 3. 每月通话状态统计
    val monthlyCallStatus = callDF
      .groupBy($"year", $"month", $"callStatus")
      .agg(count($"callId").alias("call_count"))
      .orderBy($"year", $"month", $"callStatus")

    info("Monthly call status summary")
    monthlyCallStatus.show(1024, truncate = false)
    SparkUtils.saveToMySQL(monthlyCallStatus, "monthly_call_status_summary")
    monthlyCallStatus.unpersist()

    // 4. 按月每日小时通话分布统计
    val hourlyCallDistribution = callDF
      .groupBy($"year", $"month", $"hour")
      .agg(count($"callId").alias("call_count"))
      .orderBy($"year", $"month", $"hour")

    info("Monthly (and hourly) call day distribution summary")
    hourlyCallDistribution.show(1024, truncate = false)
    SparkUtils.saveToMySQL(hourlyCallDistribution, "monthly_call_day_distribution_summary")
    hourlyCallDistribution.unpersist()
  }
}
