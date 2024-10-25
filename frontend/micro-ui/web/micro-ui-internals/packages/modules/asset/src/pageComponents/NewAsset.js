import React, { useEffect, useState } from "react";
import { FormStep, TextInput, CardLabel, Dropdown, InfoBannerIcon, LocationIcon } from "@nudmcdgnpm/digit-ui-react-components";
import { useLocation } from "react-router-dom";
import Timeline from "../components/ASTTimeline";
import { Controller, useForm } from "react-hook-form";
// import catagoriesWiseData from "./sample";

const NewAsset = ({ t, config, onSelect, formData }) => {
  const [assetDetails, setAssetDetails] = useState(
    formData.assetDetails && formData.assetDetails.assetParentCategory === formData?.asset?.assettype?.code
      ? formData.assetDetails
      : { assetParentCategory: formData?.asset?.assettype?.code }
  );

  // fetching master data from MDMS
  const { data: categoriesWiseData } = Digit.Hooks.useCustomMDMS(Digit.ULBService.getStateId(), "ASSET", [{ name: "AssetParentCategoryColumns" }], {
    select: (data) => {
      const formattedData = data?.["ASSET"]?.["AssetParentCategoryColumns"];
      return formattedData;
    },
  });

  let formJson = [];
 
  if (Array.isArray(categoriesWiseData)) {
    // Filter the array based on the selected asset type and active status
    formJson = categoriesWiseData.filter((item) => {
      return item["assetParentCategory"] === formData?.asset?.assettype?.code && item.active === true;
    });
  }

  const { pathname: url } = useLocation();
  let index = window.location.href.charAt(window.location.href.length - 1);
  let validation = {};
  const { control } = useForm();

  const goNext = () => {
    let owner = formData.assetDetails && formData.assetDetails[index];
    assetDetails.owner = owner;
    onSelect(config.key, assetDetails, false, index);
  };

  const onSkip = () => onSelect();

  const calculateAssetAge = (purchaseDate) => {
    const today = new Date();
    const purchaseDatetime = new Date(purchaseDate);
    const diffInYears = today.getFullYear() - purchaseDatetime.getFullYear();
    const diffInMonths = today.getMonth() - purchaseDatetime.getMonth();

    let age;
    if (diffInYears > 0) {
      age = diffInYears + " " + t("YEAR");
    } else if (diffInMonths >= 0) {
      age = diffInMonths + " " + t("MONTH");
    } else {
      const diffInDays = today.getDate() - purchaseDatetime.getDate();
      age = diffInDays + " " + t("DAY");
    }
    //  console.log('testing age is :- ',age);
    setAssetDetails((prevDetails) => ({
      ...prevDetails,
      assetAge: age,
    }));
  };

  // Set State Dynamically!
  const handleInputChange = (e) => {
    // Get the name & value from the input and select field
    const { name, value } = e.target ? e.target : { name: e.name, value: e };
    setAssetDetails((prevData) => ({
      ...prevData,
      [name]: value,
    }));
    name === "purchaseDate" && calculateAssetAge(value);
  };

  //  Get location
  const fetchCurrentLocation = (name) => {
    if ("geolocation" in navigator) {
      navigator.geolocation.getCurrentPosition(
        (position) => {
          const { latitude, longitude } = position.coords;
          setAssetDetails((prevDetails) => ({
            ...prevDetails,
            [name]: `${latitude}, ${longitude}`, // Update the specific field
          }));
        },
        (error) => {
          console.error("Error getting location:", error);
          alert("Unable to retrieve your location. Please check your browser settings.");
        }
      );
    } else {
      alert("Geolocation is not supported by your browser.");
    }
  };
  return (
    <React.Fragment>
      {window.location.href.includes("/employee") ? <Timeline currentStep={2} /> : null}

      <FormStep config={config} onSelect={goNext} onSkip={onSkip} t={t}>
        <React.Fragment>
          {formJson.map((row, index) => (
            <div key={index}>
              {/* Render the label with the localization key and a mandatory asterisk */}
              <CardLabel key={index}>{`${t(row.localizationKey)} *`}</CardLabel>

              {row.type === "date" ? (
                // If the type is 'date', render a standard HTML input element of type 'date'
                <TextInput
                  t={t}
                  type={"date"}
                  isMandatory={false}
                  optionKey="i18nKey"
                  name={row.name}
                  value={assetDetails[row.name]}
                  onChange={handleInputChange}
                  style={{ width: "50%" }}
                  max={new Date().toISOString().split("T")[0]}
                  rules={{
                    required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                    validDate: (val) => (/^\d{4}-\d{2}-\d{2}$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")),
                  }}
                />
              ) : row.type == "dropdown" ? (
                // if dropdown render
                <Controller
                  control={control}
                  name={row.name}
                  isMandatory={false}
                  defaultValue={assetDetails[row.name] ? assetDetails[row.name] : ""}
                  rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                  render={(props) => (
                    <Dropdown
                      className="form-field"
                      selected={assetDetails[row.name]}
                      select={handleInputChange}
                      option={row.options}
                      optionKey="i18nKey"
                      placeholder={"Select"}
                      isMandatory={false}
                      t={t}
                    />
                  )}
                />
              ) : row.addCurrentLocationButton === true ? (
                // if Fetch Location True
                <div style={{ position: "relative", width: "50%" }}>
                  <TextInput
                    t={t}
                    type={row.type}
                    isMandatory={row.isMandatory}
                    optionKey="i18nKey"
                    name={row.name}
                    value={assetDetails[row.name] || ""}
                    onChange={handleInputChange}
                    style={{ flex: 1 }}
                    ValidationRequired={false}
                    {...(validation = {
                      isRequired: true,
                      pattern: "^[-+]?([1-8]?\\d(\\.\\d+)?|90(\\.0+)?),\\s*[-+]?(180(\\.0+)?|((1[0-7]\\d)|([1-9]?\\d))(\\.\\d+)?)$",
                      type: "text",
                      title: t("VALID_LAT_LONG"),
                    })}
                  />
                  <div
                    className="butt-icon"
                    onClick={() => {
                      fetchCurrentLocation(row.name);
                    }}
                    style={{
                      position: "absolute",
                      right: "0", // Position the icon 10px from the right edge of the input
                      top: "50%",
                      transform: "translateY(-50%)",
                      cursor: "pointer",
                      display: "flex",
                      alignItems: "center",
                      padding: "2px 5px",
                    }}
                  >
                    {/* {t("AST_FETCH_LOCATION")} */}
                    <LocationIcon styles={{ width: "16px", border: "none" }} className="fill-path-primary-main" />
                  </div>
                </div>
              ) : (
                // If the type is not 'date', use the dynamically TextInput component
                <TextInput
                  t={t}
                  type={row.type}
                  isMandatory={row.isMandatory}
                  optionKey="i18nKey"
                  name={row.name}
                  value={assetDetails[row.name] || ""}
                  onChange={handleInputChange}
                  {...(validation = {
                    isRequired: true,
                    pattern: "^[a-zA-Z/-]*$",
                    type: "text",
                    title: t("PT_NAME_ERROR_MESSAGE"),
                  })}
                  style={{ width: "50%" }}
                />
              )}
            </div>
          ))}
        </React.Fragment>
      </FormStep>
    </React.Fragment>
  );
};
export default NewAsset;
