/*
PTRApplicationDetails Component:
- Displays detailed information about a pet registration application.
- Fetches data using Digit Hooks for application details, bill data, and receipts.
- Includes functionality to download acknowledgment forms, fee receipts, and certificates.
- Uses Digit PaymentService to generate and print PDFs.
- Displays application details in a series of StatusTables with applicant, pet, and address information.
- Uses MultiLink for download options and PTRWFApplicationTimeline for workflow details.
- Implements loading states and handles errors with toasts.
*/
import { Card, CardSubHeader, Header, LinkButton, Loader, Row, StatusTable, MultiLink, PopUp, Toast, SubmitBar } from "@upyog/digit-ui-react-components";
import React, { useEffect, useMemo, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory, useParams } from "react-router-dom";
import getPetAcknowledgementData from "../../getPetAcknowledgementData";
import PTRWFApplicationTimeline from "../../pageComponents/PTRWFApplicationTimeline";
import { convertEpochToDate } from "../../utils";

import get from "lodash/get";

const PTRApplicationDetails = () => {
  const { t } = useTranslation();
  const { acknowledgementIds, tenantId } = useParams();
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

  async function getRecieptSearch({ tenantId, payments, ...params }) {
    let response = { filestoreIds: [payments?.fileStoreId] };
    response = await Digit.PaymentService.generatePdf(tenantId, { Payments: [{ ...payments }] }, "petservice-receipt");
    const fileStore = await Digit.PaymentService.printReciept(tenantId, { fileStoreIds: response.filestoreIds[0] });
    window.open(fileStore[response?.filestoreIds[0]], "_blank");
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
 
  if (reciept_data && reciept_data?.Payments.length > 0 && recieptDataLoading == false)
    dowloadOptions.push({
      label: t("PTR_FEE_RECIEPT"),
      onClick: () => getRecieptSearch({ tenantId: reciept_data?.Payments[0]?.tenantId, payments: reciept_data?.Payments[0] }),
    });
  if (reciept_data && reciept_data?.Payments.length > 0 && recieptDataLoading == false)
    dowloadOptions.push({
      label: t("PTR_CERTIFICATE"),
      onClick: () => printCertificate(),
    });


    const getDate = (epochdate) => {
      return epochdate
        ? new Date(epochdate * 1000).getDate() + 
          "/" + 
          (new Date(epochdate * 1000).getMonth() + 1) + 
          "/" + 
          new Date(epochdate * 1000).getFullYear().toString()
        : "NA";
    };
  
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
          <StatusTable>
            <Row
              className="border-none"
              label={t("PTR_VALIDITY_DATE")}
              text={getDate(pet_details?.validityDate)} 
            />
          </StatusTable>
          {pet_details?.petToken&&pet_details?.petToken.length>0&&(
          <StatusTable>
            <Row
              className="border-none"
              label={t("PTR_TOKEN")}
              text={pet_details?.petToken} 
            />
          </StatusTable>)}
           
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
            <Row className="border-none" label={t("PTR_PET_NAME")} text={pet_details?.petDetails?.petName || t("CS_NA")} />
            <Row className="border-none" label={t("PTR_PET_AGE")} text={pet_details?.petDetails?.petAge || t("CS_NA")} />
            <Row className="border-none" label={t("PTR_PET_SEX")} text={pet_details?.petDetails?.petGender || t("CS_NA")} />
            <Row className="border-none" label={t("PTR_IDENTIFICATION_MARK")} text={pet_details?.petDetails?.identificationMark || t("CS_NA")} />
            <Row className="border-none" label={t("PTR_VACCINATION_NUMBER")} text={pet_details?.petDetails?.vaccinationNumber || t("CS_NA")} />
            {pet_details?.petDetails?.birthDate ? pet_details?.petDetails?.birthDate && <Row className="border-none" label={t("PTR_BIRTH")} text={convertEpochToDate(pet_details?.petDetails?.birthDate) || t("CS_NA")} /> : pet_details?.petDetails?.adoptionDate && <Row className="border-none" label={t("PTR_ADOPTION")} text={convertEpochToDate(pet_details?.petDetails?.adoptionDate) || t("CS_NA")} />}
          </StatusTable>

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
            
           
           
            

         

        
