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
const PGRApplication = ({ application, tenantId, buttonLabel }) => {
  const { t } = useTranslation();
    
  return (
    <Card>
      <KeyNote 
        keyValue={t("PGR_AI_REQUEST_ID")} 
        note={application?.service?.serviceRequestId}  // Changed from application?.requestId
      />
      
      <KeyNote 
        keyValue={t("PGR_AI_STATUS")} 
        note={application?.service?.applicationStatus}  // Changed from application?.requestStatus
      />
      
      <Link to={`/upyog-ui/citizen/pgrai/application/${application?.service?.serviceRequestId}/${application?.service?.tenantId}`}>
        <SubmitBar label={buttonLabel} />
      </Link>
    </Card>
  );
};

export default PGRApplication;