import React from "react";
import {
  PaymentModule,
  PaymentLinks,
  paymentConfigs,
} from "@upyog/digit-ui-module-common";
import { DigitUI } from "@upyog/digit-ui-module-core";
import { initLibraries } from "@nudmcdgnpm/digit-ui-libraries";
import { initBillsComponents } from "@upyog/digit-ui-module-bills";
import { SVComponents, SVLinks, SVModule } from "@nudmcdgnpm/upyog-ui-module-sv";
import { CNDComponents, CNDLinks, CNDModule } from "@nudmcdgnpm/upyog-ui-module-cnd";


initLibraries();

const enabledModules = [
  "Payment",
  "QuickPayLinks",
  "BillAmendment",
  "SV",
  "CND"
];
window.Digit.ComponentRegistryService.setupRegistry({
  ...paymentConfigs,
  PaymentModule,
  PaymentLinks,
  SVModule,
  SVLinks,
  ...SVComponents,
  ...CNDComponents, 
  CNDLinks, 
  CNDModule
});


initBillsComponents();


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
