package com.kafka_project.app.exo3_stream_processing;

import java.util.HashMap;

public class ThreadProd {

    public static void main(String[] args) {
        final int nb_bat = 2;

        HashMap<String, Thread> bats = new HashMap<>();
        for (int bat_num = 0; bat_num < nb_bat; bat_num++) {
            bats.put("bat" + bat_num, new Thread(new Batiment("bat" + bat_num)));
        }

        bats.forEach((k,v) -> {  // Start every bats
            v.start();
        });

        bats.forEach((k,v) -> {try {  // Wait for every bats
            v.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }});
    }
}
