import React from "react";
import { useParams, useHistory, useRouteMatch, useLocation } from "react-router-dom";
import Routes from "./routes";


export const MyBills = ({ stateCode }) => {
  const { businessService } = useParams();
  const { tenantId: _tenantId, isDisoconnectFlow } = Digit.Hooks.useQueryParams();

  const history = useHistory();
  const { url } = useRouteMatch();
  const location = useLocation();

  const { tenantId } = Digit.UserService.getUser()?.info || location?.state || { tenantId: _tenantId } || {};

  if (!tenantId && !location?.state?.fromSearchResults) {
    history.replace(`/sv-ui/citizen/login`, { from: url });
  }

  const { isLoading, data } = Digit.Hooks.useFetchCitizenBillsForBuissnessService(
    { businessService },
    { refetchOnMount: true, enabled: !location?.state?.fromSearchResults }
  );

  const billsList = data?.Bill || [];

  const getProps = () => ({ billsList, businessService });

  return (
    <React.Fragment>
      <Routes {...getProps()} />
    </React.Fragment>
  );
};
