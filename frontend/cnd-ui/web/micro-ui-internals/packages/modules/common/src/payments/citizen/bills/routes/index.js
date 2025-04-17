import React from "react";
import { Route, Switch, useRouteMatch } from "react-router-dom";
import BillDetails from "./bill-details/bill-details";
import { BackButton } from "@nudmcdgnpm/digit-ui-react-components";

const BillRoutes = ({ paymentRules, businessService }) => {
  const { url: currentPath, ...match } = useRouteMatch();

  return (
    <React.Fragment>
      <BackButton />
      <Switch>
        <Route path={`${currentPath}/:consumerCode`} component={() => <BillDetails {...{ paymentRules, businessService }} />} />
      </Switch>
    </React.Fragment>
  );
};

export default BillRoutes;
