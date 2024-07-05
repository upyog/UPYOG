import { Banner, Card, CardText, LinkButton, LinkLabel, Loader, Row, StatusTable, SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useEffect } from "react";
import { useTranslation } from "react-i18next";
import { Link, useRouteMatch } from "react-router-dom";
import getAssetAcknowledgementData from "../../../../getAssetAcknowledgementData";

import { Assetdata } from "../../../../utils";

const GetActionMessage = (props) => {
  const { t } = useTranslation();
  if (props.isSuccess) {
    return !window.location.href.includes("edit-application") ? t("ES_ASSET_RESPONSE_CREATE_ACTION") : t("CS_AST_UPDATE_APPLICATION_SUCCESS");
  } else if (props.isLoading) {
    return !window.location.href.includes("edit-application") ? t("CS_AST_APPLICATION_PENDING") : t("CS_AST_UPDATE_APPLICATION_PENDING");
  } else if (!props.isSuccess) {
    return !window.location.href.includes("edit-application") ? t("CS_AST_APPLICATION_FAILED") : t("CS_AST_UPDATE_APPLICATION_FAILED");
  }
};

const rowContainerStyle = {
  padding: "4px 0px",
  justifyContent: "space-between",
};

const BannerPicker = (props) => {
  console.log("props",props)
  return (
    <Banner
      message={GetActionMessage(props)}
      applicationNumber={props.data?.Assets?.[0].applicationNo}
      info={props.isSuccess ? props.t("ES_ASSET_RESPONSE_CREATE_LABEL") : ""}
      successful={props.isSuccess}
      style={{width: "100%"}}
    />
  );
};

const NewResponse = ({ data, onSuccess }) => {

  console.log("datatatata",data);

  
  const { t } = useTranslation();
  
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const mutation = Digit.Hooks.asset.useAssetCreateAPI(data?.address?.city?.code); 
  const { data: storeData } = Digit.Hooks.useStore.getInitData();
  const match = useRouteMatch();
  const { tenants } = storeData || {};


  useEffect(() => {
    try {
      
      data.tenantId = data.address?.city?.code;
      let formdata = Assetdata(data)
      

      
      mutation.mutate(formdata, {
        onSuccess,
      });
    } catch (err) {
    }
  }, []);

  

  const handleDownloadPdf = async () => {
    const { Asset = [] } = mutation.data;
    let AST = (Asset && Asset[0]) || {};
    const tenantInfo = tenants.find((tenant) => tenant.code === AST.tenantId);
    let tenantId = AST.tenantId || tenantId;
   
    const data = await getAssetAcknowledgementData({ ...AST }, tenantInfo, t);
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
      {/* {mutation.isSuccess && <SubmitBar label={t("AST_REPORT")} onSubmit={handleDownloadPdf} />} */}
      <Link to={`/digit-ui/employee`}>
        <LinkButton label={t("CORE_COMMON_GO_TO_HOME")} />
      </Link>
    </Card>
  );
};

export default NewResponse;