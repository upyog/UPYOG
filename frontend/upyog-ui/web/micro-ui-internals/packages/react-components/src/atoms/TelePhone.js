import React from "react";
import PropTypes from "prop-types";
import { Phone } from "./svgindex";
import { WrapUnMaskComponent } from "..";
import { useState } from "react";

const TelePhone = ({ mobile, text, privacy }) => {
  const [unmaskedNumber, setunmaskedNumber] = useState("");
  return (
  <React.Fragment>
    {text}
    <div className="telephone">
      <div className="call">
        <Phone fillcolor={"FFFFFF"} className="rc-tele-phone-spacing" />
        {!privacy && <a href={`tel:${mobile}`}>{"+91"} {mobile}</a>}
        {privacy && (unmaskedNumber === "") && <span><WrapUnMaskComponent value={`+91 ${mobile}`} iseyevisible={mobile?.includes("*")?true:false} privacy={privacy} className="rc-tele-phone-bottom-spacing" setunmaskedNumber={setunmaskedNumber}></WrapUnMaskComponent></span>}
        {privacy && unmaskedNumber !== "" && <a href={`tel:${unmaskedNumber}`}>{unmaskedNumber}</a>}
      </div>
    </div>
  </React.Fragment>
)};

TelePhone.propTypes = {
  mobile: PropTypes.any,
  text: PropTypes.string,
};

TelePhone.defaultProps = {
  mobile: "",
  text: "",
};

export default TelePhone;
