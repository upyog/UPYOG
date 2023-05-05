"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.fillValues = exports.createAndSave = undefined;

var _typeof2 = require("babel-runtime/helpers/typeof");

var _typeof3 = _interopRequireDefault(_typeof2);

var _extends2 = require("babel-runtime/helpers/extends");

var _extends3 = _interopRequireDefault(_extends2);

var _slicedToArray2 = require("babel-runtime/helpers/slicedToArray");

var _slicedToArray3 = _interopRequireDefault(_slicedToArray2);

var _toConsumableArray2 = require("babel-runtime/helpers/toConsumableArray");

var _toConsumableArray3 = _interopRequireDefault(_toConsumableArray2);

var _regenerator = require("babel-runtime/regenerator");

var _regenerator2 = _interopRequireDefault(_regenerator);

var _asyncToGenerator2 = require("babel-runtime/helpers/asyncToGenerator");

var _asyncToGenerator3 = _interopRequireDefault(_asyncToGenerator2);

var _http = require("http");

var _http2 = _interopRequireDefault(_http);

var _request = require("request");

var _request2 = _interopRequireDefault(_request);

var _express = require("express");

var _express2 = _interopRequireDefault(_express);

var _logger = require("./config/logger");

var _logger2 = _interopRequireDefault(_logger);

var _path = require("path");

var _path2 = _interopRequireDefault(_path);

var _fs = require("fs");

var _fs2 = _interopRequireDefault(_fs);

var _axios = require("axios");

var _axios2 = _interopRequireDefault(_axios);

var _cors = require("cors");

var _cors2 = _interopRequireDefault(_cors);

var _morgan = require("morgan");

var _morgan2 = _interopRequireDefault(_morgan);

var _bodyParser = require("body-parser");

var _bodyParser2 = _interopRequireDefault(_bodyParser);

var _expressAsyncHandler = require("express-async-handler");

var _expressAsyncHandler2 = _interopRequireDefault(_expressAsyncHandler);

var _pdfmake = require("pdfmake/build/pdfmake");

var pdfmake = _interopRequireWildcard(_pdfmake);

var _vfs_fonts = require("pdfmake/build/vfs_fonts");

var pdfFonts = _interopRequireWildcard(_vfs_fonts);

var _get = require("lodash/get");

var _get2 = _interopRequireDefault(_get);

var _set = require("lodash/set");

var _set2 = _interopRequireDefault(_set);

var _assert = require("assert");

var _repl = require("repl");

var _fileStoreAPICall = require("./utils/fileStoreAPICall");

var _directMapping = require("./utils/directMapping");

var _externalAPIMapping = require("./utils/externalAPIMapping");

var _EnvironmentVariables = require("./EnvironmentVariables");

var _EnvironmentVariables2 = _interopRequireDefault(_EnvironmentVariables);

var _qrcode = require("qrcode");

var _qrcode2 = _interopRequireDefault(_qrcode);

var _commons = require("./utils/commons");

var _queries = require("./queries");

var _consumer = require("./kafka/consumer");

function _interopRequireWildcard(obj) { if (obj && obj.__esModule) { return obj; } else { var newObj = {}; if (obj != null) { for (var key in obj) { if (Object.prototype.hasOwnProperty.call(obj, key)) newObj[key] = obj[key]; } } newObj.default = obj; return newObj; } }

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

var jp = require("jsonpath");
//create binary
pdfMake.vfs = pdfFonts.pdfMake.vfs;
var pdfMakePrinter = require("pdfmake/src/printer");

var app = (0, _express2.default)();
app.use(_express2.default.static(_path2.default.join(__dirname, "public")));
app.use(_bodyParser2.default.json({
  limit: "10mb",
  extended: true
}));
app.use(_bodyParser2.default.urlencoded({
  limit: "10mb",
  extended: true
}));

var maxPagesAllowed = _EnvironmentVariables2.default.MAX_NUMBER_PAGES;
var serverport = _EnvironmentVariables2.default.SERVER_PORT;

var dataConfigUrls = _EnvironmentVariables2.default.DATA_CONFIG_URLS;
var formatConfigUrls = _EnvironmentVariables2.default.FORMAT_CONFIG_URLS;

var dataConfigMap = {};
var formatConfigMap = {};

var topicKeyMap = {};
var topic = [];
var datafileLength = dataConfigUrls.split(",").length;
var unregisteredLocalisationCodes = [];

var fontDescriptors = {
  Cambay: {
    normal: "src/fonts/Cambay-Regular.ttf",
    bold: "src/fonts/Cambay-Bold.ttf",
    italics: "src/fonts/Cambay-Italic.ttf",
    bolditalics: "src/fonts/Cambay-BoldItalic.ttf"
  },
  Roboto: {
    bold: "src/fonts/Roboto-Bold.ttf",
    normal: "src/fonts/Roboto-Regular.ttf"
  },
  BalooBhaina: {
    normal: "src/fonts/BalooBhaina2-Regular.ttf",
    bold: "src/fonts/BalooBhaina2-Bold.ttf"
  },
  BalooPaaji: {
    normal: "src/fonts/BalooPaaji2-Regular.ttf",
    bold: "src/fonts/BalooPaaji2-Bold.ttf"
  },
  Times: {
    normal: "src/fonts/TimesNewRoman.ttf",
    bold: "src/fonts/TimesNewRoman-Bold.ttf",
    italics: "src/fonts/TimesNewRoman-Italic.ttf",
    bolditalics: "src/fonts/TimesNewRoman-BoldItalic.ttf"
  },
  NotoSansMalayalam: {
    normal: "src/fonts/NotoSansMalayalam-Regular.ttf",
    bold: "src/fonts/NotoSansMalayalam-Bold.ttf"
  },
  Meera: {
    normal: "src/fonts/Meera.ttf",
    bold: "src/fonts/Meera.ttf",
    italics: "src/fonts/Meera.ttf",
    bolditalics: "src/fonts/Meera.ttf"
  },
  Thumba: {
    normal: "src/fonts/THUMBA.ttf",
    bold: "src/fonts/THUMBA-Bold.ttf",
    italics: "src/fonts/THUMBA_ITALIC.ttf",
    bolditalics: "src/fonts/THUMBA-BoldItalic.ttf"
  }
};

