// Importing necessary components and hooks from external libraries and local files
import { CardLabel, CardLabelError, Dropdown, FormStep, LabelFieldPair, RadioOrSelect } from "@nudmcdgnpm/digit-ui-react-components"; // UI components for form steps, dropdowns, and labels
import _ from "lodash"; // Utility library for deep comparison
import React, { useEffect, useState } from "react"; // React hooks for state and lifecycle management
import { Controller, useForm } from "react-hook-form"; // React Hook Form for managing form state
import { useLocation } from "react-router-dom"; // Hook to access the current location
import Timeline from "../components/EWASTETimeline"; // Component for displaying the timeline

// Main component for selecting the address in the E-Waste module
const EWASTESelectAddress = ({ t, config, onSelect, userType, formData, setError, clearErrors, formState }) => {
  const allCities = Digit.Hooks.ew.useTenants(); // Fetching the list of all cities
  let tenantId = Digit.ULBService.getCurrentTenantId(); // Fetching the current tenant ID
  const { pathname } = useLocation(); // Extracting the current URL path
  const presentInModifyApplication = pathname.includes("modify"); // Check if the user is modifying an application

  // Determine if the address is being edited
  let isEditAddress = formData?.isEditAddress || false;
  if (presentInModifyApplication) isEditAddress = true;

  // Extracting pincode and city from the form data
  const { pincode, city } = formData?.address || "";

  // Filtering cities based on user type and pincode
  const cities =
    userType === "employee"
      ? allCities.filter((city) => city.code === tenantId)
      : pincode
      ? allCities.filter((city) => city?.pincode?.some((pin) => pin == pincode))
      : allCities;

  // State variables to manage selected city, localities, and selected locality
  const [selectedCity, setSelectedCity] = useState(() => {
    return formData?.address?.city || null;
  });
  const { data: fetchedLocalities } = Digit.Hooks.useBoundaryLocalities(
    selectedCity?.code,
    "revenue",
    {
      enabled: !!selectedCity,
    },
    t
  );
  const [localities, setLocalities] = useState();
  const [selectedLocality, setSelectedLocality] = useState();

  // Effect to set the locality when modifying an application
  useEffect(() => {
    if (userType === "employee" && presentInModifyApplication && localities?.length) {
      const code = formData?.originalData?.address?.locality?.code;
      const _locality = localities?.filter((e) => e.code === code)[0];
      setValue("locality", _locality);
    }
  }, [localities]);

  // Effect to automatically select the city if only one city is available
  useEffect(() => {
    if (cities) {
      if (cities.length === 1) {
        setSelectedCity(cities[0]);
      }
    }
  }, [cities]);

  // Effect to filter and set localities based on the selected city and pincode
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
      }
    }
  }, [selectedCity, formData?.address?.pincode, fetchedLocalities]);

  // Function to handle city selection
  function selectCity(city) {
    setSelectedLocality(null);
    setLocalities(null);
    setSelectedCity(city);
  }

  // Function to handle locality selection
  function selectLocality(locality) {
    if (formData?.address?.locality) {
      formData.address["locality"] = locality;
    }
    setSelectedLocality(locality);
    if (userType === "employee") {
      onSelect(config.key, { ...formData[config.key], locality: locality });
    }
  }

  // Function to handle form submission
  function onSubmit() {
    onSelect(config.key, { city: selectedCity, locality: selectedLocality });
  }

  const { control, formState: localFormState, watch, setValue } = useForm(); // React Hook Form methods
  const formValue = watch(); // Watching form values for changes
  const { errors } = localFormState; // Extracting errors from the form state
  const errorStyle = { width: "70%", marginLeft: "30%", fontSize: "12px", marginTop: "-21px" }; // Style for error messages

  // Effect to update form data when form values change
  useEffect(() => {
    if (userType === "employee") {
      let keys = Object.keys(formValue);
      const part = {};
      keys.forEach((key) => (part[key] = formData[config.key]?.[key]));
      if (!_.isEqual(formValue, part)) onSelect(config.key, { ...formData[config.key], ...formValue });
    }
  }, [formValue]);

  // Effect to handle form validation for employees
  useEffect(() => {
    if (userType === "employee") {
      const errorsPresent = !!Object.keys(localFormState.errors).length;
      if (errorsPresent && !formState.errors?.[config.key]) {
        // setError(config.key, { type: "required" });
      } else if (!errorsPresent && formState.errors?.[config.key]) {
        // clearErrors(config.key);
      }
    }
  }, [localFormState]);

  // Rendering the component for employees
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
          <CardLabel className="card-label-smaller">{t("EWASTE_LOCALITY") + " *"}</CardLabel>
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

  // Rendering the component for citizens
  return (
    <React.Fragment>
      {window.location.href.includes("/citizen") ? <Timeline currentStep={4} /> : null}
      <FormStep config={config} onSelect={onSubmit} t={t} isDisabled={selectedLocality ? false : true}>
        <div>
          <CardLabel>{`${t("MYCITY_CODE_LABEL")} `}</CardLabel>
          <span className={"form-ptr-dropdown-only"}>
            <RadioOrSelect
              options={cities?.sort((a, b) => a.name.localeCompare(b.name))}
              selectedOption={selectedCity}
              optionKey="i18nKey"
              onSelect={selectCity}
              t={t}
              isPTFlow={true}
              disabled={isEditAddress}
            />
          </span>
          {selectedCity && localities && <CardLabel>{`${t("EWASTE_LOCALITY")} `}</CardLabel>}
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
                disabled={isEditAddress}
              />
            </span>
          )}
        </div>
      </FormStep>
    </React.Fragment>
  );
};

export default EWASTESelectAddress; // Exporting the component