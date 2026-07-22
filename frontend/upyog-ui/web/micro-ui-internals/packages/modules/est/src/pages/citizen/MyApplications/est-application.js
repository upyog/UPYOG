/**
 * EstateApplication card — citizen My Applications list item.
 * Shows key notes and actions: view summary / make payment.
 * Make Payment is disabled when fetchBill has no due amount (already paid / no demand).
 */
import React, { useMemo } from "react";
import { Card, KeyNote, SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import { getApplicationDetailsPath, getCitizenPaymentPath } from "../../../utils/estRoutes";
import styles from "../../../styles/ESTMyApplications.module.scss";

const EST_BUSINESS_SERVICE = "est-services";

const EstateApplication = ({ application, tenantId }) => {
  const { t } = useTranslation();
  const navigate = Digit.Hooks.useCustomNavigate();
  const { path: modulePath } = Digit.Hooks.useModuleBasePath();

  const estateNo = application?.estateNo;
  const billTenantId = tenantId || application?.tenantId;

  const { data: billData, isLoading: isBillLoading } = Digit.Hooks.useFetchPayment(
    {
      tenantId: billTenantId,
      consumerCode: estateNo,
      businessService: EST_BUSINESS_SERVICE,
    },
    {
      enabled: Boolean(billTenantId && estateNo),
      retry: false,
    }
  );

  const isPaymentDisabled = useMemo(() => {
    if (isBillLoading) return true;
    const bill = billData?.Bill?.[0];
    const amountDue = Number(bill?.totalAmount ?? 0);
    // No bill or zero due → payment already done / nothing to pay.
    return !bill || amountDue <= 0;
  }, [isBillLoading, billData]);

  // Same summary page as employee portal (application-details/:assetNo).
  const handleViewSummary = () => {
    navigate(getApplicationDetailsPath(modulePath, application?.estateNo), {
      state: { applicationData: application },
    });
  };

  const handleMakePayment = () => {
    if (isPaymentDisabled) return;
    navigate({
      pathname: getCitizenPaymentPath(application?.estateNo),
      state: { tenantId: billTenantId },
    });
  };

  return (
    <Card className={styles["est-myapps__card"]}>
      <KeyNote keyValue={t("EST_ASSET_ID")} note={application?.assetId || "N/A"} />
      <KeyNote keyValue={t("EST_ESTATE_NUMBER")} note={application?.estateNo || "N/A"} />
      <KeyNote keyValue={t("EST_ASSET_NAME")} note={application?.assetName || "N/A"} />
      <KeyNote keyValue={t("EST_BUILDING_NAME")} note={application?.buildingName || "N/A"} />
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

      <div className={styles["est-myapps__actions"]}>
        <SubmitBar
          label={t("EST_VIEW_SUMMARY")}
          onSubmit={handleViewSummary}
          className={styles["est-myapps__action-btn"]}
        />
        <SubmitBar
          label={t("EST_MAKE_PAYMENT")}
          onSubmit={handleMakePayment}
          disabled={isPaymentDisabled}
          className={styles["est-myapps__action-btn"]}
        />
      </div>
    </Card>
  );
};

export default EstateApplication;
