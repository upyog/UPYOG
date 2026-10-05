import { Card, CardSubHeader, Header, KeyNote, Loader, RadioButtons, SubmitBar, TextInput } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useLocation, useParams,  Navigate } from "react-router-dom";
import ArrearSummary from "./arrear-summary";
import BillSumary from "./bill-summary";
import { stringReplaceAll } from "./utils";
import TimerServices from "../../../timer-Services/timerServices";
import { timerEnabledForBusinessService } from "./utils";

const BillDetails = ({ paymentRules, businessService }) => {
  const { t } = useTranslation();
  const navigate = Digit.Hooks.useCustomNavigate();
  const { state, pathname, search } = useLocation();
  const userInfo = Digit.UserService.getUser();
  let { consumerCode } = useParams();
  const { workflow: wrkflow, tenantId: _tenantId, authorization, ConsumerName } = Digit.Hooks.useQueryParams();
  const [bill, setBill] = useState(state?.bill);
  const tenantId = state?.tenantId || _tenantId || Digit.UserService.getUser().info?.tenantId;
  const propertyId = state?.propertyId;
  const applicationNumber = state?.applicationNumber;
  const [Time, setTime ] = useState(0);
  const skipBillingAndArrears = ["adv-services", "chb-services","request-service.mobile_toilet", "request-service.water_tanker", "request-service.tree_pruning"];

  if (wrkflow === "WNS" && consumerCode.includes("?")) consumerCode = consumerCode.substring(0, consumerCode.indexOf("?"));
  const { data, isLoading } = state?.bill
    ? { isLoading: false }
    : Digit.Hooks.useFetchPayment({
        tenantId,
        businessService,
        consumerCode: wrkflow === "WNS" ? stringReplaceAll(consumerCode, "+", "/") : consumerCode,
      });
     
  let Useruuid = data?.Bill?.[0]?.userId || "";
  let requestCriteria = [
    "/user/_search",
    {},
    { data: { uuid: [Useruuid] } },
    { recordId: Useruuid, plainRequestFields: ["mobileNumber"] },
    {
      enabled: Useruuid ? true : false,
      cacheTime: 100,
    },
  ];

  const { isLoading: isUserLoading, data: userData, revalidate } = Digit.Hooks.useCustomAPIHook(...requestCriteria);
  
  const { isLoading: isFSMLoading, isError, error, data: application, error: errorApplication } = Digit.Hooks.fsm.useApplicationDetail(
    t,
    tenantId,
    consumerCode,
    { enabled: pathname.includes("FSM") ? true : false },
    "CITIZEN"
  );
  
  let { minAmountPayable, isAdvanceAllowed } = paymentRules; 
  minAmountPayable = wrkflow === "WNS" ? 100 : minAmountPayable;
  const billDetails = bill?.billDetails?.sort((a, b) => b.fromPeriod - a.fromPeriod)?.[0] || [];
  const Arrears =
    bill?.billDetails
      ?.sort((a, b) => b.fromPeriod - a.fromPeriod)
      ?.reduce((total, current, index) => (index === 0 ? total : total + current.amount), 0) || 0;

  const { key, label } = Digit.Hooks.useApplicationsForBusinessServiceSearch({ businessService }, { enabled: false });
  const getBillingPeriod = () => {
    const { fromPeriod, toPeriod } = billDetails;
    if (fromPeriod && toPeriod) {
      let from, to;
      if (wrkflow === "mcollect" || wrkflow === "WNS") {
        from =
          new Date(fromPeriod).getDate().toString() +
          " " +
          Digit.Utils.date.monthNames[new Date(fromPeriod).getMonth()]?.toString() +
          " " +
          new Date(fromPeriod).getFullYear().toString();
        to = new Date(toPeriod).getDate() + " " + Digit.Utils.date.monthNames[new Date(toPeriod).getMonth()] + " " + new Date(toPeriod).getFullYear();
        return from + " - " + to;
      }
      from = new Date(billDetails.fromPeriod).getFullYear().toString();
      to = new Date(billDetails.toPeriod).getFullYear().toString();
      if (from === to) {
        if(window.location.href.includes("BPA"))
        {
          if(new Date(data?.Bill?.[0]?.billDate).getMonth()+1 < 4)
          {
            let newfrom =  (parseInt(from)-1).toString();
            return "FY " + newfrom + "-" + to;
          }
          else
          {
            let newTo = (parseInt(to)+1).toString();
            return "FY " + from + "-" + newTo;
          }
        }
        else
        return "FY " + from;
      }
      return "FY " + from + "-" + to;
    } else return "N/A";
  };

  const getBillBreakDown = () => billDetails?.billAccountDetails || [];

  const getTotal = () => bill?.totalAmount || 0;
  const getAdvanceAmount = () => application?.pdfData?.advanceAmount;

  const [paymentType, setPaymentType] = useState(t("CS_PAYMENT_FULL_AMOUNT"));
  const [amount, setAmount] = useState(getTotal());
  const [paymentAllowed, setPaymentAllowed] = useState(true);
  const [formError, setError] = useState("");

  if (authorization === "true" && !userInfo?.access_token) {
    localStorage.clear();
    sessionStorage.clear();
    window.location.href = `/upyog-ui/citizen/login?from=${encodeURIComponent(pathname + search)}`;
  }
  useEffect(() => {
    window.scroll({ top: 0, behavior: "smooth" });
  }, []);

  useEffect(() => {
    if (paymentType == t("CS_PAYMENT_FULL_AMOUNT")) setAmount(getTotal());
  }, [paymentType, bill]);

  useEffect(() => {
    let changeAdvanceAllowed = isAdvanceAllowed;
    if (isAdvanceAllowed && wrkflow === "WNS") changeAdvanceAllowed = false;
    const allowPayment = minAmountPayable && amount >= minAmountPayable && !changeAdvanceAllowed && amount <= getTotal() && !formError;
    if (paymentType != t("CS_PAYMENT_FULL_AMOUNT")) setPaymentAllowed(allowPayment);
    else setPaymentAllowed(true);
  }, [paymentType, amount]);

  useEffect(() => {
    if (!isFSMLoading && (application?.pdfData?.applicationStatus === "PENDING_APPL_FEE_PAYMENT_CITIZEN" || application?.pdfData?.applicationStatus ==="PENDING_APPL_FEE_PAYMENT")) {
      setPaymentAllowed(true);
      setPaymentType(t("CS_PAYMENT_ADV_COLLECTION"));
    }
  });

  useEffect(() => {
    if (!bill && data) {
      let requiredBill = data.Bill.filter((e) => e.consumerCode == (wrkflow === "WNS" ? stringReplaceAll(consumerCode, "+", "/") : consumerCode))[0];
      setBill(requiredBill);
    }
  }, [isLoading]); 

  const onSubmit = () => {
    let paymentAmount =
      paymentType === t("CS_PAYMENT_FULL_AMOUNT")
        ? businessService === "FSM.TRIP_CHARGES" ? application?.pdfData?.advanceAmount : getTotal()
        : amount || businessService === "FSM.TRIP_CHARGES"
        ? application?.pdfData?.advanceAmount
        : amount;
    if (window.location.href.includes("mcollect")) {
      navigate(`/upyog-ui/citizen/payment/collect/${businessService}/${consumerCode}?workflow=mcollect`, {
        state:{
            paymentAmount,
            tenantId: billDetails.tenantId
        }
      });
    } else if (wrkflow === "WNS") {
      navigate(`/upyog-ui/citizen/payment/billDetails/${businessService}/${consumerCode}/${paymentAmount}?workflow=WNS&ConsumerName=${ConsumerName}`, {
        state: {
          paymentAmount,
          tenantId: billDetails.tenantId,
          name: bill.payerName,
          mobileNumber: bill.mobileNumber && bill.mobileNumber?.includes("*") ? userData?.user?.[0]?.mobileNumber : bill.mobileNumber,
        }
      });
    } else if (businessService === "PT") {
      navigate(`/upyog-ui/citizen/payment/billDetails/${businessService}/${consumerCode}/${paymentAmount}`, {
        state: {
          paymentAmount,
          tenantId: billDetails.tenantId,
          name: bill.payerName,
          mobileNumber: bill.mobileNumber && bill.mobileNumber?.includes("*") ? userData?.user?.[0]?.mobileNumber : bill.mobileNumber,
        }
      });
    }
    else if (timerEnabledForBusinessService(businessService)) {
      navigate(`/upyog-ui/citizen/payment/collect/${businessService}/${consumerCode}`, {
        state: {
        paymentAmount,
        tenantId: billDetails.tenantId,
        propertyId: propertyId,
        timerValue: state?.timerValue,
        SlotSearchData: state?.SlotSearchData
        }
      });
    }
    else {
      navigate(`/upyog-ui/citizen/payment/collect/${businessService}/${consumerCode}`, { 
       state : {paymentAmount, tenantId: billDetails.tenantId, propertyId: propertyId}
       });
    }
  };

  const onChangeAmount = (value) => {
    setError("");
    if (isNaN(value) || value.includes(".")) {
      setError("AMOUNT_INVALID");
    } else if (!isAdvanceAllowed && value > getTotal()) {
      setError("CS_ADVANCED_PAYMENT_NOT_ALLOWED");
    } else if (value < minAmountPayable) {
      setError("CS_CANT_PAY_BELOW_MIN_AMOUNT");
    }
    setAmount(value);
  };

  if (isLoading || isFSMLoading) return <Loader />;

  return (
    <div className="cmn-bill-details-container">
      <div className="cmn-bill-details-header-wrap">
        <h1 className="cmn-bill-details-page-title">{t("CS_PAYMENT_BILL_DETAILS")}</h1>
      </div>

      <div className="cmn-bill-details-card">
        {/* Top Hero Row */}
        <div className="cmn-bill-hero-section">
          <div className="cmn-bill-consumer-block">
            <span className="cmn-bill-consumer-label">
              {t(businessService === "PT.MUTATION" ? "PDF_STATIC_LABEL_MUATATION_NUMBER_LABEL" : label)}
            </span>
            <span className="cmn-bill-consumer-value">
              {wrkflow === "WNS" ? stringReplaceAll(consumerCode, "+", "/") : consumerCode}
            </span>
          </div>

          {timerEnabledForBusinessService(businessService) && (
            <div className="cmn-bill-timer-badge">
              <TimerServices
                businessService={businessService}
                setTime={setTime}
                timerValues={state?.timerValue}
                t={t}
                SlotSearchData={state?.SlotSearchData}
              />
            </div>
          )}
        </div>

        {/* Bill Meta Grid: Period, Bill Number, Due Date */}
        <div className="cmn-bill-meta-grid">
          {businessService !== "PT.MUTATION" && businessService !== "FSM.TRIP_CHARGES" && !skipBillingAndArrears.includes(businessService) && (
            <div className="cmn-bill-meta-card">
              <span className="cmn-bill-meta-label">{t("CS_PAYMENT_BILLING_PERIOD")}</span>
              <span className="cmn-bill-meta-val">{getBillingPeriod()}</span>
            </div>
          )}
          {(businessService?.includes("PT") || (wrkflow === "WNS" && billDetails?.currentBillNo)) && (
            <div className="cmn-bill-meta-card">
              <span className="cmn-bill-meta-label">{t("CS_BILL_NO")}</span>
              <span className="cmn-bill-meta-val">{billDetails?.currentBillNo || "N/A"}</span>
            </div>
          )}
          {(businessService?.includes("PT") || (wrkflow === "WNS" && billDetails?.currentExpiryDate)) && (
            <div className="cmn-bill-meta-card">
              <span className="cmn-bill-meta-label">{t("CS_BILL_DUEDATE")}</span>
              <span className="cmn-bill-meta-val">
                {billDetails?.currentExpiryDate ? new Date(billDetails.currentExpiryDate).toLocaleDateString() : "N/A"}
              </span>
            </div>
          )}
        </div>

        {/* Bill Breakdown / FSM Trip Details */}
        {businessService === "FSM.TRIP_CHARGES" ? (
          <div className="cmn-fsm-bill-summary">
            <div className="cmn-fsm-row">
              <span className="fsm-label">{t("ES_PAYMENT_DETAILS_TOTAL_AMOUNT")}</span>
              <span className="fsm-val">₹ {application?.pdfData?.totalAmount}</span>
            </div>
            <div className="cmn-fsm-row">
              <span className="fsm-label">{t("ES_PAYMENT_DETAILS_ADV_AMOUNT")}</span>
              <span className="fsm-val">₹ {application?.pdfData?.advanceAmount}</span>
            </div>
            {application?.pdfData?.applicationStatus !== "PENDING_APPL_FEE_PAYMENT_CITIZEN" ||
            application?.pdfData?.applicationStatus !== "PENDING_APPL_FEE_PAYMENT" ? (
              <div className="cmn-fsm-row">
                <span className="fsm-label">{t("FSM_DUE_AMOUNT_TO_BE_PAID")}</span>
                <span className="fsm-val">
                  ₹ {application?.pdfData?.totalAmount - application?.pdfData?.advanceAmount}
                </span>
              </div>
            ) : null}
          </div>
        ) : (
          <BillSumary
            billAccountDetails={getBillBreakDown()}
            total={getTotal()}
            businessService={businessService}
            arrears={Arrears}
            skipArrears={skipBillingAndArrears}
          />
        )}

        {/* Arrear Breakdown */}
        <ArrearSummary bill={bill} />

        {/* Payment Amount & Selection */}
        <div className="cmn-bill-payment-section">
          <div className="cmn-payment-section-title">{t("CS_COMMON_PAYMENT_AMOUNT")}</div>

          {businessService === "FSM.TRIP_CHARGES" ? null : (
            <div className="cmn-bill-payment-radios">
              <RadioButtons
                selectedOption={paymentType}
                onSelect={setPaymentType}
                options={
                  paymentRules.partPaymentAllowed &&
                  application?.pdfData?.paymentPreference !== "POST_PAY" &&
                  application?.pdfData?.applicationStatus === "PENDING_APPL_FEE_PAYMENT_CITIZEN"
                    ? [t("CS_PAYMENT_ADV_COLLECTION")]
                    : [t("CS_PAYMENT_FULL_AMOUNT")]
                }
              />
            </div>
          )}

          <div className="cmn-bill-amount-input-box">
            <div className="cmn-bill-amount-input-inner">
              <span className="cmn-currency-symbol">₹</span>
              {paymentType !== t("CS_PAYMENT_FULL_AMOUNT") ? (
                businessService === "FSM.TRIP_CHARGES" ? (
                  <TextInput onChange={() => {}} value={getAdvanceAmount()} disable={true} />
                ) : (
                  <TextInput onChange={(e) => onChangeAmount(e.target.value)} value={amount} disable={getTotal() === 0} />
                )
              ) : businessService === "FSM.TRIP_CHARGES" ? (
                <TextInput value={application?.pdfData?.advanceAmount} onChange={() => {}} disable={true} />
              ) : (
                <TextInput value={getTotal()} onChange={() => {}} disable={true} />
              )}
            </div>
            {formError === "CS_CANT_PAY_BELOW_MIN_AMOUNT" ? (
              <span className="cmn-bill-error-msg">
                {t(formError)}: {"₹" + minAmountPayable}
              </span>
            ) : formError ? (
              <span className="cmn-bill-error-msg">{t(formError)}</span>
            ) : null}
          </div>

          <div className="cmn-bill-submit-wrap">
            <button
              type="button"
              className="cmn-bill-submit-btn"
              disabled={!paymentAllowed || getTotal() === 0}
              onClick={onSubmit}
            >
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
                <rect x="1" y="4" width="22" height="16" rx="2" ry="2" />
                <line x1="1" y1="10" x2="23" y2="10" />
              </svg>
              {t("CS_COMMON_PROCEED_TO_PAY")}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default BillDetails;
