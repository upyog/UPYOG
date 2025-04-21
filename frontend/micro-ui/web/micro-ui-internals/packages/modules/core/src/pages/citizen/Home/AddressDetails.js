import React, { useState } from "react";
import { Modal, AddressDetails } from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import { useForm } from "react-hook-form";

const Heading = () => <h1 className="heading-m">Address Details</h1>;
/**
 * AddressDetailss component renders a  popup for capturing and submitting user address details.
 * It utilizes Digit's UI components and services to present a form and update the user's profile address.
 * Using the `AddressDetails` component to handle all address-related input fields such as pincode, city, locality, street name, house number, landmark, and address lines.
 * - Displaying success or error toasts based on the response.
 */
const Address = ({ actionCancelOnSubmit }) => {
  const { t } = useTranslation();
  const { data: allCities } = Digit.Hooks.useTenants();
  const { handleSubmit } = useForm();

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
  /*
 * This component renders a modal for capturing and updating user address details.
 * - Manages form state for address fields like pincode, city, locality, etc., using `useState`.
 * - Uses `updateProfile` to send updated address details to the backend via `Digit.UserService.createAddressV2`.
 * - Displays success or error toasts based on the API response.
 * - Renders a form inside a modal using `AddressDetails` for input fields and React Hook Form for submission handling.
 */
  const updateProfile = async () => {
    try {
      const stateCode = Digit.ULBService.getStateId();
      const tenantId = Digit.ULBService.getCurrentTenantId();
      const userUuid = Digit.UserService.getUser()?.info?.uuid || {};

      const requestData = {
        tenantId: tenantId,
        address: formData.addressLine1,
        address2: formData.addressLine2,
        houseNo: formData.houseNo,
        streetName: formData.streetName,
        landmark: formData.landmark,
        pinCode: formData.pincode,
        city: formData.city?.code,
        locality: formData.locality?.code,
        addressType: formData.addressType?.code,
        type: formData.addressType?.code,
      };

      const { responseInfo, address } = await Digit.UserService.createAddressV2(requestData, stateCode, userUuid);

      if (responseInfo?.status === "200") {
        Digit.UI.Toast.success(t("CORE_COMMON_PROFILE_UPDATE_SUCCESS"));
      }
    } catch (error) {
      let message = t("CORE_COMMON_PROFILE_UPDATE_ERROR");
      try {
        const errorObj = JSON.parse(error);
        if (errorObj?.message) {
          message = errorObj.message;
        }
      } catch (e) {
        // fallback to default error message
      }
      Digit.UI.Toast.error(message);
    }
  };

  return (
    <Modal
      headerBarMain={<Heading />}
      popupStyles={{
        backgroundColor: "#fff",
        position: "relative",
        maxHeight: "90vh",
        overflowY: "auto",
      }}
      actionCancelLabel={"BACK"}
      actionSaveLabel={"CS_COMMON_SUBMIT"}
      actionCancelOnSubmit={actionCancelOnSubmit}
      actionSaveOnSubmit={handleSubmit(updateProfile)}
      formId="modal-action"
    >
      <div style={{ boxShadow: "none" }}>
        <form onSubmit={handleSubmit(updateProfile)}>
          <AddressDetails t={t} formData={formData} onSelect={setFormData} />
        </form>
      </div>
    </Modal>
  );
};
export default Address;
