import { Card, KeyNote, SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";

import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";

const WTApplication = ({ application, tenantId, buttonLabel }) => {
  const { t } = useTranslation();
  return (
    <Card>
      <KeyNote keyValue={t("WT_BOOKING_NO")} note={application?.bookingNo} />
      <KeyNote keyValue={t("WT_APPLICANT_NAME")} note={application?.applicantDetail?.name} />
      <KeyNote keyValue={t("WT_MOBILE_NUMBER")} note={t(`${application?.applicantDetail?.mobileNumber}`)} />
      <KeyNote keyValue={t("PT_COMMON_TABLE_COL_STATUS_LABEL")} note={t(`${application?.bookingStatus}`)} />
      <div>
        <Link to={`/digit-ui/citizen/wt/booking/${application?.bookingNo}/${tenantId}`}>
          <SubmitBar label={buttonLabel} />
        </Link> 
      </div>
    </Card>
  );
};

export default WTApplication;
