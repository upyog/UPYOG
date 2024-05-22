import { Header, CitizenHomeCard, PTIcon } from "@upyog/digit-ui-react-components";
import React, { useEffect } from "react";
import { useTranslation } from "react-i18next";
import { useRouteMatch } from "react-router-dom";

import EWCard from "./components/EWCard";




const componentsToRegister = {}



const addComponentsToRegistry = () => {
    Object.entries(componentsToRegister).forEach(([key, value]) => {
      Digit.ComponentRegistryService.setComponent(key, value);
    });
  };
  
  
  export const EWModule = ({ stateCode, userType, tenants }) => {
    const { path, url } = useRouteMatch();
  
    const moduleCode = "EW";
    const language = Digit.StoreData.getCurrentLanguage();
    const { isLoading, data: store } = Digit.Services.useStore({ stateCode, moduleCode, language });
  
    addComponentsToRegistry();
  
    Digit.SessionStorage.set("EW_TENANTS", tenants);
  
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
  
    if (userType === "employee") {
      return <EmployeeApp path={path} url={url} userType={userType} />;
    } else return <CitizenApp />;
  };
  
  export const EWLinks = ({ matchPath, userType }) => {
    const { t } = useTranslation();
    const [params, setParams, clearParams] = Digit.Hooks.useSessionStorage("EW", {});
  
    useEffect(() => {
      clearParams();
    }, []);
  
    const links = [
      
    //   {
    //     link: `${matchPath}/ptr/petservice/new-application`,
    //     i18nKey: t("PTR_CREATE_PET_APPLICATION"),
    //   },
      
    //   {
    //     link: `${matchPath}/ptr/petservice/my-application`,
    //     i18nKey: t("PTR_MY_APPLICATIONS_HEADER"),
    //   },
      
    //   {
    //     link: `${matchPath}/howItWorks`,
    //     i18nKey: t("PTR_HOW_IT_WORKS"),
    //   },
    //   {
    //     link: `${matchPath}/faqs`,
    //     i18nKey: t("PTR_FAQ_S"),
    //   },
    ];
  
    return <CitizenHomeCard header={t("ACTION_TEST_EW")} links={links} Icon={() => <PTIcon className="fill-path-primary-main" />} />;
  };
  
  export const EWComponents = {
    EWCard,
    EWModule,
    EWLinks,
    
  };