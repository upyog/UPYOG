import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { Toast } from "@upyog/workbench-ui-react-components";
import { ThemeConfig } from "../../configs/ThemeConfig";
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
  UploadBox
} from "../../components/ThemeCustomizeComponents";

// ─── Utility ─────────────────────────────────────────────────────────────────
function deepSet(obj, path, value) {
  const next = JSON.parse(JSON.stringify(obj));
  const keys = path.split(".");
  let ref = next;
  for (let i = 0; i < keys.length - 1; i++) ref = ref[keys[i]];
  ref[keys[keys.length - 1]] = value;
  return next;
}

function formatLabel(key) {
  return key
    .replace(/([A-Z])/g, " $1")
    .replace(/[_-]/g, " ")
    .replace(/^\w/, (c) => c.toUpperCase())
    .replace(/\s+/g, " ")
    .trim();
}

const labelOverrides = {
  "brand.primary": "Default Color",
  "divider.primary": "Input Color",
};

function getLabel(groupKey, colorKey) {
  const path = `${groupKey}.${colorKey}`;
  if (labelOverrides[path]) {
    return labelOverrides[path];
  }
  return `${formatLabel(colorKey)} Color`;
}

const groupMeta = {
  text: { icon: "≡", title: "Text Colors", description: "Color used for text and content" },
  brand: { icon: "✦", title: "Brand Colors", description: "Brand identity and primary colors" },
  common: { icon: "◑", title: "Common Color", description: "Common used for text and content" },
  background: { icon: "⊡", title: "Background Color", description: "Backgrounds for layout and components" },
  border: { icon: "▭", title: "Border Colors", description: "Borders and outlines" },
  divider: { icon: "—", title: "Divider", description: "Borders and outlines" },
};

