import { Header, CitizenHomeCard, PTIcon } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useEffect } from "react";
import { useTranslation } from "react-i18next";
import { useRouteMatch } from "react-router-dom";
import CitizenApp from "./pages/citizen";

// component imported from the pagecomponets/CMSearchCertificate
import CMSearchCertificate from "./pageComponents/CMSearchCertificate";

// Created components in the module are registered in component registry to be accessed anywhere
const componentsToRegister = {
  // component created in the module
  CMSearchCertificate
};

// function of component registry to add entries in the registry
const addComponentsToRegistry = () => {
  Object.entries(componentsToRegister).forEach(([key, value]) => {
    Digit.ComponentRegistryService.setComponent(key, value);
  });
};

// Main module function to get to the routes file of citizen or employee module
export const COMMONMODULEModule = ({ stateCode, userType, tenants }) => {
  // function for component registry called here
  addComponentsToRegistry();
  // setting a value in a session storage object
  Digit.SessionStorage.set("CM_TENANTS", tenants);

  // loads localization settings for an employee based on the current tenant and language when the component mounts
  useEffect(
    () =>
      userType === "employee" &&
    // getLocale function from localization service called
      Digit.LocalizationService.getLocale({
        modules: [`rainmaker-${Digit.ULBService.getCurrentTenantId()}`],
        locale: Digit.StoreData.getCurrentLanguage(),
        tenantId: Digit.ULBService.getCurrentTenantId(),
      }),
    []
  );

  // returning the citizenapp component
  return <CitizenApp />;
};

export const COMMONMODULELinks = ({ matchPath, userType }) => {
  const { t } = useTranslation();
  // getting params, setparams and clearparams functions from sessionstorage based on module
  const [params, setParams, clearParams] = Digit.Hooks.useSessionStorage("COMMONMODULE", {});

  // Clears the params from the sessionstorage the first time the function runs
  useEffect(() => {
    clearParams();
  }, []);

  // Links array to pass the links for citizenhomecard
  const links = [];

  // returns CitizenHomeCard component while passing links to be used in card and an Icon to display
  return <CitizenHomeCard header={t("ACTION_TEST_EW")} links={links} Icon={() => <PTIcon className="fill-path-primary-main" />} />;
};

// components exported outside of module
export const COMMONMODULEComponents = {
  // COMMONMODULECard,
  COMMONMODULEModule,
  COMMONMODULELinks,
};