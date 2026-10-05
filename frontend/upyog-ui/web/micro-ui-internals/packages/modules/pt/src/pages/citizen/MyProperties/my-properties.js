import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";

const MyProperty = ({ application }) => {
  const { t } = useTranslation();
  const address = application?.address;
  const owners = application?.owners;
  const [billData, setBillData] = useState(null);
  const [loading, setLoading] = useState(false);

  const fetchBillData = async () => {
    setLoading(true);
    try {
      const result = await Digit.PaymentService.fetchBill(application.tenantId, {
        businessService: "PT",
        consumerCode: application.propertyId,
      });
      setBillData(result);
    } catch (e) {
      console.warn("Error fetching bill data", e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchBillData();
  }, [application.tenantId, application.propertyId]);

  sessionStorage.removeItem("type");
  sessionStorage.removeItem("pincode");
  sessionStorage.removeItem("tenantId");
  sessionStorage.removeItem("localityCode");
  sessionStorage.removeItem("landmark");
  sessionStorage.removeItem("propertyid");

  const ownersSequences =
    owners && Array.isArray(owners)
      ? owners.slice().sort((a, b) => (a?.additionalDetails?.ownerSequence || 0) - (b?.additionalDetails?.ownerSequence || 0))
      : [];

  const ownerNames =
    ownersSequences.length > 0
      ? ownersSequences.map((o) => o?.name).filter(Boolean).join(", ")
      : t("CS_NA");

  const localityName = address?.locality?.name ? t(address.locality.name) : "";
  const cityName = address?.city ? t(address.city) : "";
  const pincode = address?.pincode ? address.pincode : "";
  const fullAddress =
    [address?.doorNo, address?.street, localityName, cityName, pincode].filter(Boolean).join(", ") ||
    (address?.locality?.name ? `${t(address?.locality?.name)}, ${t(address?.city)}` : t("CS_NA"));

  const status = application?.status || "ACTIVE";
  const statusClass = status.toLowerCase();

  const totalDue = billData?.Bill?.[0]?.totalAmount;
  const hasDue = totalDue !== undefined && totalDue !== null && totalDue > 0;
  const isPaid = totalDue !== undefined && totalDue !== null && totalDue === 0;

  return (
    <div className="pt-citizen-prop-card">
      <div className="pt-citizen-prop-card-header">
        <div className="pt-citizen-prop-card-id-wrap">
          <span className="pt-citizen-prop-icon">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z" />
              <polyline points="9 22 9 12 15 12 15 22" />
            </svg>
          </span>
          <div className="pt-citizen-prop-id-block">
            <span className="pt-citizen-prop-id-label">{t("PT_PROPERTY_PTUID")}</span>
            <span className="pt-citizen-prop-id-value">{application?.propertyId}</span>
          </div>
        </div>
        <div className="pt-citizen-prop-header-right">
          <span className={`pt-citizen-prop-status-pill status-${statusClass}`}>
            {t("PT_COMMON_" + status)}
          </span>
        </div>
      </div>

      <div className="pt-citizen-prop-card-body">
        <div className="pt-citizen-prop-info-grid">
          <div className="pt-citizen-prop-info-item">
            <span className="pt-citizen-prop-info-label">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
                <circle cx="12" cy="7" r="4" />
              </svg>
              {t("PT_COMMON_TABLE_COL_OWNER_NAME")}
            </span>
            <span className="pt-citizen-prop-info-value">{ownerNames}</span>
          </div>

          <div className="pt-citizen-prop-info-item">
            <span className="pt-citizen-prop-info-label">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z" />
                <circle cx="12" cy="10" r="3" />
              </svg>
              {t("PT_COMMON_COL_ADDRESS")}
            </span>
            <span className="pt-citizen-prop-info-value">{fullAddress}</span>
          </div>

          <div className="pt-citizen-prop-info-item pt-due-item">
            <span className="pt-citizen-prop-info-label">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <line x1="12" y1="1" x2="12" y2="23" />
                <path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6" />
              </svg>
              {t("CS_COMMON_TOTAL_AMOUNT_DUE")}
            </span>
            <span className="pt-citizen-prop-info-value">
              {loading ? (
                <span className="pt-prop-amount-loading">{t("CS_COMMON_LOADING")}...</span>
              ) : hasDue ? (
                <span className="pt-prop-amount-pill due">₹{totalDue}</span>
              ) : isPaid ? (
                <span className="pt-prop-amount-pill paid">{t("PT_PAID_STATUS") || "₹0 (No Dues)"}</span>
              ) : (
                <span className="pt-prop-amount-pill na">{t("CS_NA")}</span>
              )}
            </span>
          </div>
        </div>
      </div>

      <div className="pt-citizen-prop-card-actions">
        <Link
          to={`/upyog-ui/citizen/pt/property/properties/${application?.propertyId}`}
          className="pt-citizen-action-btn view-details"
        >
          {t("PT_VIEW_DETAILS")}
        </Link>
        {billData?.Bill?.length > 0 && totalDue > 0 ? (
          <Link
            to={`/upyog-ui/citizen/payment/my-bills/PT/${application?.propertyId}`}
            className="pt-citizen-action-btn make-payment"
          >
            {t("COMMON_MAKE_PAYMENT")}
          </Link>
        ) : null}
      </div>
    </div>
  );
};

export default MyProperty;
