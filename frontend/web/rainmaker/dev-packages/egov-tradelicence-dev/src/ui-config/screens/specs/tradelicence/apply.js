import commonConfig from "config/common.js";
import { getCommonCard, getCommonContainer, getCommonHeader, getCommonParagraph, getCommonTitle, getStepperObject, getLabel } from "egov-ui-framework/ui-config/screens/specs/utils";
import { handleScreenConfigurationFieldChange as handleField, prepareFinalObject, unMountScreen } from "egov-ui-framework/ui-redux/screen-configuration/actions";
import { getQueryArg } from "egov-ui-framework/ui-utils/commons";
import { getTenantId } from "egov-ui-kit/utils/localStorageUtils";
import get from "lodash/get";
import set from "lodash/set";
import { httpRequest } from "../../../../ui-utils";
import { getBoundaryData, updatePFOforSearchResults } from "../../../../ui-utils/commons";
import { getAllDataFromBillingSlab, getCurrentFinancialYear, pageResetAndChange } from "../utils";
import { documentList } from "./applyResource/documentList";
import { footer } from "./applyResource/footer";
import { tradeDetails } from "./applyResource/tradeDetails";
import { tradeLocationDetails } from "./applyResource/tradeLocationDetails";
import { tradeOwnerDetails } from "./applyResource/tradeOwnerDetails";
import { tradeReviewDetails } from "./applyResource/tradeReviewDetails";
import { selfdeclarationdoc } from "./searchResource/functions"

export const stepsData = [
  { labelName: "Trade Details", labelKey: "TL_COMMON_TR_DETAILS" },
  { labelName: "Owner Details", labelKey: "TL_COMMON_OWN_DETAILS" },
  { labelName: "Documents", labelKey: "TL_COMMON_DOCS" },
  { labelName: "Summary", labelKey: "TL_COMMON_SUMMARY" }
];
export const stepper = getStepperObject(
  { props: { activeStep: 0 } },
  stepsData
);
export const header = getCommonContainer({
  header:
    getQueryArg(window.location.href, "action") !== "edit"
      ? getCommonHeader({
        labelName: `Apply for New Trade License ${
          process.env.REACT_APP_NAME === "Citizen"
            ? "(" + getCurrentFinancialYear() + ")"
            : ""
          }`,
        // dynamicArray: getQueryArg(window.location.href, "action") === "EDITRENEWAL" ? [getnextFinancialYear(getCurrentFinancialYear())]:[getCurrentFinancialYear()],
        labelKey: getQueryArg(window.location.href, "action") === "EDITRENEWAL" ? "TL_COMMON_APPL_RENEWAL_LICENSE_YEAR" : "TL_COMMON_APPL_NEW_LICENSE_YEAR"

      })
      : {},
  applicationNumber: {
    uiFramework: "custom-atoms-local",
    moduleName: "egov-tradelicence",
    componentPath: "ApplicationNoContainer",
    props: {
      number: "NA"
    },
    visible: false
  }
});

export const tradeDocumentDetails = getCommonCard({
  header: getCommonTitle(
    {
      labelName: "Required Documents",
      labelKey: "TL_NEW-UPLOAD-DOCS_HEADER"
    },
    {
      style: {
        marginBottom: 18
      }
    }
  ),
  paragraph: getCommonParagraph({
    labelName:
      "Only one file can be uploaded for one document. If multiple files need to be uploaded then please combine all files in a pdf and then upload",
    labelKey: "TL_NEW-UPLOAD-DOCS_SUBHEADER"
  }),
  button: getCommonContainer({
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
        labelName: "Download Self Declaration Document",
        labelKey: "Download Self Declaration Document"
      })
    },
    onClickDefination: {
      action: "condition",
      callBack: selfdeclarationdoc
    }
  },}),
  documentList
});

export const getMdmsData = async (action, state, dispatch) => {
  debugger;
  let mdmsBody = {
    MdmsCriteria: {
      tenantId: commonConfig.tenantId,
      moduleDetails: [
        {
          moduleName: "TradeLicense",
          masterDetails: [
            { name: "TradeType" },
            { name: "AccessoriesCategory" },
            { name: "ApplicationType" },
            { name: "documentObj" }
          ]
        },
        {
          moduleName: "common-masters",
          masterDetails: [
            { name: "OwnerType" },
            { name: "DocumentType" },
            { name: "UOM" },
          ]
        },
        {
          moduleName: "tenant",
          masterDetails: [
            {
              name: "tenants"
            }
          ]
        },
        {
          moduleName: "egf-master",
          masterDetails: [{ name: "FinancialYear" }]
        }
      ]
    }
  };
  try {
    let payload = null;
    payload = await httpRequest(
      "post",
      "/egov-mdms-service/v1/_search",
      "_search",
      [],
      mdmsBody
    );
    set(
      payload,
      "MdmsRes.TradeLicense.MdmsTradeType",
      get(payload, "MdmsRes.TradeLicense.TradeType", [])
    );
    const localities = get(
      state.screenConfiguration,
      "preparedFinalObject.applyScreenMdmsData.tenant.localities",
      []
    );
    if (localities && localities.length > 0) {
      payload.MdmsRes.tenant.localities = localities;
    }
    dispatch(prepareFinalObject("applyScreenMdmsData", payload.MdmsRes));
    if(getQueryArg(window.location.href, "action") === "EDITRENEWAL"){
     let financialYearData = get(
       payload,
       "MdmsRes.egf-master.FinancialYear",
       []
   //  ).filter(item => item.module === "TL"&& item.active === true && item.code === getCurrentFinancialYear());
   ).filter(item => item.module === "TL"&& item.active === true);
     set(payload, "MdmsRes.egf-master.FinancialYear",financialYearData);
    }else{
     let financialYearData = get(
       payload,
       "MdmsRes.egf-master.FinancialYear",
       []
     ).filter(item => item.module === "TL"&& item.active === true);
   //  ).filter(item => item.module === "TL"&& item.active === true && item.code === getCurrentFinancialYear());

     set(payload, "MdmsRes.egf-master.FinancialYear",financialYearData);
    }
  } catch (e) {
    console.log(e);
  }
};

