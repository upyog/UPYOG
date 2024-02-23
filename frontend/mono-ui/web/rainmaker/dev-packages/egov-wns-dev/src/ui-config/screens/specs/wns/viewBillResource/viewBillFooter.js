import { getLabel } from "egov-ui-framework/ui-config/screens/specs/utils";
import { getCommonApplyFooter } from "../../utils";
import { downloadBill } from "../../../../../ui-utils/commons";
import "./index.css";
import { getQueryArg } from "egov-ui-framework/ui-utils/commons";

const getRedirectionWSURL = async (state, dispatch) => {
  const tenantId = getQueryArg(window.location.href, "tenantId");
  const connectionNo = getQueryArg(window.location.href, "connectionNumber");
  const businessService = getQueryArg(window.location.href, "service")==="WATER" ? "WS" : "SW";
  const environment = process.env.NODE_ENV === "production" ? process.env.REACT_APP_NAME === "Citizen" ? "citizen" : "employee" : "";
  const origin =  process.env.NODE_ENV === "production" ? window.location.origin + "/" : window.location.origin;
  window.location.assign(`${origin}${environment}/egov-common/pay?consumerCode=${connectionNo}&tenantId=${tenantId}&businessService=${businessService}`);
};
const callDownloadBill = ( mode) => {
  const tenantId = getQueryArg(window.location.href, "tenantId");
  const businessService = getQueryArg(window.location.href, "service")==="WATER" ? "WS" : "SW";

  const val = [
    {
      key: 'consumerCode',
      value: getQueryArg(window.location.href, "connectionNumber")
    },
    { key: 'tenantId', value: tenantId },
    {
      key: "businessService", value: businessService
    }
  ]
  downloadBill(val, mode);
}


export const viewBillFooter = getCommonApplyFooter("BOTTOM",{
  // downloadButton: {
  //   componentPath: "Button",
  //   props: {
  //     variant: "outlined",
  //     color: "primary",
  //     style: {
  //       minWidth: "200px",
  //       height: "48px",
  //       marginRight: "16px"
  //     }
  //   },
  //   children: {
  //     downloadButton: getLabel({
  //       labelKey: "WS_COMMON_DOWNLOAD_BILL"
  //     })
  //   },
  //   onClickDefination: {
  //     action: "condition",
  //     callBack: (state, dispatch) => {
  //       callDownloadBill( "download");
  //     }
  //   },
  // },
  payButton: {
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
      payButtonLabel: getLabel({
        labelKey: "WS_COMMON_PAY"
      })
    },
    onClickDefination: {
      action: "condition",
      callBack: getRedirectionWSURL
    }
  }
});