import { Card, CardSubHeader, Header, LinkButton, Loader, Row, StatusTable, MultiLink, PopUp, Toast, SubmitBar } from "@upyog/digit-ui-react-components";
import React, { useEffect, useMemo, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory, useParams } from "react-router-dom";
// import getEwAcknowledgementData from "../../getEwAcknowledgementData";
import EWASTEWFApplicationTimeline from "../../pageComponents/EWASTEWFApplicationTimeline";
import { pdfDownloadLink } from "../../utils";
import ApplicationTable from "../../components/inbox/ApplicationTable";

import get from "lodash/get";
import { size } from "lodash";

const EWASTECitizenApplicationDetails = () => {
  const { t } = useTranslation();
  const history = useHistory();
  const {requestId, tenantId } = useParams();
  const [acknowldgementData, setAcknowldgementData] = useState([]);
  const [showOptions, setShowOptions] = useState(false);
  const [popup, setpopup] = useState(false);
  const [showToast, setShowToast] = useState(null);
  // const tenantId = Digit.ULBService.getCurrentTenantId();
  const { data: storeData } = Digit.Hooks.useStore.getInitData();
  const { tenants } = storeData || {};
 

  const { isLoading, isError,error, data } = Digit.Hooks.ew.useEWSearch(
    {
      tenantId,
      filters: { applicationNumber: requestId },
    },
  );

  const [billData, setBillData]=useState(null);

  const EwasteApplication = get(data, "EwasteApplication", []);
  
  const ewId = get(data, "EwasteApplication[0].applicationNumber", []);
  
  let  ew_details = (EwasteApplication && EwasteApplication.length > 0 && EwasteApplication[0]) || {};
  // console.log("this is ew-detaoishdjf:::", ew_details)
  const application =  ew_details;

  
  sessionStorage.setItem("ew-pet", JSON.stringify(application));

  

  const [loading, setLoading]=useState(false);

  const fetchBillData=async()=>{
    setLoading(true);
    const result= await Digit.PaymentService.fetchBill(tenantId,{ businessService: "ew-services", consumerCode: requestId, });
  
  setBillData(result);
  setLoading(false);
};
useEffect(()=>{
fetchBillData();
}, [tenantId, requestId]); 

  const { isLoading: auditDataLoading, isError: isAuditError, data: auditResponse } = Digit.Hooks.ew.useEWSearch(
    {
      tenantId,
      filters: { applicationNumber: ewId, audit: true },
    },
    {
      enabled: true,
      
    }
  );

  const { data: reciept_data, isLoading: recieptDataLoading } = Digit.Hooks.useRecieptSearch(
    {
      tenantId: tenantId,
      businessService: "ewst",
      consumerCodes: requestId,
      isEmployee: false,
    },
    { enabled: requestId ? true : false }
  );

  if (!ew_details.workflow) {
    let workflow = {
      id: null,
      tenantId: tenantId,
      businessService: "ew-services",
      businessId: application?.applicationNumber,
      action: "",
      moduleName: "ew-services",
      state: null,
      comment: null,
      documents: null,
      assignes: null,
    };
     ew_details.workflow = workflow;
  }

  

  

 
  // let owners = [];
  // owners = application?.owners;
  // let docs = [];
  // docs = application?.documents;

  if (isLoading || auditDataLoading) {
    return <Loader />;
  }

 

  // const getAcknowledgementData = async () => {
  //   const applications = application || {};
  //   const tenantInfo = tenants.find((tenant) => tenant.code === applications.tenantId);
  //   const acknowldgementDataAPI = await getEwAcknowledgementData({ ...applications }, tenantInfo, t);
  //   Digit.Utils.pdf.generate(acknowldgementDataAPI);
  //   //setAcknowldgementData(acknowldgementDataAPI);
  // };

  let documentDate = t("CS_NA");
  if ( ew_details?.additionalDetails?.documentDate) {
    const date = new Date( ew_details?.additionalDetails?.documentDate);
    const month = Digit.Utils.date.monthNames[date.getMonth()];
    documentDate = `${date.getDate()} ${month} ${date.getFullYear()}`;
  }

  async function getRecieptSearch({ tenantId, payments, ...params }) {
    let response = { filestoreIds: [payments?.fileStoreId] };
    response = await Digit.PaymentService.generatePdf(tenantId, { Payments: [{ ...payments }] }, "ewservice-receipt");
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
    let response = await Digit.PaymentService.generatePdf(tenantId, { EwasteApplication: [data?.EwasteApplication?.[0]] }, "ewservicecertificate");
    const fileStore = await Digit.PaymentService.printReciept(tenantId, { fileStoreIds: response.filestoreIds[0] });
    window.open(fileStore[response?.filestoreIds[0]], "_blank");
  };

  let dowloadOptions = [];

  dowloadOptions.push({
    label: t("EWASTE_DOWNLOAD_ACK_FORM"),
    onClick: () => getAcknowledgementData(),
  });

  //commented out, need later for download receipt and certificate 
  if (reciept_data && reciept_data?.Payments.length > 0 && recieptDataLoading == false)
    dowloadOptions.push({
      label: t("EWASTE_FEE_RECIEPT"),
      onClick: () => getRecieptSearch({ tenantId: reciept_data?.Payments[0]?.tenantId, payments: reciept_data?.Payments[0] }),
    });
  if (data?.ResponseInfo?.status === "successful")
    dowloadOptions.push({
      label: t("EWASTE_CERTIFICATE"),
      onClick: () => printCertificate(),
    });


  const productcolumns = [
    { Header: t("PRODUCT_NAME"), accessor: "name" },
    { Header: t("PRODUCT_QUANTITY"), accessor: "quantity" },
    { Header: t("UNIT_PRICE"), accessor: "unit_price" },
    { Header: t("TOTAL_PRODUCT_PRICE"), accessor: "total_price" },
  ];

    const productRows = ew_details?.ewasteDetails?.map((product) => (
        {
            name: t(product.productName),
            quantity: product.quantity,
            unit_price: product.price,
            total_price: product.quantity * product.price,
        }
    )) || [];
  

  
  return (
    <React.Fragment>
      <div>
        <div className="cardHeaderWithOptions" style={{ marginRight: "auto", maxWidth: "960px" }}>
          <Header styles={{ fontSize: "32px" }}>{t("EWASTE_APPLICATION_DETAILS")}</Header>
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
              label={t("EWASTE_APPLICATION_NO_LABEL")}
              text={ew_details?.requestId} 
            />
          </StatusTable>
           
          <CardSubHeader style={{ fontSize: "24px" }}>{t("EWASTE_ADDRESS_HEADER")}</CardSubHeader>
          <StatusTable>
            <Row className="border-none" label={t("EWASTE_PINCODE")} text={ew_details?.address?.pincode || t("CS_NA")} />
            <Row className="border-none" label={t("EWASTE_CITY")} text={ew_details?.address?.city || t("CS_NA")} />
            <Row className="border-none" label={t("EWASTE_STREET_NAME")} text={ew_details?.address?.street || t("CS_NA")} />
            <Row className="border-none" label={t("EWASTE_HOUSE_NO")} text={ew_details?.address?.doorNo || t("CS_NA")} />
            <Row className="border-none" label={t("EWASTE_HOUSE_NAME")} text={ew_details?.address?.buildingName || t("CS_NA")} />
            <Row className="border-none" label={t("EWASTE_ADDRESS_LINE1")} text={ew_details?.address?.addressLine1 || t("CS_NA")} />
            <Row className="border-none" label={t("EWASTE_ADDRESS_LINE2")} text={ew_details?.address?.addressLine2 || t("CS_NA")} />
            <Row className="border-none" label={t("EWASTE_LANDMARK")} text={ew_details?.address?.landmark || t("CS_NA")} />
          </StatusTable>

          <CardSubHeader style={{ fontSize: "24px" }}>{t("EWASTE_APPLICANT_DETAILS")}</CardSubHeader>
          <StatusTable>
            <Row className="border-none" label={t("EWASTE_APPLICANT_NAME")} text={ew_details?.applicant?.applicantName || t("CS_NA")} />
            <Row className="border-none" label={t("EWASTE_APPLICANT_MOBILE_NO")} text={ew_details?.applicant?.mobileNumber || t("CS_NA")} />
            <Row className="border-none" label={t("EWASTE_APPLICANT_EMAIL_ID")} text={ew_details?.applicant?.emailId || t("CS_NA")} />
          </StatusTable>

          <CardSubHeader style={{ fontSize: "24px" }}>{t("EWASTE_TITLE_PRODUCT_DETAILS")}</CardSubHeader>
          <ApplicationTable
            t={t}
            data={productRows}
            columns={productcolumns}
            getCellProps={(cellInfo) => ({
              style: {
                minWidth: "150px",
                padding: "10px",
                fontSize: "16px",
                paddingLeft: "20px",
              },
            })}
            isPaginationRequired={false}
            totalRecords={productRows.length}
          />

          {/* <CardSubHeader style={{ fontSize: "24px" }}>{t("EWASTE__DETAILS_HEADER")}</CardSubHeader>
          <StatusTable>
            <Row className="border-none" label={t("PTR_PET_TYPE")} text={ew_details?.petDetails?.petType || t("CS_NA")} />
            <Row className="border-none" label={t("PTR_BREED_TYPE")} text={ew_details?.petDetails?.breedType || t("CS_NA")} />
            <Row className="border-none" label={t("PTR_DOCTOR_NAME")} text={ew_details?.petDetails?.doctorName || t("CS_NA")} />
            <Row className="border-none" label={t("PTR_CLINIC_NAME")} text={ew_details?.petDetails?.clinicName || t("CS_NA")} />
            <Row className="border-none" label={t("PTR_VACCINATED_DATE")} text={ew_details?.petDetails?.lastVaccineDate || t("CS_NA")} />
            <Row className="border-none" label={t("PTR_VACCINATION_NUMBER")} text={ew_details?.petDetails?.vaccinationNumber || t("CS_NA")} />
          </StatusTable> */}


          {/* <CardSubHeader style={{ fontSize: "24px" }}>{t("PTR_DOCUMENT_DETAILS")}</CardSubHeader>
          <div>
            {Array.isArray(docs) ? (
              docs.length > 0 && <PTRDocument ew_details={ew_details}></PTRDocument>
            ) : (
              <StatusTable>
                <Row className="border-none" text={t("PTR_NO_DOCUMENTS_MSG")} />
              </StatusTable>
            )}
          </div> */}
          <EWASTEWFApplicationTimeline application={application} id={application?.applicationNumber} userType={"citizen"} />
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

export default EWASTECitizenApplicationDetails;
            
           
           
            

         

        
