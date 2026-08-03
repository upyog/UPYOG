import React, { useState, useEffect } from "react";
import { useLocation, Routes, Route } from "react-router-dom";
import { PrivateRoute, BreadCrumb } from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import Inbox from "./Inbox";
import CommonRedirect from "../../pageComponents/CommonRedirect"
// import NewApplication from "./NewApplication";
// import Search from "./Search";
// import Response from "../Response";
import ApplicationDetails from "./ApplicationDetails";
//import ReNewApplication from "./ReNewApplication";

const TLBreadCrumb = ({ location }) => {
  const { t } = useTranslation();
  const isSearch = location?.pathname?.includes("search");
  const isInbox = location?.pathname?.includes("inbox");
  const isApplicationSearch = location?.pathname?.includes("search/application");
  const isLicenceSearch = location?.pathname?.includes("search/license");
  const isEditApplication = location?.pathname?.includes("edit-application-details");
  const isRenewalApplication = location?.pathname?.includes("renew-application-details");
  const isApplicationDetails = location?.pathname?.includes("tl/application-details");
  const isNewApplication = location?.pathname?.includes("tl/new-application");
  const isResponse = location?.pathname?.includes("tl/response");
  const isMobile = window.Digit.Utils.browser.isMobile();
  const [search, setSearch] = useState(false);

  const locationsForTLEmployee = window.location.href;
  const breadCrumbUrl = sessionStorage.getItem("breadCrumbUrl") || "";

  if (locationsForTLEmployee.includes("inbox")) {
    sessionStorage.setItem("breadCrumbUrl", "inbox");
  } else if (locationsForTLEmployee.includes("home")) {
    sessionStorage.setItem("breadCrumbUrl", "home");
  } else if (locationsForTLEmployee.includes("search/license")) {
    if (breadCrumbUrl == "home") sessionStorage.setItem("breadCrumbUrl", "home/license");
    else if (breadCrumbUrl == "inbox") sessionStorage.setItem("breadCrumbUrl", "inbox/license");
    else sessionStorage.setItem("breadCrumbUrl", breadCrumbUrl.includes("home/license") ? "home/license" : "inbox/license")
  } else if (locationsForTLEmployee.includes("search/application")) {
    if (breadCrumbUrl == "home") sessionStorage.setItem("breadCrumbUrl", "home/search");
    else if (breadCrumbUrl == "inbox") sessionStorage.setItem("breadCrumbUrl", "inbox/search");
    else sessionStorage.setItem("breadCrumbUrl", breadCrumbUrl.includes("home/search") ? "home/search" : "inbox/search")
  } else if (locationsForTLEmployee.includes("new-application")) {
    if (breadCrumbUrl == "home") sessionStorage.setItem("breadCrumbUrl", "home/newApp");
    else if (breadCrumbUrl == "inbox") sessionStorage.setItem("breadCrumbUrl", "inbox/newApp");
  } else if (locationsForTLEmployee.includes("application-details")) {
    if (breadCrumbUrl == "home/license") sessionStorage.setItem("breadCrumbUrl", "home/license/appDetails");
    else if (breadCrumbUrl == "inbox/license") sessionStorage.setItem("breadCrumbUrl", "inbox/license/appDetails");
    else if (breadCrumbUrl == "home/search") sessionStorage.setItem("breadCrumbUrl", "home/search/appDetails");
    else if (breadCrumbUrl == "inbox/search") sessionStorage.setItem("breadCrumbUrl", "inbox/search/appDetails");
    else if (breadCrumbUrl == "inbox") sessionStorage.setItem("breadCrumbUrl", "inbox/appDetails");
  } else if (locationsForTLEmployee.includes("renew-application-details")) {
    if (breadCrumbUrl == "inbox/appDetails") sessionStorage.setItem("breadCrumbUrl", "inbox/appDetails/renew");
    else if (breadCrumbUrl == "home/license/appDetails") sessionStorage.setItem("breadCrumbUrl", "home/license/appDetails/renew");
    else if (breadCrumbUrl == "inbox/license/appDetails") sessionStorage.setItem("breadCrumbUrl", "inbox/license/appDetails/renew");
    else if (breadCrumbUrl == "home/search/appDetails") sessionStorage.setItem("breadCrumbUrl", "home/search/appDetails/renew");
    else if (breadCrumbUrl == "inbox/search/appDetails") sessionStorage.setItem("breadCrumbUrl", "inbox/search/appDetails/renew");
  } else if (locationsForTLEmployee.includes("edit-application-details")) {
    if (breadCrumbUrl == "inbox/appDetails") sessionStorage.setItem("breadCrumbUrl", "inbox/appDetails/renew");
    else if (breadCrumbUrl == "home/license/appDetails") sessionStorage.setItem("breadCrumbUrl", "home/license/appDetails/edit");
    else if (breadCrumbUrl == "inbox/license/appDetails") sessionStorage.setItem("breadCrumbUrl", "inbox/license/appDetails/edit");
    else if (breadCrumbUrl == "home/search/appDetails") sessionStorage.setItem("breadCrumbUrl", "home/search/appDetails/edit");
    else if (breadCrumbUrl == "inbox/search/appDetails") sessionStorage.setItem("breadCrumbUrl", "inbox/search/appDetails/edit");
  } else if (locationsForTLEmployee.includes("response")) {
    sessionStorage.setItem("breadCrumbUrl", "")
  }

  useEffect(() => {
    if (!search) {
      setSearch(isSearch);
    } else if (isInbox && search) {
      setSearch(false);
    }
  }, [location]);

  const breadCrumbUrls = sessionStorage.getItem("breadCrumbUrl") || "";

  const crumbs = [
    {
      path: "/upyog-ui/employee",
      content: t("ES_COMMON_HOME"),
      show: true
    },
    {
      path: "/upyog-ui/employee/tl/inbox",
      content: t("ES_TITLE_INBOX"),
      show: breadCrumbUrls.includes("inbox") || isInbox
    },
    {
      path: "/upyog-ui/employee/tl/search/application",
      content: t("ES_COMMON_SEARCH_APPLICATION"),
      show: isApplicationSearch ||
      breadCrumbUrls.includes("home/search") || 
      breadCrumbUrls.includes("inbox/search")
    },
    {
      path: "/upyog-ui/employee/tl/search/license",
      content: t("TL_SEARCH_TRADE_HEADER"),
      show: isLicenceSearch || 
      breadCrumbUrls.includes("home/license") || 
      breadCrumbUrls.includes("inbox/license")
    },
    {
      path: sessionStorage.getItem("applicationNumber") ? `/upyog-ui/employee/tl/application-details/${sessionStorage.getItem("applicationNumber")}` : "",
      content: t("TL_DETAILS_HEADER_LABEL"),
      show: isApplicationDetails ||
      breadCrumbUrls.includes("inbox/appDetails") || 
      breadCrumbUrls.includes("home/license/appDetails") || 
      breadCrumbUrls.includes("inbox/license/appDetails") || 
      breadCrumbUrls.includes("home/search/appDetails") || 
      breadCrumbUrls.includes("inbox/search/appDetails")
    },
    {
      path: "/upyog-ui/employee/tl/new-application",
      content: t("TL_HOME_SEARCH_RESULTS_NEW_APP_BUTTON"),
      show: isNewApplication || 
      breadCrumbUrls.includes("home/newApp") || 
      breadCrumbUrls.includes("inbox/newApp")
    },
    {
      content: t("ES_TITLE_RENEW_TRADE_LICESE_APPLICATION"),
      show: isRenewalApplication  ||
      breadCrumbUrls.includes("inbox/appDetails/renew") || 
      breadCrumbUrls.includes("home/license/appDetails/renew") || 
      breadCrumbUrls.includes("inbox/license/appDetails/renew") || 
      breadCrumbUrls.includes("home/search/appDetails/renew") || 
      breadCrumbUrls.includes("inbox/search/appDetails/renew")
    },
    {
      content: t("ES_TITLE_RE_NEW_TRADE_LICESE_APPLICATION"),
      show: isEditApplication || 
      breadCrumbUrls.includes("inbox/appDetails/edit") || 
      breadCrumbUrls.includes("home/license/appDetails/edit") || 
      breadCrumbUrls.includes("inbox/license/appDetails/edit") || 
      breadCrumbUrls.includes("home/search/appDetails/edit") || 
      breadCrumbUrls.includes("inbox/search/appDetails/edit")
    },
    {
      path: "/upyog-ui/employee/tl/inbox",
      content: t("ACTION_TEST_RESPONSE"),
      show: isResponse
    }
  ];

  return <BreadCrumb style={isMobile?{display:"flex"}:{}}  spanStyle={{maxWidth:"min-content"}} crumbs={crumbs} />;
};


