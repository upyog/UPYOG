import React,{ useState }  from "react";
import { Card, Banner, CardText, SubmitBar } from "@upyog/digit-ui-react-components";
import { Link, useRouteMatch } from "react-router-dom";
import { useSelector } from "react-redux";
import { PgrRoutes, getRoute } from "../../constants/Routes";
import { useTranslation } from "react-i18next";
import getPGRcknowledgementData from "../../utils/getPGRcknowledgementData"
const GetActionMessage = ({ action }) => {
  const { t } = useTranslation();
  if (action === "REOPEN") {
    return t(`CS_COMMON_COMPLAINT_REOPENED`);
  } else {
    return t(`CS_COMMON_COMPLAINT_SUBMITTED`);
  }
};

const BannerPicker = ({ response }) => {
  const { complaints } = response;

  if (complaints && complaints.response && complaints.response.responseInfo) {
    sessionStorage.removeItem("type" );
    sessionStorage.removeItem("pincode");
    sessionStorage.removeItem("tenantId");
    sessionStorage.removeItem("localityCode");
    sessionStorage.removeItem("landmark"); 
    sessionStorage.removeItem("propertyid")
    return (
      <Banner
        message={GetActionMessage(complaints.response.ServiceWrappers[0].workflow)}
        complaintNumber={complaints.response.ServiceWrappers[0].service.serviceRequestId}
        successful={true}
      />
    );
  } else {
    return <Banner message={t("CS_COMMON_COMPLAINT_NOT_SUBMITTED")} successful={false} />;
  }
};

const Response = (props) => {
  const { t } = useTranslation();
  const { match } = useRouteMatch();
  const appState = useSelector((state) => state)["pgr"];
  const { data: storeData } = Digit.Hooks.useStore.getInitData();
  const { tenants } = storeData || {};
  const [enable, setEnable] = useState(false)
  let id= appState?.complaints?.response?.ServiceWrappers?.[0]?.service?.serviceRequestId
  const { isLoading, error, isError, complaintDetails, revalidate } = Digit.Hooks.pgr.useComplaintDetails({ tenantId:"pg.citya", id },{ enabled: enable ? true : false});
  
  const handleDownloadPdf = async (e) => {
    const tenantInfo = tenants.find((tenant) => tenant.code === "pg.citya");
    e.preventDefault()
    setEnable(true)
    const data = await getPGRcknowledgementData({ ...complaintDetails }, tenantInfo, t);
    Digit.Utils.pdf.generate(data);
  };
  return (
    <Card>
      {appState.complaints.response && <BannerPicker response={appState} />}
      <CardText>{t("ES_COMMON_TRACK_COMPLAINT_TEXT")}</CardText>
      <Link to="/digit-ui/employee">
        <SubmitBar label={t("CORE_COMMON_GO_TO_HOME")} />
      </Link>
      {appState.complaints.response && <SubmitBar label={t("PT_DOWNLOAD_ACK_FORM")} onSubmit={(e) =>{handleDownloadPdf(e)}} style={{marginLeft:"10px"}}/>}
    </Card>
  );
};

export default Response;
