<<<<<<< HEAD
import { Header, CitizenHomeCard, PTRIcon } from "@upyog/digit-ui-react-components";
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
=======
import { CitizenHomeCard, PTIcon } from "@upyog/digit-ui-react-components";
import React, { useEffect } from "react";
import { useTranslation } from "react-i18next";
import { useRouteMatch } from "react-router-dom";

import PTRCitizenPet from "./pageComponents/PTRCitizenPet";

import PTRCreate from "./pages/citizen/Create";
import PTRCitizenDetails from "./pageComponents/PTRCitizenDetails";
>>>>>>> master-LTS
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
<<<<<<< HEAD
import PTRWFReason from "./pageComponents/PTRWFReason";
=======
>>>>>>> master-LTS
import EmployeeApp from "./pages/employee";
import PTRCard from "./components/PTRCard";
import InboxFilter from "./components/inbox/NewInboxFilter";
import { TableConfig } from "./config/inbox-table-config";
<<<<<<< HEAD
import NewApplication from "./pages/employee/NewApplication";
import ApplicationDetails from "./pages/employee/ApplicationDetails";
import Response from "./pages/Response";
import SelectOtp from "../../core/src/pages/citizen/Login/SelectOtp";
import CitizenFeedback from "../../core/src/components/CitizenFeedback";
import AcknowledgementCF from "../../core/src/components/AcknowledgementCF";



=======
import ApplicationDetails from "./pages/employee/ApplicationDetails";
import NewApplication from "./pages/employee/NewApplication";


// Registering all components to be used in the module
>>>>>>> master-LTS
const componentsToRegister = {
  PTRCheckPage,
  PTRAcknowledgement,
  PTRWFCaption,
<<<<<<< HEAD
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
  PTRCitizenDetails,
  PTRCitizenPet,
  PTRCitizenAddress,
  PTRSelectPincode,
=======
  PTRNewApplication: NewApplication,
  ApplicationDetails: ApplicationDetails,
  PTRMyApplications: PTRMyApplications,
  PTRApplicationDetails: PTRApplicationDetails,
  PTRCreatePet: PTRCreate,
  PTRCitizenDetails,
  PTRCitizenPet,
>>>>>>> master-LTS
  PTRSelectAddress,
  PTRSelectProofIdentity,
  PTRServiceDoc,
  PTRWFApplicationTimeline,
<<<<<<< HEAD
 
  
  
  

 
};

=======
};

// function of component registry to add entries in the registry
>>>>>>> master-LTS
const addComponentsToRegistry = () => {
  Object.entries(componentsToRegister).forEach(([key, value]) => {
    Digit.ComponentRegistryService.setComponent(key, value);
  });
};

<<<<<<< HEAD

export const PTRModule = ({ stateCode, userType, tenants }) => {
  const { path, url } = useRouteMatch();

=======
// Main module function to get to the routes file of citizen or employee module
export const PTRModule = ({ stateCode, userType, tenants }) => {
  const { path, url } = useRouteMatch();
>>>>>>> master-LTS
  const moduleCode = "PTR";
  const language = Digit.StoreData.getCurrentLanguage();
  const { isLoading, data: store } = Digit.Services.useStore({ stateCode, moduleCode, language });

  addComponentsToRegistry();

<<<<<<< HEAD
  Digit.SessionStorage.set("PTR_TENANTS", tenants);

=======
  
  Digit.SessionStorage.set("PTR_TENANTS", tenants);  // setting a value in a session storage object

  // loads localization settings for an employee based on the current tenant and language when the component mounts
>>>>>>> master-LTS
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

<<<<<<< HEAD
=======
    // Displaying employee module if userType is 'employee', otherwise displaying citizen module
>>>>>>> master-LTS
  if (userType === "employee") {
    return <EmployeeApp path={path} url={url} userType={userType} />;
  } else return <CitizenApp />;
};

<<<<<<< HEAD
=======
// Function to display home card links for citizens
>>>>>>> master-LTS
export const PTRLinks = ({ matchPath, userType }) => {
  const { t } = useTranslation();
  const [params, setParams, clearParams] = Digit.Hooks.useSessionStorage("PTR_PET", {});

  useEffect(() => {
    clearParams();
  }, []);

<<<<<<< HEAD
  const links = [
    
    {
      link: `${matchPath}/ptr/petservice/new-application`,
      i18nKey: t("PTR_CREATE_PET_APPLICATION"),
    },
    
    {
      link: `${matchPath}/ptr/petservice/my-application`,
      i18nKey: t("PTR_MY_APPLICATIONS_HEADER"),
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

  return <CitizenHomeCard header={t("ACTION_TEST_PTR")} links={links} Icon={() => <PTRIcon className="fill-path-primary-main" />} />;
};

=======
  const links = [];
  
  // returns CitizenHomeCard component while passing links to be used in card and an Icon to display
  return <CitizenHomeCard header={t("ACTION_TEST_PTR")} links={links} Icon={() => <PTIcon className="fill-path-primary-main" />} />;
};

// Components Exported
>>>>>>> master-LTS
export const PTRComponents = {
  PTRCard,
  PTRModule,
  PTRLinks,
<<<<<<< HEAD
  PT_INBOX_FILTER: (props) => <InboxFilter {...props} />,
=======
  PTR_INBOX_FILTER: (props) => <InboxFilter {...props} />,
>>>>>>> master-LTS
  PTRInboxTableConfig: TableConfig,
};