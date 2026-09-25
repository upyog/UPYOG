import React, { useEffect, useState } from "react";
import { Card, Banner, CardText, SubmitBar, LinkButton } from "@nudmcdgnpm/digit-ui-react-components";
import { Link, useLocation } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { Loader } from "@nudmcdgnpm/digit-ui-react-components";
import getPDFData from "../../../getPDFData";

const GetActionMessage = () => {
  const { t } = useTranslation();
  return t("CS_FILE_DESLUDGING_APPLICATION_SUCCESS");
};

const BannerPicker = (props) => {
  return (
    <Banner
      message={GetActionMessage()}
      applicationNumber={props?.data?.fsm && props?.data?.fsm[0]?.applicationNo}
      info={props.t("CS_FILE_DESLUDGING_APPLICATION_NO")}
      successful={props.isSuccess}
    />
  );
};

const Response = () => {
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const { data: storeData } = Digit.Hooks.useStore.getInitData();
  const { tenants } = storeData || {};
  const { state } = useLocation();
  const [paymentPreference, setPaymentPreference] = useState(null);
  const [advancePay, setAdvancePay] = useState(null);
  const [zeroPay, setZeroPay] = useState(null);

  // Initial loading state
  if (!state) {
    return <Loader />;
  }

  const Data = state?.data;
  const localityCode = Data?.fsm?.[0].address?.locality?.code;
  const slumCode = Data?.fsm?.[0].address?.slumName;
  const slum = Digit.Hooks.fsm.useSlum(Data?.fsm?.[0].address?.tenantId, slumCode, localityCode, {
    enabled: slumCode ? true : false,
    retry: slumCode ? true : false,
  });

  useEffect(() => {
    const amount = Digit.SessionStorage.get("total_amount");
    const amountPerTrip = Digit.SessionStorage.get("amount_per_trip");
    setPaymentPreference(state?.formData?.selectPaymentPreference?.code);
    const advanceAmount = amount === 0 ? null : state?.formData?.selectPaymentPreference?.advanceAmount;
    amount === 0 ? setZeroPay(true) : setZeroPay(false);
    advanceAmount === 0 ? setAdvancePay(true) : setAdvancePay(false);
  }, [state]);

  const handleDownloadPdf = () => {
    const { fsm } = Data;
    const [applicationDetails, ...rest] = fsm;
    const tenantInfo = tenants.find((tenant) => tenant.code === applicationDetails.tenantId);

    const data = getPDFData({ ...applicationDetails, slum }, tenantInfo, t);
    Digit.Utils.pdf.generate(data);
  };
  const isSuccess = state?.isSuccess;

  return !state ? (
    <Loader />
  ) : (
    <Card>
      <BannerPicker t={t} data={Data} isSuccess={isSuccess} />
      <CardText>
        {t(
          (paymentPreference && paymentPreference == "POST_PAY") || advancePay
            ? "CS_FILE_PROPERTY_RESPONSE_POST_PAY"
            : zeroPay
            ? "CS_FSM_RESPONSE_CREATE_DISPLAY_ZERO_PAY"
            : "CS_FILE_PROPERTY_RESPONSE"
        )}
      </CardText>
      {isSuccess && (
        <LinkButton
          label={
            <div className="response-download-button">
              <span>
                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="#a82227">
                  <path d="M19 9h-4V3H9v6H5l7 7 7-7zM5 18v2h14v-2H5z" />
                </svg>
              </span>
              <span className="download-button">{t("CS_COMMON_DOWNLOAD")}</span>
            </div>
          }
          onClick={handleDownloadPdf}
          className="w-full"
        />
      )}
      <Link to={`/upyog-ui/citizen`}>
        <SubmitBar label={t("CORE_COMMON_GO_TO_HOME")} />
      </Link>
    </Card>
  );
};

export default Response;
