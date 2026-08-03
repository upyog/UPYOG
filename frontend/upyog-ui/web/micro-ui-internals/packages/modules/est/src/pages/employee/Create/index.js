/**
 * ESTRegCreate Component
 *
 * Handles the estate registration process: form navigation, MDMS config
 * fetching, and rendering the multi-step application routes.
 */

import { Loader } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useMemo, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { useQueryClient } from "@tanstack/react-query";
import { Navigate, Route, Routes } from "react-router-dom";
import useEstWizard from "../../../utils/useEstWizard";

const ESTRegCreate = () => {
  const queryClient = useQueryClient();
  const { t } = useTranslation();

  const { data: initialConfig, isLoading } = Digit.Hooks.useEnabledMDMS(
    Digit.ULBService.getStateId(),
    "Estate",
    [{ name: "NewRegistration" }],
    {
      // Primary: Estate.NewRegistration (data/pg/Estate/NewRegistration.json).
      // Fallback: legacy Estate.Config if still deployed.
      select: (data) =>
        data?.Estate?.NewRegistration || null,
    }
  );

  const {
    config,
    params,
    clearParams,
    location,
    match,
    handleSelect,
    onAckSuccess,
    onCheckSuccess: estcreate,
    onCheckError: estcreateError,
    isReady,
  } = useEstWizard({
    mdmsData: initialConfig,
    isLoading,
    indexRoute: "newRegistration",
    sessionKey: "EST_NEW_REGISTRATION_CREATES",
    terminalSegments: ["check", "acknowledgement", "newRegistration"],
    multiStepNavigation: true,
    invalidateQueryKey: "EST_NEW_REGISTRATION_CREATES",
  });

  const { pathname } = location;
  const routerEditData = location?.state?.editData;

  const effectiveParams = useMemo(() => {
    return routerEditData && Object.keys(routerEditData).length > 0
      ? { ...params, newRegistration: { Assets: [routerEditData] } }
      : params;
  }, [routerEditData, params]);

  useEffect(() => {
    sessionStorage.setItem(
      "applicationType",
      pathname.includes("new-application")
        ? "EST_NEWAPPLICATION"
        : "EST_RENEWAPPLICATION"
    );
  }, [pathname]);

  useEffect(() => {
    if (
      params &&
      Object.keys(params).length > 0 &&
      window.location.href.includes("/info") &&
      sessionStorage.getItem("docReqScreenByBack") !== "true"
    ) {
      clearParams();
      queryClient.invalidateQueries("EST_NEW_REGISTRATION_CREATES");
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [pathname]);

  if (!isReady) {
    return <Loader />;
  }

  const ESTDynamicCheckPage = Digit?.ComponentRegistryService?.getComponent("ESTDynamicCheckPage");
  const ESTAcknowledgement = Digit?.ComponentRegistryService?.getComponent("ESTAcknowledgement");

  return (
    <Routes>
      {config.map((routeObj, index) => {
        const { component } = routeObj;
        const Component =
          typeof component === "string"
            ? Digit.ComponentRegistryService.getComponent(component)
            : component;
        const user = Digit.UserService.getUser().info.type;

        return (
          <Route
            path={`${routeObj.route}/*`}
            key={index}
            element={
              <Component
                config={routeObj}
                onSelect={handleSelect}
                t={t}
                persistedData={effectiveParams}
                userType={user}
                parentRoute={match?.pathnameBase}
              />
            }
          />
        );
      })}

      <Route
        path="check/*"
        element={
          <ESTDynamicCheckPage
            flow="registration"
            onSubmit={estcreate}
            onError={estcreateError}
            value={params}
            config={config}
          />
        }
      />

      <Route
        path="acknowledgement/*"
        element={<ESTAcknowledgement onSuccess={onAckSuccess} />}
      />

      <Route path="/*" element={<Navigate to={config.indexRoute} replace />} />
    </Routes>
  );
};

export default ESTRegCreate;
