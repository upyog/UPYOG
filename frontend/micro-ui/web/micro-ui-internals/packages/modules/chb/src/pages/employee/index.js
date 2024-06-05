import { PrivateRoute,BreadCrumb } from "@upyog/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { Link, Switch, useLocation } from "react-router-dom";
import { CHBLinks } from "../../Module";
import Inbox from "./Inbox";
// import PaymentDetails from "./PaymentDetails";
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
      services: ["chb"],
      applicationStatus: [],
      locality: [],

    },
  };

 

  const PETBreadCrumbs = ({ location }) => {
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
        path: "/digit-ui/employee/chb/bookHall/inbox",
        content: t("ES_TITLE_INBOX"),
        show: location.pathname.includes("chb/bookHall/inbox") ? true : false,
      },
     
    
      {
        path: "/digit-ui/employee/chb/bookHall/my-applications",
        content: t("ES_COMMON_APPLICATION_SEARCH"),
        show: location.pathname.includes("/chb/bookHall/my-applications") || location.pathname.includes("/chb/bookHall/application-details/") ? true : false,
      },
      
     
      
    ];
  
    return <BreadCrumb style={isMobile?{display:"flex"}:{}}  spanStyle={{maxWidth:"min-content"}} crumbs={crumbs} />;
  }

  const NewApplication = Digit?.ComponentRegistryService?.getComponent("PTRNewApplication");
  const ApplicationDetails = Digit?.ComponentRegistryService?.getComponent("ApplicationDetails");

  // const EditApplication = Digit?.ComponentRegistryService?.getComponent("PTEditApplication");
  const Response = Digit?.ComponentRegistryService?.getComponent("PTRResponse");
  const DocsRequired = Digit?.ComponentRegistryService?.getComponent("PTRDocsRequired");
  const isRes = window.location.href.includes("chb/response");
  const isNewRegistration = window.location.href.includes("new-application") || window.location.href.includes("modify-application") || window.location.href.includes("chb/application-details");
  return (
    <Switch>
      <React.Fragment>
        <div className="ground-container">
          
          {!isRes ? <div style={isNewRegistration ? {marginLeft: "12px" } : {marginLeft:"-4px"}}><PETBreadCrumbs location={location} /></div> : null}
          <PrivateRoute exact path={`${path}/`} component={() => <CHBLinks matchPath={path} userType={userType} />} />
          <PrivateRoute
            path={`${path}/bookHall/inbox`}
            component={() => (
              <Inbox
                useNewInboxAPI={true}
                parentRoute={path}
                businessService="chb"
                filterComponent="CHB_INBOX_FILTER"
                initialStates={inboxInitialState}
                isInbox={true}
              />
            )}
          />
          <PrivateRoute path={`${path}/bookHall/new-application`} component={() => <NewApplication parentUrl={url} />} />
          <PrivateRoute path={`${path}/bookHall/application-details/:id`} component={() => <ApplicationDetails parentRoute={path} />} />
          <PrivateRoute path={`${path}/bookHall/applicationsearch/application-details/:id`} component={() => <ApplicationDetails parentRoute={path} />} />
          <PrivateRoute path={`${path}/bookHall/response`} component={(props) => <Response {...props} parentRoute={path} />} />
          <PrivateRoute path={`${path}/bookHall/search`} component={(props) => <Search {...props} t={t} parentRoute={path} />} />
          <PrivateRoute
            path={`${path}/searchold`}
            component={() => (
              <Inbox
                parentRoute={path}
                businessService="chb"
                middlewareSearch={searchMW}
                initialStates={inboxInitialState}
                isInbox={false}
                EmptyResultInboxComp={"PTEmptyResultInbox"}
              />
            )}
          />
          <PrivateRoute path={`${path}/bookHall/my-applications`} component={(props) => <SearchApp {...props} parentRoute={path} />} />
        </div>
      </React.Fragment>
    </Switch>
  );
};

export default EmployeeApp;
