/* 
 * NOC Property Details Component (Step 2 of the Citizen wizard flow)
 * Renders forms to collect building occupancy specs (no. of buildings, NBC codes, built-up area, height)
 * and location coordinates (via GIS integration, nearest fire station lookup, and Property ID matching).
 */
import React, { useEffect, useState, useMemo } from "react";
import {
  FormStep,
  TextInput,
  CardLabel,
  RadioButtons,
  Dropdown,
  LinkButton,
  Loader,
  CardLabelError,
  CardHeader,
  Toast,
} from "@nudmcdgnpm/digit-ui-react-components";
import Timeline from "../components/NocTimeline";
import GIS from "./GIS";

const NocPropertyDetails = ({ t, config, onSelect, userType, formData }) => {
  const stateId = Digit.ULBService.getStateId();

  // 1. Building MDMS and State
  const { data: buildingTypes, isLoading: isBuildingTypesLoading } = Digit.Hooks.useCustomMDMS(
    stateId,
    "firenoc",
    [{ name: "BuildingType" }],
    {
      select: (data) => {
        return data?.["firenoc"]?.["BuildingType"] || [];
      },
    }
  );

  const storedPropertyData = formData?.property;

  const initFn = (storedData) => {
    if (storedData && storedData.buildings && storedData.buildings.length > 0) {
      return storedData.buildings.map((b) => {
        let usageTypeMajor = b.buildingUsageType;
        if (typeof usageTypeMajor === "string") {
          usageTypeMajor = {
            code: usageTypeMajor,
            i18nKey: `FIRENOC_BUILDINGTYPE_${usageTypeMajor}`,
          };
        } else if (!usageTypeMajor && b.buildingSubUsageType) {
          const mCode = (
            typeof b.buildingSubUsageType === "string"
              ? b.buildingSubUsageType
              : b.buildingSubUsageType.code
          ).split(".")[0];
          usageTypeMajor = {
            code: mCode,
            i18nKey: `FIRENOC_BUILDINGTYPE_${mCode}`,
          };
        }

        let usageTypeSub = b.buildingSubUsageType;
        if (typeof usageTypeSub === "string") {
          usageTypeSub = {
            code: usageTypeSub,
            i18nKey: `FIRENOC_BUILDINGTYPE_${usageTypeSub.replaceAll(".", "_")}`,
            uom: [],
          };
        }

        return {
          name: b.name || "",
          buildingUsageType: usageTypeMajor || null,
          buildingSubUsageType: usageTypeSub || null,
          uomsMap: b.uomsMap || {
            NO_OF_FLOORS: "",
            NO_OF_BASEMENTS: "",
            PLOT_SIZE: "",
            BUILTUP_AREA: "",
            HEIGHT_OF_BUILDING: "",
          },
        };
      });
    }
    return [
      {
        name: "",
        buildingUsageType: null,
        buildingSubUsageType: null,
        uomsMap: {
          NO_OF_FLOORS: "",
          NO_OF_BASEMENTS: "",
          PLOT_SIZE: "",
          BUILTUP_AREA: "",
          HEIGHT_OF_BUILDING: "",
        },
      },
    ];
  };

  const [noOfBuildings, setNoOfBuildings] = useState(storedPropertyData?.noOfBuildings || "SINGLE");
  const [formState, setFormState] = useState(() => initFn(storedPropertyData));

  const floorsOptions = useMemo(() => {
    const list = [];
    for (let i = 1; i <= 20; i++) {
      list.push({ code: String(i), name: String(i) });
    }
    return list;
  }, []);

  const basementsOptions = useMemo(() => {
    const list = [];
    for (let i = 0; i <= 5; i++) {
      list.push({ code: String(i), name: String(i) });
    }
    return list;
  }, []);

  // 2. Location MDMS and State
  const { data: allCities, isLoading: isCitiesLoading } = Digit.Hooks.useTenants();

  const [propertyId, setPropertyId] = useState(formData?.property?.location?.property?.propertyId || formData?.location?.propertyId || "");
  const [selectedCity, setSelectedCity] = useState(formData?.property?.location?.property?.city || formData?.location?.city || null);
  const [selectedLocality, setSelectedLocality] = useState(formData?.property?.location?.property?.locality || formData?.location?.locality || null);
  const [plotNo, setPlotNo] = useState(formData?.property?.location?.property?.plotNo || formData?.location?.plotNo || "");
  const [locationBuildingName, setLocationBuildingName] = useState(formData?.property?.location?.property?.buildingName || formData?.location?.buildingName || "");
  const [streetName, setStreetName] = useState(formData?.property?.location?.property?.streetName || formData?.location?.streetName || "");
  const [pincode, setPincode] = useState(formData?.property?.location?.property?.pincode || formData?.location?.pincode || "");
  const [latitude, setLatitude] = useState(formData?.property?.location?.property?.latitude || formData?.location?.latitude || "");
  const [longitude, setLongitude] = useState(formData?.property?.location?.property?.longitude || formData?.location?.longitude || "");
  const [selectedFireStation, setSelectedFireStation] = useState(formData?.property?.location?.property?.fireStation || formData?.location?.fireStation || null);

  const [isOpen, setIsOpen] = useState(false);
  const [error, setError] = useState(null);
  const [fieldErrors, setFieldErrors] = useState({});
  const [bldgErrors, setBldgErrors] = useState({});
  const [isSearchingProperty, setIsSearchingProperty] = useState(false);

  /* Auto-dismisses warning toast messages after 3 seconds */
  useEffect(() => {
    if (error) {
      const timer = setTimeout(() => {
        setError(null);
      }, 3000);
      return () => clearTimeout(timer);
    }
  }, [error]);


  // Fetch localities
  const { data: fetchedLocalities, isLoading: isLocalitiesLoading } = Digit.Hooks.useBoundaryLocalities(
    selectedCity?.code,
    "revenue",
    {
      enabled: !!selectedCity,
    },
    t
  );

  // Fetch fire stations
  const { data: fireStationsData, isLoading: isFireStationsLoading } = Digit.Hooks.useCustomMDMS(
    stateId,
    "firenoc",
    [{ name: "FireStations" }],
    {
      select: (data) => {
        const list = data?.firenoc?.FireStations || [];
        return list
          .filter((fs) => fs.active)
          .map((fs) => ({
            code: fs.code,
            baseTenantId: fs.baseTenantId,
            i18nKey: `FIRENOC_FIRESTATIONS_${fs.code.toUpperCase()}`,
            name: t(`FIRENOC_FIRESTATIONS_${fs.code.toUpperCase()}`),
          }));
      },
    }
  );

  // Filter fire stations
  const filteredFirestations = useMemo(() => {
    if (!selectedCity || !fireStationsData) return [];
    return fireStationsData.filter((fs) => fs.baseTenantId === selectedCity.code);
  }, [selectedCity, fireStationsData]);

  // Enrich subUsageType uoms on load
  useEffect(() => {
    if (buildingTypes && buildingTypes.length > 0 && formState.length > 0) {
      let updated = false;
      const newState = formState.map((building) => {
        if (
          building.buildingSubUsageType &&
          (!building.buildingSubUsageType.uom || building.buildingSubUsageType.uom.length === 0)
        ) {
          const match = buildingTypes.find((bt) => bt.code === building.buildingSubUsageType.code);
          if (match) {
            updated = true;
            return {
              ...building,
              buildingSubUsageType: {
                ...building.buildingSubUsageType,
                uom: match.uom || [],
              },
            };
          }
        }
        return building;
      });
      if (updated) {
        setFormState(newState);
      }
    }
  }, [buildingTypes]);

  // Major building usage type list
  const majorCategories = useMemo(() => {
    if (!buildingTypes) return [];
    const uniqueMajors = new Set();
    buildingTypes.forEach((bt) => {
      if (bt.active) {
        const major = bt.code.split(".")[0];
        uniqueMajors.add(major);
      }
    });
    return Array.from(uniqueMajors).map((major) => ({
      code: major,
      i18nKey: `FIRENOC_BUILDINGTYPE_${major}`,
    }));
  }, [buildingTypes]);

  // Sub usage types
  const getSubUsageTypes = (selectedMajor) => {
    if (!buildingTypes || !selectedMajor) return [];
    return buildingTypes
      .filter((bt) => bt.active && bt.code.startsWith(`${selectedMajor.code}.`))
      .map((bt) => ({
        code: bt.code,
        i18nKey: `FIRENOC_BUILDINGTYPE_${bt.code.replaceAll(".", "_")}`,
        uom: bt.uom || [],
      }));
  };

  const handleCityChange = (cityVal) => {
    setSelectedCity(cityVal);
    setSelectedLocality(null);
    setSelectedFireStation(null);
    setFieldErrors((prev) => ({ ...prev, city: null, locality: null, fireStation: null }));
  };

  const handlePropertySearch = async () => {
    /* Validates empty input and sets search error to trigger warning toast */
    if (!propertyId) {
      setError("ERR_FILL_PROPERTY_ID");
      return;
    }

    if (!selectedCity) {
      setError("ERR_SELECT_CITY_TO_SEARCH_PROPERTY_ID");
      return;
    }
    setIsSearchingProperty(true);
    setError(null);
    try {
      const response = await Digit.PTService.search({
        tenantId: selectedCity.code,
        filters: { propertyIds: propertyId },
      });
      if (response && response.Properties && response.Properties.length > 0) {
        const property = response.Properties[0];
        const address = property.address || {};

        setPlotNo(address.doorNo || "");
        setLocationBuildingName(address.buildingName || "");
        setStreetName(address.street || "");
        setPincode(address.pincode || "");
        setLatitude(address.latitude || "");
        setLongitude(address.longitude || "");

        if (address.locality) {
          setSelectedLocality({
            code: address.locality.code,
            name: address.locality.name,
            i18nkey: `${selectedCity.code.toUpperCase().replace(/[.]/g, "_")}_REVENUE_${address.locality.code.toUpperCase().replace(/[._:-\s\/]/g, "_")}`,
          });
        }
      } else {
        setError("ERR_PROPERTY_NOT_FOUND_WITH_PROPERTY_ID");
      }
    } catch (err) {
      console.error(err);
      setError("ERR_PROPERTY_SEARCH_FAILED");
    } finally {
      setIsSearchingProperty(false);
    }
  };

  const handleNoOfBuildingsSelect = (value) => {
    setNoOfBuildings(value.code);
    if (value.code === "SINGLE" && formState.length > 1) {
      setFormState([formState[0]]);
    }
  };

  const handleAddBuilding = () => {
    setFormState([
      ...formState,
      {
        name: "",
        buildingUsageType: null,
        buildingSubUsageType: null,
        uomsMap: {
          NO_OF_FLOORS: "",
          NO_OF_BASEMENTS: "",
          PLOT_SIZE: "",
          BUILTUP_AREA: "",
          HEIGHT_OF_BUILDING: "",
        },
      },
    ]);
  };

  const handleRemoveBuilding = (index) => {
    if (formState.length > 1) {
      setFormState(formState.filter((_, i) => i !== index));
    }
  };

  const handleEditBuildingProperty = (index, key, value) => {
    setFormState((prevState) =>
      prevState.map((b, idx) => {
        if (idx === index) {
          if (key === "buildingUsageType") {
            return {
              ...b,
              buildingUsageType: value,
              buildingSubUsageType: null,
              uomsMap: {
                NO_OF_FLOORS: "",
                NO_OF_BASEMENTS: "",
                PLOT_SIZE: "",
                BUILTUP_AREA: "",
                HEIGHT_OF_BUILDING: "",
              },
            };
          } else if (key === "buildingSubUsageType") {
            const newUomsMap = {
              NO_OF_FLOORS: b.uomsMap?.NO_OF_FLOORS || "",
              NO_OF_BASEMENTS: b.uomsMap?.NO_OF_BASEMENTS || "",
              PLOT_SIZE: "",
              BUILTUP_AREA: "",
              HEIGHT_OF_BUILDING: "",
            };
            value?.uom?.forEach((uom) => {
              if (!newUomsMap.hasOwnProperty(uom)) {
                newUomsMap[uom] = "";
              }
            });
            return {
              ...b,
              buildingSubUsageType: value,
              uomsMap: newUomsMap,
            };
          } else {
            return {
              ...b,
              [key]: value,
            };
          }
        }
        return b;
      })
    );

    if (key === "name") {
      const bldgRegex = /^[^\$\"'<>?\\\\~`!@$%^()+={}\[\]*.:;“”‘’]{1,64}$/i;
      if (value && !bldgRegex.test(value)) {
        setBldgErrors((prev) => ({ ...prev, [index]: t("NOC_ERROR_INVALID_BUILDING_NAME") }));
      } else {
        setBldgErrors((prev) => {
          const copy = { ...prev };
          delete copy[index];
          return copy;
        });
      }
    }
  };

  const handleEditBuildingUom = (index, uomCode, value) => {
    setFormState((prevState) =>
      prevState.map((b, idx) => {
        if (idx === index) {
          return {
            ...b,
            uomsMap: {
              ...b.uomsMap,
              [uomCode]: value,
            },
          };
        }
        return b;
      })
    );
  };

  // Validations
  const validatePropertyForm = (stateData) => {
    if (Object.keys(bldgErrors).length > 0) {
      setError("NOC_ERROR_INVALID_BUILDING_NAME");
      return false;
    }
    for (let i = 0; i < stateData.length; i++) {
      const b = stateData[i];
      if (!b.name || !b.buildingUsageType || !b.buildingSubUsageType) {
        setError("NOC_ERROR_FILL_ALL_MANDATORY_DETAILS");
        return false;
      }

      const namePattern = Digit.Utils.getPattern("BuildingStreet");
      if (!namePattern.test(b.name)) {
        setError("NOC_ERROR_INVALID_BUILDING_NAME");
        return false;
      }

      if (!b.uomsMap?.NO_OF_FLOORS || !b.uomsMap?.NO_OF_BASEMENTS) {
        setError("NOC_ERROR_FILL_ALL_MANDATORY_DETAILS");
        return false;
      }

      const activeUoms = b.buildingSubUsageType?.uom || [];
      if (activeUoms.includes("PLOT_SIZE") && !b.uomsMap?.PLOT_SIZE) {
        setError("NOC_ERROR_FILL_ALL_MANDATORY_DETAILS");
        return false;
      }
      if (activeUoms.includes("BUILTUP_AREA") && !b.uomsMap?.BUILTUP_AREA) {
        setError("NOC_ERROR_FILL_ALL_MANDATORY_DETAILS");
        return false;
      }
      if (activeUoms.includes("HEIGHT_OF_BUILDING") && !b.uomsMap?.HEIGHT_OF_BUILDING) {
        setError("NOC_ERROR_FILL_ALL_MANDATORY_DETAILS");
        return false;
      }

      for (const uom of activeUoms) {
        if (!["NO_OF_FLOORS", "NO_OF_BASEMENTS", "PLOT_SIZE", "BUILTUP_AREA", "HEIGHT_OF_BUILDING"].includes(uom)) {
          if (!b.uomsMap?.[uom]) {
            setError("NOC_ERROR_FILL_ALL_MANDATORY_DETAILS");
            return false;
          }
        }
      }
    }
    return true;
  };

  const handleLocationFieldChange = (key, value, setter) => {
    setter(value);
    let errorMsg = null;
    if (value) {
      if (key === "plotNo") {
        const doorNoRegex = /^[^\$\"'<>?~`!@$%^={}\[\]*:;“”‘’]{1,50}$/i;
        if (!doorNoRegex.test(value)) {
          errorMsg = t("ERR_PLOT_NO_INVALID");
        }
      } else if (key === "locationBuildingName") {
        const bldgRegex = /^[^\$\"'<>?\\\\~`!@$%^()+={}\[\]*.:;“”‘’]{1,64}$/i;
        if (!bldgRegex.test(value)) {
          errorMsg = t("ERR_BUILDING_NAME_INVALID");
        }
      } else if (key === "streetName") {
        const streetRegex = /^[^\$\"'<>?\\\\~`!@$%^()+={}\[\]*.:;“”‘’]{1,64}$/i;
        if (!streetRegex.test(value)) {
          errorMsg = t("ERR_STREET_NAME_INVALID");
        }
      } else if (key === "pincode") {
        const pinRegex = /^[1-9][0-9]{5}$/i;
        if (!pinRegex.test(value)) {
          errorMsg = t("ERR_PINCODE_INVALID");
        }
      }
    }

    setFieldErrors((prev) => {
      const copy = { ...prev };
      if (errorMsg) {
        copy[key] = errorMsg;
      } else {
        delete copy[key];
      }
      return copy;
    });
  };

  const validateLocationForm = () => {
    const errors = {};
    if (!selectedCity) errors.city = t("NOC_CITY_REQUIRED");
    if (!selectedLocality) errors.locality = t("NOC_LOCALITY_REQUIRED");
    if (!selectedFireStation) errors.fireStation = t("NOC_FIRESTATION_REQUIRED");

    let hasFormatError = false;
    Object.keys(fieldErrors).forEach((key) => {
      if (["plotNo", "buildingName", "streetName", "pincode"].includes(key)) {
        hasFormatError = true;
      }
    });

    if (hasFormatError) {
      setError("NOC_ERROR_FILL_ALL_MANDATORY_DETAILS");
      return false;
    }

    setFieldErrors((prev) => ({ ...prev, ...errors }));
    if (Object.keys(errors).length > 0) {
      setError("NOC_ERROR_FILL_ALL_MANDATORY_DETAILS");
      return false;
    }
    setError(null);
    return true;
  };

  const goNext = () => {
    if (validatePropertyForm(formState) && validateLocationForm()) {
      const updatedData = {
        ...formData,
        property: {
          noOfBuildings,
          propertyType: { code: noOfBuildings },
          buildings: formState,
          propertyId,
          city: selectedCity,
          locality: selectedLocality,
          plotNo,
          buildingName: locationBuildingName,
          streetName,
          pincode,
          latitude,
          longitude,
          fireStation: selectedFireStation,
        }
      };

      onSelect(config.key, {
        noOfBuildings,
        propertyType: { code: noOfBuildings },
        buildings: formState,
        location: updatedData
      });
    }
  };

  const onSkip = () => onSelect();

  const updateCoordinates = (lat, lng) => {
    setLatitude(lat);
    setLongitude(lng);
  };

  const fetchCurrentLocation = () => {
    if ("geolocation" in navigator) {
      navigator.geolocation.getCurrentPosition(
        (position) => {
          const { latitude: lat, longitude: lng } = position.coords;
          updateCoordinates(lat, lng);
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

  const onSave = (geo, pin, place) => {
    if (geo?.latitude && geo?.longitude) {
      updateCoordinates(geo.latitude, geo.longitude);
    }
    if (pin) {
      setPincode(pin);
    }
    setIsOpen(false);
  };

  if (isBuildingTypesLoading || isCitiesLoading || isFireStationsLoading) {
    return <Loader />;
  }

  if (isOpen) {
    return <GIS t={t} onSelect={onSelect} formData={formData} handleRemove={() => setIsOpen(false)} onSave={onSave} />;
  }

  const noOfBuildingsOptions = [
    { code: "SINGLE", i18nKey: "NOC_NO_OF_BUILDINGS_SINGLE_RADIOBUTTON" },
    { code: "MULTIPLE", i18nKey: "NOC_NO_OF_BUILDINGS_MULTIPLE_RADIOBUTTON" },
  ];

  return (
    <React.Fragment>
      <Timeline currentStep={2} />
      <FormStep
        config={config}
        onSelect={goNext}
        onSkip={onSkip}
        t={t}
        isDisabled={false}
      >
        {/* --- SECTION A: Building Specs --- */}
        <CardHeader style={{ marginBottom: "20px" }}>{t("NOC_PROPERTY_DETAILS_HEADER")}</CardHeader>
        <CardLabel>{t("NOC_NO_OF_BUILDINGS_LABEL")}</CardLabel>
        <RadioButtons
          t={t}
          options={noOfBuildingsOptions}
          selectedOption={noOfBuildingsOptions.find((opt) => opt.code === noOfBuildings)}
          optionsKey="i18nKey"
          onSelect={handleNoOfBuildingsSelect}
        />

        {formState.map((field, index) => {
          const subUsageOptions = getSubUsageTypes(field.buildingUsageType);
          const activeUoms = field.buildingSubUsageType?.uom || [];

          return (
            <div
              key={index}
              style={
                noOfBuildings === "MULTIPLE"
                  ? {
                    border: "solid",
                    borderRadius: "5px",
                    padding: "15px",
                    paddingTop: "20px",
                    marginTop: "15px",
                    borderColor: "#f3f3f3",
                    background: "#FAFAFA",
                  }
                  : {}
              }
            >
              <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                <CardLabel style={{ fontWeight: "bold" }}>
                  {noOfBuildings === "MULTIPLE" ? `${t("NOC_BUILDING_LABEL")} - ${index + 1}` : t("NOC_BUILDING_LABEL")}
                </CardLabel>
                {noOfBuildings === "MULTIPLE" && formState.length > 1 && (
                  <LinkButton
                    label={t("NOC_REMOVE_BUILDING_LABEL")}
                    onClick={() => handleRemoveBuilding(index)}
                    style={{ color: "#FE7A51", fontSize: "14px" }}
                  />
                )}
              </div>

              <CardLabel>{t("NOC_PROPERTY_DETAILS_NAME_OF_BUILDING_LABEL")} <span style={{ color: "red" }}>*</span></CardLabel>
              <TextInput
                t={t}
                type="text"
                name={`building-name-${index}`}
                value={field.name || ""}
                onChange={(e) => handleEditBuildingProperty(index, "name", e.target.value)}
                placeholder={t("NOC_PROPERTY_DETAILS_NAME_OF_BUILDING_PLACEHOLDER")}
                validation={{
                  isRequired: true,
                  pattern: Digit.Utils.getPattern("BuildingStreet").source,
                  title: t("NOC_BUILDING_NAME_ERROR_MESSAGE"),
                }}
              />
              {bldgErrors?.[index] && <CardLabelError>{bldgErrors[index]}</CardLabelError>}

              <CardLabel>{t("NOC_PROPERTY_DETAILS_BUILDING_USAGE_TYPE_LABEL")} <span style={{ color: "red" }}>*</span></CardLabel>
              <Dropdown
                t={t}
                option={majorCategories}
                selected={field.buildingUsageType}
                select={(val) => handleEditBuildingProperty(index, "buildingUsageType", val)}
                optionKey="i18nKey"
              />

              <CardLabel>{t("NOC_PROPERTY_DETAILS_BUILDING_USAGE_SUBTYPE_LABEL")} <span style={{ color: "red" }}>*</span></CardLabel>
              <Dropdown
                t={t}
                option={subUsageOptions}
                selected={field.buildingSubUsageType}
                select={(val) => handleEditBuildingProperty(index, "buildingSubUsageType", val)}
                optionKey="i18nKey"
                disabled={!field.buildingUsageType}
              />

              <CardLabel>{t("NOC_PROPERTY_DETAILS_NO_OF_FLOORS_LABEL")} <span style={{ color: "red" }}>*</span></CardLabel>
              <Dropdown
                t={t}
                option={floorsOptions}
                selected={floorsOptions.find(opt => opt.code === field.uomsMap?.NO_OF_FLOORS) || null}
                select={(val) => handleEditBuildingUom(index, "NO_OF_FLOORS", val.code)}
                optionKey="name"
                placeholder={t("NOC_PROPERTY_DETAILS_NO_OF_FLOORS_PLACEHOLDER")}
              />

              <CardLabel>{t("NOC_PROPERTY_DETAILS_NO_OF_BASEMENTS_LABEL")} <span style={{ color: "red" }}>*</span></CardLabel>
              <Dropdown
                t={t}
                option={basementsOptions}
                selected={basementsOptions.find(opt => opt.code === field.uomsMap?.NO_OF_BASEMENTS) || null}
                select={(val) => handleEditBuildingUom(index, "NO_OF_BASEMENTS", val.code)}
                optionKey="name"
                placeholder={t("NOC_PROPERTY_DETAILS_NO_OF_BASEMENTS_PLACEHOLDER")}
              />

              <CardLabel>
                {t("NOC_PROPERTY_DETAILS_PLOT_SIZE_LABEL")}
                {activeUoms.includes("PLOT_SIZE") && <span style={{ color: "red" }}>*</span>}
              </CardLabel>
              <TextInput
                t={t}
                type="text"
                name={`plot-size-${index}`}
                value={field.uomsMap?.PLOT_SIZE || ""}
                onChange={(e) => handleEditBuildingUom(index, "PLOT_SIZE", e.target.value)}
                placeholder={t("NOC_PROPERTY_DETAILS_PLOT_SIZE_PLACEHOLDER")}
                ValidationRequired={true}
                validation={{
                  isRequired: activeUoms.includes("PLOT_SIZE"),
                  pattern: "^\\d{0,10}$",
                  type: "text",
                  title: t("NOC_PLOT_SIZE_ERROR_MESSAGE"),
                }}
              />

              <CardLabel>
                {t("NOC_PROPERTY_DETAILS_BUILTUP_AREA_LABEL")}
                {activeUoms.includes("BUILTUP_AREA") && <span style={{ color: "red" }}>*</span>}
              </CardLabel>
              <TextInput
                t={t}
                type="text"
                name={`builtup-area-${index}`}
                value={field.uomsMap?.BUILTUP_AREA || ""}
                onChange={(e) => handleEditBuildingUom(index, "BUILTUP_AREA", e.target.value)}
                placeholder={t("NOC_PROPERTY_DETAILS_BUILTUP_AREA_PLACEHOLDER")}
                ValidationRequired={true}
                validation={{
                  isRequired: activeUoms.includes("BUILTUP_AREA"),
                  pattern: "^\\d{0,10}$",
                  type: "text",
                  title: t("NOC_BUILTUP_AREA_ERROR_MESSAGE"),
                }}
              />

              <CardLabel>
                {t("NOC_PROPERTY_DETAILS_HEIGHT_OF_BUILDING_LABEL")}
                {activeUoms.includes("HEIGHT_OF_BUILDING") && <span style={{ color: "red" }}>*</span>}
              </CardLabel>
              <TextInput
                t={t}
                type="text"
                name={`height-of-building-${index}`}
                value={field.uomsMap?.HEIGHT_OF_BUILDING || ""}
                onChange={(e) => handleEditBuildingUom(index, "HEIGHT_OF_BUILDING", e.target.value)}
                placeholder={t("NOC_PROPERTY_DETAILS_HEIGHT_OF_BUILDING_PLACEHOLDER")}
                ValidationRequired={true}
                validation={{
                  isRequired: activeUoms.includes("HEIGHT_OF_BUILDING"),
                  pattern: "^\\d{0,10}$",
                  type: "text",
                  title: t("NOC_HEIGHT_OF_BUILDING_ERROR_MESSAGE"),
                }}
              />

              {activeUoms.map((uom) => {
                if (!["NO_OF_FLOORS", "NO_OF_BASEMENTS", "PLOT_SIZE", "BUILTUP_AREA", "HEIGHT_OF_BUILDING"].includes(uom)) {
                  return (
                    <React.Fragment key={uom}>
                      <CardLabel>{t(`NOC_PROPERTY_DETAILS_${uom}_LABEL`)} <span style={{ color: "red" }}>*</span></CardLabel>
                      <TextInput
                        t={t}
                        type="number"
                        name={`${uom}-${index}`}
                        value={field.uomsMap?.[uom] || ""}
                        onChange={(e) => handleEditBuildingUom(index, uom, e.target.value)}
                        placeholder={t(`NOC_PROPERTY_DETAILS_${uom}_PLACEHOLDER`)}
                        validation={{
                          isRequired: true,
                          pattern: "^[0-9]*$",
                          title: t(`NOC_PROPERTY_DETAILS_${uom}_ERROR_MESSAGE`),
                        }}
                      />
                    </React.Fragment>
                  );
                }
                return null;
              })}
            </div>
          );
        })}

        {noOfBuildings === "MULTIPLE" && (
          <div style={{ marginTop: "15px", marginBottom: "30px" }}>
            <button
              type="button"
              onClick={handleAddBuilding}
              style={{
                color: "#FE7A51",
                background: "none",
                border: "none",
                cursor: "pointer",
                fontWeight: "bold",
                padding: "10px 0px",
              }}
            >
              {t("NOC_ADD_BUILDING_LABEL")}
            </button>
          </div>
        )}

        {/* --- SECTION B: Location Details --- */}
        <CardHeader style={{ marginTop: "30px", marginBottom: "20px" }}>{t("NOC_LOCATION_DETAILS_HEADER")}</CardHeader>

        <CardLabel>{t("NOC_PROPERTY_ID_LABEL")}</CardLabel>
        <div style={{ display: "flex", gap: "10px", marginBottom: "15px" }}>
          <TextInput
            t={t}
            type="text"
            name="propertyId"
            value={propertyId}
            onChange={(e) => setPropertyId(e.target.value)}
            placeholder={t("NOC_PROPERTY_ID_PLACEHOLDER")}
            style={{ flex: 1 }}
          />
          <button
            className="submit-bar"
            type="button"
            onClick={handlePropertySearch}
            style={{ minWidth: "80px", height: "48px", marginTop: "0px" }}
          >
            {isSearchingProperty ? t("NOC_SEARCHING") : t("NOC_SEARCH")}
          </button>
        </div>

        <CardLabel>{t("NOC_PROPERTY_CITY_LABEL")} <span style={{ color: "red" }}>*</span></CardLabel>
        <Dropdown
          selected={selectedCity}
          option={allCities}
          select={handleCityChange}
          optionKey="code"
          t={t}
          placeholder={t("NOC_PROPERTY_CITY_PLACEHOLDER")}
        />
        {fieldErrors.city && <CardLabelError>{fieldErrors.city}</CardLabelError>}

        <CardLabel>{t("NOC_PROPERTY_DETAILS_MOHALLA_LABEL")} <span style={{ color: "red" }}>*</span></CardLabel>
        <Dropdown
          selected={selectedLocality}
          option={fetchedLocalities || []}
          select={(val) => {
            setSelectedLocality(val);
            setFieldErrors((prev) => ({ ...prev, locality: null }));
          }}
          optionKey="i18nkey"
          t={t}
          disabled={!selectedCity}
          placeholder={t("NOC_PROPERTY_DETAILS_MOHALLA_PLACEHOLDER")}
        />
        {fieldErrors.locality && <CardLabelError>{fieldErrors.locality}</CardLabelError>}

        <CardLabel>{t("NOC_PROPERTY_PLOT_NO_LABEL")}</CardLabel>
        <TextInput
          t={t}
          type="text"
          name="plotNo"
          value={plotNo}
          onChange={(e) => handleLocationFieldChange("plotNo", e.target.value, setPlotNo)}
          placeholder={t("NOC_PROPERTY_PLOT_NO_PLACEHOLDER")}
        />
        {fieldErrors.plotNo && <CardLabelError>{fieldErrors.plotNo}</CardLabelError>}

        <CardLabel>{t("NOC_PROPERTY_DETAILS_BLDG_NAME_LABEL")}</CardLabel>
        <TextInput
          t={t}
          type="text"
          name="locationBuildingName"
          value={locationBuildingName}
          onChange={(e) => handleLocationFieldChange("locationBuildingName", e.target.value, setLocationBuildingName)}
          placeholder={t("NOC_PROPERTY_DETAILS_BLDG_NAME_PLACEHOLDER")}
        />
        {fieldErrors.buildingName && <CardLabelError>{fieldErrors.buildingName}</CardLabelError>}

        <CardLabel>{t("NOC_PROPERTY_DETAILS_SRT_NAME_LABEL")}</CardLabel>
        <TextInput
          t={t}
          type="text"
          name="streetName"
          value={streetName}
          onChange={(e) => handleLocationFieldChange("streetName", e.target.value, setStreetName)}
          placeholder={t("NOC_PROPERTY_DETAILS_SRT_NAME_PLACEHOLDER")}
        />
        {fieldErrors.streetName && <CardLabelError>{fieldErrors.streetName}</CardLabelError>}

        <CardLabel>{t("NOC_PROPERTY_DETAILS_PIN_LABEL")}</CardLabel>
        <TextInput
          t={t}
          type="text"
          name="pincode"
          value={pincode}
          onChange={(e) => handleLocationFieldChange("pincode", e.target.value, setPincode)}
          placeholder={t("NOC_PROPERTY_DETAILS_PIN_PLACEHOLDER")}
        />
        {fieldErrors.pincode && <CardLabelError>{fieldErrors.pincode}</CardLabelError>}

        <CardLabel>{t("NOC_PROPERTY_DETAILS_GIS_CORD_LABEL")}</CardLabel>
        <div style={{ display: "flex", gap: "10px", alignItems: "center", marginBottom: "15px" }}>
          <TextInput
            t={t}
            type="text"
            name="gis"
            value={latitude && longitude ? `${latitude}, ${longitude}` : ""}
            disabled={true}
            placeholder={t("NOC_PROPERTY_DETAILS_GIS_CORD_PLACEHOLDER")}
            style={{ flex: 1 }}
          />
          <div style={{ display: "flex", flexDirection: "column", gap: "5px" }}>
            <LinkButton
              label={
                <div style={{ display: "flex", alignItems: "center", gap: "5px" }}>
                  <span>{t("NOC_CURRENT_LOCATION")}</span>
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M12 8c-2.21 0-4 1.79-4 4s1.79 4 4 4 4-1.79 4-4-1.79-4-4-4zm8.94 3c-.46-4.17-3.77-7.48-7.94-7.94V1h-2v2.06C6.83 3.52 3.52 6.83 3.06 11H1v2h2.06c.46 4.17 3.77 7.48 7.94 7.94V23h2v-2.06c4.17-.46 7.48-3.77 7.94-7.94H23v-2h-2.06zM12 19c-3.87 0-7-3.13-7-7s3.13-7 7-7 7 3.13 7 7-3.13 7-7 7z" fill="#FE7A51" />
                  </svg>
                </div>
              }
              onClick={fetchCurrentLocation}
              style={{ color: "#FE7A51", cursor: "pointer", fontSize: "14px", fontWeight: "bold", padding: "5px 0px" }}
            />
            <LinkButton
              label={
                <div style={{ display: "flex", alignItems: "center", gap: "5px" }}>
                  <span>{t("NOC_CHOOSE_LOCATION")}</span>
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7zm0 9.5c-1.38 0-2.5-1.12-2.5-2.5s1.12-2.5 2.5-2.5 2.5 1.12 2.5 2.5-1.12 2.5-2.5 2.5z" fill="#FE7A51" />
                  </svg>
                </div>
              }
              onClick={() => setIsOpen(true)}
              style={{ color: "#FE7A51", cursor: "pointer", fontSize: "14px", fontWeight: "bold", padding: "5px 0px" }}
            />
          </div>
        </div>

        <CardLabel>{t("NOC_PROPERTY_DETAILS_FIRESTATION_LABEL")} <span style={{ color: "red" }}>*</span></CardLabel>
        <Dropdown
          selected={selectedFireStation}
          option={filteredFirestations}
          select={(val) => {
            setSelectedFireStation(val);
            setFieldErrors((prev) => ({ ...prev, fireStation: null }));
          }}
          optionKey="i18nKey"
          t={t}
          disabled={!selectedCity}
          placeholder={t("NOC_PROPERTY_DETAILS_FIRESTATION_PLACEHOLDER")}
        />
        {fieldErrors.fireStation && <CardLabelError>{fieldErrors.fireStation}</CardLabelError>}
      </FormStep>
      {error && <Toast error={true} label={t(error)} onClose={() => setError(null)} />}
    </React.Fragment>
  );
};

export default NocPropertyDetails;
