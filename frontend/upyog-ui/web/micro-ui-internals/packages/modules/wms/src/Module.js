import {  CitizenHomeCard, PTIcon } from "@egovernments/digit-ui-react-components";
import React, { useEffect } from "react";
import { useTranslation } from "react-i18next";
import { useRouteMatch } from "react-router-dom";
import CitizenApp from "./pages/citizen";
import Home from "./pages/citizen/Home";

import Create from "./pages/citizen/SOR/Create/index";
import EmployeeApp from "./pages/employee";
import WmsSorChapter from "./pageComponents/SOR/WmsSorChapter";
import WmsSorDescriptionOfItem from "./pageComponents/SOR/WmsSorDescriptionOfItem";
import WMSSelectPhoneNumber from "./pageComponents/WMSSelectPhoneNumber";
import WMSSelectGender from "./pageComponents/WMSSelectGender";
import WMSSelectEmailId from "./pageComponents/WMSSelectEmailId";
import WMSSelectPincode from "./pageComponents/WMSSelectPincode";
import WmsSelectAddress from "./pageComponents/WMSSelectAddress";
import SelectCorrespondenceAddress from "./pageComponents/SOR/WmsCorrospondenceAddress";
import WMSCard from "./components/WmsCard";
/* import WMSManageApplication from "./pages/employee/WMSManageApplication";*/
import RegisterDetails from "./pages/employee/RegisterDetails"; 
import Response from "./pages/citizen/SOR/Response";
import WmsSorId from "./pageComponents/SOR/WmsSorId";
import WmsSorUpdate from "./pages/citizen/SOR/Update";
import WmsSorCreate from "./pages/citizen/SOR/Create";

const componentsToRegister = {
 Response,
   RegisterDetails,
 /* WMSManageApplication,*/
  WMSCard,  
  SelectCorrespondenceAddress,
  WmsSelectAddress,
  WMSSelectPincode,
  WMSSelectEmailId,
  WMSSelectGender,
  WMSSelectPhoneNumber, 
  WmsSorChapter,
  WmsSorDescriptionOfItem,
  Home:Home,
  WmsSorCreate : WmsSorCreate,
  WmsSorUpdate : WmsSorUpdate,
  WmsSorId
};

export const WMSModule = ({ stateCode, userType, tenants }) => {
  const { path, url } = useRouteMatch();

  const moduleCode = "WMS";
  const language = Digit.StoreData.getCurrentLanguage();
  const { isLoading, data: store } = Digit.Services.useStore({ stateCode, moduleCode, language });

  if (userType === "citizen") {
    return <CitizenApp path={path} stateCode={stateCode} />;
  }

  return <EmployeeApp path={path} stateCode={stateCode} />;
};

export const WMSLinks = ({ matchPath, userType }) => {
  const { t } = useTranslation();


  const links = [
  
    {
      link: `${matchPath}/sor-home`,
      i18nKey: t("SOR_HOME"),
    },
    {
      link: `${matchPath}/prjmst/home`,
      i18nKey: t("PRJMST_HOME"),
    },{
      link: `${matchPath}/pm-home`,
      i18nKey: t("PM HOME"),
    },
  ];  

  return <CitizenHomeCard header={t("CITIZEN_SERVICE_WMS")} links={links} Icon={() => <PTIcon className="fill-path-primary-main" />} />;
};

export const initWMSComponents = () => {
  Object.entries(componentsToRegister).forEach(([key, value]) => {
    Digit.ComponentRegistryService.setComponent(key, value);
  });
};

