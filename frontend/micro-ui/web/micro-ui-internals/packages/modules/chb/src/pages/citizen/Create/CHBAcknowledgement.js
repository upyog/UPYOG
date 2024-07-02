import { Banner, Card, CardText, LinkButton, LinkLabel, Loader, Row, StatusTable, SubmitBar } from "@upyog/digit-ui-react-components";
import React, { useEffect } from "react";
import { useTranslation } from "react-i18next";
import { Link, useRouteMatch } from "react-router-dom";
import getChbAcknowledgementData from "../../../getChbAcknowledgementData";
import { CHBDataConvert } from "../../../utils";

const GetActionMessage = (props) => {
  const { t } = useTranslation();
  console.log("props--->",props);
  if (props.isSuccess) {
    return !window.location.href.includes("edit-application") ? t("ES_CHB_RESPONSE_CREATE_ACTION") : t("CS_CHB_UPDATE_APPLICATION_SUCCESS");
  } else if (props.isLoading) {
    return !window.location.href.includes("edit-application") ? t("CS_CHB_APPLICATION_PENDING") : t("CS_CHB_UPDATE_APPLICATION_PENDING");
  } else if (!props.isSuccess) {
    return !window.location.href.includes("edit-application") ? t("CS_CHB_BOOKING_FAILED") : t("CS_CHB_UPDATE_BOOKING_FAILED");
  }
};

const rowContainerStyle = {
  padding: "4px 0px",
  justifyContent: "space-between",
};

const BannerPicker = (props) => {
  return (  
    <Banner
      message={GetActionMessage(props)}
      applicationNumber={props.data?.hallsBookingApplication[0].bookingNo}
      info={props.isSuccess ? props.t("CHB_BOOKING_NO") : ""}
      successful={props.isSuccess}
      style={{width: "100%"}}
    />
  );
};

const CHBAcknowledgement = ({ data, onSuccess }) => {

  
  const { t } = useTranslation();
  
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const mutation = Digit.Hooks.chb.useChbCreateAPI("pg.citya"); 
  const { data: storeData } = Digit.Hooks.useStore.getInitData();
  const match = useRouteMatch();
  const { tenants } = storeData || {};


  useEffect(() => {
    try {
      
      data.tenantId = "pg.citya";
      let formdata = CHBDataConvert(data)
      console.log("formdata-->",formdata);
      
      mutation.mutate(formdata, {
        onSuccess,
      });
    } catch (err) {
    }
  }, []);


  
  const handleDownloadPdf = async () => {
    const { hallsBookingApplication = [] } = mutation.data;
    console.log("mutation.data", mutation);
    let Chb = (hallsBookingApplication && hallsBookingApplication[0]) || {};
    const tenantInfo = tenants.find((tenant) => tenant.code === Chb.tenantId);
    let tenantId = Chb.tenantId || tenantId;
   
    const data = await getChbAcknowledgementData({ ...Chb }, tenantInfo, t);
    Digit.Utils.pdf.generate(data);
  };

  return mutation.isLoading || mutation.isIdle ? (
    <Loader />
  ) : (
    <Card>
      <BannerPicker t={t} data={mutation.data} isSuccess={mutation.isSuccess} isLoading={mutation.isIdle || mutation.isLoading} />
      <StatusTable>
        {mutation.isSuccess && (
          <Row
            rowContainerStyle={rowContainerStyle}
            last       
            textStyle={{ whiteSpace: "pre", width: "60%" }}
          />
        )}
      </StatusTable>
      {mutation.isSuccess && <SubmitBar label={t("CHB_DOWNLOAD_ACK_FORM")} onSubmit={handleDownloadPdf} />}
      <Link to={`/digit-ui/citizen`}>
        <LinkButton label={t("CORE_COMMON_GO_TO_HOME")} />
      </Link>

      <br></br>

      <Link to={`/digit-ui/citizen/payment/my-bills/` + `${"chb-services"}/` + `${mutation.data?.hallsBookingApplication[0].bookingNo}`}>
        <LinkButton label={t("CHB_PAYMENT_BUTTON")} />
      </Link>
    </Card>
  );
};

export default CHBAcknowledgement;