import React, { useState, useEffect } from 'react';
import { Toast } from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import { getSlotSearchCriteria, calculateRemainingTime } from "../utils";

export const TimerValues = ({timerValues, SlotSearchData,draftId=""}) => {
  const { t } = useTranslation();
  /* Fetch session storage parameters to retrieve the timer start timestamp */
  const [params] = Digit.Hooks.useSessionStorage("ADS_CREATE", {});
  const timerStartedAt = params?.adslist?.existingDataSet?.timervalue?.timerStartedAt;

  /* Initialize remaining time, subtracting elapsed seconds since lock creation to prevent resets on remounts */
  const [timeRemaining, setTimeRemaining] = useState(() => calculateRemainingTime(timerValues, timerStartedAt));
  const [showToast, setShowToast] = useState(null);
  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId();
  const [hasFetched, setHasFetched] = useState(false); /* To track if data has been fetched once */

  /* Sync remaining time with prop updates, accounting for the elapsed time offset */
  useEffect(() => {
    if (timerValues) {
      setTimeRemaining(calculateRemainingTime(timerValues, timerStartedAt));
    }
  }, [timerValues, timerStartedAt]);

  // Slot search data for Ads (Advertisement)
  const slotSearchData = Digit.Hooks.ads.useADSSlotSearch();

  const formdata = {
    advertisementSlotSearchCriteria: getSlotSearchCriteria(SlotSearchData, tenantId, {}, draftId)
  };


  useEffect(() => {
    const fetchSlotData = async () => {
      try {
        /* Fetching data for Advertisement Service */
        const result = await slotSearchData.mutateAsync(formdata);
        const isSlotBooked = result?.advertisementSlotAvailabiltityDetails?.some((slot) => slot.slotStaus === "BOOKED");
        /* timerValue is resolved directly from top-level of response payload per backend contract */
        const timerValue = result?.timerValue;
        if (isSlotBooked) {
          setShowToast({ error: true, label: t("ADS_ADVERTISEMENT_ALREADY_BOOKED") });
        } else {
          setTimeRemaining(timerValue || 0);
        }
      } catch (error) {
        setShowToast({ error: true, label: t("CS_SOMETHING_WENT_WRONG") });
      }
    };

    /* Only fetch if timeRemaining is 0 and data hasn't been fetched before */
    if (timeRemaining === 0 && !hasFetched) {
      fetchSlotData();
      setHasFetched(true); /* Mark that the data has been fetched once */
    }

  }, [t, timeRemaining, hasFetched]);

  /* Timer decrement logic (every second) */
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

      /* Cleanup interval when the timer is cleared or component unmounts */
      return () => clearInterval(interval);
    }
  }, [timeRemaining]);

  /* Toast cleanup (hide after 2 seconds) */
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
      <span className="astericColor">{formatTime(timeRemaining)}</span>

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
