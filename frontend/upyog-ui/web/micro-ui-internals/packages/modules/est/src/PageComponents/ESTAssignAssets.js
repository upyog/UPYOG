import React, { useMemo, useCallback, useState, useEffect } from "react";
import { Header, DynamicForm, Loader } from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import { buildDynamicAllotmentPayload } from "../utils/allotmentPayloadUtils";
import estateAllotmentFormConfig from "../config/Create/estateAllotmentFormConfig";
import {
  fetchAllotmentByAssetNo,
  mapAllotmentApiToFormData,
} from "../utils/allotmentFormUtils";

// Resolves whatever shape the asset's locality field came back in (raw code, i18nKey object,
// plain string) into a single display string. Lifted as-is from the static ESTAssignAssets.js —
// this stays here rather than inside DynamicForm/DynamicFormField because it's a display
// concern specific to this module's asset data shape, not something a generic renderer should know.
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

const ESTAssignAssets = ({ onSelect, config, formData, isEditMode, editData }) => {
  const persistedData = useMemo(() => formData || {}, [formData]);
  const [apiAllotment, setApiAllotment] = useState(null);
  const [loadingAllotment, setLoadingAllotment] = useState(false);

  const { t } = useTranslation();

  // MDMS supplies route metadata (steps, components); local form config supplies
  // corrected field rules — email pattern {2,}, computeFrom agreementStartDate/EndDate.
  const routeConfig = useMemo(
    () => ({ ...config, ...estateAllotmentFormConfig }),
    [config]
  );
  const tenantId = useMemo(() => Digit.ULBService.getCurrentTenantId(), []);
  const assetNo = persistedData?.assetData?.estateNo;

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

  // The asset (Building Name/Locality/Area/Floor/Rate/Asset No/Asset Ref) was captured in the
  // NewRegistration step, keyed by *its* step key — not this step's. DynamicForm only knows how
  // to prefill from persistedData[config.key] or from an explicit editData prop, so we merge the
  // prior step's asset record + this step's own persisted/edit allotment data here and hand the
  // whole thing to DynamicForm as editData. That reuses DynamicForm's existing prefill logic
  // ("editData wins if non-empty") without needing it to know about cross-step data at all.
  const prefillData = useMemo(() => {
    //const asset = persistedData?.newRegistration?.Assets?.[0] || {};
    const asset = persistedData?.assetData || {};
    const priorAllotment = persistedData?.[routeConfig.key]?.[routeConfig.payloadKey]?.[0] || {};
    
    return {
      assetNo: asset.estateNo || asset.assetNo || "",
      assetRefNumber: asset.assetRef || asset.refAssetNo || "",
      buildingName: asset.buildingName || "",
      localityDisplay: getLocalityText(asset, t),
      totalFloorArea: asset.totalFloorArea || "",
      buildingFloor: asset.buildingFloor || "",
      assetRate: asset.rate || "",
      ...priorAllotment,
      ...(apiAllotment || {}),
      ...(editData || {}),
    };

  }, [persistedData, routeConfig, editData, apiAllotment, t]);

  // Dev-time preview only — real submit/mutation happens further down the workflow
  // (e.g. an ESTAllotmentCheckPage), via the same shared buildDynamicAllotmentPayload()
  // so it can't drift.
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

  return (
    <div className="employeeCard">
      <Header>{t(routeConfig.pageHeading?.create || "EST_COMMMON_ASSIGN_ASSETS")}</Header>
      {loadingAllotment ? (
        <Loader />
      ) : (
        <DynamicForm
          routeConfig={routeConfig}
          onSubmit={handleSubmit}
          onSelect={onSelect}
          config={config || { key: routeConfig.key }}
          persistedData={persistedData || {}}
          isEditMode={isEditMode || false}
          editData={prefillData}
          tenantId={tenantId}
          t={t}
        />
      )}
    </div>
  );
};

export default ESTAssignAssets;
