import axios from "axios";
import commonConfig from "config/common.js";
import { getTenantId ,getAccessToken} from "egov-ui-kit/utils/localStorageUtils";
import { toggleSpinner } from "egov-ui-framework/ui-redux/screen-configuration/actions";
import {
  addQueryArg,
  isPublicSearch
} from "egov-ui-framework/ui-utils/commons";
import some from "lodash/some";
import store from "ui-redux/store";
const instance = axios.create({
  baseURL: window.location.origin,
  headers: {
    "Content-Type": "application/json",
  },
});
const wrapRequestBody = (requestBody, action, customRequestInfo,endPoint) => {
  const authToken = getAccessToken();
  let RequestInfo = {
    apiId: "Rainmaker",
    ver: ".01",
    // ts: getDateInEpoch(),
    action: action,
    did: "1",
    key: "",
    msgId: "20170310130900|en_IN",
    requesterId: "",
    authToken,
  };
  console.log("wrapRequestBody",requestBody, action, customRequestInfo)
  let userInfo = JSON.parse(`${localStorage.getItem('user-info')}` ? `${localStorage.getItem('user-info')}` : '');
  if(endPoint.includes("_fetchbill"))
  {
    RequestInfo = { ...RequestInfo, userInfo };
  }
  else {
    RequestInfo = { ...RequestInfo, ...customRequestInfo };
  }
  RequestInfo = { ...RequestInfo, ...customRequestInfo };
  if (isPublicSearch()) delete RequestInfo.authToken;
  return Object.assign(
    {},
    {
      RequestInfo,
    },
    requestBody
  );
};
export const httpRequest = async (
  method = "get",
  endPoint,
  action,
  queryObject = [],
  requestBody = {},
  headers = [],
  customRequestInfo = {}
) => {
  store.dispatch(toggleSpinner());
  let apiError = "Api Error";

  if (headers)
    instance.defaults = Object.assign(instance.defaults, {
      headers,
    });

  /* Fix for central instance to send tenantID in all query params  */
  const tenantId =
    process.env.REACT_APP_NAME === "Citizen"
      ? commonConfig.tenantId
      : (endPoint && endPoint.includes("mdms")
          ? commonConfig.tenantId
          : getTenantId()) || commonConfig.tenantId;
  if (!some(queryObject, ["key", "tenantId"]) && commonConfig.singleInstance) {
    endPoint &&
      !endPoint.includes("tenantId") &&
      queryObject &&
      queryObject.push({
        key: "tenantId",
        value: tenantId,
      });
  }

  endPoint = addQueryArg(endPoint, queryObject);
  var response;
  try {
    switch (method) {
      case "post":
        response = await instance.post(
          endPoint,
          wrapRequestBody(requestBody, action, customRequestInfo,endPoint)
        );
        break;
      default:
        response = await instance.get(endPoint);
    }
    const responseStatus = parseInt(response.status, 10);
    store.dispatch(toggleSpinner());
    if (responseStatus === 200 || responseStatus === 201) {
      return response.data;
    }
  } catch (error) {
    const { data, status } = error.response;
    if (status === 400 && data === "") {
      apiError = "INVALID_TOKEN";
    } else {
      apiError =
        (data.hasOwnProperty("Errors") &&
          data.Errors &&
          data.Errors.length &&
          data.Errors[0].message) ||
        (data.hasOwnProperty("error") &&
          data.error.fields &&
          data.error.fields.length &&
          data.error.fields[0].message) ||
        (data.hasOwnProperty("error_description") && data.error_description) ||
        apiError;
    }
    store.dispatch(toggleSpinner());
  }
  // unhandled error
  throw new Error(apiError);
};

export const loginRequest = async (username = null, password = null) => {
  let apiError = "Api Error";
  try {
    // api call for login
    alert("Logged in");
    return;
  } catch (e) {
    apiError = e.message;
    // alert(e.message);
  }

  throw new Error(apiError);
};

export const logoutRequest = async () => {
  let apiError = "Api Error";
  try {
    alert("Logged out");
    return;
  } catch (e) {
    apiError = e.message;
    // alert(e.message);
  }

  throw new Error(apiError);
};

export const prepareForm = (params) => {
  let formData = new FormData();
  for (var k in params) {
    formData.append(k, params[k]);
  }
  return formData;
};

export const uploadFile = async (endPoint, module, file, ulbLevel) => {
  // Bad idea to fetch from local storage, change as feasible
  const tenantId = getTenantId() ? (ulbLevel ? commonConfig.tenantId : commonConfig.tenantId) : "";
  const uploadInstance = axios.create({
    baseURL: window.location.origin,
    headers: {
      "Content-Type": "multipart/form-data",
      "auth-token":getAccessToken(),
    },
  });

  const requestParams = {
    tenantId,
    module,
    file,
  };
  const requestBody = prepareForm(requestParams);

  try {
    const response = await uploadInstance.post(endPoint, requestBody);
    const responseStatus = parseInt(response.status, 10);
    let fileStoreIds = [];

    if (responseStatus === 201) {
      const responseData = response.data;
      const files = responseData.files || [];
      fileStoreIds = files.map((f) => f.fileStoreId);
      return fileStoreIds[0];
    }
  } catch (error) {
    throw new Error(error);
  }
};
