import React,{useState,useEffect} from "react";
import { Modal, Card,SubmitBar, CheckBox} from "@upyog/digit-ui-react-components";
import { useHistory } from "react-router-dom";
// import { demandPayloadData } from "../utils";

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

const RenewPopup = ({ t, closeModal, onSubmit, application }) => {
    console.log(" ----- Renew POP coming ")
    const mutation = Digit.Hooks.sv.useCreateDemand();
    const history = useHistory();

    const Heading = (props) => {
        return <h1 className="heading-m">{props.label}</h1>;
    };

    const proceedWithApplication = () => {
        history.push(`renew-application/info`)
    }


    const onRedirectedToCheckPage = () => {
        console.log(' testing coming compilter')
        // try {
        //     let formdata = demandPayloadData(application)
        //     mutation.mutate(formdata);
        // } catch (err) {
        //     console.error("Error in Mutation:", {
        //         message: err.message,
        //         stack: err.stack,
        //         name: err.name
        //     });
        // }
        history.push("renew-application/check/makePayment");
    }

    return (
        <React.Fragment>
        <Modal
            headerBarMain={<Heading label={"SV_WANT_TO_EDIT"}/>}
            headerBarEnd={<CloseBtn onClick={closeModal} />}
            actionCancelLabel={t("CS_COMMON_BACK")}
            actionCancelOnSubmit={closeModal}
            hideSubmit={true}
            formId="modal-action"
        >
            <Card style={{ boxShadow: "none" }}>
            <div style={{
                    display: 'flex',
                    justifyContent: 'center',
                    gap: '20px', 
                    flexDirection: 'column', 
                    alignItems: 'center', 
                }}>
                {<SubmitBar label={t("SV_YES_PROCEED_WITH_APPLICATION")} onSubmit={proceedWithApplication} />}
                {<SubmitBar label={t("SV_NO_PROCEED_PAYEMNT")} onSubmit={onRedirectedToCheckPage} />}
            </div>
            </Card>
        </Modal>
    </React.Fragment>
    );
};
export default RenewPopup;