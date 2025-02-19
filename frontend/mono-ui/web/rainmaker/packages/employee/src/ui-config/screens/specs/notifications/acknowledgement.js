import { getCommonHeader, getLabel } from "egov-ui-framework/ui-config/screens/specs/utils";
import acknowledgementCard from "../utils/acknowledgementUtils";
import { getQueryArg } from "egov-ui-framework/ui-utils/commons";
import set from "lodash/set";

const goToHomeFooter = () => {
  return {
    uiFramework: "custom-atoms",
    componentPath: "Div",
    props: {
      className: "apply-wizard-footer",
    },
    children: {
      gotoHome: {
        componentPath: "Button",
        props: {
          variant: "outlined",
          color: "primary",
          style: {
            minWidth: "290px",
            height: "48px",
            marginRight: "16px",
          },
        },
        children: {
          downloadReceiptButtonLabel: getLabel({
            labelName: "GO TO HOME",
            labelKey: "TL_COMMON_BUTTON_HOME",
          }),
        },
        onClickDefination: {
          action: "page_change",
          path: "/",
        },
      },
    },
  };
};
const getAcknowledgementCard = (state, dispatch, purpose, status, applicationNumber, secondNumber, financialYear, tenant) => {
  if (purpose === "apply" && status === "success") {
    return {
      header: getCommonHeader({
        labelName: "Add New Public Message",
        labelKey: "ADD_NEW_PUBLIC_MESSAGE",
      }),
      applicationSuccessCard: {
        uiFramework: "custom-atoms",
        componentPath: "Div",
        children: {
          card: acknowledgementCard({
            icon: "done",
            backgroundColor: "#39CB74",
            header: {
              labelName: "Message published Successfully",
              labelKey: "MESSAGE_ADD_SUCCESS_MESSAGE_MAIN",
            },
            body: {
              labelName: "",
              labelKey: "MESSAGE_ADD_SUCCESS_MESSAGE_SUB",
            },
            number: applicationNumber,
          }),
        },
      },
      iframeForPdf: {
        uiFramework: "custom-atoms",
        componentPath: "Div",
      },
      footer: goToHomeFooter(),
    };
  } else if (purpose === "update" && status === "success") {
    return {
      header: getCommonHeader({
        labelName: "pdate UPublic Message",
        labelKey: "UPDATE_PUBLIC_MESSAGE",
      }),
      applicationSuccessCard: {
        uiFramework: "custom-atoms",
        componentPath: "Div",
        children: {
          card: acknowledgementCard({
            icon: "done",
            backgroundColor: "#39CB74",
            header: {
              labelName: "Message updated Successfully",
              labelKey: "MESSAGE_UPDATE_SUCCESS_MESSAGE_MAIN",
            },
            body: {
              labelName: "A notification regarding this message has been sent to citizen at registered Mobile No.",
              labelKey: "MESSAGE_ADD_SUCCESS_MESSAGE_SUB",
            },
            number: applicationNumber,
          }),
        },
      },
      iframeForPdf: {
        uiFramework: "custom-atoms",
        componentPath: "Div",
      },
      footer: goToHomeFooter(),
    };
  } else if (purpose === "delete" && status === "success") {
    return {
      header: getCommonHeader({
        labelName: "Delete Public Message",
        labelKey: "DELETE_PUBLIC_MESSAGE",
      }),
      applicationSuccessCard: {
        uiFramework: "custom-atoms",
        componentPath: "Div",
        children: {
          card: acknowledgementCard({
            icon: "done",
            backgroundColor: "#39CB74",
            header: {
              labelName: "Message deleted Successfully",
              labelKey: "MESSAGE_DELETE_SUCCESS_MESSAGE_MAIN",
            },
            // body: {
            //   labelName: "A notification regarding this message has been sent to citizen at registered Mobile No.",
            //   labelKey: "MESSAGE_ADD_SUCCESS_MESSAGE_SUB",
            // },
            number: applicationNumber,
          }),
        },
      },
      iframeForPdf: {
        uiFramework: "custom-atoms",
        componentPath: "Div",
      },
      footer: goToHomeFooter(),
    };
  }
};

const screenConfig = {
  uiFramework: "material-ui",
  name: "acknowledgement",
  components: {
    div: {
      uiFramework: "custom-atoms",
      componentPath: "Div",
      props: {
        className: "common-div-css",
      },
    },
  },
  beforeInitScreen: (action, state, dispatch) => {
    const purpose = getQueryArg(window.location.href, "purpose");
    const status = getQueryArg(window.location.href, "status");
    const financialYear = getQueryArg(window.location.href, "FY");
    const applicationNumber = getQueryArg(window.location.href, "applicationNumber");
    const secondNumber = getQueryArg(window.location.href, "secondNumber");
    const tenant = getQueryArg(window.location.href, "tenantId");
    const data = getAcknowledgementCard(state, dispatch, purpose, status, applicationNumber, secondNumber, financialYear, tenant);
    set(action, "screenConfig.components.div.children", data);
    return action;
  },
};

export default screenConfig;
