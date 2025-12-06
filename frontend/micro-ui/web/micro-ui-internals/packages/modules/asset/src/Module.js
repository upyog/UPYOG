import { Header, CitizenHomeCard, PTIcon } from "@upyog/digit-ui-react-components";
import React, { useEffect } from "react";
import { useTranslation } from "react-i18next";
import { useRouteMatch } from "react-router-dom";
import EmployeeApp from "./pages/employee";
import ASSETCard from "./components/ASSETCard";
import InboxFilter from "./components/inbox/NewInboxFilter";
import ApplicationDetails from "./pages/employee/ApplicationDetails";
import Response from "./pages/Response";
import Maintenance from "./pages/Maintenance";
import EditMaintenance from "./pages/EditMaintenance";
import DisposeResponse from "./pages/AssetDisposeResponse";
import ProcessDepreciationResponse from "./pages/ProcessDepreciationResponse";
import ReturnResponse from "./pages/ReturnResponse";
import { TableConfig } from "./config/inbox-table-config";
import NewAssetApplication from "./pages/employee/NewAssetApplication";
import DisposeApplication from "./pages/employee/DisposeAssetApplication";
import MaintenanceApplication from "./pages/employee/MaintenanceAssetApplication";
import AssetDocuments from "./pageComponents/AssetDocuments";
import AssetPincode from "./pageComponents/AssetPincode";
import AssetAddress from "./pageComponents/AssetAddress";
import AssetStreets from "./pageComponents/AssetStreets";
import ServiceDoc from "./pageComponents/ServiceDoc";
import ASSETCreate from "./pages/employee/TestApplication/Create";
import NewAssetClassification from "./pageComponents/NewAssetClassification";
import NewAsset from "./pageComponents/NewAsset";
import NewDocument from "./pageComponents/NewDocument";
import ASTCheckPage from "./pages/employee/TestApplication/Create/CheckPage"
import NewResponse from "./pages/employee/TestApplication/Create/NewResponse";
import AssetAssign from "./pageComponents/AssetAssign";
import AssetDispose from "./pageComponents/AssetDispose";
import AssetMaintenance from "./pageComponents/AssetMaintenance";
import EditAssetMaintenancePage from "./pageComponents/EditAssetMaintenancePage";
import ReturnAsset from "./pages/employee/ReturnAsset";
import ReturnAssignedAsset from "./pageComponents/ReturnAssignedAsset";
// import EditAsset from "./pages/employee/EditAsset";
import EditAsset from "./pages/employee/EditAsset/EditAsset";
import EditGeneralDetails from "./pageComponents/EditGeneralDetails";
import EditAssetDetails from "./pageComponents/EditAssetDetails";
import EditResponse from "./pages/employee/EditResponse";
import EditAssetMaintenance from "./pages/employee/EditAssetMaintenance";




const componentsToRegister = {
  AssignAssetApplication: NewAssetApplication,
  DisposeApplication,
  MaintenanceApplication,
  AssetDocuments,
  AssetPincode,
  AssetAddress,
  AssetStreets,
  ServiceDoc,
  AssetCreateNew: ASSETCreate,
  NewAssetClassification,
  NewAsset,
  NewDocument,
  ASTCheckPage,
  NewResponse,
  EditAssetMaintenancePage,
  ApplicationDetails,
  AssetResponse: Response, 
  Maintenance, 
  EditMaintenance,
  DisposeResponse, 
  ProcessDepreciationResponse,
  returnResponse:ReturnResponse,
  AssetAssign,
  AssetDispose,
  AssetMaintenance,
  EditAssetMaintenance,
  returnAssets:ReturnAsset,
  ReturnAssignedAsset,
  editAsset:EditAsset,
  EditGeneralDetails,
  EditAssetDetails,
  editResponse:EditResponse
  
  
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
  AST_INBOX_FILTER: (props) => <InboxFilter {...props} />,
  ASTInboxTableConfig: TableConfig,
  
};