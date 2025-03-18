package com.example.telecom

import com.example.telecom.data.{DataProcessor, FeatureEngineer}
import com.example.telecom.model.{ClusteringModel, PCATransformer}
import org.apache.spark.sql.SparkSession

object Main {
  def main(args: Array[String]): Unit = {
    implicit val spark: SparkSession = SparkSession.builder()
      .appName("UserClustering")
      .enableHiveSupport()
      .getOrCreate()

    // 数据预处理
    val telecomDF = DataProcessor.run

    // 特征工程
    val scaledData = FeatureEngineer.run(telecomDF)

    // 聚类
    val predictions = ClusteringModel.run(scaledData)

    // 主成分分析
    PCATransformer.run(predictions)

    spark.stop()
  }
}

