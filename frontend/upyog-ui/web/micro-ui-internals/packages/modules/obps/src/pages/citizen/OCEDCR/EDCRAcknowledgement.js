import { Banner, Card, CardText, LinkButton, SubmitBar, Toast } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useState, useEffect }  from "react";
import { Link,  } from "react-router-dom";
import { useTranslation } from "react-i18next";

const EDCRAcknowledgement = (props) => {
  const { t } = useTranslation();
  const navigate = Digit.Hooks.useCustomNavigate();
  const [showToast, setShowToast] = useState(false);

  useEffect(() => { 
    if (props?.data?.type == "ERROR" && !showToast) setShowToast(true); 
  }, [props?.data?.data]);

  if (props?.data?.type == "ERROR") {
    return (
      <Card className="obps-edcracknowledgement-card">
        <Banner
          message={t("CS_BPA_APPLICATION_FAILED")}
          applicationNumber={""}
          info={""}
          successful={false}
          infoStyles={{ fontSize: "18px", lineHeight: "21px", fontWeight: "bold", textAlign: "center", padding: "0px 15px" }}
          applicationNumberStyles={{ fontSize: "24px", lineHeight: "28px", fontWeight: "bold", marginTop: "10px" }}
          className="obps-edcracknowledgement-fullwidth"
        />
        <div className="obps-edcracknowledgement-container-padding">
          <Link to={`/upyog-ui/citizen`} >
            <SubmitBar label={t("CORE_COMMON_GO_TO_HOME")} />
          </Link>
        </div>
        {showToast ? <Toast error={"error"} label={t(props?.data?.data)} onClose={() => setShowToast(null)} isDleteBtn={true}/> : null}
      </Card>
    )
  }
  
  sessionStorage.setItem("isEDCRDisable", JSON.stringify(true));
  sessionStorage.setItem("isPermitApplication", true);
  const edcrData = props?.data?.[0];
  const [bpaLinks, setBpaLinks] = useState({});
  const state = Digit.ULBService.getStateId();
  const { data: homePageUrlLinks, isLoading: homePageUrlLinksLoading } = Digit.Hooks.obps.useMDMS(state, "BPA", ["homePageUrlLinks"]);
  const { isMdmsLoading, data: mdmsData } = Digit.Hooks.obps.useMDMS(state, "BPA", ["RiskTypeComputation"]);

  useEffect(() => {
    if (!homePageUrlLinksLoading && homePageUrlLinks?.BPA?.homePageUrlLinks?.length > 0) {
      homePageUrlLinks?.BPA?.homePageUrlLinks?.map(linkData => {
        if (linkData?.applicationType === edcrData?.appliactionType && linkData?.serviceType === edcrData?.applicationSubType) {
          setBpaLinks({
            linkData: linkData,
            edcrNumber: edcrData?.edcrNumber
          });
        }
      });
    }
  }, [!homePageUrlLinksLoading]);

  const printReciept = async () => {
    var win = window.open(edcrData.planReport, '_blank');
    if (win) {
      win.focus();
    }
  };

  return (
    <div>
      {edcrData?.status == "Accepted" ?
        <Card className="obps-edcracknowledgement-card">
          <Banner
            message={t("BPA_OC_EDCR_ACKNOWLEDGEMENT_SUCCESS_MESSAGE_LABEL")}
            applicationNumber={edcrData?.edcrNumber}
            info={t("EDCR_SCRUTINY_NUMBER_LABEL")}
            successful={true}
            infoStyles={{ fontSize: "18px", lineHeight: "21px", fontWeight: "bold", textAlign: "center", padding: "0px 15px" }}
            applicationNumberStyles={{ fontSize: "24px", lineHeight: "28px", fontWeight: "bold", marginTop: "10px" }}
            className="obps-edcracknowledgement-fullwidth"
          />
          <CardText className="obps-edcracknowledgement-card-2">{`${t("PDF_STATIC_LABEL_CONSOLIDATED_BILL_CONSUMER_ID_TL")} - ${edcrData?.applicationNumber}`}</CardText>
          {/* <CardText className="custom-style">{t("EDCR_ACKNOWLEDGEMENT_SUCCESS_MESSAGE_TEXT_LABEL")}</CardText> */}
          <div className="primary-label-btn d-grid obps-edcracknowledgement-bottom-spacing" onClick={printReciept}>
            <svg width="20" height="23" viewBox="0 0 20 23" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M19.3334 8H14V0H6.00002V8H0.666687L10 17.3333L19.3334 8ZM0.666687 20V22.6667H19.3334V20H0.666687Z" fill="#a82227" />
            </svg>
            {t("EDCR_DOWNLOAD_SCRUTINY_REPORT_LABEL")}
          </div>
          <div className="obps-edcracknowledgement-container-padding-2">
          <Link to={{pathname: `/upyog-ui/citizen/obps/${bpaLinks?.linkData?.flow?.toLowerCase()}/${edcrData?.appliactionType?.toLowerCase()}/${edcrData?.applicationSubType?.toLowerCase()}/docs-required`, state: bpaLinks}} replace >
              <SubmitBar label={t("BPA_APPLY_OC_FOR_BPA_LABEL")} className={"oc-aknowledgement-screen"} onSubmit={() => (sessionStorage.setItem("clickOnBPAApplyAfterEDCR",true))}/>
            </Link>
          </div>
          <div className="obps-edcracknowledgement-top-spacing-2">
            <Link to={`/upyog-ui/citizen`} >
              <LinkButton label={t("CORE_COMMON_GO_TO_HOME")} />
            </Link>
          </div>
        </Card> :
        <Card className="obps-edcracknowledgement-card">
          <Banner
            message={t("BPA_OC_EDCR_ACKNOWLEDGEMENT_REJECTED_MESSAGE_LABEL")}
            applicationNumber={""}
            info={""}
            successful={false}
            infoStyles={{ fontSize: "18px", lineHeight: "21px", fontWeight: "bold", textAlign: "center", padding: "0px 15px" }}
            applicationNumberStyles={{ fontSize: "24px", lineHeight: "28px", fontWeight: "bold", marginTop: "10px" }}
            className="obps-edcracknowledgement-fullwidth"
          />
          <CardText className="obps-edcracknowledgement-card-2">{t("EDCR_ACKNOWLEDGEMENT_REJECTED_MESSAGE_TEXT_LABEL")}</CardText>
          <CardText className="obps-edcracknowledgement-card-2">{`${t("PDF_STATIC_LABEL_CONSOLIDATED_BILL_CONSUMER_ID_TL")} - ${edcrData?.applicationNumber}`}</CardText>
          <div className="primary-label-btn d-grid obps-edcracknowledgement-bottom-spacing" onClick={printReciept}>
            <svg width="20" height="23" viewBox="0 0 20 23" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M19.3334 8H14V0H6.00002V8H0.666687L10 17.3333L19.3334 8ZM0.666687 20V22.6667H19.3334V20H0.666687Z" fill="#a82227" />
            </svg>
            {t("EDCR_DOWNLOAD_SCRUTINY_REPORT_LABEL")}
          </div>
          <div className="obps-edcracknowledgement-container-padding-3">
            <Link to={`/upyog-ui/citizen`} >
              <SubmitBar label={t("CORE_COMMON_GO_TO_HOME")} />
            </Link>
          </div>
        </Card>
      }
    </div>
  );
};
export default EDCRAcknowledgement;