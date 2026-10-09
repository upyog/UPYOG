import React, { useEffect } from "react";
import { useTranslation } from "react-i18next";

const BillSumary = ({ billAccountDetails, total, businessService, arrears, skipArrears }) => {
  const { t } = useTranslation();
  const { workflow: ModuleWorkflow } = Digit.Hooks.useQueryParams();

  useEffect(() => {
    ModuleWorkflow === "mcollect" &&
      billAccountDetails &&
      billAccountDetails.map((ob) => {
        if (ob.taxHeadCode.includes("CGST")) ob.order = 3;
        else if (ob.taxHeadCode.includes("SGST")) ob.order = 4;
      });
  }, [billAccountDetails]);

  return (
    <div className="cmn-bill-breakdown-card">
      <div className="cmn-bill-breakdown-title">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#64748b" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
          <rect x="2" y="4" width="20" height="16" rx="2" />
          <line x1="2" y1="10" x2="22" y2="10" />
        </svg>
        {t("CS_BILL_BREAKDOWN_HEADER") || t("CS_PAYMENT_BILL_DETAILS")}
      </div>
      {billAccountDetails
        .sort((a, b) => a.order - b.order)
        .map((amountDetails, index) => {
          return (
            <div key={index} className="cmn-bill-line-item">
              <div className="line-label">{t(amountDetails.taxHeadCode)}</div>
              <div className="line-value">₹ {Math.abs(amountDetails?.amount?.toFixed(2))}</div>
            </div>
          );
        })}

      {!skipArrears.includes(businessService) && (
        <div className="cmn-bill-line-item arrears-item">
          <div className="line-label">{t("COMMON_ARREARS")}</div>
          <div className="line-value">₹ {Math.abs(arrears?.toFixed?.(2) || Number(0).toFixed(2))}</div>
        </div>
      )}

      <hr className="cmn-bill-divider" />
      <div className="cmn-bill-total-row">
        <div className="total-label">{t("CS_PAYMENT_TOTAL_AMOUNT")}</div>
        <div className="total-amount">₹ {Number(total).toFixed(2)}</div>
      </div>
    </div>
  );
};

export default BillSumary;

