import React, { useEffect, useMemo } from "react";
import { useTranslation } from "react-i18next";
import { useQueryClient } from "@tanstack/react-query";
import { useParams, useLocation, Route, Routes, Navigate,  } from "react-router-dom";
import { newConfig as newConfigBPA } from "../../../config/buildingPermitConfig";
import {newConfig1} from "./NewConfig"
// import CheckPage from "./CheckPage";
// import OBPSAcknowledgement from "./OBPSAcknowledgement";

const getPath = (path, params) => {
  params && Object.keys(params).map(key => {
    path = path.replace(`:${key}`, params[key]);
  })
  return path;
}


const NewBuildingPermit = () => {
  const queryClient = useQueryClient();
  const { t } = useTranslation();
  const routeParams = useParams();
  const { pathname, state } = useLocation();
  const navigate = Digit.Hooks.useCustomNavigate();
  const { path: modulePath } = Digit.Hooks.useModuleBasePath();
  const basePath = useMemo(
    () => getPath(`${modulePath}/bpa/:applicationType/:serviceType`, routeParams),
    [modulePath, routeParams?.applicationType, routeParams?.serviceType]
  );
  Digit.SessionStorage.set("OBPS_PT", "true");
  sessionStorage.removeItem("BPA_SUBMIT_APP");

  const tenantId = Digit.ULBService.getCurrentTenantId();
  const [params, setParams, clearParams] = Digit.Hooks.useSessionStorage("BUILDING_PERMIT", state?.edcrNumber ? { data: { scrutinyNumber: { edcrNumber: state?.edcrNumber }}} : {});
  const stateId = Digit.ULBService.getStateId();
  let { data: newConfig } = Digit.Hooks.obps.SearchMdmsTypes.getFormConfig(stateId, []);

  const goNext = (skipStep) => {
    const currentPath = pathname.split("/").pop();
    const { nextStep } = newConfig1.find((routeObj) => routeObj.route === currentPath);
    let redirectWithHistory = navigate;
    if (nextStep === null) {
      return redirectWithHistory(`${basePath}/check`);
    }
    redirectWithHistory(`${basePath}/${nextStep}`);

  }

  const onSuccess = () => {
    //clearParams();
    queryClient.invalidateQueries({ queryKey: ["PT_CREATE_PROPERTY"] });
  };
  const createApplication = async () => {
    navigate(`${basePath}/acknowledgement`);
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
  newConfig = newConfig?.BuildingPermitConfig ? newConfig?.BuildingPermitConfig : newConfigBPA;
  newConfig.forEach((obj) => {
    config = config.concat(obj.body.filter((a) => !a.hideInCitizen));
  });
  config.indexRoute = "docs-required";

  useEffect(() => {
    if(sessionStorage.getItem("isPermitApplication") && sessionStorage.getItem("isPermitApplication") == "true") {
      clearParams();
      sessionStorage.setItem("isPermitApplication", false);
    }
  }, []);

  const CheckPage = Digit?.ComponentRegistryService?.getComponent('BPACheckPage') ;
  const OBPSAcknowledgement = Digit?.ComponentRegistryService?.getComponent('BPAAcknowledgement');

  return (
    <Routes>
      {newConfig1.map((routeObj, index) => {
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
      <Route path="acknowledgement" element={<OBPSAcknowledgement data={params} onSuccess={onSuccess} />} />
      <Route path="*" element={<Navigate to={`${basePath}/${config.indexRoute}`} replace />} />
    </Routes>
  );
};

export default NewBuildingPermit;