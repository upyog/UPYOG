import {
        Card,
        CardSubHeader,
        Header,
        Loader,
        Row,
        StatusTable,
        MultiLink,
        Toast
      } from "@nudmcdgnpm/digit-ui-react-components";
      import React, { useState } from "react";
      import { useTranslation } from "react-i18next";
      import { useParams } from "react-router-dom";
      import get from "lodash/get";
      import WFApplicationTimeline from "../../pageComponents/WFApplicationTimeline";
      import getTPAcknowledgementData from "../../utils/getTPAcknowledgementData";
      /**
       * `TPApplicationDetails` is a React component that fetches and displays detailed information for a specific Mobile Toilet (MT) service application.
       * It fetches data for the booking using the `useMobileToiletSearchAPI` hook and displays the details in sections such as:
       * - Booking Number
       * - Applicant Information (name, mobile number, email, etc.)
       * - Address Information (pincode, city, locality, street name, door number, etc.)
       * 
       * The component also handles:
       * - Displaying a loading state (via a `Loader` component) while fetching data.
       * - A "toast" notification for any errors or status updates.
       * - Showing downloadable options via `MultiLink` if available.
       * 
       * @returns {JSX.Element} Displays detailed Mobile Toilet application information with applicant details and address.
       */
      const TPApplicationDetails = () => {
        const { t } = useTranslation();
        const { acknowledgementIds, tenantId } = useParams();
        const [showOptions, setShowOptions] = useState(false);
        const [showToast, setShowToast] = useState(null);
        const { data: storeData } = Digit.Hooks.useStore.getInitData();
        const { tenants } = storeData || {};
      
        const { isLoading, data, refetch } = Digit.Hooks.wt.useTreePruningSearchAPI({
          tenantId,
          filters: { bookingNo: acknowledgementIds },
        });
      
        const treePruningBookingDetail = get(data, "treePruningBookingDetails", []);
        const tpId = get(data, "treePruningBookingDetails[0].bookingNo", []);
      
        let tp_details = (treePruningBookingDetail && treePruningBookingDetail.length > 0 && treePruningBookingDetail[0]) || {};
        console .log("tp_details", tp_details);
        const application = tp_details;
      
        sessionStorage.setItem("tp", JSON.stringify(application));

        const mutation = Digit.Hooks.wt.useTreePruningCreateAPI(tenantId,false); 
        const { data: reciept_data, isLoading: recieptDataLoading } = Digit.Hooks.useRecieptSearch(
          {
            tenantId: tenantId,
            businessService: "request-service.tree_pruning",
            consumerCodes: acknowledgementIds,
            isEmployee: false,
          },
          { enabled: acknowledgementIds ? true : false }
        );
      
      /**
       * This function handles the receipt generation and updates the application details
       * with the generated receipt's file store ID.
       * 
       * Steps:
       * 1. Retrieve the first application from `treePruningBookingDetail`.
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
          let application = treePruningBookingDetail[0] || {};
          let fileStoreId = application?.paymentReceiptFilestoreId
          if (!fileStoreId) {
          let response = { filestoreIds: [payments?.fileStoreId] };
          response = await Digit.PaymentService.generatePdf(tenantId, { Payments: [{ ...payments }] }, "request-service.tree_pruning-receipt");
          const updatedApplication = {
            ...application,
            paymentReceiptFilestoreId: response?.filestoreIds[0]
          };
          await mutation.mutateAsync({
            treePruningBookingDetail: updatedApplication
          });
          fileStoreId = response?.filestoreIds[0];
          refetch();
          }
          const fileStore = await Digit.PaymentService.printReciept(tenantId, { fileStoreIds: fileStoreId });
          window.open(fileStore[fileStoreId], "_blank");
        }
      
        let dowloadOptions = [];

        dowloadOptions.push({
          label: t("TP_DOWNLOAD_ACKNOWLEDGEMENT"),
          onClick: () => getAcknowledgementData(),
        });
      
        if (isLoading) {
          return <Loader />;
        }

        if (reciept_data && reciept_data?.Payments.length > 0 && recieptDataLoading == false)
          dowloadOptions.push({
            label: t("TP_FEE_RECEIPT"),
            onClick: () => getRecieptSearch({ tenantId: reciept_data?.Payments[0]?.tenantId, payments: reciept_data?.Payments[0] }),
          });
      const getAcknowledgementData = async () => {
        const applications = application || {};
        const tenantInfo = tenants.find((tenant) => tenant.code === applications.tenantId);
        const acknowldgementDataAPI = await getTPAcknowledgementData({ ...applications }, tenantInfo, t);
        Digit.Utils.pdf.generate(acknowldgementDataAPI);
      };
      
        return (
          <React.Fragment>
            <div>
              <div className="cardHeaderWithOptions" style={{ marginRight: "auto", maxWidth: "960px" }}>
                <Header styles={{ fontSize: "32px" }}>{t("TP_BOOKING_DETAILS")}</Header>
                {dowloadOptions.length > 0 && (
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
                  <Row className="border-none" label={t("WT_BOOKING_NO")} text={tp_details?.bookingNo} />
                </StatusTable>
                
      
                <CardSubHeader style={{ fontSize: "24px" }}>{t("TP_APPLICANT_DETAILS")}</CardSubHeader>
                <StatusTable>
                  <Row className="border-none" label={t("WT_APPLICANT_NAME")} text={tp_details?.applicantDetail?.name || t("CS_NA")} />
                  <Row className="border-none" label={t("WT_MOBILE_NUMBER")} text={tp_details?.applicantDetail?.mobileNumber || t("CS_NA")} />
                  <Row className="border-none" label={t("WT_ALT_MOBILE_NUMBER")} text={tp_details?.applicantDetail?.alternateNumber || t("CS_NA")} />
                  <Row className="border-none" label={t("WT_EMAIL_ID")} text={tp_details?.applicantDetail?.emailId || t("CS_NA")} />
                </StatusTable>
      
                <CardSubHeader style={{ fontSize: "24px" }}>{t("ES_TITLE_ADDRESS_DETAILS")}</CardSubHeader>
                <StatusTable>
                  <Row className="border-none" label={t("PINCODE")} text={tp_details?.address?.pincode || t("CS_NA")} />
                  <Row className="border-none" label={t("CITY")} text={tp_details?.address?.city || t("CS_NA")} />
                  <Row className="border-none" label={t("LOCALITY")} text={tp_details?.address?.locality || t("CS_NA")} />
                  <Row className="border-none" label={t("STREET_NAME")} text={tp_details?.address?.streetName || t("CS_NA")} />
                  <Row className="border-none" label={t("HOUSE_NO")} text={tp_details?.address?.houseNo || t("CS_NA")} />
                  <Row className="border-none" label={t("ADDRESS_LINE1")} text={tp_details?.address?.addressLine1 || t("CS_NA")} />
                  <Row className="border-none" label={t("ADDRESS_LINE2")} text={tp_details?.address?.addressLine2 || t("CS_NA")} />
                  <Row className="border-none" label={t("LANDMARK")} text={tp_details?.address?.landmark || t("CS_NA")} />
                </StatusTable>
      
                <CardSubHeader style={{ fontSize: "24px" }}>{t("TP_REQUEST_DETAILS")}</CardSubHeader>
                <StatusTable>
                  <Row className="border-none" label={t("REASON_FOR_PRUNING")} text={t(tp_details?.reasonForPruning) || t("CS_NA")} />
                  <Row className="border-none" label={t("LATITUDE_GEOTAG")} text={tp_details?.latitude || t("CS_NA")} />
                  <Row className="border-none" label={t("LONGITUDE_GEOTAG")} text={tp_details?.longitude || t("CS_NA")} />
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
      
      export default TPApplicationDetails;
      