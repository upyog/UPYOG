import { Header, CitizenHomeCard, PTIcon } from "@nudmcdgnpm/digit-ui-react-components";
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

const componentsToRegister = {
  EWCreatewaste: EWCreate,
  EWCheckPage,
  EWASTEProductDetails,
  EWASTEOwnerInfo,
  EWASTEProductList,
  EWASTECitizenAddress,
  EWASTESelectPincode,
  EWASTESelectAddress,
  EWASTEAcknowledgement,
  EWASTEMyApplications,
  EWASTEDocuments,
  EWASTECitizenApplicationDetails,
  EWASTEWFCaption,
  EWASTEWFReason,
  Inbox,
  EmployeeApp,
  EWApplicationDetails: EWApplicationDetails,
  EwService,
  EWASTEDocumentView,
};

const addComponentsToRegistry = () => {
  Object.entries(componentsToRegister).forEach(([key, value]) => {
    Digit.ComponentRegistryService.setComponent(key, value);
  });
};

export const EWModule = ({ stateCode, userType, tenants }) => {
  const { path, url } = useRouteMatch();

  const moduleCode = "EW";
  const language = Digit.StoreData.getCurrentLanguage();
  const { isLoading, data: store } = Digit.Services.useStore({ stateCode, moduleCode, language });

  addComponentsToRegistry();

  Digit.SessionStorage.set("EW_TENANTS", tenants);

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

export const EWLinks = ({ matchPath, userType }) => {
  const { t } = useTranslation();
  const [params, setParams, clearParams] = Digit.Hooks.useSessionStorage("EW", {});

  useEffect(() => {
    clearParams();
  }, []);

  const links = [];

  return <CitizenHomeCard header={t("ACTION_TEST_EW")} links={links} Icon={() => <PTIcon className="fill-path-primary-main" />} />;
};

export const EWComponents = {
  EWCard,
  EWModule,
  EWLinks,
  EW_INBOX_FILTER: (props) => <InboxFilter {...props} />,
  EWInboxTableConfig: TableConfig,
};
