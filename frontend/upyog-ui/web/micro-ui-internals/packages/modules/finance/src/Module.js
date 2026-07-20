import React from "react";
import EmployeeApp from "./pages";

/**
 * FinanceModule – top-level module component registered in UPYOG's
 * ComponentRegistryService under the key "FinanceModule".
 *
 * AppModules.js calls:
 *   Digit.ComponentRegistryService.getComponent(`${code}Module`)
 * where code = "Finance" (from MDMS citymodule). So the export name MUST be "FinanceModule".
 */
export const FinanceModule = ({ stateCode, userType }) => {
  const moduleCode = "Finance";
  const language = Digit.StoreData.getCurrentLanguage();
  const { isLoading, data: store } = Digit.Services.useStore({
    stateCode,
    moduleCode,
    language,
  });
  const { path, url } = Digit.Hooks.useModuleBasePath();

  if (userType === "employee") {
    return <EmployeeApp path={path} url={url} userType="employee" />;
  }

  return null;
};

/**
 * Components to register in UPYOG's ComponentRegistryService.
 *
 * - FinanceModule: looked up by AppModules as `${code}Module` → "FinanceModule"
 *
 * Both keys are derived from MDMS citymodule code = "Finance".
 */


export const FinanceComponents = {
  FinanceModule
};