import React, { useState } from "react";
import TextInput from "./TextInput";
import { LocationIcon } from "./svgindex";

/**
 * DigipinDisplay
 *
 * Displays the generated DIGIPIN and optionally provides
 * a link to view the location on Mappls.
 */
const DigipinDisplay = ({
  t,
  digipin,
  style = {},
  className = "",
  showMapLink = false,
}) => {
  // Render nothing if no digipin value is available yet
  if (!digipin) return null;

  return (
    <div
      className={`digipin-display ${className}`.trim()}
      // Allow callers to override layout (e.g. width, marginTop) via style prop
      style={style}
    >
      {/* Label + digipin code */}
      <div>
        <strong>{t("CS_DIGIPIN_LABEL")}:</strong> {digipin}
      </div>

      {/* External Mappls link — only shown when showMapLink is true */}
      {showMapLink && (
        <a
          href={`https://mappls.com/digipin/${digipin}`}
          target="_blank"
          rel="noopener noreferrer"
          className="digipin-display__map-link"
        >
          {t("CS_VIEW_ON_MAPMYINDIA")}
        </a>
      )}
    </div>
  );
};

/**
 * GeoLocationWithDigipin
 *
 * Props:
 *  - t: translation function
 *  - value: current geoTagLocation string
 *  - onChange: called with
 *    { geoTagLocation, latitude, longitude, digipin }
 *  - inputStyle: optional style for the wrapper
 *  - name: input name
 *  - showDigipin: controls DIGIPIN visibility
 *  - showMapLink: controls Mappls link visibility
 *  - viewOnly: displays only DIGIPIN details
 *  - digipin: DIGIPIN value used in view-only mode
 *  - onFetchDigipin: function used to generate DIGIPIN
 */
const GeoLocationWithDigipin = ({
  t,
  value = "",
  onChange,
  inputStyle = {},
  name = "geoTagLocation",
  showDigipin = false,
  showMapLink = false,
  viewOnly = false,
  // Renamed to avoid shadowing the local digipin state below
  digipin: digipinProp = "",
  onFetchDigipin,
}) => {
  // Local digipin state updated after a successful geolocation fetch
  const [digipin, setDigipin] = useState("");

  // View-only mode: skip the input/button and just show the digipin display
  if (viewOnly) {
    return (
      <DigipinDisplay
        t={t}
        digipin={digipinProp}
        showMapLink={showMapLink}
        style={inputStyle}
      />
    );
  }

  // Requests the browser's current GPS position, then fetches the digipin for it
  const fetchCurrentLocation = () => {
    if (!("geolocation" in navigator)) {
      alert("Geolocation is not supported by your browser.");
      return;
    }

    navigator.geolocation.getCurrentPosition(
      async ({ coords: { latitude, longitude } }) => {
        let pin = "";

        try {
          // Delegate digipin generation to the parent-supplied onFetchDigipin callback
          if (onFetchDigipin) {
            pin = await onFetchDigipin(latitude, longitude);
          }
        } catch (error) {
          console.error("DIGIPIN error:", error);
        }

        setDigipin(pin);

        // Bubble coordinates and digipin up to the parent form
        onChange({
          geoTagLocation: `${latitude}, ${longitude}`,
          latitude,
          longitude,
          digipin: pin,
        });
      },

      (error) => {
        console.error("Location error:", error);
        alert(
          "Unable to retrieve your location. Please check your browser settings."
        );
      }
    );
  };

  // Handles free-text edits in the location input; clears digipin since coords are unknown
  const handleInputChange = (event) => {
    onChange({
      geoTagLocation: event.target.value,
      latitude: "",
      longitude: "",
      digipin: "",
    });

    setDigipin("");
  };

  return (
    <div>
      {/* Input row: text field for the location string + icon button to auto-detect */}
      <div
        className="geo-location-input-row"
        style={inputStyle}
      >
        <TextInput
          t={t}
          type="text"
          isMandatory={false}
          name={name}
          value={value}
          placeholder="Select Location"
          onChange={handleInputChange}
          className="location-input wt-auto-28"
        />

        {/* Location icon button — triggers GPS fetch on click */}
        <div
          className="butt-icon wt-auto-29"
          onClick={fetchCurrentLocation}
        >
          <LocationIcon className="fill-path-primary-main" />
        </div>
      </div>

      {/* Digipin display shown below the input after a successful GPS fetch */}
      {showDigipin && (
        <DigipinDisplay
          t={t}
          digipin={digipin}
          showMapLink={showMapLink}
          style={inputStyle}
          className="digipin-display--below-input"
        />
      )}
    </div>
  );
};

export default GeoLocationWithDigipin;