// ─── Main ─────────────────────────────────────────────────────────────────────
function ThemeCustomizeForm() {
  const { t } = useTranslation();
  
  const getInitialConfig = () => {
    const saved = localStorage.getItem("UPYOG_THEME_CONFIG");
    if (saved) {
      try {
        return JSON.parse(saved);
      } catch (e) {
        console.error("Failed to parse saved theme config:", e);
      }
    }
    return ThemeConfig[0] || {};
  };

  const [config, setConfig] = useState(getInitialConfig());
  const [toast, setToast] = useState(null);

  const set = (path, value) => {
    const firstKey = path.split(".")[0];
    const finalPath = ["theme", "common", "pages"].includes(firstKey) ? path : `theme.${path}`;
    setConfig((prev) => {
      const next = deepSet(prev, finalPath, value);
      // Automatically save drafts to localStorage so the UI stays updated during layout edit
      localStorage.setItem("UPYOG_THEME_CONFIG", JSON.stringify(next));
      return next;
    });
  };

  const themeKeys = Object.keys(config.theme || {});
  const colorGroupKeys = themeKeys.filter(k => k.toLowerCase().includes("color"));
  const shadowKeys = themeKeys.filter(k => k.toLowerCase().includes("shadow"));
  const radiusKeys = themeKeys.filter(k => k.toLowerCase().includes("radius"));
  const layoutKeys = themeKeys.filter(k => k.toLowerCase().includes("layout"));
  const gradientKeys = themeKeys.filter(k => k.toLowerCase().includes("gradient"));
  const typographyKeys = themeKeys.filter(k => k.toLowerCase().includes("typography") || k.toLowerCase().includes("font"));

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

  const handleSubmit = async () => {
    console.log("Config Submitted:", JSON.stringify(config, null, 2));
    localStorage.setItem("UPYOG_THEME_CONFIG", JSON.stringify(config));

    try {
      const response = await fetch("https://jsonplaceholder.typicode.com/posts", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          title: "Theme Config Update",
          body: config,
          userId: 1,
        }),
      });

      if (response.ok) {
        const data = await response.json();
        console.log("Dummy API Response:", data);
        setToast({ label: "Configuration updated and submitted successfully!", error: false });
      } else {
        throw new Error("API responded with error status");
      }
    } catch (err) {
      console.error("API submission failed:", err);
      setToast({ label: "Failed to submit configuration to the API.", error: true });
    }
    setTimeout(() => setToast(null), 3000);
  };
  return (
    <div style={{ padding: "24px 32px", fontFamily: "Inter, system-ui, sans-serif" }}>

      {/* Page Title */}
      <div style={{ fontSize: 22, fontWeight: 700, color: "#21182C", marginBottom: 4 }}>
        {t("Customize Theme")}
      </div>

      {/* ── 1. Colors Theme ─────────────────────────────────────── */}
      <SectionHeader number={1} title="Colors Theme" />

      {colorGroupKeys.map((sectionKey) => {
        const colorsVal = config.theme[sectionKey];
        if (typeof colorsVal !== "object" || colorsVal === null) return null;

        return (
          <div key={sectionKey}>
            {/* Text Colors */}
            {colorsVal.text && (
              <Card style={{ marginBottom: 16 }}>
                <CardTitle icon="≡" title="Text Colors" description="Color used for text and content" />
                <FieldsRow>
                  {Object.entries(colorsVal.text).map(([colorKey, colorVal]) => (
                    <ColorField
                      key={colorKey}
                      label={getLabel("text", colorKey)}
                      value={colorVal}
                      onChange={(v) => set(`${sectionKey}.text.${colorKey}`, v)}
                    />
                  ))}
                </FieldsRow>
              </Card>
            )}

            {/* Brand + Common */}
            <div className="section-row-grid" style={{ marginBottom: 16 }}>
              {colorsVal.brand && (
                <Card>
                  <CardTitle icon="✦" title="Brand Colors" description="Brand identity and primary colors" />
                  <FieldsRow>
                    {Object.entries(colorsVal.brand).map(([colorKey, colorVal]) => (
                      <ColorField
                        key={colorKey}
                        label={getLabel("brand", colorKey)}
                        value={colorVal}
                        onChange={(v) => set(`${sectionKey}.brand.${colorKey}`, v)}
                      />
                    ))}
                  </FieldsRow>
                </Card>
              )}
              {colorsVal.common && (
                <Card>
                  <CardTitle icon="◑" title="Common Color" description="Common used for text and content" />
                  <FieldsRow>
                    {Object.entries(colorsVal.common).map(([colorKey, colorVal]) => (
                      <ColorField
                        key={colorKey}
                        label={getLabel("common", colorKey)}
                        value={colorVal}
                        onChange={(v) => set(`${sectionKey}.common.${colorKey}`, v)}
                      />
                    ))}
                  </FieldsRow>
                </Card>
              )}
            </div>

            {/* Background */}
            {colorsVal.background && (
              <Card style={{ marginBottom: 16 }}>
                <CardTitle icon="⊡" title="Background Color" description="Backgrounds for layout and components" />
                <FieldsRow>
                  {Object.entries(colorsVal.background).map(([colorKey, colorVal]) => (
                    <ColorField
                      key={colorKey}
                      label={getLabel("background", colorKey)}
                      value={colorVal}
                      onChange={(v) => set(`${sectionKey}.background.${colorKey}`, v)}
                    />
                  ))}
                </FieldsRow>
              </Card>
            )}

            {/* Border + Divider */}
            <div className="section-row-grid" style={{ marginBottom: 16 }}>
              {colorsVal.border && (
                <Card>
                  <CardTitle icon="▭" title="Border Colors" description="Borders and outlines" />
                  <FieldsRow>
                    {Object.entries(colorsVal.border).map(([colorKey, colorVal]) => (
                      <ColorField
                        key={colorKey}
                        label={getLabel("border", colorKey)}
                        value={colorVal}
                        onChange={(v) => set(`${sectionKey}.border.${colorKey}`, v)}
                      />
                    ))}
                  </FieldsRow>
                </Card>
              )}
              {colorsVal.divider && (
                <Card>
                  <CardTitle icon="—" title="Divider" description="Borders and outlines" />
                  <FieldsRow>
                    {Object.entries(colorsVal.divider).map(([colorKey, colorVal]) => (
                      <ColorField
                        key={colorKey}
                        label={getLabel("divider", colorKey)}
                        value={colorVal}
                        onChange={(v) => set(`${sectionKey}.divider.${colorKey}`, v)}
                      />
                    ))}
                  </FieldsRow>
                </Card>
              )}
            </div>

            {/* Dynamic/Additional Color Groups */}
            <div className="section-row-grid" style={{ marginBottom: 16 }}>
              {Object.entries(colorsVal || {}).map(([groupKey, groupVal]) => {
                const knownGroups = ["text", "brand", "common", "background", "border", "divider"];
                if (!knownGroups.includes(groupKey) && typeof groupVal === "object" && groupVal !== null) {
                  const meta = groupMeta[groupKey] || {
                    icon: "🎨",
                    title: `${formatLabel(groupKey)} Colors`,
                    description: `Custom colors for ${formatLabel(groupKey)}`,
                  };

                  return (
                    <Card key={groupKey} style={{ display: "flex", flexDirection: "column", height: "100%" }}>
                      <CardTitle icon={meta.icon} title={meta.title} description={meta.description} />
                      <FieldsRow style={{ marginTop: "auto" }}>
                        {Object.entries(groupVal).map(([colorKey, colorVal]) => (
                          <ColorField
                            key={colorKey}
                            label={`${formatLabel(colorKey)} Color`}
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

      {/* ── 2. Shadows + 3. Border Radius ── same row ─────────── */}
      <div className="section-row-grid">
        <div>
          <SectionHeader number={2} title="Shadows" />
          {shadowKeys.map((sectionKey) => {
            const shadowsVal = config.theme[sectionKey];
            if (typeof shadowsVal !== "object" || shadowsVal === null) return null;

            return (
              <Card key={sectionKey} style={{ marginBottom: 16 }}>
                <CardTitle icon="◑" title={formatLabel(sectionKey)} description="Default and outlines" />
                <div style={{ display: "flex", flexDirection: "column", gap: 16, width: "100%" }}>
                  {Object.entries(shadowsVal).map(([key, val]) => (
                    <ShadowField
                      key={key}
                      label={key === "default" ? "Default Shadow" : formatLabel(key)}
                      value={val}
                      onChange={(v) => set(`${sectionKey}.${key}`, v)}
                    />
                  ))}
                </div>
              </Card>
            );
          })}
        </div>
        <div>
          <SectionHeader number={3} title="Border Radius" />
          {radiusKeys.map((sectionKey) => {
            const radiusVal = config.theme[sectionKey];
            if (typeof radiusVal !== "object" || radiusVal === null) return null;

            return (
              <Card key={sectionKey} style={{ marginBottom: 16 }}>
                <CardTitle icon="▢" title={formatLabel(sectionKey)} description="Default and outlines" />
                <FieldsRow grid={false}>
                  {Object.entries(radiusVal).map(([key, val]) => {
                    const labelMap = { sm: "Small", md: "Medium" };
                    return (
                      <NumberField
                        key={key}
                        label={labelMap[key] || formatLabel(key)}
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

      {/* ── 4. Layout ───────────────────────────────────────────── */}
      <SectionHeader number={4} title="Layout" />
      {layoutKeys.map((sectionKey) => {
        const layoutVal = config.theme[sectionKey];
        if (typeof layoutVal !== "object" || layoutVal === null) return null;

        return (
          <div key={sectionKey} className="section-row-grid" style={{ marginBottom: 16 }}>
            {Object.entries(layoutVal).map(([subKey, subVal]) => {
              if (typeof subVal === "object" && subVal !== null) {
                const layoutMeta = {
                  sidebar: { icon: "⊟", title: "Sidebar", description: "Layout and sidebar" },
                  header: { icon: "▬", title: "Header", description: "Layout and Header" },
                };
                const meta = layoutMeta[subKey] || {
                  icon: "📋",
                  title: formatLabel(subKey),
                  description: `Layout settings for ${formatLabel(subKey)}`,
                };
                return (
                  <Card key={subKey}>
                    <CardTitle icon={meta.icon} title={meta.title} description={meta.description} />
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
                            label={labelMap[fieldKey] || formatLabel(fieldKey)}
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

      {/* ── 5. Gradients ────────────────────────────────────────── */}
      <SectionHeader number={5} title="Gradients" />
      {gradientKeys.map((sectionKey) => {
        const gradientVal = config.theme[sectionKey];
        if (typeof gradientVal !== "object" || gradientVal === null) return null;

        return Object.entries(gradientVal).map(([subKey, subVal]) => {
          if (typeof subVal === "object" && subVal !== null) {
            return (
              <Card key={subKey} style={{ marginBottom: 16 }}>
                <CardTitle icon="🎨" title={formatLabel(subKey)} description={`Gradients ${subKey}`} />
                <div className="section-row-grid" style={{ gap: 24 }}>
                  {Object.entries(subVal).map(([fieldKey, fieldVal]) => {
                    const labelMap = {
                      primary: `${formatLabel(subKey)} Primary`,
                      languages: "Languages",
                    };
                    return (
                      <GradientField
                        key={fieldKey}
                        label={labelMap[fieldKey] || formatLabel(fieldKey)}
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

      {/* ── 6. Typography ───────────────────────────────────────── */}
      <SectionHeader number={6} title="Typography" />
      {typographyKeys.map((sectionKey) => {
        const typographyVal = config.theme[sectionKey];
        if (typeof typographyVal !== "object" || typographyVal === null) return null;

        return (
          <Card key={sectionKey} style={{ marginBottom: 16 }}>
            <CardTitle icon="T" title="Button" description="Decide its button" />
            <div style={{ display: "flex", flexDirection: "column", gap: 16, maxWidth: 280 }}>
              {Object.entries(typographyVal).map(([key, val]) => {
                if (key === "fontFamily") {
                  return (
                    <div key={key} style={{ display: "flex", flexDirection: "column", gap: 6 }}>
                      <span style={{ fontSize: 12, color: "#584F74" }}>Font Family</span>
                      <select
                        value={val.split(",")[0].replace(/"/g, "").trim()}
                        onChange={(e) => set(`${sectionKey}.fontFamily`, `"${e.target.value}", system-ui, -apple-system, BlinkMacSystemFont, sans-serif`)}
                        style={{
                          border: "1.5px solid #E8E1F0", borderRadius: 8, padding: "10px 14px",
                          fontSize: 13, color: "#21182C", outline: "none", background: "#fff", height: 48,
                        }}
                      >
                        {["Inter", "Roboto", "Open Sans", "Lato", "Poppins", "Nunito", "Montserrat"].map((f) => (
                          <option key={f} value={f}>{f}</option>
                        ))}
                      </select>
                      <span style={{ fontSize: 11, color: "#A69CC6", marginTop: 2, wordBreak: "break-all" }}>
                        {val}
                      </span>
                    </div>
                  );
                } else {
                  return (
                    <TextField
                      key={key}
                      label={formatLabel(key)}
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

      {/* ── Other Settings ─────────────────────────────────────── */}
      {otherThemeKeys.length > 0 && <SectionHeader number="+" title="Other Settings" />}
      {otherThemeKeys.map((sectionKey) => {
        const val = config.theme[sectionKey];
        if (typeof val !== "object" || val === null) return null;

        return (
          <Card key={sectionKey} style={{ marginBottom: 16 }}>
            <CardTitle icon="⚙️" title={formatLabel(sectionKey)} description={`Theme settings for ${formatLabel(sectionKey)}`} />
            <FieldsRow>
              {Object.entries(val).map(([key, fieldVal]) => {
                if (typeof fieldVal === "number") {
                  return (
                    <NumberField
                      key={key}
                      label={formatLabel(key)}
                      value={fieldVal}
                      onChange={(v) => set(`${sectionKey}.${key}`, v)}
                    />
                  );
                }
                return (
                  <TextField
                    key={key}
                    label={formatLabel(key)}
                    value={fieldVal}
                    onChange={(v) => set(`${sectionKey}.${key}`, v)}
                  />
                );
              })}
            </FieldsRow>
          </Card>
        );
      })}

      {/* ── 7. Common ───────────────────────────────────────────── */}
      <SectionHeader number={7} title="Common" />
      <div style={{ display: "flex", flexDirection: "column", gap: 16, marginBottom: 16 }}>
        {configKeys.map((rootKey) => {
          const rootVal = config[rootKey];
          if (typeof rootVal !== "object" || rootVal === null) return null;

          return Object.entries(rootVal).map(([subKey, subVal]) => {
            if (typeof subVal !== "object" || subVal === null) return null;

            return Object.entries(subVal).map(([assetGroupKey, assetGroupVal]) => {
              if (typeof assetGroupVal !== "object" || assetGroupVal === null) return null;

              return (
                <Card key={`${rootKey}-${subKey}-${assetGroupKey}`} style={{ height: "100%", display: "flex", flexDirection: "column" }}>
                  <CardTitle
                    icon="🏷"
                    title={`${formatLabel(subKey)} ${formatLabel(assetGroupKey)}`}
                    description={`${formatLabel(subKey)} ${formatLabel(assetGroupKey)} for all screens`}
                  />
                  <div className="upload-grid-row" style={{ display: "grid", gridTemplateColumns: `repeat(${Object.keys(assetGroupVal).length}, 1fr)`, gap: 16, marginTop: "auto" }}>
                    {Object.entries(assetGroupVal).map(([logoKey, logoVal]) => (
                      <UploadBox
                        key={logoKey}
                        label={formatLabel(logoKey)}
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

      {/* ── Submit ──────────────────────────────────────────────── */}
      <div style={{ display: "flex", justifyContent: "flex-end", marginTop: 28, paddingBottom: 40 }}>
        <button
          onClick={handleSubmit}
          className="submit-btn"
        >
          SUBMIT CHANGES
        </button>
      </div>

      {toast && <Toast label={toast.label} error={toast.error} onClose={() => setToast(null)} />}
    </div>
  );
}

export default ThemeCustomizeForm;
