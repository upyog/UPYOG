import { Loader } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useCallback, useEffect, useRef } from "react";
import { useTranslation } from "react-i18next";
import { Navigate, Route, Routes, useLocation } from "react-router-dom";
import { buildAllotmentAcknowledgementData } from "../../../utils";
import { getAssetIdentity } from "../../../utils/allotmentFormUtils";
import useEstWizard from "../../../utils/useEstWizard";

const ESTAssignAssetCreate = ({ parentRoute }) => {
  const location = useLocation();
  const { t } = useTranslation();
  const appliedNavRef = useRef(null);

  const { data: initialConfig, isLoading } = Digit.Hooks.useEnabledMDMS(
    Digit.ULBService.getStateId(),
    "Estate",
    [{ name: "assignAssetConfig" }],
    {
      select: (data) => data?.Estate?.assignAssetConfig,
    }
  );

  const {
    config,
    params,
    setParams,
    match,
    handleSelect,
    onAckSuccess,
    onCheckSuccess: estcreate,
    onCheckError: estcreateError,
    isReady,
  } = useEstWizard({
    mdmsData: initialConfig,
    isLoading,
    indexRoute: "info",
    sessionKey: "EST_ASSIGN_ASSETS",
    terminalSegments: ["check", "acknowledgement", "info", "assign-assets"],
    multiStepNavigation: false,
    invalidateQueryKey: "EST_ASSIGN_ASSETS",
    buildSuccessAckState: (response, sessionParams) => ({
      data: buildAllotmentAcknowledgementData(sessionParams, response),
      isSuccess: true,
    }),
  });

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

  const ESTDynamicCheckPage = Digit?.ComponentRegistryService?.getComponent("ESTDynamicCheckPage");
  const ESTAllotmentAcknowledgement =
    Digit?.ComponentRegistryService?.getComponent("ESTAllotmentAcknowledgement");

  if (!isReady) {
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
                t={t}
                formData={params}
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
        element={<ESTAllotmentAcknowledgement onSuccess={onAckSuccess} />}
      />
      <Route path="/*" element={<Navigate to={config.indexRoute} replace />} />
    </Routes>
  );
};

export default ESTAssignAssetCreate;
