import { Loader } from "@egovernments/digit-ui-react-components";
import React ,{Fragment, useState}from "react";
import { useTranslation } from "react-i18next";
import { useQueryClient } from "react-query";
import { Redirect, Route, Switch, useHistory, useLocation, useRouteMatch } from "react-router-dom";
import { newConfig } from "../../../config/Create/config";

const CreateProperty = ({ parentRoute }) => {
  const queryClient = useQueryClient();
  const match = useRouteMatch();
  const { t } = useTranslation();
  const { pathname } = useLocation();
  const  location = useLocation();
  const history = useHistory();
  console.log("history==",history,location)
  const stateId = Digit.ULBService.getStateId();
  let config = [];
  const [amalgamationState, setAmalgamationState] = useState(location?.state ? location.state : null);
  // let amalgamationState = null;
  // if(location.state) {
  //   setAmalgamationState(location.state)
  // }
  console.log("amalgamationState===",amalgamationState)
  const [params, setParams, clearParams] = Digit.Hooks.useSessionStorage("PT_CREATE_PROPERTY", {});
  let { data: commonFields, isLoading } = Digit.Hooks.pt.useMDMS(stateId, "PropertyTax", "CommonFieldsConfig");
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
    console.log("currentPath==",currentPath);
    let { nextStep = {} } = config.find((routeObj) => routeObj.route === currentPath);
    // debugger
    if (typeof nextStep == "object" && nextStep != null && isMultiple != false) {
      if (nextStep[sessionStorage.getItem("ownershipCategory")]) {
        nextStep = `${nextStep[sessionStorage.getItem("ownershipCategory")]}/${index}`;
      } else if (nextStep[sessionStorage.getItem("IsAnyPartOfThisFloorUnOccupied")]) {
        if (`${nextStep[sessionStorage.getItem("IsAnyPartOfThisFloorUnOccupied")]}` === "un-occupied-area") {
          nextStep = `${nextStep[sessionStorage.getItem("IsAnyPartOfThisFloorUnOccupied")]}/${index}`;
        } else {
          nextStep = `${nextStep[sessionStorage.getItem("IsAnyPartOfThisFloorUnOccupied")]}`;
        }
      } else if (nextStep[sessionStorage.getItem("subusagetypevar")]) {
        nextStep = `${nextStep[sessionStorage.getItem("subusagetypevar")]}/${index}`;
      } else if (nextStep[sessionStorage.getItem("area")]) {
        // nextStep = `${nextStep[sessionStorage.getItem("area")]}/${index}`;

        if (`${nextStep[sessionStorage.getItem("area")]}` !== "pincode") {
          nextStep = `${nextStep[sessionStorage.getItem("area")]}/${index}`;
        } else {
          nextStep = `${nextStep[sessionStorage.getItem("area")]}`;
        }
      } else if (nextStep[sessionStorage.getItem("IsThisFloorSelfOccupied")]) {
        nextStep = `${nextStep[sessionStorage.getItem("IsThisFloorSelfOccupied")]}/${index}`;
      } else {
        nextStep = `${nextStep[sessionStorage.getItem("noOofBasements")]}/${index}`;
        //nextStep = `${"floordetails"}/${index}`;
      }
    }
    if (typeof nextStep == "object" && nextStep != null && isMultiple == false) {
      if (
        nextStep[sessionStorage.getItem("IsAnyPartOfThisFloorUnOccupied")] &&
        (nextStep[sessionStorage.getItem("IsAnyPartOfThisFloorUnOccupied")] == "pincode" ||
          nextStep[sessionStorage.getItem("IsAnyPartOfThisFloorUnOccupied")] == "un-occupied-area")
      ) {
        nextStep = `${nextStep[sessionStorage.getItem("IsAnyPartOfThisFloorUnOccupied")]}`;
      } else if (nextStep[sessionStorage.getItem("subusagetypevar")]) {
        nextStep = `${nextStep[sessionStorage.getItem("subusagetypevar")]}`;
      } else if (nextStep[sessionStorage.getItem("area")]) {
        nextStep = `${nextStep[sessionStorage.getItem("area")]}`;
      } else if (nextStep[sessionStorage.getItem("IsThisFloorSelfOccupied")]) {
        nextStep = `${nextStep[sessionStorage.getItem("IsThisFloorSelfOccupied")]}`;
      } else if (nextStep[sessionStorage.getItem("PropertyType")]) {
        nextStep = `${nextStep[sessionStorage.getItem("PropertyType")]}`;
      } else if (nextStep[sessionStorage.getItem("isResdential")]) {
        nextStep = `${nextStep[sessionStorage.getItem("isResdential")]}`;
      }
    }
    /* if (nextStep === "is-this-floor-self-occupied") {
      isMultiple = false;
    } */
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
      nextPage = isMultiple && nextStep !== "pincode" ? `${match.path}/${nextStep}/${index}` : `${match.path}/${nextStep}`;
    }

    redirectWithHistory(nextPage);
  };

  if(params && Object.keys(params).length>0 && window.location.href.includes("/info") && sessionStorage.getItem("docReqScreenByBack") !== "true")
    {
      clearParams();
      queryClient.invalidateQueries("PT_CREATE_PROPERTY");
    }

  const createProperty = async () => {
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
      // console.log("params=========",params)
      // if(amalgamationState && amalgamationState?.action=='Amalgamation') {
      //   setParams({ ...params, ...{ ['amalgamationDetails']: 'kkk' } });
      // }
      
    }
    console.log("------",skipStep, index, isAddMultiple, key)
    goNext(skipStep, index, isAddMultiple, key);
  }

  const handleSkip = () => {};
  const handleMultiple = () => {};

  const onSuccess = () => {
    clearParams();
    queryClient.invalidateQueries("PT_CREATE_PROPERTY");
  };
  if (isLoading) {
    return <Loader />;
  }

  // commonFields=newConfig;
  /* use newConfig instead of commonFields for local development in case needed */
  commonFields = newConfig;
  console.log("commonFields11==",commonFields)
  commonFields.forEach((obj) => {
    config = config.concat(obj.body.filter((a) => !a.hideInCitizen));
  });
  console.log("commonFields22==",commonFields)
  config.indexRoute = "info";
  const CheckPage = Digit?.ComponentRegistryService?.getComponent("PTCheckPage");
  const PTAcknowledgement = Digit?.ComponentRegistryService?.getComponent("PTAcknowledgement");
  console.log("config==",config)
  return (
    <div>
      {amalgamationState && 
      <div>
        <div>Amalgamated Properties:</div>
        {amalgamationState?.propertyDetails && amalgamationState.propertyDetails.map((e)=>(
          <div>Property Id: {e?.property_id}</div>
        ))}  
      </div>}
      <Switch>
      
      {config.map((routeObj, index) => {
        const { component, texts, inputs, key } = routeObj;
        // console.log("routeObj==",routeObj)
        const Component = typeof component === "string" ? Digit.ComponentRegistryService.getComponent(component) : component;
        return (
          <Route path={`${match.path}/${routeObj.route}`} key={index}>
            <div>hiii</div>
            <Component config={{ texts, inputs, key }} onSelect={handleSelect} onSkip={handleSkip} t={t} formData={params} onAdd={handleMultiple} />
          </Route>
        );
      })}
      <Route path={`${match.path}/check`}>
        <CheckPage onSubmit={createProperty} value={params} />
      </Route>
      <Route path={`${match.path}/acknowledgement`}>
        <PTAcknowledgement data={params} onSuccess={onSuccess} />
      </Route>
      <Route>
        <Redirect to={`${match.path}/${config.indexRoute}`} />
      </Route>
    </Switch>
    </div>
    
  );
};

export default CreateProperty;
