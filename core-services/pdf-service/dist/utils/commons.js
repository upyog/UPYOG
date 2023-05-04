"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.convertFooterStringtoFunctionIfExist = exports.getValue = exports.getDateInRequiredFormat = exports.getLocalisationkey = exports.findLocalisation = exports.getTransformedLocale = undefined;

var _regenerator = require("babel-runtime/regenerator");

var _regenerator2 = _interopRequireDefault(_regenerator);

var _asyncToGenerator2 = require("babel-runtime/helpers/asyncToGenerator");

var _asyncToGenerator3 = _interopRequireDefault(_asyncToGenerator2);

var _axios = require("axios");

var _axios2 = _interopRequireDefault(_axios);

var _EnvironmentVariables = require("../EnvironmentVariables");

var _EnvironmentVariables2 = _interopRequireDefault(_EnvironmentVariables);

var _get = require("lodash/get");

var _get2 = _interopRequireDefault(_get);

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

var moment = require("moment-timezone");

var datetimezone = _EnvironmentVariables2.default.DATE_TIMEZONE;
var egovLocHost = _EnvironmentVariables2.default.EGOV_LOCALISATION_HOST;
var egovLocSearchCall = _EnvironmentVariables2.default.EGOV_LOCALISATION_SEARCH;
var defaultLocale = _EnvironmentVariables2.default.DEFAULT_LOCALISATION_LOCALE;
var defaultTenant = _EnvironmentVariables2.default.DEFAULT_LOCALISATION_TENANT;
var getTransformedLocale = exports.getTransformedLocale = function getTransformedLocale(label) {
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
var findLocalisation = exports.findLocalisation = function () {
  var _ref = (0, _asyncToGenerator3.default)( /*#__PURE__*/_regenerator2.default.mark(function _callee(requestInfo, moduleList, codeList) {
    var locale, statetenantid, url, request, headers, responseBody;
    return _regenerator2.default.wrap(function _callee$(_context) {
      while (1) {
        switch (_context.prev = _context.next) {
          case 0:
            locale = requestInfo.msgId;

            if (null != locale) {
              locale = locale.split("|");
              locale = locale.length > 1 ? locale[1] : defaultLocale;
            } else {
              locale = defaultLocale;
            }
            console.log(requestInfo);
            console.log(defaultTenant);
            statetenantid = (0, _get2.default)(requestInfo, "userInfo.tenantId", defaultTenant).split(".")[0];
            url = egovLocHost + egovLocSearchCall;
            request = {
              RequestInfo: requestInfo,
              messageSearchCriteria: {
                tenantId: statetenantid,
                locale: locale,
                codes: []
              }
            };


            request.messageSearchCriteria.module = moduleList.toString();
            request.messageSearchCriteria.codes = codeList.toString().split(",");

            headers = {
              headers: {
                "content-type": "application/json;charset=UTF-8",
                accept: "application/json, text/plain, */*"
              }
            };

            console.log("localization call");
            console.log("localization call url=" + url);
            console.log(request);
            console.log(headers);
            _context.next = 16;
            return _axios2.default.post(url, request, headers).catch(function (error) {
              console.log(error);
              throw error.response.data;
            });

          case 16:
            responseBody = _context.sent;
            return _context.abrupt("return", responseBody.data);

          case 18:
          case "end":
            return _context.stop();
        }
      }
    }, _callee, undefined);
  }));

  return function findLocalisation(_x, _x2, _x3) {
    return _ref.apply(this, arguments);
  };
}();
var getLocalisationkey = exports.getLocalisationkey = function () {
  var _ref2 = (0, _asyncToGenerator3.default)( /*#__PURE__*/_regenerator2.default.mark(function _callee2(prefix, key, isCategoryRequired, isMainTypeRequired, isSubTypeRequired) {
    var delimiter = arguments.length > 5 && arguments[5] !== undefined ? arguments[5] : " / ";
    var keyArray, localisedLabels, isArray;
    return _regenerator2.default.wrap(function _callee2$(_context2) {
      while (1) {
        switch (_context2.prev = _context2.next) {
          case 0:
            keyArray = [];
            localisedLabels = [];
            isArray = false;

            if (!(key == null)) {
              _context2.next = 7;
              break;
            }

            return _context2.abrupt("return", key);

          case 7:
            if (typeof key == "string" || typeof key == "number") {
              keyArray.push(key);
            } else {
              keyArray = key;
              isArray = true;
            }

          case 8:
            keyArray.map(function (item) {
              var codeFromKey = "";

              // append main category in the beginning
              if (isCategoryRequired) {
                codeFromKey = getLocalisationLabel(item.split(".")[0], prefix);
              }

              if (isMainTypeRequired) {
                if (isCategoryRequired) codeFromKey = "" + codeFromKey + delimiter;
                codeFromKey = getLocalisationLabel(item.split(".")[1], prefix);
              }

              if (isSubTypeRequired) {
                if (isMainTypeRequired || isCategoryRequired) codeFromKey = "" + codeFromKey + delimiter;
                codeFromKey = "" + codeFromKey + getLocalisationLabel(item, prefix);
              }

              if (!isCategoryRequired && !isMainTypeRequired && !isSubTypeRequired) {
                codeFromKey = getLocalisationLabel(item, prefix);
              }

              localisedLabels.push(codeFromKey === "" ? item : codeFromKey);
            });

            if (!isArray) {
              _context2.next = 11;
              break;
            }

            return _context2.abrupt("return", localisedLabels);

          case 11:
            return _context2.abrupt("return", localisedLabels[0]);

          case 12:
          case "end":
            return _context2.stop();
        }
      }
    }, _callee2, undefined);
  }));

  return function getLocalisationkey(_x4, _x5, _x6, _x7, _x8) {
    return _ref2.apply(this, arguments);
  };
}();

var getLocalisationLabel = function getLocalisationLabel(key, prefix) {
  if (prefix != undefined && prefix != "") {
    key = prefix + "_" + key;
  }
  key = getTransformedLocale(key);
  return key;
};

var getDateInRequiredFormat = exports.getDateInRequiredFormat = function getDateInRequiredFormat(et) {
  var dateformat = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : "DD/MM/YYYY";

  if (!et) return "NA";
  // var date = new Date(Math.round(Number(et)));
  return moment(et).tz(datetimezone).format(dateformat);
};

/**
 *
 * @param {*} value - values to be checked
 * @param {*} defaultValue - default value
 * @param {*} path  - jsonpath from where the value was fetched
 */
var getValue = exports.getValue = function getValue(value, defaultValue, path) {
  if (value == undefined || value == null || value.length === 0 || value.length === 1 && (value[0] === null || value[0] === "")) {
    // logger.error(`no value found for path: ${path}`);
    return defaultValue;
  } else return value;
};

var convertFooterStringtoFunctionIfExist = exports.convertFooterStringtoFunctionIfExist = function convertFooterStringtoFunctionIfExist(footer) {
  if (footer != undefined) {
    footer = Function("'use strict'; return (" + footer + ")")();
  }
  console.log(footer);
  return footer;
};
//# sourceMappingURL=commons.js.map