import React, { useEffect, useMemo } from "react";
import { useTranslation } from "react-i18next";
import { useQueryClient } from "@tanstack/react-query";
import { Navigate, Route, Routes, useLocation,  } from "react-router-dom";
import { newConfig as newConfigWS } from "../../../config/wsCreateConfig";

const getPath = (path, params) => {
  params &&
    Object.keys(params).map((key) => {
      path = path.replace(`:${key}`, params[key]);
    });
  return path;
};

const WSCreate = () => {
  const queryClient = useQueryClient();
  const { t } = useTranslation();
  const { path: modulePath } = Digit.Hooks.useModuleBasePath();
  const basePath = useMemo(() => `${modulePath}/create-application`, [modulePath]);
  const { pathname, state } = useLocation();
  const navigate = Digit.Hooks.useCustomNavigate();

  const tenantId = Digit.ULBService.getCurrentTenantId();
  const [params, setParams, clearParams] = Digit.Hooks.useSessionStorage(
    "WS_CREATE",
    state?.edcrNumber ? { data: { scrutinyNumber: { edcrNumber: state?.edcrNumber } } } : {}
  );

  const CheckPage = Digit?.ComponentRegistryService?.getComponent("WSCheckPage");
  const Acknowledgement = Digit?.ComponentRegistryService?.getComponent("WSAcknowledgement");

  const stateId = Digit.ULBService.getStateId();
  let { data: newConfig } = Digit.Hooks.ws.useWSConfigMDMS.getFormConfig(stateId, []);
  let isModifyEdit = window.location.href.includes("/modify-connection/") || window.location.href.includes("/edit-application/");

  let config = [];
  newConfig = newConfig?.WSCreateConfig ? newConfig?.WSCreateConfig : newConfigWS;
  newConfig.forEach((obj) => {
    config = config.concat(obj.body.filter((a) => !a.hideInCitizen));
  });
  config.indexRoute = "search-property";

  const goNext = (skipStep) => {
    const currentPath = pathname.split("/").pop();
    let { nextStep } = config.find((routeObj) => routeObj.route === currentPath);
    let routeObject = config.find((routeObj) => routeObj.route === currentPath && routeObj);
    if (typeof nextStep == "object" && nextStep != null) {
      if (
        nextStep[sessionStorage.getItem("KnowProperty")] &&
        (nextStep[sessionStorage.getItem("KnowProperty")] === "search-property" || nextStep[sessionStorage.getItem("KnowProperty")] === "create-property")
      ) {
        nextStep = `${nextStep[sessionStorage.getItem("KnowProperty")]}`;
      }
    }
    if (routeObject[sessionStorage.getItem("serviceName")]) nextStep = routeObject[sessionStorage.getItem("serviceName")];
    if ((params?.cptId?.id || params?.cpt?.details?.propertyId || (isModifyEdit && params?.cpt?.details?.propertyId)) && nextStep === "know-your-property") {
      nextStep = "property-details";
    }
    if (nextStep === "docsrequired" && sessionStorage.getItem("changePropertySelected") === "yes") {
      nextStep = "property-details";
    }
    let redirectWithHistory = navigate;
    if (nextStep === null) {
      return redirectWithHistory(`${basePath}/check`);
    }
    redirectWithHistory(`${basePath}/${nextStep}`);
  };

  if (params && Object.keys(params).length > 0 && window.location.href.includes("/citizen/ws/create-application/search-property")) {
    clearParams();
    queryClient.invalidateQueries({ queryKey: ["WS_CREATE"] });
  }

  const onSuccess = () => {
    queryClient.invalidateQueries({ queryKey: ["WS_CREATE"] });
  };
  const createApplication = async () => {
    sessionStorage.setItem("isCreateEnabled", "true");
    navigate(`${basePath}/acknowledgement`);
  };

  const handleSelect = (key, data, skipStep, isFromCreateApi) => {
    if (isFromCreateApi) setParams(data);
    else if (key === "") setParams({ ...data });
    else setParams({ ...params, ...{ [key]: { ...params[key], ...data } } });
    goNext(skipStep);
  };
  const handleSkip = () => {};

  return (
    <Routes>
      {config.map((routeObj, index) => {
        const { component, texts, inputs, key, isSkipEnabled } = routeObj;
        const Component = typeof component === "string" ? Digit.ComponentRegistryService.getComponent(component) : component;
        return (
          <Route
            path={routeObj.route}
            key={index}
            element={
              <Component
                config={{ texts, inputs, key, isSkipEnabled }}
                onSelect={handleSelect}
                onSkip={handleSkip}
                t={t}
                formData={params}
                userType={"citizen"}
              />
            }
          />
        );
      })}
      <Route path="check" element={<CheckPage onSubmit={createApplication} value={params} />} />
      <Route path="acknowledgement" element={<Acknowledgement data={params} onSuccess={onSuccess} clearParams={clearParams} />} />
      <Route path="*" element={<Navigate to={config.indexRoute} replace />} />
    </Routes>
  );
};

export default WSCreate;
