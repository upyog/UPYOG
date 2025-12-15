/**
 * @description 
 * This component renders a **timeline** with multiple steps, indicating the progress of an application flow.
 * 
 * @props 
 * - `currentStep` (number): The current step in the timeline, default is 1.
 * - `flow` (string): Defines the type of flow. It determines the steps displayed in the timeline.
 * 
 * @functionality 
 * - **Dynamic Steps:** 
 *    - For the `STAKEHOLDER` flow, no steps are displayed.
 *    - For other flows, it shows steps like **Owner Details, Pet Details, Location Details, Document Details, and Summary**.
 * - **Tick Mark:** Displays a tick mark for completed steps and a step number for current or upcoming steps.
 * - **Responsive Layout:** 
 *    - Adapts styling for **mobile** and desktop views.
 *    - On desktop, it maintains a maximum and minimum width with auto margins.
 * - **Localization:** Uses translation keys to display step labels in the appropriate language.
 */

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
      actions = ["ES_TITILE_OWNER_DETAILS", "ES_TITILE_PET_DETAILS", "PTR_LOCATION_DETAILS", "ES_TITILE_DOCUMENT_DETAILS","PTR_SUMMARY"];
  }
};
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
