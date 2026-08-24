/**
 * Displays detailed information for a specific NOC application by application ID.
 * Fetches application data using useNOCDetails hook, renders status details in a table,
 * and provides a payment action button if payment is pending.
 */
import { Card, Header, Loader, Row, StatusTable, SubmitBar, CardSubHeader, Table, MultiLink } from "@nudmcdgnpm/digit-ui-react-components";
import React,{useState} from "react";
import { useTranslation } from "react-i18next";
import { Link, useParams } from "react-router-dom";
import "../../css/noc-inline.css";
import { convertEpochToDate } from "../../utils";

const NOCApplicationDetails = () => {
  const { t } = useTranslation();
  const { id } = useParams();
  const [showOptions, setShowOptions] = useState(false);
  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId();
  const { isLoading, data: applicationDetails } = Digit.Hooks.noc.useFireNOCSearch(tenantId, { applicationNumber: id });
  const application = applicationDetails?.FireNOCs?.[0]?.fireNOCDetails;

  const uomColumns = [
  { Header: t("NOC_UOM_CODE"), accessor: "code", Cell: ({ value }) => t(`${value}`) },
  { Header: t("NOC_UOM_VALUE"), accessor: "value" },
  ];

  const uomData = application?.buildings?.[0]?.uoms || [];

  const applicationNumber = application?.applicationNumber;
  const buildingName = application?.buildings?.[0]?.name || t("CS_NA");
  const ownerName = application?.applicantDetails?.owners?.[0]?.name || t("CS_NA");
  const status = application?.status;
  const propertyId = application?.propertyDetails?.propertyId;
  const nocNo = applicationDetails?.FireNOCs?.[0]?.fireNOCNumber 

  const { data: reciept_data, isLoading: recieptDataLoading } = Digit.Hooks.useRecieptSearch(
    {
      tenantId: tenantId,
      businessService: "FIRENOC",
      consumerCodes: application?.applicationNumber,
      isEmployee: false,
    },
    { enabled: application?.applicationNumber ? true : false }
  );


  async function getRecieptSearch({ tenantId, payments, ...params }) {
    let response = { filestoreIds: [payments?.fileStoreId] };
    response = await Digit.PaymentService.generatePdf(tenantId, { Payments: [{ ...payments }] }, "consolidatedreceipt");
    const fileStore = await Digit.PaymentService.printReciept(tenantId, { fileStoreIds: response.filestoreIds[0] });
    window.open(fileStore[response?.filestoreIds[0]], "_blank");
  };

  const printCertificate = async () => {
    let response = await Digit.PaymentService.generatePdf(tenantId, { FireNOCs: [applicationDetails?.FireNOCs?.[0]] }, "firenoccertificate");
    const fileStore = await Digit.PaymentService.printReciept(tenantId, { fileStoreIds: response.filestoreIds[0] });
    window.open(fileStore[response?.filestoreIds[0]], "_blank");
  };

  let dowloadOptions = [];

  if (reciept_data && reciept_data?.Payments.length > 0 && recieptDataLoading == false)
    dowloadOptions.push({
      label: t("FN_FEE_RECIEPT"),
      onClick: () => getRecieptSearch({ tenantId: reciept_data?.Payments[0]?.tenantId, payments: reciept_data?.Payments[0] }),
    });

    if (status==="APPROVED")
    dowloadOptions.push({
      label: t("FN_CERTIFICATE"),
      onClick: () => printCertificate(),
    });

if (isLoading) return <Loader />;

  return (
    <React.Fragment>
    <div>
      <div className="cardHeaderWithOptions" style={{ marginRight: "auto", maxWidth: "960px" }}>
      <Header>{t("FN_APPLICATION_DETAILS")}</Header>
      {dowloadOptions && dowloadOptions.length > 0 && (
            <MultiLink
              className="multilinkWrapper"
              onHeadClick={() => setShowOptions(!showOptions)}
              displayOptions={showOptions}
              options={dowloadOptions}
            />
          )}
      </div>
      <Card>
        <StatusTable>
          <Row label={t("NOC_COMMON_TABLE_COL_BUILDING_NAME_LABEL")} text={buildingName} />
          <Row label={t("NOC_COMMON_TABLE_COL_APP_NO_LABEL")} text={applicationNumber} />
          <Row label={t("NOC_COMMON_TABLE_COL_NOC_NO_LABEL")} text={nocNo} />
          <Row label={t("NOC_PROPERTY_ID_LABEL")} text={propertyId} />
          <Row label={t("NOC_COMMON_TABLE_COL_OWN_NAME_LABEL")} text={ownerName} />
          <Row label={t("NOC_COMMON_TABLE_COL_STATUS_LABEL")} text={t(`WF_FIRENOC_${status}`)} />
        </StatusTable>
        {application?.issuedDate && application?.validFrom && application?.validTo &&
        <StatusTable>
          <Row label={t("NOC_ISSUE_DATE")} text={convertEpochToDate(application?.issuedDate)} />
          <Row label={t("NOC_VALIDITY_FROM")} text={convertEpochToDate(application?.validFrom)} />
          <Row label={t("NOC_VALIDITY_TO")} text={convertEpochToDate(application?.validTo)} />
        </StatusTable>
          }

        {uomData.length > 0 && (
          <React.Fragment>
            <CardSubHeader className="noc-subheader-lg">{t("FN_BUILDING_DETAILS")}</CardSubHeader>
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
      </div>
    </React.Fragment>
  );
};

export default NOCApplicationDetails;
