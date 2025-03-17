package com.example.telecom

import com.example.telecom.data.{DataProcessor, FeatureEngineer}
import com.example.telecom.model.{ClusteringModel, PCATransformer}
import com.example.telecom.utils.{Config, DBUtils}
import org.apache.spark.sql.SparkSession

// KMeansJob.scala
object KMeansJob {
  def main(args: Array[String]): Unit = {
    implicit val spark: SparkSession = SparkSession.builder()
      .appName("UserClustering")
      .enableHiveSupport()
      .getOrCreate()

    // 数据预处理
    val callDF = DataProcessor.processCallData(spark).cache()
    val smsDF = DataProcessor.processSmsData(spark).cache()
    val trafficDF = DataProcessor.processTrafficData(spark).cache()

    // 特征工程
    val featureDF = FeatureEngineer.buildFeatures(callDF, smsDF, trafficDF)
    val scaledData = FeatureEngineer.scaleFeatures(
      featureDF,
      Config.SCALING_WITH_STD,
      Config.SCALING_WITH_MEAN
    )

    // KMeansJob.scala
    val (model, predictions) = ClusteringModel.trainKMeans(scaledData)

    // 解释聚类中心（需要传入scalerModel）
    ClusteringModel.explainClusterCenters(model, FeatureEngineer.scalerModel)

    // 结果处理
    PCATransformer.run(predictions)(spark)
    DBUtils.saveToDatabase(predictions)

    spark.stop()
  }
}

