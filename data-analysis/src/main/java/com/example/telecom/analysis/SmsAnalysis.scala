package com.example.telecom.analysis

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

object SmsAnalysis {
  private val log = org.slf4j.LoggerFactory.getLogger(this.getClass)

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("SmsAnalysis")
      .enableHiveSupport()
      .getOrCreate()

    import spark.implicits._

    // Load Hive tables into DataFrames
    val smsTable = spark.table("telecom_data.sms")

    val smsDF = smsTable.withColumn("year", year($"sendTime"))
      .withColumn("month", month($"sendTime"))
      .withColumn("day", day($"sendTime"))
      .withColumn("hour", hour($"sendTime"))

    // 1. 按月短信条数/长度统计
    val monthlySMSSummery = smsDF
      .groupBy($"year", $"month")
      .agg(
        count($"smsId").alias("total_count"),
        sum(length($"smsContent")).alias("total_length")
      )

    // 2. 按月按用户短信发送/接收条数和长度统计

    // 按用户短信发送条数和长度统计
    val smsSentPerUser = smsDF
      .filter($"sendDirection" === "SENT")
      .groupBy($"year", $"month", $"senderNumber")
      .agg(count("*").alias("total_sent_count"), sum(length($"smsContent")).alias("total_sent_length"))

    // 按用户短信接收条数和长度统计
    val smsReceivedPerUser = smsDF
      .filter($"sendDirection" === "RECEIVED")
      .groupBy($"year", $"month", $"receiverNumber")
      .agg(count("*").alias("total_received_count"), sum(length($"smsContent")).alias("total_received_length"))

    // 3. 按月短信状态统计
    val monthlySmsStatus = smsDF
      .groupBy($"year", $"month", $"sendStatus")
      .agg(count("*").alias("smsStatusCount"))

    // 4. 按月每日小时短信分布统计
    val hourlyCallDistribution = smsDF
      .groupBy($"year", $"month", $"hour")
      .agg(count("*").alias("sms_count"))

    spark.stop()
  }
}
