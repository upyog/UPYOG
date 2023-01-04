"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.httpRequest = undefined;

var _regenerator = require("babel-runtime/regenerator");

var _regenerator2 = _interopRequireDefault(_regenerator);

var _asyncToGenerator2 = require("babel-runtime/helpers/asyncToGenerator");

var _asyncToGenerator3 = _interopRequireDefault(_asyncToGenerator2);

var _axios = require("axios");

var _axios2 = _interopRequireDefault(_axios);

var _logger = require("../config/logger");

var _logger2 = _interopRequireDefault(_logger);

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

// const instance = axios.create({
//     endPoint: "https://egov-micro-dev.egovernments.org/",
//     headers: {
//       "Content-Type": "application/json",
//     }
//   });

var httpRequest = exports.httpRequest = function () {
  var _ref = (0, _asyncToGenerator3.default)( /*#__PURE__*/_regenerator2.default.mark(function _callee(endPoint, requestBody) {
    var headers = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : defaultheader;
    var instance, response, responseStatus, errorResponse;
    return _regenerator2.default.wrap(function _callee$(_context) {
      while (1) {
        switch (_context.prev = _context.next) {
          case 0:
            instance = _axios2.default.create({
              endPoint: endPoint,
              requestBody: requestBody

            });

            if (headers) instance.defaults = Object.assign(instance.defaults, {
              headers: headers
            });

            _context.prev = 2;
            _context.next = 5;
            return instance.post(endPoint, requestBody);

          case 5:
            response = _context.sent;
            responseStatus = parseInt(response.status, 10);

            if (!(responseStatus === 200 || responseStatus === 201)) {
              _context.next = 9;
              break;
            }

            return _context.abrupt("return", response.data);

          case 9:
            _context.next = 16;
            break;

          case 11:
            _context.prev = 11;
            _context.t0 = _context["catch"](2);
            errorResponse = _context.t0.response;

            _logger2.default.error(_context.t0.stack || _context.t0);
            throw { message: "error occured while making request to " + endPoint + ": response returned by call :" + (errorResponse ? parseInt(errorResponse.status, 10) : _context.t0.message) };

          case 16:
          case "end":
            return _context.stop();
        }
      }
    }, _callee, undefined, [[2, 11]]);
  }));

  return function httpRequest(_x, _x2) {
    return _ref.apply(this, arguments);
  };
}();

var defaultheader = {
  "content-type": "application/json;charset=UTF-8",
  "accept": "application/json, text/plain, */*"
};
//# sourceMappingURL=api.js.map