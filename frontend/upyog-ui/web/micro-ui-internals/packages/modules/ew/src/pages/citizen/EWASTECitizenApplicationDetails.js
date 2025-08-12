import { Card, CardSubHeader, Header, Loader, Row, StatusTable, MultiLink, Toast } from "@upyog/digit-ui-react-components";
import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { useParams } from "react-router-dom";
import getEwAcknowledgementData from "../../utils/getEwAcknowledgementData";
import EWASTEWFApplicationTimeline from "../../pageComponents/EWASTEWFApplicationTimeline";
import ApplicationTable from "../../components/inbox/ApplicationTable";

const EWASTECitizenApplicationDetails = () => {
  const { t } = useTranslation();
  const { requestId, tenantId } = useParams();
  const [showOptions, setShowOptions] = useState(false);
  const [showToast, setShowToast] = useState(null);
  const { data: storeData } = Digit.Hooks.useStore.getInitData();
  const { tenants } = storeData || {};

  const { isLoading, data } = Digit.Hooks.ew.useEWSearch(
    {
      tenantId,
      filters: { requestId: requestId },
    },
  );

  const EwasteApplication = data?.EwasteApplication[0];
  
  if (isLoading) {
    return <Loader />;
  }

  const getAcknowledgementData = async () => {
    const applications = EwasteApplication || {};
    const tenantInfo = tenants.find((tenant) => tenant.code === applications.tenantId);
    const acknowldgementDataAPI = await getEwAcknowledgementData({ ...applications }, tenantInfo, t);
    Digit.Utils.pdf.generateTable(acknowldgementDataAPI);
  };

  const printCertificate = async () => {
    let response = await Digit.PaymentService.generatePdf(tenantId, { EwasteApplication: [data?.EwasteApplication?.[0]] }, "ewasteservicecertificate");
    const fileStore = await Digit.PaymentService.printReciept(tenantId, { fileStoreIds: response.filestoreIds[0] });
    window.open(fileStore[response?.filestoreIds[0]], "_blank");
  };

  let dowloadOptions = [];

  dowloadOptions.push({
    label: t("EWASTE_DOWNLOAD_ACK_FORM"),
    onClick: () => getAcknowledgementData(),
  });

  if (EwasteApplication?.requestStatus === "REQUESTCOMPLETED") {
    dowloadOptions.push({
      label: t("EW_CERTIFICATE"),
      onClick: () => printCertificate(),
    });
  }


  const productcolumns = [
    { Header: t("PRODUCT_NAME"), accessor: "name" },
    { Header: t("PRODUCT_QUANTITY"), accessor: "quantity" },
    { Header: t("UNIT_PRICE"), accessor: "unit_price" },
    { Header: t("TOTAL_PRODUCT_PRICE"), accessor: "total_price" },
  ];

  const productRows = EwasteApplication?.ewasteDetails?.map((product) => (
    {
      name: t(product.productName),
      quantity: product.quantity,
      unit_price: product.price/product.quantity,
      total_price: product.price,
    }
  )) || [];



  return (
    <React.Fragment>
      <div>
        <div className="cardHeaderWithOptions" style={{ marginRight: "auto", maxWidth: "960px" }}>
          <Header styles={{ fontSize: "32px", marginLeft: "10px" }}>{t("EW_APPLICATION_DETAILS")}</Header>
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
            <Row
              className="border-none"
              label={t("EW_REQUEST_ID")}
              text={EwasteApplication?.requestId}
            />
          </StatusTable>

          <CardSubHeader style={{ fontSize: "24px" }}>{t("EW_ADDRESS_DETAILS")}</CardSubHeader>
          <StatusTable>
            <Row className="border-none" label={t("EW_PINCODE")} text={EwasteApplication?.address?.pincode || t("CS_NA")} />
            <Row className="border-none" label={t("EW_CITY")} text={EwasteApplication?.address?.city?.code || t("CS_NA")} /> 
            <Row className="border-none" label={t("EWASTE_STREET_NAME")} text={EwasteApplication?.address?.street || t("CS_NA")} />
            <Row className="border-none" label={t("EWASTE_HOUSE_NO")} text={EwasteApplication?.address?.doorNo || t("CS_NA")} />
            <Row className="border-none" label={t("EWASTE_HOUSE_NAME")} text={EwasteApplication?.address?.buildingName || t("CS_NA")} />
            <Row className="border-none" label={t("EWASTE_ADDRESS_LINE1")} text={EwasteApplication?.address?.addressLine1 || t("CS_NA")} />
            <Row className="border-none" label={t("EWASTE_ADDRESS_LINE2")} text={EwasteApplication?.address?.addressLine2 || t("CS_NA")} />
            <Row className="border-none" label={t("EWASTE_LANDMARK")} text={EwasteApplication?.address?.landmark || t("CS_NA")} />
          </StatusTable>

          <CardSubHeader style={{ fontSize: "24px" }}>{t("EW_APPLICANT_DETAILS")}</CardSubHeader>
          <StatusTable>
            <Row className="border-none" label={t("EWASTE_APPLICANT_NAME")} text={EwasteApplication?.applicant?.applicantName || t("CS_NA")} />
            <Row className="border-none" label={t("EW_MOBILE_NUMBER")} text={EwasteApplication?.applicant?.mobileNumber || t("CS_NA")} />
            <Row className="border-none" label={t("EW_EMAIL")} text={EwasteApplication?.applicant?.emailId || t("CS_NA")} />
          </StatusTable>

          <CardSubHeader style={{ fontSize: "24px" }}>{t("EW_PRODUCT_DETAILS")}</CardSubHeader>
          <div style={{ border: "2px solid #ccc", borderRadius: "8px", padding: "20px", margin: "20px 0" }}>
          <ApplicationTable
            t={t}
            data={productRows}
            columns={productcolumns}
            getCellProps={() => ({
              style: {
                minWidth: "150px",
                padding: "10px",
                fontSize: "16px",
                paddingLeft: "20px",
              },
            })}
            isPaginationRequired={false}
            totalRecords={productRows.length}
          />
          </div>

          <br></br>
          <CardSubHeader style={{ fontSize: "24px" }}>{t("ES_EW_ACTION_TRANSACTION_ID")}</CardSubHeader>
          <StatusTable>
            {EwasteApplication.calculatedAmount && <Row className="border-none" label={t("EWASTE_NET_PRICE")} text={"₹ " + EwasteApplication?.calculatedAmount} />}
            {EwasteApplication.finalAmount && <Row className="border-none" label={t("ES_EW_ACTION_FINALAMOUNT")} text={"₹ " + EwasteApplication?.finalAmount} />}
            {EwasteApplication.transactionId && <Row className="border-none" label={t("ES_EW_ACTION_TRANSACTION_ID")} text={EwasteApplication?.transactionId} />}
            {EwasteApplication.pickUpDate && <Row className="border-none" label={t("EW_PICKUP_DATE")} text={EwasteApplication?.pickUpDate} />}
          </StatusTable>

          <EWASTEWFApplicationTimeline application={EwasteApplication} id={EwasteApplication?.requestId} userType={"citizen"} />
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

export default EWASTECitizenApplicationDetails;








