import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useQueryClient } from "@tanstack/react-query";
import { Routes, Route, Navigate, useLocation, Link,  } from "react-router-dom";
import { SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";

import { newConfig as newPreApprovedConfig } from "../../../config/PreApprovedPlanConfig"
import BuildingPlanScrutiny from "../../../pageComponents/BuildingPlanScrutiny";
const PreApprovedPlan=()=>{
  
  const queryClient = useQueryClient();
  const { t } = useTranslation();
  const { path, url } = Digit.Hooks.useModuleBasePath();
  console.log("pathhh", path)
  const { pathname, state } = useLocation();
  console.log("pathnamee",pathname)
  const navigate = Digit.Hooks.useCustomNavigate();
  const [params, setParams, clearParams] = Digit.Hooks.useSessionStorage("BPA_PRE_APPROVED_CREATE", {});
  const [isShowToast, setIsShowToast] = useState(null);
  const [isSubmitBtnDisable, setIsSubmitBtnDisable] = useState(false);
  const goNext = (skipStep) => {
    const currentPath = pathname.split("/").pop();
    const { nextStep } = config.find((routeObj) => routeObj.route === currentPath);
    let redirectWithHistory = navigate;
    if (nextStep === null) {
      return redirectWithHistory(`check`);
    }
    redirectWithHistory(`${nextStep}`);

  }
  const onSuccess = () => {
    //clearParams();
    queryClient.invalidateQueries({ queryKey: ["PT_CREATE_PROPERTY"] });
  };
  const createApplication = async () => {
    navigate(`acknowledgement`);
  };
  const handleSelect = (key, data, skipStep, isFromCreateApi) => {
    console.log("dataaa", data)
    if (isFromCreateApi) setParams(data);
    else if(key=== "")
    setParams({...data});
    else setParams({ ...params, ...{ [key]: { ...params[key], ...data }}});
    goNext(skipStep);
  };
  const handleSkip = () => {};
    let config = [];
    let newConfig=[];
  newConfig = newPreApprovedConfig;
  newConfig.forEach((obj) => {
    config = config.concat(obj.body.filter((a) => !a.hideInCitizen));
  });

  config.indexRoute = "documents-required";
  console.log("configgg", config)
  const CheckPage = Digit?.ComponentRegistryService?.getComponent('BPACheckPage') ;
  const OBPSAcknowledgement = Digit?.ComponentRegistryService?.getComponent('BPAAcknowledgement');
  
  // const handleSelect=()=>{
  //   //goNext();
  // }
    return (
      
         <Routes>
          <Route index element={<Navigate to="documents-required" replace />} />
          {config.map((routeObj, index) => {
            const { component, texts, inputs, key } = routeObj;
            const Component = typeof component === "string" ? Digit.ComponentRegistryService.getComponent(component) : component;
            console.log("componentttt", Component)
            console.log("routeobj", routeObj.route)
            return (
              <Route
                path={routeObj.route}
                key={index}
                element={
                  <Component config={{ texts, inputs, key }} onSelect={handleSelect} onSkip={handleSkip} t={t} formData={params} isShowToast={isShowToast} isSubmitBtnDisable={isSubmitBtnDisable} setIsShowToast={setIsShowToast}/>
                }
              />
            );
          })}
          <Route path="check" element={<CheckPage onSubmit={createApplication} value={params} />} />
          <Route path="acknowledgement" element={<OBPSAcknowledgement data={params} onSuccess={onSuccess} />} />
        </Routes>

        
    )
};
export default PreApprovedPlan;