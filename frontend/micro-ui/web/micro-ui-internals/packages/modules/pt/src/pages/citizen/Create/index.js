import { Loader,Modal ,Card , CardHeader, StatusTable,Row} from "@upyog/digit-ui-react-components";
import React ,{Fragment,useState,useEffect}from "react";
import { useTranslation } from "react-i18next";
import { useQueryClient } from "react-query";
import { Redirect, Route, Switch, useHistory, useLocation, useRouteMatch } from "react-router-dom";
import { newConfig } from "../../../config/Create/config";

const CreateProperty = ({ parentRoute }) => {
  const queryClient = useQueryClient();
  const match = useRouteMatch();
  const { t } = useTranslation();
  const { pathname } = useLocation();
  const [showToast, setShowToast] = useState(null);
  const history = useHistory();
  const stateId = Digit.ULBService.getStateId();
  let config = [];
  const [params, setParams, clearParams] = Digit.Hooks.useSessionStorage("PT_CREATE_PROPERTY", {});
  let { data: commonFields, isLoading } = Digit.Hooks.pt.useMDMS(stateId, "PropertyTax", "CommonFieldsConfig");
  const [searchData, setSearchData] = useState({});
  const { data: propertyData, isLoading: propertyDataLoading, error, isSuccess, billData } = Digit.Hooks.pt.usePropertySearchWithDue({
    tenantId: searchData?.city,
    filters: searchData?.filters,
    auth: true /*  to enable open search set false  */,
    configs: { enabled: Object.keys(searchData).length > 0, retry: false, retryOnMount: false, staleTime: Infinity },
  });

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
      nextPage = isMultiple && nextStep !== "map" ? `${match.path}/${nextStep}/${index}` : `${match.path}/${nextStep}`;
    }

    redirectWithHistory(nextPage);
  };

  if(params && Object.keys(params).length>0 && window.location.href.includes("/info") && sessionStorage.getItem("docReqScreenByBack") !== "true")
    {
      clearParams();
      queryClient.invalidateQueries("PT_CREATE_PROPERTY");
    }

  const createProperty = async () => {
    let tempObject={
      "mobileNumber":params.owners[0].mobileNumber,
      "name":params.owners[0].name,
      "doorNo": params.address.doorNo,
      "locality": params.address.locality.code,
      "isRequestForDuplicatePropertyValidation":true
    }
    setSearchData({ city: params.address.city.code, filters: tempObject });    
    //history.push(`${match.path}/acknowledgement`);
  };
  useEffect(() => {  
    if(propertyDataLoading && propertyData?.Properties.length >0)  
    {  
      //alert("property exist"),  
      setShowToast(true) 
    }  
    else if(propertyDataLoading && propertyData?.Properties.length === 0) {  
      setShowToast(false)  
      console.log("propertyDatapropertyData",propertyData)
      history.push(`${match.path}/acknowledgement`);  
    }  
    }, [propertyData]);

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
    }
    else if(key === "propertyStructureDetails")
    {
     
      let propertyStructureDetail = params.propertyStructureDetails || {};
      // if(index){units[index] = data;}else{
        propertyStructureDetail = data;
let propertyStructureDetails ={"propertyStructureDetails":propertyStructureDetail}
      setParams({ ...params, ...propertyStructureDetails });

    } else {
      setParams({ ...params, ...{ [key]: { ...params[key], ...data } } });
    }
    goNext(skipStep, index, isAddMultiple, key);
  }
  const Heading = (props) => {
    return <h1 className="heading-m">{props.label}</h1>;
  };

  const Close = () => (
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="#FFFFFF">
      <path d="M0 0h24v24H0V0z" fill="none" />
      <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12 19 6.41z" />
    </svg>
  );

  const CloseBtn = (props) => {
    return (
      <div className="icon-bg-secondary" onClick={props.onClick}>
        <Close />
      </div>
    );
  };

  const handleSkip = () => {};
  const handleMultiple = () => {};

  const onSuccess = () => {
    clearParams();
    queryClient.invalidateQueries("PT_CREATE_PROPERTY");
  };
  if (isLoading) {
    return <Loader />;
  }

  const closeModal =() =>{
    setShowToast(false)
  }
  const setModal=()=>{
    setShowToast(false)   
    history.push(`${match.path}/acknowledgement`) 
  }
  // commonFields=newConfig;
  /* use newConfig instead of commonFields for local development in case needed */
  commonFields = newConfig;
  commonFields.forEach((obj) => {
    config = config.concat(obj.body.filter((a) => !a.hideInCitizen));
  });
