package com.example.telecom;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.OutputMode;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeoutException;

import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.from_json;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        log.info("Starting Telecom Data Processor...");

        // 创建SparkSession并启用Hive支持
        SparkSession spark = SparkSession.builder()
                .appName("TelecomDataProcessor")
                .config("spark.sql.warehouse.dir", "/user/hive/warehouse")
//                .config("spark.sql.hive.metastore.version", "3.1.3") // 可能可以写道配置文件里？
//                .config("spark.sql.hive.metastore.jars", "path")
//                .config("spark.sql.hive.metastore.jars.path", "file:///usr/local/hive/lib/*.jar") // /usr/local/hadoop/lib/native/*
                .enableHiveSupport()
                .getOrCreate();

        // 定义各主题的Schema
        StructType callSchema = new StructType()
                .add("callId", DataTypes.StringType)
                .add("callerNumber", DataTypes.StringType)
                .add("receiverNumber", DataTypes.StringType)
                .add("callStartTime", DataTypes.LongType)
                .add("callEndTime", DataTypes.LongType)
                .add("callDurationMillis", DataTypes.LongType)
                .add("callDirection", DataTypes.StringType)
                .add("callStatus", DataTypes.StringType)
                .add("stationId", DataTypes.StringType);

        StructType smsSchema = new StructType()
                .add("smsId", DataTypes.StringType)
                .add("senderNumber", DataTypes.StringType)
                .add("receiverNumber", DataTypes.StringType)
                .add("smsContent", DataTypes.StringType)
                .add("sendTime", DataTypes.LongType)
                .add("sendDirection", DataTypes.StringType)
                .add("sendStatus", DataTypes.StringType)
                .add("stationId", DataTypes.StringType);

        StructType trafficSchema = new StructType()
                .add("sessionId", DataTypes.StringType)
                .add("userNumber", DataTypes.StringType)
                .add("sessionStartTime", DataTypes.LongType)
                .add("sessionEndTime", DataTypes.LongType)
                .add("sessionDurationMillis", DataTypes.LongType)
                .add("applicationType", DataTypes.StringType)
                .add("upstreamDataVolume", DataTypes.LongType)
                .add("downstreamDataVolume", DataTypes.LongType)
                .add("networkTechnology", DataTypes.StringType)
                .add("stationId", DataTypes.StringType);

        // 从Kafka读取数据流
        Dataset<Row> kafkaDF = spark.readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", "slave1:9092,slave2:9092,slave3:9092")
                .option("subscribe", "telecom-data-call,telecom-data-sms,telecom-data-traffic")
                .load();

        // 处理通话数据
        Dataset<Row> callData = kafkaDF
                .filter("topic = 'telecom-data-call'")
                .selectExpr("CAST(value AS STRING) as json")
                .select(from_json(col("json"), callSchema).alias("data"))
                .select("data.*");

        // 处理短信数据
        Dataset<Row> smsData = kafkaDF
                .filter("topic = 'telecom-data-sms'")
                .selectExpr("CAST(value AS STRING) as json")
                .select(from_json(col("json"), smsSchema).alias("data"))
                .select("data.*");

        // 处理流量数据
        Dataset<Row> trafficData = kafkaDF
                .filter("topic = 'telecom-data-traffic'")
                .selectExpr("CAST(value AS STRING) as json")
                .select(from_json(col("json"), trafficSchema).alias("data"))
                .select("data.*");


        try {
            // 启动三个流式写入任务
            StreamingQuery callQuery = callData.writeStream()
                    .outputMode(OutputMode.Append())
                    .foreachBatch((batchDF, batchId) -> {
                        batchDF.write().format("hive").mode(SaveMode.Append).saveAsTable("`telecom_data`.`call`");
                    })
                    .start();

            StreamingQuery smsQuery = smsData.writeStream()
                    .outputMode(OutputMode.Append())
                    .foreachBatch((batchDF, batchId) -> {
                        batchDF.write().format("hive").mode(SaveMode.Append).saveAsTable("`telecom_data`.`sms`");
                    })
                    .start();

            StreamingQuery trafficQuery = trafficData.writeStream()
                    .outputMode(OutputMode.Append())
                    .foreachBatch((batchDF, batchId) -> {
                        batchDF.write().format("hive").mode(SaveMode.Append).saveAsTable("`telecom_data`.`traffic`");
                    })
                    .start();

            log.info("Streaming queries started.");

            // 保持程序运行
            callQuery.awaitTermination();
            smsQuery.awaitTermination();
            trafficQuery.awaitTermination();
        } catch (StreamingQueryException | TimeoutException e) {
            log.error("Error while writing to Hive:", e);
        }
    }
}