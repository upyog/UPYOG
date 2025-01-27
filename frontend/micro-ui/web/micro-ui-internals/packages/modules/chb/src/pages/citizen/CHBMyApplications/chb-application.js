import { Card, KeyNote, SubmitBar,Toast,CardSubHeader } from "@upyog/digit-ui-react-components";
import React, { useEffect, useState } from "react";

import { useTranslation } from "react-i18next";
import { Link,useHistory } from "react-router-dom";

const ChbApplication = ({ application, tenantId, buttonLabel }) => {
  const { t } = useTranslation();
  const history = useHistory();
  const [showToast, setShowToast] = useState(null);

  const { data: slotSearchData, refetch } = Digit.Hooks.chb.useChbSlotSearch({
    tenantId: application?.tenantId,
    filters: {
      bookingId:application?.bookingId,
      communityHallCode: application?.communityHallCode,
      bookingStartDate: application?.bookingSlotDetails?.[0]?.bookingDate,
      bookingEndDate: application?.bookingSlotDetails?.[application.bookingSlotDetails.length - 1]?.bookingDate,
      hallCode: application?.bookingSlotDetails?.[0]?.hallCode,
      isTimerRequired:true
    },
    enabled: false, // Disable automatic refetch
  });
  /*
  const [timeRemaining, setTimeRemaining] = useState(application?.timerValue);
  // Initialize time remaining on mount or when application changes
  useEffect(() => {
    setTimeRemaining(application?.timerValue || 0);
  }, [application?.timerValue]);
  
  // Timer logic
  useEffect(() => {
    if (timeRemaining <= 0) return;
  
    const interval = setInterval(() => {
      setTimeRemaining((prevTime) => Math.max(prevTime - 1, 0));
    }, 1000);
  
    return () => clearInterval(interval); // Cleanup interval
  }, [timeRemaining]);
  
  // Format seconds into "minutes:seconds" format
  const formatTime = (seconds) => {
    const minutes = Math.floor(seconds / 60);
    const remainingSeconds = seconds % 60;
    return `${minutes}:${remainingSeconds < 10 ? "0" : ""}${remainingSeconds}`;
  };

  */
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
      communityHallCode: application?.communityHallCode,
      bookingStartDate: application?.bookingSlotDetails?.[0]?.bookingDate,
      bookingEndDate: application?.bookingSlotDetails?.[application.bookingSlotDetails.length - 1]?.bookingDate,
      hallCode: application?.bookingSlotDetails?.[0]?.hallCode,
      isTimerRequired:true

    }
    const isSlotBooked = result?.data?.hallSlotAvailabiltityDetails?.some(
      (slot) => slot.slotStaus === "BOOKED"
    );

    if (isSlotBooked) {
      setShowToast({ error: true, label: t("CHB_COMMUNITY_HALL_ALREADY_BOOKED") });
    } else {
      history.push({
        pathname: `/digit-ui/citizen/payment/my-bills/${"chb-services"}/${application?.bookingNo}`,
        state: { tenantId: application?.tenantId, bookingNo: application?.bookingNo,timerValue:result?.data.timerValue ,SlotSearchData:SlotSearchData },
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
       {/* <div style={{ display: "flex", justifyContent: "space-between" }}> */}
       <KeyNote keyValue={t("CHB_BOOKING_NO")} note={application?.bookingNo} />
            {/* { timeRemaining>0 && (<CardSubHeader 
              style={{ 
                textAlign: 'right', 
                fontSize: "24px"
              }}
            >
              {t("CS_TIME_REMAINING")}: <span className="astericColor">{formatTime(timeRemaining)}</span>
            </CardSubHeader>)}
        </div> */}
      <KeyNote keyValue={t("CHB_APPLICANT_NAME")} note={application?.applicantDetail?.applicantName} />
      <KeyNote keyValue={t("CHB_COMMUNITY_HALL_NAME")} note={t(`${application?.communityHallCode}`)} />
      <KeyNote keyValue={t("CHB_BOOKING_DATE")} note={getBookingDateRange(application?.bookingSlotDetails)} />
      <KeyNote keyValue={t("PT_COMMON_TABLE_COL_STATUS_LABEL")} note={t(`${application?.bookingStatus}`)} />
      <div>
        <Link to={`/digit-ui/citizen/chb/application/${application?.bookingNo}/${application?.tenantId}`}>
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
