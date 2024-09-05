import React, { createContext, useContext, useState } from 'react';

const GlobalStateContext = createContext();

export const useTourState = () => {
  return useContext(GlobalStateContext);
};

export const TourProvider = ({ children }) => {
  const [tourState, setTourState] = useState({
    run: false,
    // stepIndex: 0,
    steps:[],
    tourActive: false,
  });

  return (
    <GlobalStateContext.Provider value={{ tourState, setTourState }}>
      {children}
    </GlobalStateContext.Provider>
  );
};