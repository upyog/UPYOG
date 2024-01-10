import { PrivateRoute,BreadCrumb } from "@egovernments/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { Link, Switch, useLocation } from "react-router-dom";
import { PTRLinks } from "../../Module";
import Inbox from "./Inbox";
import PaymentDetails from "./PaymentDetails";
import SearchApp from "./SearchApp";


const EmployeeApp = ({ path, url, userType }) => {
  const { t } = useTranslation();
  const location = useLocation();
  const mobileView = innerWidth <= 640;
  sessionStorage.removeItem("revalidateddone");
  const isMobile = window.Digit.Utils.browser.isMobile();

  const inboxInitialState = {
    searchParams: {
      uuid: { code: "ASSIGNED_TO_ALL", name: "ES_INBOX_ASSIGNED_TO_ALL" },
      services: ["ptr"],
      applicationStatus: [],
      locality: [],

    },
  };

  const combineTaxDueInSearchData = async (searchData, _break, _next) => {
    let returnData;
    const tenantId = Digit.ULBService.getCurrentTenantId();
    let businessService = ["ptr"].join();
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
    ["/digit-ui/employee/ptr/petservice/inbox"]: "ES_TITLE_INBOX",    
    ["/digit-ui/employee/ptr/petservice/new-application"]: "ES_TITLE_NEW_PET_APPLICATION",
    
  };

  const getBreadCrumb = () => {
    if (breadcrumbObj[location.pathname]) return t(breadcrumbObj[location.pathname]);
    else if (location.pathname.includes("/digit-ui/employee/ptr/petservice/application-details/")) return t("PTR_APPLICATION_TITLE");
    else if (location.pathname.includes("/digit-ui/employee/ptr/petservice/payment-details/")) return t("PT_PAYMENT_HISTORY");
    
  };

  const PTBreadCrumbs = ({ location }) => {
    const { t } = useTranslation();
    const search = useLocation().search;
    const fromScreen = new URLSearchParams(search).get("from") || null;
    const { from : fromScreen2 } = Digit.Hooks.useQueryParams();
    const crumbs = [
      {
        path: "/digit-ui/employee",
        content: t("ES_COMMON_HOME"),
        show: true,
      },
      {
        path: "/digit-ui/employee/ptr/petservice/inbox",
        content: t("ES_TITLE_INBOX"),
        show: location.pathname.includes("ptr/petservice/inbox") ? true : false,
      },
      {
        path: "/digit-ui/employee/pt/search",
        content: t("PT_COMMON_SEARCH_PROPERTY_SUB_HEADER"),
        show: location.pathname.includes("/pt/search") || location.pathname.includes("/pt/ptsearch") ? true : false,
      },
    
      {
        path: "/digit-ui/employee/ptr/petservice/my-applications",
        content: t("ES_COMMON_APPLICATION_SEARCH"),
        show: location.pathname.includes("/ptr/petservice/my-applications") || location.pathname.includes("/pt/applicationsearch/application-details/") ? true : false,
      },
      
      {
        path: `/digit-ui/employee/pt/ptsearch/property-details/${sessionStorage.getItem("propertyIdinPropertyDetail")}`,
        content: fromScreen || fromScreen2 ? `${t(fromScreen || fromScreen2)} / ${t("PT_PROPERTY_INFORMATION")}`:t("PT_PROPERTY_INFORMATION"),
        show:  location.pathname.includes("/pt/ptsearch/property-details/") || location.pathname.includes("/pt/ptsearch/payment-details/") || location.pathname.includes("/pt/ptsearch/assessment-details/")  ? true : false,
        isBack:fromScreen && true,
      },
      {
        path: `/digit-ui/employee/pt/property-details/${sessionStorage.getItem("propertyIdinPropertyDetail")}?${fromScreen2?`from=${fromScreen2}` : ''}`,
        content: fromScreen || fromScreen2 ? `${t(fromScreen || fromScreen2)} / ${t("PT_PROPERTY_INFORMATION")}`:t("PT_PROPERTY_INFORMATION"),
        show: location.pathname.includes("/pt/property-details/") || location.pathname.includes("/pt/payment-details/") ? true : false,
        isBack:true,
      },
      
      {
        path: "/digit-ui/employee/pt/payment-details/",
        content: fromScreen ? `${t(fromScreen)} / ${t("PT_PAYMENT_HISTORY")
} `: t("PT_PAYMENT_HISTORY"),
        show: location.pathname.includes("/pt/ptsearch/payment-details") || location.pathname.includes("/pt/payment-details") ? true : false,
        isBack:fromScreen && true,
      },
      
    ];
  
    return <BreadCrumb style={isMobile?{display:"flex"}:{}}  spanStyle={{maxWidth:"min-content"}} crumbs={crumbs} />;
  }

  const NewApplication = Digit?.ComponentRegistryService?.getComponent("PTNewApplication");
  const ApplicationDetails = Digit?.ComponentRegistryService?.getComponent("ApplicationDetails");

  const EditApplication = Digit?.ComponentRegistryService?.getComponent("PTEditApplication");
  const Response = Digit?.ComponentRegistryService?.getComponent("PTResponse");
  const DocsRequired = Digit?.ComponentRegistryService?.getComponent("PTDocsRequired");
  const isRes = window.location.href.includes("ptr/response");
  const isNewRegistration = window.location.href.includes("new-application") || window.location.href.includes("modify-application") || window.location.href.includes("ptr/application-details");
  return (
    <Switch>
      <React.Fragment>
        <div className="ground-container">
          {/* <p className="breadcrumb" style={{ marginLeft: mobileView ? "2vw" : "revert" }}>
            <Link to="/digit-ui/employee" style={{ cursor: "pointer", color: "#666" }}>
              {t("ES_COMMON_HOME")}
            </Link>{" "}
            / <span>{getBreadCrumb()}</span>
          </p>} */}
          {!isRes ? <div style={isNewRegistration ? {marginLeft: "12px" } : {marginLeft:"-4px"}}><PTBreadCrumbs location={location} /></div> : null}
          <PrivateRoute exact path={`${path}/`} component={() => <PTRLinks matchPath={path} userType={userType} />} />
          <PrivateRoute
            path={`${path}/petservice/inbox`}
            component={() => (
              <Inbox
                useNewInboxAPI={true}
                parentRoute={path}
                businessService="ptr"
                filterComponent="PT_INBOX_FILTER"
                initialStates={inboxInitialState}
                isInbox={true}
              />
            )}
          />
          <PrivateRoute path={`${path}/petservice/new-application`} component={() => <NewApplication parentUrl={url} />} />
          <PrivateRoute path={`${path}/petservice/application-details/:id`} component={() => <ApplicationDetails parentRoute={path} />} />
          <PrivateRoute path={`${path}/petservice/applicationsearch/application-details/:id`} component={() => <ApplicationDetails parentRoute={path} />} />
          <PrivateRoute path={`${path}/petservice/payment-details/:id`} component={() => <PaymentDetails parentRoute={path} />} />
          <PrivateRoute path={`${path}/petservice/ptsearch/payment-details/:id`} component={() => <PaymentDetails parentRoute={path} />} />
          <PrivateRoute path={`${path}/petservice/response`} component={(props) => <Response {...props} parentRoute={path} />} />
          <PrivateRoute path={`${path}/petservice/search`} component={(props) => <Search {...props} t={t} parentRoute={path} />} />
          <PrivateRoute
            path={`${path}/searchold`}
            component={() => (
              <Inbox
                parentRoute={path}
                businessService="ptr"
                middlewareSearch={searchMW}
                initialStates={inboxInitialState}
                isInbox={false}
                EmptyResultInboxComp={"PTEmptyResultInbox"}
              />
            )}
          />
          <PrivateRoute path={`${path}/petservice/my-applications`} component={(props) => <SearchApp {...props} parentRoute={path} />} />
        </div>
      </React.Fragment>
    </Switch>
  );
};

export default EmployeeApp;
