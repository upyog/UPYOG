import React from "react";
import {PaymentModule} from "@upyog/digit-ui-module-common";
import { StreetVendingUI } from "@upyog/digit-ui-module-core";
import { initLibraries } from "@nudmcdgnpm/digit-ui-libraries";
import { SVComponents, SVLinks, SVModule } from "@nudmcdgnpm/upyog-ui-module-sv";
import { initEngagementComponents } from "@upyog/digit-ui-module-engagement";

/**
 * The `App` component initializes and renders the Street Vending UI application.
 * 
 * - Initializes required libraries and engagement components.
 * - Sets up the `ComponentRegistryService` with modules and components.
 * - Defines the enabled modules for the application.
 * - Retrieves the state code from global configurations or defaults to "pg".
 * - Renders the `StreetVendingUI` component with the state code, enabled modules, and reducers.
 */

initLibraries();

const enabledModules = [
  "Payment",
  "QuickPayLinks",
  "Engagement",
  "SV"
];
window.Digit.ComponentRegistryService.setupRegistry({
  PaymentModule,
  SVModule,
  SVLinks,
  ...SVComponents,
});

const moduleReducers = (initData) => ({
  // pgr: PGRReducers(initData),
});

initEngagementComponents();
function App() {
  console.log("App component loaded");
  const stateCode =
    window.globalConfigs?.getConfig("STATE_LEVEL_TENANT_ID") ||
    "pg"; // Default state code
  console.log("State code:", stateCode);
  if (!stateCode) {
    return <h1>stateCode is not defined</h1>;
  }
  return (
    <StreetVendingUI
      stateCode={stateCode}
      enabledModules={enabledModules}
      moduleReducers={moduleReducers}
    />
  );
}

export default App;
