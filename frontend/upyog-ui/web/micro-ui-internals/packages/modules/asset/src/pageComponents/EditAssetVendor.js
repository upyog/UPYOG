import React, { useEffect, useState } from "react";
import { TextInput, CardLabel } from "@upyog/digit-ui-react-components";
import FormGridWrapper from "../utils/FormGridWrapper";

const EditAssetVendor = ({ t, config, onSelect, formData, onDataChange }) => {
  const vendorData = formData?.editAssetVendor || formData?.inventoryVendor?.[0] || {};
  const [inventoryVendor, setInventoryVendor] = useState(vendorData);

  const handleInputChange = (e, name) => {
    let fieldName, value;

    // Case 1: TextInput (event object with target)
    if (e?.target) {
      fieldName = e.target.name;
      value = e.target.value;
    }
    // Case 2: Dropdown (value passed + explicit name)
    else {
      fieldName = name;
      value = e;
    }

    setInventoryVendor((prevData) => {
      const updatedData = { ...prevData, [fieldName]: value };

      // Update FormComposer data using onSelect like EditAssetRegistry
      if (onSelect && config) {
        onSelect(config.key, { editAssetVendor: updatedData });
      }

      return updatedData;
    });
  };
  return (
    <div>
      <FormGridWrapper>
        <div key="vendorName">
          <CardLabel> {t("INV_VENDOR_NAME")} </CardLabel>
          <TextInput
            t={t}
            type="text"
            isMandatory={false}
            optionKey="i18nKey"
            name="vendorName"
            value={inventoryVendor.vendorName || ""}
            placeholder={t("Enter Vendor Name")}
            onChange={handleInputChange}
            ValidationRequired={false}
            validation={{
              isRequired: true,
              pattern: "^[a-zA-Z0-9/-]*$",
              type: "text",
              title: t("PT_NAME_ERROR_MESSAGE"),
            }}
          />
        </div>

        <div key="contactPerson">
          <CardLabel>{t("INV_CONTACT_PERSON")}</CardLabel>
          <TextInput
            t={t}
            type={"text"}
            isMandatory={false}
            optionKey="i18nKey"
            name="contactPerson"
            value={inventoryVendor.contactPerson || ""}
            placeholder={t("Enter Person Name")}
            ValidationRequired={false}
            onChange={handleInputChange}
          />
        </div>

        <div key="contactNumber">
          <CardLabel>{t("INV_CONTACT_NUMBER")}</CardLabel>
          <TextInput
            t={t}
            type={"text"}
            isMandatory={false}
            optionKey="i18nKey"
            name="contactNumber"
            value={inventoryVendor.contactNumber || ""}
            placeholder={t("Enter Number")}
            ValidationRequired={false}
            onChange={handleInputChange}
          />
        </div>

        <div key="contactEmail">
          <CardLabel>{t("INV_EMAIL_ADDRESS")}</CardLabel>
          <TextInput
            t={t}
            type={"text"}
            isMandatory={false}
            optionKey="i18nKey"
            name="contactEmail"
            value={inventoryVendor.contactEmail || ""}
            placeholder={t("Enter email")}
            ValidationRequired={false}
            onChange={handleInputChange}
          />
        </div>

        <div key="gstin">
          <CardLabel>{t("INV_GSTN")}</CardLabel>
          <TextInput
            t={t}
            type={"text"}
            isMandatory={false}
            optionKey="i18nKey"
            name="gstin"
            value={inventoryVendor.gstin || ""}
            placeholder={t("Enter GSTN")}
            onChange={handleInputChange}
            ValidationRequired={false}
          />
        </div>
        <div key="pan">
          <CardLabel>{t("INV_PAN")}</CardLabel>
          <TextInput
            t={t}
            type={"text"}
            isMandatory={false}
            optionKey="i18nKey"
            name="pan"
            value={inventoryVendor.pan || ""}
            placeholder={t("Enter PAN")}
            onChange={handleInputChange}
            ValidationRequired={false}
          />
        </div>
        <div key="vendorAddress">
          <CardLabel>{t("INV_OTHER_DETAILS")}</CardLabel>
          <TextInput
            t={t}
            type={"text"}
            isMandatory={false}
            optionKey="i18nKey"
            name="vendorAddress"
            value={inventoryVendor?.vendorAddress || vendorData?.vendorAddress || ""}
            placeholder={t("Enter address")}
            onChange={handleInputChange}
            ValidationRequired={false}
          />
        </div>
        <div key="vendorNumber">
          <CardLabel>{t("INV_UNIQUE_VENDOR_IDENTIFICATION_NO")}</CardLabel>
          <TextInput
            t={t}
            type={"text"}
            isMandatory={false}
            optionKey="i18nKey"
            name="vendorNumber"
            value={inventoryVendor.vendorNumber || ""}
            disabled={true}
            readOnly={true}
            onChange={handleInputChange}
            ValidationRequired={false}
          />
        </div>
      </FormGridWrapper>
    </div>
  );
};

export default EditAssetVendor;
