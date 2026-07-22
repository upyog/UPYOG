/**
 * DynamicObjectRenderer — recursively renders a plain JS object/array as labeled rows.
 * Useful for debug/preview of nested form or API payloads without a fixed schema.
 */
import React from "react";

function toLabel(key) {
  return key
    .replace(/([A-Z])/g, " $1")
    .replace(/^./, (s) => s.toUpperCase())
    .trim();
}

function FieldRow({ label, value }) {
  return (
    <div className="dynamicObjectRenderer__field-row">
      <span className="dynamicObjectRenderer__field-label">{toLabel(label)}</span>
      <span className="dynamicObjectRenderer__field-value">{String(value ?? "")}</span>
    </div>
  );
}

function ObjectFields({ obj }) {
  if (!obj || typeof obj !== "object") return null;
  return (
    <>
      {Object.entries(obj).map(([key, value]) => {
        if (key === "i18nKey") return null;

        if (Array.isArray(value)) {
          return (
            <div key={key} className="dynamicObjectRenderer__section">
              <div className="dynamicObjectRenderer__section-title">{toLabel(key)}</div>
              {value.map((item, i) => (
                <div key={i}>
                  {value.length > 1 && (
                    <div className="dynamicObjectRenderer__array-label">
                      Item {i + 1} of {value.length}
                    </div>
                  )}
                  <div className="dynamicObjectRenderer__card">
                    {typeof item === "object" && item !== null
                      ? <ObjectFields obj={item} />
                      : <span>{String(item)}</span>
                    }
                  </div>
                </div>
              ))}
            </div>
          );
        }

        if (typeof value === "object" && value !== null) {
          return (
            <div key={key} className="dynamicObjectRenderer__section">
              <div className="dynamicObjectRenderer__section-title">{toLabel(key)}</div>
              <div className="dynamicObjectRenderer__card">
                <ObjectFields obj={value} />
              </div>
            </div>
          );
        }

        return <FieldRow key={key} label={key} value={value} />;
      })}
    </>
  );
}

const DynamicObjectRenderer = ({ data }) => {
  if (!data) return null;

  const items = Array.isArray(data) ? data : [data];

  return (
    <div className="dynamicObjectRenderer">
      {items.map((item, i) => (
        <div key={i} className="dynamicObjectRenderer__card">
          <ObjectFields obj={item} />
        </div>
      ))}
    </div>
  );
};

export default DynamicObjectRenderer;