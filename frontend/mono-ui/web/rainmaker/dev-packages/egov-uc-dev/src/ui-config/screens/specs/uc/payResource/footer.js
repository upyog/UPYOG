import { getLabel } from "egov-ui-framework/ui-config/screens/specs/utils";
import { ifUserRoleExists } from "../../utils";
import { getSelectedTabIndex } from "egov-ui-framework/ui-utils/commons";
import cloneDeep from "lodash/cloneDeep";
import { httpRequest } from "egov-ui-framework/ui-utils/api";
import { convertDateToEpoch, validateFields } from "../../utils";
import { toggleSpinner } from "egov-ui-framework/ui-redux/screen-configuration/actions";
import {
  toggleSnackbar,
  prepareFinalObject
} from "egov-ui-framework/ui-redux/screen-configuration/actions";
import { setRoute } from "egov-ui-framework/ui-redux/app/actions";
import get from "lodash/get";
import set from "lodash/set";

const getCommonApplyFooter = children => {
  return {
    uiFramework: "custom-atoms",
    componentPath: "Div",
    props: {
      className: "apply-wizard-footer"
    },
    children
  };
};
export const getRedirectionURL = receiptNumber => {
  const redirectionURL = ifUserRoleExists("EMPLOYEE")
    ? `/uc/acknowledgement?purpose=pay&status=success&receeiptNumber=${receiptNumber}`
    : "/inbox";

  return redirectionURL;
};
export const footer = getCommonApplyFooter({
  previousButton: {
    componentPath: "Button",
    props: {
      variant: "outlined",
      color: "primary",
      style: {
        minWidth: "200px",
        height: "48px",
        marginRight: "16px"
      }
    },
    children: {
      previousButtonIcon: {
        uiFramework: "custom-atoms",
        componentPath: "Icon",
        props: {
          iconName: "keyboard_arrow_left"
        }
      },
      previousButtonLabel: getLabel({
        labelName: "GO BACK",
        labelKey: "UC_PAY_GO_BACK"
      })
    },
    onClickDefination: {
      action: "condition",
      callBack: (state, dispatch) => {
        goBack(state, dispatch);
      }
    }
  },
  nextButton: {
    componentPath: "Button",
    props: {
      variant: "contained",
      color: "primary",
      style: {
        minWidth: "200px",
        height: "48px",
        marginRight: "16px"
      }
    },
    children: {
      downloadReceiptButtonLabel: getLabel({
        labelName: "GENERATE RECEIPT",
        labelKey: "UC_BUTTON_GENERATE_RECEIPT"
      }),
      nextButtonIcon: {
        uiFramework: "custom-atoms",
        componentPath: "Icon",
        props: {
          iconName: "keyboard_arrow_right"
        }
      }
    },
    onClickDefination: {
      action: "condition",
      callBack: (state, dispatch) => {
        callBackForPay(state, dispatch);
      }
    }
  }
});

