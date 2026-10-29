import React, { useMemo } from "react";
import { useTranslation } from "react-i18next";
import { useQueryClient } from "@tanstack/react-query";
import { Navigate, Route, Routes } from "react-router-dom";
import { newConfig as newConfigWS } from "../../../config/wsDisconnectionConfig";

const WSDisconnection = () => {
  const queryClient = useQueryClient();
  const { t } = useTranslation();
  const { path: modulePath } = Digit.Hooks.useModuleBasePath();
  const basePath = useMemo(() => `${modulePath}/disconnect-application`, [modulePath]);
  const stateId = Digit.ULBService.getStateId();

  let config = [];
  let { data: newConfig, isLoading: configLoading } = Digit.Hooks.ws.useWSConfigMDMS.getFormConfig(stateId, {});
  newConfig = newConfig?.WSDisconnectionConfig ? newConfig?.WSDisconnectionConfig : newConfigWS;
  newConfig = newConfigWS;
  newConfig.filter((e) => e.head === "NEW_DISCONNECTION")?.forEach((obj) => {
    config = config.concat(obj.body.filter((a) => !a.hideInCitizen));
  });
  config.indexRoute = "docsrequired";

  return (
    <Routes>
      {config.map((routeObj, index) => {
        const { component, texts, inputs, key, isSkipEnabled } = routeObj;
        const Component = typeof component === "string" ? Digit.ComponentRegistryService.getComponent(component) : component;
        return (
          <Route
            path={`${basePath}/${routeObj.route}`}
            key={index}
            element={<Component config={{ texts, inputs, key, isSkipEnabled }} t={t} userType={"citizen"} />}
          />
        );
      })}
      <Route path="*" element={<Navigate to={`${basePath}/${config.indexRoute}`} replace />} />
    </Routes>
  );
};

export default WSDisconnection;
