//package com.example.telecom.consumer;
//
//import org.apache.spark.api.java.JavaSparkContext;
//import org.apache.spark.sql.SparkSession;
//import org.apache.spark.streaming.Duration;
//import org.apache.spark.streaming.api.java.JavaStreamingContext;
//import org.apache.spark.streaming.kafka010.*;
//import org.apache.kafka.common.serialization.StringDeserializer;
//import org.apache.kafka.clients.consumer.ConsumerConfig;
//import org.apache.spark.sql.Row;
//import org.apache.spark.sql.types.*;
//import org.apache.spark.sql.functions;
//import org.apache.spark.streaming.dstream.DStream;
//import org.apache.spark.streaming.kafka010.KafkaUtils;
//import org.apache.spark.sql.DataFrame;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class Chatgpt1 {
//
//    public static void main(String[] args) throws Exception {
//        // Initialize Spark Session
//        SparkSession spark = SparkSession.builder()
//                .appName("Spark Streaming Kafka to Hive")
//                .enableHiveSupport() // Enable Hive support
//                .getOrCreate();
//
//        // Initialize Spark Streaming Context
//        JavaSparkContext sc = new JavaSparkContext(spark.sparkContext());
//        JavaStreamingContext ssc = new JavaStreamingContext(sc, new Duration(5000));
//
//        // Set Kafka parameters
//        Map<String, Object> kafkaParams = new HashMap<>();
//        kafkaParams.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "slave1:9092,slave2:9092,slave3:9092");
//        kafkaParams.put(ConsumerConfig.GROUP_ID_CONFIG, "spark-streaming-consumer");
//        kafkaParams.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        kafkaParams.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//
//        // Subscribe to the topics
//        String[] topics = {"telecom-data-call", "telecom-data-sms", "telecom-data-traffic"};
//        Map<String, Integer> topicsMap = new HashMap<>();
//        for (String topic : topics) {
//            topicsMap.put(topic, 1);
//        }
//
//        // Create a direct stream from Kafka
//        DStream<ConsumerRecord<String, String>> messages = KafkaUtils.createDirectStream(ssc,
//                LocationStrategies.PreferConsistent(),
//                ConsumerStrategies.Subscribe(topicsMap.keySet(), kafkaParams));
//
//        // Process the messages for each topic
//        messages.foreachRDD(rdd -> {
//            if (!rdd.isEmpty()) {
//                // Convert the RDD into a DataFrame
//                String jsonString = rdd.map(ConsumerRecord::value).reduce((x, y) -> x + y);
//
//                // Define the schema for each topic
//                StructType callSchema = new StructType()
//                        .add("callId", DataTypes.StringType)
//                        .add("callerNumber", DataTypes.StringType)
//                        .add("receiverNumber", DataTypes.StringType)
//                        .add("callStartTime", DataTypes.LongType)
//                        .add("callEndTime", DataTypes.LongType)
//                        .add("callDurationMillis", DataTypes.LongType)
//                        .add("callDirection", DataTypes.StringType)
//                        .add("callStatus", DataTypes.StringType)
//                        .add("stationId", DataTypes.StringType);
//
//                StructType smsSchema = new StructType()
//                        .add("smsId", DataTypes.StringType)
//                        .add("senderNumber", DataTypes.StringType)
//                        .add("receiverNumber", DataTypes.StringType)
//                        .add("smsContent", DataTypes.StringType)
//                        .add("sendTime", DataTypes.LongType)
//                        .add("sendDirection", DataTypes.StringType)
//                        .add("sendStatus", DataTypes.StringType)
//                        .add("stationId", DataTypes.StringType);
//
//                StructType trafficSchema = new StructType()
//                        .add("sessionId", DataTypes.StringType)
//                        .add("userNumber", DataTypes.StringType)
//                        .add("sessionStartTime", DataTypes.LongType)
//                        .add("sessionEndTime", DataTypes.LongType)
//                        .add("sessionDurationMillis", DataTypes.LongType)
//                        .add("applicationType", DataTypes.StringType)
//                        .add("upstreamDataVolume", DataTypes.LongType)
//                        .add("downstreamDataVolume", DataTypes.LongType)
//                        .add("networkTechnology", DataTypes.StringType)
//                        .add("stationId", DataTypes.StringType);
//
//                // Parse each message into the corresponding DataFrame based on the topic
//                for (String topic : topics) {
//                    DataFrame df = null;
//                    if (topic.equals("telecom-data-call")) {
//                        df = spark.read()
//                                .schema(callSchema)
//                                .json(rdd.map(ConsumerRecord::value).collect());
//                    } else if (topic.equals("telecom-data-sms")) {
//                        df = spark.read()
//                                .schema(smsSchema)
//                                .json(rdd.map(ConsumerRecord::value).collect());
//                    } else if (topic.equals("telecom-data-traffic")) {
//                        df = spark.read()
//                                .schema(trafficSchema)
//                                .json(rdd.map(ConsumerRecord::value).collect());
//                    }
//
//                    // Write the DataFrame to Hive
//                    if (df != null) {
//                        df.createOrReplaceTempView(topic);
//                        spark.sql("INSERT INTO TABLE telecom_data." + topic + " SELECT * FROM " + topic);
//                    }
//                }
//            }
//        });
//
//        // Start the streaming context
//        ssc.start();
//        ssc.awaitTermination();
//    }
//}
