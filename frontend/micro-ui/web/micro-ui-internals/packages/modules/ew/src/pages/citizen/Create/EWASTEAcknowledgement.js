import { Banner, Card, CardText, LinkButton, LinkLabel, Loader, Row, StatusTable, SubmitBar } from "@upyog/digit-ui-react-components";
import React, { useEffect } from "react";
import { useTranslation } from "react-i18next";
import { Link, useRouteMatch } from "react-router-dom";
import getEwAcknowledgementData from "../../../utils/getEwAcknowledgementData";
import { EWDataConvert } from "../../../utils";

const GetActionMessage = (props) => {
  const { t } = useTranslation();
  if (props.isSuccess) {
    return !window.location.href.includes("edit-application") ? t("ES_EWASTE_RESPONSE_CREATE_ACTION") : t("CS_EWASTE_UPDATE_APPLICATION_SUCCESS");
  } else if (props.isLoading) {
    return !window.location.href.includes("edit-application") ? t("CS_EWASTE_APPLICATION_PENDING") : t("CS_EWASTE_UPDATE_APPLICATION_PENDING");
  } else if (!props.isSuccess) {
    return !window.location.href.includes("edit-application") ? t("CS_EWASTE_APPLICATION_FAILED") : t("CS_EWASTE_UPDATE_APPLICATION_FAILED");
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
      applicationNumber={props.data?.PetRegistrationApplications[0].applicationNumber}
      info={props.isSuccess ? props.t("EWASTE_APPLICATION_NO") : ""}
      successful={props.isSuccess}
      style={{width: "100%"}}
    />
  );
};

const EWASTEAcknowledgement = ({ data, onSuccess }) => {

  const { t } = useTranslation();
  
  const tenantId = Digit.ULBService.getCurrentTenantId();
  // const mutation = Digit.Hooks.ptr.usePTRCreateAPI(data.address?.city?.code); 
  const mutation = Digit.Hooks.ew.useEWCreateAPI("pg.citya"); 
  console.log("this is data and tenantId :: ", data, tenantId)
  console.log("this is mutation data :: ", mutation)
  const { data: storeData } = Digit.Hooks.useStore.getInitData();
  const match = useRouteMatch();
  const { tenants } = storeData || {};


  useEffect(() => {
    try {
      
      data.tenantId = "pg.citya";
      let formdata = EWDataConvert(data)
      

      mutation.mutate(formdata, {
        onSuccess,
      });
    } catch (err) {
    }
  }, []);

  

  // const handleDownloadPdf = async () => {
  //   const { PetRegistrationApplications = [] } = mutation.data;
  //   let Pet = (PetRegistrationApplications && PetRegistrationApplications[0]) || {};
  //   const tenantInfo = tenants.find((tenant) => tenant.code === Pet.tenantId);
  //   let tenantId = Pet.tenantId || tenantId;
   
  //   const data = await getEwAcknowledgementData({ ...Pet }, tenantInfo, t);
  //   Digit.Utils.pdf.generate(data);
  // };

  return (
  // mutation.isLoading || mutation.isIdle ? (
  //   <Loader />
  // ) : 
  // (
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
      {/* {mutation.isSuccess && <SubmitBar label={t("EWASTE_DOWNLOAD_ACK_FORM")} onSubmit={handleDownloadPdf} />} */}
      <Link to={`/digit-ui/citizen`}>
        <LinkButton label={t("CORE_COMMON_GO_TO_HOME")} />
      </Link>
    </Card>
  );
};

export default EWASTEAcknowledgement;