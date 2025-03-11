package com.example.telecom

import com.example.telecom.analysis.{CallAnalysis, SmsAnalysis, StationAnalysis, TrafficAnalysis}
import com.example.telecom.utils.{MyLogger, SparkUtils}
import org.apache.spark.sql.functions.{day, hour, month, year}

object Main extends MyLogger {
  def main(args: Array[String]): Unit = {
    val spark = SparkUtils.getSession("telecom-data-analysis")

    import spark.implicits._

    val callTable = spark.table("telecom_data.call")
    val smsTable = spark.table("telecom_data.sms")
    val trafficTable = spark.table("telecom_data.traffic")

    val callDF = callTable
      .withColumn("year", year($"callStartTime"))
      .withColumn("month", month($"callStartTime"))
      .withColumn("day", day($"callStartTime"))
      .withColumn("hour", hour($"callStartTime"))
      .cache()
    val smsDF = smsTable
      .withColumn("year", year($"sendTime"))
      .withColumn("month", month($"sendTime"))
      .withColumn("day", day($"sendTime"))
      .withColumn("hour", hour($"sendTime"))
      .cache()
    val trafficDF = trafficTable
      .withColumn("year", year($"sessionStartTime"))
      .withColumn("month", month($"sessionStartTime"))
      .withColumn("day", day($"sessionStartTime"))
      .withColumn("hour", hour($"sessionStartTime"))
      .cache()

    callTable.unpersist()
    smsTable.unpersist()
    trafficTable.unpersist()

    try {
      info("Starting Call Analysis...")
      CallAnalysis.run(spark, callDF)

      info("Starting SMS Analysis...")
      SmsAnalysis.run(spark, smsDF)

      info("Starting Traffic Analysis...")
      TrafficAnalysis.run(spark, trafficDF)

      info("Starting Station Analysis...")
      StationAnalysis.run(spark, callDF, smsDF, trafficDF)

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
