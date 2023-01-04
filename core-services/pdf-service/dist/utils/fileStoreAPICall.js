"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.fileStoreAPICall = undefined;

var _regenerator = require("babel-runtime/regenerator");

var _regenerator2 = _interopRequireDefault(_regenerator);

var _extends2 = require("babel-runtime/helpers/extends");

var _extends3 = _interopRequireDefault(_extends2);

var _asyncToGenerator2 = require("babel-runtime/helpers/asyncToGenerator");

var _asyncToGenerator3 = _interopRequireDefault(_asyncToGenerator2);

var _request = require("request");

var _request2 = _interopRequireDefault(_request);

var _fs = require("fs");

var _fs2 = _interopRequireDefault(_fs);

var _get = require("lodash/get");

var _get2 = _interopRequireDefault(_get);

var _axios = require("axios");

var _axios2 = _interopRequireDefault(_axios);

var _EnvironmentVariables = require("../EnvironmentVariables");

var _EnvironmentVariables2 = _interopRequireDefault(_EnvironmentVariables);

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

var FormData = require("form-data");


var egovFileHost = _EnvironmentVariables2.default.EGOV_FILESTORE_SERVICE_HOST;

/**
 *
 * @param {*} filename -name of localy stored temporary file
 * @param {*} tenantId - tenantID
 */
var fileStoreAPICall = exports.fileStoreAPICall = function () {
  var _ref = (0, _asyncToGenerator3.default)( /*#__PURE__*/_regenerator2.default.mark(function _callee(filename, tenantId, fileData) {
    var url, form, response;
    return _regenerator2.default.wrap(function _callee$(_context) {
      while (1) {
        switch (_context.prev = _context.next) {
          case 0:
            url = egovFileHost + "/filestore/v1/files?tenantId=" + tenantId + "&module=pdfgen&tag=00040-2017-QR";
            form = new FormData();

            form.append("file", fileData, {
              filename: filename,
              contentType: "application/pdf"
            });
            _context.next = 5;
            return _axios2.default.post(url, form, {
              headers: (0, _extends3.default)({}, form.getHeaders())
            });

          case 5:
            response = _context.sent;
            return _context.abrupt("return", (0, _get2.default)(response.data, "files[0].fileStoreId"));

          case 7:
          case "end":
            return _context.stop();
        }
      }
    }, _callee, this);
  }));

  return function fileStoreAPICall(_x, _x2, _x3) {
    return _ref.apply(this, arguments);
  };
}();
//# sourceMappingURL=fileStoreAPICall.js.map