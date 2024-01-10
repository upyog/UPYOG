import React from "react";
import { useTranslation } from "react-i18next";
import { Card, KeyNote, SubmitBar } from "@egovernments/digit-ui-react-components";
import { Link } from "react-router-dom";

const MyApplication = ({ application }) => {
  const { t } = useTranslation();

  return (
    <Card>
      <KeyNote keyValue={t("CS_FSM_APPLICATION_APPLICATION_NO")} note={application.applicationNo} />
      <KeyNote keyValue={t("CS_FSM_APPLICATION_SERVICE_CATEGORY")} note={application.serviceCategory || t("CS_TITLE_FSM")} />
      {/* Removed the application type as per Backend and Product Team */}
      {/* <KeyNote keyValue={t("CS_FSM_APPLICATION_TYPE")} note={t(`CS_FSM_APPLICATION_TYPE_${application.applicationType?.toUpperCase().replace(" ", "_")}`) || t("CS_FSM_APPLICATION_TYPE_DESLUDGING")} /> */}
      <KeyNote keyValue={t("CS_FSM_APPLICATION_DETAIL_STATUS")} note={t("CS_COMMON_" + application.applicationStatus)} />
      <Link to={{ pathname: `/digit-ui/citizen/fsm/application-details/${application.applicationNo}`, state: { tenantId: application.tenantId } }}>
        <SubmitBar label={t("CS_COMMON_VIEW")} />
      </Link>
      {application?.applicationStatus==="PENDING_APPL_FEE_PAYMENT" ? (
      <Link to={{ pathname: `/digit-ui/citizen/payment/my-bills/FSM.TRIP_CHARGES/${application?.applicationNo}/?tenantId=${application.tenantId}`,}}>
        <div style={{marginTop:"10px"}}><SubmitBar label={t("COMMON_MAKE_PAYMENT")} /></div>
      </Link>
      ):null}

    </Card>
  );
};

export default MyApplication;
