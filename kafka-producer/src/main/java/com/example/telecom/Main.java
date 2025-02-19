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
import java.nio.charset.StandardCharsets;
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
    private static final int THREAD_POOL_SIZE = 8;

    public static void main(String[] args) {
        // 初始化 Kafka 生产者
        KafkaProducer<String, String> producer = new KafkaProducer<>(getKafkaProperties());

        // 创建线程池
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        // 多线程处理 CSV 文件（每个线程处理一个文件）
        for (String arg : args) {
            executorService.submit(() -> processCsvFile(arg, producer));
        }

        executorService.shutdown();
        try {
            while (!executorService.awaitTermination(1, TimeUnit.MINUTES)) {
                log.info("Waiting for remaining tasks...");
            }
        } catch (InterruptedException e) {
            log.info("Interrupted while waiting for tasks to finish");
        }

        producer.close();
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
     * @param filePath 文件路径
     * @param producer KafkaProducer
     */
    public static void processCsvFile(String filePath, KafkaProducer<String, String> producer) {
        try (CSVParser csvParser = CSVFormat.DEFAULT.builder().setHeader()
                .setSkipHeaderRecord(true).get().parse(new FileReader(filePath, StandardCharsets.UTF_8))) {
            // 获取 CSV 文件的表头
            List<String> header = csvParser.getHeaderNames();

            Map<String, String> map = new HashMap<>();
            // 使用流处理遍历 CSV 文件，将每行数据转换为 JSON 并发送到 Kafka
            csvParser.stream().forEach(record -> {
                for (String name : header) {
                    map.put(name, record.get(name));
                }

                sendToKafka(convertToJson(map), producer);
                map.clear();
            });

        } catch (IOException e) {
            log.error("Error reading CSV file: {}", e.getMessage());
        }
    }

    /**
     * 将 Map 转换为 JSON 字符串
     * @param record Map
     */
    private static String convertToJson(Map<String, String> record) {
        try {
            return OBJECT_MAPPER.writeValueAsString(record);
        } catch (IOException e) {
            log.error("Error converting to JSON: {}", e.getMessage());
            return "{}";
        }
    }

    /**
     * 发送消息到 Kafka
     * @param message 消息
     * @param producer KafkaProducer
     */
    private static void sendToKafka(String message, KafkaProducer<String, String> producer) {
        ProducerRecord<String, String> record = new ProducerRecord<>("telecom-data", message);

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
