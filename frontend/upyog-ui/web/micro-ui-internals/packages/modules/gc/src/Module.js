import { CitizenHomeCard,ApplicantDetails, PTIcon } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useEffect } from "react";
import { useTranslation } from "react-i18next";

import GCCreate from "./pages/citizen/Create";
import CitizenApp from "./pages/citizen";
import GCCard from "./components/GCCard";
import GCSpecifications from "./pageComponents/GCSpecifications";
import GCPropertyLocDetails from "./pageComponents/GCPropertyLocDetails";
import CheckPage from "./pages/citizen/Create/CheckPage"
import GCAcknowledgement from "./pages/citizen/Create/GCAcknowledgement";
import GCDocuments from "./pageComponents/GCDocuments";
import GCDocumentDetails from "./pageComponents/GCDocumentDetails";
import GCSpecialCategory from "./pageComponents/GCSpecialCategory";
import GCApplicationDetails from "./pages/citizen/GCApplicationDetails";
import ApplicationDetails from "./pages/employee/ApplicationDetails";
import EmployeeApp from "./pages/employee";
import Inbox from "./pages/employee/Inbox";
import SearchApp from "./pages/employee/SearchApp";
import CreateApplication from "./pages/employee/Create";

const componentsToRegister = {
  GCCreate,
  GCCard,
  ApplicantDetails,
  GCSpecifications,
  GCPropertyLocDetails,
  CheckPage,
  GCAcknowledgement,
  GCDocuments,
  GCDocumentDetails,
  GCSpecialCategory,
  GCApplicationDetails,
  GCApplicationDetails_Employee: ApplicationDetails,
  GCInbox: Inbox,
  GCSearchApp: SearchApp,
  GCCreateEmp: CreateApplication,
};

const addComponentsToRegistry = () => {
  Object.entries(componentsToRegister).forEach(([key, value]) => {
    Digit.ComponentRegistryService.setComponent(key, value);
  });
};

export const GCModule = ({ stateCode, userType, tenants }) => {
  const { path } = Digit.Hooks.useModuleBasePath();
  const moduleCode = "GC";
  const language = Digit.StoreData.getCurrentLanguage();
  const { isLoading } = Digit.Services.useStore({ stateCode, moduleCode, language });
  const isEmployee = userType === "employee" || Digit.UserService.getUser()?.info?.type === "EMPLOYEE";

  addComponentsToRegistry();

  Digit.SessionStorage.set("GC_TENANTS", tenants);

  useEffect(() => {
    if (isEmployee) {
      Digit.LocalizationService.getLocale({
        modules: [`rainmaker-${Digit.ULBService.getCurrentTenantId()}`],
        locale: Digit.StoreData.getCurrentLanguage(),
        tenantId: Digit.ULBService.getCurrentTenantId(),
      });
    }
  }, [isEmployee]);

  if (isEmployee) {
    return <EmployeeApp path={path} userType={userType} />;
  }
  return <CitizenApp />;
};


export const GCComponents = {
  GCCard,
  GCModule
};