import React from "react";
import { useTranslation } from "react-i18next";
import { TickMark } from "@nudmcdgnpm/digit-ui-react-components";

/* 
Custom component to show the timeline in the UI forms for both citizen and employee side 
It uses an array of predefined actions based on the flow type, and renders each action as 
a checkpoint in the timeline, showing progress through tick marks or numbers.
*/

let actions = ["SV_VENDOR_PERSONAL_DETAILS", "SV_VENDOR_BUSINESS_DETAILS", "SV_ADDRESS_DETAILS", "SV_BANK_DETAILS","SV_DOCUMENT_DETAILS_LABEL", "SV_SUMMARY_PAGE"];

const Timeline = ({ currentStep = 1 }) => {
  const { t } = useTranslation();
  const user = Digit.UserService.getUser().info;
  const isMobile = window.Digit.Utils.browser.isMobile();
  return (
    <div className="timeline-container" style={isMobile ? {} : user?.type==="EMPLOYEE"?{maxWidth: "960px", minWidth: "640px", marginRight: "auto", marginLeft: "auto", display: "flex", justifyContent: "center"}:{maxWidth: "960px", minWidth: "640px", marginRight: "auto", display: "flex", justifyContent: "center"} }>
      {actions.map((action, index, arr) => (
        <div className="timeline-checkpoint" key={index}>
          <div className="timeline-content">
            <span className={`circle ${index <= currentStep - 1 && "active"}`}>{index < currentStep - 1 ? <TickMark /> : index + 1}</span>
            <span className="secondary-color">{t(action)}</span>
          </div>
          {index < arr.length - 1 && <span className={`line ${index < currentStep - 1 && "active"}`}></span>}
        </div>
      ))}
    </div>
  );
};

export default Timeline;
