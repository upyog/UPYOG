import React, { useState } from "react";

const isValidHex = (hex) => /^#([0-9A-Fa-f]{3}|[0-9A-Fa-f]{6})$/.test(hex);

const ColorSwatch = ({ label, value, onChange }) => {
  const [copied, setCopied] = useState(false);
  const [hexInput, setHexInput] = useState(value);
  const colorInputRef = React.useRef();

  const handleCopy = (e) => {
    e.stopPropagation();
    navigator.clipboard.writeText(value);
    setCopied(true);
    setTimeout(() => setCopied(false), 1500);
  };

  const handleHexChange = (e) => {
    const val = e.target.value;
    setHexInput(val);
    if (isValidHex(val)) onChange(val);
  };

  const handleHexBlur = () => {
    if (!isValidHex(hexInput)) setHexInput(value);
  };

  React.useEffect(() => { setHexInput(value); }, [value]);

  return (
    <div className="configuration__color-swatch">
      <span className="configuration__color-swatch__label">
        {label.replace(/([A-Z])/g, " $1").trim()} Color
      </span>
      <div className="configuration__color-swatch__box">
        <div
          className="configuration__color-swatch__preview"
          style={{ background: value }}
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
        <input
          className="configuration__color-swatch__hex-input"
          value={hexInput}
          onChange={handleHexChange}
          onBlur={handleHexBlur}
          maxLength={7}
          spellCheck={false}
        />
        <span className={`configuration__color-swatch__copy-icon${copied ? " configuration__color-swatch__copy-icon--copied" : ""}`} onClick={handleCopy}>
          {copied ? "✓" : "⧉"}
        </span>
      </div>
    </div>
  );
};

export default ColorSwatch;