import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { useLocation } from "react-router-dom";
import { Modal, ThemePreviewIcon, Toast, DeleteIconv2, CopyIcon, CheckIcon, AddIcon, UploadIcon, CloseSvg } from "@upyog/workbench-ui-react-components";
import { getCardIcon } from "../utils";

/**
 * SectionHeader Component
 * Renders a standard block header with a circular sequence index indicator.
 * Used for theme form dividers (e.g. "1. Colors Theme", "2. Shadows", etc.)
 * 
 * @param {Object} props
 * @param {string|number} props.number - The sequence number displayed in the circular badge.
 * @param {string} props.title - The title text of the section.
 */
export function SectionHeader({ number, title }) {
  return (
    <div className="theme-section-header">
      <span className="theme-section-header-badge">{number}</span>
      {title.toUpperCase()}
    </div>
  );
}

/**
 * PreviewDropdown Component
 * Renders a dropdown action button allowing quick access to navigate between different configuration forms.
 * Automatically handles outside click triggers to close the dropdown popover.
 * 
 * @returns {React.ReactNode}
 */
export function PreviewDropdown() {
  const { t } = useTranslation();
  const [isOpen, setIsOpen] = useState(false);
  const location = useLocation();
  const navigate = Digit.Hooks.useCustomNavigate();

  const pages = [
    { label: t("Customize Theme"), path: "/workbench/theme-configuration" },
    { label: t("Onboarding Content"), path: "/workbench/onboarding-common-content" },
    { label: t("Onboarding Login"), path: "/workbench/onboarding-login-configuration" }
  ];

  useEffect(() => {
    if (!isOpen) return;
    const handleOutsideClick = (e) => {
      if (!e.target.closest(".preview-dropdown-container")) {
        setIsOpen(false);
      }
    };
    document.addEventListener("click", handleOutsideClick);
    return () => document.removeEventListener("click", handleOutsideClick);
  }, [isOpen]);

  const currentPath = location.pathname;

  return (
    <div className="preview-dropdown-container">
      <button
        className="preview-btn-trigger"
        onClick={() => setIsOpen(!isOpen)}
        title={t("Preview configuration pages")}
      >
        <ThemePreviewIcon fill="#3D2364" /> {t("Preview")}
      </button>
      {isOpen && (
        <div className="preview-dropdown-menu">
          {pages.map((page) => {
            const isActive = currentPath.includes(page.path);
            return (
              <div
                key={page.path}
                className={`preview-dropdown-item ${isActive ? "active" : ""}`}
                onClick={() => {
                  setIsOpen(false);
                  navigate(page.path);
                }}
              >
                {page.label}
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
}

/**
 * ThemePageSelector Component
 * A drop-down HTML select element to toggle edit views between the different customization files.
 * Useful for navigating sections of configuration forms easily.
 * 
 * @returns {React.ReactNode}
 */
export function ThemePageSelector() {
  const { t } = useTranslation();
  const location = useLocation();
  const navigate = Digit.Hooks.useCustomNavigate();

  const options = [
    { label: t("Customize Theme"), path: "/workbench/theme-configuration" },
    { label: t("Onboarding Content"), path: "/workbench/onboarding-common-content" },
    { label: t("Onboarding Login"), path: "/workbench/onboarding-login-configuration" }
  ];

  const currentPath = location.pathname;
  const currentVal = options.find(opt => currentPath.includes(opt.path))?.path || options[0].path;

  const handleChange = (e) => {
    const selectedPath = e.target.value;
    navigate(selectedPath);
  };

  return (
    <div className="theme-page-selector-container">
      <span className="selector-label">{t("Preview Pages:")}</span>
      <select
        value={currentVal}
        onChange={handleChange}
        className="select-input theme-page-selector"
      >
        {options.map((opt) => (
          <option key={opt.path} value={opt.path}>
            {opt.label}
          </option>
        ))}
      </select>
    </div>
  );
}

/**
 * Card Component
 * A wrapper container with standard styling, padding, and layout shadows.
 * 
 * @param {Object} props
 * @param {React.ReactNode} props.children - Embedded card content components.
 * @param {Object} [props.style] - Inline style override properties.
 */
export function Card({ children, className }) {
  return (
    <div className={`theme-card ${className || ""}`}>
      {children}
    </div>
  );
}

/**
 * CardTitle Component
 * Card header block containing an icon, title, description, and optional right actions.
 * 
 * @param {Object} props
 * @param {React.ReactNode} props.icon - Icon symbol or node displayed before the header.
 * @param {string} props.title - Major card heading text.
 * @param {string} [props.description] - Description text explaining card config fields.
 * @param {React.ReactNode} [props.rightElement] - Optional widget element positioned on the right header margin.
 */
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

/**
 * FieldsRow Component
 * Layout wrapper handling standard multi-column grid alignment or flat horizontal flex layouts.
 * 
 * @param {Object} props
 * @param {React.ReactNode} props.children - Input fields rendered side-by-side.
 * @param {boolean} [props.grid=true] - If true, applies standard css grid. If false, renders as flexbox row.
 */
export function FieldsRow({ children, grid = true }) {
  if (!grid) {
    return (
      <div className="fields-row-flex">
        {children}
      </div>
    );
  }
  return (
    <div className="fields-grid">
      {children}
    </div>
  );
}

/**
 * ColorField Component
 * Custom color picker widget that displays color hex strings alongside a clickable color indicator.
 * Includes a "Copy" button to instantly copy the active hex code value to the clipboard.
 * 
 * @param {Object} props
 * @param {string} props.label - Configuration field name.
 * @param {string} props.value - Active color value hex code.
 * @param {Function} props.onChange - Selection trigger callback.
 */
export function ColorField({ label, value, onChange }) {
  const [copied, setCopied] = React.useState(false);
  const previewRef = React.useRef();

  const handleCopy = () => {
    navigator.clipboard?.writeText(value).then(() => {
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    });
  };

  React.useEffect(() => {
    if (previewRef.current) {
      previewRef.current.style.backgroundColor = value;
    }
  }, [value]);

  return (
    <div className="field-col">
      <span className="field-label-text">{label}</span>
      <div className="input-container">
        <div ref={previewRef} className="color-picker-preview-box">
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
          {copied ? <CheckIcon /> : <CopyIcon />}
        </button>
      </div>
    </div>
  );
}

/**
 * NumberField Component
 * Standard number configuration input field supporting customizable value units (e.g. "px", "rem").
 * 
 * @param {Object} props
 * @param {string} props.label - Configuration input field label name.
 * @param {number|string} props.value - Field value.
 * @param {string} [props.unit] - Target unit suffix appended on change.
 * @param {Function} props.onChange - Field value change callback handler.
 * @param {Object} [props.style] - Inline style override properties.
 */
export function NumberField({ label, value, unit, onChange }) {
  return (
    <div className="field-col">
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

/**
 * TextField Component
 * Standard text input field supporting either simple flat text inputs or multi-line textareas.
 * 
 * @param {Object} props
 * @param {string} props.label - Label name for the text parameter.
 * @param {string} props.value - Active configuration text value.
 * @param {Function} props.onChange - Form update callback method.
 * @param {string} [props.placeholder] - Hint text prompt when empty.
 * @param {string} [props.type="text"] - Renders input type ("text" or "textarea").
 * @param {Object} [props.style] - Style override configurations.
 */
export function TextField({ label, value, onChange, placeholder, type = "text" }) {
  return (
    <div className="field-col">
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

/**
 * CheckboxField Component
 * Form toggle input component displaying standard Active/Show checkboxes.
 * 
 * @param {Object} props
 * @param {string} props.label - Checkbox setting heading text.
 * @param {boolean} props.checked - Check state value parameter.
 * @param {Function} props.onChange - Change toggle callback handler.
 * @param {Object} [props.style] - Style customization definitions.
 */
export function CheckboxField({ label, checked, onChange }) {
  return (
    <div className="field-col">
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

/**
 * SelectField Component
 * Standard selector input displaying a dropdown of predefined choices.
 * 
 * @param {Object} props
 * @param {string} props.label - Dropdown label title.
 * @param {string} props.value - Active configuration select value.
 * @param {Array<string|Object>} [props.options=[]] - List of choices (either strings or { label, value } objects).
 * @param {Function} props.onChange - Dropdown selection update method.
 * @param {Object} [props.style] - Style override declarations.
 */
export function SelectField({ label, value, options = [], onChange }) {
  return (
    <div className="field-col">
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

/**
 * UploadBox Component
 * Image asset upload box that allows selecting files for uploading or using custom URLs.
 * Integrates UPYOG's built-in FileStorage/FileFetch API endpoints to upload file instances
 * asynchronously, retrieve direct URLs, and display visual thumbnails.
 * Handles parsing/resolving shared Google Drive file URLs into standard direct links.
 * 
 * @param {Object} props
 * @param {string} props.label - Section name of upload configuration target.
 * @param {Object} [props.value={}] - Value object holding source asset parameters.
 * @param {string} [props.value.src=""] - Target URL source link of the image.
 * @param {string} [props.value.alt=""] - Alt description text.
 * @param {Function} props.onChange - Value update method returning the new { src, alt } object.
 */
export function UploadBox({ label, value = {}, onChange }) {
  const { src = "", alt = "" } = value || {};
  const { t } = useTranslation();
  const [isUploading, setIsUploading] = useState(false);
  const [uploadError, setUploadError] = useState(null);

  /**
   * Helper utility method that automatically parses Google Drive sharing links
   * and maps them to direct, embeddable user-content image rendering URLs.
   * 
   * @param {string} url - Source Google Drive path string.
   * @returns {string} Fully resolved rendering URL.
   */
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

  /**
   * Handles local image selection, runs validation checks, uploads files to Filestorage,
   * fetches download URLs, and updates configuration values.
   * 
   * @param {React.ChangeEvent<HTMLInputElement>} e - File input trigger event object.
   */
  const handleFileChange = async (e) => {
    const file = e.target.files?.[0];
    if (file) {
      const allowedFileTypesRegex = /(.*?)(jpg|jpeg|png|image)$/i;

      // Limit file size to 5MB
      if (file.size >= 5242880) {
        setUploadError(t("CS_MAXIMUM_UPLOAD_SIZE_EXCEEDED"));
        return;
      }
      if (file?.type && !allowedFileTypesRegex.test(file?.type)) {
        setUploadError(t("NOT_SUPPORTED_FILE_TYPE"));
        return;
      }

      setIsUploading(true);
      setUploadError(null);

      try {
        const stateId = Digit.ULBService.getStateId() || Digit.ULBService.getCurrentTenantId()?.split(".")[0];
        const response = await Digit.UploadServices.Filestorage("workbench", file, stateId);

        if (response?.data?.files?.length > 0) {
          const fileStoreId = response?.data?.files[0]?.fileStoreId;
          const fetchRes = await Digit.UploadServices.Filefetch([fileStoreId], stateId);
          const fileUrl = fetchRes?.data?.fileStoreIds?.[0]?.url;

          if (fileUrl) {
            onChange?.({ src: fileUrl, alt });
          } else {
            setUploadError(t("CS_FILE_UPLOAD_ERROR"));
          }
        } else {
          setUploadError(t("CS_FILE_UPLOAD_ERROR"));
        }
      } catch (err) {
        console.error("File upload failed:", err);
        setUploadError(t("CS_FILE_UPLOAD_ERROR"));
      } finally {
        setIsUploading(false);
      }
    }
  };

  /**
   * Updates preview thumbnail images on typing or editing the URL text fields.
   */
  const handleUrlChange = (e) => {
    const rawVal = e.target.value;
    const resolvedUrl = getDirectImageUrl(rawVal);
    onChange?.({ src: resolvedUrl, alt });
  };

  return (
    <div className="upload-box-wrapper">
      <span className="upload-box-header">{label}</span>

      <div className="upload-box-preview-frame">
        {isUploading ? (
          <span className="upload-box-placeholder">{t("Uploading...")}</span>
        ) : src ? (
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
        <UploadIcon fill="#3D2364" width="16" height="16" /> {isUploading ? t("Uploading...") : t("Upload File")}
        <input type="file" accept="image/*" onChange={handleFileChange} className="upload-box-file-input" disabled={isUploading} />
      </label>

      {uploadError && (
        <span className="upload-box-error-msg">
          {uploadError}
        </span>
      )}
    </div>
  );
}

/**
 * SubmitButton Component
 * Flat submit button displayed on action footers.
 * 
 * @param {Object} props
 * @param {string} [props.label="SUBMIT CHANGES"] - Button text label.
 * @param {Function} props.onClick - Button click handler method.
 * @param {Object} [props.style] - Custom CSS override properties.
 */
export function SubmitButton({ label = "SUBMIT CHANGES", onClick }) {
  return (
    <button onClick={onClick} className="submit-btn">
      {label}
    </button>
  );
}

/**
 * ShadowField Component
 * Rich design widget that parses CSS box-shadow strings (e.g. "1px 2px 5px rgba(0,0,0,0.15)")
 * into granular vertical/horizontal offset sliders, spread/blur controls, and color pickers.
 * Reassembles updated properties back into valid CSS declarations.
 * 
 * @param {Object} props
 * @param {string} props.label - Configuration field name.
 * @param {string} props.value - CSS box-shadow string.
 * @param {Function} props.onChange - Value update callback method.
 */
export function ShadowField({ label, value, onChange }) {

  /**
   * Helper utility method that parses raw CSS box-shadow strings.
   * Extracts horizontal, vertical offsets, blur, spread radius metrics, and opacity colors.
   * 
   * @param {string} str - Raw box shadow value.
   * @returns {Object} Extracted parameters object.
   */
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

  /**
   * Helper method that splits colors into clean hex and alpha opacity properties.
   * 
   * @param {string} colStr - Color configuration string.
   * @returns {Object} { hex, opacity } values.
   */
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

  /**
   * Updates state data and reassembles parameters into CSS box-shadow declarations.
   * 
   * @param {Object} updatedFields - Map of modified parameter fields.
   */
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
  const rgbaPreviewRef = React.useRef();

  React.useEffect(() => {
    if (rgbaPreviewRef.current) {
      rgbaPreviewRef.current.style.backgroundColor = rgbaColor;
    }
  }, [rgbaColor]);

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

        <div className="builder-grid">
          <div className="builder-field-col">
            <span className="builder-slider-label">Horizontal Offset: {shadowData.hOffset}px</span>
            <input
              type="range" min="-50" max="50"
              value={shadowData.hOffset}
              onChange={(e) => updateShadow({ hOffset: parseInt(e.target.value) })}
              className="builder-slider-range"
            />
          </div>
          <div className="builder-field-col">
            <span className="builder-slider-label">Vertical Offset: {shadowData.vOffset}px</span>
            <input
              type="range" min="-50" max="50"
              value={shadowData.vOffset}
              onChange={(e) => updateShadow({ vOffset: parseInt(e.target.value) })}
              className="builder-slider-range"
            />
          </div>
        </div>

        <div className="builder-grid">
          <div className="builder-field-col">
            <span className="builder-slider-label">Blur Radius: {shadowData.blur}px</span>
            <input
              type="range" min="0" max="100"
              value={shadowData.blur}
              onChange={(e) => updateShadow({ blur: parseInt(e.target.value) })}
              className="builder-slider-range"
            />
          </div>
          <div className="builder-field-col">
            <span className="builder-slider-label">Spread Radius: {shadowData.spread}px</span>
            <input
              type="range" min="-30" max="30"
              value={shadowData.spread}
              onChange={(e) => updateShadow({ spread: parseInt(e.target.value) })}
              className="builder-slider-range"
            />
          </div>
        </div>

        <div className="builder-grid-align-center">
          <div className="builder-field-col">
            <span className="builder-slider-label">Color Hex</span>
            <div className="input-container builder-hex-input-wrapper">
              <div ref={rgbaPreviewRef} className="builder-mini-color-box">
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
          <div className="builder-field-col">
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

/**
 * GradientField Component
 * Dynamic CSS gradient builder widget. Parses linear-gradient expressions into granular controls:
 * - Angle slider control (0deg - 360deg).
 * - Multi-stop timeline configuration (add stops, select hex codes, position slides, delete stops).
 * Reassembles parameters into standard CSS `linear-gradient` strings.
 * 
 * @param {Object} props
 * @param {string} [props.label] - Field label header text.
 * @param {string} props.value - CSS linear-gradient value string.
 * @param {Function} props.onChange - Selection trigger callback handler.
 */
export function GradientField({ label, value, onChange }) {
  const previewBarRef = React.useRef();
  const stopRefs = React.useRef([]);

  /**
   * Parses linear-gradient configuration values into structured angles and stops.
   * 
   * @param {string} str - Raw CSS gradient definition.
   * @returns {Object} Gradient object { angle, stops }
   */
  const parseGradient = (str) => {
    const defaultVal = { angle: 90, stops: [{ color: "#6A4A91", position: 0 }, { color: "#3E285F", position: 100 }] };
    if (!str || !str.includes("linear-gradient")) return defaultVal;

    const match = str.match(/linear-gradient\((.+)\)/i);
    if (!match) return defaultVal;

    const inner = match[1];

    /**
     * Splits color stops taking nested parentheses (rgb/rgba colors) into consideration.
     */
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

  React.useEffect(() => {
    if (previewBarRef.current) {
      previewBarRef.current.style.background = value;
    }
  }, [value]);

  React.useEffect(() => {
    gradientData.stops.forEach((stop, i) => {
      if (stopRefs.current[i]) {
        stopRefs.current[i].style.backgroundColor = stop.color;
      }
    });
  }, [gradientData.stops]);

  /**
   * Helper utility mapping parsed rgb/rgba strings to direct hex codes for HTML inputs.
   */
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

  /**
   * Updates state data and updates configurations in dot-notation path.
   */
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
        <div ref={previewBarRef} className="gradient-preview-bar" />
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
            <AddIcon fill="#3D2364" /> Add Stop
          </button>
        </div>

        <div className="builder-field-col">
          <span className="builder-slider-label">Gradient Angle: {gradientData.angle}°</span>
          <input
            type="range" min="0" max="360"
            value={gradientData.angle}
            onChange={(e) => updateGradient(parseInt(e.target.value), gradientData.stops)}
            className="builder-slider-range"
          />
        </div>

        <div className="builder-field-col-gap-10">
          {gradientData.stops.map((stop, idx) => (
            <div key={idx} className="gradient-stop-row">
              <div ref={el => stopRefs.current[idx] = el} className="gradient-stop-circle">
                <input
                  type="color"
                  value={toHex(stop.color)}
                  onChange={(e) => handleStopChange(idx, { color: e.target.value })}
                  className="builder-invisible-color-input"
                />
              </div>

              <div className="gradient-stop-slider-wrapper">
                <input
                  type="range" min="0" max="100"
                  value={stop.position}
                  onChange={(e) => handleStopChange(idx, { position: parseInt(e.target.value) })}
                  className="gradient-stop-slider"
                />
                <span className="gradient-stop-position-label builder-slider-label">{stop.position}%</span>
              </div>

              {gradientData.stops.length > 2 && (
                <button
                  onClick={() => handleRemoveStop(idx)}
                  className="gradient-stop-remove-btn"
                >
                  <CloseSvg />
                </button>
              )}
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

/**
 * SubmitConfirmModal Component
 * Modal component displayed on the submit action footer to verify changes before saving.
 * 
 * @param {Object} props
 * @param {boolean} props.isOpen - Checks if modal displays.
 * @param {Function} props.onClose - Action triggered on cancel closure.
 * @param {Function} props.onConfirm - Action callback triggered on confirm updates submission.
 */
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

/**
 * PreviewButton Component
 * Eye-icon action button displayed on headers. Checks for unsaved modifications:
 * - If changes are fully saved: opens the preview URL path in a new tab.
 * - If unsaved configuration edits exist: displays a warning modal pop-up prompt
 *   supporting a direct "Submit & Preview" action to submit changes on-the-fly.
 * 
 * @param {Object} props
 * @param {string} props.targetUrl - Direct redirection URL path (e.g. "/employee").
 * @param {boolean} props.hasUnsavedChanges - State flag indicating active form edits exist.
 * @param {Function} props.onSubmit - Reusable submission method to save configurations.
 */
export function PreviewButton({ targetUrl, hasUnsavedChanges, onSubmit }) {
  const { t } = useTranslation();
  const [showWarningModal, setShowWarningModal] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handlePreviewClick = () => {
    if (hasUnsavedChanges) {
      setShowWarningModal(true);
    } else {
      window.open(targetUrl, "_blank");
    }
  };

  const handleSaveAndPreview = async () => {
    if (onSubmit) {
      setIsSubmitting(true);
      try {
        await onSubmit();
        window.open(targetUrl, "_blank");
        setShowWarningModal(false);
      } catch (err) {
        console.error("Save & Preview failed:", err);
      } finally {
        setIsSubmitting(false);
      }
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
          actionSaveLabel={isSubmitting ? t("Submitting...") : t("Submit & Preview")}
          actionSaveOnSubmit={handleSaveAndPreview}
          isDisabled={isSubmitting}
        >
          <div className="confirm-modal-content">
            {t("You have unsubmitted changes. Would you like to submit them and proceed to preview?")}
          </div>
        </Modal>
      )}
    </>
  );
}

/**
 * ThemeEditorLayout Component
 * 
 * WHY THIS WAS ADDED:
 * Collapses identical wrapper boilerplate (main layout shell, title row, preview buttons,
 * save buttons, confirmation modals, and toast alerts) that previously existed on all
 * customization form views.
 */
export function ThemeEditorLayout({
  title,
  previewUrl,
  hasUnsavedChanges,
  onSubmit,
  showConfirmModal,
  setShowConfirmModal,
  toast,
  onCloseToast,
  children,
  submitLabel
}) {
  const { t } = useTranslation();
  return (
    <div className="theme-form-container">
      <div className="theme-header-row">
        <div className="theme-form-title">
          {t(title)}
        </div>
        <PreviewButton
          targetUrl={previewUrl}
          hasUnsavedChanges={hasUnsavedChanges}
          onSubmit={onSubmit}
        />
      </div>

      {children}

      <div className="submit-container">
        <button
          onClick={() => setShowConfirmModal(true)}
          className="submit-btn"
          disabled={!hasUnsavedChanges}
        >
          {submitLabel || t("SUBMIT CHANGES")}
        </button>
      </div>

      <SubmitConfirmModal
        isOpen={showConfirmModal}
        onClose={() => setShowConfirmModal(false)}
        onConfirm={onSubmit}
      />

      {toast && <Toast label={toast.label} error={toast.error} onClose={onCloseToast} />}
    </div>
  );
}

/**
 * ResponsiveImageQuartet Component
 * 
 * WHY THIS WAS ADDED:
 * Content's background settings duplicated a 4-input row (mobile, tablet, laptop, desktop image URLs)
 * twice: once for the root background and once for the card panel background.
 * Encapsulating this into a single configuration block avoids repeating ~100 lines of markup.
 */
export function ResponsiveImageQuartet({ responsiveData, onChange, basePath }) {
  const { t } = useTranslation();
  return (
    <>
      <div className="section-sub-title">
        {t("Responsive Images")}
      </div>
      <FieldsRow>
        <TextField
          label={t("MOBILE IMAGE URL")}
          value={responsiveData?.mobile?.image || ""}
          onChange={(v) => onChange(`${basePath}.responsive.mobile.image`, v)}
          placeholder="https://..."
        />
        <TextField
          label={t("TABLET IMAGE URL")}
          value={responsiveData?.tablet?.image || ""}
          onChange={(v) => onChange(`${basePath}.responsive.tablet.image`, v)}
          placeholder="https://..."
        />
        <TextField
          label={t("LAPTOP IMAGE URL")}
          value={responsiveData?.laptop?.image || ""}
          onChange={(v) => onChange(`${basePath}.responsive.laptop.image`, v)}
          placeholder="https://..."
        />
        <TextField
          label={t("DESKTOP IMAGE URL")}
          value={responsiveData?.desktop?.image || ""}
          onChange={(v) => onChange(`${basePath}.responsive.desktop.image`, v)}
          placeholder="https://..."
        />
      </FieldsRow>
    </>
  );
}

/**
 * FieldSubsection Component
 * 
 * WHY THIS WAS ADDED:
 * Standardizes sub-headers and dividing borders (border-bottom + purple titles).
 * Previously, this markup used repeated, inline CSS declarations inside Login & Register forms.
 * Using a dedicated component enforces visual consistency and DRY standards.
 */
export function FieldSubsection({ title, hasBorder = true, children }) {
  const { t } = useTranslation();
  return (
    <div className={`field-subsection-container ${hasBorder ? "has-border" : ""}`}>
      {title && (
        <div className="field-subsection-title">
          {t(title)}
        </div>
      )}
      {children}
    </div>
  );
}

/**
 * FeatureListEditor Component
 * 
 * WHY THIS WAS ADDED:
 * Encapsulates the dynamic features-list list builder widget in OnBoardingContent.js,
 * extracting the key, index, inputs, and deletion icon layout.
 */
export function FeatureListEditor({ features, onAdd, onRemove, onChange, basePath }) {
  const { t } = useTranslation();
  return (
    <Card className="theme-card-margin">
      <CardTitle
        icon={getCardIcon("sidebar")}
        title={t("Features List")}
        description={t("Dynamic list of key features")}
        rightElement={
          <button onClick={onAdd} className="add-feature-btn">
            <AddIcon fill="#3D2364" /> {t("Add Feature")}
          </button>
        }
      />
      <div className="full-width-col">
        {features.map((feature, idx) => (
          <div key={idx} className="feature-box-row">
            <div className="feature-row-grid">
              <TextField
                label={t("ICON URL")}
                value={feature.icon || ""}
                onChange={(v) => onChange(`${basePath}.${idx}.icon`, v)}
                placeholder="https://..."
              />
              <TextField
                label={t("SHORT TITLE")}
                value={feature.title || ""}
                onChange={(v) => onChange(`${basePath}.${idx}.title`, v)}
                placeholder={t("Title")}
              />
              <TextField
                label={t("DESCRIPTION")}
                value={feature.description || ""}
                onChange={(v) => onChange(`${basePath}.${idx}.description`, v)}
                placeholder={t("Description")}
              />
            </div>
            <button
              onClick={() => onRemove(idx)}
              title={t("Remove Feature")}
              className="remove-feature-btn"
            >
              <DeleteIconv2 fill="#D85A5A" />
            </button>
          </div>
        ))}
      </div>
    </Card>
  );
}

/**
 * SecondaryActionsEditor Component
 * 
 * WHY THIS WAS ADDED:
 * Encapsulates the dynamic secondary actions layout editor widget in OnBoardingLogin.js.
 */
export function SecondaryActionsEditor({ actions, onAdd, onRemove, onChange, basePath }) {
  const { t } = useTranslation();
  return (
    <div className="full-width-col">
      <span className="section-sub-title secondary-actions-header-margin">{t("Secondary Actions")}</span>
      {actions.map((action, idx) => (
        <div key={idx} className="feature-box-row secondary-action-row-padding">
          <div className="secondary-action-grid">
            <input
              type="text"
              value={action.icon || ""}
              onChange={(e) => onChange(`${basePath}.${idx}.icon`, e.target.value)}
              placeholder={t("Icon URL (https://...)")}
              className="text-input-field secondary-action-input-field"
            />
            <input
              type="text"
              value={action.label || ""}
              onChange={(e) => onChange(`${basePath}.${idx}.label`, e.target.value)}
              placeholder={t("Action Label")}
              className="text-input-field secondary-action-input-field"
            />
          </div>
          <button
            onClick={() => onRemove(idx)}
            title={t("Remove Action")}
            className="remove-feature-btn remove-action-btn-padding"
          >
            <DeleteIconv2 fill="#D85A5A" />
          </button>
        </div>
      ))}
    </div>
  );
}
