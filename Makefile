
start_prod:mvn_package
	mvn exec:java -Dexec.mainClass="com.kafka_project.app.Prod"

start_consumer:mvn_package
	mvn exec:java -Dexec.mainClass="com.kafka_project.app.Consumer"

mvn_package:
	mvn package
