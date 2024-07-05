import React from "react";

import {
  initPGRComponents,
  PGRReducers,
} from "@nudmcdgnpm/digit-ui-module-pgr";
import { initFSMComponents } from "@nudmcdgnpm/digit-ui-module-fsm";
import {
  PTModule,
  PTLinks,
  PTComponents,
} from "@nudmcdgnpm/digit-ui-module-pt";
import { MCollectModule, MCollectLinks, initMCollectComponents } from "@nudmcdgnpm/digit-ui-module-mcollect";
import { initDSSComponents } from "@nudmcdgnpm/digit-ui-module-dss";
import {
  PaymentModule,
  PaymentLinks,
  paymentConfigs,
} from "@nudmcdgnpm/digit-ui-module-common";
import { DigitUI } from "@nudmcdgnpm/digit-ui-module-core";
import { initLibraries } from "@nudmcdgnpm/digit-ui-libraries";
import {
  HRMSModule,
  initHRMSComponents,
} from "@nudmcdgnpm/digit-ui-module-hrms";
import {
  TLModule,
  TLLinks,
  initTLComponents,
} from "@nudmcdgnpm/digit-ui-module-tl";
import {
  PTRModule,
  PTRLinks,
  PTRComponents,
} from "@nudmcdgnpm-niua/upyog-ui-module-ptr";

import { 
  EWModule, 
  EWLinks, 
  EWComponents }
  from "@nudmcdgnpm-niua/upyog-ui-module-ew";

import {  
  ASSETModule, 
  ASSETLinks,
  ASSETComponents } from "@nudmcdgnpm-niua/upyog-ui-module-asset";
import { initReceiptsComponents, ReceiptsModule } from "@nudmcdgnpm/digit-ui-module-receipts";
import { initOBPSComponents } from "@nudmcdgnpm/digit-ui-module-obps";
import { initNOCComponents } from "@nudmcdgnpm/digit-ui-module-noc";
import { initEngagementComponents } from "@nudmcdgnpm/digit-ui-module-engagement";
import { initWSComponents } from "@nudmcdgnpm/digit-ui-module-ws";
import { initCustomisationComponents } from "./Customisations";
import { initCommonPTComponents } from "@nudmcdgnpm/digit-ui-module-commonpt";
import { initBillsComponents } from "@nudmcdgnpm/digit-ui-module-bills";
import {CHBModule,CHBLinks,CHBComponents} from "@nudmcdgnpm-niua/upyog-ui-module-chb";

initLibraries();

const enabledModules = [
  "PGR",
  "FSM",
  "Payment",
  "PT",
  "QuickPayLinks",
  "DSS",
  "NDSS",
  "MCollect",
  "HRMS",
  "TL",
  "Receipts",
  "OBPS",
  "NOC",
  "Engagement",
  "CommonPT",
  "WS",
  "Reports",
  "Bills",
  "SW",
  "BillAmendment",
  "FireNoc",
  "Birth",
  "Death",
  "PTR",
  "ASSET",
  "EW",
  "CHB"
];
window.Digit.ComponentRegistryService.setupRegistry({
  ...paymentConfigs,
  PTModule,
  PTLinks,
  PaymentModule,
  PaymentLinks,
  ...PTComponents,
  MCollectLinks,
  MCollectModule,
  HRMSModule,
  TLModule,
  TLLinks,
  ReceiptsModule,
  PTRModule,
  PTRLinks,
  ...PTRComponents,
  ASSETLinks,
  ASSETModule,
  ...ASSETComponents,
  EWModule,
  EWLinks,
  ...EWComponents,
  CHBModule,
  CHBLinks,
  ...CHBComponents
});

initPGRComponents();
initFSMComponents();
initDSSComponents();
initMCollectComponents();
initHRMSComponents();
initTLComponents();
initReceiptsComponents();
initOBPSComponents();
initNOCComponents();
initEngagementComponents();
initWSComponents();
initCommonPTComponents();
initBillsComponents();
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
