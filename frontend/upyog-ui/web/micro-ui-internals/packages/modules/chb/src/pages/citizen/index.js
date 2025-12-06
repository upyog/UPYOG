import { AppContainer, BackButton, PrivateRoute } from "@upyog/digit-ui-react-components";
import React from "react";
import { Route, Switch, useRouteMatch } from "react-router-dom";
import { shouldHideBackButton } from "../../utils";
import { useTranslation } from "react-i18next";

const hideBackButtonConfig = [
  { screenPath: "bookHall/acknowledgement" },
  { screenPath: "editbookHall/acknowledgement" },

];

/**
 * Citizen App Component for CHB Module
 * 
 * This component serves as the entry point for the citizen-facing CHB module. It manages routing between different pages, such as creating a booking, viewing application details, and managing bookings.
 * 
 * Features:
 * - Configures routes for various citizen-facing pages in the CHB module.
 * - Displays a back button conditionally based on the current route.
 * - Integrates with the Digit Component Registry Service to dynamically load components.
 * 
 * Hooks:
 * - `useRouteMatch`: Provides information about the current route, including `path` and `url`.
 * - `useTranslation`: Provides the `t` function for internationalization.
 * 
 * Variables:
 * - `hideBackButtonConfig`: Array of route configurations where the back button should be hidden.
 * - `inboxInitialState`: Initial state for the inbox, including search parameters.
 * - `CHBCreate`: Component for creating a new booking, dynamically loaded from the Digit Component Registry Service.
 * - `CHBApplicationDetails`: Component for viewing application details, dynamically loaded from the Digit Component Registry Service.
 * - `CHBMyApplications`: Component for managing citizen bookings, dynamically loaded from the Digit Component Registry Service.
 * 
 * Logic:
 * - Determines whether to display the back button based on the current route using the `shouldHideBackButton` utility function.
 * - Configures private routes for:
 *    - Creating a new booking (`/bookHall`).
 *    - Viewing citizen bookings (`/myBookings`).
 *    - Viewing application details (`/application/:acknowledgementIds/:tenantId`).
 *    - Searching for bookings (`/bookHall/search`).
 * 
 * Returns:
 * - A `Switch` component that renders the appropriate page based on the current route.
 * - An `AppContainer` component that wraps the content and conditionally displays the back button.
 */
const App = () => {
  const { path, url, ...match } = useRouteMatch();
  const { t } = useTranslation();
  

  const CHBCreate = Digit?.ComponentRegistryService?.getComponent("CHBCreate");
  const CHBApplicationDetails = Digit?.ComponentRegistryService?.getComponent("CHBApplicationDetails");
  const CHBMyApplications = Digit?.ComponentRegistryService?.getComponent("CHBMyApplications");
  const CHBMapView = Digit?.ComponentRegistryService?.getComponent("CHBMapView");
 
  return (
    <span className={"chb-citizen"}style={{width:"100%"}}>
      <Switch>
        <AppContainer>
          {!shouldHideBackButton(hideBackButtonConfig) ? <BackButton>Back</BackButton> : ""}
          <PrivateRoute path={`${path}/bookHall`} component={CHBCreate} />
          <PrivateRoute path={`${path}/myBookings`} component={CHBMyApplications}></PrivateRoute>
          <PrivateRoute path={`${path}/application/:acknowledgementIds/:tenantId`} component={CHBApplicationDetails}></PrivateRoute>
          <PrivateRoute path={`${path}/map`} component={CHBMapView}></PrivateRoute>
          {/* <PrivateRoute path={`${path}/bookHall/search`} component={(props) => <Search {...props} t={t} parentRoute={path} />} />  */}
        </AppContainer>
      </Switch>
    </span>
  );
};

export default App;