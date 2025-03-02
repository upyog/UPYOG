import { Card, CardSubHeader, CardSectionHeader, Header, Loader, Row, StatusTable, MultiLink } from "@upyog/digit-ui-react-components";
import React, { useEffect, useMemo, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory, useParams, Link } from "react-router-dom";
import ADSDocument from "../../pageComponents/ADSDocument";
import ApplicationTable from "../../components/ApplicationTable";
import { pdfDownloadLink } from "../../utils";

import get from "lodash/get";
import { size } from "lodash";

/*
 * ADSApplicationDetails includes hooks for data fetching, translation, and state management.
 * The component displays various application details, such as applicant information,
 * booking data, and related documents, using components  ApplicationTable.
 */
const ADSApplicationDetails = () => {
  const { t } = useTranslation();
  const history = useHistory();
  const { acknowledgementIds, tenantId } = useParams();
  const [acknowldgementData, setAcknowldgementData] = useState([]);
  const [showOptions, setShowOptions] = useState(false);
  const [popup, setpopup] = useState(false);
  const [showToast, setShowToast] = useState(null);
  // const tenantId = Digit.ULBService.getCurrentTenantId();
  const { data: storeData } = Digit.Hooks.useStore.getInitData();
  const { tenants } = storeData || {};

  const { isLoading, isError, error, data, refetch } = Digit.Hooks.ads.useADSSearch({
    tenantId,
    filters: { bookingNo: acknowledgementIds },
  });
  const mutation = Digit.Hooks.ads.useADSCreateAPI(tenantId, false);

  const [billData, setBillData] = useState(null);

  const BookingApplication = get(data, "bookingApplication", []);
  const adsId = get(data, "bookingApplication[0].bookingNo", []);

  let ads_details = (BookingApplication && BookingApplication.length > 0 && BookingApplication[0]) || {};
  const application = ads_details;

  sessionStorage.setItem("ads", JSON.stringify(application));

  const [loading, setLoading] = useState(false);

  const fetchBillData = async () => {
    setLoading(true);
    const result = await Digit.PaymentService.fetchBill(tenantId, { businessService: "adv-services", consumerCode: acknowledgementIds });

    setBillData(result);
    setLoading(false);
  };
  useEffect(() => {
    fetchBillData();
  }, [tenantId, acknowledgementIds]);

  const { isLoading: auditDataLoading, isError: isAuditError, data: auditResponse } = Digit.Hooks.ads.useADSSearch(
    {
      tenantId,
      filters: { bookingNo: adsId, audit: true },
    },
    {
      enabled: true,
    }
  );

  const { data: reciept_data, isLoading: recieptDataLoading } = Digit.Hooks.useRecieptSearch(
    {
      tenantId: tenantId,
      businessService: "adv-services",
      consumerCodes: acknowledgementIds,
      isEmployee: false,
    },
    { enabled: acknowledgementIds ? true : false }
  );

  let docs = [];
  docs = application?.documents;

  if (isLoading || auditDataLoading) {
    return <Loader />;
  }

  let documentDate = t("CS_NA");
  if (ads_details?.additionalDetails?.documentDate) {
    const date = new Date(ads_details?.additionalDetails?.documentDate);
    const month = Digit.Utils.date.monthNames[date.getMonth()];
    documentDate = `${date.getDate()} ${month} ${date.getFullYear()}`;
  }
// in progress
  async function getRecieptSearch({ tenantId, payments, ...params }) {
    let application = data?.bookingApplication?.[0];
    let fileStoreId = application?.paymentReceiptFilestoreId;
    if (!fileStoreId) {
      let response = { filestoreIds: [payments?.fileStoreId] };
      response = await Digit.PaymentService.generatePdf(tenantId, { Payments: [{ ...payments }] }, "advservice-receipt");
      const updatedApplication = {
        ...application,
        paymentReceiptFilestoreId: response?.filestoreIds[0],
      };
      await mutation.mutateAsync({
        bookingApplication: updatedApplication,
      });
      fileStoreId = response?.filestoreIds[0];
      refetch();
    }
    const fileStore = await Digit.PaymentService.printReciept(tenantId, { fileStoreIds: fileStoreId });
    window.open(fileStore[fileStoreId], "_blank");
  }
  async function getPermissionLetter({ tenantId, payments, ...params }) {
    let application = data?.bookingApplication?.[0];
    let fileStoreId = application?.permissionLetterFilestoreId;
    if (!fileStoreId) {
      const response = await Digit.PaymentService.generatePdf(tenantId, { bookingApplication: [application] }, "advpermissionletter");
      const updatedApplication = {
        ...application,
        permissionLetterFilestoreId: response?.filestoreIds[0],
      };
      await mutation.mutateAsync({
        bookingApplication: updatedApplication,
      });
      fileStoreId = response?.filestoreIds[0];
      refetch();
    }
    const fileStore = await Digit.PaymentService.printReciept(tenantId, { fileStoreIds: fileStoreId });
    window.open(fileStore[fileStoreId], "_blank");
  }

  const handleDownload = async (document, tenantid) => {
    let tenantId = tenantid ? tenantid : tenantId;
    const res = await Digit.UploadServices.Filefetch([document?.fileStoreId], tenantId);
    let documentLink = pdfDownloadLink(res.data, document?.fileStoreId);
    window.open(documentLink, "_blank");
  };

  let dowloadOptions = [];

  if (reciept_data && reciept_data?.Payments.length > 0 && recieptDataLoading == false)
    dowloadOptions.push({
      label: t("ADS_FEE_RECEIPT"),
      onClick: () => getRecieptSearch({ tenantId: reciept_data?.Payments[0]?.tenantId, payments: reciept_data?.Payments[0] }),
    });
  if (reciept_data && reciept_data?.Payments.length > 0 && recieptDataLoading == false)
    dowloadOptions.push({
      label: t("ADS_PERMISSION_LETTER"),
      onClick: () => getPermissionLetter({ tenantId: reciept_data?.Payments[0]?.tenantId, payments: reciept_data?.Payments[0] }),
    });

  const columns = [
    { Header: `${t("ADS_TYPE")}`, accessor: "addType" },
    { Header: `${t("ADS_FACE_AREA")}`, accessor: "faceArea" },
    { Header: `${t("ADS_NIGHT_LIGHT")}`, accessor: "nightLight" },
    { Header: `${t("ADS_BOOKING_DATE")}`, accessor: "bookingDate" },
    { Header: `${t("PT_COMMON_TABLE_COL_STATUS_LABEL")}`, accessor: "bookingStatus" },
  ];
  const adslistRows =
    ads_details?.cartDetails?.map((slot) => ({
      addType: `${t(slot.addType)}`,
      faceArea: `${t(slot.faceArea)}`,
      nightLight: `${t(slot.nightLight===true?"Yes":"No")}`,
      bookingDate: `${t(slot.bookingDate)}`,
      bookingStatus: `${t(slot.status)}`,
    })) || [];
  return (
    <React.Fragment>
      <div>
        <div className="cardHeaderWithOptions" style={{ marginRight: "auto", maxWidth: "960px" }}>
          <Header styles={{ fontSize: "32px" }}>{t("ADS_BOOKING_DETAILS")}</Header>
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
            <Row className="border-none" label={t("ADS_BOOKING_NO")} text={ads_details?.bookingNo} />
          </StatusTable>
          <CardSubHeader style={{ fontSize: "24px" }}>{t("ADS_PAYMENT_DETAILS")}</CardSubHeader>
          <StatusTable>
            <Row 
              className="border-none" 
              label={t("ADS_TOTAL_AMOUNT")} 
              text={
                billData?.Bill?.[0]?.totalAmount > 0
                  ? (
                      <span >
                       ₹ {billData?.Bill?.[0]?.totalAmount || t("CS_NA")}  <strong style={{ color: '#a82227' }}>({t("PENDING_PAYMENT")})</strong>
                      </span>
                    )
                  : (
                      <span >
                      ₹ {reciept_data?.Payments?.[0]?.totalAmountPaid || t("CS_NA")}   <strong style={{ color: 'green' }}>({t("PAYMENT_PAID")})</strong>
                      </span>
                    )
              }
            />          
            </StatusTable>
          <CardSubHeader style={{ fontSize: "24px" }}>{t("ADS_APPLICANT_DETAILS")}</CardSubHeader>
          <StatusTable>
            <Row className="border-none" label={t("ADS_APPLICANT_NAME")} text={ads_details?.applicantDetail?.applicantName || t("CS_NA")} />
            <Row className="border-none" label={t("ADS_MOBILE_NUMBER")} text={ads_details?.applicantDetail?.applicantMobileNo || t("CS_NA")} />
            <Row
              className="border-none"
              label={t("ADS_ALT_MOBILE_NUMBER")}
              text={ads_details?.applicantDetail?.applicantAlternateMobileNo || t("CS_NA")}
            />
            <Row className="border-none" label={t("ADS_EMAIL_ID")} text={ads_details?.applicantDetail?.applicantEmailId || t("CS_NA")} />
          </StatusTable>

          <CardSubHeader style={{ fontSize: "24px" }}>{t("ADS_ADDRESS_DETAILS")}</CardSubHeader>
          <StatusTable>
            <Row className="border-none" label={t("ADS_HOUSE_NO")} text={ads_details?.address?.houseNo || t("CS_NA")} />
            <Row className="border-none" label={t("ADS_HOUSE_NAME")} text={ads_details?.address?.houseName || t("CS_NA")} />
            <Row className="border-none" label={t("ADS_STREET_NAME")} text={ads_details?.address?.streetName || t("CS_NA")} />
            <Row className="border-none" label={t("ADS_ADDRESS_LINE1")} text={ads_details?.address?.addressLine1 || t("CS_NA")} />
            <Row className="border-none" label={t("ADS_ADDRESS_LINE2")} text={ads_details?.address?.addressLine2 || t("CS_NA")} />
            <Row className="border-none" label={t("ADS_LANDMARK")} text={ads_details?.address?.landmark || t("CS_NA")} />
            <Row className="border-none" label={t("ADS_CITY")} text={ads_details?.address?.city || t("CS_NA")} />
            <Row className="border-none" label={t("ADS_LOCALITY")} text={ads_details?.address?.locality || t("CS_NA")} />
            <Row className="border-none" label={t("ADS_ADDRESS_PINCODE")} text={ads_details?.address?.pincode || t("CS_NA")} />
          </StatusTable>
          <CardSubHeader style={{ fontSize: "24px" }}>{t("ADS_CART_DETAILS")}</CardSubHeader>
          <ApplicationTable
            t={t}
            data={adslistRows}
            columns={columns}
            getCellProps={(cellInfo) => ({
              style: {
                minWidth: "150px",
                padding: "10px",
                fontSize: "16px",
                paddingLeft: "20px",
              },
            })}
            isPaginationRequired={false}
            totalRecords={adslistRows.length}
          />
          <CardSubHeader style={{ fontSize: "24px" }}>{t("ADS_DOCUMENTS_DETAILS")}</CardSubHeader>
          <StatusTable>
              {docs.map((doc, index) => (
                <ADSDocument value={docs} Code={doc?.documentType} index={index} />
              ))}
          </StatusTable>
        </Card>
      </div>
    </React.Fragment>
  );
};

export default ADSApplicationDetails;
