import { AppContainer, BackButton, PrivateRoute, BreadCrumb } from "@upyog/digit-ui-react-components";
import React from "react";
import { Route, Switch, useRouteMatch, useLocation } from "react-router-dom";
import { useTranslation } from "react-i18next";
import Inbox from "./Inbox";
import SearchApp from "./SearchApp";

/** The Main routes component for the employee side
 * Contains routes for every page there is to redirect in the employee side
 * Contains breadcrumbs for each page
 */
const EmployeeApp = () => {
  const { path, url, ...match } = useRouteMatch();
  const { t } = useTranslation();
  const location = useLocation();
  const isMobile = false

  const inboxInitialState = {
    searchParams: {
      uuid: { code: "ASSIGNED_TO_ALL", name: "ES_INBOX_ASSIGNED_TO_ALL" },
      services: ["street-vending"],
      status: null,
      vendingType: null,
      vendingZone: null
    },
  };

 
  const SVEmpCreate = Digit?.ComponentRegistryService?.getComponent("SVEmpCreate");
  const SVApplicationDetails = Digit?.ComponentRegistryService?.getComponent("SVApplicationDetails")
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
                initialStates={inboxInitialState}
                isInbox={true}
              />
            )}
          />
          <PrivateRoute path={`${path}/application-details/:id`} component={() => <SVApplicationDetails parentRoute={path} />} />
          <PrivateRoute path={`${path}/applicationsearch/application-details/:id`} component={() => <SVApplicationDetails parentRoute={path} />} />
          <PrivateRoute path={`${path}/my-applications`} component={(props) => <SearchApp {...props} parentRoute={path} />} />
        </AppContainer>
      </Switch>
    </span>
  );
};

export default EmployeeApp;