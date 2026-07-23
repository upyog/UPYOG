/**
 * EstateApplication card — citizen My Applications list item.
 * Shows key notes and actions: view summary / make payment.
 * Next payment due date comes from the allotment schedule (no bill prefetch).
 
 */
import React, { useEffect, useMemo, useState } from "react";
import { Card, KeyNote, SubmitBar, Toast } from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import { getApplicationDetailsPath, getCitizenPaymentPath } from "../../../utils/estRoutes";
import {
  formatPaymentDueDate,
  getBillAmountDue,
  getScheduledNextDueDate,
  isPaymentDueTodayOrPast,
} from "../../../utils/paymentDueUtils";
import styles from "../../../styles/ESTMyApplications.module.scss";

const EST_BUSINESS_SERVICE = "est-services";

/** Normalize allotmentType / propertyType from API (string or { code }). */
const toAllotmentTypeCode = (value) => {
  if (value === undefined || value === null || value === "") return "";
  if (typeof value === "object") {
    return String(value.code || value.name || "").trim().toUpperCase();
  }
  return String(value).trim().toUpperCase();
};

const isNoDemandError = (err) => {
  const code =
    err?.response?.data?.Errors?.[0]?.code ||
    err?.Errors?.[0]?.code ||
    "";
  return (
    code === "EG_BS_BILL_NO_DEMANDS_FOUND" ||
    code === "EMPTY_DEMANDS" ||
    String(code).includes("NO_DEMAND")
  );
};

const EstateApplication = ({ application, tenantId }) => {
  const { t } = useTranslation();
  const navigate = Digit.Hooks.useCustomNavigate();
  const { path: modulePath } = Digit.Hooks.useModuleBasePath();

  const [isPaymentLoading, setIsPaymentLoading] = useState(false);
  const [showToast, setShowToast] = useState(null);

  useEffect(() => {
    if (!showToast) return undefined;
    const timer = setTimeout(() => setShowToast(null), 3000);
    return () => clearTimeout(timer);
  }, [showToast]);

  const estateNo = application?.estateNo || application?.assetNo;
  const billTenantId = tenantId || application?.tenantId;
  const canMakePayment = Boolean(billTenantId && estateNo);

  // Same allotment lookup as ESTApplicationDetails (by assetNo = estate number).
  const { data: allotmentData } = Digit.Hooks.estate.useESTApplicationSearch({
    filters: {
      tenantId: billTenantId,
      assetNo: estateNo,
    },
    config: {
      enabled: Boolean(billTenantId && estateNo),
    },
  });

  const allotment = allotmentData?.Allotments?.[0] || null;

  const allotmentTypeLabel = useMemo(() => {
    const code = toAllotmentTypeCode(
      allotment?.allotmentType ??
        allotment?.propertyType ??
        application?.allotmentType ??
        application?.propertyType
    );
    if (code !== "RENT" && code !== "LEASE") return "N/A";
    return t(`EST_ALLOTMENT_TYPE_${code}`);
  }, [allotment, application, t]);

  const billingCycleLabel = useMemo(() => {
    const raw =
      allotment?.billingCycle ?? application?.billingCycle ?? "";
    const code =
      typeof raw === "object"
        ? String(raw.code || raw.name || "").trim().toUpperCase()
        : String(raw).trim().toUpperCase();
    if (!code) return "N/A";
    return t(`EST_BILLING_CYCLE_${code}`);
  }, [allotment, application, t]);

  // Schedule-based due date only — no fetchBill on list load.
  const nextPaymentDueDate = useMemo(
    () =>
      getScheduledNextDueDate(allotment || {}, [], {
        strictlyAfterToday: false,
      }),
    [allotment]
  );

  const nextPaymentDueLabel = useMemo(() => {
    const formatted = formatPaymentDueDate(nextPaymentDueDate);
    return formatted || "N/A";
  }, [nextPaymentDueDate]);

  // Show Make Payment when schedule says due today/past (bill checked on click).
  const showMakePayment = useMemo(() => {
    if (!canMakePayment) return false;
    if (nextPaymentDueDate && !isPaymentDueTodayOrPast(nextPaymentDueDate)) {
      return false;
    }
    // No schedule date yet → still allow click; fetchBill decides if anything is due.
    return true;
  }, [canMakePayment, nextPaymentDueDate]);

  const handleViewSummary = () => {
    navigate(getApplicationDetailsPath(modulePath, estateNo), {
      state: { applicationData: application },
    });
  };

  const handleMakePayment = async () => {
    if (!canMakePayment || isPaymentLoading) return;

    setIsPaymentLoading(true);
    setShowToast(null);

    try {
      const billData = await Digit.PaymentService.fetchBill(billTenantId, {
        consumerCode: estateNo,
        businessService: EST_BUSINESS_SERVICE,
      });
      const amountDue = getBillAmountDue(billData);

      if (amountDue <= 0) {
        setShowToast({ error: true, label: t("CS_BILL_NOT_FOUND") });
        return;
      }

      navigate({
        pathname: getCitizenPaymentPath(estateNo),
        state: { tenantId: billTenantId },
      });
    } catch (err) {
      if (isNoDemandError(err)) {
        setShowToast({ error: true, label: t("CS_BILL_NOT_FOUND") });
        return;
      }
      setShowToast({ error: true, label: t("CS_SOMETHING_WENT_WRONG") });
    } finally {
      setIsPaymentLoading(false);
    }
  };

  return (
    <Card className={styles["est-myapps__card"]}>
      <KeyNote keyValue={t("EST_ASSET_ID")} note={application?.assetId || "N/A"} />
      <KeyNote keyValue={t("EST_ESTATE_NUMBER")} note={estateNo || "N/A"} />
      <KeyNote keyValue={t("EST_BUILDING_NAME")} note={application?.buildingName || application?.assetName || "N/A"} />
      <KeyNote keyValue={t("EST_ALLOTMENT_TYPE")} note={allotmentTypeLabel} />
      <KeyNote keyValue={t("EST_BILLING_CYCLE")} note={billingCycleLabel} />
      <KeyNote keyValue={t("EST_RATE")} note={`₹${application?.rate || 0}`} />
      <KeyNote keyValue={t("EST_ASSET_STATUS")} note={application?.assetStatus || "N/A"} />
      <KeyNote
        keyValue={t("EST_CREATED_DATE")}
        note={
          application?.auditDetails?.createdTime
            ? new Date(application.auditDetails.createdTime).toLocaleDateString("en-GB")
            : "N/A"
        }
      />
      <KeyNote
        keyValue={t("EST_NEXT_PAYMENT_DUE_DATE")}
        note={nextPaymentDueLabel}
      />

      <div className={styles["est-myapps__actions"]}>
        <SubmitBar
          label={t("EST_VIEW_SUMMARY")}
          onSubmit={handleViewSummary}
          className={styles["est-myapps__action-btn"]}
        />
        {showMakePayment ? (
          <SubmitBar
            label={t("EST_MAKE_PAYMENT")}
            onSubmit={handleMakePayment}
            disabled={isPaymentLoading}
            className={styles["est-myapps__action-btn"]}
          />
        ) : null}
      </div>

      {showToast ? (
        <Toast
          error={showToast.error}
          label={showToast.label}
          onClose={() => setShowToast(null)}
        />
      ) : null}
    </Card>
  );
};

export default EstateApplication;
