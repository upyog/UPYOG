


import { getTextField,getPattern,getSelectField } from "egov-ui-framework/ui-config/screens/specs/utils";
import get from 'lodash/get';
import {
  handleScreenConfigurationFieldChange as handleField,
  prepareFinalObject
} from "egov-ui-framework/ui-redux/screen-configuration/actions";

//import { getTenantId } from "../../utils/localStorageUtils";
export const WSledgerId = {
    ledgerId: getTextField({
        label: { labelKey: "WS_SERV_DETAIL_LEDGER_ID" },
        placeholder: { labelKey: "WS_SERV_DETAIL_LEDGER_ID_PLACEHOLDER" },
        gridDefination: { xs: 12, sm: 6 },
        required: true,
        jsonPath: "applyScreen.additionalDetails.ledgerId",
        // pattern: /^[0-9]*$/i,
        errorMessage: "ERR_DEFAULT_INPUT_FIELD_MSG",
  }),
};

export const WSBillingAmount = {
    billingAmount: getTextField({
        label: { labelKey: "WS_SERV_DETAIL_BILLING_AMOUNT" },
        placeholder: { labelKey: "WS_SERV_DETAIL_BILLING_AMOUNT_PLACEHOLDER" },
        gridDefination: { xs: 12, sm: 6 },
        jsonPath: "applyScreen.additionalDetails.billingAmount",
        pattern: /^[0-9]*$/i,
        required: true,
        errorMessage: "ERR_DEFAULT_INPUT_FIELD_MSG",
  }),
};
export const WSunitUsageType = {
  unitUsageType: {
      ...getSelectField({
        label: { labelKey: "WS_SERV_DETAIL_UNIT_USAGE_TYPE" },
        placeholder: { labelKey: "WS_SERV_DETAIL_UNIT_USAGE_TYPE_PLACEHOLDER" },
        required: true,
        sourceJsonPath: "unitUsageTypeMdmsData.ws-services-masters.unitUsageType",
        gridDefination: { xs: 12, sm: 6 },
        errorMessage: "ERR_INVALID_BILLING_PERIOD",
        jsonPath: "applyScreen.additionalDetails.unitUsageType",
        props: {
          disabled: false,
        }
      }),
    }
};
export const WSbillingType = {
  billingType: {
    ...getSelectField({
      label: { labelKey: "WS_SERV_DETAIL_BILLING_TYPE" },
      placeholder: { labelKey: "WS_SERV_DETAIL_BILING_TYPE_PLACEHOLDER" },
      required: true,
      sourceJsonPath: "applyScreenMdmsData.ws-services-masters.billingType",
      gridDefination: { xs: 12, sm: 6 },
      errorMessage: "ERR_INVALID_BILLING_PERIOD",
      jsonPath: "applyScreen.additionalDetails.billingType",
      props: {
        disabled: false
      }
    }),
    afterFieldChange: async (action, state, dispatch) => {
      let billingType = await get(state, "screenConfiguration.preparedFinalObject.applyScreen.additionalDetails.billingType");
      let connType = await get(state, "screenConfiguration.preparedFinalObject.applyScreen.connectionType");

      console.log('billingType');
      console.log(billingType);
      if (billingType != "CUSTOM") {
        dispatch(
          handleField(
            "apply",
            "components.div.children.formwizardThirdStep.children.additionDetails.children.cardContent.children.connectiondetailscontainer.children.cardContent.children.connectionDetails.children.billingAmount",
            "visible",
            false
          )
        );
      }
      else {
        dispatch(
          handleField(
            "apply",
            "components.div.children.formwizardThirdStep.children.additionDetails.children.cardContent.children.connectiondetailscontainer.children.cardContent.children.connectionDetails.children.billingAmount",
            "visible",
            true
          )
        );
      }

    }
  },
  
};
export const WSsubUsageType = {
  subUsageType: {
      ...getSelectField({
        label: { labelKey: "WS_SERV_DETAIL_SUB_USAGE_TYPE" },
        placeholder: { labelKey: "WS_SERV_DETAIL_SUB_USAGE_TYPE_PLACEHOLDER" },
        required: true,
        sourceJsonPath: "applyScreenMdmsData.ws-services-masters.subUsageType",
        gridDefination: { xs: 12, sm: 6 },
        errorMessage: "ERR_INVALID_BILLING_PERIOD",
        jsonPath: "applyScreen.additionalDetails.waterSubUsageType",
        props: {
          disabled: false
          
        }
      }),
    }
};
export const WScompositionFee = {
  compositionFee: getTextField({
    label: {
      labelKey: "WS_ADDN_DETAILS_COMPOSITION_LABEL"
    },
    placeholder: {
      labelKey: "WS_ADDN_DETAILS_COMPOSITION_PLACEHOLDER"
    },
    gridDefination: {
      xs: 12,
      sm: 6
    },
    required: false,
    pattern: getPattern("Amount"),
    errorMessage: "ERR_DEFAULT_INPUT_FIELD_MSG",
    jsonPath: "applyScreen.additionalDetails.compositionFee"
}),
userCharges: getTextField({
  label: {
    labelKey: "WS_ADDN_USER_CHARGES_LABEL"
  },
  placeholder: {
    labelKey: "WS_ADDN_USER_CHARGES_PLACEHOLDER"
  },
  gridDefination: {
    xs: 12,
    sm: 6
  },
  required: true,
  pattern: getPattern("Amount"),
  errorMessage: "ERR_DEFAULT_INPUT_FIELD_MSG",
  jsonPath: "applyScreen.additionalDetails.userCharges"
}),
othersFee : getTextField({
  label: {
    labelKey: "WS_ADDN_OTHER_FEE_LABEL"
  },
  placeholder: {
    labelKey: "WS_ADDN_OTHER_FEE_PLACEHOLDER"
  },
  gridDefination: {
    xs: 12,
    sm: 6
  },
  required: false,
  pattern: getPattern("Amount"),
  errorMessage: "ERR_DEFAULT_INPUT_FIELD_MSG",
  jsonPath: "applyScreen.additionalDetails.othersFee"
}),

// ConnectionFee : getTextField({
//   label: {
//     labelName: "Connection Fees",
//     labelKey: "Connection Fees"
//   },
//   placeholder: {
//     labelName: "Connection Fees",
//     labelKey: "Connection Fees"
//   },
//   gridDefination: {
//     xs: 12,
//     sm: 6
//   },
//   required: false,
//   //visible:  getTenantId() == pb.bassipathana ? true : false,
//   pattern: getPattern("Amount"),
//   errorMessage: "ERR_DEFAULT_INPUT_FIELD_MSG",
//   jsonPath: "applyScreen.additionalDetails.othersFee"
// }),
};
export const WSMeterMakes = {
  meterMake: getTextField({
    label: {
      labelKey: "WS_ADDN_DETAILS_INITIAL_METER_MAKE"
    },
    placeholder: {
      labelKey: "WS_ADDN_DETAILS_INITIAL_METER_MAKE_PLACEHOLDER"
    },
    gridDefination: {
      xs: 12,
      sm: 6
    },
    required: true,
    //pattern: /^[0-9]\d{0,9}(\.\d{1,3})?%?$/,
    pattern: /^[a-zA-Z0-9_.-]*$/,
    errorMessage: "ERR_DEFAULT_INPUT_FIELD_MSG",
    jsonPath: "applyScreen.additionalDetails.meterMake"
  }),
  averageMake: getTextField({
    label: {
      labelKey: "WS_ADDN_DETAILS_INITIAL_AVERAGE_MAKE"
    },
    placeholder: {
      labelKey: "WS_ADDN_DETAILS_INITIAL_AVERAGE_MAKE_PLACEHOLDER"
    },
    gridDefination: {
      xs: 12,
      sm: 6
    },
    required: false,
    pattern: /^[0-9]\d{0,9}(\.\d{1,3})?%?$/,
    errorMessage: "ERR_DEFAULT_INPUT_FIELD_MSG",
    jsonPath: "applyScreen.additionalDetails.avarageMeterReading"
  })
};
