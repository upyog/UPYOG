import { useState, useEffect } from "react";

const useCurrentTime = () => {
  const [currentTime, setCurrentTime] = useState(new Date().toTimeString().split(' ')[0]);

  useEffect(() => {
    setCurrentTime(new Date().toTimeString().split(' ')[0]);
  }, []);

  return currentTime;
};

export default useCurrentTime;
