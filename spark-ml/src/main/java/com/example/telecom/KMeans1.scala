package com.example.telecom

import org.apache.spark.ml.clustering.KMeans
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

object KMeans1 {
  def main(args: Array[String]): Unit = {
    // Create a SparkSession with Hive support enabled
    val spark = SparkSession.builder()
      .appName("UserClustering")
      .enableHiveSupport()
      .getOrCreate()

    import spark.implicits._

    val callTable = spark.table("telecom_data.call")
    val smsTable = spark.table("telecom_data.sms")
    val trafficTable = spark.table("telecom_data.traffic")

    val callDF = callTable
      .filter($"callDirection" === "OUTGOING")
      //      .withColumn("day", day($"callStartTime"))
      .withColumn("hour", hour($"callStartTime"))
      .cache()
    val smsDF = smsTable
      .filter($"sendDirection" === "SENT")
      //      .withColumn("day", day($"sendTime"))
      .withColumn("hour", hour($"sendTime"))
      .cache()
    val trafficDF = trafficTable
      //      .withColumn("day", day($"sessionStartTime"))
      .withColumn("hour", hour($"sessionStartTime"))
      .cache()

    val callAgg = callDF
      .withColumnRenamed("callerNumber", "phone")
      .groupBy("phone")
      .agg(
        sum(when($"hour" >= 6 && $"hour" < 12, 1).otherwise(0)).alias("call_count_1"),
        sum(when($"hour" >= 12 && $"hour" < 20, 1).otherwise(0)).alias("call_count_2"),
        sum(when($"hour" >= 20 || $"hour" < 6, 1).otherwise(0)).alias("call_count_3"),
      )

    val smsAgg = smsDF
      .withColumnRenamed("senderNumber", "phone")
      .groupBy("phone")
      .agg(
        sum(when($"hour" >= 6 && $"hour" < 12, 1).otherwise(0)).alias("sms_count_1"),
        sum(when($"hour" >= 6 && $"hour" < 12, 1).otherwise(0)).alias("sms_count_2"),
        sum(when($"hour" >= 20 || $"hour" < 6, 1).otherwise(0)).alias("sms_count_3")
      )

    val trafficAgg = trafficDF
      .withColumnRenamed("userNumber", "phone")
      .groupBy("phone")
      .agg(
        sum(when($"hour" >= 6 && $"hour" < 12, 1).otherwise(0)).alias("session_count_1"),
        sum(when($"hour" >= 6 && $"hour" < 12, 1).otherwise(0)).alias("session_count_2"),
        sum(when($"hour" >= 20 || $"hour" < 6, 1).otherwise(0)).alias("session_count_3")
      )

    val userUsageDF = callAgg
      .join(smsAgg, Seq("phone"), "outer")
      .join(trafficAgg, Seq("phone"), "outer")
      .na.fill(0)

    userUsageDF.show(4096, truncate = false)

    val assembler = new VectorAssembler()
      .setInputCols(Array("call_count_1", "call_count_2", "call_count_3", "sms_count_1", "sms_count_2", "sms_count_3", "session_count_1", "session_count_2", "session_count_3"))
      .setOutputCol("features")

    val featureDF = assembler.transform(userUsageDF)

    val kmeans = new KMeans()
      .setK(2)
      .setSeed(1L)
      .setFeaturesCol("features")
      .setPredictionCol("cluster")

    val model = kmeans.fit(featureDF)

    val predictions = model.transform(featureDF)
    predictions.select("phone", "features", "cluster").show(4096, truncate = false)

    println("Cluster Centers: ")
    model.clusterCenters.foreach(center => println(center.toString))

    spark.stop()
  }
}
