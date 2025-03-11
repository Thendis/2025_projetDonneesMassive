
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