const callBackForPay = async (state, dispatch) => {
  const { href } = window.location;
  let isFormValid = true;

  // --- Validation related -----//

  const selectedPaymentType = get(
    state.screenConfiguration.preparedFinalObject,
    "ReceiptTemp[0].instrument.instrumentType.name"
  );
  const {
    selectedTabIndex,
    selectedPaymentMode,
    fieldsToValidate
  } = getSelectedTabIndex(selectedPaymentType);

  if (selectedPaymentType === "Cheque" || selectedPaymentType === "DD") {
    const bankName = get(
      state.screenConfiguration.preparedFinalObject,
      "ReceiptTemp[0].instrument.bank.name",
      null
    );
    const branchName = get(
      state.screenConfiguration.preparedFinalObject,
      "ReceiptTemp[0].instrument.branchName",
      null
    );
    if (!bankName && !branchName) {
      dispatch(prepareFinalObject("ReceiptTemp[0].instrument.ifscCode", null));
    }
  }

  isFormValid =
    fieldsToValidate
      .map(curr => {
        return validateFields(
          `components.div.children.body.children.cardContent.children.capturePayment.children.cardContent.children.tabSection.props.tabs[${selectedTabIndex}].tabContent.${selectedPaymentMode}.children.${curr}.children`,
          state,
          dispatch,
          "pay"
        );
      })
      .indexOf(false) === -1;
  if (
    get(
      state.screenConfiguration.preparedFinalObject,
      "Bill[0].billDetails[0].manualReceiptDate"
    )
  ) {
    isFormValid = validateFields(
      `components.div.children.body.children.cardContent.children.G8ReceiptDetails.children.cardContent.children.header.children`,
      state,
      dispatch,
      "pay"
    );
  }

  //------------ Validation End -------------//

  //------------- Form related ----------------//

  const ReceiptDataTemp = get(
    state.screenConfiguration.preparedFinalObject,
    "ReceiptTemp[0]"
  );
  let finalReceiptData = cloneDeep(ReceiptDataTemp);

  allDateToEpoch(finalReceiptData, [
    "Bill[0].billDetails[0].manualReceiptDate",
    "instrument.transactionDateInput"
  ]);
  if (get(finalReceiptData, "instrument.transactionDateInput")) {
    set(
      finalReceiptData,
      "instrument.instrumentDate",
      get(finalReceiptData, "instrument.transactionDateInput")
    );
  }

  //Add payerName and Mobile no
  set(
    finalReceiptData,
    "Bill[0].payerName",
    get(
      state.screenConfiguration,
      "preparedFinalObject.Demands[0].consumerName",
      ""
    )
  );
  set(
    finalReceiptData,
    "Bill[0].mobileNumber",
    get(
      state.screenConfiguration,
      "preparedFinalObject.Demands[0].mobileNumber",
      ""
    )
  );

  //Add comments
  set(
    finalReceiptData,
    "Bill[0].billDetails[0].additionalDetails.comment",
    get(
      state.screenConfiguration,
      "preparedFinalObject.Demands[0].additionalDetails.comment",
      ""
    )
  );

  if (get(finalReceiptData, "instrument.transactionNumber")) {
    set(
      finalReceiptData,
      "instrument.instrumentNumber",
      get(finalReceiptData, "instrument.transactionNumber")
    );
  }

  if (selectedPaymentType === "Card") {
    //Extra check - remove once clearing forms onTabChange is fixed
    if (
      get(finalReceiptData, "instrument.transactionNumber") !==
      get(finalReceiptData, "instrument.transactionNumberConfirm")
    ) {
      dispatch(
        toggleSnackbar(
          true,
          {
            labelName: "Transaction numbers don't match !",
            labelKey: "ERR_TRASACTION_NUMBERS_DONT_MATCH"
          },
          "error"
        )
      );
      return;
    }
  }

  //------------- Form End ----------------//

  let ReceiptBody = {
    Receipt: []
  };

  ReceiptBody.Receipt.push(finalReceiptData);

  //---------------- Create Receipt ------------------//
  if (isFormValid) {
    try {
      dispatch(toggleSpinner());
      let response = await httpRequest(
        "post",
        "collection-services/receipts/_create",
        "_create",
        [],
        ReceiptBody,
        [],
        {}
      );
      let receiptNumber = get(
        response,
        "Receipt[0].Bill[0].billDetails[0].receiptNumber",
        null
      );
      let serviceCategory = get(
        response,
        "Receipt[0].Bill[0].billDetails[0].businessService",
        ""
      );
      dispatch(prepareFinalObject("receiptSearchResponse", response));
      // moveToSuccess(href, dispatch, receiptNumber);
      dispatch(
        setRoute(
          `/uc/acknowledgement?purpose=pay&status=success&receiptNumber=${receiptNumber}&serviceCategory=${serviceCategory}`
        )
      );
      dispatch(toggleSpinner());
    } catch (e) {
      dispatch(toggleSpinner());
      dispatch(toggleSnackbar(true, { labelName: e.message }, "error"));
    }
  } else {
    dispatch(
      toggleSnackbar(
        true,
        {
          labelName:
            "Please fill all mandatory fields and upload the documents !",
          labelKey: "ERR_FILL_MANDATORY_FIELDS"
        },
        "warning"
      )
    );
  }
};

const allDateToEpoch = (finalObj, jsonPaths) => {
  jsonPaths.forEach(jsonPath => {
    if (get(finalObj, jsonPath)) {
      convertDateFieldToEpoch(finalObj, jsonPath);
    }
  });
};

const convertDateFieldToEpoch = (finalObj, jsonPath) => {
  const dateConvertedToEpoch = convertDateToEpoch(
    get(finalObj, jsonPath),
    "daystart"
  );
  set(finalObj, jsonPath, dateConvertedToEpoch);
};

const goBack = (state, dispatch) => {
  const demand = get(
    state.screenConfiguration.preparedFinalObject,
    "Demands[0]",
    []
  );
  const demandId = demand.id || null;
  if (demandId) {
    const serviceType = get(demand, "serviceType");
    const serviceCategory = get(demand, "businessService");
   
  }
  dispatch(setRoute(`/uc/newCollection`));
};
