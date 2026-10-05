import { Header, Loader } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import PTPayments from "./PTPayments";

export const PTMyPayments = () => {
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const state = Digit.ULBService.getStateId();
  const result = Digit.Hooks.pt.usePropertySearch({});
  const consumerCode = result?.data?.Properties?.map((a) => a.propertyId).join(",");

  const { data, isLoading, error } = Digit.Hooks.pt.useMyPropertyPayments(
    {
      tenantId: state,
      filters: {
        consumerCodes: consumerCode,
      },
    },
    {
      enabled: result?.data?.Properties?.length > 0 ? true : false,
      propertyData: result?.data?.Properties,
    }
  );

  if (isLoading || result?.isLoading) {
    return <Loader />;
  }

  const applicationsList = (data && data?.Payments) || [];

  return (
    <div className="pt-citizen-my-payments-container">
      <div className="pt-citizen-my-payments-header-wrap">
        <div className="pt-citizen-my-payments-title-block">
          <Header className="pt-citizen-my-payments-title">
            {t("PT_MY_PAYMENTS_HEADER")}
          </Header>
          {applicationsList && (
            <span className="pt-citizen-my-payments-count-badge">
              {applicationsList.length} {applicationsList.length === 1 ? t("PT_RECEIPT_LABEL") || "Receipt" : t("PT_RECEIPTS_LABEL") || "Receipts"}
            </span>
          )}
        </div>
        <Link
          to="/upyog-ui/citizen/pt/property/my-properties"
          className="pt-citizen-pay-property-btn"
        >
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
            <rect x="2" y="5" width="20" height="14" rx="2" />
            <line x1="2" y1="10" x2="22" y2="10" />
          </svg>
          <span>{t("PT_VIEW_MY_PROPERTIES_BTN") || t("PT_MY_PROPERTIES_HEADER")}</span>
        </Link>
      </div>

      {applicationsList?.length > 0 ? (
        <div className="pt-citizen-payments-grid">
          {applicationsList.map((application, index) => (
            <PTPayments key={application?.paymentDetails?.[0]?.receiptNumber || index} application={application} />
          ))}
        </div>
      ) : (
        <div className="pt-citizen-empty-payments-card">
          <div className="pt-citizen-empty-icon-wrap">
            <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="#94a3b8" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round">
              <rect x="2" y="5" width="20" height="14" rx="2" />
              <line x1="2" y1="10" x2="22" y2="10" />
            </svg>
          </div>
          <h3 className="pt-citizen-empty-title">{t("PT_NO_APPLICATION_FOUND_MSG")}</h3>
          <p className="pt-citizen-empty-subtitle">{t("PT_NO_PAYMENTS_FOUND_SUBTITLE") || "No payment receipts found for your registered properties."}</p>
          <Link
            to="/upyog-ui/citizen/pt/property/my-properties"
            className="pt-citizen-pay-property-btn"
          >
            {t("PT_MY_PROPERTIES_HEADER")}
          </Link>
        </div>
      )}
    </div>
  );
};

