package com.example.telecom

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

object CallDataAnalysis3 {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("CallDataAnalysis")
      .enableHiveSupport()
      .getOrCreate()

    import spark.implicits._

    val callDF = spark.table("telecom_data.call")

    val callDFWithTime = callDF.withColumn("year", year($"callStartTime"))
      .withColumn("month", month($"callStartTime"))
      .withColumn("day", day($"callStartTime"))
      .withColumn("hour", hour($"callStartTime"))

    // 1. 2. Analyze total call duration and number of calls in terms of months
    val totalDurationAndNumbersByMonth = callDFWithTime.filter($"callStatus" === "CONNECTED")
      .groupBy($"year", $"month")
      .agg(
        count("callId").as("total_call_count"),
        sum("callDurationMillis").as("total_call_duration")
      )
      .orderBy($"year", $"month")
    totalDurationAndNumbersByMonth.show(1024, truncate = false)

    // 3. 4. Call duration and number of calls per user per month
    val userDurationAndNumbersOutgoingByMonth = callDFWithTime.filter($"callStatus" === "CONNECTED" && $"callDirection" === "OUTGOING")
      .withColumn("userNumber", $"callerNumber")
      .groupBy($"year", $"month", $"userNumber")
      .agg(
        count("callId").as("user_call_count_1"),
        sum("callDurationMillis").as("user_total_call_duration_1")
      )
      .orderBy($"year", $"month", $"callerNumber")
    userDurationAndNumbersOutgoingByMonth.show(1024, truncate = false)

    val userDurationAndNumbersIncomingByMonth = callDFWithTime.filter($"callStatus" === "CONNECTED" && $"callDirection" === "INCOMING")
      .withColumn("userNumber", $"receiverNumber")
      .groupBy($"year", $"month", $"userNumber")
      .agg(
        count("callId").as("user_call_count_2"),
        sum("callDurationMillis").as("user_total_call_duration_2")
      )
      .orderBy($"year", $"month", $"receiverNumber")
    userDurationAndNumbersIncomingByMonth.show(1024, truncate = false)

   val userDurationAndNumbersByMonth =  userDurationAndNumbersOutgoingByMonth.join(userDurationAndNumbersIncomingByMonth, Seq("year", "month", "userNumber"))
      .withColumn("total_call_count", $"user_call_count_1" + $"user_call_count_2")
      .withColumn("total_call_duration", $"user_total_call_duration_1" + $"user_total_call_duration_2")
    userDurationAndNumbersByMonth.show(1024, truncate = false)

    // 5. Calculate the number of calls in each call state on a monthly basis
    // "callStatus" indicates the call state (CONNECTED, MISSED, BUSY, FAILED, etc.)
    val callsByState = callDFWithTime.groupBy($"year", $"month", $"callStatus")
      .agg(count("callId").as("state_call_count"))
      .orderBy($"year", $"month", $"callStatus")
    callsByState.show(1024, truncate = false)

    // 6. Calculate the total number of calls per month, per hour of the day (distribution of calls during the day)
    val callsPerHourByMonth = callDFWithTime.groupBy($"year", $"month", $"hour")
      .agg(count("callId").alias("calls_per_hour"))
      .orderBy($"year", $"month", $"hour")
    callsPerHourByMonth.show(1024, truncate = false)

    // Stop the SparkSession
    spark.stop()
  }
}
