import { AppContainer, BackButton, PrivateRoute, BreadCrumb } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { Route, Switch, useRouteMatch, useLocation } from "react-router-dom";
import { useTranslation } from "react-i18next";
import Inbox from "./Inbox";
import SearchApp from "./SearchApp";
import { cndStyles } from "../../utils/cndStyles";

/** The Main routes component for the employee side
 * Contains routes for every page there is to redirect in the employee side
 * Contains breadcrumbs for each page
 */
const EmployeeApp = () => {
  const { path, url, ...match } = useRouteMatch();

  const inboxInitialState = {
    searchParams: {
      uuid: { code: "ASSIGNED_TO_ALL", name: "ES_INBOX_ASSIGNED_TO_ALL" },
      services: ["cnd"],
      status: null,
    },
  };

  const ApplicationDetails = Digit?.ComponentRegistryService?.getComponent("ApplicationDetails");
  const EditCreate = Digit?.ComponentRegistryService?.getComponent("EditCreate");
  const EditResponse = Digit?.ComponentRegistryService?.getComponent("EditSubmissionResponse");
  const FacilityCentreCreationDetails = Digit?.ComponentRegistryService?.getComponent("FacilityCentreCreationDetails");
  const FacilitySubmissionResponse = Digit?.ComponentRegistryService?.getComponent("FacilitySubmissionResponse");
  const CndCreate = Digit?.ComponentRegistryService?.getComponent("CndCreate");

  return (
    <span className={"cnd-citizen"} style={cndStyles.wasteQuantityCitizen}>
      <Switch>
        <AppContainer>
          <BackButton style={cndStyles.backButton}>Back</BackButton>
          <PrivateRoute
            path={`${path}/inbox`}
            component={() => (
              <Inbox
                useNewInboxAPI={true}
                parentRoute={path}
                businessService="cnd"
                filterComponent="CND_INBOX_FILTERS"
                initialStates={inboxInitialState}
                isInbox={true}
              />
            )}
          />
          <PrivateRoute path={`${path}/apply`} component={CndCreate} />
          <PrivateRoute path={`${path}/application-details/:id`} component={() => <ApplicationDetails parentRoute={path} />} />
          <PrivateRoute path={`${path}/applicationsearch/application-details/:id`} component={() => <ApplicationDetails parentRoute={path} />} />
          <PrivateRoute path={`${path}/cnd-service/edit/:id`} component={() => <EditCreate parentUrl={url} />} />
          <PrivateRoute path={`${path}/edit-response`} component={(props) => <EditResponse {...props} parentRoute={path} />} />
          <PrivateRoute path={`${path}/my-request`} component={(props) => <SearchApp {...props} parentRoute={path} />} />
          <PrivateRoute path={`${path}/cnd-service/facility-centre/:id`} component={() => <FacilityCentreCreationDetails parentUrl={url} />} />
          <PrivateRoute path={`${path}/facility-response`} component={(props) => <FacilitySubmissionResponse {...props} parentRoute={path} />} />

        </AppContainer>
      </Switch>
    </span>
  );
};

export default EmployeeApp;