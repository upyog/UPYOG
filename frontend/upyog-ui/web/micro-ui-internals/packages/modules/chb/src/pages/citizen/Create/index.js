import { Loader } from "@upyog/digit-ui-react-components";
import React ,{Fragment}from "react";
import { useTranslation } from "react-i18next";
import { useQueryClient } from "react-query";
import { Redirect, Route, Switch, useHistory, useLocation, useRouteMatch } from "react-router-dom";
import { citizenConfig } from "../../../config/Create/citizenconfig";
import { data } from "jquery";

/**
 * CHBCreate Component
 * 
 * This component is responsible for rendering the citizen-facing "Create Application" workflow for the CHB module.
 * It manages the navigation between different steps of the application process and handles session storage for form data.
 * 
 * Props:
 * - `parentRoute`: The base route for the application workflow.
 * 
 * Hooks:
 * - `useQueryClient`: Provides access to the React Query client for managing cached data.
 * - `useRouteMatch`: Provides information about the current route.
 * - `useTranslation`: Provides the `t` function for internationalization.
 * - `useLocation`: Provides access to the current location object, including the `pathname`.
 * - `useHistory`: Provides navigation functionality within the application.
 * - `Digit.Hooks.useSessionStorage`: Custom hook to manage session storage for the application data.
 * 
 * Variables:
 * - `queryClient`: Instance of the React Query client for managing cached data.
 * - `match`: Object containing information about the current route.
 * - `t`: Translation function for internationalization.
 * - `pathname`: The current URL path.
 * - `history`: Object for navigating between routes.
 * - `stateId`: The state ID fetched using the `Digit.ULBService.getStateId` function.
 * - `config`: Array to store the configuration for the application workflow steps.
 * - `params`: Object containing the current session data for the application.
 * - `setParams`: Function to update the session data.
 * - `clearParams`: Function to clear the session data.
 * - `commonFields`: Fetched MDMS configuration for common fields in the property tax module.
 * - `isLoading`: Boolean indicating whether the MDMS data is being loaded.
 * 
 * Functions:
 * - `goNext`: Handles navigation to the next step in the application workflow.
 *    - Determines the current step based on the `pathname`.
 *    - Handles special cases for multiple entries (e.g., "-1" or "-2").
 *    - Updates the route to the next step in the workflow.
 * 
 * Logic:
 * - Fetches MDMS configurations for common fields using the `useMDMS` hook.
 * - Manages session storage for the application data using the `useSessionStorage` hook.
 * - Dynamically determines the next step in the workflow based on the current route and application state.
 * 
 * Returns:
 * - A component that renders the "Create Application" workflow for citizens, with navigation between steps and session data management.
 */
const CHBCreate = ({ parentRoute }) => {

  const queryClient = useQueryClient();
  const match = useRouteMatch();
  const { t } = useTranslation();
  const { pathname } = useLocation();
  const history = useHistory();
  const stateId = Digit.ULBService.getStateId();
  let config = [];
  const [params, setParams, clearParams] = Digit.Hooks.useSessionStorage("CHB_CREATE", {});
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
    }
     else {
      nextPage = isMultiple && nextStep !== "map" ? `${match.path}/${nextStep}/${index}` : `${match.path}/${nextStep}`;
    }

    redirectWithHistory(nextPage);
  };


  if(params && Object.keys(params).length>0 && window.location.href.includes("/searchhall") && sessionStorage.getItem("docReqScreenByBack") !== "true")
    {
      clearParams();
      queryClient.invalidateQueries("CHB_CREATE");
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
    queryClient.invalidateQueries("CHB_CREATE");
  };
  /* use newConfig instead of commonFields for local development in case needed */
  let commonFields = citizenConfig;
  commonFields.forEach((obj) => {
    config = config.concat(obj.body.filter((a) => !a.hideInCitizen));
  });
  
  config.indexRoute = "searchhall";

  const CheckPage = Digit?.ComponentRegistryService?.getComponent("CHBCheckPage");
  const CHBAcknowledgement = Digit?.ComponentRegistryService?.getComponent("CHBAcknowledgement");

  
  
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
      <Route path={`${match.path}/acknowledgement`}>
        <CHBAcknowledgement data={params} onSuccess={onSuccess} />
      </Route>
      <Route>
        <Redirect to={`${match.path}/${config.indexRoute}`} />
      </Route>
    </Switch>
  );
};

export default CHBCreate;