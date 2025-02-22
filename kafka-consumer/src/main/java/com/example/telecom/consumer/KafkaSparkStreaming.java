package com.example.telecom.consumer;

import org.apache.spark.sql.*;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.sql.types.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;

import java.util.*;
import java.util.concurrent.TimeoutException;

public class KafkaSparkStreaming {
    public static void main(String[] args) throws InterruptedException, TimeoutException, StreamingQueryException {
        // Initialize Spark Session and Streaming Context
        SparkSession spark = SparkSession.builder()
                .appName("KafkaSparkStreaming")
                .enableHiveSupport()
                .getOrCreate();

        // Kafka configuration
        String brokers = "slave1:9092,slave2:9092,slave3:9092";
        String topic = "telecom-data";
        String groupId = "spark-streaming-group";

        // Kafka parameters
        Map<String, Object> kafkaParams = new HashMap<>();
        kafkaParams.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
        kafkaParams.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        kafkaParams.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        kafkaParams.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        // Define Kafka Stream
        Dataset<Row> rawStream = spark.readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", brokers)
                .option("subscribe", topic)
                .load();

        // Parse the Kafka value (JSON message)
        Dataset<Row> parsedStream = rawStream.selectExpr("CAST(value AS STRING) AS json")
                .select(functions.from_json(functions.col("json"), getSchema()).as("data"))
                .select("data.*");

        // Write parsed data to appropriate Hive tables
        parsedStream.writeStream()
                .foreachBatch((batchDF, batchId) -> {
                    // Process each batch
                    batchDF.createOrReplaceTempView("temp_table");

                    // Insert into appropriate tables based on the message type
                    spark.sql("INSERT INTO TABLE telecom_data.call SELECT * FROM temp_table WHERE message_type = 'call'");
                    spark.sql("INSERT INTO TABLE telecom_data.sms SELECT * FROM temp_table WHERE message_type = 'sms'");
                    spark.sql("INSERT INTO TABLE telecom_data.traffic SELECT * FROM temp_table WHERE message_type = 'traffic'");
                })
                .outputMode("append")
                .start()
                .awaitTermination();
    }

    private static StructType getSchema() {
        // Define schema for parsing the JSON messages
        return new StructType()
                .add("message_type",  DataTypes.StringType, false)
                .add("callId",  DataTypes.StringType, true)
                .add("callerNumber",  DataTypes.StringType, true)
                .add("receiverNumber",  DataTypes.StringType, true)
                .add("callStartTime",  DataTypes.StringType, true)
                .add("callEndTime",  DataTypes.StringType, true)
                .add("callDurationMillis",  DataTypes.LongType, true)
                .add("callDirection",  DataTypes.StringType, true)
                .add("callStatus",  DataTypes.StringType, true)
                .add("stationId",  DataTypes.StringType, true)
                .add("smsId",  DataTypes.StringType, true)
                .add("senderNumber",  DataTypes.StringType, true)
                .add("smsContent",  DataTypes.StringType, true)
                .add("sendTime",  DataTypes.StringType, true)
                .add("sendDirection",  DataTypes.StringType, true)
                .add("sendStatus",  DataTypes.StringType, true)
                .add("sessionId",  DataTypes.StringType, true)
                .add("userNumber",  DataTypes.StringType, true)
                .add("sessionStartTime",  DataTypes.StringType, true)
                .add("sessionEndTime",  DataTypes.StringType, true)
                .add("sessionDurationMillis",  DataTypes.LongType, true)
                .add("applicationType",  DataTypes.StringType, true)
                .add("upstreamDataVolume",  DataTypes.LongType, true)
                .add("downstreamDataVolume",  DataTypes.LongType, true)
                .add("networkTechnology",  DataTypes.StringType, true);
    }
}
