import { BackButton, PrivateRoute } from "@egovernments/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { Redirect, Switch, useLocation } from "react-router-dom";

const CitizenApp = ({ path }) => {
  const location = useLocation();
  const { t } = useTranslation();

  return (
    <React.Fragment>
      <div className="fsm-citizen-wrapper">
        {location.pathname.includes("/response") || location.pathname.split("/").includes("check") ? null : <BackButton>{t("CS_COMMON_BACK")}</BackButton>}
        <Switch>
          <PrivateRoute
            path={`${path}/sample`}
            component={() =>
             <div>Sample Screen</div>
            }
          />
        </Switch>
      </div>
    </React.Fragment>
  );
};

export default CitizenApp;
