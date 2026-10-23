import { Kafka, logLevel, Partitioners } from "kafkajs";
import envVariables from "../envVariables";

const kafka = new Kafka({
  logLevel: logLevel.INFO,
  brokers: [envVariables.KAFKA_BROKER_HOST],
  clientId: "firenoc-calculator-producer",
});

const producer = kafka.producer({ createPartitioner: Partitioners.LegacyPartitioner });

const connectProducer = async () => {
  await producer.connect();
  console.log("Producer is ready");
};

connectProducer().catch(err => {
  console.log("Producer is in error state");
  console.log(err);
});

// Adapter preserves kafka-node send(payloads, callback) signature
// so create.js and update.js need zero changes
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
