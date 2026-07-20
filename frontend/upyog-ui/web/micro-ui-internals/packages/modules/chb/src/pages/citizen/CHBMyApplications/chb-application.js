import { Card, KeyNote, SubmitBar,Toast,CardSubHeader } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useEffect, useState } from "react";

import { useTranslation } from "react-i18next";
import { Link,  } from "react-router-dom";


/**
 * ChbApplication Component
 * 
 * This component is responsible for rendering the details of a specific CHB application in the citizen portal.
 * It displays application details, handles slot search functionality, and manages toast notifications.
 * 
 * Props:
 * - `application`: The application object containing details such as booking ID, tenant ID, community hall code, and booking slot details.
 * - `tenantId`: The tenant ID associated with the application.
 * - `buttonLabel`: The label for the action button displayed in the component.
 * 
 * Hooks:
 * - `useTranslation`: Provides the `t` function for internationalization.
 * - `useHistory`: Provides navigation functionality within the application.
 * - `Digit.Hooks.chb.useChbSlotSearch`: Custom hook to fetch slot search data for the given application.
 * 
 * State Variables:
 * - `showToast`: State variable to manage the visibility of toast notifications.
 * 
 * Variables:
 * - `slotSearchData`: The fetched slot search data for the application.
 * - `refetch`: Function to manually trigger the slot search data fetch.
 * 
 * Logic:
 * - Initializes the `showToast` state to manage toast notifications.
 * - Uses the `useChbSlotSearch` hook to fetch slot search data based on the application details.
 *    - Filters include booking ID, community hall code, booking dates, and hall code.
 *    - The `enabled` flag is set to `false` to disable automatic fetching.
 * - Placeholder for timer logic to manage time remaining for the application (commented out).
 * 
 * Returns:
 * - A card component displaying the application details, with an action button and toast notifications.
 */
