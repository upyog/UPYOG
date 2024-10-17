import { AppContainer, BackButton, PrivateRoute } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { Route, Switch, useRouteMatch } from "react-router-dom";
import { shouldHideBackButton } from "../../utils";
import { useTranslation } from "react-i18next";

const hideBackButtonConfig = [];

const App = () => {
  // used the useRoutematch for getting current path and url
  const { path, url, ...match } = useRouteMatch();
  const { t } = useTranslation();

  // Component imported from component registry service
  const CMSearchCertificate = Digit?.ComponentRegistryService?.getComponent("CMSearchCertificate");

  return (
    <span className={"citizen"} style={{width:"100%"}}> 
    {/* Routes to the page components of the module */}
      <Switch>
        <AppContainer>
          {/* Rendered a back button which displays on each component which comes by routing */}
          {!shouldHideBackButton(hideBackButtonConfig) ? <BackButton>Back</BackButton> : ""}
          {/* routes of the components */}
          <PrivateRoute path={`${path}/cmservice/verify`} component={CMSearchCertificate} />
        </AppContainer>
      </Switch>
    </span>
  );
};

export default App;