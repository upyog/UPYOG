import { getLabel } from "egov-ui-framework/ui-config/screens/specs/utils";
import { getCommonApplyFooter } from "../../utils";
import {
  handleScreenConfigurationFieldChange as handleField,
  prepareFinalObject,
  toggleSnackbar,
  toggleSpinner,
} from "egov-ui-framework/ui-redux/screen-configuration/actions";
import { wsDownloadConnectionDetails } from "../../../../../ui-utils/commons";
import { getQueryArg } from "egov-ui-framework/ui-utils/commons";
import "./index.css";
import jp from "jsonpath";
import get from "lodash/get";
import set from "lodash/set";
import store from "ui-redux/store";
import axios from "axios";
import { httpRequest } from "egov-ui-framework/ui-utils/api";
import {
  getFileUrlFromAPI,
  getTransformedLocale,
} from "egov-ui-framework/ui-utils/commons";
import { downloadPdf, openPdf, printPdf } from "egov-ui-kit/utils/commons";
import { downloadReceiptFromFilestoreID } from "egov-common/ui-utils/commons";
import { convertEpochToDate } from "egov-ui-framework/ui-config/screens/specs/utils";

const callDownload = (mode) => {
  const val = [
    {
      key: "connectionNumber",
      value: getQueryArg(window.location.href, "connectionNumber"),
    },

    { key: "tenantId", value: getQueryArg(window.location.href, "tenantId") },
  ];
  wsDownloadConnectionDetails(val, mode);
};

