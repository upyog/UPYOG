/**
 * EstateApplication card — citizen My Applications list item.
 * Shows key notes and actions: view summary / make payment.
 * EST_NEXT_PAYMENT_DUE_DATE uses backend `dueDate` from asset/allotment search.
 * fetchBill runs only when the user clicks EST_MAKE_PAYMENT.
 */
import React, { useEffect, useMemo, useState } from "react";
import { Card, Row, StatusTable, SubmitBar, Toast } from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import { getApplicationDetailsPath, getCitizenPaymentPath } from "../../../utils/estRoutes";
import styles from "../../../styles/ESTMyApplications.module.scss";

const EST_BUSINESS_SERVICE = "est-services";

/** Normalize allotmentType / propertyType from API (string or { code }). */
const toCode = (value) => {
  if (value === undefined || value === null || value === "") return "";
  if (typeof value === "object") {
    return String(value.code || value.name || "").trim().toUpperCase();
  }
  return String(value).trim().toUpperCase();
};

const translateOrCode = (t, prefix, code) => {
  if (!code) return "N/A";
  const key = `${prefix}_${code}`;
  const translated = t(key);
  return translated && translated !== key ? translated : code;
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

/**
 * Backend `dueDate` comes on allotment search (Allotments[].dueDate),
 * e.g. "23-08-2026". Asset search does not include it.
 */
const getDueDate = (application, allotment) =>
  allotment?.dueDate ??
  application?.dueDate ??
  allotment?.additionalDetails?.dueDate ??
  application?.additionalDetails?.dueDate ??
  null;

/** Parse backend dueDate (dd-MM-yyyy, ISO, epoch, or Jackson [y,m,d] array). */
const parseDueDate = (value) => {
  if (value === null || value === undefined || value === "") return null;
  if (value instanceof Date) {
    return Number.isNaN(value.getTime()) ? null : value;
  }
  // Jackson LocalDate without @JsonFormat → [year, month, day]
  if (Array.isArray(value) && value.length >= 3) {
    const [y, m, d] = value.map(Number);
    if (![y, m, d].every(Number.isFinite)) return null;
    const date = new Date(y, m - 1, d);
    return Number.isNaN(date.getTime()) ? null : date;
  }
  if (typeof value === "object") {
    const y = Number(value.year ?? value.Year);
    const m = Number(value.monthValue ?? value.month ?? value.Month);
    const d = Number(value.dayOfMonth ?? value.day ?? value.Day);
    if ([y, m, d].every(Number.isFinite)) {
      const date = new Date(y, m - 1, d);
      return Number.isNaN(date.getTime()) ? null : date;
    }
  }
  if (typeof value === "number" || /^\d+$/.test(String(value).trim())) {
    let num = Number(value);
    if (String(Math.trunc(num)).length === 10) num *= 1000;
    const date = new Date(num);
    return Number.isNaN(date.getTime()) ? null : date;
  }
  const raw = String(value).trim();
  const dmy = raw.match(/^(\d{1,2})[/-](\d{1,2})[/-](\d{4})$/);
  if (dmy) return new Date(+dmy[3], +dmy[2] - 1, +dmy[1]);
  const ymd = raw.match(/^(\d{4})-(\d{1,2})-(\d{1,2})/);
  if (ymd) return new Date(+ymd[1], +ymd[2] - 1, +ymd[3]);
  const date = new Date(raw);
  return Number.isNaN(date.getTime()) ? null : date;
};

const formatDueDate = (value) => {
  if (value === null || value === undefined || value === "") return "";
  if (typeof value === "string") {
    const trimmed = value.trim();
    if (/^\d{1,2}[/-]\d{1,2}[/-]\d{4}$/.test(trimmed)) {
      return trimmed.replace(/-/g, "/");
    }
  }
  const date = parseDueDate(value);
  if (!date) return typeof value === "string" ? value.trim() : "";
  const dd = String(date.getDate()).padStart(2, "0");
  const mm = String(date.getMonth() + 1).padStart(2, "0");
  return `${dd}/${mm}/${date.getFullYear()}`;
};

const EstateApplication = ({ application, allotment: allotmentProp = null, tenantId }) => {
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

  // Always load allotment by estate number (source of propertyType + billingCycle).
  const { data: allotmentData } = Digit.Hooks.estate.useESTApplicationSearch({
    filters: {
      tenantId: billTenantId,
      assetNo: estateNo,
    },
    config: {
      enabled: Boolean(billTenantId && estateNo),
    },
  });

  const fetchedAllotment =
    allotmentData?.Allotments?.[0] || allotmentData?.allotments?.[0] || null;

  // Prefer fetched allotment when it has identity/type fields from allotment search.
  const allotment = useMemo(() => {
    const hasFields = (row) =>
      Boolean(
        row &&
          (row.allotmentNo ||
            row.allotmentId ||
            row.propertyType ||
            row.allotmentType ||
            row.billingCycle)
      );
    if (hasFields(fetchedAllotment)) return fetchedAllotment;
    if (hasFields(allotmentProp)) return allotmentProp;
    return fetchedAllotment || allotmentProp || null;
  }, [fetchedAllotment, allotmentProp]);

  // Allotment type = RENT | LEASE from allotment.propertyType (required; default RENT).
  // Do not use asset.assetAllotmentType (that can be DONATED / acquisition type).
  const allotmentTypeLabel = useMemo(() => {
    const raw = toCode(
      allotment?.allotmentType ??
        allotment?.propertyType ??
        application?.allotmentType ??
        application?.propertyType
    );
    const code = raw === "RENT" || raw === "LEASE" ? raw : "RENT";
    return translateOrCode(t, "EST_ALLOTMENT_TYPE", code);
  }, [allotment, application, t]);

  // API: allotment.billingCycle (MONTHLY | QUARTERLY | YEARLY). Default MONTHLY when not set.
  const billingCycleLabel = useMemo(() => {
    const code = toCode(allotment?.billingCycle ?? application?.billingCycle) || "MONTHLY";
    return translateOrCode(t, "EST_BILLING_CYCLE", code);
  }, [allotment, application, t]);

  const nextPaymentDueLabel = formatDueDate(getDueDate(application, allotment)) || "N/A";

  // allotmentNo from allotment/_search — also used as billing consumerCode.
  const allotmentNo = String(allotment?.allotmentNo || "").trim();
  const hasAllotment = Boolean(
    String(allotment?.allotmentNo || allotment?.allotmentId || "").trim()
  );

  // Make Payment requires allotmentNo (billing consumerCode).
  const canMakePayment = Boolean(billTenantId && allotmentNo);
  const showMakePayment = canMakePayment;

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
        consumerCode: allotmentNo,
        businessService: EST_BUSINESS_SERVICE,
      });
      const amountDue = getBillAmountDue(billData);

      if (amountDue <= 0) {
        setShowToast({ error: true, label: t("CS_BILL_NOT_FOUND") });
        return;
      }

      navigate({
        pathname: getCitizenPaymentPath(allotmentNo),
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
        <Row
          className="border-none"
          label={t("EST_ALLOTMENT_ID")}
          text={allotmentNo || "N/A"}
        />
        <Row className="border-none" label={t("EST_ESTATE_NUMBER")} text={estateNo || "N/A"} />
        <Row
          className="border-none"
          label={t("EST_BUILDING_NAME")}
          text={application?.buildingName || application?.assetName || "N/A"}
        />
        {!hasAllotment ? (
          <Row
            className="border-none"
            label={(() => {
              const key = "EST_ALLOTMENT";
              const translated = t(key);
              return translated && translated !== key ? translated : "Allotment";
            })()}
            text={(() => {
              const key = "EST_PENDING_FOR_ALLOTMENT";
              const translated = t(key);
              return translated && translated !== key
                ? translated
                : "Pending for allotment";
            })()}
          />
        ) : null}
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
