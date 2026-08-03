import React from "react";

import {
  initPGRComponents,
  PGRReducers,
} from "@upyog/digit-ui-module-pgr";
import { initFSMComponents } from "@upyog/digit-ui-module-fsm";
import {
  PTModule,
  PTLinks,
  PTComponents,
} from "@upyog/digit-ui-module-pt";
import { MCollectModule, MCollectLinks, initMCollectComponents } from "@upyog/digit-ui-module-mcollect";
import { initDSSComponents } from "@upyog/digit-ui-module-dss";
import {
  PaymentModule,
  PaymentLinks,
  paymentConfigs,
} from "@upyog/digit-ui-module-common";
import { DigitUI } from "@upyog/digit-ui-module-core";
import { initLibraries } from "@upyog/digit-ui-libraries";
import {
  HRMSModule,
  initHRMSComponents,
} from "@upyog/digit-ui-module-hrms";
import {
  TLModule,
  TLLinks,
  initTLComponents,
} from "@upyog/digit-ui-module-tl";
import {
  ChallanGenerationModule,
  initChallanGenerationComponents,
  ChallanReducers,
} from "@nudmcdgnpm/digit-ui-module-challangeneration";
import { initReceiptsComponents, ReceiptsModule } from "@upyog/digit-ui-module-receipts";
import { initOBPSComponents } from "@upyog/digit-ui-module-obps";
import { initNOCComponents } from "@upyog/digit-ui-module-noc";
import { initEngagementComponents } from "@upyog/digit-ui-module-engagement";
import { initWSComponents } from "@upyog/digit-ui-module-ws";
// import { initCustomisationComponents } from "./Customisations";
import { initCommonPTComponents } from "@upyog/digit-ui-module-commonpt";
import { initBillsComponents } from "@upyog/digit-ui-module-bills";
import {
  PTRModule,
  PTRLinks,
  PTRComponents,
} from "@upyog/upyog-ui-module-ptr";
import { ASSETComponents, ASSETLinks, ASSETModule } from "@upyog/upyog-ui-module-asset";

import { 
  EWModule, 
  EWLinks, 
  EWComponents }
  from "@upyog/upyog-ui-module-ew";


import {CHBModule,CHBLinks,CHBComponents} from "@upyog/upyog-ui-module-chb";
import {ADSModule,ADSLinks,ADSComponents} from "@nudmcdgnpm/upyog-ui-module-ads";
import { WTModule, WTLinks, WTComponents } from "@upyog/upyog-ui-module-wt";
import { VENDORComponents, VENDORLinks, VENDORModule } from "@upyog/upyog-ui-module-vendor";
import { PGRAIComponents, PGRAILinks, PGRAIModule } from "@upyog/upyog-ui-module-pgrai";
import { ASSETV2Components, ASSETV2Links, ASSETV2Module } from "@nudmcdgnpm/upyog-ui-module-asset-v2";
import { GISComponents, GISLinks, GISModule } from "@nudmcdgnpm/upyog-ui-module-gis";
import { ESTComponents, ESTLinks, ESTModule } from "@nudmcdgnpm/upyog-ui-module-est";
import { initNDCComponents, NDCReducers } from "@nudmcdgnpm/upyog-ui-module-ndc";
import { GCModule, GCComponents } from "@nudmcdgnpm/upyog-ui-module-gc";
import { FinanceModule, FinanceComponents } from "@nudmcdgnpm/upyog-ui-module-finance";


// import "leaflet/dist/leaflet.css";
// import "leaflet-draw/dist/leaflet.draw.css";

initLibraries();

const enabledModules = [
  "Tqm",
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
  "ADS",
  "EW",
  "CHB",
  "WT",
  "VENDOR",
  "ChallanGeneration",
  "MT",
  "PGRAI",
  "TP",
  "ASSETV2",
   "EST",
  "GIS",
  "NDC",
  "GC",
  "Finance"
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
  ChallanGenerationModule,
  HRMSModule,
  TLModule,
  TLLinks,
  ReceiptsModule,
  PTRModule,
  PTRLinks,
  ...PTRComponents,
  ASSETModule,
  ASSETLinks,
  ...ASSETComponents,
  ADSLinks,
  ADSModule,
  ...ADSComponents,
  EWModule,
  EWLinks,
  ...EWComponents,
  CHBModule,
  CHBLinks,
  ...CHBComponents,
  WTModule,
  WTLinks,
  ...WTComponents,
  VENDORModule,
  VENDORLinks,
  ...VENDORComponents,
  PGRAIModule,
  PGRAILinks,
  ...PGRAIComponents,
  ...ASSETV2Components, 
  ASSETV2Links, 
  ASSETV2Module,
   GISLinks,
    GISModule,
    ...GISComponents,
    ESTModule,
    ESTLinks,
    ...ESTComponents,
    ...GCComponents,
    GCModule,
    FinanceModule,
    ...FinanceComponents,
});

initPGRComponents();
initFSMComponents();
initDSSComponents();
initMCollectComponents();
initChallanGenerationComponents();
initHRMSComponents();
initTLComponents();
initReceiptsComponents();
initOBPSComponents();
initNOCComponents();
initEngagementComponents();
initWSComponents();
initCommonPTComponents();
initBillsComponents();
initNDCComponents();
// initReportsComponents();
// initCustomisationComponents();

const moduleReducers = (initData) => ({
  pgr: PGRReducers(initData),
  ndc: NDCReducers(initData),
  challan: ChallanReducers(initData),
});

function App() {
  const stateCode = window.globalConfigs?.getConfig("STATE_LEVEL_TENANT_ID") || process.env.REACT_APP_STATE_LEVEL_TENANT_ID;
  
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
