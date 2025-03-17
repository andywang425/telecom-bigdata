package com.example.telecom

import com.example.telecom.data.{DataProcessor, FeatureEngineer}
import com.example.telecom.model.{ClusteringModel, PCATransformer}
import com.example.telecom.utils.{Config, DBUtils}
import org.apache.spark.sql.SparkSession

object KMeansJob {
  def main(args: Array[String]): Unit = {
    implicit val spark: SparkSession = SparkSession.builder()
      .appName("UserClustering")
      .enableHiveSupport()
      .getOrCreate()

    // 数据预处理
    val callDF = DataProcessor.processCallData
    val smsDF = DataProcessor.processSmsData
    val trafficDF = DataProcessor.processTrafficData

    // 特征工程
    val featureDF = FeatureEngineer.buildFeatures(callDF, smsDF, trafficDF)
    val scaledData = FeatureEngineer.scaleFeatures(
      featureDF,
      Config.SCALING_WITH_STD,
      Config.SCALING_WITH_MEAN
    )

    val (model, predictions) = ClusteringModel.trainKMeans(scaledData)

    // 解释聚类中心（需要传入scalerModel）
    ClusteringModel.explainClusterCenters(model, FeatureEngineer.scalerModel)

    // 结果处理
    PCATransformer.run(predictions)
    DBUtils.saveToDatabase(predictions)

    spark.stop()
  }
}

