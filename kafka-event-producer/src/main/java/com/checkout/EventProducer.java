package com.checkout;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import java.util.Random;

import java.util.Properties;

public class EventProducer {

    private static final String[] FIRST_NAMES = {"Alice", "Bob", "Charlie", "David", "Emma", "Frank", "Grace", "Henry"};
    private static final String[] LAST_NAMES = {"Anderson", "Brown", "Clark", "Davis", "Evans", "Fisher", "Garcia", "Harris"};
    private static final String[] DOMAINS = {"example.com", "domain.com", "website.org", "example.org", "test.net"};

    public void produce() {

        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:29092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        Producer<String, String> producer = new KafkaProducer<>(properties);

        ObjectMapper objectMapper = new ObjectMapper();

        while(true)
        {
            Event event = new Event(generateUserId(), generatePostalCode(), generateWebsiteUrl(), System.currentTimeMillis());

            try
            {
                ProducerRecord<String, String> record = new ProducerRecord<>("events", event.getPostcode(), event.toString());

                producer.send(record);

                producer.flush();

                System.out.println("Produced : " + event.toString());

                Thread.sleep(1000);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Close the Kafka producer
        //producer.close();
    }

    public static String generateWebsiteUrl() {
        Random random = new Random();

        String domain = DOMAINS[random.nextInt(DOMAINS.length)];

        return "http://" + domain;
    }

    public int generateUserId() {
        Random random = new Random();

        return random.nextInt((2000 - 1000) + 1) + 1000;
    }

    private String generatePostalCode() {
        Random random = new Random();

        // Keeping only few postcodes to see better aggregated results
        String letters = "SW";
        String digits = "12";

        StringBuilder postalCode = new StringBuilder();

        postalCode.append(letters.charAt(random.nextInt(letters.length())));
        postalCode.append(letters.charAt(random.nextInt(letters.length())));

        postalCode.append(digits.charAt(random.nextInt(digits.length())));
        postalCode.append(digits.charAt(random.nextInt(letters.length())));

        return postalCode.toString();
    }

}
