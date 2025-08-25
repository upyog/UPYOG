import { Card, KeyNote, SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";

const CndApplicationData = ({ application, tenantId, buttonLabel }) => {
  const { t } = useTranslation();

  return (
    <Card>
      <KeyNote keyValue={t("CND_APPLICATION_NUMBER")} note={application?.applicationNumber} />
      <KeyNote keyValue={t("CND_APPLICATION_TYPE")} note={t(application?.applicationType)} />
      <KeyNote keyValue={t("CND_WASTE_QUANTITY")} note={application?.totalWasteQuantity + " Ton"} />
      <KeyNote keyValue={t("CND_SCHEDULE_PICKUP")} note={application?.requestedPickupDate} />
      <KeyNote keyValue={t("CND_AREA_HOUSE")} note={application?.houseArea} />
      <KeyNote keyValue={t("CND_APPLICATION_STATUS")} note={t(application?.applicationStatus)} />
      <Link to={`/cnd-ui/citizen/cnd/my-requests/${application?.applicationNumber}/${application?.tenantId}`}>
        <SubmitBar label={buttonLabel} />
      </Link>
    </Card>
  );
};

export default CndApplicationData;
