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
const assetsProxyMdms = createProxyMiddleware({
  target: process.env.REACT_APP_PROXY_API_MDMS || "https://qa.digit.org",
  changeOrigin: true,
});
const assetsProxyLocalisation = createProxyMiddleware({
  target: process.env.REACT_APP_PROXY_API_LOCALISATION || "https://qa.digit.org",
  changeOrigin: true,
});
const assetsProxyPropertyService = createProxyMiddleware({
  target: process.env.REACT_APP_PROXY_API_PROPERTY_SERVICE || "https://qa.digit.org",
  changeOrigin: true,
});
const assetsProxyEgovLocation = createProxyMiddleware({
  target: process.env.REACT_APP_PROXY_API_LOCATION_SERVICE || "https://qa.digit.org",
  changeOrigin: true,
});
const assetsProxyBillingService = createProxyMiddleware({
  target: process.env.REACT_APP_PROXY_API_BILLING_SERVICE || "https://qa.digit.org",
  changeOrigin: true,
});
const assetsProxyUserService = createProxyMiddleware({
  target: process.env.REACT_APP_PROXY_API_USER_SERVICE || "https://qa.digit.org",
  changeOrigin: true,
});
const assetsProxyUserPortService = createProxyMiddleware({
  target: process.env.REACT_APP_PROXY_API_USER_OTP_SERVICE || "https://qa.digit.org",
  changeOrigin: true,
});
const assetsProxyPTCalculatorService = createProxyMiddleware({
  target: process.env.REACT_APP_PROXY_API_PT_CALCULATOR_SERVICE || "https://qa.digit.org",
  changeOrigin: true,
});
const assetsProxyWorkflowService = createProxyMiddleware({
  target: process.env.REACT_APP_PROXY_API_WORKFLOW_SERVICE || "https://qa.digit.org",
  changeOrigin: true,
});
const assetsAccessControlService = createProxyMiddleware({
  target: process.env.REACT_APP_PROXY_API_ACCESS_SERVICE || "https://qa.digit.org",
  changeOrigin: true,
});
const assetsUserEventService = createProxyMiddleware({
  target: process.env.REACT_APP_PROXY_API_USER_EVENT_SERVICE || "https://qa.digit.org",
  changeOrigin: true,
});
const assetsFileStoreService = createProxyMiddleware({
  target: process.env.REACT_APP_PROXY_API_FILE_STORE_SERVICE || "https://dev.digit.org",
  changeOrigin: true,
});
const assetseServiceRequest = createProxyMiddleware({
  target: process.env.REACT_APP_PROXY_API_SERVICE_REQUEST || "https://dev.digit.org",
  changeOrigin: true,
});
const assetseInboxSearch = createProxyMiddleware({
  target: process.env.REACT_APP_PROXY_API_INBOX_SEARCH || "https://dev.digit.org",
  changeOrigin: true,
});
const assetseHRMS = createProxyMiddleware({
  target: process.env.REACT_APP_PROXY_API_HRMS || "https://dev.digit.org",
  changeOrigin: true,
});
const collectionService = createProxyMiddleware({
  target: process.env.REACT_APP_PROXY_API_COLLECTION_SERVICE || "https://dev.digit.org",
  changeOrigin: true,
})
const pgService = createProxyMiddleware({
  target: process.env.REACT_APP_PROXY_API_PAYMENT_GATEWAY || "https://dev.digit.org",
  changeOrigin: true,
});


module.exports = function (app) {
  [
    // "/access/v1/actions/mdms",
    // "/egov-mdms-service",
    // "/egov-location",
    // "/localization",
    // "/egov-workflow-v2",
    "/pgr-services",
    // "/filestore",
    // "/egov-hrms",
    // "/user-otp",
    // "/user",
    "/fsm",
    // "/billing-service",
    // "/collection-services",
    "/pdf-service",
    // "/pg-service",
    "/vehicle",
    "/vendor",
    // "/property-services",
    "/fsm-calculator/v1/billingSlab/_search",
    // "/pt-calculator-v2",
    "/dashboard-analytics",
    "/echallan-services",
    "/egov-searcher/bill-genie/mcollectbills/_get",
    "/egov-searcher/bill-genie/billswithaddranduser/_get",
    "/egov-searcher/bill-genie/waterbills/_get",
    "/egov-searcher/bill-genie/seweragebills/_get",
    "/egov-pdf/download/UC/mcollect-challan",
    "/egov-hrms/employees/_count",
    "/tl-services/v1/_create",
    "/tl-services/v1/_search",
    "/egov-url-shortening/shortener",
    // "/inbox/v1/_search",
    "/tl-services",
    "/tl-calculator",
    "/edcr",
    "/bpa-services",
    "/noc-services",
    // "/egov-user-event",
    "/egov-document-uploader",
    "/egov-pdf",
    "/egov-survey-services",
    "/ws-services",
    "/sw-services",
    "/ws-calculator",
    "/sw-calculator/",
    "/egov-searcher",
    "/report",
    "/inbox/v1/dss/_search",
    "/inbox/v1/elastic/_search",
    "/fsm-calculator",
    // "/service-request",
  ].forEach((location) => app.use(location, createProxy));
  ["/egov-mdms-service"].forEach((location) => app.use(location, assetsProxyMdms));
  ["/localization"].forEach((location) => app.use(location, assetsProxyLocalisation));
  ["/property-services"].forEach((location) => app.use(location, assetsProxyPropertyService));
  ["/egov-location"].forEach((location) => app.use(location, assetsProxyEgovLocation));
  ["/billing-service"].forEach((location) => app.use(location, assetsProxyBillingService));
  ["/user"].forEach((location) => app.use(location, assetsProxyUserService));
  ["/user-otp"].forEach((location) => app.use(location, assetsProxyUserPortService));
  ["/pt-calculator-v2"].forEach((location) => app.use(location, assetsProxyPTCalculatorService));
  ["/egov-workflow-v2"].forEach((location) => app.use(location, assetsProxyWorkflowService));
  ["/access/v1/actions/mdms"].forEach((location)=> app.use(location, assetsAccessControlService));
  ["/egov-user-event"].forEach((location)=> app.use(location, assetsUserEventService));
  ["/filestore"].forEach((location)=> app.use(location, assetsFileStoreService));
  ["/service-request"].forEach((location)=> app.use(location, assetseServiceRequest));
  ["/inbox/v1/_search"].forEach((location)=> app.use(location, assetseInboxSearch));
  ["/egov-hrms"].forEach((location)=> app.use(location, assetseHRMS));
  ["/collection-services"].forEach((location)=> app.use(location, collectionService));
  ["/pg-service"].forEach((location)=> app.use(location, pgService));
  
  ["/pb-egov-assets"].forEach((location) => app.use(location, assetsProxy));
};