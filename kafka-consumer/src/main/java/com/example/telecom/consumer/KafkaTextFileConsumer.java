package com.example.telecom.consumer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Properties;

public class KafkaTextFileConsumer {

    public static void main(String[] args) {
        // Kafka Consumer properties
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"); // Kafka broker address
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-consumer-group"); // Consumer group ID
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // Read from the beginning of the topic if no offset is committed

        // Create the consumer
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        // Subscribe to the topic (same topic you used in the FileStreamSource connector)

        try (consumer) {
            consumer.subscribe(Collections.singletonList("your-kafka-topic-name"));
            System.out.println("Consuming messages from Kafka topic...");
            while (true) {
                // Poll for new records (messages)
                consumer.poll(Duration.of(100, ChronoUnit.MILLIS)).forEach(record -> {
                    // Print the message to the console
                    System.out.println("Received message: " + record.value());
                });
            }
        }
        // Close the consumer to release resources
    }
}
