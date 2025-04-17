import React from "react";
import { useTranslation } from "react-i18next";
import { TickMark } from "@nudmcdgnpm/digit-ui-react-components";
import { useLocation } from "react-router-dom";

/**
 * @author Shivank - NUDM
 * Timeline Component
 * ------------------
 * This component renders a dynamic progress timeline based on the config.
 *
 * - It extracts steps from the config where `timeLine` data is available.
 * - Determines the current step based on the route using React Router's `useLocation`.
 * - Highlights completed steps with a tick mark and the current step with a number.
 * - Supports both mobile and desktop layouts and adjusts layout based on user type (citizen/employee).
 *
 * The timeline is auto-sorted based on `currentStep`, and route-matching logic ensures the correct
 * step is active based on the URL.
 * 
 * Also have hideOnRoutes which you can control from parent component 
 */


const Timeline = ({ config, hideOnRoutes=[] }) => {
  const { t } = useTranslation();
  const user = Digit.UserService.getUser().info;
  const isMobile = window.Digit.Utils.browser.isMobile();
  const location = useLocation();
  const currentRoute = location.pathname.split("/").pop();
  /**
   * This filters out any config items that do not have a timeLine array or if the timeLine array is empty.
   * Array.isArray(item.timeLine) ensures that timeLine is indeed an array.
   * item.timeLine.length > 0 ensures the array isn't empty.
   * and then mapping it to simplified object and then in last it sorts the timeline steps by their step number, this ensures the timeline is rendered in the correct order.
   */
  const actions = config.filter(item => Array.isArray(item.timeLine) && item.timeLine.length > 0)
  .map(item => ({
    action: item.timeLine[0].actions,
    step: item.timeLine[0].currentStep,
    route: item.route
  })).sort((a, b) => a.step - b.step);

  // if (hideOnRoutes.includes(currentRoute)) return null;
  const validRoutes = actions.map(a => a.route);
  if (!validRoutes.includes(currentRoute)) return null;

  // Determine current step from route
  const matchedStep = actions.find(item => item.route === currentRoute);
  const currentRouteStep = matchedStep && matchedStep.step ? matchedStep.step : 1;
  return (
    <div className="timeline-container" style={isMobile ? {} : user?.type === "EMPLOYEE"? { maxWidth: "960px", minWidth: "640px", margin: "0 auto", display: "flex", justifyContent: "center" } : { maxWidth: "960px", minWidth: "640px", marginRight: "auto", display: "flex", justifyContent: "center" }}>
      {actions.map((action, index, arr) => (
        console.log("action",action),
        <div className="timeline-checkpoint" key={index}>
          <div className="timeline-content">
            <span className={`circle ${action.step <= currentRouteStep ? "active" : ""}`}>
              {action.step < currentRouteStep ? <TickMark /> : index + 1}
            </span>
            <span className="secondary-color">{t(action.action)}</span>
          </div>
          {index < arr.length - 1 && (
            <span className={`line ${action.step < currentRouteStep ? "active" : ""}`}></span>
          )}
        </div>
      ))}
    </div>
  );
};
export default Timeline;