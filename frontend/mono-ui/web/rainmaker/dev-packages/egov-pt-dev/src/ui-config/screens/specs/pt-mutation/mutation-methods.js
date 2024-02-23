
import {
  getTextField,
  getSelectField,
  getCommonContainer,
  getPattern,
  getCommonCard,
  getCommonTitle,
  getCommonParagraph,
  getLabel
} from "egov-ui-framework/ui-config/screens/specs/utils";
import { httpRequest } from "../../../../ui-utils/api";
import { getLocale } from "egov-ui-kit/utils/localStorageUtils";
import { handleScreenConfigurationFieldChange as handleField, prepareFinalObject } from "egov-ui-framework/ui-redux/screen-configuration/actions";
import { propertySearch, applicationSearch, dumm } from "./functions";
// import "./index.css";


export const resetFields = (state, dispatch) => {
  if (process.env.REACT_APP_NAME == "Citizen") {
    dispatch(
      handleField(
        "propertySearch",
        "components.div.children.propertySearchTabs.children.cardContent.children.tabSection.props.tabs[0].tabContent.searchPropertyDetails.children.cardContent.children.ulbCityContainer.children.ulbCity",
        "props.value",
        ""
      )
    );

    dispatch(prepareFinalObject(
      "ptSearchScreen.tenantId",
      ''
    ))
    dispatch(
      handleField(
        "propertySearch",
        "components.div.children.propertySearchTabs.children.cardContent.children.tabSection.props.tabs[0].tabContent.searchPropertyDetails.children.cardContent.children.ulbCityContainer.children.ulbCity",
        "props.isDisabled",
        false
      )
    );
    dispatch(
      handleField(
        "propertySearch",
        "components.div.children.propertySearchTabs.children.cardContent.children.tabSection.props.tabs[0].tabContent.searchPropertyDetails.children.cardContent.children.ulbCityContainer.children.ulbCity",
        "isDisabled",
        false
      )
    );
  }else{
    dispatch(
      handleField(
        "propertySearch",
        "components.div.children.propertySearchTabs.children.cardContent.children.tabSection.props.tabs[0].tabContent.searchPropertyDetails.children.cardContent.children.ulbCityContainer.children.ulbCity",
        "props.isDisabled",
        true
      )
    );
    dispatch(
      handleField(
        "propertySearch",
        "components.div.children.propertySearchTabs.children.cardContent.children.tabSection.props.tabs[0].tabContent.searchPropertyDetails.children.cardContent.children.ulbCityContainer.children.ulbCity",
        "isDisabled",
        true
      )
    );
  }

  dispatch(
    handleField(
      "propertySearch",
      "components.div.children.propertySearchTabs.children.cardContent.children.tabSection.props.tabs[0].tabContent.searchPropertyDetails.children.cardContent.children.ulbCityContainer.children.ownerMobNo",
      "props.value",
      ""
    )
  );
  dispatch(
    handleField(
      "propertySearch",
      "components.div.children.propertySearchTabs.children.cardContent.children.tabSection.props.tabs[0].tabContent.searchPropertyDetails.children.cardContent.children.ulbCityContainer.children.propertyTaxUniqueId",
      "props.value",
      ""
    )
  );
  dispatch(
    handleField(
      "propertySearch",
      "components.div.children.propertySearchTabs.children.cardContent.children.tabSection.props.tabs[0].tabContent.searchPropertyDetails.children.cardContent.children.ulbCityContainer.children.existingPropertyId",
      "props.value",
      ""
    )
  );
  dispatch(
    handleField(
      "propertySearch",
      "components.div.children.propertySearchTabs.children.cardContent.children.tabSection.props.tabs[1].tabContent.searchApplicationDetails.children.cardContent.children.appNumberContainer.children.propertyTaxApplicationNo",
      "props.value",
      ""
    )
  );
  dispatch(
    handleField(
      "propertySearch",
      "components.div.children.propertySearchTabs.children.cardContent.children.tabSection.props.tabs[1].tabContent.searchApplicationDetails.children.cardContent.children.appNumberContainer.children.ownerMobNoProp",
      "props.value",
      ""
    )
  );
  dispatch(
    handleField(
      "propertySearch",
      "components.div.children.propertySearchTabs.children.cardContent.children.tabSection.props.tabs[1].tabContent.searchApplicationDetails.children.cardContent.children.appNumberContainer.children.applicationPropertyTaxUniqueId",
      "props.value",
      ""
    )
  );
  dispatch(prepareFinalObject(
    "ptSearchScreen.acknowledgementIds",
    ''
  ))
  dispatch(prepareFinalObject(
    "ptSearchScreen.ids",
    ''
  ))
  dispatch(prepareFinalObject(
    "ptSearchScreen.mobileNumber",
    ''
  ))
  dispatch(prepareFinalObject(
    "ptSearchScreen.oldpropertyids",
    ''
  ))
  dispatch(prepareFinalObject(
    "ptSearchScreen.locality",
    ''
  ))
  dispatch(prepareFinalObject(
    "ptSearchScreen.name",
    ''
  ))

};

