import { Card, CardSubHeader,CardSectionHeader, Header, LinkButton, Loader, Row, StatusTable, MultiLink, PopUp, Toast, SubmitBar, CardHeader } from "@upyog/digit-ui-react-components";
import React, { useEffect, useMemo, useState } from "react";
import { useTranslation } from "react-i18next";
import { format } from 'date-fns';

import { useHistory, useParams,Link } from "react-router-dom";
import getChbAcknowledgementData from "../../getChbAcknowledgementData";
import getChbPaymentReceipt from "../../getChbPaymentReceipt";
import CHBWFApplicationTimeline from "../../pageComponents/CHBWFApplicationTimeline";
import CHBDocument from "../../pageComponents/CHBDocument";
import { pdfDownloadLink } from "../../utils";


import get from "lodash/get";
import { size } from "lodash";

const CHBApplicationDetails = () => {
  const { t } = useTranslation();
  const history = useHistory();
  const { acknowledgementIds, tenantId } = useParams();
  const [acknowldgementData, setAcknowldgementData] = useState([]);
  const [showOptions, setShowOptions] = useState(false);
  const [popup, setpopup] = useState(false);
  const [showToast, setShowToast] = useState(null);
  // const tenantId = Digit.ULBService.getCurrentTenantId();
  const { data: storeData } = Digit.Hooks.useStore.getInitData();
  const { tenants } = storeData || {};
 

  const { isLoading, isError,error, data } = Digit.Hooks.chb.useChbSearch(
    {
      tenantId,
      filters: { bookingNo: acknowledgementIds },
    },
  );
 
  const [billData, setBillData]=useState(null);


// Getting HallsBookingDetails 
  const hallsBookingApplication = get(data, "hallsBookingApplication", []);
  const chbId = get(data, "hallsBookingApplication[0].bookingNo", []);
   
  let  chb_details = (hallsBookingApplication && hallsBookingApplication.length > 0 && hallsBookingApplication[0]) || {};
  const application =  chb_details;
  console.log("application-->",application);
  
  sessionStorage.setItem("chb", JSON.stringify(application));

  

  const [loading, setLoading]=useState(false);

  const fetchBillData=async()=>{
    setLoading(true);
    const result= await Digit.PaymentService.fetchBill(tenantId,{ businessService: "chb-services", consumerCode: acknowledgementIds, });
  
  setBillData(result);
  setLoading(false);
};
  useEffect(()=>{
  fetchBillData();
}, [tenantId, acknowledgementIds]); 


  const { isLoading: auditDataLoading, isError: isAuditError, data: auditResponse } = Digit.Hooks.chb.useChbSearch(
    {
      tenantId,
      filters: { bookingNo: chbId, audit: true },
    },
    {
      enabled: true,
    }
  );

  const { data: reciept_data, isLoading: recieptDataLoading } = Digit.Hooks.useRecieptSearch(
    {
      tenantId: tenantId,
      businessService: "chb-services",
      consumerCodes: acknowledgementIds,
      isEmployee: false,
    },
    { enabled: acknowledgementIds ? true : false }
  );

  
//WorkFlow
  if (!chb_details.workflow) {
    let workflow = {
      id: null,
      tenantId: tenantId,
      businessService: "chb-services",
      businessId: application?.bookingNo,
      action: "",
      moduleName: "chb-services",
      state: null,
      comment: null,
      documents: null,
      assignes: null,
    };
     chb_details.workflow = workflow;
  }

  

  

 
  // let owners = [];
  // owners = application?.owners;
  let docs = [];
  docs = application?.documents;
  console.log("docs",docs);

  if (isLoading || auditDataLoading) {
    return <Loader />;
  }

 

  // const getAcknowledgementData = async () => {
  //   const applications = application || {};
  //   const tenantInfo = tenants.find((tenant) => tenant.code === applications.tenantId);
  //   const acknowldgementDataAPI = await getChbAcknowledgementData({ ...applications }, tenantInfo, t);
  //   Digit.Utils.pdf.generate(acknowldgementDataAPI);
    
  //   //setAcknowldgementData(acknowldgementDataAPI);
  // };


// for Generating Payment Receipt 
   const getPaymentReceiptData = async () => {
    const applications = application || {}; // getting application details 
    const tenantInfo = tenants.find((tenant) => tenant.code === applications.tenantId);
    console.log("Tenant Info:", tenantInfo);
    const acknowldgementDataAPI = await getChbPaymentReceipt({ ...applications }, tenantInfo, t);
    Digit.Utils.pdf.generate(acknowldgementDataAPI);
    
  };


  let documentDate = t("CS_NA");
  if ( chb_details?.additionalDetails?.documentDate) {
    const date = new Date( chb_details?.additionalDetails?.documentDate);
    const month = Digit.Utils.date.monthNames[date.getMonth()];
    documentDate = `${date.getDate()} ${month} ${date.getFullYear()}`;
  }

  async function getRecieptSearch({ tenantId, payments, ...params }) {
    let response = { filestoreIds: [payments?.fileStoreId] };
    response = await Digit.PaymentService.generatePdf(tenantId, { Payments: [{ ...payments }] }, "chbservice-receipt");
    const fileStore = await Digit.PaymentService.printReciept(tenantId, { fileStoreIds: response.filestoreIds[0] });
    window.open(fileStore[response?.filestoreIds[0]], "_blank");
  };

  const handleDownload = async (document, tenantid) => {
    let tenantId = tenantid ? tenantid : tenantId;
    const res = await Digit.UploadServices.Filefetch([document?.fileStoreId], tenantId);
    let documentLink = pdfDownloadLink(res.data, document?.fileStoreId);
    window.open(documentLink, "_blank");
  };

  const printCertificate = async () => {
    let response = await Digit.PaymentService.generatePdf(tenantId, { hallsBookingApplication: [data?.hallsBookingApplication?.[0]] }, "petservicecertificate");
    const fileStore = await Digit.PaymentService.printReciept(tenantId, { fileStoreIds: response.filestoreIds[0] });
    window.open(fileStore[response?.filestoreIds[0]], "_blank");
  };

  let dowloadOptions = [];

// Acknowledge Button on Acknowledgement Page
  // dowloadOptions.push({
  //   label: t("CHB_ACKNOWLEDGEMENT"),
  //   onClick: () => getAcknowledgementData(),

  // });



// Payment Receipt Button on Acknowledgement Page
  dowloadOptions.push({
    label: t("CHB_PAYMENT_RECEIPT"),
    onClick: () => getPaymentReceiptData(),

  });

  //commented out, need later for download receipt and certificate 
  if (reciept_data && reciept_data?.Payments.length > 0 && recieptDataLoading == false)
    dowloadOptions.push({
      label: t("CHB_FEE_RECIEPT"),
      onClick: () => getRecieptSearch({ tenantId: reciept_data?.Payments[0]?.tenantId, payments: reciept_data?.Payments[0] }),
    });

    
  if (reciept_data?.Payments[0]?.paymentStatus === "DEPOSITED")
    dowloadOptions.push({
      label: t("CHB_CERTIFICATE"),
      onClick: () => printCertificate(),
    });
  const timestamp = chb_details?.bookingSlotDetails[0]?.bookingDate;
  const date = new Date(timestamp);
  
  return (
    <React.Fragment>
      <div>
        <div className="cardHeaderWithOptions" style={{ marginRight: "auto", maxWidth: "960px" }}>
          <Header styles={{ fontSize: "32px" }}>{t("CHB_BOOKING_DETAILS")}</Header>
          {dowloadOptions && dowloadOptions.length > 0 && (
            <MultiLink
              className="multilinkWrapper"
              onHeadClick={() => setShowOptions(!showOptions)}
              displayOptions={showOptions}
              options={dowloadOptions}
            />
          )}
        </div>
        <Card>
          <StatusTable>
            <Row
              className="border-none"
              label={t("CHB_BOOKING_NO")}
              text={chb_details?.bookingNo} 
            />

          </StatusTable>

          <CardSubHeader style={{ fontSize: "24px" }}>{t("CHB_APPLICANT_DETAILS")}</CardSubHeader>
          <StatusTable>
          {/* <Row className="border-none" label={t("CHB_APPLICANT_NAME")} text="checkkiiinggggg something "/> */}
            <Row className="border-none" label={t("CHB_APPLICANT_NAME")} text={chb_details?.applicantName || t("CS_NA")} />
            <Row className="border-none" label={t("CHB_MOBILE_NUMBER")} text={chb_details?.applicantMobileNo || t("CS_NA")} />
            <Row className="border-none" label={t("CHB_ALT_MOBILE_NUMBER")} text={chb_details?.applicantAlternateMobileNo || t("CS_NA")} />
            <Row className="border-none" label={t("CHB_EMAIL_ID")} text={chb_details?.applicantEmailId || t("CS_NA")} />
          </StatusTable>

          <CardSubHeader style={{ fontSize: "24px" }}>{t("CHB_EVENT_DETAILS")}</CardSubHeader>
          <StatusTable>
            <Row className="border-none" label={t("CHB_RESIDENT_TYPE")} text={chb_details?.residentType?.type || t("CS_NA")} />
            <Row className="border-none" label={t("CHB_SPECIAL_CATEGORY")} text={chb_details?.specialCategory?.category || t("CS_NA")} />
            <Row className="border-none" label={t("CHB_PURPOSE")} text={chb_details?.purpose?.purpose || t("CS_NA")} />
            <Row className="border-none" label={t("CHB_PURPOSE_DESCRIPTION")} text={chb_details?.purposeDescription || t("CS_NA")} />
          </StatusTable>
          
          <CardSubHeader style={{ fontSize: "24px" }}>{t("SLOT_DETAILS")}</CardSubHeader>
            {chb_details?.bookingSlotDetails.map((slot)=>(
              <StatusTable>
              <Row className="border-none" label={t("CHB_COMMUNITY_HALL_NAME")} text={slot?.hallName || t("CS_NA")} />
              <Row className="border-none" label={t("CHB_BOOKING_DATE")} text={slot?.bookingDate} />
              <Row className="border-none" label={t("CHB_BOOKING_FROM_TIME")} text={slot?.
                bookingFromTime
                 || t("CS_NA")} />
              <Row className="border-none" label={t("CHB_BOOKING_TO_TIME")} text={slot?.bookingToTime || t("CS_NA")} />
              </StatusTable>
            ))}
          <CardSubHeader style={{ fontSize: "24px" }}>{t("CHB_BANK_DETAILS")}</CardSubHeader>
          <StatusTable>
            <Row className="border-none" label={t("CHB_ACCOUNT_NUMBER")} text={chb_details?.bankDetails?.accountNumber || t("CS_NA")} />
            <Row className="border-none" label={t("CHB_IFSC_CODE")} text={chb_details?.bankDetails?.ifscCode || t("CS_NA")} />
            <Row className="border-none" label={t("CHB_BANK_NAME")} text={chb_details?.bankDetails?.bankName || t("CS_NA")} />
            <Row className="border-none" label={t("CHB_BANK_BRANCH_NAME")} text={chb_details?.bankDetails?.bankBranchName || t("CS_NA")} />
            <Row className="border-none" label={t("CHB_ACCOUNT_HOLDER_NAME")} text={chb_details?.bankDetails?.accountHolderName || t("CS_NA")} />
          </StatusTable>

          <CardSubHeader style={{ fontSize: "24px" }}>{t("CHB_DOCUMENTS_DETAILS")}</CardSubHeader>
        <Card style={{paddingRight:"16px"}}>
        {docs.map((doc, index) => (
          <div key={`doc-${index}`}>
         {<div><CardSectionHeader>{t("CHB_" +(doc?.documentType?.split('.').slice(0,2).join('_')))}</CardSectionHeader>
          <StatusTable>
          {
           <CHBDocument value={docs} Code={doc?.documentType} index={index} /> }

          </StatusTable>
          </div>}
          </div>
        ))}
        
      </Card>
          
          <CHBWFApplicationTimeline application={application} id={application?.bookingNo} userType={"citizen"} />
          {showToast && (
          <Toast
            error={showToast.key}
            label={t(showToast.label)}
            style={{bottom:"0px"}}
            onClose={() => {
              setShowToast(null);
            }}
          />
        )}
        </Card>

        {popup && <PTCitizenFeedbackPopUp setpopup={setpopup} setShowToast={setShowToast} data={data} />}
      </div>
    </React.Fragment>
  );
};

export default CHBApplicationDetails;
            
           
           
            

         

        
