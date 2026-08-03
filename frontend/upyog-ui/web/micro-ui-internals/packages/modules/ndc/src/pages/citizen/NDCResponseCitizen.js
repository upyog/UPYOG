import { Banner, Card, CardText, ActionBar, SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { stringReplaceAll } from "../../utils";
import { useLocation } from "react-router-dom";


//NDCResponseCitizen is a component that displays the response after a citizen submits an NDC application.
//  It shows a banner with the application status and provides options to go back to the home page, go to the NDC home page, or make a payment if the application is approved.
//  The content of the banner and the available actions are based on the application status.
const NDCResponseCitizen = (props) => {
  const location = useLocation();
  const state = location?.state;
  const { t } = useTranslation();
  const navigate = Digit.Hooks.useCustomNavigate();
  const nocData = state?.data?.Noc?.[0];
  const tenantId = Digit.ULBService.getCurrentTenantId();

  const ndcCode = location.pathname.split("/").pop(); 
  const onSubmit = () => {
    navigate(`/upyog-ui/citizen`);
  };

  const onGoToNDC = () => {
    navigate(`/upyog-ui/citizen/ndc-home`);
  };

  const handlePayment = () => {
    navigate(`/upyog-ui/citizen/payment/collect/NDC/${ndcCode}/${tenantId}?tenantId=${tenantId}`);
    // pathname: `/digit-ui/citizen/payment/collect/${application?.businessService}/${application?.applicationNumber}`,
  };


  return (
    <div>
      <Card>
        <Banner
          message={"NDC Application Submitted Successfully"}
          applicationNumber={ndcCode}
          info={nocData?.applicationStatus == "REJECTED" ? "" : t(`NDC_APPROVAL_NUMBER`)}
          successful={nocData?.applicationStatus == "REJECTED" ? false : true}
          className="ndc-banner-padding"
          headerClassName="ndc-banner-header"
        />
        {/* {nocData?.applicationStatus !== "REJECTED" ? (
          <CardText>
            {t(`NDC_${stringReplaceAll(nocData?.nocType, ".", "_")}_${stringReplaceAll(nocData?.applicationStatus, ".", "_")}_SUB_HEADER`)}
          </CardText>
        ) : null} */}
        <ActionBar className="ndc-action-bar" >
          <SubmitBar label={t("CORE_COMMON_GO_TO_HOME")} onSubmit={onSubmit} />
          <SubmitBar label={t("CORE_COMMON_GO_TO_NDC")} onSubmit={onGoToNDC} />
          <SubmitBar label={t("CS_APPLICATION_DETAILS_MAKE_PAYMENT")} onSubmit={handlePayment} />
        </ActionBar>
      </Card>
    </div>
  );
};
export default NDCResponseCitizen;
