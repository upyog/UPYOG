/**
 * This file is currently unused.
 * Refactored imports from CommonJS (require)
 * to native ESM imports for node-cache and
 * moment-timezone compatibility with Node.js 22.
 */

import axios from "axios";
import envVariables from "../EnvironmentVariables.js";
import get from "lodash.get";
import NodeCache from "node-cache";
import moment from "moment-timezone";

// In-memory cache for localisation API responses
// stdTTL: 300 — cached data expires after 300 seconds (5 minutes)
// Avoids repeated HTTP calls to localisation service for the same pdfKey + locale combination
const cache = new NodeCache({ stdTTL: 300 });

let datetimezone = envVariables.DATE_TIMEZONE;
let egovLocHost = envVariables.EGOV_LOCALISATION_HOST;
let egovLocSearchCall = envVariables.EGOV_LOCALISATION_SEARCH;
let defaultLocale = envVariables.DEFAULT_LOCALISATION_LOCALE;
let defaultTenant = envVariables.DEFAULT_LOCALISATION_TENANT;
export const getTransformedLocale = (label) => {
  return label.toUpperCase().replace(/[.:-\s\/]/g, "_");
};

/**
 * This function returns localisation label from keys based on needs
 * This function does optimisation to fetch one module localisation values only once
 * @param {*} requestInfo - requestinfo from client
 * @param {*} localisationMap - localisation map containing localisation key,label fetched till now
 * @param {*} prefix - prefix to be added before key before fetching localisation ex:-"MODULE_NAME_MASTER_NAME"
 * @param {*} key - key to fetch localisation
 * @param {*} moduleName - "module name for fetching localisation"
 * @param {*} localisationModuleList - "list of modules for which localisation was already fetched"
 * @param {*} isCategoryRequired - ex:- "GOODS_RETAIL_TST-1" = get localisation for "GOODS"
 * @param {*} isMainTypeRequired  - ex:- "GOODS_RETAIL_TST-1" = get localisation for "RETAIL"
 * @param {*} isSubTypeRequired  - - ex:- "GOODS_RETAIL_TST-1" = get localisation for "GOODS_RETAIL_TST-1"
 */
// Fetches localisation messages from the localisation service
// Uses in-memory cache to avoid repeated API calls for the same pdfKey + locale
export const findLocalisation = async (
  requestInfo,
  moduleList,
  codeList,
  pdfKey
) => {
  let cacheData = null;
  // Extract locale from msgId (format: "msgId|locale"), fallback to defaultLocale
  let locale = requestInfo.msgId;
  if (null != locale) {
    locale = locale.split("|");
    locale = locale.length > 1 ? locale[1] : defaultLocale;
  } else {
    locale = defaultLocale;
  }

  // Check cache using pdfKey + locale as cache key
  if(pdfKey!=null){
    let cacheKey = pdfKey + '-' + locale;
    cacheData = await verifyCache(cacheKey);
  }

  // Return cached data if available
  if(cacheData!= null && Object.keys(cacheData).length>=1){
    return cacheData;
  }
  else{
    // Extract state-level tenantId (e.g. "pb.amritsar" -> "pb")
    let statetenantid = get(
      requestInfo,
      "userInfo.tenantId",
      defaultTenant
    ).split(".")[0];
  
    let url = egovLocHost + egovLocSearchCall;
    console.log("Localisation API URL:", url);
  
    let request = {
      RequestInfo: requestInfo,
      messageSearchCriteria:{
        tenantId: statetenantid,
        locale: locale,
        codes: []
      }
    };
  
    request.messageSearchCriteria.module = moduleList.toString();
    request.messageSearchCriteria.codes = codeList.toString().split(",");
  
    let headers = {
      headers:{
        "content-type": "application/json;charset=UTF-8",
        accept: "application/json, text/plain, */*"
      }
    };

    // Call localisation API and cache the response
    let responseBody = await axios.post(url, request, headers)
    .then(function (response) {
      console.log("Localisation API Success");
      console.log("Response Data:", response.data);
      return response;
    })
    .catch((error) => {
      console.log("Localisation API ERROR");
      if (error.response) {
        console.log("Status:", error.response.status);
        console.log("Error Data:", error.response.data);
      } else {
        console.log("Error Message:", error.message);
      }
      throw error;
    });
  
    // Store response in cache against pdfKey for future requests
    if(pdfKey!=null)
      cache.set(pdfKey, responseBody.data);
  
    return responseBody.data;
  }
}

