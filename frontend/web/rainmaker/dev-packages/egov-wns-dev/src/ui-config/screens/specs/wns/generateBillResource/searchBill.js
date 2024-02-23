import {
    getCommonCard,
    getCommonTitle,
    getTextField,
    getSelectField,
    getCommonContainer,
    getPattern,
    getLabel
  } from "egov-ui-framework/ui-config/screens/specs/utils";
  import { httpRequest } from "../../../../../ui-utils/api";
  import { resetFieldsForApplication } from '../../utils';
  //import {generateBillApiCall} from "../searchResource/functions"
  import { prepareFinalObject } from "egov-ui-framework/ui-redux/screen-configuration/actions";
  import {handleScreenConfigurationFieldChange as handleField  } from "egov-ui-framework/ui-redux/screen-configuration/actions";
  import { getLocale } from "egov-ui-kit/utils/localStorageUtils";
  import "./index.css";

  const getMhollaData = async(dispatch)=>{
    const queryObject = [
      { key: "tenantId", value: localStorage.getItem('tenant-id') },
       
    ];
    let response = await httpRequest(
      "post",
      "/egov-location/location/v11/boundarys/_search?hierarchyTypeCode=REVENUE&boundaryType=Locality",
      "_search",
      queryObject,
      {}
    );
    let mohallaDataArray = [];
    let mohallaDataRow=null;
    let name,code;
    response.TenantBoundary[0].boundary.map((element,index) => {
      name = element.name + "( "+element.code+" )";
     // code=element.code;
      mohallaDataRow={"code":name};
     mohallaDataArray.push(mohallaDataRow);
    
   });
   
   dispatch(prepareFinalObject("mohallaData", mohallaDataArray));
  }



  export const searchBill = getCommonCard({
  

  subHeader: getCommonTitle({
    label: "Search Bill"
    },
    {
      style: {
        marginBottom: 8
      }
    }
    ),
    wnsGenerateBill: getCommonContainer({

     
//  ---------------------------------------------------------------------------------------
//             Connection Type drop down
//-----------------------------------------------------------------------------------------
      applicationtype: getSelectField({

        label: {
          labelKey: "WS_GENERATE_BILL_CONNECTION_TYPE_LABEL"
        },
        placeholder: {
          labelKey: "WS_GENERATE_BILL_CONNECTION_TYPE_PLACEHOLDER"
        },
        data: [
          {
            code: "WS_CONNECTION_TYPE_WATER",
            value:"WS",
          },
          {
            code: "WS_CONNECTION_TYPE_SEWERAGE",
            value:"SW",
          }
         
        ],
       
        gridDefination: {xs: 12, sm: 4},
        required: false,
        jsonPath: "generateBillScreen.transactionType",
        labelsFromLocalisation: true,
        fullwidth: true,
        isClearable: true,
        inputLabelProps: {
        shrink: true
        }
      }),

  //---------------------------------------------------------------------------------------
//             locality drop down
//-----------------------------------------------------------------------------------------
locality: getSelectField({
  jsonPath: "mohallaData.name",
    label: { labelName: "Locality", labelKey: "WS_GENERATE_BILL_LOCALITY_LABEL" },
    placeholder: { labelName: "Select maholla", labelKey: "WS_GENERATE_BILL_LOCALITY_PLACEHOLDER" },
    sourceJsonPath: "mohallaData",
    jsonPath: "generateBillScreen.mohallaData",
    required: false,
    isClearable: true,
    labelsFromLocalisation: true,
    suggestions: [],
    required: false,
    labelsFromLocalisation: true,
    gridDefination: {xs: 12, sm: 4},
}),
      }),
//---------------------------------------------------------------------------------------
//             Reset Button and Submit Button
//-----------------------------------------------------------------------------------------
    button: getCommonContainer({
      buttonContainer: getCommonContainer({
        resetButton: {
          componentPath: "Button",
          gridDefination: { xs: 12, sm: 4 },
          props: {
            variant: "outlined",
            style: {
              color: "rgba(0, 0, 0, 0.6000000238418579)",
              borderColor: "rgba(0, 0, 0, 0.6000000238418579)",
              width: "220px",
              height: "48px",
              margin: "28px",
              float: "right"
            }
          },
          children: { buttonLabel: getLabel({ labelKey: "WS_GENERATE_BILL_RESET_BUTTON" }) },
          onClickDefination: {
            action: "condition",
            callBack: resetFieldsForApplication
          }
        },

//---------------------------------------------------------------------------------------
//             Generate Bill  Button
//-----------------------------------------------------------------------------------------
        searchButton: {
          componentPath: "Button",
          gridDefination: { xs: 12, sm: 4 },
          props: {
            variant: "contained",
            style: {
              color: "white",
              margin: "28px",
              backgroundColor: "rgba(0, 0, 0, 0.6000000238418579)",
              borderRadius: "2px",
              width: "220px",
              height: "48px",
              float: "left"
            }
          },
          children: { buttonLabel: getLabel({ label: "Search" }) },
          onClickDefination: {
         
            action: "condition",
            
          }
        },
      })


    }),
    
    
});
  