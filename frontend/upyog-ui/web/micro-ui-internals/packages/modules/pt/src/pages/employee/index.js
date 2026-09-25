import { PrivateRoute,BreadCrumb } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { Link, useLocation, Routes, Route } from "react-router-dom";
import { PTLinks } from "../../Module";
import Inbox from "./Inbox";
import PaymentDetails from "./PaymentDetails";
import Search from "./Search";
import SearchApp from "./SearchApp";
import UlbAssesment from "./UlbAssesment";
import "../../css/pt-inline.css";


const EmployeeApp = ({ path, url, userType }) => {
  const { t } = useTranslation();
  const location = useLocation();
  const mobileView = innerWidth <= 640;
  sessionStorage.removeItem("revalidateddone");
  const isMobile = window.Digit.Utils.browser.isMobile();

  const inboxInitialState = {
    searchParams: {
      uuid: { code: "ASSIGNED_TO_ALL", name: "ES_INBOX_ASSIGNED_TO_ALL" },
      services: ["PT.CREATE", "PT.MUTATION", "PT.UPDATE"],
      applicationStatus: [],
      locality: [],
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
      returnData = searchData.map((e) => ({ ...e, due_tax: obj[e.propertyId] || 0 }));
    } catch (er) {
      const err = er?.response?.data;
      if (["EG_BS_BILL_NO_DEMANDS_FOUND", "EMPTY_DEMANDS"].includes(err?.Errors?.[0].code)) {
        returnData = searchData.map((e) => ({ ...e, due_tax: 0 }));
      }
    }
    return _next(returnData);
  };

  const searchMW = [{ combineTaxDueInSearchData }];

  const breadcrumbObj = {
    [`${path}/inbox`]: "ES_TITLE_INBOX",
    [`${path}/new-application`]: "ES_TITLE_NEW_PROPERTY_APPLICATION",
    [`${path}/search`]: "PT_COMMON_SEARCH_PROPERTY_SUB_HEADER",
    [`${path}/application-search`]: "ES_COMMON_APPLICATION_SEARCH",
    [`${path}/ulb-assesment`]: "ES_COMMON_ULB_ASSESSMENT",
  };

  const getBreadCrumb = () => {
    if (breadcrumbObj[location.pathname]) return t(breadcrumbObj[location.pathname]);
    else if (location.pathname.includes(`${path}/application-details/`)) return t("PT_APPLICATION_TITLE");
    else if (location.pathname.includes(`${path}/property-details/`)) return t("PT_PROPERTY_INFORMATION");
    else if (location.pathname.includes(`${path}/payment-details/`)) return t("PT_PAYMENT_HISTORY");
    else if (location.pathname.includes(`${path}/assessment-details/`)) return t("PT_ASSESS_PROPERTY");
    else if (location.pathname.includes(`${path}/property-mutate-docs-required`)) return t("PT_REQIURED_DOC_TRANSFER_OWNERSHIP");
    else if (location.pathname.includes(`${path}/property-mutate/`)) return t("ES_TITLE_MUTATE_PROPERTY");
    else if (location.pathname.includes(`${path}/modify-application/`)) return t("PT_UPDATE_PROPERTY");
  };

  const PTBreadCrumbs = ({ location }) => {
    const { t } = useTranslation();
    const search = useLocation().search;
    const fromScreen = new URLSearchParams(search).get("from") || null;
    const { from : fromScreen2 } = Digit.Hooks.useQueryParams();
    const crumbs = [
      {
        path: "/upyog-ui/employee",
        content: t("ES_COMMON_HOME"),
        show: true,
      },
      {
        path: `${path}/inbox`,
        content: t("ES_TITLE_INBOX"),
        show: location.pathname.includes("pt/inbox") ? true : false,
      },
      {
        path: `${path}/search`,
        content: t("PT_COMMON_SEARCH_PROPERTY_SUB_HEADER"),
        show: location.pathname.includes("/pt/search") || location.pathname.includes("/pt/ptsearch") ? true : false,
      },
      {
        path: `${path}/property-mutate-docs-required`,
        content: t("PT_REQIURED_DOC_TRANSFER_OWNERSHIP"),
        show: location.pathname.includes("pt/property-mutate-docs-required") ? true : false,
      },
      {
        path: `${path}/property-mutate/`,
        content: t("ES_TITLE_MUTATE_PROPERTY"),
        show: location.pathname.includes("pt/property-mutate/") ? true : false,
      },
      {
        path: `${path}/modify-application/`,
        content: t("PT_UPDATE_PROPERTY"),
        show: location.pathname.includes("pt/modify-application") ? true : false,
      },
      {
        path: `${path}/application-search`,
        content: t("ES_COMMON_APPLICATION_SEARCH"),
        show: location.pathname.includes("/pt/application-search") || location.pathname.includes("/pt/applicationsearch/application-details/") ? true : false,
      },
      {
        path: `${path}/ulb-assesment`,
        content: t("ES_COMMON_ULB_ASSESSMENT"),
        show: location.pathname.includes("/pt/ulb-assesment") || location.pathname.includes("/pt/ulb-assesment") ? true : false,
      },
      {
        path: `${path}/ptsearch/property-details/${sessionStorage.getItem("propertyIdinPropertyDetail")}`,
        content: fromScreen || fromScreen2 ? `${t(fromScreen || fromScreen2)} / ${t("PT_PROPERTY_INFORMATION")}`:t("PT_PROPERTY_INFORMATION"),
        show:  location.pathname.includes("/pt/ptsearch/property-details/") || location.pathname.includes("/pt/ptsearch/payment-details/") || location.pathname.includes("/pt/ptsearch/assessment-details/")  ? true : false,
        isBack:fromScreen && true,
      },
      {
        path: `${path}/property-details/${sessionStorage.getItem("propertyIdinPropertyDetail")}?${fromScreen2?`from=${fromScreen2}` : ''}`,
        content: fromScreen || fromScreen2 ? `${t(fromScreen || fromScreen2)} / ${t("PT_PROPERTY_INFORMATION")}`:t("PT_PROPERTY_INFORMATION"),
        show: location.pathname.includes("/pt/property-details/") || location.pathname.includes("/pt/payment-details/") ? true : false,
        isBack:true,
      },
      {
        path: `${path}/applicationsearch/application-details/${sessionStorage.getItem("applicationNoinAppDetails")}`,
        content: t("PT_APPLICATION_TITLE"),
        show: location.pathname.includes("/pt/application-details/") || location.pathname.includes("/pt/applicationsearch/application-details/") ? true : false,
      },
      {
        path: `${path}/payment-details/`,
        content: fromScreen ? `${t(fromScreen)} / ${t("PT_PAYMENT_HISTORY")
} `: t("PT_PAYMENT_HISTORY"),
        show: location.pathname.includes("/pt/ptsearch/payment-details") || location.pathname.includes("/pt/payment-details") ? true : false,
        isBack:fromScreen && true,
      },
      {
        path: `${path}/assessment-details/`,
        content: t("PT_ASSESS_PROPERTY"),
        show: location.pathname.includes("pt/ptsearch/assessment-details") ? true : false,
      },
    ];
  
    return <BreadCrumb className={`${isMobile ? "pt-employee-breadcrumb-mobile" : ""}`} spanStyle={{maxWidth:"min-content"}} crumbs={crumbs} />;
  }

  const NewApplication = Digit?.ComponentRegistryService?.getComponent("PTNewApplication");
  const ApplicationDetails = Digit?.ComponentRegistryService?.getComponent("ApplicationDetails");
  const PropertyDetails = Digit?.ComponentRegistryService?.getComponent("PTPropertyDetails");
  const AssessmentDetails = Digit?.ComponentRegistryService?.getComponent("PTAssessmentDetails");
  const EditApplication = Digit?.ComponentRegistryService?.getComponent("PTEditApplication");
  const Response = Digit?.ComponentRegistryService?.getComponent("PTResponse");
  const TransferOwnership = Digit?.ComponentRegistryService?.getComponent("PTTransferOwnership");
  const DocsRequired = Digit?.ComponentRegistryService?.getComponent("PTDocsRequired");
  const isRes = window.location.href.includes("pt/response");
  const isLocation = window.location.href.includes("pt") || window.location.href.includes("application");
  const EnhancedReport = Digit?.ComponentRegistryService?.getComponent("EnhancedReport");
  const isNewRegistration = window.location.href.includes("new-application") || window.location.href.includes("modify-application") || window.location.href.includes("pt/application-details");
  return (
    <React.Fragment>
      <div className="ground-container">
        {!isRes ? (
          <div style={isNewRegistration ? { marginLeft: "12px" } : { marginLeft: "-4px" }}>
            <PTBreadCrumbs location={location} />
          </div>
        ) : null}
        <Routes>
          <Route path={`*`} element={<PrivateRoute><PTLinks matchPath={path} userType={userType} /></PrivateRoute>} />
          <Route
            path={`inbox`}
            element={
              <PrivateRoute>
                <Inbox
                  useNewInboxAPI={true}
                  parentRoute={path}
                  businessService="PT"
                  filterComponent="PT_INBOX_FILTER"
                  initialStates={inboxInitialState}
                  isInbox={true}
                />
              </PrivateRoute>
            }
          />
          <Route path={`new-application`} element={<PrivateRoute><NewApplication parentUrl={url} /></PrivateRoute>} />
          <Route path={`application-details/:id`} element={<PrivateRoute><ApplicationDetails parentRoute={path} /></PrivateRoute>} />
          <Route path={`property-details/:id`} element={<PrivateRoute><PropertyDetails parentRoute={path} /></PrivateRoute>} />
          <Route path={`applicationsearch/application-details/:id`} element={<PrivateRoute><ApplicationDetails parentRoute={path} /></PrivateRoute>} />
          <Route path={`ptsearch/property-details/:id`} element={<PrivateRoute><PropertyDetails parentRoute={path} /></PrivateRoute>} />
          <Route path={`payment-details/:id`} element={<PrivateRoute><PaymentDetails parentRoute={path} /></PrivateRoute>} />
          <Route path={`ptsearch/payment-details/:id`} element={<PrivateRoute><PaymentDetails parentRoute={path} /></PrivateRoute>} />
          <Route path={`assessment-details/:id`} element={<PrivateRoute><AssessmentDetails parentRoute={path} /></PrivateRoute>} />
          <Route path={`ptsearch/assessment-details/:id`} element={<PrivateRoute><AssessmentDetails parentRoute={path} /></PrivateRoute>} />
          <Route path={`modify-application/:id`} element={<PrivateRoute><EditApplication /></PrivateRoute>} />
          <Route path={`response`} element={<PrivateRoute><Response parentRoute={path} /></PrivateRoute>} />
          <Route path={`property-mutate/:id`} element={<PrivateRoute><TransferOwnership parentRoute={path} /></PrivateRoute>} />
          <Route path={`property-mutate-docs-required/:id`} element={<PrivateRoute><DocsRequired parentRoute={path} /></PrivateRoute>} />
          <Route path={`search`} element={<PrivateRoute><Search t={t} parentRoute={path} /></PrivateRoute>} />
          <Route
            path={`searchold`}
            element={
              <PrivateRoute>
                <Inbox
                  parentRoute={path}
                  businessService="PT"
                  middlewareSearch={searchMW}
                  initialStates={inboxInitialState}
                  isInbox={false}
                  EmptyResultInboxComp={"PTEmptyResultInbox"}
                />
              </PrivateRoute>
            }
          />
          <Route path={`application-search`} element={<PrivateRoute><SearchApp parentRoute={path} /></PrivateRoute>} />
          <Route path={`ulb-assesment`} element={<PrivateRoute><UlbAssesment parentRoute={path} /></PrivateRoute>} />
          <Route path={`PTReceiptRegister`} element={<PrivateRoute><EnhancedReport parentRoute={path} moduleName="pt-reports" reportName="PTReceiptRegister" /></PrivateRoute>} />
          <Route path={`PTCollectionReport`} element={<PrivateRoute><EnhancedReport parentRoute={path} moduleName="pt-reports" reportName="PTCollectionReport" /></PrivateRoute>} />
          <Route path={`DefaulterReport`} element={<PrivateRoute><EnhancedReport parentRoute={path} moduleName="pt-reports" reportName="DefaulterReport" /></PrivateRoute>} />
          <Route path={`PTGrievances`} element={<PrivateRoute><EnhancedReport parentRoute={path} moduleName="pt-reports" reportName="PTGrievances" /></PrivateRoute>} />
          <Route path={`PTCoverageReport`} element={<PrivateRoute><EnhancedReport parentRoute={path} moduleName="pt-reports" reportName="PTCoverageReport" /></PrivateRoute>} />
          <Route path={`PTTop20TaxPayers`} element={<PrivateRoute><EnhancedReport parentRoute={path} moduleName="pt-reports" reportName="PTTop20TaxPayers" /></PrivateRoute>} />
        </Routes>
      </div>
    </React.Fragment>
  );
};

export default EmployeeApp;
