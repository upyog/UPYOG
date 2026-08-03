import { AppContainer, BackButton, PrivateRoute } from "@nudmcdgnpm/upyog-ui-react-components-lts";
import React from "react";
import { Routes, Route } from "react-router-dom";

/**
 * Citizen side main index page which is handling the Routing of whole citizen side
 */
const App = () => {
  
  const SVCreate = Digit?.ComponentRegistryService?.getComponent("Create");
  const MyApplication = Digit?.ComponentRegistryService?.getComponent("SVMyApplications");
  const SvApplicationDetails = Digit?.ComponentRegistryService?.getComponent("SvApplicationDetails");
  return (
    <span className={"sv-citizen"} style={{width:"100%"}}>
      <AppContainer>
        <BackButton>Back</BackButton>
        <Routes>
          <Route path="apply/*" element={ <PrivateRoute> <SVCreate /> </PrivateRoute>}/>
          <Route path="renew-application/*" element={ <PrivateRoute> <SVCreate /> </PrivateRoute>}/>
          <Route path="edit/*" element={ <PrivateRoute> <SVCreate /> </PrivateRoute> } />
          <Route path="my-applications/*"  element={ <PrivateRoute> <MyApplication /> </PrivateRoute> } />
          <Route path="application/:applicationNo/:tenantId" element={ <PrivateRoute> <SvApplicationDetails /> </PrivateRoute>} />
        </Routes>
      </AppContainer>
    </span>
  );
};

export default App;