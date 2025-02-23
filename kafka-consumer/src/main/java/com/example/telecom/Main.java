package com.example.telecom;

import org.apache.spark.sql.SparkSession;

public class Main {
    public static void main(String[] args) {
        // 创建SparkSession并启用Hive支持
        SparkSession spark = SparkSession.builder()
                .appName("TelecomDataProcessor")
                .enableHiveSupport()
                .getOrCreate();
    }
}