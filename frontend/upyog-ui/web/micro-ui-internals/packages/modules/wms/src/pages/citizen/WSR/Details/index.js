import { ActionBar, Card, CardSubHeader, DocumentSVG, Header, Loader, Menu, Row, StatusTable, SubmitBar } from "@egovernments/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory, useParams } from "react-router-dom";
import ActionModal from "../../../../components/Modal";
import { convertEpochToDate, pdfDownloadLink } from "../../../../components/Utils";

const WmsWsrDetails = () => {
  const activeworkflowActions = ["DEACTIVATE_WSR_HEAD", "COMMON_EDIT_WSR_HEADER"];
  const deactiveworkflowActions = ["ACTIVATE_WSR_HEAD"];
  const [selectedAction, setSelectedAction] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const { t } = useTranslation();
  const { id: wsrId } = useParams();
  const { tenantId: tenantId } = useParams()
  const history = useHistory();
  const [displayMenu, setDisplayMenu] = useState(false);
  const isupdate = Digit.SessionStorage.get("isupdate");
  const { isLoading, isError, error, data, ...rest } = Digit.Hooks.wms.wsr.useWmsWsrSearch({ wsreme_id: wsrId }, tenantId, null, isupdate);
  const [errorInfo, setErrorInfo, clearError] = Digit.Hooks.useSessionStorage("WSR_WMS_ERROR_DATA", false);
  const [mutationHappened, setMutationHappened, clear] = Digit.Hooks.useSessionStorage("WSR_WMS_MUTATION_HAPPENED", false);
  const [successData, setsuccessData, clearSuccessData] = Digit.Hooks.useSessionStorage("WSR_WMS_MUTATION_SUCCESS_DATA", false);
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

  const downloadWsrDetails = async () => {
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
        <Header>{t("WMS_NEW_WSR_FORM_HEADER")}</Header>
      </div>
      {!isLoading && data?.length > 0 ? (
        <div>
          <Card>
            <CardSubHeader className="card-section-header">{t("WMS_WSR_DETAILS_FORM_HEADER")} </CardSubHeader>
            <StatusTable>
              <Row label={t("WMS_WSR_ID_LABEL")} text={data?.[0]?.wsr_id || "NA"} textStyle={{ whiteSpace: "pre" }} />
              <Row label={t("WMS_WSR_PROJECT_NAME_LABEL")} text={data?.[0]?.project_name || "NA"} textStyle={{ whiteSpace: "pre" }} />
              <Row label={t("WMS_WSR_WORK_NAME_LABEL")} text={data?.[0]?.work_name || "NA"} textStyle={{ whiteSpace: "pre" }} />
              <Row label={t("WMS_WSR_ML_NAME_LABEL")} text={data?.[0]?.milestone_name || "NA"} />
              <Row label={t("WMS_WSR_PERCENT_NAME_LABEL")} text={data?.[0]?.percent_weightage || "NA"} />
              
            </StatusTable>
            </Card>
            </div>
      ) : null}    
      <ActionBar>
        <SubmitBar label={t("WMS_COMMON_TAKE_ACTION")} onSubmit={() =>  history.push(`/upyog-ui/citizen/wms/wsr-edit/${data?.[0]?.wsr_id}`)} />
      </ActionBar>
    </React.Fragment>
  );
};

export default WmsWsrDetails;
