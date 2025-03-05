package com.example.telecom

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

object CallDataAnalysis {
  def main(args: Array[String]): Unit = {
    // Create a SparkSession with Hive support enabled
    val spark = SparkSession.builder()
      .appName("CallDataAnalysis")
      .enableHiveSupport()
      .getOrCreate()

    // Read the call table from the Hive database "telecom-data"
    val callDF = spark.table("telecom_data.call")

    // Convert callStartTime (milliseconds) to a timestamp and extract the month (formatted as "yyyy-MM")
    val callWithMonth = callDF.withColumn("month", date_format(from_unixtime(col("callStartTime") / 1000), "yyyy-MM"))

    // 1. Total call duration and total number of calls by month
    val totalByMonth = callWithMonth.groupBy("month")
      .agg(
        sum("callDurationMillis").alias("total_call_duration"),
        count("callId").alias("total_call_count")
      )
      .orderBy("month")
    totalByMonth.show()

    // 2. Number of calls in each call state on a monthly basis.
    // "callStatus" indicates the call state (CONNECTED, MISSED, BUSY, FAILED, etc.)
    val callsByState = callWithMonth.groupBy("month", "callStatus")
      .agg(count("callId").alias("state_call_count"))
      .orderBy("month", "callStatus")
    callsByState.show()

    // 3. Average number of calls per hour of the day for each month
    // Extract day and hour from callStartTime for finer-grained analysis
    val callWithDayHour = callWithMonth
      .withColumn("day", date_format(from_unixtime(col("callStartTime") / 1000), "yyyy-MM-dd"))
      .withColumn("hour", date_format(from_unixtime(col("callStartTime") / 1000), "HH"))

    // First, calculate the number of calls per hour per day within each month
    val dailyCalls = callWithDayHour.groupBy("month", "day", "hour")
      .agg(count("callId").alias("calls_per_hour"))

    // Now, average the hourly call counts across the days for each month and hour
    val avgCallsPerHour = dailyCalls.groupBy("month", "hour")
      .agg(avg("calls_per_hour").alias("avg_calls_per_hour"))
      .orderBy("month", "hour")
    avgCallsPerHour.show()

    // 4. Number of calls and call duration for each base station on a monthly basis.
    val callsByStation = callWithMonth.groupBy("month", "stationId")
      .agg(
        count("callId").alias("station_call_count"),
        sum("callDurationMillis").alias("station_total_call_duration")
      )
      .orderBy("month", "stationId")
    callsByStation.show()

    // Stop the SparkSession
    spark.stop()
  }
}
