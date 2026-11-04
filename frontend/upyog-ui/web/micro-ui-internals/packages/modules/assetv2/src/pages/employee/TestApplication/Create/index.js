import { Loader } from "@nudmcdgnpm/digit-ui-react-components";
import React ,{Fragment}from "react";
import { useTranslation } from "react-i18next";
import { useQueryClient } from "@tanstack/react-query";
import { Route, useLocation,  Routes, Navigate } from "react-router-dom";
// import { newAssetConfig } from "../../../../config/Create/newAssetConfig";
import { assetAllDetailsConfig } from "../../../../config/Create/assetAllDetailsConfig";
import { Assetdata } from "../../../../utils";

const ASSETCreate = ({ parentRoute }) => {
  const queryClient = useQueryClient();
  const match = Digit.Hooks.useModuleBasePath();
  const { t } = useTranslation();
  const { pathname } = useLocation();
  const navigate = Digit.Hooks.useCustomNavigate();
  const stateId = Digit.ULBService.getStateId();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const mutation = Digit.Hooks.asset.useAssetCreateAPI(tenantId);
  let config = [];
  const [params, setParams, clearParams] = Digit.Hooks.useSessionStorage("AST_CREATE", {});

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
    let { nextStep = {} } = config.find((routeObj) => routeObj.route === (currentPath || '0'));


    let redirectWithHistory = (to, state) => navigate(to, state != null ? { state } : undefined);
    if (skipStep) {
      redirectWithHistory = (to, state) => navigate(to, state != null ? { replace: true, state } : { replace: true });
    }
    if (isAddMultiple) {
      nextStep = key;
    }
    if (nextStep === null) {
      return redirectWithHistory(`check`);
    }
    if (!isNaN(nextStep.split("/").pop())) {
      nextPage = `${nextStep}`;
    }
     else {
      nextPage = isMultiple && nextStep !== "map" ? `${nextStep}/${index}` : `${nextStep}`;
    }

    redirectWithHistory(nextPage);
  };


  if(params && Object.keys(params).length>0 && window.location.href.includes("/info") && sessionStorage.getItem("docReqScreenByBack") !== "true")
    {
      clearParams();
      queryClient.invalidateQueries("AST_CREATE");
    }

  const handleSubmit = () => {
      const formdata = Assetdata(params); 
      formdata.Asset.tenantId = tenantId;
      mutation.mutate(formdata, {
        onSuccess: () => {
          clearParams();
          queryClient.invalidateQueries("AST_CREATE");
          navigate("acknowledgement", { replace: true });
        },
      });
    };

  function handleSelect(key, data, skipStep, index, isAddMultiple = false) {
    // setParams({ ...params, ...{ [key]: { ...params[key], ...data } } });
    setParams({ ...params, ...data });
    goNext(skipStep, index, isAddMultiple, key);
  }

  const handleSkip = () => {};
  const handleMultiple = () => {};

  const onSuccess = () => {
    clearParams();
    queryClient.invalidateQueries("AST_CREATE");
  };

  let isLoading

  if (isLoading) {
    return <Loader />;
  }

  // commonFields=newConfig;
  /* use newConfig instead of commonFields for local development in case needed */
 let commonFields = assetAllDetailsConfig;
  commonFields.forEach((obj) => {
    config = config.concat(obj.body.filter((a) => !a.hideInCitizen));
  });
  
  config.indexRoute = "assetDeatils";

  const CheckPage = Digit?.ComponentRegistryService?.getComponent("ASTCheckPage");
  const NewResponse = Digit?.ComponentRegistryService?.getComponent("NewResponse");

  
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
              <Component config={{ texts, inputs, key }} onSelect={handleSelect} onSkip={handleSkip} t={t} formData={params} onAdd={handleMultiple} />
            }
          />
        );
      })}

      <Route path={`check`} element={<CheckPage onSubmit={handleSubmit} value={params} />} />
      <Route path={`acknowledgement`} element={<NewResponse data={params} onSuccess={onSuccess} mutation={mutation} />} />
      <Route path="*" element={<Navigate to={`${config.indexRoute}`} replace />} />
    </Routes>
  );
};

export default ASSETCreate;