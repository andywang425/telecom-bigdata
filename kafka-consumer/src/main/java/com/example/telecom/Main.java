package com.example.telecom;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.OutputMode;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.sql.types.StructType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.from_json;

public class Main {
    private static final Logger log = LoggerFactory.getLogger("TelecomDataProcessor");
    private static final List<StreamingQuery> activeQueries = new ArrayList<>();
    private static SparkSession spark;

    public static void main(String[] args) {
        log.info("Starting Telecom Data Processor...");

        try {
            // 初始化 SparkSession
            spark = createSparkSession();

            // 从 Kafka 获取输入流
            Dataset<Row> kafkaDF = readFromKafka(spark);

            // 处理并启动流式查询
            startStreamingQueries(kafkaDF);

            // 阻塞主线程，等待流式任务终止
            waitForQueriesTermination();

        } catch (Exception e) {
            log.error("Unexpected error:", e);
        } finally {
            cleanupResources();
        }
    }

    /**
     * 创建 SparkSession
     */
    private static SparkSession createSparkSession() {
        return SparkSession.builder()
                .appName("TelecomDataProcessor")
                .config("spark.sql.warehouse.dir", "/user/hive/warehouse")
                .enableHiveSupport()
                .getOrCreate();
    }

    /**
     * 从 Kafka 读取数据
     *
     * @param spark SparkSession
     */
    private static Dataset<Row> readFromKafka(SparkSession spark) {
        return spark.readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", "slave1:9092,slave2:9092,slave3:9092")
                .option("subscribe", "telecom-data-call,telecom-data-sms,telecom-data-traffic")
                .load();
    }

    /**
     * 启动所有流式查询
     *
     * @param kafkaDF Kafka输入流
     */
    private static void startStreamingQueries(Dataset<Row> kafkaDF)
            throws TimeoutException {
        StreamingQuery callQuery = processAndWriteStream(kafkaDF, "telecom-data-call", Schemas.CALL, "call");
        StreamingQuery smsQuery = processAndWriteStream(kafkaDF, "telecom-data-sms", Schemas.SMS, "sms");
        StreamingQuery trafficQuery = processAndWriteStream(kafkaDF, "telecom-data-traffic", Schemas.TRAFFIC, "traffic");

        activeQueries.add(callQuery);
        activeQueries.add(smsQuery);
        activeQueries.add(trafficQuery);

        log.info("All streaming queries started.");
    }

    /**
     * 处理数据并启动流式写入
     *
     * @param kafkaDF   Kafka输入流
     * @param topic     kafka主题
     * @param schema    json模式
     * @param tableName hive表名
     */
    private static StreamingQuery processAndWriteStream(Dataset<Row> kafkaDF, String topic, StructType schema, String tableName)
            throws TimeoutException {
        Dataset<Row> data = kafkaDF
                .filter(col("topic").equalTo(topic))
                .selectExpr("CAST(value AS STRING) as json")
                .select(from_json(col("json"), schema).alias("data"))
                .select("data.*");

        return data.writeStream()
                .outputMode(OutputMode.Append())
                .foreachBatch((batchDF, batchId) -> {
                    batchDF.write()
                            .format("hive")
                            .mode(SaveMode.Append)
                            .saveAsTable("`telecom_data`.`" + tableName + "`");
                })
                .start();
    }

    /**
     * 等待所有查询终止
     * <p>
     * 如果没有异常，查询永远不会自己终止
     */
    private static void waitForQueriesTermination() {
        activeQueries.forEach(query -> {
            try {
                query.awaitTermination();
            } catch (StreamingQueryException e) {
                log.error("Streaming query failed:", e);
            }
        });
    }

    /**
     * 清理资源
     */
    private static void cleanupResources() {
        log.info("Cleaning up resources...");

        // 停止所有活跃的流式查询
        activeQueries.forEach(query -> {
            try {
                if (query != null && query.isActive()) {
                    query.stop();
                }
            } catch (Exception e) {
                log.error("Error stopping query:", e);
            }
        });
        activeQueries.clear();

        // 关闭 SparkSession
        if (spark != null) {
            try {
                spark.close();
                log.info("SparkSession closed successfully.");
            } catch (Exception e) {
                log.error("Error closing SparkSession:", e);
            }
        }
    }
}