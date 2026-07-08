import { CitizenHomeCard, PropertyHouse } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import CitizenApp from "./pages/citizen";
import ESTCard from "./components/ESTCard";
import EmployeeApp from "./pages/employee";
import MyApplications from "./pages/citizen/MyApplications";
import { ESTPaymentHistory } from "./pages/citizen/PaymentHistory";
import ESTManageProperties from "./PageComponents/ESTManageProperties";
import NewRegistration from "./PageComponents/ESTNEWRegistration";
import ESTRegCheckPage from "./pages/employee/Create/ESTRegCheckPage";
import ESTRegCreate from "./pages/employee/Create";
import ESTAcknowledgement from "./pages/employee/Create/ESTAcknowledgement";
import ESTAllotmentAcknowledgement from "./pages/employee/Create/ESTAllotmentAcknowledgement";
import ESTAssignAssetCreate from "./pages/employee/Create/AssignAssetIndex";
import ESTAssignAstRequiredDoc from "./PageComponents/ESTAssignAstRequiredDoc";
import ESTAssignAssets from "./PageComponents/ESTAssignAssets";
import ESTAssignAssetsCheckPage from "./pages/employee/Create/ESTAssignAssetsCheckPage";
import ESTDesktopInbox from "./components/ESTDesktopInbox";
import { TableConfig } from "./config/Create/inbox-table-config";
import InboxFilter from "./components/inbox/NewInboxFilter";
import ESTApplicationDetails from "./pages/citizen/ESTApplicationDetails";


const componentsToRegister = {
  MyApplications,
  ESTPaymentHistory,
  NewRegistration,
  ESTManageProperties,
  ESTRegCreate,
  ESTRegCheckPage,
  ESTAcknowledgement,
  ESTAllotmentAcknowledgement,
  ESTAssignAssetCreate,
  ESTAssignAstRequiredDoc,
  ESTAssignAssets,
  ESTDesktopInbox,
  TableConfig,
  ESTAssignAssetsCheckPage,
  ESTApplicationDetails,
};

const addComponentsToRegistry = () => {
  Object.entries(componentsToRegister).forEach(([key, value]) => {
    Digit.ComponentRegistryService.setComponent(key, value);
  });
};

addComponentsToRegistry();

export const ESTModule = ({ stateCode, userType, tenants }) => {
  const { path, url } = Digit.Hooks.useModuleBasePath();
  const moduleCode = "EST";
  const language = Digit.StoreData.getCurrentLanguage();
  const { isLoading, data: store } = Digit.Services.useStore({
    stateCode,
    moduleCode,
    language,
  });
  Digit.SessionStorage.set("EST_TENANTS", tenants);

  if (userType === "employee") {
    return <EmployeeApp path={path} url={url} userType={userType} />;
  } else {
    return <CitizenApp path={path} />;
  }
};

export const ESTLinks = ({ matchPath, userType }) => {
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
  EST_INBOX_FILTER: (props) => <InboxFilter {...props} />,
  ESTInboxTableConfig: TableConfig
};