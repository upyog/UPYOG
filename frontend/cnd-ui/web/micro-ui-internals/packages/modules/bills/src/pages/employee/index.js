/**
 * index.js (Employee Routing)
 *
 * Purpose:
 * Router entry point for all employee bill modules.
 *
 * Responsibilities:
 * - Implements breadcrumb rendering for navigation indicators.
 * - Configures React Router routes for standard bill workflows (Search, Group Bills, Cancel Bills, Bill Details).
 * - Coordinates MDMS configuration fetches for BillingService genie setup.
 */

import { PrivateRoute,BreadCrumb } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { Link, useLocation, Routes, Route } from "react-router-dom";
import DownloadBillInbox from "./DownloadBill";

import GroupBillInbox from "./GroupBill/index";

import ResponseCancelBill from "./ResponseCancelBill";
import BillDetailsv1 from "./BillDetailsv1";
import CancelBill from "./CancelBill";
import GroupBill from "./GroupBill";
import DownloadBill from "./DownloadBill";
import SearchApp from "./SearchApp";
const BILLSBreadCrumbs = ({ location }) => {
  const { t } = useTranslation();

  const search = useLocation().search;
  
  const fromScreen = new URLSearchParams(search).get("from") || null;

  const crumbs = [
    {
      path: "/upyog-ui/employee",
      content: t("ES_COMMON_HOME"),
      show: true,
    },
    {
      path: "/upyog-ui/employee/bills/cancel-bill",
      content: t("ABG_CANCEL_BILL"),
      show: location.pathname.includes("/cancel-bill") ? true : false,
    },
    {
      path: "/upyog-ui/employee/bills/bill-details",
      content: fromScreen ? `${t(fromScreen)} / ${t("ABG_BILL_DETAILS_HEADER")}` : t("ABG_BILL_DETAILS_HEADER"),
      show: location.pathname.includes("/bill-details") ? true : false,
      isBack: fromScreen && true,
    },
    {
      path: "/upyog-ui/employee/bills/group-bill",
      content: t("ABG_COMMON_HEADER"),
      show: location.pathname.includes("/group-bill") ? true : false,
    },
    {
      path: "/upyog-ui/employee/bills/inbox",
      content: t("ABG_SEARCH_BILL_COMMON_HEADER"),
      show: location.pathname.includes("/inbox") ? true : false,
    },
    {
      path: "/upyog-ui/employee/bills/download-bill-pdf",
      content: t("ABG_VIEW_DOWNLOADS_HEADER"),
      show: location.pathname.includes("/download-bill-pdf") ? true : false,
    }
    
  ];

  return <BreadCrumb crumbs={crumbs} spanStyle={{ maxWidth: "min-content" }} />;
};


const EmployeeApp = ({ path, url, userType }) => {
  const { t } = useTranslation();
  const location = useLocation();
  const mobileView = innerWidth <= 640;
  const tenantId = Digit.ULBService.getCurrentTenantId();

  const inboxInitialState = {
    searchParams: {},
  };

  const { isLoading, data: generateServiceType } = Digit.Hooks.useCommonMDMS(tenantId, "BillingService", "BillsGenieKey");

  const filterServiceType = generateServiceType?.BillingService?.BusinessService?.filter((element) => element.billGineiURL);

  let businessServiceList = [];

  filterServiceType?.forEach((element) => {
    businessServiceList.push(element.code);
  });
  const BillInbox = Digit.ComponentRegistryService.getComponent("BillInbox");

  return (
    <React.Fragment>
      <div className="ground-container">
        <p className="breadcrumb" style={{ marginLeft: mobileView ? "2vw" : "revert" }}>
          <BILLSBreadCrumbs location={location} />
        </p>
        <Routes>
          <Route
            path={`/inbox`}
            element={
              <PrivateRoute>
                <SearchApp parentRoute={path} filterComponent="BILLS_INBOX_FILTER" initialStates={inboxInitialState} isInbox={true} />
              </PrivateRoute>
            }
          />
          <Route
            path={`/group-bill`}
            element={
              <PrivateRoute>
                <GroupBill parentRoute={path} filterComponent="BILLS_INBOX_FILTER" initialStates={inboxInitialState} isInbox={true} />
              </PrivateRoute>
            }
          />
          <Route
            path={`/group-billold`}
            element={
              <PrivateRoute>
                <GroupBillInbox
                  parentRoute={path}
                  filterComponent="BILLS_GROUP_FILTER"
                  initialStates={{}}
                  isInbox={true}
                  keys={generateServiceType?.["common-masters"]?.uiCommonPay}
                />
              </PrivateRoute>
            }
          />
          <Route
            path={`/download-bill-pdf`}
            element={
              <PrivateRoute>
                <DownloadBillInbox
                  parentRoute={path}
                  filterComponent="BILLS_GROUP_FILTER"
                  initialStates={{}}
                  isInbox={true}
                  keys={generateServiceType?.["common-masters"]?.uiCommonPay}
                />
              </PrivateRoute>
            }
          />
          <Route
            path={`/cancel-bill`}
            element={
              <PrivateRoute>
                <CancelBill parentRoute={path} filterComponent="BILLS_INBOX_FILTER" initialStates={inboxInitialState} isInbox={true} />
              </PrivateRoute>
            }
          />
          <Route path={`/response-cancelBill`} element={<PrivateRoute><ResponseCancelBill parentRoute={path} /></PrivateRoute>} />
          <Route path={`/bill-details`} element={<PrivateRoute><BillDetailsv1 parentRoute={path} /></PrivateRoute>} />
        </Routes>
      </div>
    </React.Fragment>
  );
};

export default EmployeeApp;
