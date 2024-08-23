import { TickMark } from "@egovernments/digit-ui-react-components";
import React, { useState } from "react";
import { useTranslation } from "react-i18next";

let actions = [];

const getAction = (flow) => {
  switch (flow) {
    case "STAKEHOLDER":
      actions = [];
      break;
    default:
      actions = ["BMC_AADHAAR_VERIFICATION", "BMC_SELECT_SCHEME", "BMC_APPLICATION_FILL", "BMC_REVIEW"];
  }
};
const Timeline = ({ currentStep = 1, flow = "" }) => {
  const { t } = useTranslation();
  const [showTooltip, setShowTooltip] = useState(null);
  const isMobile = window.Digit.Utils.browser.isMobile();
  getAction(flow);

  const handleTooltip = (index) => {
    setShowTooltip(index === showTooltip ? null : index);
  };

  return (
    <div className="timeline-box" style={isMobile ? {} : { width: "100%", minWidth: "640px", marginRight: "auto" }}>
      {actions.map((action, index, arr) => (
        <div className="timeline-checkpoints" key={index}>
          <div className="timeline-contents">
            <span
              className={`circles ${index <= currentStep - 1 ? "active" : ""} ${index < currentStep - 1 ? "completed" : ""}`}
              onMouseEnter={() => handleTooltip(index)}
              onMouseLeave={() => handleTooltip(index)}
            >
              {index < currentStep - 1 ? <TickMark /> : <span className="circle"></span>}
              {isMobile && showTooltip === index && <span className="bmc-tooltip">{t(action)}</span>}
            </span>
            <span className="secondary-colors">{t(action)}</span>
          </div>
          {index < arr.length - 1 && <span className={`line ${index < currentStep - 1 && "active"}`}></span>}
        </div>
      ))}
    </div>
  );
};

export default Timeline;
