import { AppContainer, BackButton, PrivateRoute } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { Route, Routes } from "react-router-dom";
import { shouldHideBackButton } from "../../utils";
import { useTranslation } from "react-i18next";

/**
 * Configuration for routes where back button should be hidden
 * Add route paths to hide the back button on specific screens
 */
const hideBackButtonConfig = [];

/**
 * Main application component for the E-Waste citizen module.
 * Handles routing between different citizen-facing interfaces including:
 * - New waste request creation
 * - Application tracking
 * - Application details viewing
 * 
 * Uses private routes to ensure authenticated access to all features.
 * Manages responsive layout and navigation elements.
 *
 * @returns {JSX.Element} Root component for citizen E-Waste module
 */
const App = () => {
  const { path, url, ...match } = Digit.Hooks.useModuleBasePath();
  const { t } = useTranslation();

  const EWCreate = Digit?.ComponentRegistryService?.getComponent("EWCreatewaste");
  const EWASTEMyApplications = Digit?.ComponentRegistryService?.getComponent("EWASTEMyApplications");
  const EWASTEApplicationDetails = Digit?.ComponentRegistryService?.getComponent("EWASTECitizenApplicationDetails");
 
  return (
    <span className={"citizen"} style={{ width: "100%" }}>
      <AppContainer>
        {!shouldHideBackButton(hideBackButtonConfig) ? <BackButton>Back</BackButton> : ""}
        <Routes>
          <Route path="raiseRequest/*" element={<PrivateRoute><EWCreate /></PrivateRoute>} />
          <Route path="application/:requestId/:tenantId/*" element={<PrivateRoute><EWASTEApplicationDetails /></PrivateRoute>} />
          <Route path="myApplication/*" element={<PrivateRoute><EWASTEMyApplications /></PrivateRoute>} />
        </Routes>
      </AppContainer>
    </span>
  );
};

export default App;