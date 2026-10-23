import React, { useState, useEffect, useCallback } from "react";
import { Modal, Card, SubmitBar, CardHeader } from "@nudmcdgnpm/digit-ui-react-components";
import { ExistingBookingDetails } from "./ExistingBookingDetails";

const Close = () => (
  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="#FFFFFF">
    <path d="M0 0h24v24H0V0z" fill="none" />
    <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12 19 6.41z" />
  </svg>
);

const CloseBtn = (props) => {
  return (
    <div className="icon-bg-secondary" onClick={props.onClick}>
      <Close />
    </div>
  );
};

const Heading = (props) => {
  return <h1 className="heading-m">{props.label}</h1>;
};

const BookingPopup = ({ t, closeModal, onSubmit, setExistingDataSet }) => {
  const [showExistingBookingDetails, setShowExistingBookingDetails] = useState(false);
  const [isDataSet, setIsDataSet] = useState(false);

  const handleExistingDetailsClick = () => {
    setShowExistingBookingDetails(true);
  };

  const setData = () => {
    setIsDataSet(true);
  };

  const handleSubmit = useCallback(() => {
    if (isDataSet) {
      onSubmit();
      setIsDataSet(false);
    }
  }, [isDataSet, onSubmit]);

  useEffect(() => {
    handleSubmit();
  }, [handleSubmit]);

  const handleExistingBookingSubmit = () => {
    onSubmit();
  };

  return (
    <React.Fragment>
      <Modal
        headerBarMain={<Heading label={t('MODULE_CND')} />}
        headerBarEnd={<CloseBtn onClick={closeModal} />}
        actionCancelLabel={showExistingBookingDetails && t("CS_COMMON_BACK")}
        actionCancelOnSubmit={() => setShowExistingBookingDetails(false)}
        hideSubmit={true}
        formId="modal-action"
      >
        <Card className="cnd-booking-popup-card">
          {showExistingBookingDetails && <ExistingBookingDetails onSubmit={onSubmit} setExistingDataSet={setExistingDataSet} />}
          <div className="cnd-booking-popup-submit-wrapper">
            {!showExistingBookingDetails && <SubmitBar label={t("USE_EXISTING_DETAILS")} onSubmit={handleExistingDetailsClick} />}
            {!showExistingBookingDetails && <SubmitBar label={t("FILL_NEW_DETAILS")} onSubmit={setData} />}
          </div>
        </Card>
      </Modal>
    </React.Fragment>
  );
};

export default BookingPopup;
