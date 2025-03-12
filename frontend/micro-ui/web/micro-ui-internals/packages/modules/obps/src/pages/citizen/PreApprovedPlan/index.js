import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useQueryClient } from "react-query";
import { Switch,  Redirect } from "react-router-dom";
import { useRouteMatch, useLocation, useHistory, Route, Link} from "react-router-dom";
import { SubmitBar } from "@upyog/digit-ui-react-components";

import { newConfig as newPreApprovedConfig } from "../../../config/PreApprovedPlanConfig"
import BuildingPlanScrutiny from "../../../pageComponents/BuildingPlanScrutiny";
const PreApprovedPlan=()=>{
  
  const queryClient = useQueryClient();
  const { t } = useTranslation();
  const { path, url } = useRouteMatch();
  console.log("pathhh", path)
  const { pathname, state } = useLocation();
  console.log("pathnamee",pathname)
  const history = useHistory();
  const [params, setParams, clearParams] = Digit.Hooks.useSessionStorage("BPA_PRE_APPROVED_CREATE", {});
  const [isShowToast, setIsShowToast] = useState(null);
  const [isSubmitBtnDisable, setIsSubmitBtnDisable] = useState(false);
  const goNext = (skipStep) => {
    const currentPath = pathname.split("/").pop();
    const { nextStep } = config.find((routeObj) => routeObj.route === currentPath);
    let redirectWithHistory = history.push;
    if (nextStep === null) {
      return redirectWithHistory(`${path}/check`);
    }
    redirectWithHistory(`${path}/${nextStep}`);

  }
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

  config.indexRoute = "preApprovedPlanDetails";
  console.log("configgg", config)
  
  // const handleSelect=()=>{
  //   //goNext();
  // }
    return (
      
         <Switch>
{config.map((routeObj, index) => {
            const { component, texts, inputs, key } = routeObj;
            const Component = typeof component === "string" ? Digit.ComponentRegistryService.getComponent(component) : component;
            console.log("componentttt", Component)
            return (
              <Route path={`${path}/${routeObj.route}`} key={index}>
              <Component config={{ texts, inputs, key }} onSelect={handleSelect} onSkip={handleSkip} t={t} formData={params} isShowToast={isShowToast} isSubmitBtnDisable={isSubmitBtnDisable} setIsShowToast={setIsShowToast}/>
              </Route>
            );
          })}
        </Switch>

        
    )
};
export default PreApprovedPlan;