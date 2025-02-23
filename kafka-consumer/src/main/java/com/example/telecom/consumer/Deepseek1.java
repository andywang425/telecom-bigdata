package com.example.telecom.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;
import scala.Tuple2;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Deepseek1 {

    public static void main(String[] args) throws Exception {
        // 初始化SparkSession
        SparkSession spark = SparkSession.builder()
                .appName("TelecomDataProcessor")
                .enableHiveSupport()
                .getOrCreate();

        // 创建Hive表
        createHiveTables(spark);

        // 定义schema
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

        // 初始化StreamingContext
        JavaStreamingContext jssc = new JavaStreamingContext(
                new JavaSparkContext(spark.sparkContext()),
                Durations.seconds(1)
        );

        // Kafka配置
        Map<String, Object> kafkaParams = new HashMap<>();
        kafkaParams.put("bootstrap.servers", "slave1:9092,slave2:9092,slave3:9092");
        kafkaParams.put("key.deserializer", StringDeserializer.class);
        kafkaParams.put("value.deserializer", StringDeserializer.class);
        kafkaParams.put("group.id", "telecom-group");
        kafkaParams.put("auto.offset.reset", "latest");
        kafkaParams.put("enable.auto.commit", false);

        Collection<String> topics = Arrays.asList(
                "telecom-data-call",
                "telecom-data-sms",
                "telecom-data-traffic"
        );

        // 创建DStream
        JavaInputDStream<ConsumerRecord<String, String>> stream =
                KafkaUtils.createDirectStream(
                        jssc,
                        LocationStrategies.PreferConsistent(),
                        ConsumerStrategies.Subscribe(topics, kafkaParams)
                );

        // 处理数据
        stream.foreachRDD(rdd -> {
            if (rdd.isEmpty()) return;

            // 转换为(topic, json)元组
            JavaRDD<Tuple2<String, String>> topicJsonRDD = rdd
                    .map(record -> new Tuple2<>(record.topic(), record.value()));

            // 分割不同topic的数据
            processTopicData(topicJsonRDD, "telecom-data-call", callSchema, spark, "call");
            processTopicData(topicJsonRDD, "telecom-data-sms", smsSchema, spark, "sms");
            processTopicData(topicJsonRDD, "telecom-data-traffic", trafficSchema, spark, "traffic");
        });

        // 启动流处理
        jssc.start();
        jssc.awaitTermination();
    }

    private static void createHiveTables(SparkSession spark) {
        spark.sql("CREATE DATABASE IF NOT EXISTS `telecom-data`");

        // 创建call表
        spark.sql("CREATE TABLE IF NOT EXISTS `telecom-data`.`call` (" +
                "callId STRING, callerNumber STRING, receiverNumber STRING, " +
                "callStartTime BIGINT, callEndTime BIGINT, callDurationMillis BIGINT, " +
                "callDirection STRING, callStatus STRING, stationId STRING) " +
                "STORED AS ORC");

        // 创建sms表
        spark.sql("CREATE TABLE IF NOT EXISTS `telecom-data`.`sms` (" +
                "smsId STRING, senderNumber STRING, receiverNumber STRING, " +
                "smsContent STRING, sendTime BIGINT, sendDirection STRING, " +
                "sendStatus STRING, stationId STRING) " +
                "STORED AS ORC");

        // 创建traffic表
        spark.sql("CREATE TABLE IF NOT EXISTS `telecom-data`.`traffic` (" +
                "sessionId STRING, userNumber STRING, " +
                "sessionStartTime BIGINT, sessionEndTime BIGINT, sessionDurationMillis BIGINT, " +
                "applicationType STRING, upstreamDataVolume BIGINT, downstreamDataVolume BIGINT, " +
                "networkTechnology STRING, stationId STRING) " +
                "STORED AS ORC");
    }

    private static void processTopicData(JavaRDD<Tuple2<String, String>> topicJsonRDD,
                                         String topicName,
                                         StructType schema,
                                         SparkSession spark,
                                         String tableName) {
        JavaRDD<String> jsonRDD = topicJsonRDD
                .filter(t -> t._1().equals(topicName))
                .map(Tuple2::_2);

        if (!jsonRDD.isEmpty()) {
            Dataset<Row> df = spark.read().schema(schema).json(jsonRDD);
            df.write().format("hive").mode("append").insertInto("telecom-data." + tableName);
        }
    }
}
