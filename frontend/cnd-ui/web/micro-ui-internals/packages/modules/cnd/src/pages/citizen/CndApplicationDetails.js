import { Card, CardSubHeader, Header, LinkButton, Loader, Row, StatusTable, MultiLink, PopUp, Toast, SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useParams } from "react-router-dom";
import get from "lodash/get";
import CNDApplicationTimeLine from "../../components/CNDApplicationTimeLine";
import ApplicationTable from "../../components/inbox/ApplicationTable";
import cndAcknowledgementData from "../../utils/cndAcknowledgementData";
import { cndStyles } from "../../utils/cndStyles";
import { CNDDocumnetPreview, getOrderDocuments } from "../../utils";


/**
 *  Pre-developed componenet will mature it when work on fetch bill as well as payment , reciept, certificate download
 */

const CndApplicationDetails = () => {
    
  const { t } = useTranslation();
  const { applicationNumber, tenantId } = useParams();
  const [showOptions, setShowOptions] = useState(false);
  const [popup, setpopup] = useState(false);
  const [showToast, setShowToast] = useState(null);
  const { data: storeData } = Digit.Hooks.useStore.getInitData();
  const { tenants } = storeData || {};
 
  const { isLoading, data } = Digit.Hooks.cnd.useCndSearchApplication(
    {
      tenantId,
      filters: { applicationNumber: applicationNumber, isUserDetailRequired:true},
    },
  ); 

  const [billData, setBillData]=useState(null);
   const cndApplicationDetail = get(data, "cndApplicationDetail", []);
  let  cndData = (cndApplicationDetail && cndApplicationDetail.length > 0 && cndApplicationDetail[0]) || {};
  const application =  cndData;
  sessionStorage.setItem("cnd-application", JSON.stringify(application));
  const [loading, setLoading]=useState(false);


  const fetchBillData=async()=>{
    setLoading(true);
    const result= await Digit.PaymentService.fetchBill(tenantId,{ businessService: "cnd-service", consumerCode: applicationNumber });
    setBillData(result);
    setLoading(false);
    };
    useEffect(()=>{
    fetchBillData();
    }, [tenantId, applicationNumber]); 

  const { data: reciept_data, isLoading: recieptDataLoading } = Digit.Hooks.useRecieptSearch(
    {
      tenantId: tenantId,
      businessService: "cnd-service",
      consumerCodes: applicationNumber,
      isEmployee: false,
    },
    { enabled: applicationNumber ? true : false }
  );

   const getApplicationDocs = cndData?.documentDetails?.map((doc) => ({
    ...doc,
    module: "CND",
  })) || [];

  // Step 2: Fetch PDF details only if documents exist
  const { data: pdfDetails } = Digit.Hooks.useDocumentSearch(getApplicationDocs, {enabled: getApplicationDocs.length > 0});

  // Step 3: Extract only StreetVending PDFs
  const applicationDocs = pdfDetails?.pdfFiles?.filter((pdf) => pdf?.module === "CND") || [];

  if (!cndData.workflow) {
    let workflow = {
      id: null,
      tenantId: tenantId,
      businessService: "cnd",
      businessId: application?.applicationNumber,
      action: "",
      moduleName: "cnd-service",
      state: null,
      comment: null,
      documents: null,
      assignes: null,
    };
     cndData.workflow = workflow;
  }

  if (isLoading) {
    return <Loader />;
  }

  const getAcknowledgementData = async () => {
    const applications = application || {};
    const tenantInfo = tenants.find((tenant) => tenant.code === applications.tenantId);
    const acknowldgementDataAPI = await cndAcknowledgementData({ ...applications }, tenantInfo, t);
    Digit.Utils.pdf.generateTable(acknowldgementDataAPI);
  };


  async function getRecieptSearch({ tenantId, payments, ...params }) {
    let response = { filestoreIds: [payments?.fileStoreId] };
    response = await Digit.PaymentService.generatePdf(tenantId, { Payments: [{ ...payments }] }, "cnd-service");
    const fileStore = await Digit.PaymentService.printReciept(tenantId, { fileStoreIds: response.filestoreIds[0] });
    window.open(fileStore[response?.filestoreIds[0]], "_blank");
  };



  let dowloadOptions = [];
  dowloadOptions.push({
    label: t("CND_ACKNOWLEDGEMENT"),
    onClick: () => getAcknowledgementData(),
  });


  if (reciept_data && reciept_data?.Payments.length > 0 && recieptDataLoading == false)
    dowloadOptions.push({
      label: t("CND_FEE_RECIEPT"),
      onClick: () => getRecieptSearch({ tenantId: reciept_data?.Payments[0]?.tenantId, payments: reciept_data?.Payments[0] }),
    });


const columnName = [
  { Header: t("CND_S_NO"), accessor: "sNo" },
  { Header: t("CND_WASTE_TYPE"), accessor: "wasteType" },
  { Header: t("CND_QUANTITY"), accessor: "quantity" },
  { Header: t("CND_METRICS"), accessor: "siUnit" }
];

const operationRows = cndData.wasteTypeDetails.map((items, index) => ({
  sNo: index + 1,
  wasteType: t(items?.wasteType) || "-",
  quantity: items?.quantity?items?.quantity:"0",
  siUnit:items?.metrics ? items?.metrics : "-"
}));

  
  return (
    <React.Fragment>
      <div>
        <div className="cardHeaderWithOptions" style={cndStyles.cardHeaderWithOptions}>
          <Header styles={cndStyles.cardHeader}>{t("CND_REQUEST_DETAILS")}</Header>
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
              label={t("CND_APPLICATION_NUMBER")}
              text={cndData?.applicationNumber} 
            />
            <Row
              className="border-none"
              label={t("CND_APPLICATION_TYPE")}
              text={t(cndData?.applicationType)} 
            />
            <Row
              className="border-none"
              label={t("CND_WASTE_QUANTITY")}
              text={cndData?.totalWasteQuantity + " Ton"} 
            />
            <Row
              className="border-none"
              label={t("CND_TYPE_CONSTRUCTION")}
              text={t(cndData?.typeOfConstruction)} 
            />
            <Row
              className="border-none"
              label={t("CND_PROPERTY_USAGE")}
              text={t(cndData?.propertyType)} 
            />
            <Row
              className="border-none"
              label={t("CND_TIME_CONSTRUCTION")}
              text={cndData?.constructionFromDate + " to " + cndData?.constructionToDate} 
            />
            <Row
              className="border-none"
              label={t("CND_SCHEDULE_PICKUP")}
              text={cndData?.requestedPickupDate} 
            />
            {
              cndData?.applicationStatus==="COMPLETED" &&(
              <Row
              className="border-none"
              label={t("CND_EMP_SCHEDULE_PICKUP")}
              text={cndData?.pickupDate} 
            />
              )
            }
            <Row
              className="border-none"
              label={t("CND_AREA_HOUSE")}
              text={cndData?.houseArea} 
            />
          </StatusTable>

          {cndData?.applicationStatus==="COMPLETED" &&(
          <React.Fragment>
          <CardSubHeader style={cndStyles.citizenApplicantDetailCard}>{t("CND_FACILITY_DETAILS")}</CardSubHeader>
          <StatusTable>
            <Row className="border-none" label={t("CND_DISPOSE_DATE")} text={cndData?.facilityCenterDetail?.disposalDate?.split(" ")[0] || t("CS_NA")} />
            <Row className="border-none" label={t("CND_DISPOSE_TYPE")} text={cndData?.facilityCenterDetail?.disposalType || t("CS_NA")} />
            <Row className="border-none" label={t("CND_DISPOSAL_SITE_NAME")} text={cndData?.facilityCenterDetail?.nameOfDisposalSite || t("CS_NA")} />
            <Row className="border-none" label={t("CND_DUMPING_STATION")} text={cndData?.facilityCenterDetail?.dumpingStationName|| t("CS_NA")} />
            <Row className="border-none" label={t("CND_GROSS_WEIGHT"+ " Ton")} text={cndData?.facilityCenterDetail?.grossWeight || t("CS_NA")} />
            <Row className="border-none" label={t("CND_NET_WEIGHT"+ " Ton")} text={cndData?.facilityCenterDetail?.netWeight|| t("CS_NA")} />
          </StatusTable>
          </React.Fragment>)}
          <CardSubHeader style={cndStyles.citizenApplicantDetailCard}>{t("COMMON_PERSONAL_DETAILS")}</CardSubHeader>
          <StatusTable>
            <Row className="border-none" label={t("COMMON_APPLICANT_NAME")} text={cndData?.applicantDetail?.nameOfApplicant || t("CS_NA")} />
            <Row className="border-none" label={t("COMMON_MOBILE_NUMBER")} text={cndData?.applicantDetail?.mobileNumber || t("CS_NA")} />
            <Row className="border-none" label={t("COMMON_EMAIL_ID")} text={cndData?.applicantDetail?.emailId || t("CS_NA")} />
            <Row className="border-none" label={t("COMMON_ALT_MOBILE_NUMBER")} text={cndData?.applicantDetail?.alternateMobileNumber|| t("CS_NA")} />
          </StatusTable>
          <CardSubHeader style={cndStyles.citizenApplicantDetailCard}>{t("CND_WASTE_PICKUP_ADDRESS")}</CardSubHeader>
          <StatusTable>
            <Row className="border-none" label={t("HOUSE_NO")} text={cndData?.addressDetail?.houseNumber || t("CS_NA")} />
            <Row className="border-none" label={t("ADDRESS_LINE1")} text={cndData?.addressDetail?.addressLine1 || t("CS_NA")} />
            <Row className="border-none" label={t("ADDRESS_LINE2")} text={cndData?.addressDetail?.addressLine2|| t("CS_NA")} />
            <Row className="border-none" label={t("LANDMARK")} text={cndData?.addressDetail?.landmark || t("CS_NA")} />
            <Row className="border-none" label={t("CITY")} text={cndData?.addressDetail?.city|| t("CS_NA")} />
            <Row className="border-none" label={t("LOCALITY")} text={cndData?.addressDetail?.locality|| t("CS_NA")} />
            <Row className="border-none" label={t("PINCODE")} text={cndData?.addressDetail?.pinCode|| t("CS_NA")} />
          </StatusTable>

          <CardSubHeader style={cndStyles.citizenApplicantDetailCard}>{t("CND_WASTE_DETAILS")}</CardSubHeader>
          <StatusTable>
          <ApplicationTable
              t={t}
              data={operationRows}
              columns={columnName}
              getCellProps={(cellInfo) => ({
                style: cndStyles.citizenApplicationTable
              })}
              isPaginationRequired={false}
              totalRecords={operationRows.length}
            />
          </StatusTable>

          {cndData?.documentDetails && cndData?.documentDetails.length > 0 && (
            <React.Fragment>
              <br />
              <CardSubHeader>{t("CND_DOC_DETAILS")}</CardSubHeader>
              <CNDDocumnetPreview 
                documents={getOrderDocuments(applicationDocs)} 
                svgStyles={{}} 
                isSendBackFlow={false} 
                titleStyles={{ fontSize: "18px", fontWeight: 700, marginBottom: "10px" }} 
              />
            </React.Fragment>
          )}

          <CNDApplicationTimeLine application={application} id={application?.applicationNumber} userType={"citizen"} />
          {showToast && (
          <Toast
            error={showToast.key}
            label={t(showToast.label)}
            onClose={() => {
              setShowToast(null);
            }}
          />
        )}
        </Card>
      </div>
    </React.Fragment>
  );
};

export default CndApplicationDetails;