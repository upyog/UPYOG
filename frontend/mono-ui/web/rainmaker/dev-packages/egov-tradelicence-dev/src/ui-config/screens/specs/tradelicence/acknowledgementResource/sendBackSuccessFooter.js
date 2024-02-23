import { getLabel } from "egov-ui-framework/ui-config/screens/specs/utils";
import generateReceipt from "../../utils/receiptPdf";
import { downloadCertificateForm } from "../../utils";
import { getTenantId }  from "egov-ui-kit/utils/localStorageUtils";

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

export const sendBackSuccessFooter = getCommonApplyFooter({

  gotoHome: {
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
      downloadReceiptButtonLabel: getLabel({
        labelName: "GO TO HOME",
        labelKey: "TL_COMMON_BUTTON_HOME"
      })
    },
    onClickDefination: {
      action: "page_change",
      path: `/inbox`
    }
  }
});
