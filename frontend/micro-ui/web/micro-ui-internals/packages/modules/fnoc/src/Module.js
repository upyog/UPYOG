import { Header, CitizenHomeCard, PTIcon, PropertySearch } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useEffect } from "react";
import { useTranslation } from "react-i18next";
import { useRouteMatch } from "react-router-dom";
import CitizenApp from "./pages/citizen"
import FNOCServiceDoc from "./pageComponents/FNOCServiceDoc";
import FNOCCreate from "./pages/citizen/Create";
import FNOCBuildingDetails from "./pageComponents/FNOCBuildingDetails";
import FNOCCommonDetails from "./pageComponents/FNOCCommonDetails";
import FNOCApplicationDetails from "./pageComponents/FNOCApplicationDetails";
import FNOCDocuments from "./pageComponents/FNOCDocuments";
import FNOCPropertyDetails from "./pageComponents/FNOCPropertyDetails";


const componentsToRegister = {
    FNOCServiceDoc,
    Create:FNOCCreate,
    FNOCBuildingDetails,
    FNOCCommonDetails,
    FNOCApplicationDetails,
    PropertySearch,
    FNOCDocuments,
    FNOCPropertyDetails
   
  };
  
  const addComponentsToRegistry = () => {
    Object.entries(componentsToRegister).forEach(([key, value]) => {
      Digit.ComponentRegistryService.setComponent(key, value);
    });
  };

  export const FNOCModule = ({ stateCode, userType, tenants }) => {
    const { path, url } = useRouteMatch();
    const moduleCode = "FNOC";
    const language = Digit.StoreData.getCurrentLanguage();
    const { isLoading, data: store } = Digit.Services.useStore({ stateCode, moduleCode, language });
    addComponentsToRegistry();
    Digit.SessionStorage.set("FNOC_TENANTS", tenants);
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
  
  export const FNOCLinks = ({ matchPath, userType }) => {
    const { t } = useTranslation();
    const [params, setParams, clearParams] = Digit.Hooks.useSessionStorage("FNOC", {});
  
    useEffect(() => {
      clearParams();
    }, []);
  
    const links = [
      
      {
        link: `${matchPath}/fnoc/apply`,
        i18nKey: t("FNOC_APPLY"),
      },
      {
        link: `${matchPath}/fnoc/my-applications`,
        i18nKey: t("FNOC_MY_APPLICATION"),
      },
    ];
  
    return <CitizenHomeCard header={t("FIRENOC_SERVICES")} links={links} Icon={() => <PTIcon className="fill-path-primary-main" />} />;
  };
  
  export const FNOCComponents = {
    // FNOCCard,
    FNOCModule, 
    FNOCLinks,
    // CHB_INBOX_FILTER: (props) => <InboxFilter {...props} />,
    // CHBInboxTableConfig: TableConfig,
  };