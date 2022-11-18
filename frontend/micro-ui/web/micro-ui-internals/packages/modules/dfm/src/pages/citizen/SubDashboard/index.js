import React, { useState, useEffect } from "react";
import { Route, Switch, useRouteMatch } from "react-router-dom";
import { PrivateRoute, BreadCrumb } from "@egovernments/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import Submenu from "./Submenu";
import FormsUi from "./FormsUi";

const SubDashboard = ({ parentUrl }) => {
  const { path } = useRouteMatch();
  console.log(parentUrl);
 
  return (
    <React.Fragment>
      <Switch>
        <Route path={`${path}`} exact>
          <Submenu  path={path}/>
        </Route>
        <PrivateRoute  parentRoute={path} path={`${path}/form-ui`} component={() => <FormsUi parentUrl={path} />} />
      </Switch>
    </React.Fragment>
  );
};

export default SubDashboard;