export const test =()=>{
  return(
    <h1>Hello test</h1>
    
  )
  
}
export const searchPropertyDetails = getCommonCard({
  subHeader: getCommonTitle({
    labelName: "Search Property",
    labelKey: "SEARCH_PROPERTY"
  }),

  subParagraph: getCommonParagraph({
    labelName: "Provide at least one non-mandatory parameter to search for an application (In case of Search by locality and name . please select city name again)",
   //labelKey: "PT_HOME_SEARCH_RESULTS_DESC"
    labelKey: "Provide at least one non-mandatory parameter to search for an application (In case of search by locality and name . please select city name again)",
    
  }
  ),
  subParagraphaa: getCommonParagraph({
    labelName:"One Time settlement for Property Tax has been implemented in mSeva. You requested check and Re-Assess your property before paying tax .",
    labelKey:"One Time settlement for Property Tax has been implemented in mSeva. You requested check and Re-Assess your property before paying tax.",
    style: {
      color: "red",
      fontweight: "bold"
     
    }
  }),
  ulbCityContainer: getCommonContainer({
    ulbCity: {
      ...getSelectField({
      uiFramework: "custom-containers-local",
      moduleName: "egov-pt",
      componentPath: "AutosuggestContainer",
      props: {
        className: "autocomplete-dropdown",
        suggestions: [],
        label: {
          labelName: "ULB",
          labelKey: "PT_ULB_CITY"
        },
        placeholder: {
          labelName: "Select ULB",
          labelKey: "PT_ULB_CITY_PLACEHOLDER"
        },
        localePrefix: {
          moduleName: "TENANT",
          masterName: "TENANTS"
        },
        jsonPath: "ptSearchScreen.tenantId",
        sourceJsonPath: "searchScreenMdmsData.tenant.tenants",
        labelsFromLocalisation: true,
        required: true,
        isClearable: true,
        disabled: process.env.REACT_APP_NAME === "Citizen" ? false : true,
        inputLabelProps: {
          shrink: true
        }
      },
      required: true,
      jsonPath: "ptSearchScreen.tenantId",
      sourceJsonPath: "searchScreenMdmsData.tenant.tenants",
    }),
    beforeFieldChange: async (action, state, dispatch) => {
      //Below only runs for citizen - not required here in employee

      try {
        let payload = await httpRequest(
          "post",
          "/egov-location/location/v11/boundarys/_search?hierarchyTypeCode=REVENUE&boundaryType=Locality",
          "_search",
          [{ key: "tenantId", value: action.value }],
          {}
        );
        console.log("payload", payload)
        const mohallaData =
          payload &&
          payload.TenantBoundary[0] &&
          payload.TenantBoundary[0].boundary &&
          payload.TenantBoundary[0].boundary.reduce((result, item) => {
            result.push({
              ...item,
              name: `${action.value
                .toUpperCase()
                .replace(
                  /[.]/g,
                  "_"
                )}_REVENUE_${item.code
                  .toUpperCase()
                  .replace(/[._:-\s\/]/g, "_")}`
            });
            return result;
          }, []);

        console.log(mohallaData, "mohallaData")



        dispatch(
          prepareFinalObject(
            "applyScreenMdmsData.tenant.localities",
            mohallaData
          )
        );
        dispatch(
          handleField(
            "apply",
            "components.div.children.formwizardSecondStep.children.propertyLocationDetails.children.cardContent.children.propertyDetailsConatiner.children.propertyMohalla",
            "props.suggestions",
            mohallaData
          )
        );
        const mohallaLocalePrefix = {
          moduleName: action.value,
          masterName: "REVENUE"
        };
        dispatch(
          handleField(
            "apply",
            "components.div.children.formwizardSecondStep.children.propertyLocationDetails.children.cardContent.children.propertyDetailsConatiner.children.propertyMohalla",
            "props.localePrefix",
            mohallaLocalePrefix
          )
        );

        // dispatch(
        //   fetchLocalizationLabel(getLocale(), action.value, action.value)
        // );

      } catch (e) {
        console.log(e);
      }

    },
      gridDefination: {
        xs: 12,
        sm: 4
      }
    },
    ownerMobNo: getTextField({
      label: {
        labelName: "Owner Mobile No.",
        labelKey: "PT_HOME_SEARCH_RESULTS_OWN_MOB_LABEL"
      },
      placeholder: {
        labelName: "Enter your mobile No.",
        labelKey: "PT_HOME_SEARCH_RESULTS_OWN_MOB_PLACEHOLDER"
      },
      gridDefination: {
        xs: 12,
        sm: 4,


      },
      iconObj: {
        label: "+91 |",
        position: "start"
      },
      required: false,
      pattern: getPattern("MobileNo"),
      jsonPath: "ptSearchScreen.mobileNumber",
      disabled: process.env.REACT_APP_NAME === "Citizen" ? true : false,
      errorMessage: "ERR_INVALID_MOBILE_NUMBER"
    }),
    propertyTaxUniqueId: getTextField({
      label: {
        labelName: "Property Tax Unique Id",
        labelKey: "PT_PROPERTY_UNIQUE_ID"
      },
      placeholder: {
        labelName: "Enter Property Tax Unique Id",
        labelKey: "PT_PROPERTY_UNIQUE_ID_PLACEHOLDER"
      },
      gridDefination: {
        xs: 12,
        sm: 4,

      },
      required: false,
      pattern: /^[a-zA-Z0-9-]*$/i,
      errorMessage: "ERR_INVALID_PROPERTY_ID",
      jsonPath: "ptSearchScreen.ids"
    }),
    existingPropertyId: getTextField({
      label: {
        labelName: "Existing Property ID",
        labelKey: "PT_EXISTING_PROPERTY_ID"
      },
      placeholder: {
        labelName: "Enter Existing Property ID",
        labelKey: "PT_EXISTING_PROPERTY_ID_PLACEHOLDER"
      },
      gridDefination: {
        xs: 12,
        sm: 4,

      },
      required: false,
      pattern: /^[^\$\"'<>?\\\\~`!@$%^()+={}\[\]*:;“”‘’]{1,64}$/i,
      errorMessage: "ERR_INVALID_PROPERTY_ID",
      jsonPath: "ptSearchScreen.oldpropertyids"
    }),
    
  //-------------locality--------------
  propertyMohalla: {
    uiFramework: "custom-containers",
    componentPath: "AutosuggestContainer",
    jsonPath:"ptSearchScreen.locality",
    required: true,
    props: {
      style: {
        width: "100%",
        cursor: "pointer"
      },
      label: {
        labelName: "Locality/Mohalla",
       // labelKey: "NOC_PROPERTY_DETAILS_MOHALLA_LABEL"
      },
      placeholder: {
        labelName: "Select Locality/Mohalla",
        //labelKey: "NOC_PROPERTY_DETAILS_MOHALLA_PLACEHOLDER"
      },
      jsonPath:"ptSearchScreen.locality",
      sourceJsonPath: "applyScreenMdmsData.tenant.localities",
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
    beforeFieldChange: async (action, state, dispatch) => {
      // dispatch(
      //   prepareFinalObject(
      //     "Licenses[0].tradeLicenseDetail.address.locality.name",
      //     action.value && action.value.label
      //   )
      // );
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
    jsonPath: "ptSearchScreen.name",
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
            color: "black",
            borderColor: "black",
            width: "220px",
            height: "48px",
            margin: "8px",
            float: "right"
          }
        },
        children: {
          buttonLabel: getLabel({
            labelName: "Reset",
            labelKey: "PT_HOME_RESET_BUTTON"
          })
        },
        onClickDefination: {
          action: "condition",
          callBack: resetFields
        }
      },
      searchButton: {
        componentPath: "Button",
        gridDefination: {
          xs: 12,
          sm: 6
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
            labelName: "Search",
            labelKey: "PT_HOME_SEARCH_RESULTS_BUTTON_SEARCH"
          })
        },
        onClickDefination: {
          action: "condition",
          callBack: propertySearch
        }
      }
    })
  })
});


