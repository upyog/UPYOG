import { AppContainer, BackButton, PrivateRoute } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { Route, Routes } from "react-router-dom";
import Inbox from "./Inbox";
import SearchApp from "./SearchApp";
import { cndStyles } from "../../utils/cndStyles";

/** The Main routes component for the employee side
 * Contains routes for every page there is to redirect in the employee side
 * Contains breadcrumbs for each page
 */
const EmployeeApp = ({ path, url }) => {

  const inboxInitialState = {
    searchParams: {
      uuid: { code: "ASSIGNED_TO_ALL", name: "ES_INBOX_ASSIGNED_TO_ALL" },
      services: ["cnd"],
      status: null,
    },
  };

  const ApplicationDetails = Digit?.ComponentRegistryService?.getComponent("ApplicationDetails");
  const EditCreate = Digit?.ComponentRegistryService?.getComponent("EditCreate");
  const EditResponse = Digit?.ComponentRegistryService?.getComponent("EditSubmissionResponse");
  const FacilityCentreCreationDetails = Digit?.ComponentRegistryService?.getComponent("FacilityCentreCreationDetails");
  const FacilitySubmissionResponse = Digit?.ComponentRegistryService?.getComponent("FacilitySubmissionResponse");
  const CndCreate = Digit?.ComponentRegistryService?.getComponent("CndCreate");
  const EnhancedReport = Digit?.ComponentRegistryService?.getComponent("EnhancedReport");

  return (
    <span className={"cnd-citizen"} style={cndStyles.wasteQuantityCitizen}>
      <AppContainer>
        <BackButton style={cndStyles.backButton}>Back</BackButton>
        <Routes>
          <Route
            path="inbox"
            element={
              <PrivateRoute>
                <Inbox
                  useNewInboxAPI={true}
                  parentRoute={path}
                  businessService="cnd"
                  filterComponent="CND_INBOX_FILTERS"
                  initialStates={inboxInitialState}
                  isInbox={true}
                />
              </PrivateRoute>
            }
          />
          <Route path="apply/*" element={<PrivateRoute><CndCreate /></PrivateRoute>} />
          <Route path="application-details/:id" element={<PrivateRoute><ApplicationDetails parentRoute={path} /></PrivateRoute>} />
          <Route path="applicationsearch/application-details/:id" element={<PrivateRoute><ApplicationDetails parentRoute={path} /></PrivateRoute>} />
          <Route path="cnd-service/edit/:id" element={<PrivateRoute><EditCreate parentUrl={url} /></PrivateRoute>} />
          <Route path="edit-response" element={<PrivateRoute><EditResponse parentRoute={path} /></PrivateRoute>} />
          <Route path="my-request" element={<PrivateRoute><SearchApp parentRoute={path} /></PrivateRoute>} />
          <Route path="cnd-service/facility-centre/:id" element={<PrivateRoute><FacilityCentreCreationDetails parentUrl={url} /></PrivateRoute>} />
          <Route path="facility-response" element={<PrivateRoute><FacilitySubmissionResponse parentRoute={path} /></PrivateRoute>} />
          <Route path="CNDApplicationReport" element={<PrivateRoute><EnhancedReport parentRoute={path} moduleName="rainmaker-CND" reportName="CNDApplicationReport" /></PrivateRoute>} />
        </Routes>
      </AppContainer>
    </span>
  );
};

export default EmployeeApp;