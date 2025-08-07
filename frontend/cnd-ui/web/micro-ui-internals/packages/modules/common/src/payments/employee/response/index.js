import React, { useEffect, useState } from "react";
import { Banner, Card, CardText, SubmitBar, ActionBar } from "@nudmcdgnpm/digit-ui-react-components";
import { useParams, Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { useQueryClient } from "react-query";

export const convertEpochToDate = (dateEpoch) => {
  // Returning NA in else case because new Date(null) returns Current date from calender
  if (dateEpoch) {
    const dateFromApi = new Date(dateEpoch);
    let month = dateFromApi.getMonth() + 1;
    let day = dateFromApi.getDate();
    let year = dateFromApi.getFullYear();
    month = (month > 9 ? "" : "0") + month;
    day = (day > 9 ? "" : "0") + day;
    return `${day}/${month}/${year}`;
  } else {
    return "NA";
  }
};
export const SuccessfulPayment = (props) => {
  const { t } = useTranslation();
  const queryClient = useQueryClient();
  let { consumerCode, receiptNumber, businessService } = useParams();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  receiptNumber = receiptNumber.replace(/%2F/g, "/");
  
 

  useEffect(() => {
    return () => {
      const fetchData = async () => {
        const tenantId = Digit.ULBService.getCurrentTenantId();
        const state = Digit.ULBService.getStateId();
        const payments = await Digit.PaymentService.getReciept(tenantId, businessService, { receiptNumbers: receiptNumber });
        let response = { filestoreIds: [payments.Payments[0]?.fileStoreId] };
        if (!payments.Payments[0]?.fileStoreId) {
          response = await Digit.PaymentService.generatePdf(state, { Payments: payments.Payments }, generatePdfKey);
        }
      }

      // call the function
      fetchData()
      queryClient.clear();
    };
  }, []);

  const getMessage = () => t("ES_PAYMENT_COLLECTED");
  const getCardText = () => {
      return t("ES_PAYMENT_SUCCESSFUL_DESCRIPTION");
  };
  const { data: generatePdfKey } = Digit.Hooks.useCommonMDMS(tenantId, "common-masters", "ReceiptKey", {
    select: (data) =>
      data["common-masters"]?.uiCommonPay?.filter(({ code }) => businessService?.includes(code))[0]?.receiptKey || "cnd-service",
  });

  const printReciept = async () => {
    const tenantId = Digit.ULBService.getCurrentTenantId();
    const state = Digit.ULBService.getStateId();
    const payments = await Digit.PaymentService.getReciept(tenantId, businessService, { receiptNumbers: receiptNumber });
    let response = { filestoreIds: [payments.Payments[0]?.fileStoreId] };

    if (!payments.Payments[0]?.fileStoreId) {
       response = await Digit.PaymentService.generatePdf(state, { Payments: payments.Payments }, generatePdfKey);
    }
    const fileStore = await Digit.PaymentService.printReciept(state, { fileStoreIds: response.filestoreIds[0] });
    window.open(fileStore[response.filestoreIds[0]], "_blank");
  };
  return (
    <React.Fragment>
      <Card>
        <Banner message={getMessage()} info={t("PAYMENT_LOCALIZATION_RECIEPT_NO")} applicationNumber={receiptNumber} successful={true} />
        <CardText>{getCardText()}</CardText>
        {generatePdfKey ? (
          <div style={{ display: "flex" }}>
            {businessService == "cnd-service" ? (
              <div className="primary-label-btn d-grid" style={{ marginLeft: "unset", marginRight: "20px", marginTop:"15px",marginBottom:"15px" }} onClick={printReciept}>
                <svg xmlns="http://www.w3.org/2000/svg" height="24px" viewBox="0 0 24 24" width="24px" fill="#a82227">
                  <path d="M0 0h24v24H0V0z" fill="none" />
                  <path d="M19 9h-4V3H9v6H5l7 7 7-7zm-8 2V5h2v6h1.17L12 13.17 9.83 11H11zm-6 7h14v2H5z" />
                </svg>
                {t("CND_FEE_RECIEPT")}
              </div>
            ) : null}
          </div>
        ) : null}
      </Card>
      {(
        <ActionBar style={{ display: "flex", justifyContent: "flex-end", alignItems: "baseline" }}>
          <Link to="/cnd-ui/employee">
            <SubmitBar label={t("CORE_COMMON_GO_TO_HOME")} />
          </Link>
        </ActionBar>
      )}
    </React.Fragment>
  );
};

export const FailedPayment = (props) => {
  props.setLink("Response");
  const { addParams, clearParams } = props;
  const { t } = useTranslation();
  const { consumerCode } = useParams();

  const getMessage = () => t("ES_PAYMENT_COLLECTED_ERROR");
  return (
    <React.Fragment>
      <Card>
        <Banner message={getMessage()} complaintNumber={consumerCode} successful={false} />
        <CardText>{t("ES_PAYMENT_FAILED_DETAILS")}</CardText>
      </Card>
      <ActionBar style={{ display: "flex", justifyContent: "flex-end", alignItems: "baseline" }}>
        <Link to="/cnd-ui/employee">
          <SubmitBar label={t("CORE_COMMON_GO_TO_HOME")} />
        </Link>
      </ActionBar>
    </React.Fragment>
  );
};
