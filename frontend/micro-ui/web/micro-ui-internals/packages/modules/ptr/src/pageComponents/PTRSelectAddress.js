/**
 * PTRSelectAddress Component
 * 
 * This component handles the address selection form for the PTR module.
 * 
 * Features:
 * - Uses React hooks to manage state and form controls.
 * - Displays address fields including House No, Street Name, Landmark, City, Locality, and Pincode.
 * - Retrieves city and locality data using Digit hooks.
 * - Validates and formats the address details before submission.
 * - Supports navigation with "Next" and "Skip" buttons.
 * 
 * Dependencies:
 * - React, React Router, and Digit UI components.
 * - `useForm` and `Controller` from `react-hook-form` for form handling.
 * 
 * Props:
 * - `t`: Translation function for localization.
 * - `config`: Configuration object for form step.
 * - `onSelect`: Function to handle form submission.
 * - `formData`: Pre-filled form data.
 * - `renewApplication`: Data for renewal applications.
 * 
 * Usage:
 * - This component is used as part of the PTR application process.
 */

import React, { useState} from "react";
import { FormStep, TextInput, CardLabel, Dropdown, TextArea} from "@upyog/digit-ui-react-components";
import { useLocation, useParams } from "react-router-dom";
import { useForm, Controller } from "react-hook-form";
import Timeline from "../components/PTRTimeline";