export const getData = async (action, state, dispatch) => {
  const queryValue = getQueryArg(window.location.href, "applicationNumber");
  const tenantId = getQueryArg(window.location.href, "tenantId");

  const applicationNo = queryValue
    ? queryValue
    : get(
      state.screenConfiguration.preparedFinalObject,
      "Licenses[0].oldLicenseNumber",
      null
    );
  await getMdmsData(action, state, dispatch);
  await getAllDataFromBillingSlab(getTenantId(), dispatch);


  if (applicationNo) {
    //Edit/Update Flow ----
    const applicationType = get(
      state.screenConfiguration.preparedFinalObject,
      "Licenses[0].tradeLicenseDetail.additionalDetail.applicationType",
      null
    );
    const isEditRenewal = getQueryArg(window.location.href, "action") === "EDITRENEWAL";

    if (getQueryArg(window.location.href, "action") !== "edit" && !isEditRenewal) {
      dispatch(
        prepareFinalObject("Licenses", [
          {
            licenseType: "PERMANENT",
            oldLicenseNumber: queryValue ? "" : applicationNo,
            tradeLicenseDetail: {
              additionalDetail: {
                applicationType: applicationType ? applicationType : "NEW"
              }
            }
          }
        ])
      );
    }
    // dispatch(prepareFinalObject("LicensesTemp", []));
    await updatePFOforSearchResults(action, state, dispatch, applicationNo, tenantId);
    //To disabled the Trade Unit and Accessories 
   let applicationStatus = get( state.screenConfiguration.preparedFinalObject, "Licenses[0].status")
   if(applicationStatus != "INITIATED" || applicationStatus != "PENDINGPAYMENT") {
     let isDisabledTUData = ['tradeCategory', 'tradeType', 'tradeSubType', 'tradeUOM', 'tradeUOMValue'];
     let isDisabledASData = ['accessoriesCount', 'accessoriesName', 'accessoriesUOM', 'accessoriesUOMValue'];
     isDisabledTUData.forEach(value => {
       disabledKeyValue(dispatch, 'tradeUnitCard', value);
     });
     isDisabledASData.forEach(value => {
       disabledKeyValue(dispatch, 'accessoriesCard', value);
     });
   }

    if (!queryValue) {
      const oldApplicationNo = get(
        state.screenConfiguration.preparedFinalObject,
        "Licenses[0].applicationNumber",
        null
      );
      dispatch(
        prepareFinalObject("Licenses[0].oldLicenseNumber", oldApplicationNo)
      );
      if (oldApplicationNo !== null) {
        dispatch(prepareFinalObject("Licenses[0].financialYear", ""));
        dispatch(
          prepareFinalObject(
            "Licenses[0].tradeLicenseDetail.additionalDetail.applicationType",
            "APPLICATIONTYPE.RENEWAL"
          )
        );
        dispatch(
          handleField(
            "apply",
            "components.div.children.formwizardFirstStep.children.tradeDetails.children.cardContent.children.tradeDetailsConatiner.children.financialYear",
            "props.value",
            ""
          )
        );
        dispatch(
          handleField(
            "apply",
            "components.div.children.formwizardFirstStep.children.tradeDetails.children.cardContent.children.tradeDetailsConatiner.children.applicationType",
            "props.value",
            "APPLICATIONTYPE.RENEWAL"
          )
        );
      }

      dispatch(prepareFinalObject("Licenses[0].applicationNumber", ""));
      dispatch(
        handleField(
          "apply",
          "components.div.children.headerDiv.children.header.children.applicationNumber",
          "visible",
          false
        )
      );
    }
  }
};

export const formwizardFirstStep = {
  uiFramework: "custom-atoms",
  componentPath: "Form",
  props: {
    id: "apply_form1"
  },
  children: {
    tradeDetails,
    tradeLocationDetails
  }
};

