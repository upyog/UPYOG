import React, { useEffect, useState } from "react";
import { FormStep, TextInput, CardLabel, Dropdown, TextArea } from "@nudmcdgnpm/digit-ui-react-components";
import { useForm, Controller } from "react-hook-form";
import Timeline from "../components/Timeline";
import { forwardRef } from "react";

const SVAdrressDetails = ({ t, config, onSelect, userType, formData }) => {
  console.log("formdatatattata",formData)
  const allCities = Digit.Hooks.sv.useTenants();
  let validation = {};
  const user = Digit.UserService.getUser().info;
  const [pincode, setPincode] = useState(formData?.address?.pincode || "");
  const [city, setCity] = useState(formData?.address?.city || "");
  const [locality, setLocality] = useState(formData?.address?.locality || "");
  const [houseNo, setHouseNo] = useState(formData?.address?.houseNo || "");
  const [landmark, setLandmark] = useState(formData?.address?.landmark || "");
  const [addressline1, setAddressline1] = useState(formData?.address?.addressline1 || "");
  const [addressline2, setAddressline2] = useState(formData?.address?.addressline2 || "");
  const { control } = useForm();

  const { data: fetchedLocalities } = Digit.Hooks.useBoundaryLocalities(
    city?.code,
    "revenue",
    {
      enabled: !!city,
    },
    t
  );
  let structuredLocality = [];
  fetchedLocalities && fetchedLocalities.map((localityData, index) => {
    structuredLocality.push({i18nKey: localityData?.i18nkey, code: localityData?.code, label: localityData?.label, area: localityData?.area, boundaryNum: localityData?.boundaryNum})
  })


  const setAddressPincode = (e) => {
    setPincode(e.target.value);
  };

  const sethouseNo = (e) => {
    setHouseNo(e.target.value);
  };

  const setlandmark = (e) => {
    setLandmark(e.target.value);
  };


  const setaddressline1 = (e) => {
    setAddressline1(e.target.value)
  }

  const setaddressline2 = (e) => {
    setAddressline2(e.target.value)
  }

  const goNext = () => {
    let owner = formData.address;
    let ownerStep = { ...owner, pincode, city, locality, houseNo, landmark, addressline1, addressline2 };
    onSelect(config.key, { ...formData[config.key], ...ownerStep }, false);
   
  };

  
  const onSkip = () => onSelect();

  useEffect(() => {
    if (userType === "citizen") {
      goNext();
    }
  }, [pincode, city, locality, houseNo, landmark, addressline1, addressline2]);


  return (
    <React.Fragment>
      {window.location.href.includes("/citizen") ? <Timeline currentStep={3} /> : null}
      <FormStep
        config={config}
        onSelect={goNext}
        onSkip={onSkip}
        t={t}
        isDisabled={!city || !houseNo || !locality || !addressline1 || !addressline2 }
      >
        <div>
          <CardLabel>{`${t("SV_HOUSE_NO")}`} <span className="astericColor">*</span></CardLabel>
          <TextInput
            t={t}
            type={"text"}
            isMandatory={false}
            optionKey="i18nKey"
            name="houseNo"
            value={houseNo}
            placeholder={"Enter House No"}
            onChange={sethouseNo}
            style={{ width: user.type === "EMPLOYEE" ? "50%" : "86%" }}
            ValidationRequired={true}
            {...(validation = {
              isRequired: true,
              pattern: "^[a-zA-z0-9- ]*$",
              type: "text",
              title: t("SV_HOUSE_NO_ERROR_MESSAGE"),
            })}
          />

          <CardLabel>{`${t("SV_ADDRESS_LINE1")}`} <span className="astericColor">*</span></CardLabel>
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
              pattern: "^[a-zA-Z ]*$",
              type: "textarea",
              title: t("SV_LANDMARK_ERROR_MESSAGE"),
            })}

          />

          <CardLabel>{`${t("SV_ADDRESS_LINE2")}`} <span className="astericColor">*</span></CardLabel>
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
          <CardLabel>{`${t("SV_LANDMARK")}`}</CardLabel>
          <TextArea
            t={t}
            type={"textarea"}
            isMandatory={false}
            optionKey="i18nKey"
            name="landmark"
            value={landmark}
            placeholder={"Enter Landmark"}
            onChange={setlandmark}
            style={{ width: "50%" }}
            ValidationRequired={true}
            {...(validation = {
              isRequired: false,
              pattern: "^[a-zA-Z ]*$",
              type: "textarea",
              title: t("SV_LANDMARK_ERROR_MESSAGE"),
            })}
          />

          <CardLabel>{`${t("SV_CITY")}`} <span className="astericColor">*</span></CardLabel>
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
          <CardLabel>{`${t("SV_LOCALITY")}`} <span className="astericColor">*</span></CardLabel>
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
          <CardLabel>{`${t("SV_ADDRESS_PINCODE")}`}</CardLabel>
          <TextInput
            t={t}
            type="tel"
            isMandatory={false}
            optionKey="i18nKey"
            name="pincode"
            value={pincode}
            onChange={setAddressPincode}
            placeholder="Enter Pincode"
            style={{ width: user.type === "EMPLOYEE" ? "50%" : "86%" }}
            ValidationRequired={true}
            validation={{
              required:false,
              pattern: "^[0-9]{0,6}+$",
              type: "tel",
              title: t("SV_ADDRESS_PINCODE_INVALID"),
            }}
            minLength={6}
            maxLength={6}
          />


        </div>
      </FormStep>
    </React.Fragment>
  );
};

export default SVAdrressDetails;


