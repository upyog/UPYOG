import React from "react";
import { useParams, useLocation } from "react-router-dom";
import Routes from "./routes";


export const MyBills = ({ stateCode }) => {
  const { businessService } = useParams();
  const { tenantId: _tenantId, isDisoconnectFlow } = Digit.Hooks.useQueryParams();
 // useCustomNavigate is custom hook which is used to navigate to different routes in the application.
  const navigate = Digit.Hooks.useCustomNavigate();

  const location = useLocation();
  const url = location.pathname;

  const { tenantId } = Digit.UserService.getUser()?.info || location?.state || { tenantId: _tenantId } || {};

  if (!tenantId && !location?.state?.fromSearchResults) {
    navigate(`/sv-ui/citizen/login`, { replace: true, state: { from: url } });
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
