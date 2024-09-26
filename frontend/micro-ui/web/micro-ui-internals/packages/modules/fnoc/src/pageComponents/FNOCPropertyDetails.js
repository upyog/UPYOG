import React, { useEffect, useState} from "react";
import { FormStep, TextInput, CardLabel, SearchIcon, Dropdown } from "@nudmcdgnpm/digit-ui-react-components";
import { Controller, useForm } from "react-hook-form";

const FNOCPropertyDetails = ({ t, config, onSelect, userType,formData }) => {
  const { control } = useForm();
  let validation = {};
  const [propertyId, setpropertyId] = useState(formData?.propertyDetails?.propertyId || "");
  const [buildingName, setbuildingName] = useState(formData?.propertyDetails?.buildingName || "");
  const [doorNo, setdoorNo] = useState(formData?.propertyDetails?.doorNo || "");
  const [city, setcity] = useState(formData?.propertyDetails?.city || "");  
  const [street, setstreet] = useState(formData?.propertyDetails?.street || "");
  const [locality, setlocality] = useState(formData?.propertyDetails?.locality || "");
  const [pincode, setpincode] = useState(formData?.propertyDetails?.pincode || "");
  const [firestationId, setfirestationId] = useState(formData?.propertyDetails?.firestationId || "");
  const [gisCode, setgisCode] = useState(formData?.propertyDetails?.gisCode || "");
  const [shouldFetchDetails, setShouldFetchDetails] = useState(false);
  const tenantId =  Digit.ULBService.getCitizenCurrentTenant();
  const stateId = Digit.ULBService.getStateId();
  const allCities = Digit.Hooks.fnoc.useTenants();
  const convertToObject = (String) => String ? { i18nKey: String, code: String, value: String } : null;
  let cities = [];
  allCities &&
  allCities.map((cityData) => {
       cities.push({ i18nKey: `${cityData.name}`, code: `${cityData.code}`, value: `${cityData.name}`, pinCode:`${cityData.pincode}` });
  });
  const {data:fetchedLocalities}  = Digit.Hooks.useBoundaryLocalities(
    tenantId,
     "revenue",
    {
      enabled: !!tenantId
    },
    t
  );
  let newLocalityData  = [];
  fetchedLocalities &&
  fetchedLocalities.map((localityData) => {
    newLocalityData.push({ i18nKey: `${localityData.name}`, code: `${localityData.code}`, value: `${localityData.name}`});
  });
  
  function setBuildingName(e) {
    setbuildingName(e.target.value);
  }
  function setPropertyId(e) {
    setpropertyId(e.target.value);
  }

  function setStreet(e) {
    setstreet(e.target.value);
  }

  function setPincode(e) {
    setpincode(e.target.value);
  }

  function setGisCode(e) {
    setgisCode(e.target.value);
  }

  function setDoorNo(e) {
    setdoorNo(e.target.value);
  }


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
        console.log("Property ID is required");
      }
    };
    // Effect to handle the fetched data
    useEffect(() => {
      if (shouldFetchDetails) {
        if (isLoading) {
          alert("Loading application details...");
        } else if (isError) {
          alert("Error fetching application details");
        } else if (applicationDetails) {
          const streetNames = applicationDetails?.applicationData?.address?.street;
          const plotNumber = applicationDetails?.applicationData?.address?.doorNo;
          const pin =  applicationDetails?.applicationData?.address?.pincode;
          const cityName =  applicationDetails?.applicationData?.address?.city;
          const localityName = applicationDetails?.applicationData?.address?.locality?.name
          if (streetNames &&  plotNumber && pin && cityName && localityName) {
            setdoorNo(plotNumber)
            setstreet(streetNames);
            setpincode(pin)
            setcity(convertToObject(cityName))
            setlocality(convertToObject(localityName))
          }
        }
        setShouldFetchDetails(false);
      }
    }, [shouldFetchDetails, isLoading, isError, applicationDetails, error]);

  const { data: fireStationMDMSData } = Digit.Hooks.useCustomMDMS(stateId, "firenoc", [{ name: "FireStations" }],
      {
        select: (data) => {
            const formattedData = data?.["firenoc"]?.["FireStations"]
            const activeData = formattedData?.filter((item) => {
              return item.baseTenantId === tenantId;
            });
            return activeData;
        },
    }); 
    let fireStationData = [];

    fireStationMDMSData && fireStationMDMSData.map((firestation) => {
      fireStationData.push({i18nKey: `${firestation.code}`, code: `${firestation.code}`, value: `${firestation.code}`})
    }) 

  const goNext = () => {
    let propertyDetails = formData.propertyDetails;
    let propertyStep;
    
      propertyStep = { ...propertyDetails, propertyId,street,buildingName, city, doorNo,locality, pincode, firestationId,gisCode};
      onSelect(config.key, { ...formData[config.key], ...propertyStep }, false);
    
  };

  const onSkip = () => onSelect();

  useEffect(() => {
    if (userType === "citizen") {
      goNext();
    }
  }, [propertyId, city, doorNo, street,locality, buildingName, pincode, firestationId,gisCode]);

  return (
    <React.Fragment>
    <FormStep
      config={config}
      onSelect={goNext}
      onSkip={onSkip}
      t={t}
      isDisabled={ !city || !locality || !firestationId}
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
      
            <CardLabel>{`${t("NOC_PROPERTY_ID_LABEL")}`}</CardLabel>
            <div className="field-container">
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              name="propertyId"
              value={propertyId}
              onChange={setPropertyId}
              style={{ width: "86%" }}
              ValidationRequired={false}
              {...(validation = {
                isRequired: true,
                pattern: "^[a-zA-Z0-9/-]*$",
                type: "text",
                title: t("PT_NAME_ERROR_MESSAGE"),
              })}
            />
            <div style={{ position: "relative", zIndex: "100", right: "95px", marginTop: "-14px", marginRight:"-20px", cursor:"pointer" }} onClick={handleSearchClick}> <SearchIcon /> </div>
            </div>
            <CardLabel>{`${t("NOC_PROPERTY_DETAILS_BLDG_NAME_LABEL")}`}</CardLabel>
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              name="buildingName"
              value={buildingName}
              onChange={setBuildingName}
              style={{ width: "86%" }}
              ValidationRequired={false}
              {...(validation = {
                isRequired: true,
                pattern: "^[a-zA-Z]*$",
                type: "text",
                title: t("PT_NAME_ERROR_MESSAGE"),
              })}
            />

            <CardLabel>{`${t("NOC_PROPERTY_PLOT_NO_LABEL")}`}</CardLabel>
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              name="doorNo"
              value={doorNo}
              onChange={setDoorNo}
              style={{ width: "86%" }}
              ValidationRequired={false}
              {...(validation = {
                isRequired: true,
                pattern: "^[0-9/-]*$",
                type: "text",
                title: t("PT_NAME_ERROR_MESSAGE"),
              })}
            />

            <CardLabel>{`${t("NOC_PROPERTY_CITY_LABEL")}`}</CardLabel>
            <Controller
              control={control}
              name={"city"}
              defaultValue={city}
              rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
              render={(props) => (
                <Dropdown
                  className="form-field"
                  selected={city}
                  select={setcity}
                  option={cities}
                  optionKey="i18nKey"
                  placeholder={"Select"}
                  t={t}
                />
              )}
            />

            <CardLabel>{`${t("NOC_PROPERTY_DETAILS_SRT_NAME_LABEL")}`}</CardLabel>
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              name="street"
              value={street}
              defaultValue={street}
              onChange={setStreet}
              style={{ width: "86%" }}
              ValidationRequired={false}
              {...(validation = {
                isRequired: true,
                pattern: "^[a-zA-Z]*$",
                type: "text",
                title: t("PT_NAME_ERROR_MESSAGE"),
              })}
            />
            <CardLabel>{`${t("NOC_PROPERTY_DETAILS_MOHALLA_LABEL")}`}</CardLabel>
            <Controller
              control={control}
              name={"locality"}
              defaultValue={locality}
              rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
              render={(props) => (
                <Dropdown
                  className="form-field"
                  selected={locality}
                  select={setlocality}
                  option={newLocalityData}
                  optionKey="i18nKey"
                  placeholder={"Select"}
                  t={t}
                />
              )}
            />
            
            <CardLabel>{`${t("NOC_PROPERTY_DETAILS_PIN_LABEL")}`}</CardLabel>
            <TextInput
              t={t}
              type={"textarea"}
              isMandatory={false}
              optionKey="i18nKey"
              name="pincode"
              value={pincode}
              onChange={setPincode}
              style={{ width: "86%" }}
              ValidationRequired={false}
              {...(validation = {
                isRequired: false,
                pattern: "^[0-9]{0,6}*$",
                type: "text",
                title: t("PT_NAME_ERROR_MESSAGE"),
              })}
            />
            <CardLabel>{`${t("NOC_PROPERTY_DETAILS_FIRESTATION_LABEL")}`}</CardLabel>
            <Controller
              control={control}
              name={"firestationId"}
              defaultValue={firestationId}
              rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
              render={(props) => (
                <Dropdown
                  className="form-field"
                  selected={firestationId}
                  select={setfirestationId}
                  option={fireStationData}
                  optionKey="i18nKey"
                  placeholder={"Select"}
                  t={t}
                />
              )}
            />
            <CardLabel>{`${t("NOC_PROPERTY_DETAILS_GIS_CORD_LABEL")}`}</CardLabel>
            <TextInput
              t={t}
              type={"textarea"}
              isMandatory={false}
              optionKey="i18nKey"
              name="gisCode"
              value={gisCode}
              onChange={setGisCode}
              style={{ width: "86%" }}
              ValidationRequired={false}
              {...(validation = {
                isRequired: false,
                pattern: "",
                type: "text",
                title: t("PT_NAME_ERROR_MESSAGE"),
              })}
            />
      </div>
    </FormStep>
    </React.Fragment>
  );
};

export default FNOCPropertyDetails;


