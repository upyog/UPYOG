import { Header, CitizenHomeCard, PTIcon } from "@upyog/digit-ui-react-components";
import React, { useEffect } from "react";
import { useTranslation } from "react-i18next";
import { useRouteMatch } from "react-router-dom";

import EWCard from "./components/EWCard";
import EWASTEProductDetails from "./pageComponents/EWASTEProductDetails";
import EWASTEOwnerInfo from "./pageComponents/EWASTEOwnerInfo";
import EWASTEProductList from "./components/EWASTEProductList";
import EWASTEProductListElement from "./components/EWASTEProductListElement";
import EWCheckPage from "./pages/citizen/Create/CheckPage";
import EWCreate from "./pages/citizen/Create";
import CitizenApp from "./pages/citizen";
import EWASTEVendorDetails from "./pageComponents/EWASTEVendorDetails";
import EWASTECitizenAddress from "./pageComponents/EWASTECitizenAddress";
import EWASTESelectPincode from "./pageComponents/EWASTESelectPincode";
import EWASTESelectAddress from "./pageComponents/EWASTESelectAddress";
import EWASTEAcknowledgement from "./pages/citizen/Create/EWASTEAcknowledgement";



const componentsToRegister = {
  EWCreatewaste:EWCreate,
  EWCheckPage,
  EWASTEProductDetails,
  EWASTEOwnerInfo,
  EWASTEProductList,
  EWASTEProductListElement,
  EWASTEVendorDetails,
  EWASTECitizenAddress,
  EWASTESelectPincode,
  EWASTESelectAddress,
  EWASTEAcknowledgement
}



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
      
      // {
      //   link: `${matchPath}/ew/ew-common/raiseRequest`,
      //   i18nKey: t("EW_kuchbhi"),
      // },
      
    //   {
    //     link: `${matchPath}/EWASTE/petservice/my-applition`,
    //     i18nKey: t("EWASTE_MY_APPLICATIONS_HEADER"),
    //   },
      
    //   {
    //     link: `${matchPath}/howItWorks`,
    //     i18nKey: t("EWASTE_HOW_IT_WORKS"),
    //   },
    //   {
    //     link: `${matchPath}/faqs`,
    //     i18nKey: t("EWASTE_FAQ_S"),
    //   },
    ];
  
    return <CitizenHomeCard header={t("ACTION_TEST_EW")} links={links} Icon={() => <PTIcon className="fill-path-primary-main" />} />;
  };
  
  export const EWComponents = {
    EWCard,
    EWModule,
    EWLinks,
  };