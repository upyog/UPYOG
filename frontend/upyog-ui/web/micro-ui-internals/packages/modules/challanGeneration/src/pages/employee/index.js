import React from "react";
import { ChallanGenerationModule } from "../../Module";
import Inbox from "./Inbox";
import { Routes, Route, Link } from "react-router-dom";
import { PrivateRoute } from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";

const EmployeeApp = ({ path, url, userType }) => {
  const { t } = useTranslation();
  const mobileView = innerWidth <= 640;

  const inboxInitialState = {
    searchParams: {
      status: [],
      businessService: [],
    },
  };

  const combineTaxDueInSearchData = async (searchData, _break, _next) => {
    let returnData;
    const tenantId = Digit.ULBService.getCurrentTenantId();
    let businessService = ["PT"].join();
    let consumerCode = searchData.map((e) => e.propertyId).join();
    try {
      const res = await Digit.PaymentService.fetchBill(tenantId, { consumerCode, businessService });
      let obj = {};
      res.Bill.forEach((e) => {
        obj[e.consumerCode] = e.totalAmount;
      });
      returnData = searchData.map((e) => ({ ...e, due_tax: "₹ " + (obj[e.propertyId] || 0) }));
    } catch (er) {
      const err = er?.response?.data;
      if (["EG_BS_BILL_NO_DEMANDS_FOUND", "EMPTY_DEMANDS"].includes(err?.Errors?.[0].code)) {
        returnData = searchData.map((e) => ({ ...e, due_tax: "₹ " + 0 }));
      }
    }
    return _next(returnData);
  };

  const searchMW = [{ combineTaxDueInSearchData }];

  const EmployeeChallan = Digit?.ComponentRegistryService?.getComponent("MCollectEmployeeChallan");
  const ChallanAcknowledgement = Digit?.ComponentRegistryService?.getComponent("ChallanAcknowledgement");
  const SearchReceiptPage = Digit?.ComponentRegistryService?.getComponent("SearchReceipt");
  const SearchChallanPage = Digit?.ComponentRegistryService?.getComponent("SearchChallan");
  const ChallanSearch = Digit?.ComponentRegistryService?.getComponent("ChallanStepperForm");
  const ChallanResponseCitizen = Digit?.ComponentRegistryService?.getComponent("ChallanResponseCitizen");
  const ChallanApplicationDetails = Digit?.ComponentRegistryService?.getComponent("ChallanApplicationDetails");

  return (
    <React.Fragment>
      <div className="ground-container">
        <p className={`breadcrumb employee-main-application-details ${mobileView ? 'cg-breadcrumb-mobile-margin' : ''}`}>
          <Link to="/upyog-ui/employee" className="challan-link-href">{t("ES_COMMON_HOME")}</Link>{" "}
          / <span>{t("CHALLAN_MODULE")}</span>
        </p>
        <Routes>
          <Route path="/*" element={<PrivateRoute><ChallanGenerationModule userType={userType} /></PrivateRoute>} />
          <Route path="inbox/*" element={<PrivateRoute><Inbox parentRoute={path} businessService="PT" filterComponent="MCOLLECT_INBOX_FILTER" initialStates={inboxInitialState} isInbox={true} /></PrivateRoute>} />
          <Route path="search/*" element={<PrivateRoute><Inbox parentRoute={path} businessService="PT" middlewareSearch={searchMW} initialStates={inboxInitialState} isInbox={false} /></PrivateRoute>} />
          <Route path="acknowledgement/*" element={<PrivateRoute><ChallanAcknowledgement /></PrivateRoute>} />
          <Route path="challansearch/:challanno/*" element={<PrivateRoute><EmployeeChallan /></PrivateRoute>} />
          <Route path="search-receipt/*" element={<PrivateRoute><SearchReceiptPage /></PrivateRoute>} />
          <Route path="search-challan/*" element={<PrivateRoute><SearchChallanPage parentRoute={path} /></PrivateRoute>} />
          <Route path="generate-challan/*" element={<PrivateRoute><ChallanSearch /></PrivateRoute>} />
          <Route path="response/:id" element={<PrivateRoute><ChallanResponseCitizen /></PrivateRoute>} />
          <Route path="application/:acknowledgementIds/:tenantId" element={<PrivateRoute><ChallanApplicationDetails /></PrivateRoute>} />
        </Routes>
      </div>
    </React.Fragment>
  );
};

export default EmployeeApp;