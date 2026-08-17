import React from 'react';
import { createRoot } from 'react-dom/client';
import { initLibraries } from "@upyog/digit-ui-libraries";
/* if you want to run the css locally, then you have to add import "@nudmcdgnpm/upyog-css/src/index.scss" and comment out the import "@nudmcdgnpm/upyog-css/index.css"
If you want the npm published css to run here, then you have to add   import "@nudmcdgnpm/upyog-css/index.css" and comment out the import "@nudmcdgnpm/upyog-css/src/index.scss" */
// import "@nudmcdgnpm/upyog-css/src/index.scss";
import "@nudmcdgnpm/upyog-css/index.css";
import App from './App';

initLibraries();

const user = window.Digit.SessionStorage.get("User");

if (!user || !user.access_token || !user.info) {
  // login detection

  const parseValue = (value) => {
    try {
      return JSON.parse(value);
    } catch (e) {
      return value;
    }
  };

  const getFromStorage = (key) => {
    const value = window.localStorage.getItem(key);
    return value && value !== "undefined" ? parseValue(value) : null;
  };

  const token = getFromStorage("token");

  const citizenToken = getFromStorage("Citizen.token");
  const citizenInfo = getFromStorage("Citizen.user-info");
  const citizenTenantId = getFromStorage("Citizen.tenant-id");

  const employeeToken = getFromStorage("Employee.token");
  const employeeInfo = getFromStorage("Employee.user-info");
  const employeeTenantId = getFromStorage("Employee.tenant-id");

  const userType = token === citizenToken ? "citizen" : "employee";
  window.Digit.SessionStorage.set("user_type", userType);
  window.Digit.SessionStorage.set("userType", userType);

  const getUserDetails = (access_token, info) => ({ token: access_token, access_token, info })

  const userDetails = userType === "citizen" ? getUserDetails(citizenToken, citizenInfo) : getUserDetails(employeeToken, employeeInfo)

  window.Digit.SessionStorage.set("User", userDetails);
  window.Digit.SessionStorage.set("Citizen.tenantId", citizenTenantId);
  window.Digit.SessionStorage.set("Employee.tenantId", employeeTenantId);
}

// ✅ React 18+ root API
const root = createRoot(document.getElementById('root'));

root.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);

