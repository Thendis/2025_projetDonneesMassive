package com.kafka_project.app.exo3_stream_processing;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Properties;
import java.util.Random;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

public class Batiment implements Runnable, Serializable {

    private String nomBat;
    private int nbSalle = 5;
    Random rd = new Random();
    Producer<String, String> producer;

    public Batiment(String nomBAt_) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        
        producer = new KafkaProducer<>(props);

        nomBat = nomBAt_;
        
    }

    @Override
    public void run() {
        while (true) {
            LocalDateTime tsp = LocalDateTime.now();
            final String sep = ";";
            //Récupération des températures par salle
            for (int salle_id = 0;salle_id<nbSalle;salle_id++){
                String to_send = "salle"+salle_id+sep+LocalDateTime.now()+sep+rd.nextDouble(0, 35);
                System.out.println("From "+nomBat+" -> "+to_send);
                producer.send(new ProducerRecord<>("temperatures", nomBat, to_send));
            }  

            //Attente avant prochaine collecte
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
            }
        }
    }

   @Override 
    public String toString(){
        String ret = "\t"+nomBat;
        return ret;
    } 

}
