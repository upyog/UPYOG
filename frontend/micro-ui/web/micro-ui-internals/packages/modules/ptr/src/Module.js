import { Header, CitizenHomeCard, PTIcon } from "@nudmcdgnpm/digit-ui-react-components";
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
import ApplicationDetails from "./pages/employee/ApplicationDetails";
import Response from "./pages/Response";
import SelectOtp from "../../core/src/pages/citizen/Login/SelectOtp";
import CitizenFeedback from "@nudmcdgnpm/digit-ui-module-core/src/components/CitizenFeedback";
import AcknowledgementCF from "@nudmcdgnpm/digit-ui-module-core/src/components/AcknowledgementCF";



const componentsToRegister = {
  PTRCheckPage,
  PTRAcknowledgement,
  PTRWFCaption,
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
  PTRSelectAddress,
  PTRSelectProofIdentity,
  PTRServiceDoc,
  PTRWFApplicationTimeline,
 
  
  
  

 
};

const addComponentsToRegistry = () => {
  Object.entries(componentsToRegister).forEach(([key, value]) => {
    Digit.ComponentRegistryService.setComponent(key, value);
  });
};


export const PTRModule = ({ stateCode, userType, tenants }) => {
  const { path, url } = useRouteMatch();

  const moduleCode = "PTR";
  const language = Digit.StoreData.getCurrentLanguage();
  const { isLoading, data: store } = Digit.Services.useStore({ stateCode, moduleCode, language });

  addComponentsToRegistry();

  Digit.SessionStorage.set("PTR_TENANTS", tenants);

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

  return <CitizenHomeCard header={t("ACTION_TEST_PTR")} links={links} Icon={() => <PTIcon className="fill-path-primary-main" />} />;
};

export const PTRComponents = {
  PTRCard,
  PTRModule,
  PTRLinks,
  PT_INBOX_FILTER: (props) => <InboxFilter {...props} />,
  PTRInboxTableConfig: TableConfig,
};