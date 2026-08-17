/**
 * Displays detailed information for a specific NOC application by application ID.
 * Fetches application data using useNOCDetails hook, renders status details in a table,
 * and provides a payment action button if payment is pending.
 */
import { Card, Header, Loader, Row, StatusTable, SubmitBar, CardSubHeader, Table } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { Link, useParams } from "react-router-dom";
import "../../css/noc-inline.css";

const NOCApplicationDetails = () => {
  const { t } = useTranslation();
  const { id } = useParams();
  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId();
  const { isLoading, data: applicationDetails } = Digit.Hooks.noc.useFireNOCSearch(tenantId, { applicationNumber: id });

  const application = applicationDetails?.FireNOCs?.[0]?.fireNOCDetails;

  const uomColumns = [
  { Header: t("NOC_UOM_CODE"), accessor: "code", Cell: ({ value }) => t(`NOC_UOM_${value}`) },
  { Header: t("NOC_UOM_VALUE"), accessor: "value" },
  ];

  const uomData = application?.buildings?.[0]?.uoms || [];


  if (isLoading) return <Loader />;

  const applicationNumber = application?.applicationNumber;
  const buildingName = application?.buildings?.[0]?.name || t("CS_NA");
  const ownerName = application?.applicantDetails?.owners?.[0]?.name || t("CS_NA");
  const status = application?.status;
  const propertyId = application?.propertyDetails?.propertyId;

  return (
    <React.Fragment>
      <Header>{t("NOC_APP_DETAILS_HEADER")}</Header>
      <Card>
        <StatusTable>
          <Row label={t("NOC_COMMON_TABLE_COL_BUILDING_NAME_LABEL")} text={buildingName} />
          <Row label={t("NOC_COMMON_TABLE_COL_APP_NO_LABEL")} text={applicationNumber} />
          <Row label={t("NOC_PROPERTY_ID_LABEL")} text={propertyId} />
          <Row label={t("NOC_COMMON_TABLE_COL_OWN_NAME_LABEL")} text={ownerName} />
          <Row label={t("NOC_COMMON_TABLE_COL_STATUS_LABEL")} text={t(`WF_FIRENOC_${status}`)} />
        </StatusTable>

        {uomData.length > 0 && (
          <React.Fragment>
            <CardSubHeader className="noc-subheader-lg">{t("NOC_BUILDING_UOM_DETAILS")}</CardSubHeader>
            <Table
              t={t}
              data={uomData}
              columns={uomColumns}
              getCellProps={() => ({})}
              isPaginationRequired={false}
              disableSort={true}
            />
          </React.Fragment>
        )}

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
