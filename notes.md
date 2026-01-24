Just recording down my thoughts as I work on the project.


Sprint 0 - Notes+Thoughts:
- fraud-producer -> Data generation (this is replaceable with real transaction system if needed)

- fraud-consumer -> Business Logic (ML inference + persistence)

- fraud-common -> Shared contracts
 
The multi module system helps because then we can run 5 producers but only 2 consumer instances, you can scale them independently based on load. Being deployed independently, we can deploy a new version of the consumer without needed to touch the producer.

Helpful in keeping the docker images small because the producer doesn't need PostgreSQL driver or the ONNX runtime library. (because it doesn't need to)

Parent POM
General "Global Settings", decides the big decisions like what version of the tool the whole project is going to use. Can also define shared plugins and testing for every module.

Local POM
Local settings, can also define internal relationships between the different modules that might depend on eachother.


the docker compose file - docker-compose.yml
My application isnt made up of many different services that work together 
include:
- a message broker (kafka) to handle the stream of transactions
- a db (postgresql) to store fraud alerts
- a monitoring tool (prometheus ) to watch over the system 
- a coordination service for kafka (zookeeper)

Manually installing, configuring and running each of these steps on my laptop won't get me too far so I run them on isolated containers.

docker-compose : a tool that lets you define and manage this entire multi-container application with single config file (docker-compose.yml)


Breakdown of the Docker file:
(This will also help with understanding the application and how each service plays a role )

services: this is the main section where I defined each component of our infrastructure
	zookeeper: Kafka uses Zookeeper for managing it's cluster state, tracking which broker are alive and storing configuration metadata
	kafka: this is the core message broker. I've configured two listeners:
		-localhost:9092 : For the local application to connect to Kafka from outside the docker network
		-kafka:29092: For internal communication between services within the docker network.
	postgres: A postgreSQL db for storing fradualent transactions. I've set up a persistent volume (postgres_data) to ensure that the data is saved even if you restart the container
	prometheus: This is the monitoring service. It's configured to look for pormetheus.eml file
networks: fraud-detection-network has been defined. This allows the containers to communicated with each other using their services name
	- for example kafka service can reach the zookeeper service using it's hostname, zookeeper
volumes: This seciton defines the persistent storange for the pg db

Last note on the prometheus.yml

- this is just a simple config file for Prometheus. It tells prometheus to look at host.docker.interanal:8080 and scrape metrics from its /actuator/prometheus endpoint. 

host.docker.internal is a special DNS name that allows docker containers to connect to services running on the host machine (Prometheus running on a container will be able to interact with my springboot application running on my laptop ex.)


Testing Kafka Connectivity:
The most basic test is just creating a topic
(topic is like a category or a feed anme, producers write the messages to a topic and consumers read messages from a topic, I am going to test the transaction topic)

docker exec -it kafka kafka-topics --create --topic transactions --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1

docker exec it kafka: this tells the docker to execute a command inside the container named kafka(the -it makes the command interactive)
kafka-topics: this is the name of the Kafka script for managing topics
--create: self explanatory
--topic transactions: the name of our topic
--bootstrap-server localhost:9092: this tells the script how to connect to the kafka broker
--partitions 3: this split the topic into 3 partitions. allowing for parallel processing by consumers which is good for scalability (prob not necessary as I dont have any users lol)
--replication-factor 1: this basically means that there will be only on copy of our data. In a prod env you would have a higher replication factor for fault tolerance, but since this is a local env 1 is good enough


we now need to test the postgresql connectivity 

docker exec -it postgres psql -U fraud_user -d fraud_detection
docker exec -it postgres : execute command in postgres container
psql: postgres command line
-U fraud_user: connect as the user fraud_user (this is defined in docker-compose.yml )
-d fraud_detection: connect db named fraud_detection, which is also defined

after we connect just run a simple query like:
SELECT version(); and exit