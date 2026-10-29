import { Card, CardHeader, CardSubHeader, CardText, Loader, SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useState } from "react";
import { CND_VARIABLES } from "../utils";
import BookingPopup from "../components/BookingPopup";

// First page whic will render and shows the Pre-requistes to fill the application Form

const CndRequirementDetails = ({ t, onSelect, config }) => {
  sessionStorage.removeItem("docReqScreenByBack");
  const [existingDataSet, setExistingDataSet] = useState("");
  const [showModal, setShowModal] = useState(false);
  const [isExistingPopupRequired, setIsExistingPopupRequired] = useState(true);

  const handleOpenModal = () => {
    isExistingPopupRequired ? setShowModal(true) : goNext();
  };

  const goNext = () => {
    if (existingDataSet && Object.keys(existingDataSet).length > 0) {
      onSelect(config.key, existingDataSet);
    } else {
      onSelect(config.key, {});
    }
  };

  return (
    <React.Fragment>
      <Card>
      <CardHeader>{t(CND_VARIABLES.MODULE)}</CardHeader>
        <div>
         <CardText className={"primaryColor"}>{t("DOC_REQ_SCREEN_SUB_HEADER")}</CardText>
          <CardText className={"primaryColor"}>{t("DOC_REQ_SCREEN_TEXT")}</CardText>
          <CardText className={"primaryColor"}>{t("DOC_REQ_SCREEN_SUB_TEXT")}</CardText>
            <CardSubHeader>{t("REQ_SCREEN_LABEL")}</CardSubHeader>
            <CardText className={"primaryColor"}>{t("DOC_REQ_SCREEN_LABEL_TEXT")}</CardText>
            <CardText className={"primaryColor"}>{t('DOCUMENT_ACCEPTED_PDF_JPG_PNG')}</CardText>
            <CardText className={"primaryColor"}>
            {1}. {t("CND_UPLOAD_SITE_PHOTO")}
           </CardText>
           <CardText className={"primaryColor"}>
           {2}. {t("CND_UPLOAD_SITE_STACK_PHOTO")}
           </CardText>
        </div>
        <span>
          <SubmitBar label={t(CND_VARIABLES.NEXT)} onSubmit={handleOpenModal} />
        </span>
      </Card>

      {showModal && (
        <BookingPopup
          t={t}
          closeModal={() => setShowModal(false)}
          actionCancelOnSubmit={() => setShowModal(false)}
          onSubmit={() => {
            goNext();
            setShowModal(false);
          }}
          setExistingDataSet={setExistingDataSet}
        />
      )}
    </React.Fragment>
  );
};

export default CndRequirementDetails;