var defaultFontMapping = {
  en_IN: 'default',
  ml_IN: 'default'
};

var printer = new pdfMakePrinter(fontDescriptors);
var uuidv4 = require("uuid/v4");

var mustache = require("mustache");
mustache.escape = function (text) {
  return text;
};
var borderLayout = {
  hLineColor: function hLineColor(i, node) {
    return "#979797";
  },
  vLineColor: function vLineColor(i, node) {
    return "#979797";
  },
  hLineWidth: function hLineWidth(i, node) {
    return 0.5;
  },
  vLineWidth: function vLineWidth(i, node) {
    return 0.5;
  }
};

/**
 *
 * @param {*} key - name of the key used to identify module configs. Provided request URL
 * @param {*} listDocDefinition - doc definitions as per pdfmake and formatconfig, each for each file
 * @param {*} successCallback - callaback when success
 * @param {*} errorCallback - callback when error
 * @param {*} tenantId - tenantID
 */
var createPdfBinary = function () {
  var _ref = (0, _asyncToGenerator3.default)( /*#__PURE__*/_regenerator2.default.mark(function _callee(key, listDocDefinition, entityIds, formatconfig, successCallback, errorCallback, tenantId, starttime, totalobjectcount, userid, documentType, moduleName) {
    var noOfDefinitions, jobid, dbInsertSingleRecords, dbInsertBulkRecords;
    return _regenerator2.default.wrap(function _callee$(_context) {
      while (1) {
        switch (_context.prev = _context.next) {
          case 0:
            try {
              noOfDefinitions = listDocDefinition.length;
              jobid = "" + key + new Date().getTime();

              if (noOfDefinitions == 0) {
                _logger2.default.error("no file generated for pdf");
                errorCallback({
                  message: " error: no file generated for pdf"
                });
              } else {
                dbInsertSingleRecords = [];
                dbInsertBulkRecords = [];
                // instead of awaiting the promise, use process.nextTick to asynchronously upload the receipt
                //

                process.nextTick(function () {
                  uploadFiles(dbInsertSingleRecords, dbInsertBulkRecords, formatconfig, listDocDefinition, key, false, jobid, noOfDefinitions, entityIds, starttime, successCallback, errorCallback, tenantId, totalobjectcount, userid, documentType, moduleName), uploadFiles(dbInsertSingleRecords, dbInsertBulkRecords, formatconfig, listDocDefinition, key, true, jobid, noOfDefinitions, entityIds, starttime, successCallback, errorCallback, tenantId, totalobjectcount, userid, documentType, moduleName);
                });
              }
            } catch (err) {
              _logger2.default.error(err.stack || err);
              errorCallback({
                message: " error occured while creating pdf: " + (typeof err === "string" ? err : err.message)
              });
            }

          case 1:
          case "end":
            return _context.stop();
        }
      }
    }, _callee, undefined);
  }));

  return function createPdfBinary(_x, _x2, _x3, _x4, _x5, _x6, _x7, _x8, _x9, _x10, _x11, _x12) {
    return _ref.apply(this, arguments);
  };
}();