//   let conf = [
//     {
//         "route": "map",
//         "component": "PTSelectGeolocation",
//         "nextStep": "pincode",
//         "hideInEmployee": true,
//         "key": "address",
//         "texts": {
//             "header": "PT_GEOLOCATON_HEADER",
//             "cardText": "PT_GEOLOCATION_TEXT",
//             "nextText": "PT_COMMON_NEXT",
//             "skipAndContinueText": "CORE_COMMON_SKIP_CONTINUE"
//         }
//     },
//     {
//         "route": "pincode",
//         "component": "PTSelectPincode",
//         "texts": {
//             "headerCaption": "PT_PROPERTY_LOCATION_CAPTION",
//             "header": "PT_PINCODE_LABEL",
//             "cardText": "PT_PINCODE_TEXT",
//             "submitBarLabel": "PT_COMMON_NEXT",
//             "skipText": "CORE_COMMON_SKIP_CONTINUE"
//         },
//         "withoutLabel": true,
//         "key": "address",
//         "nextStep": "address",
//         "type": "component"
//     },
//     {
//         "route": "address",
//         "component": "PTSelectAddress",
//         "withoutLabel": true,
//         "texts": {
//             "headerCaption": "PT_PROPERTY_LOCATION_CAPTION",
//             "header": "CS_FILE_APPLICATION_PROPERTY_LOCATION_ADDRESS_TEXT",
//             "cardText": "CS_FILE_APPLICATION_PROPERTY_LOCATION_CITY_MOHALLA_TEXT",
//             "submitBarLabel": "PT_COMMON_NEXT"
//         },
//         "key": "address",
//         "nextStep": "street",
//         "isMandatory": true,
//         "type": "component"
//     },
//     {
//         "type": "component",
//         "route": "street",
//         "component": "PTSelectStreet",
//         "key": "address",
//         "withoutLabel": true,
//         "texts": {
//             "headerCaption": "PT_PROPERTY_LOCATION_CAPTION",
//             "header": "CS_FILE_APPLICATION_PROPERTY_LOCATION_ADDRESS_TEXT",
//             "cardText": "PT_STREET_TEXT",
//             "submitBarLabel": "PT_COMMON_NEXT"
//         },
//         "nextStep": "landmark"
//     },
//     {
//         "type": "component",
//         "route": "landmark",
//         "component": "PTSelectLandmark",
//         "withoutLabel": true,
//         "texts": {
//             "headerCaption": "PT_PROPERTY_LOCATION_CAPTION",
//             "header": "CS_FILE_APPLICATION_PROPERTY_LOCATION_PROVIDE_LANDMARK_TITLE",
//             "cardText": "CS_FILE_APPLICATION_PROPERTY_LOCATION_PROVIDE_LANDMARK_TEXT",
//             "submitBarLabel": "PT_COMMON_NEXT",
//             "skipText": "CORE_COMMON_SKIP_CONTINUE"
//         },
//         "key": "address",
//         "nextStep": "proof",
//         "hideInEmployee": true
//     },
//     {
//         "type": "component",
//         "route": "proof",
//         "component": "Proof",
//         "withoutLabel": true,
//         "texts": {
//             "headerCaption": "PT_PROPERTY_LOCATION_CAPTION",
//             "header": "PT_PROOF_OF_ADDRESS_HEADER",
//             "cardText": "",
//             "nextText": "PT_COMMONS_NEXT",
//             "submitBarLabel": "PT_COMMONS_NEXT"
//         },
//         "key": "address",
//         "nextStep": "owner-ship-details@0",
//         "hideInEmployee": true
//     },
//     {
//         "route": "info",
//         "component": "PropertyTax",
//         "nextStep": "property-type",
//         "hideInEmployee": true,
//         "key": "Documents"
//     },
//     {
//         "type": "component",
//         "route": "isResidential",
//         "isMandatory": true,
//         "component": "IsResidential",
//         "texts": {
//             "headerCaption": "",
//             "header": "PT_PROPERTY_DETAILS_RESIDENTIAL_PROPERTY_HEADER",
//             "cardText": "PT_PROPERTY_DETAILS_RESIDENTIAL_PROPERTY_TEXT",
//             "submitBarLabel": "PT_COMMON_NEXT"
//         },
//         "key": "isResdential",
//         "withoutLabel": true,
//         "hideInEmployee": true,
//         "nextStep": {
//             "PT_COMMON_YES": "property-type",
//             "PT_COMMON_NO": "property-usage-type"
//         }
//     },
//     {
//         "type": "component",
//         "route": "property-usage-type",
//         "isMandatory": true,
//         "component": "PropertyUsageType",
//         "texts": {
//             "headerCaption": "PT_ASSESMENT_INFO_USAGE_TYPE",
//             "header": "PT_PROPERTY_DETAILS_USAGE_TYPE_HEADER",
//             "cardText": "PT_PROPERTY_DETAILS_USAGE_TYPE_TEXT",
//             "submitBarLabel": "PT_COMMONS_NEXT"
//         },
//         "nextStep": "property-type",
//         "key": "usageCategoryMajor",
//         "withoutLabel": true
//     },
//     {
//         "type": "component",
//         "isMandatory": true,
//         "component": "ProvideSubUsageType",
//         "key": "usageCategoryMinor",
//         "withoutLabel": true
//     },
//     {
//         "type": "component",
//         "route": "provide-sub-usage-type",
//         "isMandatory": true,
//         "component": "ProvideSubUsageType",
//         "texts": {
//             "headerCaption": "PT_ASSESMENT_INFO_USAGE_TYPE",
//             "header": "PT_ASSESSMENT_FLOW_SUBUSAGE_HEADER",
//             "cardText": "PT_ASSESSMENT_FLOW_SUBUSAGE_TEXT",
//             "submitBarLabel": "PT_COMMON_NEXT"
//         },
//         "key": "units",
//         "withoutLabel": true,
//         "nextStep": {
//             "yes": "is-any-part-of-this-floor-unoccupied",
//             "no": "provide-sub-usage-type-of-rented-area"
//         },
//         "hideInEmployee": true
//     },
//     {
//         "type": "component",
//         "route": "property-type",
//         "isMandatory": true,
//         "component": "PropertyType",
//         "key": "PropertyType",
//         "texts": {
//             "headerCaption": "",
//             "header": "PT_ASSESMENT1_PROPERTY_TYPE",
//             "cardText": "",
//             "submitBarLabel": "PT_COMMONS_NEXT"
//         },
//         "nextStep": "electricity-number",
//         "withoutLabel": true
//     },
//     {
//         "type": "component",
//         "route": "electricity-number",
//         "isMandatory": true,
//         "component": "Electricity",
//         "key": "electricity",
//         "withoutLabel": true,
//         "texts": {
//             "headerCaption": "",
//             "header": "PT_ASSESMENT1_ELECTRICITY_NUMBER",
//             "cardText": "",
//             "submitBarLabel": "PT_COMMONS_NEXT"
//         },
//         "nextStep": "PropertyStructureDetails"
//     },
//     {
//       "type": "component",
//       "route": "PropertyStructureDetails",
//       "isMandatory": true,
//       "component": "PropertyStructureDetails",
//       "key": "propertyStructureDetails",
//       "withoutLabel": true,
//       "texts": {
//           "headerCaption": "",
//           "header": "PT_STRUCTURE_DETAILS",
//           "cardText": "",
//           "submitBarLabel": "PT_COMMONS_NEXT"
//       },
//       "nextStep": ""
//   },
//     {
//         "type": "component",
//         "route": "uid",
//         "isMandatory": true,
//         "component": "UID",
//         "texts": {
//             "headerCaption": "",
//             "header": "PT_ASSESMENT1_ELECTRICITY_UID_NUMBER",
//             "cardText": "",
//             "submitBarLabel": "PT_COMMONS_NEXT"
//         },
//         "nextStep": {
//             "COMMON_PROPTYPE_BUILTUP_INDEPENDENTPROPERTY": "landarea",
//             "COMMON_PROPTYPE_BUILTUP_SHAREDPROPERTY": "PtUnits",
//             "COMMON_PROPTYPE_VACANT": "area"
//         },
//         "key": "uid",
//         "withoutLabel": true
//     },

