package com.example.telecom

import org.apache.spark.ml.clustering.KMeans
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

object Main {
  def main(args: Array[String]): Unit = {
    // Create a SparkSession with Hive support enabled
    val spark = SparkSession.builder()
      .appName("UserClustering")
      .enableHiveSupport()
      .getOrCreate()

    // -------------------------------
    // 1. Aggregate Call Data Features
    // -------------------------------
    // Read call table from Hive
    val callDF = spark.table("telecom-data.call")

    // Create two datasets: one for caller and one for receiver.
    // Each call record contributes a count of 1 and its duration to both participants.
    val callerDF = callDF.select(col("callerNumber").alias("phone"), col("callDurationMillis"))
      .withColumn("call_count", lit(1))
    val receiverDF = callDF.select(col("receiverNumber").alias("phone"), col("callDurationMillis"))
      .withColumn("call_count", lit(1))

    // Union the two DataFrames and aggregate per phone
    val callUnion = callerDF.union(receiverDF)
    val callAgg = callUnion.groupBy("phone")
      .agg(
        sum("call_count").alias("call_count"),
        sum("callDurationMillis").alias("call_duration")
      )

    // -------------------------------
    // 2. Aggregate SMS Data Features
    // -------------------------------
    // Read sms table from Hive
    val smsDF = spark.table("telecom-data.sms")

    // Create separate DataFrames for sender and receiver
    val senderDF = smsDF.select(col("senderNumber").alias("phone"))
      .withColumn("sms_count", lit(1))
    val receiverSMSDF = smsDF.select(col("receiverNumber").alias("phone"))
      .withColumn("sms_count", lit(1))

    // Union and aggregate
    val smsUnion = senderDF.union(receiverSMSDF)
    val smsAgg = smsUnion.groupBy("phone")
      .agg(sum("sms_count").alias("sms_count"))

    // -------------------------------
    // 3. Aggregate Traffic Data Features
    // -------------------------------
    // Read traffic table from Hive
    val trafficDF = spark.table("telecom-data.traffic")

    // Calculate total data volume per session (upstream + downstream)
    val trafficWithVolume = trafficDF.withColumn("data_volume", col("upstreamDataVolume") + col("downstreamDataVolume"))

    // Aggregate traffic features by userNumber (renamed to phone)
    val trafficAgg = trafficWithVolume.groupBy(col("userNumber").alias("phone"))
      .agg(
        count("sessionId").alias("traffic_count"),
        sum("sessionDurationMillis").alias("session_duration"),
        sum("data_volume").alias("data_volume")
      )

    // -------------------------------
    // 4. Combine All Features for Each User
    // -------------------------------
    // Join aggregated features on phone number (outer join to include users from any source)
    val userUsageDF = callAgg
      .join(smsAgg, Seq("phone"), "outer")
      .join(trafficAgg, Seq("phone"), "outer")
      .na.fill(0) // fill missing values with 0

    // -------------------------------
    // 5. Assemble Features for Clustering
    // -------------------------------
    // Define the feature columns you want to use for clustering
    val featureCols = Array("call_count", "call_duration", "sms_count", "traffic_count", "session_duration", "data_volume")

    // Create a feature vector column
    val assembler = new VectorAssembler()
      .setInputCols(featureCols)
      .setOutputCol("features")
    val featureDF = assembler.transform(userUsageDF)

    // -------------------------------
    // 6. Apply K-Means Clustering
    // -------------------------------
    // Set up k-means with a chosen number of clusters (for example, k=5)
    val kmeans = new KMeans()
      .setK(5)
      .setSeed(1L)
      .setFeaturesCol("features")
      .setPredictionCol("cluster")

    // Fit the k-means model on the feature data
    val model = kmeans.fit(featureDF)

    // Get the cluster assignments
    val predictions = model.transform(featureDF)
    predictions.select("phone", "features", "cluster").show(50, truncate = false)

    // Optionally, print out the cluster centers for inspection
    println("Cluster Centers: ")
    model.clusterCenters.foreach(center => println(center.toString))

    // Stop the SparkSession
    spark.stop()
  }
}
