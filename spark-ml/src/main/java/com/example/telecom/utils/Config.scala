package com.example.telecom.utils

object Config {
  val JDBC_URL = "jdbc:mysql://master:3306/telecom"
  val DB_TABLE = "user_cluster"

  // 特征列名统一管理
  val FEATURE_COLUMNS: Array[String] = Array(
    "call_count_1", "call_count_2", "call_count_3",
    "sms_count_1", "sms_count_2", "sms_count_3",
    "session_count_1", "session_count_2", "session_count_3"
  )

  // 标准化参数
  val SCALING_WITH_STD = true
  val SCALING_WITH_MEAN = true

  // 聚类配置
  val K = 2
  val MAX_ITER = 20
  val RANDOM_SEED = 1L

  // 列名常量
  val SCALED_FEATURES_COL = "scaledFeatures"
  val CLUSTER_COL = "cluster"

  // PCA相关配置
  val PCA_FEATURES_COL = "pcaFeatures" // PCA输出列名
  val PCA_RESULT_PATH = "/user/root/telecom/pca_cluster_results" // 存储路径
}

