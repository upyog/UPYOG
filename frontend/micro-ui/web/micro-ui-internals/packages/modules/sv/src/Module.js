import { CitizenHomeCard, PTIcon } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useEffect } from "react";
import { useTranslation } from "react-i18next";
import { useRouteMatch } from "react-router-dom";
import SVCreate from "./pages/citizen/Create";
import CitizenApp from "./pages/citizen"
import SVApplicantDetails from "./pageComponents/SVApplicantDetails";
import SVBusinessDetails from "./pageComponents/SVBusinessDetails";
import GIS from "./pageComponents/GIS";
import SVRequiredDoc from "./pageComponents/SVRequiredDoc";
import SVDayAndTimeSlot from "./pageComponents/SVDayAndTimeSlot";
import SVAdrressDetails from "./pageComponents/SVAdrressDetails";
import SVBankDetails from "./pageComponents/SVBankDetails";
import SVDocumentsDetail from "./pageComponents/SVDocumentsDetail";
import SVCheckPage from "./pages/citizen/Create/SVCheckPage";
import SVCard from "./components/SVCard";
import SVEmpCreate from "./pages/employee/Create";
import EmployeeApp from "./pages/employee";


const componentsToRegister = {
   Create:SVCreate,
   SVApplicantDetails,
   SVBusinessDetails,
   GIS,
   SVRequiredDoc,
   SVDayAndTimeSlot,
   SVAdrressDetails,
   SVBankDetails,
   SVDocumentsDetail,
   CheckPage:SVCheckPage,
   SVEmpCreate
  };
  
  // function to register the component as per standard 
  const addComponentsToRegistry = () => {
    Object.entries(componentsToRegister).forEach(([key, value]) => {
      Digit.ComponentRegistryService.setComponent(key, value);
    });
  };

  // Parent component of module
  export const SVModule = ({ stateCode, userType, tenants }) => {
    const { path, url } = useRouteMatch();
    const moduleCode = "SV";
    const language = Digit.StoreData.getCurrentLanguage();
    const { isLoading, data: store } = Digit.Services.useStore({ stateCode, moduleCode, language });
    addComponentsToRegistry();
    Digit.SessionStorage.set("SV_TENANTS", tenants);
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
  
  export const SVLinks = ({ matchPath, userType }) => {
    const { t } = useTranslation();
    const [params, setParams, clearParams] = Digit.Hooks.useSessionStorage("SV", {});
  
    useEffect(() => {
      clearParams();
    }, []);
  
    const links = [ // need to check the links, will be removed later if not needed
      
      {
        link: `${matchPath}/sv/apply`,
        i18nKey: t("sv_APPLY"),
      },
      {
        link: `${matchPath}/sv/my-applications`,
        i18nKey: t("sv_MY_APPLICATION"),
      },
    ];
  
    return <CitizenHomeCard header={t("STREET_VENDING_SERVICES")} links={links} Icon={() => <PTIcon className="fill-path-primary-main" />} />;
  };
  
  // export the components outside of module to enable and access of module
  export const SVComponents = {
    SVModule, 
    SVLinks,
    SVCard
  };