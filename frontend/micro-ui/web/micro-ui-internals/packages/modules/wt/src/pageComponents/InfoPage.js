import { Card, CardHeader, CardText, SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useState } from "react";
import BookingPopup from "../components/BookingPopup";

// First component which will show the details for the application form

const InfoPage = ({ t, onSelect, formData, config, userType }) => {
  const [existingDataSet, setExistingDataSet] = useState("");
  const [showModal, setShowModal] = useState(false);

  // Function to open the BookingPopup
  const handleOpenModal = () => {
    setShowModal(true);
  };
  // Function to handle the next action
  const goNext = () => {
    let owner = formData.infodetails;
    let ownerStep;
      ownerStep = { ...owner, existingDataSet };
      onSelect(config.key, { ...formData[config.key], ...ownerStep });
  };

  return (
    <React.Fragment>
      <Card>
        {formData?.serviceType?.serviceType.code=== "MobileToilet" ? (
          <CardHeader>{t("MT_MODULE")}</CardHeader>
        ) : formData?.serviceType?.serviceType.code=== "WT" ? (
          <CardHeader>{t("WT_MODULE")}</CardHeader>
        ) : null}
        
        <div>
          <CardText className={"primaryColor"}>{t("SV_DOC_REQ_SCREEN_SUB_HEADER")}</CardText>
          <CardText className={"primaryColor"}>{t("SV_DOC_REQ_SCREEN_TEXT")}</CardText>
          <CardText className={"primaryColor"}>{t("SV_DOC_REQ_SCREEN_SUB_TEXT")}</CardText>
        </div>
        <span>
          <SubmitBar label={t("COMMON_NEXT")} onSubmit={handleOpenModal} />
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
          setExistingDataSet={setExistingDataSet} // Pass the setExistingDataSet function
        />
      )}
    </React.Fragment>
  );
};

export default InfoPage;
