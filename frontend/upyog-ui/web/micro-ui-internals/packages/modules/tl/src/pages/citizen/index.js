import { AppContainer, BackButton, PrivateRoute } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { Route, Routes } from "react-router-dom";
// import TradeLicense from "../../pageComponents/TradeLicense";
// import MyApplications from "../../pages/citizen/Applications/Application";
// import ApplicationDetails from "../../pages/citizen/Applications/ApplicationDetails";
// import CreateTradeLicence from "./Create";
// import EditTrade from "./EditTrade";
// import { TLList } from "./Renewal";
// import RenewTrade from "./Renewal/renewTrade";
// import SearchTradeComponent from "./SearchTrade";

const App = () => {
  const { path } = Digit.Hooks.useModuleBasePath();
  let isSuccessScreen = window.location.href.includes("acknowledgement");
  let isCommonPTPropertyScreen = window.location.href.includes("/tl/tradelicence/new-application/property-details");

  const ApplicationDetails = Digit.ComponentRegistryService.getComponent("TLApplicationDetails");
  const CreateTradeLicence = Digit?.ComponentRegistryService?.getComponent('TLCreateTradeLicence');
  const EditTrade = Digit?.ComponentRegistryService?.getComponent('TLEditTrade');
  const RenewTrade = Digit?.ComponentRegistryService?.getComponent('TLRenewTrade');
  const TradeLicense = Digit?.ComponentRegistryService?.getComponent('TradeLicense');
  const TLList = Digit?.ComponentRegistryService?.getComponent('TLList');
  const SearchTradeComponent = Digit?.ComponentRegistryService?.getComponent('TLSearchTradeComponent');
  const TLMyApplications = Digit?.ComponentRegistryService?.getComponent('TLMyApplications');

  const getBackPageNumber = () => {
    let goBacktoFromProperty = -1;
  if(sessionStorage.getItem("VisitedCommonPTSearch") === "true" && (sessionStorage.getItem("VisitedAccessoriesDetails") === "true" || sessionStorage.getItem("VisitedisAccessories") === "true") && isCommonPTPropertyScreen)
  {
    goBacktoFromProperty = -4;
    sessionStorage.removeItem("VisitedCommonPTSearch");
    return goBacktoFromProperty;
  }
  return goBacktoFromProperty;
  }

  return (
    <span className={"tl-citizen"}>
      <AppContainer>
        {!(window.location.href.includes("/acknowledgement")) && window.location.href.includes("tl/tradelicence") && (
          <BackButton isCommonPTPropertyScreen={isCommonPTPropertyScreen} isSuccessScreen={isSuccessScreen} getBackPageNumber={getBackPageNumber}>
            Back
          </BackButton>
        )}
        <Routes>
          <Route path={`tradelicence/new-application/*`} element={<PrivateRoute><CreateTradeLicence path={path} /></PrivateRoute>} />
          <Route path={`tradelicence/edit-application/:id/:tenantId/*`} element={<PrivateRoute><EditTrade /></PrivateRoute>} />
          <Route path={`tradelicence/renew-trade/:id/:tenantId/*`} element={<PrivateRoute><RenewTrade /></PrivateRoute>} />
          <Route path={`tradelicence/my-application`} element={<PrivateRoute><TLMyApplications /></PrivateRoute>} />
          <Route path={`tradelicence/my-bills`} element={<PrivateRoute><TLMyApplications view="bills" /></PrivateRoute>} />
          <Route path={`tradelicence/tl-info`} element={<PrivateRoute><TradeLicense /></PrivateRoute>} />
          <Route path={`tradelicence/application/:id/:tenantId`} element={<PrivateRoute><ApplicationDetails /></PrivateRoute>} />
          <Route path={`tradelicence/renewal-list`} element={<PrivateRoute><TLList /></PrivateRoute>} />
          <Route path={`tradelicence/trade-search`} element={<PrivateRoute><SearchTradeComponent /></PrivateRoute>} />
        </Routes>
      </AppContainer>
    </span>
  );
};

export default App;
