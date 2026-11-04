import { CitizenHomeCard, PTIcon } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useEffect } from "react";
import { useTranslation } from "react-i18next";

import PTRCitizenPet from "./pageComponents/PTRCitizenPet";

import PTRCreate from "./pages/citizen/Create";
import PTRCitizenDetails from "./pageComponents/PTRCitizenDetails";
import PTRSelectAddress from "./pageComponents/PTRSelectAddress";
import PTRSelectProofIdentity from "./pageComponents/PTRSelectProofIdentity";
import PTRServiceDoc from "./pageComponents/PTRServiceDoc";
import PTRWFApplicationTimeline from "./pageComponents/PTRWFApplicationTimeline";
import CitizenApp from "./pages/citizen";
import PTRCheckPage from "./pages/citizen/Create/CheckPage";
import PTRAcknowledgement from "./pages/citizen/Create/PTRAcknowledgement";
import { PTRMyApplications } from "./pages/citizen/PTRMyApplications";
import PTRApplicationDetails from "./pages/citizen/PTRApplicationDetails";
import PTRWFCaption from "./pageComponents/PTRWFCaption";
import EmployeeApp from "./pages/employee";
import PTRCard from "./components/PTRCard";
import InboxFilter from "./components/inbox/NewInboxFilter";
import { TableConfig } from "./config/inbox-table-config";
import ApplicationDetails from "./pages/employee/ApplicationDetails";
import { ReportSearchApplication, EnhancedReport } from "@nudmcdgnpm/digit-ui-module-reports";



// Registering all components to be used in the module
const componentsToRegister = {
  PTRCheckPage,
  PTRAcknowledgement,
  PTRWFCaption,
  ApplicationDetails: ApplicationDetails,
  PTRMyApplications: PTRMyApplications,
  PTRApplicationDetails: PTRApplicationDetails,
  PTRCreatePet: PTRCreate,
  PTRCitizenDetails,
  PTRCitizenPet,
  PTRSelectAddress,
  PTRSelectProofIdentity,
  PTRServiceDoc,
  PTRWFApplicationTimeline,
  EnhancedReport,
  ReportSearchApplication
};

// function of component registry to add entries in the registry
const addComponentsToRegistry = () => {
  Object.entries(componentsToRegister).forEach(([key, value]) => {
    Digit.ComponentRegistryService.setComponent(key, value);
  });
};

// Main module function to get to the routes file of citizen or employee module
export const PTRModule = ({ stateCode, userType, tenants }) => {
  const { path, url } = Digit.Hooks.useModuleBasePath();
  const moduleCode = "PTR";
  const language = Digit.StoreData.getCurrentLanguage();
  const { isLoading, data: store } = Digit.Services.useStore({ stateCode, moduleCode, language });

  addComponentsToRegistry();

  
  Digit.SessionStorage.set("PTR_TENANTS", tenants);  // setting a value in a session storage object

// Fetch localization data if the user is an employee if the user type is employee, fetch localization data for the current tenant and language
  useEffect(() => {
    if (userType === "employee") {
      Digit.LocalizationService.getLocale({
        modules: [`rainmaker-${Digit.ULBService.getCurrentTenantId()}`],
        locale: Digit.StoreData.getCurrentLanguage(),
        tenantId: Digit.ULBService.getCurrentTenantId(),
      });
    }
  }, [userType]);

    // Displaying employee module if userType is 'employee', otherwise displaying citizen module
  if (userType === "employee") {
    return <EmployeeApp path={path} url={url} userType={userType} />;
  } else return <CitizenApp />;
};

// Function to display home card links for citizens
export const PTRLinks = ({ matchPath, userType }) => {
  const { t } = useTranslation();
  const [params, setParams, clearParams] = Digit.Hooks.useSessionStorage("PTR_PET", {});

  useEffect(() => {
    clearParams();
  }, []);

  const links = [];
  
  // returns CitizenHomeCard component while passing links to be used in card and an Icon to display
  return <CitizenHomeCard header={t("ACTION_TEST_PTR")} links={links} Icon={() => <PTIcon className="fill-path-primary-main" />} />;
};

// Components Exported
export const PTRComponents = {
  PTRCard,
  PTRModule,
  PTRLinks,
  PTR_INBOX_FILTER: (props) => <InboxFilter {...props} />,
  PTRInboxTableConfig: TableConfig,
};