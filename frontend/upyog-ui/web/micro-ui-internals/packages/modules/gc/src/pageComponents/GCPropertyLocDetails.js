import React, { useState, useEffect } from "react";
import { FormStep, TextInput, CardLabel, Dropdown, TextArea, SearchIcon, Toast } from "@nudmcdgnpm/digit-ui-react-components";
import { useLocation, useParams } from "react-router-dom";
import { useForm, Controller } from "react-hook-form";

const GCProtertyLocDetails = ({ t, config, onSelect, formData, renewApplication }) => {

  const convertToObject = (params) => {
    return { i18nKey: params, code: params, value: params }
  }
  const allCities = Digit.Hooks.gc.useTenants();
  const { pathname } = useLocation();
  let validation = {};
  const { control } = useForm();
  const user = Digit.UserService.getUser().info;
  const addressData = formData?.[config?.key] || formData?.gcpropertylocdetails || formData?.address || {};


  const [pincode, setPincode] = useState(addressData.pincode || renewApplication?.addresses[0]?.pincode || "");
  const initialCity = addressData.city || renewApplication?.addresses[0]?.city;
  const [city, setCity] = useState(
    initialCity
      ? (typeof initialCity === 'object' ? initialCity : convertToObject(initialCity))
      : ""
  );
  const [locality, setLocality] = useState(addressData.locality || convertToObject(renewApplication?.addresses[0]?.additionalDetail?.locality) || "");
  const [streetName, setStreetName] = useState(addressData.streetName || renewApplication?.addresses[0]?.additionalDetail?.streetName || "");
  const [houseNo, setHouseNo] = useState(addressData.houseNo || renewApplication?.addresses[0]?.additionalDetail?.houseNo || "");
  const [landmark, setLandmark] = useState(addressData.landmark || renewApplication?.addresses[0]?.additionalDetail?.landmark || "");
  const [houseName, setHouseName] = useState(addressData.houseName || renewApplication?.addresses[0]?.additionalDetail?.houseName || "");
  const [addressline1, setAddressline1] = useState(addressData?.addressline1 || renewApplication?.addresses[0]?.address1 || "");
  const [addressline2, setAddressline2] = useState(addressData?.addressline2 || renewApplication?.addresses[0]?.address2 || "");
  const [propertyId, setpropertyId] = useState(addressData?.propertyId || renewApplication?.propertyId || "");
  const [shouldFetchDetails, setShouldFetchDetails] = useState(false);
  const stateId = Digit.ULBService.getStateId();
  const inputStyles = { width: user.type === "EMPLOYEE" ? "50%" : "86%" };
  const [showToast, setShowToast] = useState(null);
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
    structuredLocality.push({ i18nKey: local.i18nkey, code: local.code, label: local.label })
  })


  const setAddressPincode = (e) => {
    let value = e.target.value.replace(/\D/g, ""); // Only digits

    // Do not allow first digit to be 0
    if (value.length > 0 && value.charAt(0) === "0") {
      value = value.substring(1);
    }

    // Maximum 6 digits
    value = value.slice(0, 6);

    setPincode(value);
  };

  const setPropertyId = (e) => {
    setpropertyId(e.target.value);
  }

  const setApplicantStreetName = (e) => {
    setStreetName(e.target.value);
  };

  const setApplicantHouseNo = (e) => {
    const value = e.target.value.replace(/\D/g, ""); // Only digits
    setHouseNo(value);
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

  useEffect(() => {
    if (showToast) {
      const timer = setTimeout(() => setShowToast(null), 2000);
      return () => clearTimeout(timer);
    }
  }, [showToast]);


  // Use the hook at the top level of your component
  const { isLoading, isError, data: applicationDetails, error } = Digit.Hooks.pt.useApplicationDetail(
    t,
    stateId,
    propertyId,
    shouldFetchDetails // Only fetch when this is true
  );
  // Function to handle search icon click
  const handleSearchClick = () => {
    if (propertyId) {
      setShouldFetchDetails(true);
    } else {
      setShowToast({ error: true, label: t("PROPERTY_ID_REQUIRED") });
    }
  };
  // Effect to handle the fetched data
  useEffect(() => {
    if (shouldFetchDetails) {
      if (isLoading) {
        setShowToast({ warning: true, label: t("GC_LOADING_DETAILS") });
      } else if (isError) {
        setShowToast({ error: true, label: t("GC_ERROR_FETCHING_DETAILS") });
      } else if (applicationDetails) {
        const streetNames = applicationDetails?.applicationData?.address?.street;
        const houseNumber = applicationDetails?.applicationData?.address?.doorNo;
        const pin = applicationDetails?.applicationData?.address?.pincode;

        if (streetNames || houseNumber || pin) {
          setHouseNo(houseNumber);
          setStreetName(streetNames);
          setPincode(pin);
        }
      }
      setShouldFetchDetails(false);
    }
  }, [shouldFetchDetails, isLoading, isError, applicationDetails, error]);

  const goNext = () => {
    let ownerStep = { pincode, city, locality, streetName, houseNo, landmark, houseName, addressline1, addressline2, propertyId };
    onSelect(config.key, { ...formData[config.key], ...ownerStep }, false);
  };

  const onSkip = () => onSelect();

  return (
    <React.Fragment>
      <FormStep
        config={config}
        onSelect={goNext}
        onSkip={onSkip}
        t={t}
        isDisabled={!pincode || !city || !streetName || !houseNo || !landmark || (!(pathname.includes("revised") || pathname.includes("renew")) && !locality) || !addressline1}
      >
        <div>
          <style>
            {`
        .select-wrap .options-card {
        width: 100% !important;
        -webkit-box-shadow: 0 8px 10px 1px rgba(0, 0, 0, 0.14), 0 3px 14px 2px rgba(0, 0, 0, 0.12), 0 5px 5px -3px rgba(0, 0, 0, 0.2);
        box-shadow: 0 8px 10px 1px rgba(0, 0, 0, 0.14), 0 3px 14px 2px rgba(0, 0, 0, 0.12), 0 5px 5px -3px rgba(0, 0, 0, 0.2);
        position: absolute;
        z-index: 20;
        margin-top: 4px;
        --bg-opacity: 1;
        background-color: #fff;
        background-color: rgba(255, 255, 255, var(--bg-opacity));
        overflow: scroll;
        max-height: 250px; 
        min-height:50px;
         } `
            }
          </style>
          <CardLabel>{`${t("GC_PROPERTY_NO")}`}</CardLabel>
          <div className="field-container">
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              name="propertyId"
              value={propertyId}
              onChange={setPropertyId}
              style={inputStyles}
              ValidationRequired={true}
              {...(validation = {
                isRequired: false,
                pattern: "^[a-zA-Z0-9/-]*$",
                type: "text",
                title: t("GC_PROPERTY_ID_ERROR_MESSAGE"),
              })}
            />
            <div style={{ position: "relative", zIndex: "100", right: user.type === "EMPLOYEE" ? "52%" : "95px", marginTop: "-14px", marginRight: "-20px", cursor: "pointer" }} onClick={handleSearchClick}> <SearchIcon /> </div>
          </div>
          <CardLabel>{`${t("GC_HOUSE_NO")}`} <span className="check-page-link-button">*</span></CardLabel>
          <TextInput
            t={t}
            type="text"
            inputMode="numeric"
            isMandatory={false}
            optionKey="i18nKey"
            name="houseNo"
            value={houseNo}
            placeholder="Enter House No"
            onChange={setApplicantHouseNo}
            style={{ width: user.type === "EMPLOYEE" ? "50%" : "86%" }}
            ValidationRequired={true}
            validation={{
              isRequired: true,
              pattern: "^[0-9]+$",
              title: t("GC_HOUSE_NO_ERROR_MESSAGE"),
            }}
          />


          <CardLabel>{`${t("GC_HOUSE_NAME")}`}</CardLabel>
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

          <CardLabel>{`${t("GC_STREET_NAME")}`} <span className="check-page-link-button">*</span></CardLabel>
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
              title: t("GC_STREET_NAME_ERROR_MESSAGE"),
            })}
          />

          <CardLabel>{`${t("GC_ADDRESS_LINE1")}`} <span className="check-page-link-button">*</span></CardLabel>
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

          <CardLabel>{`${t("GC_ADDRESS_LINE2")}`}</CardLabel>
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
          <CardLabel>{`${t("GC_LANDMARK")}`} <span className="check-page-link-button">*</span></CardLabel>
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
              title: t("GC_LANDMARK_ERROR_MESSAGE"),
            })}
          />

          <CardLabel>{`${t("GC_CITY")}`} <span className="check-page-link-button">*</span></CardLabel>
          <Controller
            control={control}
            name={"city"}
            defaultValue={city}
            rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
            render={({ field }) => (
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
          <CardLabel>{`${t("GC_LOCALITY")}`} <span className="check-page-link-button">*</span></CardLabel>
          <Controller
            control={control}
            name={"locality"}
            defaultValue={locality}
            rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
            render={({ field }) => (
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

          <CardLabel>{`${t("GC_ADDRESS_PINCODE")}`} <span className="check-page-link-button">*</span></CardLabel>
          <TextInput
            t={t}
            type="text"
            inputMode="numeric"
            name="pincode"
            value={pincode}
            onChange={setAddressPincode}
            placeholder="Enter Pincode"
            style={{ width: user.type === "EMPLOYEE" ? "50%" : "86%" }}
            ValidationRequired={true}
            validation={{
              isRequired: true,
              pattern: "^[1-9][0-9]{5}$",
              title: t("SV_ADDRESS_PINCODE_INVALID"),
            }}
            maxLength={6}
          />


        </div>
      </FormStep>
      {showToast && (
        <Toast
          error={showToast.error}
          warning={showToast.warning}
          label={t(showToast.label)}
          onClose={() => setShowToast(null)}
        />
      )}
    </React.Fragment>
  );
};

export default GCProtertyLocDetails;