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
import ContrMasterAdd from "./pages/citizen/CM/index";
import ContrMasterView from "./pages/citizen/CM/ContrView";
import ContractViewTableTest from "./pages/citizen/CM/ContractViewTableTest";
import WmsCMMobileNumber from "./pageComponents/ContractMaster/WmsCMMobileNumber";
import WmsCMPFMSVendorID from "./pageComponents/ContractMaster/WmsCMPFMSVendorID";
import WmsCMUIDNumber from "./pageComponents/ContractMaster/WmsCMUIDNumber";
import WmsCMVendorName from "./pageComponents/ContractMaster/WmsCMVendorName";
import WmsCMVendorStatus from "./pageComponents/ContractMaster/WmsCMVendorStatus";
import WmsCMVendorType from "./pageComponents/ContractMaster/WmsCMVendorType";
import InboxFilter from "./components/CmList/InboxFilter";
import WMSCardContract from "./components/CmList/wmscard";
import CMView from "./pages/citizen/CM/CmView";
import ContractMasterDetail from "./pages/citizen/CM/ContractMasterDetail";
import WmsCMVATNumber from "./pageComponents/ContractMaster/WmsCMVATNumber";
import WmsCMBankBranchIFSCCode from "./pageComponents/ContractMaster/WmsCMBankBranchIFSCCode";
import WmsCMFunction from "./pageComponents/ContractMaster/WmsCMFunction";
import WmsCMVendorClass from "./pageComponents/ContractMaster/WmsCMVendorClass";
import WmsCMPFAccountNumber from "./pageComponents/ContractMaster/WmsCMPFAccountNumber";
import WmsCMSubType from "./pageComponents/ContractMaster/WmsCMSubType";
import WmsCMPayTo from "./pageComponents/ContractMaster/WmsCMPayTo";
import WmsCMEmailId from "./pageComponents/ContractMaster/WmsCMEmailId";
import WmsCMGSTNumber from "./pageComponents/ContractMaster/WmsCMGSTNumber";
import WmsCMPANNumber from "./pageComponents/ContractMaster/WmsCMPANNumber";
import WmsCMBankAccountNumber from "./pageComponents/ContractMaster/WmsCMBankAccountNumber";
import WmsCMPrimaryAccountHead from "./pageComponents/ContractMaster/WmsCMPrimaryAccountHead";
import WmsCMAddress from "./pageComponents/ContractMaster/WmsCMAddress";
import WmsCMAllowDirectPayment from "./pageComponents/ContractMaster/WmsCMAllowDirectPayment";
import WmsCMBankName from "./pageComponents/ContractMaster/bankField/WmsCMBankName";
import WmsCMBankBranch from "./pageComponents/ContractMaster/bankField/WmsCMBankBranch";
import WmsCMBankIFSCCode from "./pageComponents/ContractMaster/bankField/WmsCMBankIFSCCode";
import WmsCMBankBranchIFSC from "./pageComponents/ContractMaster/bankField/WmsCMBankBranchIFSC";
import WmsCMBankStatus from "./pageComponents/ContractMaster/bankField/WmsCMBankStatus";
import EditCitizen from "./pages/citizen/CM/EditCitizen/index";
import ViewBankTable from "./pages/citizen/CM/Master/Bank/View";
import BankAdd from "./pages/citizen/CM/Master/Bank/BankAdd";
import AppEditBank from "./pages/citizen/CM/Master/Bank/Edit/index";
import VendorTypeAdd from "./pages/citizen/CM/Master/VendorType/VendorTypeAdd";
import View from "./pages/citizen/CM/Master/VendorType/View";
import VendorTypeEdit from "./pages/citizen/CM/Master/VendorType/Edit";
import WmsCMVType from "./pageComponents/ContractMaster/vendorTypeField/WmsCMVType";
import WmsCMVTypeStatus from "./pageComponents/ContractMaster/vendorTypeField/WmsCMVTypeStatus";
import EditSubType from "./pages/citizen/CM/Master/VendorSubType/Edit/index";
import AddSubType from "./pages/citizen/CM/Master/VendorSubType/Add";
import ViewSubType from "./pages/citizen/CM/Master/VendorSubType/View";
import WmsCMVendorSubTypeName from "./pageComponents/ContractMaster/vendorSubType/WmsCMVendorSubTypeName";
import WmsCMVendorSubTypeStatus from "./pageComponents/ContractMaster/vendorSubType/WmsCMVendorSubTypeStatus";

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
  CMView,
  WMSCardContract,
  ContractViewTableTest,
  WmsCMMobileNumber,
WMSDetails: ContractMasterDetail,
WmsCMPFMSVendorID,
WmsCMUIDNumber,
WmsCMVendorName,
WmsCMVendorStatus,
WmsCMVendorType,
//     RegisterDetails,
// SelectCorrespondenceAddress,
//   WmsSelectAddress,
//   WMSSelectPincode,
//   WMSSelectEmailId,
//   WMSSelectGender,
//   WMSSelectPhoneNumber, 
  WmsSorChapter,
  WmsSorDescriptionOfItem,
  Home:Home,
  // WmsSorCreate : WmsSorCreate,
  WmsSorId,
  WMS_INBOX_FILTER: (props) => <InboxFilter {...props} />,
  WmsCMVATNumber,
  WmsCMBankBranchIFSCCode,
WmsCMFunction,
WmsCMVendorClass,
WmsCMPFAccountNumber,
WmsCMSubType,
WmsCMPayTo,
WmsCMEmailId,
WmsCMGSTNumber,
WmsCMPANNumber,
WmsCMBankAccountNumber,
WmsCMPrimaryAccountHead,
WmsCMAddress,
WmsCMAllowDirectPayment,
WMSEditCitizen:EditCitizen,
ViewBankTable,
AppEditBank,
BankAdd,
WmsCMBankName,
WmsCMBankBranch,
WmsCMBankIFSCCode,
WmsCMBankBranchIFSC,
WmsCMBankStatus,
VendorTypeAdd,
View,
VendorTypeEdit,
WmsCMVType,
WmsCMVTypeStatus,
EditSubType,
AddSubType,
ViewSubType,
WmsCMVendorSubTypeName,
WmsCMVendorSubTypeStatus


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

