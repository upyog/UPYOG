import React from "react";
import ReactDOM from "react-dom";

import { initLibraries } from "@nudmcdgnpm/digit-ui-libraries";
import { PaymentModule } from "@upyog/digit-ui-module-common";
import { CndUI } from "@upyog/digit-ui-module-core";
import { initBillsComponents, BillsModule } from "@upyog/digit-ui-module-bills";
import "@nudmcdgnpm/cnd-css";
import { CNDComponents, CNDLinks, CNDModule } from "@nudmcdgnpm/upyog-ui-module-cnd";

import { CndConstants } from "./CndConstants";

var Digit = window.Digit || {};

const enabledModules = [
  "Payment",
  "QuickPayLinks",
  "CND"
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
  PaymentModule,
  BillsModule,
  ...CNDComponents, 
  CNDLinks, 
  CNDModule
  });

  // initBillsComponents();

  const moduleReducers = (initData) => ({
    // pgr: PGRReducers(initData),
  });

  window.Digit.Customizations = {
  };

  const stateCode = window?.globalConfigs?.getConfig("STATE_LEVEL_TENANT_ID") || CndConstants.Tenant;
  initTokens(stateCode);

  // const registry = window?.Digit.ComponentRegistryService.getRegistry();
  ReactDOM.render(
  <CndUI 
  stateCode={stateCode} 
  enabledModules={enabledModules} 
  moduleReducers={moduleReducers} 
  />, 
  document.getElementById("root"));
};

initLibraries().then(() => {
  initDigitUI();
});
