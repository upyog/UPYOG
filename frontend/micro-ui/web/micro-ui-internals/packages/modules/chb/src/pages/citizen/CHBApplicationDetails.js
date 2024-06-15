import { Card, CardSubHeader, Header, LinkButton, Loader, Row, StatusTable, MultiLink, PopUp, Toast, SubmitBar } from "@upyog/digit-ui-react-components";
import React, { useEffect, useMemo, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory, useParams } from "react-router-dom";
import getChbAcknowledgementData from "../../getChbAcknowledgementData";
import CHBWFApplicationTimeline from "../../pageComponents/CHBWFApplicationTimeline";
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
      filters: { applicationNumber: acknowledgementIds },
    },
  );


 

 
  const [billData, setBillData]=useState(null);

  



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
      filters: { applicationNumber: chbId, audit: true },
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

  

  if (!chb_details.workflow) {
    let workflow = {
      id: null,
      tenantId: tenantId,
      businessService: "chb-services",
      businessId: application?.applicationNumber,
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
  // let docs = [];
  // docs = application?.documents;

  if (isLoading || auditDataLoading) {
    return <Loader />;
  }

 

  const getAcknowledgementData = async () => {
    const applications = application || {};
    const tenantInfo = tenants.find((tenant) => tenant.code === applications.tenantId);
    const acknowldgementDataAPI = await getChbAcknowledgementData({ ...applications }, tenantInfo, t);
    Digit.Utils.pdf.generate(acknowldgementDataAPI);
    //setAcknowldgementData(acknowldgementDataAPI);
  };

  let documentDate = t("CS_NA");
  if ( chb_details?.additionalDetails?.documentDate) {
    const date = new Date( chb_details?.additionalDetails?.documentDate);
    const month = Digit.Utils.date.monthNames[date.getMonth()];
    documentDate = `${date.getDate()} ${month} ${date.getFullYear()}`;
  }

  async function getRecieptSearch({ tenantId, payments, ...params }) {
    let response = { filestoreIds: [payments?.fileStoreId] };
    response = await Digit.PaymentService.generatePdf(tenantId, { Payments: [{ ...payments }] }, "petservice-receipt");
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

  dowloadOptions.push({
    label: t("CHB_DOWNLOAD_ACK_FORM"),
    onClick: () => getAcknowledgementData(),
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
              label={t("CHB_BOOKING_NO_LABEL")}
              text={chb_details?.applicationNumber} 
            />
          </StatusTable>

          <CardSubHeader style={{ fontSize: "24px" }}>{t("CHB_APPLICANT_DETAILS_HEADER")}</CardSubHeader>
          <StatusTable>
            <Row className="border-none" label={t("CHB_APPLICANT_NAME")} text={chb_details?.applicantName || t("CS_NA")} />
            <Row className="border-none" label={t("CHB_APPLICANT_MOBILE_NO")} text={chb_details?.mobileNumber || t("CS_NA")} />
            <Row className="border-none" label={t("CHB_APPLICANT_EMAILID")} text={chb_details?.emailId || t("CS_NA")} />
          </StatusTable>

          <CardSubHeader style={{ fontSize: "24px" }}>{t("CHB_SLOT_DETAILS_HEADER")}</CardSubHeader>
          <StatusTable>
            <Row className="border-none" label={t("SELECT_SLOT")} text={chb_details?.slots?.selectslot || t("CS_NA")} />
            <Row className="border-none" label={t("RESIDENT_TYPE")} text={chb_details?.slots?.residentType || t("CS_NA")} />
            <Row className="border-none" label={t("SPECIAL_CATEGORY")} text={chb_details?.slots?.specialCategory || t("CS_NA")} />
            <Row className="border-none" label={t("PURPOSE")} text={chb_details?.slots?.purpose || t("CS_NA")} />
          </StatusTable>

          <CardSubHeader style={{ fontSize: "24px" }}>{t("CHB_BANK_DETAILS")}</CardSubHeader>
          <StatusTable>
            <Row className="border-none" label={t("ACCOUNT_NUMBER")} text={chb_details?.bankdetails?.accountNumber || t("CS_NA")} />
            <Row className="border-none" label={t("CONFIRM_ACCOUNT_NUMBER")} text={chb_details?.bankdetails?.confirmAccountNumbers || t("CS_NA")} />
            <Row className="border-none" label={t("IFSC_CODE")} text={chb_details?.bankdetails?.ifscCode || t("CS_NA")} />
            <Row className="border-none" label={t("BANK_NAME")} text={chb_details?.bankdetails?.bankName || t("CS_NA")} />
            <Row className="border-none" label={t("BANK_BRANCH_NAME")} text={chb_details?.bankdetails?.bankBranchName || t("CS_NA")} />
            <Row className="border-none" label={t("ACCOUNT_HOLDER_NAME")} text={chb_details?.bankdetails?.accountHolderName || t("CS_NA")} />
          </StatusTable>


          {/* <CardSubHeader style={{ fontSize: "24px" }}>{t("PTR_DOCUMENT_DETAILS")}</CardSubHeader>
          <div>
            {Array.isArray(docs) ? (
              docs.length > 0 && <PTRDocument chb_details={chb_details}></PTRDocument>
            ) : (
              <StatusTable>
                <Row className="border-none" text={t("PTR_NO_DOCUMENTS_MSG")} />
              </StatusTable>
            )}
          </div> */}
          
          <CHBWFApplicationTimeline application={application} id={application?.applicationNumber} userType={"citizen"} />
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
            
           
           
            

         

        
