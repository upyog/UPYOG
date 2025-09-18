import {
  Card,
  CardSubHeader,
  Header,
  Loader,
  Row,
  StatusTable,
  MultiLink,
  Toast
} from "@upyog/digit-ui-react-components";
import React, {useState } from "react";
import { useTranslation } from "react-i18next";

import {useParams} from "react-router-dom";

import get from "lodash/get";
import { size } from "lodash";
import WFApplicationTimeline from "../../pageComponents/WFApplicationTimeline";
import { convertTo12HourFormat, formatDate } from "../../utils";
import getWTAcknowledgementData from "../../utils/getWTAcknowledgementData";

/**
 * `WTApplicationDetails` is a React component that fetches and displays detailed information for a specific Water Tanker (WT) service application.
 * It fetches data for the booking using the `useTankerSearchAPI` hook and displays the details in sections such as:
 * - Booking Number
 * - Applicant Information (name, mobile number, email, etc.)
 * - Address Information (pincode, city, locality, street name, door number, etc.)
 * 
 * The component also handles:
 * - Displaying a loading state (via a `Loader` component) while fetching data.
 * - A "toast" notification for any errors or status updates.
 * - Showing downloadable options via `MultiLink` if available.
 * 
 * @returns {JSX.Element} Displays detailed Water Tanker application information with applicant details and address.
 */
