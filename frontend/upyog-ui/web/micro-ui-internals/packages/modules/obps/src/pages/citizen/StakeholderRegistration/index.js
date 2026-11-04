import React, { useEffect } from "react";
import { useTranslation } from "react-i18next";
import { useQueryClient } from "@tanstack/react-query";
import { useLocation, Route,  Routes, Navigate } from "react-router-dom";
import { newConfig as newConfigBPAREG } from "../../../config/stakeholderConfig";
// import CheckPage from "./CheckPage";
// import StakeholderAcknowledgement from "./StakeholderAcknowledgement";


const StakeholderRegistration = () => {
  const queryClient = useQueryClient();
  const { t } = useTranslation();
  const { path, url } = Digit.Hooks.useModuleBasePath();
  const { pathname, state } = useLocation();
  const navigate = Digit.Hooks.useCustomNavigate();

  const tenantId = Digit.ULBService.getCurrentTenantId();
  const [params, setParams, clearParams] = Digit.Hooks.useSessionStorage("BUILDING_PERMIT", state?.edcrNumber ? { data: { scrutinyNumber: { edcrNumber: state?.edcrNumber }}} : {});

  const stateId = Digit.ULBService.getStateId();
  let { data: newConfig } = Digit.Hooks.obps.SearchMdmsTypes.getFormConfig(stateId, []);

  const goNext = (skipStep) => {
  const currentPath = pathname.split("/").pop();
  const { nextStep } = config.find((routeObj) => routeObj.route === currentPath);
  /* navigate needs full path, not just "/nextStep" which would resolve to root */
  const base = pathname.substring(0, pathname.lastIndexOf("/"));
  
  if (nextStep === null) {
    navigate(`${base}/check`);
  } else {
    navigate(`${base}/${nextStep}`);
  }
};

  const onSuccess = () => {
    clearParams();
    queryClient.invalidateQueries({ queryKey: ["PT_CREATE_PROPERTY"] });
  };
  const createApplication = async () => {
    navigate(`acknowledgement`);
  };

  const handleSelect = (key, data, skipStep, isFromCreateApi) => {
    if (isFromCreateApi) setParams(data);
    else if(key=== "")
    setParams({...data});
    else setParams({ ...params, ...{ [key]: { ...params[key], ...data }}});
    goNext(skipStep);
  };
  const handleSkip = () => {};

  // const state = tenantId.split(".")[0];
  let config = [];
  newConfig = newConfig?.StakeholderConfig ? newConfig?.StakeholderConfig : newConfigBPAREG;
  newConfig.forEach((obj) => {
    config = config.concat(obj.body.filter((a) => !a.hideInCitizen));
  });
  config.indexRoute = "stakeholder-docs-required";

  useEffect(() => {
    if(sessionStorage.getItem("isPermitApplication") && sessionStorage.getItem("isPermitApplication") == "true") {
      clearParams();
      sessionStorage.setItem("isPermitApplication", false);
    }
  }, []);

  const CheckPage = Digit?.ComponentRegistryService?.getComponent('StakeholderCheckPage') ;
  const StakeholderAcknowledgement = Digit?.ComponentRegistryService?.getComponent('StakeholderAcknowledgement');

  return (
    <Routes>
      {config.map((routeObj, index) => {
        const { component, texts, inputs, key } = routeObj;
        const Component = typeof component === "string" ? Digit.ComponentRegistryService.getComponent(component) : component;
        return (
          <Route
            path={routeObj.route}
            key={index}
            element={<Component config={{ texts, inputs, key }} onSelect={handleSelect} onSkip={handleSkip} t={t} formData={params} />}
          />
        );
      })}
      <Route path="check" element={<CheckPage onSubmit={createApplication} value={params} />} />
      <Route path="acknowledgement" element={<StakeholderAcknowledgement data={params} onSuccess={onSuccess} />} />
      <Route path="*" element={<Navigate to={config.indexRoute} replace />} />
    </Routes>
  );
};

export default StakeholderRegistration; 