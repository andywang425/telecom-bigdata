package com.example.telecom.utils

import org.apache.spark.sql.SparkSession

object SparkUtils {
  def getSession(appName: String): SparkSession = {
    SparkSession.builder()
      .appName(appName)
      .config("spark.sql.catalogImplementation", "hive") // 这些配置有用吗？？？
      .config("spark.datasource.jdbc.url", "jdbc:mysql://localhost:3306/telecom_analysis")
      .config("spark.datasource.jdbc.driver", "com.mysql.cj.jdbc.Driver")
      .config("spark.datasource.jdbc.user", "root")
      .config("spark.datasource.jdbc.password", "password")
      .enableHiveSupport()
      .getOrCreate()
  }
}
