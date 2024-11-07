import { Card, KeyNote, SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";

const StreetVendingApplication = ({ application, buttonLabel }) => {
  const { t } = useTranslation();


  return (
    <Card>
      <KeyNote keyValue={t("SV_APPLICATION_NUMBER")} note={application?.applicationNo} />
      <KeyNote keyValue={t("SV_VENDOR_NAME")} note={application?.vendorDetail?.[0]?.name} />
      <KeyNote keyValue={t("SV_VENDING_TYPE")} note={application?.vendingActivity} />
      <KeyNote keyValue={t("SV_VENDING_ZONES")} note={application?.vendingZone} />
      <KeyNote keyValue={t("SV_AREA_REQUIRED")} note={application?.vendingArea} />
      <Link to={`/digit-ui/citizen/sv/application/${application?.applicationNo}/${application?.tenantId}`}>
        <SubmitBar label={buttonLabel} />
      </Link>
    </Card>
  );
};

export default StreetVendingApplication;
