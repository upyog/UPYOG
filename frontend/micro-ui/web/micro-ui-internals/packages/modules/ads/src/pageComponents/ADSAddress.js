import React, { useEffect, useState} from "react";
import { FormStep, TextInput, CardLabel, Dropdown, TextArea } from "@nudmcdgnpm/digit-ui-react-components";
import { useLocation } from "react-router-dom";
import { useForm, Controller } from "react-hook-form";
import Timeline from "../components/ADSTimeline";

const ADSAddress = ({ t, config, onSelect, userType, formData}) => {
  const { pathname: url } = useLocation();
  let index = window.location.href.charAt(window.location.href.length - 1);
  const allCities = Digit.Hooks.ads.useTenants();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const {pathname} = useLocation();
  let validation = {};

  const user = Digit.UserService.getUser().info;

  const [pincode, setPincode] = useState((formData.address && formData.address[index] && formData.address[index].pincode) || formData?.address?.pincode || "");
  const [city, setCity] = useState((formData.address && formData.address[index] && formData.address[index].city) || formData?.address?.city || "");
  const [locality, setLocality] = useState((formData.address && formData.address[index] && formData.address[index].locality) || formData?.address?.locality || "");
  const [streetName, setStreetName] = useState((formData.address && formData.address[index] && formData.address[index].streetName) || formData?.address?.streetName || "");
  const [houseNo, setHouseNo] = useState((formData.address && formData.address[index] && formData.address[index].houseNo) || formData?.address?.houseNo || "");
  const [landmark, setLandmark] = useState((formData.address && formData.address[index] && formData.address[index].landmark) || formData?.address?.landmark || "");
  const [houseName, setHouseName] = useState((formData.address && formData.address[index] && formData.address[index].houseName) || formData?.address?.houseName || "");
  const [addressline1, setAddressline1] = useState((formData.address && formData.address[index] && formData.address[index].addressline1) || formData?.address?.addressline1 || "");
  const [addressline2, setAddressline2] = useState((formData.address && formData.address[index] && formData.address[index].addressline2) || formData?.address?.addressline2 || "");

  const { data: fetchedLocalities, isLoading: isLoadingLocalities } = Digit.Hooks.useBoundaryLocalities(
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
    structuredLocality.push({i18nKey: local.i18nkey, code: local.code, label: local.label, area: local.area, boundaryNum: local.boundaryNum})
  })

  const setAddressPincode = (e) => {
    const newPincode = e.target.value.slice(0, 6); // Truncate input to first 6 characters
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
    let applicantData = formData.address && formData.address[index];
    let applicantStep = { ...applicantData, pincode, city, locality, streetName, houseNo, landmark, houseName, addressline1, addressline2 };
    onSelect(config.key, { ...formData[config.key], ...applicantStep }, false, index);
  };

  const { control } = useForm();
  const onSkip = () => onSelect();

  useEffect(() => {
    if (userType === "citizen") {
      goNext();
    }
  }, [goNext, userType]);

  return (
    <React.Fragment>
      {window.location.href.includes("/citizen") ? <Timeline currentStep={2} /> : null}
      <FormStep
        config={config}
        onSelect={goNext}
        onSkip={onSkip}
        t={t}
        isDisabled={!pincode || !city || !streetName || !houseNo || !landmark || !locality || !addressline1}
      >
        <div>
          <CardLabel>{`${t("ADS_HOUSE_NO")}`} <span className="check-page-link-button">*</span></CardLabel>
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
              title: t("ADS_HOUSE_NO_ERROR_MESSAGE"),
            })}
          />


          <CardLabel>{`${t("ADS_HOUSE_NAME")}`}</CardLabel>
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

          <CardLabel>{`${t("ADS_STREET_NAME")}`} <span className="check-page-link-button">*</span></CardLabel>
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
              title: t("ADS_STREET_NAME_ERROR_MESSAGE"),
            })}
          />

          <CardLabel>{`${t("ADS_ADDRESS_LINE1")}`} <span className="check-page-link-button">*</span></CardLabel>
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
            ValidationRequired={false}

          />

          <CardLabel>{`${t("ADS_ADDRESS_LINE2")}`}</CardLabel>
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
            ValidationRequired={false}
          />
          <CardLabel>{`${t("ADS_LANDMARK")}`} <span className="check-page-link-button">*</span></CardLabel>
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
              title: t("ADS_LANDMARK_ERROR_MESSAGE"),
            })}
          />

          <CardLabel>{`${t("ADS_CITY")}`} <span className="check-page-link-button">*</span></CardLabel>
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
          <CardLabel>{`${t("ADS_LOCALITY")}`} <span className="check-page-link-button">*</span></CardLabel>
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

          <CardLabel>{`${t("ADS_ADDRESS_PINCODE")}`} <span className="check-page-link-button">*</span></CardLabel>
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
              pattern: "[0-9]{6}",
              type: "text",
              title: t("CHB_ADDRESS_PINCODE_INVALID"),
            }}
            minLength={6}
            maxLength={6}
          />


        </div>
      </FormStep>
    </React.Fragment>
  );
};

export default ADSAddress;
