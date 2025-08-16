import { Card, KeyNote, SubmitBar } from "@upyog/digit-ui-react-components";
import React from "react";

import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";
import { APPLICATION_PATH } from "../../../utils";

const WTApplication = ({ application, tenantId, buttonLabel }) => {
 let bookingCode=application?.bookingNo.split("-")[0]; // for selecting the Module code from booking number
  const { t } = useTranslation();
  return (
    <Card>
      <KeyNote keyValue={t("BOOKING_NO")} note={application?.bookingNo} />
      <KeyNote keyValue={t("APPLICANT_NAME")} note={application?.applicantDetail?.name} />
      <KeyNote keyValue={t("MOBILE_NUMBER")} note={t(`${application?.applicantDetail?.mobileNumber}`)} />
      <KeyNote keyValue={t("LOCALITY")} note={t(`${application?.localityCode}`)} />
      <KeyNote keyValue={t("PT_COMMON_TABLE_COL_STATUS_LABEL")} note={t(`${application?.bookingStatus}`)} />
      <div>
      {bookingCode==="WT" &&(
        <Link to={`${APPLICATION_PATH}/citizen/wt/booking/waterTanker/${application?.bookingNo}/${tenantId}`}>
          <SubmitBar label={buttonLabel} />
        </Link> 
      )}
      {bookingCode==="MT" &&(
        <Link to={`${APPLICATION_PATH}/citizen/wt/booking/mobileToilet/${application?.bookingNo}/${tenantId}`}>
          <SubmitBar label={buttonLabel} />
        </Link> 
      )}
      {bookingCode==="TP" &&(
        <Link to={`${APPLICATION_PATH}/citizen/wt/booking/treePruning/${application?.bookingNo}/${tenantId}`}>
          <SubmitBar label={buttonLabel} />
        </Link>
      )}
      </div>
    </Card>
  );
};

export default WTApplication;
