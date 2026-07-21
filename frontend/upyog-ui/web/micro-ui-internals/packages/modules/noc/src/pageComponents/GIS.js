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

  const onSkip = () => {
    if (handleRemove) handleRemove();
  };

  const onChange = (code, location, place) => {
    setPincodeServicability(null);
    setPincode(code || "");
    setGeoLocation(location || {});
    setPlaceName(place || "");
  };

  return (
    <div style={{ position: "fixed", background: "#00000050", width: "100%", height: "100vh", top: "0", left: "0", zIndex: 9999 }}>
      <div style={{ position: "relative", marginTop: "60px" }}>
        <div style={Webview ? { marginLeft: "25%", marginRight: "25%" } : {}}>
          <LocationSearchCard
            style={{ position: "relative", marginTop: "100px", marginBottom: "-100px" }}
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
