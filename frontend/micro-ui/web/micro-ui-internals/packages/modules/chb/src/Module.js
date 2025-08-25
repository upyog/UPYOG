import { Header, CitizenHomeCard,CHBIcon } from "@upyog/digit-ui-react-components";
import React, { useEffect } from "react";
import { useTranslation } from "react-i18next";
import { useRouteMatch } from "react-router-dom";
import CHBSlotDetails from "./pageComponents/CHBSlotDetails";
import CHBCreate from "./pages/citizen/Create";
import CHBCitizenDetails from "./pageComponents/CHBCitizenDetails";
import CHBBankDetails from "./pageComponents/CHBBankDetails";
import CHBAddressDetails from "./pageComponents/CHBAddressDetails"
import CHBDocumentDetails from "./pageComponents/CHBDocumentDetails";
import CHBSearchHall from "./pageComponents/CHBSearchHall";
import CHBWFApplicationTimeline from "./pageComponents/CHBWFApplicationTimeline";
import CitizenApp from "./pages/citizen";
import CHBCheckPage from "./pages/citizen/Create/CheckPage";
import CHBAcknowledgement from "./pages/citizen/Create/CHBAcknowledgement";
import { CHBMyApplications } from "./pages/citizen/CHBMyApplications"; 
import CHBApplicationDetails from "./pages/citizen/CHBApplicationDetails";
import CHBWFCaption from "./pageComponents/CHBWFCaption";
import CHBWFReason from "./pageComponents/CHBWFReason";
import EmployeeApp from "./pages/employee";
import CHBCard from "./components/CHBCard";
import InboxFilter from "./components/inbox/NewInboxFilter";
import { TableConfig } from "./config/inbox-table-config";
import ApplicationDetails from "./pages/employee/ApplicationDetails";
import Response from "./pages/Response";
import SelectOtp from "../../core/src/pages/citizen/Login/SelectOtp";
import CitizenFeedback from "@upyog/digit-ui-module-core/src/components/CitizenFeedback";
import AcknowledgementCF from "@upyog/digit-ui-module-core/src/components/AcknowledgementCF";
import CHBRequiredDoc from "./pageComponents/CHBRequiredDoc";
import CHBMapView from "./components/CHBMapView";


/**
 * CitizenFeedback Component
 * 
 * This component is responsible for rendering the feedback functionality for citizens in the CHB module.
 * It allows users to provide feedback on their experience with the application or service.
 * 
 * Features:
 * - Displays a feedback form with options for rating and comments.
 * - Submits the feedback data to the backend for processing.
 * - Provides a user-friendly interface for collecting feedback.
 * 
 * Props:
 * - `applicationId`: The ID of the application for which feedback is being provided.
 * - `onSubmit`: Callback function triggered when the feedback form is submitted.
 * - `onClose`: Callback function triggered when the feedback form is closed.
 * 
 * Hooks:
 * - `useTranslation`: Provides the `t` function for internationalization.
 * 
 * Logic:
 * - Displays a rating component for users to rate their experience.
 * - Includes a text area for users to provide additional comments.
 * - Validates the feedback form before submission.
 * - Calls the `onSubmit` callback with the feedback data when the form is submitted.
 * 
 * Returns:
 * - A feedback form component that allows users to submit their ratings and comments.
 */
const componentsToRegister = {
  CHBCheckPage,
  CHBAcknowledgement,
  CHBWFCaption,
  CHBWFReason,
  ApplicationDetails: ApplicationDetails,
  CHBResponse: Response,
  CHBMyApplications: CHBMyApplications,
  CHBApplicationDetails: CHBApplicationDetails,
  SelectOtp, // To-do: Temp fix, Need to check why not working if selectOtp module is already imported from core module
  AcknowledgementCF,
  CitizenFeedback,
  CHBCreate: CHBCreate,
  CHBCitizenDetails,
  CHBSlotDetails,
  CHBBankDetails,
  CHBAddressDetails,
  CHBDocumentDetails,
  CHBSearchHall,
  CHBWFApplicationTimeline,
  CHBRequiredDoc,
  CHBMapView
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
    
  ];

  return <CitizenHomeCard header={t("COMMUNITY_HALL_BOOKING")} links={links} Icon={() => <CHBIcon className="fill-path-primary-main" />} />;
};

export const CHBComponents = {
  CHBCard,
  CHBModule, 
  CHBLinks,
  CHB_INBOX_FILTER: (props) => <InboxFilter {...props} />,
  CHBInboxTableConfig: TableConfig,
};