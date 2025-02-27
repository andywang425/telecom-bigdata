package consumer;

import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;

public class Deepseek2 {
    public static void main(String[] args) throws Exception {
        // 创建SparkSession并启用Hive支持
        SparkSession spark = SparkSession.builder()
                .appName("TelecomDataProcessor")
                //.config("hive.metastore.uris", "thrift://master:9083") // 可能没用，改为放配置文件
                .config("spark.sql.warehouse.dir", "/user/hive/warehouse")
                .config("spark.sql.hive.metastore.version", "4.0.0") // max 3.1.3
                .enableHiveSupport()
                .getOrCreate();

//        // 创建数据库和表
//        spark.sql("CREATE DATABASE IF NOT EXISTS `telecom-data`");
//        spark.sql("USE `telecom-data`");
//
//        // 创建通话记录表
//        spark.sql("CREATE TABLE IF NOT EXISTS call (" +
//                "callId STRING, callerNumber STRING, receiverNumber STRING, " +
//                "callStartTime BIGINT, callEndTime BIGINT, callDurationMillis BIGINT, " +
//                "callDirection STRING, callStatus STRING, stationId STRING) " +
//                "STORED AS ORC");
//
//        // 创建短信记录表
//        spark.sql("CREATE TABLE IF NOT EXISTS sms (" +
//                "smsId STRING, senderNumber STRING, receiverNumber STRING, " +
//                "smsContent STRING, sendTime BIGINT, sendDirection STRING, " +
//                "sendStatus STRING, stationId STRING) " +
//                "STORED AS ORC");
//
//        // 创建流量记录表
//        spark.sql("CREATE TABLE IF NOT EXISTS traffic (" +
//                "sessionId STRING, userNumber STRING, sessionStartTime BIGINT, " +
//                "sessionEndTime BIGINT, sessionDurationMillis BIGINT, applicationType STRING, " +
//                "upstreamDataVolume DOUBLE, downstreamDataVolume DOUBLE, networkTechnology STRING, " +
//                "stationId STRING) " +
//                "STORED AS ORC");

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
                .select(org.apache.spark.sql.functions.from_json(
                        org.apache.spark.sql.functions.col("json"), callSchema).alias("data"))
                .select("data.*");

        // 处理短信数据
        Dataset<Row> smsData = kafkaDF
                .filter("topic = 'telecom-data-sms'")
                .selectExpr("CAST(value AS STRING) as json")
                .select(org.apache.spark.sql.functions.from_json(
                        org.apache.spark.sql.functions.col("json"), smsSchema).alias("data"))
                .select("data.*");

        // 处理流量数据
        Dataset<Row> trafficData = kafkaDF
                .filter("topic = 'telecom-data-traffic'")
                .selectExpr("CAST(value AS STRING) as json")
                .select(org.apache.spark.sql.functions.from_json(
                        org.apache.spark.sql.functions.col("json"), trafficSchema).alias("data"))
                .select("data.*");

        // 启动三个流式写入任务
        StreamingQuery callQuery = callData.writeStream()
                .outputMode("append")
//                .option("checkpointLocation", "/tmp/checkpoint/call") // checkpointLocation 可能没用
                .foreachBatch((batchDF, batchId) -> {
                    batchDF.write().mode("append").saveAsTable("telecom-data.call");
                })
                .start();

        StreamingQuery smsQuery = smsData.writeStream()
                .outputMode("append")
//                .option("checkpointLocation", "/tmp/checkpoint/sms")
                .foreachBatch((batchDF, batchId) -> {
                    batchDF.write().mode("append").saveAsTable("telecom-data.sms");
                })
                .start();

        StreamingQuery trafficQuery = trafficData.writeStream()
                .outputMode("append")
//                .option("checkpointLocation", "/tmp/checkpoint/traffic")
                .foreachBatch((batchDF, batchId) -> {
                    batchDF.write().mode("append").saveAsTable("telecom-data.traffic");
                })
                .start();

        // 保持程序运行
        callQuery.awaitTermination();
        smsQuery.awaitTermination();
        trafficQuery.awaitTermination();
    }
}
