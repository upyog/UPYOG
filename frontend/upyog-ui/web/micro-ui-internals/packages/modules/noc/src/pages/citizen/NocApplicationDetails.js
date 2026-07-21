import { Card, Header, Loader, Row, StatusTable, SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { Link, useParams } from "react-router-dom";

const NOCApplicationDetails = () => {
  const { t } = useTranslation();
  const { id } = useParams();
  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId();

  const { isLoading, data: applicationDetails } = Digit.Hooks.noc.useNOCDetails(t, tenantId, id);
  const application = applicationDetails?.applicationData;

  if (isLoading) {
    return <Loader />;
  }

  const applicationNumber = application?.fireNOCDetails?.applicationNumber || id;
  const buildingName = application?.fireNOCDetails?.buildings?.[0]?.name || t("CS_NA");
  const ownerName = application?.fireNOCDetails?.applicantDetails?.owners?.[0]?.name || t("CS_NA");
  const nocNo = application?.fireNOCNumber || t("CS_NA");
  const status = application?.fireNOCDetails?.status;

  return (
    <React.Fragment>
      <Header>{t("NOC_APP_DETAILS_HEADER", "Application Details")}</Header>
      <Card>
        <StatusTable>
          <Row label={t("NOC_COMMON_TABLE_COL_BUILDING_NAME_LABEL")} text={buildingName} />
          <Row label={t("NOC_COMMON_TABLE_COL_APP_NO_LABEL")} text={applicationNumber} />
          <Row label={t("NOC_COMMON_TABLE_COL_OWN_NAME_LABEL")} text={ownerName} />
          <Row label={t("NOC_COMMON_TABLE_COL_NOC_NO_LABEL")} text={nocNo} />
          <Row label={t("NOC_COMMON_TABLE_COL_STATUS_LABEL")} text={t(`WF_FIRENOC_${status}`)} />
        </StatusTable>

        {status === "PENDINGPAYMENT" && (
          <Link to={{
            pathname: `/upyog-ui/citizen/payment/collect/FIRENOC/${applicationNumber}`,
            state: { tenantId: application?.tenantId || tenantId }
          }}>
            <div style={{ marginTop: "24px" }}>
              <SubmitBar label={t("COMMON_MAKE_PAYMENT")} />
            </div>
          </Link>
        )}
      </Card>
    </React.Fragment>
  );
};

export default NOCApplicationDetails;
