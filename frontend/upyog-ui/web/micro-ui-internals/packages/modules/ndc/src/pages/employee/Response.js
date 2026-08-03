import { Banner, Card, CardText, ActionBar, SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { stringReplaceAll } from "../../utils";

// This component is the response page for the NDC application. It displays the status of the application and provides options to go back to home, go to NDC home, or make payment if the application is approved. The content of the banner and the available actions are based on the application status.
const Response = (props) => {
  const state= props?.location?.state || {};
  const { t } = useTranslation();
  const navigate = Digit.Hooks.useCustomNavigate();
  const nocData = state?.data?.Noc?.[0];
  const tenantId = window.localStorage.getItem("Employee.tenant-id");

  const pathname = window.location.pathname || "";
  const ndcCode = pathname.split("/").pop();

  const onSubmit = () => {
    navigate(`/upyog-ui/employee`);
  };

  const onGoToNDC = () => {
    navigate(`/upyog-ui/employee/ndc/inbox`);
  };

  const handlePayment = () => {
    navigate(
      `/upyog-ui/employee/payment/collect/NDC/${ndcCode}/${tenantId}?tenantId=${tenantId}`
    );
  };


  return (
    <div>
      <Card>
        <Banner
          // message={t(`NDC_${stringReplaceAll(nocData?.nocType, ".", "_")}_${stringReplaceAll(nocData?.applicationStatus, ".", "_")}_HEADER`)}
          message={"NDC Application Submitted Successfully"}
          applicationNumber={ndcCode}
          info={nocData?.applicationStatus == "REJECTED" ? "" : t(`NDC_APPROVAL_NUMBER`)}
          successful={nocData?.applicationStatus == "REJECTED" ? false : true}
          style={{ padding: "10px" }}
          headerStyles={{ fontSize: "32px", wordBreak: "break-word" }}
        />
        {/* {nocData?.applicationStatus !== "REJECTED" ? (
          <CardText>
            {t(`NDC_${stringReplaceAll(nocData?.nocType, ".", "_")}_${stringReplaceAll(nocData?.applicationStatus, ".", "_")}_SUB_HEADER`)}
          </CardText>
        ) : null} */}
        <ActionBar classname="challan-emp-acknowledgement" >
          <SubmitBar label={t("CORE_COMMON_GO_TO_HOME")} onSubmit={onSubmit} />
          <SubmitBar label={t("CORE_COMMON_GO_TO_NDC")} onSubmit={onGoToNDC} />
          <SubmitBar label={t("CS_APPLICATION_DETAILS_MAKE_PAYMENT")} onSubmit={handlePayment} />
        </ActionBar>
      </Card>
    </div>
  );
};
export default Response;
