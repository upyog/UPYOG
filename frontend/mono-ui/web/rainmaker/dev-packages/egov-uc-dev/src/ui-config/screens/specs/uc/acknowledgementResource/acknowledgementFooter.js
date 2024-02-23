import { getLabel } from "egov-ui-framework/ui-config/screens/specs/utils";
import { generateReciept } from "../../utils/recieptPdf";
import { ifUserRoleExists } from "../../utils";
import { setRoute } from "egov-ui-framework/ui-redux/app/actions";
//import { prepareFinalObject,toggleSnackbar } from "egov-ui-framework/ui-redux/screen-configuration/actions";
import { handleScreenConfigurationFieldChange as handleField, prepareFinalObject, toggleSnackbar ,setPaymentDetails} from "egov-ui-framework/ui-redux/screen-configuration/actions";
import { httpRequest } from "egov-ui-framework/ui-utils/api";
import { getCommonPayUrl } from "egov-ui-framework/ui-utils/commons";
import get from "lodash/get";
import { getQueryArg } from "egov-ui-framework/ui-utils/commons";
import { getDateFromEpoch } from "egov-ui-kit/utils/commons";
import "../../../../../index.css";
//import { paybuttonJsonpath } from "./constants";
import { EMPLOYEE } from "egov-ui-kit/utils/endPoints";

const paybuttonJsonpath = "components.div.children.footer.children.";

const checkAmount = (totalAmount, customAmount, businessService) => {
  if (totalAmount !== 0 && customAmount === 0) {
    return true;
  } else if (totalAmount === 0 && customAmount === 0 && (businessService === "WS" || businessService === "SW")) {
    return true;
  } else {
    return false;
  }
}

