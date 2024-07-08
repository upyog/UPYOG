import { Card, CardSubHeader, Header, LinkButton, Loader, Row, StatusTable, MultiLink, PopUp, Toast, SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useEffect, useMemo, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory, useParams } from "react-router-dom";
import getPetAcknowledgementData from "../../getPetAcknowledgementData";
import PTRWFApplicationTimeline from "../../pageComponents/PTRWFApplicationTimeline";
import { pdfDownloadLink } from "../../utils";


import get from "lodash/get";
import { size } from "lodash";

const PTRApplicationDetails = () => {
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
 

  const { isLoading, isError,error, data } = Digit.Hooks.ptr.usePTRSearch(
    {
      tenantId,
      filters: { applicationNumber: acknowledgementIds },
    },
  );


 

 
  const [billData, setBillData]=useState(null);

  


  const PetRegistrationApplications = get(data, "PetRegistrationApplications", []);
  
  
  const petId = get(data, "PetRegistrationApplications[0].applicationNumber", []);
  
  let  pet_details = (PetRegistrationApplications && PetRegistrationApplications.length > 0 && PetRegistrationApplications[0]) || {};
  const application =  pet_details;

  
  sessionStorage.setItem("ptr-pet", JSON.stringify(application));

  

  const [loading, setLoading]=useState(false);

  const fetchBillData=async()=>{
    setLoading(true);
    const result= await Digit.PaymentService.fetchBill(tenantId,{ businessService: "pet-services", consumerCode: acknowledgementIds, });
  
  setBillData(result);
  setLoading(false);
};
useEffect(()=>{
fetchBillData();
}, [tenantId, acknowledgementIds]); 

  const { isLoading: auditDataLoading, isError: isAuditError, data: auditResponse } = Digit.Hooks.ptr.usePTRSearch(
    {
      tenantId,
      filters: { applicationNumber: petId, audit: true },
    },
    {
      enabled: true,
      
    }
  );

  const { data: reciept_data, isLoading: recieptDataLoading } = Digit.Hooks.useRecieptSearch(
    {
      tenantId: tenantId,
      businessService: "pet-services",
      consumerCodes: acknowledgementIds,
      isEmployee: false,
    },
    { enabled: acknowledgementIds ? true : false }
  );

  if (!pet_details.workflow) {
    let workflow = {
      id: null,
      tenantId: tenantId,
      businessService: "pet-services",
      businessId: application?.applicationNumber,
      action: "",
      moduleName: "pet-services",
      state: null,
      comment: null,
      documents: null,
      assignes: null,
    };
     pet_details.workflow = workflow;
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
    const acknowldgementDataAPI = await getPetAcknowledgementData({ ...applications }, tenantInfo, t);
    Digit.Utils.pdf.generate(acknowldgementDataAPI);
    //setAcknowldgementData(acknowldgementDataAPI);
  };

  let documentDate = t("CS_NA");
  if ( pet_details?.additionalDetails?.documentDate) {
    const date = new Date( pet_details?.additionalDetails?.documentDate);
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
    let response = await Digit.PaymentService.generatePdf(tenantId, { PetRegistrationApplications: [data?.PetRegistrationApplications?.[0]] }, "petservicecertificate");
    const fileStore = await Digit.PaymentService.printReciept(tenantId, { fileStoreIds: response.filestoreIds[0] });
    window.open(fileStore[response?.filestoreIds[0]], "_blank");
  };

  let dowloadOptions = [];

  dowloadOptions.push({
    label: t("PTR_PET_DOWNLOAD_ACK_FORM"),
    onClick: () => getAcknowledgementData(),
  });

  //commented out, need later for download receipt and certificate 
  if (reciept_data && reciept_data?.Payments.length > 0 && recieptDataLoading == false)
    dowloadOptions.push({
      label: t("PTR_FEE_RECIEPT"),
      onClick: () => getRecieptSearch({ tenantId: reciept_data?.Payments[0]?.tenantId, payments: reciept_data?.Payments[0] }),
    });
  if (data?.ResponseInfo?.status === "successful")
    dowloadOptions.push({
      label: t("PTR_CERTIFICATE"),
      onClick: () => printCertificate(),
    });
  
  return (
    <React.Fragment>
      <div>
        <div className="cardHeaderWithOptions" style={{ marginRight: "auto", maxWidth: "960px" }}>
          <Header styles={{ fontSize: "32px" }}>{t("PTR_PET_APPLICATION_DETAILS")}</Header>
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
              label={t("PTR_APPLICATION_NO_LABEL")}
              text={pet_details?.applicationNumber} 
            />
          </StatusTable>
           
          <CardSubHeader style={{ fontSize: "24px" }}>{t("PTR_ADDRESS_HEADER")}</CardSubHeader>
          <StatusTable>
            <Row className="border-none" label={t("PTR_PINCODE")} text={pet_details?.address?.pincode || t("CS_NA")} />
            <Row className="border-none" label={t("PTR_CITY")} text={pet_details?.address?.city || t("CS_NA")} />
            <Row className="border-none" label={t("PTR_STREET_NAME")} text={pet_details?.address?.street || t("CS_NA")} />
            <Row className="border-none" label={t("PTR_HOUSE_NO")} text={pet_details?.address?.doorNo || t("CS_NA")} />
          </StatusTable>

          <CardSubHeader style={{ fontSize: "24px" }}>{t("PTR_APPLICANT_DETAILS_HEADER")}</CardSubHeader>
          <StatusTable>
            <Row className="border-none" label={t("PTR_APPLICANT_NAME")} text={pet_details?.applicantName || t("CS_NA")} />
            <Row className="border-none" label={t("PTR_FATHER/HUSBAND_NAME")} text={pet_details?.fatherName || t("CS_NA")} />
            <Row className="border-none" label={t("PTR_APPLICANT_MOBILE_NO")} text={pet_details?.mobileNumber || t("CS_NA")} />
            <Row className="border-none" label={t("PTR_APPLICANT_EMAILID")} text={pet_details?.emailId || t("CS_NA")} />
          </StatusTable>

          <CardSubHeader style={{ fontSize: "24px" }}>{t("PTR_PET_DETAILS_HEADER")}</CardSubHeader>
          <StatusTable>
            <Row className="border-none" label={t("PTR_PET_TYPE")} text={pet_details?.petDetails?.petType || t("CS_NA")} />
            <Row className="border-none" label={t("PTR_BREED_TYPE")} text={pet_details?.petDetails?.breedType || t("CS_NA")} />
            <Row className="border-none" label={t("PTR_DOCTOR_NAME")} text={pet_details?.petDetails?.doctorName || t("CS_NA")} />
            <Row className="border-none" label={t("PTR_CLINIC_NAME")} text={pet_details?.petDetails?.clinicName || t("CS_NA")} />
            <Row className="border-none" label={t("PTR_VACCINATED_DATE")} text={pet_details?.petDetails?.lastVaccineDate || t("CS_NA")} />
            <Row className="border-none" label={t("PTR_VACCINATION_NUMBER")} text={pet_details?.petDetails?.vaccinationNumber || t("CS_NA")} />
          </StatusTable>


          {/* <CardSubHeader style={{ fontSize: "24px" }}>{t("PTR_DOCUMENT_DETAILS")}</CardSubHeader>
          <div>
            {Array.isArray(docs) ? (
              docs.length > 0 && <PTRDocument pet_details={pet_details}></PTRDocument>
            ) : (
              <StatusTable>
                <Row className="border-none" text={t("PTR_NO_DOCUMENTS_MSG")} />
              </StatusTable>
            )}
          </div> */}
          <PTRWFApplicationTimeline application={application} id={application?.applicationNumber} userType={"citizen"} />
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

export default PTRApplicationDetails;
            
           
           
            

         

        
