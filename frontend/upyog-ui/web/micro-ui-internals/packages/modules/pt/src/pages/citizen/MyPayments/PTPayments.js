import React from "react";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";
import { convertEpochToDate, DownloadReceipt } from "../../../utils/index";

const PTPayments = ({ application }) => {
  const { t } = useTranslation();
  const state = Digit.ULBService.getStateId();
  const paymentDetail = application?.paymentDetails?.[0];
  const propertyId = paymentDetail?.bill?.consumerCode;
  const receiptNumber = paymentDetail?.receiptNumber;
  const totalAmountPaid = paymentDetail?.totalAmountPaid;
  const receiptDate = convertEpochToDate(paymentDetail?.receiptDate) || t("CS_NA");

  const ownerNames =
    application?.owners && Array.isArray(application.owners) && application.owners.length > 0
      ? application.owners.map((owner) => owner?.name).filter(Boolean).join(", ")
      : t("CS_NA");

  const handleDownload = () => {
    DownloadReceipt(propertyId, state, "PT", receiptNumber, application);
  };

  return (
    <div className="pt-citizen-payment-card">
      <div className="pt-citizen-payment-card-header">
        <div className="pt-citizen-payment-id-wrap">
          <span className="pt-citizen-payment-icon">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" />
              <polyline points="14 2 14 8 20 8" />
              <line x1="16" y1="13" x2="8" y2="13" />
              <line x1="16" y1="17" x2="8" y2="17" />
              <polyline points="10 9 9 9 8 9" />
            </svg>
          </span>
          <div className="pt-citizen-payment-id-block">
            <span className="pt-citizen-payment-id-label">{t("PT_RECEIPT_NO_LABEL")}</span>
            <span className="pt-citizen-payment-id-value">{receiptNumber || t("CS_NA")}</span>
          </div>
        </div>
        <div className="pt-citizen-payment-amount-block">
          <span className="pt-citizen-payment-amount-val">₹{totalAmountPaid !== undefined ? totalAmountPaid : 0}</span>
          <span className="pt-citizen-payment-status-pill">{t("CS_PAID") || "PAID"}</span>
        </div>
      </div>

      <div className="pt-citizen-payment-card-body">
        <div className="pt-citizen-payment-info-grid">
          {propertyId && (
            <div className="pt-citizen-payment-info-item">
              <span className="pt-citizen-payment-info-label">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                  <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z" />
                  <polyline points="9 22 9 12 15 12 15 22" />
                </svg>
                {t("PT_SEARCHPROPERTY_TABEL_PID")}
              </span>
              <span className="pt-citizen-payment-info-value">{propertyId}</span>
            </div>
          )}

          <div className="pt-citizen-payment-info-item">
            <span className="pt-citizen-payment-info-label">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
                <circle cx="12" cy="7" r="4" />
              </svg>
              {t("PT_OWNERS_NAME")}
            </span>
            <span className="pt-citizen-payment-info-value">{ownerNames}</span>
          </div>

          <div className="pt-citizen-payment-info-item">
            <span className="pt-citizen-payment-info-label">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <rect x="3" y="4" width="18" height="18" rx="2" ry="2" />
                <line x1="16" y1="2" x2="16" y2="6" />
                <line x1="8" y1="2" x2="8" y2="6" />
                <line x1="3" y1="10" x2="21" y2="10" />
              </svg>
              {t("PT_RECEIPT_DATE_LABEL")}
            </span>
            <span className="pt-citizen-payment-info-value">{receiptDate}</span>
          </div>

          {application?.paymentMode && (
            <div className="pt-citizen-payment-info-item">
              <span className="pt-citizen-payment-info-label">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                  <rect x="1" y="4" width="22" height="16" rx="2" ry="2" />
                  <line x1="1" y1="10" x2="23" y2="10" />
                </svg>
                {t("PT_PAYMENT_MODE")}
              </span>
              <span className="pt-citizen-payment-info-value">{t(`PAYMENT_METHOD_${application.paymentMode}`) || application.paymentMode}</span>
            </div>
          )}
        </div>
      </div>

      <div className="pt-citizen-payment-card-actions">
        {propertyId && (
          <Link
            to={`/upyog-ui/citizen/pt/property/properties/${propertyId}`}
            className="pt-citizen-payment-action-btn btn-view-prop"
          >
            {t("PT_VIEW_DETAILS")}
          </Link>
        )}
        <button
          type="button"
          className="pt-citizen-payment-action-btn btn-download-receipt"
          onClick={handleDownload}
        >
          <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
            <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4" />
            <polyline points="7 10 12 15 17 10" />
            <line x1="12" y1="15" x2="12" y2="3" />
          </svg>
          {t("PT_DOWNLOAD_RECEIPT")}
        </button>
      </div>
    </div>
  );
};

export default PTPayments;
