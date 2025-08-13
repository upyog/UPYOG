import { Card, Header, KeyNote, Loader, SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory, useLocation, useParams } from "react-router-dom";

const BillDetails = ({ businessService }) => {
  const { t } = useTranslation();
  const history = useHistory();
  const { state, pathname, search } = useLocation();
  const userInfo = Digit.UserService.getUser();
  let { consumerCode } = useParams();
  const { tenantId: _tenantId, authorization } = Digit.Hooks.useQueryParams();
  const [bill, setBill] = useState(state?.bill);
  const tenantId = state?.tenantId || _tenantId || Digit.UserService.getUser().info?.tenantId;

  const { data, isLoading } =  Digit.Hooks.useFetchPayment({tenantId, businessService, consumerCode: consumerCode});
  const billDetails = bill?.billDetails?.sort((a, b) => b.fromPeriod - a.fromPeriod)?.[0] || [];

  const { key, label } = Digit.Hooks.useApplicationsForBusinessServiceSearch({ businessService }, { enabled: false });
  const getBillingPeriod = () => {
    const { fromPeriod, toPeriod } = billDetails;
    if (fromPeriod && toPeriod) {
      let from, to;
      from = new Date(billDetails.fromPeriod).getFullYear().toString();
      to = new Date(billDetails.toPeriod).getFullYear().toString();
      if (from === to) {
        return "FY " + from;
      }
      return "FY " + from + "-" + to;
    } else return "N/A";
  };

  const getTotal = () => bill?.totalAmount || 0;

  const [paymentType, setPaymentType] = useState(t("CS_PAYMENT_FULL_AMOUNT"));
  const [amount, setAmount] = useState(getTotal());

  if (authorization === "true" && !userInfo?.access_token) {
    localStorage.clear();
    sessionStorage.clear();
    window.location.href = `/cnd-ui/citizen/login?from=${encodeURIComponent(pathname + search)}`;
  }
  useEffect(() => {
    window.scroll({ top: 0, behavior: "smooth" });
  }, []);

  useEffect(() => {
    if (paymentType == t("CS_PAYMENT_FULL_AMOUNT")) setAmount(getTotal());
  }, [paymentType, bill]);

  useEffect(() => {
    if (!bill && data) {
      const requiredBill = data.Bill.filter((e) => e.consumerCode == consumerCode)[0];
      setBill(requiredBill);
    }
  }, [isLoading]);
  
  const onSubmit = () => {
    let paymentAmount = paymentType === t("CS_PAYMENT_FULL_AMOUNT") ? getTotal() : amount;
      history.push(`/cnd-ui/citizen/payment/collect/${businessService}/${consumerCode}`, { paymentAmount, tenantId: billDetails.tenantId});
    
  };
  
  if (isLoading) return <Loader />;

  return (
    <React.Fragment>
      <Header>{t("CS_PAYMENT_BILL_DETAILS")}</Header>
      <Card>
        <div>
        <div style={{ display: "flex", justifyContent: "space-between" }}>
          <KeyNote keyValue={t(label)} note={consumerCode} />
        </div>
          {/* <KeyNote keyValue={t("CS_PAYMENT_BILLING_PERIOD")} note={getBillingPeriod()} /> */}
          <KeyNote keyValue={t("CS_PAYMENT_TOTAL_AMOUNT")} note={`₹ ${getTotal().toLocaleString("en-IN")}`} />
        </div>

        <div className="bill-payment-amount">
          <SubmitBar disabled={getTotal() === 0} onSubmit={onSubmit} label={t("CS_COMMON_PROCEED_TO_PAY")} />
        </div>
      </Card>
    </React.Fragment>
  );
};
export default BillDetails;
