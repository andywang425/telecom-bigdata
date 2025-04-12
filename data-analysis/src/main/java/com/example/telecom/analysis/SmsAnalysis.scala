package com.example.telecom.analysis

import com.example.telecom.utils.{MyLogger, SparkUtils}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, SparkSession}

object SmsAnalysis extends MyLogger {
  def run(smsDF: DataFrame)(implicit spark: SparkSession): Unit = {
    import spark.implicits._

    // 1. 按月短信条数/长度统计
    val monthlySmsSummery = smsDF
      .groupBy($"year", $"month")
      .agg(
        count($"sms_id").alias("total_count"),
        sum(length($"sms_content")).alias("total_length")
      )
      .orderBy($"year", $"month")
    info("Monthly SMS summary")
    monthlySmsSummery.show(1024, truncate = false)
    SparkUtils.saveToMySQL(monthlySmsSummery, "sms_summary")
    monthlySmsSummery.unpersist()

    // 2. 按月按用户短信发送/接收条数和长度统计
    // 按用户短信发送条数和长度统计
    val monthlySmsSentPerUser = smsDF
      .filter($"send_direction" === "SENT")
      .groupBy($"year", $"month", $"sender_number")
      .agg(count($"sms_id").alias("total_sent_count"), sum(length($"sms_content")).alias("total_sent_length"))
      .orderBy($"year", $"month", $"sender_number")
    info("Monthly SMS sent user summary")
    monthlySmsSentPerUser.show(1024, truncate = false)
    SparkUtils.saveToMySQL(monthlySmsSentPerUser, "sms_sent_user")
    monthlySmsSentPerUser.unpersist()

    // 按用户短信接收条数和长度统计
    val monthlySmsReceivedPerUser = smsDF
      .filter($"send_direction" === "RECEIVED")
      .groupBy($"year", $"month", $"receiver_number")
      .agg(count($"sms_id").alias("total_received_count"), sum(length($"sms_content")).alias("total_received_length"))
      .orderBy($"year", $"month", $"receiver_number")
    info("Monthly SMS received user summary")
    monthlySmsReceivedPerUser.show(1024, truncate = false)
    SparkUtils.saveToMySQL(monthlySmsReceivedPerUser, "sms_received_user")
    monthlySmsReceivedPerUser.unpersist()

    // 3. 按月短信状态统计
    val monthlySmsStatus = smsDF
      .groupBy($"year", $"month", $"send_status")
      .agg(count($"sms_id").alias("sms_count"))
      .orderBy($"year", $"month", $"send_status")
    info("Monthly SMS status summary")
    monthlySmsStatus.show(1024, truncate = false)
    SparkUtils.saveToMySQL(monthlySmsStatus, "sms_status")
    monthlySmsStatus.unpersist()

    // 4. 按月每日小时短信分布统计
    val hourlySmsDistribution = smsDF
      .groupBy($"year", $"month", $"hour")
      .agg(count($"sms_id").alias("sms_count"))
      .orderBy($"year", $"month", $"hour")
    info("Monthly (and hourly) SMS day distribution summary")
    hourlySmsDistribution.show(1024, truncate = false)
    SparkUtils.saveToMySQL(hourlySmsDistribution, "sms_day_distribution")
    hourlySmsDistribution.unpersist()
  }
}
