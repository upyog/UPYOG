import { ActionBar, Card, CardSubHeader, DocumentSVG, Header, Loader, Menu, Row, StatusTable, SubmitBar } from "@egovernments/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory, useParams } from "react-router-dom";
import ActionModal from "../../../../components/Modal";
import { convertEpochToDate, pdfDownloadLink } from "../../../../components/Utils";

const PrjDetails = () => {
  const activeworkflowActions = ["DEACTIVATE_PRJ_HEAD", "COMMON_EDIT_PRJ_HEADER"];
  const deactiveworkflowActions = ["ACTIVATE_PRJ_HEAD"];
  const [selectedAction, setSelectedAction] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const { t } = useTranslation();
  const { id: projectId } = useParams();
  const { tenantId: tenantId } = useParams()
  const history = useHistory();
  const [displayMenu, setDisplayMenu] = useState(false);
  const isupdate = Digit.SessionStorage.get("isupdate");
  const { isLoading, isError, error, data, ...rest } = Digit.Hooks.wms.prj.useWmsPrjSearch({ project_id: projectId }, tenantId, null, isupdate);
  const [errorInfo, setErrorInfo, clearError] = Digit.Hooks.useSessionStorage("PRJ_WMS_ERROR_DATA", false);
  const [mutationHappened, setMutationHappened, clear] = Digit.Hooks.useSessionStorage("PRJ_WMS_MUTATION_HAPPENED", false);
  const [successData, setsuccessData, clearSuccessData] = Digit.Hooks.useSessionStorage("PRJ_WMS_MUTATION_SUCCESS_DATA", false);
  const isMobile = window.Digit.Utils.browser.isMobile();

  useEffect(() => {
    setMutationHappened(false);
    clearSuccessData();
    clearError();
  }, []);

  function onActionSelect(action) {
    setSelectedAction(action);
    setDisplayMenu(false);
  }

  const closeModal = () => {
    setSelectedAction(null);
    setShowModal(false);
  };

  const downloadSorDetails = async () => {
    const TLcertificatefile = await Digit.PaymentService.generatePdf(tenantId, { Licenses: application }, "tlcertificate");
    const receiptFile = await Digit.PaymentService.printReciept(tenantId, { fileStoreIds: TLcertificatefile.filestoreIds[0] });
    window.open(receiptFile[TLcertificatefile.filestoreIds[0]], "_blank");
    setShowOptions(false);
  };

  const submitAction = (data) => { };
  if (isLoading) {
    return <Loader />;
  }

  return (
    <React.Fragment>
      <div style={isMobile ? {marginLeft: "-12px", 
      fontFamily: "calibri", color: "#FF0000"} :{ marginLeft: "15px", fontFamily: "calibri", color: "#FF0000" }}>
        <Header>{t("WMS_NEW_PRJ_FORM_HEADER")}</Header>
      </div>
      {!isLoading && data?.length > 0 ? (
        <div>
          <Card>
            <CardSubHeader className="card-section-header">{t("WMS_PRJ_DETAILS_FORM_HEADER")} </CardSubHeader>
            <StatusTable>
              <Row label={t("WMS_PRJ_PROJECT_ID_LABEL")} text={data?.[0]?.project_id || "NA"} textStyle={{ whiteSpace: "pre" }} />
              <Row label={t("WMS_PRJ_PROJECT_NAME_EN_LABEL")} text={data?.[0]?.project_name_en || "NA"} textStyle={{ whiteSpace: "pre" }} />
              <Row label={t("WMS_PRJ_PROJECT_NAME_REG_LABEL")} text={data?.[0]?.project_name_reg || "NA"} />
              <Row label={t("WMS_PRJ_PROJECT_DESCRIPTION_LABEL")} text={data?.[0]?.project_description || "NA"} />
              <Row label={t("WMS_PRJ_APPROVAL_NUMBER_LABEL")} text={t(data?.[0]?.approval_number) || "NA"} />
              <Row label={t("WMS_PRJ_DEPARTMENT_LABEL")} text={data?.[0]?.department || "NA"} />
              <Row label={t("WMS_PRJ_PROJECT_TIMELINE_LABEL")} text={data?.[0]?.project_timeline || "NA"} />
              <Row label={t("WMS_PRJ_SCHEME_NAME_LABEL")} text={data?.[0]?.scheme_name || "NA"} />
              <Row label={t("WMS_PRJ_SCHEME_NO_LABEL")} text={data?.[0]?.scheme_no || "NA"} />              
              <Row label={t("WMS_PRJ_SOURCE_OF_FUND_LABEL")} text={data?.[0]?.source_of_fund || "NA"} />
              <Row label={t("WMS_PRJ_APPROVAL_DATE_LABEL")} text={convertEpochToDate(data?.[0]?.approval_date) || "NA"} />
              <Row label={t("WMS_PRJ_PROJECT_START_DATE_LABEL")} text={convertEpochToDate(data?.[0]?.project_start_date) || "NA"} />
              <Row label={t("WMS_PRJ_PROJECT_END_DATE_LABEL")} text={convertEpochToDate(data?.[0]?.project_end_date) || "NA"} />
              <Row label={t("WMS_PRJ_STATUS_LABEL")} text={data?.[0]?.status || "NA"} />
            </StatusTable>
            </Card>
            </div>
      ) : null}    
      <ActionBar>
        <SubmitBar label={t("WMS_COMMON_TAKE_ACTION")} onSubmit={() =>  history.push(`/upyog-ui/citizen/wms/prj-edit/${data?.[0]?.project_id}`)} />
      </ActionBar>
    </React.Fragment>
  );
};

export default PrjDetails;
