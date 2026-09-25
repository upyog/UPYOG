import React, { useState } from "react";
// Updated: Import changed from "react-query" to "@tanstack/react-query" for TanStack Query v5 compatibility.
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { Provider } from "react-redux";
// Updated: BrowserRouter imported as Router with future prop support for React Router v6.
import { BrowserRouter as Router } from "react-router-dom";
import { getI18n } from "react-i18next";
import { Body, Loader } from "@nudmcdgnpm/upyog-ui-react-components-lts";
import { SVApp } from "./App";
import SelectOtp from "./pages/citizen/Login/SelectOtp";
import AcknowledgementCF from "./components/AcknowledgementCF";
import CitizenFeedback from "./components/CitizenFeedback";

import getStore from "./redux/store";
import ErrorBoundary from "./components/ErrorBoundaries";
import EmployeeDashboard from "./components/EmployeeDashboard";
import { union } from "lodash";

// Updated: QueryClient created outside component to avoid re-creation on every render.
// Updated: cacheTime renamed to gcTime in TanStack Query v5.
// Updated: retryDelay set to Infinity to disable automatic retries on failure.

const createQueryClient = () => new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 15 * 60 * 1000,
      // Updated: cacheTime renamed to gcTime in TanStack Query v5.
      gcTime: 50 * 60 * 1000,
      retryDelay: (attemptIndex) => Infinity,
      retry: false,
    },
  },
});

const DigitUIWrapper = ({ stateCode, enabledModules, moduleReducers }) => {
  // FIXED: This now has access to QueryClientProvider
  const { isLoading, data: initData } = Digit.Hooks.useInitStore(stateCode, enabledModules);
  
  if (isLoading) {
    return <Loader page={true} />;
  }

  const i18n = getI18n();
  
  return (
    <Provider store={getStore(initData, moduleReducers(initData))}>
      {/* Updated: future prop added for React Router v7 forward compatibility and to suppress console warnings. */}
      <Router future={{ v7_startTransition: true, v7_relativeSplatPath: true }}>
        <Body>
          <SVApp
            initData={initData}
            stateCode={stateCode}
            modules={initData?.modules}
            appTenants={initData.tenants}
            logoUrl={initData?.stateInfo?.logoUrl}
          />
        </Body>
      </Router>
    </Provider>
  );
};

export const StreetVendingUI = ({ stateCode, registry, enabledModules, moduleReducers }) => {
  const userType = Digit.UserService.getType();
  const [privacy, setPrivacy] = useState(Digit.Utils.getPrivacyObject() || {});

  // Updated: QueryClient created using useState with initializer function to ensure single instance across renders.
  const [queryClient] = useState(() => createQueryClient());

  const ComponentProvider = Digit.Contexts.ComponentProvider;
  const PrivacyProvider = Digit.Contexts.PrivacyProvider;

  return (
    <div>
      <ErrorBoundary>
        {/* Updated: Single QueryClientProvider wraps entire app to avoid multiple QueryClient instances. */}
        <QueryClientProvider client={queryClient}>
          <ComponentProvider.Provider value={registry}>
            <PrivacyProvider.Provider
              value={{
                privacy: privacy?.[window.location.pathname],
                resetPrivacy: (_data) => {
                  Digit.Utils.setPrivacyObject({});
                  setPrivacy({});
                },
                getPrivacy: () => {
                  const privacyObj = Digit.Utils.getPrivacyObject();
                  setPrivacy(privacyObj);
                  return privacyObj;
                },
                updatePrivacyDescoped: (_data) => {
                  const privacyObj = Digit.Utils.getAllPrivacyObject();
                  const newObj = { ...privacyObj, [window.location.pathname]: _data };
                  Digit.Utils.setPrivacyObject({ ...newObj });
                  setPrivacy(privacyObj?.[window.location.pathname] || {});
                },
                updatePrivacy: (uuid, fieldName) => {
                  setPrivacy(Digit.Utils.updatePrivacy(uuid, fieldName) || {});
                },
              }}
            >
              {/* Updated: DigitUIWrapper placed inside QueryClientProvider so all hooks have access to QueryClient. */}
              <DigitUIWrapper 
                stateCode={stateCode} 
                enabledModules={enabledModules} 
                moduleReducers={moduleReducers} 
              />
            </PrivacyProvider.Provider>
          </ComponentProvider.Provider>
        </QueryClientProvider>
      </ErrorBoundary>
    </div>
  );
};

const componentsToRegister = {
  SelectOtp,
  AcknowledgementCF,
  CitizenFeedback,
  EmployeeDashboard
};

export const initCoreComponents = () => {
  Object.entries(componentsToRegister).forEach(([key, value]) => {
    Digit.ComponentRegistryService.setComponent(key, value);
  });
};
