import React, { useEffect } from "react";
import { Loader } from "@nudmcdgnpm/digit-ui-react-components";
import EmployeeApp from "./pages/employee";
import WORKSCard from "./WORKScard";
import CreateActionPlan from "./pages/employee/CreateActionPlan";
import Inbox from "./pages/employee/Inbox";
import FormBuilder from "./form-builder/FormBuilder";

/**
 * Components exposed to the Digit ComponentRegistry so other modules / configs
 * can resolve them by name.
 */
const componentsToRegister = {
  WorksCreateActionPlan: CreateActionPlan,
  WorksFormBuilder: FormBuilder,
  WorksInbox: Inbox,
};

const addComponentsToRegistry = () => {
  Object.entries(componentsToRegister).forEach(([key, value]) => {
    Digit.ComponentRegistryService.setComponent(key, value);
  });
};

/**
 * WORKSModule
 *
 * Entry point for the Works module. Core resolves it as `${code}Module`
 * where code = "WORKS" (from MDMS tenant.citymodule).
 * Works is employee-only, so citizens get nothing.
 */
export const WORKSModule = ({ stateCode, userType, tenants }) => {
  const { path, url } = Digit.Hooks.useModuleBasePath();

  const moduleCode = "WORKS";
  const language = Digit.StoreData.getCurrentLanguage();
  const { isLoading } = Digit.Services.useStore({ stateCode, moduleCode, language });

  addComponentsToRegistry();

  Digit.SessionStorage.set("WORKS_TENANTS", tenants);

  // Fetch tenant-level localization for employees
  useEffect(() => {
    if (userType !== "employee") return;
    (async () => {
      await Digit.LocalizationService.getLocale({
        modules: [`rainmaker-${Digit.ULBService.getCurrentTenantId()}`],
        locale: Digit.StoreData.getCurrentLanguage(),
        tenantId: Digit.ULBService.getCurrentTenantId(),
      });
    })();
  }, []);

  if (isLoading) return <Loader />; // labels must be loaded before the form renders
  if (userType !== "employee") return null; // employee side only

  return <EmployeeApp path={path} url={url} userType={userType} />;
};

// Backward-compatible alias
export const WorksModule = WORKSModule;

/**
 * Registered into the ComponentRegistry from App.js:
 *   setupRegistry({ ...WORKSComponents })
 * Core looks up `WORKSCard` for the employee home dashboard and `WORKSModule` for routes.
 */
export const WORKSComponents = {
  WORKSCard,
  WORKSModule,
};

export const initWorksComponents = () => {
  Object.entries(WORKSComponents).forEach(([key, value]) => {
    Digit.ComponentRegistryService.setComponent(key, value);
  });
};
