import { CitizenHomeCard, PTIcon, ApplicantDetails,AddressDetails } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useEffect } from "react";
import { useTranslation } from "react-i18next";
import { useRouteMatch } from "react-router-dom";
import CndCreate from "./pages/citizen/Create";
import CitizenApp from "./pages/citizen";
import CndRequirementDetails from "./pageComponents/CndRequirementDetails"; 
import RequestPickup from "./pageComponents/RequestPickup";
import LocationPopup from "./components/LocationPopup";
import { LocationDetails } from "./components/LocationDetails";
import WasteType from "./pageComponents/WasteType";
import MultiSelectDropdown from "./pageComponents/MultiSelectDropdown";
import CndCheckPage from "./pages/citizen/Create/CndCheckPage";
import CndAcknowledgement from "./pages/citizen/Create/CndAcknowledgement";
import { MyRequests } from "./pages/citizen/MyRequests";
import CndApplicationDetails from "./pages/citizen/CndApplicationDetails";
import CNDCard from "./components/CNDCard";
import EmployeeApp from "./pages/employee";
import { TableConfig } from "./config/inbox-table-config";
import InboxFilter from "./components/inbox/NewInboxFilter";
import CNDApplicationTimeLine from "./components/CNDApplicationTimeLine";
import ApplicationDetails from "./pages/employee/ApplicationDetails";
import EditCreate from "./pages/employee/Edit/EditCreate";
import { ApplicationProvider } from "./pages/employee/Edit/ApplicationContext";
import EditSubmissionResponse from "./pages/employee/Edit/EditSubmissionResponse";
import PropertyNature from "./pageComponents/PropertyNature";
import CNDVendorCard from "./components/CNDVendorCard";
import FacilityCentreCreationDetails from "./pages/employee/FacilityCentre/FacilityCentreCreate";
import PickupArrivalDetails from "./pageComponents/PickupArrivalDetails";
import DisposeDetails from "./pageComponents/DisposeDetails";
import FacilitySubmissionResponse from "./pages/employee/FacilityCentre/FacilitySubmissionResponse";
import Address from "./pageComponents/Address";

const componentsToRegister = {
  ApplicantDetails,
  CndCreate,
  CndRequirementDetails,
  LocationPopup,
  LocationDetails,
  RequestPickup,
  WasteType,
  MultiSelectDropdown,
  AddressDetails,
  CndCheckPage,
  CndAcknowledgement,
  MyRequests,
  CndApplicationDetails,
  CNDApplicationTimeLine,
  ApplicationDetails,
  EditCreate,
  ApplicationProvider,
  EditSubmissionResponse,
  PropertyNature,
  CNDVendorCard,
  FacilityCentreCreationDetails,
  PickupArrivalDetails,
  DisposeDetails,
  FacilitySubmissionResponse,
  Address
  };
  
  // function to register the component as per standard 
  const addComponentsToRegistry = () => {
    Object.entries(componentsToRegister).forEach(([key, value]) => {
      Digit.ComponentRegistryService.setComponent(key, value);
    });
  };

  // Parent component of module
  export const CNDModule = ({stateCode, userType, tenants }) => {
    const { path, url } = useRouteMatch();
    const moduleCode = "CND";
    const language = Digit.StoreData.getCurrentLanguage();
    const { isLoading, data: store } = Digit.Services.useStore({ stateCode, moduleCode, language });
    addComponentsToRegistry();
    Digit.SessionStorage.set("CND_TENANTS", tenants);
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
  
  export const CNDLinks = ({ matchPath, userType }) => {
    const { t } = useTranslation();
    const links = [ // need to check the links, will be removed later if not needed
      {
        link: `${matchPath}/cnd/apply`,
        i18nKey: t("CND_APPLY"),
      },
      {
        link: `${matchPath}/cnd/my-request`,
        i18nKey: t("CND_SEARCH_APPLICATION"),
      }
    ];
  
    return <CitizenHomeCard header={t("CND_SERVICES")} links={links} Icon={() => <PTIcon className="fill-path-primary-main" />} />;
  };
  
  // export the components outside of module to enable and access of module
  export const CNDComponents = {
    CNDModule, 
    CNDLinks,
    CNDCard,
    CND_INBOX_FILTERS: (props) => <InboxFilter {...props} />,
    CNDInboxConfig:TableConfig
  };