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
 */

const extractRefundObject = (res) => {
  if (!res) return null;
  if (res.id || res.refundNo) return res;
  if (res.data) return extractRefundObject(res.data);
  if (Array.isArray(res) && res.length > 0) {
    return extractRefundObject(res[0]);
  }
  if (res.refund) return extractRefundObject(res.refund);
  if (res.Refund) return extractRefundObject(res.Refund);
  if (Array.isArray(res.Refunds) && res.Refunds.length > 0) return extractRefundObject(res.Refunds[0]);
  if (Array.isArray(res.refunds) && res.refunds.length > 0) return extractRefundObject(res.refunds[0]);
  return null;
};

const ChbApplication = ({ application, tenantId, buttonLabel }) => {
  const { t } = useTranslation();
  const navigate = Digit.Hooks.useCustomNavigate();
  const [showToast, setShowToast] = useState(null);

  const isCancelled = application?.bookingStatus === "CANCELLED" || application?.bookingStatus?.toUpperCase()?.includes("CANCEL");
  const consumerCode = application?.bookingNo;
  const targetTenantId = application?.tenantId || tenantId;

  const { data: recieptData } = Digit.Hooks.useRecieptSearch(
    {
      tenantId: targetTenantId,
      businessService: "chb-services",
      consumerCodes: consumerCode,
      isEmployee: false,
    },
    { enabled: !!(isCancelled && consumerCode && targetTenantId) }
  );

  const payment = recieptData?.Payments?.[0];
  const isOnlinePayment = payment?.paymentMode === "ONLINE";
  const originalTxnId = payment?.transactionNumber;

  const { data: serviceRefundData } = Digit.Hooks.useCustomAPIHook(
    "/refund-services/refund/v1/_search",
    {},
    {
      data: {
        tenantId: targetTenantId,
        moduleName: "CHB",
        businessService: "CHB.REFUND",
        consumerCode: consumerCode,
      },
    },
    {},
    {
      enabled: !!(isCancelled && consumerCode && targetTenantId),
    }
  );

  const { data: pgRefundData } = Digit.Hooks.useCustomAPIHook(
    "/pg-service/refund/v1/_search",
    {
      originalTxnId: originalTxnId || "",
      tenantId: targetTenantId,
      bookingNo: consumerCode,
    },
    {},
    {},
    {
      enabled: !!(isCancelled && isOnlinePayment && originalTxnId && targetTenantId),
    }
  );

  const serviceRefund = extractRefundObject(serviceRefundData);
  const refund = serviceRefund || extractRefundObject(pgRefundData);
  const refundStatus = refund?.status || refund?.refundStatus || null;
  const refundId = refund?.refundNo || refund?.refundId || refund?.id || null;
  const instrumentStatus = payment?.instrumentStatus;
  const isRefunded = instrumentStatus === "REFUNDED";

  const statusUpper = refundStatus?.toUpperCase() || "";
  const isRefundSuccess = Boolean(
    isRefunded ||
    statusUpper === "REFUNDED" ||
    statusUpper === "REFUND_COMPLETED" ||
    statusUpper === "COMPLETED" ||
    statusUpper === "SUCCESS" ||
    statusUpper === "SUCCESSFUL" ||
    statusUpper === "REFUND_APPROVED" ||
    statusUpper === "APPROVED" ||
    statusUpper === "SETTLED" ||
    statusUpper.includes("COMPLETE") ||
    statusUpper.includes("REFUNDED") ||
    statusUpper.includes("SUCCESS")
  );
  const isRefundInProgress = !isRefundSuccess && Boolean(
    refundStatus && (
      statusUpper === "IN_PROGRESS" ||
      statusUpper === "INPROGRESS" ||
      statusUpper === "INITIATED" ||
      statusUpper === "REFUND_INITIATED" ||
      statusUpper.includes("INITIAT") ||
      statusUpper.includes("PROGRESS")
    )
  );

  const getLocalizedRefundStatus = (status) => {
    if (!status && isRefunded) return t("REFUNDED");
    if (!status) return t("CS_NA");

    const candidateKeys = [
      `CS_COMMON_${status}`,
      `WF_CHB.REFUND_${status}`,
      `WF_${status}`,
      status,
    ];

    for (const key of candidateKeys) {
      const translated = t(key);
      if (translated && translated !== key) {
        return translated;
      }
    }

    return status;
  };

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
      {(isRefundInProgress || isRefundSuccess || refundId) ? (
        <React.Fragment>
          <KeyNote keyValue={t("CHB_REFUND_ID")} note={refundId || t("CS_NA")} />
          <KeyNote
            keyValue={t("CHB_REFUND_STATUS")}
            note={
              <span
                style={{
                  fontWeight: "600",
                  color: isRefundSuccess ? "#155724" : isRefundInProgress ? "#856404" : "#383D41",
                }}
              >
                {getLocalizedRefundStatus(refundStatus)}
              </span>
            }
          />
        </React.Fragment>
      ) : isCancelled ? (
        <KeyNote
          keyValue={t("CHB_REFUND_STATUS")}
          note={
            <span style={{ fontWeight: "600", color: "#6C757D" }}>
              {isOnlinePayment
                ? t("CHB_REFUND_NOT_INITIATED")
                : t("CHB_OFFLINE_PAYMENT_NO_REFUND")}
            </span>
          }
        />
      ) : null}
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
