import React, { useEffect, useState } from "react";
import { FormStep, TextInput, CardLabel, InfoBannerIcon, Dropdown, TextArea } from "@upyog/digit-ui-react-components";
import { useLocation } from "react-router-dom";
import Timeline from "../components/InventoryTimeLine";
import { Controller, useForm } from "react-hook-form";
import FormGridWrapper from "../utils/FormGridWrapper";

const InventoryVendor = ({ t, config, onSelect, userType, formData }) => {
  console.log("coming data:- ", formData);
  const [inventoryVendor, setInventoryVendor] = useState(formData?.inventoryVendor ? formData?.inventoryVendor : {});
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateTenantId = Digit.ULBService.getStateId();

  const { control } = useForm();
  const { pathname: url } = useLocation();
  let index = 0;
  let validation = {};

  const goNext = () => {
    let owner = formData.inventoryVendor && formData.inventoryVendor[index];
    inventoryVendor.owner = owner;
    onSelect(config.key, inventoryVendor, false, index);
  };

  const onSkip = () => onSelect();
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
      return updatedData;
    });
  };

  return (
    <React.Fragment>
      {window.location.href.includes("/employee") ? <Timeline currentStep={1} /> : null}
      <FormStep config={config} onSelect={goNext} onSkip={onSkip} t={t}>
        <FormGridWrapper>
          <div>
            <CardLabel>
              {t("INV_VENDOR_NAME")} <span style={{ color: "red" }}>*</span>
            </CardLabel>
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

          <div>
            <CardLabel>
              {t("INV_CONTACT_PERSON")} <span style={{ color: "red" }}>*</span>
            </CardLabel>
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
              {...(validation = {
                isRequired: true,
                pattern: "^[a-zA-Z0-9/-]*$",
                type: "text",
                title: t("PT_NAME_ERROR_MESSAGE"),
              })}
            />
          </div>

          <div>
            <CardLabel>
              {t("INV_CONTACT_NUMBER")} <span style={{ color: "red" }}>*</span>
            </CardLabel>
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
              {...(validation = {
                isRequired: true,
                pattern: "^[a-zA-Z0-9/-]*$",
                type: "text",
                title: t("PT_NAME_ERROR_MESSAGE"),
              })}
            />
          </div>

          <div>
            <CardLabel>
              {t("INV_EMAIL_ADDRESS")} <span style={{ color: "red" }}>*</span>
            </CardLabel>
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
              {...(validation = {
                isRequired: true,
                pattern: "^[a-zA-Z0-9/-]*$",
                type: "text",
                title: t("PT_NAME_ERROR_MESSAGE"),
              })}
            />
          </div>

          <div>
            <CardLabel>
              {t("INV_GSTN")} <span style={{ color: "red" }}>*</span>
            </CardLabel>
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
              {...(validation = {
                isRequired: true,
                pattern: "^[a-zA-Z0-9/-]*$",
                type: "text",
                title: t("PT_NAME_ERROR_MESSAGE"),
              })}
            />
          </div>
          <div>
            <CardLabel>
              {t("INV_PAN")} <span style={{ color: "red" }}>*</span>
            </CardLabel>
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
              {...(validation = {
                isRequired: true,
                pattern: "^[a-zA-Z0-9/-]*$",
                type: "text",
                title: t("PT_NAME_ERROR_MESSAGE"),
              })}
            />
          </div>
          <div>
            <CardLabel>
              {t("INV_OTHER_DETAILS")} <span style={{ color: "red" }}>*</span>
            </CardLabel>
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              name="vendorAddress"
              value={inventoryVendor?.vendorAddress || ""}
              placeholder={t("Enter address")}
              onChange={handleInputChange}
              ValidationRequired={false}
              {...(validation = {
                isRequired: true,
                pattern: "^[a-zA-Z0-9/-]*$",
                type: "text",
                title: t("PT_NAME_ERROR_MESSAGE"),
              })}
            />
          </div>
        </FormGridWrapper>
      </FormStep>
    </React.Fragment>
  );
};

export default InventoryVendor;
