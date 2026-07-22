/**
 * EstateApplication card — citizen My Applications list item.
 * Shows key notes and actions: view summary / make payment.
 */
import React from "react";
import { Card, KeyNote, SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import { getApplicationDetailsPath, getCitizenPaymentPath } from "../../../utils/estRoutes";
import styles from "../../../styles/ESTMyApplications.module.scss";

const EstateApplication = ({ application, tenantId }) => {
  const { t } = useTranslation();
  const navigate = Digit.Hooks.useCustomNavigate();
  const { path: modulePath } = Digit.Hooks.useModuleBasePath();

  // Same summary page as employee portal (application-details/:assetNo).
  const handleViewSummary = () => {
    navigate(getApplicationDetailsPath(modulePath, application?.estateNo), {
      state: { applicationData: application },
    });
  };

  const handleMakePayment = () => {
    navigate({
      pathname: getCitizenPaymentPath(application?.estateNo),
      state: { tenantId },
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
          className={styles["est-myapps__action-btn"]}
        />
      </div>
    </Card>
  );
};

export default EstateApplication;