var uploadFiles = function () {
  var _ref2 = (0, _asyncToGenerator3.default)( /*#__PURE__*/_regenerator2.default.mark(function _callee2(dbInsertSingleRecords, dbInsertBulkRecords, formatconfig, listDocDefinition, key, isconsolidated, jobid, noOfDefinitions, entityIds, starttime, successCallback, errorCallback, tenantId, totalobjectcount, userid, documentType, moduleName) {
    var convertedListDocDefinition, listOfFilestoreIds;
    return _regenerator2.default.wrap(function _callee2$(_context2) {
      while (1) {
        switch (_context2.prev = _context2.next) {
          case 0:
            convertedListDocDefinition = [];
            listOfFilestoreIds = [];


            if (!isconsolidated) {
              listDocDefinition.forEach(function (docDefinition) {
                docDefinition["content"].forEach(function (defn) {
                  var formatobject = JSON.parse(JSON.stringify(formatconfig));
                  console.log(formatobject);
                  formatobject["content"] = defn;
                  convertedListDocDefinition.push(formatobject);
                });
              });
            } else {
              convertedListDocDefinition = [].concat((0, _toConsumableArray3.default)(listDocDefinition));
            }

            convertedListDocDefinition.forEach(function (docDefinition, i) {
              // making copy because createPdfKitDocument function modifies passed object and this object is used
              // in multiple places
              var objectCopy = JSON.parse(JSON.stringify(docDefinition));
              // restoring footer because JSON.stringify destroys function() values
              console.log("format footer-------");
              console.log(formatconfig.footer);
              objectCopy.footer = (0, _commons.convertFooterStringtoFunctionIfExist)(formatconfig.footer);
              var doc = printer.createPdfKitDocument(objectCopy);
              var fileNameAppend = "-" + new Date().getTime();
              // let filename="src/pdfs/"+key+" "+fileNameAppend+".pdf"
              var filename = key + "" + fileNameAppend + ".pdf";
              //reference link
              //https://medium.com/@kainikhil/nodejs-how-to-generate-and-properly-serve-pdf-6835737d118e#d8e5

              //storing file on local computer/server

              var chunks = [];

              doc.on("data", function (chunk) {
                chunks.push(chunk);
              });
              doc.on("end", function () {
                // console.log("enddddd "+cr++);
                var data = Buffer.concat(chunks);
                (0, _fileStoreAPICall.fileStoreAPICall)(filename, tenantId, data).then(function (result) {
                  listOfFilestoreIds.push(result);
                  if (!isconsolidated) {
                    dbInsertSingleRecords.push({
                      jobid: jobid,
                      id: uuidv4(),
                      createdby: userid,
                      modifiedby: userid,
                      entityid: entityIds[i],
                      isconsolidated: false,
                      filestoreids: [result],
                      tenantId: tenantId,
                      createdtime: starttime,
                      endtime: new Date().getTime(),
                      totalcount: 1,
                      key: key,
                      documentType: documentType,
                      moduleName: moduleName
                    });

                    // insertStoreIds(jobid,entityIds[i],[result],tenantId,starttime,successCallback,errorCallback,1,false);
                  } else if (isconsolidated && listOfFilestoreIds.length == noOfDefinitions) {
                    // insertStoreIds("",);
                    // logger.info("PDF uploaded to filestore");
                    dbInsertBulkRecords.push({
                      jobid: jobid,
                      id: uuidv4(),
                      createdby: userid,
                      modifiedby: userid,
                      entityid: null,
                      isconsolidated: true,
                      filestoreids: listOfFilestoreIds,
                      tenantId: tenantId,
                      createdtime: starttime,
                      endtime: new Date().getTime(),
                      totalcount: totalobjectcount,
                      key: key,
                      documentType: documentType,
                      moduleName: moduleName
                    });
                  }
                  if (dbInsertSingleRecords.length == totalobjectcount && dbInsertBulkRecords.length == 1) {
                    (0, _queries.insertStoreIds)(dbInsertSingleRecords.concat(dbInsertBulkRecords), jobid, listOfFilestoreIds, tenantId, starttime, successCallback, errorCallback, totalobjectcount, key, documentType, moduleName);
                  }
                }).catch(function (err) {
                  _logger2.default.error(err.stack || err);
                  errorCallback({
                    message: "error occurred while uploading pdf: " + (typeof err === "string") ? err : err.message
                  });
                });
              });
              doc.end();
            });

          case 4:
          case "end":
            return _context2.stop();
        }
      }
    }, _callee2, undefined);
  }));

  return function uploadFiles(_x13, _x14, _x15, _x16, _x17, _x18, _x19, _x20, _x21, _x22, _x23, _x24, _x25, _x26, _x27, _x28, _x29) {
    return _ref2.apply(this, arguments);
  };
}();

app.post("/pdf-service/v1/_create", (0, _expressAsyncHandler2.default)(function () {
  var _ref3 = (0, _asyncToGenerator3.default)( /*#__PURE__*/_regenerator2.default.mark(function _callee3(req, res) {
    var requestInfo;
    return _regenerator2.default.wrap(function _callee3$(_context3) {
      while (1) {
        switch (_context3.prev = _context3.next) {
          case 0:
            requestInfo = void 0;
            _context3.prev = 1;

            requestInfo = (0, _get2.default)(req.body, "RequestInfo");
            _context3.next = 5;
            return createAndSave(req, res, function (response) {
              // doc successfully created
              res.status(201);
              res.json({
                ResponseInfo: requestInfo,
                message: response.message,
                filestoreIds: response.filestoreIds,
                jobid: response.jobid,
                createdtime: response.starttime,
                endtime: response.endtime,
                tenantid: response.tenantid,
                totalcount: response.totalcount,
                key: response.key,
                documentType: response.documentType,
                moduleName: response.moduleName
              });
            }, function (error) {
              res.status(400);
              // doc creation error
              res.json({
                ResponseInfo: requestInfo,
                message: "error in createPdfBinary " + error.message
              });
            });

          case 5:
            _context3.next = 12;
            break;

          case 7:
            _context3.prev = 7;
            _context3.t0 = _context3["catch"](1);

            _logger2.default.error(_context3.t0.stack || _context3.t0);
            res.status(400);
            res.json({
              ResponseInfo: requestInfo,
              message: "some unknown error while creating: " + _context3.t0.message
            });

          case 12:
          case "end":
            return _context3.stop();
        }
      }
    }, _callee3, undefined, [[1, 7]]);
  }));

  return function (_x30, _x31) {
    return _ref3.apply(this, arguments);
  };
}()));

