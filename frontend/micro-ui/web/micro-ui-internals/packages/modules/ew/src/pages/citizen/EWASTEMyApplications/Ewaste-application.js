// Importing necessary components and hooks from external libraries
import { Card, KeyNote, SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next"; // Hook for translations
import { Link } from "react-router-dom"; // Component for navigation

// Component to display details of an individual E-Waste application
const EwasteApplication = ({ application, tenantId, buttonLabel }) => {
  const { t } = useTranslation(); // Translation hook

  return (
    <Card>
      {/* Displaying the request ID */}
      <KeyNote keyValue={t("EW_REQUEST_ID")} note={application?.requestId} />

      {/* Displaying the applicant's name */}
      <KeyNote keyValue={t("EWASTE_APPLICANT_NAME")} note={application?.applicant?.applicantName} />

      {/* Displaying the current status of the request */}
      <KeyNote keyValue={t("EW_STATUS")} note={application?.requestStatus} />

      {/* Link to navigate to the detailed view of the application */}
      <Link to={`/digit-ui/citizen/ew/application/${application?.requestId}/${application?.tenantId}`}>
        <SubmitBar label={buttonLabel} /> {/* Button to view or track the application */}
      </Link>
    </Card>
  );
};

export default EwasteApplication; // Exporting the component
