import { AppContainer, BackButton, PrivateRoute } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { Route, Switch, useRouteMatch } from "react-router-dom";
// import { shouldHideBackButton } from "../../utils";
import { useTranslation } from "react-i18next";

// const hideBackButtonConfig = [
//   { screenPath: "bookHall/acknowledgement" },
//   { screenPath: "editbookHall/acknowledgement" },

// ];

const App = () => {
  const { path, url, ...match } = useRouteMatch();
  const { t } = useTranslation();
  const inboxInitialState = {
    searchParams: {},
  };

  const FNOCCreate = Digit?.ComponentRegistryService?.getComponent("Create");
  // const CHBApplicationDetails = Digit?.ComponentRegistryService?.getComponent("CHBApplicationDetails");
  // const CHBMyApplications = Digit?.ComponentRegistryService?.getComponent("CHBMyApplications");
 
  return (
    <span className={"chb-citizen"}style={{width:"100%"}}>
      <Switch>
        <AppContainer>
          {/* {!shouldHideBackButton(hideBackButtonConfig) ? <BackButton>Back</BackButton> : ""} */}
          <PrivateRoute path={`${path}/apply`} component={FNOCCreate} />
          <PrivateRoute path={`${path}/my-applications`} component={FNOCCreate}></PrivateRoute>
        </AppContainer>
      </Switch>
    </span>
  );
};

export default App;