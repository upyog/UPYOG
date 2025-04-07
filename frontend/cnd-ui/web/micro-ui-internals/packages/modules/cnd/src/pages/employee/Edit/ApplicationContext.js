import React, { createContext, useContext } from 'react';

// Create the context
const ApplicationContext = createContext();

// Create a provider component
export const ApplicationProvider = ({ children, applicationDetails }) => {
  return (
    <ApplicationContext.Provider value={applicationDetails}>
      {children}
    </ApplicationContext.Provider>
  );
};

// Create a custom hook to use the context
export const useApplicationDetails = () => {
  const context = useContext(ApplicationContext);
  if (context === undefined) {
    throw new Error('useApplicationDetails must be used within an ApplicationProvider');
  }
  return context;
};