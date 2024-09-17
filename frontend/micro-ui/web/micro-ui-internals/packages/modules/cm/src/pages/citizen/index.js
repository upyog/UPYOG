import { AppContainer, BackButton, PrivateRoute } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { Route, Switch, useRouteMatch } from "react-router-dom";
import { shouldHideBackButton } from "../../utils";
import { useTranslation } from "react-i18next";

const hideBackButtonConfig = [];

const App = () => {
  const { path, url, ...match } = useRouteMatch();
  const { t } = useTranslation();

  const CMSearchCertificate = Digit?.ComponentRegistryService?.getComponent("CMSearchCertificate");

  return (
    <span className={"citizen"} style={{width:"100%"}}> 
      <Switch>
        <AppContainer>
          {!shouldHideBackButton(hideBackButtonConfig) ? <BackButton>Back</BackButton> : ""}
          <PrivateRoute path={`${path}/cmservice/verify`} component={CMSearchCertificate} />
        </AppContainer>
      </Switch>
    </span>
  );
};

export default App;