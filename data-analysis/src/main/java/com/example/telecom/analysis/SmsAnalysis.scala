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
      .orderBy($"year", $"month")
    log.info("按月短信条数/长度统计")
    monthlySMSSummery.show(1024, truncate = false)

    // 2. 按月按用户短信发送/接收条数和长度统计
    // 按用户短信发送条数和长度统计
    val smsSentPerUser = smsDF
      .filter($"sendDirection" === "SENT")
      .groupBy($"year", $"month", $"senderNumber")
      .agg(count($"smsId").alias("total_sent_count"), sum(length($"smsContent")).alias("total_sent_length"))
      .orderBy($"year", $"month", $"senderNumber")
    log.info("按用户短信发送条数和长度统计")
    smsSentPerUser.show(1024, truncate = false)

    // 按用户短信接收条数和长度统计
    val smsReceivedPerUser = smsDF
      .filter($"sendDirection" === "RECEIVED")
      .groupBy($"year", $"month", $"receiverNumber")
      .agg(count($"smsId").alias("total_received_count"), sum(length($"smsContent")).alias("total_received_length"))
      .orderBy($"year", $"month", $"receiverNumber")
    log.info("按用户短信接收条数和长度统计")
    smsReceivedPerUser.show(1024, truncate = false)

    // 3. 按月短信状态统计
    val monthlySmsStatus = smsDF
      .groupBy($"year", $"month", $"sendStatus")
      .agg(count($"smsId").alias("smsStatusCount"))
      .orderBy($"year", $"month", $"sendStatus")
    log.info("按月短信状态统计")
    monthlySmsStatus.show(1024, truncate = false)

    // 4. 按月每日小时短信分布统计
    val hourlyCallDistribution = smsDF
      .groupBy($"year", $"month", $"hour")
      .agg(count($"smsId").alias("sms_count"))
      .orderBy($"year", $"month", $"hour")
    log.info("按月每日小时短信分布统计")
    hourlyCallDistribution.show(1024, truncate = false)

    spark.stop()
  }
}
