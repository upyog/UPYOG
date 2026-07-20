import { AppContainer, BackButton, PrivateRoute } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { Route, Routes, useLocation } from "react-router-dom";
import GCCreate from "./Create";
import GCMyApplications from "./MyApplication";
import GCApplicationDetails from "./GCApplicationDetails";
import GCEdIt from "./Edit";

const hideBackPaths = ["new-registration/acknowledgement", "my-applications"];

const shouldHideBack = () => {
  const path = window.location.pathname;
  return hideBackPaths.some((p) => path.includes(p));
};

const App = () => {
  const location = useLocation();
  return (
      <AppContainer>
        {!shouldHideBack() && <BackButton>Back</BackButton>}
        <Routes>
          <Route path="new-registration/*" element={<PrivateRoute><GCCreate /></PrivateRoute>} />
          <Route path="my-applications/*" element={<PrivateRoute><GCMyApplications key={location.pathname} /></PrivateRoute>} />
          <Route path="application-details/:applicationNo/*" element={<PrivateRoute><GCApplicationDetails key={location.pathname} /></PrivateRoute>} />
          <Route path="edit/:applicationNo/*" element={<PrivateRoute><GCEdIt /></PrivateRoute>} />
        </Routes>
      </AppContainer>
  );
};

export default App;
