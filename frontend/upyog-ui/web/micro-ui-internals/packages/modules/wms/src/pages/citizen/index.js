import { AppContainer, BackButton,LinkButton,PrivateRoute } from "@egovernments/digit-ui-react-components";
import React from "react";
import {  Switch, useRouteMatch } from "react-router-dom";

import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom/cjs/react-router-dom";


const CitizenApp = () => {
  const { path, url, ...match } = useRouteMatch();
  console.log("path ",path)
  const { t } = useTranslation();

  const WmsSorCreate = Digit?.ComponentRegistryService?.getComponent("WmsSorCreate");
  const WmsSorUpdate = Digit?.ComponentRegistryService?.getComponent("WmsSorUpdate");
  const Response = Digit?.ComponentRegistryService?.getComponent("Response");
  const PhysicalMilestone = Digit?.ComponentRegistryService?.getComponent("PhysicalMilestone");
  const ContrMasterAdd = Digit?.ComponentRegistryService?.getComponent("ContrMasterAdd");
  const ContrMasterView = Digit?.ComponentRegistryService?.getComponent("ContrMasterView");
  
  return (
    <span className={"pt-citizen"}>
      <Switch>
        <AppContainer>
        <BackButton>Back</BackButton> 
        <PrivateRoute path={`${path}/sor/create`} component={WmsSorCreate} />
          <PrivateRoute path={`${path}/sor/update/:id`} component={WmsSorUpdate} />
          <PrivateRoute path={`${path}/response`} component={Response} />
          <PrivateRoute path={`${path}/pm-home`} component={PhysicalMilestone} />
          <PrivateRoute path={`${path}/cm-home`} component={ContrMasterAdd} />
          <PrivateRoute path={`${path}/cm-table-view`} component={ContrMasterView} />
        </AppContainer>
      </Switch>
    </span>
  );
};

export default CitizenApp;
