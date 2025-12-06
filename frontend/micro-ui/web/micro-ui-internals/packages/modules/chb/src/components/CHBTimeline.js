import React from "react";
import { useTranslation } from "react-i18next";
import { TickMark } from "@upyog/digit-ui-react-components";

let actions = [];

const getAction = (flow) => {
  switch (flow) {
    case "STAKEHOLDER":
      actions = [];
      break;
    default:
      actions = ["CHB_APPLICANT_DETAILS", "CHB_EVENT_DETAILS","CHB_ADDRESS_DETAILS","CHB_BANK_DETAILS", "CHB_UPLOAD_DOCUMENTS","CHB_SUMMARY"];
  }
};

/**
 * CHBTimeline Component
 * 
 * This component is responsible for rendering a visual timeline for the CHB (Community Hall Booking) module.
 * It displays the steps involved in the application process and highlights the current step.
 * 
 * Props:
 * - `currentStep`: The current step in the application process (default is 1).
 * - `flow`: The type of flow for the timeline (e.g., "STAKEHOLDER" or default flow).
 * 
 * Variables:
 * - `actions`: An array of actions/steps to be displayed in the timeline.
 *    - Default actions include:
 *      - "CHB_APPLICANT_DETAILS"
 *      - "CHB_EVENT_DETAILS"
 *      - "CHB_ADDRESS_DETAILS"
 *      - "CHB_BANK_DETAILS"
 *      - "CHB_UPLOAD_DOCUMENTS"
 *      - "CHB_SUMMARY"
 * - `isMobile`: Boolean indicating whether the application is being accessed on a mobile device.
 * 
 * Functions:
 * - `getAction`: Determines the actions/steps to display based on the `flow` prop.
 *    - For "STAKEHOLDER" flow, no actions are displayed.
 *    - For the default flow, a predefined set of actions is used.
 * 
 * Logic:
 * - Calls the `getAction` function to populate the `actions` array based on the `flow` prop.
 * - Maps over the `actions` array to render each step in the timeline.
 *    - Highlights the current step and marks completed steps with a tick mark.
 *    - Displays a connecting line between steps, with completed steps highlighted.
 * 
 * Returns:
 * - A timeline component that visually represents the steps in the application process.
 * - Highlights the current step and completed steps for better user guidance.
 */
const Timeline = ({ currentStep = 1, flow = "" }) => {
  const { t } = useTranslation();
  const isMobile = window.Digit.Utils.browser.isMobile();
  getAction(flow);
  return (
    <div className="timeline-container" style={isMobile ? {} : { maxWidth: "960px", minWidth: "640px", marginRight: "auto" }}>
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
