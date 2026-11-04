var config = require("./config");
const { Kafka, Partitioners } = require("kafkajs");
const logger = require("./logger").logger;

const kafka = new Kafka({
  brokers: [config.KAFKA_BROKER_HOST],
  retry: { retries: 1 },
});
// Migrated Kafka producer from kafka-node to KafkaJS with explicit async connection management, retry configuration, and disconnect event handling.
const producer = kafka.producer({
  allowAutoTopicCreation: true,
  createPartitioner: Partitioners.LegacyPartitioner,
  retry: { retries: 1 },
});


let isConnected = false;

const connectProducer = async () => {
  try {
    await producer.connect();
    isConnected = true;
    logger.info("Producer is ready");
  } catch (err) {
    logger.error("Producer is in error state");
    logger.error(err.stack || err);
  }
};

producer.on(producer.events.DISCONNECT, () => {
  logger.error("Producer is in error state");
  isConnected = false;
});

connectProducer();

module.exports = { producer, isConnected };