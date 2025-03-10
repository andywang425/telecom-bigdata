package com.example.telecom.utils

import org.apache.spark.sql.SparkSession

object SparkUtils {
  def getSession(appName: String): SparkSession = {
    SparkSession.builder()
      .appName(appName)
      .enableHiveSupport()
      .getOrCreate()
  }
}
