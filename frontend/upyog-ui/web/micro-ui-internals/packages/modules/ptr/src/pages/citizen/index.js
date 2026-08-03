/* 
  App Component:
  - This is the main entry point for the pet service application.
  - It uses React Router for navigation and `digit-ui-react-components` for UI rendering.
  
  Key Features:
  - Conditional rendering of the back button based on specific route paths.
  - Dynamic component loading using `Digit.ComponentRegistryService`.
  - Private routes to ensure that only authenticated users can access the pages.
  - Supports multilingual translation using `react-i18next`.
  
  Routes:
  - `/petservice/new-application`: For creating a new pet application.
  - `/petservice/revised-application`: For renewing an existing pet application.
  - `/petservice/application/:acknowledgementIds/:tenantId`: For viewing application details.
  - `/petservice/my-applications`: For viewing the user's submitted applications.
  - `/petservice/search`: For searching applications with filters.
*/


import { AppContainer, BackButton, PrivateRoute } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { Route, Routes } from "react-router-dom";
import { shouldHideBackButton } from "../../utils";
import SearchApp from "../employee/SearchApp";


const hideBackButtonConfig = [
  { screenPath: "petservice/new-application/acknowledgement" },
  { screenPath: "petservice/edit-application/acknowledgement" },

];

const App = () => {
  const { path, url, ...match } = Digit.Hooks.useModuleBasePath();

  const PTRCreate = Digit?.ComponentRegistryService?.getComponent("PTRCreatePet");
  const PTRApplicationDetails = Digit?.ComponentRegistryService?.getComponent("PTRApplicationDetails");
  const PTRMyApplications = Digit?.ComponentRegistryService?.getComponent("PTRMyApplications");
 
  return (
    <span className={"pet-citizen"} style={{ width: "100%" }}>
      <AppContainer>
        {!shouldHideBackButton(hideBackButtonConfig) ? <BackButton>Back</BackButton> : ""}
        <Routes>
          <Route path= "petservice/new-application/*" element={<PrivateRoute><PTRCreate /></PrivateRoute>} />
          {/* path added for renew application */}
          <Route path= "petservice/revised-application/*" element={<PrivateRoute><PTRCreate /></PrivateRoute>} />
          <Route path= "petservice/application/:acknowledgementIds/:tenantId" element={<PrivateRoute><PTRApplicationDetails /></PrivateRoute>} />
          <Route path= "petservice/my-applications/*" element={<PrivateRoute><PTRMyApplications /></PrivateRoute>} />
          <Route path= "petservice/search/*" element={<PrivateRoute><SearchApp path={`/petservice/search`} /></PrivateRoute>} />
        </Routes>
      </AppContainer>
    </span>
  );
};

export default App;