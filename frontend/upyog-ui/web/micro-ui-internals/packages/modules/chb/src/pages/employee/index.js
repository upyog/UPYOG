import {AppContainer, BackButton, PrivateRoute,BreadCrumb } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import {useLocation, Routes, Route } from "react-router-dom";
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
  const EnhancedReport = Digit?.ComponentRegistryService?.getComponent("EnhancedReport");

  const CHBCreate = Digit?.ComponentRegistryService?.getComponent("CHBCreate");
  const isRes = window.location.href.includes("chb/response");
  const isNewRegistration = window.location.href.includes("searchhall") || window.location.href.includes("modify-application") || window.location.href.includes("chb/application-details");
  return (
    <AppContainer>
      <React.Fragment>
        <div className="ground-container">
          {!isRes ? (
            <div style={isNewRegistration ? { marginLeft: "12px", display: "flex", alignItems: "center" } : { marginLeft: "-4px", display: "flex", alignItems: "center" }}>
              <BackButton location={location} />
            </div>
          ) : null}
          <Routes>
            <Route path={`/*`} element={<PrivateRoute><CHBLinks userType={userType} /></PrivateRoute>} />
            <Route
              path= "inbox/*"
              element={
                <PrivateRoute>
                  <Inbox
                    useNewInboxAPI={true}
                    businessService="booking-refund"
                    filterComponent="CHB_INBOX_FILTER"
                    initialStates={inboxInitialState}
                    isInbox={true}
                    parentRoute={path}
                  />
                </PrivateRoute>
              }
              //need to check routes
            />
            <Route path= "bookHall/*" element={<PrivateRoute><CHBCreate /></PrivateRoute>} />
            <Route path= "application-details/:id" element={<PrivateRoute><ApplicationDetails /></PrivateRoute>} />
            <Route path= "applicationsearch/application-details/:id" element={<PrivateRoute><ApplicationDetails /></PrivateRoute>} />
            <Route path= "my-applications/*" element={<PrivateRoute><SearchApp /></PrivateRoute>} />
            <Route path="CHBDailyRegisterReport/*" element={<PrivateRoute><EnhancedReport parentRoute={path} moduleName="rainmaker-CHB" reportName="CHBDailyRegisterReport" /></PrivateRoute>} />
            <Route path="CHBRefundReport/*" element={<PrivateRoute><EnhancedReport parentRoute={path} moduleName="rainmaker-CHB" reportName="CHBRefundReport" /></PrivateRoute>} />
            <Route path="PaymentRefundReport/*" element={<PrivateRoute><EnhancedReport parentRoute={path} moduleName="rainmaker-CHB" reportName="PaymentRefundReport" /></PrivateRoute>} />

          </Routes>
        </div>
      </React.Fragment>
    </AppContainer>
  );
};

export default EmployeeApp;
