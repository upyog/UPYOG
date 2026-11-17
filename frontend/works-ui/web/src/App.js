import React from "react";

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

import { initReceiptsComponents, ReceiptsModule } from "@upyog/digit-ui-module-receipts";

import { initEngagementComponents } from "@upyog/digit-ui-module-engagement";
import { FinanceModule, FinanceComponents } from "@nudmcdgnpm/upyog-ui-module-finance";
import { initReportsComponents } from "@nudmcdgnpm/digit-ui-module-reports";
import { WORKSComponents } from "@nudmcdgnpm/upyog-ui-module-works";


// import "leaflet/dist/leaflet.css";
// import "leaflet-draw/dist/leaflet.draw.css";

initLibraries();

const enabledModules = [

  "Payment",
  "QuickPayLinks",
  "DSS",
  "NDSS",
  "HRMS",
  "Receipts",
  "Engagement",
  "Reports",
  "Bills",
  "Finance",
  "WORKS"
];
window.Digit.ComponentRegistryService.setupRegistry({
  ...paymentConfigs,
  PaymentModule,
  PaymentLinks,
  HRMSModule,
  ReceiptsModule,
  FinanceModule,
  ...FinanceComponents,
  ...WORKSComponents,
});

initDSSComponents();
initHRMSComponents();
initReceiptsComponents();
initEngagementComponents();
initReportsComponents();
// initCustomisationComponents();

const moduleReducers = (initData) => ({
  // pgr: PGRReducers(initData),
  // ndc: NDCReducers(initData),
  // challan: ChallanReducers(initData),
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
