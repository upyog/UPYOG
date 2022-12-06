import { AppContainer, BackButton, PrivateRoute } from "@egovernments/digit-ui-react-components";
import React from "react";
import { Route, Switch, useRouteMatch } from "react-router-dom";
import SubDashboard from './SubDashboard'


const App = () => {
  const { path, url, ...match } = useRouteMatch();
  console.log(path);
  let isSuccessScreen = window.location.href.includes("acknowledgement");
  let isCommonPTPropertyScreen = window.location.href.includes("/tl/tradelicence/new-application/property-details");

  // const ApplicationDetails = Digit.ComponentRegistryService.getComponent("TLApplicationDetails");
  // const CreateTradeLicence = Digit?.ComponentRegistryService?.getComponent('TLCreateTradeLicence');
  // const EditTrade = Digit?.ComponentRegistryService?.getComponent('TLEditTrade');
  // const RenewTrade = Digit?.ComponentRegistryService?.getComponent('TLRenewTrade');
  // const TradeLicense = Digit?.ComponentRegistryService?.getComponent('TradeLicense');
  // const TLList = Digit?.ComponentRegistryService?.getComponent('TLList');
  // const SearchTradeComponent = Digit?.ComponentRegistryService?.getComponent('TLSearchTradeComponent');
  // const MyApplications = Digit?.ComponentRegistryService?.getComponent('MyApplications');

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
console.log('dfm',path);
{/* <BackButton  isCommonPTPropertyScreen={isCommonPTPropertyScreen} isSuccessScreen={isSuccessScreen} getBackPageNumber={getBackPageNumber}>Back</BackButton> */}
  return (
    <span className={"tl-citizen"}>
      <Switch>
        {/* <AppContainer> */}
         
          <PrivateRoute path={`${path}/submenu`} component={() => < SubDashboard/>}/>  
          {/* <PrivateRoute path={`${path}/form-ui`} component={() => <GenericForms/>} /> */}
        {/* </AppContainer> */} 
      </Switch>
    </span>
  );
};

export default App;
