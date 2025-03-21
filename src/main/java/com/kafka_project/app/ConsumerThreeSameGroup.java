package com.kafka_project.app;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

public class ConsumerThreeSameGroup {
    //indicateur partagé pour signaler l'arrêt des threads (fin du traitement de toutes les données)
    private static final AtomicBoolean running = new AtomicBoolean(true);

    // Création d'un BufferedWriter pour écrire dans le fichier
    private static BufferedWriter writer;
    
    public static void main(String[] args) {
        // Initialisation du BufferedWriter pour écraser le fichier à chaque exécution et afficher dans un fichier ..txt les résultat
        try {
            // Ouvre ou crée le fichier ResultatCosummerTwoSameGroup.txt et l'écrase à chaque exécution
            writer = new BufferedWriter(new FileWriter("Resultat/ResultatCosummerThreeSameGroup.txt"));
        } catch (IOException e) {
            e.printStackTrace();
            return;  // Si l'ouverture du fichier échoue, on arrête le programme
        }

        // Propriétés du consommateur
        Properties props = new Properties();
        props.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "group1");
        props.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

       // Créer trois consommateurs avec les mêmes propriétés
        KafkaConsumer<String, String> consumer1 = new KafkaConsumer<>(props);
        KafkaConsumer<String, String> consumer2 = new KafkaConsumer<>(props);
        KafkaConsumer<String, String> consumer3 = new KafkaConsumer<>(props);

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

        // Fermer le BufferedWriter pour s'assurer que toutes les données sont écrites dans le fichier
        try {
            writer.flush();  // S'assurer que toutes les données sont écrites dans le fichier
            writer.close();  // Fermer le fichier
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    private static void consumeMessages(KafkaConsumer<String, String> consumer, List<ConsumerRecord<String, String>> buffer, int minBatchSize) {
       int consumedCount = 0; // Pour compter le nombre de messages consommés
       while (running.get()) {
            long startTime = System.nanoTime();

             // Consommer les messages avec un délai de 7 secondes
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(7000));

            if (records.isEmpty()) {
                // Si aucun message n'est reçu, vérifier si nous devons arrêter
                if (buffer.isEmpty()) {
                    running.set(false);
                    break;
                }
            } else {
                for (ConsumerRecord<String, String> record : records) {
                    buffer.add(record);
                    consumedCount++; // Compter les messages consomméss
                }
                if (buffer.size() >= minBatchSize) {
                    for (ConsumerRecord<String, String> record : buffer) {
                        // Afficher dans le terminal
                        System.out.println(record.value());
                         try {
                            // Écrire dans le fichier texte
                            writer.write(record.value());
                            writer.newLine();  // Ajouter une nouvelle ligne après chaque message
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    // Commit des offsets pour chaque partition
                    commitOffsets(consumer, records);
                    buffer.clear();
                }
            }
            if (consumedCount % 10000 == 0) { // Affiche un message toutes les 10000 valeurs
            System.out.println("Messages consommés: " + consumedCount);
            }
        } 
    }
    private static void commitOffsets(KafkaConsumer<String, String> consumer, ConsumerRecords<String, String> records) {
        // Créer une Map pour stocker les offsets
        Map<TopicPartition, OffsetAndMetadata> offsets = new java.util.HashMap<>();
        
        // Pour chaque partition traitée, récupérer l'offset et ajouter à la map
        for (ConsumerRecord<String, String> record : records) {
            TopicPartition partition = new TopicPartition(record.topic(), record.partition());
            long offset = record.offset() + 1;  // Le offset suivant est celui après le message consommé
            OffsetAndMetadata offsetAndMetadata = new OffsetAndMetadata(offset);
            offsets.put(partition, offsetAndMetadata);
        }

        // Committer les offsets manuellement
        consumer.commitSync(offsets);
    }
}