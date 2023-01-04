"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.insertStoreIds = exports.getFileStoreIds = undefined;

var _logger = require("./config/logger");

var _logger2 = _interopRequireDefault(_logger);

var _producer = require("./kafka/producer");

var _producer2 = _interopRequireDefault(_producer);

var _consumer = require("./kafka/consumer");

var _consumer2 = _interopRequireDefault(_consumer);

var _EnvironmentVariables = require("./EnvironmentVariables");

var _EnvironmentVariables2 = _interopRequireDefault(_EnvironmentVariables);

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

var Pool = require("pg").Pool;


var pool = new Pool({
  user: _EnvironmentVariables2.default.DB_USER,
  host: _EnvironmentVariables2.default.DB_HOST,
  database: _EnvironmentVariables2.default.DB_NAME,
  password: _EnvironmentVariables2.default.DB_PASSWORD,
  port: _EnvironmentVariables2.default.DB_PORT
});

var createJobKafkaTopic = _EnvironmentVariables2.default.KAFKA_CREATE_JOB_TOPIC;
var uuidv4 = require("uuid/v4");

var getFileStoreIds = exports.getFileStoreIds = function getFileStoreIds(jobid, tenantId, isconsolidated, entityid, callback) {
  var searchquery = "";
  var queryparams = [];
  var next = 1;
  var jobidPresent = false;
  searchquery = "SELECT * FROM egov_pdf_gen WHERE";

  if (jobid != undefined && jobid.length > 0) {
    searchquery += " jobid = ANY ($" + next++ + ")";
    queryparams.push(jobid);
    jobidPresent = true;
  }

  if (entityid != undefined && entityid.trim() !== "") {
    if (jobidPresent) searchquery += " and";
    searchquery += " entityid = ($" + next++ + ")";
    queryparams.push(entityid);
  }

  if (tenantId != undefined && tenantId.trim() !== "") {
    searchquery += " and tenantid = ($" + next++ + ")";
    queryparams.push(tenantId);
  }

  if (isconsolidated != undefined && isconsolidated.trim() !== "") {
    var ifTrue = isconsolidated === "true" || isconsolidated === "True";
    var ifFalse = isconsolidated === "false" || isconsolidated === "false";
    if (ifTrue || ifFalse) {
      searchquery += " and isconsolidated = ($" + next++ + ")";
      queryparams.push(ifTrue);
    }
  }
  searchquery = "SELECT pdf_1.* FROM egov_pdf_gen pdf_1 INNER JOIN (SELECT entityid, max(endtime) as MaxEndTime from (" + searchquery + ") as pdf_2 group by entityid) pdf_3 ON pdf_1.entityid = pdf_3.entityid AND pdf_1.endtime = pdf_3.MaxEndTime";
  pool.query(searchquery, queryparams, function (error, results) {
    if (error) {
      _logger2.default.error(error.stack || error);
      callback({
        status: 400,
        message: "error occured while searching records in DB : " + error.message
      });
    } else {
      if (results && results.rows.length > 0) {
        var searchresult = [];
        results.rows.map(function (crow) {
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
        _logger2.default.info(results.rows.length + " matching records found in search");
        callback({ status: 200, message: "Success", searchresult: searchresult });
      } else {
        _logger2.default.error("no result found in DB search");
        callback({ status: 404, message: "no matching result found" });
      }
    }
  });
};

var insertStoreIds = exports.insertStoreIds = function insertStoreIds(dbInsertRecords, jobid, filestoreids, tenantId, starttime, successCallback, errorCallback, totalcount, key, documentType, moduleName) {
  var payloads = [];
  var endtime = new Date().getTime();
  var id = uuidv4();
  payloads.push({
    topic: createJobKafkaTopic,
    messages: JSON.stringify({ jobs: dbInsertRecords })
  });
  _producer2.default.send(payloads, function (err, data) {
    if (err) {
      _logger2.default.error(err.stack || err);
      errorCallback({
        message: "error while publishing to kafka: " + err.message
      });
    } else {
      _logger2.default.info("jobid: " + jobid + ": published to kafka successfully");
      successCallback({
        message: "Success",
        jobid: jobid,
        filestoreIds: filestoreids,
        tenantid: tenantId,
        starttime: starttime,
        endtime: endtime,
        totalcount: totalcount,
        key: key,
        documentType: documentType,
        moduleName: moduleName
      });
    }
  });
};
//# sourceMappingURL=queries.js.map