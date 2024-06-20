import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { ActionBar, Card, CardSubHeader, DocumentSVG, Header, Loader, Menu, Row, StatusTable, SubmitBar } from "@egovernments/digit-ui-react-components";
import { useHistory, useParams } from "react-router-dom";


const WmsRAFBDetail = () => {
  const { t } = useTranslation();
  const { tenantId,id } = useParams();
  console.log("tenantId ",tenantId,id)
  const history = useHistory();
  const isMobile = window.Digit.Utils.browser.isMobile();
//   const { isLoading, isError, error, data, ...rest } = Digit.Hooks.wms.sor.useWmsSorSearch({ sor_id: sorId }, tenantId, null, isupdate);

  return (
    <React.Fragment>
      <div
        style={
          isMobile
            ? { marginLeft: "-12px", fontFamily: "calibri", color: "#FF0000" }
            : { marginLeft: "15px", fontFamily: "calibri", color: "#FF0000" }
        }
      >
        <Header>{t("WMS_NEW_SOR_FORM_HEADER")}</Header>
      </div>
      {/* {!isLoading && data?.length > 0 ? ( */}
      {true ? (
        <div>
          <Card>
            <CardSubHeader className="card-section-header">{t("WMS_SOR_DETAILS_FORM_HEADER")} </CardSubHeader>
            <StatusTable>
              <Row label={t("Applicant Name")} text={"Henry" || "NA"} textStyle={{ whiteSpace: "pre" }} />
              {/* <Row label={t("WMS_SOR_NAME_LABEL")} text={data?.[0]?.sor_name || "NA"} textStyle={{ whiteSpace: "pre" }} />
                  <Row label={t("WMS_SOR_DESC_OF_ITEM_LABEL")} text={data?.[0]?.description_of_item || "NA"} />
                  <Row label={t("WMS_SOR_CHAPTER_LABEL")} text={data?.[0]?.chapter || "NA"} />
                  <Row label={t("WMS_SOR_ITEM_NO_LABEL")} text={t(data?.[0]?.item_no) || "NA"} />
                  <Row label={t("WMS_SOR_RATE_LABEL")} text={data?.[0]?.rate || "NA"} />
                  <Row label={t("WMS_SOR_UNIT_LABEL")} text={data?.[0]?.unit || "NA"} />
                  <Row label={t("WMS_SOR_START_DATE_LABEL")} text={convertEpochToDate(data?.[0]?.start_date) || "NA"} />
                  <Row label={t("WMS_SOR_END_DATE_LABEL")} text={convertEpochToDate(data?.[0]?.end_date) || "NA"} /> */}
            </StatusTable>
          </Card>
        </div>
      ) : null}
      <ActionBar>
        {/* <SubmitBar label={t("WMS_COMMON_TAKE_ACTION")} onSubmit={() => history.push(`/upyog-ui/citizen/wms/running-account/detail/${"pg"}/:id/${data?.[0]?.sor_id}`)} /> */}
        {/* <SubmitBar label={t("WMS_COMMON_TAKE_ACTION")} onSubmit={() => history.push(`/upyog-ui/citizen/wms/running-account/edit/:${tenantId}/:${id}`)} /> */}
        <SubmitBar label={t("WMS_COMMON_TAKE_ACTION")} onSubmit={() => history.push(`/upyog-ui/citizen/wms/running-account/edit/pg/114`)} />
      </ActionBar>
    </React.Fragment>
  );
};
export default WmsRAFBDetail;
