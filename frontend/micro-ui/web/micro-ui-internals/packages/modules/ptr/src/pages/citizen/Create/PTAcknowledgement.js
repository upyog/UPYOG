import { Banner, Card, CardText, LinkButton, LinkLabel, Loader, Row, StatusTable, SubmitBar } from "@egovernments/digit-ui-react-components";
import React, { useEffect } from "react";
import { useTranslation } from "react-i18next";
import { Link, useRouteMatch } from "react-router-dom";
import getPTAcknowledgementData from "../../../getPTAcknowledgementData";
import { convertToProperty } from "../../../utils";

const GetActionMessage = (props) => {
  const { t } = useTranslation();
  if (props.isSuccess) {
    return !window.location.href.includes("edit-application") ? t("CS_PROPERTY_APPLICATION_SUCCESS") : t("CS_PROPERTY_UPDATE_APPLICATION_SUCCESS");
  } else if (props.isLoading) {
    return !window.location.href.includes("edit-application") ? t("CS_PROPERTY_APPLICATION_PENDING") : t("CS_PROPERTY_UPDATE_APPLICATION_PENDING");
  } else if (!props.isSuccess) {
    return !window.location.href.includes("edit-application") ? t("CS_PROPERTY_APPLICATION_FAILED") : t("CS_PROPERTY_UPDATE_APPLICATION_FAILED");
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
      info={props.isSuccess ? props.t("PTR_APPLICATION_NO") : ""}
      successful={props.isSuccess}
      style={{width: "100%"}}
    />
  );
};

const PTAcknowledgement = ({ data, onSuccess }) => {

  
  const { t } = useTranslation();
  
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const mutation = Digit.Hooks.ptr.usePTRCreateAPI(data.address?.city?.code); 
  const { data: storeData } = Digit.Hooks.useStore.getInitData();
  const match = useRouteMatch();
  const { tenants } = storeData || {};


  useEffect(() => {
    try {
      
      data.tenantId = data.address?.city?.code;
      let formdata = convertToProperty(data)
      

      
      mutation.mutate(formdata, {
        onSuccess,
      });
    } catch (err) {
    }
  }, []);

  

  const handleDownloadPdf = async () => {
    const { PetRegistrationApplications = [] } = mutation.data;
    let Pet = (PetRegistrationApplications && PetRegistrationApplications[0]) || {};
    const tenantInfo = tenants.find((tenant) => tenant.code === Pet.tenantId);
    let tenantId = Pet.tenantId || tenantId;
   
    const data = await getPTAcknowledgementData({ ...Pet }, tenantInfo, t);
    Digit.Utils.pdf.generate(data);
  };

  return mutation.isLoading || mutation.isIdle ? (
    <Loader />
  ) : (
    <Card>
      <BannerPicker t={t} data={mutation.data} isSuccess={mutation.isSuccess} isLoading={mutation.isIdle || mutation.isLoading} />
      {mutation.isSuccess && <CardText>{t("CS_FILE_PROPERTY_RESPONSE")}</CardText>}
      {!mutation.isSuccess && <CardText>{t("CS_FILE_PROPERTY_FAILED_RESPONSE")}</CardText>}
      <StatusTable>
        {mutation.isSuccess && (
          <Row
            rowContainerStyle={rowContainerStyle}
            last
            label={t("PTR_APPLIACATION_NUMBER")}
            text={mutation?.data?.PetRegistrationApplications[0]?.applicationNumber}
            textStyle={{ whiteSpace: "pre", width: "60%" }}
          />
        )}
      </StatusTable>
      {mutation.isSuccess && <SubmitBar label={t("PTR_DOWNLOAD_ACK_FORM")} onSubmit={handleDownloadPdf} />}
      <Link to={`/digit-ui/citizen`}>
        <LinkButton label={t("CORE_COMMON_GO_TO_HOME")} />
      </Link>
    </Card>
  );
};

export default PTAcknowledgement;