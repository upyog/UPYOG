import React from "react";

import {
  initPGRComponents,
  PGRReducers,
} from "@demodigit/digit-ui-module-pgr";
import { initDSSComponents } from "@demodigit/digit-ui-module-dss";
import {
  PaymentModule,
  PaymentLinks,
  paymentConfigs,
} from "@demodigit/digit-ui-module-common";
import { DigitUI } from "@demodigit/digit-ui-module-core";
import { initLibraries } from "@upyog/digit-ui-libraries";
import { initWSComponents } from "@demodigit/digit-ui-module-ws";
import { initCustomisationComponents } from "./Customisations";
import { initCommonPTComponents } from "@demodigit/digit-ui-module-commonpt";
import { ASSETComponents, ASSETLinks, ASSETModule } from "@demodigit/upyog-ui-module-asset";
// import { initReportsComponents } from "@egovernments/digit-ui-module-reports";
import {
  HRMSModule,
  initHRMSComponents,
} from "@demodigit/digit-ui-module-hrms";

initLibraries();

const enabledModules = [
  "PGR",
  "Payment",
  "CommonPT",
  "WS",
  "ASSET",
  "HRMS"
];
window.Digit.ComponentRegistryService.setupRegistry({
  ...paymentConfigs,
  PaymentModule,
  PaymentLinks,
  ASSETModule,
  ASSETLinks,
  HRMSModule,
  ...ASSETComponents,
});

initPGRComponents();
initDSSComponents();
initWSComponents();
initCommonPTComponents();
initHRMSComponents();
// initReportsComponents();
initCustomisationComponents();

const moduleReducers = (initData) => ({
  pgr: PGRReducers(initData),
});

function App() {
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
    />
  );
}

export default App;
