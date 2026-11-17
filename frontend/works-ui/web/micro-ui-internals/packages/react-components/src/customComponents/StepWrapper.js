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
      className="stepper rc-step-wrapper-fullwidth"
    >
      <div
        className="stepper-body rc-step-wrapper-fullwidth-2"
      >
        {children}
      </div>
    </div>
  );
};

export default StepWrapper;