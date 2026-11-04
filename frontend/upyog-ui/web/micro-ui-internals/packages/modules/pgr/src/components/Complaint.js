import React from "react";
import { useTranslation } from "react-i18next";

import { Card, DateWrap, KeyNote } from "@nudmcdgnpm/digit-ui-react-components";
import { CardSubHeader } from "@nudmcdgnpm/digit-ui-react-components";
import { LOCALIZATION_KEY } from "../constants/Localization";

// import { ConvertTimestampToDate } from "../@upyog/digit-utils/services/date";

const Complaint = ({ data, path }) => {
  let { serviceCode, serviceRequestId, applicationStatus } = data;

  const navigate = Digit.Hooks.useCustomNavigate();
  const { t } = useTranslation();

  const handleClick = () => {
    navigate(`${serviceRequestId}`);
  };

  const closedStatus = ["RESOLVED", "REJECTED", "CLOSEDAFTERREJECTION", "CLOSEDAFTERRESOLUTION"];

  return (
    <React.Fragment>
      <Card onClick={handleClick}>
        <CardSubHeader>{t(`SERVICEDEFS.${serviceCode.toUpperCase()}`)}</CardSubHeader>

        <DateWrap date={Digit.DateUtils.ConvertTimestampToDate(data.auditDetails.createdTime)} />

        <KeyNote keyValue={t(`${LOCALIZATION_KEY.CS_COMMON}_COMPLAINT_NO`)} note={serviceRequestId} />

        <div className={`status-highlight ${closedStatus.includes(applicationStatus) ? "success" : ""}`}>
          <p>{(closedStatus.includes(applicationStatus) ? t("CS_COMMON_CLOSED") : t("CS_COMMON_OPEN")).toUpperCase()}</p>
        </div>

        {t(`${LOCALIZATION_KEY.CS_COMMON}_${applicationStatus}`)}
      </Card>
    </React.Fragment>
  );
};

export default Complaint;
