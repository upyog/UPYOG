import { Card, KeyNote, SubmitBar } from "@upyog/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";

const ChbApplication = ({ application, tenantId, buttonLabel }) => {
  
  const { t } = useTranslation();
  return (
    <Card>
      <KeyNote keyValue={t("CHB_BOOKING_NO")} note={application?.bookingNo} />
      <KeyNote keyValue={t("CHB_APPLICANT_NAME")} note={application?.applicantName} />
      <KeyNote keyValue={t("CHB_COMMUNITY_HALL_NAME")} note={application?.bookingSlotDetails[0]?.hallName} />
      <KeyNote keyValue={t("CHB_BOOKING_DATE")} note={application?.slots?.selectslot} />
      <KeyNote keyValue={t("PT_COMMON_TABLE_COL_STATUS_LABEL")} note={t(`CHB_${application?.bookingStatus}`)} />
      <div>
      <Link to={`/digit-ui/citizen/chb/application/${application?.bookingNo}/${application?.tenantId}`}>
        <SubmitBar label={buttonLabel} />
      </Link>
      <Link to={{ pathname: `/digit-ui/citizen/payment/my-bills/${"chb-services"}/${application?.bookingNo}`, state: { tenantId:application?.tenantId, bookingNo :application?.bookingNo } }}  style={{ margin: "20px" }}>
        <SubmitBar label={t("CS_APPLICATION_DETAILS_MAKE_PAYMENT")} />
        </Link>
      </div>
    </Card>
  );
};

export default ChbApplication;
