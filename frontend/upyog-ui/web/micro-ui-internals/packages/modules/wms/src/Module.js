import {  CitizenHomeCard, PTIcon } from "@egovernments/digit-ui-react-components";
import React, { useEffect } from "react";
import { useTranslation } from "react-i18next";
import { useRouteMatch } from "react-router-dom";
import CitizenApp from "./pages/citizen";
import Home from "./pages/citizen/Home";
import EmployeeApp from "./pages/employee";
import ListFilter from "./components/ListFilter";
import WMSCard from "./components/WmsCard";


//** SOR */
import WmsSorId from "./pageComponents/SOR/WmsSorId";
import WmsSorName from "./pageComponents/SOR/WmsSorName";
import WmsSorChapter from "./pageComponents/SOR/WmsSorChapter";
import WmsSorItemNo from "./pageComponents/SOR/WmsSorItemNo";
import WmsSorDescriptionOfItem from "./pageComponents/SOR/WmsSorDescriptionOfItem";
import WmsSorRate from "./pageComponents/SOR/WmsSorRate";
import WmsSorUnit from "./pageComponents/SOR/WmsSorUnit";
import WmsSorStartDate from "./pageComponents/SOR/WmsSorStartDate";
import WmsSorEndDate from "./pageComponents/SOR/WmsSorEndDate";
import WmsSorResponse from "./pages/citizen/SOR/Response";

import List from "./pages/citizen/SOR/List";
import Details from "./pages/citizen/SOR/Details";
import Create from "./pages/citizen/SOR/Create";
import Edit from "./pages/citizen/SOR/Edit";

//** SOR END*/


//** PRJ */
import WmsPrjId from "./pageComponents/PRJ/WmsPrjId";
import WmsPrjNameEn from "./pageComponents/PRJ/WmsPrjNameEn";
import WmsPrjNameReg from "./pageComponents/PRJ/WmsPrjNameReg";
import WmsPrjNumber from "./pageComponents/PRJ/WmsPrjNumber";
import WmsPrjDescription from "./pageComponents/PRJ/WmsPrjDescription";
import WmsPrjApprovalNo from "./pageComponents/PRJ/WmsPrjApprovalNo";
import WmsPrjDepartment from "./pageComponents/PRJ/WmsPrjDepartment";
import WmsPrjTimeLine from "./pageComponents/PRJ/WmsPrjTimeLine";
import WmsPrjSchemeName from "./pageComponents/PRJ/WmsPrjSchemeName";
import WmsPrjSchemeNo from "./pageComponents/PRJ/WmsPrjSchemeNo";
import WmsPrjSourceOfFund from "./pageComponents/PRJ/WmsPrjSourceOfFund";
import WmsPrjApprovalDate from "./pageComponents/PRJ/WmsPrjApprovalDate";
import WmsPrjStartDate from "./pageComponents/PRJ/WmsPrjStartDate";
import WmsPrjEndDate from "./pageComponents/PRJ/WmsPrjEndDate";
import WmsPrjStatus from "./pageComponents/PRJ/WmsPrjStatus";
import WmsPrjResponse from "./pages/citizen/PRJ/Response";

import PrjCreate from "./pages/citizen/PRJ/Create";
import PrjList from "./pages/citizen/PRJ/List";
import PrjDetails from "./pages/citizen/PRJ/Details";
import PrjEdit from "./pages/citizen/PRJ/Edit";
//** PRJ END*/



//** SCH */
import WmsSchId from "./pageComponents/SCH/WmsSchId";
import WmsSchNameEn from "./pageComponents/SCH/WmsSchNameEn";
import WmsSchNameReg from "./pageComponents/SCH/WmsSchNameReg";
import WmsSchFund from "./pageComponents/SCH/WmsSchFund";
import WmsSchSourceOfFund from "./pageComponents/SCH/WmsSchSourceOfFund";
import WmsSchDescriptionOfScheme from "./pageComponents/SCH/WmsSchDescriptionOfScheme";
import WmsSchStartDate from "./pageComponents/SCH/WmsSchStartDate";
import WmsSchEndDate from "./pageComponents/SCH/WmsSchEndDate";
import WmsSchResponse from "./pages/citizen/SCH/Response";

import WmsSchList from "./pages/citizen/SCH/List";
import WmsSchDetails from "./pages/citizen/SCH/Details";
import WmsSchCreate from "./pages/citizen/SCH/Create";
import WmsSchEdit from "./pages/citizen/SCH/Edit";

//** SCH END*/


/**------------ PM */
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
  WmsSorList:List,
  WmsSorDetails:Details,
  WmsSorCreate:Create,
  WmsSorEdit:Edit,
  WMS_LIST_FILTER: (props) => <ListFilter  {...props} />,  
  WmsSorId,
  WmsSorName,
  WmsSorChapter,
  WmsSorItemNo,
  WmsSorDescriptionOfItem,
  WmsSorUnit,
  WmsSorRate,
  WmsSorStartDate,
  WmsSorEndDate,
  WmsSorResponse:WmsSorResponse,

  WmsSchList:WmsSchList,
  WmsSchDetails:WmsSchDetails,
  WmsSchCreate:WmsSchCreate,
  WmsSchEdit:WmsSchEdit,
  WmsSchId,
  WmsSchNameEn,
  WmsSchNameReg,
  WmsSchFund,
  WmsSchSourceOfFund,
  WmsSchDescriptionOfScheme,
  WmsSchStartDate,
  WmsSchEndDate,
  WmsSchResponse,
  WmsSchResponse:WmsSchResponse,

  WmsPrjList:PrjList,
  WmsPrjDetails:PrjDetails,
  WmsPrjCreate:PrjCreate,
  WmsPrjEdit:PrjEdit,
  WmsPrjId,
  WmsPrjNameEn,
  WmsPrjNameReg,
  WmsPrjNumber,
  WmsPrjDescription,
  WmsPrjApprovalNo,
  WmsPrjDepartment,
  WmsPrjTimeLine,
  WmsPrjSchemeName,
  WmsPrjSchemeNo,
  WmsPrjSourceOfFund,
  WmsPrjApprovalDate,
  WmsPrjStartDate,
  WmsPrjEndDate,
  WmsPrjStatus,
  WmsPrjResponse:WmsPrjResponse,

  PMCreate:PMCreate,
  ContrMasterAdd,
  ContrMasterView,
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

