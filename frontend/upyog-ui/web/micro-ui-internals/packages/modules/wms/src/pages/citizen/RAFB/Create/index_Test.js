import React, {useEffect, useState} from "react";
import { useTranslation } from "react-i18next";
import { useQueryClient } from "react-query";
import { Redirect, Route, Switch, useHistory, useLocation, useRouteMatch } from "react-router-dom";
// import { newConfig as newConfigWMS } from "../../../../config/RAFBconfig";
import { newConfig } from "../../../../config/RAFBconfigTest";
import { Loader } from "@egovernments/digit-ui-react-components";

const WmsRAFBCreateTest=()=>{
    const queryClient = useQueryClient();
  const match = useRouteMatch();
  const { t } = useTranslation();
  const { pathname } = useLocation();
  const history = useHistory();

  //################# PT code start
  // const stateId = Digit.ULBService.getStateId();
  // let config = [];
  // const [params, setParams, clearParams] = Digit.Hooks.useSessionStorage("PT_CREATE_PROPERTY", {});
  // let { data: commonFields, isLoading } = Digit.Hooks.pt.useMDMS(stateId, "PropertyTax", "CommonFieldsConfig");
  // console.log("commonFields ",commonFields)
  // const goNext = (skipStep, index, isAddMultiple, key) => {
  //   let currentPath = pathname.split("/").pop(),
  //     lastchar = currentPath.charAt(currentPath.length - 1),
  //     isMultiple = false,
  //     nextPage;
  //   if (Number(parseInt(currentPath)) || currentPath == "0" || currentPath == "-1") {
  //     if (currentPath == "-1" || currentPath == "-2") {
  //       currentPath = pathname.slice(0, -3);
  //       currentPath = currentPath.split("/").pop();
  //       isMultiple = true;
  //     } else {
  //       currentPath = pathname.slice(0, -2);
  //       currentPath = currentPath.split("/").pop();
  //       isMultiple = true;
  //     }
  //   } else {
  //     isMultiple = false;
  //   }
  //   if (!isNaN(lastchar)) {
  //     isMultiple = true;
  //   }
  //   let { nextStep = {} } = config.find((routeObj) => routeObj.route === currentPath);
  //   if (typeof nextStep == "object" && nextStep != null && isMultiple != false) {
  //     if (nextStep[sessionStorage.getItem("ownershipCategory")]) {
  //       nextStep = `${nextStep[sessionStorage.getItem("ownershipCategory")]}/${index}`;
  //     } else if (nextStep[sessionStorage.getItem("IsAnyPartOfThisFloorUnOccupied")]) {
  //       if (`${nextStep[sessionStorage.getItem("IsAnyPartOfThisFloorUnOccupied")]}` === "un-occupied-area") {
  //         nextStep = `${nextStep[sessionStorage.getItem("IsAnyPartOfThisFloorUnOccupied")]}/${index}`;
  //       } else {
  //         nextStep = `${nextStep[sessionStorage.getItem("IsAnyPartOfThisFloorUnOccupied")]}`;
  //       }
  //     } else if (nextStep[sessionStorage.getItem("subusagetypevar")]) {
  //       nextStep = `${nextStep[sessionStorage.getItem("subusagetypevar")]}/${index}`;
  //     } else if (nextStep[sessionStorage.getItem("area")]) {
  //       // nextStep = `${nextStep[sessionStorage.getItem("area")]}/${index}`;

  //       if (`${nextStep[sessionStorage.getItem("area")]}` !== "map") {
  //         nextStep = `${nextStep[sessionStorage.getItem("area")]}/${index}`;
  //       } else {
  //         nextStep = `${nextStep[sessionStorage.getItem("area")]}`;
  //       }
  //     } else if (nextStep[sessionStorage.getItem("IsThisFloorSelfOccupied")]) {
  //       nextStep = `${nextStep[sessionStorage.getItem("IsThisFloorSelfOccupied")]}/${index}`;
  //     } else {
  //       nextStep = `${nextStep[sessionStorage.getItem("noOofBasements")]}/${index}`;
  //       //nextStep = `${"floordetails"}/${index}`;
  //     }
  //   }
  //   if (typeof nextStep == "object" && nextStep != null && isMultiple == false) {
  //     if (
  //       nextStep[sessionStorage.getItem("IsAnyPartOfThisFloorUnOccupied")] &&
  //       (nextStep[sessionStorage.getItem("IsAnyPartOfThisFloorUnOccupied")] == "map" ||
  //         nextStep[sessionStorage.getItem("IsAnyPartOfThisFloorUnOccupied")] == "un-occupied-area")
  //     ) {
  //       nextStep = `${nextStep[sessionStorage.getItem("IsAnyPartOfThisFloorUnOccupied")]}`;
  //     } else if (nextStep[sessionStorage.getItem("subusagetypevar")]) {
  //       nextStep = `${nextStep[sessionStorage.getItem("subusagetypevar")]}`;
  //     } else if (nextStep[sessionStorage.getItem("area")]) {
  //       nextStep = `${nextStep[sessionStorage.getItem("area")]}`;
  //     } else if (nextStep[sessionStorage.getItem("IsThisFloorSelfOccupied")]) {
  //       nextStep = `${nextStep[sessionStorage.getItem("IsThisFloorSelfOccupied")]}`;
  //     } else if (nextStep[sessionStorage.getItem("PropertyType")]) {
  //       nextStep = `${nextStep[sessionStorage.getItem("PropertyType")]}`;
  //     } else if (nextStep[sessionStorage.getItem("isResdential")]) {
  //       nextStep = `${nextStep[sessionStorage.getItem("isResdential")]}`;
  //     }
  //   }
  //   /* if (nextStep === "is-this-floor-self-occupied") {
  //     isMultiple = false;
  //   } */
  //   let redirectWithHistory = history.push;
  //   if (skipStep) {
  //     redirectWithHistory = history.replace;
  //   }
  //   if (isAddMultiple) {
  //     nextStep = key;
  //   }
  //   if (nextStep === null) {
  //     return redirectWithHistory(`${match.path}/check`);
  //   }
  //   if (!isNaN(nextStep.split("/").pop())) {
  //     nextPage = `${match.path}/${nextStep}`;
  //   } else {
  //     nextPage = isMultiple && nextStep !== "map" ? `${match.path}/${nextStep}/${index}` : `${match.path}/${nextStep}`;
  //   }

  //   redirectWithHistory(nextPage);
  // };

  // if(params && Object.keys(params).length>0 && window.location.href.includes("/info") && sessionStorage.getItem("docReqScreenByBack") !== "true")
  //   {
  //     clearParams();
  //     queryClient.invalidateQueries("PT_CREATE_PROPERTY");
  //   }

  // const createProperty = async () => {
  //   history.push(`${match.path}/acknowledgement`);
  // };

  // function handleSelect(key, data, skipStep, index, isAddMultiple = false) {
  //   if (key === "owners") {
  //     let owners = params.owners || [];
  //     owners[index] = data;
  //     setParams({ ...params, ...{ [key]: [...owners] } });
  //   } else if (key === "units") {
  //     let units = params.units || [];
  //     // if(index){units[index] = data;}else{
  //     units = data;

  //     setParams({ ...params, units });
  //   } else {
  //     setParams({ ...params, ...{ [key]: { ...params[key], ...data } } });
  //   }
  //   goNext(skipStep, index, isAddMultiple, key);
  // }

  // const handleSkip = () => {};
  // const handleMultiple = () => {};

  // const onSuccess = () => {
  //   clearParams();
  //   queryClient.invalidateQueries("PT_CREATE_PROPERTY");
  // };
  // // if (isLoading) {
  // //   return <Loader />;
  // // }

  // // commonFields=newConfig;
  // /* use newConfig instead of commonFields for local development in case needed */
  // // commonFields = newConfig;
  

  // const commonFields_old = newConfig;
  // console.log("commonFields newConfig ",commonFields_old)
  // commonFields_old.forEach((obj) => {
  //   config = config.concat(obj.body.filter((a) => !a.hideInCitizen));
  //   console.log("config ssss pre ",config )
  // });
  // console.log("config ssss ",config )
  // config.indexRoute = "info";
  // const CheckPage = Digit?.ComponentRegistryService?.getComponent("WMSCheckPage");
  // const PTAcknowledgement = Digit?.ComponentRegistryService?.getComponent("PTAcknowledgement");
  // // console.log("Component abc Digit.ComponentRegistryService.getComponent out ",Digit.ComponentRegistryService.getComponent)
  //################# PT code end

  //################# TL code start
  const [params, setParams, clearParams] = Digit.Hooks.useSessionStorage("TL_CREATE_TRADE", {});
  console.log("params ",params)
  let config = [];
  let isReneworEditTrade = window.location.href.includes("/renew-trade/") || window.location.href.includes("/edit-application/")
  console.log("isReneworEditTrade ",isReneworEditTrade)
  const goNext = (skipStep, index, isAddMultiple, key, isPTCreateSkip) => {
    console.log("skipStep, index, isAddMultiple, key, isPTCreateSkip ",skipStep, index, isAddMultiple, key, isPTCreateSkip)
    let currentPath = pathname.split("/").pop(),
    
      nextPage;
    //   debugger
    console.log("currentPath ",currentPath);
    let { nextStep = {} } = config.find((routeObj) => routeObj.route === currentPath);
    console.log("currentPath nextStep",nextStep);
    let { isCreateEnabled : enableCreate = true } = config.find((routeObj) => routeObj.route === currentPath);
    if (typeof nextStep == "object" && nextStep != null) {
      if((params?.cptId?.id || params?.cpt?.details?.propertyId || (isReneworEditTrade && params?.cpt?.details?.propertyId ))  && (nextStep[sessionStorage.getItem("isAccessories")] && nextStep[sessionStorage.getItem("isAccessories")] === "know-your-property")  )
      {
        nextStep = "property-details";
      }
      if (
        nextStep[sessionStorage.getItem("isAccessories")] &&
        (
          nextStep[sessionStorage.getItem("isAccessories")] === "accessories-details" ||
          nextStep[sessionStorage.getItem("isAccessories")] === "map" ||
          nextStep[sessionStorage.getItem("isAccessories")] === "owner-ship-details" || 
          nextStep[sessionStorage.getItem("isAccessories")] === "other-trade-details")
      ) {
        nextStep = `${nextStep[sessionStorage.getItem("isAccessories")]}`;
        console.log("currentPath nextStep in if con ",nextStep)
      } else if (
        nextStep[sessionStorage.getItem("StructureType")] &&
        (nextStep[sessionStorage.getItem("StructureType")] === "Building-type" ||
          nextStep[sessionStorage.getItem("StructureType")] === "vehicle-type")
      ) {
        nextStep = `${nextStep[sessionStorage.getItem("StructureType")]}`;
      } else if (
        nextStep[sessionStorage.getItem("KnowProperty")] &&
        (nextStep[sessionStorage.getItem("KnowProperty")] === "search-property" ||
          nextStep[sessionStorage.getItem("KnowProperty")] === "create-property")
      ) {
          if(nextStep[sessionStorage.getItem("KnowProperty")] === "create-property" && !enableCreate)
          {
            nextStep = `map`;
          }
          else{
         nextStep = `${nextStep[sessionStorage.getItem("KnowProperty")]}`;
          }
      }
    }
    if(nextStep === "know-your-property" && params?.TradeDetails?.StructureType?.code === "MOVABLE")
    {
      nextStep = "map";
    }
    if(nextStep === "landmark" && params?.TradeDetails?.StructureType?.code === "MOVABLE")
    {
      nextStep = "owner-ship-details";
    }
    if(nextStep === "owner-details" && (sessionStorage.getItem("isSameAsPropertyOwner") === "true"))
    {
      nextStep = "proof-of-identity"
    }
    if( (params?.cptId?.id || params?.cpt?.details?.propertyId || (isReneworEditTrade && params?.cpt?.details?.propertyId ))  && nextStep === "know-your-property" )
    { 
      nextStep = "property-details";
    }
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
    if(isPTCreateSkip && nextStep === "acknowledge-create-property")
    {
      nextStep = "map";
    }
    nextPage = `${match.path}/${nextStep}`;
    redirectWithHistory(nextPage);
  };
  function handleSelect(key, data, skipStep, index, isAddMultiple = false) {
    console.log("onSelect TLInfo key, data, skipStep, index, isAddMultiple = false",{key, data, skipStep, index, isAddMultiple})
  alert("handleSelect")
      if(key === "formData")
      setParams({...data})
      else{
      setParams({ ...params, ...{ [key]: { ...params[key], ...data } } });
      if(key === "isSkip" && data === true)
      {
        goNext(skipStep, index, isAddMultiple, key, true);
      }
      else
      {
        goNext(skipStep, index, isAddMultiple, key);
      }
    }
    }
    
    const handleSkip = () => {};

//RAFBConfig.js file setup
// let { data: newConfig, isLoading } = Digit.Hooks.tl.useMDMS.getFormConfig(stateId, {});
// newConfig = newConfig ? newConfig : newConfigWMS;
const newConfig_temp = newConfig && newConfig;
newConfig_temp?.forEach((obj) => {
  config = config.concat(obj.body.filter((a) => !a.hideInCitizen));
});
let skipenanbledOb = newConfig_temp?.filter(obj => obj?.body?.some(com => com.component === "CPTCreateProperty"))?.[0];
let skipenabled = skipenanbledOb?.body?.filter((ob) => ob?.component === "CPTCreateProperty")?.[0]?.isSkipEnabled;
sessionStorage.setItem("skipenabled",skipenabled);
config.indexRoute = "info";

const CheckPage = Digit?.ComponentRegistryService?.getComponent("WMSCheckPage");
// const TLAcknowledgement = Digit?.ComponentRegistryService?.getComponent("TLAcknowledgement");
//################# TL code end
return(<div>
        <Switch>
      {config.map((routeObj, index) => {
        console.log("Component abc routeObj ",routeObj)
        const { component, texts, inputs, key, isSkipEnabled } = routeObj;
        console.log("Component abc typeof component ",typeof component === "string")
        console.log("Component abc Digit.ComponentRegistryService ",Digit.ComponentRegistryService)
        console.log("Component abc Digit.ComponentRegistryService.getComponent ",Digit.ComponentRegistryService.getComponent)
        console.log("Component abc Digit.ComponentRegistryService.getComponent() ",Digit.ComponentRegistryService.getComponent())

        console.log("Component abc Digit.ComponentRegistryService.getComponent(component) ",Digit.ComponentRegistryService.getComponent(component))

        const Component = typeof component === "string" ? Digit.ComponentRegistryService.getComponent(component) : component;
        console.log("Component abc ",Component)
       
        return (
          <Route path={`${match.path}/${routeObj.route}`} key={index}>
            <Component
              config={{ texts, inputs, key, isSkipEnabled }}
              onSelect={handleSelect}
              onSkip={handleSkip}
              t={t}
              formData={params}
            //   onAdd={handleMultiple}
              userType="citizen"
            />
          </Route>
        );
      })}
      <Route path={`${match.path}/check`}>
        <CheckPage 
        // onSubmit={createProperty}
        //  value={params}
          />
      </Route>
      {/* <Route path={`${match.path}/acknowledgement`}>
        <TLAcknowledgement data={params} onSuccess={onSuccess} onUpdateSuccess={onUpdateSuccess} />
      </Route> */}
      <Route>
        <Redirect to={`${match.path}/${config.indexRoute}`} />
      </Route>
    </Switch>
    
</div>)
}
export default WmsRAFBCreateTest