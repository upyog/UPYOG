import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { useLocation } from "react-router-dom";
import { Modal, ThemePreviewIcon } from "@upyog/workbench-ui-react-components";

// ─── Section Header ───────────────────────────────────────────────────────────
export function SectionHeader({ number, title }) {
  return (
    <div className="theme-section-header">
      <span className="theme-section-header-badge">{number}</span>
      {title.toUpperCase()}
    </div>
  );
}

// ─── Card Components ──────────────────────────────────────────────────────────
export function Card({ children, style }) {
  return (
    <div className="theme-card" style={style}>
      {children}
    </div>
  );
}

export function CardTitle({ icon, title, description, rightElement }) {
  return (
    <div className="theme-card-title-row">
      <div className="theme-card-title-left">
        <div className="theme-card-title-icon-container">
          {icon}
        </div>
        <div className="theme-card-title-text-container">
          <div className="theme-card-title-text">{title}</div>
          {description && <div className="theme-card-title-description">{description}</div>}
        </div>
      </div>
      {rightElement && <div className="theme-card-title-right">{rightElement}</div>}
    </div>
  );
}

// ─── Layout Grid / Flex Wrapper ───────────────────────────────────────────────
export function FieldsRow({ children, style, grid = true }) {
  if (!grid) {
    return (
      <div className="fields-row-flex" style={style}>
        {children}
      </div>
    );
  }
  return (
    <div className="fields-grid" style={style}>
      {children}
    </div>
  );
}

// ─── Form Inputs ──────────────────────────────────────────────────────────────
export function ColorField({ label, value, onChange }) {
  const [copied, setCopied] = React.useState(false);
  const handleCopy = () => {
    navigator.clipboard?.writeText(value).then(() => {
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    });
  };
  return (
    <div className="field-col">
      <span className="field-label-text">{label}</span>
      <div className="input-container">
        <div className="color-picker-preview-box" style={{ background: value }}>
          <input
            type="color"
            value={value}
            onChange={(e) => onChange(e.target.value)}
            className="color-input-element"
          />
        </div>
        <input
          type="text"
          value={value}
          onChange={(e) => onChange(e.target.value)}
          maxLength={7}
          className="color-hex-text-input"
        />
        <button
          onClick={handleCopy}
          title="Copy"
          className={`color-copy-btn ${copied ? "copied" : ""}`}
        >
          {copied ? "✓" : "⧉"}
        </button>
      </div>
    </div>
  );
}

export function NumberField({ label, value, unit, onChange, style }) {
  return (
    <div className="field-col" style={style}>
      {label && <span className="field-label-text">{label}</span>}
      <div className="input-container number-field-width">
        <input
          type="number"
          value={parseInt(value) || 0}
          onChange={(e) => onChange(e.target.value + (unit || ""))}
          className="number-field-input"
        />
        {unit && <span className="number-field-unit">{unit}</span>}
      </div>
    </div>
  );
}

export function TextField({ label, value, onChange, placeholder, type = "text", style }) {
  return (
    <div className="field-col" style={style}>
      {label && <span className="field-label-text">{label}</span>}
      {type === "textarea" ? (
        <textarea
          value={value}
          onChange={(e) => onChange(e.target.value)}
          placeholder={placeholder}
          className="textarea-input-field"
        />
      ) : (
        <input
          type="text"
          value={value}
          onChange={(e) => onChange(e.target.value)}
          placeholder={placeholder}
          className="text-input-field"
        />
      )}
    </div>
  );
}

export function CheckboxField({ label, checked, onChange, style }) {
  return (
    <div className="field-col" style={style}>
      {label && <span className="field-label-text">{label}</span>}
      <label className="checkbox-wrapper">
        <input
          type="checkbox"
          checked={!!checked}
          onChange={(e) => onChange(e.target.checked)}
          className="checkbox-input"
        />
        <span className="checkbox-text">Active / Show</span>
      </label>
    </div>
  );
}

export function SelectField({ label, value, options = [], onChange, style }) {
  return (
    <div className="field-col" style={style}>
      {label && <span className="field-label-text">{label}</span>}
      <select
        value={value}
        onChange={(e) => onChange(e.target.value)}
        className="select-input"
      >
        {options.map((opt) => {
          const val = typeof opt === "object" ? opt.value : opt;
          const labelText = typeof opt === "object" ? opt.label : opt;
          return (
            <option key={val} value={val}>
              {labelText}
            </option>
          );
        })}
      </select>
    </div>
  );
}

