import { Header, CitizenHomeCard, PTIcon } from "@upyog/digit-ui-react-components";
import React, { useEffect } from "react";
import { useTranslation } from "react-i18next";
import { useRouteMatch } from "react-router-dom";
import { TableConfig } from "./config/inbox-table-config";
import EWCard from "./components/EWCard";
import EWASTEProductDetails from "./pageComponents/EWASTEProductDetails";
import EWASTEOwnerInfo from "./pageComponents/EWASTEOwnerInfo";
import EWASTEProductList from "./components/EWASTEProductList";
import EWCheckPage from "./pages/citizen/Create/CheckPage";
import EWCreate from "./pages/citizen/Create";
import CitizenApp from "./pages/citizen";
import EWASTECitizenAddress from "./pageComponents/EWASTECitizenAddress";
import EWASTESelectPincode from "./pageComponents/EWASTESelectPincode";
import EWASTESelectAddress from "./pageComponents/EWASTESelectAddress";
import EWASTEAcknowledgement from "./pages/citizen/Create/EWASTEAcknowledgement";
import { EWASTEMyApplications } from "./pages/citizen/EWASTEMyApplications";
import EWASTECitizenApplicationDetails from "./pages/citizen/EWASTECitizenApplicationDetails";
import Inbox from "./pages/employee/Inbox";
import InboxFilter from "./components/inbox/NewInboxFilter";
import EmployeeApp from "./pages/employee/index";
import EWApplicationDetails from "./pages/employee/ApplicationDetails";
import { EwService } from "../../../libraries/src/services/elements/EW";
import EWASTEWFCaption from "./components/EWASTEWFCaption";
import EWASTEWFReason from "./components/EWASTEWFReason";
import EWASTEDocuments from "./pageComponents/EWASTEDocuments";
import EWASTEDocumentView from "./components/EWASTEDocumentView";

// Object containing all components to be registered with the Digit Component Registry Service
const componentsToRegister = {
  EWCreatewaste: EWCreate, // Component for creating waste
  EWCheckPage, // Component for checking details
  EWASTEProductDetails, // Component for product details
  EWASTEOwnerInfo, // Component for owner information
  EWASTEProductList, // Component for product list
  EWASTECitizenAddress, // Component for citizen address
  EWASTESelectPincode, // Component for selecting pincode
  EWASTESelectAddress, // Component for selecting address
  EWASTEAcknowledgement, // Component for acknowledgement
  EWASTEMyApplications, // Component for citizen's applications
  EWASTEDocuments, // Component for documents
  EWASTECitizenApplicationDetails, // Component for citizen application details
  EWASTEWFCaption, // Component for workflow caption
  EWASTEWFReason, // Component for workflow reason
  Inbox, // Component for employee inbox
  EmployeeApp, // Main employee application
  EWApplicationDetails: EWApplicationDetails, // Component for application details
  EwService, // Service for EW module
  EWASTEDocumentView, // Component for viewing documents
};

// Function to register components with the Digit Component Registry Service
const addComponentsToRegistry = () => {
  Object.entries(componentsToRegister).forEach(([key, value]) => {
    Digit.ComponentRegistryService.setComponent(key, value);
  });
};

// Main module component for EW (E-Waste) module
export const EWModule = ({ stateCode, userType, tenants }) => {
  const { path, url } = useRouteMatch(); // Hook to get route match details

  const moduleCode = "EW"; // Module code for E-Waste
  const language = Digit.StoreData.getCurrentLanguage(); // Get current language from store
  const { isLoading, data: store } = Digit.Services.useStore({ stateCode, moduleCode, language }); // Fetch store data

  addComponentsToRegistry(); // Register components

  Digit.SessionStorage.set("EW_TENANTS", tenants); // Store tenant information in session storage

  // Effect to load localization for employee user type
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

  // Render EmployeeApp for employee user type, otherwise render CitizenApp
  if (userType === "employee") {
    return <EmployeeApp path={path} url={url} userType={userType} />;
  } else return <CitizenApp />;
};

// Component to render links for the EW module
export const EWLinks = ({ matchPath, userType }) => {
  const { t } = useTranslation(); // Hook for translations
  const [params, setParams, clearParams] = Digit.Hooks.useSessionStorage("EW", {}); // Session storage hook for EW module

  useEffect(() => {
    clearParams(); // Clear session storage parameters on mount
  }, []);

  const links = []; // Placeholder for links

  // Render CitizenHomeCard with links and icon
  return <CitizenHomeCard header={t("ACTION_TEST_EW")} links={links} Icon={() => <PTIcon className="fill-path-primary-main" />} />;
};

// Object containing additional components for the EW module
export const EWComponents = {
  EWCard, // Card component for EW
  EWModule, // Main module component
  EWLinks, // Links component
  EW_INBOX_FILTER: (props) => <InboxFilter {...props} />, // Inbox filter component
  EWInboxTableConfig: TableConfig, // Table configuration for inbox
};
