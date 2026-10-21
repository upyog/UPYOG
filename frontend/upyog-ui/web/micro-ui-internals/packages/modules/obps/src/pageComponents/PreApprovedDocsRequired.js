import React, { Fragment, useEffect } from "react";
import { Card, CardHeader, CardLabel, CardSubHeader, CardText, CitizenInfoLabel, Loader, SubmitBar,NavBar,OpenLinkContainer, BackButton } from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";

import useMDMS from "../../../../libraries/src/hooks/obps/useMDMS";

const PreApprovedDocsRequired = ({ onSelect, onSkip, config }) => {
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateId = Digit.ULBService.getStateId();
  const navigate = Digit.Hooks.useCustomNavigate();
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
        <CardText className="obps-docs-required-card">{t(`PRE_APPROVE_DESCRIPTION`)}</CardText>
        {isLoading ?
          <Loader /> :
          <Fragment>
          <div className="obps-docs-required-bottom-spacing">{t("BPAPAP_PLOT_DIMENSIONS_HEADER")}</div>
            {data?.PreApprovedDisclaimer?.Disclaimer?.[0]?.plotDimensions?.map((dimensions, index) => (
                
                <div key={`dimension-${index}`}>
                
                <div className="obps-docs-required-bottom-spacing">
                    <div className="obps-docs-required-flex-container">
                    <div className="obps-pre-approved-docs-required-wrapper">&nbsp;</div>
                    <div>{t(dimensions?.code)}</div>
                    </div>
                </div>
                {dimensions?.info && (
                    <div className="obps-docs-required-bottom-spacing-2">
                    <div className="obps-docs-required-flex-container">
                        <div className="obps-pre-approved-docs-required-wrapper"></div>
                        <div className="obps-pre-approved-docs-required-text-style">{t(dimensions.info)}</div>
                    </div>
                    </div>
                )}
                </div>
            ))}
            <div className="obps-docs-required-bottom-spacing">{t("BPAPAP_REQUIRED_DOCUMENTS")}</div>
            {data?.PreApprovedDisclaimer?.Disclaimer?.[0]?.docTypes?.map((doc, index) => (
                <div key={`doctype-${index}`}>
                <div className="obps-docs-required-bottom-spacing">
                    <div className="obps-docs-required-flex-container">
                    <div className="obps-pre-approved-docs-required-wrapper">{`${index + 1}.`}&nbsp;</div>
                    <div>{t(`BPAPAP_HEADER_${doc?.code.replace('.', '_')}`)}</div>
                    </div>
                </div>
                {doc?.info && (
                    <div className="obps-docs-required-bottom-spacing-2">
                    <div className="obps-docs-required-flex-container">
                        <div className="obps-pre-approved-docs-required-wrapper"></div>
                        <div className="obps-pre-approved-docs-required-text-style">{t(doc.info.replace('.', '_'))}</div>
                    </div>
                    </div>
                )}
                </div>
            ))}
            <div className="obps-building-plan-scrutiny-top-spacing-2">{t("BPAPAP_SUBMISSION_REQUIREMENTS")}</div>
            <div className="obps-pre-approved-docs-required-top-spacing">{t("BPAPAP_ACKNOWLEDGEMENT_STATEMENT")}</div>
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