import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { Toast } from "@upyog/workbench-ui-react-components";
import { ThemeConfig } from "../../configs/ThemeConfig";
import {
  Card,
  CardTitle,
  FieldsRow,
  TextField,
  CheckboxField,
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

function OnBoardingLogin() {
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

  const loginStep = config.pages?.onboarding?.steps?.login || {};
  const fields = loginStep.fields || [];
  const footer = loginStep.footer || {};

  const set = (path, value) => {
    setConfig((prev) => {
      const next = deepSet(prev, path, value);
      localStorage.setItem("UPYOG_THEME_CONFIG", JSON.stringify(next));
      return next;
    });
  };

  const handleAddSecondaryAction = () => {
    const currentActions = footer.secondaryActions ? [...footer.secondaryActions] : [];
    const newActions = [...currentActions, { type: "custom", icon: "", label: "" }];
    set("pages.onboarding.steps.login.footer.secondaryActions", newActions);
  };

  const handleRemoveSecondaryAction = (index) => {
    const currentActions = footer.secondaryActions ? [...footer.secondaryActions] : [];
    const newActions = currentActions.filter((_, idx) => idx !== index);
    set("pages.onboarding.steps.login.footer.secondaryActions", newActions);
  };

  const handleSubmit = async () => {
    console.log("Config Submitted from OnBoardingLogin:", JSON.stringify(config, null, 2));
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
        {t("Onboarding - Login Configuration")}
      </div>

      {/* ── 1. Login Page Header ────────────────────────────────────── */}
      <Card style={{ marginBottom: 20 }}>
        <CardTitle
          icon="📝"
          title="Screen Header Settings"
          description="Manage main heading, subtitle, and text content for the login screen"
        />
        <div style={{ display: "flex", flexDirection: "column", gap: 16 }}>
          <TextField
            label="Heading"
            value={loginStep.heading || ""}
            onChange={(v) => set("pages.onboarding.steps.login.heading", v)}
          />
          <TextField
            label="Description"
            type="textarea"
            value={loginStep.description || ""}
            onChange={(v) => set("pages.onboarding.steps.login.description", v)}
          />
        </div>
      </Card>

      {/* ── 2. Form Fields Configuration ────────────────────────────── */}
      <Card style={{ marginBottom: 20 }}>
        <CardTitle
          icon="🗂️"
          title="Login Form Fields"
          description="Configure input fields, labels, placeholders, and validation rules for language, city, and mobile inputs"
        />

        {/* Language Selector */}
        {fields[0] && (
          <div style={{ borderBottom: "1.5px solid #EDE8F5", paddingBottom: 20, marginBottom: 20 }}>
            <div style={{ fontSize: 13, fontWeight: 800, color: "#3D2364", marginBottom: 12 }}>
              Language Selector
            </div>
            <FieldsRow>
              <TextField
                label="LABEL"
                value={fields[0].label || ""}
                onChange={(v) => set("pages.onboarding.steps.login.fields.0.label", v)}
              />
              <TextField
                label="DEFAULT VALUE"
                value={fields[0].defaultValue || ""}
                onChange={(v) => set("pages.onboarding.steps.login.fields.0.defaultValue", v)}
              />
              <TextField
                label="REQUIRED ERROR MESSAGE"
                value={fields[0].validation?.messages?.required || ""}
                onChange={(v) => set("pages.onboarding.steps.login.fields.0.validation.messages.required", v)}
              />
            </FieldsRow>
          </div>
        )}

        {/* City Selector */}
        {fields[1] && (
          <div style={{ borderBottom: "1.5px solid #EDE8F5", paddingBottom: 20, marginBottom: 20 }}>
            <div style={{ fontSize: 13, fontWeight: 800, color: "#3D2364", marginBottom: 12 }}>
              City Selector
            </div>
            <div style={{ display: "flex", flexDirection: "column", gap: 16 }}>
              <FieldsRow>
                <TextField
                  label="LABEL"
                  value={fields[1].label || ""}
                  onChange={(v) => set("pages.onboarding.steps.login.fields.1.label", v)}
                />
                <TextField
                  label="PLACEHOLDER"
                  value={fields[1].placeholder || ""}
                  onChange={(v) => set("pages.onboarding.steps.login.fields.1.placeholder", v)}
                />
              </FieldsRow>
              <FieldsRow>
                <TextField
                  label="START ICON URL"
                  value={fields[1].startIcon || ""}
                  onChange={(v) => set("pages.onboarding.steps.login.fields.1.startIcon", v)}
                />
                <TextField
                  label="REQUIRED ERROR MESSAGE"
                  value={fields[1].validation?.messages?.required || ""}
                  onChange={(v) => set("pages.onboarding.steps.login.fields.1.validation.messages.required", v)}
                />
              </FieldsRow>
            </div>
          </div>
        )}

        {/* Mobile Number */}
        {fields[2] && (
          <div>
            <div style={{ fontSize: 13, fontWeight: 800, color: "#3D2364", marginBottom: 12 }}>
              Mobile Number
            </div>
            <div style={{ display: "flex", flexDirection: "column", gap: 16 }}>
              <FieldsRow>
                <TextField
                  label="LABEL"
                  value={fields[2].label || ""}
                  onChange={(v) => set("pages.onboarding.steps.login.fields.2.label", v)}
                />
                <TextField
                  label="PLACEHOLDER"
                  value={fields[2].placeholder || ""}
                  onChange={(v) => set("pages.onboarding.steps.login.fields.2.placeholder", v)}
                />
                <TextField
                  label="PREFIX"
                  value={fields[2].prefix || ""}
                  onChange={(v) => set("pages.onboarding.steps.login.fields.2.prefix", v)}
                />
              </FieldsRow>
              <FieldsRow>
                <TextField
                  label="HELPER TEXT"
                  value={fields[2].helperText || ""}
                  onChange={(v) => set("pages.onboarding.steps.login.fields.2.helperText", v)}
                />
                <TextField
                  label="HELPER ICON URL"
                  value={fields[2].helperIcon || ""}
                  onChange={(v) => set("pages.onboarding.steps.login.fields.2.helperIcon", v)}
                />
              </FieldsRow>
              
              <div style={{ fontSize: 12, fontWeight: 700, color: "#584F74", marginTop: 4 }}>
                Validation Rules
              </div>
              <FieldsRow>
                <TextField
                  label="PATTERN (REGEX)"
                  value={fields[2].validation?.pattern || ""}
                  onChange={(v) => set("pages.onboarding.steps.login.fields.2.validation.pattern", v)}
                />
                <TextField
                  label="PATTERN ERROR MESSAGE"
                  value={fields[2].validation?.messages?.pattern || ""}
                  onChange={(v) => set("pages.onboarding.steps.login.fields.2.validation.messages.pattern", v)}
                />
              </FieldsRow>
            </div>
          </div>
        )}
      </Card>

      {/* ── 3. Footer Settings ──────────────────────────────────────── */}
      <Card style={{ marginBottom: 20 }}>
        <CardTitle
          icon="🏁"
          title="Footer Configuration"
          description="Configure action buttons, separator layout, and trust verification badges"
          rightElement={
            <button
              onClick={handleAddSecondaryAction}
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
              <span style={{ fontSize: 16 }}>⊕</span> Add Action
            </button>
          }
        />
        <div style={{ display: "flex", flexDirection: "column", gap: 16, marginBottom: 16 }}>
          <TextField
            label="Primary Action Label"
            value={footer.primaryAction?.label || ""}
            onChange={(v) => set("pages.onboarding.steps.login.footer.primaryAction.label", v)}
          />

          {footer.secondaryActions && footer.secondaryActions.length > 0 && (
            <div style={{ display: "flex", flexDirection: "column", gap: 12 }}>
              <span style={{ fontSize: 12, color: "#584F74" }}>Secondary Actions</span>
              {footer.secondaryActions.map((action, idx) => (
                <div key={idx} style={{ display: "flex", alignItems: "center", gap: 16 }}>
                  <div className="secondary-action-grid">
                    <input
                      type="text"
                      value={action.icon || ""}
                      onChange={(e) => set(`pages.onboarding.steps.login.footer.secondaryActions.${idx}.icon`, e.target.value)}
                      placeholder="Icon URL (https://...)"
                      className="text-input-field"
                      style={{ height: 36, fontSize: 12 }}
                    />
                    <input
                      type="text"
                      value={action.label || ""}
                      onChange={(e) => set(`pages.onboarding.steps.login.footer.secondaryActions.${idx}.label`, e.target.value)}
                      placeholder="Action Label"
                      className="text-input-field"
                      style={{ height: 36, fontSize: 12 }}
                    />
                  </div>
                  <button
                    onClick={() => handleRemoveSecondaryAction(idx)}
                    title="Remove Action"
                    style={{
                      background: "none",
                      border: "none",
                      cursor: "pointer",
                      color: "#D85A5A",
                      fontSize: 16,
                      padding: 4
                    }}
                  >
                    🗑️
                  </button>
                </div>
              ))}
            </div>
          )}
        </div>

        <div className="two-col-grid">
          <CheckboxField
            label="Show Separator"
            checked={!!footer.showSeparator}
            onChange={(v) => set("pages.onboarding.steps.login.footer.showSeparator", v)}
          />
          <CheckboxField
            label="Show Secure Info"
            checked={!!footer.showSecureInfo}
            onChange={(v) => set("pages.onboarding.steps.login.footer.showSecureInfo", v)}
          />
        </div>
      </Card>

      {/* ── Submit Button ─────────────────────────────────────────── */}
      <div style={{ display: "flex", justifyContent: "flex-end", marginTop: 28, paddingBottom: 40 }}>
        <SubmitButton onClick={handleSubmit} label="SUBMIT CHANGES" />
      </div>

      {toast && <Toast label={toast.label} error={toast.error} onClose={() => setToast(null)} />}
    </div>
  );
}

export default OnBoardingLogin;