export const callPGService = async (state, dispatch) => {
  //alert("clicked make payment");
  console.log("clicked make payment");
  const isAdvancePaymentAllowed = get(state, "screenConfiguration.preparedFinalObject.businessServiceInfo.isAdvanceAllowed");
  const tenantId = get(state, "screenConfiguration.preparedFinalObject.ReceiptTemp[0].Bill[0].tenantId");
  const consumerCode = getQueryArg(window.location.href, "consumerCode");
  const businessService = get(
    state,
    "screenConfiguration.preparedFinalObject.ReceiptTemp[0].Bill[0].businessService"
  );
  const bankBusinessServiceType = get(
    state,
    "screenConfiguration.preparedFinalObject.businessServiceInfo.type"
  );
  let bankBusinessService = '';
  if (bankBusinessServiceType == "Adhoc") {
    bankBusinessService = "MCS";
  } else {
    bankBusinessService = businessService
  }

  //const url = isPublicSearch() ? "withoutAuth/egov-common/paymentRedirectPage" : "egov-common/paymentRedirectPage";
  const url = "egov-common/paymentRedirectPage";
  const redirectUrl = process.env.NODE_ENV === "production" ? `employee/${url}` : url;
  // const businessService = getQueryArg(window.location.href, "businessService"); businessService
  let callbackUrl = `${window.origin}/${redirectUrl}`;
  const { screenConfiguration = {} } = state;
  const { preparedFinalObject = {} } = screenConfiguration;
  const { ReceiptTemp = {} } = preparedFinalObject;
  const billPayload = ReceiptTemp[0];
  const taxAmount = Number(get(billPayload, "Bill[0].totalAmount"));
  let amtToPay =
    state.screenConfiguration.preparedFinalObject.AmountType ===
      "partial_amount"
      ? state.screenConfiguration.preparedFinalObject.AmountPaid
      : taxAmount;
  amtToPay = amtToPay ? Number(amtToPay) : taxAmount;
  
  if (amtToPay > taxAmount && !isAdvancePaymentAllowed) {
    alert("Advance Payment is not allowed");
    return;
  }
  if (amtToPay < taxAmount && 
    ((""+(JSON.parse(localStorage.getItem("user-info"))).roles[0].code) == "UC_COWCESS_USER") && (businessService == "PT" || businessService == "WS" || businessService == "SW") ) {
    alert("Partial Payment is not allowed");
    return;
  }
  /*let isFormValid = validateFields(
    "components.div.children.formwizardFirstStep.children.paymentDetails.children.cardContent.children.capturePayerDetails.children.cardContent.children.payerDetailsCardContainer.children",
    state,
    dispatch,
    "pay"
  );
  */
  let isFormValid = true;
  if (!isFormValid) {
    dispatch(
      toggleSnackbar(
        true,
        {
          labelName: "Transaction numbers don't match !",
          labelKey: "ERR_FILL_ALL_FIELDS"
        },
        "error"
      )
    );
    return;
  }
  if (checkAmount(taxAmount, Number(state.screenConfiguration.preparedFinalObject.AmountPaid), businessService)) {
    dispatch(
      toggleSnackbar(
        true,
        { labelName: "Please enter an amount greater than zero!", labelKey: "ERR_ENTER_AMOUNT_MORE_THAN_ZERO" },
        "error"
      )
    );
    return;
  }

  if (checkAmount(taxAmount, Number(state.screenConfiguration.preparedFinalObject.AmountPaid), businessService)) {
    dispatch(
      toggleSnackbar(
        true,
        { labelName: "Please enter an amount greater than zero!", labelKey: "ERR_ENTER_AMOUNT_MORE_THAN_ZERO" },
        "error"
      )
    );
    return;
  }

  const payerInfo=get(billPayload, "Bill[0].payer",'').replace("COMMON_",'');
  const user = {
    name: get(billPayload, "Bill[0].paidBy", get(billPayload, "Bill[0].payerName")),
    mobileNumber: get(billPayload, "Bill[0].payerMobileNumber", get(billPayload, "Bill[0].mobileNumber")),
    tenantId
  };
  let taxAndPayments = [];
  taxAndPayments.push({
    taxAmount:taxAmount,
    businessService: businessService,
    billId: get(billPayload, "Bill[0].id"),
    amountPaid: amtToPay
  });
  const buttonJsonpath = paybuttonJsonpath + `${((""+(JSON.parse(localStorage.getItem("user-info"))).roles[0].code) == "UC_COWCESS_USER") ? "makePayment" : "generateReceipt"}`;
  try {
    dispatch(handleField("pay", buttonJsonpath, "props.disabled", true));

    const requestBody = {
      Transaction: {
        tenantId,
        txnAmount: amtToPay,
        module: businessService,
        billId: get(billPayload, "Bill[0].id"),
        consumerCode: get(billPayload, "Bill[0].consumerCode"),
        //consumerCode: consumerCode,
        productInfo: "Common Payment",
        gateway: "RAZORPAY",
        taxAndPayments,
        user,
        callbackUrl,
        businessService: bankBusinessService,
        additionalDetails: { isWhatsapp: localStorage.getItem('pay-channel') == 'whatsapp' ? true : false,
        paidBy:payerInfo }
      }
    };
    const goToPaymentGateway = await httpRequest(
      "post",
      "pg-service/transaction/v1/_create",
      "_create",
      [],
      requestBody
    );

    if (get(goToPaymentGateway, "Transaction.txnAmount") == 0) {
      const srcQuery = `?tenantId=${get(
        goToPaymentGateway,
        "Transaction.tenantId"
      )}&billIds=${get(goToPaymentGateway, "Transaction.billId")}`;

      let searchResponse = await httpRequest(
        "post",
        getPaymentSearchAPI(businessService) + srcQuery,
        "_search",
        [],
        {}
      );

      let transactionId = get(
        searchResponse,
        "Payments[0].paymentDetails[0].receiptNumber"
      );

      let  paymentDetails= get(
        searchResponse,
        "Payments[0]",
        null
      );
      dispatch(setPaymentDetails(paymentDetails))

      const ackUrl = `/egov-common/acknowledgement?status=${"success"}&consumerCode=${consumerCode}&tenantId=${tenantId}&receiptNumber=${transactionId}&businessService=${businessService}`;
      const successUrl = isPublicSearch() ? `/withoutAuth${ackUrl}` : ackUrl;
      dispatch(
        setRoute(
          successUrl
        )
      );
    } else {
      const redirectionUrl = get(goToPaymentGateway, "Transaction.redirectUrl") || get(goToPaymentGateway, "Transaction.callbackUrl");
      // if( get(goToPaymentGateway, "Transaction.tenantId")=="pb.amritsar" || get(goToPaymentGateway, "Transaction.tenantId")=="pb.mohali" || get(goToPaymentGateway, "Transaction.tenantId")=="pb.moga" || get(goToPaymentGateway, "Transaction.tenantId")=="pb.khanna" || get(goToPaymentGateway, "Transaction.tenantId")=="pb.hoshiarpur" || get(goToPaymentGateway, "Transaction.tenantId")=="pb.kapurthala" || get(goToPaymentGateway, "Transaction.tenantId")=="pb.mandigobindgarh"|| get(goToPaymentGateway, "Transaction.tenantId")=="pb.handiaya"|| get(goToPaymentGateway, "Transaction.tenantId")=="pb.sultanpurlodhi")
      //   {
      //    displayRazorpay(goToPaymentGateway);
      //   }
      //   else{
      //   window.location = redirectionUrl;
      //   }

         if( get(goToPaymentGateway, "Transaction.tenantId")=="pb.jalandhar" || get(goToPaymentGateway, "Transaction.tenantId")=="pb.testing" )  
              {
                 
          window.location = redirectionUrl;  
         }
        //  else if(get(goToPaymentGateway, "Transaction.tenantId")=="pb.amritsar"){
        // //alert("testing ASR");
        // window.location = redirectionUrl;  
        //  }
         else{
          
          displayRazorpay(goToPaymentGateway);
        
         }
    }
  } catch (e) {
    dispatch(handleField("pay", buttonJsonpath, "props.disabled", false));
    dispatch(
      toggleSnackbar(
        true,
        { labelName: e.message, labelKey: e.message },
        "error"
      )
    );
    /*     // }else{
          moveToFailure(dispatch);
        }
     */
  }
};



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
export const getRedirectionURL = () => {
  const redirectionURL = ifUserRoleExists("CITIZEN") ? "/inbox" : "/uc/newCollection" ;

  return redirectionURL;
};
export const acknowledgementSuccesFooter = getCommonApplyFooter({
  goToHomeButton: {
    componentPath: "Button",
    props: {
      // variant: "contained",
      // color: "primary",
      variant: "outlined",
      color: "primary",
      className:"gen-challan-btn"
      // style: {
      //   minWidth: "200px",
      //   height: "48px",
      //   marginRight: "16px"
      // }
    },
    children: {
      downloadReceiptButtonLabel: getLabel({
        labelName: "Go To Home",
        labelKey: "UC_BUTTON_GO_TO_HOME"
      })
    },
    onClickDefination: {
      action: "condition",
      callBack: (state, dispatch) => {
        goToHome(state, dispatch);
      }
    }
  },
  printMiniChallanButton: {
    componentPath: "Button",
    props: {
        variant: "outlined",
        color: "primary",
        // className: "apply-wizard-footer-right-button",
        className:"gen-challan-btn"

        // disabled: true
    },
    children: {
        printFormButtonLabel: getLabel({
            labelName: "PRINT MINI CHALLAN",
            labelKey: "COMMON_PRINT_MINI_CHALLAN"
        })
    },
    onClickDefination: {
        action: "condition",
        callBack: (state, dispatch) => {
          const challanData = generateMiniChallan(state, dispatch);
          try {
            console.log("printData",JSON.stringify(challanData));
            window.Android && window.Android.sendPrintData("printData",JSON.stringify(challanData));
          } catch (e) {
            console.log(e);
          }
        }
    },
    visible: JSON.parse(window.localStorage.getItem('isPOSmachine')) 
  },
    payButton: {
    componentPath: "Button",
    props: {
      variant: "contained",
      color: "primary",
      className:"gen-challan-btn"
      // style: {
      //   minWidth: "200px",
      //   height: "48px",
      //   marginRight: "16px"
      // }
    },
    children: {
        payButtonLabel: getLabel({
        labelName: "PROCEED TO PAYMENT",
        labelKey: "UC_BUTTON_PAY"
      })
    },
    onClickDefination: {
      action: "condition",
      callBack: (state, dispatch) => {
    
    //console.log("role is : "+(JSON.parse(localStorage.getItem("user-info"))).roles[0].code);
    //console.log("role iss : "+((""+(JSON.parse(localStorage.getItem("user-info"))).roles[0].code) != "UC_COWCESS_USER"));
    //.getItem("roles")[0].getItem("code")=== "UC_COW_CESS_USER");

    const challanNo = getQueryArg(window.location.href, "challanNumber");
    const tenantId = getQueryArg(window.location.href, "tenantId");
    const businessService = getQueryArg(window.location.href,"serviceCategory");
    console.info("businessService=",businessService,"tenantId=",tenantId,"challanNo=",challanNo);
      if(businessService !=null && tenantId !=null && challanNo !=null ){
        getCommonPayUrl(dispatch, challanNo, tenantId, businessService);
      }    
      
      else{
        
        dispatch(setRoute(`/uc/newCollection`));
      }
      
      }
    },
    visible : ((""+(JSON.parse(localStorage.getItem("user-info"))).roles[0].code) != "UC_COWCESS_USER")
    //(JSON.parse(localStorage.getItem("user-info"))).roles[0].code
  }
,


makePayment: {
  componentPath: "Button",
  props: {
    variant: "contained",
    color: "primary",
    className: "make-payment-com",
    // style: {
    //   width: "363px",
    //   height: "48px ",
    //   right: "19px",
    //   position: "relative",
    //   borderRadius: "0px "
    // }
  },
  children: {
    submitButtonLabel: getLabel({
      labelName: "MAKE PAYMENT",
      labelKey: "COMMON_MAKE_PAYMENT"
    }),
    submitButtonIcon: {
      uiFramework: "custom-atoms",
      componentPath: "Icon",
      props: {
        iconName: "keyboard_arrow_right",
        className: ""
      }
    }
  },
  onClickDefination: {
    action: "condition",
    callBack: callPGService
  },
   roleDefination: {
     rolePath: "user-info.roles",
     roles: ["UC_COWCESS_USER"],
     action: "PAY"
   },
  visible: ((""+(JSON.parse(localStorage.getItem("user-info"))).roles[0].code) == "UC_COWCESS_USER")
}
});




