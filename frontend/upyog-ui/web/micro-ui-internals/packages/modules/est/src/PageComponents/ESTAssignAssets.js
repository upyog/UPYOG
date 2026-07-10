import React, { useMemo, useCallback, useState, useEffect } from "react";
import { Header, DynamicForm, Loader, attachRouteConfigToStepData } from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import { buildDynamicAllotmentPayload } from "../utils/allotmentPayloadUtils";
import estateAllotmentFormConfig from "../config/Create/estateAllotmentFormConfig";
import {
  fetchAllotmentByAssetNo,
  mapAllotmentApiToFormData,
  mergeAllotmentPrefill,
  pickMeaningfulFormValues,
} from "../utils/allotmentFormUtils";

const getLocalityText = (asset, t) => {
  if (!asset) return "";
  const locObj = asset.locality || asset.address?.locality;
  if (typeof locObj === "string") {
    if (locObj.startsWith("TENANT_") || locObj.startsWith("EST_")) return t(locObj);
    return locObj;
  }
  if (locObj && typeof locObj === "object") {
    if (locObj.i18nKey) return t(locObj.i18nKey);
    return locObj.label || locObj.name || locObj.code || "";
  }
  const candidates = [asset.localityName, asset.localityCode, asset.serviceType];
  const raw = candidates.find((v) => v !== undefined && v !== null && v !== "");
  if (!raw) return "";
  if (typeof raw === "string" && (raw.startsWith("TENANT_") || raw.startsWith("EST_"))) {
    return t(raw);
  }
  return raw;
};

const ESTAssignAssets = ({ onSelect, onDraftSave, config, formData, isEditMode, editData }) => {
  const persistedData = useMemo(() => formData || {}, [formData]);
  const [apiAllotment, setApiAllotment] = useState(null);
  const [loadingAllotment, setLoadingAllotment] = useState(false);

  const { t } = useTranslation();

  const routeConfig = useMemo(
    () => ({ ...config, ...estateAllotmentFormConfig }),
    [config]
  );
  const tenantId = useMemo(() => Digit.ULBService.getCurrentTenantId(), []);
  const assetNo = persistedData?.assetData?.estateNo;

  const sessionDraft =
    persistedData?.[routeConfig.key]?.[routeConfig.payloadKey]?.[0] || {};

  useEffect(() => {
    if (!assetNo) return;

    let cancelled = false;
    const loadAllotment = async () => {
      setLoadingAllotment(true);
      try {
        const allotment = await fetchAllotmentByAssetNo(assetNo, tenantId);
        if (!cancelled && allotment) {
          setApiAllotment(mapAllotmentApiToFormData(allotment));
        }
      } catch (error) {
        console.error("Error fetching allotment for asset:", error);
      } finally {
        if (!cancelled) setLoadingAllotment(false);
      }
    };

    loadAllotment();
    return () => {
      cancelled = true;
    };
  }, [assetNo, tenantId]);

  const assetDisplay = useMemo(() => {
    const asset = persistedData?.assetData || {};
    return {
      assetNo: asset.estateNo || asset.assetNo || "",
      assetRefNumber: asset.assetRef || asset.refAssetNo || "",
      buildingName: asset.buildingName || "",
      localityDisplay: getLocalityText(asset, t),
      totalFloorArea: asset.totalFloorArea || "",
      buildingFloor: asset.buildingFloor || "",
      assetRate: asset.rate || "",
    };
  }, [persistedData, t]);

  const prefillData = useMemo(
    () =>
      mergeAllotmentPrefill(apiAllotment || {}, sessionDraft, {
        ...assetDisplay,
        ...(editData || {}),
      }),
    [apiAllotment, sessionDraft, assetDisplay, editData]
  );

  const dynamicPersistedData = useMemo(() => {
    const meaningfulDraft = pickMeaningfulFormValues(sessionDraft);
    if (Object.keys(meaningfulDraft).length === 0) {
      return persistedData;
    }

    return {
      ...persistedData,
      [routeConfig.key]: {
        [routeConfig.payloadKey]: [meaningfulDraft],
      },
    };
  }, [persistedData, sessionDraft, routeConfig]);

  const handleSubmit = useCallback(
    ({ payload, error }) => {
      if (error) {
        console.error("Submit error:", error);
        return;
      }
      const flatAllotment = payload?.Allotments?.[0] || {};
      buildDynamicAllotmentPayload(routeConfig, flatAllotment, tenantId);
    },
    [routeConfig, tenantId]
  );

  const buildDraftPayload = useCallback(
    (flatAllotment = {}) => ({
      [routeConfig.payloadKey]: [
        {
          ...flatAllotment,
          allotmentId: flatAllotment.allotmentId || apiAllotment?.allotmentId || "",
          userUuid: flatAllotment.userUuid || apiAllotment?.userUuid || "",
          auditDetails: flatAllotment.auditDetails || apiAllotment?.auditDetails || null,
        },
      ],
    }),
    [routeConfig.payloadKey, apiAllotment]
  );

  const handlePersistDraft = useCallback(
    (flatAllotment) => {
      if (!onDraftSave) return;
      onDraftSave(routeConfig.key, buildDraftPayload(flatAllotment));
    },
    [onDraftSave, routeConfig.key, buildDraftPayload]
  );

  const handleSelect = useCallback(
    (key, data, skipStep, index, isAddMultiple) => {
      const saved = data?.[routeConfig.payloadKey]?.[0] || {};
      onSelect?.(
        key,
        attachRouteConfigToStepData(buildDraftPayload(saved), routeConfig),
        skipStep,
        index,
        isAddMultiple
      );
    },
    [onSelect, routeConfig, buildDraftPayload]
  );

  return (
    <div className="employeeCard">
      <Header>{t(routeConfig.pageHeading?.create || "EST_COMMMON_ASSIGN_ASSETS")}</Header>
      {loadingAllotment ? (
        <Loader />
      ) : (
        <DynamicForm
          routeConfig={routeConfig}
          onSubmit={handleSubmit}
          onSelect={handleSelect}
          onPersistDraft={onDraftSave ? handlePersistDraft : undefined}
          config={config || { key: routeConfig.key }}
          persistedData={dynamicPersistedData || {}}
          isEditMode={Boolean(apiAllotment?.allotmentId)}
          editData={prefillData}
          tenantId={tenantId}
          t={t}
          showCancel
        />
      )}
    </div>
  );
};

export default ESTAssignAssets;
