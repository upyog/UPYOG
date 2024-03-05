import { Card, CardText, Header, LinkLabel, Loader, Row, StatusTable } from "@egovernments/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useParams } from "react-router-dom";

const convertEpochToDate = dateEpoch => {
  if (dateEpoch) {
    const dateFromApi = new Date(dateEpoch);
    let month = dateFromApi.getMonth() + 1;
    let day = dateFromApi.getDate();
    let year = dateFromApi.getFullYear();
    month = (month > 9 ? "" : "0") + month;
    day = (day > 9 ? "" : "0") + day;
    return day + "/" + month + "/" + year;
  } else {
    return null;
  }
};

// const printReciept = async (businessService, receiptNumber) => {
//   await Digit.Utils.downloadReceipt(null, businessService, "consolidatedreceipt", undefined, receiptNumber);
// };
const printReciept = async (tenantId, payments) => { 
  console.log("payments",payments)
  let response = { filestoreIds: [payments?.fileStoreId] };
  if (!payments?.fileStoreId) {
    let assessmentYear="",assessmentYearForReceipt="";
    let count=0;
    let toDate,fromDate;
  if(payments.paymentDetails[0].businessService=="PT"){
     let arrearRow={};  let arrearArray=[];
        let taxRow={};  let taxArray=[];
       

        let roundoff=0,tax=0,firecess=0,cancercess=0,penalty=0,rebate=0,interest=0,usage_exemption=0,special_category_exemption=0,adhoc_penalty=0,adhoc_rebate=0,total=0;
        let roundoffT=0,taxT=0,firecessT=0,cancercessT=0,penaltyT=0,rebateT=0,interestT=0,usage_exemptionT=0,special_category_exemptionT=0,adhoc_penaltyT=0,adhoc_rebateT=0,totalT=0;

   
        payments.paymentDetails[0].bill.billDetails.map(element => {

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
          let toDate=convertEpochToDate( payments.paymentDetails[0].bill.billDetails[0].toPeriod).split("/")[2];
          let fromDate=convertEpochToDate( payments.paymentDetails[0].bill.billDetails[0].fromPeriod).split("/")[2];
          assessmentYear=assessmentYear==""?fromDate+"-"+toDate:assessmentYear+","+fromDate+"-"+toDate; 
          assessmentYearForReceipt=fromDate+"-"+toDate;
       
        
          payments.paymentDetails[0].bill.billDetails[0].billAccountDetails.map(ele => {
       
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
    "total": payments.paymentDetails[0].bill.billDetails[0].amountPaid

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
      "total": payments.paymentDetails[0].bill.billDetails[0].amount
    };
    arrearArray.push(arrearRow);
    taxArray.push(taxRow);
    
}  
     
  const details = {
      "assessmentYears": assessmentYear,
      "arrearArray":arrearArray,
      "taxArray": taxArray
          }
    payments.paymentDetails[0].additionalDetails=details;   
        }
    response = await Digit.PaymentService.generatePdf(tenantId, { Payments: [{ ...payments }] }, "property-receipt");
  }
  const fileStore = await Digit.PaymentService.printReciept(tenantId, { fileStoreIds: response.filestoreIds[0] });
  window.open(fileStore[response?.filestoreIds[0]], "_blank");
};
const getFormattedDate = (date) => {
  const dateArray = new Date(date).toString().split(" ");
  if (dateArray.length > 0) {
    return dateArray[2] + "-" + dateArray[1] + "-" + dateArray[3];
  } else {
    return "dd-mmm-yyyy";
  }
};

const getBillPeriod = (billDetails = []) => {
  let latest = billDetails.sort((x, y) => y.fromPeriod - x.fromPeriod);
  const billPeriod = getFormattedDate(latest[latest.length - 1].fromPeriod) + " to " + getFormattedDate(latest[0].toPeriod);
  return billPeriod;
};

const PaymentDetails = () => {
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  let { id: applicationNumber } = useParams();
  const index = applicationNumber.indexOf("?")
  if(index!=-1) applicationNumber = applicationNumber.slice(0,index)
  const [paymentObject, setPaymentObject] = useState([]);
  const isMobile = window.Digit.Utils.browser.isMobile();

  const { isLoading, isError, error, data } = Digit.Hooks.receipts.useReceiptsSearch(
    { businessServices: "PT", consumerCodes: applicationNumber },
    tenantId,
    [],
    false
  );
  useEffect(() => {
    console.log("data?.Payments",data?.Payments)
    if (data) {
      setPaymentObject(
        data?.Payments?.map((payment) => {
          return {
            receiptNumber: payment.paymentDetails[0].receiptNumber,
            billPeriod: getBillPeriod(payment.paymentDetails[0].bill.billDetails),
            transactionDate: getFormattedDate(payment.transactionDate),
            billNo: payment.paymentDetails[0].bill.billNumber,
            paymentStatus: payment.paymentStatus ? `CS_${payment.paymentStatus}` : "PT_NA",
            amountPaid: payment.totalAmountPaid === 0 ? "0" : payment.totalAmountPaid,
            tenantId: payment.tenantId,
            paymentDetails: payment.paymentDetails,
            mobileNumber:payment.mobileNumber,
            paidBy:payment.paidBy,
            payerName:payment.payerName,
            paymentMode:payment.paymentMode,
            payerEmail: payment.payerEmail,
            transactionNumber:payment.transactionNumber,
            fileStoreId:payment.fileStoreId
          };
        })
      );
    }
  }, [data]);
  if (isLoading) {
    return <Loader />;
  }

  return (
    <React.Fragment>
      <Header>{t("PT_PAYMENT_HISTORY")}</Header>
      <div style={{ display: "flex", paddingBottom: "8px", marginBottom: "8px" }}>
        <h2 style={isMobile ? {marginLeft:"15px", fontSize: "16px", lineHeight: "24px", width: "30%", fontWeight: "700" } : { fontSize: "16px", lineHeight: "24px", width: "30%", fontWeight: "700" }}>{t("PT_PROPERTY_PTUID")}</h2>
        <div style={{ whiteSpace: "pre", width: "50%", fontSize: "16px", lineHeight: "24px" }}>{applicationNumber}</div>
      </div>
      {paymentObject.length>0 ? paymentObject?.map((payment) => (
        <div style={isMobile ? {} : { marginLeft: "-16px" }}>
          <Card style={isMobile ? {marginBottom:"10px"} : {}}>
            <StatusTable>
              <Row label={t("PT_HISTORY_BILL_PERIOD")} text={payment?.billPeriod} textStyle={{ whiteSpace: "pre" }} />
              <Row label={t("PT_HISTORY_BILL_NO")} text={payment?.billNo} textStyle={{ whiteSpace: "pre" }} />
              <Row label={t("PT_HISTORY_RECEIPT_NO")} text={payment?.receiptNumber} textStyle={{ whiteSpace: "pre" }} />
              <Row label={t("PT_HISTORY_PAYMENT_DATE")} text={payment?.transactionDate} textStyle={{ whiteSpace: "pre" }} />
              <Row label={t("PT_HISTORY_AMOUNT_PAID")} text={payment?.amountPaid} />
              <Row label={t("PT_HISTORY_PAYMENT_STATUS")} text={t(payment?.paymentStatus)} textStyle={{ whiteSpace: "pre" }} />
              <LinkLabel style={isMobile ? {marginLeft:"0px"} : {}} onClick={() => printReciept(payment?.tenantId, payment)}>{t("PT_DOWNLOAD_RECEIPT")}</LinkLabel>
            </StatusTable>
          </Card>
        </div>
      )):<div>
        <CardText>{t("PT_NO_PAYMENTS_HISTORY")}</CardText>
        </div>}
    </React.Fragment>
  );
};

export default PaymentDetails;
