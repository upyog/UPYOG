import { Header, CitizenHomeCard, PTIcon, PropertySearch } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useEffect, useState, createContext } from "react";
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
import PTREditDetails from "./pageComponents/PTREditDetails";
import EditDocumentPTR from "./pageComponents/EditDocumentPTR";


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
  PTREditDetails,
  EditDocumentPTR,
  PropertySearch,
  PTRRenewApplication: RenewApplication
};

// used context for renewApplication
export const ApplicationContext = createContext();

const addComponentsToRegistry = () => {
  Object.entries(componentsToRegister).forEach(([key, value]) => {
    Digit.ComponentRegistryService.setComponent(key, value);
  });
};


export const PTRModule = ({ stateCode, userType, tenants }) => {
  const { path, url } = useRouteMatch();
  const [params, setParams, clearParams] = Digit.Hooks.useSessionStorage("PTR_APPLICATION_ID", {});

  // These usestates are for the applicationcontext
  const [applicationId, setApplicationId] = useState("");
  const [applicationData, setApplicationData] = useState()
  const [apptype, setApptype] = useState("NEWAPPLICATION")
  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true);


  const { isError, error, data: app_data } = Digit.Hooks.ptr.usePTRSearch(
    {
      tenantId,
      filters: { applicationNumber: applicationId },
    },
  );

  useEffect(() => {
    if (!applicationId) setApplicationId(params) // to set applicationid from params after refresh of page so pass in search hook
    if (app_data && apptype === "RENEWAPPLICATION") {
      setParams(applicationId)
      setApplicationData(app_data?.PetRegistrationApplications[0] || {});
    }
     else {
      setApplicationData({})
    }
  }, [app_data, apptype, applicationId]);


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

  return ( //application context provider to pass context data to all child components
    <ApplicationContext.Provider value={{ applicationId, setApplicationId, applicationData, apptype, setApptype}}>
      {userType === "employee" ? (
        <EmployeeApp path={path} url={url} userType={userType} />
      ) : (
        <CitizenApp />
      )}
    </ApplicationContext.Provider>
  )
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
  PTR_INBOX_FILTER: (props) => <InboxFilter {...props} />,
  PTRInboxTableConfig: TableConfig,
};