import React,{useState,useEffect} from "react";
import { Modal, Card,SubmitBar} from "@upyog/digit-ui-react-components";
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
/**
 * BookingPopup component renders a modal popup for advertisement booking. It allows users 
 * to either use existing booking details or enter new details. 
 * - `showExistingBookingDetails`: Controls whether to display existing booking details.
 * - `setchbData`: Fetches slot availability data and updates the session state.
 * - `useEffect`: Calls `onSubmit` automatically after data is set.
 * - Uses `Digit.Hooks.ads.useADSSlotSearch` for slot search functionality.
 * - Includes two action buttons: "Use Existing Details" and "Fill New Details".
 */

const BookingPopup = ({ t, closeModal,onSubmit,setExistingDataSet,Searchdata }) => {
   
    const [showExistingBookingDetails, setShowExistingBookingDetails] = useState(false);
    const [isDataSet, setIsDataSet] = useState(false); // State to track if data has been set
    const tenantId = Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId();
    const handleExistingDetailsClick = () => {
        setShowExistingBookingDetails(true); // Show the BookingSearchDetails component
    };
    const Heading = (props) => {
        return showExistingBookingDetails && <h1 className="heading-m">{props.t("ADS_MY_BOOKINGS_HEADER")}</h1>;
    };
     // Slot search data for Ads (Advertisement)
    const slotSearchData = Digit.Hooks.ads.useADSSlotSearch();

    // Prepare form data for Advertisement Service
    const formdata = {
        advertisementSlotSearchCriteria: Searchdata.map((item) => ({
            bookingId: "",
            addType: item?.addTypeCode,
            bookingStartDate: item?.bookingDate,
            bookingEndDate: item?.bookingDate,
            faceArea: item?.faceAreaCode,
            tenantId: tenantId,
            location: item?.location,
            nightLight: item?.nightLight,
            isTimerRequired: true,
          })),
    };
     const setchbData = async() => {
            const result=await slotSearchData.mutateAsync(formdata);
            const timerValue = result?.advertisementSlotAvailabiltityDetails[0].timerValue;
            const newSessionData = {
              timervalue:{
                timervalue:timerValue || 10
              },
             draftId:result?.draftId || ""
            };
            setExistingDataSet(newSessionData);
            setIsDataSet(true);  // Set the flag to true after data is set
          };
        
          useEffect(() => {
            if (isDataSet) { // If data is set, call onSubmit
              onSubmit();
              setIsDataSet(false);  // Reset the flag after onSubmit is called
            }
          }, [isDataSet, onSubmit]);

    return (
        <React.Fragment>
        <Modal
            headerBarMain={<Heading t={t}/>}
            headerBarEnd={<CloseBtn onClick={closeModal} />}
            actionCancelLabel={showExistingBookingDetails && t("CS_COMMON_BACK")}
            actionCancelOnSubmit={() => setShowExistingBookingDetails(false)}
            hideSubmit={true}
            formId="modal-action"
        >
            <Card style={{ boxShadow: "none" }}>
            {showExistingBookingDetails && <ExistingBookingDetails onSubmit={onSubmit} setExistingDataSet={setExistingDataSet} Searchdata={Searchdata} />}
            <div style={{
                    display: 'flex',
                    justifyContent: 'center',
                    gap: '20px', 
                    flexDirection: 'column', 
                    alignItems: 'center', 
                }}>
                    {!showExistingBookingDetails && <SubmitBar label={t("USE_EXISTING_DETAILS")} onSubmit={handleExistingDetailsClick} />}
                    {!showExistingBookingDetails &&<SubmitBar label={t("FILL_NEW_DETAILS")} onSubmit={setchbData} />}
                </div>
            </Card>
        </Modal>
    </React.Fragment>
    );
};
export default BookingPopup;