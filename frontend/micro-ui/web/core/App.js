import { initLibraries } from "@egovernments/digit-ui-libraries";
import { DigitUI } from "@egovernments/digit-ui-module-core";
import { initPGRComponents, PGRReducers } from "@egovernments/digit-ui-module-pgr";
import { initUtilitiesComponents } from "@egovernments/digit-ui-module-utilities";
import { initWorkbenchComponents } from "@egovernments/digit-ui-module-workbench";
import React from "react";
import { UICustomizations } from "./Customisations/UICustomizations";

window.contextPath = window?.globalConfigs?.getConfig("CONTEXT_PATH");

const enabledModules = [
  "DSS",
  "NDSS",
  "Utilities",
  "Engagement",
  "Workbench",
  "PGR",
  "BMC",
  "DEONAR"
];

const moduleReducers = (initData) => ({
  initData, pgr: PGRReducers(initData),
});

const initDigitUI = () => {
  window.Digit.ComponentRegistryService.setupRegistry({});
  window.Digit.Customizations = {
    PGR: {},
    commonUiConfig: UICustomizations,
  };
  initUtilitiesComponents();
  initWorkbenchComponents();
  initPGRComponents();
};

initLibraries().then(() => {
  initDigitUI();
});

function App() {
  window.contextPath = window?.globalConfigs?.getConfig("CONTEXT_PATH");
  const stateCode =
    window.globalConfigs?.getConfig("STATE_LEVEL_TENANT_ID") ||
    process.env.REACT_APP_STATE_LEVEL_TENANT_ID;
  if (!stateCode) {
    return <h1>stateCode is not defined</h1>;
  }
  return (
    <DigitUI
      stateCode={stateCode}
      enabledModules={enabledModules}
      moduleReducers={moduleReducers}
      defaultLanding="employee"
    />
  );
}

export default App;
