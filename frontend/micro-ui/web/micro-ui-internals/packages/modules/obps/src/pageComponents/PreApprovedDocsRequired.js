import React, { Fragment, useEffect } from "react";
import { Card, CardHeader, CardLabel, CardSubHeader, CardText, CitizenInfoLabel, Loader, SubmitBar,NavBar,OpenLinkContainer, BackButton } from "@upyog/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import useMDMS from "../../../../libraries/src/hooks/obps/useMDMS";

const PreApprovedDocsRequired = ({ onSelect, onSkip, config }) => {
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateId = Digit.ULBService.getStateId();
  const history = useHistory();
  const { data, isLoading } = useMDMS(stateId, "PreApprovedDisclaimer", "Disclaimer");
  let isopenlink = window.location.href.includes("/openlink/");
  const isCitizenUrl = Digit.Utils.browser.isMobile()?true:false;

  useEffect(()=>{
    if(tenantId)
    Digit.LocalizationService.getLocale({modules: [`rainmaker-bpareg`], locale: Digit.StoreData.getCurrentLanguage(), tenantId: `${tenantId}`});
  },[tenantId])
  

  if (isLoading) {
    return (
      <Loader />
    )
  }

  return (
    <Fragment>
      <div className={isopenlink? "OpenlinkContainer":""}>
      <Card>
        <CardHeader>{t(`PRE_APPROVED_PLAN_FOR_VACANT_LAND`)}</CardHeader>
        <CardText style={{ color: "#0B0C0C", marginTop: "12px", fontSize: "16px", fontWeight: "400", lineHeight: "24px" }}>{t(`PRE_APPROVE_DESCRIPTION`)}</CardText>
        {isLoading ?
          <Loader /> :
          <Fragment>
          <div style={{ fontWeight: 700, marginBottom: "8px" }}>{t("BPAPAP_PLOT_DIMENSIONS_HEADER")}</div>
            {data?.PreApprovedDisclaimer?.Disclaimer?.[0]?.plotDimensions?.map((dimensions, index) => (
                
                <div key={`dimension-${index}`}>
                
                <div style={{ fontWeight: 700, marginBottom: "8px" }}>
                    <div style={{ display: "flex" }}>
                    <div style={{ minWidth: "20px" }}>&nbsp;</div>
                    <div>{t(dimensions?.code)}</div>
                    </div>
                </div>
                {dimensions?.info && (
                    <div style={{ marginBottom: "16px" }}>
                    <div style={{ display: "flex" }}>
                        <div style={{ minWidth: "20px" }}></div>
                        <div style={{ color: "#505A5F", fontSize: "16px" }}>{t(dimensions.info)}</div>
                    </div>
                    </div>
                )}
                </div>
            ))}
            <div style={{ fontWeight: 700, marginBottom: "8px" }}>{t("BPAPAP_REQUIRED_DOCUMENTS")}</div>
            {data?.PreApprovedDisclaimer?.Disclaimer?.[0]?.docTypes?.map((doc, index) => (
                <div key={`doctype-${index}`}>
                <div style={{ fontWeight: 700, marginBottom: "8px" }}>
                    <div style={{ display: "flex" }}>
                    <div style={{ minWidth: "20px" }}>{`${index + 1}.`}&nbsp;</div>
                    <div>{t(`BPAPAP_HEADER_${doc?.code.replace('.', '_')}`)}</div>
                    </div>
                </div>
                {doc?.info && (
                    <div style={{ marginBottom: "16px" }}>
                    <div style={{ display: "flex" }}>
                        <div style={{ minWidth: "20px" }}></div>
                        <div style={{ color: "#505A5F", fontSize: "16px" }}>{t(doc.info.replace('.', '_'))}</div>
                    </div>
                    </div>
                )}
                </div>
            ))}
            <div style={{marginTop:"10px", marginBottom:"10px"}}>{t("BPAPAP_SUBMISSION_REQUIREMENTS")}</div>
            <div style={{marginTop:"5px", marginBottom:"10px"}}>{t("BPAPAP_ACKNOWLEDGEMENT_STATEMENT")}</div>
            </Fragment>

        }
        <SubmitBar label={t(`CS_COMMON_NEXT`)} onSubmit={onSelect} />
      </Card>
      <CitizenInfoLabel info={t("CS_FILE_APPLICATION_INFO_LABEL")} text={t(`OBPS_DOCS_FILE_SIZE`)} className={"info-banner-wrap-citizen-override"} />
      </div>
      {/* </div> */}
    </Fragment>
  );
};

export default PreApprovedDocsRequired; 