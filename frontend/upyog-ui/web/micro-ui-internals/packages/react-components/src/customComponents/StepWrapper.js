import React from "react";
import { useTranslation } from "react-i18next";

const StepWrapper = ({
  children,
  currentStep = 1,
  nextStep,
  prevStep,
  stepsList = [],
}) => {
  const { t } = useTranslation();

  return (
    <div
      className="stepper"
      style={{
        width: "100%",
        display: "block",
      }}
    >
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
    </div>
  );
};

export default StepWrapper;