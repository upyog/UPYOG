import { Loader } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useEffect } from "react";
import { useParams, useLocation, Route, Routes } from "react-router-dom";
import { BillList } from "../bills/routes/my-bills/my-bills";
import BillDetails from "./routes/bill-details/bill-details";
import { useTranslation } from "react-i18next";
import { BackButton } from "@nudmcdgnpm/digit-ui-react-components";

export const MyBills = ({ stateCode }) => {
  const { businessService } = useParams();
  const { tenantId: _tenantId, isDisoconnectFlow } = Digit.Hooks.useQueryParams();

  const { isLoading: storeLoading, data: store } = Digit.Services.useStore({
    stateCode,
    moduleCode: businessService,
    language: Digit.StoreData.getCurrentLanguage(),
  });

  const navigate = Digit.Hooks.useCustomNavigate();
  const { url } = Digit.Hooks.useModuleBasePath();
  const location = useLocation();

  const { tenantId } = Digit.UserService.getUser()?.info || location?.state || { tenantId: _tenantId } || {};

  if (!tenantId && !location?.state?.fromSearchResults) {
    navigate(`/upyog-ui/citizen/login`, { replace: true, state: { from: url } });
  }

  const { isLoading, data } = Digit.Hooks.useFetchCitizenBillsForBuissnessService(
    { businessService },
    { refetchOnMount: true, enabled: !location?.state?.fromSearchResults }
  );
  const { isLoading: mdmsLoading, data: mdmsBillingData } = Digit.Hooks.useGetPaymentRulesForBusinessServices(tenantId);

  const billsList = data?.Bill || [];

  const getPaymentRestrictionDetails = () => {
    const payRestrictiondetails = mdmsBillingData?.MdmsRes?.BillingService?.BusinessService;
    let updatedBussinessService = ((businessService === "WS" || businessService === "SW") && isDisoconnectFlow === "true") ? "DISCONNECT" : businessService;
    if (payRestrictiondetails?.length) return payRestrictiondetails.filter((e) => e.code == updatedBussinessService)?.[0]||{
      isAdvanceAllowed: false,
      isVoucherCreationEnabled: true,
      minAmountPayable: 100,
      partPaymentAllowed: false,
    };
    else
      return {
        // isAdvanceAllowed: false,
        // isVoucherCreationEnabled: true,
        // minAmountPayable: 100,
        // partPaymentAllowed: true,
      };
  };

  const getProps = () => ({ billsList, paymentRules: getPaymentRestrictionDetails(), businessService });

  if (mdmsLoading) {
    return <Loader />;
  }

  return (
    <React.Fragment>
      <BackButton />
      <Routes>
        {/* index route = exactly /upyog-ui/citizen/payment/my-bills */}
<Route
  index
  element={
    <BillList
      billsList={billsList}
      paymentRules={getPaymentRestrictionDetails()}
      businessService={businessService}
    />
  }
/>
<Route
  path=":consumerCode"
  element={
    <BillDetails
      paymentRules={getPaymentRestrictionDetails()}
      businessService={businessService}
    />
  }
/>
      </Routes>
    </React.Fragment>
  );
};
