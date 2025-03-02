import { Card, KeyNote, SubmitBar, Toast,CardSubHeader } from "@upyog/digit-ui-react-components";
import React, { useEffect, useState } from "react";

import { useTranslation } from "react-i18next";
import { Link, useHistory } from "react-router-dom";

/*
 * AdsApplication component displays the details of a specific advertisement application.
 * It shows key information such as booking number, applicant name, advertisement name, 
 * booking dates, and application status. The component also includes functionality for 
 * making payments and navigating to the application details page.
 */

const AdsApplication = ({ application, tenantId, buttonLabel }) => {
  const { t } = useTranslation();
  const history = useHistory();
  const [showToast, setShowToast] = useState(null);

  /*
  const [timeRemaining, setTimeRemaining] = useState(application?.remainingTimerValue);
// Initialize time remaining on mount or when application changes
useEffect(() => {
  setTimeRemaining(application?.remainingTimerValue || 0);
}, [application?.remainingTimerValue]);

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
  const slotSearchData = Digit.Hooks.ads.useADSSlotSearch();
    let formdata = {
      advertisementSlotSearchCriteria:application?.cartDetails.map((item) => ({
        bookingId: application?.bookingId,
        addType: item?.addType,
        bookingStartDate: item?.bookingDate,
        bookingEndDate: item?.bookingDate,
        faceArea: item?.faceArea,
        tenantId: tenantId,
        location: item?.location,
        nightLight: item?.nightLight,
        isTimerRequired: true,
      })),
    };
   
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
          // Await the mutation and capture the result directly
          const result = await slotSearchData.mutateAsync(formdata);
          let SlotSearchData={
            bookingId:application?.bookingId,
            tenantId: tenantId,
            cartDetails:application?.cartDetails,
          };
          const isSlotBooked = result?.advertisementSlotAvailabiltityDetails?.some((slot) => slot.slotStaus === "BOOKED");
          const timerValue=result?.advertisementSlotAvailabiltityDetails[0].timerValue;
          if (isSlotBooked) {
            setShowToast({ error: true, label: t("ADS_ADVERTISEMENT_ALREADY_BOOKED") });
          } else {
            history.push({
              pathname: `/upyog-ui/citizen/payment/my-bills/${"adv-services"}/${application?.bookingNo}`,
              state: { tenantId: application?.tenantId, bookingNo: application?.bookingNo, timerValue:timerValue, SlotSearchData:SlotSearchData },
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
       <KeyNote keyValue={t("ADS_BOOKING_NO")} note={application?.bookingNo} />
            {/* { timeRemaining>0 && (<CardSubHeader 
              style={{ 
                textAlign: 'right', 
                fontSize: "24px"
              }}
            >
              {t("CS_TIME_REMAINING")}: <span className="astericColor">{formatTime(timeRemaining)}</span>
            </CardSubHeader>)}
        </div> */}
      <KeyNote keyValue={t("ADS_APPLICANT_NAME")} note={application?.applicantDetail?.applicantName} />
      <KeyNote keyValue={t("ADS_BOOKING_DATE")} note={getBookingDateRange(application?.cartDetails)} />
      <KeyNote keyValue={t("PT_COMMON_TABLE_COL_STATUS_LABEL")} note={t(`${application?.bookingStatus}`)} />
      <div>
        <Link to={`/upyog-ui/citizen/ads/application/${application?.bookingNo}/${application?.tenantId}`}>
          <SubmitBar label={buttonLabel} />
        </Link>
        {(application.bookingStatus === "BOOKING_CREATED" || application.bookingStatus === "PAYMENT_FAILED" || application.bookingStatus === "PENDING_FOR_PAYMENT")  && (
          <SubmitBar label={t("CS_APPLICATION_DETAILS_MAKE_PAYMENT")} onSubmit={handleMakePayment} style={{ margin: "20px" }} />
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

export default AdsApplication;
