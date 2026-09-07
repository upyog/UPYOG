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

        {fields.map((field, idx) => {
          const basePath = `pages.onboarding.steps.register.fields.${idx}`;
          const getSubsectionTitle = (f) => {
            if (f.name === "fullName") return "Full Name Field";
            if (f.name === "dob") return "Date of Birth Field";
            return `${f.label || f.name || `Field ${idx + 1}`}`;
          };

          return (
            <FieldSubsection
              key={field.name || idx}
              title={t(getSubsectionTitle(field))}
              hasBorder={idx !== fields.length - 1}
            >
              <div className="full-width-col">
                <FieldsRow>
                  <TextField
                    label={t("FIELD NAME (READ ONLY)")}
                    value={field.name || ""}
                    disabled={true}
                  />
                  <TextField
                    label={t("FIELD TYPE (READ ONLY)")}
                    value={field.type || ""}
                    disabled={true}
                  />
                  <TextField
                    label={t("LABEL")}
                    value={field.label || ""}
                    onChange={(v) => set(`${basePath}.label`, v)}
                  />
                </FieldsRow>

                <FieldsRow>
                  {field.placeholder !== undefined && (
                    <TextField
                      label={t("PLACEHOLDER")}
                      value={field.placeholder || ""}
                      onChange={(v) => set(`${basePath}.placeholder`, v)}
                    />
                  )}
                  {field.defaultValue !== undefined && (
                    <TextField
                      label={t("DEFAULT VALUE")}
                      value={field.defaultValue || ""}
                      onChange={(v) => set(`${basePath}.defaultValue`, v)}
                    />
                  )}
                  {field.prefix !== undefined && (
                    <TextField
                      label={t("PREFIX")}
                      value={field.prefix || ""}
                      onChange={(v) => set(`${basePath}.prefix`, v)}
                    />
                  )}
                </FieldsRow>

                {(field.startIcon !== undefined || field.helperIcon !== undefined || field.helperText !== undefined) && (
                  <FieldsRow>
                    {field.startIcon !== undefined && (
                      <TextField
                        label={t("START ICON URL")}
                        value={field.startIcon || ""}
                        onChange={(v) => set(`${basePath}.startIcon`, v)}
                      />
                    )}
                    {field.helperIcon !== undefined && (
                      <TextField
                        label={t("HELPER ICON URL")}
                        value={field.helperIcon || ""}
                        onChange={(v) => set(`${basePath}.helperIcon`, v)}
                      />
                    )}
                    {field.helperText !== undefined && (
                      <TextField
                        label={t("HELPER TEXT")}
                        value={field.helperText || ""}
                        onChange={(v) => set(`${basePath}.helperText`, v)}
                      />
                    )}
                  </FieldsRow>
                )}

                {field.validation && (
                  <>
                    <div className="section-sub-title">
                      {t("Validation Rules")}
                    </div>
                    <FieldsRow>
                      {field.validation.required !== undefined && (
                        <CheckboxField
                          label={t("Required")}
                          checked={!!field.validation.required}
                          onChange={(v) => set(`${basePath}.validation.required`, v)}
                        />
                      )}
                      {field.validation.minLength !== undefined && (
                        <NumberField
                          label={t("MIN LENGTH")}
                          value={field.validation.minLength || 0}
                          onChange={(v) => set(`${basePath}.validation.minLength`, parseInt(v) || 0)}
                        />
                      )}
                      {field.validation.maxLength !== undefined && (
                        <NumberField
                          label={t("MAX LENGTH")}
                          value={field.validation.maxLength || 0}
                          onChange={(v) => set(`${basePath}.validation.maxLength`, parseInt(v) || 0)}
                        />
                      )}
                    </FieldsRow>

                    {(field.validation.pattern !== undefined || field.validation.format !== undefined || field.validation.minimumAge !== undefined) && (
                      <FieldsRow>
                        {field.validation.pattern !== undefined && (
                          <TextField
                            label={t("PATTERN (REGEX)")}
                            value={field.validation.pattern || ""}
                            onChange={(v) => set(`${basePath}.validation.pattern`, v)}
                          />
                        )}
                        {field.validation.format !== undefined && (
                          <TextField
                            label={t("DATE FORMAT")}
                            value={field.validation.format || ""}
                            onChange={(v) => set(`${basePath}.validation.format`, v)}
                          />
                        )}
                        {field.validation.minimumAge !== undefined && (
                          <NumberField
                            label={t("MINIMUM AGE")}
                            value={field.validation.minimumAge || 0}
                            onChange={(v) => set(`${basePath}.validation.minimumAge`, parseInt(v) || 0)}
                          />
                        )}
                      </FieldsRow>
                    )}

                    {field.validation.messages && (
                      <>
                        <div className="section-sub-title">
                          {t("Validation Error Messages")}
                        </div>
                        <FieldsRow>
                          {field.validation.messages.required !== undefined && (
                            <TextField
                              label={t("REQUIRED ERROR MESSAGE")}
                              value={field.validation.messages.required || ""}
                              onChange={(v) => set(`${basePath}.validation.messages.required`, v)}
                            />
                          )}
                          {field.validation.messages.minLength !== undefined && (
                            <TextField
                              label={t("MIN LENGTH ERROR MESSAGE")}
                              value={field.validation.messages.minLength || ""}
                              onChange={(v) => set(`${basePath}.validation.messages.minLength`, v)}
                            />
                          )}
                          {field.validation.messages.maxLength !== undefined && (
                            <TextField
                              label={t("MAX LENGTH ERROR MESSAGE")}
                              value={field.validation.messages.maxLength || ""}
                              onChange={(v) => set(`${basePath}.validation.messages.maxLength`, v)}
                            />
                          )}
                          {field.validation.messages.pattern !== undefined && (
                            <TextField
                              label={t("PATTERN ERROR MESSAGE")}
                              value={field.validation.messages.pattern || ""}
                              onChange={(v) => set(`${basePath}.validation.messages.pattern`, v)}
                            />
                          )}
                          {field.validation.messages.invalid !== undefined && (
                            <TextField
                              label={t("INVALID DATE ERROR MESSAGE")}
                              value={field.validation.messages.invalid || ""}
                              onChange={(v) => set(`${basePath}.validation.messages.invalid`, v)}
                            />
                          )}
                          {field.validation.messages.minimumAge !== undefined && (
                            <TextField
                              label={t("MIN AGE ERROR MESSAGE")}
                              value={field.validation.messages.minimumAge || ""}
                              onChange={(v) => set(`${basePath}.validation.messages.minimumAge`, v)}
                            />
                          )}
                        </FieldsRow>
                      </>
                    )}
                  </>
                )}
              </div>
            </FieldSubsection>
          );
        })}
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
