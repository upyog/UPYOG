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
import styles from "../styles/ESTDocumentPreview.module.scss";

const LargePdfSvg = ({ size = 48 }) => (
  <svg
    width={size}
    height={size}
    viewBox="0 0 24 24"
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
    aria-hidden
    className={styles.pdfIcon}
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
    <a href="#" role="button" onClick={handleClick} className={styles.docThumbnail}>
      <div className={styles.meta}>
        <div className={styles.label}>{label}</div>
        {displayRef ? <div className={styles.reference}>{displayRef}</div> : null}
      </div>

      <div className={styles.thumb} style={{ width: thumbSize, height: thumbSize }}>
        <LargePdfSvg size={Math.min(thumbSize, 48)} />
      </div>

      <span className={styles.previewHint}>Click to preview</span>
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
    <a href="#" role="button" onClick={handleClick} className={styles.docLink}>
      <div
        className={styles.meta}
        style={{ ["--est-doc-label-width"]: `${labelWidth}px`, ...titleStyles }}
      >
        <div className={styles.label}>{label}</div>
        {displayRef ? <div className={styles.reference}>{displayRef}</div> : null}
      </div>
      <div className={styles.action}>
        <LargePdfSvg size={pdfSize} />
        <div className={styles.viewHint}>Click to View File</div>
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

  const rootClass = [
    styles.estDocumentPreview,
    useThumbnails ? styles["estDocumentPreview--withThumbnails"] : "",
  ]
    .filter(Boolean)
    .join(" ");

  return (
    <div className={rootClass}>
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
              <hr className={styles.divider} />
            ) : null}
          </div>
        ))
      ) : (
        !window.location.href.includes("citizen") && (
          <div className={styles.emptyState}>{t("EST_NO_DOCUMENTS_UPLOADED_LABEL")}</div>
        )
      )}
    </div>
  );
}

export default ESTDocumnetPreview;
