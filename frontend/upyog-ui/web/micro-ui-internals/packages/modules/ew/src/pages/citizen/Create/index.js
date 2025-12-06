import { Loader } from "@upyog/digit-ui-react-components";
import React, { Fragment } from "react";
import { useTranslation } from "react-i18next";
import { useQueryClient } from "react-query";
import { Redirect, Route, Switch, useHistory, useLocation, useRouteMatch } from "react-router-dom";
import { citizenConfig } from "../../../config/Create/citizenconfig";

/**
 * Main component for E-Waste creation workflow.
 * Handles multi-step form navigation, data management and submission for E-Waste requests.
 * 
 * @param {Object} props Component properties
 * @param {string} props.parentRoute Base route for the creation workflow
 * @returns {JSX.Element} Multi-step form interface for E-Waste creation
 */
const EWCreate = ({ parentRoute }) => {

  const queryClient = useQueryClient();
  const match = useRouteMatch();
  const { t } = useTranslation();
  const { pathname } = useLocation();
  const history = useHistory();
  const stateId = Digit.ULBService.getStateId();
  let config = [];
  const [params, setParams, clearParams] = Digit.Hooks.useSessionStorage("EWASTE_CREATE", {});
  let { data: commonFields, isLoading } = Digit.Hooks.pt.useMDMS(stateId, "PropertyTax", "CommonFieldsConfig");

  /**
   * Handles navigation between form steps
   * Determines next route based on current path and navigation parameters
   * 
   * @param {boolean} skipStep Whether to skip the current step
   * @param {number} index Current step index
   * @param {boolean} isAddMultiple Whether adding multiple items
   * @param {string} key Form section identifier
   */
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

  /**
   * Handles form section data updates
   * Updates session storage with new form data
   * 
   * @param {string} key Section identifier
   * @param {Object} data Section form data
   * @param {boolean} skipStep Whether to skip next step
   * @param {number} index Current step index
   * @param {boolean} isAddMultiple Whether adding multiple items
   */
  function handleSelect(key, data, skipStep, index, isAddMultiple = false) {
    if (key === "owners") {
      let owners = params.owners || [];
      owners[index] = data;
      setParams({ ...params, ...{ [key]: [...owners] } });
    } else if (key === "units") {
      let units = params.units || [];
      units = data;

      setParams({ ...params, units });
    } else {
      setParams({ ...params, ...{ [key]: { ...params[key], ...data } } });
    }
    goNext(skipStep, index, isAddMultiple, key);
  }

  const handleSkip = () => { };
  const handleMultiple = () => { };

  /**
   * Handles successful form submission
   * Clears session storage and invalidates cached data
   */
  const onSuccess = () => {
    clearParams();
    queryClient.invalidateQueries("EWASTE_CREATE");
  };
  if (isLoading) {
    return <Loader />;
  }

  commonFields = citizenConfig;
  commonFields.forEach((obj) => {
    config = config.concat(obj.body.filter((a) => !a.hideInCitizen));
  });

  config.indexRoute = "productdetails";

  const CheckPage = Digit?.ComponentRegistryService?.getComponent("EWCheckPage");
  const EWASTEAcknowledgement = Digit?.ComponentRegistryService?.getComponent("EWASTEAcknowledgement");

  if (params && Object.keys(params).length > 0 && window.location.href.includes("/info") && sessionStorage.getItem("docReqScreenByBack") !== "true") {
    clearParams();
    queryClient.invalidateQueries("EWASTE_CREATE");
  }

  const ewasteCreate = async () => {
    history.push(`${match.path}/acknowledgement`);
  };
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
        <CheckPage onSubmit={ewasteCreate} value={params} />
      </Route>
      <Route path={`${match.path}/acknowledgement`}>
        <EWASTEAcknowledgement data={params} onSuccess={onSuccess} />
      </Route>
      <Route>
        <Redirect to={`${match.path}/${config.indexRoute}`} />
      </Route>
    </Switch>
  );
};

export default EWCreate;