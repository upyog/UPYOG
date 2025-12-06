import { Header, CitizenHomeCard,PGRIcon } from "@upyog/digit-ui-react-components";
import React, { useEffect } from "react";
import { useTranslation } from "react-i18next";
import { useRouteMatch } from "react-router-dom";
import CitizenApp from "./pages/citizen";
import NewGrievance from "./pageComponents/NewGrievance";
import PGRAICreate from "./pages/citizen/Create";
import {PGRAIMyApplications} from "./pages/citizen/Create/PGRAIMyApplications";
import PGRApplication from "./pages/citizen/Create/PGRAIMyApplications/PGRAI-application";
import PGRApplicationDetails from "./pages/citizen/PGRApplicationDetails";
import PGRAICard from "./components/PGRAICard";
import { TableConfig } from "./config/inbox-table-config";
import EmployeeApp from "./pages/employee";
import InboxFilter from "./components/inbox/NewInboxFilter";
import { ComplaintDetails } from "./pages/employee/ComplaintDetails";
import WFApplicationTimeline from "./pageComponents/WFApplicationTimeline";
import WFCaption from "./pageComponents/WFCaption";

// Component registry for the PGR_AI module, mapping component names to their implementations.
// Enables dynamic registration and access of components in the application.
const componentsToRegister = {
  NewGrievance,
  PGRAICreate,
  PGRAIMyApplications,
  PGRApplication,
  PGRAIApplicationDetails:ComplaintDetails,
  PGRApplicationDetails:PGRApplicationDetails,
  WFApplicationTimeline,
  WFCaption,
 };

// Function to add components to the registry
const addComponentsToRegistry = () => {
  Object.entries(componentsToRegister).forEach(([key, value]) => {
    Digit.ComponentRegistryService.setComponent(key, value);
  });
};

// Main ADSModule component
export const PGRAIModule = ({ stateCode, userType, tenants }) => {
  const { path, url } = useRouteMatch();

  const moduleCode = "PGRAI";
  const language = Digit.StoreData.getCurrentLanguage();
  const { isLoading, data: store } = Digit.Services.useStore({ stateCode, moduleCode, language });

  addComponentsToRegistry();

  Digit.SessionStorage.set("PGRAI_TENANTS", tenants);

  // Fetch localization data if the user is an employee
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

  // Render different apps based on user type
  if (userType === "employee") {
    return <EmployeeApp path={path} url={url} userType={userType} />;
  } else return <CitizenApp />;
};

// PGR_AILinks component for rendering links in the UI
export const PGRAILinks = ({ matchPath, userType }) => {
  const { t } = useTranslation();
  const [params, setParams, clearParams] = Digit.Hooks.useSessionStorage("PGRAI", {});

  useEffect(() => {
    clearParams();
  }, []);

  const links = [];

  return <CitizenHomeCard header={t("PGRAI")} links={links} Icon={() => <PGRIcon className="fill-path-primary-main" />} />;
};

export const PGRAIComponents = {
  PGRAIModule,
  PGRAILinks,
  PGRAICard,
  PGRAI_INBOX_FILTER: (props) => <InboxFilter {...props} />,
  PGRAIInboxTableConfig:TableConfig
};