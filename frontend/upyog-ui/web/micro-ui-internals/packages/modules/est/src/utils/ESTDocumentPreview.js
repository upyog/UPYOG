/**
 * ESTDocumentPreview.js
 * Document preview with clickable thumbnails for check + acknowledgement pages.
 */

import React from "react";
import { useTranslation } from "react-i18next";
import {
  extractUrlFromFilefetchResponse,
  resolveFilePreviewUrl,
} from "@nudmcdgnpm/digit-ui-react-components";

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

const openFilePreview = async (fileStoreId, previewUrl = "") => {
  const resolved = resolveFilePreviewUrl(previewUrl);
  if (resolved) {
    window.open(resolved, "_blank", "noopener,noreferrer");
    return;
  }

  if (!fileStoreId) return;

  const tenantIds = [
    Digit.ULBService.getCurrentTenantId(),
    Digit.ULBService.getStateId(),
  ].filter(Boolean);

  for (const tenantId of [...new Set(tenantIds)]) {
    try {
      const res = await Digit.UploadServices.Filefetch([fileStoreId], tenantId);
      const url = extractUrlFromFilefetchResponse(res, fileStoreId);
      if (url) {
        window.open(url, "_blank", "noopener,noreferrer");
        return;
      }
    } catch (err) {
      console.error("EST document preview fetch failed:", err);
    }
  }
};

const DocThumbnail = ({
  label,
  url,
  thumbSize = 72,
  fileStoreId,
  reference,
  fileName,
}) => {
  const displayRef = fileName || reference || fileStoreId;

  const handleClick = (e) => {
    e.preventDefault();
    openFilePreview(fileStoreId, url);
  };

  return (
    <a
      href="#"
      role="button"
      onClick={handleClick}
      style={{
        display: "flex",
        alignItems: "center",
        gap: 16,
        textDecoration: "none",
        marginBottom: 16,
        width: "100%",
        padding: "8px 0",
        cursor: "pointer",
      }}
    >
      <div style={{ flex: 1, minWidth: 160 }}>
        <div style={{ fontWeight: 700, color: "#111", fontSize: "14px" }}>{label}</div>
        {displayRef ? (
          <div style={{ fontSize: "12px", color: "#666", marginTop: 4, wordBreak: "break-all" }}>
            {displayRef}
          </div>
        ) : null}
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
        <LargePdfSvg size={Math.min(thumbSize, 48)} />
      </div>

      <span style={{ color: "#0B5FFF", fontWeight: 500, fontSize: "14px" }}>
        Click to preview
      </span>
    </a>
  );
};

function DocLink({
  label,
  url,
  titleStyles = {},
  pdfSize = 48,
  labelWidth = 220,
  fileStoreId,
  reference,
  fileName,
}) {
  const displayRef = fileName || reference || fileStoreId;

  const handleClick = (e) => {
    e.preventDefault();
    openFilePreview(fileStoreId, url);
  };

  return (
    <a
      href="#"
      role="button"
      onClick={handleClick}
      style={{
        display: "flex",
        alignItems: "center",
        gap: 12,
        textDecoration: "none",
        marginBottom: 12,
        width: "100%",
        cursor: "pointer",
      }}
    >
      <div style={{ minWidth: labelWidth, ...titleStyles }}>
        <div style={{ fontWeight: 700, color: "#111" }}>{label}</div>
        {displayRef ? (
          <div style={{ fontSize: "12px", color: "#666", marginTop: 4, wordBreak: "break-all" }}>
            {displayRef}
          </div>
        ) : null}
      </div>
      <div style={{ display: "flex", alignItems: "center", gap: 8 }}>
        <LargePdfSvg size={pdfSize} />
        <div style={{ color: "#0B5FFF", fontWeight: 500 }}>Click to View File</div>
      </div>
    </a>
  );
}

/**
 * ESTDocumnetPreview
 * documents: [{ values: [{ url, title, documentType, fileStoreId }] }]
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
      fileStoreId: v.fileStoreId || v.documentType,
      reference: v.reference || v.fileStoreId || v.documentType,
      fileName: v.fileName,
    }))
  );

  return (
    <div style={{ marginTop: 8, padding: useThumbnails ? "0 16px 12px" : 0 }}>
      {flattened.length > 0 ? (
        flattened.map((val, idx) => (
          <div key={`est-link-${idx}`}>
            {useThumbnails ? (
              <DocThumbnail
                label={val.title}
                url={val.url}
                thumbSize={thumbSize}
                fileStoreId={val.fileStoreId}
                reference={val.reference}
                fileName={val.fileName}
              />
            ) : (
              <DocLink
                label={val.title}
                url={val.url}
                titleStyles={titleStyles}
                pdfSize={pdfSize}
                labelWidth={labelWidth}
                fileStoreId={val.fileStoreId}
                reference={val.reference}
                fileName={val.fileName}
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
