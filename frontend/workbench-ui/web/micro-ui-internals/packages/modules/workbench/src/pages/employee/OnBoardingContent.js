import React from "react";
import { useTranslation } from "react-i18next";
import {
  Card,
  CardTitle,
  ColorField,
  TextField,
  CheckboxField,
  SelectField,
  ThemeEditorLayout,
  ResponsiveImageQuartet,
  FeatureListEditor
} from "../../components/ThemeCustomizeComponents";

import { getCardIcon } from "../../utils";
import { useThemeConfigEditor } from "../../hooks/useThemeConfigEditor";

/**
 * OnBoardingContent Component
 * Renders the configuration form to manage the content displayed during the onboarding screens.
 * 
 * WHY REFACTORED:
 * - Replaced local config state and submission boilerplate with the shared `useThemeConfigEditor` hook.
 * - Enclosed the entire form inside `<ThemeEditorLayout>` to reuse headers, previews, modal prompts, and toasts.
 * - Replaced duplicate 4-column responsive image input inputs for both root and left card layouts with the `<ResponsiveImageQuartet>` component.
 * - Replaced the dynamic features array rendering code block with the `<FeatureListEditor>` component.
 */
function OnBoardingContent() {
  const { t } = useTranslation();

  // Load refactored configuration editor states
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
  const content = onboarding.content || {};
  const common = onboarding.common || {};

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
   * @param {number} index - Index location of feature item to remove.
   */
  const handleRemoveFeature = (index) => {
    const currentFeatures = content.features ? [...content.features] : [];
    const newFeatures = currentFeatures.filter((_, idx) => idx !== index);
    set("pages.onboarding.content.features", newFeatures);
  };

  return (
    <ThemeEditorLayout
      title="Onboarding - Common Content"
      previewUrl={`/${window?.contextPath}/employee/user/language-selection`}
      hasUnsavedChanges={hasUnsavedChanges}
      onSubmit={handleSubmit}
      showConfirmModal={showConfirmModal}
      setShowConfirmModal={setShowConfirmModal}
      toast={toast}
      onCloseToast={() => setToast(null)}
    >
      {/* ── 1. Background Configuration Section ── */}
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

          <ResponsiveImageQuartet
            responsiveData={content.background.root.responsive}
            onChange={set}
            basePath="pages.onboarding.content.background.root"
          />
        </Card>
      )}

      {/* ── 2. Onboarding Card Panel Styling Section ── */}
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

          <ResponsiveImageQuartet
            responsiveData={content.background.sections.left.card.responsive}
            onChange={set}
            basePath="pages.onboarding.content.background.sections.left.card"
          />
        </Card>
      )}

      {/* ── 3. Brand Content Settings Section ── */}
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
      {content.features && (
        <FeatureListEditor
          features={content.features}
          onAdd={handleAddFeature}
          onRemove={handleRemoveFeature}
          onChange={set}
          basePath="pages.onboarding.content.features"
        />
      )}

      {/* ── 5. Common Security Trust Banner Section ── */}
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
    </ThemeEditorLayout>
  );
}

export default OnBoardingContent;
