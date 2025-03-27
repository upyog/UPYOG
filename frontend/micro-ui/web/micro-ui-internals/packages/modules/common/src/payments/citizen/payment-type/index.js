import React, { useEffect, useState } from "react";
import {
  Header,
  Card,
  RadioButtons,
  SubmitBar,
  BackButton,
  CardLabel,
  CardLabelDesc,
  CardSectionHeader,
  InfoBanner,
  Loader,
  Toast,
  CardText,
} from "@upyog/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import { useForm, Controller } from "react-hook-form";
import { useParams, useHistory, useLocation, Redirect } from "react-router-dom";
import { stringReplaceAll } from "../bills/routes/bill-details/utils";
import $ from "jquery";
import { makePayment } from "./payGov";

export const SelectPaymentType = (props) => {
  const { state = {} } = useLocation();
  // console.log("SelectPaymentType==",state)
  const userInfo = Digit.UserService.getUser();
  const [showToast, setShowToast] = useState(null);
  const { tenantId: __tenantId, authorization, workflow: wrkflow , consumerCode : connectionNo } = Digit.Hooks.useQueryParams();
  const paymentAmount = state?.paymentAmount;
  const { t } = useTranslation();
  const history = useHistory();
  const { pathname, search } = useLocation();
  // const menu = ["AXIS"];
  let { consumerCode, businessService } = useParams();
  const tenantId = state?.tenantId || __tenantId || Digit.ULBService.getCurrentTenantId();
  const propertyId = state?.propertyId;
  const stateTenant = Digit.ULBService.getStateId();
  const { control, handleSubmit } = useForm();
  const { data: menu, isLoading } = Digit.Hooks.useCommonMDMS(stateTenant, "DIGIT-UI", "PaymentGateway");
  const { data: paymentdetails, isLoading: paymentLoading } = Digit.Hooks.useFetchPayment(
    { tenantId: tenantId, consumerCode: wrkflow === "WNS" ? connectionNo : consumerCode, businessService },
    {}
  );
  if (window.location.href.includes("ISWSCON") || wrkflow === "WNS") consumerCode = decodeURIComponent(consumerCode);
  if( wrkflow === "WNS") consumerCode = stringReplaceAll(consumerCode,"+","/")
  useEffect(() => {
    if (paymentdetails?.Bill && paymentdetails.Bill.length == 0) {
      setShowToast({ key: true, label: "CS_BILL_NOT_FOUND" });
    }
  }, [paymentdetails]);
  useEffect(() => {
    localStorage.setItem("BillPaymentEnabled", "true");
  }, []);
  const { name, mobileNumber } = state;

  const billDetails = paymentdetails?.Bill ? paymentdetails?.Bill[0] : {};

  const onSubmit = async (d) => {
    const filterData = {
      Transaction: {
        tenantId: billDetails?.tenantId,
        txnAmount: paymentAmount || billDetails.totalAmount,
        module: businessService,
        billId: billDetails.id,
        consumerCode: consumerCode,
        productInfo: "Common Payment",
        gateway: d?.paymentType || "AXIS",
        taxAndPayments: [
          {
            billId: billDetails.id,
            amountPaid: paymentAmount || billDetails.totalAmount,
          },
        ],
        user: {
          name: name || userInfo?.info?.name || billDetails?.payerName,
          mobileNumber: mobileNumber || userInfo?.info?.mobileNumber || billDetails?.mobileNumber,
          tenantId: billDetails?.tenantId,
        },
        // success
        callbackUrl: window.location.href.includes("mcollect") || wrkflow === "WNS"
          ? `${window.location.protocol}//${window.location.host}/digit-ui/citizen/payment/success/${businessService}/${wrkflow === "WNS"? encodeURIComponent(consumerCode):consumerCode}/${tenantId}?workflow=${wrkflow === "WNS"? wrkflow : "mcollect"}`
          : `${window.location.protocol}//${window.location.host}/digit-ui/citizen/payment/success/${businessService}/${wrkflow === "WNS"? encodeURIComponent(consumerCode):consumerCode}/${tenantId}?propertyId=${consumerCode}`,
          // `${window.location.protocol}//${window.location.host}/digit-ui/citizen/payment/success/${businessService}/${wrkflow === "WNS"? encodeURIComponent(consumerCode):consumerCode}/${tenantId}?propertyId=${propertyId}`,
        additionalDetails: {
          isWhatsapp: false,
        },
      },
    };

    try {
      const data = await Digit.PaymentService.createCitizenReciept(billDetails?.tenantId, filterData);
      // console.log("createCitizenReciept==",data,d)
      // console.log("=========",JSON.stringify(data));
      const redirectUrl = data?.Transaction?.redirectUrl;
      if (d?.paymentType == "AXIS") {
        window.location = redirectUrl;
      } else {
        // new payment gatewayfor UPYOG pay
        try {

          const gatewayParam = redirectUrl
            ?.split("?")
            ?.slice(1)
            ?.join("?")
            ?.split("&")
            ?.reduce((curr, acc) => {
              var d = acc.split("=");
              curr[d[0]] = d[1];
              return curr;
            }, {});
            // console.log("gatewayParam==",JSON.stringify(gatewayParam))
          var newForm = $("<form>", {
            action: gatewayParam.txURL,
            method: "POST",
            target: "_top",
          });

          const orderForNDSLPaymentSite = [
            "checksum",
            "messageType",
            "merchantId",
            "serviceId",
            "orderId",
            "customerId",
            "transactionAmount",
            "currencyCode",
            "requestDateTime",
            "successUrl",
            "failUrl",
            "additionalField1",
            "additionalField2",
            "additionalField3",
            "additionalField4",
            "additionalField5",
          ];

          // override default date for UPYOG Custom pay
          gatewayParam["requestDateTime"] = gatewayParam["requestDateTime"]?.split(new Date().getFullYear()).join(`${new Date().getFullYear()} `);

          gatewayParam["successUrl"]= redirectUrl?.split("successUrl=")?.[1]?.split("eg_pg_txnid=")?.[0]+'eg_pg_txnid=' +gatewayParam?.orderId;
          gatewayParam["failUrl"]= redirectUrl?.split("failUrl=")?.[1]?.split("eg_pg_txnid=")?.[0]+'eg_pg_txnid=' +gatewayParam?.orderId;
          // gatewayParam["successUrl"]= data?.Transaction?.callbackUrl;
          // gatewayParam["failUrl"]= data?.Transaction?.callbackUrl;

          // var formdata = new FormData();

          for (let key of orderForNDSLPaymentSite) {

            // formdata.append(key,gatewayParam[key]);

            newForm.append(
              $("<input>", {
                name: key,
                value: gatewayParam[key],
                // type: "hidden",
              })
            );
          }
          // console.log("newForm===",newForm)
          $(document.body).append(newForm);
          newForm.submit();
          makePayment(gatewayParam.txURL,formdata);

        } catch (e) {
          console.log("Error in payment redirect ", e);
          //window.location = redirectionUrl;
        }
      }
      // window.location = redirectUrl;
    } catch (error) {
      let messageToShow = "CS_PAYMENT_UNKNOWN_ERROR_ON_SERVER";
      if (error.response?.data?.Errors?.[0]) {
        const { code, message } = error.response?.data?.Errors?.[0];
        messageToShow = code;
      }
      setShowToast({ key: true, label: t(messageToShow) });
    }
  };

  if (authorization === "true" && !userInfo.access_token) {
    localStorage.clear();
    sessionStorage.clear();
    window.location.href = `/digit-ui/citizen/login?from=${encodeURIComponent(pathname + search)}`;
  }

  if (isLoading || paymentLoading) {
    return <Loader />;
  }

  return (
    <React.Fragment>
      <BackButton>{t("CS_COMMON_BACK")}</BackButton>
      <form onSubmit={handleSubmit(onSubmit)}>
        <Header>{t("PAYMENT_CS_HEADER")}</Header>
        <Card>
          <div className="payment-amount-info" style={{ marginBottom: "26px" }}>
            <CardLabel className="dark">{t("PAYMENT_CS_TOTAL_AMOUNT_DUE")}</CardLabel>
            <CardSectionHeader> ₹ { paymentAmount !== undefined ? Number(paymentAmount).toFixed(2) : Number(billDetails?.totalAmount).toFixed(2)}</CardSectionHeader>
          </div>
          <CardLabel>{t("PAYMENT_CS_SELECT_METHOD")}</CardLabel>
          {menu?.length && (
            <Controller
              name="paymentType"
              defaultValue={menu[0]}
              control={control}
              render={(props) => <RadioButtons selectedOption={props.value} options={menu} onSelect={props.onChange} />}
            />
          )}
          {!showToast && <SubmitBar label={t("PAYMENT_CS_BUTTON_LABEL")} submit={true} />}
        </Card>
      </form>
      <InfoBanner label={t("CS_COMMON_INFO")} text={t("CS_PAYMENT_REDIRECT_NOTICE")} />
      {showToast && (
        <Toast
          error={showToast.key}
          label={t(showToast.label)}
          onClose={() => {
            setShowToast(null);
          }}
        />
      )}
    </React.Fragment>
  );
};
