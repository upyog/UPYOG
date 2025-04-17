import { Loader } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { useRouteMatch } from "react-router-dom";
import CitizenPayment from "./citizen";

export const PaymentModule = ({ deltaConfig = {}, stateCode, cityCode, moduleCode = "Payment" }) => {
  const { path, url } = useRouteMatch();
  const store = { data: {} };

  if (!store || Object.keys(store).length === 0) {
    return <Loader />;
  }

  return <CitizenPayment {...{ stateCode, moduleCode, cityCode, path, url }} />;
};

