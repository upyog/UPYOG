import React, { useState, useEffect } from "react";
// Added GeoLocationWithDigipin to replace the inline digipin display block
import { LocationSearchCard, GeoLocationWithDigipin } from "@nudmcdgnpm/digit-ui-react-components";
import Timeline from "../components/TLTimelineInFSM";

const FSMSelectGeolocation = ({ t, config, onSelect, formData = {} }) => {
  const [pincode, setPincode] = useState(formData?.address?.pincode || "");
  const [geoLocation, setGeoLocation] = useState(formData?.address?.geoLocation || {});
  const tenants = Digit.Hooks.fsm.useTenants();
  const [pincodeServicability, setPincodeServicability] = useState(null);
  // Initialise digipin from formData to preserve value on back-navigation
  const [digipin, setDigipin] = useState(formData?.address?.digipin || "");

  // Calls the backend Digipin API via TPService instead of the old local getDigiPin utility
  const handleFetchDigipin = async (latitude, longitude) => {
    try {
      const res = await Digit.TPService.generateDigipin(latitude, longitude);
      return res?.digipin || "";
    } catch (e) {
      console.error("Error fetching digipin in FSMSelectGeolocation:", e);
      return "";
    }
  };

  const onSkip = () => onSelect();

  // Made async to await digipin fetch after a valid pincode is entered
  const onChange = async (code, location) => {
    setPincodeServicability(null);
    const foundValue = tenants?.find((obj) => obj.pincode?.find((item) => item == code));
    if (!foundValue) {
      setPincodeServicability("CS_COMMON_PINCODE_NOT_SERVICABLE");
      setPincode("");
      setGeoLocation({});
    } else {
      setPincode(code);
      setGeoLocation(location);
      // Fetch and set digipin whenever a valid location is selected via pincode
      if (location?.latitude && location?.longitude) {
        const pin = await handleFetchDigipin(location.latitude, location.longitude);
        setDigipin(pin || "");
      }
    }
  };

  // On mount, auto-detect browser geolocation and generate digipin for it
  useEffect(() => {
    if ("geolocation" in navigator) {
      navigator.geolocation.getCurrentPosition(
        async (position) => {
          const { latitude, longitude } = position.coords;
          setGeoLocation({ latitude, longitude });
          const pin = await handleFetchDigipin(latitude, longitude);
          if (pin) setDigipin(pin);
        },
        (error) => console.error("Error getting location:", error)
      );
    }
  }, []);

  return (
    <React.Fragment>
      <Timeline currentStep={1} flow="APPLY" />
      {/* Replaced inline digipin <div> block with the reusable GeoLocationWithDigipin component */}
      <GeoLocationWithDigipin
        t={t}
        value={`${geoLocation?.latitude || ""}, ${geoLocation?.longitude || ""}`}
        onChange={({ geoTagLocation, latitude, longitude, digipin: pin }) => {
          setGeoLocation({ latitude, longitude });
          setDigipin(pin);
        }}
        onFetchDigipin={handleFetchDigipin}
        showDigipin
        showMapLink={true}
      />
      <LocationSearchCard
        header={t("CS_ADDCOMPLAINT_SELECT_GEOLOCATION_HEADER")}
        cardText={t("CS_ADDCOMPLAINT_SELECT_GEOLOCATION_TEXT")}
        nextText={t("CS_COMMON_NEXT")}
        skipAndContinueText={t("CORE_COMMON_SKIP_CONTINUE")}
        skip={onSkip}
        t={t}
        position={geoLocation}
        // Now also persists digipin alongside geoLocation and pincode on save
        onSave={() => onSelect(config.key, { ...formData.address, geoLocation, pincode, digipin })}
        onChange={(code, location) => onChange(code, location)}
        disabled={pincode === ""}
        forcedError={t(pincodeServicability)}
      />
    </React.Fragment>
  );
};

export default FSMSelectGeolocation;
