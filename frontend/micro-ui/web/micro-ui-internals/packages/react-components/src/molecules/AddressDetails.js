import React, { useState } from "react";
import { useForm, Controller } from "react-hook-form";
import CardLabel from "../atoms/CardLabel";
import TextInput from "../atoms/TextInput";
import TextArea from "../atoms/TextArea";
import Dropdown from "../atoms/Dropdown";

/**
 * AddressDetails component renders a form for capturing address details including house number, house name, street name, address lines, landmark, city, locality, and pincode.
 */
const AddressDetails = ({
  t,
  houseNumber,
  setHouseNumber,
  houseName,
  setHouseName,
  streetName,
  setStreetName,
  addressLine1,
  setAddressLine1,
  addressLine2,
  setAddressLine2,
  landmark,
  setLandmark,
  pincode,
  setPincode,
  setCity,
  city,
  locality,
  setLocality,
  allCities,
}) => {
  const { data: fetchedLocalities, isLoading: isLoadingLocalities } = Digit.Hooks.useBoundaryLocalities(
    city?.code,
    "revenue",
    {
      enabled: !!city,
    },
    t
  );

  let structuredLocality = [];
  fetchedLocalities &&
    fetchedLocalities.map((local, index) => {
      structuredLocality.push({ i18nKey: local.i18nkey, code: local.code, label: local.label, area: local.area, boundaryNum: local.boundaryNum });
    });
  const { control } = useForm(); 
  const setAddressPincode = (e) => {
   
    const newPincode = e.target.value.replace(/\D/g, "").slice(0, 6); // only allow numbers and limit to 6 digits
    setPincode(newPincode);
  };
  return (
    <div>
      <CardLabel>
        {`${t("HOUSE_NO")}`} <span className="check-page-link-button">*</span>
      </CardLabel>
      <TextInput
        t={t}
        type={"text"}
        isMandatory={false}
        optionKey="i18nKey"
        name="houseNo"
        value={houseNumber}
        placeholder={"Enter House No"}
        onChange={(e) => {
          setHouseNumber(e.target.value);
        }}
        ValidationRequired={true}
        validation={{
          isRequired: true,
          pattern: "^[a-zA-Z0-9 ,\\-]+$",
          type: "text",
          title: t("HOUSE_NO_ERROR_MESSAGE"),
        }}
      />

      <CardLabel>{`${t("HOUSE_NAME")}`}</CardLabel>
      <TextInput
        t={t}
        type={"text"}
        isMandatory={false}
        optionKey="i18nKey"
        name="houseName"
        value={houseName}
        placeholder={"Enter House Name"}
        onChange={(e) => {
          setHouseName(e.target.value);
        }}
        ValidationRequired={false}
      />

      <CardLabel>
        {`${t("STREET_NAME")}`} <span className="check-page-link-button">*</span>
      </CardLabel>
      <TextInput
        t={t}
        type={"text"}
        isMandatory={false}
        optionKey="i18nKey"
        name="streetName"
        value={streetName}
        placeholder={"Enter Street Name"}
        onChange={(e) => {
          setStreetName(e.target.value);
        }}
        ValidationRequired={true}
        validation={{
          pattern: "^[a-zA-Z0-9 ,\\-]+$",
          type: "text",
          title: t("STREET_NAME_ERROR_MESSAGE"),
        }}
      />

      <CardLabel>
        {`${t("ADDRESS_LINE1")}`} <span className="check-page-link-button">*</span>
      </CardLabel>
      <TextInput
        t={t}
        type={"text"}
        isMandatory={false}
        optionKey="i18nKey"
        name="addressLine1"
        value={addressLine1}
        placeholder={"Enter Address"}
        onChange={(e) => {
          setAddressLine1(e.target.value);
        }}
        ValidationRequired={false}
      />

      <CardLabel>{`${t("ADDRESS_LINE2")}`}</CardLabel>
      <TextInput
        t={t}
        type={"text"}
        isMandatory={false}
        optionKey="i18nKey"
        name="addressLine2"
        value={addressLine2}
        placeholder={"Enter Address"}
        onChange={(e) => {
          setAddressLine2(e.target.value);
        }}
        ValidationRequired={false}
      />

      <CardLabel>
        {`${t("LANDMARK")}`} <span className="check-page-link-button">*</span>
      </CardLabel>
      <TextArea
        t={t}
        type={"textarea"}
        isMandatory={false}
        optionKey="i18nKey"
        name="landmark"
        value={landmark}
        placeholder={"Enter Landmark"}
        onChange={(e) => {
          setLandmark(e.target.value);
        }}
        ValidationRequired={true}
        validation={{
          isRequired: true,
          pattern: "^[a-zA-Z0-9 ,\\-]+$",
          type: "textarea",
          title: t("LANDMARK_ERROR_MESSAGE"),
        }}
      />

      <CardLabel>
        {`${t("CITY")}`} <span className="check-page-link-button">*</span>
      </CardLabel>
      <Controller
        control={control}
        name={"city"}
        defaultValue={city}
        rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
        render={(props) => (
          <Dropdown
            className="form-field"
            selected={city}
            style={{ width: "100%" }}
            select={setCity}
            option={allCities}
            optionKey="i18nKey"
            t={t}
            placeholder={"Select"}
          />
        )}
      />

      <CardLabel>
        {`${t("LOCALITY")}`} <span className="check-page-link-button">*</span>
      </CardLabel>
      <Controller
        control={control}
        name={"locality"}
        defaultValue={locality}
        rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
        render={(props) => (
          <Dropdown
            className="form-field"
            selected={locality}
            style={{ width: "100%" }}
            select={setLocality}
            option={structuredLocality}
            optionCardStyles={{ overflowY: "auto", maxHeight: "300px" }}
            optionKey="i18nKey"
            t={t}
            placeholder={"Select"}
          />
        )}
      />

      <CardLabel>
        {`${t("PINCODE")}`} <span className="check-page-link-button">*</span>
      </CardLabel>
      <TextInput
        t={t}
        type="text"
        isMandatory={false}
        optionKey="i18nKey"
        name="pincode"
        value={pincode}
        onChange={setAddressPincode}
        placeholder="Enter Pincode"
        ValidationRequired={true}
        validation={{
          pattern: "[0-9]{6}",
          type: "text",
          title: t("PINCODE_INVALID"),
        }}
        minLength={6}
        maxLength={6}
      />
    </div>
  );
};

export default AddressDetails;
