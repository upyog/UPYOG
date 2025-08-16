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
 * ADSCancelBooking Component d
 * This component is responsible for handling the cancellation of an ADS booking. 
 * This component ensures that the user explicitly acknowledges the cancellation before proceeding, enhancing user experience and preventing accidental cancellations.
 */

const ADSCancelBooking = ({ t, closeModal, actionCancelLabel, actionCancelOnSubmit, actionSaveLabel, actionSaveOnSubmit,onSubmit }) => {
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
                    label={t("ADS_CONFIRM_CANCEL_BOOKING")}
                    onChange={setdeclarationhandler}
                    style={{ height: "auto" }}
                    />
                </form>
            </Card>
        </Modal>
    );
};
export default ADSCancelBooking;
