import React, { useState, useEffect } from "react";
import { Route, Switch, useRouteMatch } from "react-router-dom";
import { PrivateRoute, BreadCrumb } from "@egovernments/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import { ReactComponent as BankIcon } from "../Img/BankIcon.svg";
import { ReactComponent as FileProtected } from "../Img/FileProtected.svg";
import CrFlow from "./CrFlow";
import ChildDetails from "../../../pageComponents/birthComponents/ChildDetails";
import { newConfig as newConfigCR } from "../../../config/config";

const CrFlowApp = ({ parentUrl }) => {
  const { t } = useTranslation();
  const { path } = useRouteMatch();
  let config = [];
  let { data: newConfig, isLoading } = true;
  newConfig = newConfigCR;
  newConfig?.forEach((obj) => {
    config = config.concat(obj.body.filter((a) => !a.hideInCitizen));
  });
  config.indexRoute = "child-details";
  return (
    
    <React.Fragment>
      <Switch>
        <Route path={`${path}`} exact>
          <CrFlow  path={path}/>
        </Route>
        <PrivateRoute  parentRoute={path} path={`${path}/${config.indexRoute}`} component={() => <ChildDetails parentUrl={path} />} />
      </Switch>
    </React.Fragment>
  );
};

export default CrFlowApp;
