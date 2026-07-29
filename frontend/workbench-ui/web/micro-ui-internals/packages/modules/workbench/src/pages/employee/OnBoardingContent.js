import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { Toast, DeleteIconv2 } from "@upyog/workbench-ui-react-components";
import {
  Card,
  CardTitle,
  FieldsRow,
  ColorField,
  TextField,
  CheckboxField,
  SelectField,
  SubmitConfirmModal,
  PreviewButton
} from "../../components/ThemeCustomizeComponents";

import { deepSet, getCardIcon, getInitialThemeConfig, submitThemeConfig } from "../../utils";

/**
 * OnBoardingContent Component
 * Renders settings to configure background, card layouts, titles, subtitles,
 * and key features list for the user onboarding screens.
 * 
 * DESIGN PRINCIPLES:
 * 1. ZERO Inline Styles - uses precompiled utility classes inside ThemeConfiguration.scss.
 * 2. Fully Localized - wraps all visible text outputs in the React useTranslation t() helper.
 * 3. DRY Code - relies on shared layout widgets and unified ThemeUtils helpers.
 */
function OnBoardingContent() {
  // Localization helper hook
  const { t } = useTranslation();

  // State initialization using reusable ThemeUtils configuration fetcher
  const [config, setConfig] = useState(getInitialThemeConfig());
  const [lastSavedConfig, setLastSavedConfig] = useState(getInitialThemeConfig());
  const [toast, setToast] = useState(null);
  const [showConfirmModal, setShowConfirmModal] = useState(false);

  const hasUnsavedChanges = JSON.stringify(config) !== JSON.stringify(lastSavedConfig);

  // Destructuring child pathways from the core configuration JSON
  const onboarding = config.pages?.onboarding || {};
  const content = onboarding.content || {};
  const common = onboarding.common || {};

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
   * Adds a new empty feature item to the dynamic onboarding features array.
   * Modifies content.features pathway on the state object.
   */
  const handleAddFeature = () => {
    const currentFeatures = content.features ? [...content.features] : [];
    const newFeatures = [...currentFeatures, { icon: "", title: "", description: "" }];
    set("pages.onboarding.content.features", newFeatures);
  };

  /**
   * Removes a feature item at the specified index from the dynamic features array.
   * 
   * @param {number} index - Index of the item to delete.
   */
  const handleRemoveFeature = (index) => {
    const currentFeatures = content.features ? [...content.features] : [];
    const newFeatures = currentFeatures.filter((_, idx) => idx !== index);
    set("pages.onboarding.content.features", newFeatures);
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
          {t("Onboarding - Common Content")}
        </div>
        <PreviewButton targetUrl={`/${window?.contextPath}/employee/user/language-selection`} hasUnsavedChanges={hasUnsavedChanges} />
      </div>

      {/* ── 1. Background Configuration ── */}
      {/* Allows users to change background color and responsive image URLs for mobile, tablet, laptop, and desktop views */}
      {content.background?.root && (
        <Card className="theme-card-margin">
          <CardTitle
            icon={getCardIcon("background")}
            title={t("Background Configuration")}
            description={t("Root and responsive image settings")}
          />
          <div className="two-col-grid theme-card-margin">
            <ColorField
              label={t("Root Background Color")}
              value={content.background.root.backgroundColor}
              onChange={(v) => set("pages.onboarding.content.background.root.backgroundColor", v)}
            />
            <CheckboxField
              label={t("Show Color")}
              checked={content.background.root.showColor}
              onChange={(v) => set("pages.onboarding.content.background.root.showColor", v)}
            />
          </div>

          <div className="section-sub-title">
            {t("Responsive Images")}
          </div>
          <FieldsRow>
            <TextField
              label={t("MOBILE IMAGE URL")}
              value={content.background.root.responsive?.mobile?.image || ""}
              onChange={(v) => set("pages.onboarding.content.background.root.responsive.mobile.image", v)}
              placeholder="https://..."
            />
            <TextField
              label={t("TABLET IMAGE URL")}
              value={content.background.root.responsive?.tablet?.image || ""}
              onChange={(v) => set("pages.onboarding.content.background.root.responsive.tablet.image", v)}
              placeholder="https://..."
            />
            <TextField
              label={t("LAPTOP IMAGE URL")}
              value={content.background.root.responsive?.laptop?.image || ""}
              onChange={(v) => set("pages.onboarding.content.background.root.responsive.laptop.image", v)}
              placeholder="https://..."
            />
            <TextField
              label={t("DESKTOP IMAGE URL")}
              value={content.background.root.responsive?.desktop?.image || ""}
              onChange={(v) => set("pages.onboarding.content.background.root.responsive.desktop.image", v)}
              placeholder="https://..."
            />
          </FieldsRow>
        </Card>
      )}

      {/* ── 2. Card Configuration ── */}
      {/* Settings to customize onboarding card panel border styles, backgrounds, shadows, and device-responsive graphics */}
      {content.background?.sections?.left?.card && (
        <Card className="theme-card-margin">
          <CardTitle
            icon={getCardIcon("common")}
            title={t("Card Configuration")}
            description={t("Card styling and responsive image settings")}
          />
          <div className="two-col-grid theme-card-margin">
            <ColorField
              label={t("Background Color")}
              value={content.background.sections.left.card.backgroundColor}
              onChange={(v) => set("pages.onboarding.content.background.sections.left.card.backgroundColor", v)}
            />
            <CheckboxField
              label={t("Show Color")}
              checked={content.background.sections.left.card.showColor}
              onChange={(v) => set("pages.onboarding.content.background.sections.left.card.showColor", v)}
            />
          </div>
          <div className="two-col-grid theme-card-margin">
            <SelectField
              label={t("Shadow Style")}
              value={content.background.sections.left.card.shadow}
              options={["default", "none", "sm", "md", "lg"]}
              onChange={(v) => set("pages.onboarding.content.background.sections.left.card.shadow", v)}
            />
            <CheckboxField
              label={t("Show Shadow")}
              checked={content.background.sections.left.card.showShadow}
              onChange={(v) => set("pages.onboarding.content.background.sections.left.card.showShadow", v)}
            />
          </div>

          <div className="section-sub-title">
            {t("Responsive Images")}
          </div>
          <FieldsRow>
            <TextField
              label={t("MOBILE IMAGE URL")}
              value={content.background.sections.left.card.responsive?.mobile?.image || ""}
              onChange={(v) => set("pages.onboarding.content.background.sections.left.card.responsive.mobile.image", v)}
              placeholder="https://..."
            />
            <TextField
              label={t("TABLET IMAGE URL")}
              value={content.background.sections.left.card.responsive?.tablet?.image || ""}
              onChange={(v) => set("pages.onboarding.content.background.sections.left.card.responsive.tablet.image", v)}
              placeholder="https://..."
            />
            <TextField
              label={t("LAPTOP IMAGE URL")}
              value={content.background.sections.left.card.responsive?.laptop?.image || ""}
              onChange={(v) => set("pages.onboarding.content.background.sections.left.card.responsive.laptop.image", v)}
              placeholder="https://..."
            />
            <TextField
              label={t("DESKTOP IMAGE URL")}
              value={content.background.sections.left.card.responsive?.desktop?.image || ""}
              onChange={(v) => set("pages.onboarding.content.background.sections.left.card.responsive.desktop.image", v)}
              placeholder="https://..."
            />
          </FieldsRow>
        </Card>
      )}

      {/* ── 3. Brand Settings ── */}
      {/* Configures core marketing title strings and primary/secondary subtitle descriptions */}
      {content.brand && (
        <Card className="theme-card-margin">
          <CardTitle
            icon={getCardIcon("logo")}
            title={t("Brand Settings")}
            description={t("Titles and subtitles for the onboarding flow")}
          />
          <div className="two-col-grid theme-card-margin">
            <TextField
              label={t("Default Title")}
              value={content.brand.title?.default || ""}
              onChange={(v) => set("pages.onboarding.content.brand.title.default", v)}
            />
            <TextField
              label={t("Highlight Title")}
              value={content.brand.title?.highlight || ""}
              onChange={(v) => set("pages.onboarding.content.brand.title.highlight", v)}
            />
          </div>
          <div className="two-col-grid">
            <TextField
              label={t("Primary Subtitle")}
              value={content.brand.subtitle?.primary || ""}
              onChange={(v) => set("pages.onboarding.content.brand.subtitle.primary", v)}
            />
            <TextField
              label={t("Secondary Subtitle")}
              value={content.brand.subtitle?.secondary || ""}
              onChange={(v) => set("pages.onboarding.content.brand.subtitle.secondary", v)}
            />
          </div>
        </Card>
      )}

      {/* ── 4. Features List ── */}
      {/* Allows developers to dynamically add, edit, or remove key application feature items shown on the onboarding slide decks */}
      {content.features && (
        <Card className="theme-card-margin">
          <CardTitle
            icon={getCardIcon("sidebar")}
            title={t("Features List")}
            description={t("Dynamic list of key features")}
            rightElement={
              <button
                onClick={handleAddFeature}
                className="add-feature-btn"
              >
                <span>⊕</span> {t("Add Feature")}
              </button>
            }
          />
          <div className="full-width-col">
            {content.features.map((feature, idx) => (
              <div
                key={idx}
                className="feature-box-row"
              >
                <div className="feature-row-grid">
                  <TextField
                    label={t("ICON URL")}
                    value={feature.icon || ""}
                    onChange={(v) => set(`pages.onboarding.content.features.${idx}.icon`, v)}
                    placeholder="https://..."
                  />
                  <TextField
                    label={t("SHORT TITLE")}
                    value={feature.title || ""}
                    onChange={(v) => set(`pages.onboarding.content.features.${idx}.title`, v)}
                    placeholder={t("Title")}
                  />
                  <TextField
                    label={t("DESCRIPTION")}
                    value={feature.description || ""}
                    onChange={(v) => set(`pages.onboarding.content.features.${idx}.description`, v)}
                    placeholder={t("Description")}
                  />
                </div>
                <button
                  onClick={() => handleRemoveFeature(idx)}
                  title={t("Remove Feature")}
                  className="remove-feature-btn"
                >
                  <DeleteIconv2 fill="#D85A5A" />
                </button>
              </div>
            ))}
          </div>
        </Card>
      )}

      {/* ── 5. Common Secure Info ── */}
      {/* Configures badges and text describing trust/security measures below fields */}
      {common.secureInfo && (
        <Card className="theme-card-margin">
          <CardTitle
            icon={getCardIcon("brand")}
            title={t("Common Secure Info")}
            description={t("Security badges and trust text")}
          />
          <div className="two-col-grid">
            <TextField
              label={t("Secure Icon URL")}
              value={common.secureInfo.icon || ""}
              onChange={(v) => set("pages.onboarding.common.secureInfo.icon", v)}
            />
            <TextField
              label={t("Secure Text")}
              value={common.secureInfo.text || ""}
              onChange={(v) => set("pages.onboarding.common.secureInfo.text", v)}
            />
          </div>
        </Card>
      )}

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

export default OnBoardingContent;
