import { AppContainer, BackButton, PrivateRoute } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { Route, Routes, Navigate } from "react-router-dom";
import Inbox from "../employee/Inbox";
import { cndStyles } from "../../utils/cndStyles";
import "../../css/cnd-inline-auto.scss";

/**
 * Component to handle all the routings of Citizen Side.
 * 1. Render the VendorCitizenCard for the Vendor Login and Inbox and My Request links are available in the Card for the same.
 * 2. Handle Apply and My Request for the Normal Citizen
 */

const App = () => {
  const { path, url } = Digit.Hooks.useModuleBasePath();
  const CndCreate = Digit?.ComponentRegistryService?.getComponent("CndCreate");
  const MyRequests = Digit?.ComponentRegistryService?.getComponent("MyRequests");
  const CndApplicationDetails = Digit?.ComponentRegistryService?.getComponent("CndApplicationDetails");
  const CNDCard = Digit?.ComponentRegistryService?.getComponent("CNDVendorCard");
  const ApplicationDetails = Digit?.ComponentRegistryService?.getComponent("ApplicationDetails");
  const inboxInitialState = {
    searchParams: {
      uuid: { code: "ASSIGNED_TO_ALL", name: "ES_INBOX_ASSIGNED_TO_ALL" },
      services: ["cnd"],
      status: null,
    },
  };

  return (
    <span style={cndStyles.wasteQuantityCitizen}>
      <AppContainer>
        <BackButton>Back</BackButton>
        <Routes>
          <Route path="apply/*" element={<PrivateRoute><CndCreate /></PrivateRoute>} />
          <Route path="my-request" element={<PrivateRoute><MyRequests /></PrivateRoute>} />
          <Route path="my-requests/:applicationNumber/:tenantId" element={<PrivateRoute><CndApplicationDetails /></PrivateRoute>} />
          <Route
            path="cnd-vendor"
            element={
              <PrivateRoute>
                {Digit.UserService.hasAccess(["CND_VENDOR"]) ? (
                  <CNDCard parentRoute={path} />
                ) : (
                  <Navigate to="/cnd-ui/citizen/login" state={{ from: `${path}/cnd-vendor`, role: "CND_VENDOR" }} replace />
                )}
              </PrivateRoute>
            }
          />
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
          <Route path="application-details/:id" element={<PrivateRoute><ApplicationDetails parentRoute={path} /></PrivateRoute>} />
          <Route path="applicationsearch/application-details/:id" element={<PrivateRoute><ApplicationDetails parentRoute={path} /></PrivateRoute>} />
        </Routes>
      </AppContainer>
    </span>
  );
};

export default App;