const { Kafka } = require("kafkajs");
import envVariables from "../envVariables";
import logger from "../config/logger";
import get from "lodash/get";
import set from "lodash/set";
import { searchApiResponse } from "../api/search";
import { updateApiResponse } from "../api/update";
const { initializeProducer } = require("../kafka/producer");
const async = require("async");

let producer;
initializeProducer().then((p) => {
  producer = p;
  logger.info('Kafka producer connected');
}).catch((error) => {
  logger.error(error.stack || error);
  process.exit(1);
});

const kafka = new Kafka({
  clientId: 'firenoc-services-consumer',
  brokers: [envVariables.KAFKA_BROKER_HOST],
  ssl: false,
  retry: { retries: 1 }
});

const consumer = kafka.consumer({ 
  groupId: 'firenoc-consumer-grp',
  sessionTimeout: 30000,
  heartbeatInterval: 3000
});

const CONCURRENCY_LIMIT = parseInt(envVariables.KAFKA_CONCURRENCY);
const DLQ_TOPIC = envVariables.KAFKA_TOPICS_FIRENOC_DLQ;

const worker = async (message) => {
  try {
    console.log("consumer-topic", message.topic);
    const value = JSON.parse(message.value);

    // Variables scoped inside worker for concurrency safety
    let payloads = { messages: [], topic: "" };
    let smsRequest = {};
    let events = [];
    let { RequestInfo } = value;
    const topic = envVariables.KAFKA_TOPICS_NOTIFICATION;

    const sendEventNotificaiton = () => {
      let requestPayload = { events };
      payloads.topic = envVariables.KAFKA_TOPICS_EVENT_NOTIFICATION;
      payloads.messages.push({ value: JSON.stringify(requestPayload) });
    };

    const sendFireNOCSMSRequest = (FireNOCs, RequestInfo) => {
      for (let i = 0; i < FireNOCs.length; i++) {
        smsRequest["mobileNumber"] = get(FireNOCs[i], "fireNOCDetails.applicantDetails.owners.0.mobileNumber");
        let firenocType = get(FireNOCs[i], "fireNOCDetails.fireNOCType") === "NEW" ? "new" : "provision";
        let ownerName = get(FireNOCs[i], "fireNOCDetails.applicantDetails.owners.0.name");
        let uuid = get(FireNOCs[i], "fireNOCDetails.applicantDetails.owners.0.uuid");
        let applicationNumber = get(FireNOCs[i], "fireNOCDetails.applicationNumber");
        let fireNOCNumber = get(FireNOCs[i], "fireNOCNumber");
        let validTo = get(FireNOCs[i], "fireNOCDetails.validTo");
        let tenantId = get(FireNOCs[i], "tenantId");
        let actionType = "forwarded for";
        let action = get(FireNOCs[i], "fireNOCDetails.action");
        if (action == envVariables.SENDBACK) actionType = "send back to";

        let messageForcertificate = (firenocType == 'renewal')
          ? 'your renewed Fire NOC Certificate has been generated.'
          : 'your Fire NOC Certificate has been generated.';

        let downLoadLink = `${envVariables.EGOV_HOST_BASE_URL}${envVariables.EGOV_RECEIPT_URL}?applicationNumber=${applicationNumber}&tenantId=${tenantId}`;
        let roles = get(RequestInfo, "userInfo.roles");
        let ownerInfo = (roles && roles.length > 0) ? roles[0].name : "";

        switch (FireNOCs[i].fireNOCDetails.status) {
          case "PENDINGPAYMENT":
            smsRequest["message"] = `Dear ${ownerName}, Your application for ${firenocType} Fire NOC Certificate has been submitted, the application no. is ${applicationNumber}. You can download your application form by clicking on the below link: ${downLoadLink}. Kindly pay your NOC Fees online or at your applicable fire office.|1301157492438182299|1407161492659630233`;
            break;
          case "FIELDINSPECTION":
            smsRequest["message"] = `Dear ${ownerName}, Your application for ${firenocType} Fire NOC Certificate with application no. ${applicationNumber} has been ${actionType} field inspection.|1301157492438182299|1407161492704744715`;
            break;
          case "DOCUMENTVERIFY":
            smsRequest["message"] = `Dear ${ownerName}, Your application for ${firenocType} Fire NOC Certificate with application no. ${applicationNumber} has been ${actionType} document verifier.|1301157492438182299|1407161407329037630`;
            break;
          case "PENDINGAPPROVAL":
            smsRequest["message"] = `Dear ${ownerName}, Your application for ${firenocType} Fire NOC Certificate with application no. ${applicationNumber} has been ${actionType} approver.|1301157492438182299|1407161407332754584`;
            break;
          case "APPROVED":
            var currentDate = new Date(validTo);
            var dateString = `${currentDate.getDate()}-${(currentDate.getMonth() + 1 > 9 ? currentDate.getMonth() + 1 : `0${currentDate.getMonth() + 1}`)}-${currentDate.getFullYear()}`;
            smsRequest["message"] = `Dear ${ownerName}, Your Application for ${firenocType} Fire NOC Certificate with application no. ${applicationNumber} is approved and ${messageForcertificate} Your Fire NOC Certificate No. is ${fireNOCNumber} and it is valid till ${dateString}. You can download your Fire NOC Certificate by clicking on the below link: ${downLoadLink}|1301157492438182299|1407161494277225601`;
            break;
          case "SENDBACKTOCITIZEN":
            smsRequest["message"] = `Dear ${ownerName}, Your application for ${firenocType} Fire NOC Certificate with application no. ${applicationNumber} is send back to you for further actions.Please check the comments and Re-submit application through mSeva App or by ULB counter.|1301157492438182299|1407161407355219072`;
            break;
          case "REJECTED":
            smsRequest["message"] = `Dear ${ownerName}, Your application for ${firenocType} Fire NOC Certificate with application no. ${applicationNumber} has been rejected by ${ownerInfo} .To know more details please contact your respective fire office.|1301157492438182299|1407161407368404178`;
            break;
        }

        payloads.topic = topic;
        payloads.messages.push({ value: JSON.stringify(smsRequest) });

        if (smsRequest.message) {
          events.push({
            tenantId: tenantId,
            eventType: "SYSTEMGENERATED",
            description: smsRequest.message,
            name: "Firenoc notification",
            source: "webapp",
            recepient: { toUsers: [uuid] }
          });
        }
      }
      if (events.length > 0) sendEventNotificaiton();
    };

    const sendPaymentMessage = (value) => {
      const { Payment } = value;
      let bill = get(Payment.paymentDetails[0], "Bill[0]") || get(Payment.paymentDetails[0], "bill");
      smsRequest["mobileNumber"] = get(bill, "mobileNumber") || get(bill, "billDetails[0].mobileNumber");
      let paymentAmount = get(bill, "amountPaid") || get(bill, "billDetails[0].amountPaid");
      let applicantName = get(bill, "payerName") || get(bill, "billDetails[0].payerName");
      let receiptNumber = get(Payment.paymentDetails[0], "receiptNumber") || get(bill, "billDetails[0].receiptNumber");
      let applicationNumber = get(bill, "billDetails[0].consumerCode") || get(bill, "consumerCode");
      let tenant = get(Payment, "tenantId");
      let downLoadLink = `${envVariables.EGOV_HOST_BASE_URL}${envVariables.EGOV_RECEIPT_URL}?applicationNumber=${applicationNumber}&tenantId=${tenant}`;

      smsRequest["message"] = `Dear ${applicantName}, A Payment of ${paymentAmount} has been collected successfully for your Fire NOC Certificate. The payment receipt no. is ${receiptNumber} and you can download your receipt by clicking on the below link: ${downLoadLink}|1301157492438182299|1407161407392327147`;
      payloads.topic = topic;
      payloads.messages.push({ value: JSON.stringify(smsRequest) });
    };

    const FireNOCPaymentStatus = async (value) => {
      const { Payment, RequestInfo } = value;
      let tenantId = get(Payment, "tenantId");
      const { paymentDetails } = Payment;
      if (paymentDetails) {
        for (let detail of paymentDetails) {
          if (get(detail, "businessService") === envVariables.BUSINESS_SERVICE) {
            let bill = get(detail, "Bill[0]") || get(detail, "bill");
            let applicationNumber = get(bill, "billDetails[0].consumerCode") || get(bill, "consumerCode");
            const searchResponse = await searchApiResponse({ body: { RequestInfo }, query: { tenantId, applicationNumber } });
            const { FireNOCs } = searchResponse;
            if (!FireNOCs || !FireNOCs.length) throw new Error("FIRENOC Search error");

            FireNOCs.forEach(f => set(f, "fireNOCDetails.action", envVariables.ACTION_PAY));
            if (RequestInfo.userInfo && RequestInfo.userInfo.roles) {
              RequestInfo.userInfo.roles.forEach(role => set(role, "tenantId", get(RequestInfo.userInfo, "tenantId")));
            }

            await updateApiResponse({ body: { RequestInfo, FireNOCs } });
          }
        }
      }
    };

    switch (message.topic) {
      case envVariables.KAFKA_TOPICS_FIRENOC_CREATE:
      case envVariables.KAFKA_TOPICS_FIRENOC_UPDATE:
      case envVariables.KAFKA_TOPICS_FIRENOC_WORKFLOW:
        sendFireNOCSMSRequest(value.FireNOCs, RequestInfo);
        break;

      case envVariables.KAFKA_TOPICS_RECEIPT_CREATE:
        // Enforce Business Service Check
        let bService = get(value, "Payment.paymentDetails[0].businessService") || get(value, "Payment.paymentDetails[0].Bill[0].businessService");
        if (bService === envVariables.BUSINESS_SERVICE) {
          sendPaymentMessage(value);
          await FireNOCPaymentStatus(value);
        }
        break;
    }

    if (producer && payloads.messages.length > 0) {
      await producer.send(payloads);
    }
  } catch (err) {
    logger.error("Processing failed. Sending to DLQ: " + err);
    if (producer) {
      // Sending raw message to DLQ
      await producer.send({
        topic: DLQ_TOPIC,
        messages: [{ value: message.value }]
      }).catch(e => logger.error("Fatal: Could not push to DLQ: " + e));
    }
  }
};

const queue = async.queue(worker, CONCURRENCY_LIMIT);

const runConsumer = async () => {
  await consumer.connect();
  logger.info("Kafkajs consumer connected successfully");
  
  await consumer.subscribe({ topics: [
    envVariables.KAFKA_TOPICS_FIRENOC_CREATE,
    envVariables.KAFKA_TOPICS_FIRENOC_UPDATE,
    envVariables.KAFKA_TOPICS_FIRENOC_WORKFLOW,
    envVariables.KAFKA_TOPICS_RECEIPT_CREATE
  ], fromBeginning: envVariables.KAFKA_OFFSET === "earliest" });

  await consumer.run({
    autoCommitInterval: 5000,
    eachMessage: async ({ topic, partition, message }) => {
      queue.push({
        topic,
        partition,
        offset: message.offset,
        value: message.value.toString()
      });
    }
  });
};

runConsumer().catch(err => logger.error("Kafkajs consumer crash: " + err));

export default consumer;