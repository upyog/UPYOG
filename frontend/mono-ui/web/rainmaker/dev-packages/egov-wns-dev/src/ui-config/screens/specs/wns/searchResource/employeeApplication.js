import {
  getCommonCard,
  getCommonTitle,
  getTextField,
  getSelectField,
  getCommonContainer,
  getCommonParagraph,
  getPattern,
  getLabel
} from "egov-ui-framework/ui-config/screens/specs/utils";
import { searchApiCall } from "./functions";
import { resetFieldsForConnection} from '../../utils';
import { prepareFinalObject } from "egov-ui-framework/ui-redux/screen-configuration/actions";
import { httpRequest } from "../../../../../ui-utils/api";
import store from "../../../../../ui-redux/store";
import { getTenantId,getUserInfo } from "egov-ui-kit/utils/localStorageUtils";
let datalocality ; 
async function dummy(action, state, dispatch) {
  const tenantId =getTenantId();
  debugger;
  dispatch=store.dispatch;
    try {
      let payload = await httpRequest(
        "post",
        "/egov-location/location/v11/boundarys/_search?hierarchyTypeCode=REVENUE&boundaryType=Locality",
        "_search",
        [{ key: "tenantId", value: tenantId }],
        {}
      );
      console.log("payload", payload)
      var dtlocality = payload &&
      payload.TenantBoundary[0] &&
      payload.TenantBoundary[0].boundary &&
      payload.TenantBoundary[0].boundary.reduce((result, item) => {
        result.push({ ...item });
        return result;
      }, []);
      // datalocality.push(payload.TenantBoundary[0].boundary);
      datalocality = dtlocality;
      
      dispatch(
        prepareFinalObject(
          "localities", datalocality
        )
      );
      // dispatch(
      //   fetchLocalizationLabel(getLocale(), action.value, action.value)
      // );

    } catch (e) {
      console.log(e);
    }
    
}
dummy();
console.log("cc",datalocality);
export const wnsApplication = getCommonCard({
  subHeader: getCommonTitle({
    labelKey: "WS_SEARCH_CONNECTION_SUB_HEADER"
  }),
  subParagraph: getCommonParagraph({
    labelKey: "WS_HOME_SEARCH_CONN_RESULTS_DESC"
  }),
  wnsApplicationContainer: getCommonContainer({
    city: {
      uiFramework: "custom-containers-local",
      moduleName: "egov-hrms",
      componentPath: "AutosuggestContainer",
      jsonPath: "searchConnection.tenantId",
      gridDefination: {
        xs: 12,
        sm: 4
      },
      props: {
        optionLabel: "name",
        optionValue: "code",
        label: {
            labelName: "ULB",
            labelKey: "WS_PROP_DETAIL_CITY"
        },  
        placeholder: {
            labelName: "Select ULB",
            labelKey: "WS_PROP_DETAIL_CITY_PLACEHOLDER"
        },
        localePrefix: {
            moduleName: "TENANT",
            masterName: "TENANTS"
        },
        className: "autocomplete-dropdown",
        sourceJsonPath: "applyScreenMdmsData.tenant.tenants",
        jsonPath: "searchConnection.tenantId",
        labelsFromLocalisation: true,
        required: true,
        disabled: true,
        isDisabled:true,
      },
      required: true,
      disabled: true,
      isDisabled:true,
    },
    propertyid: getTextField({
        label: {
            labelKey: "WS_PROPERTY_ID_LABEL"
        },
        placeholder: {
            labelKey: "WS_PROPERTY_ID_PLACEHOLDER"
        },
        gridDefination: {
            xs: 12,
            sm: 4
        },
        required: false,
        pattern: /^[a-zA-Z0-9-]*$/i,
        errorMessage: "ERR_DEFAULT_INPUT_FIELD_MSG",
        jsonPath: "searchConnection.propertyId"
    }),
    ownerMobNo: getTextField({
        label: {
            labelKey: "WS_HOME_SEARCH_RESULTS_OWN_MOB_LABEL"
        },
        placeholder: {
            labelKey: "WS_OWN_DETAIL_MOBILE_NO_PLACEHOLDER"
        },
        gridDefination: {
            xs: 12,
            sm: 4
        },
        iconObj: {
            label: "+91 |",
            position: "start"
        },
        required: false,
        pattern: getPattern("MobileNo"),
        jsonPath: "searchConnection.mobileNumber",
        errorMessage: "ERR_DEFAULT_INPUT_FIELD_MSG"
    }),
    consumerid: getTextField({
        label: {
            labelKey: "WS_MYCONNECTIONS_CONSUMER_NO"
        },
        placeholder: {
            labelKey: "WS_SEARCH_CONNNECTION_CONSUMER_PLACEHOLDER"
        },
        gridDefination: {
            xs: 12,
            sm: 4
        },
        required: false,
        pattern: getPattern("consumerNo"),
        errorMessage: "ERR_DEFAULT_INPUT_FIELD_MSG",
        jsonPath: "searchConnection.connectionNumber"
    }),
    oldConsumerid: getTextField({
        label: {
            labelKey: "WS_SEARCH_CONNNECTION_OLD_CONSUMER_LABEL"
        },
        placeholder: {
            labelKey: "WS_SEARCH_CONNNECTION_OLD_CONSUMER_PLACEHOLDER"
        },
        gridDefination: {
            xs: 12,
            sm: 4
        },
        required: false,
        // pattern: /^[a-zA-Z0-9-]*$/i,
        errorMessage: "ERR_DEFAULT_INPUT_FIELD_MSG",
        jsonPath: "searchConnection.oldConnectionNumber"
    }),
     //-------------locality--------------
 propertyMohalla: {
  uiFramework: "custom-containers",
  componentPath: "AutosuggestContainer",
  jsonPath:"searchConnection.locality",
  required: true,
  props: {
    // style: {
    //   // width: "100%",
    //   cursor: "pointer"
    // },
    label: {
      labelName: "Locality/Mohalla",
     // labelKey: "NOC_PROPERTY_DETAILS_MOHALLA_LABEL"
    },
    placeholder: {
      labelName: "Select Locality/Mohalla",
      //labelKey: "NOC_PROPERTY_DETAILS_MOHALLA_PLACEHOLDER"
    },
    //jsonPath:"searchConnection.locality",
    sourceJsonPath: "localities",
    //data: datalocality,
    labelsFromLocalisation: true,
    errorMessage: "ERR_DEFAULT_INPUT_FIELD_MSG",
    suggestions: [],
    fullwidth: true,
    required: false,
    disabled: process.env.REACT_APP_NAME === "Citizen" ? true : false,
   // type:hidden,
    inputLabelProps: {
      shrink: true
    }
    // className: "tradelicense-mohalla-apply"
  },
  
  gridDefination: {
    xs: 12,
    sm: 4
  }
},
//---------------locality-end--------------
//-------------------Owner Name----------------------
ownerName: getTextField({
  label: {
    labelName: "Owner Name",
    labelKey: "Owner Name"
  },
  placeholder: {
    labelName: "Enter Owner Name",
    labelKey: "Owner Name"
  },
  gridDefination: {
    xs: 12,
    sm: 4,

  },
  required: false,
 // pattern: /^[^\$\"'<>?\\\\~`!@$%^()+={}\[\]*:;“”‘’]{1,64}$/i,
  errorMessage: "ERR_INVALID_PROPERTY_ID",
  jsonPath: "searchConnection.name",
  disabled: process.env.REACT_APP_NAME === "Citizen" ? true : false,
}),
//-------------------End Owner Name--------------------------------
  }),

  button: getCommonContainer({
    buttonContainer: getCommonContainer({
      resetButton: {
        componentPath: "Button",
        gridDefination: {
          xs: 12,
          sm: 6
          // align: "center"
        },
        props: {
          variant: "outlined",
          style: {
            color: "rgba(0, 0, 0, 0.6000000238418579)",
            borderColor: "rgba(0, 0, 0, 0.6000000238418579)",
            width: "220px",
            height: "48px",
            margin: "8px",
            float: "right"
          }
        },
        children: {
          buttonLabel: getLabel({
            labelKey: "WS_SEARCH_CONNECTION_RESET_BUTTON"
          })
        },
        onClickDefination: {
          action: "condition",
          callBack: resetFieldsForConnection
        }
      },
      searchButton: {
        componentPath: "Button",
        gridDefination: {
          xs: 12,
          sm: 6,
          // align: "center"
        },
        props: {
          variant: "contained",
          style: {
            color: "white",
            margin: "8px",
            backgroundColor: "rgba(0, 0, 0, 0.6000000238418579)",
            borderRadius: "2px",
            width: "220px",
            height: "48px"
          }
        },
        children: {
          buttonLabel: getLabel({
            labelKey: "WS_SEARCH_CONNECTION_SEARCH_BUTTON"
          })
        },
        onClickDefination: {
          action: "condition",
          callBack: searchApiCall
        }
      },
    })
  })
});