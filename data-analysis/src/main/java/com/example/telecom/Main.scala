package com.example.telecom

import com.example.telecom.analysis.{CallAnalysis, SmsAnalysis, StationAnalysis, TrafficAnalysis}
import com.example.telecom.utils.{MyLogger, SparkUtils}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.{day, hour, month, year}

object Main extends MyLogger {
  def main(args: Array[String]): Unit = {
    implicit val spark: SparkSession = SparkUtils.getSession("telecom-data-analysis")

    import spark.implicits._

    val callTable = spark.table("telecom_data.call")
    val smsTable = spark.table("telecom_data.sms")
    val trafficTable = spark.table("telecom_data.traffic")

    val callDF = callTable
      .withColumn("year", year($"call_start_time"))
      .withColumn("month", month($"call_start_time"))
      .withColumn("day", day($"call_start_time"))
      .withColumn("hour", hour($"call_start_time"))
      .cache()
    val smsDF = smsTable
      .withColumn("year", year($"send_time"))
      .withColumn("month", month($"send_time"))
      .withColumn("day", day($"send_time"))
      .withColumn("hour", hour($"send_time"))
      .cache()
    val trafficDF = trafficTable
      .withColumn("year", year($"session_start_time"))
      .withColumn("month", month($"session_start_time"))
      .withColumn("day", day($"session_start_time"))
      .withColumn("hour", hour($"session_start_time"))
      .cache()

    callTable.unpersist()
    smsTable.unpersist()
    trafficTable.unpersist()

    try {
      info("Starting Call Analysis...")
      CallAnalysis.run(callDF)

      info("Starting SMS Analysis...")
      SmsAnalysis.run(smsDF)

      info("Starting Traffic Analysis...")
      TrafficAnalysis.run(trafficDF)

      info("Starting Station Analysis...")
      StationAnalysis.run(callDF, smsDF, trafficDF)

      info("All analysis tasks completed successfully.")
    } catch {
      case e: Exception =>
        error(s"Error during analysis: ${e.getMessage}")
        e.printStackTrace()
    } finally {
      spark.stop()
    }
  }
}
