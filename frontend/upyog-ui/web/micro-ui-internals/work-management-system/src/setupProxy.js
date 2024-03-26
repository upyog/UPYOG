const { createProxyMiddleware } = require("http-proxy-middleware");

const createProxy = createProxyMiddleware({
  // target: process.env.REACT_APP_PROXY_API || "https://uat.digit.org",
  // target: process.env.REACT_APP_PROXY_API,
  // target: process.env.REACT_APP_PROXY_API || "https://qa.digit.org",
  //target:"https://test.wontract.com",//https://upyog-sandbox.niua.org" ,
  // target:"https://test.wontract.com" ,
  target:"https://upyog-sandbox.niua.org" ,
  changeOrigin: true,  
  secure: false,
  https: true 
});
const assetsProxy = createProxyMiddleware({
  target: process.env.REACT_APP_PROXY_ASSETS || "https://upyog-sandbox.niua.org",
  changeOrigin: true,
});

const MapiProxy = createProxyMiddleware({
  target: "http://10.216.36.162:8484",
  changeOrigin: true,
});
const apiProxy = createProxyMiddleware({
  target: "http://localhost:8484",
  changeOrigin: true,
});

// const localProxy = createProxyMiddleware({
//   target: "http://localhost:5000",
//   changeOrigin: true,
// });
const CMProxy = createProxyMiddleware({
  // target: "http://10.216.36.67:8484",
  target: "http://10.216.36.162:8484",  
  changeOrigin: true,
});
module.exports = function (app) {
  [
    "/access/v1/actions/mdms",
    "/egov-mdms-service",
    "/egov-location",
    "/localization",
    "/egov-workflow-v2",
    "/pgr-services",
    "/filestore",
    "/user-otp",
    "/user",
    "/fsm",
    "/billing-service",
    "/collection-services",
    "/pdf-service",
    "/pg-service",
    "/vehicle",
    "/vendor",
    "/property-services",
    "/fsm-calculator/v1/billingSlab/_search",
    "/pt-calculator-v2",
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
    "/inbox/v1/_search",
    "/tl-services",
    "/tl-calculator",
    "/edcr",
    "/bpa-services",
    "/noc-services",
    "/egov-user-event",
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
    "/service-request",
    "/wms/work-management-service/v1/sor/_create",
  ].forEach((location) => app.use(location, createProxy));

  ["/pb-egov-assets"].forEach((location) => app.use(location, assetsProxy));

  ["/wms/work-management-service/v1/sor/_create",
  "/wms/work-management-service/v1/sor/_search",  
  "/wms/work-management-service/v1/sor/_count",
  "/wms/work-management-service/v1/sor/_update",
  "/wms/work-management-service/v1/sch/_create",
  "/wms/work-management-service/v1/sch/_search",  
  "/wms/work-management-service/v1/sch/_count",
  "/wms/work-management-service/v1/sch/_update",
  ] .forEach((location) => app.use(location, apiProxy));

  [
    "/wms/work-management-service/v1/sor/_create",
  "/wms/work-management-service/v1/sor/_search",  
  "/wms/work-management-service/v1/sor/_count",
  "/wms/work-management-service/v1/sor/_update",
  "/wms/work-management-service/v1/sch/_create",
  "/wms/work-management-service/v1/sch/_search",  
  "/wms/work-management-service/v1/sch/_count",
  "/wms/work-management-service/v1/sch/_update",
  "/wms/wms-services/v1/pfmilestone/_create",
  "/wms/wms-services/v1/pfmilestone/_search",  
  "/wms/wms-services/v1/pfmilestone/_view",
  "/wms/wms-services/v1/pfmilestone/_update",

  "/wms/wms-services/v1/pma/_create",
  "/wms/wms-services/v1/pma/_search",  
  "/wms/wms-services/v1/pma/_view",
  "/wms/wms-services/v1/pma/_update",

  "/wms/wms-services/v1/pregister/_create",
  "/wms/wms-services/v1/pregister/_search",  
  "/wms/wms-services/v1/pregister/_view",
  "/wms/wms-services/v1/pregister/_update",

  "/wms/wms-services/v1/wsr/_create",
  "/wms/wms-services/v1/wsr/_search",  
  "/wms/wms-services/v1/wsr/_view",
  "/wms/wms-services/v1/wsr/_update",
  ].forEach((location) => app.use(location, MapiProxy));
  

 [
  "/wms/wms-services/v1/contractor/_view",
  "/wms/wms-services/v1/contractor/_update",
  "/wms/wms-services/v1/bank/",
  "/wms/wms-services/v1/contractor/_search",
  "/wms/wms-services/v1/contractor/_create",
  "/wms/wms-services/v1/cstype",
  "/wms/wms-services/v1/vendor",
  "/wms/wms-services/v1/tenderentry",
  "/wms/wms-services/v1/func",
  "/wms/wms-services/v1/vendorc",
  "/wms/wms-services/v1/paccounth",
  "/wms/wms-services/v1/dept/",
  "/wms/wms-services/v1/tcategory/",
  "/wms/wms-services/v1/project/",
  "/wms/wms-services/v1/contractagreement/"
  ].forEach((location) => app.use(location, CMProxy));

  // ["CASteper"].forEach((location) => app.use(location, localProxy));

};