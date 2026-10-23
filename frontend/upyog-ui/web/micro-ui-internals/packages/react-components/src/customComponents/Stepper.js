import React from "react";
import { useTranslation } from "react-i18next";
//
import StepWrapper from "./StepWrapper";

const Stepper = ({ stepsList = [], onSubmit, step, setStep }) => {
  const enabledStepsList = stepsList.filter((item) => item.isStepEnabled);
  const { t } = useTranslation();

  function onGoNext() {
    if (step < enabledStepsList.length) {
      nextStep();
    } else if (step === enabledStepsList.length) {
      onSubmit();
    }
  }

  function onBackClick() {
    if (step > 1) {
      prevStep();
    }
  }

  const nextStep = () => setStep(step + 1);
  const prevStep = () => setStep(step - 1);

  const renderStep = () => {
    const currStepConfBody = enabledStepsList[step - 1];
    const { component, key, texts, currStepConfig } = currStepConfBody;
    const Component = Digit?.ComponentRegistryService?.getComponent(component);
    return (
      <React.Fragment>
        <Component config={{ key, texts, component, currStepNumber: step, currStepConfig }} onGoNext={onGoNext} onBackClick={onBackClick} t={t} />
      </React.Fragment>
    );
  };
  return (
    <StepWrapper currentStep={step} nextStep={nextStep} prevStep={prevStep} stepsList={enabledStepsList}>
      {renderStep()}
    </StepWrapper>
  );
};

export default Stepper;
