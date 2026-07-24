import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { Toast } from "@upyog/workbench-ui-react-components";
import { ThemeConfig } from "../../configs/ThemeConfig";
import {
  Card,
  CardTitle,
  FieldsRow,
  ColorField,
  TextField,
  CheckboxField,
  SelectField,
  SubmitButton
} from "../../components/ThemeCustomizeComponents";

// ─── Utility ─────────────────────────────────────────────────────────────────
function deepSet(obj, path, value) {
  const next = JSON.parse(JSON.stringify(obj));
  const keys = path.split(".");
  let ref = next;
  for (let i = 0; i < keys.length - 1; i++) {
    ref = ref[keys[i]];
  }
  ref[keys[keys.length - 1]] = value;
  return next;
}

function OnBoardingContent() {
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

  const onboarding = config.pages?.onboarding || {};
  const content = onboarding.content || {};
  const common = onboarding.common || {};

  const set = (path, value) => {
    setConfig((prev) => {
      const next = deepSet(prev, path, value);
      localStorage.setItem("UPYOG_THEME_CONFIG", JSON.stringify(next));
      return next;
    });
  };

  const handleAddFeature = () => {
    const currentFeatures = content.features ? [...content.features] : [];
    const newFeatures = [...currentFeatures, { icon: "", title: "", description: "" }];
    set("pages.onboarding.content.features", newFeatures);
  };

  const handleRemoveFeature = (index) => {
    const currentFeatures = content.features ? [...content.features] : [];
    const newFeatures = currentFeatures.filter((_, idx) => idx !== index);
    set("pages.onboarding.content.features", newFeatures);
  };

  const handleSubmit = async () => {
    console.log("Config Submitted from OnBoardingContent:", JSON.stringify(config, null, 2));
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
      <div style={{ fontSize: 22, fontWeight: 700, color: "#21182C", marginBottom: 24 }}>
        {t("Onboarding - Common Content")}
      </div>

      {/* ── 1. Background Configuration ──────────────────────────────── */}
      {content.background?.root && (
        <Card style={{ marginBottom: 20 }}>
          <CardTitle
            icon="🖼️"
            title="Background Configuration"
            description="Root and responsive image settings"
          />
          <div className="two-col-grid" style={{ marginBottom: 16 }}>
            <ColorField
              label="Root Background Color"
              value={content.background.root.backgroundColor}
              onChange={(v) => set("pages.onboarding.content.background.root.backgroundColor", v)}
            />
            <CheckboxField
              label="Show Color"
              checked={content.background.root.showColor}
              onChange={(v) => set("pages.onboarding.content.background.root.showColor", v)}
            />
          </div>
          
          <div style={{ fontSize: 12, fontWeight: 700, color: "#21182C", margin: "16px 0 8px" }}>
            Responsive Images
          </div>
          <FieldsRow>
            <TextField
              label="MOBILE IMAGE URL"
              value={content.background.root.responsive?.mobile?.image || ""}
              onChange={(v) => set("pages.onboarding.content.background.root.responsive.mobile.image", v)}
              placeholder="https://..."
            />
            <TextField
              label="TABLET IMAGE URL"
              value={content.background.root.responsive?.tablet?.image || ""}
              onChange={(v) => set("pages.onboarding.content.background.root.responsive.tablet.image", v)}
              placeholder="https://..."
            />
            <TextField
              label="LAPTOP IMAGE URL"
              value={content.background.root.responsive?.laptop?.image || ""}
              onChange={(v) => set("pages.onboarding.content.background.root.responsive.laptop.image", v)}
              placeholder="https://..."
            />
            <TextField
              label="DESKTOP IMAGE URL"
              value={content.background.root.responsive?.desktop?.image || ""}
              onChange={(v) => set("pages.onboarding.content.background.root.responsive.desktop.image", v)}
              placeholder="https://..."
            />
          </FieldsRow>
        </Card>
      )}

      {/* ── 2. Card Configuration ─────────────────────────────────── */}
      {content.background?.sections?.left?.card && (
        <Card style={{ marginBottom: 20 }}>
          <CardTitle
            icon="🎴"
            title="Card Configuration"
            description="Card styling and responsive image settings"
          />
          <div className="two-col-grid" style={{ marginBottom: 16 }}>
            <ColorField
              label="Background Color"
              value={content.background.sections.left.card.backgroundColor}
              onChange={(v) => set("pages.onboarding.content.background.sections.left.card.backgroundColor", v)}
            />
            <CheckboxField
              label="Show Color"
              checked={content.background.sections.left.card.showColor}
              onChange={(v) => set("pages.onboarding.content.background.sections.left.card.showColor", v)}
            />
          </div>
          <div className="two-col-grid" style={{ marginBottom: 16 }}>
            <SelectField
              label="Shadow Style"
              value={content.background.sections.left.card.shadow}
              options={["default", "none", "sm", "md", "lg"]}
              onChange={(v) => set("pages.onboarding.content.background.sections.left.card.shadow", v)}
            />
            <CheckboxField
              label="Show Shadow"
              checked={content.background.sections.left.card.showShadow}
              onChange={(v) => set("pages.onboarding.content.background.sections.left.card.showShadow", v)}
            />
          </div>

          <div style={{ fontSize: 12, fontWeight: 700, color: "#21182C", margin: "16px 0 8px" }}>
            Responsive Images
          </div>
          <FieldsRow>
            <TextField
              label="MOBILE IMAGE URL"
              value={content.background.sections.left.card.responsive?.mobile?.image || ""}
              onChange={(v) => set("pages.onboarding.content.background.sections.left.card.responsive.mobile.image", v)}
              placeholder="https://..."
            />
            <TextField
              label="TABLET IMAGE URL"
              value={content.background.sections.left.card.responsive?.tablet?.image || ""}
              onChange={(v) => set("pages.onboarding.content.background.sections.left.card.responsive.tablet.image", v)}
              placeholder="https://..."
            />
            <TextField
              label="LAPTOP IMAGE URL"
              value={content.background.sections.left.card.responsive?.laptop?.image || ""}
              onChange={(v) => set("pages.onboarding.content.background.sections.left.card.responsive.laptop.image", v)}
              placeholder="https://..."
            />
            <TextField
              label="DESKTOP IMAGE URL"
              value={content.background.sections.left.card.responsive?.desktop?.image || ""}
              onChange={(v) => set("pages.onboarding.content.background.sections.left.card.responsive.desktop.image", v)}
              placeholder="https://..."
            />
          </FieldsRow>
        </Card>
      )}

      {/* ── 3. Brand Settings ─────────────────────────────────────── */}
      {content.brand && (
        <Card style={{ marginBottom: 20 }}>
          <CardTitle
            icon="🏷️"
            title="Brand Settings"
            description="Titles and subtitles for the onboarding flow"
          />
          <div className="two-col-grid" style={{ marginBottom: 16 }}>
            <TextField
              label="Default Title"
              value={content.brand.title?.default || ""}
              onChange={(v) => set("pages.onboarding.content.brand.title.default", v)}
            />
            <TextField
              label="Highlight Title"
              value={content.brand.title?.highlight || ""}
              onChange={(v) => set("pages.onboarding.content.brand.title.highlight", v)}
            />
          </div>
          <div className="two-col-grid">
            <TextField
              label="Primary Subtitle"
              value={content.brand.subtitle?.primary || ""}
              onChange={(v) => set("pages.onboarding.content.brand.subtitle.primary", v)}
            />
            <TextField
              label="Secondary Subtitle"
              value={content.brand.subtitle?.secondary || ""}
              onChange={(v) => set("pages.onboarding.content.brand.subtitle.secondary", v)}
            />
          </div>
        </Card>
      )}

      {/* ── 4. Features List ──────────────────────────────────────── */}
      {content.features && (
        <Card style={{ marginBottom: 20 }}>
          <CardTitle
            icon="📋"
            title="Features List"
            description="Dynamic list of key features"
            rightElement={
              <button
                onClick={handleAddFeature}
                style={{
                  background: "none",
                  border: "none",
                  color: "#6A4A91",
                  fontWeight: 700,
                  fontSize: 13,
                  cursor: "pointer",
                  display: "flex",
                  alignItems: "center",
                  gap: 4
                }}
              >
                <span style={{ fontSize: 16 }}>⊕</span> Add Feature
              </button>
            }
          />
          <div style={{ display: "flex", flexDirection: "column", gap: 16 }}>
            {content.features.map((feature, idx) => (
              <div
                key={idx}
                style={{
                  display: "flex",
                  alignItems: "flex-end",
                  gap: 16,
                  padding: "16px 20px",
                  background: "#FAF9FC",
                  border: "1.5px solid #EDE8F5",
                  borderRadius: 10
                }}
              >
                <div className="feature-row-grid">
                  <TextField
                    label="ICON URL"
                    value={feature.icon || ""}
                    onChange={(v) => set(`pages.onboarding.content.features.${idx}.icon`, v)}
                    placeholder="https://..."
                  />
                  <TextField
                    label="SHORT TITLE"
                    value={feature.title || ""}
                    onChange={(v) => set(`pages.onboarding.content.features.${idx}.title`, v)}
                    placeholder="Title"
                  />
                  <TextField
                    label="DESCRIPTION"
                    value={feature.description || ""}
                    onChange={(v) => set(`pages.onboarding.content.features.${idx}.description`, v)}
                    placeholder="Description"
                  />
                </div>
                <button
                  onClick={() => handleRemoveFeature(idx)}
                  title="Remove Feature"
                  style={{
                    background: "none",
                    border: "none",
                    cursor: "pointer",
                    color: "#D85A5A",
                    fontSize: 18,
                    padding: "0 0 10px 0"
                  }}
                >
                  🗑️
                </button>
              </div>
            ))}
          </div>
        </Card>
      )}

      {/* ── 5. Common Secure Info ─────────────────────────────────── */}
      {common.secureInfo && (
        <Card style={{ marginBottom: 20 }}>
          <CardTitle
            icon="🛡️"
            title="Common Secure Info"
            description="Security badges and trust text"
          />
          <div className="two-col-grid">
            <TextField
              label="Secure Icon URL"
              value={common.secureInfo.icon || ""}
              onChange={(v) => set("pages.onboarding.common.secureInfo.icon", v)}
            />
            <TextField
              label="Secure Text"
              value={common.secureInfo.text || ""}
              onChange={(v) => set("pages.onboarding.common.secureInfo.text", v)}
            />
          </div>
        </Card>
      )}

      {/* ── Submit Button ─────────────────────────────────────────── */}
      <div style={{ display: "flex", justifyContent: "flex-end", marginTop: 28, paddingBottom: 40 }}>
        <SubmitButton onClick={handleSubmit} label="SUBMIT CHANGES" />
      </div>

      {toast && <Toast label={toast.label} error={toast.error} onClose={() => setToast(null)} />}
    </div>
  );
}

export default OnBoardingContent;
