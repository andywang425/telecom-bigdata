package com.example.telecom.utils

import org.apache.spark.sql.DataFrame

object DBUtils {
  def saveToDatabase(df: DataFrame): Unit = {
    df.write
      .format("jdbc")
      .option("url", Config.JDBC_URL)
      .option("dbtable", Config.DB_TABLE)
      .option("user", "root")
      .option("password", "root")
      .option("driver", "com.mysql.cj.jdbc.Driver")
      .mode("overwrite")
      .save()
  }
}