export const searchApplicationDetails = getCommonCard({
  subHeader: getCommonTitle({
    labelName: "Search Application",
    labelKey: "SEARCH_APPLICATION"
  }),

  subParagraph: getCommonParagraph({
    labelName: "Provide at least one non-mandatory parameter to search for an application",
    labelKey: "PT_HOME_SEARCH_RESULTS_DESC"
  }),
  appNumberContainer: getCommonContainer({
    propertyTaxApplicationNo: getTextField({
      label: {
        labelName: "Application No",
        labelKey: "PT_PROPERTY_APPLICATION_NO"
      },
      placeholder: {
        labelName: "Enter Application No",
        labelKey: "PT_PROPERTY_APPLICATION_NO_PLACEHOLDER"
      },
      gridDefination: {
        xs: 12,
        sm: 4,

      },
      required: false,
      pattern: /^[a-zA-Z0-9-]*$/i,
      errorMessage: "ERR_INVALID_APPLICATION_NO",
      jsonPath: "ptSearchScreen.acknowledgementIds"
    }),
    ownerMobNoProp: getTextField({
      label: {
        labelName: "Owner Mobile No.",
        labelKey: "PT_HOME_SEARCH_APP_OWN_MOB_LABEL"
      },
      placeholder: {
        labelName: "Enter your mobile No.",
        labelKey: "PT_HOME_SEARCH_RESULTS_OWN_MOB_PLACEHOLDER"
      },
      gridDefination: {
        xs: 12,
        sm: 4,


      },
      iconObj: {
        label: "+91 |",
        position: "start"
      },
      required: false,
      pattern: getPattern("MobileNo"),
      jsonPath: "ptSearchScreen.mobileNumber",
      errorMessage: "ERR_INVALID_MOBILE_NUMBER"
    }),
    applicationPropertyTaxUniqueId: getTextField({
      label: {
        labelName: "Property Tax Unique Id",
        labelKey: "PT_PROPERTY_UNIQUE_ID"
      },
      placeholder: {
        labelName: "Enter Property Tax Unique Id",
        labelKey: "PT_PROPERTY_UNIQUE_ID_PLACEHOLDER"
      },
      gridDefination: {
        xs: 12,
        sm: 4,

      },
      required: false,
      pattern: /^[a-zA-Z0-9-]*$/i,
      errorMessage: "ERR_INVALID_PROPERTY_ID",
      jsonPath: "ptSearchScreen.ids"
    }),
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
            color: "black",
            borderColor: "black",
            width: "220px",
            height: "48px",
            margin: "8px",
            float: "right"
          }
        },
        children: {
          buttonLabel: getLabel({
            labelName: "Reset",
            labelKey: "PT_HOME_RESET_BUTTON"
          })
        },
        onClickDefination: {
          action: "condition",
          callBack: resetFields
        }
      },
      searchButton: {
        componentPath: "Button",
        gridDefination: {
          xs: 12,
          sm: 6
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
            labelName: "Search",
            labelKey: "PT_HOME_SEARCH_RESULTS_BUTTON_SEARCH"
          })
        },
        onClickDefination: {
          action: "condition",
          callBack: applicationSearch
        }
      }
    })
  })
});


export const searchProperty = getCommonContainer({
  searchPropertyDetails,

});

export const searchApplication = getCommonContainer({
  searchApplicationDetails
});
