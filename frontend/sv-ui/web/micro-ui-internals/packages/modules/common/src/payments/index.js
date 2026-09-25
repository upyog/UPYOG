import { Loader } from "@nudmcdgnpm/upyog-ui-react-components-lts";
import React from "react";
import { useResolvedPath, useLocation } from "react-router-dom";
import CitizenPayment from "./citizen";
import EmployeePayment from "./employee";


export const PaymentModule = ({ deltaConfig = {}, stateCode, cityCode, moduleCode = "Payment", userType }) => {
  const resolved = useResolvedPath(".");
  const path = resolved.pathname;
  const url = useLocation().pathname;
  
  const store = { data: {} };

  if (Object.keys(store).length === 0) {
    return <Loader />;
  }

  const getPaymentHome = () => {
    if (userType === "citizen") return <CitizenPayment {...{ stateCode, moduleCode, cityCode, path, url }} />;
    else return <EmployeePayment {...{ stateCode, cityCode, moduleCode }} />;
  };
  return <React.Fragment>{getPaymentHome()}</React.Fragment>;
};
