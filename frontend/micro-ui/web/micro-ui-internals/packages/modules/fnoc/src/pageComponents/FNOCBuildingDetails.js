// import {
//   getBreak,
//   getCommonCard,
//   getCommonContainer,
//   getCommonGrayCard,
//   getCommonTitle,
//   getSelectField,
//   getTextField,
//   getPattern
// } from "egov-ui-framework/ui-config/screens/specs/utils";
// import {
//   prepareFinalObject,
//   handleScreenConfigurationFieldChange as handleField
// } from "egov-ui-framework/ui-redux/screen-configuration/actions";
// import get from "lodash/get";
// import set from "lodash/set";
// import "./index.css";
// import { getObjectValues } from 'egov-ui-framework/ui-utils/commons';

// export const triggerUpdateByKey = (state, keyIndex, value, dispatch) => {
//   if(dispatch == "set"){
//     set(state, `screenConfiguration.preparedFinalObject.DynamicMdms.firenoc.buildings.selectedValues[${keyIndex}]`, value);
//   } else {
//     dispatch(prepareFinalObject( `DynamicMdms.firenoc.buildings.${keyIndex}`, value ));
//   }
// }

// let previousUoms = [];

// const dynamic = (uom, path, buildingIndex) => {
//   return {
//     ...getTextField({
//       label: {
//         labelKey: `NOC_PROPERTY_DETAILS_${uom}_LABEL`
//       },
//       placeholder: {
//         labelKey: `NOC_PROPERTY_DETAILS_${uom}_PLACEHOLDER`
//       },
//       pattern: /^[0-9]*$/i,
//       jsonPath: `FireNOCs[0].fireNOCDetails.buildings[${buildingIndex}].uomsMap.${uom}`,
//       required: true,
//       props: { type: "number", className:"applicant-details-error" },
//       gridDefination: {
//         xs: 12,
//         sm: 12,
//         md: 6
//       }
//     }),
//     componentJsonpath: `${path}.${uom}`
//   };
// };

// const prepareSelectField = (uom, start, end) => {
//   let data = [];
//   for (let i = start; i <= end; i++) {
//     data.push({ code: `${i}`, name: `${i}`});
//   }
//   return {
//     uiFramework: "custom-containers-local",
//     moduleName: "egov-firenoc",
//     componentPath: "AutosuggestContainer",
//     props: {
//       label: {
//         labelKey: `NOC_PROPERTY_DETAILS_${uom}_LABEL`
//       },
//       placeholder: {
//         labelKey: `NOC_PROPERTY_DETAILS_${uom}_PLACEHOLDER`
//       },
//       required: true,
//       isClearable: true,
//       defaultSort:uom=="NO_OF_FLOORS"?false:true,
//       data: data,
//       className:"applicant-details-error autocomplete-dropdown",
//       jsonPath: `FireNOCs[0].fireNOCDetails.buildings[0].uomsMap.${uom}`,
//     },
//     required: true,
//     jsonPath: `FireNOCs[0].fireNOCDetails.buildings[0].uomsMap.${uom}`,
//     gridDefination: {
//       xs: 12,
//       sm: 12,
//       md: 6
//     },
//   };
// };

// const prepareTextField = uom => {
//   return {
//     ...getTextField({
//       label: {
//         labelKey: `NOC_PROPERTY_DETAILS_${uom}_LABEL`
//       },
//       placeholder: {
//         labelKey: `NOC_PROPERTY_DETAILS_${uom}_PLACEHOLDER`
//       },
//       pattern: /^\d{0,10}$/i,
      
//     //   onInput:(e)=>{ 
//     //     e.target.value = Math.max(0, parseInt(e.target.value) ).toString().slice(0,2)
//     // },
//       errorMessage: "ERR_DEFAULT_INPUT_FIELD_MSG",
//       // required: true,
//       jsonPath: `FireNOCs[0].fireNOCDetails.buildings[0].uomsMap.${uom}`,
//       gridDefination: {
//         xs: 12,
//         sm: 12,
//         md: 6
//       },
//       props:{
//         className:"applicant-details-error"
//       }
//     })
//   };
// };