const EmployeeApp = ({ path, url, userType }) => {
  const { t } = useTranslation();
  const location = useLocation();
  const mobileView = innerWidth <= 640;

  const locationCheck = window.location.href.includes("employee/tl/new-application") || window.location.href.includes("employee/tl/response") || window.location.href.includes("employee/tl/edit-application-details") || window.location.href.includes("employee/tl/renew-application-details");

  const NewApplication = Digit?.ComponentRegistryService?.getComponent('TLNewApplication');
  const ReNewApplication = Digit?.ComponentRegistryService?.getComponent('TLReNewApplication');
  const Response = Digit?.ComponentRegistryService?.getComponent('TLResponse');
  const Search = Digit?.ComponentRegistryService?.getComponent('TLSearch');
  const EnhancedReport = Digit?.ComponentRegistryService?.getComponent("EnhancedReport");
  

  return (
    <React.Fragment>
      <div className="ground-container" style={locationCheck ? { width: "100%", marginLeft: "0px" } : { marginLeft: "0px" }}>
        <div style={locationCheck ? { marginLeft: "15px" } : {}}>
          <TLBreadCrumb location={location} />
        </div>
        <Routes>
          <Route
            path="*"
            element={
              <PrivateRoute>
                <Inbox parentRoute={path} businessService="TL" filterComponent="TL_INBOX_FILTER" initialStates={{}} isInbox={true} />
              </PrivateRoute>
            }
          />
          <Route path={`/common/:filestore`} element={<PrivateRoute><CommonRedirect parentUrl={url} /></PrivateRoute>} />
          <Route path={`new-application`} element={<PrivateRoute><NewApplication parentUrl={url} /></PrivateRoute>} />
          <Route path={`/application-details/:id`} element={<PrivateRoute><ApplicationDetails parentRoute={path} /></PrivateRoute>} />
          <Route path={`renew-application-details/:id`} element={<PrivateRoute><ReNewApplication parentRoute={path} /></PrivateRoute>} />
          <Route path={`/edit-application-details/:id`} element={<PrivateRoute><ReNewApplication header={t("TL_ACTION_RESUBMIT")} parentRoute={path} /></PrivateRoute>} />
          <Route path={`/response`} element={<PrivateRoute><Response parentRoute={path} /></PrivateRoute>} />
          <Route path={`/search/:variant`} element={<PrivateRoute><Search parentRoute={path} /></PrivateRoute>} />
          <Route path={`/TLRegistryReport`} element={<PrivateRoute><EnhancedReport parentRoute={path} moduleName="rainmaker-tl" reportName="TLRegistryReport" /></PrivateRoute>} />
          <Route path={`/StateTradeLicenseCancelledRegistryReport`} element={<PrivateRoute><EnhancedReport parentRoute={path} moduleName="rainmaker-tl" reportName="StateTradeLicenseCancelledRegistryReport" /></PrivateRoute>} />
          <Route path={`/TradeLicenseCancelledRegistryReport`} element={<PrivateRoute><EnhancedReport parentRoute={path} moduleName="rainmaker-tl" reportName="TradeLicenseCancelledRegistryReport" /></PrivateRoute>} />
          <Route path={`/TradeWiseCollectionReport`} element={<PrivateRoute><EnhancedReport parentRoute={path} moduleName="rainmaker-tl" reportName="TradeWiseCollectionReport" /></PrivateRoute>} />
          <Route path={`/TradeLicenseApplicationStatusReport`} element={<PrivateRoute><EnhancedReport parentRoute={path} moduleName="rainmaker-tl" reportName="TradeLicenseApplicationStatusReport" /></PrivateRoute>} />
          <Route path={`/TradeLicenseULBWiseApplicationStatusReport`} element={<PrivateRoute><EnhancedReport parentRoute={path} moduleName="rainmaker-tl" reportName="TradeLicenseULBWiseApplicationStatusReport" /></PrivateRoute>} />
          <Route path={`/StateLevelStatus`} element={<PrivateRoute><EnhancedReport parentRoute={path} moduleName="rainmaker-tl" reportName="StateLevelStatus" /></PrivateRoute>} />
          <Route path={`/StateLevelTradeWiseCollectionReport`} element={<PrivateRoute><EnhancedReport parentRoute={path} moduleName="rainmaker-tl" reportName="StateLevelTradeWiseCollectionReport" /></PrivateRoute>} />
          <Route path={`/StateLevelTradeLicenseRegistryReport`} element={<PrivateRoute><EnhancedReport parentRoute={path} moduleName="rainmaker-tl" reportName="StateLevelTradeLicenseRegistryReport" /></PrivateRoute>} />
          <Route path={`/TradeLicenseDailyCollectionReport`} element={<PrivateRoute><EnhancedReport parentRoute={path} moduleName="rainmaker-tl" reportName="TradeLicenseDailyCollectionReport" /></PrivateRoute>} />
          <Route path={`/TLApplicationStatusReport`} element={<PrivateRoute><EnhancedReport parentRoute={path} moduleName="rainmaker-tl" reportName="TLApplicationStatusReport" /></PrivateRoute>} />
          <Route path={`/TLRenewalPendingReport`} element={<PrivateRoute><EnhancedReport parentRoute={path} moduleName="rainmaker-tl" reportName="TLRenewalPendingReport" /></PrivateRoute>} />
          <Route path={`/TradeLicenseDefaulterReport`} element={<PrivateRoute><EnhancedReport parentRoute={path} moduleName="rainmaker-tl" reportName="TradeLicenseDefaulterReport" /></PrivateRoute>} />
        </Routes>
      </div>
    </React.Fragment>
  );
};

export default EmployeeApp;
