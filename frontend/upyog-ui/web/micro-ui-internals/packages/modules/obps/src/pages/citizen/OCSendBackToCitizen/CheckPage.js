import {
  Card,
  CardHeader,
  CardSubHeader,
  Header,
  LinkButton,
  Row,
  StatusTable,
  SubmitBar,
  Table,
  CardSectionHeader,
  EditIcon,
  PDFSvg,
  Toast,
  Loader
} from "@upyog/digit-ui-react-components";
import React, { useMemo, useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { useHistory, useRouteMatch } from "react-router-dom";
import Timeline from "../../../components/Timeline";
import ActionModal from "../BpaApplicationDetail/Modal";
import { convertToBPAObject, stringReplaceAll, convertEpochToDateDMY, getOrderDocuments } from "../../../utils";
import cloneDeep from "lodash/cloneDeep";
import { useQueryClient } from "react-query";
import DocumentsPreview from "../../../../../templates/ApplicationDetails/components/DocumentsPreview";
import usePreApprovedSearch from "../../../../../../libraries/src/hooks/obps/usePreApprovedSearch";

const CheckPage = ({ onSubmit, value }) => {
  const { t } = useTranslation();
  const history = useHistory();
  const match = useRouteMatch();
  let user = Digit.UserService.getUser(), BusinessService;
  const tenantId = user?.info?.permanentCity;
  const [selectedAction, setSelectedAction] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [showToast, setShowToast] = useState(null);
  const datafromAPI = value?.data?.edcrDetails;
  const queryClient = useQueryClient();
  const { data, address, owners, nocDocuments, documents, additionalDetails, subOccupancy,PrevStateDocuments, PrevStateNocDocuments, applicationNo } = value;
    
  let routeLink = `/upyog-ui/citizen/obps/sendbacktocitizen/${value.businessService=="BPA_OC" ? "ocbpa" : "bpa"}/${value?.tenantId}/${value?.applicationNo}`;
  if (value?.uiFlow?.flow === "OCBPA") routeLink = `/upyog-ui/citizen/obps/sendbacktocitizen/ocbpa/${value?.tenantId}/${value?.applicationNo}`;
  if (value.businessService === "BPA_LOW") BusinessService = "BPA.LOW_RISK_PERMIT_FEE";
  else if (value.businessService === "BPA"||value.businessService ==="BPA-PAP") BusinessService = "BPA.NC_APP_FEE";
  else BusinessService = "BPA.NC_OC_APP_FEE";

   // for application documents
   let improvedDoc = [];
   PrevStateDocuments?.map(preDoc => { improvedDoc.push({...preDoc, module: "OBPS"}) });
   documents?.documents?.map(appDoc => { improvedDoc.push({...appDoc, module: "OBPS"}) });
  value?.documents?.map(appDoc => { improvedDoc.push({...appDoc, module: "OBPS"}) });
   //for NOC documents 
   PrevStateNocDocuments?.map(preNocDoc => { improvedDoc.push({...preNocDoc, module: "NOC"}) });
   nocDocuments?.nocDocuments?.map(nocDoc => { improvedDoc.push({...nocDoc, module: "NOC"}) });

   const { data: pdfDetails, isLoading:pdfLoading, error } = Digit.Hooks.useDocumentSearch( improvedDoc, { enabled: improvedDoc?.length > 0 ? true : false});
   
   let applicationDocs = [], nocAppDocs = [];
   if (pdfDetails?.pdfFiles?.length > 0) {  
     pdfDetails?.pdfFiles?.map(pdfAppDoc => {
       if (pdfAppDoc?.module == "OBPS") applicationDocs.push(pdfAppDoc);
       if (pdfAppDoc?.module == "NOC") nocAppDocs.push(pdfAppDoc);
     });
   }
const { data: preApprovedResponse} = usePreApprovedSearch({drawingNo:value?.edcrNumber})
  const { data: reciept_data, isLoading: recieptDataLoading } = Digit.Hooks.useRecieptSearch(
    {
      tenantId: value?.tenantId,
      businessService: BusinessService,
      consumerCodes: value?.applicationNo,
    },
    { enabled: value?.applicationNo && BusinessService && value?.tenantId ? true : false }
  );

  const mutation = Digit.Hooks.obps.useObpsAPI(value?.tenantId, false);

  const workflowDetails = Digit.Hooks.useWorkflowDetails({
    tenantId: value?.tenantId,
    id: value?.applicationNo,
    moduleCode: "OBPS",
    config: {
      enabled: value?.applicationNo && value?.tenantId ? true : false
    }
  });

  const closeToast = () => {
    setShowToast(null);
  };

  const closeModal = () => {
    setSelectedAction(null);
    setShowModal(false);
  };

  const submitAction = (workflow) => {
    let bpaData = value;
    bpaData.assignee = [];
    const isOCBPS = true;
    const formdata = convertToBPAObject(bpaData, isOCBPS, true);
    mutation.mutate(
      { BPA: { ...formdata?.BPA, workflow } },
      {
        onError: (error, variables) => {
          setShowModal(false);
          setShowToast({ key: "error", action: error?.response?.data?.Errors[0]?.message || error });
          setTimeout(closeToast, 5000);
        },
        onSuccess: (data, variables) => {
          setShowModal(false);
          setShowToast({ key: "success", action: selectedAction });
          setTimeout(closeToast, 5000);
          queryClient.invalidateQueries("BPA_DETAILS_PAGE");
          queryClient.invalidateQueries("workFlowDetails");
          history.replace(`/upyog-ui/citizen/obps/sendbacktocitizen/ocbpa/${value?.tenantId}/${value?.applicationNo}/acknowledgement`, { data: value?.applicationNo });
        },
      }
    );
  }

  const tableHeader = [
    {
      name: "BPA_TABLE_COL_FLOOR",
      id: "Floor",
    },
    {
      name: "BPA_TABLE_COL_LEVEL",
      id: "Level",
    },
    {
      name: "BPA_TABLE_COL_OCCUPANCY",
      id: "Occupancy",
    },
    {
      name: "BPA_TABLE_COL_BUILDUPAREA",
      id: "BuildupArea",
    },
    {
      name: "BPA_TABLE_COL_FLOORAREA",
      id: "FloorArea",
    },
    {
      name: "BPA_TABLE_COL_CARPETAREA",
      id: "CarpetArea",
    }
  ];

  const accessData = (plot) => {
    const name = plot;
    return (originalRow, rowIndex, columns) => {
      return originalRow[name];
    }
  };

  const tableColumns = useMemo(
    () => {
      return tableHeader.map((ob) => ({
        Header: t(`${ob.name}`),
        accessor: accessData(ob.id),
        id: ob.id
      }));
    });

  function onActionSelect(action = "FORWARD") {
    setSelectedAction("FORWARD");
    setShowModal(true);
  }

  function getdate(date) {
    let newdate = Date.parse(date);
    return `${new Date(newdate).getDate().toString() + "/" + (new Date(newdate).getMonth() + 1).toString() + "/" + new Date(newdate).getFullYear().toString()
      }`;
  }

  function getFloorData(block) {
    let floors = [];
    block?.building?.floors.map((ob) => {
      floors.push({
        Floor: ob?.floorName||t(`BPA_FLOOR_NAME_${ob.number}`),
        Level: ob?.number || ob?.floorNo,
        Occupancy: "NA",
        BuildupArea: ob?.occupancies?.[0]?.builtUpArea || ob?.builtUpArea,
        FloorArea: ob?.occupancies?.[0]?.floorArea || ob?.builtUpArea,
        CarpetArea: ob?.occupancies?.[0]?.carpetArea || 0,
        key: ob?.floorName||t(`BPA_FLOOR_NAME_${ob.number}`),
      });
    });
    return floors;
  }

  function routeTo(jumpTo) {
    location.href = jumpTo;
  }

  function getBlockSubOccupancy(index){
    let subOccupancyString = "";
    let returnValueArray = [];
    subOccupancy && subOccupancy[`Block_${index+1}`] && subOccupancy[`Block_${index+1}`].map((ob) => {
      // subOccupancyString += `${t(ob.i18nKey)}, `;
      returnValueArray.push(`${t(stringReplaceAll(ob?.i18nKey?.toUpperCase(), "-", "_"))}`);
    })
    return returnValueArray?.length ? returnValueArray.join(', ') : "NA"
    // return subOccupancyString;
  }

  useEffect(() => {
    const workflow = { action: selectedAction }
    switch (selectedAction) {
      case "FORWARD":
        setShowModal(true);
    }
  }, [selectedAction]);

  if (pdfLoading || recieptDataLoading) {
    return <Loader />
  }

  return (
    <React.Fragment>
      <Timeline currentStep={4}  flow= {value?.uiFlow?.flow === "OCBPA" ? "OCBPA" : ""} />
      <Header styles={{marginLeft: "10px"}}>{t("BPA_STEPPER_SUMMARY_HEADER")}</Header>
      <Card style={{paddingRight:"16px"}}>
        <StatusTable>
          <Row className="border-none" label={t(`BPA_APPLICATION_NUMBER_LABEL`)} text={applicationNo?applicationNo:""} />
        </StatusTable>
    </Card>
      <Card style={{paddingRight:"16px"}}>
        <CardHeader>{t(`BPA_BASIC_DETAILS_TITLE`)}</CardHeader>
        <StatusTable>
          <Row className="border-none" label={t(`BPA_BASIC_DETAILS_APP_DATE_LABEL`)} text={convertEpochToDateDMY(data?.applicationDate)||value?.applicationDate} />
          <Row className="border-none" label={t(`BPA_BASIC_DETAILS_APPLICATION_TYPE_LABEL`)} text={t(`WF_BPA_${data?.applicationType||value?.additionalDetails?.applicationType}`)} />
          <Row className="border-none" label={t(`BPA_BASIC_DETAILS_SERVICE_TYPE_LABEL`)} text={t(data?.serviceType||value?.additionalDetails?.serviceType)} />
          <Row className="border-none" label={t(`BPA_BASIC_DETAILS_OCCUPANCY_LABEL`)} text={preApprovedResponse?.[0]?.drawingDetail?.occupancy||data?.occupancyType} />
          <Row className="border-none" label={t(`BPA_BASIC_DETAILS_RISK_TYPE_LABEL`)} text={t(`WF_BPA_${data?.riskType||value?.riskType}`)} />
          <Row className="border-none" label={t(`BPA_BASIC_DETAILS_APPLICATION_NAME_LABEL`)} text={data?.applicantName||"NA"} />
        </StatusTable>
      </Card>
      <Card style={{paddingRight:"16px"}}>
        <CardHeader>{t("BPA_PLOT_DETAILS_TITLE")}</CardHeader>
        <StatusTable>
          <Row className="border-none" label={t(`BPA_BOUNDARY_PLOT_AREA_LABEL`)} text={datafromAPI?.planDetail?.planInformation?.plotArea ? `${datafromAPI?.planDetail?.planInformation?.plotArea} ${t(`BPA_SQ_FT_LABEL`)}` :`${preApprovedResponse?.[0]?.drawingDetail?.plotArea} ${t(`BPA_SQ_FT_LABEL`)}`} />
          <Row className="border-none" label={t(`BPA_PLOT_NUMBER_LABEL`)} text={datafromAPI?.planDetail?.planInformation?.plotNo || value?.additionalDetails?.plotNo||"NA"} />
          <Row className="border-none" label={t(`BPA_KHATHA_NUMBER_LABEL`)} text={datafromAPI?.planDetail?.planInformation?.khataNo ||value?.additionalDetails?.khataNo||"NA"} />
          <Row className="border-none" label={t(`BPA_HOLDING_NUMBER_LABEL`)} text={data?.holdingNumber || t("CS_NA")} />
          <Row className="border-none" label={t(`BPA_BOUNDARY_LAND_REG_DETAIL_LABEL`)} text={data?.registrationDetails || t("CS_NA")} />
          <Row className="border-none" label={t(`BPA_BOUNDARY_WALL_LENGTH_LABEL`)} text={ data?.boundaryWallLength ||t("CS_NA")} />
        </StatusTable>
      </Card>
      <Card style={{paddingRight:"16px"}}>
        <CardHeader>{t("BPA_STEPPER_SCRUTINY_DETAILS_HEADER")}</CardHeader>
        <CardSubHeader>{t(value?.uiFlow?.flow === "OCBPA" ? "BPA_OC_EDCR_NO_LABEL" : "BPA_EDCR_DETAILS")}</CardSubHeader>
        <StatusTable style={{ border: "none" }}>
          <Row className="border-none" label={t("BPA_EDCR_NO_LABEL")} text={data?.scrutinyNumber?.edcrNumber || value?.edcrNumber||t("CS_NA")}></Row>
          <CardSubHeader>{t("BPA_UPLOADED_PLAN_DIAGRAM")}</CardSubHeader>
          <LinkButton
            label={<PDFSvg />}
            onClick={() => routeTo(datafromAPI?.updatedDxfFile||preApprovedResponse?.[0]?.documents.find(doc => doc?.additionalDetails?.fileName.includes("pdf"))?.additionalDetails?.fileUrl)}
          />
          <p style={{ marginTop: "8px", marginBottom: "20px", textAlign:"Left", fontSize: "16px", lineHeight: "19px", color: "#505A5F", fontWeight: "400" }}>{t(`BPA_UPLOADED_PLAN_DXF`)}</p>
          {datafromAPI?.planReport ? (
        <div>
      <CardSubHeader>{t("BPA_SCRUNTINY_REPORT_OUTPUT")}</CardSubHeader>
      <LinkButton
        label={ <PDFSvg /> }
          onClick={() => routeTo(datafromAPI?.planReport)}
       />
       
       </div>
       ):null}
        
        </StatusTable>
        <hr style={{ color: "#cccccc", backgroundColor: "#cccccc", height: "2px", marginTop: "20px", marginBottom: "20px" }} />
        <CardSubHeader>{value?.uiFlow?.flow === "OCBPA" ? t("BPA_ACTUAL_BUILDING_EXTRACT_HEADER") : t("BPA_BUILDING_EXTRACT_HEADER")}</CardSubHeader>
        <StatusTable>
          <Row className="border-none" label={t("BPA_TOTAL_BUILT_UP_AREA_HEADER")} text={`${preApprovedResponse?.[0]?.drawingDetail?.totalBuitUpArea} ${t("BPA_SQ_MTRS_LABEL")}`||`${datafromAPI?.planDetail?.blocks?.[0]?.building?.totalBuitUpArea} ${t("BPA_SQ_MTRS_LABEL")}`}></Row>
          <Row className="border-none" label={t("BPA_SCRUTINY_DETAILS_NUMBER_OF_FLOORS_LABEL")} text={datafromAPI?.planDetail?.blocks?.[0]?.building?.totalFloors||preApprovedResponse?.[0]?.drawingDetail?.blocks[0]?.building?.totalFloors}></Row>
          <Row className="border-none" label={t("BPA_HEIGHT_FROM_GROUND_LEVEL_FROM_MUMTY")} text={`${preApprovedResponse?.[0]?.drawingDetail?.blocks?.[0]?.building?.buildingHeight} ${t("BPA_MTRS_LABEL")}`||`${datafromAPI?.planDetail?.blocks?.[0]?.building?.declaredBuildingHeight} ${t("BPA_MTRS_LABEL")}`}></Row>
        </StatusTable>
        <hr style={{ color: "#cccccc", backgroundColor: "#cccccc", height: "2px", marginTop: "20px", marginBottom: "20px" }} />
        <CardSubHeader>{t("BPA_OCC_SUBOCC_HEADER")}</CardSubHeader>
        {datafromAPI?.planDetail?.blocks.map((block, index) => (
          <div key={index}>
            <CardSubHeader style={{marginTop:"15px"}}>{t("BPA_BLOCK_SUBHEADER")} {index + 1}</CardSubHeader>
            <StatusTable >
              <Row className="border-none" textStyle={{wordBreak:"break-word"}} label={t("BPA_SUB_OCCUPANCY_LABEL")} text={getBlockSubOccupancy(index) === "" ? t("CS_NA") : getBlockSubOccupancy(index)}></Row>
            </StatusTable>
            <div style={{ overflow: "scroll" }}>
              <Table
                className="customTable table-fixed-first-column table-border-style"
                t={t}
                disableSort={false}
                autoSort={true}
                manualPagination={false}
                isPaginationRequired={false}
                initSortId="S N "
                data={getFloorData(block)}
                columns={tableColumns}
                getCellProps={(cellInfo) => {
                  return {
                    style: {},
                  };
                }}
              />
            </div>
          </div>))}
          {preApprovedResponse?.[0]?.drawingDetail?.blocks.map((block,index)=>(
      <div key={index} style={preApprovedResponse?.[0]?.drawingDetail?.blocks?.length > 1 ?{ marginTop: "19px", background: "#FAFAFA", border: "1px solid #D6D5D4", borderRadius: "4px", padding: "8px", lineHeight: "19px", maxWidth: "960px", minWidth: "280px" } : {}}>
      <CardSubHeader style={{marginTop:"15px", fontSize: "18px"}}>{t("BPA_BLOCK_SUBHEADER")} {index+1}</CardSubHeader>
      <StatusTable >
      <Row className="border-none" textStyle={{wordBreak:"break-word"}} label={t("BPA_SUB_OCCUPANCY_LABEL")} text={getBlockSubOccupancy(index) === ""?t("CS_NA"):getBlockSubOccupancy(index)}></Row>
      </StatusTable>
      <div style={{overflow:"scroll"}}>
      <Table
        className="customTable table-fixed-first-column table-border-style"
        t={t}
        disableSort={false}
        autoSort={true}
        manualPagination={false}
        isPaginationRequired={false}
        initSortId="S N "
        data={getFloorData(block)}
        columns={tableColumns}
        getCellProps={(cellInfo) => {
          return {
            style: {},
          };
        }}
      />
      </div>
      </div>))}
          <hr style={{ color: "#cccccc", backgroundColor: "#cccccc", height: "2px", marginTop: "20px", marginBottom: "20px" }} />
        <CardSubHeader>{t("BPA_APP_DETAILS_DEMOLITION_DETAILS_LABEL")}</CardSubHeader>
        <StatusTable style={{ border: "none" }}>
          <Row className="border-none" label={t("BPA_APPLICATION_DEMOLITION_AREA_LABEL")} text={datafromAPI?.planDetail?.planInformation?.demolitionArea ? `${datafromAPI?.planDetail?.planInformation?.demolitionArea} ${t("BPA_SQ_MTRS_LABEL")} ` : t("CS_NA")}></Row>
        </StatusTable>
      </Card>
      {value?.uiFlow?.flow !== "OCBPA" ? <div>
        <Card style={{paddingRight:"16px"}}>
          <CardHeader>{t("BPA_NEW_TRADE_DETAILS_HEADER_DETAILS")}</CardHeader>
          <StatusTable>
            <Row className="border-none" label={t(`BPA_DETAILS_PIN_LABEL`)} text={address?.pincode || value?.landInfo?.address?.pincode||t("CS_NA")} />
            <Row className="border-none" label={t(`BPA_CITY_LABEL`)} text={address?.city?.name ||value?.landInfo?.address?.city ||t("CS_NA")} />
            <Row className="border-none" label={t(`BPA_LOC_MOHALLA_LABEL`)} text={address?.locality?.name ||value?.landInfo?.address?.locality?.name|| t("CS_NA")} />
            <Row className="border-none" label={t(`BPA_DETAILS_SRT_NAME_LABEL`)} text={address?.street ||value?.landInfo?.address?.street|| t("CS_NA")} />
            <Row className="border-none" label={t(`ES_NEW_APPLICATION_LOCATION_LANDMARK`)} text={address?.landmark ||value?.landInfo?.address?.landmark|| t("CS_NA")} />
          </StatusTable>
        </Card>
        <Card style={{paddingRight:"16px"}}>
          <CardHeader>{t("BPA_APPLICANT_DETAILS_HEADER")}</CardHeader>
          {owners?.owners && owners?.owners.map((ob, index) => (
            <div key={index}>
              {owners.owners.length > 1 && <CardSubHeader>{t("COMMON_OWNER")} {index + 1}</CardSubHeader>}
              <StatusTable>
                <Row className="border-none" label={t(`CORE_COMMON_NAME`)} text={ob?.name} />
                <Row className="border-none" label={t(`BPA_APPLICANT_GENDER_LABEL`)} text={t(ob?.gender?.i18nKey)} />
                <Row className="border-none" label={t(`CORE_COMMON_MOBILE_NUMBER`)} text={ob?.mobileNumber} />
                <Row className="border-none" label={t(`BPA_IS_PRIMARY_OWNER_LABEL`)} text={`${ob?.isPrimaryOwner}`} />
              </StatusTable>
            </div>))}
            {value?.landInfo && value?.landInfo?.owners.map((ob, index) => (
            <div key={index}>
              {value?.landInfo?.owners.length > 1 && <CardSubHeader>{t("COMMON_OWNER")} {index + 1}</CardSubHeader>}
              <StatusTable>
                <Row className="border-none" label={t(`CORE_COMMON_NAME`)} text={ob?.name} />
                <Row className="border-none" label={t(`BPA_APPLICANT_GENDER_LABEL`)} text={t(ob?.gender?.i18nKey||ob?.gender)} />
                <Row className="border-none" label={t(`CORE_COMMON_MOBILE_NUMBER`)} text={ob?.mobileNumber} />
                <Row className="border-none" label={t(`BPA_IS_PRIMARY_OWNER_LABEL`)} text={`${ob?.isPrimaryOwner}`} />
              </StatusTable>
            </div>))}
        </Card>
      </div> : null}

      <Card style={{paddingRight:"16px"}}>
      <StatusTable>
        <CardHeader>{t("BPA_DOCUMENT_DETAILS_LABEL")}</CardHeader>
        <LinkButton
          label={<EditIcon style={{ marginTop: "-10px", float: "right", position: "relative", bottom: "32px" }} />}
          style={{ width: "100px", display: "inline" }}
          onClick={() => routeTo(`${routeLink}/document-details`)}
        />
        {<DocumentsPreview documents={getOrderDocuments(applicationDocs)} svgStyles = {{}} isSendBackFlow = {false} isHrLine = {true} titleStyles ={{fontSize: "18px", lineHeight: "24px", "fontWeight": 700, marginBottom: "10px"}}/>}
        </StatusTable>
      </Card>
      <Card style={{paddingRight:"16px"}}>
      {nocDocuments && nocDocuments?.NocDetails?.map((noc, index) => (
  <div key={`noc-${index}`} style={nocDocuments?.NocDetails?.length > 1 ? { marginTop: "19px", background: "#FAFAFA", border: "1px solid #D6D5D4", borderRadius: "4px", padding: "8px", lineHeight: "19px", maxWidth: "960px", minWidth: "280px" } : {}}>
    <CardHeader>{t("BPA_NOC_DETAILS_SUMMARY")}</CardHeader>
          <LinkButton
            label={<EditIcon style={{ marginTop: "-10px", float: "right", position: "relative", bottom: "32px" }} />}
            style={{ width: "100px", display: "inline" }}
            onClick={() => routeTo(`${routeLink}/noc-details`)}
          />

        <CardSectionHeader style={{marginBottom: "24px"}}>{`${t(`BPA_${noc?.nocType}_HEADER`)}`}</CardSectionHeader>
        <StatusTable>
          <Row className="border-none" label={t(`BPA_${noc?.nocType}_LABEL`)} text={noc?.applicationNo} />
          <Row className="border-none" label={t(`BPA_NOC_STATUS`)} text={t(`${noc?.applicationStatus}`)} textStyle={noc?.applicationStatus == "APPROVED" || noc?.applicationStatus == "AUTO_APPROVED" ? {color : "#00703C"} : {color: "#D4351C"}} />
          {noc?.additionalDetails?.SubmittedOn ? <Row className="border-none" label={`${t("BPA_NOC_SUBMISSION_DATE")}`} text={noc?.additionalDetails?.SubmittedOn ? convertEpochToDateDMY(Number(noc?.additionalDetails?.SubmittedOn)) : "NA"} /> : null }
          {noc?.nocNo ? <Row className="border-none" label={`${t("BPA_APPROVAL_NUMBER_LABEL")}`} text={noc?.nocNo || "NA"} /> : null }
          {(noc?.applicationStatus === "APPROVED" || noc?.applicationStatus === "REJECTED" || noc?.applicationStatus === "AUTO_APPROVED" || noc?.applicationStatus === "AUTO_REJECTED") ? <Row className="border-none" label={`${t("BPA_APPROVED_REJECTED_ON_LABEL")}`} text= {convertEpochToDateDMY(Number(noc?.auditDetails?.lastModifiedTime))} /> : null }
          <Row className="border-none" label={t(`BPA_DOCUMENT_DETAILS_LABEL`)} text={""} />
          {<DocumentsPreview documents={getOrderDocuments(nocAppDocs?.filter(data => data?.documentType?.includes(noc?.nocType?.split("_")?.[0])), true)} svgStyles = {{}} isSendBackFlow = {false} isHrLine = {true} titleStyles ={{fontSize: "18px", lineHeight: "24px", "fontWeight": 700, marginBottom: "10px"}}/>}
        </StatusTable>
      </div>
      ))}
        </Card>
      <Card style={{paddingRight:"16px"}}>
        <CardSubHeader>{t("BPA_SUMMARY_FEE_EST")}</CardSubHeader>
        <StatusTable>
          {reciept_data?.Payments?.[0]?.paymentDetails?.[0]?.bill?.billDetails?.[0]?.billAccountDetails.map((bill, index) => (
            <div key={index}>
              <Row className="border-none" label={t(`${bill.taxHeadCode}`)} text={`₹ ${bill?.amount}`} />
            </div>
          ))}
          <Row className="border-none" label={t(`BPA_COMMON_TOTAL_AMT`)} text={`₹ ${reciept_data?.Payments?.[0]?.paymentDetails?.[0]?.bill?.billDetails?.[0]?.amount || "0"}`} />
        </StatusTable>
        <hr style={{ color: "#cccccc", backgroundColor: "#cccccc", height: "2px", marginTop: "20px", marginBottom: "20px" }} />
        <CardHeader>{t("BPA_COMMON_TOTAL_AMT")}</CardHeader>
        <CardHeader>₹ {reciept_data?.Payments?.[0]?.paymentDetails?.[0]?.bill?.billDetails?.[0]?.amount || "0"}</CardHeader>
        <SubmitBar label={t("BPA_COMMON_BUTTON_SUBMIT")} onSubmit={onActionSelect} />
      </Card>
      {showModal ? (
        <ActionModal
          t={t}
          action={selectedAction}
          tenantId={tenantId}
          id={value?.applicationNo}
          closeModal={closeModal}
          submitAction={submitAction}
          actionData={workflowDetails?.data?.timeline}
        />
      ) : null}
      {showToast && (
        <Toast
          error={showToast.key === "error" ? true : false}
          label={t(showToast.key === "success" ? `ES_OBPS_${showToast.action}_UPDATE_SUCCESS` : showToast.action)}
          onClose={closeToast}
        />
      )}
    </React.Fragment>
  );
};

export default CheckPage;