app.post("/pdf-service/v1/_createnosave", (0, _expressAsyncHandler2.default)(function () {
  var _ref4 = (0, _asyncToGenerator3.default)( /*#__PURE__*/_regenerator2.default.mark(function _callee4(req, res) {
    var requestInfo, starttime, key, tenantId, formatconfig, dataconfig, valid, _ref5, _ref6, formatConfigByFile, totalobjectcount, entityIds, doc, fileNameAppend, filename, chunks;

    return _regenerator2.default.wrap(function _callee4$(_context4) {
      while (1) {
        switch (_context4.prev = _context4.next) {
          case 0:
            requestInfo = void 0;
            _context4.prev = 1;
            starttime = new Date().getTime();
            key = req.query.key;
            tenantId = req.query.tenantId;
            formatconfig = formatConfigMap[key];

            console.log("printing format config--------");
            console.log(formatconfig);
            dataconfig = dataConfigMap[key];

            _logger2.default.info("received createnosave request on key: " + key);
            requestInfo = (0, _get2.default)(req.body, "RequestInfo");
            //

            valid = validateRequest(req, res, key, tenantId, requestInfo);

            if (!valid) {
              _context4.next = 30;
              break;
            }

            _context4.next = 15;
            return prepareBegin(key, req, requestInfo, true, formatconfig, dataconfig);

          case 15:
            _ref5 = _context4.sent;
            _ref6 = (0, _slicedToArray3.default)(_ref5, 3);
            formatConfigByFile = _ref6[0];
            totalobjectcount = _ref6[1];
            entityIds = _ref6[2];

            // restoring footer function
            console.log("---footer");
            console.log(formatconfig.footer);
            formatConfigByFile[0].footer = (0, _commons.convertFooterStringtoFunctionIfExist)(formatconfig.footer);
            doc = printer.createPdfKitDocument(formatConfigByFile[0]);
            fileNameAppend = "-" + new Date().getTime();
            filename = key + "" + fileNameAppend + ".pdf";
            chunks = [];

            doc.on("data", function (chunk) {
              chunks.push(chunk);
            });
            doc.on("end", function () {
              // console.log("enddddd "+cr++);
              var data = Buffer.concat(chunks);
              res.writeHead(201, {
                // 'Content-Type': mimetype,
                "Content-disposition": "attachment;filename=" + filename,
                "Content-Length": data.length
              });
              _logger2.default.info("createnosave success for pdf with key: " + key + ", entityId " + entityIds);
              res.end(Buffer.from(data, "binary"));
            });
            doc.end();

          case 30:
            _context4.next = 37;
            break;

          case 32:
            _context4.prev = 32;
            _context4.t0 = _context4["catch"](1);

            _logger2.default.error(_context4.t0.stack || _context4.t0);
            res.status(400);
            res.json({
              message: "some unknown error while creating: " + _context4.t0.message
            });

          case 37:
          case "end":
            return _context4.stop();
        }
      }
    }, _callee4, undefined, [[1, 32]]);
  }));

  return function (_x32, _x33) {
    return _ref4.apply(this, arguments);
  };
}()));

app.post("/pdf-service/v1/_search", (0, _expressAsyncHandler2.default)(function () {
  var _ref7 = (0, _asyncToGenerator3.default)( /*#__PURE__*/_regenerator2.default.mark(function _callee5(req, res) {
    var requestInfo, tenantid, jobid, isconsolidated, entityid;
    return _regenerator2.default.wrap(function _callee5$(_context5) {
      while (1) {
        switch (_context5.prev = _context5.next) {
          case 0:
            requestInfo = void 0;

            try {
              tenantid = req.query.tenantid;
              jobid = req.query.jobid;
              isconsolidated = req.query.isconsolidated;
              entityid = req.query.entityid;

              requestInfo = (0, _get2.default)(req.body, "RequestInfo");
              if ((jobid == undefined || jobid.trim() == "") && (entityid == undefined || entityid.trim() == "")) {
                res.status(400);
                res.json({
                  ResponseInfo: requestInfo,
                  message: "jobid and entityid both can not be empty"
                });
              } else {
                if (jobid) {
                  if (jobid.includes(",")) {
                    jobid = jobid.split(",");
                  } else {
                    jobid = [jobid];
                  }
                }

                (0, _queries.getFileStoreIds)(jobid, tenantid, isconsolidated, entityid, function (responseBody) {
                  // doc successfully created
                  res.status(responseBody.status);
                  delete responseBody.status;
                  res.json((0, _extends3.default)({
                    ResponseInfo: requestInfo
                  }, responseBody));
                });
              }
            } catch (error) {
              _logger2.default.error(error.stack || error);
              res.status(400);
              res.json({
                ResponseInfo: requestInfo,
                message: "some unknown error while searching: " + error.message
              });
            }

          case 2:
          case "end":
            return _context5.stop();
        }
      }
    }, _callee5, undefined);
  }));

  return function (_x34, _x35) {
    return _ref7.apply(this, arguments);
  };
}()));

