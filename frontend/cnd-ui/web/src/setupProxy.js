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
  );
};