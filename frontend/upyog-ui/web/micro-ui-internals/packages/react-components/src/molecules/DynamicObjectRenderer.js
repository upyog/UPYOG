/**
 * DynamicObjectRenderer.js
 *
 * Schema-free recursive renderer for a plain JS object or array of objects.
 * Walks nested structures and prints labeled rows — useful for debug / preview
 * of form payloads, API responses, or any nested data without a fixed MDMS
 * field config (contrast with DynamicCheckPage, which is config-driven).
 *
 * Responsibilities
 * ----------------
 * 1. Accept `data` as a single object or an array; wrap a single object in a
 *    one-item list so each top-level item gets its own card.
 * 2. Recurse via ObjectFields:
 *    - primitives → FieldRow (camelCase key → humanized label + string value)
 *    - nested objects → section title + nested card
 *    - arrays → section title + one card per item (with "Item N of M" when
 *      length > 1); object items recurse, primitives print as text
 * 3. Skip the key "i18nKey" so Digit option metadata does not clutter the view.
 * 4. Return null when data is falsy.
 *
 * Label formatting
 * ----------------
 * Keys are humanized by inserting spaces before capitals and uppercasing the
 * first character (e.g. buildingName → "Building Name").
 *
 * Typical usage
 * -------------
 *   <DynamicObjectRenderer data={payload} />
 *   <DynamicObjectRenderer data={[recordA, recordB]} />
 *
 * Props
 * -----
 * @param {object|object[]|null|undefined} data  Object or array of objects to render.
 *                                               Falsy values render nothing.
 *
 * CSS classes (BEM under .dynamicObjectRenderer)
 * ----------------------------------------------
 * - __card, __section, __section-title, __array-label
 * - __field-row, __field-label, __field-value
 *
 * @see DynamicCheckPage  (config-driven summary; prefer that for wizard review)
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