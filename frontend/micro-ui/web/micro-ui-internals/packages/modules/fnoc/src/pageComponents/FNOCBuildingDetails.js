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




import { CardLabel, Dropdown, FormStep, LinkButton, Loader, RadioButtons, TextInput } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useEffect, useState } from "react";


const FNOCBuildingDetails = ({ t, config, onSelect, userType, formData }) => {
  console.log("formdatata",formData);
  let validation = {};
  const [noOfBuildings, setnoOfBuildings] = useState(formData?.buildings?.units?.noOfBuildings||formData?.buildings?.noOfBuildings||"");
  const [name, setname] = useState(formData?.buildings?.units?.name ||formData?.buildings?.name || "");
  const [usageTypeMajor, setusageTypeMajor] = useState(formData?.buildings?.units?.usageTypeMajor||formData?.buildings?.usageTypeMajor || "");
  const [usageType, setusageType] = useState(formData?.buildings?.units?.usageType ||formData?.buildings?.usageType|| "");
  const [noOfFloors, setnoOfFloors] = useState(formData?.buildings?.units?.noOfFloors || formData?.buildings?.noOfFloors|| "");

  const [noOfBasements, setnoOfBasements] = useState(formData?.buildings?.units?.noOfBasements||formData?.buildings?.noOfBasements||"");
  const [plotSize, setplotSize] = useState(formData?.buildings?.units?.plotSize ||formData?.buildings?.plotSize|| "");
  const [builtArea, setbuiltArea] = useState(formData?.buildings?.units?.builtArea ||formData?.buildings?.builtArea|| "");
  const [heightOfBuilding, setheightOfBuilding] = useState(formData?.buildings?.units?.heightOfBuilding ||formData?.buildings?.heightOfBuilding|| "");
  const [noOfStudents, setnoOfStudents] = useState(formData?.buildings?.units?.noOfStudents||formData?.buildings?.noOfStudents || "");

  const [error, setError] = useState(null);
  const [fields, setFeilds] = useState(
    (formData?.buildings && formData?.buildings?.units) || [{ noOfBuildings: "", name: "", usageTypeMajor: "", usageType: "", noOfFloors: "", noOfBasements: "", plotSize: "", builtArea: "", heightOfBuilding: "", noOfStudents: "" }]
  );

  const tenantId = Digit.ULBService.getCitizenCurrentTenant();
  const stateId = Digit.ULBService.getStateId();
  function handleAdd() {
    const values = [...fields];
    values.push({ noOfBuildings: "", name: "", usageTypeMajor: "", usageType: "", noOfFloors: "", noOfBasements: "", plotSize: "", builtArea: "", heightOfBuilding: "", noOfStudents: ""  });
    setFeilds(values);
  }

  function handleRemove(index) {
    const values = [...fields];
    if (values.length != 1) {
      values.splice(index, 1);
      setFeilds(values);
    }
  }

  const common = [
    {
      code: "SINGLE",
      i18nKey: "Single Building",
      value:"NOC_NO_OF_BUILDINGS_SINGLE_RADIOBUTTON"
    },
    {
      code: "MULTIPLE",
      i18nKey: "Multiple Building",
      value:"NOC_NO_OF_BUILDINGS_SINGLE_RADIOBUTTON"
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
  

  if (fields?.[0]?.usageTypeMajor?.code) {
    usageTypes && usageTypes.forEach((buildingUsage) => {
        if (buildingUsage.code.startsWith(fields?.[0]?.usageTypeMajor?.code)) { // Check if it matches the selected major type
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
        i18nKey: `${i}`,  
        code: `${i}`,      
        value: `${i}`,           
    });
}

const dropdownOptionsForBasements = [];
for (let i = 1; i <= 5; i++) {
  dropdownOptionsForBasements.push({
        i18nKey: `${i}`,  
        code: `${i}`,      
        value: `${i}`,
    });
}


  function selectnoOfBuildings(i, value) {
    let units = [...fields];
    units[i].noOfBuildings = value;
    setnoOfBuildings(value);
    setFeilds(units);
  }
  function selectname(i, e) {
    let units = [...fields];
    units[i].name = e.target.value;
    setname(e.target.value);
    setFeilds(units);
  }
  function selectusageTypeMajor(i, value) {
    let units = [...fields];
    units[i].usageTypeMajor = value;
    setusageTypeMajor(value);
    setFeilds(units);
  }
  function selectusageType(i, value) {
    let units = [...fields];
    units[i].usageType = value;
    setusageType(value);
    setFeilds(units);
  }
  function selectnoOfFloors(i, value) {
    let units = [...fields];
    units[i].noOfFloors = value;
    setnoOfFloors(value);
    setFeilds(units);
}

function selectnoOfBasements(i, value) {
    let units = [...fields];
    units[i].noOfBasements = value;
    setnoOfBasements(value);
    setFeilds(units);
  }
  function selectplotSize(i, e) {
    let units = [...fields];
    units[i].plotSize = e.target.value;
    setplotSize(e.target.value);
    setFeilds(units);
  }

  function selectheightOfBuilding(i, e) {
    let units = [...fields];
    units[i].heightOfBuilding = e.target.value;
    setheightOfBuilding(e.target.value);
    setFeilds(units);
}

function selectbuiltArea(i, e) {
    let units = [...fields];
    units[i].builtArea = e.target.value;
    setbuiltArea(e.target.value);
    setFeilds(units);
  }
  function selectnoOfStudents(i, e) {
    let units = [...fields];
    units[i].noOfStudents = e.target.value;
    setnoOfStudents(e.target.value);
    setFeilds(units);
  }
  
  const goNext = () => {
      let buildingDetails = formData.buildings || {};
      console.log("buildingDetailsbuildingDetails",buildingDetails);
      let buildingStep = { 
        ...buildingDetails, 
        units: fields
      };
      onSelect(config.key, { ...formData[config.key], ...buildingStep }, false);
    
  };
  const onSkip = () => onSelect();
  return (
    <React.Fragment>
      {/* {window.location.href.includes("/citizen") ? <Timeline /> : null} */}
        <FormStep
          config={config}
          onSelect={goNext}
          onSkip={onSkip}
          t={t}
          forcedError={t(error)}
          isDisabled={!fields[0].noOfBuildings || !fields[0].name || !fields[0].usageTypeMajor || !fields[0].usageType||!fields[0].noOfFloors||!fields[0].noOfBasements}
        >
          {fields.map((field, index) => {
            return (
              <div key={`${field}-${index}`}>
                <div
                  style={{
                    border: "solid",
                    borderRadius: "5px",
                    padding: "10px",
                    paddingTop: "20px",
                    marginTop: "10px",
                    borderColor: "#f3f3f3",
                    background: "#FAFAFA",
                  }}
                >
                  <CardLabel>{`${t("NOC_NO_OF_BUILDINGS_LABEL")}`}</CardLabel>
                  <LinkButton
                    label={
                      <div>
                        <span>
                          <svg
                            style={{ float: "right", position: "relative", bottom: "32px" }}
                            width="24"
                            height="24"
                            viewBox="0 0 24 24"
                            fill="none"
                            xmlns="http://www.w3.org/2000/svg"
                          >
                            <path
                              d="M1 16C1 17.1 1.9 18 3 18H11C12.1 18 13 17.1 13 16V4H1V16ZM14 1H10.5L9.5 0H4.5L3.5 1H0V3H14V1Z"
                              fill={!(fields.length == 1) ? "#494848" : "#FAFAFA"}
                            />
                          </svg>
                        </span>
                      </div>
                    }
                    style={{ width: "100px", display: "inline" }}
                    onClick={(e) => handleRemove(index)}
                  />
                    <RadioButtons
                      t={t}
                      options={common}
                      optionsKey="i18nKey"
                      name={`noOfBuildings-${index}`}
                      value={field?.noOfBuildings}
                      selectedOption={field?.noOfBuildings}
                      onSelect={(e) => selectnoOfBuildings(index, e)}
                      labelKey="i18nKey"
                      isPTFlow={true}
                    />
                  <CardLabel>{`${t("NOC_PROPERTY_DETAILS_NAME_OF_BUILDING_LABEL")}`}</CardLabel>
                  <TextInput
                    style={{ background: "#FAFAFA" }}
                    t={t}
                    type={"text"}
                    isMandatory={false}
                    optionKey="i18nKey"
                    name="name"
                    value={field?.name}
                    onChange={(e) => selectname(index, e)}
                    disable={false}
                  />
                  <CardLabel>{`${t("FIRENOC_BUILDINGUSAGETYPE_LABEL ")}`}</CardLabel>
                    <Dropdown
                      t={t}
                      optionKey="i18nKey"
                      isMandatory={config.isMandatory}
                      option={buildingUsageType}
                      selected={field?.usageTypeMajor}
                      optionCardStyles={{maxHeight:"125px",overflow:"scroll"}}
                      select={(e) => selectusageTypeMajor(index, e)}
                    />
                    <CardLabel>{`${t("FIRENOC_BUILDINGSUBUSAGETYPE_LABEL")}`}</CardLabel>
                    <Dropdown
                      t={t}
                      optionKey="i18nKey"
                      isMandatory={config.isMandatory}
                      option={buildingType}
                      selected={field?.usageType}
                      optionCardStyles={{maxHeight:"125px",overflow:"scroll"}}
                      select={(e) => selectusageType(index, e)}
                    />
                    <CardLabel>{`${t("NOC_PROPERTY_DETAILS_NO_OF_FLOORS_LABEL")}`}</CardLabel>
                    <Dropdown
                      t={t}
                      optionKey="i18nKey"
                      isMandatory={config.isMandatory}
                      option={dropdownOptionsforfloor}
                      selected={field?.noOfFloors}
                      optionCardStyles={{maxHeight:"125px",overflow:"scroll"}}
                      select={(e) => selectnoOfFloors(index, e)}
                    />
                    <CardLabel>{`${t("NOC_PROPERTY_DETAILS_NO_OF_BASEMENTS_LABEL")}`}</CardLabel>
                    <Dropdown
                      t={t}
                      optionKey="i18nKey"
                      isMandatory={config.isMandatory}
                      option={dropdownOptionsForBasements}
                      selected={field?.noOfBasements}
                      optionCardStyles={{maxHeight:"125px",overflow:"scroll"}}
                      select={(e) => selectnoOfBasements(index, e)}
                    />
                  <CardLabel>{`${t("NOC_PROPERTY_DETAILS_PLOT_SIZE_LABEL")}`}</CardLabel>
                  <TextInput
                    style={{ background: "#FAFAFA" }}
                    t={t}
                    type={"text"}
                    isMandatory={false}
                    optionKey="i18nKey"
                    name="plotSize"
                    value={field?.plotSize}
                    onChange={(e) => selectplotSize(index, e)}
                    disable={false}
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
                    style={{ background: "#FAFAFA" }}
                    t={t}
                    type={"text"}
                    isMandatory={false}
                    optionKey="i18nKey"
                    name="builtArea"
                    value={field?.builtArea}
                    onChange={(e) => selectbuiltArea(index, e)}
                    disable={false}
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
                    style={{ background: "#FAFAFA" }}
                    t={t}
                    type={"text"}
                    isMandatory={false}
                    optionKey="i18nKey"
                    name="heightOfBuilding"
                    value={field?.heightOfBuilding}
                    onChange={(e) => selectheightOfBuilding(index, e)}
                    disable={false}
                    ValidationRequired={false}
                    {...(validation = {
                      isRequired: false,
                      pattern: "^[0-9]*$",
                      type: "text",
                      title: t("PT_NAME_ERROR_MESSAGE"),
                    })}
                  />
                  {fields?.[0]?.usageTypeMajor?.code==="GROUP_B_EDUCATIONAL" && fields?.[0]?.usageType?.code==="GROUP_B_EDUCATIONAL.SUBDIVISIONB-1" && (
                    <React.Fragment>
                  <CardLabel>{`${t("NOC_PROPERTY_DETAILS_NO_OF_STUDENTS_LABEL")}`}</CardLabel>
                  <TextInput
                    style={{ background: "#FAFAFA" }}
                    t={t}
                    type={"text"}
                    isMandatory={false}
                    optionKey="i18nKey"
                    name="noOfStudents"
                    value={field?.noOfStudents}
                    onChange={(e) => selectnoOfStudents(index, e)}
                    disable={false}
                    ValidationRequired={false}
                    {...(validation = {
                      isRequired: false,
                      pattern: "^[0-9]*$",
                      type: "text",
                      title: t("PT_NAME_ERROR_MESSAGE"),
                    })}
                  />
                    </React.Fragment>
                  )}
                </div>
              </div>
            );
          })}
          {fields?.[0]?.noOfBuildings?.code==="MULTIPLE" && (
            <div className="astericColor"  style={{ display: "flex", paddingBottom: "15px", color: "#FF8C00" }}>
            <button type="button" style={{ paddingTop: "10px" }} onClick={() => handleAdd()}>
              {`${t("FNOC_ADD_BUTTON")}`}
            </button>
          </div>
          )}
          
        </FormStep>
      
    </React.Fragment>
  );
};
export default FNOCBuildingDetails;
