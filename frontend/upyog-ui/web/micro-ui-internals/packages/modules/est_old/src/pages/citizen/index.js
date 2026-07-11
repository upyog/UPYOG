import React from "react";
import { Routes, Route } from "react-router-dom";
import { AppContainer, BackButton, PrivateRoute } from "@nudmcdgnpm/digit-ui-react-components";
import ESTApplicationDetails from "./ESTApplicationDetails";
import { ESTPaymentHistory } from "./PaymentHistory";
import ESTMyApplications from "./MyApplications";

const CitizenApp = ({ path }) => {
  return (
    <span className="citizen" style={{ width: "100%" }}>
      <AppContainer>
        <BackButton>Back</BackButton>
        <Routes>
          <Route path="application/:assetNo/:tenantId" element={<PrivateRoute><ESTApplicationDetails /></PrivateRoute>} />
          <Route path="my-applications/*" element={<PrivateRoute><ESTMyApplications /></PrivateRoute>} />
          <Route path="payment-history/*" element={<PrivateRoute><ESTPaymentHistory /></PrivateRoute>} />
        </Routes>
      </AppContainer>
    </span>
  );
};

export default CitizenApp;