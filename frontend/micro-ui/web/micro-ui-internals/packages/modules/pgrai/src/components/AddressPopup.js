import React, { useState } from "react";
import { Modal, AddressDetails } from "@nudmcdgnpm/digit-ui-react-components";


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
      actionSaveLabel={t("CS_COMMON_SUBMIT")}
      actionCancelLabel={t("BACK")}
      actionCancelOnSubmit={onClose}
      actionSaveOnSubmit={() => onSubmit(formData)}
      popupStyles={{
        backgroundColor: "#fff",
        position: "relative",
        maxHeight: "90vh",
        overflowY: "auto",
      }}
    >
      <div style={{ boxShadow: "none" }}>
        <AddressDetails t={t} formData={formData} onSelect={setFormData} />
      </div>
    </Modal>
  );
};

export default AddressPopup;