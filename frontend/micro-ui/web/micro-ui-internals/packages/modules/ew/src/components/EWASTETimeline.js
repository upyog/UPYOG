import React from "react";
import { useTranslation } from "react-i18next";
import { TickMark } from "@upyog/digit-ui-react-components";

let actions = [];

/**
 * Configures the timeline actions based on the application flow type.
 * For stakeholder flow, no actions are set. For all other flows,
 * default E-Waste application steps are configured.
 *
 * @param {string} flow - The type of application flow
 */
const getAction = (flow) => {
  switch (flow) {
    case "STAKEHOLDER":
      actions = [];
      break;
    default:
      actions = [
        "EWASTE_TITLE_PRODUCT_DETAILS",
        "EWASTE_TITLE_DOCUMENT_DETAILS",
        "EWASTE_TITLE_OWNER_DETAILS",
        "EWASTE_LOCATION_DETAILS",
        "EWASTE_TITLE_SUMMARY",
      ];
  }
};

/**
 * Renders a timeline component showing the progress of E-Waste application submission.
 * Displays steps as checkpoints with connecting lines, marking completed steps
 * with tick marks and highlighting the current step.
 *
 * @param {Object} props - Component properties
 * @param {number} [props.currentStep=1] - Current active step in the timeline
 * @param {string} [props.flow=""] - Type of application flow
 * @returns {JSX.Element} Timeline component with checkpoints
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