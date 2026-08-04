import React from "react";
import { useTranslation } from "react-i18next";
import {
  Card,
  CardTitle,
  FieldsRow,
  TextField,
  CheckboxField,
  ThemeEditorLayout,
  FieldSubsection,
  SecondaryActionsEditor
} from "../../components/ThemeCustomizeComponents";

import { getCardIcon } from "../../utils";
import { useThemeConfigEditor } from "../../hooks/useThemeConfigEditor";

/**
 * OnBoardingLogin Component
 * Handles layout settings, fields definition, validations, actions, and separator setups
 * for the user onboarding login view.
 * 
 * WHY REFACTORED:
 * - Centralized local drafts cache and submit API actions using the central `useThemeConfigEditor` custom hook.
 * - Encapsulated form-level layout containers (page titles, previews, save footers, confirm dialogs, and alerts) into `<ThemeEditorLayout>`.
 * - Eliminated CSS inline styling repetitions on sub-headers/borders by replacing them with the `<FieldSubsection>` component.
 * - Extracted dynamic footer secondary actions rendering array list into the `<SecondaryActionsEditor>` component.
 */
function OnBoardingLogin() {
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
  const loginStep = steps.login || {};
  const fields = loginStep.fields || [];
  const footer = loginStep.footer || {};

  /**
   * Adds a new empty secondary action button configuration to the login footer actions list.
   */
  const handleAddSecondaryAction = () => {
    const currentActions = footer.secondaryActions ? [...footer.secondaryActions] : [];
    const newActions = [...currentActions, { icon: "", label: "" }];
    set("pages.onboarding.steps.login.footer.secondaryActions", newActions);
  };

  /**
   * Removes a secondary action button item at the specified index.
   * 
   * @param {number} index - Index of the action to delete.
   */
  const handleRemoveSecondaryAction = (index) => {
    const currentActions = footer.secondaryActions ? [...footer.secondaryActions] : [];
    const newActions = currentActions.filter((_, idx) => idx !== index);
    set("pages.onboarding.steps.login.footer.secondaryActions", newActions);
  };

  return (
    <ThemeEditorLayout
      title="Onboarding - Login Configuration"
      previewUrl={`/${window?.contextPath}/employee/user/login`}
      hasUnsavedChanges={hasUnsavedChanges}
      onSubmit={handleSubmit}
      showConfirmModal={showConfirmModal}
      setShowConfirmModal={setShowConfirmModal}
      toast={toast}
      onCloseToast={() => setToast(null)}
    >
      {/* ── 1. Login Page Header ── */}
      <Card className="theme-card-margin">
        <CardTitle
          icon={getCardIcon("text")}
          title={t("Screen Header Settings")}
          description={t("Manage main heading, subtitle, and text content for the login screen")}
        />
        <div className="full-width-col">
          <TextField
            label={t("Heading")}
            value={loginStep.heading || ""}
            onChange={(v) => set("pages.onboarding.steps.login.heading", v)}
          />
          <TextField
            label={t("Description")}
            type="textarea"
            value={loginStep.description || ""}
            onChange={(v) => set("pages.onboarding.steps.login.description", v)}
          />
        </div>
      </Card>

      {/* ── 2. Form Fields Configuration ── */}
      <Card className="theme-card-margin">
        <CardTitle
          icon={getCardIcon("common")}
          title={t("Login Form Fields")}
          description={t("Configure input fields, labels, placeholders, and validation rules for language, city, and mobile inputs")}
        />

        {/* Language Selector Sub-Section */}
        {fields[0] && (
          <FieldSubsection title="Language Selector">
            <FieldsRow>
              <TextField
                label={t("LABEL")}
                value={fields[0].label || ""}
                onChange={(v) => set("pages.onboarding.steps.login.fields.0.label", v)}
              />
              <TextField
                label={t("DEFAULT VALUE")}
                value={fields[0].defaultValue || ""}
                onChange={(v) => set("pages.onboarding.steps.login.fields.0.defaultValue", v)}
              />
              <TextField
                label={t("REQUIRED ERROR MESSAGE")}
                value={fields[0].validation?.messages?.required || ""}
                onChange={(v) => set("pages.onboarding.steps.login.fields.0.validation.messages.required", v)}
              />
            </FieldsRow>
          </FieldSubsection>
        )}

        {/* City Selector Sub-Section */}
        {fields[1] && (
          <FieldSubsection title="City Selector">
            <div className="full-width-col">
              <FieldsRow>
                <TextField
                  label={t("LABEL")}
                  value={fields[1].label || ""}
                  onChange={(v) => set("pages.onboarding.steps.login.fields.1.label", v)}
                />
                <TextField
                  label={t("PLACEHOLDER")}
                  value={fields[1].placeholder || ""}
                  onChange={(v) => set("pages.onboarding.steps.login.fields.1.placeholder", v)}
                />
              </FieldsRow>
              <FieldsRow>
                <TextField
                  label={t("START ICON URL")}
                  value={fields[1].startIcon || ""}
                  onChange={(v) => set("pages.onboarding.steps.login.fields.1.startIcon", v)}
                />
                <TextField
                  label={t("REQUIRED ERROR MESSAGE")}
                  value={fields[1].validation?.messages?.required || ""}
                  onChange={(v) => set("pages.onboarding.steps.login.fields.1.validation.messages.required", v)}
                />
              </FieldsRow>
            </div>
          </FieldSubsection>
        )}

        {/* Mobile Number Sub-Section */}
        {fields[2] && (
          <FieldSubsection title="Mobile Number" hasBorder={false}>
            <div className="full-width-col">
              <FieldsRow>
                <TextField
                  label={t("LABEL")}
                  value={fields[2].label || ""}
                  onChange={(v) => set("pages.onboarding.steps.login.fields.2.label", v)}
                />
                <TextField
                  label={t("PLACEHOLDER")}
                  value={fields[2].placeholder || ""}
                  onChange={(v) => set("pages.onboarding.steps.login.fields.2.placeholder", v)}
                />
                <TextField
                  label={t("PREFIX")}
                  value={fields[2].prefix || ""}
                  onChange={(v) => set("pages.onboarding.steps.login.fields.2.prefix", v)}
                />
              </FieldsRow>
              <FieldsRow>
                <TextField
                  label={t("HELPER TEXT")}
                  value={fields[2].helperText || ""}
                  onChange={(v) => set("pages.onboarding.steps.login.fields.2.helperText", v)}
                />
                <TextField
                  label={t("HELPER ICON URL")}
                  value={fields[2].helperIcon || ""}
                  onChange={(v) => set("pages.onboarding.steps.login.fields.2.helperIcon", v)}
                />
              </FieldsRow>

              <div className="section-sub-title">
                {t("Validation Rules")}
              </div>
              <FieldsRow>
                <TextField
                  label={t("PATTERN (REGEX)")}
                  value={fields[2].validation?.pattern || ""}
                  onChange={(v) => set("pages.onboarding.steps.login.fields.2.validation.pattern", v)}
                />
                <TextField
                  label={t("PATTERN ERROR MESSAGE")}
                  value={fields[2].validation?.messages?.pattern || ""}
                  onChange={(v) => set("pages.onboarding.steps.login.fields.2.validation.messages.pattern", v)}
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
          description={t("Configure action buttons, separator layout, and trust verification badges")}
          rightElement={
            <button
              onClick={handleAddSecondaryAction}
              className="add-feature-btn"
            >
              <span>⊕</span> {t("Add Action")}
            </button>
          }
        />
        <div className="full-width-col theme-card-margin">
          <TextField
            label={t("Primary Action Label")}
            value={footer.primaryAction?.label || ""}
            onChange={(v) => set("pages.onboarding.steps.login.footer.primaryAction.label", v)}
          />

          {footer.secondaryActions && footer.secondaryActions.length > 0 && (
            <SecondaryActionsEditor
              actions={footer.secondaryActions}
              onAdd={handleAddSecondaryAction}
              onRemove={handleRemoveSecondaryAction}
              onChange={set}
              basePath="pages.onboarding.steps.login.footer.secondaryActions"
            />
          )}
        </div>

        <div className="two-col-grid">
          <CheckboxField
            label={t("Show Separator")}
            checked={!!footer.showSeparator}
            onChange={(v) => set("pages.onboarding.steps.login.footer.showSeparator", v)}
          />
          <CheckboxField
            label={t("Show Secure Info")}
            checked={!!footer.showSecureInfo}
            onChange={(v) => set("pages.onboarding.steps.login.footer.showSecureInfo", v)}
          />
        </div>
      </Card>
    </ThemeEditorLayout>
  );
}

export default OnBoardingLogin;
