import React from 'react';
import ReactDOM from 'react-dom';
import { initLibraries } from "@upyog/digit-ui-libraries";
// import "@egovernments/digit-ui-css/dist/index.css";
import "./index.css";
import App from './App';
import { TLCustomisations } from './Customisations/tl/TLCustomisation';


initLibraries();


window.Digit.Customizations = { PGR: {} ,TL:TLCustomisations};

const user = window.Digit.SessionStorage.get("User");

if (!user || !user.access_token || !user.info) {
  // login detection

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

  const token = sessionStorage.getItem("token")

  const citizenToken = sessionStorage.getItem("Citizen.token")
  const citizenInfo = sessionStorage.getItem("Citizen.user-info")
  const citizenTenantId = sessionStorage.getItem("Citizen.tenant-id")

  const employeeToken = sessionStorage.getItem("Employee.token")
  const employeeInfo = sessionStorage.getItem("Employee.user-info")
  const employeeTenantId = getFromStorage("Employee.tenant-id")

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

ReactDOM.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>,
  document.getElementById('root')
);