app.post("/pdf-service/v1/_getUnrigesteredCodes", (0, _expressAsyncHandler2.default)(function () {
  var _ref8 = (0, _asyncToGenerator3.default)( /*#__PURE__*/_regenerator2.default.mark(function _callee6(req, res) {
    var requestInfo;
    return _regenerator2.default.wrap(function _callee6$(_context6) {
      while (1) {
        switch (_context6.prev = _context6.next) {
          case 0:
            requestInfo = void 0;

            try {
              requestInfo = (0, _get2.default)(req.body, "RequestInfo");
              res.status(200);
              res.json({
                ResponseInfo: requestInfo,
                unregisteredLocalisationCodes: unregisteredLocalisationCodes
              });
            } catch (error) {
              _logger2.default.error(error.stack || error);
              res.status(400);
              res.json({
                ResponseInfo: requestInfo,
                message: "Error while retreving the codes"
              });
            }

          case 2:
          case "end":
            return _context6.stop();
        }
      }
    }, _callee6, undefined);
  }));

  return function (_x36, _x37) {
    return _ref8.apply(this, arguments);
  };
}()));

app.post("/pdf-service/v1/_clearUnrigesteredCodes", (0, _expressAsyncHandler2.default)(function () {
  var _ref9 = (0, _asyncToGenerator3.default)( /*#__PURE__*/_regenerator2.default.mark(function _callee7(req, res) {
    var requestInfo, resposnseMap;
    return _regenerator2.default.wrap(function _callee7$(_context7) {
      while (1) {
        switch (_context7.prev = _context7.next) {
          case 0:
            requestInfo = void 0;
            _context7.prev = 1;

            requestInfo = (0, _get2.default)(req.body, "RequestInfo");
            _context7.next = 5;
            return (0, _commons.findLocalisation)(requestInfo, [], unregisteredLocalisationCodes);

          case 5:
            resposnseMap = _context7.sent;


            resposnseMap.messages.map(function (item) {
              if (unregisteredLocalisationCodes.includes(item.code)) {
                var index = unregisteredLocalisationCodes.indexOf(item.code);
                unregisteredLocalisationCodes.splice(index, 1);
              }
            });
            res.status(200);
            res.json({
              ResponseInfo: requestInfo,
              unregisteredLocalisationCodes: unregisteredLocalisationCodes
            });
            _context7.next = 16;
            break;

          case 11:
            _context7.prev = 11;
            _context7.t0 = _context7["catch"](1);

            _logger2.default.error(_context7.t0.stack || _context7.t0);
            res.status(400);
            res.json({
              ResponseInfo: requestInfo,
              message: "Error while retreving the codes"
            });

          case 16:
          case "end":
            return _context7.stop();
        }
      }
    }, _callee7, undefined, [[1, 11]]);
  }));

  return function (_x38, _x39) {
    return _ref9.apply(this, arguments);
  };
}()));

var i = 0;
dataConfigUrls && dataConfigUrls.split(",").map(function (item) {
  item = item.trim();
  if (item.includes("file://")) {
    item = item.replace("file://", "");
    _fs2.default.readFile(item, "utf8", function (err, data) {
      try {
        if (err) {
          _logger2.default.error("error when reading file for dataconfig: file:///" + item);
          _logger2.default.error(err.stack);
        } else {
          data = JSON.parse(data);
          dataConfigMap[data.key] = data;
          if (data.fromTopic != null) {
            topicKeyMap[data.fromTopic] = data.key;
            topic.push(data.fromTopic);
          }
          i++;
          if (i == datafileLength) {
            (0, _consumer.listenConsumer)(topic);
          }
          _logger2.default.info("loaded dataconfig: file:///" + item);
        }
      } catch (error) {
        _logger2.default.error("error in loading dataconfig: file:///" + item);
        _logger2.default.error(error.stack);
      }
    });
  } else {
    (0, _asyncToGenerator3.default)( /*#__PURE__*/_regenerator2.default.mark(function _callee8() {
      var response;
      return _regenerator2.default.wrap(function _callee8$(_context8) {
        while (1) {
          switch (_context8.prev = _context8.next) {
            case 0:
              _context8.prev = 0;
              _context8.next = 3;
              return _axios2.default.get(item);

            case 3:
              response = _context8.sent;

              dataConfigMap[response.data.key] = response.data;
              _logger2.default.info("loaded dataconfig: " + item);
              _context8.next = 12;
              break;

            case 8:
              _context8.prev = 8;
              _context8.t0 = _context8["catch"](0);

              _logger2.default.error("error in loading dataconfig: " + item);
              _logger2.default.error(_context8.t0.stack);

            case 12:
            case "end":
              return _context8.stop();
          }
        }
      }, _callee8, undefined, [[0, 8]]);
    }))();
  }
});

formatConfigUrls && formatConfigUrls.split(",").map(function (item) {
  item = item.trim();
  if (item.includes("file://")) {
    item = item.replace("file://", "");
    _fs2.default.readFile(item, "utf8", function (err, data) {
      try {
        if (err) {
          _logger2.default.error(err.stack);
          _logger2.default.error("error when reading file for formatconfig: file:///" + item);
        } else {
          data = JSON.parse(data);
          formatConfigMap[data.key] = data.config;
          _logger2.default.info("loaded formatconfig: file:///" + item);
        }
      } catch (error) {
        _logger2.default.error("error in loading formatconfig: file:///" + item);
        _logger2.default.error(error.stack);
      }
    });
  } else {
    (0, _asyncToGenerator3.default)( /*#__PURE__*/_regenerator2.default.mark(function _callee9() {
      var response;
      return _regenerator2.default.wrap(function _callee9$(_context9) {
        while (1) {
          switch (_context9.prev = _context9.next) {
            case 0:
              _context9.prev = 0;
              _context9.next = 3;
              return _axios2.default.get(item);

            case 3:
              response = _context9.sent;

              formatConfigMap[response.data.key] = response.data.config;
              _logger2.default.info("loaded formatconfig: " + item);
              _context9.next = 12;
              break;

            case 8:
              _context9.prev = 8;
              _context9.t0 = _context9["catch"](0);

              _logger2.default.error("error in loading formatconfig: " + item);
              _logger2.default.error(_context9.t0.stack);

            case 12:
            case "end":
              return _context9.stop();
          }
        }
      }, _callee9, undefined, [[0, 8]]);
    }))();
  }
});

