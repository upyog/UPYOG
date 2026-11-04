import React, { useState, useMemo } from "react";
import { useTranslation } from "react-i18next";

const getModuleIcon = (service = "") => {
  const s = (service || "").toLowerCase();
  if (s.includes("chb")) return "🏛️";
  if (s.includes("pt") || s.includes("property")) return "🏠";
  if (s.includes("adv")) return "📣";
  if (s.includes("pet") || s.includes("ptr")) return "🐾";
  if (s.includes("bpa") || s.includes("obps")) return "🏗️";
  if (s.includes("ws") || s.includes("water") || s.includes("sw")) return "💧";
  if (s.includes("fsm")) return "🚛";
  if (s.includes("tl") || s.includes("trade")) return "📜";
  if (s.includes("fire")) return "🚒";
  if (s.includes("gc") || s.includes("garbage")) return "🗑️";
  if (s.includes("challan")) return "🧾";
  if (s.includes("est")) return "🏢";
  if (s.includes("ndc")) return "📄";
  return "📋";
};

const getLabel = (t, key, fallback) => {
  if (!key) return fallback;
  const translated = t(key);
  if (!translated || translated === key) return fallback;
  return translated;
};

export const DueRenewalsModal = ({ isOpen, onClose, bills = [], tenantId, navigate }) => {
  const { t } = useTranslation();
  const [searchTerm, setSearchTerm] = useState("");

  const totalAmount = useMemo(() => {
    return bills.reduce((acc, b) => acc + (parseFloat(b?.totalAmount) || 0), 0);
  }, [bills]);

  const filteredBills = useMemo(() => {
    if (!searchTerm.trim()) return bills;
    const term = searchTerm.toLowerCase();
    return bills.filter((b) => {
      const svc = (b?.businessService || "").toLowerCase();
      const code = (b?.consumerCode || "").toLowerCase();
      const num = (b?.billNumber || "").toLowerCase();
      return svc.includes(term) || code.includes(term) || num.includes(term);
    });
  }, [bills, searchTerm]);

  if (!isOpen) return null;

  const handlePay = (bill) => {
    onClose();
    const resolvedTenant = bill?.tenantId || tenantId || Digit?.ULBService?.getCitizenCurrentTenant(true);
    if (bill?.redirectUrl) {
      const baseUrl = bill.redirectUrl.replace("/digit-ui/", "/upyog-ui/");
      if (!resolvedTenant || /[?&]tenantId=/.test(baseUrl)) {
        navigate(baseUrl);
      } else {
        const separator = baseUrl.includes("?") ? "&" : "?";
        navigate(`${baseUrl}${separator}tenantId=${encodeURIComponent(resolvedTenant)}`);
      }
    } else if (bill?.consumerCode && bill?.businessService) {
      navigate(`/upyog-ui/citizen/payment/pay?consumerCode=${bill.consumerCode}&tenantId=${resolvedTenant}&businessService=${bill.businessService}`);
    }
  };

  return (
    <div className="due-renewals-modal-overlay" onClick={onClose}>
      <div className="due-renewals-modal-content" onClick={(e) => e.stopPropagation()}>
        <div className="due-renewals-modal-header">
          <div className="due-renewals-modal-title-group">
            <h2 className="due-renewals-modal-title">
              {getLabel(t, "CITIZEN_DUE_RENEWALS", "Due Renewals")}
            </h2>
            <span className="due-renewals-modal-count-badge">{bills.length}</span>
          </div>
          <button
            type="button"
            className="due-renewals-modal-close-btn"
            onClick={onClose}
            aria-label="Close"
          >
            ✕
          </button>
        </div>

        <div className="due-renewals-summary-card">
          <div className="due-renewals-summary-left">
            <span className="due-renewals-summary-label">
              {getLabel(t, "TOTAL_AMOUNT_DUE", "Total Amount Due")}
            </span>
            <span className="due-renewals-summary-amount">
              ₹ {totalAmount.toLocaleString("en-IN")}
            </span>
          </div>
          <div className="due-renewals-summary-right">
            <span className="due-renewals-summary-count">
              {bills.length} {bills.length === 1 ? getLabel(t, "BILL_DUE", "Bill Due") : getLabel(t, "BILLS_DUE", "Bills Due")}
            </span>
          </div>
        </div>

        {bills.length > 3 && (
          <div className="due-renewals-search-wrap">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#64748b" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <circle cx="11" cy="11" r="8" />
              <line x1="21" y1="21" x2="16.65" y2="16.65" />
            </svg>
            <input
              type="text"
              className="due-renewals-search-input"
              placeholder={getLabel(t, "SEARCH_BILLS_PLACEHOLDER", "Search by service or consumer code...")}
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
            {searchTerm && (
              <button
                type="button"
                className="due-renewals-search-clear"
                onClick={() => setSearchTerm("")}
              >
                ✕
              </button>
            )}
          </div>
        )}

        <div className="due-renewals-items-list">
          {filteredBills.length > 0 ? (
            filteredBills.map((bill, index) => {
              const dateStr = bill?.dueDate
                ? new Date(bill.dueDate).toLocaleDateString("en-IN", {
                    day: "2-digit",
                    month: "short",
                    year: "numeric",
                  })
                : "";
              const svcLabel = bill?.businessService
                ? getLabel(t, bill.businessService.toUpperCase(), bill.businessService)
                : getLabel(t, "BILL_PAYMENT", "Bill Payment");

              return (
                <div key={`${bill?.consumerCode || bill?.billNumber || index}-${index}`} className="due-renewals-item-card">
                  <div className="due-renewals-item-main">
                    <div className="due-renewals-item-icon">
                      {getModuleIcon(bill?.businessService)}
                    </div>
                    <div className="due-renewals-item-info">
                      <div className="due-renewals-item-title-row">
                        <span className="due-renewals-item-title">{svcLabel}</span>
                        {bill?.businessService && (
                          <span className="due-renewals-item-badge">{bill.businessService}</span>
                        )}
                      </div>
                      <div className="due-renewals-item-subtitle">
                        {bill?.consumerCode || bill?.billNumber || ""}
                      </div>
                      {dateStr && (
                        <div className="due-renewals-item-due-date">
                          <span>{getLabel(t, "DUE_DATE", "Due Date")}:</span> {dateStr}
                        </div>
                      )}
                    </div>
                  </div>

                  <div className="due-renewals-item-action-group">
                    <div className="due-renewals-item-amount-wrap">
                      <span className="due-renewals-item-amount-label">
                        {getLabel(t, "AMOUNT_DUE", "Amount Due")}
                      </span>
                      <span className="due-renewals-item-amount">
                        ₹ {(bill?.totalAmount !== undefined ? bill.totalAmount : 0).toLocaleString("en-IN")}
                      </span>
                    </div>
                    <button
                      type="button"
                      className="due-renewals-pay-btn"
                      onClick={() => handlePay(bill)}
                    >
                      {getLabel(t, "PAY_NOW", "Pay Now")}
                    </button>
                  </div>
                </div>
              );
            })
          ) : (
            <div className="due-renewals-empty-state">
              <p>{getLabel(t, "NO_DUE_BILLS_FOUND", "No due renewals or bills match your search.")}</p>
            </div>
          )}
        </div>

        <div className="due-renewals-modal-footer">
          <button
            type="button"
            className="due-renewals-close-action-btn"
            onClick={onClose}
          >
            {getLabel(t, "COMMON_CLOSE", "Close")}
          </button>
        </div>
      </div>
    </div>
  );
};

export default DueRenewalsModal;