const PAYMENTSEARCH = {
  GET: {
    URL: "/collection-services/payments/",
    ACTION: "_search",
  },
};
const getPaymentSearchAPI = (businessService = "") => {
  if (businessService == "-1") {
    return `${PAYMENTSEARCH.GET.URL}${PAYMENTSEARCH.GET.ACTION}`;
  } else if (process.env.REACT_APP_NAME === "Citizen") {
    return `${PAYMENTSEARCH.GET.URL}${PAYMENTSEARCH.GET.ACTION}`;
  }
  return `${PAYMENTSEARCH.GET.URL}${businessService}/${PAYMENTSEARCH.GET.ACTION}`;
};
const download = async (mode = "download", state, showConfirmation = false) => {
  let configKey = "ws-onetime-receipt";
  const FETCHRECEIPT = {
    GET: {
      URL: "/collection-services/payments/_search",
      ACTION: "_get",
    },
  };
  const DOWNLOADRECEIPT = {
    GET: {
      URL: "/pdf-service/v1/_create",
      ACTION: "_get",
    },
  };
  let consumerCode = getQueryArg(window.location.href, "connectionNumber");
  let tenantId = getQueryArg(window.location.href, "tenantId");
  let applicationNumber = getQueryArg(
    window.location.href,
    "applicationNumber"
  );
  let queryObject = [
    { key: "tenantId", value: tenantId },
    {
      key: "applicationNumber",
      value: consumerCode ? consumerCode : applicationNumber,
    },
  ];
  const queryObjectForConn = [
    { key: "connectionNumber", value: consumerCode },
    { key: "tenantId", value: tenantId },
  ];
  const responseSewerage = await httpRequest(
    "post",
    "/sw-services/swc/_search",
    "_search",
    queryObjectForConn
  );

  const responseWater = await httpRequest(
    "post",
    "/ws-services/wc/_search",
    "_search",
    queryObjectForConn
  );
  let businessService = "";
  let service = getQueryArg(window.location.href, "service");
  if (service == "WATER") {
    businessService = "WS.ONE_TIME_FEE";
  } else {
    businessService = "SW.ONE_TIME_FEE";
  }

  const receiptQueryString = [
    {
      key: "consumerCodes",
      value:
        service == "WATER"
          ? responseWater.WaterConnection[0].applicationNo
          : responseSewerage.SewerageConnections[0].applicationNo,
    },
    {
      key: "tenantId",
      value: tenantId,
    },
    {
      key: "businessService",
      value: businessService,
    },
  ];
  try {
    await httpRequest(
      "post",
      getPaymentSearchAPI(businessService),
      FETCHRECEIPT.GET.ACTION,
      receiptQueryString
    ).then((payloadReceiptDetails) => {
      if (payloadReceiptDetails.Payments[0].payerName != null) {
        payloadReceiptDetails.Payments[0].payerName =
          payloadReceiptDetails.Payments[0].payerName.trim();
      } else if (
        payloadReceiptDetails.Payments[0].payerName == null &&
        payloadReceiptDetails.Payments[0].paymentDetails[0].businessService ==
          "FIRENOC" &&
        payloadReceiptDetails.Payments[0].paidBy != null
      ) {
        payloadReceiptDetails.Payments[0].payerName =
          payloadReceiptDetails.Payments[0].paidBy.trim();
      }
      if (payloadReceiptDetails.Payments[0].paidBy != null) {
        payloadReceiptDetails.Payments[0].paidBy =
          payloadReceiptDetails.Payments[0].paidBy.trim();
      }

      if (
        payloadReceiptDetails.Payments[0].paymentDetails[0].businessService ==
          "WS.ONE_TIME_FEE" ||
        payloadReceiptDetails.Payments[0].paymentDetails[0].businessService ==
          "SW.ONE_TIME_FEE"
      ) {
        let dcbRow = null,
          dcbArray = [];
        let installment,
          totalamount = 0;
        payloadReceiptDetails.Payments[0].paymentDetails[0].bill.billDetails.map(
          (element, index) => {
            if (element.amountPaid > 0 || element.amountPaid < 0) {
              installment =
                convertEpochToDate(element.fromPeriod) +
                "-" +
                convertEpochToDate(element.toPeriod);
              element.billAccountDetails.map((dd) => {
                if (
                  dd.adjustedAmount > 0 ||
                  dd.adjustedAmount < 0 ||
                  dd.amount < 0
                ) {
                  totalamount = totalamount;
                  dcbArray.push(dcbRow);
                }
              });
            }
          }
        );
        dcbRow = {
          taxhead: "Total Amount Paid",
          amount: totalamount,
        };
        dcbArray.push(dcbRow);
        payloadReceiptDetails.Payments[0].paymentDetails[0].additionalDetails =
          dcbArray;
      }

      const queryStr = [
        { key: "key", value: configKey },
        { key: "tenantId", value: receiptQueryString[1].value.split(".")[0] },
      ];
      if (
        payloadReceiptDetails &&
        payloadReceiptDetails.Payments &&
        payloadReceiptDetails.Payments.length == 0
      ) {
        console.log("Could not find any receipts");
        store.dispatch(
          toggleSnackbar(
            true,
            {
              labelName: "Receipt not Found",
              labelKey: "ERR_RECEIPT_NOT_FOUND",
            },
            "error"
          )
        );
        return;
      }
      // Setting the Payer and mobile from Bill to reflect it in PDF

      if (payloadReceiptDetails.Payments[0].paymentMode == "CASH") {
        payloadReceiptDetails.Payments[0].instrumentDate = null;
        payloadReceiptDetails.Payments[0].instrumentNumber = null;
      }
      if (
        !payloadReceiptDetails.Payments[0].payerName &&
        process.env.REACT_APP_NAME === "Citizen" &&
        billDetails
      ) {
        payloadReceiptDetails.Payments[0].payerName = billDetails.payerName;
        // payloadReceiptDetails.Payments[0].paidBy = billDetails.payer;
        payloadReceiptDetails.Payments[0].mobileNumber =
          billDetails.mobileNumber;
      }
      if (
        (payloadReceiptDetails.Payments[0].payerName == null ||
          payloadReceiptDetails.Payments[0].mobileNumber == null) &&
        payloadReceiptDetails.Payments[0].paymentDetails[0].businessService ==
          "FIRENOC" &&
        process.env.REACT_APP_NAME === "Citizen"
      ) {
        payloadReceiptDetails.Payments[0].payerName =
          response.FireNOCs[0].fireNOCDetails.applicantDetails.owners[0].name;
        payloadReceiptDetails.Payments[0].mobileNumber =
          response.FireNOCs[0].fireNOCDetails.applicantDetails.owners[0].mobileNumber;
      }
      const oldFileStoreId = get(
        payloadReceiptDetails.Payments[0],
        "fileStoreId"
      );
      if (oldFileStoreId) {
        downloadReceiptFromFilestoreID(
          oldFileStoreId,
          mode,
          undefined,
          showConfirmation
        );
      } else {
        httpRequest(
          "post",
          DOWNLOADRECEIPT.GET.URL,
          DOWNLOADRECEIPT.GET.ACTION,
          queryStr,
          { Payments: payloadReceiptDetails.Payments },
          { Accept: "application/json" },
          { responseType: "arraybuffer" }
        ).then((res) => {
          res.filestoreIds[0];
          if (res && res.filestoreIds && res.filestoreIds.length > 0) {
            res.filestoreIds.map((fileStoreId) => {
              downloadReceiptFromFilestoreID(
                fileStoreId,
                mode,
                undefined,
                showConfirmation
              );
            });
          } else {
            console.log("Some Error Occured while downloading Receipt!");
            store.dispatch(
              toggleSnackbar(
                true,
                {
                  labelName: "Error in Receipt Generation",
                  labelKey: "ERR_IN_GENERATION_RECEIPT",
                },
                "error"
              )
            );
          }
        });
      }
    });
  } catch (exception) {
    console.log("Some Error Occured while downloading Receipt!");
    store.dispatch(
      toggleSnackbar(
        true,
        {
          labelName: "Error in Receipt Generation",
          labelKey: "ERR_IN_GENERATION_RECEIPT",
        },
        "error"
      )
    );
  }
};
let receiptDownloadObject = {
  label: { labelName: "Estimation Receipt", labelKey: "TL_RECEIPT" },
  link: () => {
    download("download", false);
  },
  leftIcon: "receipt",
};
let receiptPrintObject = {
  label: { labelName: "Estimation Receipt", labelKey: "TL_RECEIPT" },
  link: () => {
    download("print", false);
  },
  leftIcon: "receipt",
};
let applicationDownloadObject = {
  label: { labelName: "Application", labelKey: "TL_APPLICATION" },
  link: () => {
    callDownload("download");
  },
  leftIcon: "assignment",
};
let applicationPrintObject = {
  label: { labelName: "Application", labelKey: "TL_APPLICATION" },
  link: () => {
    callDownload("print");
  },
  leftIcon: "assignment",
};
let downloadMenu = [];
let printMenu = [];

