/**
 * AddressDetails.js
 *
 * Purpose:
 * Form component capturing address details for application flow.
 *
 * Responsibilities:
 * - Validates input fields including house number, street name, pincode, and landmark.
 * - Fetches localities dynamically based on selected city.
 * - Triggers state updates and parent step advancement on form submit.
 */

import React, { useEffect, useState, useMemo } from "react";
import { useLocation } from "react-router-dom";
import { useForm, Controller } from "react-hook-form";
import { FormStep, TextInput, Dropdown, CardLabel } from "@nudmcdgnpm/digit-ui-react-components";

const AddressDetails = ({ t, config, onSelect, formData, isEdit }) => {
  const { data: tenants, isLoading: isTenantsLoading } = Digit.Hooks.useTenants();

  const parseOption = (val) => {
    if (!val) return null;
    if (typeof val === "string") return { i18nKey: val, code: val, value: val };
    return val;
  };

  const user = Digit.UserService.getUser().info;
  const location = useLocation();
  const usedAddressTypes = location.state?.usedAddressTypes || [];

  const addressTypeOptions = useMemo(() => {
    const vt = [
      { name: "Correspondence", code: "CORRESPONDENCE", i18nKey: "COMMON_ADDRESS_TYPE_CORRESPONDENCE" },
      { name: "Permanent", code: "PERMANENT", i18nKey: "COMMON_ADDRESS_TYPE_PERMANENT" },
      { name: "Other", code: "OTHER", i18nKey: "COMMON_ADDRESS_TYPE_OTHER" }
    ];
    return usedAddressTypes.length === 3
      ? vt.filter((an) => an.code === "OTHER")
      : vt.filter((an) => !usedAddressTypes.includes(an.code));
  }, [usedAddressTypes]);

  // States initialized with exact original population logic
  const [addressType, setAddressType] = useState(
    parseOption(formData?.addressType) || parseOption(formData?.address?.addressType) || parseOption(formData?.infodetails?.existingDataSet?.address?.addressType) || ""
  );
  const [houseNo, setHouseNo] = useState(
    formData?.houseNo || formData?.address?.houseNo || formData?.infodetails?.existingDataSet?.address?.houseNo || ""
  );
  const [streetName, setStreetName] = useState(
    formData?.streetName || formData?.address?.streetName || formData?.infodetails?.existingDataSet?.address?.streetName || ""
  );
  const [addressLine1, setAddressLine1] = useState(
    formData?.addressLine1 || formData?.address?.addressLine1 || formData?.infodetails?.existingDataSet?.address?.addressline1 || ""
  );
  const [addressLine2, setAddressLine2] = useState(
    formData?.addressLine2 || formData?.address?.addressLine2 || formData?.infodetails?.existingDataSet?.address?.addressline2 || ""
  );
  const [landmark, setLandmark] = useState(
    formData?.landmark || formData?.address?.landmark || formData?.infodetails?.existingDataSet?.address?.landmark || ""
  );
  const [city, setCity] = useState(
    parseOption(formData?.city) || parseOption(formData?.address?.city) || parseOption(formData?.infodetails?.existingDataSet?.address?.cityValue) || ""
  );
  const [locality, setLocality] = useState(
    parseOption(formData?.locality) || parseOption(formData?.address?.locality) || parseOption(formData?.infodetails?.existingDataSet?.address?.locality) || ""
  );
  const [pincode, setPincode] = useState(
    formData?.pincode || formData?.address?.pincode || formData?.infodetails?.existingDataSet?.address?.pincode || ""
  );

  const { control } = useForm();

  // Load localities based on selected city
  const { data: localitiesData } = Digit.Hooks.useBoundaryLocalities(
    city?.code,
    "revenue",
    { enabled: !!city },
    t
  );

  const localitiesOptions = useMemo(() => {
    let list = [];
    if (localitiesData) {
      localitiesData.forEach((item) => {
        list.push({
          i18nKey: item.i18nkey,
          code: item.code,
          label: item.label,
          area: item.area,
          boundaryNum: item.boundaryNum
        });
      });
    }
    return list;
  }, [localitiesData]);

  // Resolve standard validation pattern from project utils
  const pincodePattern = useMemo(() => {
    try {
      const pattern = Digit.Utils.getPattern("Pincode");
      if (pattern) return pattern;
    } catch (err) {
      console.warn("Failed to load standard Pincode regex, using fallback", err);
    }
    return /^[1-9][0-9]{5}$/;
  }, []);

  const handlePincodeChange = (e) => {
    const val = e.target.value;
    // Enforce numeric-only input and maximum length of 6 digits
    if (/^\d*$/.test(val) && val.length <= 6) {
      setPincode(val);
    }
  };

  const goNext = () => {
    let addressData = {
      pincode,
      city,
      locality,
      houseNo,
      landmark,
      addressLine1,
      addressLine2,
      streetName,
      addressType
    };
    onSelect(config.key, { ...formData[config.key], ...addressData }, false);
    if (config === undefined) {
      onSelect(addressData);
    }
  };

  useEffect(() => {
    if (config === undefined && houseNo && city && locality && pincode && addressLine1 && streetName && addressLine2) {
      const isPincodeValid = pincodePattern.test(pincode);
      if (isPincodeValid) {
        onSelect({
          pincode,
          city,
          locality,
          houseNo,
          landmark,
          addressLine1,
          addressLine2,
          streetName,
          addressType
        });
      }
    }
  }, [pincode, city, locality, houseNo, landmark, addressLine1, addressLine2, streetName, addressType, config, onSelect, pincodePattern]);

  const formStyles = { width: user.type === "EMPLOYEE" ? "50%" : "100%" };

  // Validation checks: match exact parity of required fields, and enforce standard pincode pattern
  const isPincodeValid = pincodePattern.test(pincode);
  const isSaveDisabled = !houseNo || !city || !locality || !pincode || !addressLine1 || !streetName || !addressLine2 || !isPincodeValid;

  return (
    <FormStep config={config} onSelect={goNext} t={t} isDisabled={isSaveDisabled}>
      <CardLabel>{`${t("COMMON_ADDRESS_TYPE")} *`}</CardLabel>
      <Dropdown
        className="form-field"
        selected={addressType}
        select={setAddressType}
        disable={isEdit}
        option={addressTypeOptions}
        optionCardStyles={{ overflowY: "auto", maxHeight: "300px" }}
        optionKey="i18nKey"
        t={t}
        style={formStyles}
        placeholder="Select Address Type"
      />

      <CardLabel>{`${t("HOUSE_NO")} *`}</CardLabel>
      <TextInput
        t={t}
        type="text"
        isMandatory={false}
        optionKey="i18nKey"
        name="houseNo"
        value={houseNo}
        style={formStyles}
        placeholder="Enter House No"
        onChange={(e) => setHouseNo(e.target.value)}
        ValidationRequired={true}
        validation={{
          isRequired: true,
          pattern: "^[a-zA-Z0-9 ,\\-]+$",
          title: t("HOUSE_NO_ERROR_MESSAGE")
        }}
      />

      <CardLabel>{`${t("STREET_NAME")} *`}</CardLabel>
      <TextInput
        t={t}
        type="text"
        isMandatory={false}
        optionKey="i18nKey"
        name="streetName"
        value={streetName}
        style={formStyles}
        placeholder="Enter Street Name"
        onChange={(e) => setStreetName(e.target.value)}
        ValidationRequired={true}
        validation={{
          pattern: "^[a-zA-Z0-9 ,\\-]+$",
          title: t("STREET_NAME_ERROR_MESSAGE")
        }}
      />

      <CardLabel>{`${t("ADDRESS_LINE1")} *`}</CardLabel>
      <TextInput
        t={t}
        type="text"
        isMandatory={false}
        optionKey="i18nKey"
        name="addressLine1"
        value={addressLine1}
        style={formStyles}
        placeholder="Enter Address"
        onChange={(e) => setAddressLine1(e.target.value)}
        ValidationRequired={false}
        isRequired={false}
        pattern="^[a-zA-Z,-/ ]*$"
        title={t("ADDRESS_ERROR_MESSAGE")}
      />

      <CardLabel>{`${t("ADDRESS_LINE2")} *`}</CardLabel>
      <TextInput
        t={t}
        type="text"
        isMandatory={false}
        optionKey="i18nKey"
        name="addressLine2"
        value={addressLine2}
        style={formStyles}
        placeholder="Enter Address"
        onChange={(e) => setAddressLine2(e.target.value)}
        ValidationRequired={false}
        isRequired={false}
        pattern="^[a-zA-Z,-/ ]*$"
        title={t("ADDRESS_ERROR_MESSAGE")}
      />

      <CardLabel>{t("LANDMARK")}</CardLabel>
      <TextInput
        t={t}
        type="textarea"
        isMandatory={false}
        optionKey="i18nKey"
        name="landmark"
        value={landmark}
        style={formStyles}
        placeholder="Enter Landmark"
        onChange={(e) => setLandmark(e.target.value)}
        ValidationRequired={true}
        validation={{
          isRequired: false,
          pattern: "^[a-zA-Z0-9 ]+$",
          title: t("LANDMARK_ERROR_MESSAGE")
        }}
      />

      <CardLabel>{`${t("CITY")} *`}</CardLabel>
      <Controller
        control={control}
        name="city"
        defaultValue={city}
        rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
        render={() => (
          <Dropdown
            className="form-field"
            selected={city}
            select={(val) => {
              setCity(val);
              setLocality("");
            }}
            option={tenants}
            optionCardStyles={{ overflowY: "auto", maxHeight: "300px" }}
            optionKey="i18nKey"
            t={t}
            style={formStyles}
            placeholder="Select"
          />
        )}
      />

      <CardLabel>{`${t("LOCALITY")} *`}</CardLabel>
      <Controller
        control={control}
        name="locality"
        defaultValue={locality}
        rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
        render={() => (
          <Dropdown
            className="form-field"
            selected={locality}
            select={setLocality}
            option={localitiesOptions}
            optionCardStyles={{ overflowY: "auto", maxHeight: "300px" }}
            optionKey="i18nKey"
            t={t}
            style={formStyles}
            placeholder="Select"
          />
        )}
      />

      <CardLabel>{`${t("PINCODE")} *`}</CardLabel>
      <TextInput
        t={t}
        type="text"
        isMandatory={false}
        optionKey="i18nKey"
        name="pincode"
        value={pincode}
        style={formStyles}
        placeholder="Enter Pincode"
        onChange={handlePincodeChange}
        ValidationRequired={true}
        validation={{
          required: true,
          pattern: pincodePattern.source,
          title: t("SV_ADDRESS_PINCODE_INVALID")
        }}
        maxlength={6}
      />
    </FormStep>
  );
};

export default AddressDetails;
