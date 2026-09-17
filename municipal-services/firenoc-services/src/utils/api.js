// src/utils/api.js
import httpClient from "../config/httpClient";
import { addQueryArg } from "./index";

/**
 * Strip transport-level headers (host, content-length, transfer-encoding) from the incoming request
 * before forwarding to downstream services, as these headers describe the original HTTP connection
 * and would cause routing failures or malformed requests if forwarded as-is.
 */

const STRIP_HEADERS = ["content-length", "host", "transfer-encoding"];

export const httpRequest = async ({
  hostURL,
  endPoint,
  queryObject = [],
  requestBody = {},
  headers = [],
  customRequestInfo = {}
}) => {
  let instance = httpClient(hostURL);
  let errorReponse = {};
  if (headers) {
    const safeHeaders = Object.fromEntries(
      Object.entries(headers).filter(([k]) => !STRIP_HEADERS.includes(k.toLowerCase()))
    );
    instance.defaults = Object.assign(instance.defaults, { headers: safeHeaders });
  }
  endPoint = addQueryArg(endPoint, queryObject);
  try {
    const response = await instance.post(endPoint, requestBody);
    const responseStatus = parseInt(response.status, 10);
    if (responseStatus === 200 || responseStatus === 201) {
      return response.data;
    }
  } catch (error) {
    errorReponse = error.response;
     // console.log("test 1");
  }

  // console.log("test -",JSON.stringify(error));
  // console.log("error from api utils:", errorReponse);
  throw errorReponse;
};
