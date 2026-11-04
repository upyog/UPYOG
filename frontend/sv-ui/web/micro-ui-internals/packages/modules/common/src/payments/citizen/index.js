import React from "react";
import { Routes, Route } from "react-router-dom";
import PayersDetails from "./payers-details";
import { MyBills } from "./bills";
import { SelectPaymentType } from "./payment-type/index";
import { SuccessfulPayment, FailedPayment } from "./response";

const CitizenPayment = ({ stateCode, cityCode, moduleCode }) => {

  const commonProps = { stateCode, cityCode, moduleCode };

  return (
    <React.Fragment>
      <div className="bills-citizen-wrapper">
        <Routes>
          <Route
          path="my-bills/:businessService/*"
          element={<MyBills stateCode={stateCode} />}
        />

        <Route
          path="billDetails/:businessService/:consumerCode/:paymentAmt"
          element={<PayersDetails {...commonProps} />}
        />

        <Route
          path="collect/:businessService/:consumerCode"
          element={<SelectPaymentType {...commonProps} />}
        />

        <Route
          path="success/:businessService/:consumerCode/:tenantId"
          element={<SuccessfulPayment {...commonProps} />}
        />

        <Route
          path="failure"
          element={<FailedPayment {...commonProps} />}
        />
        </Routes>
      </div>
    </React.Fragment>
  );
};

export default CitizenPayment;
