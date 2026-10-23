import React from "react";
import { useParams, useLocation, Route, Routes } from "react-router-dom";
import BillDetails from "./routes/bill-details/bill-details";

export const MyBills = ({ stateCode }) => {
  const { businessService } = useParams();
  const { tenantId: _tenantId, isDisoconnectFlow } = Digit.Hooks.useQueryParams();

  const navigate = Digit.Hooks.useCustomNavigate();
  const { url } = Digit.Hooks.useModuleBasePath();
  const location = useLocation();

  const { tenantId } = Digit.UserService.getUser()?.info || location?.state || { tenantId: _tenantId } || {};

  if (!tenantId && !location?.state?.fromSearchResults) {
    navigate(`/cnd-ui/citizen/login`, { replace: true, state: { from: url } });
  }

  const { isLoading, data } = Digit.Hooks.useFetchCitizenBillsForBuissnessService(
    { businessService },
    { refetchOnMount: true, enabled: !location?.state?.fromSearchResults }
  );

  const billsList = data?.Bill || [];

  return (
    <React.Fragment>
      <Routes>
        <Route
          path=":consumerCode"
          element={
            <BillDetails
              businessService={businessService}
            />
          }
        />
      </Routes>
    </React.Fragment>
  );
};