// Checks if localisation data for the given pdfKey exists in cache
// Returns cached data if found, null otherwise
export const verifyCache = async (pdfKey) => {
  let cacheData = null;
  if (cache.has(pdfKey)) {
    cacheData = cache.get(pdfKey);
    return Promise.resolve(cacheData);
  }
  else
    return cacheData;
}

// Builds a localisation key from the given value by applying prefix and required parts
// Supports category, main type, and sub type parts joined by delimiter
// Returns array if input was array, single value otherwise
export const getLocalisationkey = async (
  prefix,
  key,
  isCategoryRequired,
  isMainTypeRequired,
  isSubTypeRequired,
  delimiter = " / "
) => {

  let keyArray = [];
  let localisedLabels = [];
  let isArray = false;

  if (key == null) {
    return key;
  } else if (typeof key == "string" || typeof key == "number") {
    keyArray.push(key);
  } else {
    // input is an array — process each item individually
    keyArray = key;
    isArray = true;
  }

  keyArray.map((item) => {
    let codeFromKey = "";

    // append main category in the beginning
    if (isCategoryRequired) {
        codeFromKey = getLocalisationLabel(
        item.split(".")[0],
        prefix
      );
    }

    if (isMainTypeRequired) {
     if (isCategoryRequired) codeFromKey = `${codeFromKey}${delimiter}`;
        codeFromKey = getLocalisationLabel(
        item.split(".")[1],
        prefix
      );
    }

    if (isSubTypeRequired) {
      if (isMainTypeRequired || isCategoryRequired)
        codeFromKey = `${codeFromKey}${delimiter}`;
        codeFromKey = `${codeFromKey}${getLocalisationLabel(
        item,
        prefix
      )}`;
    }

    // if no part is required, use the full key as-is
    if (!isCategoryRequired && !isMainTypeRequired && !isSubTypeRequired) {
      codeFromKey = getLocalisationLabel(item, prefix);
    }

    localisedLabels.push(codeFromKey === "" ? item : codeFromKey);
  });
  if (isArray) {
    return localisedLabels;
  }
  return localisedLabels[0];
};

const getLocalisationLabel = (key, prefix) => {
  if (prefix != undefined && prefix != "") {
    key = `${prefix}_${key}`;
  }
  key = getTransformedLocale(key);
  return key;
};

// Converts epoch timestamp to formatted date string using configured timezone
// Falls back to "DD/MM/YYYY" if no format is provided
export const getDateInRequiredFormat = (et, dateformat = "DD/MM/YYYY") => {
  if (!et) return "NA";
  return moment(et).tz(datetimezone).format(dateformat);
};

/**
 *
 * @param {*} value - values to be checked
 * @param {*} defaultValue - default value
 * @param {*} path  - jsonpath from where the value was fetched
 */
export const getValue = (value, defaultValue, path) => {
  if (
    value == undefined ||
    value == null ||
    value.length === 0 ||
    (value.length === 1 && (value[0] === null || value[0] === ""))
  ) {
    // logger.error(`no value found for path: ${path}`);
    return defaultValue;
  } else return value;
};

// Converts footer string from format config into a function if it exists
// pdfmake requires footer to be a function — JSON.stringify destroys functions,
// so footer is stored as a string in config and restored here before PDF generation
export const convertFooterStringtoFunctionIfExist = (footer) => {
  if (footer != undefined) {
    footer = Function(`'use strict'; return (${footer})`)();
  }
  return footer;
};