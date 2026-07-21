import React, { useEffect, useState, useMemo } from "react";
import {
  FormStep,
  TextInput,
  CardLabel,
  Dropdown,
  LinkButton,
  Loader,
  CardLabelError,
  LabelFieldPair,
  Toast,
} from "@nudmcdgnpm/digit-ui-react-components";
import Timeline from "../components/NocTimeline";
import GIS from "./GIS";

const NocLocationDetails = ({ t, config, onSelect, userType, formData }) => {
  const stateId = Digit.ULBService.getStateId();

  // Load cities
  const { data: allCities, isLoading: isCitiesLoading } = Digit.Hooks.useTenants();

  // Local state initialization from wizard state
  const [propertyId, setPropertyId] = useState(formData?.property?.location?.property?.propertyId || formData?.location?.propertyId || "");
  const [selectedCity, setSelectedCity] = useState(formData?.property?.location?.property?.city || formData?.location?.city || null);
  const [selectedLocality, setSelectedLocality] = useState(formData?.property?.location?.property?.locality || formData?.location?.locality || null);
  const [plotNo, setPlotNo] = useState(formData?.property?.location?.property?.plotNo || formData?.location?.plotNo || "");
  const [buildingName, setBuildingName] = useState(formData?.property?.location?.property?.buildingName || formData?.location?.buildingName || "");
  const [streetName, setStreetName] = useState(formData?.property?.location?.property?.streetName || formData?.location?.streetName || "");
  const [pincode, setPincode] = useState(formData?.property?.location?.property?.pincode || formData?.location?.pincode || "");
  const [latitude, setLatitude] = useState(formData?.property?.location?.property?.latitude || formData?.location?.latitude || "");
  const [longitude, setLongitude] = useState(formData?.property?.location?.property?.longitude || formData?.location?.longitude || "");
  const [selectedFireStation, setSelectedFireStation] = useState(formData?.property?.location?.property?.fireStation || formData?.location?.fireStation || null);

  const [isOpen, setIsOpen] = useState(false);
  const [error, setError] = useState(null);
  const [fieldErrors, setFieldErrors] = useState({});
  const [isSearchingProperty, setIsSearchingProperty] = useState(false);

  // Fetch localities for selected city
  const { data: fetchedLocalities, isLoading: isLocalitiesLoading } = Digit.Hooks.useBoundaryLocalities(
    selectedCity?.code,
    "revenue",
    {
      enabled: !!selectedCity,
    },
    t
  );

  // Fetch fire stations from MDMS
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

  // Filter fire stations based on selected city baseTenantId
  const filteredFirestations = useMemo(() => {
    if (!selectedCity || !fireStationsData) return [];
    return fireStationsData.filter((fs) => fs.baseTenantId === selectedCity.code);
  }, [selectedCity, fireStationsData]);

  // Handle city selection change (clears locality/fire station dependent values)
  const handleCityChange = (cityVal) => {
    setSelectedCity(cityVal);
    setSelectedLocality(null);
    setSelectedFireStation(null);
    setFieldErrors((prev) => ({ ...prev, city: null, locality: null, fireStation: null }));
  };

  // Property ID search callback (maps exact address fields back into state)
  const handlePropertySearch = async () => {
    if (!propertyId) return;
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
        setBuildingName(address.buildingName || "");
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

  // Form Validation
  const validateForm = () => {
    const errors = {};
    if (!selectedCity) errors.city = t("NOC_CITY_REQUIRED");
    if (!selectedLocality) errors.locality = t("NOC_LOCALITY_REQUIRED");
    if (!selectedFireStation) errors.fireStation = t("NOC_FIRESTATION_REQUIRED");

    if (plotNo) {
      const doorNoRegex = /^[^\$\"'<>?~`!@$%^={}\[\]*:;“”‘’]{1,50}$/i;
      if (!doorNoRegex.test(plotNo)) {
        errors.plotNo = t("ERR_PLOT_NO_INVALID");
      }
    }
    if (buildingName) {
      const bldgRegex = /^[^\$\"'<>?\\\\~`!@$%^()+={}\[\]*.:;“”‘’]{1,64}$/i;
      if (!bldgRegex.test(buildingName)) {
        errors.buildingName = t("ERR_BUILDING_NAME_INVALID");
      }
    }
    if (streetName) {
      const streetRegex = /^[^\$\"'<>?\\\\~`!@$%^()+={}\[\]*.:;“”‘’]{1,64}$/i;
      if (!streetRegex.test(streetName)) {
        errors.streetName = t("ERR_STREET_NAME_INVALID");
      }
    }
    if (pincode) {
      const pinRegex = /^[1-9][0-9]{5}$/i;
      if (!pinRegex.test(pincode)) {
        errors.pincode = t("ERR_PINCODE_INVALID");
      }
    }

    setFieldErrors(errors);
    if (Object.keys(errors).length > 0) {
      setError("NOC_ERROR_FILL_ALL_MANDATORY_DETAILS");
      return false;
    }
    setError(null);
    return true;
  };

  const goNext = () => {
    if (validateForm()) {
      onSelect(config.key, {
        propertyId,
        city: selectedCity,
        locality: selectedLocality,
        plotNo,
        buildingName,
        streetName,
        pincode,
        latitude,
        longitude,
        fireStation: selectedFireStation,
      });
    }
  };

  const onSkip = () => onSelect();

  function onSave(geo, pin, place) {
    if (geo) {
      setLatitude(geo.latitude || "");
      setLongitude(geo.longitude || "");
    }
    if (pin) {
      setPincode(pin);
    }
    setIsOpen(false);
  }

  const handleRemove = () => {
    setIsOpen(false);
  };

  if (isCitiesLoading || isFireStationsLoading) {
    return <Loader />;
  }

  if (isOpen) {
    return <GIS t={t} onSelect={onSelect} formData={formData} handleRemove={handleRemove} onSave={onSave} />;
  }

  // --- Employee UI Layout ---
  if (userType === "employee") {
    return (
      <div style={{ padding: "10px" }}>
        <LabelFieldPair>
          <CardLabel className="card-label-smaller">{t("NOC_PROPERTY_ID_LABEL")}</CardLabel>
          <div className="field" style={{ display: "flex", gap: "10px" }}>
            <TextInput
              t={t}
              type="text"
              name="propertyId"
              value={propertyId}
              onChange={(e) => setPropertyId(e.target.value)}
              placeholder={t("NOC_PROPERTY_ID_PLACEHOLDER")}
            />
            <button
              className="submit-bar"
              type="button"
              onClick={handlePropertySearch}
              style={{ minWidth: "80px", height: "40px", marginTop: "0px" }}
            >
              {isSearchingProperty ? t("NOC_SEARCHING") : t("NOC_SEARCH")}
            </button>
          </div>
        </LabelFieldPair>

        <LabelFieldPair>
          <CardLabel className="card-label-smaller">{t("NOC_PROPERTY_CITY_LABEL")} <span style={{ color: "red" }}>*</span></CardLabel>
          <Dropdown
            className="form-field"
            selected={selectedCity}
            option={allCities}
            select={handleCityChange}
            optionKey="code"
            t={t}
            placeholder={t("NOC_PROPERTY_CITY_PLACEHOLDER")}
          />
        </LabelFieldPair>
        {fieldErrors.city && <CardLabelError>{fieldErrors.city}</CardLabelError>}

        <LabelFieldPair>
          <CardLabel className="card-label-smaller">{t("NOC_PROPERTY_DETAILS_MOHALLA_LABEL")} <span style={{ color: "red" }}>*</span></CardLabel>
          <Dropdown
            className="form-field"
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
        </LabelFieldPair>
        {fieldErrors.locality && <CardLabelError>{fieldErrors.locality}</CardLabelError>}

        <LabelFieldPair>
          <CardLabel className="card-label-smaller">{t("NOC_PROPERTY_PLOT_NO_LABEL")}</CardLabel>
          <TextInput
            t={t}
            type="text"
            name="plotNo"
            value={plotNo}
            onChange={(e) => setPlotNo(e.target.value)}
            placeholder={t("NOC_PROPERTY_PLOT_NO_PLACEHOLDER")}
          />
        </LabelFieldPair>
        {fieldErrors.plotNo && <CardLabelError>{fieldErrors.plotNo}</CardLabelError>}

        <LabelFieldPair>
          <CardLabel className="card-label-smaller">{t("NOC_PROPERTY_DETAILS_BLDG_NAME_LABEL")}</CardLabel>
          <TextInput
            t={t}
            type="text"
            name="buildingName"
            value={buildingName}
            onChange={(e) => setBuildingName(e.target.value)}
            placeholder={t("NOC_PROPERTY_DETAILS_BLDG_NAME_PLACEHOLDER")}
          />
        </LabelFieldPair>
        {fieldErrors.buildingName && <CardLabelError>{fieldErrors.buildingName}</CardLabelError>}

        <LabelFieldPair>
          <CardLabel className="card-label-smaller">{t("NOC_PROPERTY_DETAILS_SRT_NAME_LABEL")}</CardLabel>
          <TextInput
            t={t}
            type="text"
            name="streetName"
            value={streetName}
            onChange={(e) => setStreetName(e.target.value)}
            placeholder={t("NOC_PROPERTY_DETAILS_SRT_NAME_PLACEHOLDER")}
          />
        </LabelFieldPair>
        {fieldErrors.streetName && <CardLabelError>{fieldErrors.streetName}</CardLabelError>}

        <LabelFieldPair>
          <CardLabel className="card-label-smaller">{t("NOC_PROPERTY_DETAILS_PIN_LABEL")}</CardLabel>
          <TextInput
            t={t}
            type="text"
            name="pincode"
            value={pincode}
            onChange={(e) => setPincode(e.target.value)}
            placeholder={t("NOC_PROPERTY_DETAILS_PIN_PLACEHOLDER")}
          />
        </LabelFieldPair>
        {fieldErrors.pincode && <CardLabelError>{fieldErrors.pincode}</CardLabelError>}

        <LabelFieldPair>
          <CardLabel className="card-label-smaller">{t("NOC_PROPERTY_DETAILS_FIRESTATION_LABEL")} <span style={{ color: "red" }}>*</span></CardLabel>
          <Dropdown
            className="form-field"
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
        </LabelFieldPair>
        {fieldErrors.fireStation && <CardLabelError>{fieldErrors.fireStation}</CardLabelError>}


      </div>
    );
  }

  // --- Citizen UI Layout ---
  return (
    <React.Fragment>
      <Timeline currentStep={4} />
      <FormStep
        config={config}
        onSelect={goNext}
        onSkip={onSkip}
        t={t}
        isDisabled={false}
      >
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
          onChange={(e) => setPlotNo(e.target.value)}
          placeholder={t("NOC_PROPERTY_PLOT_NO_PLACEHOLDER")}
        />
        {fieldErrors.plotNo && <CardLabelError>{fieldErrors.plotNo}</CardLabelError>}

        <CardLabel>{t("NOC_PROPERTY_DETAILS_BLDG_NAME_LABEL")}</CardLabel>
        <TextInput
          t={t}
          type="text"
          name="buildingName"
          value={buildingName}
          onChange={(e) => setBuildingName(e.target.value)}
          placeholder={t("NOC_PROPERTY_DETAILS_BLDG_NAME_PLACEHOLDER")}
        />
        {fieldErrors.buildingName && <CardLabelError>{fieldErrors.buildingName}</CardLabelError>}

        <CardLabel>{t("NOC_PROPERTY_DETAILS_SRT_NAME_LABEL")}</CardLabel>
        <TextInput
          t={t}
          type="text"
          name="streetName"
          value={streetName}
          onChange={(e) => setStreetName(e.target.value)}
          placeholder={t("NOC_PROPERTY_DETAILS_SRT_NAME_PLACEHOLDER")}
        />
        {fieldErrors.streetName && <CardLabelError>{fieldErrors.streetName}</CardLabelError>}

        <CardLabel>{t("NOC_PROPERTY_DETAILS_PIN_LABEL")}</CardLabel>
        <TextInput
          t={t}
          type="text"
          name="pincode"
          value={pincode}
          onChange={(e) => setPincode(e.target.value)}
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
          <LinkButton
            label={
              <div style={{ display: "flex", alignItems: "center", gap: "5px" }}>
                <span>{t("NOC_LOCATE_ON_MAP")}</span>
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M11 7C8.79 7 7 8.79 7 11C7 13.21 8.79 15 11 15C13.21 15 15 13.21 15 11C15 8.79 13.21 7 11 7ZM19.94 10C19.48 5.83 16.17 2.52 12 2.06V0H10V2.06C5.83 2.52 2.52 5.83 2.06 10H0V12H2.06C2.52 16.17 5.83 19.48 10 19.94V22H12V19.94C16.17 19.48 19.48 16.17 19.94 12H22V10H19.94ZM11 18C7.13 18 4 14.87 4 11C4 7.13 7.13 4 11 4C14.87 4 18 7.13 18 11C18 14.87 14.87 18 11 18Z" fill="#FE7A51" />
                </svg>
              </div>
            }
            onClick={() => setIsOpen(true)}
            style={{ color: "#FE7A51", cursor: "pointer", fontSize: "14px", fontWeight: "bold", padding: "10px 0px" }}
          />
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

export default NocLocationDetails;
