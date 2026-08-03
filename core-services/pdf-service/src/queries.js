// Before: mixed require() and import, uuid v3 subpath import, missing .js extensions
// Change: replaced require() with ESM imports, fixed uuid to v9 API, added .js extensions

import { Pool } from 'pg';
import logger from "./config/logger.js";
import producer from "./kafka/producer.js";
import envVariables from "./EnvironmentVariables.js";
import PDFMerger from 'pdf-merger-js';
import { fileStoreAPICall, getFilestoreUrl } from "./utils/fileStoreAPICall.js";
import fs from "fs";
import { v4 as uuidv4 } from "uuid";

// PostgreSQL connection pool — reuses connections across requests for better performance
const pool = new Pool({
  user: envVariables.DB_USER,
  host: envVariables.DB_HOST,
  database: envVariables.DB_NAME,
  password: envVariables.DB_PASSWORD,
  port: envVariables.DB_PORT,
})

let createJobKafkaTopic = envVariables.KAFKA_CREATE_JOB_TOPIC;

/**
 * Searches egov_pdf_gen table for already generated PDFs
 * Supports search by jobid, entityid, tenantid and isconsolidated
 * Returns the latest record per entityid (based on max endtime)
 * Used by the _search API endpoint to avoid regenerating existing PDFs
 */
export const getFileStoreIds = (
  jobid,
  tenantId,
  isconsolidated,
  entityid,
  callback
) => {
  var searchquery = "";
  var queryparams = [];
  var next = 1;
  var jobidPresent = false;
  searchquery = "SELECT * FROM egov_pdf_gen WHERE";

  if (jobid != undefined && jobid.length > 0) {
    searchquery += ` jobid = ANY ($${next++})`;
    queryparams.push(jobid);
    jobidPresent = true;
  }

  if (entityid != undefined && entityid.trim() !== "") {
    if (jobidPresent) searchquery += " and";
    searchquery += ` entityid = ($${next++})`;
    queryparams.push(entityid);
  }

  if (tenantId != undefined && tenantId.trim() !== "") {
    searchquery += ` and tenantid = ($${next++})`;
    queryparams.push(tenantId);
  }

  if (isconsolidated != undefined && isconsolidated.trim() !== "") {
    var ifTrue = isconsolidated === "true" || isconsolidated === "True";
    var ifFalse = isconsolidated === "false" || isconsolidated === "false";
    if (ifTrue || ifFalse) {
      searchquery += ` and isconsolidated = ($${next++})`;
      queryparams.push(ifTrue);
    }
  }
  searchquery = `SELECT pdf_1.* FROM egov_pdf_gen pdf_1 INNER JOIN (SELECT entityid, max(endtime) as MaxEndTime from (`+searchquery+`) as pdf_2 group by entityid) pdf_3 ON pdf_1.entityid = pdf_3.entityid AND pdf_1.endtime = pdf_3.MaxEndTime`;
  pool.query(searchquery, queryparams, (error, results) => {
    if (error) {
      logger.error(error.stack || error);
      callback({
        status: 400,
        message: `error occured while searching records in DB : ${error.message}`
      });
    } else {
      if (results && results.rows.length > 0) {
        var searchresult = [];
        results.rows.map(crow => {
          searchresult.push({
            filestoreids: crow.filestoreids,
            jobid: crow.jobid,
            tenantid: crow.tenantid,
            createdtime: crow.createdtime,
            endtime: crow.endtime,
            totalcount: crow.totalcount,
            key: crow.key,
            documentType: crow.documenttype,
            moduleName: crow.modulename
          });
        });
        logger.info(results.rows.length + " matching records found in search");
        callback({ status: 200, message: "Success", searchresult });
      } else {
        logger.error("no result found in DB search");
        callback({ status: 404, message: "no matching result found" });
      }
    }
  });
};

/**
 * Publishes generated PDF records to Kafka topic PDF_GEN_CREATE
 * egov-persister service consumes this topic and saves the records to DB
 * Called after all PDFs are uploaded to filestore successfully
 * Triggers successCallback with jobid and filestoreIds on success
 */
