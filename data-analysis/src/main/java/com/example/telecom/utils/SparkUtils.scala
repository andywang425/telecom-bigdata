package com.example.telecom.utils

import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}

import java.util.Properties

object SparkUtils {
  private final val jdbcUrl = "jdbc:mysql://master:3306/telecom"

  private final val connectionProperties = new Properties()
  connectionProperties.put("user", "root")
  connectionProperties.put("password", "root")
  connectionProperties.put("driver", "com.mysql.cj.jdbc.Driver")

  def getSession(appName: String): SparkSession = {
    SparkSession.builder()
      .appName(appName)
      .enableHiveSupport()
      .getOrCreate()
  }

  def saveToMySQL(df: DataFrame, tableName: String): Unit = {
    df.write
      .mode(SaveMode.Overwrite)
      .jdbc(jdbcUrl, tableName, connectionProperties)
  }
}
