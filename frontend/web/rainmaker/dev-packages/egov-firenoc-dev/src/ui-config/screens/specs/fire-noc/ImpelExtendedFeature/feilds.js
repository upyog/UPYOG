import {getSelectField,getTextField,getPattern} from "egov-ui-framework/ui-config/screens/specs/utils";

import { handleScreenConfigurationFieldChange as handleField } from "egov-ui-framework/ui-redux/screen-configuration/actions";


export const NOCareaTypeField = {
    areaType: getSelectField({
        label: {
          labelName: "Area Type",
          // labelKey: "NOC_AREA_TYPE_LABEL"
          },
          placeholder: {
          labelName: "Select Application Area Type",
          // labelKey: "NOC_APPLICATION_AREA_TYPE"
          }, 
  
        data: [
          {
            code: "Urban",
            label: "NOC_TYPE_URBAN"
          },
          {
            code: "Rural",
            label: "NOC_TYPE_RURAL"
          }
        ],
        jsonPath: "searchScreen.areaType",
        gridDefination: {
          xs: 12,
          sm: 4
        }
      }),
      newProvisionalType: getSelectField({
        label: {
          labelName: "NOC Type",
          labelKey: "NOC_TYPE_LABEL"
          },
          placeholder: {
          labelName: "Select Application Type",
          labelKey: "NOC_APPLICATION_TYPE_PLACEHOLDER"
          },
  
        data: [
          {
            code: "NEW",
            label: "NOC_TYPE_NEW_RADIOBUTTON"
          },
          {
            code: "PROVISIONAL",
            label: "NOC_TYPE_PROVISIONAL_RADIOBUTTON"
          },
          {
            code: "RENEWAL",
            // label: "NOC_TYPE_PROVISIONAL_RADIOBUTTON"
          }
        ],
        // jsonPath: "FireNOCs[0].fireNOCDetails.fireNOCType",
        jsonPath: "searchScreen.fireNOCType",
        // sourceJsonPath: "applyScreenMdmsData.searchScreen.fireNOCType",
        gridDefination: {
          xs: 12,
          sm: 4
        }
  
      }),
    }
    export const NOCLandandTotalCoveredArea = {
      landArea: {
        ...getTextField({
          label: {
            labelName: "Land Area (in Sq meters)",
            labelKey: "NOC_PROPERTY_DETAILS_LAND_AREA_LABEL"
          },
          placeholder: {
            labelName: "Enter Land Area of the building (in Sq meters)",
            labelKey: "NOC_PROPERTY_DETAILS_LAND_AREA_PLACEHOLDER"
          },
          required: true,
          // pattern: getPattern("MobileNo"),
          errorMessage: "ERR_DEFAULT_INPUT_FIELD_MSG",
  
          jsonPath: "FireNOCs[0].fireNOCDetails.buildings[0].landArea",
          // type: "string",
          
          // props: {
          //   style: {
          //     maxWidth: "400px"
          //   }
          // },
          gridDefination: {
            xs: 12,
            sm: 12,
            md: 6
          },
          props:{
            type: "number",
            className:"applicant-details-error"
          }
        })
      },
      totalCoveredArea: {
        ...getTextField({
          label: {
            labelName: "Total Covered Area (in Sq meters)",
            labelKey: "NOC_PROPERTY_DETAILS_COVERED_AREA_LABEL"
          },
          placeholder: {
            labelName: "Enter Total Covered Area (in Sq meters)",
            labelKey: "NOC_PROPERTY_DETAILS_COVERED_AREA_PLACEHOLDER"
          },
          required: true,
          // pattern: getPattern("MobileNo"),
          errorMessage: "ERR_DEFAULT_INPUT_FIELD_MSG",
          jsonPath: "FireNOCs[0].fireNOCDetails.buildings[0].totalCoveredArea",
          // props: {
          //   style: {
          //     maxWidth: "400px"
          //   }
          // },
          gridDefination: {
            xs: 12,
            sm: 12,
            md: 6
          },
          props:{
            type: "number",
            className:"applicant-details-error"
          }
        })
      },

      landArea: {
        ...getTextField({
          label: {
            labelName: "Land Area (in Sq meters)",
            labelKey: "NOC_PROPERTY_DETAILS_LAND_AREA_LABEL"
          },
          placeholder: {
            labelName: "Enter Land Area of the building (in Sq meters)",
            labelKey: "NOC_PROPERTY_DETAILS_LAND_AREA_PLACEHOLDER"
          },
          required: true,
          // pattern: getPattern("MobileNo"),
          errorMessage: "ERR_DEFAULT_INPUT_FIELD_MSG",
  
          jsonPath: "FireNOCs[0].fireNOCDetails.buildings[0].landArea",
          // type: "string",
          
          // props: {
          //   style: {
          //     maxWidth: "400px"
          //   }
          // },
          gridDefination: {
            xs: 12,
            sm: 12,
            md: 6
          },
          props:{
            type: "number",
            className:"applicant-details-error"
          }
        })
      },
      totalCoveredArea: {
        ...getTextField({
          label: {
            labelName: "Total Covered Area (in Sq meters)",
            labelKey: "NOC_PROPERTY_DETAILS_COVERED_AREA_LABEL"
          },
          placeholder: {
            labelName: "Enter Total Covered Area (in Sq meters)",
            labelKey: "NOC_PROPERTY_DETAILS_COVERED_AREA_PLACEHOLDER"
          },
          required: true,
          // pattern: getPattern("MobileNo"),
          errorMessage: "ERR_DEFAULT_INPUT_FIELD_MSG",
          jsonPath: "FireNOCs[0].fireNOCDetails.buildings[0].totalCoveredArea",
          // props: {
          //   style: {
          //     maxWidth: "400px"
          //   }
          // },
          gridDefination: {
            xs: 12,
            sm: 12,
            md: 6
          },
          props:{
            type: "number",
            className:"applicant-details-error"
          }
        })
      },
      parkingArea: {
        ...getTextField({
          label: {
            labelName: "Parking Area (in Sq meters)",
            labelKey: "NOC_PROPERTY_DETAILS_PARKING_AREA_LABEL"
          },
          placeholder: {
            labelName: "Enter Parking Area (in Sq meters)",
            labelKey: "NOC_PROPERTY_DETAILS_PARKING_AREA_PLACEHOLDER"
          },
           required: true,
          // pattern: getPattern("MobileNo"),
          errorMessage: "ERR_DEFAULT_INPUT_FIELD_MSG",
          jsonPath: "FireNOCs[0].fireNOCDetails.buildings[0].parkingArea",
          // props: {
          //   style: {
          //     maxWidth: "400px"
          //   }
          // },
          gridDefination: {
            xs: 12,
            sm: 12,
            md: 6
          },
          props:{
            type: "number",
            className:"applicant-details-error"
          }
        })
      },
      leftSurrounding: {
        ...getTextField({
          label: {
            labelName: "Left surrounding",
            // labelKey: "NOC_PROPERTY_DETAILS_PARKING_AREA_LABEL"
          },
          placeholder: {
            labelName: "Enter Left surrounding",
            // labelKey: "NOC_PROPERTY_DETAILS_PARKING_AREA_PLACEHOLDER"
          },
          // required: true,
          pattern: getPattern("TradeName"),
          errorMessage: "ERR_DEFAULT_INPUT_FIELD_MSG",
          jsonPath: "FireNOCs[0].fireNOCDetails.buildings[0].leftSurrounding",
          // props: {
          //   style: {
          //     maxWidth: "400px"
          //   }
          // },
          gridDefination: {
            xs: 12,
            sm: 12,
            md: 6
          },
          props:{
            className:"applicant-details-error"
          }
        })
      },
      rightSurrounding: {
        ...getTextField({
          label: {
            labelName: "Right surrounding",
            // labelKey: "NOC_PROPERTY_DETAILS_PARKING_AREA_LABEL"
          },
          placeholder: {
            labelName: "Enter Right surrounding",
            // labelKey: "NOC_PROPERTY_DETAILS_PARKING_AREA_PLACEHOLDER"
          },
          // required: true,
          pattern: getPattern("TradeName"),
          errorMessage: "ERR_DEFAULT_INPUT_FIELD_MSG",
          jsonPath: "FireNOCs[0].fireNOCDetails.buildings[0].rightSurrounding",
          gridDefination: {
            xs: 12,
            sm: 12,
            md: 6
          },
          props:{
            className:"applicant-details-error",
            number: "NA"
          }
        })
      },
      frontSurrounding: {
        ...getTextField({
          label: {
            labelName: "Front surrounding",
            // labelKey: "NOC_PROPERTY_DETAILS_PARKING_AREA_LABEL"
          },
          placeholder: {
            labelName: "Enter Front surrounding",
            // labelKey: "NOC_PROPERTY_DETAILS_PARKING_AREA_PLACEHOLDER"
          },
          // required: true,
          pattern: getPattern("TradeName"),
          errorMessage: "ERR_DEFAULT_INPUT_FIELD_MSG",
          jsonPath: "FireNOCs[0].fireNOCDetails.buildings[0].frontSurrounding",
          // props: {
          //   style: {
          //     maxWidth: "400px"
          //   }
          // },
          gridDefination: {
            xs: 12,
            sm: 12,
            md: 6
          },
          props:{
            className:"applicant-details-error"
          }
        })
      },
      backSurrounding: {
        ...getTextField({
          label: {
            labelName: "Back surrounding",
            // labelKey: "NOC_PROPERTY_DETAILS_PARKING_AREA_LABEL"
          },
          placeholder: {
            labelName: "Enter Back surrounding",
            // labelKey: "NOC_PROPERTY_DETAILS_PARKING_AREA_PLACEHOLDER"
          },
          // required: true,
          pattern: getPattern("TradeName"),
          errorMessage: "ERR_DEFAULT_INPUT_FIELD_MSG",
          jsonPath: "FireNOCs[0].fireNOCDetails.buildings[0].backSurrounding",
          // props: {
          //   style: {
          //     maxWidth: "400px"
          //   }
          // },
          gridDefination: {
            xs: 12,
            sm: 12,
            md: 6
          },
          props:{
            className:"applicant-details-error"
          }
        })
      },

}