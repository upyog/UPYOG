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


import WmsPhmId from "./pageComponents/PHM/WmsPhmId";
import WmsPhmPrjName from "./pageComponents/PHM/WmsPhmPrjName";
import WmsPhmWorkName from "./pageComponents/PHM/WmsPhmWorkName";
import WmsPhmMLName from "./pageComponents/PHM/WmsPhmMLName"
import WmsPhmPercent from "./pageComponents/PHM/WmsPhmPercent";
// import WmsPhmFund from "./pageComponents/PHM/WmsPhmFund";
// import WmsPhmSourceOfFund from "./pageComponents/PHM/WmsPhmSourceOfFund";
// import WmsPhmDescriptionOfPhmeme from "./pageComponents/PHM/WmsPhmDescriptionOfPhmeme";

// import WmsPhmStartDate from "./pageComponents/PHM/WmsPhmStartDate";
// import WmsPhmEndDate from "./pageComponents/PHM/WmsPhmEndDate";

import WmsPhmResponse from "./pages/citizen/PHM/Response";
import WmsPhmList from "./pages/citizen/PHM/List";
import WmsPhmDetails from "./pages/citizen/PHM/Details";
import WmsPhmCreate from "./pages/citizen/PHM/Create";
import WmsPhmEdit from "./pages/citizen/PHM/Edit";


/**------------ PMA */

import WmsPmaId from "./pageComponents/PMA/WmsPmaId";
import WmsPmaDescriptionOfItem from "./pageComponents/PMA/WmsPmaDescriptionOfItem";
import WmsPmaPercent from "./pageComponents/PMA/WmsPmaPercent";
import WmsPmaStartDate from "./pageComponents/PMA/WmsPmaStartDate";
import WmsPmaEndDate from "./pageComponents/PMA/WmsPmaEndDate";



// import WmsPmaFund from "./pageComponents/PMA/WmsPmaFund";
// import WmsPmaSourceOfFund from "./pageComponents/PMA/WmsPmaSourceOfFund";
// import WmsPmaDescriptionOfPmaeme from "./pageComponents/PMA/WmsPmaDescriptionOfPmaeme";

// import WmsPmaStartDate from "./pageComponents/PMA/WmsPmaStartDate";
// import WmsPmaEndDate from "./pageComponents/PMA/WmsPmaEndDate";

import WmsPmaResponse from "./pages/citizen/PMA/Response";
import WmsPmaList from "./pages/citizen/PMA/List";
import WmsPmaDetails from "./pages/citizen/PMA/Details";
import WmsPmaCreate from "./pages/citizen/PMA/Create";
import WmsPmaEdit from "./pages/citizen/PMA/Edit";


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
import TenderEntryAdd from "./pages/citizen/TE/index";
import TenderEntryView from "./pages/citizen/TE/View"
import EditTender from "./pages/citizen/TE/Edit/index"
import WmsTMResulationNo from "./pageComponents/TE/WmsTMResulationNo"
import WmsTMDepartment from "./pageComponents/TE/WmsTMDepartment"
import WmsTEPreBuildMeetingDate from "./pageComponents/TE/WmsTEPreBuildMeetingDate"
import WmsTEUploadDocuments from "./pageComponents/TE/WmsTEUploadDocuments"
import WmsTEIssueFromDate from "./pageComponents/TE/WmsTEIssueFromDate"
import WmsTEPublishDate from "./pageComponents/TE/WmsTEPublishDate"
import WmsTETecnicalBidOpenDate from "./pageComponents/TE/WmsTETecnicalBidOpenDate"
import WmsTMTenderCategory from "./pageComponents/TE/WmsTMTenderCategory"
import WmsTMProvisionalSum from "./pageComponents/TE/WmsTMProvisionalSum"
import WmsTMProjectName from "./pageComponents/TE/WmsTMProjectName"

import WmsTEResolutionDate from "./pageComponents/TE/WmsTEResolutionDate"
import WmsTMMeetingLocation from "./pageComponents/TE/WmsTMMeetingLocation"
import WmsTEIssueTillDate from "./pageComponents/TE/WmsTEIssueTillDate"
import WmsTEFinancialBidOpenDate from "./pageComponents/TE/WmsTEFinancialBidOpenDate"
import WmsTMValidityOfTenderInDay from "./pageComponents/TE/WmsTMValidityOfTenderInDay"
import WmsTMAdditionalPerformanceSD from "./pageComponents/TE/WmsTMAdditionalPerformanceSD"
import WmsTMBankGuarantee from "./pageComponents/TE/WmsTMBankGuarantee"

import VendorClassEdit from "./pages/citizen/CM/Master/VendorClass/Edit/index";
import VendorClassAdd from "./pages/citizen/CM/Master/VendorClass/VendorClassAdd";
import VendorClassView from "./pages/citizen/CM/Master/VendorClass/View";
import WmsCMVCName from "./pageComponents/ContractMaster/vendorClass/WmsCMVCName"
import WmsCMVCStatus from "./pageComponents/ContractMaster/vendorClass/WmsCMVCStatus"


import AccountHeadAdd from "./pages/citizen/CM/Master/AccountHead/AccountHeadAdd";
import AccountHeadView from "./pages/citizen/CM/Master/AccountHead/View";
import AccountHeadEdit from "./pages/citizen/CM/Master/AccountHead/Edit/index";
import WmsAHAccountno from "./pageComponents/ContractMaster/AccountHead/WmsAHAccountno"
import WmsAHLocation from "./pageComponents/ContractMaster/AccountHead/WmsAHLocation"
import WmsAHName from "./pageComponents/ContractMaster/AccountHead/WmsAHName"
import WmsAHStatus from "./pageComponents/ContractMaster/AccountHead/WmsAHStatus"

