import React, { useState, useEffect} from "react";
import { Modal, AddressDetails } from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import { useForm } from "react-hook-form";

const Heading = ({t}) => <h1 className="heading-m">{t("FILL_ADDRESS_DETAILS")}</h1>;
/**
 * AddressDetailss component renders a  popup for capturing and submitting user address details.
 * It utilizes Digit's UI components and services to present a form and update the user's profile address.
 * Using the `AddressDetails` component to handle all address-related input fields such as pincode, city, locality, street name, house number, landmark, and address lines.
 * - Displaying success or error toasts based on the response.
 */
const Address = ({ address, actionCancelOnSubmit, isEdit }) => {
  const { t } = useTranslation();
  const { data: allCities } = Digit.Hooks.useTenants();
  const { handleSubmit } = useForm();
  const [showToast, setShowToast] = useState(null);
  const [formData, setFormData] = useState({
    addressType: address?.addressType||"",
    pincode: address?.pinCode||"",
    city: address?.city||"",
    locality: address?.locality||"",
    streetName: address?.streetName||"",
    houseNo: address?.houseNumber||"",
    landmark: address?.landmark||"",
    addressLine1: address?.address||"",
    addressLine2: address?.address2||""
  }); 
  /*
 * This component renders a modal for capturing and updating user address details.
 * - Manages form state for address fields like pincode, city, locality, etc., using `useState`.
 * - Uses `updateProfile` to send updated address details to the backend via `Digit.UserService.createAddressV2`.
 * - Displays success or error toasts based on the API response.
 * - Renders a form inside a modal using `AddressDetails` for input fields and React Hook Form for submission handling.
 */
    // timer for toast
  useEffect(() => {
    if (showToast) {
      const timer = setTimeout(() => {
        setShowToast(null);
      }, 1000);
      return () => clearTimeout(timer);
    }
  }, [showToast]);

  const updateProfile = async () => {
    try {
      const stateCode = Digit.ULBService.getStateId();
      const tenantId = Digit.ULBService.getCurrentTenantId();
      const userUuid = Digit.UserService.getUser()?.info?.uuid || {};

      const requestData = {
        tenantId: tenantId,
        address: formData.addressLine1,
        address2: formData.addressLine2,
        houseNumber: formData.houseNo,
        streetName: formData.streetName,
        landmark: formData.landmark,
        pinCode: formData.pincode,
        city: formData.city?.city?.name,
        locality: formData.locality?.i18nKey,
        addressType: formData.addressType?.code,
        type: formData.addressType?.code,
      };

      const { responseInfo, address } = await Digit.UserService.createAddressV2(requestData, stateCode, userUuid);
      if (responseInfo?.status === "201") {
        actionCancelOnSubmit();
      }
    } catch (error) {
      let message = t("CORE_COMMON_PROFILE_UPDATE_ERROR");
      try {
        const errorObj = JSON.parse(error);
        message = errorObj?.message || message;
      } catch (e) {}
      setShowToast({ 
        error: true,
        label: message
      });
    }
  };


  const updateAddress = async () => {
    try {
      const stateCode = Digit.ULBService.getStateId();
      const tenantId = Digit.ULBService.getCurrentTenantId();

      const requestUpdatedData = {
        tenantId: tenantId,
        address: formData.addressLine1,
        address2: formData.addressLine2,
        houseNumber: formData.houseNo,
        streetName: formData.streetName,
        landmark: formData.landmark,
        pinCode: formData.pincode,
        city: formData.city?.city?.name,
        locality: formData.locality?.i18nKey,
        addressType: formData.addressType?.code,
        type: formData.addressType?.code,
        id: address?.id,
        userId: address?.userId,
        lastModifiedBy: null,
        lastModifiedDate: null
      };

      const { responseInfo } = await Digit.UserService.updateAddressV2(requestUpdatedData, stateCode,);
      if (responseInfo?.status === "200") {
        actionCancelOnSubmit();
      }
    } catch (error) {
      let message = t("CORE_COMMON_PROFILE_UPDATE_ERROR");
      try {
        const errorObj = JSON.parse(error);
        message = errorObj?.message || message;
      } catch (e) {}
      setShowToast({ 
        error: true,
        label: message
      });
    }
  };

  return (
    <Modal
      headerBarMain={<Heading t={t} />}
      popupStyles={{
        backgroundColor: "#fff",
        position: "relative",
        maxHeight: "90vh",
        overflowY: "auto",
      }}
      actionSaveLabel={isEdit ?("CS_COMMON_UPDATE") :t("CS_COMMON_SUBMIT")}
      actionCancelLabel={t("BACK")}
      actionCancelOnSubmit={actionCancelOnSubmit}
      actionSaveOnSubmit={handleSubmit(isEdit?updateAddress:updateProfile)}
      formId="modal-action"
      setError={setShowToast}
      error={showToast?.label}
    >
      <div style={{ boxShadow: "none" }}>
          <AddressDetails t={t} formData={formData} onSelect={setFormData} isEdit={isEdit} />
      </div>
    </Modal>
  );
};
export default Address;
