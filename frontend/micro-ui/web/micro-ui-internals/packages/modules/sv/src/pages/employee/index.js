import { AppContainer, BackButton, PrivateRoute, BreadCrumb } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { Route, Switch, useRouteMatch, useLocation } from "react-router-dom";
import { useTranslation } from "react-i18next";
import Inbox from "./Inbox";
import SearchApp from "./SearchApp";

const EmployeeApp = () => {
  const { path, url, ...match } = useRouteMatch();
  const { t } = useTranslation();
  const location = useLocation();
  const isMobile = false
  const inboxInitialState = {
    searchParams: {},
  };
  const SVEmpCreate = Digit?.ComponentRegistryService?.getComponent("SVEmpCreate");
  return (
    <span className={"sv-citizen"}style={{width:"100%"}}>
      <Switch>
        <AppContainer>
          <BackButton style={{marginTop:"15px"}}>Back</BackButton>
          <PrivateRoute path={`${path}/apply`} component={SVEmpCreate} />
          <PrivateRoute
            path={`${path}/inbox`}
            component={() => (
              <Inbox
                useNewInboxAPI={true}
                parentRoute={path}
                businessService="sv"
                filterComponent="SV_INBOX_FILTER"
                // initialStates={inboxInitialState}
                isInbox={true}
              />
            )}
          />
          <PrivateRoute path={`${path}/my-applications`} component={(props) => <SearchApp {...props} parentRoute={path} />} />
        </AppContainer>
      </Switch>
    </span>
  );
};

export default EmployeeApp;345678