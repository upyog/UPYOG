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
      import { convertTo12HourFormat, formatDate } from "../../utils";
      
      /**
       * `MTApplicationDetails` is a React component that fetches and displays detailed information for a specific Mobile Toilet (MT) service application.
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
      const MTApplicationDetails = () => {
        const { t } = useTranslation();
        const { acknowledgementIds, tenantId } = useParams();
        const [showOptions, setShowOptions] = useState(false);
        const [showToast, setShowToast] = useState(null);
        const { data: storeData } = Digit.Hooks.useStore.getInitData();
        const { tenants } = storeData || {};
      
        const { isLoading, data, refetch } = Digit.Hooks.wt.useMobileToiletSearchAPI({
          tenantId,
          filters: { bookingNo: acknowledgementIds },
        });
      
        const mobileToiletBookingDetail = get(data, "mobileToiletBookingDetails", []);
        const mtId = get(data, "mobileToiletBookingDetails[0].bookingNo", []);
      
        let mt_details = (mobileToiletBookingDetail && mobileToiletBookingDetail.length > 0 && mobileToiletBookingDetail[0]) || {};
        const application = mt_details;
      
        sessionStorage.setItem("mt", JSON.stringify(application));
      
        const { isLoading: auditDataLoading, data: auditResponse } = Digit.Hooks.wt.useMobileToiletSearchAPI(
          {
            tenantId,
            filters: { bookingNo: mtId, audit: true },
          },
          {
            enabled: true,
          }
        );
      
        let dowloadOptions = [];
        let docs = application?.documents || [];
      
        if (isLoading || auditDataLoading) {
          return <Loader />;
        }
      console.log("application",application);
      console.log("mt_details",mt_details);
        return (
          <React.Fragment>
            <div>
              <div className="cardHeaderWithOptions" style={{ marginRight: "auto", maxWidth: "960px" }}>
                <Header styles={{ fontSize: "32px" }}>{t("MT_BOOKING_DETAILS")}</Header>
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
                  <Row className="border-none" label={t("MT_BOOKING_NO")} text={mt_details?.bookingNo} />
                </StatusTable>
                
      
                <CardSubHeader style={{ fontSize: "24px" }}>{t("MT_APPLICANT_DETAILS")}</CardSubHeader>
                <StatusTable>
                  <Row className="border-none" label={t("MT_APPLICANT_NAME")} text={mt_details?.applicantDetail?.name || t("CS_NA")} />
                  <Row className="border-none" label={t("MT_MOBILE_NUMBER")} text={mt_details?.applicantDetail?.mobileNumber || t("CS_NA")} />
                  <Row className="border-none" label={t("MT_ALT_MOBILE_NUMBER")} text={mt_details?.applicantDetail?.alternateNumber || t("CS_NA")} />
                  <Row className="border-none" label={t("MT_EMAIL_ID")} text={mt_details?.applicantDetail?.emailId || t("CS_NA")} />
                </StatusTable>
      
                {/* <CardSubHeader style={{ fontSize: "24px" }}>{t("MT_ADDRESS_DETAILS")}</CardSubHeader>
                <StatusTable>
                  <Row className="border-none" label={t("MT_PINCODE")} text={mt_details?.address?.pincode || t("CS_NA")} />
                  <Row className="border-none" label={t("MT_CITY")} text={mt_details?.address?.city || t("CS_NA")} />
                  <Row className="border-none" label={t("MT_LOCALITY")} text={mt_details?.address?.locality || t("CS_NA")} />
                  <Row className="border-none" label={t("MT_STREET_NAME")} text={mt_details?.address?.streetName || t("CS_NA")} />
                  <Row className="border-none" label={t("MT_DOOR_NO")} text={mt_details?.address?.doorNo || t("CS_NA")} />
                  <Row className="border-none" label={t("MT_HOUSE_NO")} text={mt_details?.address?.houseNo || t("CS_NA")} />
                  <Row className="border-none" label={t("MT_ADDRESS_LINE1")} text={mt_details?.address?.addressLine1 || t("CS_NA")} />
                  <Row className="border-none" label={t("MT_ADDRESS_LINE2")} text={mt_details?.address?.addressLine2 || t("CS_NA")} />
                  <Row className="border-none" label={t("MT_LANDMARK")} text={mt_details?.address?.landmark || t("CS_NA")} />
                </StatusTable>
       */}
                <CardSubHeader style={{ fontSize: "24px" }}>{t("ES_REQUEST_DETAILS")}</CardSubHeader>
                <StatusTable>
                  <Row className="border-none" label={t("MT_NUMBER_OF_MOBILE_TOILETS")} text={mt_details?.noOfMobileToilet || t("CS_NA")} />
                  <Row className="border-none" label={t("MT_DELIVERY_FROM_DATE")} text={formatDate(mt_details?.deliveryFromDate) || t("CS_NA")} />
                  <Row className="border-none" label={t("MT_DELIVERY_TO_DATE")} text={formatDate(mt_details?.deliveryToDate) || t("CS_NA")} />
                  <Row className="border-none" label={t("MT_REQUIREMNENT_FROM_TIME")} text={convertTo12HourFormat(mt_details?.deliveryFromTime) || t("CS_NA")} />
                  <Row className="border-none" label={t("MT_REQUIREMNENT_TO_TIME")} text={convertTo12HourFormat(mt_details?.deliveryToTime) || t("CS_NA")} />
                  <Row className="border-none" label={t("MT_SPECIAL_REQUEST")} text={mt_details?.description || t("CS_NA")} />
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
      
      export default MTApplicationDetails;
      