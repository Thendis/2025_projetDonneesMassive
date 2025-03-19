# CHEMIN VERS DOSSIER QUI CONTIENT KAFKA SETUP
kafka_dir = "/home1/em963948/Téléchargements/kafka_2.13-3.0.0"

start_prod:mvn_package
	mvn exec:java -Dexec.mainClass="com.kafka_project.app.Prod"

start_consumer:mvn_package
	mvn exec:java -Dexec.mainClass="com.kafka_project.app.Consumer"

start_consumerTwoSameGroup:mvn_package
	mvn exec:java -Dexec.mainClass="com.kafka_project.app.ConsumerTwoSameGroup"
	
start_consumerThreeSameGroup:mvn_package
	mvn exec:java -Dexec.mainClass="com.kafka_project.app.ConsumerThreeSameGroup"
	
start_consumerTwoDifferentGroup:mvn_package
	mvn exec:java -Dexec.mainClass="com.kafka_project.app.ConsumerTwoDifferentGroup"

start_consumerThreeDifferentGroup:mvn_package
	mvn exec:java -Dexec.mainClass="com.kafka_project.app.ConsumerThreeDifferentGroup"

mvn_package:
	mvn package

zookeeper_start:
	$(kafka_dir)/bin/zookeeper-server-start.sh $(kafka_dir)/config/zookeeper.properties 

broker1_start:
	$(kafka_dir)/bin/kafka-server-start.sh $(kafka_dir)/config/server.properties
	
broker2_start:
	$(kafka_dir)/bin/kafka-server-start.sh $(kafka_dir)/config/server_bis.properties

topic1_create:
	$(kafka_dir)/bin/kafka-topics.sh --create --topic topic1 --partitions 2  --bootstrap-server localhost:9092 --replication-factor 2
topic2_create:
	$(kafka_dir)/bin/kafka-topics.sh --create --topic topic2 --partitions 2  --bootstrap-server localhost:9093 --replication-factor 2

topic1_delete:
	$(kafka_dir)/bin/kafka-topics.sh --delete --topic topic1 --bootstrap-server localhost:9092
topic2_delete:
	$(kafka_dir)/bin/kafka-topics.sh --delete --topic topic2 --bootstrap-server localhost:9093
	
producer_start:
	$(kafka_dir)/bin/kafka-console-producer.sh --topic topic1 --bootstrap-server localhost:9092

consummer_start:
	$(kafka_dir)/bin/kafka-console-consumer.sh --from-beginning --topic topic1 --bootstrap-server localhost:9092
	
	$(kafka_dir)/bin/kafka-topics.sh --list --bootstrap-server localhost:9092 