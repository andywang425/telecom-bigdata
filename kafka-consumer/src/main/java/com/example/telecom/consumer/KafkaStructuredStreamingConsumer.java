package com.example.telecom.consumer;

import org.apache.spark.sql.*;
import org.apache.spark.sql.streaming.*;

import java.util.concurrent.TimeoutException;

public class KafkaStructuredStreamingConsumer {

    public static void main(String[] args) throws StreamingQueryException, TimeoutException {
        // Spark session with Hive support
        SparkSession spark = SparkSession.builder()
                .appName("Kafka to Hive Structured Streaming")
                .config("spark.sql.catalogImplementation", "hive")
                .enableHiveSupport()
                .getOrCreate();

        // Read from Kafka topic
        Dataset<Row> kafkaDF = spark.readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", "localhost:9092")
                .option("subscribe", "your-kafka-topic")
                .load();

        // Convert Kafka binary values to String
        Dataset<Row> messages = kafkaDF.selectExpr("CAST(value AS STRING) AS message");

        // Write to Hive in append mode every 10 seconds
        StreamingQuery query = messages.writeStream()
                .outputMode(OutputMode.Append())
                .format("hive")
                .option("checkpointLocation", "/tmp/spark_checkpoint")
                .queryName("KafkaToHive")
                .trigger(Trigger.ProcessingTime("10 seconds"))
                .foreachBatch((batchDF, batchId) -> {
                    batchDF.write().mode(SaveMode.Append).insertInto("your_hive_table_name");
                })
                .start();

        // Await termination of the stream
        query.awaitTermination();
    }
}