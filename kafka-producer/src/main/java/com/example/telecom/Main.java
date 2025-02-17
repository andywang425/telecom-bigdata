package com.example.telecom;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class Main {

    private static final String BOOTSTRAP_SERVERS = "localhost:9092"; // Kafka server address
    private static final String TOPIC = "your-topic"; // Kafka topic name

    public static void main(String[] args) throws Exception {
        // Create Kafka producer
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

        // Read and process CSV files
        processCsvFile("call.csv", producer);
        processCsvFile("sms.csv", producer);
        processCsvFile("traffic.csv", producer);

        producer.close();
    }

    public static void processCsvFile(String filePath, KafkaProducer<String, String> producer) {



        try (CSVParser csvParser =  CSVFormat.DEFAULT.builder().setHeader()
                             .setSkipHeaderRecord(true).get().parse(new FileReader(filePath))) {
            List<String> header = csvParser.getHeaderNames();

//            if (header == null) {
//                System.err.println("No data in file: " + filePath);
//                return;
//            }

            // Process each record in the CSV file
            List<CSVRecord> records = csvParser.getRecords();
            for (CSVRecord record : records) {
                // Create a map for the CSV row to JSON conversion
                StringBuilder jsonString = new StringBuilder("{");
                for (String name : header) {
                    jsonString.append("\"").append(name).append("\": \"").append(record.get(name)).append("\",");
                }

                // Remove trailing comma and close the JSON object
                jsonString.deleteCharAt(jsonString.length() - 1);
                jsonString.append("}");

                // Send the JSON message to Kafka
                sendToKafka(jsonString.toString(), producer);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendToKafka(String message, KafkaProducer<String, String> producer) {
        // Use an asynchronous callback for non-blocking message send
        ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC, message);
        producer.send(record, (metadata, exception) -> {
            if (exception != null) {
                System.err.println("Error sending message: " + exception.getMessage());
            }
        });

    }
}