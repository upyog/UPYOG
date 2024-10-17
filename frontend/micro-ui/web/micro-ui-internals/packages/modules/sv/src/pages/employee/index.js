import { AppContainer, BackButton, PrivateRoute } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { Route, Switch, useRouteMatch } from "react-router-dom";
import { useTranslation } from "react-i18next";

const EmployeeApp = () => {
  const { path, url, ...match } = useRouteMatch();
  const { t } = useTranslation();
  const inboxInitialState = {
    searchParams: {},
  };

  const SVEmpCreate = Digit?.ComponentRegistryService?.getComponent("SVEmpCreate");
 
  return (
    <span className={"sv-citizen"}style={{width:"100%"}}>
      <Switch>
        <AppContainer>
          <BackButton>Back</BackButton>
          <PrivateRoute path={`${path}/apply`} component={SVEmpCreate} />
        </AppContainer>
      </Switch>
    </span>
  );
};

export default EmployeeApp;345678