export const formwizardSecondStep = {
  uiFramework: "custom-atoms",
  componentPath: "Form",
  props: {
    id: "apply_form2"
  },
  children: {
    tradeOwnerDetails
  },
  visible: false
};

export const formwizardThirdStep = {
  uiFramework: "custom-atoms",
  componentPath: "Form",
  props: {
    id: "apply_form3"
  },
  children: {
    tradeDocumentDetails
  },
  visible: false
};

export const formwizardFourthStep = {
  uiFramework: "custom-atoms",
  componentPath: "Form",
  props: {
    id: "apply_form4"
  },
  children: {
    tradeReviewDetails
  },
  visible: false
};
const disabledKeyValue = (dispatch, key, value ) => {
  dispatch(
    handleField(
      "apply",
      `components.div.children.formwizardFirstStep.children.tradeDetails.children.cardContent.children.${key}.props.items[0].item0.children.cardContent.children.${key}Container.children.${value}`,
      "props.disabled",
      true
    )
  );
}
const screenConfig = {
  uiFramework: "material-ui",
  name: "apply",
  // hasBeforeInitAsync:true,
  beforeInitScreen: (action, state, dispatch) => {
    // let { isRequiredDocuments } = state.screenConfiguration.preparedFinalObject;
    dispatch(unMountScreen("search"));
    dispatch(unMountScreen("search-preview"));
    const tenantId = getTenantId();
    const URL = window.location.href
    const URLsplit = URL.split("/")
    if (URLsplit[URLsplit.length - 1] == "apply") {
      pageResetAndChange(state, dispatch, tenantId)
    }
    // dispatch(fetchLocalizationLabel(getLocale(), tenantId, tenantId));
    getData(action, state, dispatch).then(responseAction => {
      const queryObj = [{ key: "tenantId", value: tenantId }];
      getBoundaryData(action, state, dispatch, queryObj);
      let props = get(
        action.screenConfig,
        "components.div.children.formwizardFirstStep.children.tradeLocationDetails.children.cardContent.children.tradeDetailsConatiner.children.tradeLocCity.props",
        {}
      );
      props.value = tenantId;
      props.disabled = true;
      set(
        action.screenConfig,
        "components.div.children.formwizardFirstStep.children.tradeLocationDetails.children.cardContent.children.tradeDetailsConatiner.children.tradeLocCity.props",
        props
      );
      dispatch(
        prepareFinalObject(
          "Licenses[0].tradeLicenseDetail.address.city",
          tenantId
        )
      );
      const mohallaLocalePrefix = {
        moduleName: tenantId,
        masterName: "REVENUE"
      };
      set(
        action.screenConfig,
        "components.div.children.formwizardFirstStep.children.tradeLocationDetails.children.cardContent.children.tradeDetailsConatiner.children.tradeLocMohalla.props.localePrefix",
        mohallaLocalePrefix
      );
      //hardcoding license type to permanent
      set(
        action.screenConfig,
        "components.div.children.formwizardFirstStep.children.tradeDetails.children.cardContent.children.tradeDetailsConatiner.children.tradeLicenseType.props.value",
        "PERMANENT"
      );
      const applicationType = get(state.screenConfiguration.preparedFinalObject, 'Licenses[0].applicationType', "");
      if(applicationType === "RENEWAL") {
        dispatch(
          handleField(
            "apply",
            "components.div.children.formwizardFirstStep.children.tradeLocationDetails",
            "props.style",
            {"pointer-events":"none"}
          )
         );
        }
         if(applicationType === "RENEWAL") {
         dispatch(
          handleField(
            "apply",
            "components.div.children.formwizardSecondStep.children.tradeOwnerDetails.children.cardContent.children.ownershipType",
            "props.style",
            {"pointer-events":"none"}
          )
         ); 
      }

      if(getQueryArg(window.location.href, "action") === "edit" || getQueryArg(window.location.href, "action") === "EDITRENEWAL") {
        dispatch(
          handleField(
            "apply",
            "components.div.children.formwizardFirstStep.children.tradeDetails.children.cardContent.children.tradeUnitCard.props.items[0].item0.children.cardContent.children.tradeUnitCardContainer",
            "props.style",
            {"pointer-events":"none"}
          )
        );
      }
      
    });

    return action;
  },

  components: {
    div: {
      uiFramework: "custom-atoms",
      componentPath: "Div",
      props: {
        className: "common-div-css"
      },
      children: {
        headerDiv: {
          uiFramework: "custom-atoms",
          componentPath: "Container",
          children: {
            header: {
              gridDefination: {
                xs: 12,
                sm: 10
              },
              ...header
            }
          }
        },
        stepper,
        formwizardFirstStep,
        formwizardSecondStep,
        formwizardThirdStep,
        formwizardFourthStep,
        footer
      }
    },
    breakUpDialog: {
      uiFramework: "custom-containers-local",
      moduleName: "egov-tradelicence",
      componentPath: "ViewBreakupContainer",
      props: {
        open: false,
        maxWidth: "md",
        screenKey: "apply"
      }
    }
  }
};

export default screenConfig;
