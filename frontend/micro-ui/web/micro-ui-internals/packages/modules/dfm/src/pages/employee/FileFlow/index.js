import React, { useState, useEffect } from "react";
import { Route, Switch, useRouteMatch } from "react-router-dom";
import { PrivateRoute, BreadCrumb } from "@egovernments/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import { ReactComponent as BankIcon } from "../Img/BankIcon.svg";
import { ReactComponent as FileProtected } from "../Img/FileProtected.svg";
import FileFlow from "./FileFlow";
import TradeLisense from "./TradeLisense";

const FileFlowApp = ({ parentUrl }) => {
  const { path } = useRouteMatch();
  console.log(parentUrl);
 
  return (
    <React.Fragment>
      <Switch>
        <Route path={`${path}`} exact>
          <FileFlow  path={path}/>
        </Route>
        <PrivateRoute  parentRoute={path} path={`${path}/trade-lisense`} component={() => <TradeLisense parentUrl={path} />} />
      </Switch>
    </React.Fragment>
  );
};

export default FileFlowApp;
