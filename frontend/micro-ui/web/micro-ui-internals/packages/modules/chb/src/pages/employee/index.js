import {AppContainer, BackButton, PrivateRoute,BreadCrumb } from "@upyog/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { Link, Switch, useLocation } from "react-router-dom";
import { CHBLinks } from "../../Module";
import Inbox from "./Inbox";
// import PaymentDetails from "./PaymentDetails";
import SearchApp from "./SearchApp";

 /*
    EmployeeApp is a routing container for various components related to employee interactions
    within the Community Hall Booking (CHB) module. It handles multiple routes such as inbox 
    management, hall booking creation, application search, and application details display.

    It dynamically renders different child components based on the route and user type.
  */
const EmployeeApp = ({ path, url, userType }) => {
  const { t } = useTranslation();
  const location = useLocation();
  const mobileView = innerWidth <= 640;
  sessionStorage.removeItem("revalidateddone");
  const isMobile = window.Digit.Utils.browser.isMobile();

  const inboxInitialState = {
    searchParams: {
      uuid: { code: "ASSIGNED_TO_ALL", name: "ES_INBOX_ASSIGNED_TO_ALL" },
      services: ["booking-refund"],
      applicationStatus: [],
      locality: [],

    },
  };

 

  const ApplicationDetails = Digit?.ComponentRegistryService?.getComponent("ApplicationDetails");

  const Response = Digit?.ComponentRegistryService?.getComponent("CHBResponse");
  const CHBCreate = Digit?.ComponentRegistryService?.getComponent("CHBCreate");
  const isRes = window.location.href.includes("chb/response");
  const isNewRegistration = window.location.href.includes("searchhall") || window.location.href.includes("modify-application") || window.location.href.includes("chb/application-details");
  return (
    <Switch>
      <AppContainer>
      <React.Fragment>
        <div className="ground-container">
            {!isRes ? 
              <div style={isNewRegistration ? { marginLeft: "12px",display: "flex", alignItems: "center" } : { marginLeft: "-4px",display: "flex", alignItems: "center" }}>
                  <BackButton location={location} />
                  {/* <CHBBreadCrumbs location={location} /> */}
               
              </div>
          : null}
          <PrivateRoute exact path={`${path}/`} component={() => <CHBLinks matchPath={path} userType={userType} />} />
          <PrivateRoute
            path={`${path}/inbox`}
            component={() => (
              <Inbox
                useNewInboxAPI={true}
                parentRoute={path}
                businessService="booking-refund"
                filterComponent="CHB_INBOX_FILTER"
                initialStates={inboxInitialState}
                isInbox={true}
              />
            )}
          />
          <PrivateRoute path={`${path}/bookHall`} component={CHBCreate} />
          <PrivateRoute path={`${path}/application-details/:id`} component={() => <ApplicationDetails parentRoute={path} />} />
          <PrivateRoute path={`${path}/applicationsearch/application-details/:id`} component={() => <ApplicationDetails parentRoute={path} />} />
          <PrivateRoute path={`${path}/response`} component={(props) => <Response {...props} parentRoute={path} />} />
          <PrivateRoute path={`${path}/search`} component={(props) => <Search {...props} t={t} parentRoute={path} />} />
          <PrivateRoute
            path={`${path}/searchold`}
            component={() => (
              <Inbox
                parentRoute={path}
                businessService="booking-refund"
                middlewareSearch={searchMW}
                initialStates={inboxInitialState}
                isInbox={false}
                EmptyResultInboxComp={"PTEmptyResultInbox"}
              />
            )}
          />
          <PrivateRoute path={`${path}/my-applications`} component={(props) => <SearchApp {...props} parentRoute={path} />} />
        </div>
        </React.Fragment>
      </AppContainer>
    </Switch>
  );
};

export default EmployeeApp;
