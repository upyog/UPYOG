import React, { Fragment } from "react";
import { Card, CardHeader, SubmitBar, CitizenInfoLabel, CardText, Loader, CardSubHeader, BackButton, BreadCrumb, Header, CardLabel, CardSectionHeader, CardCaption, ActionBar, PrintBtnCommon } from "@upyog/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import { useHistory, useRouteMatch } from "react-router-dom";
import "../css/ws-inline-auto.css";
const WSDocsRequired = ({
  onSelect,
  userType,
  onSkip,
  config
}) => {
  const {
    t
  } = useTranslation();
  const history = useHistory();
  const match = useRouteMatch();
  const tenantId = Digit.ULBService.getStateId();
  const goNext = () => {
    onSelect("DocsReq", "");
  };
  sessionStorage.removeItem("Digit.PT_CREATE_EMP_WS_NEW_FORM");
  sessionStorage.removeItem("IsDetailsExists");
  sessionStorage.removeItem("FORMSTATE_ERRORS");
  const {
    isLoading: wsDocsLoading,
    data: wsDocs
  } = Digit.Hooks.ws.WSSearchMdmsTypes.useWSServicesMasters(tenantId);
  if (userType === "citizen") {
    return <Fragment>
        <Card>
          <CardHeader>{t(`WS_COMMON_APPL_NEW_CONNECTION`)}</CardHeader>
          <CitizenInfoLabel textStyle={{
          color: "#0B0C0C"
        }} text={t(`WS_DOCS_REQUIRED_TIME`)} showInfo={false} className="ws-auto-105" />
          <CardText className="ws-auto-106">{t(`WS_NEW_CONNECTION_TEST_1`)}</CardText>
          <CardText className="ws-auto-107">{t(`WS_NEW_CONNECTION_TEST_2`)}</CardText>
          <CardSubHeader>{t("WS_DOC_REQ_SCREEN_LABEL")}</CardSubHeader>
          <CardText className="ws-auto-108">{t(`WS_NEW_CONNECTION_TEST_3`)}</CardText>
          {wsDocsLoading ? <Loader /> : <Fragment>
              {wsDocs?.Documents?.map((doc, index) => <div>
                  <div key={index} className="ws-auto-109">
                    <div className="ws-auto-110">
                      <div>{`${index + 1}.`}&nbsp;</div>
                      <div>{` ${t(doc?.code.replace('.', '_'))}`}</div>
                    </div>
                  </div>
                  <div className="ws-auto-111">
                    {doc?.dropdownData?.map((value, index) => doc?.dropdownData?.length !== index + 1 ? <span>{`${t(value?.i18nKey)}, `}</span> : <span>{`${t(value?.i18nKey)}`}</span>)}
                  </div>
                </div>)}
            </Fragment>}
          <SubmitBar label={t(`CS_COMMON_NEXT`)} onSubmit={goNext} />
        </Card>
      </Fragment>;
  }
  const printDiv = () => {
    let content = document.getElementById("documents-div").innerHTML;
    //APK button to print required docs
    if (window.mSewaApp && window.mSewaApp.isMsewaApp()) {
      window.mSewaApp.downloadBase64File(window.btoa(content), t("WS_REQ_DOCS"));
    } else {
      let printWindow = window.open("", "");
      printWindow.document.write(`<html><body>${content}</body></html>`);
      printWindow.document.close();
      printWindow.focus();
      printWindow.print();
    }
  };
  return <div className="ws-auto-112">
      <div>
        <Header styles={{
        fontSize: "32px",
        marginLeft: "18px",
        display: "flex",
        justifyContent: "space-between",
        marginRight: "12px"
      }}>
          <div>
            {t("WS_WATER_AND_SEWERAGE_NEW_CONNECTION_LABEL")}
          </div>
          <div onClick={printDiv} className="ws-auto-113">
            <PrintBtnCommon /><div className="ws-auto-114">{"Print"}</div>
          </div>
        </Header>
      </div>
      <Card>
        {wsDocsLoading ? <Loader /> : <div id="documents-div">
            {wsDocs?.Documents?.map((doc, index) => <div key={index} className="ws-auto-115">
                <CardSectionHeader className="ws-auto-116">{t(doc?.code.replace('.', '_'))}</CardSectionHeader>
                {doc.dropdownData && doc.dropdownData.length > 1 && <p className="ws-auto-117">{t(`${doc?.code.replace('.', '_')}_DESCRIPTION`)}</p>}
                <div className="ws-auto-118">
                  {doc?.dropdownData?.map((value, idx) => <p className="ws-auto-119">{`${idx + 1}. ${t(value?.i18nKey)}`}</p>)}
                </div>
                <p className="ws-auto-120">{t(`${doc?.code.replace('.', '_')}_BELOW_DESCRIPTION`)}</p>
              </div>)}
          </div>}
        <ActionBar className="ws-auto-121">
          {<SubmitBar label={t("ACTION_TEST_APPLY")} onSubmit={() => {
          history.push(match.path.replace("create-application", "new-application"));
        }} disabled={wsDocsLoading ? true : false} className="ws-auto-122" />}
        </ActionBar>
      </Card>
    </div>;
};
export default WSDocsRequired;
