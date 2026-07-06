import React, { useMemo, useCallback } from "react";
import { useTranslation } from "react-i18next";
import { useQueryClient } from "@tanstack/react-query";
import { Navigate, Route, Routes, useLocation } from "react-router-dom";
import { Config } from '../../../config/Create/config'


const ESTRegCreate = ({ parentRoute }) => {
  const queryClient = useQueryClient();
  const match = Digit.Hooks.useModuleBasePath();
  const { t } = useTranslation();
  const location = useLocation();
  const { pathname } = location;
  const navigate = Digit.Hooks.useCustomNavigate();

  const [params, setParams, clearParams] = Digit.Hooks.useSessionStorage(
    "EST_NEW_REGISTRATION_CREATES",
    {}
  );

  // Merge router state editData into params when user returns from check page via Edit
  const routerEditData = location?.state?.editData;

  const effectiveParams = useMemo(() => {
    return routerEditData && Object.keys(routerEditData).length > 0
      ? { ...params, newRegistration: { Assets: [routerEditData] } }
      : params;
  }, [routerEditData, params]);

  // ─── MDMS config fetch ─────────────────────────────────────────────────────────
  // Shape returned: [{ head, body: [ {key, route, component, hideInEmployee, ...} ] }]
  // i.e. the SAME shape as the "Config" array in the Estate MDMS JSON.
  // const { data: initialConfig, isLoading } = Digit.Hooks.useEnabledMDMS(
  //   Digit.ULBService.getStateId(),
  //   "Estate",
  //   [{ name: "Config" }],
  //   {
  //     select: (data) => data?.["Estate"]?.["Config"],
  //   }
  // );

  // // Don't compute anything that touches initialConfig until it has actually loaded.
  // if (isLoading || !initialConfig || initialConfig.length === 0) {
  //   return null; // or a <Loader /> if available
  // }

  // formConfig = the first top-level config entry (head + body) — what ESTRegCheckPage needs.
  //const formConfig = initialConfig[0];

  // Build config ONCE from the static import, without mutating the shared
  // module-level array (Config is a module singleton — mutating it in place
  // on every render is unsafe and gives unstable identity to consumers below).
  const config = useMemo(() => {
    const body = Config[0]?.body || [];
    const cloned = [...body];
    cloned.indexRoute = "newRegistration";
    return cloned;
  }, []); // Config is a static import; this only needs to run once

  // const config = initialConfig.reduce((acc, entry) => {
  //   if (!entry?.body) return acc;
  //   return acc.concat(entry.body.filter((step) => !step.hideInEmployee));
  // }, []);
  // config.indexRoute = "newRegistration";

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

  const goNext = useCallback((skipStep, index, isAddMultiple, key) => {
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

    let { nextStep = {} } = config.find((routeObj) => routeObj.route === currentPath) || {};

    let redirectWithHistory = (to, state) =>
      navigate(to, state != null ? { state } : undefined);

    if (skipStep) {
      redirectWithHistory = (to, state) =>
        navigate(to, state != null ? { replace: true, state } : { replace: true });
    }

    if (isAddMultiple) nextStep = key;
    if (nextStep === null) return redirectWithHistory("check");

    if (!isNaN(nextStep.split("/").pop())) {
      nextPage = `${nextStep}`;
    } else {
      nextPage = isMultiple && nextStep !== "map"
        ? `${nextStep}/${index}`
        : `${nextStep}`;
    }

    redirectWithHistory(nextPage);
  }, [pathname, config, navigate]);

  if (
    params &&
    Object.keys(params).length > 0 &&
    window.location.href.includes("/info") &&
    sessionStorage.getItem("docReqScreenByBack") !== "true"
  ) {
    clearParams();
    queryClient.invalidateQueries("EST_NEW_REGISTRATION_CREATES");
  }

  // ─── estcreate ───────────────────────────────────────────────────────────────
  // Called by ESTRegCheckPage with the API response on success.
  const estcreate = useCallback((response) => {
    clearParams();
    queryClient.invalidateQueries("EST_NEW_REGISTRATION_CREATES");
    const basePath = getBasePath();
    navigate(`${basePath}/acknowledgement`, {
      state: {
        data: response,
        isSuccess: true,
      },
    });
  }, [clearParams, queryClient, getBasePath, navigate]);

  // ─── estcreateError ──────────────────────────────────────────────────────────
  // Called by ESTRegCheckPage with the error on failure.
  const estcreateError = useCallback((error) => {
    const basePath = getBasePath();
    navigate(`${basePath}/acknowledgement`, {
      state: {
        data: null,
        isSuccess: false,
        error,
      },
    });
  }, [getBasePath, navigate]);

  const handleSelect = useCallback((key, data, skipStep, index, isAddMultiple = false) => {
    setParams({ ...params, [key]: data });
    goNext(skipStep, index, isAddMultiple, key);
  }, [params, setParams, goNext]);

  const handleSkip = useCallback(() => {}, []);
  const handleMultiple = useCallback(() => {}, []);

  const onSuccess = useCallback(() => {
    clearParams();
    queryClient.invalidateQueries("EST_NEW_REGISTRATION_CREATES");
  }, [clearParams, queryClient]);

  const ESTRegCheckPage    = Digit?.ComponentRegistryService?.getComponent("ESTRegCheckPage");
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
          <ESTRegCheckPage
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