# CHEMIN VERS DOSSIER QUI CONTIENT KAFKA SETUP
kafka_dir = /home1/em963948/Téléchargements/kafka_2.13-3.0.0

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

start_emk_prod:mvn_package
	mvn exec:java -Dexec.mainClass="com.kafka_project.app.exo3_stream_processing.ThreadProd"

start_emk_consum:mvn_package
	mvn exec:java -Dexec.mainClass="com.kafka_project.app.exo3_stream_processing.StreamConsumer"

mvn_package:
	mvn package


#####


zookeeper_start:
	$(kafka_dir)/bin/zookeeper-server-start.sh $(kafka_dir)/config/zookeeper.properties 

broker1_start:
	$(kafka_dir)/bin/kafka-server-start.sh $(kafka_dir)/config/server.properties
	
broker2_start:
	$(kafka_dir)/bin/kafka-server-start.sh $(kafka_dir)/config/server_bis.properties

temperatures_topic_create:temperatures_topic_delete
	$(kafka_dir)/bin/kafka-topics.sh --create --topic temperatures --partitions 2  --bootstrap-server localhost:9092 --replication-factor 2

avgTopic_topic_create:avgTopic_topic_delete
	$(kafka_dir)/bin/kafka-topics.sh --create --topic avgTopic --partitions 2  --bootstrap-server localhost:9093 --replication-factor 2

temperatures_topic_delete:
	$(kafka_dir)/bin/kafka-topics.sh --delete --topic temperatures --bootstrap-server localhost:9092

avgTopic_topic_delete:
	$(kafka_dir)/bin/kafka-topics.sh --delete --topic avgTopic --bootstrap-server localhost:9092
	

producer_start:
	$(kafka_dir)/bin/kafka-console-producer.sh --topic topic1 --bootstrap-server localhost:9092

consummer_temperatures_start:
	$(kafka_dir)/bin/kafka-console-consumer.sh --from-beginning --topic temperatures --bootstrap-server localhost:9092

consummer_avgTopic_start:
	$(kafka_dir)/bin/kafka-console-consumer.sh --from-beginning --topic avgTopic --bootstrap-server localhost:9092