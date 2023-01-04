"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.directMapping = undefined;

var _regenerator = require("babel-runtime/regenerator");

var _regenerator2 = _interopRequireDefault(_regenerator);

var _typeof2 = require("babel-runtime/helpers/typeof");

var _typeof3 = _interopRequireDefault(_typeof2);

var _asyncToGenerator2 = require("babel-runtime/helpers/asyncToGenerator");

var _asyncToGenerator3 = _interopRequireDefault(_asyncToGenerator2);

var _get = require("lodash/get");

var _get2 = _interopRequireDefault(_get);

var _logger = require("../config/logger");

var _logger2 = _interopRequireDefault(_logger);

var _axios = require("axios");

var _axios2 = _interopRequireDefault(_axios);

var _EnvironmentVariables = require("../EnvironmentVariables");

var _EnvironmentVariables2 = _interopRequireDefault(_EnvironmentVariables);

var _commons = require("./commons");

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

var jp = require("jsonpath");

var externalHost = _EnvironmentVariables2.default.EGOV_EXTERNAL_HOST;
/**
 *
 * @param {*} req - current module object, picked from request body
 * @param {*} dataconfig  - data config
 * @param {*} variableTovalueMap - map used for filling values by template engine 'mustache'
 * @param {*} localisationMap - Map to store localisation key, value pair
 * @param {*} requestInfo - request info from request body
 */