export const insertStoreIds = async (
  dbInsertRecords,
  jobid,
  filestoreids,
  tenantId,
  starttime,
  successCallback,
  errorCallback,
  totalcount,
  key,
  documentType,
  moduleName
) => {
  var endtime = new Date().getTime();

  // Before: kafka-node producer.send used callback style
  // Change: kafkajs producer.send is async/await
  try {
    await producer.send({
      topic: createJobKafkaTopic,
      messages: [{ value: JSON.stringify({ jobs: dbInsertRecords }) }]
    });
    logger.info("jobid: " + jobid + ": published to kafka successfully");
    successCallback({
      message: "Success",
        jobid,
        filestoreIds: filestoreids,
        tenantid: tenantId,
        starttime,
        endtime,
        totalcount,
        key,
        documentType,
        moduleName
      });
    } catch (err) {
      logger.error(err.stack || err);
      errorCallback({
        message: `error while publishing to kafka: ${err.message}`
      });
    }
  };

/**
 * Tracks progress of a bulk PDF generation job in egov_bulk_pdf_info table
 * On first call: inserts a new record with status INPROGRESS
 * On subsequent calls: increments recordscompleted count
 * Called after each individual PDF is generated and saved to disk
 */
export async function insertRecords(bulkPdfJobId, totalPdfRecords, currentPdfRecords, userid, tenantId, locality, bussinessService, consumerCode, isConsolidated) {
  try {
    const result = await pool.query('select * from egov_bulk_pdf_info where jobid = $1', [bulkPdfJobId]);
    if(result.rowCount<1){
      const insertQuery = 'INSERT INTO egov_bulk_pdf_info(jobid, uuid, recordscompleted, totalrecords, createdtime, filestoreid, lastmodifiedby, lastmodifiedtime, tenantid, locality, businessservice, consumercode, isconsolidated, status) VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12, $13, $14)';
      const curentTimeStamp = new Date().getTime();
      const status = 'INPROGRESS';
      await pool.query(insertQuery,[bulkPdfJobId, userid, currentPdfRecords, totalPdfRecords, curentTimeStamp, null, userid, curentTimeStamp, tenantId, locality, bussinessService, consumerCode, isConsolidated, status]);
    }
    else{
      var recordscompleted = parseInt(result.rows[0].recordscompleted);
      var totalrecords = parseInt(result.rows[0].totalrecords);
      if(recordscompleted < totalrecords){
        const updateQuery = 'UPDATE egov_bulk_pdf_info SET recordscompleted = recordscompleted + $1, lastmodifiedby = $2, lastmodifiedtime = $3 WHERE jobid = $4';
        const curentTimeStamp = new Date().getTime();
        await pool.query(updateQuery,[currentPdfRecords, userid, curentTimeStamp, bulkPdfJobId]);
      }
    }
  } catch (err) {
    logger.error(err.stack || err);
  } 
}

/**
 * Merges all individual PDF files into a single output.pdf once all records are completed
 * Checks if recordscompleted >= totalrecords and all expected files are present on disk
 * Uploads merged PDF to filestore, updates DB with filestoreid and status DONE
 * Sends SMS notification with download link via Kafka
 * Cleans up temp PDF files and folder after successful upload
 * Skips merge if job status is CANCEL
 */
