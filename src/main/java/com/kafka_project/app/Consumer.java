package com.kafka_project.app;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

public class Consumer {
    public static void main(String[] args) {

        // Propriétés du consommateur
        Properties props = new Properties();
        props.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "test");
        props.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

        // Créer un consommateur avec la propriété
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        // S'abonner aux mêmes sujets
        consumer.subscribe(Arrays.asList("topic1", "topic2"));
        final int minBatchSize = 50;  // Nombre minimum de message a pull par le consumer
        List <ConsumerRecord <String, String>> buffer = new ArrayList<>();
        
        while(true) {
            ConsumerRecords<String, String> records =
                consumer.poll(Duration.ofMillis(100000));
            for (ConsumerRecord<String,String> record : records){
                buffer.add(record);
            }
            if(buffer.size() >= minBatchSize){
                //insertIntoDb(buffer);
                for (ConsumerRecord<String, String> record : buffer) {
            System.out.println("Message Value: " + record.value());
        }
                consumer.commitSync();
                buffer.clear();

            }
        }
   

    }
    
}