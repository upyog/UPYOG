/**
 * EstateApplication card — citizen My Applications list item.
 * Shows key notes and actions: view summary / make payment.
 * Next payment due date is read from the backend (duePaymentDate).
 * fetchBill runs only when the user clicks EST_MAKE_PAYMENT.
 */
import React, { useEffect, useMemo, useState } from "react";
import { Card, Row, StatusTable, SubmitBar, Toast } from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import { getApplicationDetailsPath, getCitizenPaymentPath } from "../../../utils/estRoutes";
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

const getBillAmountDue = (billData) => {
  const bill = billData?.Bill?.[0];
  if (!bill) return 0;
  const total = Number(bill.totalAmount);
  if (Number.isFinite(total)) return total;
  const details = bill.billDetails || [];
  return details.reduce((sum, d) => sum + (Number(d?.amount) || 0), 0);
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

/** Parse backend due date (dd-MM-yyyy, dd/MM/yyyy, ISO, or epoch ms/s). */
const parseBackendDueDate = (value) => {
  if (value === null || value === undefined || value === "") return null;
  if (value instanceof Date) {
    return Number.isNaN(value.getTime()) ? null : value;
  }
  if (typeof value === "number" || /^\d+$/.test(String(value).trim())) {
    let num = Number(value);
    if (String(Math.trunc(num)).length === 10) num *= 1000;
    const d = new Date(num);
    return Number.isNaN(d.getTime()) ? null : d;
  }
  const raw = String(value).trim();
  const dmy = raw.match(/^(\d{2})[/-](\d{2})[/-](\d{4})$/);
  if (dmy) return new Date(+dmy[3], +dmy[2] - 1, +dmy[1]);
  const d = new Date(raw);
  return Number.isNaN(d.getTime()) ? null : d;
};

const formatBackendDueDate = (value) => {
  if (value === null || value === undefined || value === "") return "";
  // Already a display string from backend (dd-MM-yyyy / dd/MM/yyyy).
  if (typeof value === "string" && /^\d{2}[/-]\d{2}[/-]\d{4}$/.test(value.trim())) {
    return value.trim().replace(/-/g, "/");
  }
  const d = parseBackendDueDate(value);
  if (!d) return "";
  const dd = String(d.getDate()).padStart(2, "0");
  const mm = String(d.getMonth() + 1).padStart(2, "0");
  return `${dd}/${mm}/${d.getFullYear()}`;
};

const isPaymentDueTodayOrPast = (value) => {
  const due = parseBackendDueDate(value);
  if (!due) return false;
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  due.setHours(0, 0, 0, 0);
  return today >= due;
};

/** Resolve next payment due date from backend payload (no client calculation). */
const getBackendDuePaymentDate = (application, allotment) =>
  application?.duePaymentDate ??
  application?.nextPaymentDueDate ??
  application?.rentPaymentDetails?.duePaymentDate ??
  allotment?.duePaymentDate ??
  allotment?.nextPaymentDueDate ??
  allotment?.rentPaymentDetails?.duePaymentDate ??
  null;

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

  const duePaymentDate = getBackendDuePaymentDate(application, allotment);
  const nextPaymentDueLabel = formatBackendDueDate(duePaymentDate) || "N/A";

  // Show Make Payment when backend due date is today/past (bill checked on click).
  const showMakePayment = useMemo(() => {
    if (!canMakePayment) return false;
    if (duePaymentDate && !isPaymentDueTodayOrPast(duePaymentDate)) {
      return false;
    }
    // No due date from backend yet → still allow click; fetchBill decides.
    return true;
  }, [canMakePayment, duePaymentDate]);

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
      <StatusTable>
        <Row className="border-none" label={t("EST_ASSET_ID")} text={application?.assetId || "N/A"} />
        <Row className="border-none" label={t("EST_ESTATE_NUMBER")} text={estateNo || "N/A"} />
        <Row
          className="border-none"
          label={t("EST_BUILDING_NAME")}
          text={application?.buildingName || application?.assetName || "N/A"}
        />
        <Row className="border-none" label={t("EST_ALLOTMENT_TYPE")} text={allotmentTypeLabel} />
        <Row className="border-none" label={t("EST_BILLING_CYCLE")} text={billingCycleLabel} />
        <Row className="border-none" label={t("EST_RATE")} text={`₹${application?.rate || 0}`} />
        <Row className="border-none" label={t("EST_ASSET_STATUS")} text={application?.assetStatus || "N/A"} />
        <Row
          className="border-none"
          label={t("EST_CREATED_DATE")}
          text={
            application?.auditDetails?.createdTime
              ? new Date(application.auditDetails.createdTime).toLocaleDateString("en-GB")
              : "N/A"
          }
        />
        <Row
          className="border-none"
          label={t("EST_NEXT_PAYMENT_DUE_DATE")}
          text={nextPaymentDueLabel}
        />
      </StatusTable>

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
