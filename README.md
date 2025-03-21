# 2025_projetDonneesMassive
Kafka, stream processing, comparaison JMS

## En cas de problèmes de lancement Kafka
Si ./Kafka_2.13.0.0 possède des fichiers déféctueux : 
Installer Kafka_2.13.0.0 https://kafka.apache.org/downloads
Se fier à la configuration dans ./Kafka_2.13.0.0/config pour configurer plusieurs brokers
Une fois Kafka mis en place

## Lancement Kafka

Dans des terminaux différents :

Lancer zookeeper
```bash
make zookeeper_start
```

Lancer le broker1 (Attendre que Zookeeper soit correctement démarré)
```bash
make broker1_start
```

Lancer le broker2 (Attendre que Zookeeper soit correctement démarré)
```bash
make broker2_start
```

Au besoin, créer les topics
```bash
make temperatures_topic_create
make avgTopic_topic_create
make topic1_create
```

## Stream Processing

Dans des terminaux différents
Afficher le topic de sortie du stream processing
```bash
make consummer_avgTopic_start
```
Lancer le consomateur
```bash
make start_emk_consum
```
Lancer le producteur
```bash
make start_emk_prod
```
