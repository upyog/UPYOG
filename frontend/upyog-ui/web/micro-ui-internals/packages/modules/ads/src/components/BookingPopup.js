import React, { useState, useEffect } from "react";
import { Modal, Card, SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";
import { ExistingBookingDetails } from "./ExistingBookingDetails";
import { getSlotSearchCriteria } from "../utils";

const Close = () => <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="#FFFFFF">
        <path d="M0 0h24v24H0V0z" fill="none" />
        <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12 19 6.41z" />
    </svg>;
const CloseBtn = props => {
  return <div className="icon-bg-secondary" onClick={props.onClick}>
            <Close />
        </div>;
};
/**
 * BookingPopup component renders a modal popup for advertisement booking. It allows users 
 * to either use existing booking details or enter new details. 
 * - `showExistingBookingDetails`: Controls whether to display existing booking details.
 * - `setchbData`: Fetches slot availability data and updates the session state.
 * - `useEffect`: Calls `onSubmit` automatically after data is set.
 * - Uses `Digit.Hooks.ads.useADSSlotSearch` for slot search functionality.
 * - Includes two action buttons: "Use Existing Details" and "Fill New Details".
 */

const BookingPopup = ({
  t,
  closeModal,
  onSubmit,
  setExistingDataSet,
  Searchdata,
  selectedLocation // Added selectedLocation to map raw location codes instead of translated strings
}) => {
  const [showExistingBookingDetails, setShowExistingBookingDetails] = useState(false);
  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId();
  const handleExistingDetailsClick = () => {
    setShowExistingBookingDetails(true); // Show the BookingSearchDetails component
  };
  const Heading = props => {
    return showExistingBookingDetails && <h1 className="heading-m">{props.t("ADS_MY_BOOKINGS_HEADER")}</h1>;
  };
  // Slot search data for Ads (Advertisement)
  const slotSearchData = Digit.Hooks.ads.useADSSlotSearch();

  const formdata = {
    advertisementSlotSearchCriteria: getSlotSearchCriteria(Searchdata, tenantId, selectedLocation)
  };
  const setchbData = async () => {
    const result = await slotSearchData.mutateAsync(formdata);
    /* timerValue is resolved directly from top-level of response payload per backend contract */
    const timerValue = result?.timerValue;
    const newSessionData = {
      timervalue: {
        timervalue: timerValue || 0,
        /* Store lock start timestamp to calculate elapsed offset during navigation remounts */
        timerStartedAt: Date.now()
      },
      draftId: result?.draftId || ""
    };
    setExistingDataSet(newSessionData);
    /* Propagate sessionData synchronously to avoid React state batching race conditions in goNext */
    onSubmit(newSessionData);
  };
  return <React.Fragment>
        <Modal headerBarMain={<Heading t={t} />} headerBarEnd={<CloseBtn onClick={closeModal} />} actionCancelLabel={showExistingBookingDetails && t("CS_COMMON_BACK")} actionCancelOnSubmit={() => setShowExistingBookingDetails(false)} hideSubmit={true} formId="modal-action">
            <Card className="ads-auto-41">
            {showExistingBookingDetails && <ExistingBookingDetails onSubmit={onSubmit} setExistingDataSet={setExistingDataSet} Searchdata={Searchdata} selectedLocation={selectedLocation} />}
            <div className="ads-auto-42">
                    {!showExistingBookingDetails && <SubmitBar label={t("USE_EXISTING_DETAILS")} onSubmit={handleExistingDetailsClick} />}
                    {!showExistingBookingDetails && <SubmitBar label={t("FILL_NEW_DETAILS")} onSubmit={setchbData} />}
                </div>
            </Card>
        </Modal>
    </React.Fragment>;
};
export default BookingPopup;
