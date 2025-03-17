// PCATransformer.scala
package com.example.telecom.model

import com.example.telecom.utils.Config
import org.apache.log4j.{Level, Logger}
import org.apache.spark.ml.feature.PCA
import org.apache.spark.ml.linalg.Vector
import org.apache.spark.sql.functions.col
import org.apache.spark.sql.{DataFrame, SparkSession}

object PCATransformer {
  private val logger = Logger.getLogger(this.getClass)
  Logger.getLogger("org").setLevel(Level.WARN)

  /**
   * 执行PCA降维并存储结果
   *
   * @param data 包含特征向量的DataFrame
   * @param k    降维后的维度（默认2维）
   * @return 包含PCA坐标的DataFrame
   */
  def run(data: DataFrame, k: Int = 2)(implicit spark: SparkSession): DataFrame = {
    validateInput(data)

    logger.info(s"开始PCA降维（k=$k）...")

    // 1. 训练PCA模型
    val (pcaModel, pcaDF) = trainPCA(data, k)

    // 2. 提取坐标信息
    val resultDF = extractCoordinates(pcaDF)

    // 3. 存储结果
    saveResults(resultDF)

    resultDF
  }

  /**
   * 训练PCA模型
   */
  private def trainPCA(data: DataFrame, k: Int) = {
    val pca = new PCA()
      .setInputCol(Config.SCALED_FEATURES_COL)
      .setOutputCol(Config.PCA_FEATURES_COL)
      .setK(k)

    val model = pca.fit(data)
    val transformed = model.transform(data)
    (model, transformed)
  }

  /**
   * 提取PCA坐标为独立列
   */
  private def extractCoordinates(pcaDF: DataFrame)(implicit spark: SparkSession): DataFrame = {
    import spark.implicits._

    pcaDF.select(
      col("phone"),
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
   * 存储结果到配置路径
   */
  private def saveResults(df: DataFrame): Unit = {
    df.coalesce(1)
      .write
      .option("header", "true")
      .mode("overwrite")
      .csv(Config.PCA_RESULT_PATH)

    logger.info(s"PCA结果已存储到：${Config.PCA_RESULT_PATH}")
  }

  /**
   * 输入数据校验
   */
  private def validateInput(data: DataFrame): Unit = {
    require(data.columns.contains(Config.SCALED_FEATURES_COL),
      s"输入数据必须包含特征列：${Config.SCALED_FEATURES_COL}")

    require(data.columns.contains("phone"),
      "输入数据必须包含phone列")
  }
}
