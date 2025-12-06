<<<<<<< HEAD
import { CardLabel, CardLabelError, Dropdown, FormStep, LabelFieldPair, RadioOrSelect } from "@upyog/digit-ui-react-components";
import _ from "lodash";
import React, { useEffect, useState } from "react";
import { Controller, useForm } from "react-hook-form";
import { useLocation } from "react-router-dom";
import Timeline from "../components/PTRTimeline";

const PTRSelectAddress = ({ t, config, onSelect, userType, formData, setError, clearErrors, formState }) => {
  const allCities = Digit.Hooks.ptr.useTenants();
  let tenantId = Digit.ULBService.getCurrentTenantId();
  const { pathname } = useLocation();
  const presentInModifyApplication = pathname.includes("modify");
  
  


  

  let isEditAddress = formData?.isEditAddress || false;
  if (presentInModifyApplication) isEditAddress = true;
  
  const { pincode, city } = formData?.address || "";
  const cities =
    userType === "employee" ? allCities.filter((city) => city.code === tenantId) : pincode  ? allCities.filter((city) => city?.pincode?.some((pin) => pin == pincode)) : allCities;

  const [selectedCity, setSelectedCity] = useState(() => {
    return formData?.address?.city || null;
  });

  const { data: fetchedLocalities } = Digit.Hooks.useBoundaryLocalities(
    selectedCity?.code,
     "revenue",
    {
      enabled: !!selectedCity,
=======
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

import React, { useState,useEffect} from "react";
import { FormStep, TextInput, CardLabel, Dropdown, TextArea, SearchIcon, Toast} from "@upyog/digit-ui-react-components";
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
  const [propertyId, setpropertyId] = useState(renewApplication?.address?.propertyId || formData?.address?.propertyId || "");
  const [shouldFetchDetails, setShouldFetchDetails] = useState(false);
  const stateId = Digit.ULBService.getStateId();
  const inputStyles = { width: user.type === "EMPLOYEE" ? "50%" : "86%" };
  const [showToast, setShowToast] = useState(null);
  const { data: fetchedLocalities } = Digit.Hooks.useBoundaryLocalities(
    city?.code,
    "revenue",
    {
      enabled: !!city,
>>>>>>> master-LTS
    },
    t
  );

<<<<<<< HEAD
  const [localities, setLocalities] = useState();

  const [selectedLocality, setSelectedLocality] = useState();

  useEffect(() => {
    if (userType === "employee" && presentInModifyApplication && localities?.length) {
      const code = formData?.originalData?.address?.locality?.code;
      const _locality = localities?.filter((e) => e.code === code)[0];
      setValue("locality", _locality);
    }
  }, [localities]);

  useEffect(() => {
    if (cities) {
      if (cities.length === 1) {
        setSelectedCity(cities[0]);
      }
    }
  }, [cities]);

  useEffect(() => {
    if (selectedCity && fetchedLocalities) {
      let __localityList = fetchedLocalities;
      let filteredLocalityList = [];

      if (formData?.address?.locality) {
        setSelectedLocality(formData.address.locality);
      }

      if (formData?.address?.pincode) {
        filteredLocalityList = __localityList.filter((obj) => obj.pincode?.find((item) => item == formData.address.pincode));
        if (!formData?.address?.locality) setSelectedLocality();
      }
      setLocalities(() => (filteredLocalityList.length > 0 ? filteredLocalityList : __localityList));

      if (filteredLocalityList.length === 1) {
        setSelectedLocality(filteredLocalityList[0]);
        // if (userType === "employee") {
        //   onSelect(config.key, { ...formData[config.key], locality: filteredLocalityList[0] });
        // }
      }
    }
  }, [selectedCity, formData?.address?.pincode, fetchedLocalities]);

  

  function selectCity(city) {
    setSelectedLocality(null);
    setLocalities(null);
    setSelectedCity(city);
  }

  function selectLocality(locality) {
    if (formData?.address?.locality) {
      formData.address["locality"] = locality;
    }
    setSelectedLocality(locality);
    if (userType === "employee") {
      onSelect(config.key, { ...formData[config.key], locality: locality });
    }
  }

  function onSubmit() {
    onSelect(config.key, { city: selectedCity, locality: selectedLocality });
  }

  const { control, formState: localFormState, watch, /*setError: setLocalError, clearErrors: clearLocalErrors,*/ setValue } = useForm();
  const formValue = watch();
  const { errors } = localFormState;
  const errorStyle = { width: "70%", marginLeft: "30%", fontSize: "12px", marginTop: "-21px" };

  useEffect(() => {
    if (userType === "employee") {
      let keys = Object.keys(formValue);
      const part = {};
      keys.forEach((key) => (part[key] = formData[config.key]?.[key]));
      if (!_.isEqual(formValue, part)) onSelect(config.key, { ...formData[config.key], ...formValue });
      for (let key in formValue) {
        if (!formValue[key] && !localFormState?.errors[key]) {
          // setLocalError(key, { type: `${key.toUpperCase()}_REQUIRED`, message: t(`CORE_COMMON_REQUIRED_ERRMSG`) });
        } else if (formValue[key] && localFormState.errors[key]) {
          // clearLocalErrors([key]);
        }
      }
    }
  }, [formValue]);

  useEffect(() => {
    if (userType === "employee") {
      const errorsPresent = !!Object.keys(localFormState.errors).lengtha;
      if (errorsPresent && !formState.errors?.[config.key]) /*setError(config.key, { type: "required" })*/;
      else if (!errorsPresent && formState.errors?.[config.key]) /*clearErrors(config.key)*/;
    }
  }, [localFormState]);

  if (userType === "employee") {
    return (
      <div>
        <LabelFieldPair>
          <CardLabel className="card-label-smaller">{t("MYCITY_CODE_LABEL") + " *"}</CardLabel>
          <Controller
            name={"city"}
            defaultValue={cities?.length === 1 ? cities[0] : selectedCity}
            control={control}
            render={(props) => (
              <Dropdown
                className="form-field"
                selected={props.value}
                disable={isEditAddress ? isEditAddress : cities?.length === 1}
                option={cities}
                select={props.onChange}
                optionKey="code"
                onBlur={props.onBlur}
                t={t}
              />
            )}
          />
        </LabelFieldPair>
        <CardLabelError style={errorStyle}>{localFormState.touched.city ? errors?.city?.message : ""}</CardLabelError>
        <LabelFieldPair>
          <CardLabel className="card-label-smaller">{t("PTR_LOCALITY") + " *"}</CardLabel>
          <Controller
            name="locality"
            defaultValue={null}
            control={control}
            render={(props) => (
              <Dropdown
                className="form-field"
                selected={props.value}
                option={localities}
                select={props.onChange}
                onBlur={props.onBlur}
                optionKey="i18nkey"
                t={t}
                disable={isEditAddress ? isEditAddress : false}
              />
            )}
          />
        </LabelFieldPair>
        <CardLabelError style={errorStyle}>{localFormState.touched.locality ? errors?.locality?.message : ""}</CardLabelError>
      </div>
    );
  }
  return (
    <React.Fragment>
      {window.location.href.includes("/citizen") ? <Timeline currentStep={3} /> : null}
      <FormStep config={config} onSelect={onSubmit} t={t} isDisabled={selectedLocality ? false : true}>
        <div>
          <CardLabel>{`${t("MYCITY_CODE_LABEL")} `}</CardLabel>
          <span className={"form-ptr-dropdown-only"}>
            <RadioOrSelect
              options={cities.sort((a, b) => a.name.localeCompare(b.name))}
              selectedOption={selectedCity}
              optionKey="i18nKey"
              onSelect={selectCity}
              t={t}
              isPTFlow={true}
              //isDependent={true}
              //labelKey="TENANT_TENANTS"
              disabled={isEditAddress}
            />
          </span>
          {selectedCity && localities && <CardLabel>{`${t("PTR_LOCALITY")} `}</CardLabel>}
          {selectedCity && localities && (
            <span className={"form-ptr-dropdown-only"}>
              <RadioOrSelect
                dropdownStyle={{ paddingBottom: "20px" }}
                isMandatory={config.isMandatory}
                options={localities.sort((a, b) => a.name.localeCompare(b.name))}
                selectedOption={selectedLocality}
                optionKey="i18nkey"
                onSelect={selectLocality}
                t={t}
                //isDependent={true}
                labelKey=""
                disabled={isEditAddress}
              />
            </span>
          )}
        </div>
      </FormStep>
=======
  // Fixing the locality data coming from the useboundarylocalities hook
  let structuredLocality = [];
  fetchedLocalities && fetchedLocalities.map((local, index) => {
    structuredLocality.push({i18nKey: local.i18nkey, code: local.code, label: local.label})
  })


  const setAddressPincode = (e) => {
    const newPincode = e.target.value.slice(0, 6);
    setPincode(newPincode);
  };

  const setPropertyId =(e)=> {
    setpropertyId(e.target.value);
  }

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
            setShowToast({ warning: true, label: t("PTR_LOADING_DETAILS") });
          } else if (isError) {
            setShowToast({ error: true, label: t("PTR_ERROR_FETCHING_DETAILS") });
          } else if (applicationDetails) {
            const streetNames = applicationDetails?.applicationData?.address?.street;
            const houseNumber = applicationDetails?.applicationData?.address?.doorNo;
            const pin =  applicationDetails?.applicationData?.address?.pincode;
            
            if (streetNames ||  houseNumber || pin) {
              setHouseNo(houseNumber);
              setStreetName(streetNames);
              setPincode(pin);
            }
          }
          setShouldFetchDetails(false);
        }
      }, [shouldFetchDetails, isLoading, isError, applicationDetails, error]);

  const goNext = () => {
    let owner = formData.address ;
    let ownerStep = { ...owner, pincode, city, locality, streetName, houseNo, landmark, houseName, addressline1, addressline2,propertyId };
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
          <CardLabel>{`${t("PTR_PROPERTY_NO")}`}</CardLabel>
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
              ValidationRequired={false}
              {...(validation = {
                isRequired: true,
                pattern: "^[a-zA-Z0-9/-]*$",
                type: "text",
                title: t("PT_NAME_ERROR_MESSAGE"),
              })}
            />
            <div style={{ position: "relative", zIndex: "100", right: user.type === "EMPLOYEE" ? "52%" :"95px", marginTop: "-14px", marginRight:"-20px", cursor:"pointer" }} onClick={handleSearchClick}> <SearchIcon /> </div>
            </div>
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
      {showToast && (
              <Toast
                error={showToast.error}
                warning={showToast.warning}
                label={t(showToast.label)}
                onClose={() => setShowToast(null)}
              />
            )}
>>>>>>> master-LTS
    </React.Fragment>
  );
};

export default PTRSelectAddress;
<<<<<<< HEAD
=======

>>>>>>> master-LTS
