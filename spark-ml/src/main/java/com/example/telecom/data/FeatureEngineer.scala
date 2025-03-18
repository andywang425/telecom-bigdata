package com.example.telecom.data

import com.example.telecom.config.Config
import org.apache.spark.ml.feature.{StandardScaler, VectorAssembler}
import org.apache.spark.sql.DataFrame

object FeatureEngineer {
  /**
   * 构建特征向量并进行标准化
   */
  def run(df: DataFrame): DataFrame = {
    val featureDF = buildFeatures(df)

    scaleFeatures(featureDF)
  }

  /**
   * 构建特征向量
   */
  private def buildFeatures(df: DataFrame): DataFrame = {
    new VectorAssembler()
      .setInputCols(Config.FEATURE_COLUMNS)
      .setOutputCol("features")
      .transform(df)
      .select("phone", "features")
  }

  /**
   * 特征向量标准化
   *
   * @param featureDF 包含特征向量的 DataFrame
   */
  private def scaleFeatures(featureDF: DataFrame): DataFrame = {
    new StandardScaler()
      .setInputCol("features")
      .setOutputCol("scaledFeatures")
      .setWithStd(Config.SCALING_WITH_STD)
      .setWithMean(Config.SCALING_WITH_MEAN)
      .fit(featureDF)
      .transform(featureDF)
      .select("phone", "features", "scaledFeatures")
  }
}

