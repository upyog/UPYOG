import React from "react";
import { useTranslation } from "react-i18next";
import { TickMark } from "@nudmcdgnpm/digit-ui-react-components";

const actions = [
  "NOC_DETAILS",
  "NOC_PROPERTY_DETAILS",
  "NOC_APPLICANT_DETAILS",
  "NOC_DOCUMENT_DETAILS",
];

const Timeline = ({ currentStep = 1 }) => {
  const { t } = useTranslation();
  const isMobile = window.Digit.Utils.browser.isMobile();
  return (
    <div className="timeline-container" style={isMobile ? {} : { maxWidth: "960px", minWidth: "640px", marginRight: "auto" }} >
      {actions?.map((action, index, arr) => (
        <div className="timeline-checkpoint" key={index}>
          <div className="timeline-content">
            <span className={`circle ${index <= currentStep - 1 && 'active'}`}>{index < currentStep - 1 ? <TickMark /> : index + 1}</span>
            <span className="secondary-color">{t(action)}</span>
          </div>
          {index < arr.length - 1 && <span className={`line ${index < currentStep - 1 && 'active'}`}></span>}
        </div>
      ))}
    </div>
  );
};

export default Timeline;
