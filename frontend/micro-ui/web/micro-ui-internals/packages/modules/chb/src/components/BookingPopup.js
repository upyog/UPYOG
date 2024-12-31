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
