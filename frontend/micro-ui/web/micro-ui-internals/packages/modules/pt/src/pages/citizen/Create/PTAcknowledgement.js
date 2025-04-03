import { Banner, Card, CardText, LinkButton, LinkLabel, Loader, Row, StatusTable, SubmitBar } from "@upyog/digit-ui-react-components";
import React, { useEffect } from "react";
import { useTranslation } from "react-i18next";
import { Link,useHistory, useRouteMatch } from "react-router-dom";
import getPTAcknowledgementData from "../../../getPTAcknowledgementData";
import { convertToProperty, convertToUpdateProperty } from "../../../utils";

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
      applicationNumber={props.data?.Properties[0].acknowldgementNumber}
      info={props.isSuccess ? props.t("PT_APPLICATION_NO") : ""}
      successful={props.isSuccess}
      style={{width: "100%"}}
    />
  );
};

let isBifurcation = false;
let bifurcationDetails=null;

const PTAcknowledgement = ({ data, onSuccess }) => {
  // console.log("PTAcknowledgement===data==",data)
  const { t } = useTranslation();
  const isPropertyMutation = window.location.href.includes("property-mutation");
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const mutation = Digit.Hooks.pt.usePropertyAPI(
    data?.address?.city ? data.address?.city?.code : tenantId,
    !window.location.href.includes("edit-application") && !isPropertyMutation
  );
  const { data: storeData } = Digit.Hooks.useStore.getInitData();
  const match = useRouteMatch();
  const { tenants } = storeData || {};
  const history = useHistory();
  if(data && data?.bifurcationDetails?.action=="BIFURCATION") {
    bifurcationDetails = data?.bifurcationDetails;
    isBifurcation = true;
    
  }


  useEffect(() => {
    try {
      let tenantId = isPropertyMutation ? data.Property?.address.tenantId : data?.address?.city ? data.address?.city?.code : tenantId;
      data.tenantId = tenantId;
      let formdata = !window.location.href.includes("edit-application") ? isPropertyMutation ? data : convertToProperty(data) : convertToUpdateProperty(data,t);
      formdata.Property.tenantId = formdata?.Property?.tenantId || tenantId;
      console.log("isPropertyMutation==",isPropertyMutation,data)
      console.log("formdata==========",formdata);
      return;
      mutation.mutate(formdata, {
        onSuccess,
      });
    } catch (err) {
    }
  }, []);

  const handleDownloadPdf = async () => {
    const { Properties = [] } = mutation.data;
    let Property = (Properties && Properties[0]) || {};
    const tenantInfo = tenants.find((tenant) => tenant.code === Property.tenantId);
    let tenantId = Property.tenantId || tenantId;
    const propertyDetails = await Digit.PTService.search({ tenantId, filters: { propertyIds: Property?.propertyId, status: "INACTIVE" } });
    Property.transferorDetails = propertyDetails?.Properties?.[0] || [];
    Property.isTransferor = true;
    Property.transferorOwnershipCategory = propertyDetails?.Properties?.[0]?.ownershipCategory
    const data = await getPTAcknowledgementData({ ...Property }, tenantInfo, t);
    Digit.Utils.pdf.generate(data);
  };
  const addMoreProperty = ()=> {
    history.push({pathname: "/digit-ui/citizen/pt/property/new-application", state: bifurcationDetails})

  }

  return mutation.isLoading || mutation.isIdle ? (
    <Loader />
  ) : (
    <Card>
      <BannerPicker t={t} data={mutation.data} isSuccess={mutation.isSuccess} isLoading={mutation.isIdle || mutation.isLoading} />
      {mutation.isSuccess && <CardText>{t("CS_FILE_PROPERTY_RESPONSE")}</CardText>}
      {!mutation.isSuccess && <CardText>{t("CS_FILE_PROPERTY_FAILED_RESPONSE")}</CardText>}
      {/* {mutation.isSuccess && (
        <LinkButton
          label={
            <div className="response-download-button">
              <span>
                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="#0f4f9e">
                  <path d="M19 9h-4V3H9v6H5l7 7 7-7zM5 18v2h14v-2H5z" />
                </svg>
              </span>
              <span className="download-button">{t("CS_COMMON_DOWNLOAD")}</span> 
            </div>
          }
          onClick={handleDownloadPdf}
          className="w-full"
        />)}*/}
      <StatusTable>
        {mutation.isSuccess && (
          <Row
            rowContainerStyle={rowContainerStyle}
            last
            label={t("PT_COMMON_TABLE_COL_PT_ID")}
            text={mutation?.data?.Properties[0]?.propertyId}
            textStyle={{ whiteSpace: "pre", width: "60%" }}
          />
        )}
      </StatusTable>
      {/* {mutation.isSuccess && <Link to={`/digit-ui/citizen/feedback?redirectedFrom=${match.path}&propertyId=${mutation.isSuccess ? mutation?.data?.Properties[0]?.propertyId : ""}&acknowldgementNumber=${mutation.isSuccess ? mutation?.data?.Properties[0]?.acknowldgementNumber : ""}&creationReason=${mutation.isSuccess ? mutation?.data?.Properties[0]?.creationReason : ""}&tenantId=${mutation.isSuccess ? mutation?.data?.Properties[0]?.tenantId : ""}&locality=${mutation.isSuccess ? mutation?.data?.Properties[0]?.address?.locality?.code : ""}`}>
          <SubmitBar label={t("CS_REVIEW_AND_FEEDBACK")}/>
      </Link>} */}
      {mutation.isSuccess && <SubmitBar label={t("PT_DOWNLOAD_ACK_FORM")} onSubmit={handleDownloadPdf} />}
      {
        isBifurcation && mutation?.data?.Properties[0]?.bifurcationCount<2 && <a href="javascript:void(0)" onClick={addMoreProperty}>
          <LinkButton label={t("Add One More Property To Complete Separation of Ownership")} />
        </a>
      }
      {
        (!isBifurcation || mutation?.data?.Properties[0]?.bifurcationCount>1) && <Link to={`/digit-ui/citizen`}>
          <LinkButton label={t("CORE_COMMON_GO_TO_HOME")} />
        </Link>
      }
      
    </Card>
  );
};

export default PTAcknowledgement;