export const acknowledgementFailureFooter = getCommonApplyFooter({
  nextButton: {
    componentPath: "Button",
    props: {
      variant: "contained",
      color: "primary",
      style: {
        minWidth: "200px",
        height: "48px",
        marginRight: "16px"
      },
      className:"gen-challan-btn"
    },
    children: {
      goToHomeButtonLabel: getLabel({
        labelName: "Go To Home",
        labelKey: "UC_BUTTON_GO_TO_HOME"
      })
    },
    onClickDefination: {
      action: "page_change",
      path: `${getRedirectionURL()}`
    }
  }
});

const viewReceipt = (state, dispatch) => {
  generateReciept(state, dispatch);
};

const goToHome = (state, dispatch) => { 
  dispatch(prepareFinalObject("Challan", []));
  dispatch(setRoute(`${getRedirectionURL()}`));
};

const generateMiniChallan = (state, dispatch) => { 
  const ReceiptDataTemp = get(
    state.screenConfiguration.preparedFinalObject,"Challan"
  );
  

  const challanDateFormatted = new Date().toLocaleDateString('en-GB', {
    day : 'numeric',
    month : 'short',
    year : 'numeric'
  }).split(' ').join('-');           
  const fromPeriod = getDateFromEpoch(ReceiptDataTemp.taxPeriodFrom);
  const toPeriod = getDateFromEpoch(ReceiptDataTemp.taxPeriodTo);
  const consumerName = ReceiptDataTemp.consumerName;
  let id = getQueryArg(window.location.href, "tenantId"); 
  let localizedULBName = "";
  if(id != null){
   id =  id.split(".")[1];
   localizedULBName =  id[0].toUpperCase() + id.slice(1);
    
  }
  var collectorName = ""; 
  
   var empInfo = JSON.parse(localStorage.getItem("Employee.user-info"));
   collectorName = empInfo.name;

  const businessService = getQueryArg(window.location.href,"serviceCategory");
  const totalAmt = ReceiptDataTemp.amount.reduce(function(total, arr) { 
    // return the sum with previous value
    return total + arr.amount;
  
    // set initial value as 0
  },0);

  var UCminiChallanData = {
    ulbType: localizedULBName,
    receiptNumber: getQueryArg(window.location.href, "challanNumber"),
    tenantid: getQueryArg(window.location.href, "tenantId"),
    consumerName: consumerName,
    businessService: businessService,
    fromPeriod: fromPeriod,
    toPeriod: toPeriod,
    receiptAmount: totalAmt,
    receiptDate:challanDateFormatted,
    collectorName:collectorName,
    status:"Active"
  };  

  return UCminiChallanData;
 // return UCminiChallanBuilder(UCminiChallanData);
};


