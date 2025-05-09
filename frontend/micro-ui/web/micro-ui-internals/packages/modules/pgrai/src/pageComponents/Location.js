import { CardLabel, CardLabelError, FormStep, LabelFieldPair, TextInput, LocationIcon } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";

/**
 * Location Component
 * 
 * This component is used to capture and manage location details in a form step.
 * 
 * Props:
 * - t: Translation function for internationalization.
 * - config: Configuration object for the form step.
 * - onSelect: Function to handle the selection of the location and proceed to the next step.
 * - formData: Object containing the pre-filled form data, if any.
 * 
 * State:
 * - location: Stores the current location input by the user or fetched via geolocation.
 * - error: Stores any error messages related to location input or fetching.
 * - isFetchingLocation: Boolean to indicate whether the geolocation API is currently fetching the location.
 * 
 * Functions:
 * - handleLocationChange: Updates the location state when the user types in the input field.
 * - fetchCurrentLocation: Fetches the user's current geographic location using the browser's geolocation API.
 * - goNext: Validates the location input and triggers the onSelect function to proceed to the next step.
 * - onSkip: Skips the current step and triggers the onSelect function without location data.
 * 
 * Usage:
 * - This component is used in multi-step forms where location input is required.
 * - It provides both manual input and automatic fetching of the user's current location.
 */

const Location = ({ t, config, onSelect, formData }) => {
  const { pathname } = useLocation();
  const [location, setLocation] = useState(formData?.location || "");
  const [error, setError] = useState(null);
  const [isFetchingLocation, setIsFetchingLocation] = useState(false);

  useEffect(() => {
    if (formData && formData.location) {
      setLocation(formData.location);
    }
  }, [formData]);

  const handleLocationChange = (e) => {
    setLocation(e.target.value);
    if (error) setError(null);
  };

  const fetchCurrentLocation = () => {
    setIsFetchingLocation(true);
    if ("geolocation" in navigator) {
      navigator.geolocation.getCurrentPosition(
        (position) => {
          const { latitude, longitude } = position.coords;
          setLocation(`${latitude}, ${longitude}`);
          setIsFetchingLocation(false);
          if (error) setError(null);
        },
        (error) => {
          console.error("Error getting location:", error);
          setError(t("LOCATION_FETCH_FAILED"));
          setIsFetchingLocation(false);
        }
      );
    } else {
      setError(t("GEOLOCATION_NOT_SUPPORTED"));
      setIsFetchingLocation(false);
    }
  };

  const goNext = () => {
    if (!location.trim()) {
      setError(t("REQUIRED_FIELD"));
      return;
    }
    onSelect(config.key, { location });
  };

  const onSkip = () => onSelect();

  return (
    <React.Fragment>
      <FormStep config={config} onSelect={goNext} onSkip={onSkip}>
        <LabelFieldPair>
          <CardLabel>{`${t("GRIEVANCE_LOCATION")}`} <span className="astericColor">*</span></CardLabel>
          <div className="field" style={{ display:"flex", position: "relative" }}>
            <TextInput
              value={location}
              onChange={handleLocationChange}
              onBlur={(e) => !e.target.value && setError(t("REQUIRED_FIELD"))}
              style={{ paddingRight: "30px" }}
            />
            <div 
              style={{ 
                position: "relative", 
                // right: "8px", 
                // top: "100%", 
                left: "8px",
                cursor: "pointer" 
              }}
              onClick={fetchCurrentLocation}
            >
              <LocationIcon styles={{ width: "16px", border: "none" }} className="fill-path-primary-main" />
            </div>
          </div>
        </LabelFieldPair>
        {error && <CardLabelError>{error}</CardLabelError>}
        {isFetchingLocation && <div>{t("FETCHING_LOCATION")}</div>}
      </FormStep>
    </React.Fragment>
  );
};

export default Location;