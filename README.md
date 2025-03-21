# 2025_projetDonneesMassive
Kafka, stream processing, comparaison JMS


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
