package com.example.telecom.model

import com.example.telecom.config.Config
import org.apache.spark.ml.feature.PCA
import org.apache.spark.ml.linalg.Vector
import org.apache.spark.sql.functions.col
import org.apache.spark.sql.{DataFrame, SparkSession}

object PCATransformer {
  /**
   * 执行PCA降维并存储结果
   *
   * @param data 包含特征向量的 DataFrame
   * @param k    降维后的维度（默认2维）
   * @return 包含 PCA 坐标的 DataFrame
   */
  def run(data: DataFrame, k: Int = 2)(implicit spark: SparkSession): DataFrame = {
    val pcaDF = trainPCA(data, k)

    val resultDF = extractCoordinates(pcaDF)

    saveResults(resultDF)

    resultDF
  }

  /**
   * 训练PCA模型
   */
  private def trainPCA(data: DataFrame, k: Int): DataFrame = {
    new PCA()
      .setInputCol(Config.SCALED_FEATURES_COL)
      .setOutputCol(Config.PCA_FEATURES_COL)
      .setK(k)
      .fit(data)
      .transform(data)
  }

  /**
   * 提取PCA坐标为独立列
   */
  private def extractCoordinates(pcaDF: DataFrame)(implicit spark: SparkSession): DataFrame = {
    import spark.implicits._

    pcaDF.select(
      $"phone",
      col(Config.CLUSTER_COL),
      col(Config.PCA_FEATURES_COL)
    ).map { row =>
      val phone = row.getString(0)
      val cluster = row.getInt(1)
      val pcaFeatures = row.getAs[Vector](2).toArray

      (phone, cluster, pcaFeatures(0), pcaFeatures(1))
    }.toDF("phone", "cluster", "pca_x", "pca_y")
  }

  /**
   * 存储结果至HDFS
   */
  private def saveResults(df: DataFrame): Unit = {
    df.coalesce(1)
      .write
      .option("header", "true")
      .mode("overwrite")
      .csv(Config.PCA_RESULT_PATH)
  }
}
