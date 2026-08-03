import { AppContainer, BackButton, PrivateRoute } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { Routes, Route } from "react-router-dom";
// import SearchChallanComponent from "./SearchChallan";
// import SearchResultsComponent from "./SearchResults";
// import MyChallanResultsComponent from "./MyChallan";
//import BillInfo from "./SearchResults/BillInfo";

const App = () => {
  console.log("App rendered");
  const { path, url, ...match } = Digit.Hooks.useModuleBasePath();

  const SearchChallanComponent = Digit?.ComponentRegistryService?.getComponent("MCollectSearchChallanComponent");
  const SearchResultsComponent = Digit?.ComponentRegistryService?.getComponent("MCollectSearchResultsComponent");
  const MyChallanResultsComponent = Digit?.ComponentRegistryService?.getComponent("MCollectMyChallanResultsComponent");

  return (
    <span className={"mcollect-citizen"}>
      <AppContainer>
        <BackButton style={{ top: "55px" }}>Back</BackButton>
        <Routes>
          <Route path={`search`} element={<PrivateRoute><SearchChallanComponent /></PrivateRoute>} />
          <Route path={`search-results`} element={<PrivateRoute><SearchResultsComponent /></PrivateRoute>} />
          <Route path={`My-Challans`} element={<PrivateRoute><MyChallanResultsComponent /></PrivateRoute>} />
        </Routes>
      </AppContainer>
    </span>
  );
};

export default App;
