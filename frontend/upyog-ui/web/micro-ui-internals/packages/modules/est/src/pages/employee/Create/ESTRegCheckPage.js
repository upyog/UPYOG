import React, { useState, useMemo, useCallback } from "react";
import {
  Card,
  CardHeader,
  CardSubHeader,
  CheckBox,
  SubmitBar,
  EditIcon,
  LinkButton,
  DynamicObjectRenderer,
} from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import styles from "../../../styles/ESTRegCheckPage.module.scss";
import { buildDynamicAssetPayload, getEstateRequestInfo } from "../../../utils/assetPayloadUtils";
import estateFormConfig from "../../../config/estateFormConfig";

// ─── ActionButton ──────────────────────────────────────────────────────────────
const ActionButton = ({ jumpTo, flatAsset }) => {
  const navigate = Digit.Hooks.useCustomNavigate();
  return (
    <LinkButton
      label={<EditIcon className={styles["estRegCheckPage__edit-icon"]} />}
      className="check-page-link-button"
      onClick={() => navigate(jumpTo, { state: { editData: flatAsset } })}
    />
  );
};

// ─── ESTRegCheckPage ───────────────────────────────────────────────────────────
const ESTRegCheckPage = ({ onSubmit, onError, value = {}, config }) => {
  const { t } = useTranslation();
  const [agree, setAgree] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const tenantId = useMemo(() => Digit.ULBService.getCurrentTenantId(), []);
  const mutation = Digit.Hooks.estate.useESTCreateAPI(tenantId);

  // value = params from ESTRegCreate (session storage)
  // shape: { newRegistration: { Assets: [flatAsset] } }
  const assetsArray = useMemo(
    () =>
      value?.newRegistration?.Assets ||
      value?.Assets?.Assets ||
      (Array.isArray(value?.Assets) ? value.Assets : null) ||
      [],
    [value]
  );

  const flatAsset = assetsArray[0] || {};

  // ── FIX: resolve the step config from EITHER shape ────────────────────────
  // ESTRegCreate passes `config` as a plain ARRAY of steps (Config[0].body
  // cloned, with .indexRoute tacked on) — not as { head, body }. The old
  // lookup `config?.body?.find(...)` was undefined for an array, so it fell
  // back to `config` itself, and `{ ...array, ...estateFormConfig }` produced
  // an object with numeric keys and NO `.form`. buildDynamicAssetPayload then
  // walked an empty form list, so the submitted Asset contained ONLY
  // staticFields/computedFields (assetName, locality, assetType, department…)
  // while every form field — buildingName, floor, totalFloorArea, dimensions,
  // rate, refAssetNo — was silently dropped and persisted as null.
  const rawRouteConfig = useMemo(() => {
    const steps = Array.isArray(config) ? config : config?.body || [];
    return (
      steps.find?.((step) => step.key === "newRegistration") ||
      steps.find?.((step) => Array.isArray(step.form) && step.form.length > 0) ||
      {}
    );
  }, [config]);
  const routeConfig = useMemo(
    () => ({ ...rawRouteConfig, ...estateFormConfig }),
    [rawRouteConfig]
  );

  // ─── Final submit ─────────────────────────────────────────────────────────────
  // Navigation is handled entirely by ESTRegCreate via onSubmit/onError props.
  // ESTRegCreate uses match.pathnameBase for absolute paths so we never land
  // on /check/acknowledgement by mistake.
  //
  // Gated on `agree`: SubmitBar is disabled until checked (UI level), and this
  // handler re-checks `agree` itself (logic level) so the mutation can never
  // fire without explicit user consent, regardless of how onSubmit is invoked.
  const handleFinalSubmit = useCallback(() => {
    if (!agree || isSubmitting) return;

    // Safety net: never submit a field-less payload again. If the form walk
    // has nothing to walk, something upstream broke — surface it loudly
    // instead of silently persisting a null-riddled Asset.
    if (!Array.isArray(routeConfig.form) || routeConfig.form.length === 0) {
      console.error(
        "EST_CREATE: routeConfig.form is empty — refusing to submit an incomplete Asset. config prop was:",
        config
      );
      onError && onError(new Error("EST_CREATE: empty form config"));
      return;
    }

    const assetPayload = buildDynamicAssetPayload(routeConfig, flatAsset, tenantId);
    const payload_updated = {
      RequestInfo: getEstateRequestInfo({ msgId: `${Date.now()}|en_IN`, plainAccessRequest: {} }),
      Assets: [assetPayload],
    };

    setIsSubmitting(true);

    mutation.mutate(payload_updated, {
      onSuccess: (response) => {
        setIsSubmitting(false);
        // Hand off to ESTRegCreate.estcreate — it clears session and navigates
        // to the absolute acknowledgement path
        onSubmit && onSubmit(response);
      },
      onError: (error) => {
        console.error("EST Create error status:", error?.response?.status);
        console.error("EST Create error response:", error?.response?.data);
        setIsSubmitting(false);
        // Hand off to ESTRegCreate.estcreateError
        onError && onError(error);
      },
    });
  }, [agree, isSubmitting, routeConfig, flatAsset, tenantId, mutation, onSubmit, onError, config]);

  const handleAgreeChange = useCallback(() => setAgree((prev) => !prev), []);

  return (
    <Card>
      <CardHeader>{t("EST_REGISTRATION_SUMMARY")}</CardHeader>
      <CardSubHeader>{t("EST_ASSET_DETAILS")}</CardSubHeader>

      <div className={styles["estRegCheckPage__action-row"]}>
        <ActionButton
          jumpTo="/upyog-ui/employee/est/create-asset/newRegistration"
          flatAsset={flatAsset}
        />
      </div>

      <DynamicObjectRenderer data={assetsArray} t={t} />

      <div className={styles["estRegCheckPage__declaration"]}>
        <CheckBox label={t("EST_FINAL_DECLARATION_MESSAGE")} onChange={handleAgreeChange} />
      </div>

      <div className={styles["estRegCheckPage__submit-row"]}>
        <SubmitBar
          label={t("EST_COMMON_SUBMIT")}
          onSubmit={handleFinalSubmit}
          disabled={!agree || isSubmitting}
        />
      </div>
    </Card>
  );
};

export default ESTRegCheckPage;
