import { Banner, Card, CardText, Loader, Row, StatusTable, SubmitBar, DownloadPrefixIcon } from "@upyog/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useQueryClient } from "react-query";
import { Link, useParams } from "react-router-dom";

export const SuccessfulPayment = (props)=>{
  if(localStorage.getItem("BillPaymentEnabled")!=="true"){
    window.history.forward();
   return null;
 }
 return <WrapPaymentComponent {...props}/>
}

export const convertEpochToDate = (dateEpoch) => {
  // Returning NA in else case because new Date(null) returns Current date from calender
  if (dateEpoch) {
    const dateFromApi = new Date(dateEpoch);
    let month = dateFromApi.getMonth() + 1;
    let day = dateFromApi.getDate();
    let year = dateFromApi.getFullYear();
    month = (month > 9 ? "" : "0") + month;
    day = (day > 9 ? "" : "0") + day;
    return `${day}/${month}/${year}`;
  } else {
    return "NA";
  }
};
 const WrapPaymentComponent = (props) => {
  const { t } = useTranslation();
  const queryClient = useQueryClient();
  const { eg_pg_txnid: egId, workflow: workflw, propertyId } = Digit.Hooks.useQueryParams();
  const [printing, setPrinting] = useState(false);
  const [allowFetchBill, setallowFetchBill] = useState(false);
  const { businessService: business_service, consumerCode, tenantId } = useParams();
  const { data: bpaData = {}, isLoading: isBpaSearchLoading, isSuccess: isBpaSuccess, error: bpaerror } = Digit.Hooks.obps.useOBPSSearch(
    "", {}, tenantId, { applicationNo: consumerCode }, {}, {enabled:(window.location.href.includes("bpa") || window.location.href.includes("BPA"))}
  );
  
  const { isLoading, data, isError } = Digit.Hooks.usePaymentUpdate({ egId }, business_service, {
    
    retry: false,
    staleTime: Infinity,
    refetchOnWindowFocus: false,
  });
  
  const cities = Digit.Hooks.useTenants();
  let ulbType=""
  const loginCity=JSON.parse(sessionStorage.getItem("Digit.User"))?.value?.info?.permanentCity
  if(cities.data!==undefined){
    const selectedTenantData = cities.data.find(item => item?.city?.districtTenantCode=== loginCity);
    ulbType=selectedTenantData?.city?.ulbGrade  
  }

  const { label } = Digit.Hooks.useApplicationsForBusinessServiceSearch({ businessService: business_service }, { enabled: false });

  // const { data: demand } = Digit.Hooks.useDemandSearch(
  //   { consumerCode, businessService: business_service },
  //   { enabled: !isLoading, retry: false, staleTime: Infinity, refetchOnWindowFocus: false }
  // );

  // const { data: billData, isLoading: isBillDataLoading } = Digit.Hooks.useFetchPayment(
  //   { tenantId, consumerCode, businessService: business_service },
  //   { enabled: allowFetchBill, retry: false, staleTime: Infinity, refetchOnWindowFocus: false }
  // );
  const newTenantId=business_service.includes("WS.ONE_TIME_FEE" || "SW.ONE_TIME_FEE")?Digit.ULBService.getStateId():tenantId;
  const { data: reciept_data, isLoading: recieptDataLoading } = Digit.Hooks.useRecieptSearch(
    {
      tenantId,
      businessService: business_service,
      receiptNumbers: data?.payments?.Payments?.[0]?.paymentDetails[0].receiptNumber,
    },
    {
      retry: false,
      staleTime: Infinity,
      refetchOnWindowFocus: false,
      select: (dat) => {
        return dat.Payments[0];
      },
      enabled: allowFetchBill,
    }
  );

  const { data: generatePdfKey } = Digit.Hooks.useCommonMDMS(newTenantId, "common-masters", "ReceiptKey", {
    select: (data) =>
      data["common-masters"]?.uiCommonPay?.filter(({ code }) => business_service?.includes(code))[0]?.receiptKey || "consolidatedreceipt",
    retry: false,
    staleTime: Infinity,
    refetchOnWindowFocus: false,
  });

  const payments = data?.payments;

  useEffect(() => {
    return () => {
      localStorage.setItem("BillPaymentEnabled","false")
      queryClient.clear();
    };
  }, []);

  useEffect(() => {
    if (data && data.txnStatus && data.txnStatus !== "FAILURE") {
      setallowFetchBill(true);
    }
  }, [data]);

  if (isLoading || recieptDataLoading) {
    return <Loader />;
  }

  const applicationNo = data?.applicationNo;

  const isMobile = window.Digit.Utils.browser.isMobile();


  if (isError || !payments || !payments.Payments || payments.Payments.length === 0 || data.txnStatus === "FAILURE") {
    return (
      <Card>
        <Banner
          message={t("CITIZEN_FAILURE_COMMON_PAYMENT_MESSAGE")}
          info={t("CS_PAYMENT_TRANSANCTION_ID")}
          applicationNumber={egId}
          successful={false}
        />
        <CardText>{t("CS_PAYMENT_FAILURE_MESSAGE")}</CardText>
        {!(business_service?.includes("PT")) ? (
          <Link to={`/digit-ui/citizen`}>
            <SubmitBar label={t("CORE_COMMON_GO_TO_HOME")} />
          </Link>
        ) : (
          <React.Fragment>
            <Link to={(applicationNo && `/digit-ui/citizen/payment/my-bills/${business_service}/${applicationNo}`) || "/digit-ui/citizen"}>
              <SubmitBar label={t("CS_PAYMENT_TRY_AGAIN")} />
            </Link>
            {/* {business_service?.includes("PT") &&<div style={{marginTop:"10px"}}><Link to={`/digit-ui/citizen/feedback?redirectedFrom=${"digit-ui/citizen/payment/success"}&propertyId=${consumerCode? consumerCode : ""}&acknowldgementNumber=${egId ? egId : ""}&tenantId=${tenantId}&creationReason=${business_service?.split(".")?.[1]}`}>
              <SubmitBar label={t("CS_REVIEW_AND_FEEDBACK")} />
            </Link></div>} */}
            <div className="link" style={isMobile ? { marginTop: "8px", width: "100%", textAlign: "center" } : { marginTop: "8px" }}>
              <Link to={`/digit-ui/citizen`}>{t("CORE_COMMON_GO_TO_HOME")}</Link>
            </div>
          </React.Fragment>
        )}
      </Card>
    );
  }

  const paymentData = data?.payments?.Payments[0];
  const amount = reciept_data?.paymentDetails?.[0]?.totalAmountPaid;
  const transactionDate = paymentData?.transactionDate;
  const printCertificate = async () => {
    //const tenantId = Digit.ULBService.getCurrentTenantId();
    const state = tenantId;
    const applicationDetails = await Digit.TLService.search({ applicationNumber: consumerCode, tenantId });
    const generatePdfKeyForTL = "tlcertificate";

    if (applicationDetails) {
      let response = await Digit.PaymentService.generatePdf(state, { Licenses: applicationDetails?.Licenses }, generatePdfKeyForTL);
      const fileStore = await Digit.PaymentService.printReciept(state, { fileStoreIds: response.filestoreIds[0] });
      window.open(fileStore[response.filestoreIds[0]], "_blank");
    }
  };
  // const printpetCertificate = async () => {
  //   // const tenantId = Digit.ULBService.getCurrentTenantId();
  //   const state = tenantId;
  //   const applicationDetails = await Digit.PTRService.search({ applicationNumber: consumerCode, tenantId });
  //   console.log("aplllldetailllin citizen",applicationDetails)
  //   const generatePdfKeyForPTR = "petservicecertificate";

  //   if (applicationDetails) {
  //     let response = await Digit.PaymentService.generatePdf(state, { PetRegistrationApplications: applicationDetails?.PetRegistrationApplications }, generatePdfKeyForPTR);
  //     const fileStore = await Digit.PaymentService.printReciept(state, { fileStoreIds: response.filestoreIds[0] });
  //     window.open(fileStore[response.filestoreIds[0]], "_blank");
  //   }
  // };

  const printReciept = async () => {
    let generatePdfKeyForWs="ws-onetime-receipt";
    if (printing) return;
    setPrinting(true);
    let paymentArray=[];
    const tenantId = paymentData?.tenantId;
    const state = Digit.ULBService.getStateId();
    let response = { filestoreIds: [payments.Payments[0]?.fileStoreId] };
    if (!paymentData?.fileStoreId) {
      let assessmentYear="",assessmentYearForReceipt="";
      let count=0;
      let toDate,fromDate;
	  if(payments.Payments[0].paymentDetails[0].businessService=="PT"){
       
      payments.Payments[0].paymentDetails[0].bill.billDetails.map(element => {

          if(element.amount >0 || element.amountPaid>0)
          { count=count+1;
            toDate=convertEpochToDate(element.toPeriod).split("/")[2];
            fromDate=convertEpochToDate(element.fromPeriod).split("/")[2];
            assessmentYear=assessmentYear==""?fromDate+"-"+toDate+"(Rs."+element.amountPaid+")":assessmentYear+","+fromDate+"-"+toDate+"(Rs."+element.amountPaid+")";
            assessmentYearForReceipt=fromDate+"-"+toDate;
          }
    
          });
  
          if(count==0)
          {
            let toDate=convertEpochToDate( payments.Payments[0].paymentDetails[0].bill.billDetails[0].toPeriod).split("/")[2];
            let fromDate=convertEpochToDate( payments.Payments[0].paymentDetails[0].bill.billDetails[0].fromPeriod).split("/")[2];
            assessmentYear=assessmentYear==""?fromDate+"-"+toDate:assessmentYear+","+fromDate+"-"+toDate; 
            assessmentYearForReceipt=fromDate+"-"+toDate;
          }

      
         
           const details = {
              "assessmentYears": assessmentYear,
              "arrearCode": ""
                } 
          
         
            payments.Payments[0].paymentDetails[0].additionalDetails=details; 
            printRecieptNew(payments)
        }
        else {
          let details

          if(payments.Payments[0].paymentDetails[0].businessService=="BPAREG")
          {
              details = {...payments.Payments[0].additionalDetails,
              "stakeholderType":"Application"
                } 
          }
          payments.Payments[0].additionalDetails=details;
          paymentArray[0]=payments.Payments[0]
          console.log("generatedpdfkey",generatePdfKey)
          if(business_service=="WS" || business_service=="SW"){
            response = await Digit.PaymentService.generatePdf(state, { Payments: [{...paymentData}] }, generatePdfKeyForWs);
          }
          else if(paymentData.paymentDetails[0].businessService.includes("BPA")){
            const designation=(ulbType==="Municipal Corporation") ? "Municipal Commissioner" : "Executive Officer"
            const updatedpayments={
              ...paymentData,
              additionalDetails:{
                  ...paymentData.additionalDetails,
                  designation:designation,
                  ulbType:ulbType
              }
            }

            response = await Digit.PaymentService.generatePdf(state, { Payments: [{...updatedpayments}] }, generatePdfKey);
          }
          else{
            response = await Digit.PaymentService.generatePdf(state, { Payments: [{...paymentData}] }, generatePdfKey);
          }
          
        }       
    }
    const fileStore = await Digit.PaymentService.printReciept(state, { fileStoreIds: response.filestoreIds[0] });
    if (fileStore && fileStore[response.filestoreIds[0]]) {
      window.open(fileStore[response.filestoreIds[0]], "_blank");
    }
    setPrinting(false);
  };

  const convertDateToEpoch = (dateString, dayStartOrEnd = "dayend") => {
    //example input format : "2018-10-02"
    try {
      const parts = dateString.match(/(\d{4})-(\d{1,2})-(\d{1,2})/);
      const DateObj = new Date(Date.UTC(parts[1], parts[2] - 1, parts[3]));
      DateObj.setMinutes(DateObj.getMinutes() + DateObj.getTimezoneOffset());
      if (dayStartOrEnd === "dayend") {
        DateObj.setHours(DateObj.getHours() + 24);
        DateObj.setSeconds(DateObj.getSeconds() - 1);
      }
      return DateObj.getTime();
    } catch (e) {
      return dateString;
    }
  };

  const printPdf = (blob) => {
    const fileURL = URL.createObjectURL(blob);
    var myWindow = window.open(fileURL);
    if (myWindow != undefined) {
      myWindow.addEventListener("load", (event) => {
        myWindow.focus();
        myWindow.print();
      });
    }
  };

  const downloadPdf = (blob, fileName) => {
    if (window.mSewaApp && window.mSewaApp.isMsewaApp() && window.mSewaApp.downloadBase64File) {
      var reader = new FileReader();
      reader.readAsDataURL(blob);
      reader.onloadend = function () {
        var base64data = reader.result;
        mSewaApp.downloadBase64File(base64data, fileName);
      };
    } else {
      const link = document.createElement("a");
      // create a blobURI pointing to our Blob
      link.href = URL.createObjectURL(blob);
      link.download = fileName;
      // some browser needs the anchor to be in the doc
      document.body.append(link);
      link.click();
      link.remove();
      // in case the Blob uses a lot of memory
      setTimeout(() => URL.revokeObjectURL(link.href), 7000);
    }
  };
  // let workflowDetails = Digit.Hooks.useWorkflowDetails({
  //   tenantId: data?.[0]?.tenantId,
  //   id: data?.[0]?.applicationNo,
  //   moduleCode: "OBPS",
  // });
  const getPermitOccupancyOrderSearch = async(order, mode="download") => {
    let queryObj = { applicationNo: bpaData?.[0]?.applicationNo };
    let bpaResponse = await Digit.OBPSService.BPASearch(bpaData?.[0]?.tenantId, queryObj);
    const edcrResponse = await Digit.OBPSService.scrutinyDetails(bpaData?.[0]?.tenantId, { edcrNumber: bpaData?.[0]?.edcrNumber });
    let bpaDataDetails = bpaResponse?.BPA?.[0], edcrData = edcrResponse?.edcrDetail?.[0];
    let currentDate = new Date();
    bpaDataDetails.additionalDetails.runDate = convertDateToEpoch(
      currentDate.getFullYear() + "-" + (currentDate.getMonth() + 1) + "-" + currentDate.getDate()
    );
    let reqData = { ...bpaDataDetails, edcrDetail: [{ ...edcrData }] };
    const state = Digit.ULBService.getStateId();

    let count=0;
    // console.log("workflowDetails",workflowDetails)
    // debugger
    // for(let i=0;i<workflowDetails?.data?.processInstances?.length;i++){
    //   if((workflowDetails?.data?.processInstances[i]?.action==="POST_PAYMENT_APPLY" ||workflowDetails?.data?.processInstances[i]?.action==="PAY" ) && (workflowDetails?.data?.processInstances?.[i]?.state?.applicationStatus==="APPROVAL_INPROGRESS")   && count==0 ){
    //       reqData.additionalDetails.submissionDate=workflowDetails?.data?.processInstances[i]?.auditDetails?.createdTime;
    //       count=1;
    //     }
    // }

    if(reqData?.additionalDetails?.approvedColony=="NO"){
      reqData.additionalDetails.permitData= "The plot has been officially regularized under No."+reqData?.additionalDetails?.NocNumber +"  dated dd/mm/yyyy, registered in the name of <name as per the NOC>. This regularization falls within the jurisdiction of "+ state +".Any form of misrepresentation of the NoC is strictly prohibited. Such misrepresentation renders the building plan null and void, and it will be regarded as an act of impersonation. Criminal proceedings will be initiated against the owner and concerned architect / engineer/ building designer / supervisor involved in such actions"
    }
    else if(reqData?.additionalDetails?.approvedColony=="YES" ){
      reqData.additionalDetails.permitData="The building plan falls under approved colony "+reqData?.additionalDetails?.nameofApprovedcolony
    }
    else{
      reqData.additionalDetails.permitData="The building plan falls under Lal Lakir"
    }
    let response = await Digit.PaymentService.generatePdf(bpaDataDetails?.tenantId, { Bpa: [reqData] }, order);
    const fileStore = await Digit.PaymentService.printReciept(bpaDataDetails?.tenantId, { fileStoreIds: response.filestoreIds[0] });
    window.open(fileStore[response?.filestoreIds[0]], "_blank");

    reqData["applicationType"] = bpaDataDetails?.additionalDetails?.applicationType;
    let edcrresponse = await Digit.OBPSService.edcr_report_download({ BPA: { ...reqData } });
    const responseStatus = parseInt(edcrresponse.status, 10);
    if (responseStatus === 201 || responseStatus === 200) {
      mode == "print"
        ? printPdf(new Blob([edcrresponse.data], { type: "application/pdf" }))
        : downloadPdf(new Blob([edcrresponse.data], { type: "application/pdf" }), `edcrReport.pdf`);
    }
  };

  const getBillingPeriod = (billDetails) => {
    const { taxPeriodFrom, taxPeriodTo, fromPeriod, toPeriod } = billDetails || {};
    if (taxPeriodFrom && taxPeriodTo) {
      let from = new Date(taxPeriodFrom).getFullYear().toString();
      let to = new Date(taxPeriodTo).getFullYear().toString();
      return "FY " + from + "-" + to;
    } else if (fromPeriod && toPeriod) {
      if (workflw === "mcollect") {
        let from =
          new Date(fromPeriod).getDate().toString() +
          " " +
          Digit.Utils.date.monthNames[new Date(fromPeriod).getMonth() ].toString() +
          " " +
          new Date(fromPeriod).getFullYear().toString();
        let to =
          new Date(toPeriod).getDate() +
          " " +
          Digit.Utils.date.monthNames[new Date(toPeriod).getMonth()] +
          " " +
          new Date(toPeriod).getFullYear();
        return from + " - " + to;
      }
      else if(workflw === "WNS")
      {
        let from =
          new Date(fromPeriod).getDate().toString() +
          "/" +
          (new Date(fromPeriod).getMonth() + 1).toString() +
          "/" +
          new Date(fromPeriod).getFullYear().toString();
        let to =
          new Date(toPeriod).getDate() +
          "/" +
          (new Date(toPeriod).getMonth() + 1) +
          "/" +
          new Date(toPeriod).getFullYear();
        return from + " - " + to;
      }
      let from = new Date(fromPeriod).getFullYear().toString();
      let to = new Date(toPeriod).getFullYear().toString();
      return "FY " + from + "-" + to;
    } else return "N/A";
  };

  let bannerText;
  if (workflw) {
    bannerText = `CITIZEN_SUCCESS_UC_PAYMENT_MESSAGE`;
  } else {
    if (paymentData?.paymentDetails?.[0]?.businessService && paymentData?.paymentDetails?.[0]?.businessService?.includes("BPA")) {
      let nameOfAchitect = sessionStorage.getItem("BPA_ARCHITECT_NAME");
      let parsedArchitectName = nameOfAchitect ? JSON.parse(nameOfAchitect) : "ARCHITECT";
      bannerText = `CITIZEN_SUCCESS_${paymentData?.paymentDetails[0]?.businessService.replace(/\./g, "_")}_${parsedArchitectName}_PAYMENT_MESSAGE`;
    } else if (business_service?.includes("WS") || business_service?.includes("SW")) {
      bannerText = t(`CITIZEN_SUCCESS_${paymentData?.paymentDetails[0].businessService.replace(/\./g, "_")}_WS_PAYMENT_MESSAGE`);
    } else {
      bannerText = paymentData?.paymentDetails[0]?.businessService ? `CITIZEN_SUCCESS_${paymentData?.paymentDetails[0]?.businessService.replace(/\./g, "_")}_PAYMENT_MESSAGE` : t("CITIZEN_SUCCESS_UC_PAYMENT_MESSAGE");
    }
  }

  // https://dev.digit.org/collection-services/payments/FSM.TRIP_CHARGES/_search?tenantId=pb.amritsar&consumerCodes=107-FSM-2021-02-18-063433

  // if (billDataLoading) return <Loader />;

  const rowContainerStyle = {
    padding: "4px 0px",
    justifyContent: "space-between",
  };
  //New Payment Reciept For PT module with year bifurcations

  const printRecieptNew = async (payment) => {
    console.log("paymentpayment",payment,payment.Payments[0].paymentDetails[0].receiptNumber,payment.Payments[0])
    const tenantId = Digit.ULBService.getCurrentTenantId();
    const state = Digit.ULBService.getStateId();
    let paymentArray=[];
    const payments = await Digit.PaymentService.getReciept(tenantId, "PT", { receiptNumbers: payment.Payments[0].paymentDetails[0].receiptNumber });
    let response = { filestoreIds: [payments.Payments[0]?.fileStoreId] };
    if (true) {
      let assessmentYear="",assessmentYearForReceipt="";
      let count=0;
      let toDate,fromDate;
    if(payments.Payments[0].paymentDetails[0].businessService=="PT"){
       let arrearRow={};  let arrearArray=[];
          let taxRow={};  let taxArray=[];
         
  
          let roundoff=0,tax=0,firecess=0,cancercess=0,penalty=0,rebate=0,interest=0,usage_exemption=0,special_category_exemption=0,adhoc_penalty=0,adhoc_rebate=0,total=0;
          let roundoffT=0,taxT=0,firecessT=0,cancercessT=0,penaltyT=0,rebateT=0,interestT=0,usage_exemptionT=0,special_category_exemptionT=0,adhoc_penaltyT=0,adhoc_rebateT=0,totalT=0;
  
     
      payments.Payments[0].paymentDetails[0].bill.billDetails.map(element => {
  
          if(element.amount >0 || element.amountPaid>0)
          { count=count+1;
            toDate=convertEpochToDate(element.toPeriod).split("/")[2];
            fromDate=convertEpochToDate(element.fromPeriod).split("/")[2];
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
  
          if(count==0)
          {
            let toDate=convertEpochToDate( payments.Payments[0].paymentDetails[0].bill.billDetails[0].toPeriod).split("/")[2];
            let fromDate=convertEpochToDate( payments.Payments[0].paymentDetails[0].bill.billDetails[0].fromPeriod).split("/")[2];
            assessmentYear=assessmentYear==""?fromDate+"-"+toDate:assessmentYear+","+fromDate+"-"+toDate; 
            assessmentYearForReceipt=fromDate+"-"+toDate;
         
          
            payments.Payments[0].paymentDetails[0].bill.billDetails[0].billAccountDetails.map(ele => {
         
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
      "total": payments.Payments[0].paymentDetails[0].bill.billDetails[0].amountPaid
  
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
        "total": payments.Payments[0].paymentDetails[0].bill.billDetails[0].amount
      };
      arrearArray.push(arrearRow);
      taxArray.push(taxRow);
      
  }  
          
          const details = {
        "assessmentYears": assessmentYear,
        "arrearArray":arrearArray,
        "taxArray": taxArray
            }
            payments.Payments[0].paymentDetails[0].additionalDetails=details;
            
        }
    
         paymentArray[0]=payments.Payments[0]
        console.log("payments",payments)
      response = await Digit.PaymentService.generatePdf(state, { Payments: paymentArray }, generatePdfKey);
      console.log("responseresponse",response)
    }
    const fileStore = await Digit.PaymentService.printReciept(state, { fileStoreIds: response.filestoreIds[0] });
    window.open(fileStore[response.filestoreIds[0]], "_blank");
  };
  const ommitRupeeSymbol = ["PT"].includes(business_service);

  if ((window.location.href.includes("bpa") || window.location.href.includes("BPA")) && isBpaSearchLoading) return <Loader />

  return (
    <Card>
      <Banner
        svg={
          <svg className="payment-svg" xmlns="http://www.w3.org/2000/svg" width="40" height="40" viewBox="0 0 40 40" fill="none">
            <path
              d="M20 0C8.96 0 0 8.96 0 20C0 31.04 8.96 40 20 40C31.04 40 40 31.04 40 20C40 8.96 31.04 0 20 0ZM16 30L6 20L8.82 17.18L16 24.34L31.18 9.16L34 12L16 30Z"
              fill="white"
            />
          </svg>
        }
        message={t("CS_COMMON_PAYMENT_COMPLETE")}
        info={t("CS_COMMON_RECIEPT_NO")}
        applicationNumber={paymentData?.paymentDetails[0].receiptNumber}
        successful={true}
      />
      <CardText></CardText>
      <StatusTable>
        <Row rowContainerStyle={rowContainerStyle} last label={t(label)} text={applicationNo} />
        {/** TODO : move this key and value into the hook based on business Service */}
        {(business_service === "PT" || workflw) && (
          <Row
            rowContainerStyle={rowContainerStyle}
            last
            label={t("CS_PAYMENT_BILLING_PERIOD")}
            text={getBillingPeriod(paymentData?.paymentDetails[0]?.bill?.billDetails[0])}
          />
        )}

        {(business_service === "PT" || workflw) && (
          <Row
            rowContainerStyle={rowContainerStyle}
            last
            label={t("CS_PAYMENT_AMOUNT_PENDING")}
            text={paymentData?.totalDue-paymentData?.totalAmountPaid || (reciept_data?.paymentDetails?.[0]?.totalDue && reciept_data?.paymentDetails?.[0]?.totalAmountPaid ) ? `₹ ${reciept_data?.paymentDetails?.[0]?.totalDue - reciept_data?.paymentDetails?.[0]?.totalAmountPaid}` : `₹ ${0}`}
          />
        )}

        <Row rowContainerStyle={rowContainerStyle} last label={t("CS_PAYMENT_TRANSANCTION_ID")} text={egId} />
        <Row
          rowContainerStyle={rowContainerStyle}
          last
          label={t(ommitRupeeSymbol ? "CS_PAYMENT_AMOUNT_PAID_WITHOUT_SYMBOL" : "CS_PAYMENT_AMOUNT_PAID")}
          text={paymentData?.totalAmountPaid ||( reciept_data?.paymentDetails?.[0]?.totalAmountPaid ? ("₹ " +  reciept_data?.paymentDetails?.[0]?.totalAmountPaid) : `₹ 0`) }
        />
        {(business_service !== "PT" || workflw) && (
          <Row
            rowContainerStyle={rowContainerStyle}
            last
            label={t("CS_PAYMENT_TRANSANCTION_DATE")}
            text={transactionDate && new Date(transactionDate).toLocaleDateString("in")}
          />
        )}
      </StatusTable>
      <div style={{display:"flex"}}>
      {business_service == "TL" ? (
        <div className="primary-label-btn d-grid" style={{ marginLeft: "unset", marginRight: "20px", marginTop:"15px",marginBottom:"15px" }} onClick={printReciept}>
          <svg xmlns="http://www.w3.org/2000/svg" height="24px" viewBox="0 0 24 24" width="24px" fill="#a82227">
            <path d="M0 0h24v24H0V0z" fill="none" />
            <path d="M19 9h-4V3H9v6H5l7 7 7-7zm-8 2V5h2v6h1.17L12 13.17 9.83 11H11zm-6 7h14v2H5z" />
          </svg>
          {t("TL_RECEIPT")}
        </div>
      ) : null}
      {business_service == "TL" ? (
        <div className="primary-label-btn d-grid" style={{ marginLeft: "unset", marginTop:"15px" }} onClick={printCertificate}>
          <svg xmlns="http://www.w3.org/2000/svg" height="24px" viewBox="0 0 24 24" width="24px" fill="#a82227">
            <path d="M0 0h24v24H0V0z" fill="none" />
            <path d="M19 9h-4V3H9v6H5l7 7 7-7zm-8 2V5h2v6h1.17L12 13.17 9.83 11H11zm-6 7h14v2H5z" />
          </svg>
          {t("TL_CERTIFICATE")}
        </div>
      ) : null}
      {/*for pett */}
      {business_service == "pet-services" ? (
        <div className="primary-label-btn d-grid" style={{ marginLeft: "unset", marginRight: "20px", marginTop:"15px",marginBottom:"15px" }} onClick={printReciept}>
          <svg xmlns="http://www.w3.org/2000/svg" height="24px" viewBox="0 0 24 24" width="24px" fill="#a82227">
            <path d="M0 0h24v24H0V0z" fill="none" />
            <path d="M19 9h-4V3H9v6H5l7 7 7-7zm-8 2V5h2v6h1.17L12 13.17 9.83 11H11zm-6 7h14v2H5z" />
          </svg>
          {t("PTR_FEE_RECEIPT")}
        </div>
      ) : null}
      {/* {business_service == "pet-services" ? (
        <div className="primary-label-btn d-grid" style={{ marginLeft: "unset", marginTop:"15px" }} onClick={printpetCertificate}>
          <svg xmlns="http://www.w3.org/2000/svg" height="24px" viewBox="0 0 24 24" width="24px" fill="#a82227">
            <path d="M0 0h24v24H0V0z" fill="none" />
            <path d="M19 9h-4V3H9v6H5l7 7 7-7zm-8 2V5h2v6h1.17L12 13.17 9.83 11H11zm-6 7h14v2H5z" />
          </svg>
          {t("PTR_CERTIFICATE")}
        </div>
      ) : null} */}
      {/*for pett */}
      {bpaData?.[0]?.businessService === "BPA_OC" && (bpaData?.[0]?.status==="APPROVED" || bpaData?.[0]?.status==="PENDING_SANC_FEE_PAYMENT") ? (
        <div className="primary-label-btn d-grid" style={{ marginLeft: "unset" }} onClick={e => getPermitOccupancyOrderSearch("occupancy-certificate")}>
          <DownloadPrefixIcon />
            {t("BPA_OC_CERTIFICATE")}
          </div>
      ) : null}
      {bpaData?.[0]?.businessService === "BPA_LOW" ? (
        <div className="primary-label-btn d-grid" style={{ marginLeft: "unset" }} onClick={r => getPermitOccupancyOrderSearch("buildingpermit-low")}>
          <DownloadPrefixIcon />
            {t("BPA_PERMIT_ORDER")}
          </div>
      ) : null}
      {bpaData?.[0]?.businessService === "BPA" && (bpaData?.[0]?.businessService !== "BPA_LOW") && (bpaData?.[0]?.businessService !== "BPA_OC") && (bpaData?.[0]?.status==="PENDING_SANC_FEE_PAYMENT" || bpaData?.[0]?.status==="APPROVED")? (
        <div className="primary-label-btn d-grid" style={{ marginLeft: "unset" }} onClick={r => getPermitOccupancyOrderSearch("buildingpermit")}>
          <DownloadPrefixIcon />
            {t("BPA_PERMIT_ORDER")}
          </div>
        ) : null}
      </div>
      {business_service?.includes("PT") &&<div style={{marginTop:"10px"}}><Link to={`/digit-ui/citizen/feedback?redirectedFrom=${"digit-ui/citizen/payment/success"}&propertyId=${consumerCode? consumerCode : ""}&acknowldgementNumber=${egId ? egId : ""}&tenantId=${tenantId}&creationReason=${business_service?.split(".")?.[1]}`}>
          <SubmitBar label={t("CS_REVIEW_AND_FEEDBACK")} />
      </Link></div>}
      {business_service?.includes("PT") ? (
        <div className="link" style={isMobile ? { marginTop: "8px", width: "100%", textAlign: "center" } : { marginTop: "8px" }} onClick={printReciept}>
            {t("CS_DOWNLOAD_RECEIPT")}
          </div>
      ) : null}
      {business_service?.includes("WS") ? (
        <div className="link" style={isMobile ? { marginTop: "8px", width: "100%", textAlign: "center" } : { marginTop: "8px" }} onClick={printReciept}>
            {t("CS_DOWNLOAD_RECEIPT")}
          </div>
      ) : null}
      {business_service?.includes("SW") ? (
        <div className="link" style={isMobile ? { marginTop: "8px", width: "100%", textAlign: "center" } : { marginTop: "8px" }} onClick={printReciept}>
            {t("CS_DOWNLOAD_RECEIPT")}
          </div>
      ) : null}      
      {business_service?.includes("FSM") ? (
        <div className="link" style={isMobile ? { marginTop: "8px", width: "100%", textAlign: "center" } : { marginTop: "8px" }} onClick={printReciept}>
            {t("CS_DOWNLOAD_RECEIPT")}
          </div>
      ) : null}
      {business_service?.includes("BPA") ? (
        <div className="link" style={isMobile ? { marginTop: "8px", width: "100%", textAlign: "center" } : { marginTop: "8px" }} onClick={printReciept}>
            {t("CS_DOWNLOAD_RECEIPT")}
          </div>
      ) : null}
      {!(business_service == "TL") || !(business_service?.includes("PT")) && <SubmitBar onSubmit={printReciept} label={t("COMMON_DOWNLOAD_RECEIPT")} />}
      {!(business_service == "TL") || !(business_service?.includes("PT")) && (
        <div className="link" style={isMobile ? { marginTop: "8px", width: "100%", textAlign: "center" } : { marginTop: "8px" }}>
          <Link to={`/digit-ui/citizen`}>{t("CORE_COMMON_GO_TO_HOME")}</Link>
        </div>
      )}
      {business_service == "TL" && (
        <Link to={`/digit-ui/citizen`}>
          <SubmitBar label={t("CORE_COMMON_GO_TO_HOME")} />
        </Link>
      )}
      {business_service == "pet-services" && (
        <Link to={`/digit-ui/citizen`}>
          <SubmitBar label={t("CORE_COMMON_GO_TO_HOME")} />
        </Link>
      )}
    </Card>
  );
};

export const FailedPayment = (props) => {
  const { addParams, clearParams } = props;
  const { t } = useTranslation();
  const { consumerCode } = useParams();

  const getMessage = () => "Failure !";
  return (
    <Card>
      <Banner message={getMessage()} complaintNumber={consumerCode} successful={false} />
      <CardText>{t("ES_COMMON_TRACK_COMPLAINT_TEXT")}</CardText>
    </Card>
  );
};


