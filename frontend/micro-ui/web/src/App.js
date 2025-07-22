import React from "react";

import {
  initPGRComponents,
  PGRReducers,
} from "@demodigit/digit-ui-module-pgr";
import { initFSMComponents } from "@demodigit/digit-ui-module-fsm";
import { PTComponents } from "@demodigit/digit-ui-module-pt";
import { MCollectModule, MCollectLinks, initMCollectComponents } from "@demodigit/digit-ui-module-mcollect";
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
import {
  initTLComponents,
} from "@demodigit/digit-ui-module-tl";
import { initOBPSComponents } from "@demodigit/digit-ui-module-obps";
import { initNOCComponents } from "@demodigit/digit-ui-module-noc";





// import { initReportsComponents } from "@upyog/digit-ui-module-reports";

initLibraries();

const enabledModules = [
  "PGR",
  "FSM",
  "Payment",
  "CommonPT",
  "WS",
  "ASSET",
  "HRMS",
  "MCollect",
  "PT",
  "TL",
  "OBPS",
];
window.Digit.ComponentRegistryService.setupRegistry({
  ...paymentConfigs,
  PaymentModule,
  PaymentLinks,
  ...PTComponents,
  MCollectLinks,
  MCollectModule,
  ASSETModule,
  ASSETLinks,
  HRMSModule,
  ...ASSETComponents,
});

initPGRComponents();
initFSMComponents();
initDSSComponents();
initMCollectComponents();
initTLComponents();
initOBPSComponents();
initNOCComponents();
initWSComponents();
initCommonPTComponents();
initHRMSComponents();
// initReportsComponents();
// initCustomisationComponents();

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