export async function mergePdf(bulkPdfJobId, tenantId, userid, numberOfFiles, mobileNumber){

  try {
    const updateResult = await pool.query('select * from egov_bulk_pdf_info where jobid = $1', [bulkPdfJobId]);
    var recordscompleted = parseInt(updateResult.rows[0].recordscompleted);
    var totalrecords = parseInt(updateResult.rows[0].totalrecords);
    var baseFolder = envVariables.SAVE_PDF_DIR + bulkPdfJobId + '/';
    //var baseFolder = process.cwd() + '/' + bulkPdfJobId + '/';
    
    let fileNames = fs.readdirSync(baseFolder);
    
    if(recordscompleted >= totalrecords && fileNames.length == numberOfFiles){
      var merger = new PDFMerger();
    
      logger.info('Files to be merged: ',fileNames);
      (async () => {
        var processStatus = updateResult.rows[0].status;
        if(processStatus != 'CANCEL'){
          try {
            for (let i = 0; i < fileNames.length; i++){
              logger.info(baseFolder+fileNames[i]);
              await merger.add(baseFolder+fileNames[i]);  //merge all pages. parameter is the path to file and filename.
            }
            await merger.save(baseFolder+'/output.pdf');        //save under given name and reset the internal document
          } catch (err) {
            logger.error(err.stack || err);
          }
      
          var mergePdfData = fs.createReadStream(baseFolder+'output.pdf');
          await fileStoreAPICall('output.pdf', tenantId, mergePdfData).then((filestoreid) => {
            const updateQuery = 'UPDATE egov_bulk_pdf_info SET filestoreid = $1, lastmodifiedby = $2, lastmodifiedtime = $3, status = $5 WHERE jobid = $4';
            const curentTimeStamp = new Date().getTime();
            const status = 'DONE';
            pool.query(updateQuery,[filestoreid, userid, curentTimeStamp, bulkPdfJobId, status]);
            sendNoitification(filestoreid, mobileNumber, tenantId);
        
          }).catch((err) => {
            logger.error(err.stack || err);
          });
        }

        try {
          if( fs.existsSync(baseFolder) ) {
            fs.readdirSync(baseFolder).forEach(function(file,index){
              var curPath = baseFolder + file;
              if(fs.lstatSync(curPath).isDirectory()) { // recurse
                deleteFolderRecursive(curPath);
              } else { // delete file
                fs.unlinkSync(curPath);
              }
            });
            fs.rmdirSync(baseFolder);
          }
        } catch (error) {
          logger.error(error.stack || error);
          try {
            await producer.send({
              topic: envVariables.KAFKA_PDF_ERROR_TOPIC,
              messages: [{ value: JSON.stringify(error) }]
            });
          } catch (kafkaErr) {
            logger.error("error while publishing to kafka: " + kafkaErr.message);
            logger.error(kafkaErr.stack || kafkaErr);
            } 
          }
          
        })();        
      
    }
  } catch (err) {
    logger.error(err.stack || err);
  }
  
}

/**
 * Sends SMS notification to the user with a download link for the merged PDF
 * Fetches the filestore URL, shortens it via URL shortening service
 * Publishes SMS request to Kafka notification topic for egov-notification-sms service
 */
export async function sendNoitification(filestoreid, mobileNumber, tenantId){
  const topic = envVariables.KAFKA_TOPICS_NOTIFICATION;
  var pdfLink = await getFilestoreUrl(filestoreid, tenantId);
  let smsRequest = {};
  smsRequest['mobileNumber'] = mobileNumber;
  smsRequest['message'] = "Your download is ready. It will expire in 24 hours. Please click on the link below to download the pdf.\n"+pdfLink;
  

  try {
    await producer.send({
      topic,
      messages: [{ value: JSON.stringify(smsRequest) }]
    });
  } catch (err) {
    logger.error(err);  
}
}




/**
 * Fetches bulk PDF job details from egov_bulk_pdf_info table
 * Searches by jobId if provided, otherwise by uuid (userid)
 * Supports pagination via limit and offset
 * Returns job progress, filestoreid, status and other metadata
 */
export async function getBulkPdfRecordsDetails(userid, offset, limit, jobId){
  try {
    let data = [];
    let param;
    let query = 'select * from egov_bulk_pdf_info where ';
    if(jobId){
      query = query + 'jobid = $1 ';
      param = jobId;
    }
    else{
      query = query + 'uuid = $1 ';
      param = userid;
    }

    query = query + 'limit $2 offset $3';

    const result = await pool.query(query, [param, limit, offset]);
    if(result.rowCount>=1){
      
      for(let row of result.rows){
        let value = {
          jobid: row.jobid,
          uuid: row.uuid,
          totalrecords: row.totalrecords,
          recordscompleted: row.recordscompleted,
          filestoreid: row.filestoreid,
          createdtime: row.createdtime,
          lastmodifiedby: row.lastmodifiedby,
          lastmodifiedtime: row.lastmodifiedtime,
          tenantId: row.tenantid,
          locality: row.locality,
          bussinessService: row.businessservice,
          consumercode: row.consumercode,
          isConsolidated: row.isconsolidated,
          status: row.status
        };
        data.push(value);
      }
    }
    return data;
    
  } catch (err) {
    logger.error(err.stack || err);
    
  }

}

