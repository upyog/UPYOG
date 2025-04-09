// Importing necessary components and hooks from external libraries and local files
import { AppContainer, BackButton, PrivateRoute } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { Route, Switch, useRouteMatch } from "react-router-dom";
import { shouldHideBackButton } from "../../utils"; // Utility function to determine if the back button should be hidden
import { useTranslation } from "react-i18next"; // Hook for translations

// Configuration array for hiding the back button
const hideBackButtonConfig = [];

// Main application component for the citizen module
const App = () => {
  const { path, url, ...match } = useRouteMatch(); // Hook to get route match details
  const { t } = useTranslation(); // Hook for translations

  // Fetching components dynamically from the Digit Component Registry Service
  const EWCreate = Digit?.ComponentRegistryService?.getComponent("EWCreatewaste"); // Component for creating a new waste request
  const EWASTEMyApplications = Digit?.ComponentRegistryService?.getComponent("EWASTEMyApplications"); // Component for viewing citizen applications
  const EWASTEApplicationDetails = Digit?.ComponentRegistryService?.getComponent("EWASTECitizenApplicationDetails"); // Component for viewing application details

  return (
    <span className={"citizen"} style={{ width: "100%" }}> 
      <Switch>
        <AppContainer>
          {/* Conditionally render the back button based on the configuration */}
          {!shouldHideBackButton(hideBackButtonConfig) ? <BackButton>Back</BackButton> : ""}
          
          {/* Route for raising a new waste request */}
          <PrivateRoute path={`${path}/raiseRequest`} component={EWCreate} />
          
          {/* Route for viewing application details */}
          <PrivateRoute path={`${path}/application/:requestId/:tenantId`} component={EWASTEApplicationDetails}></PrivateRoute>
          
          {/* Route for viewing the list of citizen applications */}
          <PrivateRoute path={`${path}/myApplication`} component={EWASTEMyApplications}></PrivateRoute>
        </AppContainer>
      </Switch>
    </span>
  );
};

export default App; // Exporting the main application component