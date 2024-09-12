import React from "react";
import ReactDOM from "react-dom";

import { initLibraries } from "@nudmcdgnpm/digit-ui-libraries";
import { PGRReducers } from "@nudmcdgnpm/digit-ui-module-pgr";
import { PTModule, PTLinks, PTComponents } from "@nudmcdgnpm/digit-ui-module-pt";
import { MCollectModule, MCollectLinks } from "@nudmcdgnpm/digit-ui-module-mcollect";
// import { TLModule, TLLinks } from "@nudmcdgnpm/digit-ui-module-tl";
import { initFSMComponents } from "@nudmcdgnpm/digit-ui-module-fsm";
import { initPGRComponents } from "@nudmcdgnpm/digit-ui-module-pgr";
import { initDSSComponents } from "@nudmcdgnpm/digit-ui-module-dss";
import { initHRMSComponents } from "@nudmcdgnpm/digit-ui-module-hrms";
import { initReceiptsComponents, ReceiptsModule } from "@nudmcdgnpm/digit-ui-module-receipts";
// import { initReportsComponents } from "@egovernments/digit-ui-module-reports";
import { initMCollectComponents } from "@nudmcdgnpm/digit-ui-module-mcollect";
import { initTLComponents } from "@nudmcdgnpm/digit-ui-module-tl";
import { PaymentModule, PaymentLinks, paymentConfigs } from "@nudmcdgnpm/digit-ui-module-common";
import { HRMSModule } from "@nudmcdgnpm/digit-ui-module-hrms";
import { initOBPSComponents } from "@nudmcdgnpm/digit-ui-module-obps";
import { initEngagementComponents } from "@nudmcdgnpm/digit-ui-module-engagement";
import { initNOCComponents } from "@nudmcdgnpm/digit-ui-module-noc";
import { initWSComponents } from "@nudmcdgnpm/digit-ui-module-ws";
import { DigitUI } from "@nudmcdgnpm/digit-ui-module-core";
import { initCommonPTComponents } from "@nudmcdgnpm/digit-ui-module-commonpt";
import { initBillsComponents, BillsModule } from "@nudmcdgnpm/digit-ui-module-bills";

import { PTRModule, PTRLinks, PTRComponents } from "@nudmcdgnpm/upyog-ui-module-ptr";

import { EWModule, EWLinks, EWComponents } from "@nudmcdgnpm/upyog-ui-module-ew";

import { ASSETComponents, ASSETLinks, ASSETModule } from "@nudmcdgnpm/upyog-ui-module-asset";

import { COMMONMODULELinks, COMMONMODULEModule, COMMONMODULEComponents } from "@nudmcdgnpm/upyog-ui-module-cm";


// import {initCustomisationComponents} from "./customisations";

// import { PGRModule, PGRLinks } from "@egovernments/digit-ui-module-pgr";
// import { Body, TopBar } from "@nudmcdgnpm/digit-ui-react-components";
// import "@nudmcdgnpm/upyog-css/example/index.css";
import "@nudmcdgnpm/upyog-css"
// import * as comps from "@nudmcdgnpm/digit-ui-react-components";

// import { subFormRegistry } from "@nudmcdgnpm/digit-ui-libraries";
import { CHBModule, CHBLinks, CHBComponents } from "@nudmcdgnpm/upyog-ui-module-chb";

import { pgrCustomizations, pgrComponents } from "./pgr";

var Digit = window.Digit || {};

const enabledModules = [
  "PGR",
  "FSM",
  "Payment",
  "PT",
  "QuickPayLinks",
  "DSS",
  "MCollect",
  "HRMS",
  "TL",
  "Receipts",
  "Reports",
  "OBPS",
  "Engagement",
  "NOC",
  "WS",
  "CommonPT",
  "NDSS",
  "Bills",
  "SW",
  "BillAmendment",
  "FireNoc",
  "Birth",
  "Death",
  "PTR",
  "ASSET",
  "EW",
  "CHB",
  "COMMONMODULE"
];

const initTokens = (stateCode) => {
  const userType = window.sessionStorage.getItem("userType") || process.env.REACT_APP_USER_TYPE || "CITIZEN";

  const token = window.localStorage.getItem("token")|| process.env[`REACT_APP_${userType}_TOKEN`];
 
  const citizenInfo = window.localStorage.getItem("Citizen.user-info")
 
  const citizenTenantId = window.localStorage.getItem("Citizen.tenant-id") || stateCode;

  const employeeInfo = window.localStorage.getItem("Employee.user-info");
  const employeeTenantId = window.localStorage.getItem("Employee.tenant-id");

  const userTypeInfo = userType === "CITIZEN" || userType === "QACT" ? "citizen" : "employee";
  window.Digit.SessionStorage.set("user_type", userTypeInfo);
  window.Digit.SessionStorage.set("userType", userTypeInfo);

  if (userType !== "CITIZEN") {
    window.Digit.SessionStorage.set("User", { access_token: token, info: userType !== "CITIZEN" ? JSON.parse(employeeInfo) : citizenInfo });
  } else {
    // if (!window.Digit.SessionStorage.get("User")?.extraRoleInfo) window.Digit.SessionStorage.set("User", { access_token: token, info: citizenInfo });
  }

  window.Digit.SessionStorage.set("Citizen.tenantId", citizenTenantId);

  if (employeeTenantId && employeeTenantId.length) window.Digit.SessionStorage.set("Employee.tenantId", employeeTenantId);
};

const initDigitUI = () => {
  window?.Digit.ComponentRegistryService.setupRegistry({
    ...pgrComponents,
    PaymentModule,
    ...paymentConfigs,
    PaymentLinks,
    PTModule,
    PTLinks,
    ...PTComponents,
    MCollectLinks,
    MCollectModule,
    HRMSModule,
    ReceiptsModule,
    BillsModule,
    // TLModule,
    // TLLinks,
    PTRModule,
    PTRLinks,
    ...PTRComponents,
    ASSETModule,
    ASSETLinks,
    ...ASSETComponents,
    EWModule,
    EWLinks,
    ...EWComponents,
    CHBModule, 
    CHBLinks, 
    ...CHBComponents,
    COMMONMODULELinks,
    COMMONMODULEModule,
    ...COMMONMODULEComponents,
  });

  initFSMComponents();
  initPGRComponents();
  initDSSComponents();
  initMCollectComponents();
  initHRMSComponents();
  initTLComponents();
  initReceiptsComponents();
  // initReportsComponents();
  initOBPSComponents();
  initEngagementComponents();
  initNOCComponents();
  initWSComponents();
  initCommonPTComponents();
  initBillsComponents();

  // initCustomisationComponents();

  const moduleReducers = (initData) => ({
    pgr: PGRReducers(initData),
  });

  window.Digit.Customizations = {
    PGR: pgrCustomizations,
    TL: {
      customiseCreateFormData: (formData, licenceObject) => licenceObject,
      customiseRenewalCreateFormData: (formData, licenceObject) => licenceObject,
      customiseSendbackFormData: (formData, licenceObject) => licenceObject,
    },
  };

  const stateCode = window?.globalConfigs?.getConfig("STATE_LEVEL_TENANT_ID") || "pb";
  initTokens(stateCode);

  const registry = window?.Digit.ComponentRegistryService.getRegistry();
  ReactDOM.render(<DigitUI stateCode={stateCode} enabledModules={enabledModules} moduleReducers={moduleReducers} />, document.getElementById("root"));
};

initLibraries().then(() => {
  initDigitUI();
});
