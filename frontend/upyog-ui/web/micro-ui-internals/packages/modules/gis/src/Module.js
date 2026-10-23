import { CitizenHomeCard, PTIcon,ApplicantDetails, AddressDetails } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useEffect } from "react";
import { useTranslation } from "react-i18next";
import CitizenApp from "./pages";
import EmployeeApp from "./pages/employee";
import ServiceTypes from "./components/ServiceTypes";
import MapView from "./components/MapView";
import LayerView from "./components/LayerView";
import MarkOnMap from "./components/MarkOnMap";
import ViewOnMap from "./components/ViewOnMap";
import DiginpinMapPopup from "./components/DiginpinMapPopup";
import GISCard from "./components/GISCard";
import ViewOnMapAsset from "./components/ViewOnMapAsset";

// Object containing components to be registered in the Digit Component Registry Service for the components used in the module
const componentsToRegister = {
    ServiceTypes,
    MapView,
    LayerView,
    ViewOnMapAsset,
    MarkOnMap,
    ViewOnMap,
    DiginpinMapPopup,
    GISCard,
    EmployeeApp
  };
  
  // function to register the component as per standard 
  const addComponentsToRegistry = () => {
    Object.entries(componentsToRegister).forEach(([key, value]) => {
      Digit.ComponentRegistryService.setComponent(key, value);
    });
  };

  // Parent component of module
  export const GISModule = ({ stateCode, userType, tenants }) => {
    const { path, url } = Digit.Hooks.useModuleBasePath();
    const moduleCode = "GIS";
    const language = Digit.StoreData.getCurrentLanguage();
    const { isLoading, data: store } = Digit.Services.useStore({ stateCode, moduleCode, language });
    addComponentsToRegistry();
    Digit.SessionStorage.set("WT_TENANTS", tenants);

// Fetch localization data if the user is an employee if the user type is employee, fetch localization data for the current tenant and language
    useEffect(() => {
      if (userType === "employee") {
        Digit.LocalizationService.getLocale({
          modules: [`rainmaker-${Digit.ULBService.getCurrentTenantId()}`],
          locale: Digit.StoreData.getCurrentLanguage(),
          tenantId: Digit.ULBService.getCurrentTenantId(),
        });
      }
    }, [userType]);
  
    if (userType === "employee") {
      return <EmployeeApp path={path} url={url} userType={userType} />;
    } else return <CitizenApp />;
  };
  
  export const GISLinks = ({ matchPath, userType }) => {
    const { t } = useTranslation();
    const [params, setParams, clearParams] = Digit.Hooks.useSessionStorage("GIS", {});
  
    useEffect(() => {
      clearParams();
    }, []);
  
    
  
    return <CitizenHomeCard header={t("GIS")} links={links} Icon={() => <PTIcon className="fill-path-primary-main" />} />;
  };
  
  // export the components outside of module to enable and access of module
  export const GISComponents = {
    GISCard,
    GISModule,
    GISLinks,
   
  };

  export { MarkOnMap, MapView, ServiceTypes, ViewOnMap, DiginpinMapPopup };