//--------RazorPay checkout function-------------//
function loadScript(src) {
  return new Promise((resolve) => {
      const script = document.createElement("script");
      script.src = src;
      script.onload = () => {
          resolve(true);
      };
      script.onerror = () => {
          resolve(false);
      };
      document.body.appendChild(script);
  });
}


async function displayRazorpay(getOrderData) {
  const res = await loadScript(
      "https://checkout.razorpay.com/v1/checkout.js"
  );
  
  if (!res) {
      alert("Razorpay SDK failed to load. Are you online?");
      return;
  }
  
  function getQueryVariable(variable)
  {
    const query = get(getOrderData, "Transaction.redirectUrl");
          var vars = query.split("&");
          for (var i=0;i<vars.length;i++) {
                      var pair = vars[i].split("=");
          if(pair[0] == variable){return pair[1];}
           }
           return(false);
  }
  const options = {
      key: getQueryVariable('merchant_key'),
      amount: get(getOrderData, "Transaction.txnAmount")*100,
      //currency: getQueryVariable('currency'),
      name: "mSeva | Punjab",
      description: get(getOrderData, "Transaction.businessService")+" Charge Collection",
      image: "https://mseva.lgpunjab.gov.in/citizen/browser-icon.png",
      order_id: getQueryVariable('orderId'),
      handler: async function (response) {
          const data = {
              razorpayPaymentId: response.razorpay_payment_id,
              razorpayOrderId: response.razorpay_order_id,
              razorpaySignature: response.razorpay_signature,
          };
  
        window.location = get(getOrderData, "Transaction.callbackUrl")+"&razorpayPaymentId="+data.razorpayPaymentId+"&razorpayOrderId="+data.razorpayOrderId+"&razorpaySignature="+data.razorpaySignature;
      },
      prefill: {
          name: get(getOrderData, "Transaction.user.userName"),
          email: get(getOrderData, "Transaction.user.emailId"),
          contact: get(getOrderData, "Transaction.user.mobileNumber"),
      },
      theme: {
          color: "#61dafb",
      },
  };
  
  const paymentObject = new window.Razorpay(options);
  paymentObject.open();
  }
  








// const UCminiChallanBuilder=(h)=> {
//   var NEXTLINE = "&&";
//   let challanString = "     " + h["ulbType"];
//   challanString = challanString + NEXTLINE + "        Collection Receipt" + NEXTLINE;
//   challanString = challanString + "******************************************" + NEXTLINE;

//   challanString = challanString + " Receipt No    : " + h["receiptNumber"] + NEXTLINE;
//   challanString = challanString + " Receipt Date  : " + h["challanDate"] + NEXTLINE;
//   challanString = challanString + " Consumer Name : " + h["consumerName"] + NEXTLINE; 

//   challanString = challanString + " Category      : " + h["businessService"] + NEXTLINE;
//   challanString = challanString + " From Period   : " + h["fromPeriod"] + NEXTLINE;
//   challanString = challanString + " To Period     : " + h["toPeriod"] + NEXTLINE;
//   challanString = challanString + " Paid Amount   : Rs." + h["receiptAmount"] + NEXTLINE;
//   challanString = challanString + " Created By: " + h["collectorName"] + NEXTLINE;
//   challanString = challanString + "******************************************" + NEXTLINE; 
//   //console.log(challanString.replace(/&&/g, "\n"));

//   return "egov://print/" + challanString;
// };