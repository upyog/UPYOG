/**
 * This imports the createProxyMiddleware function from http-proxy-middleware.
 * It is used to create a proxy that forwards requests to a target backend server.
 */

const { createProxyMiddleware } = require("http-proxy-middleware");
const createProxy = createProxyMiddleware({
  target: process.env.REACT_APP_PROXY_URL,
  changeOrigin: true,
});
module.exports = function (app) {
  [
    "/egov-mdms-service",
    "/egov-location",
    "/localization",
    "/egov-workflow-v2",
    "/filestore",
    "/user-otp",
    "/user",
    "/billing-service",
    "/collection-services",
    "/pdf-service",
    "/pg-service",
  ].forEach((location) =>
    app.use(location, createProxy)
    /**
 * Above loops through a list of API paths and applies the createProxy middleware to each one.
 * Any request that matches these paths will be automatically forwarded to https://qa.digit.org
 */
  );
};