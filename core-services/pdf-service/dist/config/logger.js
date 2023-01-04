"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});

var _require = require("winston"),
    createLogger = _require.createLogger,
    format = _require.format,
    transports = _require.transports;

var logger = createLogger({
  format: format.combine(format.timestamp({ format: "    YYYY-MM-DD HH:mm:ss.SSSZZ" }), format.simple()),
  transports: [new transports.Console()]
});

exports.default = logger;
//# sourceMappingURL=logger.js.map