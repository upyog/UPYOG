const kafka = require("kafka-node");
import envVariables from "../envVariables";
import producer from "./producer";
import get from "lodash/get";
import set from "lodash/set";
import { searchApiResponse } from "../api/search";
import { updateApiResponse } from "../api/update";
// import { httpRequest } from "../api";

var options = {
  // connect directly to kafka broker (instantiates a KafkaClient)
  kafkaHost: envVariables.KAFKA_BROKER_HOST,
  groupId: "firenoc-consumer-grp",
  autoCommit: true,
  autoCommitIntervalMs: 5000,
  sessionTimeout: 15000,
  fetchMaxBytes: 10 * 1024 * 1024, // 10 MB
  // An array of partition assignment protocols ordered by preference. 'roundrobin' or 'range' string for
  // built ins (see below to pass in custom assignment protocol)
  protocol: ["roundrobin"],
  // Offsets to use for new groups other options could be 'earliest' or 'none'
  // (none will emit an error if no offsets were saved) equivalent to Java client's auto.offset.reset
  fromOffset: "latest",
  // how to recover from OutOfRangeOffset error (where save offset is past server retention)
  // accepts same value as fromOffset
  outOfRangeOffset: "earliest"
};

var consumerGroup = new kafka.ConsumerGroup(options, [
  envVariables.KAFKA_TOPICS_FIRENOC_CREATE,
  envVariables.KAFKA_TOPICS_FIRENOC_UPDATE,
  envVariables.KAFKA_TOPICS_FIRENOC_WORKFLOW,
  envVariables.KAFKA_TOPICS_RECEIPT_CREATE
]);

console.log("Consumer ");

