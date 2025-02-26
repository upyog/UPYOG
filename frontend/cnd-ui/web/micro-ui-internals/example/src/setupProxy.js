const { createProxyMiddleware } = require("http-proxy-middleware");

const createProxy = createProxyMiddleware({
  //target: process.env.REACT_APP_PROXY_API || "https://uat.digit.org",
  // target: process.env.REACT_APP_PROXY_API || "https://qa.digit.org",
  target: process.env.REACT_APP_PROXY_API || "https://qa.digit.org",
  changeOrigin: true,
});
const assetsProxy = createProxyMiddleware({
  target: process.env.REACT_APP_PROXY_ASSETS || "https://qa.digit.org",
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
    "/pg-service"
    
  ].forEach((location) => app.use(location, createProxy));
  ["/pb-egov-assets"].forEach((location) => app.use(location, assetsProxy));
};