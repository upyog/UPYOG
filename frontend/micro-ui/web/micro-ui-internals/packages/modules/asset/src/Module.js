import { Header, CitizenHomeCard, PTIcon } from "@egovernments/digit-ui-react-components";
import React, { useEffect } from "react";
import { useTranslation } from "react-i18next";
import { useRouteMatch } from "react-router-dom";

import AssetCard from "./components/AssetCard";




const componentsToRegister = {
    // PTRCheckPage,
    // PTRAcknowledgement,
    // PTRWFCaption,
    // PTRWFReason,
    // PTRNewApplication: NewApplication,
    // ApplicationDetails: ApplicationDetails,
    // PTRResponse: Response,
    // PTRMyApplications: PTRMyApplications,
    // PTRApplicationDetails: PTRApplicationDetails,
    // SelectOtp, // To-do: Temp fix, Need to check why not working if selectOtp module is already imported from core module
    // AcknowledgementCF,
    // CitizenFeedback,
   
    
    
    
  
   
};

const addComponentsToRegistry = () => {
Object.entries(componentsToRegister).forEach(([key, value]) => {
    Digit.ComponentRegistryService.setComponent(key, value);
});
};

export const AssetModule = ({ stateCode, userType, tenants }) => {
    const { path, url } = useRouteMatch();
  
    const moduleCode = "ASSET";
    const language = Digit.StoreData.getCurrentLanguage();
    const { isLoading, data: store } = Digit.Services.useStore({ stateCode, moduleCode, language });
  
    addComponentsToRegistry();
  
    Digit.SessionStorage.set("ASSET_TENANTS", tenants);
  
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

export const AssetLinks = ({ matchPath, userType }) => {
    const { t } = useTranslation();
    const [params, setParams, clearParams] = Digit.Hooks.useSessionStorage("PTR_PET", {});
  
    useEffect(() => {
      clearParams();
    }, []);
  
    const links = [
      
    //   {
    //     link: `${matchPath}/ptr/petservice/new-application`,
    //     i18nKey: t("PTR_CREATE_PET_APPLICATION"),
    //   },
      
    //   {
    //     link: `${matchPath}/ptr/petservice/my-applications`,
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
  
    return <CitizenHomeCard header={t("ACTION_TEST_ASSET")} links={links} Icon={() => <PTIcon className="fill-path-primary-main" />} />;
  };
  
  export const  AssetComponents = {
    AssetCard,
    AssetModule,
    AssetLinks,
    // PT_INBOX_FILTER: (props) => <InboxFilter {...props} />,
    // PTRInboxTableConfig: TableConfig,
  };

  