package com.example.telecom;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final int THREAD_POOL_SIZE = 3;
    private static final Map<String, Class<?>> FIELD_TYPE_MAP = new HashMap<>();

    static {
        FIELD_TYPE_MAP.put("callStartTime", Long.class);
        FIELD_TYPE_MAP.put("callEndTime", Long.class);
        FIELD_TYPE_MAP.put("callDurationMillis", Long.class);
        FIELD_TYPE_MAP.put("sendTime", Long.class);
        FIELD_TYPE_MAP.put("sessionStartTime", Long.class);
        FIELD_TYPE_MAP.put("sessionEndTime", Long.class);
        FIELD_TYPE_MAP.put("sessionDurationMillis", Long.class);
        FIELD_TYPE_MAP.put("upstreamDataVolume", Long.class);
        FIELD_TYPE_MAP.put("downstreamDataVolume", Long.class);
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            log.error("Usage: <call.csv> <sms.csv> <traffic.csv>");
            System.exit(1);
        }

        // 初始化 Kafka 生产者
        KafkaProducer<String, String> producer = new KafkaProducer<>(getKafkaProperties());

        // 创建线程池
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        // 多线程处理 CSV 文件（每个线程处理一个文件）
        executorService.submit(() -> processCsvFile("telecom-data-call", args[0], producer));
        executorService.submit(() -> processCsvFile("telecom-data-sms", args[1], producer));
        executorService.submit(() -> processCsvFile("telecom-data-traffic", args[2], producer));

        executorService.shutdown();
        try {
            do {
                log.info("Waiting for remaining tasks...");
            } while (!executorService.awaitTermination(1, TimeUnit.MINUTES));
        } catch (InterruptedException e) {
            log.info("Interrupted while waiting for tasks to finish");
        }

        producer.close();
        log.info("All tasks completed.");
    }

    /**
     * 获取 Kafka 生产者配置
     */
    private static Properties getKafkaProperties() {
        Properties properties = new Properties();

        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "slave1:9092,slave2:9092,slave3:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.ACKS_CONFIG, "all"); // leader将等待完整的同步副本来确认记录
        properties.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy"); // 使用压缩
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, 32768); // 批量发送大小 32KB
        properties.put(ProducerConfig.LINGER_MS_CONFIG, 10); // 在批量发送前等10ms
        properties.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 67108864); // 缓冲区大小 64MB
        properties.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 60000); // 阻塞时间1分钟
        properties.put(ProducerConfig.RETRIES_CONFIG, 3); // 重试次数3

        return properties;
    }

    /**
     * 处理 CSV 文件
     *
     * @param topic    kafka topic
     * @param filePath 文件路径
     * @param producer KafkaProducer
     */
    public static void processCsvFile(String topic, String filePath, KafkaProducer<String, String> producer) {
        log.info("Processing CSV file: {}", filePath);

        try (CSVParser csvParser = CSVFormat.DEFAULT.builder().setHeader()
                .setSkipHeaderRecord(true).get().parse(new FileReader(filePath))) {
            // 获取 CSV 文件的表头
            List<String> header = csvParser.getHeaderNames();

            Map<String, Object> map = new HashMap<>();
            // 使用流处理遍历 CSV 文件，将每行数据转换为 JSON 并发送到 Kafka
            csvParser.stream().forEach(record -> {
                for (String name : header) {
                    String value = record.get(name);

                    // 判断是否需要类型转换
                    if (FIELD_TYPE_MAP.containsKey(name) && value != null && !value.isEmpty()) {
                        // 获取字段类型
                        Class<?> fieldType = FIELD_TYPE_MAP.get(name);
                        // 类型转换
                        if (fieldType.equals(Long.class)) {
                            map.put(name, Long.parseLong(value));
                        } else if (fieldType.equals(Integer.class)) {
                            map.put(name, Integer.parseInt(value));
                        } else if (fieldType.equals(Double.class)) {
                            map.put(name, Double.parseDouble(value));
                        }
                    } else {
                        // 保持字符串类型
                        map.put(name, value);
                    }
                }

                sendToKafka(topic, convertToJson(map), producer);
                map.clear();
            });

        } catch (IOException e) {
            log.error("Error reading CSV file: {}", e.getMessage());
        }
    }

    /**
     * 将 Map 转换为 JSON 字符串
     *
     * @param record Map
     */
    private static String convertToJson(Map<String, Object> record) {
        try {
            return OBJECT_MAPPER.writeValueAsString(record);
        } catch (IOException e) {
            log.error("Error converting to JSON: {}", e.getMessage());
            return "{}";
        }
    }

    /**
     * 发送消息到 Kafka
     *
     * @param message  消息
     * @param producer KafkaProducer
     */
    private static void sendToKafka(String topic, String message, KafkaProducer<String, String> producer) {
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, message);

        producer.send(record, (metadata, exception) -> {
            if (exception != null) {
                log.error("Error sending message: {}", exception.getMessage());
            } else {
                log.debug("Sent Message to topic {}, partition {}, offset {}",
                        metadata.topic(), metadata.partition(), metadata.offset());
            }
        });
    }
}
