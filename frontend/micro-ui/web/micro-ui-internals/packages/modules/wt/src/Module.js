import { CitizenHomeCard, PTIcon,ApplicantDetails, AddressDetails } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useEffect } from "react";
import { useTranslation } from "react-i18next";
import { useRouteMatch } from "react-router-dom";
import WTCreate from "./pages/citizen/Create";
import CitizenApp from "./pages/citizen";
import InfoPage from "./pageComponents/InfoPage";
import RequestDetails from "./pageComponents/RequestDetails";
import WTCheckPage from "./pages/citizen/Create/CheckPage";
import WTAcknowledgement from "./pages/citizen/Create/WTAcknowledgement";


const componentsToRegister = {
    ApplicantDetails,
    AddressDetails,
    WTCreate,
    InfoPage,
    RequestDetails,
    WTCheckPage,
    WTAcknowledgement
  };
  
  // function to register the component as per standard 
  const addComponentsToRegistry = () => {
    Object.entries(componentsToRegister).forEach(([key, value]) => {
      Digit.ComponentRegistryService.setComponent(key, value);
    });
  };

  // Parent component of module
  export const WTModule = ({ stateCode, userType, tenants }) => {
    const { path, url } = useRouteMatch();
    const moduleCode = "WT";
    const language = Digit.StoreData.getCurrentLanguage();
    const { isLoading, data: store } = Digit.Services.useStore({ stateCode, moduleCode, language });
    addComponentsToRegistry();
    Digit.SessionStorage.set("WT_TENANTS", tenants);
    useEffect(() =>
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
  
  export const WTLinks = ({ matchPath, userType }) => {
    const { t } = useTranslation();
    const [params, setParams, clearParams] = Digit.Hooks.useSessionStorage("WT", {});
  
    useEffect(() => {
      clearParams();
    }, []);
  
    const links = [ // need to check the links, will be removed later if not needed
      
      {
        link: `${matchPath}/wt`,
        i18nKey: t("WT_REAQUEST_TANKER"),
      },
      {
        link: `${matchPath}/wt/status`,
        i18nKey: t("WT_VIEW_APPLICATIONS"),
      },
    ];
  
    return <CitizenHomeCard header={t("WATER_TANKER_SERVICE")} links={links} Icon={() => <PTIcon className="fill-path-primary-main" />} />;
  };
  
  // export the components outside of module to enable and access of module
  export const WTComponents = {
    WTModule, 
    WTLinks
  };