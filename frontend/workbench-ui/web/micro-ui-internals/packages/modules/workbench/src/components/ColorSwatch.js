import React, { useState } from "react";
import { CopyIcon, CheckIcon } from "@upyog/workbench-ui-react-components";

/**
 * Helper utility to validate if a string is a standard 3-digit or 6-digit Hex color code.
 * E.g., "#fff", "#FFFFFF", "#3D2" are valid; "red", "#ff", "#12345" are invalid.
 * 
 * @param {string} hex - The string to check.
 * @returns {boolean} True if the string is a valid Hex color code.
 */
const isValidHex = (hex) => /^#([0-9A-Fa-f]{3}|[0-9A-Fa-f]{6})$/.test(hex);

/**
 * ColorSwatch Component
 * 
 * PURPOSE & USAGE:
 * This component is a reusable custom color input widget. It provides a visual preview
 * swatch of a color, a text input field to manually enter/edit Hex codes, and a copy-to-clipboard button.
 * 
 * KEY FEATURES:
 * 1. Live Visual Preview: Displays the active color in a colored block.
 * 2. Native Color Picker Toggle: Clicking the preview block triggers the native browser color picker.
 * 3. Hex Text Validation: Restricts manual input to Hex format; invalid entries are reverted on blur.
 * 4. Copy-to-Clipboard: Clicking the copy icon copies the hex string with visual feedback (check icon).
 */
const ColorSwatch = ({ label, value, onChange }) => {
  const [copied, setCopied] = useState(false);
  const [hexInput, setHexInput] = useState(value);
  const colorInputRef = React.useRef();
  const previewRef = React.useRef();

  /**
   * Copies the current color hex code to the user's system clipboard
   * and displays a visual checkmark state for 1.5 seconds.
   */
  const handleCopy = (e) => {
    e.stopPropagation();
    navigator.clipboard.writeText(value);
    setCopied(true);
    setTimeout(() => setCopied(false), 1500);
  };

  /**
   * Handles user typing inside the manual Hex input field.
   * If the input becomes a valid Hex string, it triggers the onChange callback immediately.
   */
  const handleHexChange = (e) => {
    const val = e.target.value;
    setHexInput(val);
    if (isValidHex(val)) onChange(val);
  };

  /**
   * Resets the input value to the last valid color state if the user leaves the input
   * with an invalid Hex string.
   */
  const handleHexBlur = () => {
    if (!isValidHex(hexInput)) setHexInput(value);
  };

  // Synchronize internal text state with external prop changes
  React.useEffect(() => { setHexInput(value); }, [value]);

  // Imperatively set background color of the preview swatch block to avoid inline CSS styles
  React.useEffect(() => {
    if (previewRef.current) {
      previewRef.current.style.backgroundColor = value;
    }
  }, [value]);

  return (
    <div className="configuration__color-swatch">
      {/* Label: Automatically formats camelCase text into separate capitalized words */}
      <span className="configuration__color-swatch__label">
        {label.replace(/([A-Z])/g, " $1").trim()} Color
      </span>

      <div className="configuration__color-swatch__box">
        {/* Color Preview Block: Clicking this opens the hidden native color picker */}
        <div
          ref={previewRef}
          className="configuration__color-swatch__preview"
          onClick={() => colorInputRef.current.click()}
        >
          <input
            ref={colorInputRef}
            type="color"
            value={isValidHex(value) ? value : "#000000"}
            onChange={(e) => { onChange(e.target.value); setHexInput(e.target.value); }}
            className="configuration__color-swatch__input"
          />
        </div>

        {/* Manual Hex Input Field */}
        <input
          className="configuration__color-swatch__hex-input"
          value={hexInput}
          onChange={handleHexChange}
          onBlur={handleHexBlur}
          maxLength={7}
          spellCheck={false}
        />

        {/* Clipboard Copy Trigger Icon */}
        <span className={`configuration__color-swatch__copy-icon${copied ? " configuration__color-swatch__copy-icon--copied" : ""}`} onClick={handleCopy}>
          {copied ? <CheckIcon /> : <CopyIcon />}
        </span>
      </div>
    </div>
  );
};

export default ColorSwatch;