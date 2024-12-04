import React, { useState, useEffect } from 'react';

const TimerValues = ({ businessService, consumerCode, t,timerValues }) => {
  const [timeRemaining, setTimeRemaining] = useState(timerValues || 0);

  useEffect(() => {
      const savedTime = localStorage.getItem(`timeRemaining-${consumerCode}`);
      if (savedTime) {
        setTimeRemaining(Number(savedTime));
      }

      const interval = setInterval(() => {
        setTimeRemaining(prevTime => {
          if (prevTime <= 0) {
            clearInterval(interval);
            localStorage.removeItem(`timeRemaining-${consumerCode}`);
            return 0;
          }
          const newTime = prevTime - 1;
          localStorage.setItem(`timeRemaining-${consumerCode}`, newTime);
          return newTime;
        });
      }, 1000);

      return () => clearInterval(interval); // Cleanup on unmount
  }, [businessService, consumerCode]);

  // Format seconds into "minutes:seconds" format
  const formatTime = (seconds) => {
    const minutes = Math.floor(seconds / 60);
    const remainingSeconds = seconds % 60;
    return `${minutes}:${remainingSeconds < 10 ? '0' : ''}${remainingSeconds}`;
  };

  return (
    <div>
      {t("CS_TIME_REMAINING")}: <span className="astericColor">{formatTime(timeRemaining)}</span>
    </div>
  );
};

export default TimerValues;
