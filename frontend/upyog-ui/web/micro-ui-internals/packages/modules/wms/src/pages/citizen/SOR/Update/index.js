// import React from "react";
import { Loader } from "@egovernments/digit-ui-react-components";
import React, { useEffect } from "react";
import { useTranslation } from "react-i18next";
import { useQueryClient } from "react-query";
import { Redirect, Route, Switch, useHistory, useLocation, useRouteMatch } from "react-router-dom";
import { newConfig } from "../../../../components/config";

import { checkArrayLength, stringReplaceAll,getSuperBuiltUpareafromob } from "./../../../../utils";

const getSorEditDetails = (data = { }) => {
  // converting owners details

  return data;
};
const WmsSorUpdate = ({ parentRoute }) => {
  const queryClient = useQueryClient();
  const match = useRouteMatch();
  const { t } = useTranslation();
  const { pathname } = useLocation();
  const history = useHistory();
  let config = [];
  const [params, setParams, clearParams] = Digit.Hooks.useSessionStorage("PT_CREATE_SOR", { });
  const stateId = Digit.ULBService.getStateId();
  let { data: commonFields, isLoading } = Digit.Hooks.pt.useMDMS(stateId, "SOR", "CommonFieldsConfig");
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const acknowledgementIds = window.location.href.split("/").pop();
  const propertyIds = window.location.href.split("/").pop();
  let application = { };
  const updateSor = window.location.href.includes("action=UPDATE");
  const typeOfSor = window.location.href.includes("UPDATE") ? true : false;
  const wmsSor = JSON.parse(sessionStorage.getItem("wms-sor")) || { };
  //const data = { Properties: [wmsSor] };
    const { isLoading: isPTloading, isError, error, data } = Digit.Hooks.pt.useSorSearch(
    { filters: updateSor ? { propertyIds, isSearchInternal:true  } : { acknowledgementIds, isSearchInternal:true  } },
    {
      filters: updateSor ? { propertyIds, isSearchInternal:true  } : { acknowledgementIds, isSearchInternal:true  },
    }
  ); 
  sessionStorage.setItem("isEditApplication", false);

  useEffect(() => {
    application = data?.Properties && data.Properties[0] && data.Properties[0];
    if (data && application && data?.Properties?.length > 0) {
      application = data?.Properties[0];
      if (updateSor) {
        application.isUpdateSor = true;
        application.isEditSor = false;
      } else {
        application.isUpdateSor = typeOfSor;
        application.isEditSor = true;
      }
      sessionStorage.setItem("sorInitialObject", JSON.stringify({ ...application }));
      let sorEditDetails = getSorEditDetails(application);
      setParams({ ...params, ...sorEditDetails });
    }
  }, [data]);

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
    let { nextStep = { } } = config.find((routeObj) => routeObj.route === currentPath);
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

        if (`${nextStep[sessionStorage.getItem("area")]}` !== "map") {
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
        (nextStep[sessionStorage.getItem("IsAnyPartOfThisFloorUnOccupied")] == "map" ||
          nextStep[sessionStorage.getItem("IsAnyPartOfThisFloorUnOccupied")] == "un-occupied-area")
      ) {
        nextStep = `${nextStep[sessionStorage.getItem("IsAnyPartOfThisFloorUnOccupied")]}`;
      } else if (nextStep[sessionStorage.getItem("subusagetypevar")]) {
        nextStep = `${nextStep[sessionStorage.getItem("subusagetypevar")]}`;
      } else if (nextStep[sessionStorage.getItem("area")]) {
        nextStep = `${nextStep[sessionStorage.getItem("area")]}`;
      } else if (nextStep[sessionStorage.getItem("IsThisFloorSelfOccupied")]) {
        nextStep = `${nextStep[sessionStorage.getItem("IsThisFloorSelfOccupied")]}`;
      } else if (nextStep[sessionStorage.getItem("SorType")]) {
        nextStep = `${nextStep[sessionStorage.getItem("SorType")]}`;
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
      nextPage = isMultiple && nextStep !== "map" ? `${match.path}/${nextStep}/${index}` : `${match.path}/${nextStep}`;
    }

    redirectWithHistory(nextPage);
  };

  const createSor = async () => {
    history.push(`${match.path}/acknowledgement`);
  };

  function handleSelect(key, data, skipStep, index, isAddMultiple = false) {
    if (key === "owners") {
      let owners = params.owners || [];
      owners[index] = data;
      setParams({ ...params, ...{ [key]: [...owners] } });
    } else if (key === "units") {
      // let units = params.units || [];
      // units[index] = data;
      // setParams({ ...params, units });
      setParams({ ...params, ...{ [key]: [...data] } });
    } else {
      setParams({ ...params, ...{ [key]: { ...params[key], ...data } } });
    }
    goNext(skipStep, index, isAddMultiple, key);
  }

  const handleSkip = () => { };
  const handleMultiple = () => { };

  const onSuccess = () => {
    clearParams();
    queryClient.invalidateQueries("PT_CREATE_PROPERTY");
    sessionStorage.setItem("propertyInitialObject", JSON.stringify({ }));
    sessionStorage.setItem("pt-property", JSON.stringify({ }));
  };


  if (isLoading || isPTloading) {
    return <Loader />;
  }

  /* use newConfig instead of commonFields for local development in case needed */
  commonFields=newConfig;
  commonFields.forEach((obj) => {
    config = config.concat(obj.body.filter((a) => !a.hideInCitizen));
  });
  config.indexRoute = `info`;
  const  CheckPage = Digit?.ComponentRegistryService?.getComponent('PTCheckPage');
  const PTAcknowledgement = Digit?.ComponentRegistryService?.getComponent('PTAcknowledgement');

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
        <CheckPage onSubmit={createSor} value={params} />
      </Route>
      <Route path={`${match.path}/acknowledgement`}>
        <PTAcknowledgement data={params} onSuccess={onSuccess} />
      </Route>
      <Route>
        <Redirect to={`${match.path}/${config.indexRoute}`} />
      </Route>
    </Switch>
  );
};

export default WmsSorUpdate;
