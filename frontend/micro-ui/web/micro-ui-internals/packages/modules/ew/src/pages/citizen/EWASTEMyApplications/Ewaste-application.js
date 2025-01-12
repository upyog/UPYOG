import { Card, KeyNote, SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";

const EwasteApplication = ({ application, tenantId, buttonLabel }) => {
  
  
  const { t } = useTranslation();
  return (
    <Card>
      <KeyNote keyValue={t("EW_REQUEST_ID")} note={application?.requestId} />
      <KeyNote keyValue={t("EWASTE_APPLICANT_NAME")} note={application?.applicant?.applicantName} />
      <KeyNote keyValue={t("EW_STATUS")} note={application?.requestStatus} />
      <Link to={`/digit-ui/citizen/ew/application/${application?.requestId}/${application?.tenantId}`}>
        <SubmitBar label={buttonLabel} />
      </Link>
    </Card>
  );
};

export default EwasteApplication;
