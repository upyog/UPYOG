import { CitizenHomeCard, PTIcon,ApplicantDetails, AddressDetails } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useEffect } from "react";
import { useTranslation } from "react-i18next";
import WTCreate from "./pages/citizen/Create";
import CitizenApp from "./pages/citizen";
import InfoPage from "./pageComponents/InfoPage";
import RequestDetails from "./pageComponents/RequestDetails";
import WTCheckPage from "./pages/citizen/Create/CheckPage";
import WTAcknowledgement from "./pages/citizen/Create/WTAcknowledgement";
import WTCard from "./components/WTCard";
import EmployeeApp from "./pages/employee";
import { WTMyApplications } from "./pages/citizen/MyApplications";
import WTApplicationDetails from "./pages/citizen/WTApplicationDetails";
import {TableConfig} from "./config/inbox-table-config";
import InboxFilter from "./components/inbox/NewInboxFilter";
import ApplicationDetails from "./pages/employee/ApplicationDetails";
import WFApplicationTimeline from "./pageComponents/WFApplicationTimeline";
import WFCaption from "./pageComponents/WFCaption";
import Inbox from "./pages/employee/Inbox";
import WTCitizenCard from "./components/WTCitizenCard";
import MTCard from "./components/MTCard";
import TPCard from "./components/TPCard";
import ServiceTypes from "./pageComponents/ServiceTypes";
import ToiletRequestDetails from "./pageComponents/ToiletRequestDetails";
import TreePruningRequestDetails from "./pageComponents/TreePruningRequestDetails";
import MTAcknowledgement from "./pages/citizen/Create/MTAcknowledgement";
import TPAcknowledgement from "./pages/citizen/Create/TPAcknowledgement";
import MTApplicationDetails from "./pages/citizen/MTApplicationDetails";
import TPApplicationDetails from "./pages/citizen/TPApplicationDetails";
import MTCitizenCard from "./components/MTCitizenCard";
import { ReportSearchApplication, EnhancedReport } from "@nudmcdgnpm/digit-ui-module-reports";
const componentsToRegister = {
    ApplicantDetails,
    AddressDetails,
    WTCreate,
    InfoPage,
    RequestDetails,
    WTCheckPage,
    WTAcknowledgement,
    WTApplicationDetails: WTApplicationDetails,
    MTApplicationDetails: MTApplicationDetails,
    TPApplicationDetails: TPApplicationDetails,
    WTMyApplications: WTMyApplications,
    ApplicationDetails: ApplicationDetails,
    WFApplicationTimeline: WFApplicationTimeline,
    WFCaption,
    WTEmpInbox: Inbox,
    WTCitizenCard: WTCitizenCard,
    ServiceTypes,
    ToiletRequestDetails,
    TreePruningRequestDetails,
    MTAcknowledgement,
    TPAcknowledgement,
    EnhancedReport,
    ReportSearchApplication,
    MTCitizenCard: MTCitizenCard,
  };
  
  // function to register the component as per standard 
  const addComponentsToRegistry = () => {
    Object.entries(componentsToRegister).forEach(([key, value]) => {
      Digit.ComponentRegistryService.setComponent(key, value);
    });
  };

  // Parent component of module
  export const WTModule = ({ stateCode, userType, tenants }) => {
    const { path, url } = Digit.Hooks.useModuleBasePath();
    const moduleCode = "WT";
    const language = Digit.StoreData.getCurrentLanguage();
    const { isLoading, data: store } = Digit.Services.useStore({ stateCode, moduleCode, language });
    addComponentsToRegistry();
    Digit.SessionStorage.set("WT_TENANTS", tenants);

// Fetch localization data if the user is an employee if the user type is employee, fetch localization data for the current tenant and saved in db
    useEffect(() => {
      if (userType === "employee") {
        Digit.LocalizationService.getLocale({
          modules: [`rainmaker-${Digit.ULBService.getCurrentTenantId()}`],
          locale: Digit.StoreData.getCurrentLanguage(),
          tenantId: Digit.ULBService.getCurrentTenantId(),
        });
      }
    }, [userType]);
  
    if (userType === "employee") {
      return <EmployeeApp path={path} url={url} userType={userType} />;
    } else return <CitizenApp />;
  };
  
  export const WTLinks = ({ matchPath, userType }) => {
    const { t } = useTranslation();
    const [params, setParams, clearParams] = Digit.Hooks.useSessionStorage("WT", {});
  
    useEffect(() => {
      clearParams();
    }, []);
  
    const links = [ // need to check the links, will be removed later if not needed
      
      {
        link: `/wt`,
        i18nKey: t("WT_REAQUEST_TANKER"),
      },
      {
        link: `/wt/status`,
        i18nKey: t("WT_VIEW_APPLICATIONS"),
      },
    ];
  
    return <CitizenHomeCard header={t("WATER_TANKER_SERVICE")} links={links} Icon={() => <PTIcon className="fill-path-primary-main" />} />;
  };
  
  // export the components outside of module to enable and access of module
  export const WTComponents = {
    WTCard,
    WTModule,
    MTCard, 
    TPCard,
    WTLinks,
    WT_INBOX_FILTER: (props) => <InboxFilter {...props} />,
    WTInboxTableConfig: TableConfig,
  };