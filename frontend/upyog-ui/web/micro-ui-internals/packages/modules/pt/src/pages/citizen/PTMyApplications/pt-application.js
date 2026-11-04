import React from "react";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";

const PTApplication = ({ application, tenantId, buttonLabel }) => {
  const { t } = useTranslation();

  const status = application?.status || "INWORKFLOW";
  const statusClass = status.toLowerCase();

  const owners = application?.owners;
  const ownersSequences =
    owners && Array.isArray(owners)
      ? owners.slice().sort((a, b) => (a?.additionalDetails?.ownerSequence || 0) - (b?.additionalDetails?.ownerSequence || 0))
      : [];
  const ownerNames =
    ownersSequences.length > 0
      ? ownersSequences.map((o) => o?.name).filter(Boolean).join(", ")
      : null;

  const address = application?.address;
  const localityName = address?.locality?.name ? t(address.locality.name) : "";
  const cityName = address?.city ? t(address.city) : "";
  const addressStr = [localityName, cityName].filter(Boolean).join(", ");

  const appType = application?.creationReason ? t(`PT.${application.creationReason}`) : t("CS_NA");

  return (
    <div className="pt-citizen-app-card">
      <div className="pt-citizen-app-card-header">
        <div className="pt-citizen-app-card-id-wrap">
          <span className="pt-citizen-app-icon">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" />
              <polyline points="14 2 14 8 20 8" />
              <line x1="16" y1="13" x2="8" y2="13" />
              <line x1="16" y1="17" x2="8" y2="17" />
              <polyline points="10 9 9 9 8 9" />
            </svg>
          </span>
          <div className="pt-citizen-app-id-block">
            <span className="pt-citizen-app-id-label">{t("PT_APPLICATION_NO_LABEL")}</span>
            <span className="pt-citizen-app-id-value">{application?.acknowldgementNumber}</span>
          </div>
        </div>
        <div className="pt-citizen-app-header-right">
          <span className={`pt-citizen-app-status-pill status-${statusClass}`}>
            {t(`PT_COMMON_${status}`)}
          </span>
        </div>
      </div>

      <div className="pt-citizen-app-card-body">
        <div className="pt-citizen-app-info-grid">
          {application?.propertyId && (
            <div className="pt-citizen-app-info-item">
              <span className="pt-citizen-app-info-label">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                  <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z" />
                  <polyline points="9 22 9 12 15 12 15 22" />
                </svg>
                {t("PT_SEARCHPROPERTY_TABEL_PTUID")}
              </span>
              <span className="pt-citizen-app-info-value">{application.propertyId}</span>
            </div>
          )}

          <div className="pt-citizen-app-info-item">
            <span className="pt-citizen-app-info-label">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <rect x="2" y="7" width="20" height="14" rx="2" ry="2" />
                <path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16" />
              </svg>
              {t("PT_COMMON_TABLE_COL_APP_TYPE")}
            </span>
            <span className="pt-citizen-app-info-value">{appType}</span>
          </div>

          {ownerNames && (
            <div className="pt-citizen-app-info-item">
              <span className="pt-citizen-app-info-label">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                  <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
                  <circle cx="12" cy="7" r="4" />
                </svg>
                {t("PT_COMMON_TABLE_COL_OWNER_NAME")}
              </span>
              <span className="pt-citizen-app-info-value">{ownerNames}</span>
            </div>
          )}

          {addressStr && (
            <div className="pt-citizen-app-info-item">
              <span className="pt-citizen-app-info-label">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                  <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z" />
                  <circle cx="12" cy="10" r="3" />
                </svg>
                {t("PT_COMMON_COL_ADDRESS")}
              </span>
              <span className="pt-citizen-app-info-value">{addressStr}</span>
            </div>
          )}
        </div>
      </div>

      <div className="pt-citizen-app-card-actions">
        <Link
          to={`/upyog-ui/citizen/pt/property/application/${application?.acknowldgementNumber}/${application?.tenantId}`}
          className="pt-citizen-app-action-btn"
        >
          {buttonLabel || t("CS_CF_TRACK")}
        </Link>
      </div>
    </div>
  );
};

export default PTApplication;
