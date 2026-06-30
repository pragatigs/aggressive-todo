#!/bin/bash
set -e

echo "Building task-service..."
cd task-service
./gradlew clean build -x test
cd ..

echo "Building auth-service..."
cd auth-service
./gradlew clean build -x test
cd ..

echo "Starting containers..."
docker compose up --build -d

echo ""
echo "Waiting for auth-service..."
until curl -fsS http://localhost:8081/actuator/health >/dev/null; do
  echo "auth-service not ready..."
  sleep 2
done

echo "Waiting for task-service..."
until curl -fsS http://localhost:8082/actuator/health >/dev/null; do
  echo "task-service not ready..."
  sleep 2
done

echo "Creating Elasticsearch index..."
cd elasticsearch
./create-index.sh

cd ..

echo ""

echo "Creating kafka topics..."
docker exec kafka /opt/kafka/bin/kafka-topics.sh \
  --bootstrap-server localhost:9092 \
  --create --if-not-exists \
  --topic outbox.event.TASK_CREATED \
  --partitions 1 \
  --replication-factor 1

docker exec kafka /opt/kafka/bin/kafka-topics.sh \
  --bootstrap-server localhost:9092 \
  --create --if-not-exists \
  --topic outbox.event.TASK_UPDATED \
  --partitions 1 \
  --replication-factor 1

docker exec kafka /opt/kafka/bin/kafka-topics.sh \
  --bootstrap-server localhost:9092 \
  --create --if-not-exists \
  --topic outbox.event.TASK_DELETED \
  --partitions 1 \
  --replication-factor 1

echo ""
echo "Everything is healthy."
echo "Frontend: http://localhost:3000"
echo "Kibana: http://localhost:5601"