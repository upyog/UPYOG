import React, { useEffect, useState } from "react";
import { LabelFieldPair, CardLabel, TextInput, CardLabelError, LinkButton, ActionLinks } from "@egovernments/digit-ui-react-components";
import { useLocation, Link } from "react-router-dom";

const SelectSWEmployeePhoneNumber = ({ t, config, onSelect, formData = {}, userType, register, errors, setError: seterror, clearErrors }) => {
  const { pathname: url } = useLocation();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const [iserror, setError] = useState(false);
  const [checkMobile, setCheckMobile] = useState(null);
  const [isMobilePresent, setIsMobilePresent] = useState(null);

  let isMobile = window.Digit.Utils.browser.isMobile();
  const inputs = [
    {
      label: t("FSM_REGISTRY_APPLICANT_MOBILE_NO"),
      isMandatory: true,
      type: "text",
      name: "mobileNumber",
      populators: {
        validation: {
          required: true,
          pattern: /^[6-9]\d{9}$/,
        },
        componentInFront: <div className="employee-card-input employee-card-input--front">+91</div>,
        error: t("CORE_COMMON_MOBILE_ERROR"),
      },
    },
  ];

  const { data: checkWorker, isLoading: isWorkerLoading } = Digit.Hooks.fsm.useWorkerSearch({
    tenantId,
    params: {
      offset: 0,
      limit: 100,
    },
    details: {
      Individual: {
        mobileNumber: checkMobile,
      },
    },
    config: {
      enabled: checkMobile?.length === 10,
    },
  });
  useEffect(() => {
    if (!isNaN(checkMobile) && checkMobile?.length === 10 && checkWorker?.Individual?.length > 0) {
      setIsMobilePresent(checkWorker?.Individual?.[0]?.individualId);
      seterror(config.key, { ...errors[config.key], isMobilePresent: true });
      return;
    }
    setIsMobilePresent(false);
  }, [checkMobile, checkWorker]);

  useEffect(() => {
    if (isMobilePresent === false) {
      clearErrors(config.key);
    }
  }, [isMobilePresent]);

  function setValue(value, input) {
    setCheckMobile(value);
    onSelect(config.key, { ...formData[config.key], [input]: value });
  }
  function validate(value, input) {
    setError(!input.populators.validation.pattern.test(value));
    seterror(config.key, { ...errors[config.key], invalidMobile: !input.populators.validation.pattern.test(value) });
  }

  return (
    <div>
      {inputs?.map((input, index) => (
        <React.Fragment key={index}>
          <LabelFieldPair style={{ alignItems: "baseline" }}>
            <CardLabel className="card-label-smaller">
              {t(input.label)}
              {input.isMandatory ? " * " : null}
            </CardLabel>
            <div className="field-container" style={{ width: isMobile ? "100%" : "50%", display: "block" }}>
              <div>
                <div style={{ display: "flex" }}>
                  <div className="employee-card-input employee-card-input--front">+91</div>
                  <TextInput
                    className="field desktop-w-full"
                    key={input.name}
                    value={formData && formData[config.key] ? formData[config.key][input.name] : undefined}
                    onChange={(e) => {
                      setValue(e.target.value, input.name, validate(e.target.value, input));
                    }}
                    disable={false}
                    defaultValue={undefined}
                    onBlur={(e) => validate(e.target.value, input)}
                    {...input.validation}
                  />
                </div>
                <div>{iserror && <CardLabelError style={{ width: "100%",position:"sticky",zIndex:"1000",marginTop:"-20px" }}>{t(input.populators.error)}</CardLabelError>}</div>
                <div>
                  {isMobilePresent && (
                    <CardLabelError style={{ width: "100%",position:"sticky",zIndex:"1000",marginTop:"-20px" }}>
                      {t("FSM_REGISTRY_WORKER_MOBILE_EXIST_ERROR", { SW: isMobilePresent })}
                      <Link to={`/${window?.contextPath}/employee/fsm/registry/worker-details?id=${checkWorker?.Individual?.[0]?.individualId}`}>
                        {t("FSM_REGISTRY_WORKER_LINK_VIEW_DETAILS")}
                      </Link>
                    </CardLabelError>
                  )}
                </div>
              </div>
            </div>
          </LabelFieldPair>
        </React.Fragment>
      ))}
    </div>
  );
};

export default SelectSWEmployeePhoneNumber;