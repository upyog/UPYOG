import React from "react";
import { useTranslation } from "react-i18next";
import { Routes, Route, useLocation } from "react-router-dom";
import { PrivateRoute, BreadCrumb } from "@nudmcdgnpm/digit-ui-react-components";


import WSResponse from "./WSResponse";
import Response from "./Response";
import ResponseBillAmend from "./ResponseBillAmend";
import WSDisconnectionResponse from "./DisconnectionApplication/WSDisconnectionResponse";
import WSRestorationResponse from "./RestorationApplication/WSRestorationResponse";

const BILLSBreadCrumbs = ({ location }) => {
  const { t } = useTranslation();

  const search = useLocation().search;
  const fromScreen = new URLSearchParams(search).get("from") || null;
  const IsEdit = new URLSearchParams(search).get("isEdit") || null;
  const applicationNumbercheck = new URLSearchParams(search).get("applicationNumber") || null;
  let isMobile = window.Digit.Utils.browser.isMobile();
  let requestParam = window.location.href.split("?")[1];

  function findLastIndex(array, searchKey, searchValue) {
    var index = array.slice().reverse().findIndex(x => x[searchKey] === searchValue);
    var count = array.length - 1
    var finalIndex = index >= 0 ? count - index : index;
    return finalIndex;
  }

  let crumbs = [
    {
      path: "/upyog-ui/employee",
      content: t("ES_COMMON_HOME"),
      show: true,
      style: isMobile ? { width: "20%" } : {},
    },
    {
      path: "/upyog-ui/employee/ws/create-application",
      content: t("ES_COMMON_WS_DOCUMENTS_REQUIRED"),
      show: location.pathname.includes("/create-application") ? true : false,
    },
    {
      path: "/upyog-ui/employee/water/inbox",
      content: t("ES_COMMON_BILLS_WATER_INBOX_LABEL"),
      show: location.pathname.includes("/water/inbox") ? true : false,
    },
    {
      path: "/upyog-ui/employee/ws/water/bill-amendment/inbox",
      content: t("ES_COMMON_BILL_AMEND_WATER_INBOX_LABEL"),
      show: location.pathname.includes("/water/bill-amendment/inbox") ? true : false,
    },
    {
      path: "/upyog-ui/employee/ws/sewerage/bill-amendment/inbox",
      content: t("ES_COMMON_BILL_AMEND_SEWERAGE_INBOX_LABEL"),
      show: location.pathname.includes("/sewerage/bill-amendment/inbox") ? true : false,
    },
    {
      path: "/upyog-ui/employee/ws/water/search-application",
      content: fromScreen ? `${t(fromScreen)} / ${t("WS_SEARCH_APPLICATIONS")}` : t("WS_SEARCH_APPLICATIONS"),
      show: location.pathname.includes("/water/search-application") ? true : false,
      isBack: fromScreen && true,
    },
    {
      path: "/upyog-ui/employee/ws/water/search-connection",
      content: fromScreen ? `${t(fromScreen)} / ${t("WS_SEARCH_CONNECTION")}` : t("WS_SEARCH_CONNECTION"),
      show: location.pathname.includes("/water/search-connection") ? true : false,
      isBack: fromScreen && true,
    },
    {
      path: "/upyog-ui/employee/ws/water/wns-search",
      content: fromScreen ? `${t(fromScreen)} / ${t("WS_SEARCH_INTEGRATED_BILL")}` : t("WS_SEARCH_INTEGRATED_BILL"),
      show: location.pathname.includes("/water/wns-search") ? true : false,
      isBack: fromScreen && true,
    },
    {
      path: "/upyog-ui/employee/ws/sewerage/search-application",
      content: fromScreen ? `${t(fromScreen)} / ${t("WS_SEARCH_APPLICATIONS")}` : t("WS_SEARCH_APPLICATIONS"),
      show: location.pathname.includes("/sewerage/search-application") ? true : false,
      isBack: fromScreen && true,
    },
    {
      path: "/upyog-ui/employee/ws/sewerage/search-connection",
      content: fromScreen ? `${t(fromScreen)} / ${t("WS_SEARCH_CONNECTION")}` : t("WS_SEARCH_CONNECTION"),
      show: location.pathname.includes("/sewerage/search-connection") ? true : false,
      isBack: fromScreen && true,
    },
    {
      path: "/upyog-ui/employee/sewerage/inbox",
      content: t("ES_COMMON_BILLS_SEWERAGE_INBOX_LABEL"),
      show: location.pathname.includes("/sewerage/inbox") ? true : false,
    },
    {
      path: "/upyog-ui/employee/ws/new-application",
      content: t("ES_COMMON_WS_NEW_CONNECTION"),
      show: location.pathname.includes("/new-application") ? true : false,
    },
    {
      path: `${location?.pathname}${location.search}`,
      content: t("ACTION_TEST_RESPONSE"),
      show: location.pathname.includes("/ws-response") ? true : false,
    },
    {
      path: "/upyog-ui/employee/ws/consumption-details",
      content: fromScreen ? `${t(fromScreen)} / ${t("WS_VIEW_CONSUMPTION_DETAIL")}` : t("WS_VIEW_CONSUMPTION_DETAIL"),
      show: location.pathname.includes("/consumption-details") ? true : false,
      isBack: fromScreen && true,
    },
    // {
    //   path: sessionStorage.getItem("redirectedfromEDIT") === "true"? (applicationNumbercheck?.includes("SW_AP")?  "/upyog-ui/employee/ws/sewerage/search-application" : "/upyog-ui/employee/ws/water/search-application") : "/upyog-ui/employee/ws/application-details",
    //   content: fromScreen ? `${t(fromScreen)} / ${t("WS_APPLICATION_DETAILS_HEADER")}` : t("WS_APPLICATION_DETAILS_HEADER"),
    //   show: location.pathname.includes("/generate-note") ? true : false,
    //   isBack: sessionStorage.getItem("redirectedfromEDIT") !== "true" && fromScreen && true,
    // },
    {
      path: sessionStorage.getItem("redirectedfromEDIT") === "true" ? (applicationNumbercheck?.includes("SW_AP") ? "/upyog-ui/employee/ws/sewerage/search-application" : "/upyog-ui/employee/ws/water/search-application") : "/upyog-ui/employee/ws/application-details",
      content: fromScreen ? `${t(fromScreen)} / ${t("WS_APPLICATION_DETAILS_HEADER")}` : t("WS_APPLICATION_DETAILS_HEADER"),
      show: location.pathname.includes("/application-details") ? true : false,
      isBack: sessionStorage.getItem("redirectedfromEDIT") !== "true" && fromScreen && true,
    },
    {
      path: sessionStorage.getItem("redirectedfromEDIT") === "true" ? (applicationNumbercheck?.includes("SW_AP") ? "/upyog-ui/employee/ws/sewerage/search-application" : "/upyog-ui/employee/ws/water/search-application") : "/upyog-ui/employee/ws/modify-details",
      content: fromScreen ? `${t(fromScreen)} / ${t("WS_APPLICATION_DETAILS_HEADER")}` : t("WS_APPLICATION_DETAILS_HEADER"),
      show: location.pathname.includes("/modify-details") ? true : false,
      isBack: sessionStorage.getItem("redirectedfromEDIT") !== "true" && fromScreen && true,
    },
    {
      path: "/upyog-ui/employee/ws/disconnection-details",
      content: fromScreen ? `${t(fromScreen)} / ${t("WS_APPLICATION_DETAILS_HEADER")}` : t("WS_APPLICATION_DETAILS_HEADER"),
      show: location.pathname.includes("/disconnection-details") ? true : false,
      isBack: fromScreen && true,
    },
    {
      path: "/upyog-ui/employee/ws/connection-details",
      content: fromScreen ? `${t(fromScreen)} / ${t("WS_COMMON_CONNECTION_DETAIL")}` : t("WS_COMMON_CONNECTION_DETAIL"),
      show: location.pathname.includes("/connection-details") ? true : false,
      isBack: fromScreen && true,
    },
    {
      path: "/upyog-ui/employee/ws/edit-application",
      content: `${t("WS_APPLICATION_DETAILS_HEADER")} / ${t("WS_APP_FOR_WATER_AND_SEWERAGE_EDIT_LABEL")}`,
      show: location.pathname.includes("/edit-application") ? true : false,
      isBack: true,
    },
    {
      path: `${location?.pathname}${location.search}`,
      content: `${t("WS_APPLICATION_DETAILS_HEADER")} / ${t("WF_EMPLOYEE_NEWSW1_ACTIVATE_CONNECTION")}`,
      show: location.pathname.includes("/activate-connection") ? true : false,
      isBack: true,
    },
    {
      path: `${location?.pathname}${location.search}`,
      content: `${t("WS_APPLICATION_DETAILS_HEADER")} / ${t("WS_WATER_SEWERAGE_DISCONNECTION_EDIT_LABEL")}`,
      show: location.pathname.includes("edit-disconnection-application") ? true : false,
      isBack: true,
    },
    {
      path: `${location?.pathname}${location.search}`,
      content: `${t("WS_APPLICATION_DETAILS_HEADER")} / ${t("WS_WATER_SEWERAGE_DISCONNECTION_EDIT_LABEL")}`,
      show: location.pathname.includes("config-by-disconnection-application") ? true : false,
      isBack: true,
    },
    {
      path: `${location?.pathname}${location.search}`,
      content: `${t("WS_APPLICATION_DETAILS_HEADER")} / ${t("WS_WATER_SEWERAGE_DISCONNECTION_EDIT_LABEL")}`,
      show: location.pathname.includes("resubmit-disconnection-application") ? true : false,
      isBack: true,
    },
    {
      path: `/upyog-ui/employee/ws/new-disconnection/docsrequired`,
      content: t("WS_NEW_DISCONNECTION_DOCS_REQUIRED"),
      show: location.pathname.includes("/new-disconnection/docsrequired") ? true : false,
    },
    {
      path: `/upyog-ui/employee/ws/new-disconnection/application-form`,
      content: isMobile ? `${t("WS_NEW_DISCONNECTION_DOCS_REQUIRED")} / ${t("WS_NEW_DISCONNECTION_APPLICATION")}` : `${t("WS_NEW_DISCONNECTION_DOCS_REQUIRED")} / ${t("WS_NEW_DISCONNECTION_APPLICATION")}`,
      show: location.pathname.includes("/new-disconnection/application-form") ? true : false,
      isBack: true
    },
    {
      path: `${location?.pathname}${location.search}`,
      content: `${t("WS_NEW_DISCONNECTION_RESPONSE")}`,
      show: location.pathname.includes("/ws-disconnection-response") ? true : false,
      isBack: true
    },
    // {
    //   path: "/upyog-ui/employee/sewerage/bill-amendment/inbox",
    //   content: t("ES_COMMON_BILLS_SEWERAGE_INBOX_LABEL"),
    //   show: location.pathname.includes("/sewerage/bill-amendment/inbox") ? true : false,
    // },
    {
      path: `${location?.pathname}${location.search}`,
      content: fromScreen ? `${t(fromScreen)} / ${t("WS_MODIFY_CONNECTION_BUTTON")}` : t("WS_MODIFY_CONNECTION_BUTTON"),
      show: location.pathname.includes("ws/modify-application") ? true : false,
      isBack: true,
    },
    {
      path: "/upyog-ui/employee/ws/required-documents",
      content: t("ES_COMMON_WS_DOCUMENTS_REQUIRED"),
      show: location.pathname.includes("/required-documents") ? true : false,
    },
    {
      path: requestParam ? `/upyog-ui/employee/ws/bill-amendment?${requestParam}` : "/upyog-ui/employee/ws/bill-amendment",
      content: t("WS_BILL_AMEND_APP"),
      show: location.pathname.includes("ws/bill-amendment") && !IsEdit ? true : false,
    },
    {
      path: "/upyog-ui/employee/ws/bill-amendment",
      content: t("WS_BILL_AMEND_EDIT_APP"),
      show: location.pathname.includes("ws/bill-amendment") && IsEdit ? true : false,
    },
    {
      path: "/upyog-ui/employee/ws/response",
      content: t("WS_ACK_SCREEN"),
      show: location.pathname.includes("/employee/ws/response") ? true : false,
      isclickable: false,
    },
    {
      path: "/upyog-ui/employee/ws/generate-note-bill-amendment",
      content: t("CS_TITLE_GENERATE_NOTE"),
      show: location.pathname.includes("/generate-note-bill-amendment") ? true : false,
      //isclickable : false,
    },
    {
      path: "/upyog-ui/employee/ws/water/bulk-bil",
      content: t("CS_TITLE_BULK_BILL"),
      show: location.pathname.includes("/ws/water/bulk-bill") ? true : false,
      //isclickable : false,
    }
  ];

  let lastCrumbIndex = findLastIndex(crumbs, "show", true)
  crumbs[lastCrumbIndex] = { ...crumbs[lastCrumbIndex], isclickable: false }

  return <div style={window?.location.href.includes("/employee/ws/bill-amendment") || window?.location.href.includes("/employee/ws/response") ? { marginLeft: "20px" } : {}}><BreadCrumb crumbs={crumbs} spanStyle={{ maxWidth: "min-content" }} /></div>;
};
const App = ({ path }) => {
  const location = useLocation();

  const WSDocsRequired = Digit?.ComponentRegistryService?.getComponent("WSDocsRequired");
  const WSInbox = Digit?.ComponentRegistryService?.getComponent("WSInbox");
  const WSDisconnectionDocsRequired = Digit?.ComponentRegistryService?.getComponent('WSDisconnectionDocsRequired');
  const WSApplicationBillAmendment = Digit?.ComponentRegistryService?.getComponent("WSApplicationBillAmendment");
  const WSRequiredDocuments = Digit?.ComponentRegistryService?.getComponent("WSRequiredDocuments");
  const WSNewApplication = Digit?.ComponentRegistryService?.getComponent("WSNewApplication");
  const WSApplicationDetails = Digit?.ComponentRegistryService?.getComponent("WSApplicationDetails");
  const WSGetConnectionDetails = Digit?.ComponentRegistryService?.getComponent("WSGetConnectionDetails");
  const WSActivateConnection = Digit?.ComponentRegistryService?.getComponent("WSActivateConnection");
  const WSApplicationDetailsBillAmendment = Digit?.ComponentRegistryService?.getComponent("WSApplicationDetailsBillAmendment");
  const WSSearch = Digit?.ComponentRegistryService?.getComponent("WSSearch");
  const WSSearchWater = Digit?.ComponentRegistryService?.getComponent("WSSearchWater");
  const WSEditApplication = Digit?.ComponentRegistryService?.getComponent("WSEditApplication");
  const WSConsumptionDetails = Digit?.ComponentRegistryService?.getComponent("WSConsumptionDetails");
  const WSModifyApplication = Digit?.ComponentRegistryService?.getComponent("WSModifyApplication");
  const WSEditModifyApplication = Digit?.ComponentRegistryService?.getComponent("WSEditModifyApplication");
  const WSDisconnectionApplication = Digit?.ComponentRegistryService?.getComponent("WSDisconnectionApplication");
  const WSRestorationApplication = Digit?.ComponentRegistryService?.getComponent("WSRestorationApplication");
  const WSEditApplicationByConfig = Digit?.ComponentRegistryService?.getComponent("WSEditApplicationByConfig");
  const WSBillIAmendMentInbox = Digit?.ComponentRegistryService?.getComponent("WSBillIAmendMentInbox");
  const WSGetDisconnectionDetails = Digit?.ComponentRegistryService?.getComponent("WSGetDisconnectionDetails");
  const WSModifyApplicationDetails = Digit?.ComponentRegistryService?.getComponent("WSModifyApplicationDetails");
  const WSEditDisconnectionApplication = Digit?.ComponentRegistryService?.getComponent("WSEditDisconnectionApplication");
  const WSEditDisconnectionByConfig = Digit?.ComponentRegistryService?.getComponent("WSEditDisconnectionByConfig");
  const WSResubmitDisconnection = Digit?.ComponentRegistryService?.getComponent("WSResubmitDisconnection");
  const WSSearchIntegrated = Digit?.ComponentRegistryService?.getComponent("WSSearchIntegrated");
  const WSBulkBillGeneration = Digit?.ComponentRegistryService?.getComponent("WSBulkBillGeneration");
  const EnhancedReport = Digit?.ComponentRegistryService?.getComponent("EnhancedReport");

  const locationCheck =
    window.location.href.includes("/employee/ws/new-application") ||
    window.location.href.includes("/employee/ws/modify-application") ||
    window.location.href.includes("/employee/ws/edit-application") ||
    window.location.href.includes("/employee/ws/activate-connection") ||
    window.location.href.includes("/employee/ws/application-details") ||
    window.location.href.includes("/employee/ws/modify-details") ||
    window.location.href.includes("/employee/ws/ws-response") ||
    window.location.href.includes("/employee/ws/new-disconnection/application-form") ||
    window.location.href.includes("/employee/ws/ws-disconnection-response") ||
    window.location.href.includes("/employee/ws/consumption-details") ||
    window.location.href.includes("/employee/ws/edit-disconnection-application") ||
    window.location.href.includes("/employee/ws/config-by-disconnection-application") ||
    window.location.href.includes("/employee/ws/resubmit-disconnection-application") ||
    window.location.href.includes("/employee/ws/water/bulk-bill");



  const locationCheckReqDocs = window.location.href.includes("/employee/ws/create-application") || window.location.href.includes("/employee/ws/new-disconnection/docsrequired");

  return (
    <div className="ground-container">
      <div style={locationCheck ? { marginLeft: "12px" } : (locationCheckReqDocs ? { marginLeft: "25px" } : { marginLeft: "-4px" })}>
        <BILLSBreadCrumbs location={location} />
      </div>
      <Routes>
        <Route path={`/create-application`} element={<PrivateRoute><WSDocsRequired /></PrivateRoute>} />
        <Route path={`/new-application`} element={<PrivateRoute><WSNewApplication /></PrivateRoute>} />
        <Route path={`/edit-application`} element={<PrivateRoute><WSEditApplication /></PrivateRoute>} />
        <Route path={`/edit-disconnection-application`} element={<PrivateRoute><WSEditDisconnectionApplication /></PrivateRoute>} />
        <Route path={`/resubmit-disconnection-application`} element={<PrivateRoute><WSResubmitDisconnection /></PrivateRoute>} />
        <Route path={`/config-by-disconnection-application`} element={<PrivateRoute><WSEditDisconnectionByConfig /></PrivateRoute>} />
        <Route path={`/application-details`} element={<PrivateRoute><WSApplicationDetails /></PrivateRoute>} />
        <Route path={`/modify-details`} element={<PrivateRoute><WSModifyApplicationDetails /></PrivateRoute>} />
        <Route path={`/connection-details`} element={<PrivateRoute><WSGetConnectionDetails /></PrivateRoute>} />
        <Route path={`/bill-amendment`} element={<PrivateRoute><WSApplicationBillAmendment {...{ path }} /></PrivateRoute>} />
        <Route path={`/generate-note-bill-amendment`} element={<PrivateRoute><WSApplicationDetailsBillAmendment {...{ path }} /></PrivateRoute>} />
        <Route path={`/response`} element={<PrivateRoute><Response {...{ path }} /></PrivateRoute>} />
        <Route path={`/response-bill-amend`} element={<PrivateRoute><ResponseBillAmend {...{ path }} /></PrivateRoute>} />
        <Route path={`/required-documents`} element={<PrivateRoute><WSRequiredDocuments {...{ path }} /></PrivateRoute>} />
        <Route path={`/activate-connection`} element={<PrivateRoute><WSActivateConnection /></PrivateRoute>} />
        <Route path={`/water/search-application`} element={<PrivateRoute><WSSearch parentRoute={path} /></PrivateRoute>} />
        <Route path={`/sewerage/search-application`} element={<PrivateRoute><WSSearch parentRoute={path} /></PrivateRoute>} />
        <Route path={`/ws-response`} element={<PrivateRoute><WSResponse /></PrivateRoute>} />
        <Route path={`/ws-disconnection-response`} element={<PrivateRoute><WSDisconnectionResponse /></PrivateRoute>} />
        <Route path={`/ws-restoration-response`} element={<PrivateRoute><WSRestorationResponse /></PrivateRoute>} />
        <Route path={`/water/search-connection`} element={<PrivateRoute><WSSearchWater parentRoute={path} /></PrivateRoute>} />
        <Route path={`/sewerage/search-connection`} element={<PrivateRoute><WSSearchWater parentRoute={path} /></PrivateRoute>} />
        <Route path={`/water/search-demand`} element={<PrivateRoute><WSSearchWater parentRoute={path} /></PrivateRoute>} />
        <Route path={`/sewerage/search-demand`} element={<PrivateRoute><WSSearchWater parentRoute={path} /></PrivateRoute>} />
        <Route path={`/consumption-details`} element={<PrivateRoute><WSConsumptionDetails /></PrivateRoute>} />
        <Route path={`/modify-application`} element={<PrivateRoute><WSModifyApplication /></PrivateRoute>} />
        <Route path={`/modify-application-edit`} element={<PrivateRoute><WSEditModifyApplication /></PrivateRoute>} />
        <Route path={`/disconnection-application`} element={<PrivateRoute><WSDisconnectionDocsRequired /></PrivateRoute>} />
        <Route path={`/new-disconnection/*`} element={<PrivateRoute><WSDisconnectionApplication /></PrivateRoute>} />
        <Route path={`/new-restoration/*`} element={<PrivateRoute><WSRestorationApplication /></PrivateRoute>} />
        <Route path={`/bill-amend/inbox`} element={<PrivateRoute><WSBillIAmendMentInbox parentRoute={path} /></PrivateRoute>} />
        <Route path={`/water/inbox`} element={<PrivateRoute><WSInbox parentRoute={path} /></PrivateRoute>} />
        <Route path={`/sewerage/inbox`} element={<PrivateRoute><WSInbox parentRoute={path} /></PrivateRoute>} />
        <Route path={`/edit-application-by-config`} element={<PrivateRoute><WSEditApplicationByConfig /></PrivateRoute>} />
        <Route path={`/disconnection-details`} element={<PrivateRoute><WSGetDisconnectionDetails /></PrivateRoute>} />
        <Route path={`/water/bill-amendment/inbox`} element={<PrivateRoute><WSBillIAmendMentInbox parentRoute={path} /></PrivateRoute>} />
        <Route path={`/sewerage/bill-amendment/inbox`} element={<PrivateRoute><WSBillIAmendMentInbox parentRoute={path} /></PrivateRoute>} />
        <Route path={`/water/wns-search`} element={<PrivateRoute><WSSearchIntegrated parentRoute={path} /></PrivateRoute>} />
        <Route path={`/water/bulk-bill`} element={<PrivateRoute><WSBulkBillGeneration parentRoute={path} /></PrivateRoute>} />
        <Route path={`/ReceiptRegisterSW`} element={<PrivateRoute><EnhancedReport parentRoute={path} moduleName="rainmaker-ws" reportName="ReceiptRegisterSW" /></PrivateRoute>} />
        <Route path={`/ReceiptRegisterSW_OLD`} element={<PrivateRoute><EnhancedReport parentRoute={path} moduleName="rainmaker-ws" reportName="ReceiptRegisterSW_OLD" /></PrivateRoute>} />
        <Route path={`/ReceiptRegisterReportSW`} element={<PrivateRoute><EnhancedReport parentRoute={path} moduleName="rainmaker-ws" reportName="ReceiptRegisterReportSW" /></PrivateRoute>} />
        <Route path={`/BaseRegisterReportWS`} element={<PrivateRoute><EnhancedReport parentRoute={path} moduleName="rainmaker-ws" reportName="BaseRegisterReportWS" /></PrivateRoute>} />
        <Route path={`/FieldCollectionSearchReportWS`} element={<PrivateRoute><EnhancedReport parentRoute={path} moduleName="rainmaker-ws" reportName="FieldCollectionSearchReportWS" /></PrivateRoute>} />
        <Route path={`/FieldCollectionSearchReceiptWS`} element={<PrivateRoute><EnhancedReport parentRoute={path} moduleName="rainmaker-ws" reportName="FieldCollectionSearchReceiptWS" /></PrivateRoute>} />
        <Route path={`/ChequeCollectionReportWS`} element={<PrivateRoute><EnhancedReport parentRoute={path} moduleName="rainmaker-ws" reportName="ChequeCollectionReportWS" /></PrivateRoute>} />
        <Route path={`/SearchOnlineReceiptsWS`} element={<PrivateRoute><EnhancedReport parentRoute={path} moduleName="rainmaker-ws" reportName="SearchOnlineReceiptsWS" /></PrivateRoute>} />
        <Route path={`/ReceiptCollections`} element={<PrivateRoute><EnhancedReport parentRoute={path} moduleName="rainmaker-ws" reportName="ReceiptCollections" /></PrivateRoute>} />
        <Route path={`/JE_PendencyReport`} element={<PrivateRoute><EnhancedReport parentRoute={path} moduleName="rainmaker-ws" reportName="JE_PendencyReport" /></PrivateRoute>} />
        <Route path={`/CashCollectionReport`} element={<PrivateRoute><EnhancedReport parentRoute={path} moduleName="rainmaker-ws" reportName="CashCollectionReport" /></PrivateRoute>} />
        <Route path={`/CollectionsSummaryHeadwiseReport`} element={<PrivateRoute><EnhancedReport parentRoute={path} moduleName="rainmaker-ws" reportName="CollectionsSummaryHeadwiseReport" /></PrivateRoute>} />
        <Route path={`/ArrearReport`} element={<PrivateRoute><EnhancedReport parentRoute={path} moduleName="rainmaker-ws" reportName="ArrearReport" /></PrivateRoute>} />
        <Route path={`/SearchWaterChargeReport`} element={<PrivateRoute><EnhancedReport parentRoute={path} moduleName="rainmaker-ws" reportName="SearchWaterChargeReport" /></PrivateRoute>} />
        <Route path={`/SearchSewerageChargeReport`} element={<PrivateRoute><EnhancedReport parentRoute={path} moduleName="rainmaker-ws" reportName="SearchSewerageChargeReport" /></PrivateRoute>} />
        <Route path={`/WSApplicationStatusReport`} element={<PrivateRoute><EnhancedReport parentRoute={path} moduleName="rainmaker-ws" reportName="WSApplicationStatusReport" /></PrivateRoute>} />
        <Route path={`/WaterDisconnectionReport`} element={<PrivateRoute><EnhancedReport parentRoute={path} moduleName="rainmaker-ws" reportName="WaterDisconnectionReport" /></PrivateRoute>} />
        <Route path={`/WaterConnectionReport`} element={<PrivateRoute><EnhancedReport parent Route={path} moduleName="rainmaker-ws" reportName="WaterConnectionReport" /></PrivateRoute>} />
        <Route path={`/SewerageConnectionReport`} element={<PrivateRoute><EnhancedReport parent Route={path} moduleName="rainmaker-ws" reportName="SewerageConnectionReport" /></PrivateRoute>} />
        <Route path={`/SewerageDisconnection Report`} element={< PrivateRoute>< EnhancedReport parent Route={path} moduleName="rainmaker-ws" reportName="SewerageDisconnectionReport" /></PrivateRoute>} />
        <Route path={`/DemandCollectionBalancedRegister`} element={<PrivateRoute><EnhancedReport parentRoute={path} moduleName="rainmaker-ws" reportName="DemandCollectionBalancedRegister" /></PrivateRoute>} />

        {/* <Route path={`/search`} component={SearchConnectionComponent} />
          <Route path={`/search-results`} component={SearchResultsComponent} /> */}
      </Routes>
    </div>
  );
};

export default App;