// const checkUomIsDefault = uom => {
//   if (
//     [
//       "NO_OF_FLOORS",
//       "NO_OF_BASEMENTS",
//       "PLOT_SIZE",
//       "BUILTUP_AREA",
//       "HEIGHT_OF_BUILDING"
//     ].indexOf(uom) >= 0
//   ) {
//     return true;
//   }
//   return false;
// };

// const setMandatory = (dispatch, path, value) => {
//   dispatch(handleField("apply", path, "required", value));
//   dispatch(handleField("apply", path, "props.required", value));
// };

// export const updateUsageType = async ( state, dispatch ) => {  
  
//   let buildings = get( state, "screenConfiguration.preparedFinalObject.FireNOCs[0].fireNOCDetails.buildings", []);

//   for (let i = 0; i < buildings.length; i++) {
//       const subUsageType = get( buildings[i], "usageType", '')||'';
//       const usageType = subUsageType && subUsageType.split('.')[0];
//       let formObj = { buildingUsageType: usageType, buildingSubUsageType: subUsageType }
//       triggerUpdateByKey(state, i, formObj, 'set');
//       triggerUpdateByKey(state, `buildingSubUsageTypeTransformed.allDropdown[${i}]`, getObjectValues(get( state, `screenConfiguration.preparedFinalObject.DynamicMdms.firenoc.buildings.buildingsTransformed.${usageType}`, [])) , dispatch);    
//       triggerUpdateByKey(state, `selectedValues[${i}]`, formObj , dispatch);


//       const cardType = get(state, "screenConfiguration.preparedFinalObject.FireNOCs[0].fireNOCDetails.noOfBuildings", null);
//       let path = "";
//       if(cardType === "SINGLE") {
//         path = "components.div.children.formwizardSecondStep.children.propertyDetails.children.cardContent.children.propertyDetailsConatiner.children.buildingDataCard.children.singleBuildingContainer.children.singleBuilding.children.cardContent.children.singleBuildingCard.children";
//       } else {
//         path = `components.div.children.formwizardSecondStep.children.propertyDetails.children.cardContent.children.propertyDetailsConatiner.children.buildingDataCard.children.multipleBuildingContainer.children.multipleBuilding.props.items[${i}].item${i}.children.cardContent.children.multipleBuildingCard.children`
//       }
//     // Get the list of uom for selected building subtype
//     let uomsList = get(
//       state,
//       "screenConfiguration.preparedFinalObject.applyScreenMdmsData.firenoc.BuildingType",
//       []
//     ).filter(item => {
//       return item.code === subUsageType;
//     });
//     let uoms = get(uomsList, "[0].uom", []);
    
//     // Remove previous dynamic uoms
//     previousUoms.forEach(uom => {
//       !checkUomIsDefault(uom) &&
//         dispatch(handleField("apply", `${path}.${uom}`, "visible", false));
//     });
    
//     // Set required fields defaults
//     setMandatory(dispatch, `${path}.PLOT_SIZE`, false);
//     setMandatory(dispatch, `${path}.BUILTUP_AREA`, false);
//     setMandatory(dispatch, `${path}.HEIGHT_OF_BUILDING`, false);
    
//     // Dynamically create UOM's based on building subtype selection
//     uoms.forEach(uom => {
//       if (checkUomIsDefault(uom)) {
//         setMandatory(dispatch, `${path}.${uom}`, true);
//       } else {
//         dispatch(
//           handleField("apply", path, uom, dynamic(uom, path, i))
//         );
//       }
//     });
    
//     // Set previous uoms array
//     previousUoms = uoms;
//     }
// }

// const buildingUsageTypeChange = (reqObj) => {
//   const {dispatch, state, value, index} = reqObj;
//   dispatch(prepareFinalObject(`FireNOCs[0].fireNOCDetails.buildings[${index}].usageTypeMajor`, value ? value : "none"));
// }

