import {
  getCommonCard,
  getCommonTitle,
  getTextField,
  getSelectField,
  getCommonContainer,
  getCommonParagraph,
  getPattern,
  getDateField,
  getLabel
} from "egov-ui-framework/ui-config/screens/specs/utils";
import store from "../../../../../ui-redux/store";
import { searchApiCall, dataloc } from "./functions";
import { httpRequest } from "../../../../../ui-utils/api";
import { prepareFinalObject } from "egov-ui-framework/ui-redux/screen-configuration/actions";
import { getTenantId,getUserInfo } from "../../utils/localStorageUtils";
// import { getTenantId,getUserInfo } from "egov-ui-kit/utils/localStorageUtils";
var datalocality ;
async function dummy(action, state, dispatch) {
  const tenantId = getTenantId();
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
      
      console.log("ddg", datalocality +"  "+dtlocality)

 
      dispatch(
        prepareFinalObject(
          "applyScreenMdmsData.tenant.localities", datalocality
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

export const tradeLicenseApplication = getCommonCard ({
  subHeader: getCommonTitle({
    labelName: "Search Trade License Application",
    labelKey: "TL_HOME_SEARCH_RESULTS_HEADING"
  }),
  subParagraph: getCommonParagraph({
    labelName: "Provide at least one parameter to search for an application",
    labelKey: "TL_HOME_SEARCH_RESULTS_DESC"
  }),
  appTradeAndMobNumContainer: getCommonContainer({
    applicationNo: getTextField({
      label: {
        labelName: "Application No.",
        labelKey: "TL_HOME_SEARCH_RESULTS_APP_NO_LABEL"
      },
      placeholder: {
        labelName: "Enter Application No.",
        labelKey: "TL_HOME_SEARCH_RESULTS_APP_NO_PLACEHOLDER"
      },
      gridDefination: {
        xs: 12,
        sm: 4
      },
      required: false,
      pattern: /^[a-zA-Z0-9-]*$/i,
      errorMessage: "ERR_INVALID_APPLICATION_NO",
      jsonPath: "searchScreen.applicationNumber"
    }),

    tradeLicenseNo: getTextField({
      label: {
        labelName: "Trade License No.",
        labelKey: "TL_HOME_SEARCH_RESULTS_TL_NO_LABEL"
      },
      placeholder: {
        labelName: "Enter Trade License No.",
        labelKey: "TL_HOME_SEARCH_RESULTS_TL_NO_PLACEHOLDER"
      },
      gridDefination: {
        xs: 12,
        sm: 4
      },
      required: false,
      pattern: /^[a-zA-Z0-9-]*$/i,
      errorMessage: "ERR_INVALID_TRADE_LICENSE_NO",
      jsonPath: "searchScreen.licenseNumbers"
    }),
    ownerMobNo: getTextField({
      label: {
        labelName: "Owner Mobile No.",
        labelKey: "TL_HOME_SEARCH_RESULTS_OWN_MOB_LABEL"
      },
      placeholder: {
        labelName: "Enter your mobile No.",
        labelKey: "TL_HOME_SEARCH_RESULTS_OWN_MOB_PLACEHOLDER"
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
      jsonPath: "searchScreen.mobileNumber",
      errorMessage: "ERR_INVALID_MOBILE_NUMBER"
    })
  }),
  applicationTypeAndToFromDateContainer: getCommonContainer({
    applicationType: {
        uiFramework: "custom-containers-local",
        moduleName: "egov-tradelicence",
        componentPath: "AutosuggestContainer",
        jsonPath:
          "searchScreen.applicationType",
        sourceJsonPath: "applyScreenMdmsData.searchScreen.applicationType",
        gridDefination: {
          xs: 12,
          sm: 4
        },
        props: {
          className: "applicant-details-error autocomplete-dropdown",
          labelsFromLocalisation: true,
          suggestions: [],
          jsonPath:
          "searchScreen.applicationType",
          sourceJsonPath: "applyScreenMdmsData.searchScreen.applicationType",
          label: {
            labelName: "Application Type",
            labelKey: "TL_APPLICATION_TYPE_LABEL"
          },
          placeholder: {
            labelName: "Select Application Type",
            labelKey: "TL_APPLICATION_TYPE_PLACEHOLDER"
          },
          localePrefix: {
            moduleName: "TradeLicense",
            masterName: "ApplicationType"
          },
          fullwidth: true,
          required: false,
          isClearable:true,
          inputLabelProps: {
            shrink: true
          }
        }
    },
    fromDate: getDateField({
      label: { labelName: "From Date", labelKey: "TL_COMMON_FROM_DATE_LABEL" },
      placeholder: {
        labelName: "Select From Date",
        labelKey: "TL_FROM_DATE_PLACEHOLDER"
      },
      jsonPath: "searchScreen.fromDate",
      gridDefination: {
        xs: 12,
        sm: 4
      },
      pattern: getPattern("Date"),
      errorMessage: "ERR_INVALID_DATE",
      required: false
    }),

    toDate: getDateField({
      label: { labelName: "To Date", labelKey: "TL_COMMON_TO_DATE_LABEL" },
      placeholder: {
        labelName: "Select to Date",
        labelKey: "TL_COMMON_TO_DATE_PLACEHOLDER"
      },
      jsonPath: "searchScreen.toDate",
      gridDefination: {
        xs: 12,
        sm: 4
      },
      pattern: getPattern("Date"),
      errorMessage: "ERR_INVALID_DATE",
      required: false
    })
  }),
  appStatusContainer: getCommonContainer({
    applicationNo: {
      uiFramework: "custom-containers-local",
      moduleName: "egov-tradelicence",
      componentPath: "AutosuggestContainer",
      props: {
        label: {
          labelName: "Application status",
          labelKey: "TL_HOME_SEARCH_RESULTS_APP_STATUS_LABEL"
        },
        placeholder: {
          labelName: "Select Application Status",
          labelKey: "TL_HOME_SEARCH_RESULTS_APP_STATUS_PLACEHOLDER"
        },
        required: false,
        localePrefix: {
          moduleName: "WF",
          masterName: "NEWTL"
        },
        className: "autocomplete-dropdown",
        labelsFromLocalisation: true,
        isClearable:true,
        data:[
          {
            code : "INITIATED"
          },
          {
            code : "APPLIED"
          },
          {
            code : "FIELDINSPECTION"
          },
          {
            code : "PENDINGAPPROVAL"
          },
          {
            code : "PENDINGPAYMENT"
          },
          {
            code : "APPROVED"
          },
          {
            code : "CITIZENACTIONREQUIRED"
          },     
          {
            code : "EXPIRED"
          },
          {
            code : "CANCELLED"
          },
          {
            code : "REJECTED"
          }
        ],
      },
      jsonPath: "searchScreen.status",
      gridDefination: {
        xs: 12,
        sm: 4
      }
    },
 //-------------locality--------------
 propertyMohalla: {
  uiFramework: "custom-containers",
  componentPath: "AutosuggestContainer",
  jsonPath:"searchScreen.locality",
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
    //jsonPath:"ptSearchScreen.locality",
    sourceJsonPath: "applyScreenMdmsData.tenant.localities",
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
  // onclick: async (action, state, dispatch) => {
  //   console.log("helllo GG");
  //   dummy(action, state, dispatch)
  //   dispatch(
  //     prepareFinalObject(
  //       "Licenses[0].tradeLicenseDetail.address.locality.name",
  //       action.value && action.value.label
  //     )
  //   );
  // },
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
  jsonPath: "searchScreen.name",
  disabled: process.env.REACT_APP_NAME === "Citizen" ? true : false,
}),
//-------------------End Owner Name--------------------------------
  }),
 

  button: getCommonContainer({
    // firstCont: {

    buttonContainer: getCommonContainer({
      firstCont: {
        uiFramework: "custom-atoms",
        componentPath: "Div",
        gridDefination: {
          xs: 12,
          sm: 4
        }
      },
      searchButton: {
        componentPath: "Button",
        gridDefination: {
          xs: 12,
          sm: 4
        },
        props: {
          variant: "contained",
          style: {
            color: "white",

            backgroundColor: "rgba(0, 0, 0, 0.6000000238418579)",
            borderRadius: "2px",
            width: "80%",
            height: "48px"
          }
        },
        children: {
          buttonLabel: getLabel({
            labelName: "Search",
            labelKey: "TL_HOME_SEARCH_RESULTS_BUTTON_SEARCH"
          })
        },
        onClickDefination: {
          action: "condition",
          callBack: searchApiCall
        }
      },
      lastCont: {
        uiFramework: "custom-atoms",
        componentPath: "Div",
        gridDefination: {
          xs: 12,
          sm: 4
        }
      }
    })
  }),
},
{
  style: {
    overflow: "visible"
  },
});
