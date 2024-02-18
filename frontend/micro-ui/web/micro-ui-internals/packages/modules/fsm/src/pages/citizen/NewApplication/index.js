import React, { useEffect, useMemo, useState } from "react";
import { useTranslation } from "react-i18next";
import { Redirect, Route, BrowserRouter as Router, Switch, useHistory, useRouteMatch, useLocation } from "react-router-dom";
import { TypeSelectCard, Loader } from "@egovernments/digit-ui-react-components";
import { newConfig } from "../../../config/NewApplication/config";
import CheckPage from "./CheckPage";
import Response from "./Response";
import { useQueryClient } from "react-query";

const FileComplaint = ({ parentRoute }) => {
  const queryClient = useQueryClient();
  const match = useRouteMatch();
  const { t } = useTranslation();
  const { pathname } = useLocation();
  const history = useHistory();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateId = Digit.ULBService.getStateId();
  let config = [];
  let configs = []
  const [params, setParams, clearParams] = Digit.Hooks.useSessionStorage("FSM_CITIZEN_FILE_PROPERTY", {});
  const { data: commonFields, isLoading } = Digit.Hooks.fsm.useMDMS(stateId, "FSM", "CommonFieldsConfig");

  const [mutationHappened, setMutationHappened, clear] = Digit.Hooks.useSessionStorage("FSM_MUTATION_HAPPENED", false);
  const [errorInfo, setErrorInfo, clearError] = Digit.Hooks.useSessionStorage("FSM_ERROR_DATA", false);
  const [successData, setsuccessData, clearSuccessData] = Digit.Hooks.useSessionStorage("FSM_MUTATION_SUCCESS_DATA", false);

  useEffect(() => {
    if (!pathname?.includes('new-application/response')) {
      setMutationHappened(false);
      clearSuccessData();
      clearError();
    }
  }, []);

  const goNext = (skipStep) => {
    const currentPath = pathname.split("/").pop();
    const { nextStep } = configs.find((routeObj) => routeObj.route === currentPath);
    let redirectWithHistory = history.push;
    if (skipStep) {
      redirectWithHistory = history.replace;
    }
    if (nextStep === null) {
      return redirectWithHistory(`${parentRoute}/new-application/check`);
    }
    redirectWithHistory(`${match.path}/${nextStep}`);
  };

  const submitComplaint = async () => {
    history.push(`${parentRoute}/new-application/response`);
  };

  function handleSelect(key, data, skipStep) {
    setParams({ ...params, ...{ [key]: { ...params[key], ...data } }, ...{ source: "ONLINE" } });
    goNext(skipStep);
  }

  const handleSkip = () => { };

  const handleSUccess = () => {
    clearParams();
    queryClient.invalidateQueries("FSM_CITIZEN_SEARCH");
    setMutationHappened(true);
  };

  if (isLoading) {
    return <Loader />;
  }

  commonFields.forEach((obj) => {
    config = config.concat(obj.body.filter((a) => !a.hideInCitizen));
  });

  
let newConfig=[ 
        {
          "type": "component",
          "route": "search-property",
          "isMandatory": true,
          "component": "CPTSearchProperty", 
          "key": "cptsearchproperty",
          "withoutLabel": true,
          "nextStep": 'search-results',
          "hideInEmployee": true,
        },
        {
          "type": "component",
          "route": "search-results",
          "isMandatory": true,
          "component": "CPTSearchResults", 
          "key": "cptsearchresults",
          "withoutLabel": true,
          "nextStep": 'property-type',
          "hideInEmployee": true,
        },
        {
          "type": "component",
          "route": "create-property", 
          "isMandatory": true,
          "component": "CPTCreateProperty", 
          "key": "cptcreateproperty",
          "withoutLabel": true,
          "isSkipEnabled" : true,
          "nextStep": 'acknowledge-create-property',
          "hideInEmployee": true,
        },
        {
          "type": "component",
          "route": "acknowledge-create-property", 
          "isMandatory": true,
          "component": "CPTAcknowledgement", 
          "key": "cptacknowledgement",
          "withoutLabel": true,
          "nextStep": 'property-type',
          "hideInEmployee": true,
        },
        {
          "type": "component",
          "route": "property-details",
          "isMandatory": true,
          "component": "CPTPropertyDetails", 
          "key": "propertydetails",
          "withoutLabel": true,
          "nextStep": 'property-type',
          "hideInEmployee": true,
        },
        
        {
          "head": "FSM_NEW_APPLICATION_PROPERTY",
          "body": [
            {
              "component": "CPTPropertySearchNSummary",
              "withoutLabel": true,
              "key": "cpt",
              "type": "component",
              "hideInCitizen": true
            }
          ]
        },
      
  {
      "label": "ES_NEW_APPLICATION_PROPERTY_TYPEs",
      "isMandatory": true,
      "type": "component",
      "route": "property-type",
      "key": "propertyType",
      "component": "SelectPropertyType",
      "texts": {
          "headerCaption": "",
          "header": "CS_FILE_APPLICATION_PROPERTY_LABEL",
          "cardText": "CS_FILE_APPLICATION_PROPERTY_TEXT",
          "submitBarLabel": "CS_COMMON_NEXT"
      },
      "nextStep": "property-subtype"
  },
  {
      "label": "ES_NEW_APPLICATION_PROPERTY_SUB-TYPE",
      "isMandatory": true,
      "type": "component",
      "route": "property-subtype",
      "key": "subtype",
      "component": "SelectPropertySubtype",
      "texts": {
          "headerCaption": "",
          "header": "CS_FILE_APPLICATION_PROPERTY_SUBTYPE_LABEL",
          "cardText": "CS_FILE_APPLICATION_PROPERTY_SUBTYPE_TEXT",
          "submitBarLabel": "CS_COMMON_NEXT"
      },
      "nextStep": "map"
  },
  {
      "route": "map",
      "component": "FSMSelectGeolocation",
      "nextStep": "pincode",
      "hideInEmployee": true,
      "key": "address"
  },
  {
      "route": "pincode",
      "component": "FSMSelectPincode",
      "texts": {
          "headerCaption": "",
          "header": "CS_FILE_APPLICATION_PINCODE_LABEL",
          "cardText": "CS_FILE_APPLICATION_PINCODE_TEXT",
          "submitBarLabel": "CS_COMMON_NEXT",
          "skipText": "CORE_COMMON_SKIP_CONTINUE"
      },
      "withoutLabel": true,
      "key": "address",
      "nextStep": "address",
      "type": "component"
  },
  {
      "route": "address",
      "component": "FSMSelectAddress",
      "withoutLabel": true,
      "texts": {
          "headerCaption": "CS_FILE_APPLICATION_PROPERTY_LOCATION_LABEL",
          "header": "CS_FILE_APPLICATION_PROPERTY_LOCATION_ADDRESS_TEXT",
          "cardText": "CS_FILE_APPLICATION_PROPERTY_LOCATION_CITY_MOHALLA_TEXT",
          "submitBarLabel": "CS_COMMON_NEXT"
      },
      "key": "address",
      "nextStep": "check-slum",
      "isMandatory": true,
      "type": "component"
  },
  {
      "type": "component",
      "route": "check-slum",
      "isMandatory": true,
      "component": "CheckSlum",
      "texts": {
          "header": "ES_NEW_APPLICATION_SLUM_CHECK",
          "submitBarLabel": "CS_COMMON_NEXT"
      },
      "key": "address",
      "withoutLabel": true,
      "nextStep": "slum-details",
      "hideInEmployee": true
  },
  {
      "type": "component",
      "route": "slum-details",
      "isMandatory": true,
      "component": "SelectSlumName",
      "texts": {
          "header": "CS_NEW_APPLICATION_SLUM_NAME",
          "cardText": "CS_NEW_APPLICATION_SLUM_TEXT",
          "submitBarLabel": "CS_COMMON_NEXT"
      },
      "withoutLabel": true,
      "key": "address",
      "nextStep": "street"
  },
  {
      "type": "component",
      "route": "street",
      "component": "FSMSelectStreet",
      "key": "address",
      "withoutLabel": true,
      "texts": {
          "headerCaption": "CS_FILE_APPLICATION_PROPERTY_LOCATION_LABEL",
          "header": "CS_FILE_APPLICATION_PROPERTY_LOCATION_ADDRESS_TEXT",
          "cardText": "CS_FILE_APPLICATION_PROPERTY_LOCATION_STREET_DOOR_NO_LABEL",
          "submitBarLabel": "CS_COMMON_NEXT"
      },
      "nextStep": "landmark"
  },
  {
      "type": "component",
      "route": "landmark",
      "component": "FSMSelectLandmark",
      "withoutLabel": true,
      "texts": {
          "headerCaption": "CS_FILE_APPLICATION_PROPERTY_LOCATION_LABEL",
          "header": "CS_FILE_APPLICATION_PROPERTY_LOCATION_PROVIDE_LANDMARK_TITLE",
          "cardText": "CS_FILE_APPLICATION_PROPERTY_LOCATION_PROVIDE_LANDMARK_TEXT",
          "submitBarLabel": "CS_COMMON_NEXT",
          "skipText": "CORE_COMMON_SKIP_CONTINUE"
      },
      "key": "address",
      "nextStep": "pit-type"
  },
  {
      "label": "ES_NEW_APPLICATION_ROAD_WIDTH",
      "isMandatory": true,
      "type": "component",
      "route": "road-details",
      "key": "roadWidth",
      "hideInEmployee": true,
      "component": "SelectRoadDetails",
      "texts": {
          "header": "CS_FILE_PROPERTY_ROAD_WIDTH",
          "cardText": "CS_FILE_PROPERTY_ROAD_WIDTH_TEXT",
          "submitBarLabel": "CS_COMMON_NEXT"
          
      },
      "nextStep": "tank-size"
  },
  {
      "label": "ES_NEW_APPLICATION_PIT_TYPE",
      "isMandatory": true,
      "type": "component",
      "route": "pit-type",
      "key": "pitType",
      "component": "SelectPitType",
      "texts": {
          "header": "CS_FILE_PROPERTY_PIT_TYPE",
          "cardText": "CS_FILE_PROPERTY_PIT_TYPE_TEXT",
          "submitBarLabel": "CS_COMMON_NEXT"
      },
      "nextStep": "road-details"
  },
  {
      "route": "tank-size",
      "component": "SelectTankSize",
      "isMandatory": false,
      "texts": {
          "headerCaption": "",
          "header": "CS_FILE_APPLICATION_PIT_SEPTIC_TANK_SIZE_TITLE",
          "cardText": "CS_FILE_APPLICATION_PIT_SEPTIC_TANK_SIZE_TEXT",
          "submitBarLabel": "CS_COMMON_NEXT",
          "skipText": "CORE_COMMON_SKIP_CONTINUE"
      },
      "type": "component",
      "key": "pitDetail",
      "nextStep": "select-payment-preference",
      "label": "ES_NEW_APPLICATION_PIT_DIMENSION"
  },
  {
      "type": "component",
      "key": "tripData",
      "withoutLabel": true,
      "component": "SelectTrips"
  },
  {
      "label": "a",
      "isMandatory": true,
      "type": "component",
      "route": "select-trip-number",
      "key": "selectTripNo",
      "component": "SelectTripNo",
      "hideInEmployee": true,
      "texts": {
          "headerCaption": "",
          "header": "ES_FSM_SERVICE_REQUEST",
          "cardText": "ES_FSM_SERVICE_REQUEST_TEXT",
          "skipText": "CORE_COMMON_SKIP_CONTINUE",
          "submitBarLabel": "CS_COMMON_NEXT",
          "skipLabel": "CS_COMMON_SERVICE_SKIP_INFO"
      },
      "nextStep": "search-property"
  },
  {
      "label": "a",
      "isMandatory": false,
      "type": "component",
      "route": "select-gender",
      "hideInEmployee": true,
      "key": "selectGender",
      "component": "SelectGender",
      "texts": {
          "headerCaption": "",
          "header": "CS_COMMON_CHOOSE_GENDER",
          "cardText": "CS_COMMON_SELECT_GENDER",
          "submitBarLabel": "CS_COMMON_NEXT",
          "skipText": "CORE_COMMON_SKIP_CONTINUE"
      },
      "nextStep": "select-payment-preference"
  },
  {
      "label": "a",
      "isMandatory": false,
      "type": "component",
      "route": "select-payment-preference",
      "key": "selectPaymentPreference",
      "hideInEmployee": true,
      "component": "SelectPaymentPreference",
      "texts": {
          "headerCaption": "",
          "header": "ES_FSM_PAYMENT_PREFERENCE_LABEL",
          "cardText": "ES_FSM_PAYMENT_PREFERENCE_TEXT",
          "submitBarLabel": "CS_COMMON_NEXT",
          "skipText": "CORE_COMMON_SKIP_CONTINUE"
      },
      "nextStep": null
  },
  {
      "type": "component",
      "key": "tripData",
      "withoutLabel": true,
      "component": "SelectTripData"
  },
  {
      "type": "component",
      "key": "advancepaymentPreference",
      "withoutLabel": true,
      "component": "AdvanceCollection"
  }
]
configs = [...newConfig]
  configs.indexRoute = "select-trip-number";
console.log("newConfig",newConfig)
  return (
    <Switch>
      {configs.map((routeObj, index) => {
        const { component, texts, inputs, key } = routeObj;
        const Component = typeof component === "string" ? Digit.ComponentRegistryService.getComponent(component) : component;
        return (
          <Route path={`${match.path}/${routeObj.route}`} key={index}>
            <Component config={{ texts, inputs, key }} onSelect={handleSelect} onSkip={handleSkip} t={t} formData={params} />
          </Route>
        );
      })}
      <Route path={`${match.path}/check`}>
        <CheckPage onSubmit={submitComplaint} value={params} />
      </Route>
      <Route path={`${match.path}/response`}>
        <Response data={params} onSuccess={handleSUccess} />
      </Route>
      <Route>
        <Redirect to={`${match.path}/${configs.indexRoute}`} />
      </Route>
    </Switch>
  );
};

export default FileComplaint;