consumerGroup.on("message", function(message) {
  console.log("consumer-topic", message.topic);
  // console.log("consumer-value", JSON.parse(message.value));
  const value = JSON.parse(message.value);

  let payloads = [];
  const topic = envVariables.KAFKA_TOPICS_NOTIFICATION;
  let smsRequest = {};
  let fireNOCRequest = {};
  let events = [];
  let { RequestInfo } = value;

  const sendEventNotificaiton = () => {
    let requestPayload = {
      // RequestInfo,
      events
    };

    payloads.push({
      topic: envVariables.KAFKA_TOPICS_EVENT_NOTIFICATION,
      messages: JSON.stringify(requestPayload)
    });
    // httpRequest({
    //   hostURL: envVariables.EGOV_EVENT_HOST,
    //   endPoint: `${envVariables.EGOV_EVENT_CONTEXT_PATH}${envVariables.EGOV_EVENT_CREATE_ENPOINT}`,
    //   requestPayload
    // }).then(
    //   function(response) {
    //     console.log(response);
    //   },
    //   function(error) {
    //     console.log(error);
    //   }
    // );
  };

  const sendFireNOCSMSRequest = (FireNOCs,RequestInfo) => {
    
    for (let i = 0; i < FireNOCs.length; i++) {
      smsRequest["mobileNumber"] = get(
        FireNOCs[i],
        "fireNOCDetails.applicantDetails.owners.0.mobileNumber"
      );
      let firenocType =
        get(FireNOCs[i], "fireNOCDetails.fireNOCType") === "NEW"
          ? "new"
          : "provision";

      let ownerName = get(
        FireNOCs[i],
        "fireNOCDetails.applicantDetails.owners.0.name"
      );
      let uuid = get(
        FireNOCs[i],
        "fireNOCDetails.applicantDetails.owners.0.uuid"
      );
      let applicationNumber = get(
        FireNOCs[i],
        "fireNOCDetails.applicationNumber"
      );
      let fireNOCNumber = get(FireNOCs[i], "fireNOCNumber");
      let validTo = get(FireNOCs[i], "fireNOCDetails.validTo");
      let tenantId = get(FireNOCs[i], "tenantId");
      let actionType="forwarded for";
      let action=get(FireNOCs[i],"fireNOCDetails.action");
      if(action==envVariables.SENDBACK){
        actionType="send back to";
      }
	  
	  let messageForcertificate;
	    
		if(firenocType=='renewal'){
		messageForcertificate=	'your renewed Fire NOC Certificate has been generated.'
		}else{
			messageForcertificate=	'your Fire NOC Certificate has been generated.'
		}
	  
      let downLoadLink=`${envVariables.EGOV_HOST_BASE_URL}${envVariables.EGOV_RECEIPT_URL}?applicationNumber=${applicationNumber}&tenantId=${tenantId}`;
	    
     console.log("download link "+ downLoadLink);	    
     
      let ownerInfo="";
      ownerInfo= get(RequestInfo,"userInfo.roles");
      console.log("ownerInfo is", ownerInfo);
      if(ownerInfo!=null  && ownerInfo.length>0){
       ownerInfo=ownerInfo[0].name;
      }
     console.log("ownerInfo is",ownerInfo);
     console.log("firenoc status is",FireNOCs[i].fireNOCDetails.status);
      switch (FireNOCs[i].fireNOCDetails.status) {
/*case "INITIATED":
          smsRequest[
            "message"
          ] = `Dear ${ownerName},Your application for ${firenocType} Fire NOC has been generated. Your application no. is ${applicationNumber}.|1301157492438182299|1407161407309889909`;
          break;*/
        case "PENDINGPAYMENT":
          smsRequest[
            "message"
          ] = `Dear ${ownerName}, 
          Your application for ${firenocType} Fire NOC Certificate has been submitted, the application no. is ${applicationNumber}. 
          You can download your application form by clicking on the below link: 
           ${downLoadLink}.
          Kindly pay your NOC Fees online or at your applicable fire office.|1301157492438182299|1407161492659630233`;
          break;
        case "FIELDINSPECTION":
          smsRequest[
            "message"
          ] = `Dear ${ownerName}, 
          Your application for ${firenocType} Fire NOC Certificate with application no. ${applicationNumber}  has been ${actionType} field inspection.|1301157492438182299|1407161492704744715`;
          break;
        case "DOCUMENTVERIFY":
          smsRequest[
            "message"
          ] = `Dear ${ownerName}, 
          Your application for ${firenocType} Fire NOC Certificate with application no. ${applicationNumber}  has been ${actionType} document verifier.|1301157492438182299|1407161407329037630`;
           break;
        case "PENDINGAPPROVAL":
          smsRequest[
            "message"
          ] = `Dear ${ownerName}, 
          Your application for ${firenocType} Fire NOC Certificate with application no. ${applicationNumber}  has been ${actionType} approver.|1301157492438182299|1407161407332754584`;
          break;
        case "APPROVED":
          var currentDate = new Date(validTo);
          var date = currentDate.getDate();
          var month = currentDate.getMonth(); //Be careful! January is 0 not 1
          var year = currentDate.getFullYear();

          var dateString =
            date +
            "-" +
            (month + 1 > 9 ? month + 1 : `0${month + 1}`) +
            "-" +
            year;

          smsRequest[
            "message"
          ] = `Dear ${ownerName}, 
          Your Application for  ${firenocType} Fire NOC Certificate with application no. ${applicationNumber} is approved and  ${messageForcertificate}
          Your Fire NOC Certificate No. is ${fireNOCNumber} and it is valid till ${dateString}.
          You can download your Fire NOC Certificate by clicking on the below link:
          ${downLoadLink}|1301157492438182299|1407161494277225601`;
          break;
       case "SENDBACKTOCITIZEN":
          smsRequest[
            "message"
          ] = `Dear ${ownerName}, 
         Your application for ${firenocType} Fire NOC Certificate with application no. ${applicationNumber} is send back to you for further actions.Please check the comments and Re-submit application through mSeva App or by ULB counter.|1301157492438182299|1407161407355219072`;
          break;
        /* case "CITIZENACTIONREQUIRED-DV":
          smsRequest[
            "message"
          ] = `Dear ${ownerName}, 
          Your application for ${firenocType} Fire NOC Certificate with application no. ${applicationNumber} is send back to you for further actions.Please check the comments and Re-submit application through mSeva App or by ULB counter.\n\nEGOVS`;
          break;
        case "CITIZENACTIONREQUIRED":
          smsRequest[
             "message"
          ] = `Dear ${ownerName}, 
          Your application for ${firenocType} Fire NOC Certificate with application no. ${applicationNumber} is send back to you for further actions.Please check the comments and Re-submit application through mSeva App or by ULB counter.\n\nEGOVS`;
          break; */
        case "REJECTED":
          smsRequest[
            "message"
          ] = `Dear ${ownerName}, 
          Your application for ${firenocType} Fire NOC Certificate with application no. ${applicationNumber} has been rejected by ${ownerInfo} .To know more details please contact your respective fire office.|1301157492438182299|1407161407368404178
          `;
          break;
        // case "CANCELLED":
        //   break;
        default:
      }
      payloads.push({
        topic,
        messages: JSON.stringify(smsRequest)
      });
       console.log("smsRequest is",smsRequest);
      if (smsRequest.message) {
        events.push({
          tenantId: tenantId,
          eventType: "SYSTEMGENERATED",
          description: smsRequest.message,
          name: "Firenoc notification",
          source: "webapp",
          recepient: {
            toUsers: [uuid]
          }
        });
      }
    }
    // console.log("events",events);
    if (events.length > 0) {
      sendEventNotificaiton();
    }
  };
  const sendPaymentMessage=value=>{
    
    const { Payment, RequestInfo } = value;
    smsRequest["mobileNumber"] = get(
      Payment.paymentDetails[0],
      "Bill[0].mobileNumber"
    );

    let businessService=get(Payment.paymentDetails[0],"Bill[0].businessService")
    if (businessService === envVariables.BUSINESS_SERVICE) {
    let paymentAmount=get(Payment.paymentDetails[0],"Bill[0].amountPaid");
    console.log("paid amount is",paymentAmount);

    let applicantName=get(Payment.paymentDetails[0],"Bill[0].payerName");
    console.log("applicantName is",applicantName);

    let receiptNumber=get(Payment.paymentDetails[0],"Bill[0].billDetails[0].receiptNumber");

    let applicationNumber=get(Payment.paymentDetails[0],"Bill[0].billDetails[0].consumerCode");
    let tenant=get(Payment,"tenantId");

    let downLoadLink=`${envVariables.EGOV_HOST_BASE_URL}${envVariables.EGOV_RECEIPT_URL}?applicationNumber=${applicationNumber}&tenantId=${tenant}`;

    smsRequest[
      "message"
    ] = `Dear ${applicantName}, 
    A Payment of ${paymentAmount} has been collected successfully for your Fire NOC Certificate.
    The payment receipt no. is  ${receiptNumber} and you can download your receipt by clicking on the below link:
    ${downLoadLink}|1301157492438182299|1407161407392327147`;

    payloads.push({
      topic,
      messages: JSON.stringify(smsRequest)
    });
  } 
  }
  const FireNOCPaymentStatus = async value => {
    try {
      console.log("Consumer Payment data"+JSON.stringify(value));
      const { Payment, RequestInfo } = value;
      let tenantId = get(Payment, "tenantId");
      const { paymentDetails } = Payment;
      if (paymentDetails) {
        for (var index = 0; index < paymentDetails.length; index++) {
          let businessService = get(paymentDetails[index], "businessService");
          if (businessService === envVariables.BUSINESS_SERVICE) {
            let applicationNumber = get(
              paymentDetails[index],
              "bill.consumerCode"
            );
            const query = {
              tenantId,
              applicationNumber
            };
            const body = { RequestInfo };
            const searchRequest = { body, query };
            const searchResponse = await searchApiResponse(searchRequest);
            console.log("search response: "+JSON.stringify(searchResponse));
            const { FireNOCs } = searchResponse;
            if (!FireNOCs.length) {
              throw "FIRENOC Search error";
            }
            for (
              var firenocIndex = 0;
              firenocIndex < FireNOCs.length;
              firenocIndex++
            ) {
              set(
                FireNOCs[firenocIndex],
                "fireNOCDetails.action",
                envVariables.ACTION_PAY
              );
            }

            for(var index =0; index < RequestInfo.userInfo.roles.length;index++){
              let tenantId = get(RequestInfo.userInfo,"tenantId");
              set(RequestInfo.userInfo.roles[index],"tenantId",tenantId);
              console.log("Workflow TenantId",get(body.RequestInfo.userInfo.roles[index],"tenantId"));
            }

            const updateBody = { RequestInfo, FireNOCs };
            const updateRequest = { body: updateBody };
            console.log("update Request: "+JSON.stringify(updateRequest));
            const updateResponse = await updateApiResponse(updateRequest);
            console.log("update Response: "+JSON.stringify(updateResponse));
          }
        }
      }
    } catch (error) {
      throw error;
    }
  };

  switch (message.topic) {
    case envVariables.KAFKA_TOPICS_FIRENOC_CREATE:
      {
        const { FireNOCs,RequestInfo } = value;
        sendFireNOCSMSRequest(FireNOCs,RequestInfo);
      }
      break;
    case envVariables.KAFKA_TOPICS_FIRENOC_UPDATE:
      {
        const { FireNOCs } = value;
        sendFireNOCSMSRequest(FireNOCs,RequestInfo);
      }
      break;
    case envVariables.KAFKA_TOPICS_FIRENOC_WORKFLOW:
      {
        const { FireNOCs } = value;
        sendFireNOCSMSRequest(FireNOCs,RequestInfo);
      }
      break;

    // case envVariables.KAFKA_TOPICS_RECEIPT_CREATE:
    //   {
    //     console.log("reciept hit");
    //   }
    //   break;
    case envVariables.KAFKA_TOPICS_RECEIPT_CREATE:
      {
        sendPaymentMessage(value);
        FireNOCPaymentStatus(value);
      }
      break;
  }

	console.log("payloads is",payloads);
  producer.send(payloads, function(err, data) {
    if (!err) {
      console.log("sucessfully pushed" + data);
    } else {
      console.log("failed to push " + err);
    }
  });
});

consumerGroup.on("error", function(err) {
  console.log("Error:", err);
});

consumerGroup.on("offsetOutOfRange", function(err) {
  console.log("offsetOutOfRange:", err);
});

export default consumerGroup;
