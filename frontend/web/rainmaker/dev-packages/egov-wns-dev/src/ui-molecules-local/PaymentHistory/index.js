import { Button, Card } from "components";
import React, { Component } from "react";
import { connect } from "react-redux";
import Label from "egov-ui-kit/utils/translationNode";
import { handleScreenConfigurationFieldChange as handleField, prepareFinalObject, toggleSnackbar, toggleSpinner } from "egov-ui-framework/ui-redux/screen-configuration/actions";
import get from "lodash/get";
import {
    getCommonHeader,
    getCommonCaption,
  } from "egov-ui-framework/ui-config/screens/specs/utils";
  import { httpRequest } from "egov-ui-framework/ui-utils/api";
  import store from "ui-redux/store";
  import { getFileUrlFromAPI, getTransformedLocale } from "egov-ui-framework/ui-utils/commons";
  import { getQueryArg } from "egov-ui-framework/ui-utils/commons";
  import { downloadPdf, openPdf, printPdf } from "egov-ui-kit/utils/commons";


class PaymentHistory extends Component {
    constructor(props) {
        super(props);
    }
    getCommonValue = (value, props = {}) => {
        return getCommonHeader(value, { variant: "body2", ...props });
    };
    getLabelWithValue = (label, value, props = {}) => {
        return {
          uiFramework: "custom-atoms",
          componentPath: "Div",
          gridDefination: {
            xs: 3,
            sm: 2
          },
          props: {
            style: {
              marginBottom: "16px",
              wordBreak : "break-word"
            },
            ...props
          },
          children: {
            label: getCommonCaption(label),
            value: this.getCommonValue(value)
          }
        };
    };

    getFullRow = (labelKey, labelValue, rowGrid = 12) => {
        let subRowGrid = 1;
        if (rowGrid == 6) {
            subRowGrid = 2;
        }
        return (<div className={`col-sm-${rowGrid} col-xs-12`} style={{ marginBottom: 1, marginTop: 1 }}>
            <div className={`col-sm-${2 * subRowGrid} col-xs-4`} style={{ padding: "3px 0px 0px 0px" }}>
                <Label
                    labelStyle={{ letterSpacing: 0, color: "rgba(0, 0, 0, 0.54)", fontWeight: "400", lineHeight: "19px !important" }}
                    label={labelKey}
                    fontSize="14px"
                />
            </div>
            <div className={`col-sm-${4 * subRowGrid} col-xs-8`} style={{ padding: "3px 0px 0px 0px", paddingLeft: rowGrid == 12 ? '10px' : '15px' }}>
                <Label
                    labelStyle={{ letterSpacing: "0.47px", color: "rgba(0, 0, 0, 1.87)", fontWeight: "400", lineHeight: "19px !important" }}
                    label={labelValue}
                    fontSize="14px"
                />
            </div>
        </div>)
    }
    downloadReceiptFromFilestoreID = (fileStoreId, mode, tenantId,showConfirmation=false) => {
        getFileUrlFromAPI(fileStoreId, tenantId).then(async (fileRes) => {
          if (fileRes && !fileRes[fileStoreId]) {
            console.error('ERROR IN DOWNLOADING RECEIPT');
            return;
          }
          if (mode === 'download') {
            if(localStorage.getItem('pay-channel')&&localStorage.getItem('pay-redirectNumber')){
              setTimeout(()=>{
                const weblink = "https://api.whatsapp.com/send?phone=" + localStorage.getItem('pay-redirectNumber') + "&text=" + ``;
                window.location.href = weblink
              },1500)
            }
            downloadPdf(fileRes[fileStoreId]);
            if(showConfirmation){
              store.dispatch(toggleSnackbar(true, { labelName: "Success in Receipt Generation", labelKey: "SUCCESS_IN_GENERATION_RECEIPT" }
            , "success"));
            }
          } else if (mode === 'open') {
            if(localStorage.getItem('pay-channel')&&localStorage.getItem('pay-redirectNumber')){
              setTimeout(()=>{
                const weblink = "https://api.whatsapp.com/send?phone=" + localStorage.getItem('pay-redirectNumber') + "&text=" + ``;
                window.location.href = weblink
              },1500)
            }
            openPdf(fileRes[fileStoreId], '_self')
            if(showConfirmation){
              store.dispatch(toggleSnackbar(true, { labelName: "Success in Receipt Generation", labelKey: "SUCCESS_IN_GENERATION_RECEIPT" }
            , "success"));
            }
          }
          else {
            printPdf(fileRes[fileStoreId]);
          }
        });
      }
   