import FunctionAppAdd from "./pages/citizen/CM/Master/FunctionApp/FunctionAppAdd";
import FunctionAppEdit from "./pages/citizen/CM/Master/FunctionApp/Edit/index";
import FunctionAppView from "./pages/citizen/CM/Master/FunctionApp/View";

import WmsFACode from "./pageComponents/ContractMaster/FunctionApp/WmsFACode"
import WmsFALevel from "./pageComponents/ContractMaster/FunctionApp/WmsFALevel"
import WmsFAName from "./pageComponents/ContractMaster/FunctionApp/WmsFAName"
import WmsFAStatus from "./pageComponents/ContractMaster/FunctionApp/WmsFAStatus"
import DepartmentAdd from "./pages/citizen/TE/Master/Department/DepartmentAdd";
import DepartmentEdit from "./pages/citizen/TE/Master/Department/Edit/index";
import DepartmentView from "./pages/citizen/TE/Master/Department/View";
import WmsDepartmentStatus from "./pageComponents/TE/department/WmsDepartmentStatus";
import WmsDepartmentName from "./pageComponents/TE/department/WmsDepartmentName";
import TenderCategoryAdd from "./pages/citizen/TE/Master/TenderCategory/TenderCategoryAdd";
import TenderCategoryView from "./pages/citizen/TE/Master/TenderCategory/View";
import TenderCategoryEdit from "./pages/citizen/TE/Master/TenderCategory/Edit/index";
import WmsTenderCategoryName from "./pageComponents/TE/tenderCategory/WmsTenderCategoryName";
import WmsTenderCategoryStatus from "./pageComponents/TE/tenderCategory/WmsTenderCategoryStatus";
import MasterPageList from "./pages/citizen/MasterPageList";
import ContractAgreementAdd from "./pages/citizen/CA/index";
import ContractAgreementView from "./pages/citizen/CA/View";
import WmsCAContractor from "./pageComponents/CA/PartySecond/WmsCAContractor";

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
  
  WmsPhmList:WmsPhmList,
  WmsPhmDetails:WmsPhmDetails,
  WmsPhmCreate:WmsPhmCreate,
  WmsPhmEdit:WmsPhmEdit,
  WmsPhmResponse:WmsPhmResponse,
  WmsPhmId,
  WmsPhmMLName,
  WmsPhmPrjName,
  WmsPhmWorkName,
  WmsPhmPercent,
  // WmsPhmFund,
  // WmsPhmSourceOfFund,
  // WmsPhmDescriptionOfPhmeme,
  // WmsPhmStartDate,
  // WmsPhmEndDate,

  WmsPmaList:WmsPmaList,
  WmsPmaDetails:WmsPmaDetails,
  WmsPmaCreate:WmsPmaCreate,
  WmsPmaEdit:WmsPmaEdit,
  WmsPmaResponse:WmsPmaResponse,
  WmsPmaId,
  WmsPmaDescriptionOfItem,
  WmsPmaPercent,
  WmsPmaStartDate,
  WmsPmaEndDate,
  // WmsPmaFund,
  // WmsPmaSourceOfFund,
  // WmsPmaDescriptionOfPmaeme,
  // WmsPmaStartDate,
  // WmsPmaEndDate,

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

  ContrMasterAdd,
  ContrMasterView,
  WMSCard,
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
WmsCMVendorSubTypeStatus,
TenderEntryAdd,
TenderEntryView,
EditTender,
WmsTMResulationNo,
WmsTMDepartment,
WmsTEPreBuildMeetingDate,
WmsTEUploadDocuments,
WmsTEIssueFromDate,
WmsTEPublishDate,
WmsTETecnicalBidOpenDate,
WmsTMTenderCategory,
WmsTMProvisionalSum,
WmsTMProjectName,
WmsTEResolutionDate,
WmsTMMeetingLocation,
WmsTEIssueTillDate,
WmsTEFinancialBidOpenDate,
WmsTMValidityOfTenderInDay,
WmsTMAdditionalPerformanceSD,
WmsTMBankGuarantee,
VendorClassEdit,
VendorClassAdd,
VendorClassView,
WmsCMVCName,
WmsCMVCStatus,
AccountHeadAdd,
AccountHeadView,
AccountHeadEdit,
WmsAHAccountno,
WmsAHLocation,
WmsAHStatus,
WmsAHName,
FunctionAppAdd,
FunctionAppEdit,
FunctionAppView,
WmsFACode,
WmsFALevel,
WmsFAName,
WmsFAStatus,
DepartmentAdd,
DepartmentView,
DepartmentEdit,
WmsDepartmentName,
WmsDepartmentStatus,
TenderCategoryAdd,
WmsTenderCategoryName,
WmsTenderCategoryStatus,
TenderCategoryView,
TenderCategoryEdit,
MasterPageList,
ContractAgreementAdd,
ContractAgreementView,
WmsCAContractor
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
      link: `${matchPath}/prj-home`,
      i18nKey: t("PRJ_HOME"),
    },
    {
      link: `${matchPath}/sch-home`,
      i18nKey: t("SCH_HOME"),
    },
    {
      link: `${matchPath}/phm-home`,
      i18nKey: t("PM HOME"),
    }
    ,{
      link: `${matchPath}/cm-home`,
      i18nKey: t("Contact Master"),
    },
    // {
    //   link: `${matchPath}/cm-table-view`,
    //   i18nKey: t("Contact Master Tabless"),
    // },
  ];
  return <CitizenHomeCard header={t("CITIZEN_SERVICE_WMS")} links={links} Icon={() => <PTIcon className="fill-path-primary-main" />} />;
};
export const initWMSComponents = () => {
  Object.entries(componentsToRegister).forEach(([key, value]) => {
    Digit.ComponentRegistryService.setComponent(key, value);
  });
};

