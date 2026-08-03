import React, { useState } from "react";
import { Modal, AddressDetails } from "@nudmcdgnpm/digit-ui-react-components";

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
 * AddressPopup Component
 * 
 * This component renders a modal popup for capturing address details.
 * 
 * Props:
 * - t: Translation function for internationalization.
 * - isOpen: Boolean to control the visibility of the modal.
 * - onClose: Function to handle the closing of the modal.
 * - onSubmit: Function to handle the submission of the address form data.
 * 
 * State:
 * - formData: Object to store the address details entered by the user.
 * 
 * Child Components:
 * - Modal: A reusable modal component for displaying the popup.
 * - AddressDetails: A component for rendering and managing address input fields.
 * 
 * Usage:
 * - This component is used to collect and submit address details in a structured format.
 */
const AddressPopup = ({ t, isOpen, onClose, onSubmit }) => {
  const [formData, setFormData] = useState({
    addressType: "",
    pincode: "",
    city: "",
    locality: "",
    streetName: "",
    houseNo: "",
    landmark: "",
    addressLine1: "",
    addressLine2: "",
  });


  return (
    <Modal
      headerBarMain={<h1 className="heading-m">{t("PGR_AI_FILL_ADDRESS_DETAILS")}</h1>}
      headerBarEnd={<CloseBtn onClick={onClose} />}
      actionSaveLabel={t("CS_COMMON_SUBMIT")}
      actionCancelLabel={t("BACK")}
      actionCancelOnSubmit={onClose}
      actionSaveOnSubmit={(e) => {
        e?.preventDefault();
        e?.stopPropagation();
        onSubmit(formData);
      }}
      popupStyles={{
        backgroundColor: "#fff",
        position: "relative",
        transform: "scale(0.65)",
        overflowY: "hidden",
      }}
    >
      <div style={{ boxShadow: "none" }}>
        <AddressDetails t={t} formData={formData} onSelect={setFormData} />
      </div>
    </Modal>
  );
};

export default AddressPopup;