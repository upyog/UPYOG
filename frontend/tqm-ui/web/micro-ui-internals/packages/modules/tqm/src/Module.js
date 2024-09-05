import { Loader, TourProvider } from "@egovernments/digit-ui-react-components";
import React, { useEffect } from "react";
import { Link, useRouteMatch } from "react-router-dom";
import TqmCard from "./components/TqmCard";
import EmployeeApp from "./pages/employee";
import CitizenApp from "./pages/citizen";
import { UICustomizations } from "./configs/UICustomizations";
import TQMPendingTask from "./pages/employee/TQMPendingTask";
import TQMLanding from "./pages/employee/TQMLanding";
import { CustomisedHooks } from "./hooks";

// TQM specific components
import TqmInbox from "./pages/employee/inbox/TqmInbox";
import TestDetails from "./pages/employee/test-details/TestDetails";
import { MultiCardReading } from "./components/CardReadings";
import Response from "./pages/employee/Response";
import ViewTestResults from "./pages/employee/test-results/ViewTestResults";
import DetailsTable from "./components/DetailsTable";
import DocumentsPreview from "./components/DocumentsPreview";
import ParameterReadings from "./components/ParameterReadings";
import TQMSummary from "./components/TQMSummary";
import QualityParameter from "./pages/employee/add-test-results/QualityComponent";
import SensorScreen from "./pages/employee/sensor-monitoring/SensorScreen";
import TqmAdminNotification from "./pages/employee/TqmAdminNotification";
import TqmTopBar from "./components/TqmTopBar";
import ChangePlant from "./components/ChangePlant";
import TestStandard from "./pages/employee/add-test-results/TestStandard";

const TQMModule = ({ stateCode, userType, tenants }) => {
  const moduleCode = ["TQM", "mdms"];
  const { path, url } = useRouteMatch();
  const language = Digit.StoreData.getCurrentLanguage();
  const { isLoading, data: store } = Digit.Services.useStore({ stateCode, moduleCode, language });

  if (isLoading) {
    return <Loader />;
  }

  if (userType === "citizen") {
    return <CitizenApp path={path} />;
  } else {
    return (
      <TourProvider>
        <EmployeeApp path={path} url={url} userType={userType} />
      </TourProvider>
    );
  }
};

const componentsToRegister = {
  SensorScreen,
  QualityParameter,
  TqmModule: TQMModule,
  TqmCard,
  TQMPendingTask,
  TQMLanding,
  TqmInbox,
  TestDetails,
  TqmCardReading: MultiCardReading,
  TqmResponse: Response,
  TqmViewTestResults: ViewTestResults,
  TqmDetailsTable: DetailsTable,
  TqmDocumentsPreview: DocumentsPreview,
  TQMSummary,
  TqmParameterReadings: ParameterReadings,
  TqmAdminNotification,
  CustomEmployeeTopBar:TqmTopBar,
  ChangePlant,
  TestStandard
};

const overrideHooks = () => {
  Object.keys(CustomisedHooks).map((ele) => {
    if (ele === "Hooks") {
      Object.keys(CustomisedHooks[ele]).map((hook) => {
        Object.keys(CustomisedHooks[ele][hook]).map((method) => {
          setupHooks(hook, method, CustomisedHooks[ele][hook][method]);
        });
      });
    } else if (ele === "Utils") {
      Object.keys(CustomisedHooks[ele]).map((hook) => {
        Object.keys(CustomisedHooks[ele][hook]).map((method) => {
          setupHooks(hook, method, CustomisedHooks[ele][hook][method], false);
        });
      });
    } else {
      Object.keys(CustomisedHooks[ele]).map((method) => {
        setupLibraries(ele, method, CustomisedHooks[ele][method]);
      });
    }
  });
};

/* To Overide any existing hook we need to use similar method */
const setupHooks = (HookName, HookFunction, method, isHook = true) => {
  window.Digit = window.Digit || {};
  window.Digit[isHook ? "Hooks" : "Utils"] = window.Digit[isHook ? "Hooks" : "Utils"] || {};
  window.Digit[isHook ? "Hooks" : "Utils"][HookName] = window.Digit[isHook ? "Hooks" : "Utils"][HookName] || {};
  window.Digit[isHook ? "Hooks" : "Utils"][HookName][HookFunction] = method;
};

/* To Overide any existing libraries  we need to use similar method */
const setupLibraries = (Library, service, method) => {
  window.Digit = window.Digit || {};
  window.Digit[Library] = window.Digit[Library] || {};
  window.Digit[Library][service] = method;
};

/* To Overide any existing config/middlewares  we need to use similar method */
const updateCustomConfigs = () => {
  setupLibraries("Customizations", "commonUiConfig", { ...window?.Digit?.Customizations?.commonUiConfig, ...UICustomizations });
};

export const initTQMComponents = () => {
  overrideHooks();
  updateCustomConfigs();
  Object.entries(componentsToRegister).forEach(([key, value]) => {
    Digit.ComponentRegistryService.setComponent(key, value);
  });
};
