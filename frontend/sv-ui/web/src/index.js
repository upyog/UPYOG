import React from 'react';
import ReactDOM from 'react-dom/client';
import { initLibraries } from "@nudmcdgnpm/digit-ui-libraries";
// import "@upyog/sv-ui-css/dist/index.css";
import "./index.css";
import App from './App';

// Suppress React 19 useEffect warnings for UPYOG libraries
const originalConsoleError = console.error;
console.error = (...args) => {
  if (args[0]?.includes?.('useEffect must not return anything')) {
    return; // Ignore useEffect warnings
  }
  originalConsoleError.apply(console, args);
};


initLibraries();


// window.Digit.Customizations = { PGR: {} ,TL:TLCustomisations};

/**
 * Checks if user data is already stored in session storage.
 * If not, retrieves user details from local storage and saves them in session storage.
 */
const user = window.Digit.SessionStorage.get("User");

if (!user || !user.access_token || !user.info) {

  const parseValue = (value) => {
    try {
      return JSON.parse(value)
    } catch (e) {
      return value
    }
  }

  const getFromStorage = (key) => {
    const value = window.localStorage.getItem(key);
    return value && value !== "undefined" ? parseValue(value) : null;
  }

  const token = getFromStorage("token")

  const citizenToken = getFromStorage("Citizen.token")
  const citizenInfo = getFromStorage("Citizen.user-info")
  const citizenTenantId = getFromStorage("Citizen.tenant-id")

  const employeeToken = getFromStorage("Employee.token")
  const employeeInfo = getFromStorage("Employee.user-info")
  const employeeTenantId = getFromStorage("Employee.tenant-id")

  /**
   * Determines if the user is a citizen or an employee based on the token.
   * Saves the user type in session storage.
   */
  const userType = token === citizenToken ? "citizen" : "employee";
  window.Digit.SessionStorage.set("user_type", userType);
  window.Digit.SessionStorage.set("userType", userType);

  const getUserDetails = (access_token, info) => ({ token: access_token, access_token, info })

  const userDetails = userType === "citizen" ? getUserDetails(citizenToken, citizenInfo) : getUserDetails(employeeToken, employeeInfo)

  window.Digit.SessionStorage.set("User", userDetails);
  window.Digit.SessionStorage.set("Citizen.tenantId", citizenTenantId);
  window.Digit.SessionStorage.set("Employee.tenantId", employeeTenantId);
  // end
}

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(<App />);