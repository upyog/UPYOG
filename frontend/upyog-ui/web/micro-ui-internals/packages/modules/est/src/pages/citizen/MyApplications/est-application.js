import React from "react";
import { Card, KeyNote, SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";

// Estate Application Component
// This component displays the details of an estate application and provides options to view the summary or make a payment.

import { getCitizenPaymentPath } from "../../../utils/estRoutes";

const EstateApplication = ({ application, tenantId }) => {
  const { t } = useTranslation();
  const navigate = Digit.Hooks.useCustomNavigate();
  const { path: modulePath } = Digit.Hooks.useModuleBasePath();

  const handleViewSummary = () => {
    navigate(`${modulePath}/application/${application?.estateNo}/${application?.tenantId}`, {
      assetData: application,
      tenantId,
    });
  };

  const handleMakePayment = () => {
    navigate({
      pathname: getCitizenPaymentPath(application?.estateNo),
      state: { tenantId },
    });
  };

  return (
    <Card style={{ marginTop: "16px" }}>
      <KeyNote keyValue={t("EST_ASSET_ID")} note={application?.assetId || "N/A"} />
      <KeyNote keyValue={t("EST_ESTATE_NUMBER")} note={application?.estateNo || "N/A"} />
      <KeyNote keyValue={t("EST_ASSET_NAME")} note={application?.assetName || "N/A"} />
      <KeyNote keyValue={t("EST_BUILDING_NAME")} note={application?.buildingName || "N/A"} />
      <KeyNote keyValue={t("EST_RATE")} note={`₹${application?.rate || 0}`} />
      <KeyNote keyValue={t("EST_ASSET_STATUS")} note={application?.assetStatus || "N/A"} />
      <KeyNote keyValue={t("EST_CREATED_DATE")} note={
        application?.auditDetails?.createdTime 
          ? new Date(application.auditDetails.createdTime).toLocaleDateString("en-GB")
          : "N/A"
      } />
      
      <div style={{ display: "flex", gap: "12px", marginTop: "16px" }}>
        <SubmitBar 
          label={t("EST_VIEW_SUMMARY")} 
          onSubmit={handleViewSummary}
          style={{ flex: 1 }}
        />
        <SubmitBar 
          label={t("EST_MAKE_PAYMENT")} 
          onSubmit={handleMakePayment}
          style={{ flex: 1 }}
        />
      </div>
    </Card>
  );
};

export default EstateApplication;
