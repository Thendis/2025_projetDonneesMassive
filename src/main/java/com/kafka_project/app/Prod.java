package com.kafka_project.app;
import java.util.Properties;

import java.util.concurrent.CountDownLatch;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;


/**
 * Hello world!
 */
public class Prod {
    private static final int NUM_MESSAGES = 200; //1_000_000
    private static final int NUM_PARTITIONS = 2;
    private static final int NUM_PRODUCERS = 500; // 10_000
    private static final String TOPIC = "topic1";

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
    
        
   
   /* Producer<String, String> producer1 = new KafkaProducer<>(props);
    Producer<String, String> producer2 = new KafkaProducer<>(props);

    
    for(int i = 0; i < 100; i++){
        producer1.send(new ProducerRecord<>("topic1", Integer.toString(i), Integer.toString(i)));
        producer2.send(new ProducerRecord<>("topic1", Integer.toString(i), Integer.toString(i)));
    }

    producer1.close();
    producer2.close();*/
    /*
    CountDownLatch latch = new CountDownLatch(NUM_PRODUCERS);

        for (int i = 0; i < NUM_PRODUCERS; i++) {
            new Thread(() -> {
                try (Producer<String, String> producer = new KafkaProducer<>(props)) {
                    for (int j = 0; j < NUM_MESSAGES / NUM_PRODUCERS; j++) {
                        int messageNumber = (int) (Math.random() * NUM_MESSAGES);
                        int partition = messageNumber % NUM_PARTITIONS;
                        producer.send(new ProducerRecord<>(TOPIC, partition, Integer.toString(messageNumber), Integer.toString(messageNumber)));
                    }
                } finally {
                    latch.countDown();
                }
            }).start();
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }*/

       for (int i = 0; i < NUM_PRODUCERS; i++) {
            new Thread(() -> {
                try (Producer<String, String> producer = new KafkaProducer<>(props)) {
                    for (int j = 0; j < NUM_MESSAGES / NUM_PRODUCERS; j++) {
                        int messageNumber = (int) (Math.random() * 1001);
                        int partition = messageNumber % NUM_PARTITIONS;
                        producer.send(new ProducerRecord<>(TOPIC, partition, Integer.toString(messageNumber), Integer.toString(messageNumber)));
                    }
                }
            }).start();
        }
    
    }
}
