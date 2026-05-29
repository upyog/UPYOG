import { Card, CardHeader, CardSubHeader, CardText, CitizenInfoLabel, LinkButton, Row, StatusTable, SubmitBar, EditIcon, Header, CardSectionHeader } from "@upyog/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { useHistory, useRouteMatch, Link } from "react-router-dom";
import Timeline from "../../../components/Timeline";
import WSDocument from "../../../pageComponents/WSDocument";
import "../../../css/ws-inline-auto.css";
const CheckPage = ({
  onSubmit,
  value
}) => {
  const {
    t
  } = useTranslation();
  const history = useHistory();
  const match = useRouteMatch();
  let isMobile = window.Digit.Utils.browser.isMobile();
  const {
    ConnectionHolderDetails,
    plumberPreference,
    serviceName,
    waterConectionDetails,
    sewerageConnectionDetails,
    documents,
    cpt
  } = value;
  let routeLink = `/upyog-ui/citizen/ws/create-application`;
  if (window.location.href.includes("/edit-application/")) routeLink = `/upyog-ui/citizen/ws/edit-application/${value?.tenantId}`;
  function routeTo(jumpTo) {
    location.href = jumpTo;
  }
  let propAddArr = [];
  if (cpt && cpt?.details && Object.keys(cpt?.details).length > 0) {
    if (cpt?.details?.address?.doorNo) {
      propAddArr.push(cpt?.details?.address?.doorNo);
    }
    if (cpt?.details?.address?.street) {
      propAddArr.push(cpt?.details?.address?.street);
    }
    if (cpt?.details?.address?.landmark) {
      propAddArr.push(cpt?.details?.address?.landmark);
    }
    if (cpt?.details?.address?.locality?.code) {
      propAddArr.push(t(Digit.Utils.pt.getMohallaLocale(cpt?.details?.address?.locality?.code, cpt?.details?.tenantId)));
    }
    if (cpt?.details?.tenantId) {
      propAddArr.push(t(Digit.Utils.pt.getCityLocale(cpt?.details?.tenantId)));
    }
    if (cpt?.details?.address?.pincode) {
      propAddArr.push(cpt?.details?.address?.pincode);
    }
  }
  const reversedOwners = Array.isArray(cpt?.details?.owners) ? cpt?.details?.owners.slice().reverse() : [];
  return <React.Fragment>
    <Timeline currentStep={4} />
    <Header styles={{
      fontSize: "32px"
    }}>{t("WS_COMMON_SUMMARY")}</Header>
    <Card className="ws-auto-219">
    <CardHeader styles={{
        fontSize: "28px"
      }}>{t(`WS_BASIC_DETAILS_HEADER`)}</CardHeader>
        <StatusTable>
          <Row className="border-none" label={t("WS_PROPERTY_ID_LABEL")} text={cpt?.details?.propertyId} />
          <Row className="border-none" label={t("WS_OWNERS_NAME_LABEL")} text={t(reversedOwners[0]?.name)} />
          <Row className="border-none" label={t("WS_COMMON_TABLE_COL_ADDRESS")} text={propAddArr.join(', ')} />
          <Row className="border-none" label={t("WS_CONNECTION_DETAILS_STATUS_LABEL")} text={t(cpt?.details?.status)} />
        </StatusTable>
        <div className="ws-auto-220">
          <Link to={`/upyog-ui/citizen/commonpt/view-property?propertyId=${cpt?.details?.propertyId}&tenantId=${cpt?.details?.tenantId}`}>
            <LinkButton label={t("PT_VIEW_PROPERTY")} className="ws-auto-221" />
          </Link>
        </div>
    </Card>
    <Card className="ws-auto-222">
    <div className="ws-auto-223">
    <CardHeader styles={{
          fontSize: "28px"
        }}>{t("WS_COMMON_CONNECTION_HOLDER_DETAILS_HEADER")}</CardHeader>
    <LinkButton label={<EditIcon className="ws-auto-225" />} onClick={() => routeTo(`${routeLink}/connection-holder`)} className="ws-auto-224" />
      </div>
        <StatusTable>
          <Row className="border-none" textStyle={isMobile ? {
          marginRight: "-5px"
        } : {}} label={t("WS_OWN_MOBILE_NO")} text={ConnectionHolderDetails?.mobileNumber} />
          <Row className="border-none" label={t("WS_OWN_DETAIL_NAME")} text={ConnectionHolderDetails?.name} />
          <Row className="border-none" label={t("WS_OWN_DETAIL_GENDER_LABEL")} text={t(ConnectionHolderDetails?.gender?.i18nKey) || t("CS_NA")} />
          <Row className="border-none" label={t("WS_FATHERS_HUSBAND_NAME")} text={ConnectionHolderDetails?.guardian || t("CS_NA")} />
          <Row className="border-none" label={t("WS_CONN_HOLDER_OWN_DETAIL_RELATION_LABEL")} text={t(ConnectionHolderDetails?.relationship?.i18nKey) || t("CS_NA")} />
          <Row className="border-none" label={t("WS_OWN_DETAIL_CROSADD")} text={ConnectionHolderDetails?.address || t("CS_NA")} />
          <Row className="border-none" label={t("WS_OWN_DETAIL_SPECIAL_APPLICANT_LABEL")} text={t(ConnectionHolderDetails?.specialCategoryType?.i18nKey) || t("CS_NA")} />
          <Row className="border-none" label={t("WS_EMAIL_ID")} text={ConnectionHolderDetails?.emailId || t("CS_NA")} />
    </StatusTable>
    </Card>
    <Card className="ws-auto-226">
    <div className="ws-auto-227">
    <CardHeader styles={{
          fontSize: "28px"
        }}>{t("WS_COMMON_CONNECTION_DETAIL")}</CardHeader>
    <LinkButton label={<EditIcon className="ws-auto-229" />} onClick={() => routeTo(`${routeLink}/service-name`)} className="ws-auto-228" />
      </div>
        <StatusTable>
          <Row className="border-none" textStyle={isMobile ? {
          marginRight: "-10px"
        } : {}} label={t("WS_SERVICE_NAME_LABEL")} text={t(serviceName?.i18nKey)} />
          {waterConectionDetails && Object.keys(waterConectionDetails)?.length > 0 && <div>
            <Row className="border-none" label={t("WS_NO_OF_TAPS_PROPOSED")} text={waterConectionDetails?.proposedTaps} />
            <Row className="border-none" label={t("WS_SERV_DETAIL_PIPE_SIZE")} text={t(waterConectionDetails?.proposedPipeSize?.i18nKey)} />
          </div>}
          {sewerageConnectionDetails && Object.keys(sewerageConnectionDetails)?.length > 0 && <div>
            <Row className="border-none" label={t("WS_NO_OF_WATER_CLOSETS")} text={sewerageConnectionDetails?.proposedWaterClosets} />
            <Row className="border-none" label={t("WS_SERV_DETAIL_NO_OF_TOILETS")} text={sewerageConnectionDetails?.proposedToilets} />
          </div>}
        </StatusTable>
    </Card>
    <Card className="ws-auto-230">
      <div className="ws-auto-231">
        <CardHeader styles={{
          fontSize: "28px"
        }}>{t("WS_COMMON_DOCUMENT_DETAILS")}</CardHeader>
          <LinkButton label={<EditIcon className="ws-auto-233" />} onClick={() => routeTo(`${routeLink}/document-details`)} className="ws-auto-232" />
        </div>
        {documents && documents?.documents.map((doc, index) => <div key={`doc-${index}`}>
         {<div><CardSectionHeader>{t(doc?.documentType?.split('.').slice(0, 2).join('_'))}</CardSectionHeader>
          <StatusTable>
          {<WSDocument value={value} Code={doc?.documentType} index={index} />}
          {documents?.documents.length != index + 1 ? <hr className="ws-auto-234" /> : null}
          </StatusTable>
          </div>}
          </div>)}
      </Card>
      <SubmitBar label={t("CS_COMMON_SUBMIT")} onSubmit={onSubmit} className="ws-auto-235" />
    </React.Fragment>;
};
export default CheckPage;
