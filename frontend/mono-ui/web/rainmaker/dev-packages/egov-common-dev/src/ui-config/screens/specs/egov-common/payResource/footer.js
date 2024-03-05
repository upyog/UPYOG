import { getLabel } from "egov-ui-framework/ui-config/screens/specs/utils";
import { setRoute } from "egov-ui-framework/ui-redux/app/actions";
import { handleScreenConfigurationFieldChange as handleField, prepareFinalObject, toggleSnackbar ,setPaymentDetails} from "egov-ui-framework/ui-redux/screen-configuration/actions";
import { getQueryArg, isPublicSearch } from "egov-ui-framework/ui-utils/commons";
import { transformById } from "egov-ui-kit/utils/commons";
import cloneDeep from "lodash/cloneDeep";
import get from "lodash/get";
import set from "lodash/set";
import { httpRequest } from "egov-ui-framework/ui-utils/api";
import { convertDateToEpoch, ifUserRoleExists, validateFields } from "../../utils";
import { paybuttonJsonpath } from "./constants";
import { convertEpochToDate, getTranslatedLabel } from "../../utils";
import {downloadReceiptFromFilestoreID} from "../../../../../ui-utils/commons";
import "./index.css";
import { toggleSpinner } from "egov-ui-framework/ui-redux/screen-configuration/actions";
const PAYMENTSEARCH = {
  GET: {
    URL: "/collection-services/payments/",
    ACTION: "_search",
  },
};
const getPaymentSearchAPI = (businessService='')=>{
  if(businessService=='-1'){
    return `${PAYMENTSEARCH.GET.URL}${PAYMENTSEARCH.GET.ACTION}`
  }else if (process.env.REACT_APP_NAME === "Citizen") {
    return `${PAYMENTSEARCH.GET.URL}${PAYMENTSEARCH.GET.ACTION}`;
  }
  return `${PAYMENTSEARCH.GET.URL}${businessService}/${PAYMENTSEARCH.GET.ACTION}`;
}

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
   const BusinessService=get(state, "screenConfiguration.preparedFinalObject.ReceiptTemp[0].Bill[0].businessService");
    var diffDays;
    if(BusinessService.toUpperCase() =="FIRENOC"){
    var getdate=get(state, "screenConfiguration.preparedFinalObject.FireNOCs[0].auditDetails.createdTime");
    debugger;
    

    const currentDate = new Date();
    const appDate = new Date(getdate);
    const diffTime = Math.abs(appDate - currentDate);
    diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)); 
    console.log(diffTime + " milliseconds");
    console.log(diffDays + " days");
    }
    if (diffDays>=5 && BusinessService.toUpperCase() =="FIRENOC"){
      alert("Re-submit application can be applied within 5 days of date of application.");
      }
   
       else{
           
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
    
      const url = isPublicSearch() ? "withoutAuth/egov-common/paymentRedirectPage" : "egov-common/paymentRedirectPage";
      const redirectUrl = process.env.NODE_ENV === "production" ? `citizen/${url}` : url;
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
      if (amtToPay < taxAmount && process.env.REACT_APP_NAME === "Citizen" && (businessService == "PT" || businessService == "WS" || businessService == "SW") ) {
        alert("Partial Payment is not allowed");
        return;
      }
      let isFormValid = validateFields(
        "components.div.children.formwizardFirstStep.children.paymentDetails.children.cardContent.children.capturePayerDetails.children.cardContent.children.payerDetailsCardContainer.children",
        state,
        dispatch,
        "pay"
      );
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
      const buttonJsonpath = paybuttonJsonpath + `${(process.env.REACT_APP_NAME === "Citizen" || (((JSON.parse(localStorage.getItem("user-info"))).roles[0].code) === "UC_COWCESS_USER")) ? "makePayment" : "generateReceipt"}`;
      try {
        dispatch(handleField("pay", buttonJsonpath, "props.disabled", true));
        const requestBody = {
          Transaction: {
            tenantId,
            txnAmount: amtToPay,
            module: businessService,
            billId: get(billPayload, "Bill[0].id"),
            consumerCode: consumerCode,
            productInfo: "Common Payment",
            gateway: "CCAVANUE",
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
          console.log("reurl", redirectionUrl);
          // if( get(goToPaymentGateway, "Transaction.tenantId")=="pb.amritsar" || get(goToPaymentGateway, "Transaction.tenantId")=="pb.mohali" || get(goToPaymentGateway, "Transaction.tenantId")=="pb.moga" || get(goToPaymentGateway, "Transaction.tenantId")=="pb.khanna" || get(goToPaymentGateway, "Transaction.tenantId")=="pb.hoshiarpur" || get(goToPaymentGateway, "Transaction.tenantId")=="pb.kapurthala" || get(goToPaymentGateway, "Transaction.tenantId")=="pb.mandigobindgarh"|| get(goToPaymentGateway, "Transaction.tenantId")=="pb.handiaya"|| get(goToPaymentGateway, "Transaction.tenantId")=="pb.sultanpurlodhi")
          //   {
          //    displayRazorpay(goToPaymentGateway);
          //   }
          //   else{
          //   window.location = redirectionUrl;
          //   }
    
             if( get(goToPaymentGateway, "Transaction.tenantId")=="pb.jalandhar" || get(goToPaymentGateway, "Transaction.tenantId")=="pb.testing" || get(goToPaymentGateway, "Transaction.tenantId")=="pb.amritsar")         {
             debugger;
              window.location = redirectionUrl;  
             }
            //  else if(get(goToPaymentGateway, "Transaction.tenantId")=="pb.amritsar"){
            //  // alert("testing ASR2");
            //   window.location = redirectionUrl; 
            //    }
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
    
    
    
  }
};
export const download = async (receiptQueryString, mode = "download" ,configKey="consolidatedreceipt" , state) => {
  if(state && process.env.REACT_APP_NAME === "Citizen" && configKey === "consolidatedreceipt"){
    const uiCommonPayConfig = get(state.screenConfiguration.preparedFinalObject , "commonPayInfo");
    configKey = get(uiCommonPayConfig, "receiptKey")
  }



  const FETCHRECEIPT = {
    GET: {
      URL: "/_search",
      ACTION: "_get",
    },
  };

  const DOWNLOADRECEIPT = {
    GET: {
      URL: "/pdf-service/v1/_create",
      ACTION: "_get",
    },
  };
  let consumerCode = getQueryArg(window.location.href, "consumerCode")?getQueryArg(window.location.href, "consumerCode"):receiptQueryString[0].value;
  let tenantId = getQueryArg(window.location.href, "tenantId")?getQueryArg(window.location.href, "tenantId"):receiptQueryString[1].value;
  let applicationNumber = getQueryArg(window.location.href, "applicationNumber");

  let queryObject = [
    { key: "tenantId", value:tenantId },
    { key: "applicationNumber", value: consumerCode?consumerCode:applicationNumber}
  ];

  let queryObjectForPT = [
    { key: "tenantId", value:tenantId },
    { key: "propertyIds", value: consumerCode?consumerCode:applicationNumber}
  ];
  const FETCHFIREDETAILS = {
    GET: {
      URL: "/firenoc-services/v1/_search",
      ACTION: "_get",
    },
  };

  const FETCHPROPERTYDETAILS = {
    GET: {
      URL: "/property-services/property/_search",
      ACTION: "_get",
    },
  };
  const FETCHTRADEDETAILS = {
    GET: {
      URL: "/tl-services/v1/_search",
      ACTION: "_get",
    },
  };
  const responseForTrade = await httpRequest("post", FETCHTRADEDETAILS.GET.URL, FETCHTRADEDETAILS.GET.ACTION,queryObject);
  const response =  await httpRequest("post", FETCHFIREDETAILS.GET.URL, FETCHFIREDETAILS.GET.ACTION,queryObject);
  const responseForPT =  await httpRequest("post", FETCHPROPERTYDETAILS.GET.URL, FETCHPROPERTYDETAILS.GET.ACTION,queryObjectForPT);

  let uuid=responseForPT && responseForPT.Properties[0]?responseForPT.Properties[0].auditDetails.lastModifiedBy:null;
  let data = {};
  let bodyObject = {
    uuid: [uuid]
  };
  let responseForUser = await getUserDataFromUuid(bodyObject);
  let lastmodifier=responseForUser && responseForUser.user[0]?responseForUser.user[0].name:null;
  let businessService = '';
  receiptQueryString && Array.isArray(receiptQueryString) && receiptQueryString.map(query => {
  if (query.key == "businessService") {
    businessService = query.value;
  }
  })
  receiptQueryString = receiptQueryString && Array.isArray(receiptQueryString) && receiptQueryString.filter(query => query.key != "businessService")
  try {
    const payloadReceiptDetails = await httpRequest("post",getPaymentSearchAPI(businessService), FETCHRECEIPT.GET.ACTION, receiptQueryString);
    // loadUserNameData(payloadReceiptDetails.Payments[0].auditDetails.createdBy,tenantId);
      if (payloadReceiptDetails && payloadReceiptDetails.Payments && payloadReceiptDetails.Payments.length == 0) {
        console.log("Could not find any receipts");
        return;
      }
      if(payloadReceiptDetails.Payments[0].payerName!=null){
      payloadReceiptDetails.Payments[0].payerName=payloadReceiptDetails.Payments[0].payerName.trim();}
      else if(payloadReceiptDetails.Payments[0].payerName == null && payloadReceiptDetails.Payments[0].paymentDetails[0].businessService=="FIRENOC" && payloadReceiptDetails.Payments[0].paidBy !=null)
       { payloadReceiptDetails.Payments[0].payerName=payloadReceiptDetails.Payments[0].paidBy.trim();
      }
      if(payloadReceiptDetails.Payments[0].paidBy!=null)
      {
        payloadReceiptDetails.Payments[0].paidBy=payloadReceiptDetails.Payments[0].paidBy.trim();
      }


      if(payloadReceiptDetails.Payments[0].paymentDetails[0].receiptNumber.includes("MP")){
        let tax,field,cgst,sgst;
let billaccountarray=payloadReceiptDetails.Payments[0].paymentDetails[0].bill.billDetails[0].billAccountDetails;

billaccountarray.map(element => {

if(element.taxHeadCode.includes("CGST")){  cgst=element.amount;}
else if(element.taxHeadCode.includes("SGST")){  sgst=element.amount;}
else if(element.taxHeadCode.includes("FIELD_FEE")){  field=element.amount;}
else  { tax=element.amount;}
});

let taxheads = {
  "tax": tax,
  "fieldfee":field,
  "cgst":cgst,
  "sgst":sgst
  }
payloadReceiptDetails.Payments[0].paymentDetails[0].additionalDetails=taxheads; 

      }

      let assessmentYear="",assessmentYearForReceipt="";
      let count=0;
      if(payloadReceiptDetails.Payments[0].paymentDetails[0].businessService=="PT"){
        let reasonss = null;
        let adhocPenaltyReason=null,adhocRebateReason=null;
        if(state && get(state.screenConfiguration,"preparedFinalObject") && (get(state.screenConfiguration.preparedFinalObject,"adhocExemptionPenalty.adhocExemptionReason") || get(state.screenConfiguration.preparedFinalObject,"adhocExemptionPenalty.adhocPenaltyReason")))
        {
          adhocPenaltyReason = get(
          state.screenConfiguration.preparedFinalObject,"adhocExemptionPenalty.adhocPenaltyReason");
          adhocRebateReason = get(
          state.screenConfiguration.preparedFinalObject,"adhocExemptionPenalty.adhocExemptionReason");
          if(adhocPenaltyReason == "Others")
          { adhocPenaltyReason=get(
            state.screenConfiguration.preparedFinalObject,"adhocExemptionPenalty.adhocOtherPenaltyReason");}
          if(adhocRebateReason == "Others")
            { adhocRebateReason=get(
              state.screenConfiguration.preparedFinalObject,"adhocExemptionPenalty.adhocOtherExemptionReason");}

        }
          reasonss = {
            "adhocPenaltyReason": adhocPenaltyReason,
            "adhocRebateReason":adhocRebateReason,
            "lastModifier":lastmodifier
            };
        payloadReceiptDetails.Payments[0].paymentDetails[0].bill.additionalDetails=reasonss; 
          let arrearRow={};  let arrearArray=[];
          let taxRow={};  let taxArray=[];
         

          let roundoff=0,tax=0,firecess=0,cancercess=0,penalty=0,rebate=0,interest=0,usage_exemption=0,special_category_exemption=0,adhoc_penalty=0,adhoc_rebate=0,total=0;
          let roundoffT=0,taxT=0,firecessT=0,cancercessT=0,penaltyT=0,rebateT=0,interestT=0,usage_exemptionT=0,special_category_exemptionT=0,adhoc_penaltyT=0,adhoc_rebateT=0,totalT=0;

          payloadReceiptDetails.Payments[0].paymentDetails[0].bill.billDetails.map(element => {

          if(element.amount >0 || element.amountPaid>0)
          { count=count+1;
            totalT=0;
            let toDate=convertEpochToDate(element.toPeriod).split("/")[2];
            let fromDate=convertEpochToDate(element.fromPeriod).split("/")[2];
            assessmentYear=assessmentYear==""?fromDate+"-"+toDate+"(Rs."+element.amountPaid+")":assessmentYear+","+fromDate+"-"+toDate+"(Rs."+element.amountPaid+")";
         assessmentYearForReceipt=fromDate+"-"+toDate;
       
    element.billAccountDetails.map(ele => {
    if(ele.taxHeadCode == "PT_TAX")
    {tax=ele.adjustedAmount;
      taxT=ele.amount}
    else if(ele.taxHeadCode == "PT_TIME_REBATE")
    {rebate=ele.adjustedAmount;
      rebateT=ele.amount;}
    else if(ele.taxHeadCode == "PT_CANCER_CESS")
    {cancercess=ele.adjustedAmount;
    cancercessT=ele.amount;}
    else if(ele.taxHeadCode == "PT_FIRE_CESS")
    {firecess=ele.adjustedAmount;
      firecessT=ele.amount;}
    else if(ele.taxHeadCode == "PT_TIME_INTEREST")
    {interest=ele.adjustedAmount;
      interestT=ele.amount;}
    else if(ele.taxHeadCode == "PT_TIME_PENALTY")
    {penalty=ele.adjustedAmount;
      penaltyT=ele.amount;}
    else if(ele.taxHeadCode == "PT_OWNER_EXEMPTION")
    {special_category_exemption=ele.adjustedAmount;
      special_category_exemptionT=ele.amount;}	
    else if(ele.taxHeadCode == "PT_ROUNDOFF")
    {roundoff=ele.adjustedAmount;
      roundoffT=ele.amount;}	
    else if(ele.taxHeadCode == "PT_UNIT_USAGE_EXEMPTION")
    {usage_exemption=ele.adjustedAmount;
      usage_exemptionT=ele.amount;}	
    else if(ele.taxHeadCode == "PT_ADHOC_PENALTY")
    {adhoc_penalty=ele.adjustedAmount;
      adhoc_penaltyT=ele.amount;}
    else if(ele.taxHeadCode == "PT_ADHOC_REBATE")
    {adhoc_rebate=ele.adjustedAmount;
      adhoc_rebateT=ele.amount;}
  
    totalT=totalT+ele.amount;
    });
  arrearRow={
  "year":assessmentYearForReceipt,
  "tax":tax,
  "firecess":firecess,
  "cancercess":cancercess,
  "penalty":penalty,
  "rebate": rebate,
  "interest":interest,
  "usage_exemption":usage_exemption,
  "special_category_exemption": special_category_exemption,
  "adhoc_penalty":adhoc_penalty,
  "adhoc_rebate":adhoc_rebate,
  "roundoff":roundoff,
  "total":element.amountPaid
  };
  taxRow={
    "year":assessmentYearForReceipt,
    "tax":taxT,
    "firecess":firecessT,
    "cancercess":cancercessT,
    "penalty":penaltyT,
    "rebate": rebateT,
    "interest":interestT,
    "usage_exemption":usage_exemptionT,
    "special_category_exemption": special_category_exemptionT,
    "adhoc_penalty":adhoc_penaltyT,
    "adhoc_rebate":adhoc_rebateT,
    "roundoff":roundoffT,
    "total":element.amount
    };
  arrearArray.push(arrearRow);
  taxArray.push(taxRow);
            } 
          });
  
          if(count==0){  total=0; totalT=0;
            let index=payloadReceiptDetails.Payments[0].paymentDetails[0].bill.billDetails.length;
            let toDate=convertEpochToDate( payloadReceiptDetails.Payments[0].paymentDetails[0].bill.billDetails[0].toPeriod).split("/")[2];
            let fromDate=convertEpochToDate( payloadReceiptDetails.Payments[0].paymentDetails[0].bill.billDetails[0].fromPeriod).split("/")[2];
            assessmentYear=assessmentYear==""?fromDate+"-"+toDate:assessmentYear+","+fromDate+"-"+toDate; 
            assessmentYearForReceipt=fromDate+"-"+toDate;
            payloadReceiptDetails.Payments[0].paymentDetails[0].bill.billDetails[0].billAccountDetails.map(ele => {
               
              if(ele.taxHeadCode == "PT_TAX")
              {tax=ele.adjustedAmount;
                taxT=ele.amount}
              else if(ele.taxHeadCode == "PT_TIME_REBATE")
              {rebate=ele.adjustedAmount;
                rebateT=ele.amount;}
              else if(ele.taxHeadCode == "PT_CANCER_CESS")
              {cancercess=ele.adjustedAmount;
              cancercessT=ele.amount;}
              else if(ele.taxHeadCode == "PT_FIRE_CESS")
              {firecess=ele.adjustedAmount;
                firecessT=ele.amount;}
              else if(ele.taxHeadCode == "PT_TIME_INTEREST")
              {interest=ele.adjustedAmount;
                interestT=ele.amount;}
              else if(ele.taxHeadCode == "PT_TIME_PENALTY")
              {penalty=ele.adjustedAmount;
                penaltyT=ele.amount;}
              else if(ele.taxHeadCode == "PT_OWNER_EXEMPTION")
              {special_category_exemption=ele.adjustedAmount;
                special_category_exemptionT=ele.amount;}	
              else if(ele.taxHeadCode == "PT_ROUNDOFF")
              {roundoff=ele.adjustedAmount;
                roundoffT=ele.amount;}	
              else if(ele.taxHeadCode == "PT_UNIT_USAGE_EXEMPTION")
              {usage_exemption=ele.adjustedAmount;
                usage_exemptionT=ele.amount;}	
              else if(ele.taxHeadCode == "PT_ADHOC_PENALTY")
              {adhoc_penalty=ele.adjustedAmount;
                adhoc_penaltyT=ele.amount;}
              else if(ele.taxHeadCode == "PT_ADHOC_REBATE")
              {adhoc_rebate=ele.adjustedAmount;
                adhoc_rebateT=ele.amount;}
            
              total=total+ele.adjustedAmount;
              totalT=totalT+ele.amount;
  
              });
            arrearRow={
            "year":assessmentYearForReceipt,
            "tax":tax,
            "firecess":firecess,
            "cancercess":cancercess,
            "penalty":penalty,
            "interest":interest,
            "usage_exemption":usage_exemption,
            "special_category_exemption": special_category_exemption,
            "adhoc_penalty":adhoc_penalty,
            "adhoc_rebate":adhoc_rebate,
            "roundoff":roundoff,
            "total": payloadReceiptDetails.Payments[0].paymentDetails[0].bill.billDetails[0].amountPaid
            };
            taxRow={
              "year":assessmentYearForReceipt,
              "tax":taxT,
              "firecess":firecessT,
              "cancercess":cancercessT,
              "penalty":penaltyT,
              "rebate": rebateT,
              "interest":interestT,
              "usage_exemption":usage_exemptionT,
              "special_category_exemption": special_category_exemptionT,
              "adhoc_penalty":adhoc_penaltyT,
              "adhoc_rebate":adhoc_rebateT,
              "roundoff":roundoffT,
              "total":payloadReceiptDetails.Payments[0].paymentDetails[0].bill.billDetails[0].amount

              };
            arrearArray.push(arrearRow);
            taxArray.push(taxRow);
    
    }  
          
          const details = {
        "assessmentYears": assessmentYear,
        "arrearArray":arrearArray,
        "taxArray": taxArray
            }
            payloadReceiptDetails.Payments[0].paymentDetails[0].additionalDetails=details; 
        }
     
      if(payloadReceiptDetails.Payments[0].paymentDetails[0].businessService=="WS.ONE_TIME_FEE" || payloadReceiptDetails.Payments[0].paymentDetails[0].businessService=="SW.ONE_TIME_FEE" || payloadReceiptDetails.Payments[0].paymentDetails[0].businessService=="WS" || payloadReceiptDetails.Payments[0].paymentDetails[0].businessService=="SW"){
        let dcbRow=null,dcbArray=[];
        let installment,totalamount=0;
        payloadReceiptDetails.Payments[0].paymentDetails[0].bill.billDetails.map((element,index) => {
      if(element.amountPaid >0 || element.amountPaid < 0)
      {
      installment=convertEpochToDate(element.fromPeriod) +"-"+convertEpochToDate(element.toPeriod);
      element.billAccountDetails.map((dd)=>{
      if((dd.adjustedAmount > 0 || dd.adjustedAmount < 0) || (dd.amount < 0))
      {
        let code=null,amount=null;
        if(dd.taxHeadCode == "WS_CHARGE")
        {
        code="Water Charges";
        amount=dd.adjustedAmount;
        }
        else if( dd.taxHeadCode == "SW_CHARGE")
        {
        code="Sewerage Charges";
        amount=dd.adjustedAmount;
        }
        else if(dd.taxHeadCode == "WS_Round_Off" || dd.taxHeadCode == "SW_Round_Off")
        {
        code="Round Off";
        amount=dd.adjustedAmount;

        }
        else if(dd.taxHeadCode == "WS_TIME_INTEREST" || dd.taxHeadCode == "SW_TIME_INTEREST")
        {
        code="Interest";         amount=dd.adjustedAmount;

        }
        else if(dd.taxHeadCode == "WS_TIME_PENALTY" || dd.taxHeadCode == "SW_TIME_PENALTY")
        {
        code="Penalty";         amount=dd.adjustedAmount;

        }  else if(dd.taxHeadCode == "WS_SCRUTINY_FEE" || dd.taxHeadCode == "SW_SCRUTINY_FEE")
        {
        code="Scrutiny Fee";         amount=dd.adjustedAmount;

        }  else if(dd.taxHeadCode == "WS_ROAD_CUTTING_CHARGE" || dd.taxHeadCode == "SW_ROAD_CUTTING_CHARGE")
        {
        code="Road Cutting Charges";         amount=dd.adjustedAmount;

        }  else if(dd.taxHeadCode == "WS_METER_TESTING_FEE" || dd.taxHeadCode == "SW_METER_TESTING_FEE")
        {
        code="Meter Testing Fee";         amount=dd.adjustedAmount;

        }  else if(dd.taxHeadCode == "WS_SECURITY_DEPOSIT" || dd.taxHeadCode == "SW_SECURITY_DEPOSIT")
        {
        code="Security Deposit";         amount=dd.adjustedAmount;

        }  else if(dd.taxHeadCode == "WS_OTHER_FEE" || dd.taxHeadCode == "SW_OTHER_FEE")
        {
        code="Other Fee";         amount=dd.adjustedAmount;

        }else if(dd.taxHeadCode == "WS_USER_CHARGE" || dd.taxHeadCode == "SW_USER_CHARGE")
        {
        code="User Charges";         amount=dd.adjustedAmount;

        }else if(dd.taxHeadCode == "WS_CONNECTION_FEE" || dd.taxHeadCode == "SW_CONNECTION_FEE")
        {
        code="Connection Fee";         amount=dd.adjustedAmount;

        }else if(dd.taxHeadCode == "WS_COMPOSITION_FEE" || dd.taxHeadCode == "SW_COMPOSITION_FEE")
        {
        code="Composition Fee";         amount=dd.adjustedAmount;

        }else if(dd.taxHeadCode == "SW_ADVANCE_CARRYFORWARD" || dd.taxHeadCode == "WS_ADVANCE_CARRYFORWARD" )
        {
        code="Advance";         amount=-dd.amount;

        }
        if(payloadReceiptDetails.Payments[0].paymentDetails[0].businessService=="WS.ONE_TIME_FEE" || payloadReceiptDetails.Payments[0].paymentDetails[0].businessService=="SW.ONE_TIME_FEE")
        {
          dcbRow={
            "taxhead":code ,
            "amount":amount
          };
        }
        else{
        dcbRow={
          "taxhead":code + "("+installment+")",
          "amount":amount
        };}
totalamount=totalamount+amount;
dcbArray.push(dcbRow);
      }

     
      });    
      };
        });
		dcbRow={
			"taxhead":"Total Amount Paid",
			"amount":totalamount
		  };
		dcbArray.push(dcbRow);
        payloadReceiptDetails.Payments[0].paymentDetails[0].additionalDetails=dcbArray;
        }

      if(payloadReceiptDetails.Payments[0].paymentDetails[0].businessService=="TL"){

        configKey="tradelicense-receipt";

        const details = {
          "address": responseForTrade.Licenses[0].tradeLicenseDetail.address.locality.code
          }
    payloadReceiptDetails.Payments[0].paymentDetails[0].bill.billDetails[0].additionalDetails=details; 


      }

      if(payloadReceiptDetails.Payments[0].paymentDetails[0].businessService=="FIRENOC"){

      let owners=""; let contacts="";
      response.FireNOCs[0].fireNOCDetails.applicantDetails.owners.map(ele=>{
        if(owners=="")
        {owners=ele.name; 
          contacts=ele.mobileNumber;}
        else{
          owners=owners+","+ele.name; 
          contacts=contacts+","+ele.mobileNumber;
        }

      });
      payloadReceiptDetails.Payments[0].payerName=owners;
      payloadReceiptDetails.Payments[0].mobileNumber=contacts;
      let receiptDate=convertEpochToDate(payloadReceiptDetails.Payments[0].paymentDetails[0].receiptDate);
      let year=receiptDate.split("/")[2];
      year++;
      var nextyear=year;
      year--;
      var lastyear=year-1;
      let month=receiptDate.split("/")[1];
      let from=null,to=null;
      if(month<=3){ from=convertDateToEpoch("04/01/"+lastyear);
      to=convertDateToEpoch("03/31/"+year);}
      else{from=convertDateToEpoch("04/01/"+year);
      to=convertDateToEpoch("03/31/"+nextyear);}
      let building='';
      let length=response.FireNOCs[0].fireNOCDetails.buildings.length;
      response.FireNOCs[0].fireNOCDetails.buildings.map( (item,index) => {
if(index == 0)
      building=building + item.name;
else 
building = building + "," + item.name;
      });


      const details = {
    "address": "Building :"+building +","+ response.FireNOCs[0].fireNOCDetails.applicantDetails.owners[0].correspondenceAddress
  }
       payloadReceiptDetails.Payments[0].paymentDetails[0].bill.billDetails[0].additionalDetails=details; 
       payloadReceiptDetails.Payments[0].paymentDetails[0].bill.billDetails[0].fromPeriod=from;
       payloadReceiptDetails.Payments[0].paymentDetails[0].bill.billDetails[0].toPeriod=to; 


    } 
    const queryStr = [
      { key: "key", value: configKey },
      { key: "tenantId", value: receiptQueryString[1].value.split('.')[0] }
    ]
      // Setting the Payer and mobile from Bill to reflect it in PDF
      state = state ? state : {};
         if(payloadReceiptDetails.Payments[0].paymentMode=="CHEQUE" || payloadReceiptDetails.Payments[0].paymentMode=="DD" || payloadReceiptDetails.Payments[0].paymentMode=="OFFLINE_NEFT" || payloadReceiptDetails.Payments[0].paymentMode=="OFFLINE_RTGS" || payloadReceiptDetails.Payments[0].paymentMode=="ONLINE"){
        let ifsc = get(state, "screenConfiguration.preparedFinalObject.ReceiptTemp[0].instrument.ifscCode", null);
        let branchName = get(state, "screenConfiguration.preparedFinalObject.ReceiptTemp[0].instrument.branchName", null);
        let bank = get(state, "screenConfiguration.preparedFinalObject.ReceiptTemp[0].instrument.bank.name", null);
        payloadReceiptDetails.Payments[0].ifscCode=ifsc; 
        const details = [{
           "branchName": branchName ,
          "bankName":bank }
        ]       
      payloadReceiptDetails.Payments[0].additionalDetails=details; 
    }
      let billDetails = get(state, "screenConfiguration.preparedFinalObject.ReceiptTemp[0].Bill[0]", null);
      if ((billDetails && !billDetails.payerName) || !billDetails) {
        billDetails = {
          payerName: get(state, "screenConfiguration.preparedFinalObject.applicationDataForReceipt.owners[0].name", null) || get(state, "screenConfiguration.preparedFinalObject.applicationDataForPdf.owners[0].name", null),
          mobileNumber: get(state, "screenConfiguration.preparedFinalObject.applicationDataForReceipt.owners[0].mobile", null) || get(state, "screenConfiguration.preparedFinalObject.applicationDataForPdf.owners[0].mobile", null),
        };
      }
       if(payloadReceiptDetails.Payments[0].paymentMode=="CASH")
      {
        payloadReceiptDetails.Payments[0].instrumentDate=null;
        payloadReceiptDetails.Payments[0].instrumentNumber=null;
      }
      if (!payloadReceiptDetails.Payments[0].payerName && process.env.REACT_APP_NAME === "Citizen" && billDetails) {
        payloadReceiptDetails.Payments[0].payerName = billDetails.payerName;
        // payloadReceiptDetails.Payments[0].paidBy = billDetails.payer;
        payloadReceiptDetails.Payments[0].mobileNumber = billDetails.mobileNumber;
      }
      if((payloadReceiptDetails.Payments[0].payerName==null || payloadReceiptDetails.Payments[0].mobileNumber==null)  && payloadReceiptDetails.Payments[0].paymentDetails[0].businessService=="FIRENOC" && process.env.REACT_APP_NAME === "Citizen")
      {
        payloadReceiptDetails.Payments[0].payerName=response.FireNOCs[0].fireNOCDetails.applicantDetails.owners[0].name;
        payloadReceiptDetails.Payments[0].mobileNumber= response.FireNOCs[0].fireNOCDetails.applicantDetails.owners[0].mobileNumber;
      }
      const oldFileStoreId = get(payloadReceiptDetails.Payments[0], "fileStoreId")
      if (oldFileStoreId) {
        downloadReceiptFromFilestoreID(oldFileStoreId, mode)
      }
      else {
        const propertiesById = get(state.properties , "propertiesById");
        const propertiesFound = propertiesById ? Object.values(propertiesById) : null;
        let queryData = { Payments: payloadReceiptDetails.Payments };
        if(propertiesFound) {
          queryData.properties = propertiesFound;
        }
        httpRequest("post", DOWNLOADRECEIPT.GET.URL, DOWNLOADRECEIPT.GET.ACTION, queryStr, queryData, { 'Accept': 'application/json' }, { responseType: 'arraybuffer' });

      }
  
  } catch (exception) {
    console.log('Some Error Occured while downloading Receipt!');
  }
}

const moveToSuccess = (dispatch, receiptNumber, paymentDetails) => {
  const consumerCode = getQueryArg(window.location, "consumerCode");
  const tenantId = getQueryArg(window.location, "tenantId");
  const businessService = getQueryArg(window.location, "businessService");
  const status = "success";
  const appendUrl =
    process.env.REACT_APP_SELF_RUNNING === "true" ? "/egov-ui-framework" : "";
  let moduleName = "egov-common";
  if (businessService && businessService.indexOf("BPA") > -1) {
    moduleName = "egov-bpa"
  }
  const url = `${appendUrl}/${moduleName}/acknowledgement?status=${status}&consumerCode=${consumerCode}&tenantId=${tenantId}&receiptNumber=${receiptNumber}&businessService=${businessService}`;
  const ackSuccessUrl = isPublicSearch() ? `/withoutAuth${url}` : url;
  dispatch(
    setRoute(ackSuccessUrl)
  );
};
const moveToFailure = dispatch => {
  const consumerCode = getQueryArg(window.location, "consumerCode");
  const tenantId = getQueryArg(window.location, "tenantId");
  const businessService = getQueryArg(window.location, "businessService");
  const status = "failure";
  const appendUrl =
    process.env.REACT_APP_SELF_RUNNING === "true" ? "/egov-ui-framework" : "";
  const url = `${appendUrl}/egov-common/acknowledgement?status=${status}&consumerCode=${consumerCode}&tenantId=${tenantId}&businessService=${businessService}`
  const ackFailureUrl = isPublicSearch() ? `/withoutAuth${url}` : url;
  dispatch(
    setRoute(
      ackFailureUrl
    )
  );
};
const getUserDataFromUuid = async bodyObject => {
  try {
    const response = await httpRequest(
      "post",
      "/user/_search",
      "",
      [],
      bodyObject
    );
    return response;
  } catch (error) {
    console.log(error);
    return {};
  }
};
const getSelectedTabIndex = paymentType => {
  switch (paymentType) {
    case "CASH":
      return {
        selectedPaymentMode: "cash",
        selectedTabIndex: 0,
        fieldsToValidate: ["payeeDetails"]
      };
    case "CHEQUE":
      return {
        selectedPaymentMode: "cheque",
        selectedTabIndex: 1,
        fieldsToValidate: ["payeeDetails", "chequeDetails"]
      };

    case "CARD":
      return {
        selectedPaymentMode: "card",
        selectedTabIndex: 2,
        fieldsToValidate: ["payeeDetails", "cardDetails"]
      };
    case "OFFLINE_NEFT":
      return {
        selectedPaymentMode: "offline_neft",
        selectedTabIndex: 3,
        fieldsToValidate: ["payeeDetails", "onlineDetails"]
      };
    case "OFFLINE_RTGS":
      return {
        selectedPaymentMode: "offline_rtgs",
        selectedTabIndex: 4,
        fieldsToValidate: ["payeeDetails", "onlineDetails"]
      };
    case "POSTAL_ORDER":
      return {
        selectedPaymentMode: "postal_order",
        selectedTabIndex: 5,
        fieldsToValidate: ["payeeDetails", "poDetails"]
      };
    case "DD":
      return {
        selectedPaymentMode: "demandDraft",
        selectedTabIndex: 6,
        fieldsToValidate: ["payeeDetails", "demandDraftDetails"]
      };

    default:
      return {
        selectedPaymentMode: "cash",
        selectedTabIndex: 0,
        fieldsToValidate: ["payeeDetails"]
      };
  }
};

const validateString = (str = "") => {
  str = str && str != null && str.trim() || "";
  if (str.length > 0) {
    return true;
  }
  return false;
}
const convertDateFieldToEpoch = (finalObj, jsonPath) => {
  const dateConvertedToEpoch = convertDateToEpoch(
    get(finalObj, jsonPath),
    "daystart"
  );
  set(finalObj, jsonPath, dateConvertedToEpoch);
};

const allDateToEpoch = (finalObj, jsonPaths) => {
  jsonPaths.forEach(jsonPath => {
    if (get(finalObj, jsonPath)) {
      convertDateFieldToEpoch(finalObj, jsonPath);
    }
  });
};

const updatePayAction = async (
  state,
  dispatch,
  consumerCode,
  tenantId,
  receiptNumber
) => {
  try {
    moveToSuccess(dispatch, receiptNumber);
  } catch (e) {
    moveToFailure(dispatch);
    dispatch(
      toggleSnackbar(
        true,
        { labelName: e.message, labelKey: e.message },
        "error"
      )
    );
    console.log(e);
  }
};

const callBackForPay = async (state, dispatch) => {
  let isFormValid = true;
  const isAdvancePaymentAllowed = get(state, "screenConfiguration.preparedFinalObject.businessServiceInfo.isAdvanceAllowed");
  const roleExists = ifUserRoleExists("CITIZEN");
  if (roleExists) {
    alert("You are not Authorized!");
    return;
  }

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

  isFormValid =
    fieldsToValidate
      .map(curr => {
        return validateFields(
          `components.div.children.formwizardFirstStep.children.paymentDetails.children.cardContent.children.capturePaymentDetails.children.cardContent.children.tabSection.props.tabs[${selectedTabIndex}].tabContent.${selectedPaymentMode}.children.${curr}.children`,
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
      `components.div.children.formwizardFirstStep.children.paymentDetails.children.cardContent.children.g8Details.children.cardContent.children.receiptDetailsCardContainer.children`,
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

  // if (get(finalReceiptData, "Bill[0].billDetails[0].manualReceiptDate")) {
  //   convertDateFieldToEpoch(
  //     finalReceiptData,
  //     "Bill[0].billDetails[0].manualReceiptDate"
  //   );
  // }

  // if (get(finalReceiptData, "instrument.transactionDateInput")) {
  //   convertDateFieldToEpoch(
  //     finalReceiptData,
  //     "Bill[0].billDetails[0].manualReceiptDate"
  //   );
  // }
  if (get(finalReceiptData, "instrument.transactionDateInput")) {
    set(
      finalReceiptData,
      "instrument.instrumentDate",
      get(finalReceiptData, "instrument.transactionDateInput")
    );
  }

  if (get(finalReceiptData, "instrument.transactionNumber")) {
    set(
      finalReceiptData,
      "instrument.instrumentNumber",
      get(finalReceiptData, "instrument.transactionNumber")
    );
  }

  if (selectedPaymentType === "CARD") {
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
            labelKey: "ERR_TRANSACTION_NO_DONT_MATCH"
          },
          "error"
        )
      );
      return;
    }
  }
  if (selectedPaymentType === "CHEQUE" || selectedPaymentType === "OFFLINE_NEFT" || selectedPaymentType === "OFFLINE_RTGS") {
    //Extra check - to verify ifsc and bank details are populated 


    let ifscCode = get(finalReceiptData, "instrument.ifscCode", "");
    let branchName = get(finalReceiptData, "instrument.branchName", "");
    let bankName = get(finalReceiptData, "instrument.bank.name", "");
    if (
      !validateString(ifscCode) || !validateString(branchName) || !validateString(bankName) || ifscCode !== get(
        state.screenConfiguration.preparedFinalObject,
        "validIfscCode", ""
      )
    ) {
      dispatch(
        prepareFinalObject("ReceiptTemp[0].instrument.bank.name", "")
      );
      dispatch(
        prepareFinalObject("ReceiptTemp[0].instrument.branchName", "")
      );
      dispatch(
        toggleSnackbar(
          true,
          {
            labelName: "Enter a Valid IFSC code !",
            labelKey: "ERR_ENTER_VALID_IFSC"
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
  let ReceiptBodyNew = {
    Payment: { paymentDetails: [] }
  };

  ReceiptBody.Receipt.push(finalReceiptData);
  const totalAmount = Number(finalReceiptData.Bill[0].totalAmount);

  ReceiptBodyNew.Payment["tenantId"] = finalReceiptData.tenantId;
  ReceiptBodyNew.Payment["totalDue"] = totalAmount;

  ReceiptBodyNew.Payment["paymentMode"] =
    finalReceiptData.instrument.instrumentType.name;
  ReceiptBodyNew.Payment["paidBy"] = finalReceiptData.Bill[0].paidBy;
  ReceiptBodyNew.Payment["mobileNumber"] =
    finalReceiptData.Bill[0].payerMobileNumber;
    if(finalReceiptData.Bill[0].consumerCode.includes("UC"))
    {    ReceiptBodyNew.Payment["payerName"] = finalReceiptData.Bill[0].payerName;
  }else{
    ReceiptBodyNew.Payment["payerName"] = finalReceiptData.Bill[0].paidBy?finalReceiptData.Bill[0].paidBy:(finalReceiptData.Bill[0].payerName||finalReceiptData.Bill[0].payer);}  if (finalReceiptData.instrument.transactionNumber) {
    ReceiptBodyNew.Payment["transactionNumber"] =
      finalReceiptData.instrument.transactionNumber;
  }
  if (finalReceiptData.instrument.instrumentNumber) {
    ReceiptBodyNew.Payment["instrumentNumber"] =
      finalReceiptData.instrument.instrumentNumber;
  }
  if (finalReceiptData.instrument.instrumentDate) {
    ReceiptBodyNew.Payment["instrumentDate"] =
      finalReceiptData.instrument.instrumentDate;
  }
  if( finalReceiptData.instrument.instrumentType.name != "Cash" && finalReceiptData.instrument.ifscCode){
    ReceiptBodyNew.Payment["ifscCode"] =
      finalReceiptData.instrument.ifscCode;
      const details = [{
        "branchName": finalReceiptData.instrument.branchName ,
       "bankName":finalReceiptData.instrument.bank.name }]

     ReceiptBodyNew.Payment["additionalDetails"] =details;
  }
  let amtPaid =
    state.screenConfiguration.preparedFinalObject.AmountType ===
      "partial_amount"
      ? state.screenConfiguration.preparedFinalObject.AmountPaid
      : finalReceiptData.Bill[0].totalAmount;
  amtPaid = amtPaid ? Number(amtPaid) : totalAmount;


  if (amtPaid > totalAmount && !isAdvancePaymentAllowed) {
    alert("Advance Payment is not allowed");
    return;
  }
  if (amtPaid < totalAmount && process.env.REACT_APP_NAME === "Citizen" && (businessService == "PT" || businessService == "WS" || businessService == "SW") ) {
    alert("Partial Payment is not allowed");
    return;
  }
  if (checkAmount(totalAmount, Number(state.screenConfiguration.preparedFinalObject.AmountPaid), finalReceiptData.Bill[0].businessService)) {
    dispatch(
      toggleSnackbar(
        true,
        { labelName: "Please enter an amount greater than zero!", labelKey: "ERR_ENTER_AMOUNT_MORE_THAN_ZERO" },
        "error"
      )
    );
    return;
  }

  ReceiptBodyNew.Payment.paymentDetails.push({
    manualReceiptDate:
      finalReceiptData.Bill[0].billDetails[0].manualReceiptDate,
    manualReceiptNumber:
      finalReceiptData.Bill[0].billDetails[0].manualReceiptNumber,
    businessService: finalReceiptData.Bill[0].businessService,
    billId: finalReceiptData.Bill[0].id,
    totalDue: totalAmount,
    totalAmountPaid: amtPaid
  });
  ReceiptBodyNew.Payment["totalAmountPaid"] = amtPaid;

  //---------------- Create Receipt ------------------//
  if (isFormValid) {
    const buttonJsonpath = paybuttonJsonpath + `${(process.env.REACT_APP_NAME === "Citizen" || (((JSON.parse(localStorage.getItem("user-info"))).roles[0].code) === "UC_COWCESS_USER")) ? "makePayment" : "generateReceipt"}`;
    dispatch(handleField("pay", buttonJsonpath, "props.disabled", true));
    try {
      let response = await httpRequest(
        "post",
        "collection-services/payments/_create",
        "_create",
        [],
        ReceiptBodyNew,
        [],
        {}
      );

      const receiptQueryString = [
        {
          key: "receiptNumbers",
          value: response.Payments[0].paymentDetails[0].receiptNumber
        },
        {
          key: "tenantId",
          value: response.Payments[0].tenantId
        },
        {
          key: "businessService",
          value: response.Payments[0].paymentDetails[0].businessService
        }
      ];
      const uiCommonPayConfig = get(state.screenConfiguration.preparedFinalObject , "commonPayInfo");
      const receiptKey = get(uiCommonPayConfig, "receiptKey");
      download(receiptQueryString,"download",receiptKey,state);
      let receiptNumber = get(
        response,
        "Payments[0].paymentDetails[0].receiptNumber",
        null
      );

      let  paymentDetails= get(
        response,
        "Payments[0]",
        null
      );
      dispatch(setPaymentDetails(paymentDetails))

      // Search NOC application and update action to PAY
      const consumerCode = getQueryArg(window.location, "consumerCode");
      const tenantId = getQueryArg(window.location, "tenantId");
      await updatePayAction(
        state,
        dispatch,
        consumerCode,
        tenantId,
        receiptNumber,
        paymentDetails
      );
    } catch (e) {

      dispatch(handleField("pay", buttonJsonpath, "props.disabled", false));
      dispatch(
        toggleSnackbar(
          true,
          { labelName: e.message, labelKey: e.message },
          "error"
        )
      );
      console.log(e);
    }
  } else {
    dispatch(
      toggleSnackbar(
        true,
        {
          labelName: "Please fill all the mandatory fields",
          labelKey: "ERR_FILL_ALL_FIELDS"
        },
        "error"
      )
    );
  }
};

export const getCommonApplyFooter = children => {
  return {
    uiFramework: "custom-atoms",
    componentPath: "Div",
    props: {
      className: "apply-wizard-footer"
    },
    children
  };
};

export const footer = getCommonApplyFooter({
  generateReceipt: {
    componentPath: "Button",
    props: {
      variant: "contained",
      color: "primary",
      className: "gen-receipt-com",
      // style: {
      //   width: "379px",
      //   height: "48px ",
      //   right: "19px ",
      //   position: "relative",
      //   borderRadius: "0px "
      // }
    },
    children: {
      submitButtonLabel: getLabel({
        labelName: "GENERATE RECEIPT",
        labelKey: "COMMON_GENERATE_RECEIPT"
      }),
      submitButtonIcon: {
        uiFramework: "custom-atoms",
        componentPath: "Icon",
        props: {
          iconName: "keyboard_arrow_right"
        }
      }
    },
    onClickDefination: {
      action: "condition",
      callBack: callBackForPay
    },
    // roleDefination: {
    //   rolePath: "user-info.roles",
    //   roles: ["NOC_CEMP"],
    //   action: "PAY"
    // },
    visible: (process.env.REACT_APP_NAME === "Citizen" || (((JSON.parse(localStorage.getItem("user-info"))).roles[0].code) === "UC_COWCESS_USER")) ? false : true
  },
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
    // roleDefination: {
    //   rolePath: "user-info.roles",
    //   roles: ["CITIZEN"],
    //   action: "PAY"
    // },
    visible: (process.env.REACT_APP_NAME === "Citizen" || (((JSON.parse(localStorage.getItem("user-info"))).roles[0].code) === "UC_COWCESS_USER")) ? true : false
  }
});

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

