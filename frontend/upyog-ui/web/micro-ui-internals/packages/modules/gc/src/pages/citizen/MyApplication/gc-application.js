import React from "react";
import { Card, KeyNote, SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";
import "../../../css/gc-inline-auto.css";

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

  const name = application?.name;
  const mobileNumber = application?.mobileNumber;

  const appNo = application?.grbgApplication?.applicationNo || application?.grbgApplicationNumber || application?.applicationNo;
  const appStatus = application?.grbgApplication?.status || application?.applicationStatus || application?.status;

  const propertyId = application?.propertyId;
  const collectionUnit = application?.grbgCollectionUnits?.[0] || {};
  const category = collectionUnit?.category;
  const typeOfCollection = collectionUnit?.unitType;

  const handleMakePayment = () => {
    navigate(`/upyog-ui/citizen/payment/my-bills/garbage-service/${appNo}`);
  };

  const handleEditApplication = () => {
    navigate(`/upyog-ui/citizen/gc/edit/${appNo}`);
  };

  return (
    <Card style={{ marginTop: "16px" }}>
      <KeyNote keyValue={t("GC_APPLICATION_NUMBER_LABEL")} note={appNo || t("CS_NA")} />
      <KeyNote keyValue={t("GC_NAME")} note={name || t("CS_NA")} />
      <KeyNote keyValue={t("GC_MOBILE_NUMBER")} note={mobileNumber || t("CS_NA")} />
      {propertyId && <KeyNote keyValue={t("GC_PROPERTY_ID")} note={propertyId} />}
      {category && <KeyNote keyValue={t("GC_CATEGORY")} note={t(category)} />}
      {typeOfCollection && <KeyNote keyValue={t("GC_TYPE_OF_COLLECTION")} note={t(typeOfCollection)} />}
      <KeyNote keyValue={t("GC_APPLICATION_STATUS_LABEL")} note={appStatus ? t(`GC_STATUS_${appStatus}`) : t("CS_NA")} />
      {application?.dueDate && <KeyNote keyValue={t("GC_DUE_DATE")} note={application.dueDate} />}
      
      <div className="gc-btn-row">
        <Link to={`/upyog-ui/citizen/gc/application-details/${encodeURIComponent(appNo)}`}>
          <SubmitBar label={t("CS_VIEW_DETAILS")} />
        </Link>
        {appStatus === "PENDING_FOR_PAYMENT" && (
          <SubmitBar label={t("CS_APPLICATION_DETAILS_MAKE_PAYMENT")} onSubmit={handleMakePayment} />
        )}
        {appStatus === "EDIT_APPLICATION" && (
          <SubmitBar label={t("GC_EDIT_APPLICATION")} onSubmit={handleEditApplication} />
        )}
      </div>
    </Card>
  );
};

export default GCApplication;