// const buildingSubUsageTypeChange = (reqObj) => {
//   const {dispatch, state, value, index} = reqObj;
//   dispatch(prepareFinalObject(`FireNOCs[0].fireNOCDetails.buildings[${index}].usageType`, value));
//   const cardType = get(state, "screenConfiguration.preparedFinalObject.FireNOCs[0].fireNOCDetails.noOfBuildings", null);
//   let path = "";
//   if(cardType === "SINGLE") {
//     path = "components.div.children.formwizardSecondStep.children.propertyDetails.children.cardContent.children.propertyDetailsConatiner.children.buildingDataCard.children.singleBuildingContainer.children.singleBuilding.children.cardContent.children.singleBuildingCard.children";
//   } else {
//     path = `components.div.children.formwizardSecondStep.children.propertyDetails.children.cardContent.children.propertyDetailsConatiner.children.buildingDataCard.children.multipleBuildingContainer.children.multipleBuilding.props.items[${index}].item${index}.children.cardContent.children.multipleBuildingCard.children`
//   }
// // Get the list of uom for selected building subtype
// let uomsList = get(
//   state,
//   "screenConfiguration.preparedFinalObject.applyScreenMdmsData.firenoc.BuildingType",
//   []
// ).filter(item => {
//   return item.code === value;
// });
// let uoms = get(uomsList, "[0].uom", []);

// // Remove previous dynamic uoms
// previousUoms.forEach(uom => {
//   !checkUomIsDefault(uom) &&
//     dispatch(handleField("apply", `${path}.${uom}`, "visible", false));
// });

// // Set required fields defaults
// setMandatory(dispatch, `${path}.PLOT_SIZE`, false);
// setMandatory(dispatch, `${path}.BUILTUP_AREA`, false);
// setMandatory(dispatch, `${path}.HEIGHT_OF_BUILDING`, false);

// // Dynamically create UOM's based on building subtype selection
// uoms.forEach(uom => {
//   if (checkUomIsDefault(uom)) {
//     setMandatory(dispatch, `${path}.${uom}`, true);
//   } else {
//     dispatch(
//       handleField("apply", path, uom, dynamic(uom, path, index))
//     );
//   }
// });

// // Set previous uoms array
// previousUoms = uoms;
// }

// const commonBuildingData = buildingType => {
//   let plotSize = {};
//   if (buildingType === "SINGLE") {
//     plotSize = {
//       ...getTextField({
//         label: {
//           labelName: "Plot Size (in Sq meters)",
//           labelKey: "NOC_PROPERTY_DETAILS_PLOT_SIZE_LABEL"
//         },
//         placeholder: {
//           labelName: "Enter Plot Size (in Sq meters)",
//           labelKey: "NOC_PROPERTY_DETAILS_PLOT_SIZE_PLACEHOLDER"
//         },
//         pattern: /^[0-9]*$/i,
//         errorMessage: "ERR_DEFAULT_INPUT_FIELD_MSG",
//         jsonPath: "FireNOCs[0].fireNOCDetails.buildings[0].plotsize",
//         gridDefination: {
//           xs: 12,
//           sm: 12,
//           md: 6
//         },
//         props:{
//           className:"applicant-details-error"
//         }
//       })
//     };
//   }
//   return {
//     buildingName: {
//       ...getTextField({
//         label: {
//           labelName: "Name of the Building",
//           labelKey: "NOC_PROPERTY_DETAILS_NAME_OF_BUILDING_LABEL"
//         },
//         placeholder: {
//           labelName: "Enter Name of the Building",
//           labelKey: "NOC_PROPERTY_DETAILS_NAME_OF_BUILDING_PLACEHOLDER"
//         },
//         required: true,
//         pattern: getPattern("TradeName"),
//         errorMessage: "ERR_DEFAULT_INPUT_FIELD_MSG",
//         jsonPath: "FireNOCs[0].fireNOCDetails.buildings[0].name",
//         // props: {
//         //   style: {
//         //     maxWidth: "400px"
//         //   }
//         // },
//         gridDefination: {
//           xs: 12,
//           sm: 12,
//           md: 6
//         },
//         props:{
//           className:"applicant-details-error"
//         }
//       })
//     },
//     dummyDiv: {
//       uiFramework: "custom-atoms",
//       componentPath: "Div",
//       gridDefination: {
//         xs: 12,
//         sm: 12,
//         md: 6
//       },
//       props: {
//         disabled: true
//       }
//     },
//     dynamicMdms : {
//       uiFramework: "custom-containers",
//       componentPath: "DynamicMdmsContainer",
//       props: {
//         dropdownFields: [
//           {
//             key : 'buildingUsageType',
//             isRequired: false,
//             fieldType : "autosuggest",
//             className:"applicant-details-error autocomplete-dropdown",
//             requiredValue : true,
//             callBack: buildingUsageTypeChange
//           },
//           {
//             key : 'buildingSubUsageType',
//             isRequired: false,
//             fieldType : "autosuggest",
//             className:"applicant-details-error autocomplete-dropdown",
//             requiredValue : true,
//             callBack: buildingSubUsageTypeChange
//           }
//         ],
//         callBackEdit: updateUsageType,
//         moduleName: "firenoc",
//         masterName: "BuildingType",
//         rootBlockSub : 'buildings',
//       }
//     },
//     NO_OF_FLOORS: prepareSelectField("NO_OF_FLOORS", 1, 20),
//     NO_OF_BASEMENTS: prepareSelectField("NO_OF_BASEMENTS", 0, 5),
//     PLOT_SIZE: prepareTextField("PLOT_SIZE"),
//     BUILTUP_AREA: prepareTextField("BUILTUP_AREA"),
//     HEIGHT_OF_BUILDING: prepareTextField("HEIGHT_OF_BUILDING")
//   };
// };

