import React, { useState, useEffect } from 'react';
import { Toast } from "@upyog/digit-ui-react-components";

const TimerServices = ({ businessService, t, timerValues, SlotSearchData = "", setTime }) => {
  const [timeRemaining, setTimeRemaining] = useState(0); // Initialize with `timerValues`
  const [showToast, setShowToast] = useState(null);
  const [hasFetched, setHasFetched] = useState(false); // To track if data has been fetched once
  
  // Refetch logic for CHB (Community Hall Booking)
  const { refetch } = Digit.Hooks.chb.useChbSlotSearch({
    tenantId: SlotSearchData?.tenantId,
    filters: {
      bookingId: SlotSearchData?.bookingId,
      communityHallCode: SlotSearchData?.communityHallCode,
      bookingStartDate: SlotSearchData?.bookingStartDate,
      bookingEndDate: SlotSearchData?.bookingEndDate,
      hallCode: SlotSearchData?.hallCode,
      isTimerRequired: true,
    },
    enabled: false,
  });

  // Slot search data for Ads (Advertisement)
  const slotSearchData = Digit.Hooks.ads.useADSSlotSearch();

  useEffect(() => {
    const fetchSlotData = async () => {
      try {
        if (businessService === "adv-services") {
          // Prepare form data for Advertisement Service
            const formdata = {
              advertisementSlotSearchCriteria:SlotSearchData?.cartDetails.map((item) => ({
                bookingId: SlotSearchData?.bookingId,
                addType: item?.addType,
                bookingStartDate: item?.bookingDate,
                bookingEndDate: item?.bookingDate,
                faceArea: item?.faceArea,
                tenantId: SlotSearchData?.tenantId,
                location: item?.location,
                nightLight: item?.nightLight,
                isTimerRequired: true,
              })),
            };
          // Fetching data for Advertisement Service
          const result = await slotSearchData.mutateAsync(formdata);
          const isSlotBooked = result?.advertisementSlotAvailabiltityDetails?.some((slot) => slot.slotStaus === "BOOKED");
          const timerValue = result?.advertisementSlotAvailabiltityDetails[0].timerValue;

          if (isSlotBooked) {
            setShowToast({ error: true, label: t("ADS_ADVERTISEMENT_ALREADY_BOOKED") });
          } else {
            setTimeRemaining(timerValue || 0);
          }
        } else if (businessService === "chb-services") {
          // Fetching data for Community Hall Booking Service
          const result = await refetch();
          const isSlotBooked = result?.data?.hallSlotAvailabiltityDetails?.some(
            (slot) => slot.slotStaus === "BOOKED"
          );

          if (isSlotBooked) {
            setShowToast({ error: true, label: t("CHB_COMMUNITY_HALL_ALREADY_BOOKED") });
          } else {
            setTimeRemaining(result?.data.timerValue || 0);
          }
        }
      } catch (error) {
        setShowToast({ error: true, label: t("CS_SOMETHING_WENT_WRONG") });
      }
    };

    // Only fetch if timeRemaining is 0 and data hasn't been fetched before
    if (timeRemaining === 0 && !hasFetched) {
      fetchSlotData();
      setHasFetched(true); // Mark that the data has been fetched once
    }

  }, [businessService, SlotSearchData, refetch, t, timeRemaining, hasFetched, slotSearchData]);

  // Timer decrement logic (every second)
  useEffect(() => {
    if (timeRemaining > 0) {
      const interval = setInterval(() => {
        setTimeRemaining((prevTime) => {
          if (prevTime <= 0) {
            clearInterval(interval);
            return 0;
          }
          return prevTime - 1;
        });
      }, 1000);
       setTime(timeRemaining);
      // Cleanup interval when the timer is cleared or component unmounts
      return () => clearInterval(interval);
    }
  }, [timeRemaining]);
    
  // Toast cleanup (hide after 2 seconds)
  useEffect(() => {
    if (showToast) {
      const timer = setTimeout(() => {
        setShowToast(null);
      }, 2000);

      return () => clearTimeout(timer);
    }
  }, [showToast]);

  // Format time in MM:SS format
  const formatTime = (seconds) => {
    const minutes = Math.floor(seconds / 60);
    const remainingSeconds = seconds % 60;
    return `${minutes}:${remainingSeconds < 10 ? '0' : ''}${remainingSeconds}`;
  };

  return (
    <div>
      {t("CS_TIME_REMAINING")}: <span className="astericColor">{formatTime(timeRemaining)}</span>
      
      {/* Show Toast Message */}
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
    </div>
  );
};

export default TimerServices;
