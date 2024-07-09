import React ,{useState}from "react";
import { Card, Banner, CardText, SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";
import { Link } from "react-router-dom";
import { useSelector } from "react-redux";
import { PgrRoutes, getRoute } from "../../constants/Routes";
import { useTranslation } from "react-i18next";

const GetActionMessage = ({ action }) => {
  const { t } = useTranslation();
  switch (action) {
    case "REOPEN":
      return t(`CS_COMMON_COMPLAINT_REOPENED`);
    case "RATE":
      return t("CS_COMMON_THANK_YOU");
    default:
      return t(`CS_COMMON_COMPLAINT_SUBMITTED`);
  }
};

const BannerPicker = ({ response }) => {
  const { complaints } = response;
  const { t } = useTranslation();
  if (complaints && complaints.response && complaints.response.responseInfo) {
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

const TextPicker = ({ response }) => {
  const { complaints } = response;
  const { t } = useTranslation();
  if (complaints && complaints.response && complaints.response.responseInfo) {
    const { action } = complaints.response.ServiceWrappers[0].workflow;
    return action === "RATE" ? <CardText>{t("CS_COMMON_RATING_SUBMIT_TEXT")}</CardText> : <CardText>{t("CS_COMMON_TRACK_COMPLAINT_TEXT")}</CardText>;
  }
};

const Response = (props) => {
  const { t } = useTranslation();
  const appState = useSelector((state) => state)["pgr"];
  const { data: storeData } = Digit.Hooks.useStore.getInitData();
  const { tenants } = storeData || {};
  const [enable, setEnable] = useState(false)
  let id= appState?.complaints?.response?.ServiceWrappers?.[0]?.service?.serviceRequestId
  const { isLoading, error, isError, complaintDetails, revalidate } = Digit.Hooks.pgr.useComplaintDetails({ tenantId:"pg.citya", id },{ enabled: enable ? true : false});
console.log("appStateappState",appState)
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
      {appState.complaints.response && <TextPicker response={appState} />}
     
      {appState.complaints.response?.ServiceWrappers?.[0]?.workflow.action == "RATE" ?"": <div style={{marginBottom:"10px"}}><SubmitBar label={t("PT_DOWNLOAD_ACK_FORM")} onSubmit={(e) =>{handleDownloadPdf(e)}} /></div>}
      <Link to="/digit-ui/citizen">
        <SubmitBar label={t("CORE_COMMON_GO_TO_HOME")} />
      </Link>
    </Card>
  );
};

export default Response;
