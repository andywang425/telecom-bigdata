// ClusteringModel.scala
package com.example.telecom.model

import com.example.telecom.utils.Config
import org.apache.spark.ml.clustering.{KMeans, KMeansModel}
import org.apache.spark.ml.feature.StandardScalerModel
import org.apache.spark.ml.linalg.Vector
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.expressions.Window
import org.apache.log4j.{Level, Logger}

object ClusteringModel {
  private val logger = Logger.getLogger(this.getClass)
  Logger.getLogger("org").setLevel(Level.WARN)

  /**
   * 训练KMeans模型并返回预测结果
   * @param scaledData 已标准化的特征数据
   * @param k 聚类数量（默认使用Config.K）
   * @return (KMeansModel, 预测结果DataFrame)
   */
  def trainKMeans(scaledData: DataFrame, k: Int = Config.K): (KMeansModel, DataFrame) = {
    require(scaledData.columns.contains(Config.SCALED_FEATURES_COL),
      s"Input data must contain scaled features column: ${Config.SCALED_FEATURES_COL}")

    val kmeans = new KMeans()
      .setK(k)
      .setSeed(Config.RANDOM_SEED)
      .setFeaturesCol(Config.SCALED_FEATURES_COL)
      .setPredictionCol(Config.CLUSTER_COL)
      .setMaxIter(Config.MAX_ITER)

    val model = kmeans.fit(scaledData)
    val predictions = model.transform(scaledData)

    logger.info(s"成功训练KMeans模型，聚类数: $k")
    (model, predictions)
  }

  /**
   * 将标准化后的聚类中心转换回原始特征空间
   * @param model 训练好的KMeans模型
   * @param scalerModel 使用的标准化模型
   */
  def explainClusterCenters(model: KMeansModel, scalerModel: StandardScalerModel): Unit = {
    val originalCenters = model.clusterCenters.map { scaledVector =>
      val mean = scalerModel.mean.toArray
      val std = scalerModel.std.toArray
      val originalValues = scaledVector.toArray.zipWithIndex.map {
        case (scaled, idx) => scaled * std(idx) + mean(idx)
      }
      org.apache.spark.ml.linalg.Vectors.dense(originalValues)
    }

    logger.info("原始特征空间的聚类中心：")
    originalCenters.zipWithIndex.foreach {
      case (center, idx) => logger.info(f"Cluster $idx: ${center.toArray.map("%.2f".format(_)).mkString("[", ", ", "]")}")
    }
  }
}
