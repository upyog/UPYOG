import { getLabel } from "egov-ui-framework/ui-config/screens/specs/utils";
import { isPublicSearch } from "egov-ui-framework/ui-utils/commons";
import get from "lodash/get";
import { ifUserRoleExists } from "../../utils";
import './acknowledgementUtils.css';
import { getQueryArg } from "egov-ui-framework/ui-utils/commons";

const getHomeButtonPath = (item) => {
    const consumerCode = getQueryArg(window.location.href, "consumerCode");
    let url = "/withoutAuth/pt-mutation/public-search";
    if(consumerCode.includes("WS") || consumerCode.includes("SW")) {
        url = "/withoutAuth/wns/public-search";
    }
    return isPublicSearch() ? url : (ifUserRoleExists("CITIZEN") ? get(item, "citizenUrl", "/") : get(item, "employeeUrl", "/inbox"));
}
const getBackButtonPath = (item) => {
    const consumerCode = getQueryArg(window.location.href, "consumerCode");
    let url = "/withoutAuth/pt-mutation/public-search";
    if(consumerCode.includes("UC")) {
        url = "/withoutAuth/uc/newCollection";

        return isPublicSearch() ? url : (ifUserRoleExists("CITIZEN") ? get(item, "citizenUrl", "/") : get(item, "employeeUrl", "/uc/newCollection"));

    }
    //return isPublicSearch() ? url : (ifUserRoleExists("CITIZEN") ? get(item, "citizenUrl", "/") : get(item, "employeeUrl", "/inbox"));
}

const getCommonApplyFooter = (children) => {
    return {
        uiFramework: "custom-atoms",
        componentPath: "Div",
        props: {
            className: "apply-wizard-footer common-footer-mobile"
        },
        children
    }
}

const defaultValues = {
    "code": "DEFAULT",
    "headerBandLabel": "PAYMENT_COMMON_CONSUMER_CODE",
    "receiptKey": "property-receipt",
    "billKey": "property-bill",
    "buttons": [
        {
            "label": "COMMON_BUTTON_HOME",
            "citizenUrl": "/",
            "employeeUrl": "/inbox"
        }
    ]
}

export const paymentFooter = (state, consumerCode, tenant, status, businessService, extraData) => {
    const uiCommonPayConfig = get(state.screenConfiguration.preparedFinalObject, "commonPayInfo", defaultValues);
    const buttons = get(uiCommonPayConfig[0], "buttons")?get(uiCommonPayConfig[0], "buttons"):get(uiCommonPayConfig, "buttons");
    const redirectionURL = isPublicSearch() ? "/withoutAuth/egov-common/pay" : "/egov-common/pay";
    const path = `${redirectionURL}?consumerCode=${consumerCode}&tenantId=${tenant}&businessService=${businessService}`;
    
    let gobackPath="/inbox";
    if(consumerCode.includes("UC")){
        gobackPath = isPublicSearch() ? "/withoutAuth/uc/search" : "/uc/search";
    }
    setTimeout(function(){if(extraData!=null){
        if(extraData.payment.paymentDetails[0].businessService=="PT"){
          if (window.appOverrides && window.appOverrides.validateForm)
          {
           window.appOverrides.validateForm("PTReceiptAvailable", {extraData: extraData});
          }
      }
        //-----------add condition for making Tradelicence Mini receipt ----------------//
        
       if(extraData.payment.paymentDetails[0].businessService=="TL"){
          if (window.appOverrides && window.appOverrides.validateForm)
          {
           window.appOverrides.validateForm("TLReceiptAvailable", {extraData: extraData});
          }
      }
        //------------- End Of Condtion For Trade Licence Minireceipt -----------------//
        
      let isUCPayment=extraData.payment.paymentDetails[0].businessService!="PT"
      &&extraData.payment.paymentDetails[0].businessService!="TL"&&
      extraData.payment.paymentDetails[0].businessService!="FIRENOC";
      if(isUCPayment){
        if (window.appOverrides && window.appOverrides.validateForm)
        {
         window.appOverrides.validateForm("UCEmployeeReceiptAvailable", {receipt:extraData.payment });
        } 
      }
      };
    },2000);
    // gotoHome: {
    //     componentPath: "Button",
    //     props: {
    //         variant: "contained",
    //         color: "primary",
    //         className:"common-footer-mobile",
    //         style: {
    //             minWidth: "200px",
    //             height: "48px",
    //             marginRight: "16px",
    //             marginLeft: "40px"
    //         }
    //     },
    //     children: {
    //         downloadReceiptButtonLabel: getLabel({
    //             labelKey : label
    //         //    ...footer.label,
    //             //  labelName: get(footer,"label.labelName","GO TO HOME"),
    //             //  labelKey: get(footer,"label.labelKey","GO_TO_HOME")
    //         })
    //     },
    //     onClickDefination: {
    //         action: "page_change",
    //         path: get(footer,"link", `/inbox`)
    //     },
    // },



    const footer = buttons && buttons.map((item, index) => {
        return {
            componentPath: "Button",
            props: {
                variant: "contained",
                color: "primary",
                className: "common-footer-mobile",
                style: {
                    minWidth: "200px",
                    height: "48px",
                    marginRight: "16px",
                    marginLeft: "40px"
                }
            },
            children: {
                downloadReceiptButtonLabel: getLabel({
                    labelKey: get(item, "label", "GO_TO_HOME")
                })
            },
            onClickDefination: {
                action: "page_change",
                path: getHomeButtonPath(item)
            },
        }
    })

    // const goBack = buttons && buttons.map((item, index) => {
    //     return {
    //         componentPath: "Button",
    //         props: {
    //             variant: "contained",
    //             color: "primary",
    //             className: "common-footer-mobile",
    //             style: {
    //                 minWidth: "200px",
    //                 height: "48px",
    //                 marginRight: "16px",
    //                 marginLeft: "40px"
    //             }
    //         },
    //         children: {
    //             downloadReceiptButtonLabel: getLabel({
    //                 labelKey: get(item, "label", "GO Back")
    //             })
    //         },
    //         onClickDefination: {
    //             action: "page_change",
    //             path: getBackButtonPath(item)
    //         },
    //     }
    // })
    return getCommonApplyFooter({
        ...footer,
        goBackButton: {
            componentPath: "Button",
            props: {
                variant: "contained",
                color: "primary",
                className: "common-footer-mobile",
                style: {
                    minWidth: "200px",
                    height: "48px",
                    marginRight: "16px",
                    marginLeft: "40px",
                }
            },
            children: {
                downloadReceiptButtonLabel: getLabel({
                    labelName: "Go Back",
                    labelKey: "Go Back"
                })
            },
            onClickDefination: {
                action: "page_change",
                path : gobackPath
            }
        },
        retryButton: {
            componentPath: "Button",
            props: {
                variant: "contained",
                color: "primary",
                className: "common-footer-mobile",
                style: {
                    minWidth: "200px",
                    height: "48px",
                    marginRight: "16px",
                    marginLeft: "40px",
                }
            },
            children: {
                downloadReceiptButtonLabel: getLabel({
                    labelName: "RETRY",
                    labelKey: "COMMON_RETRY"
                })
            },
            onClickDefination: {
                action: "page_change",
                path
            },
            visible: status === "failure" ? true : false
        }
    });
};
