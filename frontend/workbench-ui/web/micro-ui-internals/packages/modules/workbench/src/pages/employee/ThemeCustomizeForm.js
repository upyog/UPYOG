import React from "react";
import { useTranslation } from "react-i18next";
import {
  SectionHeader,
  Card,
  CardTitle,
  FieldsRow,
  ColorField,
  NumberField,
  TextField,
  ShadowField,
  GradientField,
  UploadBox,
  ThemeEditorLayout
} from "../../components/ThemeCustomizeComponents";

import { formatLabel, getLabel, getCardIcon, groupMeta } from "../../utils";
import { useThemeConfigEditor } from "../../hooks/useThemeConfigEditor";

// ─── Main Component ───────────────────────────────────────────────────────────

/**
 * ThemeCustomizeForm Component
 * Renders the primary form to customize global theme presets (colors, shadows, radius, gradient stop configurations, etc.).
 * 
 * WHY REFACTORED:
 * - Replaced duplicate state management with the shared `useThemeConfigEditor` custom hook.
 * - Removed local layout wrappers, save footers, confirm modals, and toast boilerplates,
 *   replacing them with the unified `ThemeEditorLayout` wrapper.
 */
function ThemeCustomizeForm() {
  const { t } = useTranslation();

  // Loads centralized editor functions, active configurations, and dialog triggers
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

  // Group theme settings keys based on substring matches for loop categorizations.
  const themeKeys = Object.keys(config.theme || {});
  const colorGroupKeys = themeKeys.filter(k => k.toLowerCase().includes("color"));
  const shadowKeys = themeKeys.filter(k => k.toLowerCase().includes("shadow"));
  const radiusKeys = themeKeys.filter(k => k.toLowerCase().includes("radius"));
  const layoutKeys = themeKeys.filter(k => k.toLowerCase().includes("layout"));
  const gradientKeys = themeKeys.filter(k => k.toLowerCase().includes("gradient"));
  const typographyKeys = themeKeys.filter(k => k.toLowerCase().includes("typography") || k.toLowerCase().includes("font"));

  // Track standard section keys to identify any custom or additional group entries.
  const handledKeys = [
    ...colorGroupKeys,
    ...shadowKeys,
    ...radiusKeys,
    ...layoutKeys,
    ...gradientKeys,
    ...typographyKeys
  ];
  const otherThemeKeys = themeKeys.filter(k => !handledKeys.includes(k));
  const configKeys = Object.keys(config || {}).filter(k => k === "common");

  return (
    <ThemeEditorLayout
      title="Customize Theme"
      previewUrl={`/${window?.contextPath}/employee`}
      hasUnsavedChanges={hasUnsavedChanges}
      onSubmit={handleSubmit}
      showConfirmModal={showConfirmModal}
      setShowConfirmModal={setShowConfirmModal}
      toast={toast}
      onCloseToast={() => setToast(null)}
    >

      {/* ── 1. Colors Theme Section ─────────────────────────────────── */}
      <SectionHeader number={1} title={t("Colors Theme")} />

      {colorGroupKeys.map((sectionKey) => {
        const colorsVal = config.theme[sectionKey];
        if (typeof colorsVal !== "object" || colorsVal === null) return null;

        return (
          <div key={sectionKey}>
            {/* Text Colors */}
            {colorsVal.text && (
              <Card className="theme-card-margin">
                <CardTitle icon={groupMeta.text.icon} title={t(groupMeta.text.title)} description={t(groupMeta.text.description)} />
                <FieldsRow>
                  {Object.entries(colorsVal.text).map(([colorKey, colorVal]) => (
                    <ColorField
                      key={colorKey}
                      label={t(getLabel("text", colorKey))}
                      value={colorVal}
                      onChange={(v) => set(`${sectionKey}.text.${colorKey}`, v)}
                    />
                  ))}
                </FieldsRow>
              </Card>
            )}

            {/* Brand + Common Colors (Side-by-side row) */}
            <div className="section-row-grid theme-card-margin">
              {colorsVal.brand && (
                <Card>
                  <CardTitle icon={groupMeta.brand.icon} title={t(groupMeta.brand.title)} description={t(groupMeta.brand.description)} />
                  <div className="full-width-col">
                    {Object.entries(colorsVal.brand).map(([colorKey, colorVal]) => (
                      <GradientField
                        key={colorKey}
                        label={t(getLabel("brand", colorKey))}
                        value={colorVal}
                        onChange={(v) => set(`${sectionKey}.brand.${colorKey}`, v)}
                      />
                    ))}
                  </div>
                </Card>
              )}
              {colorsVal.common && (
                <Card>
                  <CardTitle icon={groupMeta.common.icon} title={t(groupMeta.common.title)} description={t(groupMeta.common.description)} />
                  <FieldsRow>
                    {Object.entries(colorsVal.common).map(([colorKey, colorVal]) => (
                      <ColorField
                        key={colorKey}
                        label={t(getLabel("common", colorKey))}
                        value={colorVal}
                        onChange={(v) => set(`${sectionKey}.common.${colorKey}`, v)}
                      />
                    ))}
                  </FieldsRow>
                </Card>
              )}
            </div>

            {/* Background Colors */}
            {colorsVal.background && (
              <Card className="theme-card-margin">
                <CardTitle icon={groupMeta.background.icon} title={t(groupMeta.background.title)} description={t(groupMeta.background.description)} />
                <FieldsRow>
                  {Object.entries(colorsVal.background).map(([colorKey, colorVal]) => (
                    <ColorField
                      key={colorKey}
                      label={t(getLabel("background", colorKey))}
                      value={colorVal}
                      onChange={(v) => set(`${sectionKey}.background.${colorKey}`, v)}
                    />
                  ))}
                </FieldsRow>
              </Card>
            )}

            {/* Border + Divider Colors (Side-by-side row) */}
            <div className="section-row-grid theme-card-margin">
              {colorsVal.border && (
                <Card>
                  <CardTitle icon={groupMeta.border.icon} title={t(groupMeta.border.title)} description={t(groupMeta.border.description)} />
                  <FieldsRow>
                    {Object.entries(colorsVal.border).map(([colorKey, colorVal]) => (
                      <ColorField
                        key={colorKey}
                        label={t(getLabel("border", colorKey))}
                        value={colorVal}
                        onChange={(v) => set(`${sectionKey}.border.${colorKey}`, v)}
                      />
                    ))}
                  </FieldsRow>
                </Card>
              )}
              {colorsVal.divider && (
                <Card>
                  <CardTitle icon={groupMeta.divider.icon} title={t(groupMeta.divider.title)} description={t(groupMeta.divider.description)} />
                  <FieldsRow>
                    {Object.entries(colorsVal.divider).map(([colorKey, colorVal]) => (
                      <ColorField
                        key={colorKey}
                        label={t(getLabel("divider", colorKey))}
                        value={colorVal}
                        onChange={(v) => set(`${sectionKey}.divider.${colorKey}`, v)}
                      />
                    ))}
                  </FieldsRow>
                </Card>
              )}
            </div>

            {/* Dynamic/Additional Custom Color Groups */}
            <div className="section-row-grid theme-card-margin">
              {Object.entries(colorsVal || {}).map(([groupKey, groupVal]) => {
                const knownGroups = ["text", "brand", "common", "background", "border", "divider"];
                if (!knownGroups.includes(groupKey) && typeof groupVal === "object" && groupVal !== null) {
                  const meta = {
                    icon: getCardIcon("gradients"),
                    title: `${formatLabel(groupKey)} Colors`,
                    description: `Custom colors for ${formatLabel(groupKey)}`,
                  };

                  return (
                    <Card key={groupKey} className="theme-flex-card">
                      <CardTitle icon={meta.icon} title={t(meta.title)} description={t(meta.description)} />
                      <FieldsRow className="fields-row-auto-margin">
                        {Object.entries(groupVal).map(([colorKey, colorVal]) => (
                          <ColorField
                            key={colorKey}
                            label={t(`${formatLabel(colorKey)} Color`)}
                            value={colorVal}
                            onChange={(v) => set(`${sectionKey}.${groupKey}.${colorKey}`, v)}
                          />
                        ))}
                      </FieldsRow>
                    </Card>
                  );
                }
                return null;
              })}
            </div>
          </div>
        );
      })}

      {/* ── 2. Shadows + 3. Border Radius Sections ─────────────────── */}
      <div className="section-row-grid">
        {/* Shadows (Full Card Width) */}
        <div>
          <SectionHeader number={2} title={t("Shadows")} />
          {shadowKeys.map((sectionKey) => {
            const shadowsVal = config.theme[sectionKey];
            if (typeof shadowsVal !== "object" || shadowsVal === null) return null;

            return (
              <Card key={sectionKey} className="theme-card-margin">
                <CardTitle icon={getCardIcon("shadows")} title={t(formatLabel(sectionKey))} description={t("Default and outlines")} />
                <div className="full-width-col">
                  {Object.entries(shadowsVal).map(([key, val]) => (
                    <ShadowField
                      key={key}
                      label={key === "default" ? t("Default Shadow") : t(formatLabel(key))}
                      value={val}
                      onChange={(v) => set(`${sectionKey}.${key}`, v)}
                    />
                  ))}
                </div>
              </Card>
            );
          })}
        </div>

        {/* Border Radius */}
        <div>
          <SectionHeader number={3} title={t("Border Radius")} />
          {radiusKeys.map((sectionKey) => {
            const radiusVal = config.theme[sectionKey];
            if (typeof radiusVal !== "object" || radiusVal === null) return null;

            return (
              <Card key={sectionKey} className="theme-card-margin">
                <CardTitle icon={getCardIcon("borderRadius")} title={t(formatLabel(sectionKey))} description={t("Default and outlines")} />
                <FieldsRow grid={false}>
                  {Object.entries(radiusVal).map(([key, val]) => {
                    const labelMap = { sm: "Small", md: "Medium" };
                    return (
                      <NumberField
                        key={key}
                        label={t(labelMap[key] || formatLabel(key))}
                        value={val}
                        unit="px"
                        onChange={(v) => set(`${sectionKey}.${key}`, v)}
                      />
                    );
                  })}
                </FieldsRow>
              </Card>
            );
          })}
        </div>
      </div>

      {/* ── 4. Layout Section ───────────────────────────────────────── */}
      <SectionHeader number={4} title={t("Layout")} />
      {layoutKeys.map((sectionKey) => {
        const layoutVal = config.theme[sectionKey];
        if (typeof layoutVal !== "object" || layoutVal === null) return null;

        return (
          <div key={sectionKey} className="section-row-grid theme-card-margin">
            {Object.entries(layoutVal).map(([subKey, subVal]) => {
              if (typeof subVal === "object" && subVal !== null) {
                const layoutMeta = {
                  sidebar: { icon: getCardIcon("sidebar"), title: "Sidebar", description: "Layout and sidebar" },
                  header: { icon: getCardIcon("header"), title: "Header", description: "Layout and Header" },
                };
                const meta = layoutMeta[subKey] || {
                  icon: getCardIcon("sidebar"),
                  title: formatLabel(subKey),
                  description: `Layout settings for ${formatLabel(subKey)}`,
                };
                return (
                  <Card key={subKey}>
                    <CardTitle icon={meta.icon} title={t(meta.title)} description={t(meta.description)} />
                    <FieldsRow grid={false}>
                      {Object.entries(subVal).map(([fieldKey, fieldVal]) => {
                        const labelMap = {
                          width: "Small",
                          collapsedWidth: "Small / Collapsed",
                          height: "Height",
                        };
                        return (
                          <NumberField
                            key={fieldKey}
                            label={t(labelMap[fieldKey] || formatLabel(fieldKey))}
                            value={fieldVal}
                            unit="px"
                            onChange={(v) => set(`${sectionKey}.${subKey}.${fieldKey}`, v)}
                          />
                        );
                      })}
                    </FieldsRow>
                  </Card>
                );
              }
              return null;
            })}
          </div>
        );
      })}

      {/* ── 5. Gradients Section ────────────────────────────────────── */}
      <SectionHeader number={5} title={t("Gradients")} />
      {gradientKeys.map((sectionKey) => {
        const gradientVal = config.theme[sectionKey];
        if (typeof gradientVal !== "object" || gradientVal === null) return null;

        return Object.entries(gradientVal).map(([subKey, subVal]) => {
          if (typeof subVal === "object" && subVal !== null) {
            return (
              <Card key={subKey} className="theme-card-margin">
                <CardTitle icon={getCardIcon("gradients")} title={t(formatLabel(subKey))} description={t("Gradients " + subKey)} />
                <div className="section-row-grid">
                  {Object.entries(subVal).map(([fieldKey, fieldVal]) => {
                    const labelMap = {
                      primary: `${formatLabel(subKey)} Primary`,
                      languages: "Languages",
                    };
                    return (
                      <GradientField
                        key={fieldKey}
                        label={t(labelMap[fieldKey] || formatLabel(fieldKey))}
                        value={fieldVal}
                        onChange={(v) => set(`${sectionKey}.${subKey}.${fieldKey}`, v)}
                      />
                    );
                  })}
                </div>
              </Card>
            );
          }
          return null;
        });
      })}

      {/* ── 6. Typography Section ───────────────────────────────────── */}
      <SectionHeader number={6} title={t("Typography")} />
      {typographyKeys.map((sectionKey) => {
        const typographyVal = config.theme[sectionKey];
        if (typeof typographyVal !== "object" || typographyVal === null) return null;

        return (
          <Card key={sectionKey} className="theme-card-margin">
            <CardTitle icon={getCardIcon("typography")} title={t("Button")} description={t("Decide its button")} />
            <div className="full-width-col">
              {Object.entries(typographyVal).map(([key, val]) => {
                if (key === "fontFamily") {
                  return (
                    <div key={key} className="font-family-group">
                      <span className="font-family-label">{t("Font Family")}</span>
                      <div className="font-family-row">
                        <select
                          value={val.split(",")[0].replace(/"/g, "").trim()}
                          onChange={(e) => set(`${sectionKey}.fontFamily`, `"${e.target.value}", system-ui, -apple-system, BlinkMacSystemFont, sans-serif`)}
                          className="font-family-select"
                        >
                          {["Inter", "Roboto", "Open Sans", "Lato", "Poppins", "Nunito", "Montserrat"].map((f) => (
                            <option key={f} value={f}>{f}</option>
                          ))}
                        </select>
                        <span className="font-family-preview">
                          {val.replace(/"/g, "")}
                        </span>
                      </div>
                    </div>
                  );
                } else {
                  return (
                    <TextField
                      key={key}
                      label={t(formatLabel(key))}
                      value={val}
                      onChange={(v) => set(`${sectionKey}.${key}`, v)}
                    />
                  );
                }
              })}
            </div>
          </Card>
        );
      })}

      {/* ── Additional Settings Section ──────────────────────────────── */}
      {otherThemeKeys.length > 0 && <SectionHeader number="+" title={t("Additional Settings")} />}
      {otherThemeKeys.map((sectionKey) => {
        const val = config.theme[sectionKey];
        if (typeof val !== "object" || val === null) return null;

        return (
          <Card key={sectionKey} className="theme-card-margin">
            <CardTitle title={t(formatLabel(sectionKey))} description={t("Theme settings for " + formatLabel(sectionKey))} />
            <FieldsRow>
              {Object.entries(val).map(([key, fieldVal]) => {
                if (typeof fieldVal === "number") {
                  return (
                    <NumberField
                      key={key}
                      label={t(formatLabel(key))}
                      value={fieldVal}
                      onChange={(v) => set(`${sectionKey}.${key}`, v)}
                    />
                  );
                }
                return (
                  <TextField
                    key={key}
                    label={t(formatLabel(key))}
                    value={fieldVal}
                    onChange={(v) => set(`${sectionKey}.${key}`, v)}
                  />
                );
              })}
            </FieldsRow>
          </Card>
        );
      })}

      {/* ── 7. Common Assets Section ────────────────────────────────── */}
      <SectionHeader number={7} title={t("Common")} />
      <div className="full-width-col theme-card-margin">
        {configKeys.map((rootKey) => {
          const rootVal = config[rootKey];
          if (typeof rootVal !== "object" || rootVal === null) return null;

          return Object.entries(rootVal).map(([subKey, subVal]) => {
            if (typeof subVal !== "object" || subVal === null) return null;

            return Object.entries(subVal).map(([assetGroupKey, assetGroupVal]) => {
              if (typeof assetGroupVal !== "object" || assetGroupVal === null) return null;

              return (
                <Card key={`${rootKey}-${subKey}-${assetGroupKey}`} className="theme-flex-card">
                  <CardTitle
                    icon={getCardIcon("logo")}
                    title={t(`${formatLabel(subKey)} ${formatLabel(assetGroupKey)}`)}
                    description={t(`${formatLabel(subKey)} ${formatLabel(assetGroupKey)} for all screens`)}
                  />
                  <div className="upload-grid-row" style={{ gridTemplateColumns: `repeat(${Object.keys(assetGroupVal).length}, 1fr)` }}>
                    {Object.entries(assetGroupVal).map(([logoKey, logoVal]) => (
                      <UploadBox
                        key={logoKey}
                        label={t(formatLabel(logoKey))}
                        value={logoVal}
                        onChange={(v) => set(`${rootKey}.${subKey}.${assetGroupKey}.${logoKey}`, v)}
                      />
                    ))}
                  </div>
                </Card>
              );
            });
          });
        })}
      </div>

    </ThemeEditorLayout>
  );
}

export default ThemeCustomizeForm;
