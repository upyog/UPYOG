import React from "react";
import { useTranslation } from "react-i18next";
import { TickMark } from "@nudmcdgnpm/digit-ui-react-components";

let actions = [];

const getAction = (flow) => {
  switch (flow) {
    case "STAKEHOLDER":
      actions = [];
      break;
    default:
      actions = ["NDC_APPLICATION_DETAILS", "NDC_DOCUMENTS_REQUIRED", "NDC_SUMMARY"];
  }
};
// this timeline component is used to show the progress of the application in the citizen and employee create NDC application flow. It takes currentStep and flow as props and displays the checkpoints accordingly. 
// The checkpoints are defined based on the flow and the current step is highlighted with a tick mark if it is completed. The component is responsive and adjusts its layout based on the screen size.
const Timeline = ({ currentStep = 1, flow = "" }) => {
  const { t } = useTranslation();
  const isMobile = window.Digit.Utils.browser.isMobile();
  getAction(flow);
  
  return (
    <div className={`timeline-container ${isMobile ? '' : 'ndc-row-container'}`}>
      {actions.map((action, index, arr) => (
        <div className="timeline-checkpoint" key={index}>
          <div className="timeline-content">
            <span className={`circle ${index <= currentStep - 1 && "active"}`}>
              {index < currentStep - 1 ? <TickMark /> : index + 1}
            </span>
            <span className="secondary-color">{t(action)}</span>
          </div>
          {index < arr.length - 1 && <span className={`line ${index < currentStep - 1 && "active"}`}></span>}
        </div>
      ))}
    </div>
  );
};

export default Timeline;
