import React from "react";
import { useTranslation } from "react-i18next";
import { useQueryClient } from "@tanstack/react-query";
import { Navigate, Route, Routes, useLocation } from "react-router-dom";
import { Config } from "../../../config/config";

// parent component index page for employee which will set ui forms through config
const SVEmpCreate = ({ parentRoute }) => {

  const queryClient = useQueryClient();
  const { t } = useTranslation();
  const { pathname } = useLocation();
  const navigate = Digit.Hooks.useCustomNavigate();
  const basePath = pathname.split("/").slice(0, pathname.split("/").indexOf("apply") + 1).join("/");
  
  let config = [];
  const [params, setParams, clearParams] = Digit.Hooks.useSessionStorage("SV_EMP_CREATES", {});
  
  // function used for traversing through form screens 
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
    let { nextStep = {} } = config.find((routeObj) => routeObj.route === currentPath);


    const redirectWithHistory = (path) => {
      if (skipStep) {
        navigate(path, { replace: true });
      } else {
        navigate(path);
      }
    };

    if (isAddMultiple) {
      nextStep = key;
    }
    if (nextStep === null) {
      return redirectWithHistory(`${basePath}/check`);   // match.path → basePath
    }
    if (!isNaN(nextStep.split("/").pop())) {
      nextPage = `${basePath}/${nextStep}`;   // match.path → basePath

    } else {
      // match.path → basePath
      nextPage = isMultiple && nextStep !== "map" ? `${basePath}/${nextStep}/${index}` : `${basePath}/${nextStep}`;
    }

    redirectWithHistory(nextPage);
  };

  // to clear formdata if the data is present before coming to first page of form
  if(params && Object.keys(params).length>0 && window.location.href.includes("/info") && sessionStorage.getItem("docReqScreenByBack") !== "true")
    {
      clearParams();
      queryClient.invalidateQueries("SV_EMP_CREATE"); // to be changed if error occurs while re-submitting data
    }

  const svcreate = async () => {
    // OLD:history.replace(`${match.path}/acknowledgement`);
    navigate(`${basePath}/acknowledgement`, { replace: true });

  };

  // To do: need to check later according to requirments
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
    queryClient.invalidateQueries("SV_EMP_CREATE");
    sessionStorage.removeItem("CategoryDocument");
    sessionStorage.removeItem("vendingApplicationID");
    sessionStorage.removeItem("ApplicationId");
    sessionStorage.removeItem("Response");
  };

  
  let commonFields = Config;
  commonFields.forEach((obj) => {
    config = config.concat(obj.body.filter((a) => !a.hideInCitizen));
  });
  
  config.indexRoute = "info";

  const SVCheckPage = Digit?.ComponentRegistryService?.getComponent("CheckPage");  
  const SVAcknowledgement = Digit?.ComponentRegistryService?.getComponent("SVAcknowledgement");
  
  return (
    <Routes>
      {config.map((routeObj, index) => {
        const { component, texts, inputs, key } = routeObj;
        const Component = typeof component === "string" ? Digit.ComponentRegistryService.getComponent(component) : component;
        const user = Digit.UserService.getUser().info.type;
        return (
           <Route 
            path={routeObj.route}   // relative path
            key={index}
            element={               //children → element prop
              <Component 
                config={{ texts, inputs, key }} 
                onSelect={handleSelect} 
                onSkip={handleSkip} 
                t={t} 
                formData={params} 
                onAdd={handleMultiple} 
                userType={user} 
              />
            }
            />
          );
        })}

      
      <Route 
        path="check" 
        element={<SVCheckPage onSubmit={svcreate} value={params} />} 
      />
      <Route 
        path="acknowledgement" 
        element={<SVAcknowledgement data={params} onSuccess={onSuccess} />} 
      />
      <Route 
        path="*" 
        element={<Navigate to={`${basePath}/${config.indexRoute}`} replace />} 
      />
    </Routes>
  );
};

export default SVEmpCreate;