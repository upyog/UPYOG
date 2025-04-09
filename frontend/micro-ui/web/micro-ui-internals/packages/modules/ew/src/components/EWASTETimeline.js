// Importing necessary components and hooks from external libraries
import React from "react";
import { useTranslation } from "react-i18next"; // Hook for translations
import { TickMark } from "@nudmcdgnpm/digit-ui-react-components"; // Component for rendering a tick mark icon

// Array to store the list of actions for the timeline
let actions = [];

// Function to set the actions based on the flow type
const getAction = (flow) => {
  switch (flow) {
    case "STAKEHOLDER":
      actions = []; // No actions for the "STAKEHOLDER" flow
      break;
    default:
      // Default actions for the timeline
      actions = [
        "EWASTE_TITLE_PRODUCT_DETAILS", // Step for product details
        "EWASTE_TITLE_DOCUMENT_DETAILS", // Step for document details
        "EWASTE_TITLE_OWNER_DETAILS", // Step for owner details
        "EWASTE_LOCATION_DETAILS", // Step for location details
        "EWASTE_TITLE_SUMMARY", // Step for summary
      ];
  }
};

// Component to render the timeline for the E-Waste module
const Timeline = ({ currentStep = 1, flow = "" }) => {
  const { t } = useTranslation(); // Translation hook
  const isMobile = window.Digit.Utils.browser.isMobile(); // Check if the user is on a mobile device
  getAction(flow); // Set the actions based on the flow type

  return (
    <div className="timeline-container" style={isMobile ? {} : { maxWidth: "960px", minWidth: "640px", marginRight: "auto" }}>
      {/* Render each action as a checkpoint in the timeline */}
      {actions.map((action, index, arr) => (
        <div className="timeline-checkpoint" key={index}>
          <div className="timeline-content">
            {/* Render the circle with a tick mark if the step is completed */}
            <span className={`circle ${index <= currentStep - 1 && "active"}`}>
              {index < currentStep - 1 ? <TickMark /> : index + 1}
            </span>
            {/* Render the translated action label */}
            <span className="secondary-color">{t(action)}</span>
          </div>
          {/* Render the connecting line between checkpoints */}
          {index < arr.length - 1 && <span className={`line ${index < currentStep - 1 && "active"}`}></span>}
        </div>
      ))}
    </div>
  );
};

export default Timeline; // Exporting the component