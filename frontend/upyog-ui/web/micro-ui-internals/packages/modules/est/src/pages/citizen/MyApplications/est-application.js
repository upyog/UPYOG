/**
 * EstateApplication card — citizen My Applications list item.
 * Shows key notes and actions: view summary / make payment.
 * Allotment (allotmentNo, dueDate, billingCycle) comes from parent list search.
 * fetchBill runs only when the user clicks EST_MAKE_PAYMENT (consumerCode = allotmentNo).
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
  getBillAmountDue,
  isNoDemandError,
  toBillingCycleLabel,
  translateOrCode,
} from "../../../utils/estDisplayUtils";
import styles from "../../../styles/ESTMyApplications.module.scss";

const EST_BUSINESS_SERVICE = "est-services";

const labelWithFallback = (t, key, fallback) => {
  const translated = t(key);
  return translated && translated !== key ? translated : fallback;
};

const EstateApplication = ({ application, allotment = null, tenantId }) => {
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

  // Allotment type = RENT | LEASE from allotment.propertyType (required; default RENT).
  // Do not use asset.assetAllotmentType (that can be DONATED / acquisition type).
  const allotmentTypeLabel = useMemo(() => {
    const raw = optionCode(
      allotment?.allotmentType ??
        allotment?.propertyType ??
        application?.allotmentType ??
        application?.propertyType
    );
    const code = raw === "RENT" || raw === "LEASE" ? raw : "RENT";
    return translateOrCode(t, "EST_ALLOTMENT_TYPE", code);
  }, [allotment, application, t]);

  const billingCycleLabel = useMemo(
    () =>
      toBillingCycleLabel(
        allotment?.billingCycle ?? application?.billingCycle ?? "MONTHLY",
        t
      ),
    [allotment, application, t]
  );

  const nextPaymentDueLabel =
    formatEstDueDate(getAllotmentDueDate(application, allotment)) || "N/A";

  // allotmentNo from allotment/_search — also used as billing consumerCode.
  const allotmentNo = String(allotment?.allotmentNo || "").trim();
  const hasAllotment = Boolean(
    String(allotment?.allotmentNo || allotment?.allotmentId || "").trim()
  );
  const canMakePayment = Boolean(billTenantId && allotmentNo);

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
            label={labelWithFallback(t, "EST_ALLOTMENT", "Allotment")}
            text={labelWithFallback(
              t,
              "EST_PENDING_FOR_ALLOTMENT",
              "Pending for allotment"
            )}
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
