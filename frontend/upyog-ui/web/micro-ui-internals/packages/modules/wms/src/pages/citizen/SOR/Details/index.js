import { ActionBar, Card, CardSubHeader, DocumentSVG, Header, Loader, Menu, Row, StatusTable, SubmitBar } from "@egovernments/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory, useParams } from "react-router-dom";
import ActionModal from "../../../../components/Modal";
import { convertEpochToDate, pdfDownloadLink } from "../../../../components/Utils";

const WmsSorDetails = () => {
  const activeworkflowActions = ["DEACTIVATE_SOR_HEAD", "COMMON_EDIT_SOR_HEADER"];
  const deactiveworkflowActions = ["ACTIVATE_SOR_HEAD"];
  const [selectedAction, setSelectedAction] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const { t } = useTranslation();
  const { id: sorId } = useParams();
  const { tenantId: tenantId } = useParams()
  const history = useHistory();
  const [displayMenu, setDisplayMenu] = useState(false);
  const isupdate = Digit.SessionStorage.get("isupdate");
  const { isLoading, isError, error, data, ...rest } = Digit.Hooks.wms.sor.useWmsSorSearch({ sor_id: sorId }, tenantId, null, isupdate);
  const [errorInfo, setErrorInfo, clearError] = Digit.Hooks.useSessionStorage("SOR_WMS_ERROR_DATA", false);
  const [mutationHappened, setMutationHappened, clear] = Digit.Hooks.useSessionStorage("SOR_WMS_MUTATION_HAPPENED", false);
  const [successData, setsuccessData, clearSuccessData] = Digit.Hooks.useSessionStorage("SOR_WMS_MUTATION_SUCCESS_DATA", false);
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
        <Header>{t("WMS_NEW_SOR_FORM_HEADER")}</Header>
      </div>
      {!isLoading && data?.length > 0 ? (
        <div>
          <Card>
            <CardSubHeader className="card-section-header">{t("WMS_SOR_DETAILS_FORM_HEADER")} </CardSubHeader>
            <StatusTable>
              <Row label={t("WMS_SOR_ID_LABEL")} text={data?.[0]?.sor_id || "NA"} textStyle={{ whiteSpace: "pre" }} />
              <Row label={t("WMS_SOR_NAME_LABEL")} text={data?.[0]?.sor_name || "NA"} textStyle={{ whiteSpace: "pre" }} />
              <Row label={t("WMS_SOR_DESC_OF_ITEM_LABEL")} text={data?.[0]?.description_of_item || "NA"} />
              <Row label={t("WMS_SOR_CHAPTER_LABEL")} text={data?.[0]?.chapter || "NA"} />
              <Row label={t("WMS_SOR_ITEM_NO_LABEL")} text={t(data?.[0]?.item_no) || "NA"} />
              <Row label={t("WMS_SOR_RATE_LABEL")} text={data?.[0]?.rate || "NA"} />
              <Row label={t("WMS_SOR_UNIT_LABEL")} text={data?.[0]?.unit || "NA"} />
              <Row label={t("WMS_SOR_START_DATE_LABEL")} text={convertEpochToDate(data?.[0]?.start_date) || "NA"} />
              <Row label={t("WMS_SOR_END_DATE_LABEL")} text={convertEpochToDate(data?.[0]?.end_date) || "NA"} />
            </StatusTable>
            </Card>
            </div>
      ) : null}    
      <ActionBar>
        <SubmitBar label={t("WMS_COMMON_TAKE_ACTION")} onSubmit={() =>  history.push(`/upyog-ui/citizen/wms/sor-edit/${data?.[0]?.sor_id}`)} />
      </ActionBar>
    </React.Fragment>
  );
};

export default WmsSorDetails;
