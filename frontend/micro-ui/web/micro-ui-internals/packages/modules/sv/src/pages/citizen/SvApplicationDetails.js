import { Card, CardSubHeader, Header, LinkButton, Loader, Row, StatusTable, MultiLink, PopUp, Toast, SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useEffect, useMemo, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory, useParams } from "react-router-dom";
// import { pdfDownloadLink } from "../../utils";
import ViewTimeline from "../../components/ViewTimeline";
import get from "lodash/get";

/**
 * Component is still in development so please ignore as this code is crucial to fetch the Bill details, so that payment button but for now it is 
 * doing his job but fetching the user data and show in the page
 * 
 * TODO:  
 * 1. Add a Configure it for Bill search and collection Service 
 * 2. Add the Download buttons for Certificate and Bill Search
 * 3. Add the logic for Receipt search and bill search
 * 
 */


const SvApplicationDetails = () => {
  const { t } = useTranslation();
  const history = useHistory();
  const { applicationNo, tenantId } = useParams();
  const [acknowldgementData, setAcknowldgementData] = useState([]);
  const [showOptions, setShowOptions] = useState(false);
  const [popup, setpopup] = useState(false);
  const [showToast, setShowToast] = useState(null);
  const { data: storeData } = Digit.Hooks.useStore.getInitData();
  const { tenants } = storeData || {};
 
  const { isLoading, data } = Digit.Hooks.sv.useSvSearchApplication(
    {
      tenantId,
      filters: { applicationNo: applicationNo },
    },
  ); 
  const [billData, setBillData]=useState(null);

  


   const SVDetail = get(data, "SVDetail", []);
  
  
//   const petId = get(data, "PetRegistrationApplications[0].applicationNumber", []);
  
  let  streetVendingDetails = (SVDetail && SVDetail.length > 0 && SVDetail[0]) || {};
  console.log("streetVendingDetailsstreetVendingDetails",streetVendingDetails);
  const application =  streetVendingDetails;

  
  sessionStorage.setItem("ptr-pet", JSON.stringify(application));

  

  const [loading, setLoading]=useState(false);

  const fetchBillData=async()=>{
    setLoading(true);
    const result= await Digit.PaymentService.fetchBill(tenantId,{ businessService: "sv-services", consumerCode: applicationNo, });
  
  setBillData(result);
  setLoading(false);
};
useEffect(()=>{
fetchBillData();
}, [tenantId, applicationNo]); 

//   const { isLoading: auditDataLoading, isError: isAuditError, data: auditResponse } = Digit.Hooks.ptr.usePTRSearch(
//     {
//       tenantId,
//       filters: { applicationNumber: petId, audit: true },
//     },
//     {
//       enabled: true,
      
//     }
//   );

//   const { data: reciept_data, isLoading: recieptDataLoading } = Digit.Hooks.useRecieptSearch(
//     {
//       tenantId: tenantId,
//       businessService: "pet-services",
//       consumerCodes: applicationNo,
//       isEmployee: false,
//     },
//     { enabled: applicationNo ? true : false }
//   );

  if (!streetVendingDetails.workflow) {
    let workflow = {
      id: null,
      tenantId: tenantId,
      businessService: "street-vending",
      businessId: application?.applicationNo,
      action: "",
      moduleName: "sv-services",
      state: null,
      comment: null,
      documents: null,
      assignes: null,
    };
     streetVendingDetails.workflow = workflow;
  }

  

  

 
  // let owners = [];
  // owners = application?.owners;
  // let docs = [];
  // docs = application?.documents;

  if (isLoading) {
    return <Loader />;
  }

 

//   const getAcknowledgementData = async () => {
//     const applications = application || {};
//     const tenantInfo = tenants.find((tenant) => tenant.code === applications.tenantId);
//     const acknowldgementDataAPI = await getPetAcknowledgementData({ ...applications }, tenantInfo, t);
//     Digit.Utils.pdf.generate(acknowldgementDataAPI);
//     //setAcknowldgementData(acknowldgementDataAPI);
//   };

//   let documentDate = t("CS_NA");
//   if ( streetVendingDetails?.additionalDetails?.documentDate) {
//     const date = new Date( streetVendingDetails?.additionalDetails?.documentDate);
//     const month = Digit.Utils.date.monthNames[date.getMonth()];
//     documentDate = `${date.getDate()} ${month} ${date.getFullYear()}`;
//   }

//   async function getRecieptSearch({ tenantId, payments, ...params }) {
//     let response = { filestoreIds: [payments?.fileStoreId] };
//     response = await Digit.PaymentService.generatePdf(tenantId, { Payments: [{ ...payments }] }, "petservice-receipt");
//     const fileStore = await Digit.PaymentService.printReciept(tenantId, { fileStoreIds: response.filestoreIds[0] });
//     window.open(fileStore[response?.filestoreIds[0]], "_blank");
//   };

//   const handleDownload = async (document, tenantid) => {
//     let tenantId = tenantid ? tenantid : tenantId;
//     const res = await Digit.UploadServices.Filefetch([document?.fileStoreId], tenantId);
//     let documentLink = pdfDownloadLink(res.data, document?.fileStoreId);
//     window.open(documentLink, "_blank");
//   };

//   const printCertificate = async () => {
//     let response = await Digit.PaymentService.generatePdf(tenantId, { PetRegistrationApplications: [data?.PetRegistrationApplications?.[0]] }, "petservicecertificate");
//     const fileStore = await Digit.PaymentService.printReciept(tenantId, { fileStoreIds: response.filestoreIds[0] });
//     window.open(fileStore[response?.filestoreIds[0]], "_blank");
//   };

//   let dowloadOptions = [];

//   dowloadOptions.push({
//     label: t("PTR_PET_DOWNLOAD_ACK_FORM"),
//     onClick: () => getAcknowledgementData(),
//   });

  //commented out, need later for download receipt and certificate 
//   if (reciept_data && reciept_data?.Payments.length > 0 && recieptDataLoading == false)
//     dowloadOptions.push({
//       label: t("PTR_FEE_RECIEPT"),
//       onClick: () => getRecieptSearch({ tenantId: reciept_data?.Payments[0]?.tenantId, payments: reciept_data?.Payments[0] }),
//     });
//   if (data?.ResponseInfo?.status === "successful")
//     dowloadOptions.push({
//       label: t("PTR_CERTIFICATE"),
//       onClick: () => printCertificate(),
//     });
  
  return (
    <React.Fragment>
      <div>
        <div className="cardHeaderWithOptions" style={{ marginRight: "auto", maxWidth: "960px" }}>
          <Header styles={{ fontSize: "32px" }}>{t("SV_APPLICATION_DETAILS")}</Header>
          {/* {dowloadOptions && dowloadOptions.length > 0 && (
            <MultiLink
              className="multilinkWrapper"
              onHeadClick={() => setShowOptions(!showOptions)}
              displayOptions={showOptions}
              options={dowloadOptions}
            />
          )} */}
        </div>
        <Card>
          <StatusTable>
            <Row
              className="border-none"
              label={t("SV_APPLICATION_NUMBER")}
              text={streetVendingDetails?.applicationNo} 
            />
          </StatusTable>
           
          <CardSubHeader style={{ fontSize: "24px" }}>{t("SV_VENDOR_PERSONAL_DETAILS")}</CardSubHeader>
          <StatusTable>
            <Row className="border-none" label={t("SV_VENDOR_NAME")} text={streetVendingDetails?.vendorDetail?.[0]?.name || t("CS_NA")} />
            <Row className="border-none" label={t("SV_FATHER_NAME")} text={streetVendingDetails?.vendorDetail?.[0]?.fatherName || t("CS_NA")} />
            <Row className="border-none" label={t("SV_REGISTERED_MOB_NUMBER")} text={streetVendingDetails?.vendorDetail?.[0]?.mobileNo || t("CS_NA")} />
            <Row className="border-none" label={t("SV_DATE_OF_BIRTH")} text={streetVendingDetails?.vendorDetail?.[0]?.dob || t("CS_NA")} />
            <Row className="border-none" label={t("SV_EMAIL")} text={streetVendingDetails?.vendorDetail?.[0]?.emailId || t("CS_NA")} />
          </StatusTable>
          <CardSubHeader style={{ fontSize: "24px" }}>{t("SV_ADDRESS_DETAILS")}</CardSubHeader>
          <StatusTable>
          <Row className="border-none" label={t("SV_HOUSE_NO")} text={streetVendingDetails?.addressDetails?.[0]?.houseNo || t("CS_NA")} />
          <Row className="border-none" label={t("SV_ADDRESS_LINE1")} text={streetVendingDetails?.addressDetails?.[0]?.addressLine1 || t("CS_NA")} />
          <Row className="border-none" label={t("SV_ADDRESS_LINE2")} text={streetVendingDetails?.addressDetails?.[0]?.addressLine2 || t("CS_NA")} />
            <Row className="border-none" label={t("SV_ADDRESS_PINCODE")} text={streetVendingDetails?.addressDetails?.[0]?.pincode || t("CS_NA")} />
            <Row className="border-none" label={t("SV_CITY")} text={streetVendingDetails?.addressDetails?.[0]?.city || t("CS_NA")} />
            <Row className="border-none" label={t("SV_LOCALITY")} text={streetVendingDetails?.addressDetails?.[0]?.locality || t("CS_NA")} />
          </StatusTable>

          

          <CardSubHeader style={{ fontSize: "24px" }}>{t("SV_VENDOR_BUSINESS_DETAILS")}</CardSubHeader>
          <StatusTable>
            <Row className="border-none" label={t("SV_VENDING_ZONES")} text={streetVendingDetails?.vendingZone || t("CS_NA")} />
            <Row className="border-none" label={t("SV_VENDING_TYPE")} text={streetVendingDetails?.vendingActivity || t("CS_NA")} />
            <Row className="border-none" label={t("SV_AREA_REQUIRED")} text={streetVendingDetails?.vendingArea || t("CS_NA")} />
            <Row className="border-none" label={t("SV_LOCAL_AUTHORITY_NAME")} text={streetVendingDetails?.localAuthorityName || t("CS_NA")} />
            <Row className="border-none" label={t("SV_BENEFICIARY_SCHEMES")} text={streetVendingDetails?.benificiaryOfSocialSchemes || t("CS_NA")} />
            <Row className="border-none" label={t("SV_DISABILITY_STATUS")} text={streetVendingDetails?.disabilityStatus || t("CS_NA")} />
          </StatusTable>


         
          <ViewTimeline application={application} id={application?.applicationNo} userType={"citizen"} />
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

export default SvApplicationDetails;