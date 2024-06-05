import { Header, CitizenHomeCard, PTIcon } from "@upyog/digit-ui-react-components";
import React, { useEffect } from "react";
import { useTranslation } from "react-i18next";
import { useRouteMatch } from "react-router-dom";
import PTRPetdetails from "./pageComponents/PTRPetdetails";
import PTROwnerDetails from "./pageComponents/PTROwnerDetails";
import CHBSlotDetails from "./pageComponents/CHBSlotDetails";
import PTRDocumentUpload from "./pageComponents/PTRDocumentUpload";
import PTRSelectStreet from "./pageComponents/PTRSelectStreet";
import PTRCreate from "./pages/citizen/Create";
import CHBCitizenDetails from "./pageComponents/CHBCitizenDetails";
import CHBBankDetails from "./pageComponents/CHBBankDetails";
import PTRSelectPincode from "./pageComponents/PTRSelectPincode";
import PTRSelectAddress from "./pageComponents/PTRSelectAddress";
import CHBDocumentDetails from "./pageComponents/CHBDocumentDetails";
import CHBSearchHall from "./pageComponents/CHBSearchHall";
import PTRWFApplicationTimeline from "./pageComponents/PTRWFApplicationTimeline";
import CitizenApp from "./pages/citizen";
import PTRCheckPage from "./pages/citizen/Create/CheckPage";
import PTRAcknowledgement from "./pages/citizen/Create/PTRAcknowledgement";
import { PTRMyApplications } from "./pages/citizen/PTRMyApplications";
import PTRApplicationDetails from "./pages/citizen/PTRApplicationDetails";
import PTRWFCaption from "./pageComponents/PTRWFCaption";
import PTRWFReason from "./pageComponents/PTRWFReason";
import EmployeeApp from "./pages/employee";
import CHBCard from "./components/CHBCard";
import InboxFilter from "./components/inbox/NewInboxFilter";
import { TableConfig } from "./config/inbox-table-config";
import {ChbTableConfig} from "./config/chb-table-config";
import NewApplication from "./pages/employee/NewApplication";
import ApplicationDetails from "./pages/employee/ApplicationDetails";
import Response from "./pages/Response";
import SelectOtp from "../../core/src/pages/citizen/Login/SelectOtp";
import CitizenFeedback from "@upyog/digit-ui-module-core/src/components/CitizenFeedback";
import AcknowledgementCF from "@upyog/digit-ui-module-core/src/components/AcknowledgementCF";



const componentsToRegister = {
  PTRCheckPage,
  PTRAcknowledgement,
  PTRWFCaption,
  PTRWFReason,
  PTRNewApplication: NewApplication,
  ApplicationDetails: ApplicationDetails,
  PTRResponse: Response,
  PTRMyApplications: PTRMyApplications,
  PTRApplicationDetails: PTRApplicationDetails,
  SelectOtp, // To-do: Temp fix, Need to check why not working if selectOtp module is already imported from core module
  AcknowledgementCF,
  CitizenFeedback,
  PTRPetdetails,
  PTROwnerDetails,
  PTRCreatePet: PTRCreate,
  PTRDocumentUpload,
  PTRSelectStreet,
  CHBCitizenDetails,
  CHBSlotDetails,
  CHBBankDetails,
  PTRSelectPincode,
  PTRSelectAddress,
  CHBDocumentDetails,
  CHBSearchHall,
  PTRWFApplicationTimeline,
 
  
  
  

 
};

const addComponentsToRegistry = () => {
  Object.entries(componentsToRegister).forEach(([key, value]) => {
    Digit.ComponentRegistryService.setComponent(key, value);
  });
};


export const CHBModule = ({ stateCode, userType, tenants }) => {
  const { path, url } = useRouteMatch();

  const moduleCode = "CHB";
  const language = Digit.StoreData.getCurrentLanguage();
  const { isLoading, data: store } = Digit.Services.useStore({ stateCode, moduleCode, language });

  addComponentsToRegistry();

  Digit.SessionStorage.set("CHB_TENANTS", tenants);

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

export const CHBLinks = ({ matchPath, userType }) => {
  const { t } = useTranslation();
  const [params, setParams, clearParams] = Digit.Hooks.useSessionStorage("CHB", {});

  useEffect(() => {
    clearParams();
  }, []);

  const links = [
    
    {
      link: `${matchPath}chb/bookHall`,
      i18nKey: t("CHB_SEARCH_HALL_HEADER"),
    },
    
    {
      link: `${matchPath}/chb/myBookings`,
      i18nKey: t("CHB_MY_APPLICATIONS_HEADER"),
    },
    
    {
      link: `${matchPath}/howItWorks`,
      i18nKey: t("PTR_HOW_IT_WORKS"),
    },
    {
      link: `${matchPath}/faqs`,
      i18nKey: t("PTR_FAQ_S"),
    },
  ];

  return <CitizenHomeCard header={t("COMMUNITY_HALL_BOOKING")} links={links} Icon={() => <PTIcon className="fill-path-primary-main" />} />;
};

export const CHBComponents = {
  CHBCard,
  CHBModule,
  CHBLinks,
  PTR_INBOX_FILTER: (props) => <InboxFilter {...props} />,
  PTRInboxTableConfig: TableConfig,
  // CHBTableConfig:ChbTableConfig,
};