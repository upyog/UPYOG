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
import React, {useState } from "react";
import { useTranslation } from "react-i18next";

import {useParams} from "react-router-dom";

import get from "lodash/get";
import { size } from "lodash";
import WFApplicationTimeline from "../../pageComponents/WFApplicationTimeline";
import { convertTo12HourFormat, formatDate } from "../../utils";

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
  let immediateRequired = (wt_details?.extraCharge) ? "YES":"NO"

  
  const { isLoading: auditDataLoading, isError: isAuditError, data: auditResponse } = Digit.Hooks.wt.useTankerSearchAPI(
    {
      tenantId,
      filters: { bookingNo: wtId, audit: true },
    },
    {
      enabled: true,
    }
  );
  let dowloadOptions=[];
  
  let docs = [];
  docs = application?.documents;

  if (isLoading || auditDataLoading) {
    return <Loader />;
  }


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
              <Row className="border-none" label={t("WT_TANKER_QUANTITY")} text={wt_details?.tankerQuantity|| t("CS_NA")}/>
              <Row className="border-none" label={t("WT_WATER_QUANTITY")} text={wt_details?.waterQuantity|| t("CS_NA")} />
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