// export const propertyDetails = getCommonCard({
//   header: getCommonTitle(
//     {
//       labelName: "Property Details",
//       labelKey: "PROPERTY_DETAILS_HEADER"
//     },
//     {
//       style: {
//         marginBottom: 18
//       }
//     }
//   ),
//   break: getBreak(),
//   propertyDetailsConatiner: getCommonContainer({
//     buildingRadioGroup: {
//       uiFramework: "custom-containers",
//       componentPath: "RadioGroupContainer",
//       gridDefination: {
//         xs: 12
//       },
//       jsonPath: "FireNOCs[0].fireNOCDetails.noOfBuildings",
//       props: {
//         className:"applicant-details-error",
//         required: true,
//         label: { name: "No. of Buildings", key: "NOC_NO_OF_BUILDINGS_LABEL" },
//         buttons: [
//           {
//             labelName: "Single Building",
//             labelKey: "NOC_NO_OF_BUILDINGS_SINGLE_RADIOBUTTON",
//             value: "SINGLE"
//           },
//           {
//             label: "Multiple Building",
//             labelKey: "NOC_NO_OF_BUILDINGS_MULTIPLE_RADIOBUTTON",
//             value: "MULTIPLE"
//           }
//         ],
//         jsonPath: "FireNOCs[0].fireNOCDetails.noOfBuildings",
//         defaultValue: "SINGLE"
//       },
//       type: "array",
//       afterFieldChange: (action, state, dispatch) => {
//         let singleBuildingContainerJsonPath =
//           "components.div.children.formwizardSecondStep.children.propertyDetails.children.cardContent.children.propertyDetailsConatiner.children.buildingDataCard.children.singleBuildingContainer";
//         let multipleBuildingContainerJsonPath =
//           "components.div.children.formwizardSecondStep.children.propertyDetails.children.cardContent.children.propertyDetailsConatiner.children.buildingDataCard.children.multipleBuildingContainer";
//         if (action.value === "SINGLE") {
//           dispatch(
//             handleField(
//               "apply",
//               singleBuildingContainerJsonPath,
//               "props.style",
//               {}
//             )
//           );
//           dispatch(
//             handleField(
//               "apply",
//               multipleBuildingContainerJsonPath,
//               "props.style",
//               { display: "none" }
//             )
//           );
//         } else if (action.value === "MULTIPLE") {
//           dispatch(
//             handleField(
//               "apply",
//               singleBuildingContainerJsonPath,
//               "props.style",
//               { display: "none" }
//             )
//           );
//           dispatch(
//             handleField(
//               "apply",
//               multipleBuildingContainerJsonPath,
//               "props.style",
//               {}
//             )
//           );
//         }
//       }
//     },
//     buildingDataCard: getCommonContainer({
//       singleBuildingContainer: {
//         uiFramework: "custom-atoms",
//         componentPath: "Div",
//         gridDefination: {
//           xs: 12
//         },
//         children: {
//           singleBuilding: getCommonGrayCard({
//             singleBuildingCard: getCommonContainer(commonBuildingData("SINGLE"))
//           })
//         }
//       },
//       multipleBuildingContainer: {
//         uiFramework: "custom-atoms",
//         componentPath: "Div",
//         props: {
//           className:"applicant-details-error",
//           style: {
//             display: "none"
//           }
//         },
//         gridDefination: {
//           xs: 12
//         },
//         children: {
//           multipleBuilding: {
//             uiFramework: "custom-containers",
//             componentPath: "MultiItem",
//             props: {
//               scheama: getCommonGrayCard({
//                 multipleBuildingCard: getCommonContainer(
//                   commonBuildingData("MULTIPLE")
//                 )
//               }),
//               items: [],
//               addItemLabel: {
//                 labelKey: "NOC_PROPERTY_DETAILS_ADD_BUILDING_LABEL",
//                 labelName: "ADD BUILDING"
//               },
//               sourceJsonPath: "FireNOCs[0].fireNOCDetails.buildings",
//               // prefixSourceJsonPath:
//               //   "children.cardContent.children.buildingDataCard.children.multipleBuildingContainer.children",
//               prefixSourceJsonPath:
//                 "children.cardContent.children.multipleBuildingCard.children"
//             },
//             type: "array"
//           }
//         }
//       }
//     })
//   })
// });

