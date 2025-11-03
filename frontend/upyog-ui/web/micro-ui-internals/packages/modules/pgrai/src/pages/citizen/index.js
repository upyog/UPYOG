import { AppContainer, BackButton, PrivateRoute } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { Route, Switch, useRouteMatch } from "react-router-dom";
import { shouldHideBackButton } from "../../utils";
import { useTranslation } from "react-i18next";

const hideBackButtonConfig = [];

const App = () => {
  const { path, url, ...match } = useRouteMatch();
  const { t } = useTranslation();
  const PGRAICreate = Digit?.ComponentRegistryService?.getComponent("PGRAICreate");
  const PGRAIApplicationDetails = Digit?.ComponentRegistryService?.getComponent("PGRAIMyApplications");
  const PGRApplicationDetails = Digit?.ComponentRegistryService?.getComponent("PGRApplicationDetails");
//  to show back button on top left of the page in order to go back to previous page
//this has been added in order show my bookings page
  return (
    <span className={"ads-citizen"}style={{width:"100%"}}>
      <Switch>
        <AppContainer>
          {!shouldHideBackButton(hideBackButtonConfig) ? <BackButton>Back</BackButton> : ""}
         <PrivateRoute path={`${path}/fileGrievance`} component={PGRAICreate}/>
         <PrivateRoute path={`${path}/myGrievance`} component={PGRAIApplicationDetails}></PrivateRoute>
         <PrivateRoute path={`${path}/application/:acknowledgementIds/:tenantId`} component={PGRApplicationDetails}></PrivateRoute>


        </AppContainer>
      </Switch>
    </span>
  );
};

export default App;