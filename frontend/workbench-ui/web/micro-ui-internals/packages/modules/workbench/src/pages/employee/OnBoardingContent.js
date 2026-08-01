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
 * Renders the configuration form to manage the content displayed during the onboarding screens.
 * Allows visual designers and developers to configure backgrounds, layout styling, onboarding card panels,
 * brand titles, responsive images, dynamic features lists, and common security trust indicators.
 * 
 * DESIGN PRINCIPLES:
 * 1. ZERO Inline Styles - uses precompiled class names mapping to style rules in ThemeConfiguration.css.
 * 2. Full Localization - wraps all text labels and instructions in translation helper methods.
 * 3. Unified abstractions - utilizes shared components from ThemeCustomizeComponents.
 * 
 * @returns {React.ReactNode} Onboarding settings page view.
 */
function OnBoardingContent() {
  // Hook to handle application translations
  const { t } = useTranslation();

  // State handles configuration drafts and API tracking
  const [config, setConfig] = useState(getInitialThemeConfig());
  const [lastSavedConfig, setLastSavedConfig] = useState(getInitialThemeConfig());
  const [toast, setToast] = useState(null);
  const [showConfirmModal, setShowConfirmModal] = useState(false);

  // Computes unsaved changes status to dynamically disable/enable action footer buttons
  const hasUnsavedChanges = JSON.stringify(config) !== JSON.stringify(lastSavedConfig);

  // Extract nested onboarding layout config sections
  const onboarding = config.pages?.onboarding || {};
  const content = onboarding.content || {};
  const common = onboarding.common || {};

  /**
   * Updates configuration values dynamically at the given dot-notation path.
   * Also caches draft updates to localStorage for real-time persistence.
   * 
   * @param {string} path - Dot-notation destination path to set (e.g. "pages.onboarding.content.features").
   * @param {*} value - The new value (object, array, string) to save.
   */
  const set = (path, value) => {
    setConfig((prev) => {
      const next = deepSet(prev, path, value);
      localStorage.setItem("UPYOG_THEME_CONFIG", JSON.stringify(next));
      return next;
    });
  };

  /**
   * Appends a new blank feature item structure to the onboarding feature slider array.
   */
  const handleAddFeature = () => {
    const currentFeatures = content.features ? [...content.features] : [];
    const newFeatures = [...currentFeatures, { icon: "", title: "", description: "" }];
    set("pages.onboarding.content.features", newFeatures);
  };

  /**
   * Removes a specific onboarding feature item at the given index list.
   * 
   * @param {number} index - Index index list location of feature item to remove.
   */
  const handleRemoveFeature = (index) => {
    const currentFeatures = content.features ? [...content.features] : [];
    const newFeatures = currentFeatures.filter((_, idx) => idx !== index);
    set("pages.onboarding.content.features", newFeatures);
  };

  /**
   * Submits current theme changes. Acknowledges confirm modal action,
   * posts configurations to the service API, updates the saved checkpoint, and displays toast feedback.
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

      {/* Page Title & Live Preview Trigger Actions */}
      <div className="theme-header-row">
        <div className="theme-form-title">
          {t("Onboarding - Common Content")}
        </div>
        <PreviewButton targetUrl={`/${window?.contextPath}/employee/user/language-selection`} hasUnsavedChanges={hasUnsavedChanges} onSubmit={handleSubmit} />
      </div>

      {/* ── 1. Background Configuration Section ── */}
      {/* Configure solid background colors and responsive graphics for mobile, tablet, laptop, and desktop views */}
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

      {/* ── 2. Onboarding Card Panel Styling Section ── */}
      {/* Customize background color, card drop-shadow styles, and device-responsive graphics for card grids */}
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

      {/* ── 3. Brand Content Settings Section ── */}
      {/* Configures major title headers, highlighted subtexts, and subtitle lines */}
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

      {/* ── 4. Key Features Configuration List Section ── */}
      {/* Dynamic list rendering onboarding slide icons, heading strings, and descriptions */}
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

      {/* ── 5. Common Security Trust Banner Section ── */}
      {/* Trust banners positioned at onboarding footers (displays badge icon URLs and security slogans) */}
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

      {/* ── Submit Action Footer ── */}
      <div className="submit-container">
        <button
          onClick={() => setShowConfirmModal(true)}
          className="submit-btn"
          disabled={!hasUnsavedChanges}
        >
          {t("SUBMIT CHANGES")}
        </button>
      </div>

      {/* Confirmation Modal to double-check configuration changes */}
      <SubmitConfirmModal
        isOpen={showConfirmModal}
        onClose={() => setShowConfirmModal(false)}
        onConfirm={handleSubmit}
      />

      {/* State notification toasts */}
      {toast && <Toast label={toast.label} error={toast.error} onClose={() => setToast(null)} />}
    </div>
  );
}

export default OnBoardingContent;
