// FeatureEngineer.scala
package com.example.telecom.data

import com.example.telecom.utils.Config
import org.apache.spark.ml.feature.{StandardScaler, StandardScalerModel, VectorAssembler}
import org.apache.spark.sql.DataFrame

object FeatureEngineer {
  var scalerModel: StandardScalerModel = _

  // 特征向量组合（保持与原始代码相同逻辑）
  def buildFeatures(dataFrames: DataFrame*): DataFrame = {
    // 合并数据表（复用DataProcessor中的合并逻辑）
    val mergedDF = DataProcessor.mergeDataFrames(dataFrames: _*)

    // 创建特征向量
    new VectorAssembler()
      .setInputCols(Config.FEATURE_COLUMNS)
      .setOutputCol("features")
      .transform(mergedDF)
      .select("phone", "features") // 提前筛选减少后续计算量
  }

  // 特征标准化处理（可配置标准化参数）
  def scaleFeatures(featureDF: DataFrame, withStd: Boolean = true, withMean: Boolean = true): DataFrame = {
    scalerModel = new StandardScaler()
      .setInputCol("features")
      .setOutputCol("scaledFeatures")
      .setWithStd(withStd)
      .setWithMean(withMean)
      .fit(featureDF)


    scalerModel.transform(featureDF)
      .select("phone", "features", "scaledFeatures") // 保留原始特征用于解释
  }
}

