package com.kafka_project.app.exo3_stream_processing;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Windowed;

public class StreamConsumer {

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "5min_temperature_avg");
        props.put(StreamsConfig.STATE_DIR_CONFIG, "./tmp");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        StreamsBuilder builder = new StreamsBuilder();

        // Lis depuis topic temperatures
        KStream<String, String> src = builder.stream("temperatures");
        KTable<Windowed<String>, Agg> windowSumOfNumber = src
                .selectKey((k, v) -> k +"/"+ v.split(";")[0])  // Définit la clé comme étant : batx/sallex
                .groupByKey()
                .windowedBy(TimeWindows.of(Duration.ofMinutes(5)))  // Définit la fénêtre de calcule
                .aggregate(  // Aggrège sur la clé et calcule dans un objet dédié (Agg)
                        () -> new Agg("", 0.0, 0),  //Init l'objet
                        (key, value, agg) -> {
                            agg.setKey(key);
                            agg.incSomme(Double.parseDouble(value.split(";")[2]));
                            agg.incCount(1);
                            System.out.println("DEBUG(aggregate) -> " + value + " - " + agg);
                            return agg;
                        }, Materialized.with(Serdes.String(), new AggSerd())  // Définit les Serde. Un serd dédié à Agg à été crée
                );

        windowSumOfNumber.toStream()  // Map l'aggrégation et l'envoi dans un nouveau topic
                .map(
                        (windowedKey, value) -> new KeyValue<>(
                                windowedKey.key() + "@" + windowedKey.window().startTime() + "-" + windowedKey.window().endTime(),
                                value.getKey()+":FROM "+windowedKey.window().startTime() + " TO " + windowedKey.window().endTime()+":"+value.getAvg()))
                .to("avgTopic", Produced.with(Serdes.String(), Serdes.String()));

        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));  // Ferme proprement le stream en cas d'arrêt
        System.out.println("Kafka Streams application started");
    }

    /**
     * Class de gestion de donnée pour l'aggregation
     */
    public static class Agg implements Serializable {

        private String key;
        private double somme;
        private int count;

        public Agg(String k, double s, int c) {
            this.key=k;
            this.somme = s;
            this.count = c;
        }

        public void setKey(String k){
            this.key = k;
        }

        public void incSomme(double v) {
            somme += v;
        }

        public void incCount(int v) {
            count += v;
        }

        public String getKey(){
            return this.key;
        }

        public double getSomme() {
            return somme;
        }

        public int getCount() {
            return count;
        }

        public Double getAvg(){
            return getSomme()/getCount();
        } 

        @Override
        public String toString() {
            return "{"+getKey()+", " + getSomme() + ", " + getCount() + "}";
        }

    }

    /**
     * Class a utiliser comme sérializeur et désérializeur
     */
    public static class AggSerd implements Serde<Agg> {

        @Override
        public void configure(Map<String, ?> configs, boolean isKey) {
            // No configuration needed
        }

        @Override
        public void close() {
            // No resources to close
        }

        @Override
        public Serializer<Agg> serializer() {
            return (topic, data) -> {
                if (data == null) {
                    return null;
                }
                String serialized = data.getKey()+":"+data.getSomme() + ":" + data.getCount();
                return serialized.getBytes(StandardCharsets.UTF_8);
            };
        }

        @Override
        public Deserializer<Agg> deserializer() {
            return (topic, data) -> {
                if (data == null) {
                    return null;
                }
                String[] parts = new String(data, StandardCharsets.UTF_8).split(":");
                String key = parts[0];
                double value = Double.parseDouble(parts[1]);
                int count = Integer.parseInt(parts[2]);
                return new Agg(key, value, count);
            };
        }
    }
}
