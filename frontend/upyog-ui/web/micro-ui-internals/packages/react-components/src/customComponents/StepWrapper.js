import React from "react";
import { useTranslation } from "react-i18next";
import { TickMark } from "@nudmcdgnpm/digit-ui-react-components";

const StepWrapper = ({ children, currentStep = 1, nextStep, prevStep, stepsList = [] }) => {
  const { t } = useTranslation();
  const stepStyle = (isActive, isLast) => ({
    display: "flex",
    alignItems: "center",
    justifyContent: "flex-start",
    flex: "0 0 auto",
    color: isActive ? "#0D43A7" : "#9CA3AF",
    padding: "14px 12px",
    width: "260px",
    marginBottom: isLast ? "6px" : "18px",
  });

  // const circleStyle = (isActive) => ({
  //   width: "30px",
  //   height: "30px",
  //   borderRadius: "50%",
  //   backgroundColor: isActive ? "#3f51b5" : "#ccc",
  //   color: isActive ? "white" : "black",
  //   display: "flex",
  //   alignItems: "center",
  //   justifyContent: "center",
  //   marginBottom: "5px",
  //   fontWeight: "normal",
  // });

  const circleStyle = (stepNumber) => {
    const completed = stepNumber <= currentStep;
    return {
      width: "56px",
      height: "56px",
      borderRadius: "50%",
      background: completed ? "linear-gradient(135deg,#2563eb,#7c3aed)" : "#ffffff",
      color: completed ? "#ffffff" : "#0f172a",
      display: "flex",
      alignItems: "center",
      justifyContent: "center",
      marginBottom: "8px",
      fontWeight: 600,
      fontSize: "18px",
      border: completed ? "none" : "1px solid rgba(15,23,42,0.08)",
      boxShadow: completed ? "0 6px 18px rgba(37,99,235,0.18)" : "0 2px 6px rgba(2,6,23,0.04)",
      position: "relative",
    };
  };

  const labelStyle = {
    fontSize: "16px",
    color: "#0f172a",
    fontWeight: 600,
    fontFamily: "Noto Sans, sans-serif",
    marginRight: "8px",
    textAlign: "right",
  };

  const stepNumberStyle = {
    fontSize: "14px",
    color: "#6b7280",
    fontWeight: 500,
    fontFamily: "Noto Sans, sans-serif",
    margin: "0 6px 0 0",
  };

  const buttonStyle = {
    next: {
      backgroundColor: "#3f51b5",
      color: "white",
      border: "none",
      padding: "10px 20px",
      cursor: "pointer",
      marginLeft: "10px",
    },
    back: {
      backgroundColor: "transparent",
      color: "#3f51b5",
      border: "1px solid #3f51b5",
      padding: "10px 20px",
      cursor: "pointer",
    },
  };

  const lineStyle = {
    position: "absolute",
    width: "3px",
    height: "56px",
    backgroundColor: "rgba(15,23,42,0.06)",
    left: "50%",
    top: "100%",
    transform: "translateX(-50%)",
    borderRadius: "2px",
  };
  const isMobile = window.Digit.Utils.browser.isMobile();
  const totalSteps = stepsList.length;

  // Mobile-only styles
  const mobileNavBarStyle = isMobile ? {
    flexDirection: "row",
    overflowX: "auto",
    whiteSpace: "nowrap",
    minWidth: "100%",
    maxWidth: "100%",
    gap: "8px",
    padding: "10px",
  } : {};

  const mobileCircleStyle = isMobile ? {
    width: "32px",
    height: "32px",
    fontSize: "12px",
  } : {};

  const mobileLabelStyle = isMobile ? {
    fontSize: "10px",
  } : {};

  const mobileStepStyle = isMobile ? {
    width: "auto",
    padding: "6px",
    marginBottom: "0",
    flexDirection: "column",
    alignItems: "center",
  } : {};

  return (
    <div
      className="stepper"
      style={{
        width: "100%",
        display: "flex",
        flexDirection: isMobile ? "column" : "row",
        gap: "24px",
      }}
    >
      <div
        className="stepper-navigation-bar"
        style={{
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
          padding: "18px",
          minWidth: "120px",
          maxWidth: "320px",
          marginBottom: "20px",
          marginTop: isMobile ? "20px" : "0",
          height: isMobile ? "106px" : "",
          ...mobileNavBarStyle,
        }}
      >
        {[...Array(totalSteps)].map((_, index) => (
          <div className="step-content" key={index} style={{...stepStyle(index + 1 <= currentStep, index === totalSteps - 1), ...mobileStepStyle}}>
            <div className="step-circle" style={{...circleStyle(index + 1), ...mobileCircleStyle}}>
              {index + 1}
              {index < totalSteps - 1 && !isMobile && <div style={lineStyle}></div>}
            </div>
            <div className="step-sub-content" style={{ display: "flex", flexDirection: "column", alignItems: isMobile ? "center" : "flex-end", margin: isMobile ? "4px" : "10px" }}>
              {index + 1 === totalSteps ? null : (
                <div className="step-number" style={stepNumberStyle}>
                  {/* Step {index + 1} */}
                </div>
              )}
              <div className="step-label" style={{...labelStyle, ...mobileLabelStyle, textAlign: isMobile ? "center" : "right"}}>
                {t(stepsList[index].stepLabel)}
              </div>
            </div>
          </div>
        ))}
      </div>
      {/* <div className="timeline-container" style={isMobile ? {} : { maxWidth: "960px", minWidth: "640px", marginRight: "auto" }}>
        {stepLabels.map((action, index, arr) => (
          <div className="timeline-checkpoint" key={index}>
            <div className="timeline-content">
              <span className={`circle ${index <= currentStep - 1 && "active"}`}>{index < currentStep - 1 ? <TickMark /> : index + 1}</span>
              <span className="secondary-color">{t(action)}</span>
            </div>
            {index < arr.length - 1 && <span className={`line ${index < currentStep - 1 && "active"}`}></span>}
          </div>
        ))}
      </div> */}
      <div
        className="stepper-body"
        style={{
          width: "100%",
          marginBottom: "20px",
          background: "transparent",
        }}
      >
        {children}
      </div>
      {/* <div style={{ display: "flex", justifyContent: "flex-end", width: "100%", position: "fixed", bottom: "50px", right: "20px", padding: "10px" }}> */}
      {/* {currentStep > 1 && (
          <button style={buttonStyle.back} onClick={prevStep}>
            Back
          </button>
        )} */}
      {/* {currentStep < totalSteps && (
          <button style={buttonStyle.next} onClick={nextStep}>
            Next &rarr;
          </button>
        )}
        {currentStep === totalSteps && (
          <button style={buttonStyle.next} type="submit">
            Submit
          </button>
        )} */}
      {/* </div> */}
    </div>
  );
};

export default StepWrapper;