function escapeRegex(string) {
  if (typeof string == "string") return string.replace(/[\\"]/g, '\\$&');else return string;
}

var directMapping = exports.directMapping = function () {
  var _ref = (0, _asyncToGenerator3.default)( /*#__PURE__*/_regenerator2.default.mark(function _callee(req, dataconfig, variableTovalueMap, requestInfo, unregisteredLocalisationCodes) {
    var directArr, localisationCodes, localisationModules, variableToModuleMap, objectOfDirectMapping, i, fun, response, arrayOfOwnerObject, _directArr$i, _directArr$i$format, format, _directArr$i$val, val, variable, _format$scema, scema, j, ownerObject, k, fieldValue, myDate, replaceValue, loc, currentValue, arrayOfBuiltUpDetails, isOrderedList, _directArr$i2, _directArr$i2$format, _format, _directArr$i2$val, _val, _variable, _format$scema2, _scema, _j, arrayOfItems, _k, _fieldValue, _myDate, _replaceValue, p, orderedList, _loc, stringBuildpDetails, code, _myDate2, _replaceValue2, _code, _currentValue, _currentValue2, localisationMap, resposnseMap;

    return _regenerator2.default.wrap(function _callee$(_context) {
      while (1) {
        switch (_context.prev = _context.next) {
          case 0:
            directArr = [];
            localisationCodes = [];
            localisationModules = [];
            variableToModuleMap = {};
            // using jp-jsonpath because loadash can not handele '*'

            objectOfDirectMapping = jp.query(dataconfig, "$.DataConfigs.mappings.*.mappings.*.direct.*");

            objectOfDirectMapping = (0, _commons.getValue)(objectOfDirectMapping, [], "$.DataConfigs.mappings.*.mappings.*.direct.*");
            directArr = objectOfDirectMapping.map(function (item) {
              return {
                jPath: item.variable,
                val: item.value && (0, _commons.getValue)(jp.query(req, item.value.path), "NA", item.value.path),
                valJsonPath: item.value && item.value.path,
                type: item.type,
                url: item.url,
                format: item.format,
                localisation: item.localisation,
                uCaseNeeded: item.isUpperCaseRequired
              };
            });

            i = 0;

          case 8:
            if (!(i < directArr.length)) {
              _context.next = 153;
              break;
            }

            //for array type direct mapping
            if (directArr[i].type == "citizen-employee-title") {
              if ((0, _get2.default)(requestInfo, "userInfo.type", "NA").toUpperCase() == "EMPLOYEE") {
                variableTovalueMap[directArr[i].jPath] = "Employee Copy";
              } else {
                variableTovalueMap[directArr[i].jPath] = "Citizen Copy";
              }
            }

            if (!(directArr[i].type == "selectFromRequestInfo")) {
              _context.next = 16;
              break;
            }

            directArr[i].val = (0, _commons.getValue)(jp.query(requestInfo, directArr[i].valJsonPath), "NA", directArr[i].valJsonPath);

            if ((0, _typeof3.default)(directArr[i].val) == "object" && directArr[i].val.length > 0) directArr[i].val = directArr[i].val[0];

            variableTovalueMap[directArr[i].jPath] = directArr[i].val;
            _context.next = 150;
            break;

          case 16:
            if (!(directArr[i].type == "external_host")) {
              _context.next = 20;
              break;
            }

            variableTovalueMap[directArr[i].jPath] = externalHost;
            _context.next = 150;
            break;

          case 20:
            if (!(directArr[i].type == "function")) {
              _context.next = 25;
              break;
            }

            fun = Function("type", directArr[i].format);

            variableTovalueMap[directArr[i].jPath] = fun(directArr[i].val[0]);
            _context.next = 150;
            break;

          case 25:
            if (!(directArr[i].type == "image")) {
              _context.next = 39;
              break;
            }

            _context.prev = 26;
            _context.next = 29;
            return _axios2.default.get(directArr[i].url, {
              responseType: "arraybuffer"
            });

          case 29:
            response = _context.sent;

            variableTovalueMap[directArr[i].jPath] = "data:" + response.headers["content-type"] + ";base64," + Buffer.from(response.data).toString("base64");
            //  logger.info("loaded image: "+directArr[i].url);
            _context.next = 37;
            break;

          case 33:
            _context.prev = 33;
            _context.t0 = _context["catch"](26);

            _logger2.default.error(_context.t0.stack || _context.t0);
            throw {
              message: "error while loading image from: " + directArr[i].url
            };

          case 37:
            _context.next = 150;
            break;

          case 39:
            if (!(directArr[i].type == "array")) {
              _context.next = 77;
              break;
            }

            arrayOfOwnerObject = [];
            // let ownerObject = JSON.parse(JSON.stringify(get(formatconfig, directArr[i].jPath + "[0]", [])));

            _directArr$i = directArr[i], _directArr$i$format = _directArr$i.format, format = _directArr$i$format === undefined ? {} : _directArr$i$format, _directArr$i$val = _directArr$i.val, val = _directArr$i$val === undefined ? [] : _directArr$i$val, variable = _directArr$i.variable;
            _format$scema = format.scema, scema = _format$scema === undefined ? [] : _format$scema;

            //taking values about owner from request body

            j = 0;

          case 44:
            if (!(j < val.length)) {
              _context.next = 74;
              break;
            }

            // var x = 1;
            ownerObject = {};
            k = 0;

          case 47:
            if (!(k < scema.length)) {
              _context.next = 70;
              break;
            }

            fieldValue = (0, _get2.default)(val[j], scema[k].value, "NA");

            fieldValue = fieldValue == null ? "NA" : fieldValue;

            if (!(scema[k].type == "date")) {
              _context.next = 55;
              break;
            }

            myDate = new Date(fieldValue);

            if (isNaN(myDate) || fieldValue === 0) {
              ownerObject[scema[k].variable] = "NA";
            } else {
              replaceValue = (0, _commons.getDateInRequiredFormat)(fieldValue, scema[k].format);
              // set(formatconfig,externalAPIArray[i].jPath[j].variable,replaceValue);

              ownerObject[scema[k].variable] = replaceValue;
            }
            _context.next = 67;
            break;

          case 55:
            if (!(fieldValue !== "NA" && scema[k].localisation && scema[k].localisation.required)) {
              _context.next = 63;
              break;
            }

            loc = scema[k].localisation;
            _context.next = 59;
            return (0, _commons.getLocalisationkey)(loc.prefix, fieldValue, loc.isCategoryRequired, loc.isMainTypeRequired, loc.isSubTypeRequired, loc.delimiter);

          case 59:
            fieldValue = _context.sent;

            if (!localisationCodes.includes(fieldValue)) localisationCodes.push(fieldValue);

            if (!localisationModules.includes(loc.module)) localisationModules.push(loc.module);

            variableToModuleMap[scema[k].variable] = loc.module;

          case 63:
            currentValue = fieldValue;

            if ((typeof currentValue === "undefined" ? "undefined" : (0, _typeof3.default)(currentValue)) == "object" && currentValue.length > 0) currentValue = currentValue[0];

            currentValue = escapeRegex(currentValue);
            ownerObject[scema[k].variable] = currentValue;

          case 67:
            k++;
            _context.next = 47;
            break;

          case 70:
            arrayOfOwnerObject.push(ownerObject);

          case 71:
            j++;
            _context.next = 44;
            break;

          case 74:
            // set(formatconfig, directArr[i].jPath, arrayOfOwnerObject);
            variableTovalueMap[directArr[i].jPath] = arrayOfOwnerObject;
            _context.next = 150;
            break;

          case 77:
            if (!(directArr[i].type == "array-column")) {
              _context.next = 118;
              break;
            }

            arrayOfBuiltUpDetails = [];
            isOrderedList = false;
            // let arrayOfFields=get(formatconfig, directArr[i].jPath+"[0]",[]);
            // arrayOfBuiltUpDetails.push(arrayOfFields);

            _directArr$i2 = directArr[i], _directArr$i2$format = _directArr$i2.format, _format = _directArr$i2$format === undefined ? {} : _directArr$i2$format, _directArr$i2$val = _directArr$i2.val, _val = _directArr$i2$val === undefined ? [] : _directArr$i2$val, _variable = _directArr$i2.variable;
            _format$scema2 = _format.scema, _scema = _format$scema2 === undefined ? [] : _format$scema2;
            //to get data of multiple floor Built up details

            _j = 0;

          case 83:
            if (!(_j < _val.length)) {
              _context.next = 113;
              break;
            }

            arrayOfItems = [];
            _k = 0;

          case 86:
            if (!(_k < _scema.length)) {
              _context.next = 109;
              break;
            }

            _fieldValue = (0, _get2.default)(_val[_j], _scema[_k].value, "NA");

            _fieldValue = _fieldValue == null ? "NA" : _fieldValue;

            if (!(_scema[_k].type == "date")) {
              _context.next = 94;
              break;
            }

            _myDate = new Date(_fieldValue);

            if (isNaN(_myDate) || _fieldValue === 0) {
              arrayOfItems.push("NA");
            } else {
              _replaceValue = (0, _commons.getDateInRequiredFormat)(_fieldValue, _scema[_k].format);
              // set(formatconfig,externalAPIArray[i].jPath[j].variable,replaceValue);

              arrayOfItems.push(_replaceValue);
            }
            _context.next = 106;
            break;

          case 94:
            if (!(_scema[_k].type == "array-orderedlist" && Array.isArray(_fieldValue))) {
              _context.next = 98;
              break;
            }

            if (_fieldValue !== "NA") {
              for (p = 0; p < _fieldValue.length; p++) {
                orderedList = [];

                orderedList.push(_fieldValue[p]);
                arrayOfBuiltUpDetails.push(orderedList);
              }
              isOrderedList = true;
            }
            _context.next = 106;
            break;

          case 98:
            if (!(_fieldValue !== "NA" && _scema[_k].localisation && _scema[_k].localisation.required)) {
              _context.next = 105;
              break;
            }

            _loc = _scema[_k].localisation;
            _context.next = 102;
            return (0, _commons.getLocalisationkey)(_loc.prefix, _fieldValue, _loc.isCategoryRequired, _loc.isMainTypeRequired, _loc.isSubTypeRequired, _loc.delimiter);

          case 102:
            _fieldValue = _context.sent;

            if (!localisationCodes.includes(_fieldValue)) localisationCodes.push(_fieldValue);

            if (!localisationModules.includes(_loc.module)) localisationModules.push(_loc.module);

          case 105:
            arrayOfItems.push(_fieldValue);

          case 106:
            _k++;
            _context.next = 86;
            break;

          case 109:
            if (isOrderedList === false) arrayOfBuiltUpDetails.push(arrayOfItems);

          case 110:
            _j++;
            _context.next = 83;
            break;

          case 113:

            // remove enclosing [ &  ]
            stringBuildpDetails = JSON.stringify(arrayOfBuiltUpDetails).replace("[", "");

            stringBuildpDetails = stringBuildpDetails.substring(0, stringBuildpDetails.length - 1);

            variableTovalueMap[directArr[i].jPath] = stringBuildpDetails;
            // set(formatconfig,directArr[i].jPath,arrayOfBuiltUpDetails);
            _context.next = 150;
            break;

          case 118:
            if (!(directArr[i].type == "label")) {
              _context.next = 128;
              break;
            }

            _context.next = 121;
            return (0, _commons.getLocalisationkey)(directArr[i].localisation.prefix, directArr[i].valJsonPath, directArr[i].localisation.isCategoryRequired, directArr[i].localisation.isMainTypeRequired, directArr[i].localisation.isSubTypeRequired, directArr[i].localisation.delimiter);

          case 121:
            code = _context.sent;

            if (!localisationCodes.includes(code)) localisationCodes.push(code);

            if (!localisationModules.includes(directArr[i].localisation.module)) localisationModules.push(directArr[i].localisation.module);

            variableTovalueMap[directArr[i].jPath] = code;
            variableToModuleMap[directArr[i].jPath] = directArr[i].localisation.module;

            _context.next = 150;
            break;

          case 128:
            if (!(directArr[i].type == "date")) {
              _context.next = 133;
              break;
            }

            _myDate2 = new Date(directArr[i].val[0]);

            if (isNaN(_myDate2) || directArr[i].val[0] === 0) {
              variableTovalueMap[directArr[i].jPath] = "NA";
            } else {
              _replaceValue2 = (0, _commons.getDateInRequiredFormat)(directArr[i].val[0], directArr[i].format);

              variableTovalueMap[directArr[i].jPath] = _replaceValue2;
            }
            _context.next = 150;
            break;

          case 133:
            directArr[i].val = (0, _commons.getValue)(directArr[i].val, "NA", directArr[i].valJsonPath);

            if (!(directArr[i].val !== "NA" && directArr[i].localisation && directArr[i].localisation.required)) {
              _context.next = 145;
              break;
            }

            _context.next = 137;
            return (0, _commons.getLocalisationkey)(directArr[i].localisation.prefix, directArr[i].val, directArr[i].localisation.isCategoryRequired, directArr[i].localisation.isMainTypeRequired, directArr[i].localisation.isSubTypeRequired, directArr[i].localisation.delimiter);

          case 137:
            _code = _context.sent;


            if ((typeof _code === "undefined" ? "undefined" : (0, _typeof3.default)(_code)) == "object" && _code.length > 0) _code = _code[0];

            if (!localisationCodes.includes(_code)) localisationCodes.push(_code);

            if (!localisationModules.includes(directArr[i].localisation.module)) localisationModules.push(directArr[i].localisation.module);

            variableTovalueMap[directArr[i].jPath] = _code;

            variableToModuleMap[directArr[i].jPath] = directArr[i].localisation.module;

            _context.next = 149;
            break;

          case 145:
            _currentValue = directArr[i].val;

            if ((typeof _currentValue === "undefined" ? "undefined" : (0, _typeof3.default)(_currentValue)) == "object" && _currentValue.length > 0) _currentValue = _currentValue[0];

            // currentValue=currentValue.replace(/\\/g,"\\\\").replace(/"/g,'\\"');
            _currentValue = escapeRegex(_currentValue);
            variableTovalueMap[directArr[i].jPath] = _currentValue;

          case 149:
            if (directArr[i].uCaseNeeded) {
              _currentValue2 = variableTovalueMap[directArr[i].jPath];

              if ((typeof _currentValue2 === "undefined" ? "undefined" : (0, _typeof3.default)(_currentValue2)) == "object" && _currentValue2.length > 0) _currentValue2 = _currentValue2[0];
              variableTovalueMap[directArr[i].jPath] = _currentValue2.toUpperCase();
            }

          case 150:
            i++;
            _context.next = 8;
            break;

          case 153:
            localisationMap = [];
            _context.prev = 154;
            _context.next = 157;
            return (0, _commons.findLocalisation)(requestInfo, localisationModules, localisationCodes);

          case 157:
            resposnseMap = _context.sent;


            resposnseMap.messages.map(function (item) {
              localisationMap[item.code + "_" + item.module] = item.message;
            });
            _context.next = 165;
            break;

          case 161:
            _context.prev = 161;
            _context.t1 = _context["catch"](154);

            _logger2.default.error(_context.t1.stack || _context.t1);
            throw {
              message: "Error in localisation service call: " + _context.t1.Errors[0].message
            };

          case 165:

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

          case 166:
          case "end":
            return _context.stop();
        }
      }
    }, _callee, undefined, [[26, 33], [154, 161]]);
  }));

  return function directMapping(_x, _x2, _x3, _x4, _x5) {
    return _ref.apply(this, arguments);
  };
}();
//# sourceMappingURL=directMapping.js.map