import React, { useState } from "react";
import { LocationSearchCard, LinkButton, Card } from "@upyog/digit-ui-react-components";
/**
 * GIS Component
 * 
 * This component provides a location search interface as a popup/modal.
 * It allows users to search and select geographic locations and validates them against available tenant pincodes.
 * 
 * @param {Object} props Component properties
 * @param {Function} props.t - Translation function for internationalization
 * @param {Function} props.onSelect - Callback when selection is complete
 * @param {Object} props.formData - Existing form data (optional)
 * @param {Function} props.handleRemove - Function to remove the location
 * @param {Function} props.onSave - Function to save the selected location
 */
const GIS = ({ t, onSelect, formData = {},handleRemove,onSave }) => {
  const [location, setlocation] = useState(formData?.address?.location || {});
  const tenants = Digit.Hooks.obps.useTenants();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateId = Digit.ULBService.getStateId();
  let Webview = !Digit.Utils.browser.isMobile();
  const onSkip = () => onSelect();
  const onChange = (code, location) => {
    const foundValue = tenants?.find((obj) => obj.pincode?.find((item) => item == code));
    if (!foundValue) {
      setlocation({});
    } else {
      
      setlocation(location);

    }
  };

  return (
    <div style={{position:"fixed",background:"#00000050",width:"100%",height:"100vh",top:"21px",left:"0"}}>
    <div style={{position:"relative",marginTop:"60px"}}>
  
    <div style={Webview?{marginLeft:"25%", marginRight:"25%"}:{}}>
    <LocationSearchCard
      style={{position:"relative",marginTop:"100px",marginBottom:"-100px"}}
      header={t("SV_GIS_LABEL")}
      cardText={t("")}
      nextText={t("SV_PIN_LOCATION_LABEL")}
      skip={onSkip}
      t={t}
      position={location}
      onSave={() => onSave(location)}
      onChange={(code, location) => onChange(code, location)}
      disabled={false}
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
