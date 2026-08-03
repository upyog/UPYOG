// Before: imported unused request, named { post } from axios (removed in 1.x), 
//         used require() for form-data, missing .js extensions on local imports
// Change: removed request and { post }, replaced require() with import, added .js extensions

import fs from "fs";
import get from "lodash.get";
import axios from "axios";
import FormData from "form-data";
import logger from "../config/logger.js";
import envVariables from "../EnvironmentVariables.js";

let egovFileHost = envVariables.EGOV_FILESTORE_SERVICE_HOST;
let externalHost = envVariables.EGOV_EXTERNAL_HOST;

/**
 *
 * @param {*} filename -name of localy stored temporary file
 * @param {*} tenantId - tenantID
 * 
 */
// Refactored variable declarations to use const, fixed malformed URL issue, and added robust error handling for filestore API failures.
export const fileStoreAPICall = async function(filename, tenantId, fileData) {
   const url = `${egovFileHost}filestore/v1/files?tenantId=${tenantId}&module=pdfgen&tag=00040-2017-QR`;

  // Prepare form data
  const form = new FormData();
  form.append('file', fileData, {
    filename: filename,
    contentType: 'application/pdf',
  });

  let response = null;
  
  try {
    // Sending the file to Filestore API
    response = await axios.post(url, form, {
      maxContentLength: Infinity,
      maxBodyLength: Infinity,
      headers: {
        ...form.getHeaders(),
      },
    });

    return get(response.data, 'files[0].fileStoreId');

  } catch (error) {
    // Log the error to help debugging
    logger.error('Axios error:', error);

    // Throw the error back so it can be handled in the calling function
    throw new Error('Something went wrong with the external Filestore API request');
  }
};

export async function getFilestoreUrl(filestoreid, tenantId){
  var url = `${egovFileHost}/filestore/v1/files/url?tenantId=${tenantId}&fileStoreIds=${filestoreid}`;
  let response = await axios.get(url);
  let data = response.data;
  var fileURL = data['fileStoreIds'][0]['url'].split(",");
  var shorteningUrl = getShortneningUrl(fileURL[0]);
  return shorteningUrl;
}

export async function getShortneningUrl(actualUrl){
  var url = `${externalHost}egov-url-shortening/shortener`;
  var request = {
    "url": actualUrl
  };

  let headers = {
    headers:{
      'Content-Type': 'application/json'
    }
  };

  let response = await axios.post(url,request, headers);
  let shortUrl = response.data;
  return shortUrl;
}