import {
  getCommonGrayCard,
  getCommonSubHeader,
  getCommonContainer,
  getLabel,
  getLabelWithValueForModifiedLabel
} from "egov-ui-framework/ui-config/screens/specs/utils";

 
export const getPaymentDetails = () => {
  return getCommonGrayCard({
      headerDiv: {
          uiFramework: "custom-atoms",
          componentPath: "Container",
          props: {
            style: { marginBottom: "10px" }
          },
          children: {
            header: {
              gridDefination: {
                xs: 12,
                sm: 10
              },
              ...getCommonSubHeader({
                labelKey: "WS_PAYMENT_HISTORY_HEADER"
              })
            }
          }
        },
      paymentHistory:{
      uiFramework: "custom-molecules-local",
      moduleName: "egov-wns",
      componentPath: "PaymentHistory",
      props: {
        className: "payment-history",
        jsonpath: "paymentHistory",
      },
      type: "array"
    } 
      });
};