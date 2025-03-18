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
    val predictions = trainKMeans(scaledData)

    saveResults(predictions)

    predictions
  }

  /**
   * 训练KMeans模型并返回预测结果
   *
   * @param scaledData 已标准化的特征数据
   */
  private def trainKMeans(scaledData: DataFrame): DataFrame = {
    new KMeans()
      .setK(Config.K)
      .setSeed(Config.RANDOM_SEED)
      .setFeaturesCol(Config.SCALED_FEATURES_COL)
      .setPredictionCol(Config.CLUSTER_COL)
      .setMaxIter(Config.MAX_ITER)
      .fit(scaledData)
      .transform(scaledData)
  }

  /**
   * 保存聚类结果到 MySQL
   */
  private def saveResults(df: DataFrame): Unit = {
    df.select("phone", "cluster")
      .write
      .format("jdbc")
      .option("url", Config.JDBC_URL)
      .option("dbtable", Config.DB_TABLE)
      .option("user", Config.DB_USER)
      .option("password", Config.DB_PASSWORD)
      .option("driver", Config.DB_DRIVER)
      .mode("overwrite")
      .save()
  }
}