app.listen(serverport, function () {
  _logger2.default.info("Server running at http:" + serverport + "/");
});

/**
 *
 * @param {*} formatconfig - format config read from formatconfig file
 */

// Create endpoint flow
// createAndSave-> prepareBegin-->prepareBulk --> handlelogic-------------|
// createPdfBinary<---prepareBegin <--createPdfBinary <------prepareBulk ---<

var createAndSave = exports.createAndSave = function () {
  var _ref12 = (0, _asyncToGenerator3.default)( /*#__PURE__*/_regenerator2.default.mark(function _callee10(req, res, successCallback, errorCallback) {
    var starttime, topic, key, tenantId, formatconfigNew, dataconfig, userid, requestInfo, documentType, moduleName, formatconfig, valid, _ref13, _ref14, formatConfigByFile, totalobjectcount, entityIds, locale;

    return _regenerator2.default.wrap(function _callee10$(_context10) {
      while (1) {
        switch (_context10.prev = _context10.next) {
          case 0:
            starttime = new Date().getTime();
            topic = (0, _get2.default)(req, "topic");
            key = void 0;

            if (topic != null && topicKeyMap[topic] != null) {
              key = topicKeyMap[topic];
            } else {
              key = (0, _get2.default)(req.query || req, "key");
            }
            //let key = get(req.query || req, "key");
            tenantId = (0, _get2.default)(req.query || req, "tenantId");
            formatconfigNew = formatConfigMap[key];
            dataconfig = dataConfigMap[key];
            userid = (0, _get2.default)(req.body || req, "RequestInfo.userInfo.id");
            requestInfo = (0, _get2.default)(req.body || req, "RequestInfo");
            documentType = (0, _get2.default)(dataconfig, "documentType", "");
            moduleName = (0, _get2.default)(dataconfig, "DataConfigs.moduleName", "");
            formatconfig = JSON.parse(JSON.stringify(formatconfigNew));

            console.log(formatconfig.defaultStyle);
            valid = validateRequest(req, res, key, tenantId, requestInfo);

            if (!valid) {
              _context10.next = 29;
              break;
            }

            _context10.next = 17;
            return prepareBegin(key, req, requestInfo, false, formatconfig, dataconfig);

          case 17:
            _ref13 = _context10.sent;
            _ref14 = (0, _slicedToArray3.default)(_ref13, 3);
            formatConfigByFile = _ref14[0];
            totalobjectcount = _ref14[1];
            entityIds = _ref14[2];


            // logger.info(`Applied templating engine on ${moduleObjectsArray.length} objects output will be in ${formatConfigByFile.length} files`);
            _logger2.default.info("Applied templating engine on " + totalobjectcount + " objects output will be in " + formatConfigByFile.length + " files");
            // var util = require('util');
            // fs.writeFileSync('./data.txt', util.inspect(JSON.stringify(formatconfig)) , 'utf-8');
            //function to download pdf automatically
            locale = requestInfo.msgId.split('|')[1];

            if (!locale) locale = _EnvironmentVariables2.default.DEFAULT_LOCALISATION_LOCALE;

            if (defaultFontMapping[locale] != 'default') {
              formatconfig.defaultStyle.font = defaultFontMapping[locale];
            }

            console.log(" Font type selected :::: " + formatconfig.defaultStyle.font);
            console.log("Locale passed:::::::" + locale);

            createPdfBinary(key, formatConfigByFile, entityIds, formatconfig, successCallback, errorCallback, tenantId, starttime, totalobjectcount, userid, documentType, moduleName).catch(function (err) {
              _logger2.default.error(err.stack || err);
              errorCallback({
                message: "error occurred in createPdfBinary call: " + (typeof err === "string") ? err : err.message
              });
            });

          case 29:
          case "end":
            return _context10.stop();
        }
      }
    }, _callee10, undefined);
  }));

  return function createAndSave(_x40, _x41, _x42, _x43) {
    return _ref12.apply(this, arguments);
  };
}();
var updateBorderlayout = function updateBorderlayout(formatconfig) {
  formatconfig.content = formatconfig.content.map(function (item) {
    if (item.hasOwnProperty("layout") && (0, _typeof3.default)(item.layout) === "object" && Object.keys(item.layout).length === 0) {
      item.layout = borderLayout;
    }
    return item;
  });
  return formatconfig;
};

/**
 *
 * @param {*} variableTovalueMap - key, value map. Keys are variable defined in data config
 * and value is their corresponding values. Map will be used by Moustache template engine
 * @param {*} formatconfig -format config read from formatconfig file
 */
