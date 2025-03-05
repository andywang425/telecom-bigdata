package com.example.telecom

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

object CallDataAnalysis2 {
  def main(args: Array[String]): Unit = {
    // Create a SparkSession with Hive support enabled
    val spark = SparkSession.builder()
      .appName("CallDataAnalysis")
      .enableHiveSupport()
      .getOrCreate()

    import spark.implicits._

    // Read the call table from the Hive database "telecom-data"
    val callDF = spark.table("telecom_data.call")

    val callDFwithTime = callDF.withColumn("year", date_format($"callStartTime", "yyyy"))
      .withColumn("month", date_format($"callStartTime", "MM"))
      .withColumn("day", date_format($"callStartTime", "dd"))
      .withColumn("hour", date_format($"callStartTime", "HH"))
//      .withColumn("minute", date_format($"callStartTime", "mm"))
//      .withColumn("second", date_format($"callStartTime", "ss"))
//      .withColumn("millisecond", date_format($"callStartTime", "SSS"))

    // 1. Total call duration and total number of calls by month
    val totalByMonth = callDFwithTime.groupBy($"year", $"month")
      .agg(
        sum("callDurationMillis").alias("total_call_duration"),
        count("callId").alias("total_call_count")
      )
      .orderBy("month")
    totalByMonth.show(false)

    // 2. Number of calls in each call state on a monthly basis.
    // "callStatus" indicates the call state (CONNECTED, MISSED, BUSY, FAILED, etc.)
    val callsByState = callDFwithTime.groupBy($"year", $"month", $"callStatus")
      .agg(count("callId").alias("state_call_count"))
      .orderBy("month", "callStatus")
    callsByState.show(false)

    // 3. Average number of calls per hour of the day for each month
    // Extract day and hour from callStartTime for finer-grained analysis
//    val callWithDayHour = callDFwithTime
//      .withColumn("day", date_format(from_unixtime(col("callStartTime") / 1000), "yyyy-MM-dd"))
//      .withColumn("hour", date_format(from_unixtime(col("callStartTime") / 1000), "HH"))

    // First, calculate the number of calls per hour per day within each month
    val dailyCalls = callDFwithTime.groupBy($"year", $"month", $"day", $"hour")
      .agg(count("callId").alias("calls_per_hour"))

    // Now, average the hourly call counts across the days for each month and hour
    val avgCallsPerHour = dailyCalls.groupBy($"year", $"month", $"hour")
      .agg(avg("calls_per_hour").alias("avg_calls_per_hour"))
      .orderBy("month", "hour")
    avgCallsPerHour.show(false)

    // 4. Number of calls and call duration for each base station on a monthly basis.
    val callsByStation = callDFwithTime.groupBy($"year", $"month", $"stationId")
      .agg(
        count("callId").alias("station_call_count"),
        sum("callDurationMillis").alias("station_total_call_duration")
      )
      .orderBy("month", "stationId")
    callsByStation.show(false)

    // Stop the SparkSession
    spark.stop()
  }
}
