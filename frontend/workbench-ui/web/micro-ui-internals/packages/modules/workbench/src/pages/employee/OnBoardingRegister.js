import React from "react";
import { useTranslation } from "react-i18next";
import {
  Card,
  CardTitle,
  FieldsRow,
  TextField,
  NumberField,
  CheckboxField,
  ThemeEditorLayout,
  FieldSubsection
} from "../../components/ThemeCustomizeComponents";

import { getCardIcon } from "../../utils";
import { useThemeConfigEditor } from "../../hooks/useThemeConfigEditor";

/**
 * OnBoardingRegister Component
 * Handles layout settings, fields definitions, validation patterns, and footer setups
 * for the user onboarding registration view.
 * 
 * WHY REFACTORED:
 * - Collapsed duplicate state, dirty-checking, and submit API actions under the centralized `useThemeConfigEditor` hook.
 * - Eliminated boilerplate wrappers (main layouts, action footers, confirmations, toasts) by wrapping the page contents in `<ThemeEditorLayout>`.
 * - Cleaned up repeated, inline styles for dividers and subheadings using the `<FieldSubsection>` component.
 */
function OnBoardingRegister() {
  const { t } = useTranslation();

  // Load refactored theme editor hook functions and properties
  const {
    config,
    toast,
    setToast,
    showConfirmModal,
    setShowConfirmModal,
    hasUnsavedChanges,
    set,
    handleSubmit
  } = useThemeConfigEditor();

  const onboarding = config.pages?.onboarding || {};
  const steps = onboarding.steps || {};
  const registerStep = steps.register || {};
  const fields = registerStep.fields || [];
  const footer = registerStep.footer || {};

  return (
    <ThemeEditorLayout
      title="Onboarding - Register Configuration"
      previewUrl={`/${window?.contextPath}/employee/user/language-selection`}
      hasUnsavedChanges={hasUnsavedChanges}
      onSubmit={handleSubmit}
      showConfirmModal={showConfirmModal}
      setShowConfirmModal={setShowConfirmModal}
      toast={toast}
      onCloseToast={() => setToast(null)}
    >
      {/* ── 1. Screen Header Settings ── */}
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
      <Card className="theme-card-margin">
        <CardTitle
          icon={getCardIcon("common")}
          title={t("Register Form Fields")}
          description={t("Configure input fields, labels, placeholders, and validation rules for full name and date of birth inputs")}
        />

        {/* Full Name Sub-Section */}
        {fields[0] && (
          <FieldSubsection title="Full Name Field">
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
          </FieldSubsection>
        )}

        {/* Date of Birth Sub-Section */}
        {fields[1] && (
          <FieldSubsection title="Date of Birth Field" hasBorder={false}>
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
          </FieldSubsection>
        )}
      </Card>

      {/* ── 3. Footer Settings ── */}
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
    </ThemeEditorLayout>
  );
}

export default OnBoardingRegister;
