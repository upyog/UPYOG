import { Card, CardSubHeader, Header, LinkButton, Loader, Row, StatusTable, MultiLink, PopUp, Toast, SubmitBar } from "@egovernments/digit-ui-react-components";
import React, { useEffect, useMemo, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory, useParams } from "react-router-dom";
import getPTAcknowledgementData from "../../getPTAcknowledgementData";
import PetDocument from "../../pageComponents/PetDocument";
import PTWFApplicationTimeline from "../../pageComponents/PTWFApplicationTimeline";
import { pdfDownloadLink } from "../../utils";


import get from "lodash/get";
import { size } from "lodash";

const PTApplicationDetails = () => {
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

  const [billAmount, setBillAmount] = useState(null);
  const [billStatus, setBillStatus] = useState(null);

  let serviceSearchArgs = {
    tenantId : tenantId,
    code: [`PTR_${data?.PetRegistrationApplications?.[0]?.creationReason}`], 
    module: ["PTR"],
    referenceIds : [data?.PetRegistrationApplications?.[0]?.applicationNumber]
    
  }

  //const { isLoading:serviceloading, error : serviceerror, data : servicedata} = Digit.Hooks.pt.useServiceSearchCF({ filters: { serviceSearchArgs } },{ filters: { serviceSearchArgs }, enabled : data?.PetRegistrationApplications?.[0]?.applicationNumber ?true : false, cacheTime : 0 });


  const PetRegistrationApplications = get(data, "PetRegistrationApplications", []);
  
  
  const petId = get(data, "PetRegistrationApplications[0].applicationNumber", []);
  
  let  pet_details = (PetRegistrationApplications && PetRegistrationApplications.length > 0 && PetRegistrationApplications[0]) || {};
  const application =  pet_details;
  
  sessionStorage.setItem("ptr-property", JSON.stringify(application));

  // useMemo(() => {
  //   if((data?.PetRegistrationApplications?.[0]?.status === "ACTIVE" || data?.PetRegistrationApplications?.[0]?.status === "INACTIVE") && popup == false && servicedata?.Service?.length == 0)
  //     setpopup(true);
  // },[data,servicedata])

  useEffect(async () => {
    if (acknowledgementIds && tenantId &&  pet_details) {
      const res = await Digit.PaymentService.searchBill(tenantId, { Service: "pet-services", consumerCode: acknowledgementIds });
      if (!res.Bill.length) {
        const res1 = await Digit.PTService.ptCalculateMutation({  pet_details:  pet_details }, tenantId);
        setBillAmount(res1?.[acknowledgementIds]?.totalAmount || t("CS_NA"));
        setBillStatus(t(`PTR_MUT_BILL_ACTIVE`));
      } else {
        setBillAmount(res?.Bill[0]?.totalAmount || t("CS_NA"));
        setBillStatus(t(`PTR_MUT_BILL_${res?.Bill[0]?.status?.toUpperCase()}`));
      }
    }
  }, [tenantId, acknowledgementIds,  pet_details]);

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
      isEmployee: true,
    },
    { enabled: acknowledgementIds ? true : false }
  );

  if (! pet_details.workflow) {
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

  

  

 
  let owners = [];
  owners = application?.owners;
  let docs = [];
  docs = application?.documents;

  if (isLoading || auditDataLoading) {
    return <Loader />;
  }

 

  const getAcknowledgementData = async () => {
    const applications = application || {};
    const tenantInfo = tenants.find((tenant) => tenant.code === applications.tenantId);
    const acknowldgementDataAPI = await getPTAcknowledgementData({ ...applications }, tenantInfo, t);
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
    response = await Digit.PaymentService.generatePdf(tenantId, { Payments: [{ ...payments }] }, "consolidatedreceipt");
    const fileStore = await Digit.PaymentService.printReciept(tenantId, { fileStoreIds: response.filestoreIds[0] });
    window.open(fileStore[response?.filestoreIds[0]], "_blank");
  }

  const handleDownload = async (document, tenantid) => {
    let tenantId = tenantid ? tenantid : tenantId;
    const res = await Digit.UploadServices.Filefetch([document?.fileStoreId], tenantId);
    let documentLink = pdfDownloadLink(res.data, document?.fileStoreId);
    window.open(documentLink, "_blank");
  };

  const printCertificate = async () => {
    let response = await Digit.PaymentService.generatePdf(tenantId, { PetRegistrationApplications: [data?.PetRegistrationApplications?.[0]] }, "ptmutationcertificate");
    const fileStore = await Digit.PaymentService.printReciept(tenantId, { fileStoreIds: response.filestoreIds[0] });
    window.open(fileStore[response?.filestoreIds[0]], "_blank");
  };

  let dowloadOptions = [];

  dowloadOptions.push({
    label: data?.PetRegistrationApplications?.[0]?.creationReason === "MUTATION" ? t("MT_APPLICATION") : t("PTR_PET_DOWNLOAD_ACK_FORM"),
    onClick: () => getAcknowledgementData(),
  });
  if (reciept_data && reciept_data?.Payments.length > 0 && recieptDataLoading == false)
    dowloadOptions.push({
      label: t("MT_FEE_RECIEPT"),
      onClick: () => getRecieptSearch({ tenantId: reciept_data?.Payments[0]?.tenantId, payments: reciept_data?.Payments[0] }),
    });
  if (data?.PetRegistrationApplications?.[0]?.creationReason === "MUTATION" && data?.PetRegistrationApplications?.[0]?.status === "ACTIVE")
    dowloadOptions.push({
      label: t("MT_CERTIFICATE"),
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
              docs.length > 0 && <PetDocument pet_details={pet_details}></PetDocument>
            ) : (
              <StatusTable>
                <Row className="border-none" text={t("PTR_NO_DOCUMENTS_MSG")} />
              </StatusTable>
            )}
          </div> */}
          <PTWFApplicationTimeline application={application} id={application?.applicationNumber} userType={"citizen"} />
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

export default PTApplicationDetails;
            
           
           
            

         

        
