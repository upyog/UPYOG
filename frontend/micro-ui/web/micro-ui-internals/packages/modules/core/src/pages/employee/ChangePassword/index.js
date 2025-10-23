import React, { useMemo } from "react";
import { useTranslation } from "react-i18next";
import { AppContainer } from "@upyog/digit-ui-react-components";
import { Route, Switch, useRouteMatch } from "react-router-dom";
import { config } from "./config";
import ChangePasswordComponent from "./changePassword";

const EmployeeChangePassword = () => {
  const { t } = useTranslation();
  const { path } = useRouteMatch();

  const params = useMemo(() =>
    config.map(
      (step) => {
        const texts = {};
        for (const key in step.texts) {
          texts[key] = t(step.texts[key]);
        }
        return { ...step, texts };
      },
      [config]
    )
  );

  return (
    <Switch>
      <Route path={`${path}`} exact>
        <ChangePasswordComponent config={params[0]} t={t} />
      </Route>
    </Switch>
  );
};

export default EmployeeChangePassword;
