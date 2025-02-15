import React, { useState, useEffect } from "react";
import { Modal, Card, SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";
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
// The BookingPopup component renders a modal popup for booking functionality.
// It provides options to either use existing booking details or fill in new details.

const BookingPopup = ({ t, closeModal, onSubmit, setExistingDataSet, Searchdata }) => {
  const [showExistingBookingDetails, setShowExistingBookingDetails] = useState(false);
  const [isDataSet, setIsDataSet] = useState(false); // State to track if data has been set
  // const tenantId = Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId();
  const handleExistingDetailsClick = () => {
    setShowExistingBookingDetails(true); // Show the BookingSearchDetails component
  };
  const Heading = (props) => {
    return showExistingBookingDetails && <h1 className="heading-m">{props.t("WT_MY_BOOKINGS_HEADER")}</h1>;
  };

  const setwtData = () => {
    
    const newSessionData = {
      
    };
    setExistingDataSet(newSessionData);
    setIsDataSet(true); // Set the flag to true after data is set
  };

  useEffect(() => {
    if (isDataSet) {
      onSubmit();
      setIsDataSet(false); // Reset the flag after data is submitted
    }
  }, [isDataSet, onSubmit]);

  return (
    <React.Fragment>
      <Modal
        headerBarMain={<Heading t={t} />}
        headerBarEnd={<CloseBtn onClick={closeModal} />}
        actionCancelLabel={showExistingBookingDetails && t("CS_COMMON_BACK")}
        actionCancelOnSubmit={() => setShowExistingBookingDetails(false)}
        hideSubmit={true}
        formId="modal-action"
      >
        <Card style={{ boxShadow: "none" }}>
          {showExistingBookingDetails && (
            <ExistingBookingDetails onSubmit={onSubmit} setExistingDataSet={setExistingDataSet} Searchdata={Searchdata} />
          )}
          <div
            style={{
              display: "flex",
              justifyContent: "center",
              gap: "20px",
              flexDirection: "column",
              alignItems: "center",
            }}
          >
            {!showExistingBookingDetails && <SubmitBar label={t("USE_EXISTING_DETAILS")} onSubmit={handleExistingDetailsClick} />}
            {!showExistingBookingDetails && <SubmitBar label={t("FILL_NEW_DETAILS")} onSubmit={setwtData} />}
          </div>
        </Card>
      </Modal>
    </React.Fragment>
  );
};
export default BookingPopup;
