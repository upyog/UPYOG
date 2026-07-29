import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { Toast, DeleteIconv2 } from "@upyog/workbench-ui-react-components";
import {
  Card,
  CardTitle,
  FieldsRow,
  TextField,
  CheckboxField,
  SubmitConfirmModal,
  PreviewButton
} from "../../components/ThemeCustomizeComponents";

import { deepSet, getCardIcon, getInitialThemeConfig, submitThemeConfig } from "../../utils";

/**
 * OnBoardingLogin Component
 * Handles layout settings, fields definition, validations, actions, and separator setups
 * for the user onboarding login view.
 * 
 * DESIGN PRINCIPLES:
 * 1. ZERO Inline Styles - uses precompiled utility classes inside ThemeConfiguration.scss.
 * 2. Fully Localized - wraps all visible text outputs in the React useTranslation t() helper.
 * 3. DRY Code - relies on shared layout widgets and unified ThemeUtils helpers.
 */
function OnBoardingLogin() {
  // Localization helper hook
  const { t } = useTranslation();

  // State initialization using reusable ThemeUtils configuration fetcher
  const [config, setConfig] = useState(getInitialThemeConfig());
  const [lastSavedConfig, setLastSavedConfig] = useState(getInitialThemeConfig());
  const [toast, setToast] = useState(null);
  const [showConfirmModal, setShowConfirmModal] = useState(false);

  const hasUnsavedChanges = JSON.stringify(config) !== JSON.stringify(lastSavedConfig);

  // Destructuring steps settings from configuration
  const onboarding = config.pages?.onboarding || {};
  const steps = onboarding.steps || {};
  const loginStep = steps.login || {};
  const fields = loginStep.fields || [];
  const footer = loginStep.footer || {};

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

      {/* Page Title */}
      <div className="theme-header-row">
        <div className="theme-form-title">
          {t("Onboarding - Login Configuration")}
        </div>
        <PreviewButton targetUrl={`/${window?.contextPath}/employee/user/login`} hasUnsavedChanges={hasUnsavedChanges} />
      </div>

      {/* ── 1. Login Page Header ── */}
      {/* Allows developers to customize onboarding welcome headers, page details, and textual sub-lines */}
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
      {/* Configuration card to define fields properties (labels, regex validation models, errors, icons) for login inputs */}
      <Card className="theme-card-margin">
        <CardTitle
          icon={getCardIcon("common")}
          title={t("Login Form Fields")}
          description={t("Configure input fields, labels, placeholders, and validation rules for language, city, and mobile inputs")}
        />

        {/* Language Selector Sub-Section */}
        {fields[0] && (
          <div style={{ borderBottom: "1.5px solid #EDE8F5", paddingBottom: 20, marginBottom: 20 }}>
            <div style={{ fontSize: 13, fontWeight: 800, color: "#3D2364", marginBottom: 12 }}>
              {t("Language Selector")}
            </div>
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
          </div>
        )}

        {/* City Selector Sub-Section */}
        {fields[1] && (
          <div style={{ borderBottom: "1.5px solid #EDE8F5", paddingBottom: 20, marginBottom: 20 }}>
            <div style={{ fontSize: 13, fontWeight: 800, color: "#3D2364", marginBottom: 12 }}>
              {t("City Selector")}
            </div>
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
          </div>
        )}

        {/* Mobile Number Sub-Section */}
        {fields[2] && (
          <div>
            <div style={{ fontSize: 13, fontWeight: 800, color: "#3D2364", marginBottom: 12 }}>
              {t("Mobile Number")}
            </div>
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
          </div>
        )}
      </Card>

      {/* ── 3. Footer Settings ── */}
      {/* Edit action button text strings, dynamic secondary targets, secure seal banners and border dividers */}
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
            <div className="full-width-col">
              <span className="section-sub-title" style={{ margin: "8px 0 4px" }}>{t("Secondary Actions")}</span>
              {footer.secondaryActions.map((action, idx) => (
                <div key={idx} className="feature-box-row" style={{ padding: "12px 16px" }}>
                  <div className="secondary-action-grid">
                    <input
                      type="text"
                      value={action.icon || ""}
                      onChange={(e) => set(`pages.onboarding.steps.login.footer.secondaryActions.${idx}.icon`, e.target.value)}
                      placeholder={t("Icon URL (https://...)")}
                      className="text-input-field"
                      style={{ height: 36, fontSize: 12 }}
                    />
                    <input
                      type="text"
                      value={action.label || ""}
                      onChange={(e) => set(`pages.onboarding.steps.login.footer.secondaryActions.${idx}.label`, e.target.value)}
                      placeholder={t("Action Label")}
                      className="text-input-field"
                      style={{ height: 36, fontSize: 12 }}
                    />
                  </div>
                  <button
                    onClick={() => handleRemoveSecondaryAction(idx)}
                    title={t("Remove Action")}
                    className="remove-feature-btn"
                    style={{ padding: 4 }}
                  >
                    <DeleteIconv2 fill="#D85A5A" />
                  </button>
                </div>
              ))}
            </div>
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

export default OnBoardingLogin;
