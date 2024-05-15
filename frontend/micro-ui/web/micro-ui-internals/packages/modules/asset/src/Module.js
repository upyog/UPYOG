import { Header, CitizenHomeCard, PTIcon } from "@upyog/digit-ui-react-components";
import React, { useEffect } from "react";
import { useTranslation } from "react-i18next";
import { useRouteMatch } from "react-router-dom";


import EmployeeApp from "./pages/employee";

import ASSETCard from "./components/ASSETCard";
import InboxFilter from "./components/inbox/NewInboxFilter";
import { TableConfig } from "./config/inbox-table-config";

import ApplicationDetails from "./pages/employee/ApplicationDetails";
import Response from "./pages/Response";
import SelectOtp from "../../core/src/pages/citizen/Login/SelectOtp";
import AcknowledgementCF from "@upyog/digit-ui-module-core/src/components/AcknowledgementCF";


import NewAssetApplication from "./pages/employee/NewAssetApplication";
import AssetClassification from "./pageComponents/AssetClassification";
import AssetDocuments from "./pageComponents/AssetDocuments";
import AssetCommonDetails from "./pageComponents/AssetCommonDetails";
import AssetPincode from "./pageComponents/AssetPincode";
import AssetAddress from "./pageComponents/AssetAddress";
import AssetCommonSelection from "./pageComponents/AssetCommonSelection";
import AssetLand from "./pageComponents/AssetLand";
import AssetBuildings from "./pageComponents/AssetBuildings";
import AssetStreets from "./pageComponents/AssetStreets";



const componentsToRegister = {


  AssetApplication: NewAssetApplication,
  AssetClassification,
  AssetDocuments,
  AssetCommonDetails,
  AssetCommonSelection,
  AssetPincode,
  AssetAddress,
  AssetLand,
  AssetBuildings,
  AssetStreets,
  


  ApplicationDetails: ApplicationDetails,
  AssetResponse: Response,

  SelectOtp, 
  AcknowledgementCF,

  
  

 
};

const addComponentsToRegistry = () => {
  Object.entries(componentsToRegister).forEach(([key, value]) => {
    Digit.ComponentRegistryService.setComponent(key, value);
  });
};


export const ASSETModule = ({ stateCode, userType, tenants }) => {
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

export const ASSETLinks = ({ matchPath, userType }) => {
  const { t } = useTranslation();
  const [params, setParams, clearParams] = Digit.Hooks.useSessionStorage("ASSET", {});

  useEffect(() => {
    clearParams();
  }, []);

  const links = [
    
  ];

  return null;
  
};

export const ASSETComponents = {
  ASSETCard,
  ASSETModule,
  ASSETLinks,
  
};