	  getPaymentSearchAPI = (businessService='')=>{
       
		if(businessService=='-1'){
		  return `/collection-services/payments/_search`
		}else if (process.env.REACT_APP_NAME === "Citizen") {
		  return `/collection-services/payments/_search`;
		}
		return '/collection-services/payments/'+businessService+'/_search';
	  }

 convertEpochToDate = dateEpoch => {
        const dateFromApi = new Date(dateEpoch);
        let month = dateFromApi.getMonth() + 1;
        let day = dateFromApi.getDate();
        let year = dateFromApi.getFullYear();
        month = (month > 9 ? "" : "0") + month;
        day = (day > 9 ? "" : "0") + day;
        return `${day}/${month}/${year}`;
      };
      
downloadReceiptpt = async(receiptQueryString) => {
           const configKey="ws-onetime-receipt";
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
      
            let tenantId = getQueryArg(window.location.href, "tenantId")?getQueryArg(window.location.href, "tenantId"):receiptQueryString[1].value;

        
            let businessService = '';
            receiptQueryString && Array.isArray(receiptQueryString) && receiptQueryString.map(query => {
              if (query.key == "businessService") {
                businessService = query.value;
              }
            })
            receiptQueryString = receiptQueryString && Array.isArray(receiptQueryString) && receiptQueryString.filter(query => query.key != "businessService")
            try {
              await httpRequest("post",this.getPaymentSearchAPI(businessService), FETCHRECEIPT.GET.ACTION, receiptQueryString).then((payloadReceiptDetails) => {
       if(payloadReceiptDetails.Payments[0].payerName!=null){
            payloadReceiptDetails.Payments[0].payerName=payloadReceiptDetails.Payments[0].payerName.trim();}
            else if(payloadReceiptDetails.Payments[0].payerName == null && payloadReceiptDetails.Payments[0].paymentDetails[0].businessService=="FIRENOC" && payloadReceiptDetails.Payments[0].paidBy !=null)
             { payloadReceiptDetails.Payments[0].payerName=payloadReceiptDetails.Payments[0].paidBy.trim();
            }
            if(payloadReceiptDetails.Payments[0].paidBy!=null)
            {
              payloadReceiptDetails.Payments[0].paidBy=payloadReceiptDetails.Payments[0].paidBy.trim();
            }
                
            if(payloadReceiptDetails.Payments[0].paymentDetails[0].businessService=="WS" || payloadReceiptDetails.Payments[0].paymentDetails[0].businessService=="SW"){
              let dcbRow=null,dcbArray=[];
              let installment,totalamount=0;
              payloadReceiptDetails.Payments[0].paymentDetails[0].bill.billDetails.map((element,index) => {
            if(element.amountPaid >0 || element.amountPaid < 0)
            {
            installment=this.convertEpochToDate(element.fromPeriod) +"-"+this.convertEpochToDate(element.toPeriod);
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
               
      
               const queryStr = [
                  { key: "key", value: configKey },
                  { key: "tenantId", value: receiptQueryString[1].value.split('.')[0] }
                ]
                if (payloadReceiptDetails && payloadReceiptDetails.Payments && payloadReceiptDetails.Payments.length == 0) {
                  console.log("Could not find any receipts");
                  store.dispatch(toggleSnackbar(true, { labelName: "Receipt not Found", labelKey: "ERR_RECEIPT_NOT_FOUND" }
                    , "error"));
                  return;
                }
                // Setting the Payer and mobile from Bill to reflect it in PDF
               // state = state ? state : {};
            //     if(payloadReceiptDetails.Payments[0].paymentMode=="CHEQUE" || payloadReceiptDetails.Payments[0].paymentMode=="DD" || payloadReceiptDetails.Payments[0].paymentMode=="OFFLINE_NEFT" || payloadReceiptDetails.Payments[0].paymentMode=="OFFLINE_RTGS" || payloadReceiptDetails.Payments[0].paymentMode=="ONLINE"){
            //       let ifsc = get(state, "screenConfiguration.preparedFinalObject.ReceiptTemp[0].instrument.ifscCode", null);
            //       let branchName = get(state, "screenConfiguration.preparedFinalObject.ReceiptTemp[0].instrument.branchName", null);
            //       let bank = get(state, "screenConfiguration.preparedFinalObject.ReceiptTemp[0].instrument.bank.name", null);
            //       payloadReceiptDetails.Payments[0].ifscCode=ifsc; 
            //       const details = [{
            //          "branchName": branchName ,
            //         "bankName":bank }
            //       ]       
            //     payloadReceiptDetails.Payments[0].additionalDetails=details; 
            //   }
                // let billDetails = get(state, "screenConfiguration.preparedFinalObject.ReceiptTemp[0].Bill[0]", null);
                // if ((billDetails && !billDetails.payerName) || !billDetails) {
                //   billDetails = {
                //     payerName: get(state, "screenConfiguration.preparedFinalObject.applicationDataForReceipt.owners[0].name", null) || get(state, "screenConfiguration.preparedFinalObject.applicationDataForPdf.owners[0].name", null),
                //     mobileNumber: get(state, "screenConfiguration.preparedFinalObject.applicationDataForReceipt.owners[0].mobile", null) || get(state, "screenConfiguration.preparedFinalObject.applicationDataForPdf.owners[0].mobile", null),
                //   };
                // }
                
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
                  this.downloadReceiptFromFilestoreID(oldFileStoreId, "download",undefined,false)
                }
                else {
                  httpRequest("post", DOWNLOADRECEIPT.GET.URL, DOWNLOADRECEIPT.GET.ACTION, queryStr, { Payments: payloadReceiptDetails.Payments }, { 'Accept': 'application/json' }, { responseType: 'arraybuffer' })
                    .then(res => {
                      res.filestoreIds[0]
                      if (res && res.filestoreIds && res.filestoreIds.length > 0) {
                        res.filestoreIds.map(fileStoreId => {
                          this.downloadReceiptFromFilestoreID(fileStoreId, "download",undefined,false)
                        })
                      } else {
                        console.log('Some Error Occured while downloading Receipt!');
                        store.dispatch(toggleSnackbar(true, { labelName: "Error in Receipt Generation", labelKey: "ERR_IN_GENERATION_RECEIPT" }
                          , "error"));
                      }
                    });
                }
              })
            } catch (exception) {
              console.log('Some Error Occured while downloading Receipt!');
              store.dispatch(toggleSnackbar(true, { labelName: "Error in Receipt Generation", labelKey: "ERR_IN_GENERATION_RECEIPT" }
                , "error"));
            }
          }
      

    getTransformedPaymentHistory() {
        const labelStyle = {
            letterSpacing: 1.2,
            fontWeight: "600",
            lineHeight: "35px",
        };
        const buttonStyle = {
            float: 'right',
            lineHeight: "35px",
            height: "35px",
            backgroundColor: "rgb(242, 242, 242)",
            boxShadow: "none",
            border: "1px solid rgb(254, 122, 81)",
            borderRadius: "2px",
            outline: "none",
            alignItems: "right",
        };
        const { paymentHistory = [] } = this.props;
        const paymentHistoryItems = paymentHistory.map((payment, index) => {
            return (
                <div>
                    { this.getFullRow("Receipt No", payment.receiptNumber ? payment.receiptNumber : "NA", 12 ) } 
                    { this.getFullRow("Receipt Date", payment.receiptDate ? payment.receiptDate : "NA", 12 ) } 
                    { this.getFullRow("Total Due", payment.totalDue ? `Rs ${payment.totalDue}` : "NA", 12 ) } 
                    { this.getFullRow("Total Paid", payment.totalAmountPaid ? `Rs ${payment.totalAmountPaid}` : "NA", 6 ) } 

                    <div className="col-sm-6 col-xs-12" style={{ marginBottom: 10, marginTop: 5 }}>
                        <div className="assess-history" style={{ float: "right" }}>
                            <Button 
                                label={<Label buttonLabel={true} label="Download Receipt" color="rgb(254, 122, 81)" fontSize="16px" height="35px" labelStyle={labelStyle} />}
                                buttonStyle={buttonStyle}
                                onClick={() => {
                                    const receiptQueryString= [
                                            { key: "receiptNumbers", value: payment.receiptNumber },
                                            { key: "tenantId", value: payment.tenant },
                                            { key: "businessService", value: payment.service }
                                          ]
                                    this.downloadReceiptpt(receiptQueryString)
                                }}
                            ></Button>
                        </div>
                    </div >
                </div>)

        })
        return paymentHistoryItems;
    }

    render() {
        const { paymentHistory, backgroundColor = 'rgb(242, 242, 242)' } = this.props;
        let paymentHistoryItem = [];
        if (paymentHistory.length > 0) {
            paymentHistoryItem = this.getTransformedPaymentHistory();
        }
        return (
            <div>
                {paymentHistoryItem && 
                <Card style={{ backgroundColor, boxShadow: 'none' }}
                    textChildren={
                        <div>{paymentHistoryItem}</div>
                    }
                />
                }
            </div>
        )
    }
}

const mapStateToProps = (state, ownProps) => {
    const paymentHistory = get(
        state.screenConfiguration.preparedFinalObject,
        ownProps.jsonpath,
        []
      );
    return { paymentHistory };
};


const mapDispatchToProps = (dispatch) => {
    return {
     downloadReceiptpt: (receiptQueryString) => dispatch(downloadReceiptpt(receiptQueryString)),
    };
  };

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(PaymentHistory);
