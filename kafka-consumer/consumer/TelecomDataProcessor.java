package consumer;

import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.StreamingQueryException;

import java.util.concurrent.TimeoutException;

import static org.apache.spark.sql.functions.*;

public class TelecomDataProcessor {
    public static void main(String[] args) throws StreamingQueryException, TimeoutException {
        // 创建SparkSession并启用Hive支持
        SparkSession spark = SparkSession.builder()
                .appName("TelecomDataProcessor")
                .enableHiveSupport()
                .getOrCreate();

        // 从Kafka读取数据流
        Dataset<Row> kafkaStream = spark.readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", "slave1:9092,slave2:9092,slave3:9092")
                .option("subscribe", "telecom-data")
                .load();

        // 将二进制值转换为字符串
        Dataset<String> jsonStream = kafkaStream.select(col("value").cast("string"))
                .as(Encoders.STRING());

        // 处理流数据
        StreamingQuery query = jsonStream.writeStream()
                .foreachBatch((batchDF, batchId) -> {
                    // 创建临时视图
                    batchDF.createOrReplaceTempView("temp_stream");

                    // 解析通话记录
                    Dataset<Row> callDF = spark.sql(
                            "SELECT " +
                                    "get_json_object(value, '$.callId') as callId, " +
                                    "get_json_object(value, '$.callerNumber') as callerNumber, " +
                                    "get_json_object(value, '$.receiverNumber') as receiverNumber, " +
                                    "get_json_object(value, '$.callStartTime') as callStartTime, " +
                                    "get_json_object(value, '$.callEndTime') as callEndTime, " +
                                    "cast(get_json_object(value, '$.callDurationMillis') as long) as callDurationMillis, " +
                                    "get_json_object(value, '$.callDirection') as callDirection, " +
                                    "get_json_object(value, '$.callStatus') as callStatus, " +
                                    "get_json_object(value, '$.stationId') as stationId " +
                                    "FROM temp_stream " +
                                    "WHERE get_json_object(value, '$.callId') IS NOT NULL"
                    );

                    // 解析短信记录
                    Dataset<Row> smsDF = spark.sql(
                            "SELECT " +
                                    "get_json_object(value, '$.smsId') as smsId, " +
                                    "get_json_object(value, '$.senderNumber') as senderNumber, " +
                                    "get_json_object(value, '$.receiverNumber') as receiverNumber, " +
                                    "get_json_object(value, '$.smsContent') as smsContent, " +
                                    "get_json_object(value, '$.sendTime') as sendTime, " +
                                    "get_json_object(value, '$.sendDirection') as sendDirection, " +
                                    "get_json_object(value, '$.sendStatus') as sendStatus, " +
                                    "get_json_object(value, '$.stationId') as stationId " +
                                    "FROM temp_stream " +
                                    "WHERE get_json_object(value, '$.smsId') IS NOT NULL"
                    );

                    // 解析流量记录
                    Dataset<Row> trafficDF = spark.sql(
                            "SELECT " +
                                    "get_json_object(value, '$.sessionId') as sessionId, " +
                                    "get_json_object(value, '$.userNumber') as userNumber, " +
                                    "get_json_object(value, '$.sessionStartTime') as sessionStartTime, " +
                                    "get_json_object(value, '$.sessionEndTime') as sessionEndTime, " +
                                    "cast(get_json_object(value, '$.sessionDurationMillis') as long) as sessionDurationMillis, " +
                                    "get_json_object(value, '$.applicationType') as applicationType, " +
                                    "cast(get_json_object(value, '$.upstreamDataVolume') as long) as upstreamDataVolume, " +
                                    "cast(get_json_object(value, '$.downstreamDataVolume') as long) as downstreamDataVolume, " +
                                    "get_json_object(value, '$.networkTechnology') as networkTechnology, " +
                                    "get_json_object(value, '$.stationId') as stationId " +
                                    "FROM temp_stream " +
                                    "WHERE get_json_object(value, '$.sessionId') IS NOT NULL"
                    );

                    // 写入Hive表
                    if (!callDF.isEmpty()) {
                        callDF.write()
                                .format("hive")
                                .mode("append")
                                .saveAsTable("telecom-data.call");
                    }

                    if (!smsDF.isEmpty()) {
                        smsDF.write()
                                .format("hive")
                                .mode("append")
                                .saveAsTable("telecom-data.sms");
                    }

                    if (!trafficDF.isEmpty()) {
                        trafficDF.write()
                                .format("hive")
                                .mode("append")
                                .saveAsTable("telecom-data.traffic");
                    }
                })
                .start();

        query.awaitTermination();
    }
}
