/* 
 * Fire NOC Module Entry Point (Module.js)
 * Registers Citizen and Employee components, timelines, and forms in the global Digit Component Registry.
 * Handles the base module initialization, tenant configuration caching, and dynamic routing based on user type.
 */
import { Loader } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import EmployeeApp from "./pages/employee";
import ApplicationOverview from "./pages/employee/ApplicationOverview";
import NOCCard from "./pages/employee/EmployeeCard";
import Inbox from "./pages/employee/Inbox";
import NOCSearchApplication from "./pages/employee/SearchApplication/Search";
import CitizenApp from "./pages/citizen";
import CreateNoc from "./pages/citizen/Create";
import NocTypeSelection from "./pageComponents/NocTypeSelection";
import NocOwnerShipDetails from "./pageComponents/NocOwnerShipDetails";
import NocOwnerDetails from "./pageComponents/NocOwnerDetails";
import NocPropertyDetails from "./pageComponents/NocPropertyDetails";
import NocLocationDetails from "./pageComponents/NocLocationDetails";
import NocDocumentDetails from "./pageComponents/NocDocumentDetails";
import CheckPage from "./pages/citizen/Create/CheckPage";
import NOCAcknowledgement from "./pages/citizen/Create/NOCAcknowledgement";
import NocCitizenHome from "./pages/citizen/NocCitizenHome";
import NOCServiceDoc from "./pageComponents/NOCServiceDoc";
import { NocMyApplications } from "./pages/citizen/NocMyApplications";
import NOCApplicationDetails from "./pages/citizen/NocApplicationDetails";

const NOCModule = ({ stateCode, userType, tenants }) => {
  const moduleCode = ["noc", "firenoc", "common-noc"];
  const { path, url } = Digit.Hooks.useModuleBasePath();
  const language = Digit.StoreData.getCurrentLanguage();
  const { isLoading, data: store } = Digit.Services.useStore({ stateCode, moduleCode, language });

  Digit.SessionStorage.set("NOC_TENANTS", tenants);

  if (isLoading) {
    return <Loader />;
  }

  if (userType === "citizen") {
    return <CitizenApp />;
  }

  return <EmployeeApp path={path} stateCode={stateCode} />;
};

const componentsToRegister = {
  NOCModule,
  FireNocModule: NOCModule,
  NOCCard,
  NOCApplicationOverview: ApplicationOverview,
  NOCInbox: Inbox,
  NOCSearchApplication,
  NOCCreateApplication: CreateNoc,
  NocTypeSelection,
  NocOwnerShipDetails,
  NocOwnerDetails,
  NocPropertyDetails,
  NocLocationDetails,
  NocDocumentDetails,
  NOCCheckPage: CheckPage,
  NOCAcknowledgement,
  NOCCitizenHome: NocCitizenHome,
  NOCServiceDoc,
  NOCMyApplications: NocMyApplications,
  NOCApplicationDetails
};

export const initNOCComponents = () => {
  Object.entries(componentsToRegister).forEach(([key, value]) => {
    Digit.ComponentRegistryService.setComponent(key, value);
  });
};
