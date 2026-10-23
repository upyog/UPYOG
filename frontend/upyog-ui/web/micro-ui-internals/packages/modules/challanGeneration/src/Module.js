import { CitizenHomeCard, Loader, PTIcon } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useEffect } from "react";
import InboxFilter from "./components/inbox/NewInboxFilter";
import ChallanGenerationCard from "./components/ChallanGenerationCard";
import EmployeeChallan from "./EmployeeChallan";
import ConsumerDetails from "./pageComponents/ConsumerDetails";
import ServiceDetails from "./pageComponents/ServiceDetails";
import EmployeeApp from "./pages/employee";
import SearchReceipt from "./pages/employee/SearchReceipt";
import SearchChallan from "./pages/employee/SearchChallan";
import ChallanStepperForm from "./pageComponents/ChallanStepper/ChallanStepperForm";
import OffenceDetails from "./pageComponents/OffenceDetails";
import ChallanDocuments from "./pageComponents/ChallanDocuments";
import getRootReducer from "../redux/reducer";
import ChallanResponseCitizen from "./components/ChallanResponseCitizen";
import ChallanApplicationDetails from "./pages/employee/ChallanApplicationDetails";

/**
 * ChallanGenerationModule:
 * - Entry point for challan module (employee & citizen)
 * - Registers components and handles routing initialization
 */

export const ChallanGenerationModule = ({ stateCode, userType, tenants }) => {
  const moduleCode = "UC";
  const language = Digit.StoreData.getCurrentLanguage();

  const { isLoading, data: store } = Digit.Services.useStore({
    stateCode,
    moduleCode,
    language,
  });

  
  const { path, url } = Digit.Hooks.useModuleBasePath();

  Digit.SessionStorage.set("ChallanGeneration_TENANTS", tenants);

  if (isLoading) {
    return <Loader />;
  }

  if (userType === "employee") {
    return <EmployeeApp path={path} url={url} userType={userType} />;
  }

  return <CitizenApp />;
};

export const ChallanReducers = getRootReducer;

const componentsToRegister = {
  ConsumerDetails,
  ServiceDetails,
  ChallanGenerationCard,
  ChallanGenerationModule,
  MCollectEmployeeChallan: EmployeeChallan,
  SearchReceipt,
  SearchChallan,
  MCOLLECT_INBOX_FILTER: (props) => <InboxFilter {...props} />,
  ChallanStepperForm,
  ChallanApplicationDetails,
  OffenceDetails,
  ChallanDocuments,
  ChallanResponseCitizen
};

export const initChallanGenerationComponents = () => {
  Object.entries(componentsToRegister).forEach(([key, value]) => {
    Digit.ComponentRegistryService.setComponent(key, value);
  });
};
