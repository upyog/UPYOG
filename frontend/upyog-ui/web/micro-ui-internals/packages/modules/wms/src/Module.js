import {  CitizenHomeCard, PTIcon } from "@egovernments/digit-ui-react-components";
import React, { useEffect } from "react";
import { useTranslation } from "react-i18next";
import { useRouteMatch } from "react-router-dom";
import CitizenApp from "./pages/citizen";
import Home from "./pages/citizen/Home";
import EmployeeApp from "./pages/employee";

import WMSCard from "./components/WmsCard";


import Response from "./pages/citizen/SOR/Response";


/**------------ PM */

import List from "./pages/citizen/PM/List";
import PMCreate from "./pages/citizen/PM/Create";
import WmsPmId from "./pageComponents/PM/WmsPmId";
import WmsPmMlName from "./pageComponents/PM/WmsPmMlName";
import WmsPmPer from "./pageComponents/PM/WmsPmPer";
import WmsPmPrjName from "./pageComponents/PM/WmsPmPrjName";
import WmsPmWrkName from "./pageComponents/PM/WmsPmWrkName";

/**---------------- */
import ContrMasterAdd from "./pages/citizen/ContrMaster/index";
import ContrMasterView from "./pages/citizen/ContrMaster/ContrView";
const componentsToRegister = {  
  Home:Home,  
  PMCreate:PMCreate,
  WmsPMList:List,
  ContrMasterAdd,
  ContrMasterView,
  Response,
  WMSCard,
  WmsPmId,
  WmsPmMlName,
  WmsPmPer,
  WmsPmPrjName,
  WmsPmWrkName,
};
export const WMSModule = ({ stateCode, userType, tenants }) => {
  const { path, url } = useRouteMatch();
  const moduleCode = "WMS";
  const language = Digit.StoreData.getCurrentLanguage();
  Digit.SessionStorage.set("WMS_TENANTS", tenants);
  const { isLoading, data: store } = Digit.Services.useStore({ stateCode, moduleCode, language });
  useEffect(
    () =>
      userType === "citizen" &&
      Digit.LocalizationService.getLocale({
        modules: [`rainmaker-${Digit.ULBService.getCurrentTenantId()}`],
        locale: Digit.StoreData.getCurrentLanguage(),
        tenantId: Digit.ULBService.getCurrentTenantId(),
      }),
    []
  );
  if (userType === "citizen") {
    return <CitizenApp path={path} stateCode={stateCode} />;
  }
  return <EmployeeApp path={path} stateCode={stateCode} />;
};
export const WMSLinks = ({ matchPath, userType }) => {
  const { t } = useTranslation();
  const links = [
     {
      link: `${matchPath}/wms-home`,
      i18nKey: t("WMS_HOME"),
    }, 
    {
      link: `${matchPath}/prjmst/home`,
      i18nKey: t("PRJMST_HOME"),
    },
    {
      link: `${matchPath}/pm-home`,
      i18nKey: t("PM HOME"),
    }
    ,{
      link: `${matchPath}/cm-home`,
      i18nKey: t("Contact Master"),
    },
    {
      link: `${matchPath}/cm-table-view`,
      i18nKey: t("Contact Master Tabless"),
    },
  ];
  return <CitizenHomeCard header={t("CITIZEN_SERVICE_WMS")} links={links} Icon={() => <PTIcon className="fill-path-primary-main" />} />;
};
export const initWMSComponents = () => {
  Object.entries(componentsToRegister).forEach(([key, value]) => {
    Digit.ComponentRegistryService.setComponent(key, value);
  });
};

