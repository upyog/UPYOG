import React, { Fragment } from "react";
import { Card, CardHeader, SubmitBar, CitizenInfoLabel, CardText, Loader, CardSubHeader, BackButton, BreadCrumb, Header, CardLabel, CardSectionHeader, CardCaption, ActionBar } from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import {  useLocation } from "react-router-dom";

const WSDisconnectionDocsRequired = ({ userType }) => {
  const { t } = useTranslation();
  const navigate = Digit.Hooks.useCustomNavigate();
  const { pathname } = useLocation();
  const tenantId = Digit.ULBService.getStateId();
  const goNext = () => {};
  const {
    isLoading: wsDocsLoading,
    data: wsDocs
  } = Digit.Hooks.ws.WSSearchMdmsTypes.useWSServicesMasters(tenantId, "DisconnectionDocuments");
  if (userType === "citizen") {
    return <Fragment>
        <Card>
          <CardHeader>{t(`WS_COMMON_APPLICATION_DISCONNECTION`)}</CardHeader>
          <CitizenInfoLabel textStyle={{
          color: "#0B0C0C",
          paddingLeft: "40px",
          paddingRight: "40px"
        }} text={t(`WS_DOCS_REQUIRED_TIME`)} showInfo={false} className="ws-disconnection-docs-required-label" />
          <CardText className="ws-disconnection-docs-required-label-2">{t(`WS_NEW_CONNECTION_TEST_1`)}</CardText>
          <CardText className="ws-disconnection-docs-required-label-2">{t(`WS_NEW_CONNECTION_TEST_2`)}</CardText>
          <CardSubHeader>{t("WS_DOC_REQ_SCREEN_LABEL")}</CardSubHeader>
          <CardText className="ws-disconnection-docs-required-label-2">{t(`WS_NEW_CONNECTION_TEST_3`)}</CardText>
          {wsDocsLoading ? <Loader /> : <Fragment>
              {wsDocs?.DisconnectionDocuments?.map((doc, index) => <div>
                  <div key={index} className="ws-disconnection-docs-required-label-bold">
                    <div className="ws-disconnection-docs-required-flex">
                      <div>{`${index + 1}.`}&nbsp;</div>
                      <div>{` ${t(doc?.code.replace('.', '_'))}`}</div>
                    </div>
                  </div>
                  <div className="ws-disconnection-docs-required-grey-text">
                    {doc?.dropdownData?.map((value, index) => doc?.dropdownData?.length !== index + 1 ? <span className="ws-disconnection-docs-required-grey-text-2">{`${t(value?.i18nKey)}, `}</span> : <span className="ws-disconnection-docs-required-grey-text-2">{`${t(value?.i18nKey)}`}</span>)}
                  </div>
                </div>)}
            </Fragment>}
          <SubmitBar label={t(`CS_COMMON_NEXT`)} onSubmit={() => {
                navigate(pathname.replace("docsrequired", "application-form"));
              }} />
        </Card>
      </Fragment>;
  }
  return <div className="ws-disconnection-docs-required-item">
      <Header styles={{
      fontSize: "32px",
      marginLeft: "18px"
    }}>{t("WS_WATER_AND_SEWERAGE_DISCONNECTION")}</Header>
      <Card>
        {wsDocsLoading ? <Loader /> : <div id="documents-div">
            {wsDocs?.DisconnectionDocuments?.map((doc, index) => <div key={index} className="ws-disconnection-docs-required-mt-md">
                <CardSectionHeader className="ws-disconnection-docs-required-title-lg">{t(doc?.code.replace('.', '_'))}</CardSectionHeader>
                {doc.dropdownData && doc.dropdownData.length > 1 && <p className="ws-disconnection-docs-required-item-2">{t(`${doc?.code.replace('.', '_')}_DESCRIPTION`)}</p>}
                <div className="ws-disconnection-docs-required-item-3">
                  {doc?.dropdownData?.map((value, idx) => <p className="ws-disconnection-docs-required-card-bold">{`${idx + 1}. ${t(value?.i18nKey)}`}</p>)}
                </div>
                <p className="ws-disconnection-docs-required-item-4">{t(`${doc?.code.replace('.', '_')}_BELOW_DESCRIPTION`)}</p>
              </div>)}
          </div>}
        <ActionBar className="ws-disconnection-docs-required-flex-2">
          {<SubmitBar label={t("ACTION_TEST_APPLY")} onSubmit={() => {
          navigate(pathname.replace("docsrequired", "application-form"));
        }} disabled={wsDocsLoading ? true : false} className="ws-disconnection-docs-required-link" />}
        </ActionBar>
      </Card>
    </div>;
};
export default WSDisconnectionDocsRequired;
