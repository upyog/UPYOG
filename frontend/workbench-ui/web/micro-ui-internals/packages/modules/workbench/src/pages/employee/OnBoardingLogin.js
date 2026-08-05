import React from "react";
import { useTranslation } from "react-i18next";
import { AddIcon } from "@upyog/workbench-ui-react-components";
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

        {fields.map((field, idx) => {
          const basePath = `pages.onboarding.steps.login.fields.${idx}`;
          const getSubsectionTitle = (f) => {
            if (f.name === "language") return "Language Selector";
            if (f.name === "city") return "City Selector";
            if (f.name === "mobileNumber") return "Mobile Number Field";
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
                        <TextField
                          label={t("MIN LENGTH")}
                          type="number"
                          value={field.validation.minLength}
                          onChange={(v) => set(`${basePath}.validation.minLength`, parseInt(v) || "")}
                        />
                      )}
                      {field.validation.maxLength !== undefined && (
                        <TextField
                          label={t("MAX LENGTH")}
                          type="number"
                          value={field.validation.maxLength}
                          onChange={(v) => set(`${basePath}.validation.maxLength`, parseInt(v) || "")}
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
                          <TextField
                            label={t("MINIMUM AGE")}
                            type="number"
                            value={field.validation.minimumAge}
                            onChange={(v) => set(`${basePath}.validation.minimumAge`, parseInt(v) || "")}
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
          description={t("Configure action buttons, separator layout, and trust verification badges")}
          rightElement={
            <button
              onClick={handleAddSecondaryAction}
              className="add-feature-btn"
            >
              <AddIcon fill="#3D2364" /> {t("Add Action")}
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
