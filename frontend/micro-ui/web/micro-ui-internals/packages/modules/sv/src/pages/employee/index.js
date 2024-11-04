/** The Main routes component for the employee side
 * Contains routes for every page there is to redirect in the employee side
 * Contains breadcrumbs for each page
 */

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

  /** Displays the path above the form
   * Can redirect the previous components on clicking
   */
  const SVBreadCrumbs = ({ location }) => {
    const { t } = useTranslation();
    const search = useLocation().search;
    const fromScreen = new URLSearchParams(search).get("from") || null;
    const { from : fromScreen2 } = Digit.Hooks.useQueryParams();
    const crumbs = [
      {
        path: "/digit-ui/employee",
        content: t("ES_COMMON_HOME"),
        show: true,
      },
      {
        path: "/digit-ui/employee/sv/inbox",
        content: t("ES_TITLE_INBOX"),
        show: location.pathname.includes("sv/inbox") ? true : false,
      },
      {
        path: "/digit-ui/employee/sv/my-applications",
        content: t("ES_COMMON_APPLICATION_SEARCH"),
        show: location.pathname.includes("sv/my-applications") || location.pathname.includes("/sv/applicationsearch/application-details/") ? true : false,
      },
    ];
  
    return <BreadCrumb style={isMobile?{display:"flex"}:{}}  spanStyle={{maxWidth:"min-content"}} crumbs={crumbs} />;
  }

  const isRes = window.location.href.includes("sv/response");
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
                // initialStates={inboxInitialState}
                isInbox={true}
              />
            )}
          />
          <PrivateRoute path={`${path}/applicationsearch/application-details/:id`} component={() => <SVApplicationDetails parentRoute={path} />} />
          <PrivateRoute path={`${path}/my-applications`} component={(props) => <SearchApp {...props} parentRoute={path} />} />
        </AppContainer>
      </Switch>
    </span>
  );
};

export default EmployeeApp;345678