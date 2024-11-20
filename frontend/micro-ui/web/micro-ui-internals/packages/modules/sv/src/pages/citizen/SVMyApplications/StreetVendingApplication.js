import { ApplyFilterBar, Card, KeyNote, SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { Link,useHistory } from "react-router-dom";

const StreetVendingApplication = ({ application, buttonLabel }) => {
  const { t } = useTranslation();
  console.log("appplicatioooooo",application);
  const history = useHistory();
  // Function to handle the click event, setting the applicationId and redirecting
  const handleEditClick = () => {
    sessionStorage.setItem("vendingApplicationID", application?.applicationNo);
    sessionStorage.setItem("ApplicationId",application?.applicationId);
    sessionStorage.setItem("applicationStatus",application?.applicationStatus);
    history.push(`/digit-ui/citizen/sv/edit`);
  };


  return (
    <Card>
      <KeyNote keyValue={t("SV_APPLICATION_NUMBER")} note={application?.applicationNo} />
      <KeyNote keyValue={t("SV_VENDOR_NAME")} note={application?.vendorDetail?.[0]?.name} />
      <KeyNote keyValue={t("SV_VENDING_TYPE")} note={application?.vendingActivity} />
      <KeyNote keyValue={t("SV_VENDING_ZONES")} note={application?.vendingZone} />
      {application?.vendingActivity==="STATIONARY"&&(
      <KeyNote keyValue={t("SV_AREA_REQUIRED")} note={application?.vendingArea} />)}
      {(application?.applicationStatus == "CITIZENACTIONREQUIRED") && 
      <SubmitBar style={{ marginBottom: "5px" }} label={"SV_EDIT"} onSubmit={handleEditClick} />}
      <Link to={`/digit-ui/citizen/sv/application/${application?.applicationNo}/${application?.tenantId}`}>
        <SubmitBar label={buttonLabel} />
      </Link>
    </Card>
  );
};

export default StreetVendingApplication;
