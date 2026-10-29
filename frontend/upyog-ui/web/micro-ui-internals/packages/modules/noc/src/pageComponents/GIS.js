/**
 * Displays a popup card using LocationSearchCard to allow users to search, pin,
and select geographical location details (latitude, longitude, pincode, place name) for an NOC application.
 */
import React, { useState } from "react";
import { LocationSearchCard } from "@nudmcdgnpm/digit-ui-react-components";

const GIS = ({ t, onSelect, formData = {}, handleRemove, onSave }) => {
  const [pincode, setPincode] = useState(formData?.location?.pincode || "");
  const [geoLocation, setGeoLocation] = useState({
    latitude: formData?.location?.latitude || "",
    longitude: formData?.location?.longitude || "",
  });
  const tenants = Digit.Hooks.useTenants();
  const [pincodeServicability, setPincodeServicability] = useState(null);
  const [placeName, setPlaceName] = useState("");
  let Webview = !Digit.Utils.browser.isMobile();

  /** Handles skipping GIS location selection and closing modal popup. */
  const onSkip = () => {
    if (handleRemove) handleRemove();
  };

  /** Updates pincode, geo-coordinates (latitude/longitude), and place name when location is updated. */
  const onChange = (code, location, place) => {
    setPincodeServicability(null);
    setPincode(code || "");
    setGeoLocation(location || {});
    setPlaceName(place || "");
  };

  return (
    <div className="noc-gis-fullwidth">
      <div className="noc-gis-top-spacing">
        <div style={Webview ? { marginLeft: "25%", marginRight: "25%" } : {}}>
          <LocationSearchCard
            className="noc-gis-card"
            header={t("NOC_GIS_LABEL")}
            cardText={t("")}
            nextText={t("NOC_PIN_LOCATION_LABEL")}
            skip={onSkip}
            t={t}
            position={geoLocation}
            onSave={() => onSave(geoLocation, pincode, placeName)}
            onChange={(code, location, place) => onChange(code, location, place)}
            disabled={pincode === ""}
            forcedError={t(pincodeServicability)}
            isPlaceRequired={true}
            handleRemove={handleRemove}
            Webview={Webview}
            isPopUp={true}
          />
        </div>
      </div>
    </div>
  );
};

export default GIS;