/**
 * Cancels an in-progress bulk PDF job by setting status to CANCEL in DB
 * Prevents cancellation if job is already completed (recordscompleted == totalrecords)
 * mergePdf checks for CANCEL status and skips the merge if set
 * Returns error map if job not found or already completed
 */
export async function cancelBulkPdfProcess(requestInfo, jobId, userid){
  let errorMap = [];
  try{
    const result = await pool.query('select * from egov_bulk_pdf_info where jobid = $1 and uuid = $2', [jobId, userid]);
    if(result.rowCount==1){
      var recordscompleted = parseInt(result.rows[0].recordscompleted);
      var totalrecords = parseInt(result.rows[0].totalrecords);
      if(recordscompleted == totalrecords){
        let error = {
          message: `Not allowed to cancel already completed process`,
        };
        errorMap.push(error);
        return errorMap;
      }
      const updateQuery = 'UPDATE egov_bulk_pdf_info SET status = $1, lastmodifiedby = $2, lastmodifiedtime = $3 WHERE jobid = $4';
      const curentTimeStamp = new Date().getTime();
      const status = 'CANCEL';
      await pool.query(updateQuery,[status, userid, curentTimeStamp, jobId]);
  
    }
    else{
      let error = {
        message: `No process with jobId: ${jobId} present for ${requestInfo.userInfo.userName}`,
      };
      errorMap.push(error);
      return errorMap;
    }
  }catch (err){
    logger.error(err.stack || err);
    let error = {
      message: `Error occured while getting details from database`,
    };
    errorMap.push(error);
    return errorMap;
  }
  
}

/**
 * Same as getBulkPdfRecordsDetails but for defaulter notice PDF jobs
 * Fetches records from egov_defaulter_notice_pdf_info table
 * Searches by jobId if provided, otherwise by uuid (userid)
 */
export async function getDefaulterPdfRecordsDetails(userid, offset, limit, jobId){
  try {
    let data = [];
    let param;
    let query = 'select * from egov_defaulter_notice_pdf_info where ';
    if(jobId){
      query = query + 'jobid = $1 ';
      param = jobId;
    }
    else{
      query = query + 'uuid = $1 ';
      param = userid;
    }

    query = query + 'limit $2 offset $3';

    const result = await pool.query(query, [param, limit, offset]);
    if(result.rowCount>=1){
      
      for(let row of result.rows){
        let value = {
          jobid: row.jobid,
          uuid: row.uuid,
          totalrecords: row.totalrecords,
          recordscompleted: row.recordscompleted,
          filestoreid: row.filestoreid,
          createdtime: row.createdtime,
          lastmodifiedby: row.lastmodifiedby,
          lastmodifiedtime: row.lastmodifiedtime,
          tenantId: row.tenantid,
          locality: row.locality,
          bussinessService: row.businessservice,
          consumercode: row.consumercode,
          isConsolidated: row.isconsolidated,
          status: row.status
        };
        data.push(value);
      }
    }
    return data;
    
  } catch (err) {
    logger.error(err.stack || err);
    
  }

}

/**
 * Same as insertRecords but for defaulter notice PDF jobs
 * Tracks progress in egov_defaulter_notice_pdf_info table
 * Includes additional propertytype field specific to defaulter notices
 */
