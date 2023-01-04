"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.externalAPIMapping = undefined;

var _regenerator = require("babel-runtime/regenerator");

var _regenerator2 = _interopRequireDefault(_regenerator);

var _typeof2 = require("babel-runtime/helpers/typeof");

var _typeof3 = _interopRequireDefault(_typeof2);

var _asyncToGenerator2 = require("babel-runtime/helpers/asyncToGenerator");

var _asyncToGenerator3 = _interopRequireDefault(_asyncToGenerator2);

var _get = require("lodash/get");

var _get2 = _interopRequireDefault(_get);

var _axios = require("axios");

var _axios2 = _interopRequireDefault(_axios);

var _commons = require("./commons");

var _logger = require("../config/logger");

var _logger2 = _interopRequireDefault(_logger);

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

/**
 *
 * @param {*} key -name of the key used to identify module configs. Provided request URL
 * @param {*} req -current module object, picked from request body
 * @param {*} dataconfig - data config
 * @param {*} variableTovalueMap -map used for filling values by template engine 'mustache'
 * @param {*} localisationMap -Map to store localisation key, value pair
 * @param {*} requestInfo -request info from request body
 */

function escapeRegex(string) {
  if (typeof string == "string") return string.replace(/[\\"]/g, '\\$&');else return string;
}

var externalAPIMapping = exports.externalAPIMapping = function () {
  var _ref = (0, _asyncToGenerator3.default)( /*#__PURE__*/_regenerator2.default.mark(function _callee(key, req, dataconfig, variableTovalueMap, requestInfo, unregisteredLocalisationCodes) {
    var jp, objectOfExternalAPI, externalAPIArray, localisationCodes, localisationModules, variableToModuleMap, responses, responsePromises, i, temp1, temp2, flag, j, temp3, _j, headers, resPromise, _i, res, _j2, replaceValue, loc, imageData, len, response, myDate, arrayOfOwnerObject, _externalAPIArray$_i$, _externalAPIArray$_i$2, format, _externalAPIArray$_i$3, value, variable, _format$scema, scema, val, l, ownerObject, k, fieldValue, _myDate, _replaceValue, _loc, currentValue, _currentValue, _currentValue2, _currentValue3, localisationMap, resposnseMap;

    return _regenerator2.default.wrap(function _callee$(_context) {
      while (1) {
        switch (_context.prev = _context.next) {
          case 0:
            jp = require("jsonpath");
            objectOfExternalAPI = (0, _commons.getValue)(jp.query(dataconfig, "$.DataConfigs.mappings.*.mappings.*.externalAPI.*"), [], "$.DataConfigs.mappings.*.mappings.*.externalAPI.*");
            externalAPIArray = objectOfExternalAPI.map(function (item) {
              return {
                uri: item.path,
                queryParams: item.queryParam,
                jPath: item.responseMapping,
                requesttype: item.requesttype || "POST",
                variable: "",
                val: ""
              };
            });
            localisationCodes = [];
            localisationModules = [];
            variableToModuleMap = {};
            responses = [];
            responsePromises = [];


            for (i = 0; i < externalAPIArray.length; i++) {
              temp1 = "";
              temp2 = "";
              flag = 0;
              //to convert queryparam and uri into properURI

              //for PT module

              if (key == "pt-receipt") {
                for (j = 0; j < externalAPIArray[i].queryParams.length; j++) {
                  if (externalAPIArray[i].queryParams[j] == "$") {
                    flag = 1;
                  }
                  if (externalAPIArray[i].queryParams[j] == "," || externalAPIArray[i].queryParams[j] == ":") {
                    if (flag == 1) {
                      temp2 = temp1;
                      temp3 = (0, _commons.getValue)(jp.query(req, temp1), "NA", temp1);

                      externalAPIArray[i].queryParams = externalAPIArray[i].queryParams.replace(temp2, temp3);

                      j = 0;
                      flag = 0;
                      temp1 = "";
                      temp2 = "";
                    }
                  }

                  if (flag == 1) {
                    temp1 += externalAPIArray[i].queryParams[j];
                  }
                  if (j == externalAPIArray[i].queryParams.length - 1 && flag == 1) {
                    temp2 = temp1;
                    temp3 = (0, _commons.getValue)(jp.query(req, temp1), "NA", temp1);


                    externalAPIArray[i].queryParams = externalAPIArray[i].queryParams.replace(temp2, temp3);

                    flag = 0;
                    temp1 = "";
                    temp2 = "";
                  }
                }
              }
              //for other modules
              else {
                  for (_j = 0; _j < externalAPIArray[i].queryParams.length; _j++) {
                    if (externalAPIArray[i].queryParams[_j] == "{") {
                      externalAPIArray[i].queryParams = externalAPIArray[i].queryParams.replace("{", "");
                    }

                    if (externalAPIArray[i].queryParams[_j] == "$") {
                      flag = 1;
                    }
                    if (externalAPIArray[i].queryParams[_j] == "," || externalAPIArray[i].queryParams[_j] == "}") {
                      if (flag == 1) {
                        temp2 = temp1;

                        temp3 = (0, _commons.getValue)(jp.query(req, temp1), "NA", temp1);

                        externalAPIArray[i].queryParams = externalAPIArray[i].queryParams.replace(temp2, temp3);

                        _j = 0;
                        flag = 0;
                        temp1 = "";
                        temp2 = "";
                      }
                      if (externalAPIArray[i].queryParams[_j] == "}") {
                        externalAPIArray[i].queryParams = externalAPIArray[i].queryParams.replace("}", "");
                      }
                    }
                    if (flag == 1) {
                      temp1 += externalAPIArray[i].queryParams[_j];
                    }
                    if (_j == externalAPIArray[i].queryParams.length - 1 && flag == 1) {
                      temp2 = temp1;
                      temp3 = (0, _commons.getValue)(jp.query(req, temp1), "NA", temp1);


                      externalAPIArray[i].queryParams = externalAPIArray[i].queryParams.replace(temp2, temp3);

                      flag = 0;
                      temp1 = "";
                      temp2 = "";
                    }
                  }
                }
              externalAPIArray[i].queryParams = externalAPIArray[i].queryParams.replace(/,/g, "&");
              headers = {
                "content-type": "application/json;charset=UTF-8",
                accept: "application/json, text/plain, */*"
              };

              if (externalAPIArray[i].requesttype == "POST") {
                resPromise = _axios2.default.post(externalAPIArray[i].uri + "?" + externalAPIArray[i].queryParams, {
                  RequestInfo: requestInfo
                }, {
                  headers: headers
                });
              } else {
                resPromise = _axios2.default.get(externalAPIArray[i].uri + "?" + externalAPIArray[i].queryParams, {
                  responseType: "application/json"
                });
              }
              responsePromises.push(resPromise);
            }

            _context.next = 11;
            return Promise.all(responsePromises);

          case 11:
            responses = _context.sent;
            _i = 0;

          case 13:
            if (!(_i < externalAPIArray.length)) {
              _context.next = 103;
              break;
            }

            res = responses[_i].data;

            //putting required data from external API call in format config

            _j2 = 0;

          case 16:
            if (!(_j2 < externalAPIArray[_i].jPath.length)) {
              _context.next = 100;
              break;
            }

            replaceValue = (0, _commons.getValue)(jp.query(res, externalAPIArray[_i].jPath[_j2].value), "NA", externalAPIArray[_i].jPath[_j2].value);
            loc = externalAPIArray[_i].jPath[_j2].localisation;

            if (!(externalAPIArray[_i].jPath[_j2].type == "image")) {
              _context.next = 37;
              break;
            }

            // default empty image
            imageData = "data:image/jpeg;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8Xw8AAoMBgDTD2qgAAAAASUVORK5CYII=";

            if (!(replaceValue != "NA")) {
              _context.next = 34;
              break;
            }

            _context.prev = 22;
            len = replaceValue[0].split(",").length;
            _context.next = 26;
            return _axios2.default.get(replaceValue[0].split(",")[len - 1], {
              responseType: "arraybuffer"
            });

          case 26:
            response = _context.sent;

            imageData = "data:" + response.headers["content-type"] + ";base64," + Buffer.from(response.data).toString("base64");
            _context.next = 34;
            break;

          case 30:
            _context.prev = 30;
            _context.t0 = _context["catch"](22);

            _logger2.default.error(_context.t0.stack || _context.t0);
            throw {
              message: "error while loading image from: " + replaceValue[0]
            };

          case 34:
            variableTovalueMap[externalAPIArray[_i].jPath[_j2].variable] = imageData;
            _context.next = 97;
            break;

          case 37:
            if (!(externalAPIArray[_i].jPath[_j2].type == "date")) {
              _context.next = 42;
              break;
            }

            myDate = new Date(replaceValue[0]);

            if (isNaN(myDate) || replaceValue[0] === 0) {
              variableTovalueMap[externalAPIArray[_i].jPath[_j2].variable] = "NA";
            } else {
              replaceValue = (0, _commons.getDateInRequiredFormat)(replaceValue[0], externalAPIArray[_i].jPath[_j2].format);
              variableTovalueMap[externalAPIArray[_i].jPath[_j2].variable] = replaceValue;
            }
            _context.next = 97;
            break;

          case 42:
            if (!(externalAPIArray[_i].jPath[_j2].type == "array")) {
              _context.next = 81;
              break;
            }

            arrayOfOwnerObject = [];
            // let ownerObject = JSON.parse(JSON.stringify(get(formatconfig, directArr[i].jPath + "[0]", [])));

            _externalAPIArray$_i$ = externalAPIArray[_i].jPath[_j2], _externalAPIArray$_i$2 = _externalAPIArray$_i$.format, format = _externalAPIArray$_i$2 === undefined ? {} : _externalAPIArray$_i$2, _externalAPIArray$_i$3 = _externalAPIArray$_i$.value, value = _externalAPIArray$_i$3 === undefined ? [] : _externalAPIArray$_i$3, variable = _externalAPIArray$_i$.variable;
            _format$scema = format.scema, scema = _format$scema === undefined ? [] : _format$scema;
            val = (0, _commons.getValue)(jp.query(res, value), "NA", value);

            //taking values about owner from request body

            l = 0;

          case 48:
            if (!(l < val.length)) {
              _context.next = 78;
              break;
            }

            // var x = 1;
            ownerObject = {};
            k = 0;

          case 51:
            if (!(k < scema.length)) {
              _context.next = 74;
              break;
            }

            fieldValue = (0, _get2.default)(val[l], scema[k].value, "NA");

            fieldValue = fieldValue == null ? "NA" : fieldValue;

            if (!(scema[k].type == "date")) {
              _context.next = 59;
              break;
            }

            _myDate = new Date(fieldValue);

            if (isNaN(_myDate) || fieldValue === 0) {
              ownerObject[scema[k].variable] = "NA";
            } else {
              _replaceValue = (0, _commons.getDateInRequiredFormat)(fieldValue, scema[k].format);
              // set(formatconfig,externalAPIArray[i].jPath[j].variable,replaceValue);

              ownerObject[scema[k].variable] = _replaceValue;
            }
            _context.next = 71;
            break;

          case 59:
            if (!(fieldValue !== "NA" && scema[k].localisation && scema[k].localisation.required)) {
              _context.next = 67;
              break;
            }

            _loc = scema[k].localisation;
            _context.next = 63;
            return (0, _commons.getLocalisationkey)(_loc.prefix, fieldValue, _loc.isCategoryRequired, _loc.isMainTypeRequired, _loc.isSubTypeRequired, _loc.delimiter);

          case 63:
            fieldValue = _context.sent;

            if (!localisationCodes.includes(fieldValue)) localisationCodes.push(fieldValue);

            if (!localisationModules.includes(_loc.module)) localisationModules.push(_loc.module);

            variableToModuleMap[scema[k].variable] = _loc.module;

          case 67:
            //console.log("\nvalue-->"+fieldValue)
            currentValue = fieldValue;

            if ((typeof currentValue === "undefined" ? "undefined" : (0, _typeof3.default)(currentValue)) == "object" && currentValue.length > 0) currentValue = currentValue[0];

            currentValue = escapeRegex(currentValue);
            ownerObject[scema[k].variable] = currentValue;

          case 71:
            k++;
            _context.next = 51;
            break;

          case 74:
            arrayOfOwnerObject.push(ownerObject);

          case 75:
            l++;
            _context.next = 48;
            break;

          case 78:

            variableTovalueMap[variable] = arrayOfOwnerObject;
            //console.log("\nvariableTovalueMap[externalAPIArray[i].jPath.variable]--->\n"+JSON.stringify(variableTovalueMap[externalAPIArray[i].jPath.variable]));

            _context.next = 97;
            break;

          case 81:
            if (!(replaceValue !== "NA" && externalAPIArray[_i].jPath[_j2].localisation && externalAPIArray[_i].jPath[_j2].localisation.required && externalAPIArray[_i].jPath[_j2].localisation.prefix)) {
              _context.next = 92;
              break;
            }

            _context.next = 84;
            return (0, _commons.getLocalisationkey)(loc.prefix, replaceValue, loc.isCategoryRequired, loc.isMainTypeRequired, loc.isSubTypeRequired, loc.delimiter);

          case 84:
            _currentValue = _context.sent;

            if ((typeof _currentValue === "undefined" ? "undefined" : (0, _typeof3.default)(_currentValue)) == "object" && _currentValue.length > 0) _currentValue = _currentValue[0];

            //currentValue = escapeRegex(currentValue);
            if (!localisationCodes.includes(_currentValue)) localisationCodes.push(_currentValue);

            if (!localisationModules.includes(loc.module)) localisationModules.push(loc.module);

            variableTovalueMap[externalAPIArray[_i].jPath[_j2].variable] = _currentValue;

            variableToModuleMap[externalAPIArray[_i].jPath[_j2].variable] = loc.module;

            _context.next = 96;
            break;

          case 92:
            _currentValue2 = replaceValue;

            if ((typeof _currentValue2 === "undefined" ? "undefined" : (0, _typeof3.default)(_currentValue2)) == "object" && _currentValue2.length > 0) _currentValue2 = _currentValue2[0];

            // currentValue=currentValue.replace(/\\/g,"\\\\").replace(/"/g,'\\"');
            _currentValue2 = escapeRegex(_currentValue2);
            variableTovalueMap[externalAPIArray[_i].jPath[_j2].variable] = _currentValue2;

          case 96:
            if (externalAPIArray[_i].jPath[_j2].isUpperCaseRequired) {
              _currentValue3 = variableTovalueMap[externalAPIArray[_i].jPath[_j2].variable];

              if ((typeof _currentValue3 === "undefined" ? "undefined" : (0, _typeof3.default)(_currentValue3)) == "object" && _currentValue3.length > 0) _currentValue3 = _currentValue3[0];

              variableTovalueMap[externalAPIArray[_i].jPath[_j2].variable] = _currentValue3.toUpperCase();
            }

          case 97:
            _j2++;
            _context.next = 16;
            break;

          case 100:
            _i++;
            _context.next = 13;
            break;

          case 103:
            localisationMap = [];
            _context.prev = 104;
            _context.next = 107;
            return (0, _commons.findLocalisation)(requestInfo, localisationModules, localisationCodes);

          case 107:
            resposnseMap = _context.sent;


            resposnseMap.messages.map(function (item) {
              localisationMap[item.code + "_" + item.module] = item.message;
            });
            _context.next = 115;
            break;

          case 111:
            _context.prev = 111;
            _context.t1 = _context["catch"](104);

            _logger2.default.error(_context.t1.stack || _context.t1);
            throw {
              message: "Error in localisation service call: " + _context.t1.Errors[0].message
            };

          case 115:

            Object.keys(variableTovalueMap).forEach(function (key) {
              if (variableToModuleMap[key] && typeof variableTovalueMap[key] == 'string') {
                var code = variableTovalueMap[key];
                var module = variableToModuleMap[key];
                if (localisationMap[code + "_" + module]) {
                  variableTovalueMap[key] = localisationMap[code + "_" + module];
                  if (unregisteredLocalisationCodes.includes(code)) {
                    var index = unregisteredLocalisationCodes.indexOf(code);
                    unregisteredLocalisationCodes.splice(index, 1);
                  }
                } else {
                  if (!unregisteredLocalisationCodes.includes(code)) unregisteredLocalisationCodes.push(code);
                }
              }

              if ((0, _typeof3.default)(variableTovalueMap[key]) == 'object') {
                Object.keys(variableTovalueMap[key]).forEach(function (objectKey) {
                  Object.keys(variableTovalueMap[key][objectKey]).forEach(function (objectItemkey) {
                    if (variableToModuleMap[objectItemkey]) {
                      var module = variableToModuleMap[objectItemkey];
                      var code = variableTovalueMap[key][objectKey][objectItemkey];
                      if (localisationMap[code + "_" + module]) {
                        variableTovalueMap[key][objectKey][objectItemkey] = localisationMap[code + "_" + module];
                        if (unregisteredLocalisationCodes.includes(code)) {
                          var index = unregisteredLocalisationCodes.indexOf(code);
                          unregisteredLocalisationCodes.splice(index, 1);
                        }
                      } else {
                        if (!unregisteredLocalisationCodes.includes(code)) unregisteredLocalisationCodes.push(code);
                      }
                    }
                  });
                });
              }
            });

          case 116:
          case "end":
            return _context.stop();
        }
      }
    }, _callee, this, [[22, 30], [104, 111]]);
  }));

  return function externalAPIMapping(_x, _x2, _x3, _x4, _x5, _x6) {
    return _ref.apply(this, arguments);
  };
}();
//# sourceMappingURL=externalAPIMapping.js.map