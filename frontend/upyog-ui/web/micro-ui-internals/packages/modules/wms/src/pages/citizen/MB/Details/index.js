import { ActionBar, Card, CardSubHeader, DocumentSVG, Header, Loader, Menu, Row, StatusTable, SubmitBar } from "@egovernments/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory, useParams } from "react-router-dom";
import ActionModal from "../../../../components/Modal";
import { convertEpochToDate, pdfDownloadLink } from "../../../../components/Utils";

const WmsMbDetails = () => {
  const activeworkflowActions = ["DEACTIVATE_MB_HEAD", "COMMON_EDIT_MB_HEADER"];
  const deactiveworkflowActions = ["ACTIVATE_MB_HEAD"];
  const [selectedAction, setSelectedAction] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const { t } = useTranslation();
  const { id: mbId } = useParams();
  const { tenantId: tenantId } = useParams()
  const history = useHistory();
  const [displayMenu, setDisplayMenu] = useState(false);
  const isupdate = Digit.SessionStorage.get("isupdate");
  const { isLoading, isError, error, data, ...rest } = Digit.Hooks.wms.mb.useWmsMbSearch({ mb_id: mbId }, tenantId, null, isupdate);
  const [errorInfo, setErrorInfo, clearError] = Digit.Hooks.useSessionStorage("MB_WMS_ERROR_DATA", false);
  const [mutationHappened, setMutationHappened, clear] = Digit.Hooks.useSessionStorage("MB_WMS_MUTATION_HAPPENED", false);
  const [successData, setsuccessData, clearSuccessData] = Digit.Hooks.useSessionStorage("MB_WMS_MUTATION_SUCCESS_DATA", false);
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

  const downloadMbDetails = async () => {
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
        <Header>{t("WMS_NEW_MB_FORM_HEADER")}</Header>
      </div>
      {!isLoading && data?.length > 0 ? (
        <div>
          <Card>
            <CardSubHeader className="card-section-header">{t("WMS_MB_DETAILS_FORM_HEADER")} </CardSubHeader>
            <StatusTable>
              <Row label={t("WMS_MB_ID_LABEL")} text={data?.[0]?.mb_id || "NA"} textStyle={{ whiteSpace: "pre" }} />
              <Row label={t("WMS_MB_DESC_OF_ITEM_LABEL")} text={data?.[0]?.description_of_item || "NA"} />
              <Row label={t("WMS_MB_CHAPTER_LABEL")} text={data?.[0]?.chapter || "NA"} />
              <Row label={t("WMS_MB_DATE_LABEL")} text={convertEpochToDate(data?.[0]?.mb_date) || "NA"} />
            </StatusTable>
            </Card>
            </div>
      ) : null}    
      <ActionBar>
        <SubmitBar label={t("WMS_COMMON_TAKE_ACTION")} onSubmit={() =>  history.push(`/upyog-ui/citizen/wms/mb-edit/${data?.[0]?.mb_id}`)} />
      </ActionBar>
    </React.Fragment>
  );
};

export default WmsMbDetails;
