const { Kafka } = require("kafkajs");
const config = require("./config");
const logger = require("./logger").logger;
const { create_bulk_pdf, create_bulk_pdf_pt, create_defaulter_notice_pdf_pt } = require("./api");

const listenConsumer = async () => {
  const kafka = new Kafka({
    brokers: [config.KAFKA_BROKER_HOST],
    retry: { retries: 1 },
  });
// Migrated Kafka consumer implementation from kafka-node ConsumerGroup to KafkaJS with async/await processing, 
// improved connection handling, and crash event monitoring.
  const consumer = kafka.consumer({
    groupId: "egov-pdf-group",
    sessionTimeout: 15000,
    maxBytes: 10 * 1024 * 1024, // 10 MB - equivalent to fetchMaxBytes in kafka-node
    // roundrobin partition assignment - equivalent to protocol: ["roundrobin"] in kafka-node
    partitionAssigners: [kafka.partitioners.RoundRobinAssigner],
    // autoCommit is true by default in KafkaJS (autoCommitInterval default is 5000ms)
  });

  try {
    await consumer.connect();
    // fromBeginning: false = fromOffset: "latest" in kafka-node (only consume new messages)
    // outOfRangeOffset: "earliest" is handled at broker level in KafkaJS
    await consumer.subscribe({ topic: config.KAFKA_BULK_PDF_TOPIC, fromBeginning: false });
    logger.info("Consumer is ready");
  } catch (err) {
    logger.error("Failed to start Kafka consumer: " + err.message);
    logger.error(err.stack || err);
    return;
  }

  await consumer.run({
    eachMessage: async ({ message }) => {
      logger.info("record received on consumer for create");
      try {
        const data = JSON.parse(message.value.toString());
        if (data?.bussinessService === "PT") {
          await create_defaulter_notice_pdf_pt(data);
          logger.info("Record created for PT consumer request");
        } else {
          await create_bulk_pdf(data);
          logger.info("record created for consumer request");
        }
      } catch (error) {
        logger.error("error in create request by consumer " + error.message);
        logger.error(error.stack || error);
      }
    },
  });

  consumer.on(consumer.events.CRASH, ({ payload }) => {
    logger.error("Consumer crashed: " + payload.error.message);
  });
};

module.exports = { listenConsumer};