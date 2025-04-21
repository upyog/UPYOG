
import React ,{Children, Fragment}from "react";
import { useTranslation } from "react-i18next";
import { useQueryClient } from "react-query";
import { Redirect, Route, Switch, useHistory, useLocation, useRouteMatch } from "react-router-dom";
import { Config } from "../../../config/config";
import { Timeline } from "@nudmcdgnpm/digit-ui-react-components";

const CndCreate = ({ parentRoute }) => {
  const queryClient = useQueryClient();
  const match = useRouteMatch();
  const { t } = useTranslation();
  const { pathname } = useLocation();
  const history = useHistory();
  const stateId = Digit.ULBService.getStateId();
  let config = [];
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

  // to clear formdata if the data is present before coming to first page of form
  if(params && Object.keys(params).length>0 && window.location.href.includes("/info") && sessionStorage.getItem("docReqScreenByBack") !== "true")
    {
      clearParams();
      queryClient.invalidateQueries("CND_Creates");
    }

  const cndCreate = async () => {
    history.replace(`${match.path}/acknowledgement`);
  };

  function handleSelect(key, data, skipStep, index, isAddMultiple = false) {
    if (key === "owners") {
      let owners = params.owners || [];
      owners[index] = data;
      setParams({ ...params, ...{ [key]: [...owners] } });
    } 
    else {
      setParams({ ...params, ...{ [key]: { ...params[key], ...data } } });
    }
    goNext(skipStep, index, isAddMultiple, key);
  }

  const handleSkip = () => {};
  const handleMultiple = () => {};


  /**
   * this onSuccess function will execute once the application submitted successfully 
   * it will clear all the params from the session storage  and also invalidate the query client
   * as well as remove the beneficiary & disabilityStatus from the session storage
   */
  const onSuccess = () => {
    clearParams();
    queryClient.invalidateQueries("CND_Creates");
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
    <Timeline config={config}/>
    <Switch>
      {config.map((routeObj, index) => {
        const { component, texts, inputs, key} = routeObj;
        const Component = typeof component === "string" ? Digit.ComponentRegistryService.getComponent(component) : component;
        const user = Digit.UserService.getUser().info.type;
        return (
          <Route path={`${match.path}/${routeObj.route}`} key={index}>
            <Component config={{ texts, inputs, key }} onSelect={handleSelect} onSkip={handleSkip} t={t} formData={params} onAdd={handleMultiple} userType={user}/>
          </Route>
        );
      })}

      <Route path={`${match.path}/check`}>
        <CndCheckPage onSubmit={cndCreate} value={params} /> 
      </Route>
      <Route path={`${match.path}/acknowledgement`}>
        <CndAcknowledgement data={params} onSuccess={onSuccess}/>
      </Route>
      <Route>
        <Redirect to={`${match.path}/${config.indexRoute}`} />
      </Route>
    </Switch>
    </React.Fragment>
  );
};

export default CndCreate;