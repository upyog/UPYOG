import React, { use } from "react";
import { useTranslation } from "react-i18next";
import { useQueryClient } from "react-query";
import { Redirect, Route, Switch, useHistory, useLocation, useRouteMatch } from "react-router-dom";
import { commonConfig } from "../../../config/config";
import { Timeline } from "@upyog/digit-ui-react-components";


const WTCreate = () => {
  const queryClient = useQueryClient();
  const match = useRouteMatch();
  const { t } = useTranslation();
  const { pathname } = useLocation();
  const history = useHistory();

  let config = [];
  const [params, setParams, clearParams] = Digit.Hooks.useSessionStorage("WT_Create", {});

  // Sets the serviceType in case of employee side for WT, MT, and TP
  if ((!params.serviceType || Object.keys(params.serviceType).length === 0) && pathname.includes("employee")) {
    if (pathname.includes("mt")) {
      console.log("MT Create");
      setParams({
        "serviceType": {
          "serviceType": {
            "code": "MobileToilet",
            "i18nKey": "Mobile Toilet",
            "value": "Mobile Toilet"
          }
        }
      })
    } else if (pathname.includes("tp")) {
      setParams({
        "serviceType": {
          "serviceType": {
            "code": "TREE_PRUNING",
            "i18nKey": "Tree Pruning",
            "value": "Tree Pruning"
          }
        }
      })
    }
    else {
      setParams({
        "serviceType": {
          "serviceType": {
            "code": "WT", "i18nKey": "Water Tanker", "value": "Water Tanker"
          }
        }
      })
    }
  }

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
    // Change next step to "toiletRequest-details" if the current step is "request-details" and the service type code is not "WT".
    if (nextStep === "request-details") { 
        const code = params?.serviceType?.serviceType?.code;
        if (code === "MobileToilet") {
          nextStep = "toiletRequest-details";
        } else if (code === "TREE_PRUNING") {
          nextStep = "treePruningRequest-details"; 
        }
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

 

  if(params && Object.keys(params).length>0 && window.location.href.includes("/service-type") && sessionStorage.getItem("docReqScreenByBack") !== "true")
    {
      clearParams();
      queryClient.invalidateQueries("WT_Create");
    }

  const wt_create = async () => {

    if(params?.serviceType?.serviceType?.code === "WT"){
      history.push(`${match.path}/wt-acknowledgement`);
    }
    if(params?.serviceType?.serviceType?.code === "MobileToilet"){
      history.push(`${match.path}/mt-acknowledgement`);
    }
    if(params?.serviceType?.serviceType?.code === "TREE_PRUNING"){
      history.push(`${match.path}/tp-acknowledgement`);
    }
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

  const onSuccess = () => {
    clearParams();
    queryClient.invalidateQueries("WT_Create");
  };

  let commonFields = commonConfig;
  commonFields.forEach((obj) => {
    config = config.concat(obj.body.filter((a) => !a.hideInCitizen));
  });

  // Changes the indexRoute based on the pathname
  config.indexRoute = pathname.includes("citizen")? "service-type" : "info";

  const CheckPage = Digit?.ComponentRegistryService?.getComponent("WTCheckPage");
  const WTAcknowledgement = Digit?.ComponentRegistryService?.getComponent("WTAcknowledgement");
  const MTAcknowledgement = Digit?.ComponentRegistryService?.getComponent("MTAcknowledgement");
  const TPAcknowledgement = Digit?.ComponentRegistryService?.getComponent("TPAcknowledgement");




  return (
    <React.Fragment>
    <Timeline config={config}/>
    <Switch>
      {config.map((routeObj, index) => {
        const { component, texts, inputs, key,additionaFields } = routeObj;
        const Component = typeof component === "string" ? Digit.ComponentRegistryService.getComponent(component) : component;
        return (
          <Route path={`${match.path}/${routeObj.route}`} key={index}>
            <Component config={{ texts, inputs, key,additionaFields }} onSelect={handleSelect} t={t} formData={params}/>
          </Route>
        );
      })}
      <Route path={`${match.path}/check`}>
        <CheckPage onSubmit={wt_create} value={params} />
      </Route>
      <Route path={`${match.path}/wt-acknowledgement`}>
        <WTAcknowledgement data={params} onSuccess={onSuccess} />
      </Route>
      <Route path={`${match.path}/mt-acknowledgement`}>
        <MTAcknowledgement data={params} onSuccess={onSuccess} />
      </Route>
       <Route path={`${match.path}/tp-acknowledgement`}>
        <TPAcknowledgement data={params} onSuccess={onSuccess} />
      </Route>
      <Route>
        <Redirect to={`${match.path}/${config.indexRoute}`} />
      </Route>
    </Switch>
    </React.Fragment>
  );
};

export default WTCreate;