downloadMenu = [applicationDownloadObject, receiptDownloadObject];
printMenu = [applicationPrintObject, receiptPrintObject];

export const connectionDetailsDownload = getCommonApplyFooter("RIGHT", {
  // downloadButton: {
  //   componentPath: "Button",
  //   props: {
  //     variant: "outlined",
  //     color: "primary",
  //     style: {
  //       minWidth: "150px",
  //       height: "48px",
  //       marginRight: "10px"
  //     }
  //   },
  //   children: {
  //     downloadButton: getLabel({
  //       labelKey: "WS_COMMON_BUTTON_DOWNLOAD"
  //     })
  //   },
  //   onClickDefination: {
  //     action: "condition",
  //     callBack: (state, dispatch) => {
  //       callDownload("download");
  //     }
  //   },
  // },
  // payButton: {
  //   componentPath: "Button",
  //   props: {
  //     variant: "contained",
  //     color: "primary",
  //     style: {
  //       minWidth: "150px",
  //       height: "48px",
  //       marginRight: "10px"
  //     }
  //   },
  //   children: {
  //     printButton: getLabel({
  //       labelKey: "WS_COMMON_BUTTON_PRINT"
  //     })
  //   },
  //   onClickDefination: {
  //     action: "condition",
  //     callBack: (state, dispatch) => {
  //       callDownload("print");
  //     }
  //   },
  //   // visible: false
  // },
  downloadMenu: {
    uiFramework: "custom-molecules",
    componentPath: "DownloadPrintButton",
    props: {
      style: {
        minWidth: "150px",
        height: "48px",
        marginRight: "10px",
      },
      data: {
        label: { labelName: "DOWNLOAD", labelKey: "TL_DOWNLOAD" },
        leftIcon: "cloud_download",
        rightIcon: "arrow_drop_down",
        props: {
          variant: "outlined",
          style: { height: "60px", color: "#FE7A51", marginRight: "2px" },
          className: "tl-download-button",
        },
        menu: downloadMenu,
      },
    },
  },
  printMenu: {
    uiFramework: "custom-molecules",
    componentPath: "DownloadPrintButton",
    props: {
      data: {
        label: { labelName: "PRINT", labelKey: "TL_PRINT" },
        leftIcon: "print",
        rightIcon: "arrow_drop_down",
        props: {
          variant: "outlined",
          style: { height: "60px", color: "#FE7A51" },
          className: "tl-print-button",
        },
        menu: printMenu,
      },
    },
  },
});
