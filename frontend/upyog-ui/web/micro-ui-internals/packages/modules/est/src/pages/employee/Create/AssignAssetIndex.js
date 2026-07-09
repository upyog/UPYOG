import { Loader } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useMemo, useCallback, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { useQueryClient } from "@tanstack/react-query";
import { Navigate, Route, Routes, useLocation } from "react-router-dom";
import { buildAllotmentAcknowledgementData } from "../../../utils";

const ESTAssignAssetCreate = ({ parentRoute }) => {
  const location = useLocation();
  const queryClient = useQueryClient();
  const match = Digit.Hooks.useModuleBasePath();
  const { t } = useTranslation();
  const { pathname } = location;
  const navigate = Digit.Hooks.useCustomNavigate();
  const [params, setParams, clearParams] = Digit.Hooks.useSessionStorage("EST_ASSIGN_ASSETS", {});

  const { data: initialConfig, isLoading } = Digit.Hooks.useEnabledMDMS(
    Digit.ULBService.getStateId(),
    "Estate",
    [{ name: "assignAssetConfig" }],
    {
      select: (data) => data?.Estate?.assignAssetConfig,
    }
  );

  const config = useMemo(() => {
    if (!initialConfig || !Array.isArray(initialConfig)) return [];
    const merged = initialConfig.reduce((acc, entry) => {
      if (!entry?.body) return acc;
      return acc.concat(entry.body.filter((step) => !step.hideInEmployee));
    }, []);
    merged.indexRoute = "info";
    return merged;
  }, [initialConfig]);

  useEffect(() => {
    const incoming = location.state?.assetData;
    if (!incoming || Object.keys(incoming).length === 0) return;

    const identityOf = (a) => a?.estateNo || a?.assetId || a?.refAssetNo || "";
    setParams((prev) => {
      if (identityOf(prev?.assetData) === identityOf(incoming)) return prev;
      return { ...prev, assetData: incoming };
    });
  }, [location.state, setParams]);

  const getBasePath = useCallback(() => {
    if (match?.pathnameBase) return match.pathnameBase;

    const parts = pathname.split("/");
    const terminalSegments = ["check", "acknowledgement", "info", "assign-assets"];
    const terminalIndex = parts.findIndex((p) => terminalSegments.includes(p));
    if (terminalIndex > 0) {
      return parts.slice(0, terminalIndex).join("/");
    }
    return parts.slice(0, -1).join("/");
  }, [match, pathname]);

  const goNext = useCallback(
    (skipStep, index, isAddMultiple, key) => {
      let currentPath = pathname.split("/").pop();
      let { nextStep = {} } = config.find((routeObj) => routeObj.route === currentPath) || {};

      let redirectWithHistory = (to, state) =>
        navigate(to, state != null ? { state } : undefined);
      if (skipStep) {
        redirectWithHistory = (to, state) =>
          navigate(to, state != null ? { replace: true, state } : { replace: true });
      }

      if (isAddMultiple) nextStep = key;
      if (nextStep === null) return redirectWithHistory("check");
      if (typeof nextStep !== "string") return redirectWithHistory("check");

      redirectWithHistory(`${nextStep}`);
    },
    [pathname, config, navigate]
  );

  const handleSelect = useCallback(
    (key, data, skipStep, index, isAddMultiple = false) => {
      setParams((prev) => ({ ...prev, [key]: data }));
      goNext(skipStep, index, isAddMultiple, key);
    },
    [setParams, goNext]
  );

  const handleSkip = useCallback(() => {}, []);
  const handleMultiple = useCallback(() => {}, []);

  const onSuccess = useCallback(() => {
    clearParams();
    queryClient.invalidateQueries("EST_ASSIGN_ASSETS");
  }, [clearParams, queryClient]);

  const estcreate = useCallback(
    (response) => {
      const merged = buildAllotmentAcknowledgementData(params, response);
      clearParams();
      queryClient.invalidateQueries("EST_ASSIGN_ASSETS");
      navigate(`${getBasePath()}/acknowledgement`, {
        state: { data: merged, isSuccess: true },
      });
    },
    [params, clearParams, queryClient, getBasePath, navigate]
  );

  const estcreateError = useCallback(
    (error) => {
      navigate(`${getBasePath()}/acknowledgement`, {
        state: { data: null, isSuccess: false, error },
      });
    },
    [getBasePath, navigate]
  );

  const ESTAssignAssetsCheckPage = Digit?.ComponentRegistryService?.getComponent("ESTAssignAssetsCheckPage");
  const ESTAllotmentAcknowledgement = Digit?.ComponentRegistryService?.getComponent("ESTAllotmentAcknowledgement");

  if (isLoading || !initialConfig || config.length === 0) {
    return <Loader />;
  }

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
                formData={params}
                onAdd={handleMultiple}
                userType={user}
                parentRoute={match?.pathnameBase || parentRoute}
              />
            }
          />
        );
      })}
      <Route
        path="check/*"
        element={
          <ESTAssignAssetsCheckPage
            onSubmit={estcreate}
            onError={estcreateError}
            value={params}
            config={config}
          />
        }
      />
      <Route
        path="acknowledgement/*"
        element={<ESTAllotmentAcknowledgement data={params} onSuccess={onSuccess} />}
      />
      <Route path="/*" element={<Navigate to={config.indexRoute} replace />} />
    </Routes>
  );
};

export default ESTAssignAssetCreate;
