import { Card, KeyNote, SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";

const NocApplication = ({ application, tenantId }) => {
  const { t } = useTranslation();
  const { path } = Digit.Hooks.useModuleBasePath();

  const applicationNumber = application?.fireNOCDetails?.applicationNumber;
  const buildingName = application?.fireNOCDetails?.buildings?.[0]?.name || t("CS_NA");
  const ownerName = application?.fireNOCDetails?.applicantDetails?.owners?.[0]?.name || t("CS_NA");
  const nocNo = application?.fireNOCNumber || t("CS_NA");
  const status = application?.fireNOCDetails?.status;

  return (
    <Card>
      <KeyNote keyValue={t("NOC_COMMON_TABLE_COL_BUILDING_NAME_LABEL")} note={buildingName} />
      <KeyNote keyValue={t("NOC_COMMON_TABLE_COL_APP_NO_LABEL")} note={applicationNumber} />
      <KeyNote keyValue={t("NOC_COMMON_TABLE_COL_OWN_NAME_LABEL")} note={ownerName} />
      <KeyNote keyValue={t("NOC_COMMON_TABLE_COL_NOC_NO_LABEL")} note={nocNo} />
      <KeyNote keyValue={t("NOC_COMMON_TABLE_COL_STATUS_LABEL")} note={t(`WF_FIRENOC_${status}`)} />

      <Link to={`${path}/application-details/${applicationNumber}`}>
        <SubmitBar label={t("NOC_VIEW_DETAILS")} />
      </Link>
      {status === "PENDINGPAYMENT" && (
        <Link to={{
          pathname: `/upyog-ui/citizen/payment/collect/FIRENOC/${applicationNumber}`,
          state: { tenantId: application?.tenantId }
        }}>
          <div style={{ marginTop: "10px" }}>
            <SubmitBar label={t("COMMON_MAKE_PAYMENT")} />
          </div>
        </Link>
      )}
    </Card>
  );
};

export default NocApplication;