const ChbApplication = ({ application, tenantId, buttonLabel }) => {
  const { t } = useTranslation();
  const navigate = Digit.Hooks.useCustomNavigate();
  const [showToast, setShowToast] = useState(null);

  const isCancelled = application?.bookingStatus === "CANCELLED";

  const { data: recieptData } = Digit.Hooks.useRecieptSearch(
    {
      tenantId: tenantId || application?.tenantId,
      businessService: "chb-services",
      consumerCodes: application?.bookingNo,
      isEmployee: false,
    },
    { enabled: !!(isCancelled && application?.bookingNo) }
  );

  const payment = recieptData?.Payments?.[0];
  const isOnlinePayment = payment?.paymentMode === "ONLINE";
  const originalTxnId = payment?.transactionNumber;

  // Use the same refund API as detail pages for accurate pipeline status (INITIATED, SUCCESS, etc.)
  // bookingNo is included in params purely as a query-key discriminator so each card
  // in the list has a unique React Query cache entry (prevents shared-key collisions).
  const { data: refundData } = Digit.Hooks.useCustomAPIHook(
    "/pg-service/refund/v1/_search",
    {
      originalTxnId: originalTxnId || "",
      tenantId: payment?.tenantId || tenantId || application?.tenantId,
      bookingNo: application?.bookingNo,
    },
    {},
    {},
    {
      enabled: !!(isCancelled && isOnlinePayment && originalTxnId),
    }
  );

  const refund = refundData?.Refund?.[0] || refundData?.Refunds?.[0] || refundData?.[0];
  // Show exact refund pipeline status from the API (INITIATED, SUCCESS, etc.)
  const refundStatus = refund?.status || refund?.refundStatus || null;

  const { data: slotSearchData, refetch } = Digit.Hooks.chb.useChbSlotSearch({
    tenantId: application?.tenantId,
    filters: {
      bookingId:application?.bookingId,
      venueCode: application?.venueCode,
      bookingStartDate: application?.bookingSlotDetails?.[0]?.bookingDate,
      bookingEndDate: application?.bookingSlotDetails?.[application.bookingSlotDetails.length - 1]?.bookingDate,
      unitCode: application?.bookingSlotDetails?.[0]?.unitCode,
      isTimerRequired:true,
      fromTime:application?.bookingSlotDetails?.[0]?.bookingFromTime,
      toTime:application?.bookingSlotDetails?.[0]?.bookingToTime
    },
    enabled: false, // Disable automatic refetch
  });
 
  const getBookingDateRange = (bookingSlotDetails) => {
    if (!bookingSlotDetails || bookingSlotDetails.length === 0) {
      return t("CS_NA");
    }
    const startDate = bookingSlotDetails[0]?.bookingDate;
    const endDate = bookingSlotDetails[bookingSlotDetails.length - 1]?.bookingDate;
    if (startDate === endDate) {
      return startDate; // Return only the start date
    } else {
      // Format date range as needed, for example: "startDate - endDate"
      return startDate && endDate ? `${startDate}  -  ${endDate}` : t("CS_NA");
    }
  };

  
  const handleMakePayment = async () => {
    try {
    const result = await refetch();
    let SlotSearchData={
      tenantId: application?.tenantId,
      bookingId:application?.bookingId,
      venueCode: application?.venueCode,
      bookingStartDate: application?.bookingSlotDetails?.[0]?.bookingDate,
      bookingEndDate: application?.bookingSlotDetails?.[application.bookingSlotDetails.length - 1]?.bookingDate,
      unitCode: application?.bookingSlotDetails?.[0]?.unitCode,
      isTimerRequired:true,
      fromTime:application?.bookingSlotDetails?.[0]?.bookingFromTime,
      toTime:application?.bookingSlotDetails?.[0]?.bookingToTime

    }
    const isSlotBooked = result?.data?.hallSlotAvailabiltityDetails?.some(
      (slot) => slot.slotStaus === "BOOKED"
    );


    if (isSlotBooked) {
      setShowToast({ error: true, label: t("CHB_COMMUNITY_HALL_ALREADY_BOOKED") });
    } else {
      navigate(`/upyog-ui/citizen/payment/my-bills/${"chb-services"}/${application?.bookingNo}`,
        {
        state: { tenantId: application?.tenantId, bookingNo: application?.bookingNo,timerValue:result?.data?.timerValue ,SlotSearchData:application },
    });
    }
  } catch (error) {
    setShowToast({ error: true, label: t("CS_SOMETHING_WENT_WRONG") });
    }
  };
  useEffect(() => {
    if (showToast) {
      const timer = setTimeout(() => {
        setShowToast(null);
      }, 2000); // Close toast after 2 seconds

      return () => clearTimeout(timer); // Clear timer on cleanup
    }
  }, [showToast]);

  return (
    <Card>

      <KeyNote keyValue={t("CHB_BOOKING_NO")} note={application?.bookingNo} />
      <KeyNote keyValue={t("CHB_APPLICANT_NAME")} note={application?.applicantDetail?.applicantName} />
      <KeyNote keyValue={t("CHB_VENUE_NAME")} note={t(`${application?.venueCode}`)} />
      <KeyNote keyValue={t("CHB_BOOKING_DATE")} note={getBookingDateRange(application?.bookingSlotDetails)} />
      <KeyNote keyValue={t("PT_COMMON_TABLE_COL_STATUS_LABEL")} note={t(`${application?.bookingStatus}`)} />
      {isCancelled && (
        <KeyNote
          keyValue={t("CHB_REFUND_STATUS") || "Refund Status"}
          note={
            <span
              style={{
                fontWeight: "600",
                color: (refundStatus?.toUpperCase() === "SUCCESS" || refundStatus?.toUpperCase() === "SUCCESSFUL" || refundStatus?.toUpperCase() === "COMPLETED" || refundStatus?.toUpperCase() === "REFUNDED")
                  ? "#155724"
                  : (refundStatus?.toUpperCase() === "INITIATED" || refundStatus?.toUpperCase() === "IN_PROGRESS" || refundStatus?.toUpperCase() === "INPROGRESS")
                  ? "#856404"
                  : refundStatus
                  ? "#383D41"
                  : "#6C757D",
              }}
            >
              {refundStatus
                ? refundStatus
                : isOnlinePayment
                ? t("CHB_REFUND_NOT_INITIATED") || t("CHB_REFUND_NOT_INITIATED")
                : t("CHB_OFFLINE_PAYMENT_NO_REFUND") || t("CHB_OFFLINE_PAYMENT_NO_REFUND")}
            </span>
          }
        />
      )}
      <div>
        <Link to={`/upyog-ui/citizen/chb/application/${application?.bookingNo}/${application?.tenantId}`}>
          <SubmitBar label={buttonLabel} />
        </Link> 
        {(application.bookingStatus === "BOOKING_CREATED" || application.bookingStatus === "PAYMENT_FAILED" || application.bookingStatus === "PENDING_FOR_PAYMENT") && (
        <SubmitBar label={t("CS_APPLICATION_DETAILS_MAKE_PAYMENT")} onSubmit={handleMakePayment}  style={{ margin: "20px" }}/>
        )}
      </div>
      {showToast && (
      <Toast
        error={showToast.error}
        warning={showToast.warning}
        label={t(showToast.label)}
        onClose={() => {
          setShowToast(null);
        }}
      />
    )}
    </Card>
  );
};

export default ChbApplication;
