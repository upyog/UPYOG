import React from "react";
import { Route, Routes } from "react-router-dom";
import BillDetails from "./bill-details/bill-details";
import { BackButton } from "@nudmcdgnpm/digit-ui-react-components";

const BillRoutes = ({ paymentRules, businessService }) => {

  return (
    <React.Fragment>
      <BackButton />
      <Routes>
        <Route
          path=":consumerCode"
          element={
            <BillDetails
              paymentRules={paymentRules}
              businessService={businessService}
            />
          }
        />
      </Routes>
    </React.Fragment>
  );
};

export default BillRoutes;
