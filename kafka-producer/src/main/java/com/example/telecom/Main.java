package com.example.telecom;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;

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
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(Main.class);
    private static final String BOOTSTRAP_SERVERS = "slave1:9092,slave2:9092,slave3:9092";
    private static final String TOPIC = "telecom-data";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final int THREAD_POOL_SIZE = 8; // Number of concurrent threads

    public static void main(String[] args) {
        // Kafka Producer Setup
        KafkaProducer<String, String> producer = new KafkaProducer<>(getKafkaProperties());

        // Executor service for concurrency
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        // Process CSV files concurrently
        for (String arg : args) {
            executorService.submit(() -> processCsvFile(arg, producer));
        }

        // Shut down executor service
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

    private static Properties getKafkaProperties() {
        Properties properties = new Properties();

        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.ACKS_CONFIG, "all"); // Ensure data is fully written before confirming
        properties.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy"); // Optional: Use compression for better performance
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, 32768); // 32KB batch size (you can adjust this)
        properties.put(ProducerConfig.LINGER_MS_CONFIG, 20);     // Wait for 5ms before sending a batch of messages
        properties.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432); // 32MB默认值，确认是否足够
        properties.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 60000); // 避免快速失败
        properties.put(ProducerConfig.RETRIES_CONFIG, 3);        // 启用重试

        return properties;
    }

    public static void processCsvFile(String filePath, KafkaProducer<String, String> producer) {
        try (CSVParser csvParser = CSVFormat.DEFAULT.builder().setHeader()
                .setSkipHeaderRecord(true).get().parse(new FileReader(filePath, StandardCharsets.UTF_8))) {
            List<String> header = csvParser.getHeaderNames();

            Map<String, String> map = new HashMap<>();
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

    public static String convertToJson(Map<String, String> record) {
        try {
            return OBJECT_MAPPER.writeValueAsString(record);
        } catch (IOException e) {
            log.error("Error converting to JSON: {}", e.getMessage());
            return "{}";
        }
    }

    public static void sendToKafka(String message, KafkaProducer<String, String> producer) {
        ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC, message);
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
