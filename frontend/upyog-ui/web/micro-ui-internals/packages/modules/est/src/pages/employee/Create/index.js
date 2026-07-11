/**
 * ESTRegCreate Component
 *
 * Handles the estate registration process: form navigation, MDMS config
 * fetching, and rendering the multi-step application routes.
 *
 
 */

import { Loader } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useMemo, useCallback, useEffect } from "react";
import { mergeSessionStepWithRouteConfig } from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import { useQueryClient } from "@tanstack/react-query";
import { Navigate, Route, Routes, useLocation } from "react-router-dom";

const ESTRegCreate = ({ parentRoute }) => {
  // ─── ALL hooks first — no early return may appear before this block ends ───
  const queryClient = useQueryClient();
  const match = Digit.Hooks.useModuleBasePath();
  const { t } = useTranslation();
  const location = useLocation();
  const { pathname } = location;
  const navigate = Digit.Hooks.useCustomNavigate();

  const tenantId =
    Digit.ULBService.getCitizenCurrentTenant(true) ||
    Digit.ULBService.getCurrentTenantId();
  const mutation = Digit.Hooks.ptr.usePTRCreateAPI(tenantId);
  const [params, setParams, clearParams] = Digit.Hooks.useSessionStorage(
    "EST_NEW_REGISTRATION_CREATES",
    {}
  );

  // Merge router-state editData into params when user returns from check page via Edit
  const routerEditData = location?.state?.editData;

  const effectiveParams = useMemo(() => {
    return routerEditData && Object.keys(routerEditData).length > 0
      ? { ...params, newRegistration: { Assets: [routerEditData] } }
      : params;
  }, [routerEditData, params]);

  // Fetches common field configurations from MDMS
  const { data: initialConfig, isLoading } = Digit.Hooks.useEnabledMDMS(
    Digit.ULBService.getStateId(),
    "Estate",
    [{ name: "Config" }],
    {
      select: (data) => data?.["Estate"]?.["Config"],
    }
  );

  // Build config safely even while initialConfig is still undefined.
  // (Must run on every render — cannot sit below a conditional return.)
  const config = useMemo(() => {
    if (!initialConfig || !Array.isArray(initialConfig)) return [];
    const merged = initialConfig.reduce((acc, entry) => {
      if (!entry?.body) return acc;
      return acc.concat(entry.body.filter((step) => !step.hideInEmployee));
    }, []);
    merged.indexRoute = "newRegistration";
    return merged;
  }, [initialConfig]);

  // Side effect: set applicationType (was previously executed during render)
  useEffect(() => {
    sessionStorage.setItem(
      "applicationType",
      pathname.includes("new-application")
        ? "EST_NEWAPPLICATION"
        : "EST_RENEWAPPLICATION"
    );
  }, [pathname]);

  // Side effect: clear stale params when landing back on /info
  // (was previously executed during render, which can cause render loops)
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

  const getBasePath = useCallback(() => {
    if (match?.pathnameBase) return match.pathnameBase;

    const parts = pathname.split("/");
    const terminalSegments = ["check", "acknowledgement", "newRegistration"];
    const terminalIndex = parts.findIndex((p) => terminalSegments.includes(p));
    if (terminalIndex > 0) {
      return parts.slice(0, terminalIndex).join("/");
    }
    return parts.slice(0, -1).join("/");
  }, [match, pathname]);

  const goNext = useCallback(
    (skipStep, index, isAddMultiple, key) => {
      let currentPath = pathname.split("/").pop(),
        lastchar = currentPath.charAt(currentPath.length - 1),
        isMultiple = false,
        nextPage;

      if (Number(parseInt(currentPath)) || currentPath == "0" || currentPath == "-1") {
        if (currentPath == "-1" || currentPath == "-2") {
          currentPath = pathname.slice(0, -3);
          currentPath = currentPath.split("/").pop();
          isMultiple = true;
        } else {
          currentPath = pathname.slice(0, -2);
          currentPath = currentPath.split("/").pop();
          isMultiple = true;
        }
      } else {
        isMultiple = false;
      }

      if (!isNaN(lastchar)) isMultiple = true;

      let { nextStep = {} } =
        config.find((routeObj) => routeObj.route === currentPath) || {};

      let redirectWithHistory = (to, state) =>
        navigate(to, state != null ? { state } : undefined);

      if (skipStep) {
        redirectWithHistory = (to, state) =>
          navigate(to, state != null ? { replace: true, state } : { replace: true });
      }

      if (isAddMultiple) nextStep = key;
      if (nextStep === null) return redirectWithHistory("check");

      // Guard: if no matching route was found, nextStep is the default {}
      // and calling .split() on it throws. Fall back to the check page.
      if (typeof nextStep !== "string") return redirectWithHistory("check");

      if (!isNaN(nextStep.split("/").pop())) {
        nextPage = `${nextStep}`;
      } else {
        nextPage =
          isMultiple && nextStep !== "map" ? `${nextStep}/${index}` : `${nextStep}`;
      }

      redirectWithHistory(nextPage);
    },
    [pathname, config, navigate]
  );

  // Called by ESTRegCheckPage with the API response on success.
  const estcreate = useCallback(
    (response) => {
      clearParams();
      queryClient.invalidateQueries("EST_NEW_REGISTRATION_CREATES");
      const basePath = getBasePath();
      navigate(`${basePath}/acknowledgement`, {
        state: {
          data: response,
          isSuccess: true,
        },
      });
    },
    [clearParams, queryClient, getBasePath, navigate]
  );

  // Called by ESTRegCheckPage with the error on failure.
  const estcreateError = useCallback(
    (error) => {
      const basePath = getBasePath();
      navigate(`${basePath}/acknowledgement`, {
        state: {
          data: null,
          isSuccess: false,
          error,
        },
      });
    },
    [getBasePath, navigate]
  );

  const onSuccess = useCallback(() => {
    clearParams();
    queryClient.invalidateQueries("EST_NEW_REGISTRATION_CREATES");
  }, [clearParams, queryClient]);

  const handleSelect = useCallback(
    (key, data, skipStep, index, isAddMultiple = false) => {
      setParams((prev) => mergeSessionStepWithRouteConfig(prev, key, data));
      goNext(skipStep, index, isAddMultiple, key);
    },
    [setParams, goNext]
  );

  const handleSkip = useCallback(() => {}, []);
  const handleMultiple = useCallback(() => {}, []);

  // ─── Early return is now SAFE: every hook above has already been called ────
  if (isLoading || !initialConfig || config.length === 0) {
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
                onSkip={handleSkip}
                t={t}
                persistedData={effectiveParams}
                onAdd={handleMultiple}
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
        element={<ESTAcknowledgement data={params} onSuccess={onSuccess} />}
      />

      <Route path="/*" element={<Navigate to={config.indexRoute} replace />} />
    </Routes>
  );
};

export default ESTRegCreate;