const PTRSelectAddress = ({ t, config, onSelect, formData, renewApplication }) => {
  const convertToObject = (String) => String ? { i18nKey: String, code: String, value: String } : null;
  const allCities = Digit.Hooks.ptr.useTenants();
  const {pathname} = useLocation();
  let validation = {};
  const { control } = useForm();
  const user = Digit.UserService.getUser().info;
  const [pincode, setPincode] = useState(renewApplication?.address?.pincode|| formData?.address?.pincode || "");
  const [city, setCity] = useState(formData?.address?.city ||convertToObject(renewApplication?.address?.city)|| "");
  const [locality, setLocality] = useState(convertToObject(renewApplication?.address?.locality) || formData?.address?.locality || "");
  const [streetName, setStreetName] = useState(renewApplication?.address?.streetName || renewApplication?.address?.street || formData?.address?.streetName || "");
  const [houseNo, setHouseNo] = useState(renewApplication?.address?.houseNo || renewApplication?.address?.doorNo || formData?.address?.houseNo || "");
  const [landmark, setLandmark] = useState(renewApplication?.address?.landmark || formData?.address?.landmark || "");
  const [houseName, setHouseName] = useState(renewApplication?.address?.houseName || renewApplication?.address?.buildingName || formData?.address?.houseName || "");
  const [addressline1, setAddressline1] = useState(renewApplication?.address?.addressline1 || renewApplication?.address?.addressLine1 || formData?.address?.addressline1 || "");
  const [addressline2, setAddressline2] = useState(renewApplication?.address?.addressline2 || renewApplication?.address?.addressLine2 || formData?.address?.addressline2 || "");


  const { data: fetchedLocalities } = Digit.Hooks.useBoundaryLocalities(
    city?.code,
    "revenue",
    {
      enabled: !!city,
    },
    t
  );

  // Fixing the locality data coming from the useboundarylocalities hook
  let structuredLocality = [];
  fetchedLocalities && fetchedLocalities.map((local, index) => {
    structuredLocality.push({i18nKey: local.i18nkey, code: local.code, label: local.label})
  })


  const setAddressPincode = (e) => {
    const newPincode = e.target.value.slice(0, 6);
    setPincode(newPincode);
  };

  const setApplicantStreetName = (e) => {
    setStreetName(e.target.value);
  };

  const setApplicantHouseNo = (e) => {
    setHouseNo(e.target.value);
  };

  const setApplicantLandmark = (e) => {
    setLandmark(e.target.value);
  };

  const sethouseName = (e) => {
    setHouseName(e.target.value)
  }

  const setaddressline1 = (e) => {
    setAddressline1(e.target.value)
  }

  const setaddressline2 = (e) => {
    setAddressline2(e.target.value)
  }

  const goNext = () => {
    let owner = formData.address ;
    let ownerStep = { ...owner, pincode, city, locality, streetName, houseNo, landmark, houseName, addressline1, addressline2 };
    onSelect(config.key, { ...formData[config.key], ...ownerStep }, false);
  };

  const onSkip = () => onSelect();

  return (
    <React.Fragment>
      {<Timeline currentStep={3} />}
      <FormStep
        config={config}
        onSelect={goNext}
        onSkip={onSkip}
        t={t}
        isDisabled={!pincode || !city || !streetName || !houseNo || !landmark || (!(pathname.includes("revised") || pathname.includes("renew")) && !locality) || !addressline1 }
      >
        <div>
          <CardLabel>{`${t("PTR_HOUSE_NO")}`} <span className="check-page-link-button">*</span></CardLabel>
          <TextInput
            t={t}
            type={"text"}
            isMandatory={false}
            optionKey="i18nKey"
            name="houseNo"
            value={houseNo}
            placeholder={"Enter House No"}
            onChange={setApplicantHouseNo}
            style={{ width: user.type === "EMPLOYEE" ? "50%" : "86%" }}
            ValidationRequired={true}
            {...(validation = {
              isRequired: true,
              pattern: "^[a-zA-Z0-9 ,\\-]+$",
              type: "text",
              title: t("PTR_HOUSE_NO_ERROR_MESSAGE"),
            })}
          />


          <CardLabel>{`${t("PTR_HOUSE_NAME")}`}</CardLabel>
          <TextInput
            t={t}
            type={"text"}
            isMandatory={false}
            optionKey="i18nKey"
            name="houseName"
            value={houseName}
            placeholder={"Enter House Name"}
            onChange={sethouseName}
            style={{ width: user.type === "EMPLOYEE" ? "50%" : "86%" }}
            ValidationRequired={false}
          />

          <CardLabel>{`${t("PTR_STREET_NAME")}`} <span className="check-page-link-button">*</span></CardLabel>
          <TextInput
            t={t}
            type={"text"}
            isMandatory={false}
            optionKey="i18nKey"
            name="streetName"
            value={streetName}
            placeholder={"Enter Street Name"}
            onChange={setApplicantStreetName}
            style={{ width: user.type === "EMPLOYEE" ? "50%" : "86%" }}
            ValidationRequired={true}
            {...(validation = {
              pattern: "^[a-zA-Z0-9 ,\\-]+$",
              type: "text",
              title: t("PTR_STREET_NAME_ERROR_MESSAGE"),
            })}
          />

          <CardLabel>{`${t("PTR_ADDRESS_LINE1")}`} <span className="check-page-link-button">*</span></CardLabel>
          <TextInput
            t={t}
            type={"text"}
            isMandatory={false}
            optionKey="i18nKey"
            name="addressline1"
            value={addressline1}
            placeholder={"Enter Address"}
            onChange={setaddressline1}
            style={{ width: user.type === "EMPLOYEE" ? "50%" : "86%" }}
            ValidationRequired={true}
            {...(validation = {
              isRequired: false,
              pattern: "^[a-zA-Z0-9 .,?!'\"-]+$",
              type: "textarea",
              title: t("SV_LANDMARK_ERROR_MESSAGE"),
            })}

          />

          <CardLabel>{`${t("PTR_ADDRESS_LINE2")}`}</CardLabel>
          <TextInput
            t={t}
            type={"text"}
            isMandatory={false}
            optionKey="i18nKey"
            name="addressline2"
            value={addressline2}
            placeholder={"Enter Address"}
            onChange={setaddressline2}
            style={{ width: user.type === "EMPLOYEE" ? "50%" : "86%" }}
            ValidationRequired={true}
            {...(validation = {
              isRequired: false,
              pattern: "^[a-zA-Z ]*$",
              type: "textarea",
              title: t("SV_LANDMARK_ERROR_MESSAGE"),
            })}
          />
          <CardLabel>{`${t("PTR_LANDMARK")}`} <span className="check-page-link-button">*</span></CardLabel>
          <TextArea
            t={t}
            type={"textarea"}
            isMandatory={false}
            optionKey="i18nKey"
            name="landmark"
            value={landmark}
            placeholder={"Enter Landmark"}
            onChange={setApplicantLandmark}
            style={{ width: "50%" }}
            ValidationRequired={true}
            {...(validation = {
              isRequired: true,
              pattern: "^[a-zA-Z0-9 ,\\-]+$",
              type: "textarea",
              title: t("PTR_LANDMARK_ERROR_MESSAGE"),
            })}
          />

          <CardLabel>{`${t("PTR_CITY")}`} <span className="check-page-link-button">*</span></CardLabel>
          <Controller
            control={control}
            name={"city"}
            defaultValue={city}
            rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
            render={(props) => (
              <Dropdown
                className="form-field"
                selected={city}
                select={setCity}
                option={allCities}
                optionKey="i18nKey"
                t={t}
                placeholder={"Select"}
              />
            )}
          />
          <CardLabel>{`${t("PTR_LOCALITY")}`} <span className="check-page-link-button">*</span></CardLabel>
          <Controller
            control={control}
            name={"locality"}
            defaultValue={locality}
            rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
            render={(props) => (
              <Dropdown
                className="form-field"
                selected={locality}
                select={setLocality}
                option={structuredLocality}
                optionCardStyles={{ overflowY: "auto", maxHeight: "300px" }}
                optionKey="i18nKey"
                t={t}
                placeholder={"Select"}
              />
            )}
          />

          <CardLabel>{`${t("PTR_ADDRESS_PINCODE")}`} <span className="check-page-link-button">*</span></CardLabel>
          <TextInput
            t={t}
            type="text"
            isMandatory={false}
            optionKey="i18nKey"
            name="pincode"
            value={pincode}
            onChange={setAddressPincode}
            placeholder="Enter Pincode"
            style={{ width: user.type === "EMPLOYEE" ? "50%" : "86%" }}
            ValidationRequired={true}
            validation={{
              required: false,
              pattern: "^[0-9]{6}$",
              type: "tel",
              title: t("SV_ADDRESS_PINCODE_INVALID"),
            }}
            maxLength={6}
          />


        </div>
      </FormStep>
    </React.Fragment>
  );
};

export default PTRSelectAddress;

