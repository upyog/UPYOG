var express = require("express");
var router = express.Router();
var producer = require("../producer").producer ;
var url = require("url");
var config = require("../config");
const uuidv4 = require("uuid/v4");
var logger = require("../logger").logger;
const { Pool } = require('pg');


const pool = new Pool({
  user: config.DB_USER,
  host: config.DB_HOST,
  database: config.DB_NAME,
  password: config.DB_PASSWORD,
  port: config.DB_PORT,
});
var {
  search_property,
  search_bill,
  search_payment,
  create_pdf,
  search_workflow,
  create_defaulter_notice_pdf_pt
} = require("../api");

const { asyncMiddleware } = require("../utils/asyncMiddleware");

function renderError(res, errorMessage, errorCode) {
  if (errorCode == undefined) errorCode = 500;
  res.status(errorCode).send({ errorMessage });
}

/* GET users listing. */
router.post(
  "/ptmutationcertificate",
  asyncMiddleware(async function (req, res, next) {
    var tenantId = req.query.tenantId;
    var uuid = req.query.uuid;
    var requestinfo = req.body;
    if (requestinfo == undefined) {
      return renderError(res, "requestinfo can not be null", 400);
    }
    if (!tenantId || !uuid) {
      return renderError(
        res,
        "tenantId and uuid are mandatory to generate the ptmutationcertificate",
        400
      );
    }

    try {
      try {
        resProperty = await search_property(uuid, tenantId, requestinfo);
      } catch (ex) {
        
        if (ex.response && ex.response.data) console.log(ex.response.data);
        return renderError(res, "Failed to query details of the property", 500);
      }
      var properties = resProperty.data;

      if (
        properties &&
        properties.Properties &&
        properties.Properties.length > 0
      ) {
        var creationReason = properties.Properties[0].creationReason;
        if (creationReason != "MUTATION")
          return renderError(
            res,
            "ptmutation certificate allowed only on mutation applications",
            400
          );
        try {
          var applicationNumber = properties.Properties[0].acknowldgementNumber;
          var workflowResponse = await search_workflow(
            applicationNumber,
            tenantId,
            requestinfo
          );
          var status = workflowResponse.data.ProcessInstances[0].state.state;
          if (status != "APPROVED")
            return renderError(
              res,
              `ptmutation certificate allowed only on Approved status, but current application status is ${status}`,
              400
            );
        } catch (ex) {
          
          if (ex.response && ex.response.data) console.log(ex.response.data);
          return renderError(
            res,
            "Failed to get status for property from workflow",
            500
          );
        }
        var pdfResponse;
        var pdfkey = config.pdf.ptmutationcertificate_pdf_template;
        try {
          pdfResponse = await create_pdf(
            tenantId,
            pdfkey,
            properties,
            requestinfo
          );
        } catch (ex) {
          
          if (ex.response && ex.response.data) console.log(ex.response.data);
          return renderError(res, "Failed to generate PDF for property", 500);
        }

        var filename = `${pdfkey}_${new Date().getTime()}`;

        //pdfData = pdfResponse.data.read();
        res.writeHead(200, {
          "Content-Type": "application/pdf",
          "Content-Disposition": `attachment; filename=${filename}.pdf`,
        });
        pdfResponse.data.pipe(res);
      } else {
        return renderError(
          res,
          "There is no property for you for this id",
          404
        );
      }
    } catch (ex) {
      return renderError(res, "Failed to query certificate details of the property", 500);
    }
  })
);

router.post(
  "/ptbill",
  asyncMiddleware(async function (req, res, next) {
    var tenantId = req.query.tenantId;
    var uuid = req.query.uuid;
    var requestinfo = req.body;
    if (requestinfo == undefined) {
      return renderError(res, "requestinfo can not be null", 400);
    }
    if (!tenantId || !uuid) {
      return renderError(
        res,
        "tenantId and uuid are mandatory to generate the ptbill",
        400
      );
    }
    try {
      try {
        resProperty = await search_property(uuid, tenantId, requestinfo, true);
      } catch (ex) {
        
        if (ex.response && ex.response.data) console.log(ex.response.data);
        return renderError(res, "Failed to query details of the property", 500);
      }
      var properties = resProperty.data;
      if (
        properties &&
        properties.Properties &&
        properties.Properties.length > 0
      ) {
        var propertyid = properties.Properties[0].propertyId;
        var billresponse;
        try {
          billresponse = await search_bill(propertyid, tenantId, requestinfo);
        } catch (ex) {
          
          if (ex.response && ex.response.data) console.log(ex.response.data);
          return renderError(res, `Failed to query bills for property`, 500);
        }
        var bills = billresponse.data;
        if (bills && bills.Bills && bills.Bills.length > 0) {
          var pdfResponse;
          var pdfkey = config.pdf.ptbill_pdf_template;
          try {
            var billArray = { Bill: bills.Bills };
            pdfResponse = await create_pdf(
              tenantId,
              pdfkey,
              billArray,
              requestinfo
            );
          } catch (ex) {
            
            if (ex.response && ex.response.data) console.log(ex.response.data);
            return renderError(res, "Failed to generate PDF for property", 500);
          }

          var filename = `${pdfkey}_${new Date().getTime()}`;

          //pdfData = pdfResponse.data.read();
          res.writeHead(200, {
            "Content-Type": "application/pdf",
            "Content-Disposition": `attachment; filename=${filename}.pdf`,
          });
          pdfResponse.data.pipe(res);
        } else {
          return renderError(res, "There is no bill for this id", 404);
        }
      } else {
        return renderError(
          res,
          "There is no property for you for this id",
          404
        );
      }
    } catch (ex) {
      return renderError(res, "Failed to query bill details of the property", 500);
    }
  })
);

