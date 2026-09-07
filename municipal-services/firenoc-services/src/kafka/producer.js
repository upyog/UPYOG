/**
 * Kafka Producer — Upgraded from kafka-node to kafkajs
 *
 * WHY WE UPGRADED:
 * - kafka-node is unmaintained (last release 2020) and incompatible with Node 22
 * - kafkajs is the actively maintained standard for Kafka in Node.js
 * - kafkajs uses native Promises/async-await vs kafka-node's callback-only API
 *
 * WHAT CHANGED:
 * - Replaced `kafka-node` KafkaClient + Producer with kafkajs Kafka + producer
 * - Producer connection is now async (connectProducer) instead of event-based
 *   (producer.on("ready")) 
 * - kafkajs send API shape: { topic, messages: [{ value }] }
 *   vs kafka-node shape:    [{ topic, messages: stringValue }]
 *
 * BACKWARD COMPATIBILITY:
 * - The exported `send(payloads, callback)` adapter preserves the original
 *   kafka-node call signature used across create.js, update.js,
 *   notificationUtil.js and consumer.js — so none of those files needed changes
 */

import { Kafka, logLevel, Partitioners } from "kafkajs";
import envVariables from "../envVariables";

const kafka = new Kafka({
  logLevel: logLevel.INFO,
  brokers: [envVariables.KAFKA_BROKER_HOST],
  clientId: "firenoc-producer",
});

// const producer = kafka.producer();
 const producer = kafka.producer({ createPartitioner: Partitioners.LegacyPartitioner });



const connectProducer = async () => {
  await producer.connect();
  console.log("Producer is ready");
};

connectProducer().catch(err => {
  console.log("Producer is in error state");
  console.log(err);
});

// Adapter to match the kafka-node send API shape used across the codebase:
// send([{ topic, messages: stringValue }], callback)
// callback follows Node.js error-first convention: callback(err) on failure, callback(null, true) on success.
const send = async (payloads, callback) => {
  try {
    for (const payload of payloads) {
      await producer.send({
        topic: payload.topic,
        messages: [{ value: payload.messages }],
      });
    }
    if (callback) callback(null, true);
  } catch (err) {
    console.log(err);
    if (callback) callback(err);
  }
};

export default { send };
