import React, { useMemo } from "react";
import { useTranslation } from "react-i18next";
import { DynamicFormStep } from "@nudmcdgnpm/digit-ui-react-components";
import estateAllotmentFormOverrides from "../config/Create/estateAllotmentFormOverrides";
import {
  mergeAllotmentPrefill,
  pickMeaningfulFormValues,
} from "../utils/allotmentFormUtils";
import { buildAllotmentAssetDisplay } from "../utils/estMdmsUtils";
import styles from "../styles/ESTAssignAssets.module.scss";

const PAYLOAD_KEY = "Allotments";
const STEP_KEY = "Allotments";

const ESTAssignAssets = ({ onSelect, onDraftSave, onDraftClear, config, formData, t: tProp }) => {
  const { t: tHook } = useTranslation();
  const t = tProp || tHook;
  const persistedData = useMemo(() => formData || {}, [formData]);

  const sessionDraft =
    persistedData?.[STEP_KEY]?.[PAYLOAD_KEY]?.[0] || {};

  const assetDisplay = useMemo(
    () => buildAllotmentAssetDisplay(persistedData?.assetData || {}, {}, t),
    [persistedData, t]
  );

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
    <DynamicFormStep
      config={config}
      localOverrides={estateAllotmentFormOverrides}
      onSelect={onSelect}
      formData={dynamicPersistedData}
      editData={prefillData}
      resetBaseline={resetBaseline}
      t={t}
      wrapperClassName={`employeeCard ${styles.estAssignAssets}`}
      defaultHeaderCode="EST_COMMMON_ASSIGN_ASSETS"
      draft={
        onDraftSave
          ? {
              buildStepData: (flat) => ({ [PAYLOAD_KEY]: [{ ...flat }] }),
              onPersist: onDraftSave,
              onClear: onDraftClear,
              label: "EST_ADD_AS_DRAFT",
              successLabel: "EST_DRAFT_SAVED",
            }
          : undefined
      }
    />
  );
};

export default ESTAssignAssets;
