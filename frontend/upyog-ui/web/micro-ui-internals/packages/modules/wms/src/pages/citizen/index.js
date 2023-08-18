import { AppContainer, BackButton,PrivateRoute } from "@egovernments/digit-ui-react-components";
import React from "react";
import {  Switch, useRouteMatch } from "react-router-dom";

import { useTranslation } from "react-i18next";



const CitizenApp = () => {
  const { path, url, ...match } = useRouteMatch();
  const { t } = useTranslation();

  const WmsSorCreate = Digit?.ComponentRegistryService?.getComponent("WmsSorCreate");
  const WmsSorUpdate = Digit?.ComponentRegistryService?.getComponent("WmsSorUpdate");
  const Response = Digit?.ComponentRegistryService?.getComponent("Response");
  
  return (
    <span className={"pt-citizen"}>
      <Switch>
        <AppContainer>
        <BackButton>Back</BackButton> 
        
          <PrivateRoute path={`${path}/sor/create`} component={WmsSorCreate} />
          <PrivateRoute path={`${path}/sor/update/:id`} component={WmsSorUpdate} />
          <PrivateRoute path={`${path}/response`} component={Response} />
        </AppContainer>
      </Switch>
    </span>
  );
};

export default CitizenApp;
