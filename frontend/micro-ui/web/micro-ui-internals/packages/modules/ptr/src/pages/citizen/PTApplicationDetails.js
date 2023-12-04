import { Card, CardSubHeader, Header, LinkButton, Loader, Row, StatusTable, MultiLink, PopUp, Toast, SubmitBar } from "@egovernments/digit-ui-react-components";
import React, { useEffect, useMemo, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory, useParams } from "react-router-dom";
import getPTAcknowledgementData from "../../getPTAcknowledgementData";
import PropertyDocument from "../../pageComponents/PropertyDocument";
import PTWFApplicationTimeline from "../../pageComponents/PTWFApplicationTimeline";
import { getCityLocale, getPropertyTypeLocale, propertyCardBodyStyle, getMohallaLocale, pdfDownloadLink } from "../../utils";
import PTCitizenFeedbackPopUp from "../../pageComponents/PTCitizenFeedbackPopUp";
//import PTCitizenFeedback from "@egovernments/digit-ui-module-core/src/components/PTCitizenFeedback";

import get from "lodash/get";
import { size } from "lodash";

const PTApplicationDetails = () => {
  const { t } = useTranslation();
  const history = useHistory();
  const { applicationNumber, tenantId } = useParams();
  const [acknowldgementData, setAcknowldgementData] = useState([]);
  const [showOptions, setShowOptions] = useState(false);
  const [popup, setpopup] = useState(false);
  const [showToast, setShowToast] = useState(null);
  // const tenantId = Digit.ULBService.getCurrentTenantId();
  const { data: storeData } = Digit.Hooks.useStore.getInitData();
  const { tenants } = storeData || {};
  const { isLoading, isError, error, data } = Digit.Hooks.ptr.usePTRSearch(
    { filters: { applicationNumber, tenantId } },
    { filters: { applicationNumber, tenantId } }
  );
  const [billAmount, setBillAmount] = useState(null);
  const [billStatus, setBillStatus] = useState(null);

  let serviceSearchArgs = {
    tenantId : tenantId,
    code: [`${data?.PetRegistrationApplications?.[0]?.creationReason}`], 
    module: ["pet-services"],
    referenceId : [data?.PetRegistrationApplications?.[0]?.applicationNumber]
    
    //removing thid as of now sending ack no in referenceId
    // attributes: {
    //         "attributeCode": "referenceId",
    //         "value": data?.PetRegistrationApplications?.[0]?.applicationNumber,
    //     }
  }
  console.log("refrenseeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee",data)

  const { isLoading:serviceloading, error : serviceerror, data : servicedata} = Digit.Hooks.pt.useServiceSearchCF({ filters: { serviceSearchArgs } },{ filters: { serviceSearchArgs }, enabled : data?.PetRegistrationApplications?.[0]?.applicationNumber ?true : false, cacheTime : 0 });


  const PetRegistrationApplications = get(data, "PetRegistrationApplications", []);
  // const propertyId = get(data, "PetRegistrationApplications[0].applicationNumber", []);
  
  let pet_details = (PetRegistrationApplications && PetRegistrationApplications.length > 0 && PetRegistrationApplications[0]) || {};
  const application = pet_details;
  sessionStorage.setItem("pt-property", JSON.stringify(application));
  console.log("pet_details equal to petregistration",pet_details)

  useMemo(() => {
    if((data?.PetRegistrationApplications?.[0]?.status === "ACTIVE"  || data?.PetRegistrationApplications?.[0]?.status === "INACTIVE")&& popup == false && servicedata?.Service?.length == 0)
      setpopup(true);
  },[data,servicedata])

  useEffect(async () => {
    if (applicationNumber && tenantId && pet_details) {
      const res = await Digit.PaymentService.searchBill(tenantId, { Service: "PT.MUTATION", consumerCode: applicationNumber });
      if (!res.Bill.length) {
        const res1 = await Digit.PTService.ptCalculateMutation({ Pet_Details: pet_details }, tenantId);
        setBillAmount(res1?.[applicationNumber]?.totalAmount || t("CS_NA"));
        setBillStatus(t(`PT_MUT_BILL_ACTIVE`));
      } else {
        setBillAmount(res?.Bill[0]?.totalAmount || t("CS_NA"));
        setBillStatus(t(`PT_MUT_BILL_${res?.Bill[0]?.status?.toUpperCase()}`));
      }
    }
  }, [tenantId, applicationNumber, pet_details]);

  const { isLoading: auditDataLoading, isError: isAuditError, data: auditResponse } = Digit.Hooks.ptr.usePTRSearch(
    {
      tenantId,
      filters: { applicationNumber: applicationNumber, audit: true },
    },
    {
      enabled: true,
    }
  );

  const { data: reciept_data, isLoading: recieptDataLoading } = Digit.Hooks.useRecieptSearch(
    {
      tenantId: tenantId,
      businessService: "ptr",
      consumerCodes: applicationNumber,
      isEmployee: false,
    },
    { enabled: applicationNumber ? true : false }
  );

   if (!pet_details.workflow) {
    let workflow = {
      id: null,
      tenantId: application?.tenantId,
      businessService: "ptr",
      businessIds: application?.applicationNumber,
      action: "",
      moduleName: "pet-services",
      state: null,
      comment: null,
      documents: null,
      assignes: null,
    };
    pet_details.workflow = workflow;
   }

  

  if (pet_details && pet_details.owners && pet_details.owners.length > 0) {
    let ownersTemp = [];
    let owners = [];
    pet_details.owners.map((owner) => {
      owner.documentUid = owner.documents ? owner.documents[0].documentUid : "NA";
      owner.documentType = owner.documents ? owner.documents[0].documentType : "NA";
      if (owner.status == "ACTIVE") {
        ownersTemp.push(owner);
      } else {
        owners.push(owner);
      }
    });
    pet_details.ownersInit = owners;
    pet_details.ownersTemp = ownersTemp;
  }
  // pet_details.ownershipCategoryTemp = pet_details?.ownershipCategory;
  // pet_details.ownershipCategoryInit = "NA";
  // Set Institution/Applicant info card visibility
  if (get(application, "PetRegistrationApplications[0].ownershipCategory", "")?.startsWith("INSTITUTION")) {
    pet_details.institutionTemp = pet_details.institution;
  }

  if (auditResponse && Array.isArray(get(auditResponse, "PetRegistrationApplications", [])) && get(auditResponse, "PetRegistrationApplications", []).length > 0) {
    const propertiesAudit = get(auditResponse, "PetRegistrationApplications", []);
    const propertyIndex = pet_details.status == "ACTIVE" ? 1 : 0;
    // const previousActiveProperty = propertiesAudit.filter(property => property.status == 'ACTIVE').sort((x, y) => y.auditDetails.lastModifiedTime - x.auditDetails.lastModifiedTime)[propertyIndex];
    // Removed filter(property => property.status == 'ACTIVE') condition to match result in qa env
    const previousActiveProperty = propertiesAudit
      .filter((pet_details) => pet_details.status == "ACTIVE")
      .sort((x, y) => y.auditDetails.lastModifiedTime - x.auditDetails.lastModifiedTime)[propertyIndex];
    // pet_details.ownershipCategoryInit = previousActiveProperty?.ownershipCategory;
    pet_details.ownersInit = previousActiveProperty?.owners?.filter((owner) => owner.status == "ACTIVE");

    const curWFProperty = propertiesAudit.sort((x, y) => y.auditDetails.lastModifiedTime - x.auditDetails.lastModifiedTime)[0];
    pet_details.ownersTemp = curWFProperty.owners.filter((owner) => owner.status == "ACTIVE");

    // if (pet_details?.ownershipCategoryInit?.startsWith("INSTITUTION")) {
    //   pet_details.institutionInit = previousActiveProperty.institution;
    // }
  }

  // let transfereeOwners = get(pet_details, "ownersTemp", []);
  // let transferorOwners = get(pet_details, "ownersInit", []);

  // let transfereeInstitution = get(pet_details, "institutionTemp", []);
  // let isInstitution = pet_details?.ownershipCategoryInit?.startsWith("INSTITUTION");
  // let transferorInstitution = get(pet_details, "institutionInit", []);

  // let units = [];
  // units = application?.units;
  // units &&
  //   units.sort((x, y) => {
  //     let a = x.floorNo,
  //       b = y.floorNo;
  //     if (x.floorNo < 0) {
  //       a = x.floorNo * -20;
  //     }
  //     if (y.floorNo < 0) {
  //       b = y.floorNo * -20;
  //     }
  //     if (a > b) {
  //       return 1;
  //     } else {
  //       return -1;
  //     }
  //   });
  let owners = [];
  owners = application?.owners;
  let docs = [];
  docs = application?.documents;

  if (isLoading || auditDataLoading) {
    return <Loader />;
  }

  // let flrno,
  //   i = 0;
  // flrno = units && units[0]?.floorNo;

  // const isPropertyTransfer = pet_details?.creationReason && pet_details.creationReason === "MUTATION" ? true : false;

  const getAcknowledgementData = async () => {
    const applications = application || {};
    const tenantInfo = tenants.find((tenant) => tenant.code === applications.tenantId);
    const acknowldgementDataAPI = await getPTAcknowledgementData({ ...applications }, tenantInfo, t);
    Digit.Utils.pdf.generate(acknowldgementDataAPI);
    //setAcknowldgementData(acknowldgementDataAPI);
  };

  let documentDate = t("CS_NA");
  if (pet_details?.additionalDetails?.documentDate) {
    const date = new Date(pet_details?.additionalDetails?.documentDate);
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
    label: data?.PetRegistrationApplications?.[0]?.creationReason === "MUTATION" ? t("MT_APPLICATION") : t("PTR_APPLICATION"),
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
            <Row className="border-none" label={t("PTR_APPLICANT_EMAILID")} text={pet_details?.doorNo || t("CS_NA")} />
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


          <CardSubHeader style={{ fontSize: "24px" }}>{t("PTR_DOCUMENT_DETAILS")}</CardSubHeader>
          <div>
            {Array.isArray(docs) ? (
              docs.length > 0 && <PropertyDocument pet_details={pet_details}></PropertyDocument>
            ) : (
              <StatusTable>
                <Row className="border-none" text={t("PTR_NO_DOCUMENTS_MSG")} />
              </StatusTable>
            )}
          </div>
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

            

            
           
           
            

         

        
