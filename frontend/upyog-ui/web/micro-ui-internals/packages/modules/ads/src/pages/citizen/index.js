import { AppContainer, BackButton, PrivateRoute } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { Route, Routes } from "react-router-dom";
import { shouldHideBackButton } from "../../utils";
import { useTranslation } from "react-i18next";
import "../../css/ads-inline-auto.css";
const hideBackButtonConfig = [];
const App = () => {
  // const { path, url, ...match } = Digit.Hooks.useModuleBasePath();
  const { t } = useTranslation();
  const ADSCreate = Digit?.ComponentRegistryService?.getComponent("ADSCreate");
//  to show back button on top left of the page in order to go back to previous page
const ADSMyApplications = Digit?.ComponentRegistryService?.getComponent("ADSMyApplications");
const ADSApplicationDetails = Digit?.ComponentRegistryService?.getComponent("ADSApplicationDetails");
//this has been added in order show my bookings page
  return (
    <span className={"ads-citizen"} style={{ width: "100%" }}>
      <AppContainer>
        {!shouldHideBackButton(hideBackButtonConfig) ? <BackButton>Back</BackButton> : ""}
       <Routes>
        <Route path="bookad/*" element={<PrivateRoute><ADSCreate /></PrivateRoute>} />
        <Route path="myBookings/*" element={<PrivateRoute><ADSMyApplications /></PrivateRoute>} />
        <Route path="application/:acknowledgementIds/:tenantId" element={<PrivateRoute><ADSApplicationDetails /></PrivateRoute>} />
      </Routes>
      </AppContainer>
    </span>
  );
};
export default App;
