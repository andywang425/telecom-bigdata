package com.example.telecom.analysis

import com.example.telecom.utils.MyLogger
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, SparkSession}

object SmsAnalysis extends MyLogger {
  def run(spark: SparkSession, smsDF: DataFrame): Unit = {
    import spark.implicits._

    // 1. 按月短信条数/长度统计
    val monthlySmsSummery = smsDF
      .groupBy($"year", $"month")
      .agg(
        count($"smsId").alias("total_count"),
        sum(length($"smsContent")).alias("total_length")
      )
      .orderBy($"year", $"month")
    info("Monthly SMS count and length summary")
    monthlySmsSummery.show(1024, truncate = false)

    // 2. 按月按用户短信发送/接收条数和长度统计
    // 按用户短信发送条数和长度统计
    val smsSentPerUser = smsDF
      .filter($"sendDirection" === "SENT")
      .groupBy($"year", $"month", $"senderNumber")
      .agg(count($"smsId").alias("total_sent_count"), sum(length($"smsContent")).alias("total_sent_length"))
      .orderBy($"year", $"month", $"senderNumber")
    info("Monthly SMS sent count and length per user")
    smsSentPerUser.show(1024, truncate = false)

    // 按用户短信接收条数和长度统计
    val smsReceivedPerUser = smsDF
      .filter($"sendDirection" === "RECEIVED")
      .groupBy($"year", $"month", $"receiverNumber")
      .agg(count($"smsId").alias("total_received_count"), sum(length($"smsContent")).alias("total_received_length"))
      .orderBy($"year", $"month", $"receiverNumber")
    info("Monthly SMS received count and length per user")
    smsReceivedPerUser.show(1024, truncate = false)

    // 3. 按月短信状态统计
    val monthlySmsStatus = smsDF
      .groupBy($"year", $"month", $"sendStatus")
      .agg(count($"smsId").alias("sms_status_count"))
      .orderBy($"year", $"month", $"sendStatus")
    info("Monthly SMS status summary")
    monthlySmsStatus.show(1024, truncate = false)

    // 4. 按月每日小时短信分布统计
    val hourlySmsDistribution = smsDF
      .groupBy($"year", $"month", $"hour")
      .agg(count($"smsId").alias("sms_count"))
      .orderBy($"year", $"month", $"hour")
    info("Monthly (and hourly) SMS distribution summary")
    hourlySmsDistribution.show(1024, truncate = false)
  }
}