var fillValues = exports.fillValues = function fillValues(variableTovalueMap, formatconfig) {
  var input = JSON.stringify(formatconfig).replace(/\\/g, "");

  //console.log(variableTovalueMap);
  //console.log(mustache.render(input, variableTovalueMap).replace(/""/g,"\"").replace(/"\[/g,"\[").replace(/\]"/g,"\]").replace(/\]\[/g,"\],\[").replace(/"\{/g,"\{").replace(/\}"/g,"\}"));
  var output = JSON.parse(mustache.render(input, variableTovalueMap).replace(/""/g, '\""')
  //.replace(/\\/g, "")
  .replace(/"\[/g, "[").replace(/\]"/g, "]").replace(/\]\[/g, "],[").replace(/"\{/g, "{").replace(/\n/g, "\\n").replace(/\t/g, "\\t"));
  return output;
};

/**
 * generateQRCodes-function to geneerate qrcodes
 * moduleObject-current module object from request body
 * dataconfig- data config read from dataconfig of module
 */
var generateQRCodes = function () {
  var _ref15 = (0, _asyncToGenerator3.default)( /*#__PURE__*/_regenerator2.default.mark(function _callee11(moduleObject, dataconfig, variableTovalueMap) {
    var qrcodeMappings, i, len, qrmapping, varname, qrtext, qrCodeImage;
    return _regenerator2.default.wrap(function _callee11$(_context11) {
      while (1) {
        switch (_context11.prev = _context11.next) {
          case 0:
            qrcodeMappings = (0, _commons.getValue)(jp.query(dataconfig, "$.DataConfigs.mappings.*.mappings.*.qrcodeConfig.*"), [], "$.DataConfigs.mappings.*.mappings.*.qrcodeConfig.*");
            i = 0, len = qrcodeMappings.length;

          case 2:
            if (!(i < len)) {
              _context11.next = 13;
              break;
            }

            qrmapping = qrcodeMappings[i];
            varname = qrmapping.variable;
            qrtext = mustache.render(qrmapping.value, variableTovalueMap);
            _context11.next = 8;
            return _qrcode2.default.toDataURL(qrtext);

          case 8:
            qrCodeImage = _context11.sent;

            variableTovalueMap[varname] = qrCodeImage;

          case 10:
            i++;
            _context11.next = 2;
            break;

          case 13:
          case "end":
            return _context11.stop();
        }
      }
    }, _callee11, undefined);
  }));

  return function generateQRCodes(_x44, _x45, _x46) {
    return _ref15.apply(this, arguments);
  };
}();

var handleDerivedMapping = function handleDerivedMapping(dataconfig, variableTovalueMap) {
  var derivedMappings = (0, _commons.getValue)(jp.query(dataconfig, "$.DataConfigs.mappings.*.mappings.*.derived.*"), [], "$.DataConfigs.mappings.*.mappings.*.derived.*");

  for (var i = 0, len = derivedMappings.length; i < len; i++) {
    var mapping = derivedMappings[i];
    var expression = mustache.render(mapping.formula.replace(/-/g, " - ").replace(/\+/g, " + "), variableTovalueMap).replace(/NA/g, "0");
    variableTovalueMap[mapping.variable] = Function("'use strict'; return (" + expression + ")")();
  }
};

var validateRequest = function validateRequest(req, res, key, tenantId, requestInfo) {
  var errorMessage = "";
  if (key == undefined || key.trim() === "") {
    errorMessage += " key is missing,";
  }
  if (tenantId == undefined || tenantId.trim() === "") {
    errorMessage += " tenantId is missing,";
  }
  if (requestInfo == undefined) {
    errorMessage += " requestInfo is missing,";
  }
  if (requestInfo && requestInfo.userInfo == undefined) {
    errorMessage += " userInfo is missing,";
  }
  if (formatConfigMap[key] == undefined || dataConfigMap[key] == undefined) {
    errorMessage += " no config found for key " + key;
  }
  if (res && errorMessage !== "") {
    res.status(400);
    res.json({
      message: errorMessage,
      ResponseInfo: requestInfo
    });
    return false;
  } else {
    return true;
  }
};

var prepareBegin = function () {
  var _ref16 = (0, _asyncToGenerator3.default)( /*#__PURE__*/_regenerator2.default.mark(function _callee12(key, req, requestInfo, returnFileInResponse, formatconfig, dataconfig) {
    var baseKeyPath, entityIdPath;
    return _regenerator2.default.wrap(function _callee12$(_context12) {
      while (1) {
        switch (_context12.prev = _context12.next) {
          case 0:
            baseKeyPath = (0, _get2.default)(dataconfig, "DataConfigs.baseKeyPath");
            entityIdPath = (0, _get2.default)(dataconfig, "DataConfigs.entityIdPath");

            if (!(baseKeyPath == null)) {
              _context12.next = 5;
              break;
            }

            _logger2.default.error("baseKeyPath is absent in config");
            throw {
              message: "baseKeyPath is absent in config"
            };

          case 5:
            _context12.next = 7;
            return prepareBulk(key, dataconfig, formatconfig, req, baseKeyPath, requestInfo, returnFileInResponse, entityIdPath);

          case 7:
            return _context12.abrupt("return", _context12.sent);

          case 8:
          case "end":
            return _context12.stop();
        }
      }
    }, _callee12, undefined);
  }));

  return function prepareBegin(_x47, _x48, _x49, _x50, _x51, _x52) {
    return _ref16.apply(this, arguments);
  };
}();

var handlelogic = function () {
  var _ref17 = (0, _asyncToGenerator3.default)( /*#__PURE__*/_regenerator2.default.mark(function _callee13(key, formatObject, moduleObject, dataconfig, isCommonTableBorderRequired, requestInfo) {
    var variableTovalueMap;
    return _regenerator2.default.wrap(function _callee13$(_context13) {
      while (1) {
        switch (_context13.prev = _context13.next) {
          case 0:
            variableTovalueMap = {};
            //direct mapping service

            _context13.next = 3;
            return Promise.all([(0, _directMapping.directMapping)(moduleObject, dataconfig, variableTovalueMap, requestInfo, unregisteredLocalisationCodes),
            //external API mapping
            (0, _externalAPIMapping.externalAPIMapping)(key, moduleObject, dataconfig, variableTovalueMap, requestInfo, unregisteredLocalisationCodes)]);

          case 3:
            _context13.next = 5;
            return generateQRCodes(moduleObject, dataconfig, variableTovalueMap);

          case 5:
            handleDerivedMapping(dataconfig, variableTovalueMap);
            formatObject = fillValues(variableTovalueMap, formatObject);
            if (isCommonTableBorderRequired === true) formatObject = updateBorderlayout(formatObject);
            return _context13.abrupt("return", formatObject);

          case 9:
          case "end":
            return _context13.stop();
        }
      }
    }, _callee13, undefined);
  }));

  return function handlelogic(_x53, _x54, _x55, _x56, _x57, _x58) {
    return _ref17.apply(this, arguments);
  };
}();

// const prepareSingle=(key)=>{
//   handlelogic();
// }

var prepareBulk = function () {
  var _ref18 = (0, _asyncToGenerator3.default)( /*#__PURE__*/_regenerator2.default.mark(function _callee14(key, dataconfig, formatconfig, req, baseKeyPath, requestInfo, returnFileInResponse, entityIdPath) {
    var isCommonTableBorderRequired, formatObjectArrayObject, formatConfigByFile, totalobjectcount, entityIds, countOfObjectsInCurrentFile, moduleObjectsArray, i, len, moduleObject, entityKey, formatObject, formatconfigCopy, locale;
    return _regenerator2.default.wrap(function _callee14$(_context14) {
      while (1) {
        switch (_context14.prev = _context14.next) {
          case 0:
            isCommonTableBorderRequired = (0, _get2.default)(dataconfig, "DataConfigs.isCommonTableBorderRequired");
            formatObjectArrayObject = [];
            formatConfigByFile = [];
            totalobjectcount = 0;
            entityIds = [];
            countOfObjectsInCurrentFile = 0;
            moduleObjectsArray = (0, _commons.getValue)(jp.query(req.body || req, baseKeyPath), [], baseKeyPath);

            if (!(Array.isArray(moduleObjectsArray) && moduleObjectsArray.length > 0)) {
              _context14.next = 28;
              break;
            }

            totalobjectcount = moduleObjectsArray.length;
            i = 0, len = moduleObjectsArray.length;

          case 10:
            if (!(i < len)) {
              _context14.next = 25;
              break;
            }

            moduleObject = moduleObjectsArray[i];
            entityKey = (0, _commons.getValue)(jp.query(moduleObject, entityIdPath), [null], entityIdPath);

            entityIds.push(entityKey[0]);

            formatObject = JSON.parse(JSON.stringify(formatconfig));

            // Multipage pdf, each pdf from new page

            if (formatObjectArrayObject.length != 0 && formatObject["content"][0] !== undefined) {
              formatObject["content"][0]["pageBreak"] = "before";
            }

            /////////////////////////////
            _context14.next = 18;
            return handlelogic(key, formatObject, moduleObject, dataconfig, isCommonTableBorderRequired, requestInfo);

          case 18:
            formatObject = _context14.sent;


            formatObjectArrayObject.push(formatObject["content"]);
            countOfObjectsInCurrentFile++;
            if (!returnFileInResponse && countOfObjectsInCurrentFile == maxPagesAllowed || i + 1 == len) {
              formatconfigCopy = JSON.parse(JSON.stringify(formatconfig));
              locale = requestInfo.msgId.split('|')[1];

              if (!locale) locale = _EnvironmentVariables2.default.DEFAULT_LOCALISATION_LOCALE;

              if (defaultFontMapping[locale] != 'default') {
                formatconfigCopy.defaultStyle.font = defaultFontMapping[locale];
              }

              formatconfigCopy["content"] = formatObjectArrayObject;
              formatConfigByFile.push(formatconfigCopy);
              formatObjectArrayObject = [];
              countOfObjectsInCurrentFile = 0;
            }

          case 22:
            i++;
            _context14.next = 10;
            break;

          case 25:
            return _context14.abrupt("return", [formatConfigByFile, totalobjectcount, entityIds]);

          case 28:
            _logger2.default.error("could not find property of type array in request body with name " + baseKeyPath);
            throw {
              message: "could not find property of type array in request body with name " + baseKeyPath
            };

          case 30:
          case "end":
            return _context14.stop();
        }
      }
    }, _callee14, undefined);
  }));

  return function prepareBulk(_x59, _x60, _x61, _x62, _x63, _x64, _x65, _x66) {
    return _ref18.apply(this, arguments);
  };
}();
exports.default = app;
//# sourceMappingURL=index.js.map