import React,{useEffect,useState} from "react";
import { Modal, Card, FormComposer} from "@nudmcdgnpm/digit-ui-react-components";
import { LocationDetails } from "./LocationDetails";

/* Modal File / Popup file which will render when click on Deposit direct to centre and inside 
it will render pincode andf locality fields using this component - LocationDetails
*/
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

const LocationPopup = ({ t, closeModal }) => {
    const [config, setConfig] = useState({});


     useEffect(() => {
        setConfig(
            LocationDetails({
            t
          })
        );
      }, [t]);
    const Heading = (props) => {
        return <h1 className="heading-m">{props.label}</h1>;
    };

    return (
        <React.Fragment>
        <Modal
            headerBarMain={<Heading label={"CND_DEPOSIT_DIRECT_CENTRE"}/>}
            headerBarEnd={<CloseBtn onClick={closeModal} />}
            actionCancelLabel={t("CS_COMMON_BACK")}
            actionCancelOnSubmit={closeModal}
            hideSubmit={true}
            formId="modal-action"
        >
    
        <FormComposer
            config={config.form}
            noBoxShadow
            inline
            childrenAtTheBottom
            formId="modal-action"
            />
        </Modal>
    </React.Fragment>
    );
};
export default LocationPopup;