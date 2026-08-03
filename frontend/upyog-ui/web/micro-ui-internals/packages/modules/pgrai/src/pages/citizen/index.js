import { AppContainer, BackButton, PrivateRoute } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { Route, Routes } from "react-router-dom";
import { shouldHideBackButton } from "../../utils";
import { useTranslation } from "react-i18next";

const hideBackButtonConfig = [];

const App = () => {
  const { path, url, ...match } = Digit.Hooks.useModuleBasePath();
  const { t } = useTranslation();
  const PGRAICreate = Digit?.ComponentRegistryService?.getComponent("PGRAICreate");
  const PGRAIApplicationDetails = Digit?.ComponentRegistryService?.getComponent("PGRAIMyApplications");
  const PGRApplicationDetails = Digit?.ComponentRegistryService?.getComponent("PGRApplicationDetails");
//  to show back button on top left of the page in order to go back to previous page
//this has been added in order show my bookings page
  return (
    <span className={"ads-citizen"} style={{ width: "100%" }}>
      <AppContainer>
        {!shouldHideBackButton(hideBackButtonConfig) ? <BackButton>Back</BackButton> : ""}
        <Routes>
          <Route path= "fileGrievance/*" element={<PrivateRoute><PGRAICreate /></PrivateRoute>} />
          <Route path= "myGrievance/*" element={<PrivateRoute><PGRAIApplicationDetails /></PrivateRoute>} />
          <Route path= "application/:acknowledgementIds/:tenantId" element={<PrivateRoute><PGRApplicationDetails /></PrivateRoute>} />
        </Routes>
      </AppContainer>
    </span>
  );
};

export default App;