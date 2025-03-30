package com.example.telecom.analysis

import com.example.telecom.utils.{MyLogger, SparkUtils}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, SparkSession}

object CallAnalysis extends MyLogger {
  def run(callDF: DataFrame)(implicit spark: SparkSession): Unit = {
    import spark.implicits._

    // 1. 按月总通话时长和数量
    val monthlyCallSummary = callDF
      .groupBy($"year", $"month")
      .agg(
        sum($"call_duration_millis").alias("total_duration_millis"),
        count($"call_id").alias("total_calls")
      )
      .orderBy($"year", $"month")

    info("Monthly call summary")
    monthlyCallSummary.show(1024, truncate = false)
    SparkUtils.saveToMySQL(monthlyCallSummary, "call_summary")
    monthlyCallSummary.unpersist()

    // 2. 每月用户通话时长和数量（包含主被叫双方）
    val monthlyCallerCallSummary = callDF
      .withColumn("user_number", $"caller_number")
      .groupBy($"year", $"month", $"user_number")
      .agg(
        count($"call_id").divide(2).alias("caller_call_count"),
        sum($"call_duration_millis").divide(2).alias("caller_total_call_duration")
      )
      .orderBy($"year", $"month", $"user_number")

    info("Monthly caller call summary")
    monthlyCallerCallSummary.show(1024, truncate = false)

    val monthlyReceiverCallSummary = callDF
      .withColumn("user_number", $"receiver_number")
      .groupBy($"year", $"month", $"user_number")
      .agg(
        count($"call_id").divide(2).alias("receiver_call_count"),
        sum($"call_duration_millis").divide(2).alias("receiver_total_call_duration")
      )
      .orderBy($"year", $"month", $"user_number")

    info("Monthly receiver call summary")
    monthlyReceiverCallSummary.show(1024, truncate = false)

    val monthlyUserCallSummary = monthlyCallerCallSummary
      .join(monthlyReceiverCallSummary, Seq("year", "month", "user_number"))
      .withColumn("total_call_count", $"caller_call_count" + $"receiver_call_count")
      .withColumn("total_call_duration", $"caller_total_call_duration" + $"receiver_total_call_duration")

    info("Monthly user call summary")
    monthlyUserCallSummary.show(1024, truncate = false)
    SparkUtils.saveToMySQL(monthlyUserCallSummary, "call_user")
    monthlyCallerCallSummary.unpersist()
    monthlyReceiverCallSummary.unpersist()
    monthlyUserCallSummary.unpersist()

    // 3. 每月通话状态统计
    val monthlyCallStatus = callDF
      .groupBy($"year", $"month", $"call_status")
      .agg(count($"call_id").alias("call_count"))
      .orderBy($"year", $"month", $"call_status")

    info("Monthly call status summary")
    monthlyCallStatus.show(1024, truncate = false)
    SparkUtils.saveToMySQL(monthlyCallStatus, "call_status")
    monthlyCallStatus.unpersist()

    // 4. 按月每日小时通话分布统计
    val hourlyCallDistribution = callDF
      .groupBy($"year", $"month", $"hour")
      .agg(count($"call_id").alias("call_count"))
      .orderBy($"year", $"month", $"hour")

    info("Monthly (and hourly) call day distribution summary")
    hourlyCallDistribution.show(1024, truncate = false)
    SparkUtils.saveToMySQL(hourlyCallDistribution, "call_day_distribution")
    hourlyCallDistribution.unpersist()
  }
}
