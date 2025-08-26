import { Card, CardSubHeader, Header, Loader, Row, StatusTable, Toast } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { useParams, useHistory } from "react-router-dom";
import WFApplicationTimeline from "../../pageComponents/WFApplicationTimeline";

/**
 * Detailed view of a citizen's PGR AI application.
 * Displays comprehensive information including application status, complaint details,
 * applicant information.
 */
const PGRApplicationDetails = () => {
  const { t } = useTranslation();
  const history = useHistory();
  const { acknowledgementIds, tenantId } = useParams();
  // Using the PGR AI hook to fetch data for application details
  const { isLoading, isError, error, data } = Digit.Hooks.pgrAi.useSearchPGRAI({
    tenantId,
    filters: { serviceRequestId: acknowledgementIds },
  });
  const serviceWrapper = data?.ServiceWrappers?.[0] || {};
  const service = serviceWrapper?.service || {};
  const [showToast, setShowToast] = useState(null);

  if (isLoading) {
    return <Loader />;
  }

  // Format date for display
  const formatDate = (date) => {
    if (!date) return t("CS_NA");
    const dateObj = new Date(date);
    return `${dateObj.getDate()}/${dateObj.getMonth() + 1}/${dateObj.getFullYear()}`;
  };

  return (
    <React.Fragment>
      <div>
     
        <div className="cardHeaderWithOptions" style={{ marginRight: "auto", maxWidth: "960px" }}>
          <Header styles={{ fontSize: "32px", marginLeft: "10px" }}>{t("PGR_AI_APPLICATION_DETAILS")}</Header>
        </div>
        <Card>
          <StatusTable>
            <Row
              className="border-none"
              label={t("PGR_AI_SERVICE_REQUEST_ID")}
              text={acknowledgementIds || t("CS_NA")}
            />
          </StatusTable>

          <CardSubHeader style={{ fontSize: "24px", marginTop: "16px" }}>{t("PGR_AI_COMPLAINT_DETAILS")}</CardSubHeader>
          <StatusTable>
            <Row
              className="border-none"
              label={t("PGR_AI_SERVICE_TYPE")}
              text={service?.serviceType || t("CS_NA")}
            />
            <Row
              className="border-none"
              label={t("PGR_AI_SERVICE_CODE")}
              text={service?.serviceCode || t("CS_NA")}
            />
            <Row
              className="border-none"
              label={t("PGR_AI_APPLICATION_STATUS")}
              text={t(service?.applicationStatus) || t("CS_NA")}
            />
            <Row
              className="border-none"
              label={t("PGR_AI_FILING_DATE")}
              text={formatDate(service?.auditDetails?.createdTime)}
            />
            <Row
              className="border-none"
              label={t("PGR_AI_INPUT_GRIEVANCE")}
              text={service?.inputGrievance || t("CS_NA")}
            />
          </StatusTable>
          <CardSubHeader style={{ fontSize: "24px" }}>{t("PGR_AI_ADDRESS_DETAILS")}</CardSubHeader>
          <StatusTable>
            <Row className="border-none" label={t("PGR_AI_REGION")} text={service?.address?.region || t("CS_NA")} />
            <Row className="border-none" label={t("PGR_AI_STATE")} text={service?.address?.state || t("CS_NA")} />
            <Row className="border-none" label={t("PGR_AI_PINCODE")} text={service?.address?.pincode || t("CS_NA")} />
            <Row className="border-none" label={t("PGR_AI_LANDMARK")} text={service?.address?.landmark || t("CS_NA")} />
            <Row className="border-none" label={t("PGR_AI_LOCALITY")} text={service?.address?.locality?.code || t("CS_NA")} />
          </StatusTable>

          <WFApplicationTimeline
            application={serviceWrapper}
            id={acknowledgementIds}
            tenantId={tenantId} 
            userType="citizen"
          />
          {showToast && (
            <Toast
              error={showToast.key}
              label={t(showToast.label)}
              style={{ bottom: "0px" }}
              onClose={() => {
                setShowToast(null);
              }}
            />
          )}
        </Card>
      </div>
    </React.Fragment>
  );
};

export default PGRApplicationDetails;