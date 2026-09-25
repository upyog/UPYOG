import React, { useEffect, useState } from "react";
import {
  FormStep,
  TextInput,
  CardLabel,
  Dropdown,
  InfoBannerIcon,
  LocationIcon,
  Card,
  CardHeader,
  CardCaption,
  Toast
} from "@nudmcdgnpm/digit-ui-react-components";
import { useLocation } from "react-router-dom";
import Timeline from "../components/ASTTimeline";
import { Controller, useForm } from "react-hook-form";
// import MarkPropertyMap from "./MarkPropertyMap";

const NewAsset = ({ t, config, onSelect, formData }) => {
  // const [geometry, setGeometry] = useState(formData.assetDetails?.geometry ||null);
  // const [showMap, setShowMap] = useState(false);
  // const [area, setArea] = useState(formData.assetDetails?.area || null);

  const groupPattern = [5, 4, 4, 7]; // first 5, then 4,4,4
  let currentIndex = 0;

  const [assetDetails, setAssetDetails] = useState(
    formData.assetDetails && formData.assetDetails.assetParentCategory === formData?.asset?.assettype?.code
      ? formData.assetDetails
      : { assetParentCategory: formData?.asset?.assettype?.code,
        geometry: formData.assetDetails?.geometry || null,
        area: formData.assetDetails?.area || null
       }
  );
  const [currentLocation, setCurrentLocation] = useState(null);


  const [categoriesWiseData, setCategoriesWiseData] = useState();

  //  * get @param city & state id
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateTenantId = Digit.ULBService.getStateId();

  //  This call with tenantId (Get city-level data)
  const cityResponseObject = Digit.Hooks.useCustomMDMS(tenantId, "ASSET", [{ name: "AssetParentCategoryFields" }], {
    select: (data) => {
      const formattedData = data?.["ASSET"]?.["AssetParentCategoryFields"];
      return formattedData;
    },
  });

  // This call with stateTenantId (Get state-level data)
  const stateResponseObject = Digit.Hooks.useEnabledMDMS(stateTenantId, "ASSET", [{ name: "AssetParentCategoryFields" }], {
    select: (data) => {
      const formattedData = data?.["ASSET"]?.["AssetParentCategoryFields"];
      return formattedData;
    },
  });

  useEffect(() => {
    let combinedData;
    // if city level master is not available then fetch  from state-level
    if (cityResponseObject?.data) {
      combinedData = cityResponseObject.data;
    } else if (stateResponseObject?.data) {
      combinedData = stateResponseObject.data;
    } else {
      combinedData = []; // Or an appropriate default value for empty data
      console.log("Both cityResponseObject and stateResponseObject data are unavailable.");
    }
    setCategoriesWiseData(combinedData);
  }, [cityResponseObject, stateResponseObject]);


  let formJson = [];
  if (Array.isArray(categoriesWiseData)) {
    // Filter categories based on the selected assetParentCategory
    formJson = categoriesWiseData
      .filter((category) => {
        const isMatch = category.assetParentCategory === formData?.asset?.assettype?.code || category.assetParentCategory === "COMMON";
        return isMatch;
      })
      .map((category) => category.fields) // Extract the fields array
      .flat() // Flatten the fields array
      .filter((field) => field.active === true && field.isNeeded !== false); // Filter by active status
  }

  const { pathname: url } = useLocation();
  let index = window.location.href.charAt(window.location.href.length - 1);
  let validation = {};
  const { control } = useForm();

  //  regexPattern function is use for validation
  const regexPattern = (columnType) => {
    if (!columnType) {
      return "^[-+]?([1-8]?\\d(\\.\\d+)?|90(\\.0+)?),\\s*[-+]?(180(\\.0+)?|((1[0-7]\\d)|([1-9]?\\d))(\\.\\d+)?)$";
    } else if (columnType === "number") {
      return "^[0-9]+(\\.[0-9]+)?$";
    } else if (columnType === "text") {
      return "^[-+]?([1-8]?\\d(\\.\\d+)?|90(\\.0+)?),\\s*[-+]?(180(\\.0+)?|((1[0-7]\\d)|([1-9]?\\d))(\\.\\d+)?)$";
    } else {
      return "^[-+]?([1-8]?\\d(\\.\\d+)?|90(\\.0+)?),\\s*[-+]?(180(\\.0+)?|((1[0-7]\\d)|([1-9]?\\d))(\\.\\d+)?)$";
    }
  };

  const goNext = () => {
    let owner = formData.assetDetails && formData.assetDetails[index];
    assetDetails.owner = owner;
    onSelect(config.key, assetDetails, false, index);
  };

  const onSkip = () => onSelect();

  // useEffect(() => {}, [assetDetails]); // Triggers when purchaseDate changes

  // Set State Dynamically!
  const handleInputChange = (e) => {
    // Get the name & value from the input and select field
    const { name, value } = e.target ? e.target : { name: e.name, value: e.code };

    if (name === "lifeOfAsset" && value.length > 3) {
      // Validation for life of Asset
      alert("Maximum limit is 3 digits only!");
      return false;
    }
    setAssetDetails((prevData) => {
      // Update the current field
      const updatedData = {
        ...prevData,
        [name]: value,
      };

      // Check if both acquisitionCost and purchaseCost are set and calculate bookValue
      const acquisitionCost = parseFloat(updatedData.acquisitionCost) || 0;
      const purchaseCost = parseFloat(updatedData.purchaseCost) || 0;

      if (acquisitionCost >= 0 || purchaseCost >= 0) {
        updatedData.bookValue = acquisitionCost + purchaseCost;
      }

      return updatedData;
    });
  };

  //  Get location
  const fetchCurrentLocation = (name) => {
    if ("geolocation" in navigator) {
      navigator.geolocation.getCurrentPosition(
        (position) => {
          const { latitude, longitude } = position.coords;
          const locationString = `${latitude}, ${longitude}`;
          setAssetDetails((prevDetails) => ({
            ...prevDetails,
            [name]: locationString, // Update the specific field
          }));
          // Set the location for the map component
          setCurrentLocation({ lat: latitude, lng: longitude });
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
      {/* {window.location.href.includes("/employee") ? <Timeline currentStep={2} /> : null} */}
      <Card>
        <CardCaption>
          {t(formData.asset.Department["value"])}/{formData.asset.assetclassification["value"]}/{formData.asset.assettype["value"]}/
          {formData.asset.assetsubtype["value"]}/{formData.asset.BookPagereference}
        </CardCaption>
      </Card>
      <FormStep
        config={config}
        onSelect={goNext}
        onSkip={onSkip}
        t={t}
        isDisabled={!assetDetails["purchaseDate"] || !assetDetails["modeOfPossessionOrAcquisition"] || !assetDetails["purchaseOrderNumber"]}
      >
        
      <React.Fragment>
            {/* Group 1: Basic Purchase Information */}
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '20px', marginBottom: '30px',  border: "1px solid rgb(101 43 43)", borderRadius: '8px', padding: '16px' }}>
      {assetDetails?.assetParentCategory === "LAND" && (
        <div>
          <div>
            {`${t("AST_SURVEY_NUMBER")}`}
            <div className="tooltip" style={{ width: "12px", height: "5px", marginLeft: "10px", display: "inline-flex", alignItems: "center" }}>
              <InfoBannerIcon />
              <span className="tooltiptext" style={{ whiteSpace: "pre-wrap", fontSize: "small", wordWrap: "break-word", width: "300px", marginLeft: "15px", marginBottom: "-10px" }}>
                {`${t("ASSET_SURVEY_NUMBER")}`}
              </span>
            </div>
          </div>
          <TextInput
            t={t}
            type={"text"}
            isMandatory={false}
            optionKey="i18nKey"
            name="surveyNumber"
            value={assetDetails["surveyNumber"]}
            onChange={handleInputChange}
            {...(validation = {
              isRequired: true,
              pattern: "^[a-zA-Z0-9/-]*$",
              type: "text",
              title: t("PT_NAME_ERROR_MESSAGE"),
            })}
            style={{ width: "100%" }}
          />
        </div>
      )}

      <div>
        <div>
          {`${t("AST_PURCHASE_DATE")}`} <span style={{ color: "red" }}>*</span>
          <div className="tooltip" style={{ width: "12px", height: "5px", marginLeft: "10px", display: "inline-flex", alignItems: "center" }}>
            <InfoBannerIcon />
            <span className="tooltiptext" style={{ whiteSpace: "pre-wrap", fontSize: "small", wordWrap: "break-word", width: "300px", marginLeft: "15px", marginBottom: "-10px" }}>
              {`${t("ASSET_PURCHASE_DATE")}`}
            </span>
          </div>
        </div>
        <TextInput
          t={t}
          type={"date"}
          isMandatory={false}
          optionKey="i18nKey"
          name={"purchaseDate"}
          value={assetDetails["purchaseDate"]}
          onChange={handleInputChange}
          style={{ width: "100%" }}
          max={new Date().toISOString().split("T")[0]}
          rules={{
            required: t("CORE_COMMON_REQUIRED_ERRMSG"),
            validDate: (val) => (/^\d{4}-\d{2}-\d{2}$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")),
          }}
        />
      </div>

      <div>
        <div>
          {`${t("AST_PURCHASE_ORDER")}`} <span style={{ color: "red" }}>*</span>
        </div>
        <TextInput
          t={t}
          type={"text"}
          isMandatory={false}
          optionKey="i18nKey"
          name="purchaseOrderNumber"
          value={assetDetails["purchaseOrderNumber"]}
          onChange={handleInputChange}
          {...(validation = {
            isRequired: true,
            pattern: "^[a-zA-Z0-9/-]*$",
            type: "text",
            title: t("PT_NAME_ERROR_MESSAGE"),
          })}
          style={{ width: "100%" }}
        />
      </div>

      <div>
        <div>
          {`${t("AST_LIFE")}`} <span style={{ color: "red" }}>*</span>
          <div className="tooltip" style={{ width: "12px", height: "5px", marginLeft: "10px", display: "inline-flex", alignItems: "center" }}>
            <InfoBannerIcon />
            <span className="tooltiptext" style={{ whiteSpace: "pre-wrap", fontSize: "small", wordWrap: "break-word", width: "300px", marginLeft: "15px", marginBottom: "-10px" }}>
              {`${t("ASSET_USEFUL_LIFECYCLE")} `}
            </span>
          </div>
        </div>
        <TextInput
          t={t}
          type={"text"}
          isMandatory={false}
          optionKey="i18nKey"
          name="lifeOfAsset"
          value={assetDetails["lifeOfAsset"]}
          onChange={handleInputChange}
          {...(validation = {
            isRequired: true,
            pattern: regexPattern("number"),
            type: "number",
            title: t("PT_NAME_ERROR_MESSAGE"),
          })}
          style={{ width: "100%" }}
        />
      </div>
       <div style={{ marginBottom: '30px' }}>
      <div>
        {`${t("AST_LOCATION_DETAILS")}`} <span style={{ color: "red" }}>*</span>
        <div className="tooltip" style={{ width: "12px", height: "5px", marginLeft: "10px", display: "inline-flex", alignItems: "center" }}>
          <InfoBannerIcon />
          <span className="tooltiptext" style={{ whiteSpace: "pre-wrap", fontSize: "small", wordWrap: "break-word", width: "300px", marginLeft: "15px", marginBottom: "-10px" }}>
            {`${t("ASSET_LOCATION_DETAILS")} `}
          </span>
        </div>
      </div>
      <div style={{ position: "relative" }}>
        <TextInput
          t={t}
          type={"text"}
          isMandatory={false}
          optionKey="i18nKey"
          name={"location"}
          value={assetDetails["location"] || ""}
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
          onClick={() => fetchCurrentLocation("location")}
          style={{
            position: "absolute",
            right: "0",
            top: "50%",
            transform: "translateY(-50%)",
            cursor: "pointer",
            display: "flex",
            alignItems: "center",
            padding: "2px 5px",
          }}
        >
          <LocationIcon styles={{ width: "16px", border: "none" }} className="fill-path-primary-main" />
        </div>
      </div>

      {/* {assetDetails?.location && (
        <div>
          <button style={{ color: "#a82227" }} onClick={() => setShowMap(true)}>
            Mark Asset on Map
          </button>
        </div>
      )}

      {showMap && (
        <MarkPropertyMap
          onGeometrySave={(geoJson) => {
            setGeometry(geoJson);
            setAssetDetails((prevDetails) => ({
              ...prevDetails,
              geometry: geoJson,
            }));
          }}
          onAreaSave={(polygonArea) => {
            setArea(polygonArea);
            setAssetDetails((prevDetails) => ({
              ...prevDetails,
              area: polygonArea,
            }));
          }}
          closeModal={() => setShowMap(false)}
          location={currentLocation}
        />
      )} */}
    </div>
    </div>

      {assetDetails?.assetParentCategory !== "LAND" && (
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '20px', marginBottom: '30px', border: "1px solid rgb(101 43 43)", borderRadius: '8px', padding: '16px' }}>
        <div>
          <div>
            {`${t("AST_INVOICE_DATE")}`} <span style={{ color: "red" }}>*</span>
            <div className="tooltip" style={{ width: "12px", height: "5px", marginLeft: "10px", display: "inline-flex", alignItems: "center" }}>
              <InfoBannerIcon />
              <span className="tooltiptext" style={{ whiteSpace: "pre-wrap", fontSize: "small", wordWrap: "break-word", width: "300px", marginLeft: "15px", marginBottom: "-10px" }}>
                {`${t("ASSET_INVOICE_ISSUE_DATE")} `}
              </span>
            </div>
          </div>
          <TextInput
            t={t}
            key={assetDetails["purchaseDate"] || "no-purchase"}
            type={"date"}
            isMandatory={false}
            optionKey="i18nKey"
            name={"invoiceDate"}
            value={assetDetails["invoiceDate"]}
            onChange={handleInputChange}
            style={{ width: "100%" }}
            min={assetDetails["purchaseDate"] || ""}
            disabled={!assetDetails["purchaseDate"]}
            rules={{
              required: t("CORE_COMMON_REQUIRED_ERRMSG"),
              validDate: (val) => (/^\d{4}-\d{2}-\d{2}$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")),
              validate: (val) => {
                if (!assetDetails["purchaseDate"]) return t("INVOICE_DATE_REQUIRES_PURCHASE_DATE");
                return true;
              },
            }}
          />
        </div>

        <div>
          <div>
            {`${t("AST_INVOICE_NUMBER")}`} <span style={{ color: "red" }}>*</span>
            <div className="tooltip" style={{ width: "12px", height: "5px", marginLeft: "10px", display: "inline-flex", alignItems: "center" }}>
              <InfoBannerIcon />
              <span className="tooltiptext" style={{ whiteSpace: "pre-wrap", fontSize: "small", wordWrap: "break-word", width: "300px", marginLeft: "15px", marginBottom: "-10px" }}>
                {`${t("ASSET_INVOICE_ISSUE_DATE")} `}
              </span>
            </div>
          </div>
          <TextInput
            t={t}
            type={"text"}
            isMandatory={false}
            optionKey="i18nKey"
            name="invoiceNumber"
            value={assetDetails["invoiceNumber"]}
            onChange={handleInputChange}
            {...(validation = {
              isRequired: true,
              pattern: "^[a-zA-Z0-9/-]*$",
              type: "text",
              title: t("PT_NAME_ERROR_MESSAGE"),
            })}
            style={{ width: "100%" }}
          />
        </div>
      </div>
    )}
     



    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '20px', marginBottom: '30px',  border: "1px solid rgb(101 43 43)", borderRadius: '8px', padding: '16px' }}>
      <div>
        <div>
          {`${t("AST_MARKET_RATE")}`}
          <div className="tooltip" style={{ width: "12px", height: "5px", marginLeft: "10px", display: "inline-flex", alignItems: "center" }}>
            <InfoBannerIcon />
            <span className="tooltiptext" style={{ whiteSpace: "pre-wrap", fontSize: "small", wordWrap: "break-word", width: "300px", marginLeft: "15px", marginBottom: "-10px" }}>
              {`${t("ASSET_MARKET_VALUE")} `}
            </span>
          </div>
        </div>
        <TextInput
          t={t}
          type={"number"}
          isMandatory={false}
          optionKey="i18nKey"
          name="marketRate"
          value={assetDetails["marketRate"]}
          onChange={handleInputChange}
          ValidationRequired={true}
          {...(validation = {
            isRequired: true,
            pattern: regexPattern("number"),
            type: "number",
            title: t("PT_NAME_ERROR_MESSAGE"),
          })}
          style={{ width: "100%" }}
        />
      </div>

      <div>
        <div>
          {`${t("AST_PURCHASE_COST")}`} <span style={{ color: "red" }}>*</span>
          <div className="tooltip" style={{ width: "12px", height: "5px", marginLeft: "10px", display: "inline-flex", alignItems: "center" }}>
            <InfoBannerIcon />
            <span className="tooltiptext" style={{ whiteSpace: "pre-wrap", fontSize: "small", wordWrap: "break-word", width: "300px", marginLeft: "15px", marginBottom: "-10px" }}>
              {`${t("ASSET_PURCHASE_COST")} `}
            </span>
          </div>
        </div>
        <TextInput
          t={t}
          type={"number"}
          isMandatory={false}
          optionKey="i18nKey"
          name="purchaseCost"
          value={assetDetails["purchaseCost"]}
          onChange={handleInputChange}
          ValidationRequired={true}
          {...(validation = {
            isRequired: true,
            pattern: regexPattern("number"),
            type: "number",
            title: t("PT_NAME_ERROR_MESSAGE"),
          })}
          style={{ width: "100%" }}
        />
      </div>

      <div>
        <div>
          {`${t("AST_ACQUISITION_COST")}`} <span style={{ color: "red" }}>*</span>
          <div className="tooltip" style={{ width: "12px", height: "5px", marginLeft: "10px", display: "inline-flex", alignItems: "center" }}>
            <InfoBannerIcon />
            <span className="tooltiptext" style={{ whiteSpace: "pre-wrap", fontSize: "small", wordWrap: "break-word", width: "300px", marginLeft: "15px", marginBottom: "-10px" }}>
              {`${t("ASSET_ACQUISITION_COST")} `}
            </span>
          </div>
        </div>
        <TextInput
          t={t}
          type={"text"}
          isMandatory={false}
          optionKey="i18nKey"
          name="acquisitionCost"
          value={assetDetails["acquisitionCost"]}
          onChange={handleInputChange}
          ValidationRequired={true}
          {...(validation = {
            isRequired: true,
            pattern: regexPattern("number"),
            type: "number",
            title: t("PT_NAME_ERROR_MESSAGE"),
          })}
          style={{ width: "100%" }}
        />
      </div>

      <div>
        <div>
          {`${t("AST_BOOK_VALUE")}`} <span style={{ color: "red" }}>*</span>
          <div className="tooltip" style={{ width: "12px", height: "5px", marginLeft: "10px", display: "inline-flex", alignItems: "center" }}>
            <InfoBannerIcon />
            <span className="tooltiptext" style={{ whiteSpace: "pre-wrap", fontSize: "small", wordWrap: "break-word", width: "300px", marginLeft: "15px", marginBottom: "-10px" }}>
              {`${t("ASSET_BOOK_VALUE")} `}
            </span>
          </div>
        </div>
        <TextInput
          t={t}
          type={"text"}
          isMandatory={false}
          optionKey="i18nKey"
          name="bookValue"
          value={assetDetails["bookValue"]}
          onChange={handleInputChange}
          {...(validation = {
            isRequired: true,
            pattern: "^[a-zA-Z0-9/-]*$",
            type: "text",
            title: t("PT_NAME_ERROR_MESSAGE"),
          })}
          style={{ width: "100%" }}
        />
      </div>
    </div>


        
          {/* Dynamic Fields - 2 column grid */}
    {/* <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '20px', border: '1px solid #ccc', borderRadius: '8px', boxShadow: 'rgb(0 0 0 / 26%) 0px 4px 24px', padding: '16px' }}>
      {formJson.map((row, index) => {
        if (row.conditionalField) {
          const { dependsOn, showWhen } = row.conditionalField;
          if (assetDetails[dependsOn] !== showWhen) {
            return null;
          }
        } */}
        {groupPattern.map((size, gIndex) => {
      const group = formJson.slice(currentIndex, currentIndex + size);
      currentIndex += size;

      return (
        <div
          key={gIndex}
          style={{
            display: "grid",
            gridTemplateColumns: "1fr 1fr",
            gap: "20px",
            border: "1px solid rgb(101 43 43)",
            borderRadius: "8px",
            padding: "16px",
            marginBottom: "20px",
          }}
        >
          {group.map((row, index) => {
            console.log("roewowow",row);
            if (row.conditionalField) {
              const { dependsOn, showWhen } = row.conditionalField;
              if (assetDetails[dependsOn] !== showWhen) {
                return null;
              }
            }

        return (
          <div key={index}>
            <div>
              {`${t(row.code)}`} {row.isMandatory ? <span style={{ color: "red" }}>*</span> : null}
              <div className="tooltip" style={{ width: "12px", height: "5px", marginLeft: "10px", display: "inline-flex", alignItems: "center" }}>
                <InfoBannerIcon />
                <span className="tooltiptext" style={{ whiteSpace: "pre-wrap", fontSize: "small", wordWrap: "break-word", width: "300px", marginLeft: "15px", marginBottom: "-10px" }}>
                  {`${t(row.code + "_INFO")} `}
                </span>
              </div>
            </div>

            {row.type === "date" ? (
              <TextInput
                t={t}
                type={"date"}
                isMandatory={false}
                optionKey="i18nKey"
                name={row.name}
                value={assetDetails[row.name]}
                onChange={handleInputChange}
                style={{ width: "100%" }}
                // max={new Date().toISOString().split("T")[0]}
                rules={{
                  required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                  validDate: (val) => (/^\d{4}-\d{2}-\d{2}$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")),
                }}
              />
            ) : row.type === "dropdown" ? (
              <Controller
                control={control}
                name={row.name}
                isMandatory={row.isMandatory}
                defaultValue={assetDetails[row.name] ? assetDetails[row.name] : ""}
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                render={(props) => (
                  <Dropdown
                    // className="form-field"
                    selected={assetDetails[row.code]}
                    select={(value) => handleInputChange({ name: row.name, ...value })}
                    option={row.options}
                    optionKey="i18nKey"
                    placeholder={"Select"}
                    isMandatory={row.isMandatory}
                    t={t}
                  />
                )}
              />
            ) : row.type === "num" ? (
              <TextInput
                t={t}
                type={"number"}
                isMandatory={row.isMandatory}
                optionKey="i18nKey"
                name={row.name}
                value={assetDetails[row.name] || ""}
                onChange={handleInputChange}
                {...(validation = {
                  isRequired: row.isMandatory,
                  pattern: regexPattern(row.columnType),
                  type: row.columnType,
                  title: t("PT_NAME_ERROR_MESSAGE"),
                })}
                style={{ width: "100%" }}
                readOnly={row.isReadOnly}
              />
            ) : row.addCurrentLocationButton === true ? (
              <div style={{ position: "relative" }}>
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
                    pattern: regexPattern(row.columnType),
                    type: row.columnType,
                    title: t("VALID_LAT_LONG"),
                  })}
                />
                <div
                  className="butt-icon"
                  onClick={() => fetchCurrentLocation(row.name)}
                  style={{
                    position: "absolute",
                    right: "0",
                    top: "50%",
                    transform: "translateY(-50%)",
                    cursor: "pointer",
                    display: "flex",
                    alignItems: "center",
                    padding: "2px 5px",
                  }}
                >
                  <LocationIcon styles={{ width: "16px", border: "none" }} className="fill-path-primary-main" />
                </div>
              </div>
            ) : (
              <TextInput
                t={t}
                type={row.type}
                isMandatory={row.isMandatory}
                optionKey="i18nKey"
                name={row.name}
                value={assetDetails[row.name] || ""}
                onChange={handleInputChange}
                {...(validation = {
                  isRequired: row.isMandatory,
                  pattern: regexPattern(row.columnType),
                  type: row.columnType,
                  title: t("PT_NAME_ERROR_MESSAGE"),
                })}
                style={{ width: "100%" }}
                readOnly={row.isReadOnly}
              />
            )}
          </div>
        );
      })}
    </div>
    );
    })}
  </React.Fragment>
      </FormStep>
    </React.Fragment>
  );
};
export default NewAsset;
