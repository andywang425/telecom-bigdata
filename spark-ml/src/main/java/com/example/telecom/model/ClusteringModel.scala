package com.example.telecom.model

import com.example.telecom.config.Config
import org.apache.spark.ml.clustering.KMeans
import org.apache.spark.sql.DataFrame

object ClusteringModel {
  /**
   * 执行KMeans聚类并存储结果
   *
   * @param scaledData 包含已标准化的特征向量的DataFrame
   * @return 聚类结果
   */
  def run(scaledData: DataFrame): DataFrame = {
    new KMeans()
      .setK(Config.K)
      .setSeed(Config.RANDOM_SEED)
      .setFeaturesCol(Config.SCALED_FEATURES_COL)
      .setPredictionCol(Config.CLUSTER_COL)
      .setMaxIter(Config.MAX_ITER)
      .fit(scaledData)
      .transform(scaledData)
  }
}
