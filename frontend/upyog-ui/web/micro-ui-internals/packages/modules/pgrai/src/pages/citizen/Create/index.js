import { Loader } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { useQueryClient } from "@tanstack/react-query";

import { Route, useLocation,  Routes, Navigate } from "react-router-dom";

import { citizenConfig } from "../../../config/Create/citizenconfig";
import Acknowledgement from "./Acknowledgement";

/**
 * PGRAICreate component manages the flow of the ADS creation process,
 * including rendering the appropriate form components based on the current route.
 * It handles user input, navigates between steps, and manages session storage for form data.
 */


const PGRAICreate = ({ parentRoute }) => {
  const queryClient = useQueryClient();
  const match = Digit.Hooks.useModuleBasePath();
  const { t } = useTranslation();
  const { pathname } = useLocation();
  const navigate = Digit.Hooks.useCustomNavigate();
  const stateId = Digit.ULBService.getStateId();
  let config = [];

  const [params, setParams, clearParams] = Digit.Hooks.useSessionStorage("PGRAICreate", {});
  let { data: commonFields, isLoading } = Digit.Hooks.pt.useMDMS(stateId, "PropertyTax", "CommonFieldsConfig"); // PROPERTY CONFIG HOOK , just for commkonfeild config
  const goNext = (skipStep, index, isAddMultiple, key) => {
    let currentPath = pathname.split("/").pop(),
      lastchar = currentPath.charAt(currentPath.length - 1),
      isMultiple = false,
      nextPage;
    if (Number(parseInt(currentPath)) || currentPath == "0" || currentPath == "-1") {
      if (currentPath == "-1" || currentPath == "-2") {
        currentPath = pathname.slice(0, -3);
        currentPath = currentPath.split("/").pop();
        isMultiple = true;
      } else {
        currentPath = pathname.slice(0, -2);
        currentPath = currentPath.split("/").pop();

        isMultiple = true;
      }
    } else {
      isMultiple = false;
    }
    if (!isNaN(lastchar)) {
      isMultiple = true;
    }
    // let { nextStep = {} } = config.find((routeObj) => routeObj.route === currentPath);
    let { nextStep = {} } = config.find((routeObj) => routeObj.route === (currentPath || "0"));

    let redirectWithHistory = (to, state) => navigate(to, state != null ? { state } : undefined);
    if (skipStep) {
      redirectWithHistory = (to, state) => navigate(to, state != null ? { replace: true, state } : { replace: true });
    }
    if (isAddMultiple) {
      nextStep = key;
    }
    if (nextStep === null) {
      return redirectWithHistory(`acknowledgement`);
    }
    if (!isNaN(nextStep.split("/").pop())) {
      nextPage = `${nextStep}`;
    } else {
      nextPage = isMultiple && nextStep !== "map" ? `${nextStep}/${index}` : `${nextStep}`;
    }
    redirectWithHistory(nextPage);
  };

  
  function handleSelect(key, data, skipStep, index, isAddMultiple = false) {
    if (key === "owners") {
      let owners = params.owners || [];
      owners[index] = data;
      setParams({ ...params, ...{ [key]: [...owners] } });
    } else if (key === "units") {
      let units = params.units || [];
      // if(index){units[index] = data;}else{
      units = data;
      setParams({ ...params, units });
    } else {
      setParams({ ...params, ...{ [key]: { ...params[key], ...data } } });
    }
    goNext(skipStep, index, isAddMultiple, key);
  }
  const handleSkip = () => {};
  const handleMultiple = () => {};
  const onSuccess = () => {
    clearParams();
    queryClient.invalidateQueries("PGRAICREATE");
  };
  if (isLoading) {
    return <Loader />;
  }
  // commonFields=newConfig;
  /* use newConfig instead of commonFields for local development in case needed */
  commonFields = citizenConfig;
  commonFields.forEach((obj) => {
    config = config.concat(obj.body.filter((a) => !a.hideInCitizen));
  });
  config.indexRoute = "fileGrievance";
  const CheckPage = Digit?.ComponentRegistryService?.getComponent("PGRAICheckPage");
  // const ADSAcknowledgement = Digit?.ComponentRegistryService?.getComponent("ADSAcknowledgement");
  return (
    <Routes>
      {config.map((routeObj, index) => {
        const { component, texts, inputs, key } = routeObj;
        const Component = typeof component === "string" ? Digit.ComponentRegistryService.getComponent(component) : component;
        return (
          <Route
            path={`${routeObj.route}`}
            key={index}
            element={
              <Component config={{ texts, inputs, key}} onSelect={handleSelect} onSkip={handleSkip} t={t} formData={params} onAdd={handleMultiple} />
            }
          />
        );
      })}

      {/* <Route path={`check`} element={<CheckPage onSubmit={chbcreate} value={params} />} /> */}
      <Route path={`acknowledgement`} element={<Acknowledgement data={params} onSuccess={onSuccess} />} />
      <Route path="*" element={<Navigate to={`${config.indexRoute}`} replace />} />
    </Routes>
  );
};
export default PGRAICreate;
