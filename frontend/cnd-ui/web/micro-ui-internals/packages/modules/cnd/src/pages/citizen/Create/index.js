import React from "react";
import { useTranslation } from "react-i18next";
import { useQueryClient } from "@tanstack/react-query";
import { Route, useLocation, Routes, Navigate } from "react-router-dom";
import { Config } from "../../../config/config";
import { Timeline } from "@nudmcdgnpm/digit-ui-react-components";
import { cndPayload } from "../../../utils";

const CndCreate = ({ parentRoute }) => {
  const queryClient = useQueryClient();
  const { t } = useTranslation();
  const { pathname } = useLocation();
  const navigate = Digit.Hooks.useCustomNavigate();
  const stateId = Digit.ULBService.getStateId();
  let config = [];
  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId();
  const mutation = Digit.Hooks.cnd.useCndCreateApi(tenantId);
  const [params, setParams, clearParams] = Digit.Hooks.useSessionStorage("CND_Creates", {});

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


    let redirectWithHistory = (to) => navigate(to);
    if (skipStep) {
      redirectWithHistory = (to) => navigate(to, { replace: true });
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

  // to clear formdata if the data is present before coming to first page of form
  if (params && Object.keys(params).length > 0 && window.location.href.includes("/info") && sessionStorage.getItem("docReqScreenByBack") !== "true") {
    clearParams();
    queryClient.invalidateQueries({ queryKey: ["CND_Creates"] });
  }

  const cndCreate = async () => {
    try {
      params.tenantId = tenantId;
      let formdata = cndPayload(params);
      mutation.mutate(formdata, {
        onSuccess: (response) => {
          onSuccess();
          navigate(`acknowledgement?applicationNumber=${response?.cndApplicationDetails?.applicationNumber}&tenantId=${response?.cndApplicationDetails?.tenantId}`, {
            state: {
              data: response,
              isSuccess: true,
            },
          });
        },
        onError: (error) => {
          navigate("acknowledgement", {
            state: {
              data: null,
              isSuccess: false,
              error: error,
            },
          });
        },
      });
    } catch (err) {
      navigate("acknowledgement", {
        state: {
          data: null,
          isSuccess: false,
        },
      });
    }
  };

  function handleSelect(key, data, skipStep, index, isAddMultiple = false) {
    if (key === "owners") {
      let owners = params.owners || [];
      owners[index] = data;
      setParams({ ...params, ...{ [key]: [...owners] } });
    } else if (key === "Documents") {
      // Spread the data from Documents key to root level
      setParams({ ...params, ...data });
    } else {
      setParams({ ...params, ...{ [key]: { ...params[key], ...data } } });
    }
    goNext(skipStep, index, isAddMultiple, key);
  }

  const handleSkip = () => { };
  const handleMultiple = () => { };


  /**
   * this onSuccess function will execute once the application submitted successfully 
   * it will clear all the params from the session storage  and also invalidate the query client
   * as well as remove the beneficiary & disabilityStatus from the session storage
   */
  const onSuccess = () => {
    clearParams();
    queryClient.invalidateQueries({ queryKey: ["CND_Creates"] });
  };

  let commonFields = Config;
  commonFields.forEach((obj) => {
    config = config.concat(obj.body.filter((a) => !a.hideInCitizen));
  });

  config.indexRoute = "info";

  const CndCheckPage = Digit?.ComponentRegistryService?.getComponent("CndCheckPage");
  const CndAcknowledgement = Digit?.ComponentRegistryService?.getComponent("CndAcknowledgement");

  return (
    <React.Fragment>
      <Timeline config={config} />
      <Routes>
        {config.map((routeObj, index) => {
          const { component, texts, inputs, key } = routeObj;
          const Component = typeof component === "string" ? Digit.ComponentRegistryService.getComponent(component) : component;
          const user = Digit.UserService.getUser().info.type;
          return (
            <Route path={`${routeObj.route}/*`} key={index} element={
              <Component config={{ texts, inputs, key }} onSelect={handleSelect} onSkip={handleSkip} t={t} formData={params} onAdd={handleMultiple} userType={user} />
            } />
          );
        })}

        <Route path={`check/*`} element={
          <CndCheckPage onSubmit={cndCreate} value={params} />
        } />
        <Route path={`acknowledgement/*`} element={
          <CndAcknowledgement />
        } />
        <Route path="*" element={
          <Navigate to={`${config.indexRoute}`} replace />
        } />
      </Routes>
    </React.Fragment>
  );
};

export default CndCreate;