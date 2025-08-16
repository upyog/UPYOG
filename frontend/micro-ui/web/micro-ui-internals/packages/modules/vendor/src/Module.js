import { Header, CitizenHomeCard, PTIcon } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useEffect } from "react";
import { useTranslation } from "react-i18next";
import { useRouteMatch } from "react-router-dom";
// import EmployeeApp from "./pages/employee";
import EmployeeApp from "./pages/employee";
import VENDORCard from "./components/VENDORCard";
import AddVendor from "./pages/employee/RegisterVendor/AddVendor";

import VendorDetails from "./pageComponents/VendorDetails";
//import VENDOREMPCreate from "./pages/employee/NewApplication";
// import VendorAddress from "./pageComponents/VendorAddress";
// import VendorPincode from "./pageComponents/VendorPincode";
import VendorDocuments from "./pageComponents/VendorDocuments";
import ServiceDoc from "./pageComponents/ServiceDoc";
import VendorSelectAddress from "./pageComponents/VendorSelectAddress";
//import SearchVendor from "./pages/employee/SearchVendor/SearchVendor";
import SearchApp from "./pages/employee/SearchApp";
import SearchVendor from "./pages/employee/SearchVendor/Index";
import SelectServiceType from "./pageComponents/SelectServiceType";
import AddDriver from "./pages/employee/RegisterDriver/AddDriver";
import EditVendorDetails from "./pages/employee/RegisterVendor/EditVendorDetails";
import AddVehicle from "./pages/employee/RegisterVehicle/AddVehicle";
import VENDORCreate from "./pages/employee/Create";
import CheckPage from "./pages/employee/Create/CheckPage";
import NewResponse from "./pages/employee/Create/NewResponse";
import DriverDetails from "./pages/employee/RegisterDriver/DriverDetails";
import VehicleDetails from "./pages/employee/RegisterVehicle/VehicleDetails";
import SelectVehicleType from "./pageComponents/SelectVehicleType";







const componentsToRegister = {
  VendorDetails,
  //VENDOREMPCreate,
  // VendorAddress,
  // VendorPincode,
  VendorDocuments,
  ServiceDoc,
  AddVendor,
  VendorSelectAddress,
  //SearchVendor,
  SearchApp,
  SearchVendor,
  SelectServiceType,
  SelectVehicleType,
  AddDriver,
  EditVendorDetails,
  AddVehicle,
  VENDORCreate,
  VENDORCheckPage : CheckPage,
  NewResponse,
  DriverDetails,
  VehicleDetails,
};




const addComponentsToRegistry = () => {
  Object.entries(componentsToRegister).forEach(([key, value]) => {
    Digit.ComponentRegistryService.setComponent(key, value);
  });
};

export const VENDORModule = ({ stateCode, userType, tenants }) => {
  const { path, url } = useRouteMatch();

  const moduleCode = "VENDOR";
  const language = Digit.StoreData.getCurrentLanguage();
  const { isLoading, data: store } = Digit.Services.useStore({ stateCode, moduleCode, language });

  addComponentsToRegistry();

  Digit.SessionStorage.set("VENDOR_TENANTS", tenants);

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

  return <EmployeeApp path={path} url={url} userType={userType} />;
};

export const VENDORLinks = ({ matchPath, userType }) => {
  const { t } = useTranslation();
  const [params, setParams, clearParams] = Digit.Hooks.useSessionStorage("VENDOR", {});

  useEffect(() => {
    clearParams();
  }, []);

  const links = [];

  return null;
};




export const VENDORComponents = {
  VENDORCard,
  VENDORModule,
  VENDORLinks,
  // AST_INBOX_FILTER: (props) => <InboxFilter {...props} />,
  // ASTInboxTableConfig: TableConfig,
};