/*
- The code configures a dynamic form for Fire NOC applications, allowing users to enter details about buildings.
- Depending on the selected building type (single or multiple), usage, and subtype, different fields are displayed, such as height, number of floors, plot size, etc.
- The form adjusts dynamically by showing/hiding fields and making certain fields mandatory based on user input.


*/




import React, { useEffect, useState } from "react";
import { FormStep, TextInput, CardLabel, InfoBannerIcon, RadioButtons, Dropdown, Label } from "@nudmcdgnpm/digit-ui-react-components";
import { useLocation } from "react-router-dom";
// import Timeline from "../components/ASTTimeline";
import { Controller, useForm } from "react-hook-form";

/*
This Component  is used to render the building details form for Fire NOC applications using  the react-UI components library.
we are using hooks to fetch the data from MDMS and using states to save the details
*/
const FNOCBuildingDetails = ({config, onSelect, userType, formData }) => {
  const { control } = useForm();
  const { pathname: url } = useLocation();
  let index = 0
  let validation = {};

    
  const [noOfBuildings, setnoOfBuildings] = useState((formData.buildings && formData.buildings[index] && formData.buildings[index].noOfBuildings) || formData?.buildings?.noOfBuildings || "");
  const [name, setname] = useState((formData.buildings && formData.buildings[index] && formData.buildings[index].name) || formData?.buildings?.name || "");
  const [usageTypeMajor, setusageTypeMajor] = useState((formData.buildings && formData.buildings[index] && formData.buildings[index].usageTypeMajor) || formData?.buildings?.usageTypeMajor || "");
  const [usageType, setusageType] = useState((formData.buildings && formData.buildings[index] && formData.buildings[index].usageType) || formData?.buildings?.usageType || "");  
  const [noOfFloors, setnoOfFloors] = useState((formData.buildings && formData.buildings[index] && formData.buildings[index].noOfFloors) || formData?.buildings?.noOfFloors || "");
  const [noOfBasements, setnoOfBasements] = useState((formData.buildings && formData.buildings[index] && formData.buildings[index].noOfBasements) || formData?.buildings?.noOfBasements || "");
  const [plotSize, setplotSize] = useState((formData.buildings && formData.buildings[index] && formData.buildings[index].plotSize) || formData?.buildings?.plotSize || "");
  const [builtArea, setbuiltArea] = useState((formData.buildings && formData.buildings[index] && formData.buildings[index].builtArea) || formData?.buildings?.builtArea || "");
  const [heightOfBuilding, setheightOfBuilding] = useState((formData.buildings && formData.buildings[index] && formData.buildings[index].heightOfBuilding) || formData?.buildings?.heightOfBuilding || "");
  const [noOfStudents, setnoOfStudents] = useState((formData.buildings && formData.buildings[index] && formData.buildings[index].noOfStudents) || formData?.buildings?.noOfStudents || "");


  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateId = Digit.ULBService.getStateId();
  console.log("noOfBuildingsnoOfBuildings",noOfBuildings);

    const common = [
      {
        code: "SINGLE",
        i18nKey: "Single Building",
        value:"SINGLE"
      },
      {
        code: "MULTIPLE",
        i18nKey: "Multiple Building",
        value:"MULTIPLE"
      },
    ]


    const { data: usageMajorType } = Digit.Hooks.useCustomMDMS(Digit.ULBService.getStateId(), "firenoc", [{ name: "BuildingType" }],
    {
      select: (data) => {
          const formattedData = data?.["firenoc"]?.["BuildingType"]
          const activeData = formattedData?.filter(item => item.active === true);
          return activeData;
      },
      });   // Note : used direct custom MDMS to get the Data ,Do not copy and paste without understanding the Context

      let buildingUsageType = [];
      let uniqueCodes = new Set();  // To store unique trimmed codes
      usageMajorType && usageMajorType.forEach((buildingMajorUsage) => {
        // Trim everything after the "." in the code
        let trimmedCode = buildingMajorUsage.code.split(".")[0];

        // If the trimmed code is not already in the set, add it to avoid duplicates
        if (!uniqueCodes.has(trimmedCode)) {
            uniqueCodes.add(trimmedCode);
            buildingUsageType.push({ i18nKey: `${trimmedCode}`, code: `${trimmedCode}`, value: `${trimmedCode}` });
        }
    });

    const { data: usageTypes } = Digit.Hooks.useCustomMDMS(Digit.ULBService.getStateId(), "firenoc", [{ name: "BuildingType" }],
        {
          select: (data) => {
              const formattedData = data?.["firenoc"]?.["BuildingType"]
              const activeData = formattedData?.filter(item => item.active === true);
              return activeData;
          },
      });  

  let buildingType = [];
  

  if (usageTypeMajor?.code) {
    usageTypes && usageTypes.forEach((buildingUsage) => {
        if (buildingUsage.code.startsWith(usageTypeMajor?.code)) { // Check if it matches the selected major type
            buildingType.push({ 
                i18nKey: `${buildingUsage.code}`, 
                code: `${buildingUsage.code}`, 
                value: `${buildingUsage.name}` 
            });
        }
    });
}


const dropdownOptionsforfloor = [];

for (let i = 1; i <= 20; i++) {
  dropdownOptionsforfloor.push({
        i18nKey: `${i}`,  // Unique i18n key
        code: `${i}`,      // Code for the dropdown item
        value: `${i}`           // Display value (1-20)
    });
}

const dropdownOptionsForBasements = [];
for (let i = 1; i <= 5; i++) {
  dropdownOptionsForBasements.push({
        i18nKey: `${i}`,  // Unique i18n key
        code: `${i}`,      // Code for the dropdown item
        value: `${i}`           // Display value (1-20)
    });
}

  

  function setName(e) {
    setname(e.target.value);
  }
  function setBuiltArea(e) {
    setbuiltArea(e.target.value);
  }

  function setPlotSize(e) {
    setplotSize(e.target.value);
  }

  function setHeightOfBuilding(e) {
    setheightOfBuilding(e.target.value);
  }

  
  function setNoOfStudents(e) {
    setnoOfStudents(e.target.value);
  }
  const goNext = () => {
    let buildingDetails = formData.buildings && formData.buildings[index];
    let buildingStep;
    
      buildingStep = { ...buildingDetails, noOfBuildings,noOfFloors, name, usageType, usageTypeMajor,noOfBasements, plotSize, builtArea,heightOfBuilding, noOfStudents};
      onSelect(config.key, { ...formData[config.key], ...buildingStep }, false, index);
    
  };

  const onSkip = () => onSelect();


  useEffect(() => {
    if (userType === "citizen") {
      goNext();
    }
  }, [noOfBuildings, usageType, usageTypeMajor, noOfFloors,noOfBasements, name, plotSize, builtArea,heightOfBuilding]);



  return (
    <React.Fragment>
    {
    //   window.location.href.includes("/employee") ? <Timeline currentStep={1} /> : null
    }

    <FormStep
      config={config}
      onSelect={goNext}
      onSkip={onSkip}
      t={t}
      isDisabled={ formData?.buildings?.usageTypeMajor?.code==="GROUP_B_EDUCATIONAL" && formData?.buildings?.usageType?.code==="GROUP_B_EDUCATIONAL.SUBDIVISIONB-1" ? !noOfBuildings || !usageType || !usageTypeMajor || !name || !noOfBasements || !noOfFloors || !noOfStudents : !noOfBuildings || !usageType || !usageTypeMajor || !name || !noOfBasements || !noOfFloors}

    >

      <div>
      <style>
        {`
        .select-wrap .options-card {
                width: 100% !important;
                -webkit-box-shadow: 0 8px 10px 1px rgba(0, 0, 0, 0.14), 0 3px 14px 2px rgba(0, 0, 0, 0.12), 0 5px 5px -3px rgba(0, 0, 0, 0.2);
                box-shadow: 0 8px 10px 1px rgba(0, 0, 0, 0.14), 0 3px 14px 2px rgba(0, 0, 0, 0.12), 0 5px 5px -3px rgba(0, 0, 0, 0.2);
                position: absolute;
                z-index: 20;
                margin-top: 4px;
                --bg-opacity: 1;
                background-color: #fff;
                background-color: rgba(255, 255, 255, var(--bg-opacity));
                overflow: scroll;
                max-height: 250px; 
                min-height:50px;
         } `
        }
      </style>
            <CardLabel>{`${t("NOC_NO_OF_BUILDINGS_LABEL")}`}</CardLabel>
            <Controller
              control={control}
              name={"noOfBuildings"}
              defaultValue={noOfBuildings}
              rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
              render={(props) => (
                <Dropdown
                  className="form-field"
                  selected={noOfBuildings}
                  select={setnoOfBuildings}
                  option={common}
                  optionKey="i18nKey"
                  placeholder={"Select"}
                  t={t}
                />
              )}
            />
            <CardLabel>{`${t("NOC_PROPERTY_DETAILS_NAME_OF_BUILDING_LABEL")}`}</CardLabel>
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              name="name"
              value={name}
              onChange={setName}
              style={{ width: "86%" }}
              ValidationRequired={false}
              {...(validation = {
                isRequired: true,
                pattern: "^[a-zA-Z]*$",
                type: "text",
                title: t("PT_NAME_ERROR_MESSAGE"),
              })}
            />

            <CardLabel>{`${t("FIRENOC_BUILDINGUSAGETYPE_LABEL ")}`}</CardLabel>
            <Controller
              control={control}
              name={"usageTypeMajor"}
              defaultValue={usageTypeMajor}
              rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
              render={(props) => (
                <Dropdown
                  className="form-field"
                  selected={usageTypeMajor}
                  select={setusageTypeMajor}
                  option={buildingUsageType}
                  optionKey="i18nKey"
                  placeholder={"Select"}
                  t={t}
                />

              )}
            />
            <CardLabel>{`${t("FIRENOC_BUILDINGSUBUSAGETYPE_LABEL")}`}</CardLabel>
            <Controller
              control={control}
              name={"usageType"}
              defaultValue={usageType}
              rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
              render={(props) => (
                <Dropdown
                  className="form-field"
                  selected={usageType}
                  select={setusageType}
                  option={buildingType}
                  optionKey="i18nKey"
                  placeholder={"Select"}

                  t={t}
                />

              )}

            />

            <CardLabel>{`${t("NOC_PROPERTY_DETAILS_NO_OF_FLOORS_LABEL")}`}</CardLabel>
            <Controller
              control={control}
              name={"noOfFloors"}
              defaultValue={noOfFloors}
              rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
              render={(props) => (
                <Dropdown
                  className="form-field"
                  selected={noOfFloors}
                  select={setnoOfFloors}
                  option={dropdownOptionsforfloor}
                  optionKey="i18nKey"
                  placeholder={"Select"}
                  t={t}
                />
              )}
            />
            <CardLabel>{`${t("NOC_PROPERTY_DETAILS_NO_OF_BASEMENTS_LABEL")}`}</CardLabel>
            <Controller
              control={control}
              name={"noOfBasements"}
              defaultValue={noOfBasements}
              rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
              render={(props) => (
                <Dropdown
                  className="form-field"
                  selected={noOfBasements}
                  select={setnoOfBasements}
                  option={dropdownOptionsForBasements}
                  optionKey="i18nKey"
                  placeholder={"Select"}
                  t={t}
                />
              )}
            />
            
            <CardLabel>{`${t("NOC_PROPERTY_DETAILS_PLOT_SIZE_LABEL")}`}</CardLabel>
            <TextInput
              t={t}
              type={"textarea"}
              isMandatory={false}
              optionKey="i18nKey"
              name="plotSize"
              value={plotSize}
              onChange={setPlotSize}
              style={{ width: "86%" }}
              ValidationRequired={false}
              {...(validation = {
                isRequired: false,
                pattern: "^[0-9]*$",
                type: "text",
                title: t("PT_NAME_ERROR_MESSAGE"),
              })}
            />
            <CardLabel>{`${t("NOC_PROPERTY_DETAILS_BUILTUP_AREA_LABEL")}`}</CardLabel>
            <TextInput
              t={t}
              type={"textarea"}
              isMandatory={false}
              optionKey="i18nKey"
              name="builtArea"
              value={builtArea}
              onChange={setBuiltArea}
              style={{ width: "86%" }}
              ValidationRequired={false}
              {...(validation = {
                isRequired: false,
                pattern: "^[0-9]*$",
                type: "text",
                title: t("PT_NAME_ERROR_MESSAGE"),
              })}
            />
            <CardLabel>{`${t("NOC_PROPERTY_DETAILS_HEIGHT_OF_BUILDING_LABEL")}`}</CardLabel>
            <TextInput
              t={t}
              type={"textarea"}
              isMandatory={false}
              optionKey="i18nKey"
              name="heightOfBuilding"
              value={heightOfBuilding}
              onChange={setHeightOfBuilding}
              style={{ width: "86%" }}
              ValidationRequired={false}
              {...(validation = {
                isRequired: false,
                pattern: "^[0-9]*$",
                type: "text",
                title: t("PT_NAME_ERROR_MESSAGE"),
              })}
            />
            {usageTypeMajor?.code==="GROUP_B_EDUCATIONAL" && usageType?.code==="GROUP_B_EDUCATIONAL.SUBDIVISIONB-1" && (
            <React.Fragment>
            <CardLabel>{`${t("NOC_PROPERTY_DETAILS_NO_OF_STUDENTS_LABEL")}`}</CardLabel>
            <TextInput
              t={t}
              type={"textarea"}
              isMandatory={false}
              optionKey="i18nKey"
              name="noOfStudents"
              value={noOfStudents}
              onChange={setNoOfStudents}
              style={{ width: "86%" }}
              ValidationRequired={false}
              {...(validation = {
                isRequired: true,
                pattern: "^[0-9]*$",
                type: "text",
                title: t("PT_NAME_ERROR_MESSAGE"),
              })}
            />
            </React.Fragment>
            )}
            {/* {noOfBuildings?.code==="MULTIPLE" && (
              <React.Fragment>
                <div className="astericColor"  style={{ justifyContent: "left", display: "flex", paddingBottom: "15px"}}>
                <button type="button" style={{ paddingTop: "10px" }} onClick={() => FNOCBuildingDetails()}>
                  {`${t("PT_ADD_UNIT")}`}
                </button>
                </div>
              </React.Fragment>
            )} */}
      </div>
    </FormStep>
    </React.Fragment>
  );
};
export default FNOCBuildingDetails;