export function UploadBox({ label, value = {}, onChange }) {
  const { src = "", alt = "" } = value || {};

  const getDirectImageUrl = (url) => {
    if (!url) return "";
    const fileDMatch = url.match(/drive\.google\.com\/file\/d\/([a-zA-Z0-9_-]+)/i);
    const openIdMatch = url.match(/drive\.google\.com\/open\?id=([a-zA-Z0-9_-]+)/i);
    const lhMatch = url.match(/lh3\.googleusercontent\.com\/d\/([a-zA-Z0-9_-]+)/i);
    const fileId = (fileDMatch && fileDMatch[1]) || (openIdMatch && openIdMatch[1]) || (lhMatch && lhMatch[1]);
    if (fileId) {
      return `https://lh3.googleusercontent.com/d/${fileId}=s0`;
    }
    return url;
  };

  const handleFileChange = (e) => {
    const file = e.target.files?.[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = (event) => {
        onChange?.({ src: event.target.result, alt });
      };
      reader.readAsDataURL(file);
    }
  };

  const handleUrlChange = (e) => {
    const rawVal = e.target.value;
    const resolvedUrl = getDirectImageUrl(rawVal);
    onChange?.({ src: resolvedUrl, alt });
  };

  return (
    <div className="upload-box-wrapper">
      <span className="upload-box-header">{label}</span>

      <div className="upload-box-preview-frame">
        {src ? (
          <img src={getDirectImageUrl(src)} alt={alt || "Logo preview"} className="upload-box-preview-img" />
        ) : (
          <span className="upload-box-placeholder">No image preview</span>
        )}
      </div>

      <div className="upload-box-field-wrapper">
        <span className="upload-box-field-title">Logo URL</span>
        <input
          type="text"
          value={src}
          onChange={handleUrlChange}
          placeholder="https://..."
          className="text-input-field upload-box-input-height"
        />
      </div>

      <div className="upload-box-field-wrapper">
        <span className="upload-box-field-title">Alt Text</span>
        <input
          type="text"
          value={alt}
          onChange={(e) => onChange?.({ src, alt: e.target.value })}
          placeholder="Image description"
          className="text-input-field upload-box-input-height"
        />
      </div>

      <label className="upload-box-label upload-box-input-height">
        <span>📎</span> Upload File
        <input type="file" accept="image/*" onChange={handleFileChange} style={{ display: "none" }} />
      </label>
    </div>
  );
}

export function SubmitButton({ label = "SUBMIT CHANGES", onClick, style }) {
  return (
    <button onClick={onClick} className="submit-btn" style={style}>
      {label}
    </button>
  );
}

