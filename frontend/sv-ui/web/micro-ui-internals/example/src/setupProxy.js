/**
 * This imports the createProxyMiddleware function from http-proxy-middleware.
 * It is used to create a proxy that forwards requests to a target backend server.
 */
const { createProxyMiddleware } = require("http-proxy-middleware");

const createProxy = createProxyMiddleware({
  target: process.env.REACT_APP_PROXY_API || "https://niuatt.niua.in",
  changeOrigin: true,
});
const assetsProxy = createProxyMiddleware({
  target: process.env.REACT_APP_PROXY_ASSETS || "https://niuatt.niua.in",
  changeOrigin: true,
});
module.exports = function (app) {
  [
    "/access/v1/actions/mdms",
    "/egov-mdms-service",
    "/egov-location",
    "/mdms-v2",
    "/localization",
    "/egov-workflow-v2",
    "/filestore",
    "/user-otp",
    "/user",
    "/billing-service",
    "/collection-services",
    "/pdf-service",
    "/pg-service",
    "/inbox/v1/_search",
    "/egov-hrms/employees/_search",
    "/egov-user-event",
    "/sv-services/street-vending/_create",
    "/sv-services/street-vending/_search",
    "/sv-services/street-vending/_update",
    "/sv-services/street-vending/_deletedraft",
    "/employee-dashboard/_search"
  ].forEach((location) => app.use(location, createProxy));
/**
 * Above loops through a list of API paths and applies the createProxy middleware to each one.
 * Any request that matches these paths will be automatically forwarded to https://qa.digit.org
 */
};