//     {
//         "type": "component",
//         "isMandatory": true,
//         "component": "Area",
//         "key": "landarea",
//         "withoutLabel": true
//     },
//     {
//         "type": "component",
//         "route": "PtUnits",
//         "isMandatory": true,
//         "component": "SelectPTUnits",
//         "texts": {
//             "headerCaption": "",
//             "header": "PT_FLAT_DETAILS",
//             "cardText": "PT_FLAT_DETAILS_DESC",
//             "submitBarLabel": "PT_COMMON_NEXT"
//         },
//         "key": "units",
//         "withoutLabel": true,
//         "nextStep": "map",
//         "hideInEmployee": true
//     },
//     {
//         "type": "component",
//         "route": "landarea",
//         "isMandatory": true,
//         "component": "PTLandArea",
//         "texts": {
//             "headerCaption": "",
//             "header": "PT_PLOT_SIZE_HEADER",
//             "cardText": "",
//             "submitBarLabel": "PT_COMMON_NEXT"
//         },
//         "key": "units",
//         "withoutLabel": true,
//         "nextStep": "number-of-floors",
//         "hideInEmployee": true
//     },
//     {
//         "type": "component",
//         "route": "area",
//         "isMandatory": true,
//         "component": "Area",
//         "texts": {
//             "headerCaption": "",
//             "header": "PT_PLOT_SIZE_HEADER",
//             "cardText": "PT_FORM2_PLOT_SIZE_PLACEHOLDER",
//             "submitBarLabel": "PT_COMMON_NEXT"
//         },
//         "key": "units",
//         "withoutLabel": true,
//         "nextStep": "map",
//         "hideInEmployee": true
//     },
//     {
//         "type": "component",
//         "route": "number-of-floors",
//         "isMandatory": true,
//         "component": "PropertyBasementDetails",
//         "texts": {
//             "headerCaption": "",
//             "header": "PT_PROPERTY_DETAILS_NO_OF_BASEMENTS_HEADER",
//             "cardText": "",
//             "submitBarLabel": "PT_COMMONS_NEXT"
//         },
//         "nextStep": "number-of-basements@0",
//         "key": "noOofBasements",
//         "withoutLabel": true
//     },
//     {
//         "type": "component",
//         "component": "Units",
//         "key": "units",
//         "withoutLabel": true
//     },
//     {
//         "type": "component",
//         "route": "provide-floor-no",
//         "isMandatory": true,
//         "component": "ProvideFloorNo",
//         "texts": {
//             "headerCaption": "",
//             "header": "PT_FLOOR_NUMBER_HEADER",
//             "cardText": "",
//             "submitBarLabel": "PT_COMMONS_NEXT"
//         },
//         "nextStep": "units",
//         "key": "Floorno",
//         "withoutLabel": true,
//         "hideInEmployee": true
//     },
//     {
//         "type": "component",
//         "route": "is-this-floor-self-occupied",
//         "isMandatory": true,
//         "component": "IsThisFloorSelfOccupied",
//         "texts": {
//             "headerCaption": "",
//             "header": "PT_ASSESSMENT_FLOW_FLOOR_OCC_HEADER",
//             "cardText": "PT_ASSESSMENT_FLOW_FLOOR_OCC_TEXT",
//             "submitBarLabel": "PT_COMMON_NEXT"
//         },
//         "key": "units",
//         "withoutLabel": true,
//         "nextStep": {
//             "PT_YES_IT_IS_SELFOCCUPIED": "provide-sub-usage-type",
//             "PT_YES_IT_IS_SELFOCCUPIED1": "is-any-part-of-this-floor-unoccupied",
//             "PT_PARTIALLY_RENTED_OUT": "area",
//             "PT_PARTIALLY_RENTED_OUT1": "area",
//             "PT_FULLY_RENTED_OUT": "provide-sub-usage-type-of-rented-area",
//             "PT_FULLY_RENTED_OUT1": "rental-details"
//         },
//         "hideInEmployee": true
//     },
//     {
//         "type": "component",
//         "route": "number-of-basements@0",
//         "isMandatory": true,
//         "component": "PropertyFloorDetails",
//         "texts": {
//             "headerCaption": "",
//             "header": "BPA_SCRUTINY_DETAILS_NUMBER_OF_FLOORS_LABEL",
//             "cardText": "PT_PROPERTY_DETAILS_NO_OF_FLOORS_TEXT",
//             "submitBarLabel": "PT_COMMONS_NEXT"
//         },
//         "nextStep": {
//             "PT_NO_BASEMENT_OPTION": "units",
//             "PT_ONE_BASEMENT_OPTION": "units",
//             "PT_TWO_BASEMENT_OPTION": "units"
//         },
//         "key": "noOfFloors",
//         "withoutLabel": true,
//         "hideInEmployee": true
//     },
//     {
//         "type": "component",
//         "route": "units",
//         "isMandatory": true,
//         "component": "SelectPTUnits",
//         "texts": {
//             "headerCaption": "",
//             "header": "PT_FLAT_DETAILS",
//             "cardText": "PT_FLAT_DETAILS_DESC",
//             "submitBarLabel": "PT_COMMON_NEXT"
//         },
//         "nextStep": "map",
//         "key": "units",
//         "withoutLabel": true,
//         "hideInEmployee": true
//     },
//     {
//         "type": "component",
//         "route": "rental-details",
//         "isMandatory": true,
//         "component": "RentalDetails",
//         "texts": {
//             "header": "PT_ASSESSMENT_FLOW_RENTAL_DETAIL_HEADER",
//             "cardText": "",
//             "submitBarLabel": "PT_COMMON_NEXT"
//         },
//         "key": "units",
//         "withoutLabel": true,
//         "nextStep": "is-any-part-of-this-floor-unoccupied",
//         "hideInEmployee": true
//     },
//     {
//         "type": "component",
//         "route": "provide-sub-usage-type-of-rented-area",
//         "isMandatory": true,
//         "component": "ProvideSubUsageTypeOfRentedArea",
//         "texts": {
//             "headerCaption": "PT_ASSESMENT_INFO_USAGE_TYPE",
//             "header": "PT_ASSESSMENT_FLOW_RENT_SUB_USAGE_HEADER",
//             "cardText": "PT_ASSESSMENT_FLOW_SUBUSAGE_TEXT",
//             "submitBarLabel": "PT_COMMON_NEXT"
//         },
//         "key": "units",
//         "withoutLabel": true,
//         "nextStep": "rental-details",
//         "hideInEmployee": true
//     },
//     {
//         "type": "component",
//         "route": "is-any-part-of-this-floor-unoccupied",
//         "isMandatory": true,
//         "component": "IsAnyPartOfThisFloorUnOccupied",
//         "texts": {
//             "header": "PT_ASSESSMENT_FLOW_ISUNOCCUPIED_HEADER",
//             "cardText": "PT_ASSESSMENT_FLOW_ISUNOCCUPIED_TEXT",
//             "submitBarLabel": "PT_COMMON_NEXT"
//         },
//         "key": "units",
//         "withoutLabel": true,
//         "nextStep": {
//             "PT_COMMON_NO": "map",
//             "PT_COMMON_YES": "un-occupied-area"
//         },
//         "hideInEmployee": true
//     },
//     {
//         "type": "component",
//         "route": "un-occupied-area",
//         "isMandatory": true,
//         "component": "UnOccupiedArea",
//         "texts": {
//             "header": "PT_ASSESSMENT_FLOW_UNOCCUPIED_AREA_HEADER",
//             "cardText": "PT_ASSESSMENT_FLOW_UNOCCUPIED_AREA_TEXT",
//             "submitBarLabel": "PT_COMMON_NEXT",
//             "skipText": ""
//         },
//         "key": "units",
//         "withoutLabel": true,
//         "nextStep": "map",
//         "hideInEmployee": true
//     },
//     {
//         "type": "component",
//         "route": "owner-ship-details@0",
//         "isMandatory": true,
//         "component": "SelectOwnerShipDetails",
//         "texts": {
//             "headerCaption": "PT_PROPERTIES_OWNERSHIP",
//             "header": "PT_PROVIDE_OWNERSHIP_DETAILS",
//             "cardText": "PT_PROVIDE_OWNERSHI_DETAILS_SUB_TEXT",
//             "submitBarLabel": "PT_COMMON_NEXT"
//         },
//         "key": "ownershipCategory",
//         "withoutLabel": true,
//         "nextStep": {
//             "INSTITUTIONALPRIVATE": "inistitution-details",
//             "INSTITUTIONALGOVERNMENT": "inistitution-details",
//             "INDIVIDUAL.SINGLEOWNER": "owner-details",
//             "INDIVIDUAL.MULTIPLEOWNERS": "owner-details"
//         }
//     },
//     {
//         "isMandatory": true,
//         "type": "component",
//         "route": "owner-details",
//         "key": "owners",
//         "component": "SelectOwnerDetails",
//         "texts": {
//             "headerCaption": "",
//             "header": "PT_OWNERSHIP_INFO_SUB_HEADER",
//             "cardText": "PT_FORM3_HEADER_MESSAGE",
//             "submitBarLabel": "PT_COMMON_NEXT"
//         },
//         "withoutLabel": true,
//         "nextStep": "special-owner-category",
//         "hideInEmployee": true
//     },
//     {
//         "type": "component",
//         "route": "special-owner-category",
//         "isMandatory": true,
//         "component": "SelectSpecialOwnerCategoryType",
//         "texts": {
//             "headerCaption": "PT_OWNERS_DETAILS",
//             "header": "PT_SPECIAL_OWNER_CATEGORY",
//             "cardText": "PT_FORM3_HEADER_MESSAGE",
//             "submitBarLabel": "PT_COMMON_NEXT"
//         },
//         "key": "owners",
//         "withoutLabel": true,
//         "nextStep": "owner-address",
//         "hideInEmployee": true
//     },
//     {
//         "type": "component",
//         "route": "owner-address",
//         "isMandatory": true,
//         "component": "SelectOwnerAddress",
//         "texts": {
//             "headerCaption": "PT_OWNERS_DETAILS",
//             "header": "PT_OWNERS_ADDRESS",
//             "cardText": "",
//             "submitBarLabel": "PT_COMMON_NEXT"
//         },
//         "key": "owners",
//         "withoutLabel": true,
//         "nextStep": "special-owner-category-proof",
//         "hideInEmployee": true
//     },
//     {
//         "type": "component",
//         "component": "SelectAltContactNumber",
//         "key": "owners",
//         "withoutLabel": true,
//         "hideInEmployee": true
//     },
//     {
//         "type": "component",
//         "route": "special-owner-category-proof",
//         "isMandatory": true,
//         "component": "SelectSpecialProofIdentity",
//         "texts": {
//             "headerCaption": "PT_OWNERS_DETAILS",
//             "header": "PT_SPECIAL_OWNER_CATEGORY_PROOF_HEADER",
//             "cardText": "",
//             "submitBarLabel": "PT_COMMON_NEXT"
//         },
//         "key": "owners",
//         "withoutLabel": true,
//         "nextStep": "proof-of-identity",
//         "hideInEmployee": true
//     },
//     {
//         "type": "component",
//         "route": "proof-of-identity",
//         "isMandatory": true,
//         "component": "SelectProofIdentity",
//         "texts": {
//             "headerCaption": "PT_DOCUMENT_DETAILS",
//             "header": "PT_PROOF_IDENTITY_HEADER",
//             "cardText": "",
//             "submitBarLabel": "PT_COMMON_NEXT",
//             "addMultipleText": "PT_COMMON_ADD_APPLICANT_LABEL"
//         },
//         "key": "owners",
//         "withoutLabel": true,
//         "nextStep": null,
//         "hideInEmployee": true
//     },
//     {
//         "type": "component",
//         "route": "inistitution-details",
//         "isMandatory": true,
//         "component": "SelectInistitutionOwnerDetails",
//         "texts": {
//             "headerCaption": "",
//             "header": "PT_INSTITUTION_DETAILS_HEADER",
//             "cardText": "PT_FORM3_HEADER_MESSAGE",
//             "submitBarLabel": "PT_COMMON_NEXT"
//         },
//         "key": "owners",
//         "withoutLabel": true,
//         "nextStep": "institutional-owner-address",
//         "hideInEmployee": true
//     },
//     {
//         "type": "component",
//         "route": "institutional-owner-address",
//         "isMandatory": true,
//         "component": "SelectOwnerAddress",
//         "texts": {
//             "headerCaption": "PT_OWNERS_DETAILS",
//             "header": "PT_OWNERS_ADDRESS",
//             "cardText": "",
//             "submitBarLabel": "PT_COMMON_NEXT"
//         },
//         "key": "owners",
//         "withoutLabel": true,
//         "nextStep": "institutional-proof-of-identity",
//         "hideInEmployee": true
//     },
//     {
//         "type": "component",
//         "route": "institutional-proof-of-identity",
//         "isMandatory": true,
//         "component": "SelectProofIdentity",
//         "texts": {
//             "headerCaption": "PT_OWNERS_DETAILS",
//             "header": "PT_PROOF_IDENTITY_HEADER",
//             "cardText": "",
//             "submitBarLabel": "PT_COMMON_NEXT"
//         },
//         "key": "owners",
//         "withoutLabel": true,
//         "nextStep": null,
//         "hideInEmployee": true
//     },
//     {
//         "component": "SelectDocuments",
//         "withoutLabel": true,
//         "key": "documents",
//         "type": "component"
//     }
// ]
config.indexRoute = "info";
 // console.log("configconfigconfig",config)

  const CheckPage = Digit?.ComponentRegistryService?.getComponent("PTCheckPage");
  const PTAcknowledgement = Digit?.ComponentRegistryService?.getComponent("PTAcknowledgement");
  return (
    <div>
      <div>
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
    <div>
      { showToast &&   <Modal
      headerBarMain={<Heading label={t("CR_PROPERTY_DUPLICATE")} />}
      headerBarEnd={<CloseBtn onClick={closeModal} />}
      actionCancelLabel={"Cancel"}
      actionCancelOnSubmit={closeModal}
      actionSaveLabel={"Proceed"}
      actionSaveOnSubmit={setModal}
      formId="modal-action"
    >  <div style={{ width: "100%" }}>
    <Card>
        <CardHeader>Property Details</CardHeader>
     
            <StatusTable>
                <Row label={t("CR_PROPERTY_NUMBER")} text={propertyData?.Properties?.[0]?.propertyId || "NA"} textStyle={{ whiteSpace: "pre" }} />
                <Row label={t("CR_OWNER_NAME")} text={propertyData?.Properties?.[0]?.owners?.[0].name || "NA"} />
                <Row label={t("CR_MOBILE_NUMBER")} text={propertyData?.Properties?.[0]?.owners?.[0].mobileNumber|| "NA"} />
                <Row label={t("CR_ADDRESS")}    text={( propertyData?.Properties?.[0]?.address?.doorNo +", "+ propertyData?.Properties?.[0]?.address?.locality?.name +", "+ propertyData?.Properties?.[0]?.address?.city ) || "NA"}/>
            </StatusTable>
    </Card>
</div>
      </Modal>}
    </div>
    </div>
  );
};

export default CreateProperty;