export function ShadowField({ label, value, onChange }) {
  const parseShadow = (str) => {
    let color = "rgba(0, 0, 0, 0.1)";
    let rest = str || "";

    const rgbaMatch = rest.match(/rgba\([^\)]+\)/i);
    const rgbMatch = rest.match(/rgb\([^\)]+\)/i);
    const hexMatch = rest.match(/#[0-9a-fA-F]+/);

    if (rgbaMatch) {
      color = rgbaMatch[0];
      rest = rest.replace(color, "");
    } else if (rgbMatch) {
      color = rgbMatch[0];
      rest = rest.replace(color, "");
    } else if (hexMatch) {
      color = hexMatch[0];
      rest = rest.replace(color, "");
    }

    const parts = rest.trim().split(/\s+/).filter(Boolean);
    const nums = parts.map(p => parseInt(p) || 0);

    return {
      hOffset: nums[0] || 0,
      vOffset: nums[1] || 0,
      blur: nums[2] || 0,
      spread: nums[3] || 0,
      color: color.trim()
    };
  };

  const shadowData = parseShadow(value);

  const parseColor = (colStr) => {
    let hex = "#000000";
    let opacity = 1;
    const lower = colStr.toLowerCase().trim();

    if (lower.startsWith("#")) {
      hex = lower;
      opacity = 1;
    } else if (lower.startsWith("rgba")) {
      const match = lower.match(/rgba\(\s*(\d+)\s*,\s*(\d+)\s*,\s*(\d+)\s*,\s*([\d\.]+)\s*\)/i);
      if (match) {
        const r = parseInt(match[1]).toString(16).padStart(2, "0");
        const g = parseInt(match[2]).toString(16).padStart(2, "0");
        const b = parseInt(match[3]).toString(16).padStart(2, "0");
        hex = `#${r}${g}${b}`;
        opacity = parseFloat(match[4]);
      }
    } else if (lower.startsWith("rgb")) {
      const match = lower.match(/rgb\(\s*(\d+)\s*,\s*(\d+)\s*,\s*(\d+)\s*\)/i);
      if (match) {
        const r = parseInt(match[1]).toString(16).padStart(2, "0");
        const g = parseInt(match[2]).toString(16).padStart(2, "0");
        const b = parseInt(match[3]).toString(16).padStart(2, "0");
        hex = `#${r}${g}${b}`;
        opacity = 1;
      }
    }
    return { hex, opacity };
  };

  const colorData = parseColor(shadowData.color);

  const [localHex, setLocalHex] = useState(colorData.hex);

  useEffect(() => {
    setLocalHex(colorData.hex);
  }, [colorData.hex]);

  const updateShadow = (updatedFields) => {
    const finalData = { ...shadowData, ...updatedFields };
    let finalColor = finalData.color;
    if (updatedFields.hex !== undefined || updatedFields.opacity !== undefined) {
      const hex = updatedFields.hex !== undefined ? updatedFields.hex : colorData.hex;
      const opacity = updatedFields.opacity !== undefined ? updatedFields.opacity : colorData.opacity;
      const r = parseInt(hex.slice(1, 3), 16) || 0;
      const g = parseInt(hex.slice(3, 5), 16) || 0;
      const b = parseInt(hex.slice(5, 7), 16) || 0;
      finalColor = `rgba(${r}, ${g}, ${b}, ${opacity})`;
    }

    const assembled = `${finalData.hOffset}px ${finalData.vOffset}px ${finalData.blur}px ${finalData.spread}px ${finalColor}`;
    onChange(assembled);
  };

  const handleHexChange = (val) => {
    setLocalHex(val);
    if (/^#[0-9a-fA-F]{3}$|^#[0-9a-fA-F]{6}$/.test(val)) {
      updateShadow({ hex: val });
    }
  };

  const r = parseInt(colorData.hex.slice(1, 3), 16) || 0;
  const g = parseInt(colorData.hex.slice(3, 5), 16) || 0;
  const b = parseInt(colorData.hex.slice(5, 7), 16) || 0;
  const rgbaColor = `rgba(${r}, ${g}, ${b}, ${colorData.opacity})`;

  return (
    <div className="picker-section-wrapper">
      <div className="field-col">
        <span className="field-label-text">{label}</span>
        <input
          type="text"
          value={value}
          onChange={(e) => onChange(e.target.value)}
          className="text-input-field"
        />
      </div>

      <div className="builder-frame-container">
        <div className="builder-title-text">
          SHADOW PICKER & BUILDER
        </div>

        <div className="two-col-grid" style={{ gap: 12 }}>
          <div className="field-col" style={{ gap: 4 }}>
            <span className="builder-slider-label">Horizontal Offset: {shadowData.hOffset}px</span>
            <input
              type="range" min="-50" max="50"
              value={shadowData.hOffset}
              onChange={(e) => updateShadow({ hOffset: parseInt(e.target.value) })}
              className="builder-slider-range"
            />
          </div>
          <div className="field-col" style={{ gap: 4 }}>
            <span className="builder-slider-label">Vertical Offset: {shadowData.vOffset}px</span>
            <input
              type="range" min="-50" max="50"
              value={shadowData.vOffset}
              onChange={(e) => updateShadow({ vOffset: parseInt(e.target.value) })}
              className="builder-slider-range"
            />
          </div>
        </div>

        <div className="two-col-grid" style={{ gap: 12 }}>
          <div className="field-col" style={{ gap: 4 }}>
            <span className="builder-slider-label">Blur Radius: {shadowData.blur}px</span>
            <input
              type="range" min="0" max="100"
              value={shadowData.blur}
              onChange={(e) => updateShadow({ blur: parseInt(e.target.value) })}
              className="builder-slider-range"
            />
          </div>
          <div className="field-col" style={{ gap: 4 }}>
            <span className="builder-slider-label">Spread Radius: {shadowData.spread}px</span>
            <input
              type="range" min="-30" max="30"
              value={shadowData.spread}
              onChange={(e) => updateShadow({ spread: parseInt(e.target.value) })}
              className="builder-slider-range"
            />
          </div>
        </div>

        <div className="two-col-grid" style={{ gap: 12, alignItems: "center" }}>
          <div className="field-col" style={{ gap: 4 }}>
            <span className="builder-slider-label">Color Hex</span>
            <div className="input-container builder-hex-input-wrapper">
              <div className="builder-mini-color-box" style={{ background: rgbaColor }}>
                <input
                  type="color"
                  value={colorData.hex}
                  onChange={(e) => handleHexChange(e.target.value)}
                  className="builder-invisible-color-input"
                />
              </div>
              <input
                type="text"
                value={localHex}
                onChange={(e) => handleHexChange(e.target.value)}
                className="builder-hex-text-input"
              />
            </div>
          </div>
          <div className="field-col" style={{ gap: 4 }}>
            <span className="builder-slider-label">Opacity: {Math.round(colorData.opacity * 100)}%</span>
            <input
              type="range" min="0" max="1" step="0.01"
              value={colorData.opacity}
              onChange={(e) => updateShadow({ opacity: parseFloat(e.target.value) })}
              className="builder-slider-range"
            />
          </div>
        </div>
      </div>
    </div>
  );
}

