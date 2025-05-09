import { Header, CitizenHomeCard,CHBIcon } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useEffect } from "react";
import { useTranslation } from "react-i18next";
import { useRouteMatch } from "react-router-dom";
import CitizenApp from "./pages/citizen";
import NewGrievance from "./pageComponents/NewGrievance";
import PGRAICreate from "./pages/citizen/Create";
import Location from "./pageComponents/Location";
import {PGRAIMyApplications} from "./pages/citizen/Create/PGRAIMyApplications";
import PGRApplication from "./pages/citizen/Create/PGRAIMyApplications/PGRAI-application";
import PGRApplicationDetails from "./pages/citizen/PGRApplicationDetails";

import { AddressDetails } from "@nudmcdgnpm/digit-ui-react-components";
// Component registry for the PGR_AI module, mapping component names to their implementations.
// Enables dynamic registration and access of components in the application.
const componentsToRegister = {
  NewGrievance,
  PGRAICreate,
  Location,
  AddressDetails,
  PGRAIMyApplications,
  PGRApplication,
  PGRApplicationDetails:PGRApplicationDetails,
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

  Digit.SessionStorage.set("ADS_TENANTS", tenants);

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

// ADSLinks component for rendering links in the UI
export const PGRAILinks = ({ matchPath, userType }) => {
  const { t } = useTranslation();
  const [params, setParams, clearParams] = Digit.Hooks.useSessionStorage("PGRAI", {});

  useEffect(() => {
    clearParams();
  }, []);

  const links = [];

  return <CitizenHomeCard header={t("PGRAI")} links={links} Icon={() => <CHBIcon className="fill-path-primary-main" />} />;
};

export const PGRAIComponents = {
  PGRAIModule,
  PGRAILinks
};