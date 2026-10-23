// Before: used require() for winston - CommonJS style, incompatible with ESM
// Change: replaced require() with import - native ESM for Node 22

import { createLogger, format, transports } from "winston";

const logger = createLogger({
  format: format.combine(
    format.timestamp({ format: "    YYYY-MM-DD HH:mm:ss.SSSZZ" }),
    format.simple()
  ),
  transports: [new transports.Console()]
});

export default logger;