export function GradientField({ label, value, onChange }) {
  const parseGradient = (str) => {
    const defaultVal = { angle: 90, stops: [{ color: "#6A4A91", position: 0 }, { color: "#3E285F", position: 100 }] };
    if (!str || !str.includes("linear-gradient")) return defaultVal;

    const match = str.match(/linear-gradient\((.+)\)/i);
    if (!match) return defaultVal;

    const inner = match[1];

    const splitColorStops = (str) => {
      const stops = [];
      let current = "";
      let depth = 0;
      for (let i = 0; i < str.length; i++) {
        const char = str[i];
        if (char === "(") depth++;
        if (char === ")") depth--;
        if (char === "," && depth === 0) {
          stops.push(current.trim());
          current = "";
        } else {
          current += char;
        }
      }
      if (current.trim()) stops.push(current.trim());
      return stops;
    };

    const rawParts = splitColorStops(inner);
    if (rawParts.length < 2) return defaultVal;

    let angle = 90;
    let stopsStartIdx = 0;

    const firstPart = rawParts[0].trim();
    if (firstPart.includes("deg")) {
      angle = parseInt(firstPart) || 90;
      stopsStartIdx = 1;
    } else if (firstPart.startsWith("to ")) {
      const dirMap = {
        "to right": 90,
        "to left": 270,
        "to bottom": 180,
        "to top": 0,
        "to bottom right": 135,
        "to top right": 45,
        "to bottom left": 225,
        "to top left": 315
      };
      angle = dirMap[firstPart] || 90;
      stopsStartIdx = 1;
    }

    const stops = [];
    for (let i = stopsStartIdx; i < rawParts.length; i++) {
      const part = rawParts[i].trim();
      const lastSpaceIdx = part.lastIndexOf(" ");
      if (lastSpaceIdx === -1) {
        stops.push({ color: part, position: Math.round(((i - stopsStartIdx) / (rawParts.length - 1 - stopsStartIdx)) * 100) });
      } else {
        const color = part.substring(0, lastSpaceIdx).trim();
        const posStr = part.substring(lastSpaceIdx).trim();
        const position = parseInt(posStr) || 0;
        stops.push({ color, position });
      }
    }

    return { angle, stops };
  };

  const gradientData = parseGradient(value);

  const toHex = (col) => {
    if (col.startsWith("#")) return col;
    if (col.startsWith("rgba") || col.startsWith("rgb")) {
      const match = col.match(/\d+/g);
      if (match && match.length >= 3) {
        const r = parseInt(match[0]).toString(16).padStart(2, "0");
        const g = parseInt(match[1]).toString(16).padStart(2, "0");
        const b = parseInt(match[2]).toString(16).padStart(2, "0");
        return `#${r}${g}${b}`;
      }
    }
    return "#000000";
  };

  const updateGradient = (angle, stops) => {
    const stopsStr = stops
      .sort((a, b) => a.position - b.position)
      .map(s => `${s.color} ${s.position}%`)
      .join(", ");
    const assembled = `linear-gradient(${angle}deg, ${stopsStr})`;
    onChange(assembled);
  };

  const handleAddStop = () => {
    const newStops = [...gradientData.stops, { color: "#ffffff", position: 50 }];
    updateGradient(gradientData.angle, newStops);
  };

  const handleRemoveStop = (idx) => {
    if (gradientData.stops.length <= 2) return;
    const newStops = gradientData.stops.filter((_, i) => i !== idx);
    updateGradient(gradientData.angle, newStops);
  };

  const handleStopChange = (idx, fields) => {
    const newStops = gradientData.stops.map((stop, i) => {
      if (i === idx) return { ...stop, ...fields };
      return stop;
    });
    updateGradient(gradientData.angle, newStops);
  };

  return (
    <div className="picker-section-wrapper">
      <div className="field-col">
        {label && <span className="field-label-text">{label}</span>}
        <div className="gradient-preview-bar" style={{ background: value }} />
        <input
          type="text"
          value={value}
          onChange={(e) => onChange(e.target.value)}
          className="text-input-field"
        />
      </div>

      <div className="builder-frame-container">
        <div className="builder-title-row">
          <div className="builder-title-text">
            GRADIENT PICKER & BUILDER
          </div>
          <button
            onClick={handleAddStop}
            className="gradient-stop-add-btn"
          >
            <span>⊕</span> Add Stop
          </button>
        </div>

        <div className="field-col" style={{ gap: 4 }}>
          <span className="builder-slider-label">Gradient Angle: {gradientData.angle}°</span>
          <input
            type="range" min="0" max="360"
            value={gradientData.angle}
            onChange={(e) => updateGradient(parseInt(e.target.value), gradientData.stops)}
            className="builder-slider-range"
          />
        </div>

        <div className="field-col" style={{ gap: 10 }}>
          {gradientData.stops.map((stop, idx) => (
            <div key={idx} className="gradient-stop-row">
              <div className="gradient-stop-circle" style={{ background: stop.color }}>
                <input
                  type="color"
                  value={toHex(stop.color)}
                  onChange={(e) => handleStopChange(idx, { color: e.target.value })}
                  className="builder-invisible-color-input"
                />
              </div>

              <div style={{ flex: 1, display: "flex", alignItems: "center", gap: 8 }}>
                <input
                  type="range" min="0" max="100"
                  value={stop.position}
                  onChange={(e) => handleStopChange(idx, { position: parseInt(e.target.value) })}
                  className="gradient-stop-slider"
                />
                <span className="builder-slider-label" style={{ minWidth: 28, textAlign: "right" }}>{stop.position}%</span>
              </div>

              {gradientData.stops.length > 2 && (
                <button
                  onClick={() => handleRemoveStop(idx)}
                  className="gradient-stop-remove-btn"
                >
                  ✕
                </button>
              )}
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

// ─── Reusable Submit Confirmation Modal ──────────────────────────────────────
export function SubmitConfirmModal({ isOpen, onClose, onConfirm }) {
  const { t } = useTranslation();
  if (!isOpen) return null;
  return (
    <Modal
      headerBarMain={t("Confirm Changes")}
      actionCancelLabel={t("Cancel")}
      actionCancelOnSubmit={onClose}
      actionSaveLabel={t("Confirm")}
      actionSaveOnSubmit={onConfirm}
    >
      <div className="confirm-modal-content">
        {t("Are you sure you want to apply and submit these theme configuration changes?")}
      </div>
    </Modal>
  );
}

// ─── Reusable Preview Button ──────────────────────────────────────────────────
export function PreviewButton({ targetUrl, hasUnsavedChanges }) {
  const { t } = useTranslation();
  const [showWarningModal, setShowWarningModal] = useState(false);

  const handlePreviewClick = () => {
    if (hasUnsavedChanges) {
      setShowWarningModal(true);
    } else {
      window.open(targetUrl, "_blank");
    }
  };

  return (
    <>
      <button
        onClick={handlePreviewClick}
        className="preview-btn"
        title={t("Preview changes in a new tab")}
      >
        <ThemePreviewIcon fill="#6A4A91" /> {t("Preview")}
      </button>

      {showWarningModal && (
        <Modal
          headerBarMain={t("Unsubmitted Changes")}
          actionCancelLabel={t("Close")}
          actionCancelOnSubmit={() => setShowWarningModal(false)}
          hideSubmit={true}
        >
          <div className="confirm-modal-content">
            {t("You have unsubmitted changes. Please click 'SUBMIT CHANGES' to save your work before previewing.")}
          </div>
        </Modal>
      )}
    </>
  );
}
