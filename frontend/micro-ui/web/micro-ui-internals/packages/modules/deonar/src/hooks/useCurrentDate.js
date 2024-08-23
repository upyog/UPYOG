import { useState, useEffect } from "react";

const useDate = (offset = 0) => {
  const getCurrentDate = () => {
    const dt = new Date();
    dt.setDate(dt.getDate() + offset);
    return dt.toISOString().split('T')[0]; // Format as YYYY-MM-DD
  };

  const [currentDate, setCurrentDate] = useState(getCurrentDate);

  useEffect(() => {
    setCurrentDate(getCurrentDate());
  }, [offset]); // Recalculate date if offset changes

  return currentDate;
};

export default useDate;
