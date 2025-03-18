package com.example.telecom.config

object Config {
  // MySQL参数
  val JDBC_URL = "jdbc:mysql://master:3306/telecom"
  val DB_TABLE = "user_cluster"
  val DB_USER = "root"
  val DB_PASSWORD = "root"
  val DB_DRIVER = "com.mysql.cj.jdbc.Driver"

  // 特征列名
  val FEATURE_COLUMNS: Array[String] = Array(
    "call_count_1", "call_count_2", "call_count_3",
    "sms_count_1", "sms_count_2", "sms_count_3",
    "session_count_1", "session_count_2", "session_count_3"
  )

  // 标准化参数
  val SCALING_WITH_STD = true // 是否将数据标准化为单位标准差
  val SCALING_WITH_MEAN = true // 在缩放之前是否使用平均值来中心化数据（不适合稀疏数据）

  // 聚类配置
  val K = 2 // 聚类数量
  val MAX_ITER = 20 // 最大迭代次数
  val RANDOM_SEED = 1L // 随机种子

  // 列名常量
  val SCALED_FEATURES_COL = "scaledFeatures" // 标准化后的向量列名
  val CLUSTER_COL = "cluster" // 聚类结果列名

  // PCA相关配置
  val PCA_FEATURES_COL = "pcaFeatures" // PCA输出特征列名
  val PCA_RESULT_PATH = "/user/root/telecom/pca_cluster_results" // PCA结果存储HDFS路径
}

