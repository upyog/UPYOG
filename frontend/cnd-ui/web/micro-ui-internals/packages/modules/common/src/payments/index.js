import { Loader } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { useRouteMatch } from "react-router-dom";
import CitizenPayment from "./citizen";
import EmployeePayment from "./employee";


export const PaymentModule = ({ deltaConfig = {}, stateCode, cityCode, moduleCode = "Payment", userType }) => {
  const { path, url } = useRouteMatch();
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
