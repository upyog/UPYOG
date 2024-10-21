import { CitizenHomeCard, PTIcon, PropertySearch } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useEffect } from "react";
import { useTranslation } from "react-i18next";
import { useRouteMatch } from "react-router-dom";
import PTRPetdetails from "./pageComponents/PTRPetdetails";
import PTROwnerDetails from "./pageComponents/PTROwnerDetails";
import PTRCitizenPet from "./pageComponents/PTRCitizenPet";
import PTRDocumentUpload from "./pageComponents/PTRDocumentUpload";
import PTRSelectStreet from "./pageComponents/PTRSelectStreet";
import PTRCreate from "./pages/citizen/Create";
import PTRCitizenDetails from "./pageComponents/PTRCitizenDetails";
import PTRCitizenAddress from "./pageComponents/PTRCitizenAddress";
import PTRSelectPincode from "./pageComponents/PTRSelectPincode";
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
import NewApplication from "./pages/employee/NewApplication";
import RenewApplication from "./pages/employee/RenewApplication";
import ApplicationDetails from "./pages/employee/ApplicationDetails";
import Response from "./pages/Response";


const componentsToRegister = {
  PTRCheckPage,
  PTRAcknowledgement,
  PTRWFCaption,
  PTRNewApplication: NewApplication,
  ApplicationDetails: ApplicationDetails,
  PTRResponse: Response,
  PTRMyApplications: PTRMyApplications,
  PTRApplicationDetails: PTRApplicationDetails,
  PTRPetdetails,
  PTROwnerDetails,
  PTRCreatePet: PTRCreate,
  PTRDocumentUpload,
  PTRSelectStreet,
  PTRCitizenDetails,
  PTRCitizenPet,
  PTRCitizenAddress,
  PTRSelectPincode,
  PTRSelectAddress,
  PTRSelectProofIdentity,
  PTRServiceDoc,
  PTRWFApplicationTimeline,
  PropertySearch, // component added for property search
  PTRRenewApplication: RenewApplication //component added for renewapplication
};

// function of component registry to add entries in the registry
const addComponentsToRegistry = () => {
  Object.entries(componentsToRegister).forEach(([key, value]) => {
    Digit.ComponentRegistryService.setComponent(key, value);
  });
};

// Main module function to get to the routes file of citizen or employee module
export const PTRModule = ({ stateCode, userType, tenants }) => {
  const { path, url } = useRouteMatch();
  const moduleCode = "PTR";
  const language = Digit.StoreData.getCurrentLanguage();
  const { isLoading, data: store } = Digit.Services.useStore({ stateCode, moduleCode, language });

  addComponentsToRegistry();

  Digit.SessionStorage.set("PTR_TENANTS", tenants);  // setting a value in a session storage object

  // loads localization settings for an employee based on the current tenant and language when the component mounts
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