const Urls = {
  MDMS: `https://upyog-sandbox.niua.org/egov-mdms-service/v1/_search`,
  WorkFlow: `https://upyog-sandbox.niua.org/egov-workflow-v2/egov-wf/businessservice/_search`,
  WorkFlowProcessSearch: `https://upyog-sandbox.niua.org/egov-workflow-v2/egov-wf/process/_search`,
  localization: `https://upyog-sandbox.niua.org/localization/messages/v1/_search`,
  location: {
    localities: `https://upyog-sandbox.niua.org/egov-location/location/v11/boundarys/_search?hierarchyTypeCode=ADMIN&boundaryType=Locality`,
    revenue_localities: `https://upyog-sandbox.niua.org/egov-location/location/v11/boundarys/_search?hierarchyTypeCode=REVENUE&boundaryType=Locality`,
  },

  pgr_search: `https://upyog-sandbox.niua.org/pgr-services/v2/request/_search`,
  pgr_update: `https://upyog-sandbox.niua.org/pgr-services/v2/request/_update`,
  filter_data: `https://run.mocky.io/v3/597a50a0-90e5-4a45-b82e-8a2186b760bd`,
  FileStore: "https://upyog-sandbox.niua.org/filestore/v1/files",

  FileFetch: "https://upyog-sandbox.niua.org/filestore/v1/files/url",
  PGR_Create: `https://upyog-sandbox.niua.org/pgr-services/v2/request/_create`,
  pgr_count: `https://upyog-sandbox.niua.org/pgr-services/v2/request/_count`,

  OTP_Send: "http://localhost:8089/user-otp/v1/_send",
  Authenticate: "https://upyog-sandbox.niua.org/user/oauth/token",
  RegisterUser: "https://upyog-sandbox.niua.org/user/citizen/_create",
  ChangePassword: "https://upyog-sandbox.niua.org/user/password/nologin/_update",
  ChangePassword1: "https://upyog-sandbox.niua.org/user/password/_update",
  UserProfileUpdate: "https://upyog-sandbox.niua.org/user/profile/_update",
  EmployeeSearch: "https://upyog-sandbox.niua.org/egov-hrms/employees/_search",

  InboxSearch: "https://upyog-sandbox.niua.org/inbox/v1/_search",

  UserSearch: "https://upyog-sandbox.niua.org/user/_search",
  UserLogout: "https://upyog-sandbox.niua.org/user/_logout",

  Shortener: "https://upyog-sandbox.niua.org/egov-url-shortening/shortener",

  fsm: {
    search: "https://upyog-sandbox.niua.org/fsm/v1/_search",
    create: "https://upyog-sandbox.niua.org/fsm/v1/_create",
    update: "https://upyog-sandbox.niua.org/fsm/v1/_update",
    vendorSearch: "https://upyog-sandbox.niua.org/vendor/v1/_search",
    vehicleSearch: "https://upyog-sandbox.niua.org/vehicle/v1/_search",
    audit: "https://upyog-sandbox.niua.org/fsm/v1/_audit",
    vehicleTripSearch: "https://upyog-sandbox.niua.org/vehicle/trip/v1/_search",
    billingSlabSearch: "https://upyog-sandbox.niua.org/fsm-calculator/v1/billingSlab/_search",
    vehilceUpdate: "https://upyog-sandbox.niua.org/vehicle/trip/v1/_update",
    createVendor: "https://upyog-sandbox.niua.org/vendor/v1/_create",
    updateVendor: "https://upyog-sandbox.niua.org/vendor/v1/_update",
    createVehicle: "https://upyog-sandbox.niua.org/vehicle/v1/_create",
    updateVehicle: "https://upyog-sandbox.niua.org/vehicle/v1/_update",
    driverSearch: "https://upyog-sandbox.niua.org/vendor/driver/v1/_search",
    createDriver: "https://upyog-sandbox.niua.org/vendor/driver/v1/_create",
    updateDriver: "https://upyog-sandbox.niua.org/vendor/driver/v1/_update",
    vehicleTripCreate: "https://upyog-sandbox.niua.org/vehicle/trip/v1/_create",
    advanceBalanceCalculate: "https://upyog-sandbox.niua.org/fsm-calculator/v1/_advancebalancecalculate",
  },

  payment: {
    fetch_bill: "https://upyog-sandbox.niua.org/billing-service/bill/v2/_fetchbill",
    demandSearch: "https://upyog-sandbox.niua.org/billing-service/demand/_search",
    create_reciept: "https://upyog-sandbox.niua.org/collection-services/payments/_create",
    print_reciept: "https://upyog-sandbox.niua.org/collection-services/payments",
    generate_pdf: "https://upyog-sandbox.niua.org/pdf-service/v1/_create",
    create_citizen_reciept: "https://upyog-sandbox.niua.org/pg-service/transaction/v1/_create",
    update_citizen_reciept: "https://upyog-sandbox.niua.org/pg-service/transaction/v1/_update",
    search_bill: "https://upyog-sandbox.niua.org/billing-service/bill/v2/_search",
    reciept_search: "https://upyog-sandbox.niua.org/collection-services/payments/:buisnessService/_search",
    obps_Reciept_Search: "https://upyog-sandbox.niua.org/collection-services/payments/_search",
    billAmendmentSearch: "https://upyog-sandbox.niua.org/billing-service/amendment/_search",
    getBulkPdfRecordsDetails: "https://upyog-sandbox.niua.org/pdf-service/v1/_getBulkPdfRecordsDetails",
  },

  pt: {
    fectch_property: "https://upyog-sandbox.niua.org/property-services/property/_search",
    fetch_payment_details: "https://upyog-sandbox.niua.org/billing-service/bill/v2/_fetchbill",
    create: "https://upyog-sandbox.niua.org/property-services/property/_create",
    search: "https://upyog-sandbox.niua.org/property-services/property/_search",
    update: "https://upyog-sandbox.niua.org/property-services/property/_update",
    pt_calculation_estimate: "https://upyog-sandbox.niua.org/pt-calculator-v2/propertytax/v2/_estimate",
    assessment_create: "https://upyog-sandbox.niua.org/property-services/assessment/_create",
    assessment_search: "https://upyog-sandbox.niua.org/property-services/assessment/_search",
    payment_search: "https://upyog-sandbox.niua.org/collection-services/payments/PT/_search",
    pt_calculate_mutation: "https://upyog-sandbox.niua.org/pt-calculator-v2/propertytax/mutation/_calculate",
    cfcreate: "https://upyog-sandbox.niua.org/service-request/service/v1/_create",
    cfdefinitionsearch: "https://upyog-sandbox.niua.org/service-request/service/definition/v1/_search",
    cfsearch: "https://upyog-sandbox.niua.org/service-request/service/v1/_search",
  },

  dss: {
    dashboardConfig: "https://upyog-sandbox.niua.org/dashboard-analytics/dashboard/getDashboardConfig",
    getCharts: "https://upyog-sandbox.niua.org/dashboard-analytics/dashboard/getChartV2",
  },

  mcollect: {
    search: "https://upyog-sandbox.niua.org/echallan-services/eChallan/v1/_search",
    create: "https://upyog-sandbox.niua.org/echallan-services/eChallan/v1/_create?",
    fetch_bill: "https://upyog-sandbox.niua.org/billing-service/bill/v2/_fetchbill?",
    search_bill: "https://upyog-sandbox.niua.org/egov-searcher/bill-genie/mcollectbills/_get",
    search_bill_pt: "https://upyog-sandbox.niua.org/egov-searcher/bill-genie/billswithaddranduser/_get",
    update: "https://upyog-sandbox.niua.org/echallan-services/eChallan/v1/_update",
    download_pdf: "https://upyog-sandbox.niua.org/egov-pdf/download/UC/mcollect-challan",
    receipt_download: "https://upyog-sandbox.niua.org/egov-pdf/download/PAYMENT/consolidatedreceipt",
    bill_download: "https://upyog-sandbox.niua.org/egov-pdf/download/BILL/consolidatedbill",
    count: "https://upyog-sandbox.niua.org/echallan-services/eChallan/v1/_count",
  },
  hrms: {
    search: "https://upyog-sandbox.niua.org/egov-hrms/employees/_search",
    count: "https://upyog-sandbox.niua.org/egov-hrms/employees/_count",
    create: "https://upyog-sandbox.niua.org/egov-hrms/employees/_create",
    update: "https://upyog-sandbox.niua.org/egov-hrms/employees/_update",
  },
  tl: {
    create: "https://upyog-sandbox.niua.org/tl-services/v1/_create",
    search: "https://upyog-sandbox.niua.org/tl-services/v1/_search",
    fetch_payment_details: "https://upyog-sandbox.niua.org/billing-service/bill/v2/_fetchbill",
    download_pdf: "https://upyog-sandbox.niua.org/egov-pdf/download/TL/",
    update: "https://upyog-sandbox.niua.org/tl-services/v1/_update",
    billingslab: "https://upyog-sandbox.niua.org/tl-calculator/billingslab/_search",
  },
  receipts: {
    receipt_download: "https://upyog-sandbox.niua.org/egov-pdf/download/PAYMENT/consolidatedreceipt",
    payments: "https://upyog-sandbox.niua.org/collection-services/payments",
    count: "https://upyog-sandbox.niua.org/egov-hrms/employees/_count",
  },
  obps: {
    scrutinyDetails: "https://upyog-sandbox.niua.org/edcr/rest/dcr/scrutinydetails",
    comparisionReport: "https://upyog-sandbox.niua.org/edcr/rest/dcr/occomparison",
    create: "https://upyog-sandbox.niua.org/bpa-services/v1/bpa/_create",
    nocSearch: "https://upyog-sandbox.niua.org/noc-services/v1/noc/_search",
    updateNOC: "https://upyog-sandbox.niua.org/noc-services/v1/noc/_update",
    update: "https://upyog-sandbox.niua.org/bpa-services/v1/bpa/_update",
    bpaSearch: "https://upyog-sandbox.niua.org/bpa-services/v1/bpa/_search",
    bpaRegSearch: "https://upyog-sandbox.niua.org/tl-services/v1/BPAREG/_search",
    bpaRegCreate: "https://upyog-sandbox.niua.org/tl-services/v1/BPAREG/_create",
    bpaRegGetBill: "https://upyog-sandbox.niua.org/tl-calculator/v1/BPAREG/_getbill",
    bpaRegUpdate: "https://upyog-sandbox.niua.org/tl-services/v1/BPAREG/_update",
    receipt_download: "https://upyog-sandbox.niua.org/egov-pdf/download/PAYMENT/consolidatedreceipt",
    edcrreportdownload: "https://upyog-sandbox.niua.org/bpa-services/v1/bpa/_permitorderedcr",
    getSearchDetails: "https://upyog-sandbox.niua.org/inbox/v1/dss/_search",
  },

  edcr: {
    create: "https://upyog-sandbox.niua.org/edcr/rest/dcr/scrutinize",
  },

  events: {
    search: "https://upyog-sandbox.niua.org/egov-user-event/v1/events/_search",
    update: "https://upyog-sandbox.niua.org/egov-user-event/v1/events/lat/_update",
    updateEvent: "https://upyog-sandbox.niua.org/egov-user-event/v1/events/_update",
    updateEventCDG: "https://upyog-sandbox.niua.org/egov-user-event/v1/events/lat/_update",
    count: "https://upyog-sandbox.niua.org/egov-user-event/v1/events/notifications/_count",
    create: "https://upyog-sandbox.niua.org/egov-user-event/v1/events/_create",
  },

  ws: {
    water_create: "https://upyog-sandbox.niua.org/ws-services/wc/_create",
    sewarage_create: "https://upyog-sandbox.niua.org/sw-services/swc/_create",
    water_search: "https://upyog-sandbox.niua.org/ws-services/wc/_search",
    sewarage_search: "https://upyog-sandbox.niua.org/sw-services/swc/_search",
    water_update: "https://upyog-sandbox.niua.org/ws-services/wc/_update",
    sewarage_update: "https://upyog-sandbox.niua.org/sw-services/swc/_update",
    ws_calculation_estimate: "https://upyog-sandbox.niua.org/ws-calculator/waterCalculator/_estimate",
    sw_calculation_estimate: "https://upyog-sandbox.niua.org/sw-calculator/sewerageCalculator/_estimate",
    ws_connection_search: "https://upyog-sandbox.niua.org/ws-calculator/meterConnection/_search",
    sw_payment_search: "https://upyog-sandbox.niua.org/collection-services/payments/SW/_search",
    ws_payment_search: "https://upyog-sandbox.niua.org/collection-services/payments/WS/_search",
    billAmendmentCreate: "https://upyog-sandbox.niua.org/billing-service/amendment/_create",
    billAmendmentUpdate: "https://upyog-sandbox.niua.org/billing-service/amendment/_update",
    ws_meter_conncetion_create: "https://upyog-sandbox.niua.org/ws-calculator/meterConnection/_create",
    sw_meter_conncetion_create: "https://upyog-sandbox.niua.org/sw-calculator/meterConnection/_create",
    wns_group_bill: "https://upyog-sandbox.niua.org/egov-pdf/download/WNS/wnsgroupbill",
    cancel_group_bill: "https://upyog-sandbox.niua.org/pdf-service/v1/_cancelProcess",
    wns_generate_pdf: "https://upyog-sandbox.niua.org/egov-pdf/download/WNS/wnsbill",
    water_applyAdhocTax: "https://upyog-sandbox.niua.org/ws-calculator/waterCalculator/_applyAdhocTax",
    sewerage_applyAdhocTax: "https://upyog-sandbox.niua.org/sw-calculator/sewerageCalculator/_applyAdhocTax",
    getSearchDetails: "https://upyog-sandbox.niua.org/inbox/v1/dss/_search",
    disconnection_notice: "https://upyog-sandbox.niua.org/pdf-service/v1/_createnosave",
  },

  engagement: {
    document: {
      search: "https://upyog-sandbox.niua.org/egov-document-uploader/egov-du/document/_search",
      create: "https://upyog-sandbox.niua.org/egov-document-uploader/egov-du/document/_create",
      delete: "https://upyog-sandbox.niua.org/egov-document-uploader/egov-du/document/_delete",
      update: "https://upyog-sandbox.niua.org/egov-document-uploader/egov-du/document/_update",
    },
    surveys: {
      create: "https://upyog-sandbox.niua.org/egov-survey-services/egov-ss/survey/_create",
      update: "https://upyog-sandbox.niua.org/egov-survey-services/egov-ss/survey/_update",
      search: "https://upyog-sandbox.niua.org/egov-survey-services/egov-ss/survey/_search",
      delete: "https://upyog-sandbox.niua.org/egov-survey-services/egov-ss/survey/_delete",
      submitResponse: "https://upyog-sandbox.niua.org/egov-survey-services/egov-ss/survey/response/_submit",
      showResults: "https://upyog-sandbox.niua.org/egov-survey-services/egov-ss/survey/response/_results",
    },
  },

  noc: {
    nocSearch: "https://upyog-sandbox.niua.org/noc-services/v1/noc/_search",
  },
  reports: {
    reportSearch: "https://upyog-sandbox.niua.org/report/",
  },
  bills: {
    cancelBill: "https://upyog-sandbox.niua.org/billing-service/bill/v2/_cancelbill",
  },
  wms:{
    SchemeMaster:{create:"",search:"",update:"",count:""},
    SORApplications:{create: "https://64c200dcfa35860baea10c66.mockapi.io/wms/work-management-service/v1/SORApplications",
    update:"https://64c200dcfa35860baea10c66.mockapi.io/wms/work-management-service/v1/SORApplications:id",
    get:"https://64c200dcfa35860baea10c66.mockapi.io/wms/work-management-service/v1/SORApplications",
    search:"https://64c200dcfa35860baea10c66.mockapi.io/wms/work-management-service/SORApplications",
    delete:"https://64c200dcfa35860baea10c66.mockapi.io/wms/work-management-service/v1/SORApplications:id"},
    ProjectMaster:{create:"",search:"",update:"",count:""},
    Work:{create:"",search:"",update:"",count:""},
  },
  
wms_old:{
  Scheme_Master:{
    create: "https://64c200dcfa35860baea10c66.mockapi.io/wms/work-management-service/v1/Scheme_Master",
    update:"https://64c200dcfa35860baea10c66.mockapi.io/wms/work-management-service/v1/Scheme_Master:id",
    get:"https://64c200dcfa35860baea10c66.mockapi.io/wms/work-management-service/v1/Scheme_Master:id",
    search:"https://64c200dcfa35860baea10c66.mockapi.io/wms/work-management-service/v1/Scheme_Master",
    delete:"https://64c200dcfa35860baea10c66.mockapi.io/wms/work-management-service/v1/Scheme_Master:id"
  },
  Project_Master:{
    create: "https://64c200dcfa35860baea10c66.mockapi.io/wms/work-management-service/v1/Project_Master",
    update:"https://64c200dcfa35860baea10c66.mockapi.io/wms/work-management-service/v1/Project_Master:id",
    get:"https://64c200dcfa35860baea10c66.mockapi.io/wms/work-management-service/v1/Project_Master:id",
    search:"https://64c200dcfa35860baea10c66.mockapi.io/wms/work-management-service/v1/Project_Master",
    delete:"https://64c200dcfa35860baea10c66.mockapi.io/wms/work-management-service/v1/Project_Master:id"
  },
  SORApplications:{
    create: "https://64c200dcfa35860baea10c66.mockapi.io/wms/work-management-service/v1/SORApplications",
    update:"https://64c200dcfa35860baea10c66.mockapi.io/wms/work-management-service/v1/SORApplications:id",
    get:"https://64c200dcfa35860baea10c66.mockapi.io/wms/work-management-service/v1/SORApplications",
    search:"https://64c200dcfa35860baea10c66.mockapi.io/wms/work-management-service/SORApplications",
    delete:"https://64c200dcfa35860baea10c66.mockapi.io/wms/work-management-service/v1/SORApplications:id"
  },

  },
  access_control: "https://upyog-sandbox.niua.org/access/v1/actions/mdms/_get",
  billgenie: "https://upyog-sandbox.niua.org/egov-searcher",
  audit: "https://upyog-sandbox.niua.org/inbox/v1/elastic/_search",
};

export default Urls;
