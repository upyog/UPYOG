import React from "react";
import {PaymentModule} from "@upyog/digit-ui-module-common";
import { CndUI } from "@upyog/digit-ui-module-core";
import { initLibraries } from "@nudmcdgnpm/digit-ui-libraries";
import { initBillsComponents } from "@upyog/digit-ui-module-bills";
import { CNDComponents, CNDLinks, CNDModule } from "@nudmcdgnpm/upyog-ui-module-cnd";


initLibraries();

const enabledModules = [
  "Payment",
  "QuickPayLinks",
  "CND"
];
window.Digit.ComponentRegistryService.setupRegistry({
  PaymentModule,
  ...CNDComponents, 
  CNDLinks, 
  CNDModule
});


initBillsComponents();

const moduleReducers = (initData) => ({
  // pgr: PGRReducers(initData),
});


function App() {
  const stateCode =
    window.globalConfigs?.getConfig("STATE_LEVEL_TENANT_ID") ||
    process.env.REACT_APP_STATE_LEVEL_TENANT_ID;
  if (!stateCode) {
    return <h1>stateCode is not defined</h1>;
  }
  return (
    <CndUI
      stateCode={stateCode}
      enabledModules={enabledModules}
      moduleReducers={moduleReducers}
    />
  );
}

export default App;