export async function insertRecordsForDN(bulkPdfJobId, totalPdfRecords, currentPdfRecords, userid, tenantId, locality,propertytype, bussinessService, consumerCode, isConsolidated) {
  try {
    const result = await pool.query('select * from egov_defaulter_notice_pdf_info where jobid = $1', [bulkPdfJobId]);
    if(result.rowCount<1){
      const insertQuery = 'INSERT INTO egov_defaulter_notice_pdf_info(jobid, uuid, recordscompleted, totalrecords, createdtime, filestoreid, lastmodifiedby, lastmodifiedtime, tenantid, locality,propertytype, businessservice, consumercode, isconsolidated, status) VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12, $13, $14,$15)';      const curentTimeStamp = new Date().getTime();
      const status = 'INPROGRESS';
      await pool.query(insertQuery,[bulkPdfJobId, userid, currentPdfRecords, totalPdfRecords, curentTimeStamp, null, userid, curentTimeStamp, tenantId, locality, propertytype,bussinessService, consumerCode, isConsolidated, status]);
    }
    else{
      var recordscompleted = parseInt(result.rows[0].recordscompleted);
      var totalrecords = parseInt(result.rows[0].totalrecords);
      if(recordscompleted < totalrecords){
        const updateQuery = 'UPDATE egov_defaulter_notice_pdf_info SET recordscompleted = recordscompleted + $1, lastmodifiedby = $2, lastmodifiedtime = $3 WHERE jobid = $4';
        const curentTimeStamp = new Date().getTime();
        await pool.query(updateQuery,[currentPdfRecords, userid, curentTimeStamp, bulkPdfJobId]);
      }
    }
  } catch (err) {
    logger.error(err.stack || err);
  } 
}

/**
 * Same as mergePdf but for defaulter notice PDF jobs
 * Merges PDFs, uploads to filestore, updates egov_defaulter_notice_pdf_info table
 * Sends SMS notification and cleans up temp files after successful upload
 */
export async function mergePdfForDN(bulkPdfJobId, tenantId, userid, numberOfFiles, mobileNumber){

  try {
    const updateResult = await pool.query('select * from egov_defaulter_notice_pdf_info where jobid = $1', [bulkPdfJobId]);
    var recordscompleted = parseInt(updateResult.rows[0].recordscompleted);
    var totalrecords = parseInt(updateResult.rows[0].totalrecords);
    var baseFolder = envVariables.SAVE_PDF_DIR + bulkPdfJobId + '/';
    //var baseFolder = process.cwd() + '/' + bulkPdfJobId + '/';
    
    let fileNames = fs.readdirSync(baseFolder);
    
    if(recordscompleted >= totalrecords && fileNames.length == numberOfFiles){
      var merger = new PDFMerger();
    
      logger.info('Files to be merged: ',fileNames);
      (async () => {
        var processStatus = updateResult.rows[0].status;
        if(processStatus != 'CANCEL'){
          try {
            for (let i = 0; i < fileNames.length; i++){
              logger.info(baseFolder+fileNames[i]);
              await merger.add(baseFolder+fileNames[i]);            //merge all pages. parameter is the path to file and filename.
            }
            await merger.save(baseFolder+'/output.pdf');        //save under given name and reset the internal document
          } catch (err) {
            logger.error(err.stack || err);
          }
      
          var mergePdfData = fs.createReadStream(baseFolder+'output.pdf');
          await fileStoreAPICall('output.pdf', tenantId, mergePdfData).then((filestoreid) => {
            const updateQuery = 'UPDATE egov_defaulter_notice_pdf_info SET filestoreid = $1, lastmodifiedby = $2, lastmodifiedtime = $3, status = $5 WHERE jobid = $4';
            const curentTimeStamp = new Date().getTime();
            const status = 'DONE';
            pool.query(updateQuery,[filestoreid, userid, curentTimeStamp, bulkPdfJobId, status]);
            sendNoitification(filestoreid, mobileNumber, tenantId);
        
          }).catch((err) => {
            logger.error(err.stack || err);
          });
        }

        try {
          if( fs.existsSync(baseFolder) ) {
            fs.readdirSync(baseFolder).forEach(function(file,index){
              var curPath = baseFolder + file;
              if(fs.lstatSync(curPath).isDirectory()) { // recurse
                deleteFolderRecursive(curPath);
              } else { // delete file
                fs.unlinkSync(curPath);
              }
            });
            fs.rmdirSync(baseFolder);
          }
        } catch (error) {
          logger.error(error.stack || error);
          try {
  // Refactored Kafka error publishing to use async/await, simplified payload structure, and added try/catch handling for producer send failures.
            await producer.send({
              topic: envVariables.KAFKA_PDF_ERROR_TOPIC,
              messages: [{ value: JSON.stringify(error) }]
            });
          } catch (kafkaErr) {
            logger.error(kafkaErr.stack || kafkaErr);
          }
        }

      })();
    }
  } catch (err) {
    logger.error(err.stack || err);
  }
}