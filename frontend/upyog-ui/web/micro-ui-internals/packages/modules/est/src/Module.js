import { CitizenHomeCard, PropertyHouse } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import CitizenApp from "./pages/citizen";
import ESTCard from "./components/ESTCard";
import EmployeeApp from "./pages/employee";
import MyApplications from "./pages/citizen/MyApplications";
import { ESTPaymentHistory } from "./pages/citizen/PaymentHistory";
import NewRegistration from "./PageComponents/ESTNEWRegistration";
import ESTDynamicCheckPage from "./pages/employee/Create/ESTDynamicCheckPage";
import ESTRegCreate from "./pages/employee/Create";
import ESTAcknowledgement from "./pages/employee/Create/ESTAcknowledgement";
import ESTAllotmentAcknowledgement from "./pages/employee/Create/ESTAllotmentAcknowledgement";
import ESTAssignAssetCreate from "./pages/employee/Create/AssignAssetIndex";
import ESTAssignAstRequiredDoc from "./PageComponents/ESTAssignAstRequiredDoc";
import ESTAssignAssets from "./PageComponents/ESTAssignAssets";
import ESTApplicationDetails from "./pages/citizen/ESTApplicationDetails";

const componentsToRegister = {
  MyApplications,
  ESTPaymentHistory,
  NewRegistration,
  ESTRegCreate,
  ESTDynamicCheckPage,
  ESTAcknowledgement,
  ESTAllotmentAcknowledgement,
  ESTAssignAssetCreate,
  ESTAssignAstRequiredDoc,
  ESTAssignAssets,
  ESTApplicationDetails,
};

let componentsRegistered = false;

const addComponentsToRegistry = () => {
  if (componentsRegistered || !Digit?.ComponentRegistryService?.setComponent) return;
  Object.entries(componentsToRegister).forEach(([key, value]) => {
    Digit.ComponentRegistryService.setComponent(key, value);
  });
  componentsRegistered = true;
};

export const ESTModule = ({ stateCode, userType, tenants }) => {
  const { path } = Digit.Hooks.useModuleBasePath();
  const moduleCode = "EST";
  const language = Digit.StoreData.getCurrentLanguage();
  Digit.Services.useStore({ stateCode, moduleCode, language });
  addComponentsToRegistry();
  Digit.SessionStorage.set("EST_TENANTS", tenants);

  if (userType === "employee") {
    return <EmployeeApp path={path} userType={userType} />;
  }
  return <CitizenApp />;
};

export const ESTLinks = () => {
  const { t } = useTranslation();

  const links = [
    {
      link: `/my-applications`,
      i18nKey: t("EST_MY_APPLICATIONS"),
    },
    {
      link: `/payment-history`,
      i18nKey: t("EST_PAYMENT_HISTORY"),
    },
  ];

  return (
    <CitizenHomeCard
      header={t("ESTATE_MANAGEMENT")}
      links={links}
      Icon={() => <PropertyHouse />}
    />
  );
};

export const ESTComponents = {
  ESTModule,
  ESTLinks,
  ESTCard,
};
