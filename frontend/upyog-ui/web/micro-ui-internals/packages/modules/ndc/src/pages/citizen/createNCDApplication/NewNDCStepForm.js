import "../../../../css/ndc.css";
import React, { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useTranslation } from "react-i18next";
import { useLocation } from "react-router-dom";
import { Stepper, Toast, BackButton } from "@nudmcdgnpm/digit-ui-react-components";

import { config } from "../../../config/citizen/CitizenNDCApplicationConfig";
import { setNDCStep, updateNDCForm, resetNDCForm } from "../../../redux/actions/NDCFormActions";

const getLocalLabel = (t, key, fallback) => {
  if (!key) return fallback;
  const translated = t(key);
  if (!translated || translated === key) return fallback;
  return translated;
};

const createEmployeeConfig = [
  {
    head: "Application Details",
    stepLabel: "Application Details",
    stepNumber: 1,
    isStepEnabled: true,
    type: "component",
    component: "NewNDCStepFormOneCitizen",
    key: "NDCDetails",
    withoutLabel: true,
    texts: {
      submitBarLabel: "Next",
    },
  },

  {
    head: "NDC_DOCUMENTS_REQUIRED",
    stepLabel: "Document Info",
    stepNumber: 2,
    isStepEnabled: true,
    type: "component",
    component: "NewNDCStepFormTwoCitizen",
    key: "DocummentDetails",
    withoutLabel: true,
    texts: {
      submitBarLabel: "Next",
    },
  },

  {
    head: "Summary",
    stepLabel: "Summary",
    stepNumber: 3,
    isStepEnabled: true,
    type: "component",
    component: "NDCNewFormSummaryStepThreeCitizen",
    key: "PTSummary",
    withoutLabel: true,
    texts: {
      submitBarLabel: "Submit",
    },
  },
];

const updatedCreateEmployeeconfig = createEmployeeConfig.map((item) => {
  return {
    ...item,
    currStepConfig: config.filter((newConfigItem) => newConfigItem.stepNumber === item.stepNumber),
  };
});

export const NewNDCStepForm = () => {
  const navigate = Digit.Hooks.useCustomNavigate();
  const { t } = useTranslation();
  const dispatch = useDispatch();

  const [showToast, setShowToast] = useState(null);

  const formState = useSelector((state) => state.ndc.NDCForm);
  const formData = formState.formData;
  const step = formState.step || 1;

  const location = useLocation();

  const tenantId = Digit.ULBService.getCurrentTenantId();

  const id = window.location.pathname.split("/").pop();

  const { isLoading, data: applicationDetails } = Digit.Hooks.ndc.useSearchEmployeeApplication(
    {
      applicationNo: id,
    },
    tenantId,
  );

  useEffect(() => {
    if (applicationDetails?.Applications?.length) {
      dispatch(updateNDCForm("responseData", applicationDetails.Applications));
    }
  }, [applicationDetails, dispatch]);

  const setStep = (updatedStepNumber) => {
    dispatch(setNDCStep(updatedStepNumber));
  };

  const handleSubmit = () => {};

  useEffect(() => {
    dispatch(resetNDCForm());
  }, [location.pathname, dispatch]);

  const stepsMeta = [
    {
      number: 1,
      title: getLocalLabel(t, "NDC_APPLICATION_DETAILS", "Application Details"),
      subtitle: getLocalLabel(t, "NDC_STEP1_SUBTITLE", "Reason, Property & Dues Check"),
    },
    {
      number: 2,
      title: getLocalLabel(t, "NDC_DOCUMENTS_REQUIRED", "Required Documents"),
      subtitle: getLocalLabel(t, "NDC_STEP2_SUBTITLE", "Upload Supporting Proofs"),
    },
    {
      number: 3,
      title: getLocalLabel(t, "NDC_SUMMARY", "Review & Submit"),
      subtitle: getLocalLabel(t, "NDC_STEP3_SUBTITLE", "Verify & Submit Application"),
    },
  ];

  const headerTitle =
    getLocalLabel(t, "NDC_HEADER_APPLICATION", "") ||
    getLocalLabel(t, "ndc_header_application", "Apply for No Due Certificate (NDC)");

  return (
    <div className="citizen-ndc-page employeeCard ndc-hide-vertical-stepper">
      <div className="citizen-ndc-top-nav">
        <BackButton>{getLocalLabel(t, "CS_COMMON_BACK", "Back")}</BackButton>
      </div>

      <div className="citizen-ndc-hero-card">
        <div className="citizen-ndc-hero-left">
          <div className="citizen-ndc-hero-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <circle cx="12" cy="9" r="6" />
              <polyline points="9.5 9 11.2 10.8 14.8 7.2" />
              <path d="M8.5 14L7 22l5-2.5L17 22l-1.5-8" />
            </svg>
          </div>
          <div className="citizen-ndc-hero-text">
            <h1 className="citizen-ndc-title">{headerTitle}</h1>
            <p className="citizen-ndc-subtitle">
              {getLocalLabel(
                t,
                "NDC_APPLICATION_SUBTITLE",
                "Verify property tax & utility dues, attach supporting documents, and submit your No Due Certificate request online."
              )}
            </p>
          </div>
        </div>
        <div className="citizen-ndc-step-badge">
          {getLocalLabel(t, "STEP", "Step")} {step} {getLocalLabel(t, "OF", "of")} 3
        </div>
      </div>

      <div className="citizen-ndc-stepper-card">
        {stepsMeta.map((s, idx) => {
          const isCompleted = step > s.number;
          const isActive = step === s.number;
          return (
            <React.Fragment key={s.number}>
              <div
                className={`citizen-ndc-step-item ${isActive ? "is-active" : ""} ${isCompleted ? "is-completed" : ""}`}
                onClick={() => {
                  if (isCompleted) setStep(s.number);
                }}
              >
                <div className="citizen-ndc-step-circle">
                  {isCompleted ? (
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
                      <polyline points="20 6 9 17 4 12" />
                    </svg>
                  ) : (
                    s.number
                  )}
                </div>
                <div className="citizen-ndc-step-info">
                  <span className="citizen-ndc-step-title">{s.title}</span>
                  <span className="citizen-ndc-step-desc">{s.subtitle}</span>
                </div>
              </div>
              {idx < stepsMeta.length - 1 && (
                <div className={`citizen-ndc-step-connector ${isCompleted ? "is-completed" : ""}`} />
              )}
            </React.Fragment>
          );
        })}
      </div>

      <div className="citizen-ndc-form-shell">
        <Stepper stepsList={updatedCreateEmployeeconfig} onSubmit={handleSubmit} step={step} setStep={setStep} />
      </div>

      {showToast && (
        <Toast
          error={showToast.key}
          label={t(showToast.label)}
          onClose={() => {
            setShowToast(null);
          }}
          isDleteBtn={"true"}
        />
      )}
    </div>
  );
};
