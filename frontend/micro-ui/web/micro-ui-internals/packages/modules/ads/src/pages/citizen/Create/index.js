import { Loader } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { useQueryClient } from "react-query";

import { Redirect, Route, Switch, useHistory, useLocation, useRouteMatch } from "react-router-dom";

import { citizenConfig } from "../../../config/Create/citizenconfig";
import { data } from "jquery";

/**
 * ADSCreate component manages the flow of the ADS creation process,
 * including rendering the appropriate form components based on the current route.
 * It handles user input, navigates between steps, and manages session storage for form data.
 */


const ADSCreate = ({ parentRoute }) => {
  const queryClient = useQueryClient();
  const match = useRouteMatch();
  const { t } = useTranslation();
  const { pathname } = useLocation();
  const history = useHistory();
  const stateId = Digit.ULBService.getStateId();
  let config = [];
  const [params, setParams, clearParams] = Digit.Hooks.useSessionStorage("ADS_CREATE", {});
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

    let redirectWithHistory = history.push;
    if (skipStep) {
      redirectWithHistory = history.replace;
    }
    if (isAddMultiple) {
      nextStep = key;
    }
    if (nextStep === null) {
      return redirectWithHistory(`${match.path}/check`);
    }
    if (!isNaN(nextStep.split("/").pop())) {
      nextPage = `${match.path}/${nextStep}`;
    } else {
      nextPage = isMultiple && nextStep !== "map" ? `${match.path}/${nextStep}/${index}` : `${match.path}/${nextStep}`;
    }
    redirectWithHistory(nextPage);
  };

  if (
    params &&
    Object.keys(params).length > 0 &&
    window.location.href.includes("/searchads") &&
    sessionStorage.getItem("docReqScreenByBack") !== "true"
  ) {
    clearParams();
    queryClient.invalidateQueries("ADS_CREATE");
  }
  const chbcreate = async () => {
    history.push(`${match.path}/acknowledgement`);
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
    queryClient.invalidateQueries("ADS_CREATE");
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
  config.indexRoute = "searchads";
  const CheckPage = Digit?.ComponentRegistryService?.getComponent("ADSCheckPage");
  return (
    <Switch>
      {config.map((routeObj, index) => {
        const { component, texts, inputs, key } = routeObj;
        const Component = typeof component === "string" ? Digit.ComponentRegistryService.getComponent(component) : component;
        return (
          <Route path={`${match.path}/${routeObj.route}`} key={index}>
            <Component config={{ texts, inputs, key }} onSelect={handleSelect} onSkip={handleSkip} t={t} formData={params} onAdd={handleMultiple} />
          </Route>
        );
      })}

      <Route path={`${match.path}/check`}>
        <CheckPage onSubmit={chbcreate} value={params} />
      </Route>
      {/* <Route path={`${match.path}/acknowledgement`}>
<CHBAcknowledgement data={params} onSuccess={onSuccess} />
</Route> */}
      <Route>
        <Redirect to={`${match.path}/${config.indexRoute}`} />
      </Route>
    </Switch>
  );
};
export default ADSCreate;
