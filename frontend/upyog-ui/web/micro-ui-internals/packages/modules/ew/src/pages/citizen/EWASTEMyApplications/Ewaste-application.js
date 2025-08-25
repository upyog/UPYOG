import { Card, KeyNote, SubmitBar } from "@upyog/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";

/**
 * Displays a card view of an individual E-Waste application with key details.
 * Provides a link to the detailed view of the application.
 *
 * @param {Object} props Component properties
 * @param {Object} props.application E-Waste application data
 * @param {string} props.application.requestId Unique request identifier
 * @param {Object} props.application.applicant Applicant information
 * @param {string} props.application.requestStatus Current status of the request
 * @param {string} props.tenantId Tenant identifier
 * @param {string} props.buttonLabel Label for the action button
 * @returns {JSX.Element} Card containing application details
 */
const EwasteApplication = ({ application, tenantId, buttonLabel }) => {
  
  
  const { t } = useTranslation();
  return (
    <Card>
      <KeyNote keyValue={t("EW_REQUEST_ID")} note={application?.requestId} />
      <KeyNote keyValue={t("EWASTE_APPLICANT_NAME")} note={application?.applicant?.applicantName} />
      <KeyNote keyValue={t("EW_STATUS")} note={application?.requestStatus} />
      <Link to={`/upyog-ui/citizen/ew/application/${application?.requestId}/${application?.tenantId}`}>
        <SubmitBar label={buttonLabel} />
      </Link>
    </Card>
  );
};

export default EwasteApplication;
