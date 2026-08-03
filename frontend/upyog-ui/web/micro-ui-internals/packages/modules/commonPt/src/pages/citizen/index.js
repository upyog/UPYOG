import { AppContainer, BackButton, PrivateRoute } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { Route, Routes } from "react-router-dom";
import CreateProperty from "./Create";
import SearchPropertyComponent from "./SearchProperty";
import SearchResultsComponent from "./SearchResults";
import PropertyLinkSuccess from "./LinkSuccess";
import CitizenOtp from "./Otp";
import ViewProperty from "../pageComponents/ViewProperty";

const App = ({ stateCode }) => {
  const { path, url, ...match } = Digit.Hooks.useModuleBasePath();
  return (
    <span className={"pt-citizen"} style={{ width: "100%" }}>
      <AppContainer>
        <BackButton>Back</BackButton>
        <Routes>
          <Route path="property/citizen-search" element={<SearchPropertyComponent />} />
          <Route path="property/search-results" element={<SearchResultsComponent stateCode={stateCode} />} />
          <Route path="property/citizen-otp" element={<CitizenOtp stateCode={stateCode} />} />
          <Route path="property/link-success/:propertyIds" element={<PrivateRoute><PropertyLinkSuccess /></PrivateRoute>} />
          <Route path="property/new-application/*" element={<PrivateRoute><CreateProperty /></PrivateRoute>} />
          <Route path="view-property" element={<PrivateRoute><ViewProperty /></PrivateRoute>} />
        </Routes>
      </AppContainer>
    </span>
  );
};

export default App;