router.post(
  "/ptreceipt",
  asyncMiddleware(async function (req, res, next) {
    var tenantId = req.query.tenantId;
    var uuid = req.query.uuid;
    var requestinfo = req.body;
    if (requestinfo == undefined) {
      return renderError(res, "requestinfo can not be null", 400);
    }
    if (!tenantId || !uuid) {
      return renderError(
        res,
        "tenantId and uuid are mandatory to generate the ptreceipt",
        400
      );
    }
    try {
      try {
        resProperty = await search_property(uuid, tenantId, requestinfo);
      } catch (ex) {
        
        if (ex.response && ex.response.data) console.log(ex.response.data);
        return renderError(res, "Failed to query details of the property", 500);
      }
      var properties = resProperty.data;
      if (
        properties &&
        properties.Properties &&
        properties.Properties.length > 0
      ) {
        var propertyid = properties.Properties[0].propertyId;
        var bussinessService = "PT";
        var paymentresponse;
        try {
          paymentresponse = await search_payment(
            propertyid,
            tenantId,
            requestinfo,
            bussinessService
          );
        } catch (ex) {
          
          if (ex.response && ex.response.data) console.log(ex.response.data);
          return renderError(res, `Failed to query payment for property`, 500);
        }
        var payments = paymentresponse.data;
        if (payments && payments.Payments && payments.Payments.length > 0) {
          var pdfResponse;
          var pdfkey = config.pdf.ptreceipt_pdf_template;
          try {
            pdfResponse = await create_pdf(
              tenantId,
              pdfkey,
              payments,
              requestinfo
            );
          } catch (ex) {
            
            if (ex.response && ex.response.data) console.log(ex.response.data);
            return renderError(res, "Failed to generate PDF for property", 500);
          }

          var filename = `${pdfkey}_${new Date().getTime()}`;

          //pdfData = pdfResponse.data.read();
          res.writeHead(200, {
            "Content-Type": "application/pdf",
            "Content-Disposition": `attachment; filename=${filename}.pdf`,
          });
          pdfResponse.data.pipe(res);
        } else {
          return renderError(res, "There is no payment for this id", 404);
        }
      } else {
        return renderError(
          res,
          "There is no property for you for this id",
          404
        );
      }
    } catch (ex) {
      return renderError(res, "Failed to query receipt details of the property", 500);
    }
  })
);

router.post(
  "/ptdefaulternotice",
  asyncMiddleware(async function (req, res, next) {
    var tenantId = req.query.tenantId;
    var requestinfo = req.body.RequestInfo;
    var properties=req.body.properties;
    var locality=req.query.locality;
    var propertytype=req.query.propertytype;
    if (requestinfo == undefined) {
      return renderError(res, "requestinfo can not be null", 400);
    }
    if (!tenantId) {
      return renderError(
        res,
        "tenantId are mandatory to generate the Pt defaulter notice",
        400
      );
    }
    try {
    
      
      if (
        properties &&
        properties.length > 0
      ) {
       
        var bussinessService = "PT";
        var id = uuidv4();
      var jobid = `${config.pdf.pt_defaulter_notice}-${new Date().getTime()}-${id}`;

      var kafkaData = {
        requestinfo: requestinfo,
        tenantId: tenantId,
        bussinessService: bussinessService,
        jobid: jobid,
        locality:locality,
        propertytype:propertytype,
        properties: properties
      };
      //create_defaulter_notice_pdf_pt(kafkaData);

      try {
        var payloads = [];
        var records=properties.length;
        logger.info("::Pushing data to kafka::");
        payloads.push({
          topic: config.KAFKA_BULK_PDF_TOPIC,
          messages: JSON.stringify(kafkaData)
        });
        producer.send(payloads, function(err, data) {
          if (err) {
            logger.error(err.stack || err);
            errorCallback({
              message: `error while publishing to kafka: ${err.message}`
            });
          } else {
            logger.info("jobid: " + jobid + ": published to kafka successfully");
          }
        });

        try {
          const result = await pool.query('select * from egov_defaulter_notice_pdf_info where jobid = $1', [jobid]);
          if(result.rowCount<1){
            var userid = requestinfo.userInfo.uuid;
            const insertQuery = 'INSERT INTO egov_defaulter_notice_pdf_info(jobid, uuid, recordscompleted, totalrecords, createdtime, filestoreid, lastmodifiedby, lastmodifiedtime, tenantid, locality,propertytype, businessservice, consumercode, isconsolidated, status) VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12, $13, $14,$15)';
            const curentTimeStamp = new Date().getTime();
            const status = 'INPROGRESS';
            await pool.query(insertQuery,[jobid, userid, 0, records, curentTimeStamp, null, userid, curentTimeStamp, tenantId, locality,propertytype, bussinessService, null, true, status]);
          }
        } catch (err) {
          logger.error(err.stack || err);
        } 

        res.status(201);
        res.json({
          ResponseInfo: requestinfo.RequestInfo,
          jobId:jobid,
          message: "Bulk pdf creation is in process",
        });
        
      } catch (error) {
        return renderError(res, `Failed to query bill for water and sewerage application`);
      }
     } 
       else {
        return renderError(
          res,
          "There is no property reecived in the request body",
          404
        );
      }
    } catch (ex) {
      return renderError(res, "Failed to query receipt details of the property", 500);
    }
  })
);

module.exports = router;
