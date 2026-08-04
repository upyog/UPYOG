/**
 * EstateApplication card — citizen My Applications list item.
 * Row data comes from allotment _search (application === allotment).
 * EST_MAKE_PAYMENT is hidden when Allotments[].status is PAID.
 * fetchBill runs only on EST_MAKE_PAYMENT (consumerCode = allotmentNo).
 */
import React, { useEffect, useMemo, useState } from "react";
import {
  Card,
  Row,
  StatusTable,
  SubmitBar,
  Toast,
  optionCode,
} from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import { getApplicationDetailsPath, getCitizenPaymentPath } from "../../../utils/estRoutes";
import {
  formatEstDueDate,
  getAllotmentDueDate,
  getAllotmentPaymentStatus,
  getBillAmountDue,
  isAllotmentPaymentPaid,
  isNoDemandError,
  normalizeCitizenPaymentStatus,
  toBillingCycleLabel,
  translateOrCode,
} from "../../../utils/estDisplayUtils";
import { getAllotmentNo } from "../../../utils/estMdmsUtils";
import styles from "../../../styles/ESTMyApplications.module.scss";

const EST_BUSINESS_SERVICE = "est-services";

const EstateApplication = ({ application, allotment = null, tenantId }) => {
  const { t } = useTranslation();
  const navigate = Digit.Hooks.useCustomNavigate();
  const { path: modulePath } = Digit.Hooks.useModuleBasePath();

  const [isPaymentLoading, setIsPaymentLoading] = useState(false);
  const [showToast, setShowToast] = useState(null);

  const row = allotment || application || {};

  useEffect(() => {
    if (!showToast) return undefined;
    const timer = setTimeout(() => setShowToast(null), 3000);
    return () => clearTimeout(timer);
  }, [showToast]);

  const billTenantId = tenantId || row?.tenantId;
  const allotmentNo = getAllotmentNo(row);

  const allotmentTypeLabel = useMemo(() => {
    const raw = optionCode(row?.allotmentType ?? row?.propertyType);
    const code = raw === "RENT" || raw === "LEASE" ? raw : "RENT";
    return translateOrCode(t, "EST_ALLOTMENT_TYPE", code);
  }, [row, t]);

  const billingCycleLabel = useMemo(
    () => toBillingCycleLabel(row?.billingCycle ?? "MONTHLY", t),
    [row, t]
  );

  const nextPaymentDueLabel =
    formatEstDueDate(getAllotmentDueDate(row)) || "N/A";
  const agreementStartDate =
    formatEstDueDate(
      row?.agreementStartDate ?? row?.additionalDetails?.agreementStartDate
    ) || "N/A";
  const agreementEndDate =
    formatEstDueDate(
      row?.agreementEndDate ?? row?.additionalDetails?.agreementEndDate
    ) || "N/A";

  const paymentStatusCode = normalizeCitizenPaymentStatus(
    getAllotmentPaymentStatus(row)
  );
  const isPaid = isAllotmentPaymentPaid(row);
  const canMakePayment = Boolean(billTenantId && allotmentNo && !isPaid);

  const handleViewSummary = () => {
    if (!allotmentNo) return;
    navigate(getApplicationDetailsPath(modulePath, allotmentNo), {
      state: { applicationData: row, allotmentData: row },
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
      if (getBillAmountDue(billData) <= 0) {
        setShowToast({ error: true, label: t("CS_BILL_NOT_FOUND") });
        return;
      }

      navigate({
        pathname: getCitizenPaymentPath(allotmentNo),
        state: { tenantId: billTenantId },
      });
    } catch (err) {
      setShowToast({
        error: true,
        label: t(isNoDemandError(err) ? "CS_BILL_NOT_FOUND" : "CS_SOMETHING_WENT_WRONG"),
      });
    } finally {
      setIsPaymentLoading(false);
    }
  };

  return (
    <Card className={styles["est-myapps__card"]}>
      <StatusTable>
        <Row
          className="border-none"
          label={t("EST_ALLOTMENT_NUMBER")}
          text={allotmentNo || "N/A"}
        />
        <Row className="border-none" label={t("EST_ALLOTMENT_TYPE")} text={allotmentTypeLabel} />
        <Row className="border-none" label={t("EST_ALLOTTEE_NAME")} text={row?.alloteeName || "N/A"} />
        <Row className="border-none" label={t("EST_BILLING_CYCLE")} text={billingCycleLabel} />
        <Row
          className="border-none"
          label={t("EST_MONTHLY_RENT_IN_INR")}
          text={`₹${row?.monthlyRent || 0}`}
        />
        <Row
          className="border-none"
          label={t("EST_ASSET_STATUS")}
          text={translateOrCode(t, "EST_PAYMENT_STATUS", paymentStatusCode)}
        />
        <Row
          className="border-none"
          label={t("EST_AGREEMENT_START_DATE")}
          text={agreementStartDate}
        />
        <Row
          className="border-none"
          label={t("EST_AGREEMENT_END_DATE")}
          text={agreementEndDate}
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
        {canMakePayment ? (
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
