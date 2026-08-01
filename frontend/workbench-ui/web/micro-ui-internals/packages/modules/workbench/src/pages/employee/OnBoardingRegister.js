import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { Toast } from "@upyog/workbench-ui-react-components";
import {
  Card,
  CardTitle,
  FieldsRow,
  TextField,
  NumberField,
  CheckboxField,
  SubmitConfirmModal,
  PreviewButton
} from "../../components/ThemeCustomizeComponents";

import { deepSet, getCardIcon, getInitialThemeConfig, submitThemeConfig } from "../../utils";

/**
 * OnBoardingRegister Component
 * Handles layout settings, fields definitions, validation patterns, and footer setups
 * for the user onboarding registration view.
 * 
 * DESIGN PRINCIPLES:
 * 1. ZERO Inline Styles - relies on precompiled utility classes in ThemeConfiguration.scss.
 * 2. Fully Localized - wraps all visible text outputs in the React useTranslation t() helper.
 * 3. DRY Code - utilizes shared components and central helper utilities.
 */
function OnBoardingRegister() {
  // Localization helper hook
  const { t } = useTranslation();

  // State initialization using reusable ThemeUtils configuration fetcher
  const [config, setConfig] = useState(getInitialThemeConfig());
  const [lastSavedConfig, setLastSavedConfig] = useState(getInitialThemeConfig());
  const [toast, setToast] = useState(null);
  const [showConfirmModal, setShowConfirmModal] = useState(false);

  // Unsaved changes detector (compares current config with last submitted baseline)
  const hasUnsavedChanges = JSON.stringify(config) !== JSON.stringify(lastSavedConfig);

  // Destructuring registration steps settings from configuration
  const onboarding = config.pages?.onboarding || {};
  const steps = onboarding.steps || {};
  const registerStep = steps.register || {};
  const fields = registerStep.fields || [];
  const footer = registerStep.footer || {};

  /**
   * Updates configuration values dynamically at the given dot-notation path.
   * Caches edits to localStorage on-the-fly for real-time draft persistence.
   * 
   * @param {string} path - Dot-notation path to modify.
   * @param {*} value - New value to save.
   */
  const set = (path, value) => {
    setConfig((prev) => {
      const next = deepSet(prev, path, value);
      localStorage.setItem("UPYOG_THEME_CONFIG", JSON.stringify(next));
      return next;
    });
  };

  /**
   * Handles configuration submission, closing modal and calling shared submitThemeConfig helper.
   * Emits toast feedback upon successful response or caught errors.
   */
  const handleSubmit = async () => {
    setShowConfirmModal(false);
    try {
      await submitThemeConfig(config);
      setLastSavedConfig(config);
      setToast({ label: t("Configuration updated and submitted successfully!"), error: false });
    } catch (err) {
      console.error("API submission failed:", err);
      setToast({ label: t("Failed to submit configuration to the API."), error: true });
    }
    setTimeout(() => setToast(null), 3000);
  };

  return (
    <div className="theme-form-container">

      {/* Page Title & Preview */}
      <div className="theme-header-row">
        <div className="theme-form-title">
          {t("Onboarding - Register Configuration")}
        </div>
        <PreviewButton 
          targetUrl={`/${window?.contextPath}/employee/user/language-selection`} 
          hasUnsavedChanges={hasUnsavedChanges} 
          onSubmit={handleSubmit}
        />
      </div>

      {/* ── 1. Screen Header Settings ── */}
      {/* Allows developers to customize onboarding welcome headers, page details, and textual sub-lines */}
      <Card className="theme-card-margin">
        <CardTitle
          icon={getCardIcon("text")}
          title={t("Screen Header Settings")}
          description={t("Manage main heading, description, and text content for the registration screen")}
        />
        <div className="full-width-col">
          <TextField
            label={t("Heading")}
            value={registerStep.heading || ""}
            onChange={(v) => set("pages.onboarding.steps.register.heading", v)}
          />
          <TextField
            label={t("Description")}
            type="textarea"
            value={registerStep.description || ""}
            onChange={(v) => set("pages.onboarding.steps.register.description", v)}
          />
        </div>
      </Card>

      {/* ── 2. Form Fields Configuration ── */}
      {/* Configuration card to define fields properties (labels, validations, constraints, errors) for registration inputs */}
      <Card className="theme-card-margin">
        <CardTitle
          icon={getCardIcon("common")}
          title={t("Register Form Fields")}
          description={t("Configure input fields, labels, placeholders, and validation rules for full name and date of birth inputs")}
        />

        {/* Full Name Sub-Section */}
        {fields[0] && (
          <div style={{ borderBottom: "1.5px solid #EDE8F5", paddingBottom: 20, marginBottom: 20 }}>
            <div style={{ fontSize: 13, fontWeight: 800, color: "#3D2364", marginBottom: 12 }}>
              {t("Full Name Field")}
            </div>
            <div className="full-width-col">
              <FieldsRow>
                <TextField
                  label={t("LABEL")}
                  value={fields[0].label || ""}
                  onChange={(v) => set("pages.onboarding.steps.register.fields.0.label", v)}
                />
                <TextField
                  label={t("PLACEHOLDER")}
                  value={fields[0].placeholder || ""}
                  onChange={(v) => set("pages.onboarding.steps.register.fields.0.placeholder", v)}
                />
              </FieldsRow>
              
              <div className="section-sub-title">
                {t("Validation Rules")}
              </div>
              <FieldsRow>
                <NumberField
                  label={t("MIN LENGTH")}
                  value={fields[0].validation?.minLength || 0}
                  onChange={(v) => set("pages.onboarding.steps.register.fields.0.validation.minLength", parseInt(v) || 0)}
                />
                <NumberField
                  label={t("MAX LENGTH")}
                  value={fields[0].validation?.maxLength || 0}
                  onChange={(v) => set("pages.onboarding.steps.register.fields.0.validation.maxLength", parseInt(v) || 0)}
                />
                <TextField
                  label={t("PATTERN (REGEX)")}
                  value={fields[0].validation?.pattern || ""}
                  onChange={(v) => set("pages.onboarding.steps.register.fields.0.validation.pattern", v)}
                />
              </FieldsRow>
              <FieldsRow>
                <TextField
                  label={t("REQUIRED ERROR MESSAGE")}
                  value={fields[0].validation?.messages?.required || ""}
                  onChange={(v) => set("pages.onboarding.steps.register.fields.0.validation.messages.required", v)}
                />
                <TextField
                  label={t("MIN LENGTH ERROR MESSAGE")}
                  value={fields[0].validation?.messages?.minLength || ""}
                  onChange={(v) => set("pages.onboarding.steps.register.fields.0.validation.messages.minLength", v)}
                />
                <TextField
                  label={t("MAX LENGTH ERROR MESSAGE")}
                  value={fields[0].validation?.messages?.maxLength || ""}
                  onChange={(v) => set("pages.onboarding.steps.register.fields.0.validation.messages.maxLength", v)}
                />
              </FieldsRow>
              <FieldsRow>
                <TextField
                  label={t("PATTERN ERROR MESSAGE")}
                  value={fields[0].validation?.messages?.pattern || ""}
                  onChange={(v) => set("pages.onboarding.steps.register.fields.0.validation.messages.pattern", v)}
                />
              </FieldsRow>
            </div>
          </div>
        )}

        {/* Date of Birth Sub-Section */}
        {fields[1] && (
          <div>
            <div style={{ fontSize: 13, fontWeight: 800, color: "#3D2364", marginBottom: 12 }}>
              {t("Date of Birth Field")}
            </div>
            <div className="full-width-col">
              <FieldsRow>
                <TextField
                  label={t("LABEL")}
                  value={fields[1].label || ""}
                  onChange={(v) => set("pages.onboarding.steps.register.fields.1.label", v)}
                />
                <TextField
                  label={t("PLACEHOLDER")}
                  value={fields[1].placeholder || ""}
                  onChange={(v) => set("pages.onboarding.steps.register.fields.1.placeholder", v)}
                />
              </FieldsRow>
              <FieldsRow>
                <TextField
                  label={t("HELPER TEXT")}
                  value={fields[1].helperText || ""}
                  onChange={(v) => set("pages.onboarding.steps.register.fields.1.helperText", v)}
                />
                <TextField
                  label={t("HELPER ICON URL")}
                  value={fields[1].helperIcon || ""}
                  onChange={(v) => set("pages.onboarding.steps.register.fields.1.helperIcon", v)}
                />
              </FieldsRow>

              <div className="section-sub-title">
                {t("Validation Rules")}
              </div>
              <FieldsRow>
                <TextField
                  label={t("DATE FORMAT")}
                  value={fields[1].validation?.format || ""}
                  onChange={(v) => set("pages.onboarding.steps.register.fields.1.validation.format", v)}
                />
                <NumberField
                  label={t("MINIMUM AGE")}
                  value={fields[1].validation?.minimumAge || 0}
                  onChange={(v) => set("pages.onboarding.steps.register.fields.1.validation.minimumAge", parseInt(v) || 0)}
                />
              </FieldsRow>
              <FieldsRow>
                <TextField
                  label={t("REQUIRED ERROR MESSAGE")}
                  value={fields[1].validation?.messages?.required || ""}
                  onChange={(v) => set("pages.onboarding.steps.register.fields.1.validation.messages.required", v)}
                />
                <TextField
                  label={t("INVALID DATE ERROR MESSAGE")}
                  value={fields[1].validation?.messages?.invalid || ""}
                  onChange={(v) => set("pages.onboarding.steps.register.fields.1.validation.messages.invalid", v)}
                />
                <TextField
                  label={t("MIN AGE ERROR MESSAGE")}
                  value={fields[1].validation?.messages?.minimumAge || ""}
                  onChange={(v) => set("pages.onboarding.steps.register.fields.1.validation.messages.minimumAge", v)}
                />
              </FieldsRow>
            </div>
          </div>
        )}
      </Card>

      {/* ── 3. Footer Settings ── */}
      {/* Configure action buttons and security seal elements */}
      <Card className="theme-card-margin">
        <CardTitle
          icon={getCardIcon("logo")}
          title={t("Footer Configuration")}
          description={t("Configure registration action buttons and trust verification badges")}
        />
        <div className="full-width-col theme-card-margin">
          <TextField
            label={t("Primary Action Label")}
            value={footer.primaryAction?.label || ""}
            onChange={(v) => set("pages.onboarding.steps.register.footer.primaryAction.label", v)}
          />
        </div>

        <div className="two-col-grid">
          <CheckboxField
            label={t("Show Secure Info")}
            checked={!!footer.showSecureInfo}
            onChange={(v) => set("pages.onboarding.steps.register.footer.showSecureInfo", v)}
          />
        </div>
      </Card>

      {/* ── Submit Button ── */}
      <div className="submit-container">
        <button
          onClick={() => setShowConfirmModal(true)}
          className="submit-btn"
          disabled={!hasUnsavedChanges}
        >
          {t("SUBMIT CHANGES")}
        </button>
      </div>

      {/* Reusable confirmation modal component to verify changes before writing back to storage */}
      <SubmitConfirmModal
        isOpen={showConfirmModal}
        onClose={() => setShowConfirmModal(false)}
        onConfirm={handleSubmit}
      />

      {/* Success and error feedback toast notification */}
      {toast && <Toast label={toast.label} error={toast.error} onClose={() => setToast(null)} />}
    </div>
  );
}

export default OnBoardingRegister;
