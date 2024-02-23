import {
  prepareFinalObject,
  handleScreenConfigurationFieldChange as handleField
} from "egov-ui-framework/ui-redux/screen-configuration/actions";
import {
  updatePFOforSearchResults,
  getBoundaryData
} from "../../../../ui-utils/commons";
import get from "lodash/get";
import { footer } from "../tradelicence/applyResource/footer";
import { getQueryArg } from "egov-ui-framework/ui-utils/commons";
import {
  header,
  formwizardFirstStep,
  formwizardSecondStep,
  formwizardThirdStep,
  formwizardFourthStep,
  stepper,
  getMdmsData
} from "../tradelicence/apply";
import { getAllDataFromBillingSlab } from "../utils";
import { fetchLocalizationLabel } from "egov-ui-kit/redux/app/actions";
import { getLocale } from "egov-ui-kit/utils/localStorageUtils";

const getData = async (action, state, dispatch, tenantId) => {
  await getMdmsData(action, state, dispatch);
  await getAllDataFromBillingSlab(tenantId, dispatch);
  await getBoundaryData(action, state, dispatch, [
    { key: "tenantId", value: tenantId }
  ]);
  dispatch(
    prepareFinalObject(
      "Licenses[0].tradeLicenseDetail.address.tenantId",
      tenantId
    )
  );
  dispatch(
    prepareFinalObject("Licenses[0].tradeLicenseDetail.address.city", tenantId)
  );
};
const updateSearchResults = async (
  action,
  state,
  dispatch,
  queryValue,
  tenantId
) => {
  await getData(action, state, dispatch, tenantId);
  await updatePFOforSearchResults(
    action,
    state,
    dispatch,
    queryValue,
    tenantId
  );
  const queryValueFromUrl = getQueryArg(
    window.location.href,
    "applicationNumber"
  );
  dispatch(prepareFinalObject("Licenses[0].tradeLicenseDetail.adhocPenalty", null));
  dispatch(prepareFinalObject("Licenses[0].tradeLicenseDetail.adhocExemption", null));
  dispatch(prepareFinalObject("Licenses[0].tradeLicenseDetail.adhocPenaltyReason", null));
  dispatch(prepareFinalObject("Licenses[0].tradeLicenseDetail.adhocExemptionReason", null));
  if (!queryValueFromUrl) {
    dispatch(
      prepareFinalObject(
        "Licenses[0].oldLicenseNumber",
        get(
          state.screenConfiguration.preparedFinalObject,
          "Licenses[0].applicationNumber",
          ""
        )
      )
    );
  
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
  beforeInitScreen: (action, state, dispatch) => {
    function getCurrentURL () {
      return window.location.href
    }

    // Example
    // const urlmseva = getCurrentURL();
    // let urldata = urlmseva.split("https://mseva.lgpunjab.gov.in/citizen/tradelicense-citizen/apply?");

    // if(urlmseva){

    // }
    // alert(url);
    const queryValue = getQueryArg(window.location.href, "applicationNumber");
    const tenantId = getQueryArg(window.location.href, "tenantId");
    const applicationNo = queryValue
      ? queryValue
      : get(
          state.screenConfiguration.preparedFinalObject,
          "Licenses[0].oldLicenseNumber",
          null
        );
    if (applicationNo) {
      updateSearchResults(action, state, dispatch, applicationNo, tenantId);
    } else {
      getData(action, state, dispatch, tenantId);
    }
    dispatch(fetchLocalizationLabel(getLocale(), tenantId, tenantId));
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
