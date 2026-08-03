import { Loader } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useMemo } from "react";
import { useTranslation } from "react-i18next";
import { Navigate, Route, Routes } from "react-router-dom";
import { newConfig as newConfigWS } from "../../../config/wsDisconnectionConfig";

const DisconnectionApplication = () => {
  const { t } = useTranslation();
  const { path: modulePath } = Digit.Hooks.useModuleBasePath();
  const basePath = useMemo(() => `${modulePath}/new-disconnection`, [modulePath]);
  const stateId = Digit.ULBService.getStateId();
  let { data: newConfig, isLoading } = Digit.Hooks.ws.useWSConfigMDMS.WSDisconnectionConfig(stateId, {});

  let config = [];

  if (!isLoading && newConfig.length > 0) {
    newConfig.forEach((obj) => {
      config = config.concat(obj.body.filter((a) => !a.hideInCitizen));
    });
    config.indexRoute = "docsrequired";
  } else {
    return <Loader />
  }


  return (
    <Routes>
      {config.map((routeObj, index) => {
        const { component, texts, inputs, key, isSkipEnabled } = routeObj;
        const Component = typeof component === "string" ? Digit.ComponentRegistryService.getComponent(component) : component;
        return (
          <Route
            path={routeObj.route}
            key={index}
            element={<Component config={{ texts, inputs, key, isSkipEnabled }} t={t} userType={"employee"} />}
          />
        );
      })}

      <Route path="*" element={<Navigate to={config.indexRoute} replace />} />
    </Routes>
  );
};

export default DisconnectionApplication;

