/*
 * Kafka producer setup using KafkaJS (replaces deprecated kafka-node).
 * Creates a producer instance, configures brokers from environment variables,
 * adds retry behavior, exposes an explicit connect function for controlled
 * startup, and logs connection/disconnection events for monitoring.
 * The producer is used to send messages/jobs to Kafka topics for async processing.
 */

import { Kafka } from "kafkajs";
import logger from "../config/logger.js";
import envVariables from "../EnvironmentVariables.js";

const kafka = new Kafka({
  // brokers expects an array — split converts KAFKA_BROKER_HOST comma-separated string (as in kafka-node kafkaHost) to array format required by KafkaJS
  brokers: envVariables.KAFKA_BROKER_HOST.split(",").map(b => b.trim()),
  // retry — matches old kafka-node connectRetryOptions: { retries: 1 }
  retry: {
    retries: 1
  }
});

const producer = kafka.producer();

// In kafka-node, producer connected automatically on instantiation.
// In KafkaJS, .connect() must be called explicitly to ensure correct order —
// connectProducer() is called inside app.listen() in index.js so Kafka connects only after the Express server is up.
export const connectProducer = async () => {
  await producer.connect();
};

producer.on(producer.events.CONNECT, () => logger.info("Producer is ready"));
producer.on(producer.events.DISCONNECT, () => logger.error("Producer disconnected"));

export default producer;