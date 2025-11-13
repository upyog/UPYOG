import { Banner, Card, CardText, LinkButton, LinkLabel, Loader, Row, StatusTable, SubmitBar } from "@upyog/digit-ui-react-components";
import React, { useEffect } from "react";
import { useTranslation } from "react-i18next";
import { Link,useHistory, useRouteMatch } from "react-router-dom";
import getPTAcknowledgementData from "../../../getPTAcknowledgementData";
import { convertToProperty, convertToUpdateProperty } from "../../../utils";

const rowContainerStyle = {
    padding: "4px 0px",
    justifyContent: "space-between",
};

const GetActionMessage = (props) => {
    const { t } = useTranslation();
    if (props.isSuccess) {
      return t("CS_PROPERTY_APPLICATION_SUCCESS");
    } else if (!props.isSuccess) {
      return t("CS_PROPERTY_APPLICATION_FAILED");
    }
  };

const BannerPicker = (props) => {
    return (
      <Banner
        message={GetActionMessage(props)}
        applicationNumber={props.data?.Appeals[0].acknowldgementNumber}
        info={props.isSuccess ? props.t("PT_APPLICATION_NO") : ""}
        successful={props.isSuccess}
        style={{width: "100%"}}
      />
    );
  };

const AppealAcknowledgement = (props)=>{
    const { t } = useTranslation();
    const { data: storeData } = Digit.Hooks.useStore.getInitData();
    const { tenants } = storeData || {};
    const handleDownloadPdf = async () => {
        const { Appeals = [] } = props.data;
        let Appeal = (Appeals && Appeals[0]) || {};
        const tenantInfo = tenants.find((tenant) => tenant.code === Appeal.tenantId);
        let tenantId = Appeal.tenantId || tenantId;
        
        const data = await getPTAcknowledgementData({ ...Appeal }, tenantInfo, t);
        Digit.Utils.pdf.generate(data);
    };
    return (
        <Card>
        <BannerPicker t={t} data={props?.data} isSuccess={props.isSuccess} />
        {props.isSuccess && <CardText>{t("CS_FILE_PROPERTY_RESPONSE")}</CardText>}
        {!props.isSuccess && <CardText>{t("CS_FILE_PROPERTY_FAILED_RESPONSE")}</CardText>}
        <StatusTable>
            {props.isSuccess && (
            <Row
                rowContainerStyle={rowContainerStyle}
                last
                label={t("Appeal ID")}
                text={props?.data?.Appeals[0]?.appealId}
                textStyle={{ whiteSpace: "pre", width: "60%" }}
            />
            )}
        </StatusTable>
        {/* {props.isSuccess && <Link to={`/digit-ui/citizen/feedback?redirectedFrom=${match.path}&propertyId=${props.isSuccess ? props?.data?.Appeals[0]?.propertyId : ""}&acknowldgementNumber=${props.isSuccess ? props?.data?.Appeals[0]?.acknowldgementNumber : ""}&creationReason=${props.isSuccess ? props?.data?.Appeals[0]?.creationReason : ""}&tenantId=${props.isSuccess ? props?.data?.Appeals[0]?.tenantId : ""}&locality=${props.isSuccess ? props?.data?.Appeals[0]?.address?.locality?.code : ""}`}>
            <SubmitBar label={t("CS_REVIEW_AND_FEEDBACK")}/>
        </Link>} */}
        {/* {props.isSuccess && <SubmitBar label={t("PT_DOWNLOAD_ACK_FORM")} onSubmit={handleDownloadPdf} />} */}
        <Link to={`/digit-ui/citizen`}>
            <LinkButton label={t("CORE_COMMON_GO_TO_HOME")} />
        </Link>
        
        </Card>
    )
} 
export default AppealAcknowledgement;