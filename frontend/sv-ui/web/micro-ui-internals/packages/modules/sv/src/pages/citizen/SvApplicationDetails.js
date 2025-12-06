import { Card, CardSubHeader, Header, LinkButton, Loader, Row, StatusTable, MultiLink, PopUp, Toast, SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useEffect, useMemo, useState, Fragment } from "react";
import { useTranslation } from "react-i18next";
import { useParams } from "react-router-dom";
import ViewTimeline from "../../components/ViewTimeline";
import get from "lodash/get";
import getSVAcknowledgementData from "../../utils/getSVAcknowledgementData";
import { checkForNA,SVDocumnetPreview, getOrderDocuments } from "../../utils";


const SvApplicationDetails = () => {
  const { t } = useTranslation();
  const { applicationNo, tenantId } = useParams();
  const [showOptions, setShowOptions] = useState(false);
  const [popup, setpopup] = useState(false);
  const [showToast, setShowToast] = useState(null);
  const { data: storeData } = Digit.Hooks.useStore.getInitData();
  const { tenants } = storeData || {};

  const { isLoading, data } = Digit.Hooks.sv.useSvSearchApplication(
    {
      tenantId,
      filters: { applicationNumber: applicationNo, isDraftApplication: false },
    },
  );

  const [billData, setBillData] = useState(null);
  const SVDetail = get(data, "SVDetail", []);
  let streetVendingDetails = (SVDetail && SVDetail.length > 0 && SVDetail[0]) || {};
  const application = streetVendingDetails;
  sessionStorage.setItem("streetvending", JSON.stringify(application));
  const [loading, setLoading] = useState(false);

  const fetchBillData = async () => {
    setLoading(true);
    const result = await Digit.PaymentService.fetchBill(tenantId, { businessService: "sv-services", consumerCode: applicationNo });
    setBillData(result);
    setLoading(false);
  };
  useEffect(() => {
    fetchBillData();
  }, [tenantId, applicationNo]);

  const { data: reciept_data, isLoading: recieptDataLoading } = Digit.Hooks.useRecieptSearch(
    {
      tenantId: tenantId,
      businessService: "sv-services",
      consumerCodes: applicationNo,
      isEmployee: false,
    },
    { enabled: applicationNo ? true : false }
  );
    // Step 1: Prepare document list safely
  const getApplicationDocs = streetVendingDetails?.documentDetails?.map((doc) => ({
    ...doc,
    module: "StreetVending",
  })) || [];

  // Step 2: Fetch PDF details only if documents exist
  const { data: pdfDetails } = Digit.Hooks.useDocumentSearch(getApplicationDocs, {enabled: getApplicationDocs.length > 0});

  // Step 3: Extract only StreetVending PDFs
  const applicationDocs = pdfDetails?.pdfFiles?.filter((pdf) => pdf?.module === "StreetVending") || [];

  if (!streetVendingDetails.workflow) {
    let workflow = {
      id: null,
      tenantId: tenantId,
      businessService: "street-vending",
      businessId: application?.applicationNo,
      action: "",
      moduleName: "sv-services",
      state: null,
      comment: null,
      documents: null,
      assignes: null,
    };
    streetVendingDetails.workflow = workflow;
  }


  if (isLoading) {
    return <Loader />;
  }

  const getAcknowledgementData = async () => {
    const applications = application || {};
    const tenantInfo = tenants.find((tenant) => tenant.code === applications.tenantId);
    const acknowldgementDataAPI = await getSVAcknowledgementData({ ...applications }, tenantInfo, t);
    Digit.Utils.pdf.generate(acknowldgementDataAPI);
  };


  async function getRecieptSearch({ tenantId, payments, ...params }) {
    let response = { filestoreIds: [payments?.fileStoreId] };
    response = await Digit.PaymentService.generatePdf(tenantId, { Payments: [{ ...payments }] }, "svservice-receipt");
    const fileStore = await Digit.PaymentService.printReciept(tenantId, { fileStoreIds: response.filestoreIds[0] });
    window.open(fileStore[response?.filestoreIds[0]], "_blank");
  };

  const printCertificate = async () => {
    let response = await Digit.PaymentService.generatePdf(tenantId, { SVDetail: [data?.SVDetail?.[0]] }, "svcertificate");
    const fileStore = await Digit.PaymentService.printReciept(tenantId, { fileStoreIds: response.filestoreIds[0] });
    window.open(fileStore[response?.filestoreIds[0]], "_blank");
  };
  const printIdCard = async () => {
    let response = await Digit.PaymentService.generatePdf(tenantId, { SVDetail: [data?.SVDetail?.[0]] }, "svidentitycard");
    const fileStore = await Digit.PaymentService.printReciept(tenantId, { fileStoreIds: response.filestoreIds[0] });
    window.open(fileStore[response?.filestoreIds[0]], "_blank");
  };

  let dowloadOptions = [];
  dowloadOptions.push({
    label: t("SV_ACKNOWLEDGEMENT"),
    onClick: () => getAcknowledgementData(),
  });


  if (reciept_data && reciept_data?.Payments.length > 0 && recieptDataLoading == false)
    dowloadOptions.push({
      label: t("SV_FEE_RECIEPT"),
      onClick: () => getRecieptSearch({ tenantId: reciept_data?.Payments[0]?.tenantId, payments: reciept_data?.Payments[0] }),
    });
  if (reciept_data && reciept_data?.Payments.length > 0 && recieptDataLoading == false)
    dowloadOptions.push({
      label: t("SV_CERTIFICATE"),
      onClick: () => printCertificate(),
    });

  if (reciept_data && reciept_data?.Payments.length > 0 && recieptDataLoading == false)
    dowloadOptions.push({
      label: t("SV_ID_CARD"),
      onClick: () => printIdCard(),
    });



  return (
    <React.Fragment>
      <div>
        <div className="cardHeaderWithOptions" style={{ marginRight: "auto", maxWidth: "960px" }}>
          <Header styles={{ fontSize: "32px" }}>{t("SV_APPLICATION_DETAILS")}</Header>
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
              label={t("SV_APPLICATION_NUMBER")}
              text={streetVendingDetails?.applicationNo}
            />
            <Row
              className="border-none"
              label={t("SV_VALIDITY_DATE")}
              text={streetVendingDetails?.validityDate}
            />
            <Row
              className="border-none"
              label={t("SV_CERTIFICATE_NUMBER")}
              text={streetVendingDetails?.certificateNo}
            />
          </StatusTable>

          <CardSubHeader style={{ fontSize: "24px" }}>{t("SV_VENDOR_PERSONAL_DETAILS")}</CardSubHeader>
          <StatusTable>
            <Row className="border-none" label={t("SV_VENDOR_NAME")} text={streetVendingDetails?.vendorDetail?.[0]?.name || t("CS_NA")} />
            <Row className="border-none" label={t("SV_FATHER_NAME")} text={streetVendingDetails?.vendorDetail?.[0]?.fatherName || t("CS_NA")} />
            <Row className="border-none" label={t("SV_REGISTERED_MOB_NUMBER")} text={streetVendingDetails?.vendorDetail?.[0]?.mobileNo || t("CS_NA")} />
            <Row className="border-none" label={t("SV_DATE_OF_BIRTH")} text={streetVendingDetails?.vendorDetail?.[0]?.dob || t("CS_NA")} />
            <Row className="border-none" label={t("SV_EMAIL")} text={streetVendingDetails?.vendorDetail?.[0]?.emailId || t("CS_NA")} />
          </StatusTable>
          <CardSubHeader style={{ fontSize: "24px" }}>{t("SV_ADDRESS_DETAILS")}</CardSubHeader>
          <StatusTable>
            <Row className="border-none" label={t("SV_HOUSE_NO")} text={streetVendingDetails?.addressDetails?.[0]?.houseNo || t("CS_NA")} />
            <Row className="border-none" label={t("SV_ADDRESS_LINE1")} text={streetVendingDetails?.addressDetails?.[0]?.addressLine1 || t("CS_NA")} />
            <Row className="border-none" label={t("SV_ADDRESS_LINE2")} text={streetVendingDetails?.addressDetails?.[0]?.addressLine2 || t("CS_NA")} />
            <Row className="border-none" label={t("SV_ADDRESS_PINCODE")} text={streetVendingDetails?.addressDetails?.[0]?.pincode || t("CS_NA")} />
            <Row className="border-none" label={t("SV_CITY")} text={streetVendingDetails?.addressDetails?.[0]?.city || t("CS_NA")} />
            <Row className="border-none" label={t("SV_LOCALITY")} text={streetVendingDetails?.addressDetails?.[0]?.locality || t("CS_NA")} />
          </StatusTable>



          <CardSubHeader style={{ fontSize: "24px" }}>{t("SV_VENDOR_BUSINESS_DETAILS")}</CardSubHeader>
          <StatusTable>
            <Row className="border-none" label={t("SV_VENDING_ZONES")} text={streetVendingDetails?.vendingZoneValue || t("CS_NA")} />
            <Row className="border-none" label={t("SV_VENDING_LOCALITY")} text={streetVendingDetails?.localityValue || t("CS_NA")} />
            <Row className="border-none" label={t("SV_VENDING_TYPE")} text={streetVendingDetails?.vendingActivity || t("CS_NA")} />
            {streetVendingDetails?.vendingActivity === "STATIONARY" && (
            <Row className="border-none" label={t("SV_AREA_REQUIRED")} text={streetVendingDetails?.vendingArea || t("CS_NA")} />)}
            <Row className="border-none" label={t("SV_LOCAL_AUTHORITY_NAME")} text={streetVendingDetails?.localAuthorityName || t("CS_NA")} />
            <Row className="border-none" label={t("SV_CATEGORY")} text={t(streetVendingDetails?.disabilityStatus) || t("CS_NA")} />
          </StatusTable>

          <CardSubHeader style={{ fontSize: "24px" }}>{t("SV_ADDITIONAL_DETAILS")}</CardSubHeader>
          <StatusTable>
            {streetVendingDetails?.benificiaryOfSocialSchemes[0]?.schemeName ?
              streetVendingDetails?.benificiaryOfSocialSchemes.map((item) => (
                <>
                  <Row
                    className="border-none"
                    label={t("SV_BENEFICIARY_SCHEMES")}
                    text={`${t(checkForNA(item?.schemeName))}`}
                  />

                  < Row
                    className="border-none"
                    label={t("SV_ENROLLMENT_APPLICATION_NUMBER")}
                    text={`${t(checkForNA(item?.enrollmentId))}`}
                  />
                </>
              ))
              : null}
          </StatusTable>

          <br></br>
          <CardSubHeader>{t("SV_DOCUMENT_DETAILS_LABEL")}</CardSubHeader>
          {<SVDocumnetPreview documents={getOrderDocuments(applicationDocs)} svgStyles={{}} isSendBackFlow={false} titleStyles={{ fontSize: "18px", "fontWeight": 700, marginBottom: "10px" }} />}

          <ViewTimeline application={application} id={application?.applicationNo} userType={"citizen"} />
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

export default SvApplicationDetails;