import { Card, KeyNote, SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useContext } from "react";
import { useTranslation } from "react-i18next";
import { Link, useHistory } from "react-router-dom";

const CndApplicationData = ({ application, tenantId, buttonLabel }) => {
    console.log("applicationapplication",application);
  const { t } = useTranslation();
  const history = useHistory();


  return (
    <Card>
      <KeyNote keyValue={t("CND_APPLICATION_NUMBER")} note={application?.applicationNumber} />
      <KeyNote keyValue={t("CND_APPLICATION_TYPE")} note={application?.applicationType} />
      <KeyNote keyValue={t("CND_APPLICATION_STATUS")} note={application?.applicationStatus} />

      <Link to={`/cnd-ui/citizen/cnd/my-requests/${application?.applicationNumber}/${application?.tenantId}`}>
        <SubmitBar label={buttonLabel} />
      </Link>
    </Card>
  );
};

export default CndApplicationData;
