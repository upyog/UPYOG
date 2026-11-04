import React from "react";
import { Route, Routes } from "react-router-dom";
import { BillList } from "./my-bills/my-bills";
import BillDetails from "./bill-details/bill-details";
import { BackButton } from "@nudmcdgnpm/digit-ui-react-components";

const BillRoutes = ({ billsList, paymentRules, businessService }) => {
  const { url: currentPath, ...match } = Digit.Hooks.useModuleBasePath();

  return (
    <React.Fragment>
      <BackButton />
      <Routes>
        <Route path={`${currentPath}`} element={<BillList {...{ billsList, currentPath, paymentRules, businessService }} />} />
        <Route path={`${currentPath}/:consumerCode`} element={<BillDetails {...{ paymentRules, businessService }} />} />
      </Routes>
    </React.Fragment>
  );
};

export default BillRoutes;
