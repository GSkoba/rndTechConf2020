# Getting Started
## Run kafka and zookeeper containers in docker 
Zookeeper
```
docker run --name zookeeper -d --restart always -p 2888:2888 -p 2181:2181 -p 3888:3888 zookeeper
```
Kafka
```
docker run --name kafka -d --hostname kafka -e KAFKA_ZOOKEEPER_CONNECT=172.17.0.2:2181 -e KAFKA_LISTENER_SECURITY_PROTOCOL_MAP=INSIDE:PLAINTEXT,OUTSIDE:PLAINTEXT -e KAFKA_ADVERTISED_LISTENERS=INSIDE://localhost:9092,OUTSIDE://localhost:9093 -e KAFKA_LISTENERS=INSIDE://0.0.0.0:9092,OUTSIDE://0.0.0.0:9093 -e KAFKA_INTER_BROKER_LISTENER_NAME=INSIDE -e KAFKA_CREATE_TOPICS=bellman_call_insert:8:1,nexign_call_rejected:8:1,nx.gus.calls:8:1,bellman_balance_update:8:1,nx.gus.bis-events:8:1,bellman_call_rollback:8:1,nx.gus.call-rollbacks:8:1nx.gus.subscriber-activity:8:1 -e KAFKA_BROKER_ID=998 -p 9092:9092 wurstmeister/kafka:latest
```
## Run demo Spring-Kafka
```
gradle bootRun -p demo-kafka-spring
```