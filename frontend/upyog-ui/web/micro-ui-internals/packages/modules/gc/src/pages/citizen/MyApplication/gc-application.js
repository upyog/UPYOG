import React from "react";
import { Card, KeyNote, SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";

// Garbage Collection Application Component
// This component displays the details of an application and provides options to view the summary or make a payment.

/**
 * GCApplication Component
 * 
 * Displays a summary card for a single GC application in the citizen's "My Applications" list.
 * Shows application number, applicant name, and status. Provides buttons to view details
 * and optionally make a payment if the application status is PENDING_FOR_PAYMENT.
 * 
 * Props:
 * - `application`: The application object containing applicant details, status, etc.
 * - `tenantId`: Current tenant ID for payment navigation
 */
const GCApplication = ({ application, tenantId }) => {
  const { t } = useTranslation();
  const navigate = Digit.Hooks.useCustomNavigate();

  const owners = application?.applicantDetails || [];
  const ownerNames = owners
    ?.map((owner) => owner?.name || owner?.applicantName)
    ?.filter(Boolean)
    ?.join(", ") || application?.name || application?.garbageSpecification?.name;

  const appNo = application?.grbgApplication?.applicationNo || application?.applicationNo;
  const appStatus = application?.applicationStatus || application?.status || application?.grbgApplication?.status;

  const handleViewSummary = () => {
    navigate(`/upyog-ui/citizen/gc/application-details/${encodeURIComponent(appNo)}`);
  };

  const handleMakePayment = () => {
     navigate(`/upyog-ui/citizen/payment/my-bills/${"garbage-service"}/${appNo}`);
  };

  return (
    <Card style={{ marginTop: "16px" }}>
      <KeyNote keyValue={t("GC_APPLICATION_NUMBER_LABEL")} note={appNo || t("CS_NA")} />
      <KeyNote keyValue={t("GC_APPLICANT_NAME")} note={ownerNames || t("CS_NA")} />
      <KeyNote keyValue={t("GC_APPLICATION_STATUS_LABEL")} note={appStatus ? t(`GC_STATUS_${appStatus}`) : t("CS_NA")} />
      
      <div style={{ display: "flex", gap: "12px", marginTop: "16px" }}>
        <SubmitBar label={t("CS_VIEW_DETAILS")} onSubmit={handleViewSummary} style={{ flex: 1 }} />
        
        {appStatus === "PENDING_FOR_PAYMENT" && (
          <SubmitBar label={t("CS_APPLICATION_DETAILS_MAKE_PAYMENT")} onSubmit={handleMakePayment} style={{ flex: 1 }} />
        )}
      </div>
    </Card>
  );
};

export default GCApplication;