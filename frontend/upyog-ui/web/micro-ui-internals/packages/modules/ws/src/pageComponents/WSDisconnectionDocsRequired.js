import React, { Fragment } from "react";
import { Card, CardHeader, SubmitBar, CitizenInfoLabel, CardText, Loader, CardSubHeader, BackButton, BreadCrumb, Header, CardLabel, CardSectionHeader, CardCaption, ActionBar } from "@upyog/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import { useHistory, useRouteMatch } from "react-router-dom";
import "../css/ws-inline-auto.css";
const WSDisconnectionDocsRequired = ({
  userType
}) => {
  const {
    t
  } = useTranslation();
  const history = useHistory();
  const match = useRouteMatch();
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
        }} text={t(`WS_DOCS_REQUIRED_TIME`)} showInfo={false} className="ws-auto-63" />
          <CardText className="ws-auto-64">{t(`WS_NEW_CONNECTION_TEST_1`)}</CardText>
          <CardText className="ws-auto-65">{t(`WS_NEW_CONNECTION_TEST_2`)}</CardText>
          <CardSubHeader>{t("WS_DOC_REQ_SCREEN_LABEL")}</CardSubHeader>
          <CardText className="ws-auto-66">{t(`WS_NEW_CONNECTION_TEST_3`)}</CardText>
          {wsDocsLoading ? <Loader /> : <Fragment>
              {wsDocs?.DisconnectionDocuments?.map((doc, index) => <div>
                  <div key={index} className="ws-auto-67">
                    <div className="ws-auto-68">
                      <div>{`${index + 1}.`}&nbsp;</div>
                      <div>{` ${t(doc?.code.replace('.', '_'))}`}</div>
                    </div>
                  </div>
                  <div className="ws-auto-69">
                    {doc?.dropdownData?.map((value, index) => doc?.dropdownData?.length !== index + 1 ? <span className="ws-auto-70">{`${t(value?.i18nKey)}, `}</span> : <span className="ws-auto-71">{`${t(value?.i18nKey)}`}</span>)}
                  </div>
                </div>)}
            </Fragment>}
          <SubmitBar label={t(`CS_COMMON_NEXT`)} onSubmit={() => {
          history.push(match.path.replace("docsrequired", "application-form"));
        }} />
        </Card>
      </Fragment>;
  }
  return <div className="ws-auto-72">
      <Header styles={{
      fontSize: "32px",
      marginLeft: "18px"
    }}>{t("WS_WATER_AND_SEWERAGE_DISCONNECTION")}</Header>
      <Card>
        {wsDocsLoading ? <Loader /> : <div id="documents-div">
            {wsDocs?.DisconnectionDocuments?.map((doc, index) => <div key={index} className="ws-auto-73">
                <CardSectionHeader className="ws-auto-74">{t(doc?.code.replace('.', '_'))}</CardSectionHeader>
                {doc.dropdownData && doc.dropdownData.length > 1 && <p className="ws-auto-75">{t(`${doc?.code.replace('.', '_')}_DESCRIPTION`)}</p>}
                <div className="ws-auto-76">
                  {doc?.dropdownData?.map((value, idx) => <p className="ws-auto-77">{`${idx + 1}. ${t(value?.i18nKey)}`}</p>)}
                </div>
                <p className="ws-auto-78">{t(`${doc?.code.replace('.', '_')}_BELOW_DESCRIPTION`)}</p>
              </div>)}
          </div>}
        <ActionBar className="ws-auto-79">
          {<SubmitBar label={t("ACTION_TEST_APPLY")} onSubmit={() => {
          history.push(match.path.replace("docsrequired", "application-form"));
        }} disabled={wsDocsLoading ? true : false} className="ws-auto-80" />}
        </ActionBar>
      </Card>
    </div>;
};
export default WSDisconnectionDocsRequired;
