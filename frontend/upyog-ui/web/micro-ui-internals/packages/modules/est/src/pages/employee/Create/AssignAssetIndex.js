import { Loader, mergeSessionStepWithRouteConfig } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useMemo, useCallback, useEffect, useRef } from "react";
import { useTranslation } from "react-i18next";
import { useQueryClient } from "@tanstack/react-query";
import { Navigate, Route, Routes, useLocation } from "react-router-dom";
import { buildAllotmentAcknowledgementData } from "../../../utils";
import { getAssetIdentity } from "../../../utils/allotmentFormUtils";

const ESTAssignAssetCreate = ({ parentRoute }) => {
  const location = useLocation();
  const queryClient = useQueryClient();
  const match = Digit.Hooks.useModuleBasePath();
  const { t } = useTranslation();
  const { pathname } = location;
  const navigate = Digit.Hooks.useCustomNavigate();
  const [params, setParams, clearParams] = Digit.Hooks.useSessionStorage("EST_ASSIGN_ASSETS", {});
  const appliedNavRef = useRef(null);

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
    const allotmentData = location.state?.allotmentData;
    const resetSession = location.state?.resetSession;

    if (!incoming && !allotmentData) return;

    const navKey = [
      getAssetIdentity(incoming),
      allotmentData?.allotmentId || "",
      Boolean(resetSession),
    ].join("|");

    if (appliedNavRef.current === navKey) return;
    appliedNavRef.current = navKey;

    setParams((prev) => {
      const sameAsset =
        getAssetIdentity(prev?.assetData) === getAssetIdentity(incoming);

      if (resetSession || !sameAsset) {
        const next = { assetData: incoming };
        if (allotmentData) {
          next.Allotments = { Allotments: [allotmentData] };
          if (allotmentData.allotmentId) {
            next.allotmentId = allotmentData.allotmentId;
          }
        }
        return next;
      }

      const hasDraft = Boolean(prev?.Allotments?.Allotments?.[0]);

      if (allotmentData && hasDraft) {
        return {
          ...prev,
          assetData: incoming,
        };
      }

      if (allotmentData) {
        return {
          ...prev,
          assetData: incoming,
          Allotments: { Allotments: [allotmentData] },
          ...(allotmentData.allotmentId
            ? { allotmentId: allotmentData.allotmentId }
            : {}),
        };
      }

      return prev?.assetData ? prev : { ...prev, assetData: incoming };
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
      setParams((prev) => mergeSessionStepWithRouteConfig(prev, key, data));
      goNext(skipStep, index, isAddMultiple, key);
    },
    [setParams, goNext]
  );

  const handleDraftSave = useCallback(
    (key, data) => {
      setParams((prev) => ({
        ...prev,
        [key]: data,
      }));
    },
    [setParams]
  );

  const handleDraftClear = useCallback(
    (key) => {
      setParams((prev) => {
        const next = { ...prev };
        delete next[key];
        return next;
      });
    },
    [setParams]
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

  const ESTDynamicCheckPage = Digit?.ComponentRegistryService?.getComponent("ESTDynamicCheckPage");
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
                onDraftSave={handleDraftSave}
                onDraftClear={handleDraftClear}
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
          <ESTDynamicCheckPage
            flow="allotment"
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
