import { Router } from "express";
import producer from "../kafka/producer";
import {
  requestInfoToResponseInfo,
  createWorkFlow,
  getLocationDetails
} from "../utils";
import envVariables from "../envVariables";
import mdmsData from "../utils/mdmsData";
import { addUUIDAndAuditDetails, updateStatus } from "../utils/create";
import { calculate } from "../services/firenocCalculatorService";
import { validateFireNOCModel } from "../utils/modelValidation";
import { getStateSpecificTopicName } from "../utils/index";
import set from "lodash/set";
import get from "lodash/get";
import { sendFireNOCSMSRequest } from "../utils/notificationUtil";

const asyncHandler = require("express-async-handler");

export default ({ config }) => {
  let api = Router();
  api.post(
    "/_create",
    asyncHandler(async (request, res) => {
      let response = await createApiResponse(request);
      if (response.Errors)
        res.status(400);
      res.json(response);
    })
  );
  return api;
};

export const createApiResponse = async (request) => {
  var body = JSON.parse(JSON.stringify(request.body));
  var header = JSON.parse(JSON.stringify(request.headers));
  let payloads = [];
  //getting mdms data
  let mdms = await mdmsData(body.RequestInfo, body.FireNOCs[0].tenantId, header);

  //location data
  let locationResponse = await getLocationDetails(
    body.RequestInfo,
    body.FireNOCs[0].tenantId,
    header
  );

  set(mdms, "MdmsRes.firenoc.boundary", get(locationResponse, "TenantBoundary.0.boundary"));

  let errors = validateFireNOCModel(body, mdms);
  if (errors.length > 0) {
    console.log("Validation errors:", JSON.stringify(errors, null, 2));
    return {
      ResponseInfo: requestInfoToResponseInfo(body.RequestInfo, true),
      Errors: errors
    };
  }

  // console.log(JSON.stringify(mdms));
  body = await addUUIDAndAuditDetails(body, "_create", header);
  //console.log("Created Body:  "+JSON.stringify(body));
  let workflowResponse = await createWorkFlow(body, header);
  // console.log(JSON.stringify(workflowResponse));

  //need to implement notification
  //calculate call
  let { FireNOCs, RequestInfo } = body;
  for (var i = 0; i < FireNOCs.length; i++) {
    await calculate(FireNOCs[i], RequestInfo, header);
  }
  body.FireNOCs = updateStatus(FireNOCs, workflowResponse);

  let topic = envVariables.KAFKA_TOPICS_FIRENOC_CREATE;
  let tenantId = body.FireNOCs[0].tenantId;

  var isCentralInstance = envVariables.IS_ENVIRONMENT_CENTRAL_INSTANCE;
  if (typeof isCentralInstance == "string")
    isCentralInstance = (isCentralInstance.toLowerCase() == "true");

  if (isCentralInstance)
    topic = getStateSpecificTopicName(tenantId, topic);

  payloads.push({ topic, messages: JSON.stringify(body) });

  sendFireNOCSMSRequest(body.FireNOCs, RequestInfo);

  producer.send(payloads, function(err) {
    if (err) console.log(err);
  });

  return {
    ResponseInfo: requestInfoToResponseInfo(body.RequestInfo, true),
    FireNOCs: body.FireNOCs
  };
};
