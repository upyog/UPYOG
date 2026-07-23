/**
 * EstateApplication card — citizen My Applications list item.
 * Shows key notes and actions: view summary / make payment.
 * Next payment due date is shown from bill expiry (fallback: allotment schedule).
 * Make Payment appears only when amount > 0 AND today is on/after that due date.
 * Allotment type (RENT/LEASE) is loaded via allotmentSearch by assetNo
 * — same source as application-details.
 */
import React, { useMemo } from "react";
import { Card, KeyNote, SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import { getApplicationDetailsPath, getCitizenPaymentPath } from "../../../utils/estRoutes";
import {
  formatPaymentDueDate,
  getBillAmountDue,
  getNextPaymentDueDate,
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

const EstateApplication = ({ application, tenantId }) => {
  const { t } = useTranslation();
  const navigate = Digit.Hooks.useCustomNavigate();
  const { path: modulePath } = Digit.Hooks.useModuleBasePath();

  const estateNo = application?.estateNo || application?.assetNo;
  const billTenantId = tenantId || application?.tenantId;
  const canFetchBill = Boolean(billTenantId && estateNo);

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

  const {
    data: billData,
    isLoading: isBillLoading,
    isFetching: isBillFetching,
    isError: isBillError,
    isSuccess: isBillSuccess,
  } = Digit.Hooks.useFetchPayment(
    {
      tenantId: billTenantId,
      consumerCode: estateNo,
      businessService: EST_BUSINESS_SERVICE,
    },
    {
      enabled: canFetchBill,
      retry: false,
    }
  );

  const billAmountDue = useMemo(() => getBillAmountDue(billData), [billData]);

  const nextPaymentDueDate = useMemo(
    () =>
      getNextPaymentDueDate({
        billData,
        allotment,
        amountDue: billAmountDue,
      }),
    [billData, allotment, billAmountDue]
  );

  const nextPaymentDueLabel = useMemo(() => {
    const formatted = formatPaymentDueDate(nextPaymentDueDate);
    return formatted || "N/A";
  }, [nextPaymentDueDate]);

  const showMakePayment = useMemo(() => {
    if (!canFetchBill) return false;
    if ((isBillLoading || isBillFetching) && !billData) return false;
    if (isBillError) return false;
    if (!isBillSuccess && !billData) return false;
    if (billAmountDue <= 0) return false;
    // Amount due, but only expose the button on/after the current due date.
    if (nextPaymentDueDate && !isPaymentDueTodayOrPast(nextPaymentDueDate)) {
      return false;
    }
    return true;
  }, [
    canFetchBill,
    isBillLoading,
    isBillFetching,
    isBillError,
    isBillSuccess,
    billData,
    billAmountDue,
    nextPaymentDueDate,
  ]);

  const handleViewSummary = () => {
    navigate(getApplicationDetailsPath(modulePath, estateNo), {
      state: { applicationData: application },
    });
  };

  const handleMakePayment = () => {
    navigate({
      pathname: getCitizenPaymentPath(estateNo),
      state: { tenantId: billTenantId },
    });
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
            className={styles["est-myapps__action-btn"]}
          />
        ) : null}
      </div>
    </Card>
  );
};

export default EstateApplication;
