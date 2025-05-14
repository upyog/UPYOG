import { AppContainer, BackButton, PrivateRoute, BreadCrumb } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { Route, Switch, useRouteMatch, useLocation } from "react-router-dom";
import { useTranslation } from "react-i18next";
import Inbox from "./Inbox";

/** The Main routes component for the employee side
 * Contains routes for every page there is to redirect in the employee side
 * Contains breadcrumbs for each page
 */
const EmployeeApp = () => {
  const { path, url, ...match } = useRouteMatch();
  const inboxInitialState = {
    searchParams: {
      uuid: { code: "ASSIGNED_TO_ALL", name: "ES_INBOX_ASSIGNED_TO_ALL" },
      services: ["PGRAI"],
    },
  };

 
  const ComplaintDetails = Digit?.ComponentRegistryService?.getComponent("PGRAIApplicationDetails");
  const PGRAICreate = Digit?.ComponentRegistryService?.getComponent("PGRAICreate");
  return (
    <span className={"pgr-ai-citizen"}style={{width:"50%"}}>
      <Switch>
        <AppContainer>
          <BackButton style={{marginTop:"15px"}}>Back</BackButton>
          <PrivateRoute path={`${path}/complaint/create`} component={PGRAICreate} />
          <PrivateRoute
            path={`/digit-ui/employee/pgrai/inbox`}
            component={() => (
              <Inbox
                useNewInboxAPI={true}
                parentRoute={path}
                businessService="pgrai"
                filterComponent="PGRAI_INBOX_FILTER"
                initialStates={inboxInitialState}
                isInbox={true}
              />
            )}
          />
          <PrivateRoute path={`${path}/complaint-details/:id`} component={() => <ComplaintDetails parentRoute={path} />} />
        </AppContainer>
      </Switch>
    </span>
  );
};

export default EmployeeApp;