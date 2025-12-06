/**
 * This component, `PetApplication`, displays the details of a single pet registration application.
 * It uses `Card` and `KeyNote` components from `@nudmcdgnpm/digit-ui-react-components` 
 * to present the application's information in a structured format.
 * 
 * Key Features:
 * - Displays application details such as application number, applicant name, type, status, and pet type.
 * - Shows a "Renewal" button if the application status is "Expired".
 * - On clicking the "Renewal" button, the user is redirected to the revised application page.
 * - Includes a button to navigate to the detailed application view.
 * 
 * Props:
 * - `application`: Object containing application details.
 * - `tenantId`: Tenant ID associated with the application.
 * - `buttonLabel`: Label for the action button (either "View" or "Track" based on feedback availability).
 * 
 */


import { Card, KeyNote, SubmitBar } from "@upyog/digit-ui-react-components";
import React, { useContext } from "react";
import { useTranslation } from "react-i18next";
import { Link, useHistory } from "react-router-dom";

const PetApplication = ({ application, tenantId, buttonLabel }) => {
  const { t } = useTranslation();
  const history = useHistory();

  // Function to handle the click event, setting the applicationId and redirecting
  const handleRenewalClick = () => {
    sessionStorage.setItem("petId", application?.applicationNumber)
    application?.petToken && application?.petToken?.length > 0 ? sessionStorage.setItem("petToken",application?.petToken) : ""
    history.push(`/digit-ui/citizen/ptr/petservice/revised-application`);
  };

  return (
    <Card>
      <KeyNote keyValue={t("PTR_APPLICATION_NO_LABEL")} note={application?.applicationNumber} />
      <KeyNote keyValue={t("PTR_APPLICANT_NAME")} note={application?.applicantName} />
      <KeyNote keyValue={t("PTR_APPLICATION_TYPE")} note={application?.applicationType} />
      <KeyNote keyValue={t("PTR_APPLICATION_STATUS")} note={application?.status} />
      <KeyNote keyValue={t("PTR_SEARCH_PET_TYPE")} note={application?.petDetails?.petType} />

      {(application?.status == "Expired") && 
      <SubmitBar style={{ marginBottom: "5px" }} label={"Renewal"} onSubmit={handleRenewalClick} />
}
      <Link to={`/digit-ui/citizen/ptr/petservice/application/${application?.applicationNumber}/${application?.tenantId}`}>
        <SubmitBar label={buttonLabel} />
      </Link>
    </Card>
  );
};

export default PetApplication;
