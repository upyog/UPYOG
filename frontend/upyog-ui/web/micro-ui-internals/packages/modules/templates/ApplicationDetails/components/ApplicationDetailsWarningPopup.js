import { Modal } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";

const WarningAlertIcon = () => (
  <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.3" strokeLinecap="round" strokeLinejoin="round">
    <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z" />
    <line x1="12" y1="9" x2="12" y2="13" />
    <line x1="12" y1="17" x2="12.01" y2="17" />
  </svg>
);

const CloseIcon = () => (
  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
    <line x1="18" y1="6" x2="18" y2="18" />
    <line x1="6" y1="6" x2="18" y2="18" />
  </svg>
);

const CardPaymentIcon = () => (
  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round">
    <rect x="2" y="5" width="20" height="14" rx="2" />
    <line x1="2" y1="10" x2="22" y2="10" />
  </svg>
);

const CloseBtn = ({ onClick }) => (
  <button
    type="button"
    className="app-details-warning-close-btn"
    onClick={onClick}
    aria-label="Close"
  >
    <CloseIcon />
  </button>
);

function ApplicationDetailsWarningPopup({ action, workflowDetails, businessService, isWarningPop, closeWarningPopup }) {
  const { t } = useTranslation();
  const isMobile = window.Digit.Utils.browser.isMobile();

  const formattedAmount =
    action?.AmountDueForPay !== undefined && action?.AmountDueForPay !== null
      ? typeof action.AmountDueForPay === "number" || (!isNaN(action.AmountDueForPay) && action.AmountDueForPay !== "")
        ? Number(action.AmountDueForPay).toLocaleString("en-IN")
        : action.AmountDueForPay
      : "0";

  return (
    <Modal
      headerBarMain={
        <div className="app-details-warning-header-content">
          <div className="app-details-warning-header-badge">
            <WarningAlertIcon />
          </div>
          <div className="app-details-warning-header-text">
            <h2 className="app-details-warning-header-title">{t("PT_DUES_ARE_PENDING")}</h2>
            <span className="app-details-warning-header-sub">{t("CS_ACTION_REQUIRED") || "Action Required"}</span>
          </div>
        </div>
      }
      headerBarEnd={<CloseBtn onClick={closeWarningPopup} />}
      headerBarMainStyle={{
        padding: "18px 22px 14px 22px",
        borderBottom: "1px solid #F1F5F9",
        backgroundColor: "#FFFFFF",
        display: "flex",
        alignItems: "center",
        justifyContent: "space-between",
      }}
      popupModuleMianStyles={{
        padding: isMobile ? "16px 16px 20px 16px" : "20px 24px 24px 24px",
        backgroundColor: "#FFFFFF",
      }}
      hideSubmit={true}
      isDisabled={false}
      popupStyles={
        isMobile
          ? {
              width: "92%",
              maxWidth: "460px",
              margin: "auto",
              borderRadius: "16px",
              overflow: "hidden",
              boxShadow: "0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04)",
              border: "1px solid #E2E8F0",
            }
          : {
              width: "480px",
              maxWidth: "92vw",
              margin: "auto",
              borderRadius: "16px",
              overflow: "hidden",
              boxShadow: "0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04)",
              border: "1px solid #E2E8F0",
            }
      }
    >
      <div className="app-details-warning-popup-container">
        <div className="app-details-warning-notice-box">
          <div className="app-details-warning-notice-icon-wrap">
            <span className="app-details-warning-pulse-dot" />
          </div>
          <p className="app-details-warning-notice-text">
            {t("PT_YOU_HAVE")}{" "}
            <strong className="app-details-warning-due-highlight">
              ₹{formattedAmount}
            </strong>{" "}
            {t("PT_DUE_WARNING_MSG2")}
          </p>
        </div>

        <div className="app-details-warning-amount-card">
          <div className="app-details-warning-amount-top">
            <span className="app-details-warning-amount-label">{t("PT_AMOUNT_DUE")}</span>
            <span className="app-details-warning-status-pill">{t("COMMON_PENDING") || "Pending"}</span>
          </div>
          <div className="app-details-warning-amount-value">
            <span className="app-details-warning-currency">₹</span>
            <span className="app-details-warning-number">{formattedAmount}</span>
          </div>
          <div className="app-details-warning-amount-note">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <circle cx="12" cy="12" r="10" />
              <line x1="12" y1="16" x2="12" y2="12" />
              <line x1="12" y1="8" x2="12.01" y2="8" />
            </svg>
            <span>{t("PT_CLEAR_DUES_INSTRUCTION") || "Please clear all outstanding property dues before processing this application."}</span>
          </div>
        </div>

        <div className="app-details-warning-actions">
          <button
            type="button"
            className="app-details-warning-btn app-details-warning-btn-cancel"
            onClick={closeWarningPopup}
          >
            {t("ES_PT_COMMON_CANCEL")}
          </button>
          <button
            type="button"
            className="app-details-warning-btn app-details-warning-btn-collect"
            onClick={() => window.location.assign(`${window.location.origin}${action?.redirectionUrl?.pathname}`)}
          >
            <CardPaymentIcon />
            <span>{t("PT_COLLECT")}</span>
          </button>
        </div>
      </div>
    </Modal>
  );
}

export default ApplicationDetailsWarningPopup;