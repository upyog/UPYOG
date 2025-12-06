import React from "react";
import { useTranslation } from "react-i18next";
import { TickMark } from "@upyog/digit-ui-react-components";

import { useLocation } from "react-router-dom";

const Timeline = ({ config }) => {
  const { t } = useTranslation();
  const user = Digit.UserService.getUser().info;
  const isMobile = window.Digit.Utils.browser.isMobile();
  const location = useLocation();
  const currentRoute = location.pathname.split("/").pop();

  if (currentRoute === "info") return null;

  // Step 1: Get all timeline items
  const actions = config
    .filter(item => Array.isArray(item.timeLine) && item.timeLine.length > 0)
    .map(item => ({
      step: item.timeLine[0].currentStep,
      action: item.timeLine[0].actions,
      route: item.route,
    }));

  // Step 2: Group by currentStep
  const stepGroups = [];
  actions.forEach(item => {
    const existingStep = stepGroups.find(s => s.step === item.step);
    if (existingStep) {
      existingStep.routes.push(item.route);
    } else {
      stepGroups.push({
        step: item.step,
        action: item.action,
        routes: [item.route],
      });
    }
  });

  const validRoutes = actions.map(a => a.route);
  if (!validRoutes.includes(currentRoute)) return null;

  // Step 3: Sort by step number
  stepGroups.sort((a, b) => a.step - b.step);

  // Step 4: Find current step from any matching route
  const currentStepObj = stepGroups.find(group => group.routes.includes(currentRoute));
  const currentRouteStep = currentStepObj ? currentStepObj.step : 1;

  return (
    <div className="timeline-container" style={isMobile ? {} : user?.type === "EMPLOYEE"? { maxWidth: "960px", minWidth: "640px", margin: "0 auto", display: "flex", justifyContent: "center" } : { maxWidth: "960px", minWidth: "640px", marginRight: "auto", display: "flex", justifyContent: "center" }}>
      {stepGroups.map((stepObj, index, arr) => (
        <div className="timeline-checkpoint" key={index}>
          <div className="timeline-content">
            <span className={`circle ${stepObj.step <= currentRouteStep ? "active" : ""}`}>
              {stepObj.step < currentRouteStep ? <TickMark /> : index + 1}
            </span>
            <span className="secondary-color">{t(stepObj.action)}</span>
          </div>
          {index < arr.length - 1 && (
            <span className={`line ${stepObj.step < currentRouteStep ? "active" : ""}`}></span>
          )}
        </div>
      ))}
    </div>
  );
};

export default Timeline;