const WTApplicationDetails = () => {
  const { t } = useTranslation();
  const { acknowledgementIds, tenantId } = useParams();
  const [showOptions, setShowOptions] = useState(false);
  const [showToast, setShowToast] = useState(null);
  const { data: storeData } = Digit.Hooks.useStore.getInitData();
  const { tenants } = storeData || {};

  const { isLoading, isError, error, data,refetch } = Digit.Hooks.wt.useTankerSearchAPI({
    tenantId,
    filters: { bookingNo: acknowledgementIds },
  });

  const waterTankerBookingDetail = get(data, "waterTankerBookingDetail", []);
  const wtId = get(data, "waterTankerBookingDetail[0].bookingNo", []);

  let wt_details = (waterTankerBookingDetail && waterTankerBookingDetail.length > 0 && waterTankerBookingDetail[0]) || {};
  const application = wt_details;

  sessionStorage.setItem("wt", JSON.stringify(application));
  let immediateRequired = (wt_details?.extraCharge==="N") ? "No":"Yes"

  const mutation = Digit.Hooks.wt.useTankerCreateAPI(tenantId,false); 

  const { data: reciept_data, isLoading: recieptDataLoading } = Digit.Hooks.useRecieptSearch(
    {
      tenantId: tenantId,
      businessService: "request-service.water_tanker",
      consumerCodes: acknowledgementIds,
      isEmployee: false,
    },
    { enabled: acknowledgementIds ? true : false }
  );

/**
 * This function handles the receipt generation and updates the water tanker booking details
 * with the generated receipt's file store ID.
 * 
 * Steps:
 * 1. Retrieve the first application from `waterTankerBookingDetail`.
 * 2. Check if the `paymentReceiptFilestoreId` already exists in the application.
 *    - If it exists, no further action is taken.
 *    - If it does not exist:
 *      a. Generate a PDF receipt using the `Digit.PaymentService.generatePdf` method.
 *      b. Update the application with the generated `paymentReceiptFilestoreId`.
 *      c. Use the `mutation.mutateAsync` method to persist the updated application.
 *      d. Refetch the data to ensure the UI reflects the latest state.
 * 
 * Parameters:
 * - tenantId: The tenant ID for which the receipt is being generated.
 * - payments: Payment details used to generate the receipt.
 * - params: Additional parameters (not used in this function).
 * 
 * Returns:
 * - None (the function performs asynchronous updates and refetches data).
 */
  async function getRecieptSearch({ tenantId, payments, ...params }) {
    let application = waterTankerBookingDetail[0] || {};
    let fileStoreId = application?.paymentReceiptFilestoreId
    if (!fileStoreId) {
    let response = { filestoreIds: [payments?.fileStoreId] };
    response = await Digit.PaymentService.generatePdf(tenantId, { Payments: [{ ...payments }] }, "request-service.water_tanker-receipt");
    const updatedApplication = {
      ...application,
      paymentReceiptFilestoreId: response?.filestoreIds[0]
    };
    await mutation.mutateAsync({
      waterTankerBookingDetail: updatedApplication
    });
    fileStoreId = response?.filestoreIds[0];
    refetch();
    }
    const fileStore = await Digit.PaymentService.printReciept(tenantId, { fileStoreIds: fileStoreId });
    window.open(fileStore[fileStoreId], "_blank");
  }

  let dowloadOptions=[];
    dowloadOptions.push({
      label: t("WT_DOWNLOAD_ACKNOWLEDGEMENT"),
      onClick: () => getAcknowledgementData(),
    });
    
    const getAcknowledgementData = async () => {
      const applications = application || {};
      const tenantInfo = tenants.find((tenant) => tenant.code === applications.tenantId);
      const acknowldgementDataAPI = await getWTAcknowledgementData({ ...applications }, tenantInfo, t);
      Digit.Utils.pdf.generate(acknowldgementDataAPI);
    };

  if (isLoading) {
    return <Loader />;
  }
  if (reciept_data && reciept_data?.Payments.length > 0 && recieptDataLoading == false)
    dowloadOptions.push({
      label: t("WT_FEE_RECEIPT"),
      onClick: () => getRecieptSearch({ tenantId: reciept_data?.Payments[0]?.tenantId, payments: reciept_data?.Payments[0] }),
    });

  return (
    <React.Fragment>
      <div>
        <div className="cardHeaderWithOptions" style={{ marginRight: "auto", maxWidth: "960px" }}>
          <Header styles={{ fontSize: "32px" }}>{t("WT_BOOKING_DETAILS")}</Header>
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
            <Row className="border-none" label={t("WT_BOOKING_NO")} text={wt_details?.bookingNo} />
          </StatusTable>

          <CardSubHeader style={{ fontSize: "24px" }}>{t("WT_APPLICANT_DETAILS")}</CardSubHeader>
          <StatusTable>
            <Row className="border-none" label={t("WT_APPLICANT_NAME")} text={wt_details?.applicantDetail?.name || t("CS_NA")} />
            <Row className="border-none" label={t("WT_MOBILE_NUMBER")} text={wt_details?.applicantDetail?.mobileNumber || t("CS_NA")} />
            <Row className="border-none" label={t("WT_ALT_MOBILE_NUMBER")} text={wt_details?.applicantDetail?.alternateNumber || t("CS_NA")} />
            <Row className="border-none" label={t("WT_EMAIL_ID")} text={wt_details?.applicantDetail?.emailId || t("CS_NA")} />
          </StatusTable>

          <CardSubHeader style={{ fontSize: "24px" }}>{t("WT_ADDRESS_DETAILS")}</CardSubHeader>
            <StatusTable>
              <Row className="border-none" label={t("WT_PINCODE")} text={wt_details?.address?.pincode || t("CS_NA")} />
              <Row className="border-none" label={t("WT_CITY")} text={wt_details?.address?.city || t("CS_NA")}/>
              <Row className="border-none" label={t("WT_LOCALITY")} text={wt_details?.address?.locality || t("CS_NA")} />
              <Row className="border-none" label={t("WT_STREET_NAME")} text={wt_details?.address?.streetName || t("CS_NA")} />
              <Row className="border-none" label={t("WT_DOOR_NO")} text={wt_details?.address?.doorNo || t("CS_NA")} />
              <Row className="border-none" label={t("WT_HOUSE_NO")} text={wt_details?.address?.houseNo || t("CS_NA")} />
              <Row className="border-none" label={t("WT_ADDRESS_LINE1")} text={wt_details?.address?.addressLine1 || t("CS_NA")} />
              <Row className="border-none" label={t("WT_ADDRESS_LINE2")} text={wt_details?.address?.addressLine2 || t("CS_NA")} />
              <Row className="border-none" label={t("WT_LANDMARK")} text={wt_details?.address?.landmark || t("CS_NA")} />
            </StatusTable>

            <CardSubHeader style={{ fontSize: "24px" }}>{t("ES_REQUEST_DETAILS")}</CardSubHeader>
            <StatusTable>
              <Row className="border-none" label={t("WT_TANKER_TYPE")} text={wt_details?.tankerType || t("CS_NA")} />
              <Row className="border-none" label={t("WT_WATER_TYPE")} text={t(wt_details?.waterType) || t("CS_NA")} />
              <Row className="border-none" label={t("WT_TANKER_QUANTITY")} text={wt_details?.tankerQuantity|| t("CS_NA")}/>
              <Row className="border-none" label={t("WT_WATER_QUANTITY")} text={wt_details?.waterQuantity + " Ltr"|| t("CS_NA")} />
              <Row className="border-none" label={t("WT_DELIVERY_DATE")} text={formatDate(wt_details?.deliveryDate)|| t("CS_NA")} />
              <Row className="border-none" label={t("WT_DELIVERY_TIME")} text={convertTo12HourFormat(wt_details?.deliveryTime) || t("CS_NA")} />
              <Row className="border-none" label={t("WT_DESCRIPTION")} text={wt_details?.description || t("CS_NA")} />
              <Row className="border-none" label={t("WT_IMMEDIATE")} text={immediateRequired || t("CS_NA")} />
            </StatusTable>
            
            <WFApplicationTimeline application={application} id={application?.bookingNo} userType={"citizen"} />
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

export default WTApplicationDetails;
