import React, { useMemo } from "react";
import { useTranslation } from "react-i18next";
import ESTDynamicFormStep from "./ESTDynamicFormStep";
import estateAllotmentFormOverrides from "../config/Create/estateAllotmentFormOverrides";
import {
  mergeAllotmentPrefill,
  pickMeaningfulFormValues,
} from "../utils/allotmentFormUtils";
import { resolveLocalityDisplay } from "../utils/estMdmsUtils";

const PAYLOAD_KEY = "Allotments";
const STEP_KEY = "Allotments";

const ESTAssignAssets = ({ onSelect, onDraftSave, onDraftClear, config, formData }) => {
  const { t } = useTranslation();
  const persistedData = useMemo(() => formData || {}, [formData]);

  const sessionDraft =
    persistedData?.[STEP_KEY]?.[PAYLOAD_KEY]?.[0] || {};

  const assetDisplay = useMemo(() => {
    const asset = persistedData?.assetData || {};
    return {
      assetNo: asset.estateNo || asset.assetNo || "",
      assetRefNumber: asset.assetRef || asset.refAssetNo || "",
      buildingName: asset.buildingName || "",
      localityDisplay: resolveLocalityDisplay(asset, t),
      totalFloorArea: asset.totalFloorArea || "",
      buildingFloor: asset.buildingFloor || "",
      assetRate: asset.rate || "",
    };
  }, [persistedData, t]);

  const prefillData = useMemo(
    () => mergeAllotmentPrefill({}, sessionDraft, assetDisplay),
    [sessionDraft, assetDisplay]
  );

  const resetBaseline = useMemo(
    () => mergeAllotmentPrefill({}, {}, assetDisplay),
    [assetDisplay]
  );

  const dynamicPersistedData = useMemo(() => {
    const meaningfulDraft = pickMeaningfulFormValues(sessionDraft);
    if (Object.keys(meaningfulDraft).length === 0) {
      return persistedData;
    }
    return {
      ...persistedData,
      [STEP_KEY]: {
        [PAYLOAD_KEY]: [meaningfulDraft],
      },
    };
  }, [persistedData, sessionDraft]);

  return (
    <ESTDynamicFormStep
      config={config}
      localOverrides={estateAllotmentFormOverrides}
      onSelect={onSelect}
      formData={dynamicPersistedData}
      editData={prefillData}
      resetBaseline={resetBaseline}
      draft={
        onDraftSave
          ? {
              buildStepData: (flat) => ({ [PAYLOAD_KEY]: [{ ...flat }] }),
              onPersist: onDraftSave,
              onClear: onDraftClear,
            }
          : undefined
      }
    />
  );
};

export default ESTAssignAssets;
