import React,{useState,useEffect} from "react";
import { Modal, Card,SubmitBar} from "@upyog/digit-ui-react-components";
import {useForm } from "react-hook-form";
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
 * BookingPopup Component
 * 
 * This component is responsible for rendering a popup modal for managing community hall bookings in the CHB module.
 * It allows users to view existing booking details, submit new booking requests, and manage booking-related actions.
 * 
 * Props:
 * - `t`: Translation function for internationalization.
 * - `closeModal`: Function to close the popup modal.
 * - `onSubmit`: Callback function triggered when the booking form is submitted.
 * - `setExistingDataSet`: Function to update the existing dataset with fetched booking details.
 * - `Searchdata`: Object containing search parameters for fetching booking details.
 * 
 * State Variables:
 * - `showExistingBookingDetails`: Boolean state to manage the visibility of the existing booking details component.
 * - `isDataSet`: Boolean state to track whether the booking data has been set.
 * 
 * Functions:
 * - `Close`: Renders the close button icon for the modal.
 * - `CloseBtn`: Wrapper component for the close button, with an `onClick` handler to close the modal.
 * - `handleExistingDetailsClick`: Toggles the visibility of the existing booking details component.
 * - `Heading`: Renders the heading for the popup modal, displayed only when `showExistingBookingDetails` is true.
 * 
 * Logic:
 * - Displays a modal with options to view existing booking details or submit a new booking request.
 * - Manages the visibility of the existing booking details component using the `showExistingBookingDetails` state.
 * - Tracks whether booking data has been set using the `isDataSet` state.
 * - Integrates with the `Digit.Hooks.chb.useChbSlotSearch` hook (commented out in the current code) to fetch slot availability data.
 * 
 * Returns:
 * - A modal component with options to view existing booking details or submit a new booking request.
 * - Includes a close button to hide the modal.
 */
const BookingPopup = ({ t, closeModal,onSubmit,setExistingDataSet,Searchdata }) => {
   
    const [showExistingBookingDetails, setShowExistingBookingDetails] = useState(false);
    const tenantId = Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId();
    const handleExistingDetailsClick = () => {
        setShowExistingBookingDetails(true); // Show the BookingSearchDetails component
    };
    const Heading = (props) => {
        return showExistingBookingDetails && <h1 className="heading-m">{props.t("CHB_MY_APPLICATION_HEADER")}</h1>;
    };
    const [isDataSet, setIsDataSet] = useState(false); // State to track if data has been set
    // Define the slot_search hook to refetch data on search
    //   const {refetch} = Digit.Hooks.chb.useChbSlotSearch({
    //     tenantId:tenantId,
    //     filters: {
    //       communityHallCode:Searchdata.communityHallCode,
    //       bookingStartDate:Searchdata.bookingStartDate,
    //       bookingEndDate:Searchdata.bookingEndDate,
    //       hallCode:Searchdata.hallCode,
    //       isTimerRequired:true,
    //     }
    //   });
    
      const setchbData = () => {
        // const result =refetch();
        const newSessionData = {
          timervalue:{
            // timervalue:result?.timerValue || 10
            timervalue:1800
          }
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
            hideSubmit={true}
            actionCancelLabel={showExistingBookingDetails && t("CS_COMMON_BACK")}
            actionCancelOnSubmit={() => setShowExistingBookingDetails(false)}
            formId="modal-action"
        >
            <Card style={{ boxShadow: "none" }}>
            {showExistingBookingDetails && <ExistingBookingDetails onSubmit={onSubmit} setExistingDataSet={setExistingDataSet} Searchdata={Searchdata} setShowExistingBookingDetails={setShowExistingBookingDetails} />}
            <div style={{
                    display: 'flex',
                    justifyContent: 'center',
                    gap: '20px', // Adjust gap as needed
                    flexDirection: 'column', // Vertically stack the buttons
                    alignItems: 'center', // Center the buttons horizontally
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
