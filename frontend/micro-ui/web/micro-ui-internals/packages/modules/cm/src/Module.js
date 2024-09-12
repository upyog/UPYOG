import { Header, CitizenHomeCard, PTIcon } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useEffect } from "react";
import { useTranslation } from "react-i18next";
import { useRouteMatch } from "react-router-dom";
import CitizenApp from "./pages/citizen";

import CMSearchCertificate from "./pageComponents/CMSearchCertificate";

const componentsToRegister = {
  CMSearchCertificate
};

const addComponentsToRegistry = () => {
  Object.entries(componentsToRegister).forEach(([key, value]) => {
    Digit.ComponentRegistryService.setComponent(key, value);
  });
};


export const COMMONMODULEModule = ({ stateCode, userType, tenants }) => {
  addComponentsToRegistry();

  Digit.SessionStorage.set("CM_TENANTS", tenants);

  useEffect(
    () =>
      userType === "employee" &&
      Digit.LocalizationService.getLocale({
        modules: [`rainmaker-${Digit.ULBService.getCurrentTenantId()}`],
        locale: Digit.StoreData.getCurrentLanguage(),
        tenantId: Digit.ULBService.getCurrentTenantId(),
      }),
    []
  );

  return <CitizenApp />;
};

export const COMMONMODULELinks = ({ matchPath, userType }) => {
  const { t } = useTranslation();
  const [params, setParams, clearParams] = Digit.Hooks.useSessionStorage("COMMONMODULE", {});

  useEffect(() => {
    clearParams();
  }, []);

  const links = [];

  return <CitizenHomeCard header={t("ACTION_TEST_EW")} links={links} Icon={() => <PTIcon className="fill-path-primary-main" />} />;

};


export const COMMONMODULEComponents = {
  // COMMONMODULECard,
  COMMONMODULEModule,
  COMMONMODULELinks,
};