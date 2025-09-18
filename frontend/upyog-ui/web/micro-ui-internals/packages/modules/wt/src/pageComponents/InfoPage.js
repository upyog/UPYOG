import { Card, CardHeader, CardText, SubmitBar } from "@upyog/digit-ui-react-components";
import React, { useState } from "react";
import BookingPopup from "../components/BookingPopup";

const InfoPage = ({ t, onSelect, formData, config, userType }) => {
  const [existingDataSet, setExistingDataSet] = useState("");
  const [showModal, setShowModal] = useState(false);
  const [isExistingPopupRequired, setIsExistingPopupRequired] = useState(false);

  // Module mapping for dynamic header
  const moduleMap = {
    MobileToilet: "MT_MODULE",
    WT: "WT_MODULE",
    TREE_PRUNING: "TREE_PRUNING",
  };

  const code = formData?.serviceType?.serviceType?.code;
  const moduleKey = moduleMap[code];

  const handleOpenModal = () => {
    isExistingPopupRequired ? setShowModal(true) : goNext();
  };

  const goNext = () => {
    const owner = formData.infodetails || {};
    const ownerStep = { ...owner, existingDataSet };
    onSelect(config.key, { ...formData[config.key], ...ownerStep });
  };

  return (
    <React.Fragment>
      <Card>
        {moduleKey && <CardHeader>{t(moduleKey)}</CardHeader>}
        <div>
          <CardText className="primaryColor">{t("SV_DOC_REQ_SCREEN_SUB_HEADER")}</CardText>
          <CardText className="primaryColor">{t("SV_DOC_REQ_SCREEN_TEXT")}</CardText>
          <CardText className="primaryColor">{t("SV_DOC_REQ_SCREEN_SUB_TEXT")}</CardText>
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
          setExistingDataSet={setExistingDataSet}
        />
      )}
    </React.Fragment>
  );
};

export default InfoPage;
