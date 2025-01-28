import React, { useState } from "react";
import { LocationSearchCard, LinkButton, Card } from "@nudmcdgnpm/digit-ui-react-components";

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
