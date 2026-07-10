/**
 * ESTDocumentPreview.js
 * Document preview with clickable thumbnails for check + acknowledgement pages.
 */

import React, { useState } from "react";
import { useTranslation } from "react-i18next";

const LargePdfSvg = ({ size = 48 }) => (
  <svg
    width={size}
    height={size}
    viewBox="0 0 24 24"
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
    aria-hidden
    style={{ flexShrink: 0 }}
  >
    <rect width="24" height="24" rx="4" fill="#D32F2F" />
    <text
      x="3"
      y="16"
      fontFamily="Arial, sans-serif"
      fontSize="10"
      fontWeight="700"
      fill="#FFFFFF"
    >
      PDF
    </text>
  </svg>
);

const DocThumbnail = ({ href, label, url, thumbSize = 72 }) => {
  const [imgFailed, setImgFailed] = useState(false);
  const showImage = Boolean(url) && !imgFailed;

  return (
    <a
      href={href}
      target="_blank"
      rel="noopener noreferrer"
      style={{
        display: "flex",
        alignItems: "center",
        gap: 16,
        textDecoration: "none",
        marginBottom: 16,
        width: "100%",
        padding: "8px 0",
      }}
    >
      <div
        style={{
          minWidth: 160,
          fontWeight: 700,
          color: "#111",
          fontSize: "14px",
        }}
      >
        {label}
      </div>

      <div
        style={{
          width: thumbSize,
          height: thumbSize,
          border: "1px solid #E0E0E0",
          borderRadius: 6,
          overflow: "hidden",
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
          backgroundColor: "#FAFAFA",
          flexShrink: 0,
        }}
      >
        {showImage ? (
          <img
            src={url}
            alt={label}
            onError={() => setImgFailed(true)}
            style={{ width: "100%", height: "100%", objectFit: "cover" }}
          />
        ) : (
          <LargePdfSvg size={Math.min(thumbSize, 48)} />
        )}
      </div>

      <span style={{ color: "#0B5FFF", fontWeight: 500, fontSize: "14px" }}>
        Click to preview
      </span>
    </a>
  );
};

/** Legacy row layout (list mode without thumbnail box). */
function DocLink({ href, label, titleStyles = {}, pdfSize = 48, labelWidth = 220 }) {
  return (
    <a
      href={href}
      target="_blank"
      rel="noopener noreferrer"
      style={{
        display: "flex",
        alignItems: "center",
        gap: 12,
        textDecoration: "none",
        marginBottom: 12,
        width: "100%",
      }}
    >
      <div style={{ minWidth: labelWidth, fontWeight: 700, color: "#111", ...titleStyles }}>
        {label}
      </div>
      <div style={{ display: "flex", alignItems: "center", gap: 8 }}>
        <LargePdfSvg size={pdfSize} />
        <div style={{ color: "#0B5FFF", textDecoration: "none", fontWeight: 500 }}>
          Click to View File
        </div>
      </div>
    </a>
  );
}

/**
 * ESTDocumnetPreview
 * documents: [{ values: [{ url, title, documentType }] }]
 */
export function ESTDocumnetPreview({
  documents = [],
  titleStyles = {},
  isHrLine = false,
  pdfSize = 48,
  labelWidth = 220,
  useThumbnails = true,
  thumbSize = 72,
}) {
  const { t } = useTranslation();

  const flattened = (documents || []).flatMap((group) =>
    (group.values || []).map((v) => ({
      url: v.url,
      title: t(v.title || v.documentType || "DOCUMENT"),
      documentType: v.documentType,
    }))
  );

  return (
    <div style={{ marginTop: 8, padding: useThumbnails ? "0 16px 12px" : 0 }}>
      {flattened.length > 0 ? (
        flattened.map((val, idx) => (
          <div key={`est-link-${idx}`}>
            {useThumbnails ? (
              <DocThumbnail href={val.url} label={val.title} url={val.url} thumbSize={thumbSize} />
            ) : (
              <DocLink
                href={val.url}
                label={val.title}
                titleStyles={titleStyles}
                pdfSize={pdfSize}
                labelWidth={labelWidth}
              />
            )}
            {isHrLine && idx !== flattened.length - 1 ? (
              <hr
                style={{
                  border: 0,
                  height: 1,
                  backgroundColor: "#E5E5E5",
                  margin: "8px 0 12px",
                }}
              />
            ) : null}
          </div>
        ))
      ) : (
        !window.location.href.includes("citizen") && (
          <div style={{ color: "#666" }}>{t("EST_NO_DOCUMENTS_UPLOADED_LABEL")}</div>
        )
      )}
    </div>
  );
}

export default ESTDocumnetPreview;
