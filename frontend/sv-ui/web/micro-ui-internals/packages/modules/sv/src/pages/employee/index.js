import { AppContainer, BackButton, PrivateRoute, BreadCrumb } from "@nudmcdgnpm/upyog-ui-react-components-lts";
import React from "react";
import { Route, Routes, useLocation } from "react-router-dom";
import { useTranslation } from "react-i18next";
import Inbox from "./Inbox";
import SearchApp from "./SearchApp";


/** The Main routes component for the employee side
 * Contains routes for every page there is to redirect in the employee side
 * Contains breadcrumbs for each page
 */
const EmployeeApp = () => {
 
  const { t } = useTranslation();
  const location = useLocation();
  const isMobile = false

  const inboxInitialState = {
    searchParams: {
      uuid: { code: "ASSIGNED_TO_ALL", name: "ES_INBOX_ASSIGNED_TO_ALL" },
      services: ["street-vending"],
      status: null,
      vendingType: null,
      vendingZone: null
    },
  };

 
  const SVEmpCreate = Digit?.ComponentRegistryService?.getComponent("SVEmpCreate");
  const EnhancedReport = Digit?.ComponentRegistryService?.getComponent("EnhancedReport");
  const SVApplicationDetails = Digit?.ComponentRegistryService?.getComponent("SVApplicationDetails")
  
  return (
    <span className={"sv-citizen"}style={{width:"100%"}}>
      <AppContainer>
        <BackButton style={{marginTop:"15px"}}>Back</BackButton>
        <Routes>
          <Route 
            path="apply/*" 
            element={
              <PrivateRoute>
                <SVEmpCreate />
              </PrivateRoute>
            } 
          />
          <Route 
            path="inbox" 
            element={
              <PrivateRoute>
                <Inbox
                  useNewInboxAPI={true}
                  parentRoute="/sv-ui/employee/sv"
                  businessService="sv"
                  filterComponent="SV_INBOX_FILTER"
                  initialStates={inboxInitialState}
                  isInbox={true}
                />
              </PrivateRoute>
            } 
          />
          <Route 
            path="application-details/:id" 
            element={
              <PrivateRoute>
                <SVApplicationDetails />
              </PrivateRoute>
            } 
          />
          <Route 
            path="applicationsearch/application-details/:id" 
            element={
              <PrivateRoute>
                <SVApplicationDetails />
              </PrivateRoute>
            } 
          />
          <Route 
            path="my-applications" 
            element={
              <PrivateRoute>
                <SearchApp />
              </PrivateRoute>
            } 
          />
        <Route path="StreetVendingReport/*" element={<PrivateRoute><EnhancedReport parentRoute="/sv-ui/employee/sv" moduleName="sv-report" reportName="StreetVendingReport" /></PrivateRoute>} />
        </Routes>
      </AppContainer>
    </span>
  );
};

export default EmployeeApp;