import React,{useState} from "react";
import { Modal, Card,CheckBox} from "@upyog/digit-ui-react-components";
import {useForm } from "react-hook-form";

const Heading = (props) => {
    return <h1 className="heading-m">{props.t("CHB_CANCEL")}</h1>;
};

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
 * CHBCancelBooking Component
 * 
 * This component is responsible for rendering the cancellation modal for community hall bookings in the CHB module.
 * It allows users to confirm their intent to cancel a booking and submit the cancellation request.
 * 
 * Props:
 * - `t`: Translation function for internationalization.
 * - `closeModal`: Function to close the cancellation modal.
 * - `actionCancelLabel`: Label for the cancel button.
 * - `actionCancelOnSubmit`: Callback function triggered when the cancel button is clicked.
 * - `actionSaveLabel`: Label for the save/confirm button.
 * - `actionSaveOnSubmit`: Callback function triggered when the save/confirm button is clicked.
 * - `onSubmit`: Callback function triggered when the form is submitted.
 * 
 * State Variables:
 * - `agree`: Boolean state to track whether the user has agreed to the terms and conditions for cancellation.
 * 
 * Functions:
 * - `setdeclarationhandler`: Toggles the `agree` state when the user checks or unchecks the agreement checkbox.
 * - `handleSubmit`: Function from React Hook Form to handle form submission.
 * 
 * Subcomponents:
 * - `Heading`: Renders the heading for the cancellation modal.
 * - `Close`: Renders the close button icon for the modal.
 * - `CloseBtn`: Wrapper component for the close button, with an `onClick` handler to close the modal.
 * 
 * Logic:
 * - Displays a modal with a cancellation confirmation message and agreement checkbox.
 * - Disables the save/confirm button until the user agrees to the terms and conditions.
 * - Calls the appropriate callback functions when the cancel or save/confirm buttons are clicked.
 * 
 * Returns:
 * - A modal component with a heading, agreement checkbox, and action buttons for canceling or confirming the booking cancellation.
 */
const CHBCancelBooking = ({ t, closeModal, actionCancelLabel, actionCancelOnSubmit, actionSaveLabel, actionSaveOnSubmit,onSubmit }) => {
    const [agree, setAgree] = useState(false);
    const setdeclarationhandler = () => {
      setAgree(!agree);
    };

    const {handleSubmit } = useForm();

    return (
        <Modal
            headerBarMain={<Heading t={t}/>}
            headerBarEnd={<CloseBtn onClick={closeModal} />}
            actionCancelLabel={t(actionCancelLabel)}
            actionCancelOnSubmit={actionCancelOnSubmit}
            actionSaveLabel={t(actionSaveLabel)}
            actionSaveOnSubmit={handleSubmit(actionSaveOnSubmit)}
            isDisabled={!agree}
            formId="modal-action"
        >
            <Card style={{ boxShadow: "none" }}>
                <form onSubmit={handleSubmit(onSubmit)}>
                <CheckBox
                    label={t("CHB_CONFIRM_CANCEL_BOOKING")}
                    onChange={setdeclarationhandler}
                    style={{ height: "auto" }}
                    />
                </form>
            </Card>
        </Modal>
    );
};
export default CHBCancelBooking;
