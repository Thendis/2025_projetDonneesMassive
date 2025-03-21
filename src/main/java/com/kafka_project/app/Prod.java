
package com.kafka_project.app;

import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

public class Prod {
    
    private static final String TOPIC = "topic1"; 
    private static final String BOOTSTRAP_SERVERS = "localhost:9092"; 

    public static void main(String[] args) {
        
        int totalMessages = 1000000;  
        int numProducers = 100; 

        // Création de producteurs parallèles
        for (int i = 0; i < numProducers; i++) {
            final int producerId = i;
            Thread producerThread = new Thread(() -> {
                // Propriétés pour chaque producteur 
                Properties props = new Properties();
                props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
                props.put(ProducerConfig.ACKS_CONFIG, "all");
                props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
                props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

                Producer<String, String> producer = new KafkaProducer<>(props);

                int messagesPerProducer = totalMessages / numProducers;
                for (int j = producerId * messagesPerProducer; j < (producerId + 1) * messagesPerProducer; j++) {
                    producer.send(new ProducerRecord<>(TOPIC, Integer.toString(j), Integer.toString(j)));
                }

                producer.close();
            });
            producerThread.start();
        }
    }
}