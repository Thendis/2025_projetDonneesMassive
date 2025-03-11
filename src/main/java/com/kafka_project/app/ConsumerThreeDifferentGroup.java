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
import java.util.concurrent.atomic.AtomicBoolean;

public class ConsumerThreeDifferentGroup {

    //indicateur partagé pour signaler l'arrêt des threads (fin du traitement de toutes les données)
    private static final AtomicBoolean running = new AtomicBoolean(true);

    public static void main(String[] args) {

        // Propriétés pour le premier consommateur
        Properties props1 = new Properties();
        props1.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props1.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "group1");
        props1.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props1.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props1.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");


        // Propriétés pour le deuxième consommateur
        Properties props2 = new Properties();
        props2.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props2.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "group2");
        props2.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props2.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props2.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
       
        // Propriétés pour le troisième consommateur
        Properties props3 = new Properties();
        props3.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props3.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "group3");
        props3.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props3.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props3.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");



       // Créer trois consommateurs avec les mêmes propriétés
        KafkaConsumer<String, String> consumer1 = new KafkaConsumer<>(props1);
        KafkaConsumer<String, String> consumer2 = new KafkaConsumer<>(props2);
        KafkaConsumer<String, String> consumer3 = new KafkaConsumer<>(props3);

         // S'abonner aux mêmes sujets
        consumer1.subscribe(Arrays.asList("topic1", "topic2"));
        consumer2.subscribe(Arrays.asList("topic1", "topic2"));
        consumer3.subscribe(Arrays.asList("topic1", "topic2"));

        final int minBatchSize = 50;  // Nombre minimum de messages à pull par le consommateur
        List<ConsumerRecord<String, String>> buffer1 = new ArrayList<>();
        List<ConsumerRecord<String, String>> buffer2 = new ArrayList<>();
        List<ConsumerRecord<String, String>> buffer3 = new ArrayList<>();

        // Lancer les consommateurs dans des threads séparés
        Thread consumerThread1 = new Thread(() -> consumeMessages(consumer1, buffer1, minBatchSize));
        Thread consumerThread2 = new Thread(() -> consumeMessages(consumer2, buffer2, minBatchSize));
        Thread consumerThread3 = new Thread(() -> consumeMessages(consumer3, buffer3, minBatchSize));
   
        consumerThread1.start();
        consumerThread2.start();
        consumerThread3.start();

        // Attendre que les threads se terminent
        try {
            consumerThread1.join();
            consumerThread2.join();
            consumerThread3.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Fermer les consommateurs
        consumer1.close();
        consumer2.close();
        consumer3.close();
    }

     private static void consumeMessages(KafkaConsumer<String, String> consumer, List<ConsumerRecord<String, String>> buffer, int minBatchSize) {
       while (running.get()) {
            long startTime = System.nanoTime();

            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));

            if (records.isEmpty()) {
                // Si aucun message n'est reçu, vérifier si nous devons arrêter
                if (buffer.isEmpty()) {
                    running.set(false);
                    break;
                }
            } else {
                for (ConsumerRecord<String, String> record : records) {
                    buffer.add(record);
                }
                if (buffer.size() >= minBatchSize) {
                    // insertIntoDb(buffer);
                    for (ConsumerRecord<String, String> record : buffer) {
                        System.out.println(record.value());
                    }
                    consumer.commitSync();
                    buffer.clear();
                }
            }
        } 
    }
}