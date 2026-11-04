import { CardHeader, Header, Toast, Card, StatusTable, Row, Loader, Menu, PDFSvg, SubmitBar, LinkButton, ActionBar, CheckBox, MultiLink, CardText, CardSubHeader } from "@nudmcdgnpm/digit-ui-react-components";
import React, { Fragment, useEffect, useState } from "react";
import { useParams,  } from "react-router-dom";
import { useQueryClient } from "@tanstack/react-query";
import { useTranslation } from "react-i18next";
import BPAApplicationTimeline from "./BPAApplicationTimeline";
import DocumentDetails from "../../../components/DocumentDetails";
import ActionModal from "./Modal";
import OBPSDocument from "../../../pageComponents/OBPSDocuments";
import SubOccupancyTable from "../../../../../templates/ApplicationDetails/components/SubOccupancyTable";
import InspectionReport from "../../../../../templates/ApplicationDetails/components/InspectionReport";
import { getBusinessServices, getCheckBoxLabelData, getBPAFormData, convertDateToEpoch, printPdf, downloadPdf, getOrderDocuments  } from "../../../utils";
import cloneDeep from "lodash/cloneDeep";
import DocumentsPreview from "../../../../../templates/ApplicationDetails/components/DocumentsPreview";
import ScruntinyDetails from "../../../../../templates/ApplicationDetails/components/ScruntinyDetails";
import { Link } from "react-router-dom";
import useBPADetailsPage from "../../../../../../libraries/src/hooks/obps/useBPADetailsPage";
const BpaApplicationDetail = () => {
  const { id } = useParams();
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateCode = Digit.ULBService.getStateId();
  const isMobile = window.Digit.Utils.browser.isMobile();
  const queryClient = useQueryClient();
  const [showToast, setShowToast] = useState(null);
  const [isTocAccepted, setIsTocAccepted] = useState(false); 
  const [displayMenu, setDisplayMenu] = useState(false);
  const [showModal, setShowModal] = useState(false);
  const [showTermsModal, setShowTermsModal] = useState(false);
  const [selectedAction, setSelectedAction] = useState(null);
  const [showOptions, setShowOptions] = useState(false);
  const [checkBoxVisible, setCheckBoxVisible] = useState(false);
  const [isEnableLoader, setIsEnableLoader] = useState(false);
  const [viewTimeline, setViewTimeline]=useState(false);
  sessionStorage.removeItem("BPA_SUBMIT_APP");
  sessionStorage.setItem("isEDCRDisable", JSON.stringify(true));
  sessionStorage.setItem("BPA_IS_ALREADY_WENT_OFF_DETAILS", JSON.stringify(false));

  const navigate = Digit.Hooks.useCustomNavigate();
  sessionStorage.setItem("bpaApplicationDetails", false);
  let isFromSendBack = false;
  const { data: stakeHolderDetails, isLoading: stakeHolderDetailsLoading } = Digit.Hooks.obps.useMDMS(stateCode, "StakeholderRegistraition", "TradeTypetoRoleMapping");
  const { isLoading: bpaDocsLoading, data: bpaDocs } = Digit.Hooks.obps.useMDMS(stateCode, "BPA", ["DocTypeMapping"]);
  const { data, isLoading } = useBPADetailsPage(tenantId, { applicationNo: id });
  const { isMdmsLoading, data: mdmsData } = Digit.Hooks.obps.useMDMS(stateCode, "BPA", ["RiskTypeComputation"]);
  const mutation = Digit.Hooks.obps.useObpsAPI(data?.applicationData?.tenantId, false);

  const WF_CITIZEN_APPROVAL_INPROCESS = "CITIZEN_APPROVAL_INPROCESS";

  const userInfo = Digit.UserService.getUser();
  const rolearray = userInfo?.info?.roles;
  const isCitizenApprovalInProcess = data?.applicationData?.status == WF_CITIZEN_APPROVAL_INPROCESS;
  const isInProgress = data?.applicationData?.status == "INPROGRESS";
  const isArchitect = rolearray?.some(role => role?.code === "BPA_ARCHITECT");
  const isActionBarVisible = !(isCitizenApprovalInProcess && isArchitect);
  
  let workflowDetails = Digit.Hooks.useWorkflowDetails({
    tenantId: data?.applicationData?.tenantId,
    id: id,
    moduleCode: "OBPS",
    config: {
      enabled: !!data
    }
  });

  let businessService = [];

  if(data && data?.applicationData?.businessService === "BPA_LOW")
  {
    businessService = ["BPA.LOW_RISK_PERMIT_FEE"]
  }
  else if(data && data?.applicationData?.businessService === "BPA" && data?.applicationData?.riskType === "HIGH")
  {
    businessService = ["BPA.NC_APP_FEE","BPA.NC_SAN_FEE"];
  }
  else
  {
    businessService = ["BPA.NC_OC_APP_FEE","BPA.NC_OC_SAN_FEE"];
  }

  useEffect(() => {
    if(!bpaDocsLoading && !isLoading){
      let filtredBpaDocs = [];
      if (bpaDocs?.BPA?.DocTypeMapping) {
        filtredBpaDocs = bpaDocs?.BPA?.DocTypeMapping?.filter(ob => (ob.WFState == "INPROGRESS" && ob.RiskType == data?.applicationData?.riskType && ob.ServiceType == data?.applicationData?.additionalDetails?.serviceType && ob.applicationType == data?.applicationData?.additionalDetails?.applicationType))
        let documents = data?.applicationDetails?.filter((ob) => ob.title === "BPA_DOCUMENT_DETAILS_LABEL")[0]?.additionalDetails?.obpsDocuments?.[0]?.values;
        let RealignedDocument = [];
        filtredBpaDocs && filtredBpaDocs?.[0]?.docTypes && filtredBpaDocs?.[0]?.docTypes.map((ob) => {
            documents && documents.filter(x => ob.code === x.documentType.slice(0,x.documentType.lastIndexOf("."))).map((doc) => {
                RealignedDocument.push(doc);
            })
        })
        const newApplicationDetails = data?.applicationDetails.map((obj) => {
          if(obj.title === "BPA_DOCUMENT_DETAILS_LABEL")
          {
            return {...obj, additionalDetails:{obpsDocuments:[{title:"",values:RealignedDocument}]}}
          }
          return obj;
        })
        data.applicationDetails = [...newApplicationDetails];
    }
    }
  },[bpaDocs,data])


  useEffect(() => {
    setCheckBoxVisible((isCitizenApprovalInProcess || isInProgress) && isActionBarVisible);
  }, [isCitizenApprovalInProcess, isInProgress, isArchitect]);

  const getTranslatedValues = (dataValue, isNotTranslated) => {
    if(dataValue) {
      return !isNotTranslated ? t(dataValue) : dataValue
    } else {
      return t("NA")
    }
  };


  async function getRecieptSearch({tenantId, payments, ...params}) {
    let response=null;
    if (payments?.fileStoreId ) {
       response = { filestoreIds: [payments?.fileStoreId] };      
    }
    else{
      const formattedStakeholderType=data?.applicationData?.additionalDetails?.typeOfArchitect
            const stakeholderType=formattedStakeholderType.charAt(0).toUpperCase()+formattedStakeholderType.slice(1).toLowerCase()
      const updatedpayments={
        ...payments,
       
            paymentDetails:[
              {
                ...payments.paymentDetails?.[0],
                additionalDetails:{
                  ...payments.paymentDetails[0].additionalDetails,
                  "propertyID":data?.applicationData?.additionalDetails?.propertyID,
                  "stakeholderType":stakeholderType,
                  "contact":data?.applicationData?.businessService==="BPA-PAP"? t("APPLICANT_CONTACT") : `${stakeholderType} Contact`,
                  "idType":data?.applicationData?.businessService==="BPA-PAP" ? t("APPLICATION_NUMBER"):`${stakeholderType} ID`,
                  "name":data?.applicationData?.businessService==="BPA-PAP" ? t("APPLICANT_NAME"):`${stakeholderType} Name`,
                },
              },
            ],  
         
      }
      response = await Digit.PaymentService.generatePdf(stateCode, { Payments: [{...updatedpayments}] }, "bpa-receipt");
    }
    const fileStore = await Digit.PaymentService.printReciept(stateCode, { fileStoreIds: response.filestoreIds[0] });
    window.open(fileStore[response?.filestoreIds[0]], "_blank");
  }

  async function getPermitOccupancyOrderSearch({tenantId}, order, mode="download") {
    let currentDate = new Date();
    data.applicationData.additionalDetails.runDate = convertDateToEpoch(currentDate.getFullYear() + '-' + (currentDate.getMonth() + 1) + '-' + currentDate.getDate());
    let requestData = {...data?.applicationData, edcrDetail:[{...data?.edcrDetails}]}
    let response = await Digit.PaymentService.generatePdf(tenantId, { Bpa: [requestData] }, order);
    const fileStore = await Digit.PaymentService.printReciept(tenantId, { fileStoreIds: response.filestoreIds[0] });
    window.open(fileStore[response?.filestoreIds[0]], "_blank");
    requestData["applicationType"] = data?.applicationData?.additionalDetails?.applicationType;
    let edcrResponse = await Digit.OBPSService.edcr_report_download({BPA: {...requestData}});
    const responseStatus = parseInt(edcrResponse.status, 10);
    if (responseStatus === 201 || responseStatus === 200) {
      mode == "print"
        ? printPdf(new Blob([edcrResponse.data], { type: "application/pdf" }))
        : downloadPdf(new Blob([edcrResponse.data], { type: "application/pdf" }), `edcrReport.pdf`);
    }
  }

  async function getRevocationPDFSearch({tenantId, ...params}) {
    let requestData = {...data?.applicationData}
    let response = await Digit.PaymentService.generatePdf(tenantId, { Bpa: [requestData] }, "bpa-revocation");
    const fileStore = await Digit.PaymentService.printReciept(tenantId, { fileStoreIds: response.filestoreIds[0] });
    window.open(fileStore[response?.filestoreIds[0]], "_blank");
  }

  useEffect(() => {
    const workflow = { action: selectedAction }
    switch (selectedAction) {
      case "APPROVE":
      case "SEND_TO_ARCHITECT":
      case "APPLY":
      case "SKIP_PAYMENT":
        setShowModal(true);
    }
  }, [selectedAction]);

  const closeToast = () => {
    setShowToast(null);
  };

  const downloadDiagram = (val) => {
    location.href = val;
  }

  const handleChange = () => {

  }

  const closeModal = () => {
    setSelectedAction(null);
    setShowModal(false);
  };

  const closeTermsModal = () => {
    setShowTermsModal(false);
  }

  function onActionSelect(action) {
    let path = data?.applicationData?.businessService == "BPA_OC" ? "ocbpa" : "bpa";  
    if(action === "FORWARD") {
      navigate(`/upyog-ui/citizen/obps/sendbacktocitizen/ocbpa/${data?.applicationData?.tenantId}/${data?.applicationData?.applicationNo}/check`, { replace: true, state: { data: data?.applicationData, edcrDetails: data?.edcrDetails } });
    }
    if (action === "PAY") {
      window.location.assign(`${window.location.origin}/upyog-ui/citizen/payment/collect/${`${getBusinessServices(data?.businessService, data?.applicationStatus)}/${id}?tenantId=${data?.tenantId}`}`);
    }
    if (action === "SEND_TO_CITIZEN"){
      if (workflowDetails?.data?.processInstances?.length > 2) {
        window.location.replace(`/upyog-ui/citizen/obps/editApplication/${path}/${data?.applicationData?.tenantId}/${data?.applicationData?.applicationNo}`)
      } else {
        getBPAFormData(data?.applicationData, mdmsData, navigate, t)
      }
    }
    setSelectedAction(action);
    setDisplayMenu(false);
  }

  function checkForSubmitDisable () {
    if(checkBoxVisible) return isFromSendBack ? !isFromSendBack : !isTocAccepted;
    else return false;
  }

  const submitAction = (workflow) => {
    setIsEnableLoader(true);
    mutation.mutate(
      { BPA: { ...data?.applicationData, workflow } },
      {
        onError: (error, variables) => {
          setIsEnableLoader(false);
          setShowModal(false);
          setShowToast({ key: "error", action: error?.response?.data?.Errors[0]?.message ? error?.response?.data?.Errors[0]?.message : error });
          setTimeout(closeToast, 5000);
        },
        onSuccess: (data, variables) => {
          setIsEnableLoader(false);
          navigate(`/upyog-ui/citizen/obps/response`, { replace: true, state: { data: data } });
          setShowModal(false);
          setShowToast({ key: "success", action: selectedAction });
          setTimeout(closeToast, 5000);
          queryClient.invalidateQueries({ queryKey: ["BPA_DETAILS_PAGE"] });
          queryClient.invalidateQueries({ queryKey: ["workFlowDetails"] });
        },
      }
    );
  }

  if (workflowDetails?.data?.nextActions?.length > 0 && isCitizenApprovalInProcess) {
    if (isCitizenApprovalInProcess) {
      if (rolearray?.some(role => role?.code === "CITIZEN")) {
        workflowDetails.data.nextActions = workflowDetails?.data?.nextActions;
      } else {
        workflowDetails.data.nextActions = [];
      }
    }
     else if (isInProgress) {
      let isArchitect = false;
      stakeHolderDetails?.StakeholderRegistraition?.TradeTypetoRoleMapping?.map(type => {
        type?.role?.map(role => { roles.push(role); });
      });
      const uniqueRoles = roles.filter((item, i, ar) => ar.indexOf(item) === i);
      if (rolearray?.length > 1) {
        rolearray.forEach(role => {
          if (uniqueRoles.includes(role.code)) {
            isArchitect = true;
          }
        })
      }
      if (isArchitect) {
        workflowDetails.data.nextActions = workflowDetails?.data?.nextActions;
      } else {
        workflowDetails.data.nextActions = [];
      }
    }
  }


  if (workflowDetails?.data?.processInstances?.[0]?.action === "SEND_BACK_TO_CITIZEN") {
      if(isTocAccepted) setIsTocAccepted(true);
      isFromSendBack = true;
      if (rolearray?.some(role => role?.code === "CITIZEN")) {
        workflowDetails.data.nextActions = workflowDetails?.data?.nextActions;
      } else {
        workflowDetails.data.nextActions = [];
      }
  }

  if (isLoading || isEnableLoader) {
    return <Loader />
  }

  let dowloadOptions = [];

  if (data?.collectionBillDetails?.length > 0) {
    const bpaPayments = cloneDeep(data?.collectionBillDetails);
    bpaPayments.forEach(pay => {
      if (pay?.paymentDetails[0]?.businessService === "BPA.NC_OC_APP_FEE") {
        dowloadOptions.push({
          order: 1,
          label: t("BPA_APP_FEE_RECEIPT"),
          onClick: () => getRecieptSearch({ tenantId: data?.applicationData?.tenantId, payments: pay, consumerCodes: data?.applicationData?.applicationNo }),
        });
      }

      if (pay?.paymentDetails[0]?.businessService === "BPA.NC_OC_SAN_FEE") {
        dowloadOptions.push({
          order: 2,
          label: t("BPA_OC_DEV_PEN_RECEIPT"),
          onClick: () => getRecieptSearch({ tenantId: data?.applicationData?.tenantId, payments: pay, consumerCodes: data?.applicationData?.applicationNo }),
        });
      }

      if (pay?.paymentDetails[0]?.businessService === "BPA.LOW_RISK_PERMIT_FEE") {
        dowloadOptions.push({
          order: 1,
          label: t("BPA_FEE_RECEIPT"),
          onClick: () => getRecieptSearch({ tenantId: data?.applicationData?.tenantId, payments: pay, consumerCodes: data?.applicationData?.applicationNo }),
        });
      }

      if (pay?.paymentDetails[0]?.businessService === "BPA.NC_APP_FEE") {
        dowloadOptions.push({
          order: 1,
          label: t("BPA_APP_FEE_RECEIPT"),
          onClick: () => getRecieptSearch({ tenantId: data?.applicationData?.tenantId, payments: pay, consumerCodes: data?.applicationData?.applicationNo }),
        });
      }

      if (pay?.paymentDetails[0]?.businessService === "BPA.NC_SAN_FEE") {
        dowloadOptions.push({
          order: 2,
          label: t("BPA_SAN_FEE_RECEIPT"),
          onClick: () => getRecieptSearch({ tenantId: data?.applicationData?.tenantId, payments: pay, consumerCodes: data?.applicationData?.applicationNo }),
        });
      }
    })
  }


  if(data && data?.applicationData?.businessService === "BPA_LOW" && data?.collectionBillDetails?.length > 0) {
    !(data?.applicationData?.status.includes("REVOCATION")) && dowloadOptions.push({
      order: 3,
      label: t("BPA_PERMIT_ORDER"),
      onClick: () => getPermitOccupancyOrderSearch({tenantId: data?.applicationData?.tenantId},"buildingpermit-low"),
    });
    (data?.applicationData?.status.includes("REVOCATION")) && dowloadOptions.push({
      order: 3,
      label: t("BPA_REVOCATION_PDF_LABEL"),
      onClick: () => getRevocationPDFSearch({tenantId: data?.applicationData?.tenantId}),
    });
    
  } else if(data && (data?.applicationData?.businessService === "BPA"||data?.applicationData?.businessService === "BPA-PAP") && data?.collectionBillDetails?.length > 0) {
    if(data?.applicationData?.status==="APPROVED"){
    dowloadOptions.push({
      order: 3,
      label: t("BPA_PERMIT_ORDER"),
      onClick: () => getPermitOccupancyOrderSearch({tenantId: data?.applicationData?.tenantId},"buildingpermit"),
    });}
  } else {
    if(data?.applicationData?.status==="APPROVED"){
      dowloadOptions.push({
        order: 3,
        label: t("BPA_OC_CERTIFICATE"),
        onClick: () => getPermitOccupancyOrderSearch({tenantId: data?.applicationData?.tenantId},"occupancy-certificate"),
      });
    }
  }

  if(data?.comparisionReport){
    dowloadOptions.push({
      order: 4,
      label: t("BPA_COMPARISON_REPORT_LABEL"),
      onClick: () => window.open(data?.comparisionReport?.comparisonReport, "_blank"),
    });
  }

  dowloadOptions.sort(function (a, b) { return a.order - b.order; });

  if (workflowDetails?.data?.nextActions?.length > 0) {
    workflowDetails.data.nextActions = workflowDetails?.data?.nextActions?.filter(actn => actn.action !== "INITIATE");
    workflowDetails.data.nextActions = workflowDetails?.data?.nextActions?.filter(actn => actn.action !== "ADHOC");
    workflowDetails.data.nextActions = workflowDetails?.data?.nextActions?.filter(actn => actn.action !== "SKIP_PAYMENT");
  };

  if (data?.applicationDetails?.length > 0) {
    data.applicationDetails = data?.applicationDetails?.length > 0 && data?.applicationDetails?.filter(bpaData => Object.keys(bpaData).length !== 0);
  }


  const getStatusClass = (status) => {
    if (!status) return "status-default";
    const s = String(status).toUpperCase();
    if (s.includes("APPROV") || s.includes("ACTIVE") || s.includes("PAID") || s.includes("COMPLET")) {
      return "status-approved";
    }
    if (s.includes("REJECT") || s.includes("CANCEL") || s.includes("REVOC") || s.includes("INACTIVE")) {
      return "status-rejected";
    }
    if (s.includes("PEND") || s.includes("INIT") || s.includes("SUBMIT") || s.includes("PROG") || s.includes("ARCHITECT") || s.includes("CITIZEN") || s.includes("SCRUTINY")) {
      return "status-pending";
    }
    return "status-default";
  };

  const getSectionIcon = (titleKey = "") => {
    const tk = String(titleKey).toUpperCase();
    if (tk.includes("BASIC")) {
      return (
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#7c3aed" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
          <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" />
          <polyline points="14 2 14 8 20 8" />
          <line x1="16" y1="13" x2="8" y2="13" />
          <line x1="16" y1="17" x2="8" y2="17" />
        </svg>
      );
    }
    if (tk.includes("PLOT") || tk.includes("LAND")) {
      return (
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#7c3aed" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
          <polygon points="1 6 1 22 8 18 16 22 23 18 23 2 16 6 8 2 1 6" />
          <line x1="8" y1="2" x2="8" y2="18" />
          <line x1="16" y1="6" x2="16" y2="22" />
        </svg>
      );
    }
    if (tk.includes("OWNER") || tk.includes("APPLICANT")) {
      return (
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#7c3aed" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
          <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" />
          <circle cx="9" cy="7" r="4" />
          <path d="M23 21v-2a4 4 0 0 0-3-3.87" />
          <path d="M16 3.13a4 4 0 0 1 0 7.75" />
        </svg>
      );
    }
    if (tk.includes("DOC")) {
      return (
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#7c3aed" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
          <path d="M13 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V9z" />
          <polyline points="13 2 13 9 20 9" />
        </svg>
      );
    }
    if (tk.includes("NOC")) {
      return (
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#7c3aed" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
          <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" />
          <polyline points="9 12 11 14 15 10" />
        </svg>
      );
    }
    if (tk.includes("INSPECTION")) {
      return (
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#7c3aed" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
          <circle cx="11" cy="11" r="8" />
          <line x1="21" y1="21" x2="16.65" y2="16.65" />
        </svg>
      );
    }
    if (tk.includes("SCRUTINY") || tk.includes("EDCR")) {
      return (
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#7c3aed" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
          <path d="M9 11l3 3L22 4" />
          <path d="M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11" />
        </svg>
      );
    }
    if (tk.includes("BUILDING") || tk.includes("OCCUPANCY") || tk.includes("PROPOSED")) {
      return (
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#7c3aed" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
          <rect x="4" y="2" width="16" height="20" rx="2" ry="2" />
          <line x1="9" y1="22" x2="9" y2="2" />
          <line x1="8" y1="6" x2="5" y2="6" />
          <line x1="8" y1="10" x2="5" y2="10" />
          <line x1="8" y1="14" x2="5" y2="14" />
          <line x1="8" y1="18" x2="5" y2="18" />
          <line x1="19" y1="6" x2="16" y2="6" />
          <line x1="19" y1="10" x2="16" y2="10" />
          <line x1="19" y1="14" x2="16" y2="14" />
          <line x1="19" y1="18" x2="16" y2="18" />
        </svg>
      );
    }
    return (
      <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#7c3aed" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <circle cx="12" cy="12" r="10" />
        <line x1="12" y1="16" x2="12" y2="12" />
        <line x1="12" y1="8" x2="12.01" y2="8" />
      </svg>
    );
  };

  const getCheckBoxLable = () => {
    return (
      <div>
        <span>{`${t("BPA_I_AGREE_THE_LABEL")} `}</span>
        <span className="obps-index-clickable" onClick={() => setShowTermsModal(!showTermsModal)}>{t(`BPA_TERMS_AND_CONDITIONS_LABEL`)}</span>
      </div>
    )
  }
  const handleViewTimeline=()=>{ 
    const timelineSection=document.getElementById('timeline');
      if(timelineSection){
        timelineSection.scrollIntoView({behavior: 'smooth'});
      } 
      setViewTimeline(true);   
  };

  const results = data?.applicationDetails?.filter(element => {
    if (Object.keys(element).length !== 0) {
      return true;
    }
    return false;
  });

  if (results?.length > 0) {
    data.applicationDetails = results;
  }

  const appStatus = data?.applicationData?.status;

  return (
    <div className="obps-details-page-container">
      {/* Modern Header Bar */}
      <div className="obps-details-header-wrap">
        <div className="obps-details-title-block">
          <h1 className="obps-details-page-title">{t("CS_TITLE_APPLICATION_DETAILS")}</h1>
        </div>
        <div className="obps-details-actions">
          {dowloadOptions && dowloadOptions.length > 0 && (
            <MultiLink
              className="multilinkWrapper"
              onHeadClick={() => setShowOptions(!showOptions)}
              displayOptions={showOptions}
              options={dowloadOptions}
            />
          )}
          <button type="button" className="obps-btn-timeline" onClick={handleViewTimeline}>
            <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <circle cx="12" cy="12" r="10" />
              <polyline points="12 6 12 12 16 14" />
            </svg>
            {t("VIEW_TIMELINE")}
          </button>
        </div>
      </div>

      {/* Hero Overview Card */}
      <div className="obps-app-hero-card">
        <div className="obps-app-hero-header">
          <div className="obps-app-id-section">
            <div className="obps-app-id-label">{t("BPA_BASIC_DETAILS_APP_NO_LABEL")}</div>
            <div className="obps-app-id-value">{data?.applicationData?.applicationNo || id}</div>
            {data?.applicationData?.edcrNumber && (
              <div className="obps-app-sub-id">
                {t("BPA_EDCR_NO_LABEL")}: {data?.applicationData?.edcrNumber}
              </div>
            )}
            {data?.applicationData?.additionalDetails?.propertyID && (
              <div className="obps-app-sub-id">
                {t("BPA_PROPERTY_ID_LABEL")}: {data?.applicationData?.additionalDetails?.propertyID}
              </div>
            )}
          </div>
          <div className={`obps-app-status-badge ${getStatusClass(appStatus)}`}>
            {t(`WF_BPA_${appStatus}`) || t(`CS_${appStatus}`) || t(appStatus) || t("CS_NA")}
          </div>
        </div>

        <div className="obps-app-summary-grid">
          <div className="obps-app-summary-item">
            <span className="obps-app-summary-label">{t("BPA_BASIC_DETAILS_SERVICE_TYPE_LABEL")}</span>
            <span className="obps-app-summary-val">
              {data?.applicationData?.additionalDetails?.serviceType
                ? t(`BPA_SERVICETYPE_${data?.applicationData?.additionalDetails?.serviceType}`)
                : t(data?.applicationData?.businessService) || t("CS_NA")}
            </span>
          </div>

          <div className="obps-app-summary-item">
            <span className="obps-app-summary-label">{t("BPA_BASIC_DETAILS_APPLICATION_TYPE_LABEL")}</span>
            <span className="obps-app-summary-val">
              {data?.applicationData?.additionalDetails?.applicationType
                ? t(`BPA_APPTYPE_${data?.applicationData?.additionalDetails?.applicationType}`)
                : t("BUILDING_PLAN_SCRUTINY") || t("CS_NA")}
            </span>
          </div>

          {data?.applicationData?.riskType && (
            <div className="obps-app-summary-item">
              <span className="obps-app-summary-label">{t("BPA_BASIC_DETAILS_RISK_TYPE_LABEL")}</span>
              <span className="obps-app-summary-val">
                {t(`BPA_RISK_TYPE_${data?.applicationData?.riskType}`) || data?.applicationData?.riskType}
              </span>
            </div>
          )}

          <div className="obps-app-summary-item">
            <span className="obps-app-summary-label">{t("BPA_CITY_LABEL") || t("CS_COMMON_CITY")}</span>
            <span className="obps-app-summary-val">
              {t(`TENANT_TENANTS_${data?.applicationData?.tenantId?.toUpperCase().replace(/[.]/g, "_")}`) || data?.applicationData?.tenantId || t("CS_NA")}
            </span>
          </div>
        </div>
      </div>

      {/* Detailed Section Cards */}
      {data?.applicationDetails?.filter((ob) => Object.keys(ob).length > 0).map((detail, index) => {
        if (detail?.isNotAllowed) return null;

        return (
          <div key={index} className="obps-details-card">
            {!detail?.isTitleVisible && detail?.title ? (
              <div className="obps-section-header">
                <div className="obps-section-title">
                  {getSectionIcon(detail?.title)}
                  <span>{t(detail?.title)}</span>
                </div>
              </div>
            ) : null}

            {/* If Document Details Section */}
            {detail?.isDocumentDetails ? (
              <div className="obps-documents-grid">
                {getOrderDocuments(detail?.additionalDetails?.obpsDocuments?.[0]?.values)?.map((docGroup, docIdx) => (
                  <div key={docIdx} className="obps-doc-group-card">
                    {docGroup?.title && (
                      <div className="obps-doc-group-header">
                        <span className="obps-doc-category-label">{t(docGroup?.title)}</span>
                      </div>
                    )}
                    <div className="obps-doc-group-items">
                      {docGroup?.values?.map((value, vIdx) => (
                        <a
                          key={vIdx}
                          target="_blank"
                          rel="noopener noreferrer"
                          href={value?.url}
                          className="app-details-doc-card"
                        >
                          <div className="app-details-doc-icon-wrap">
                            <PDFSvg />
                          </div>
                          <div className="app-details-doc-info">
                            <p className="app-details-doc-title">{t(value?.title)}</p>
                          </div>
                          <span className="app-details-doc-action">{t("CS_COMMON_VIEW") || "View"} &rarr;</span>
                        </a>
                      ))}
                    </div>
                  </div>
                ))}
              </div>
            ) : (
              <StatusTable className="obps-details-grid">
                {/* Common Values */}
                {(detail?.isCommon && detail?.values?.length > 0) ? detail?.values?.map((value, vIdx) => {
                  if (value?.isUnit) return <Row key={vIdx} className="border-none" label={t(value?.title)} text={value?.value ? `${getTranslatedValues(value?.value, value?.isNotTranslated)} ${t(value?.isUnit)}` : t("CS_NA")} />;
                  if (value?.isLink) return <Row key={vIdx} className="border-none" label={t(value?.title)} text={<div><Link to={value?.to}><span className="link obps-index-wrapper-6">{value?.value}</span></Link></div>} />;
                  return <Row key={vIdx} className="border-none" label={t(value?.title)} text={getTranslatedValues(value?.value, value?.isNotTranslated) || t("CS_NA")} />;
                }) : null}

                {/* Additional Common Values */}
                {!detail?.isFeeDetails && detail?.additionalDetails?.values?.length > 0 ? detail?.additionalDetails?.values?.map((value, vIdx) => (
                  <div key={vIdx} className={value?.isHeader ? "full-width" : ""}>
                    {!detail?.isTitleRepeat && !value?.isHeader && !value?.isUnit ? <Row className="border-none" label={t(value?.title)} textStyle={value?.value === "Paid"?{color:"darkgreen"}:(value?.value === "Unpaid"?{color:"red"}:{})} text={value?.value ? getTranslatedValues(value?.value, value?.isNotTranslated) : t("CS_NA")} /> : null}
                    {!detail?.isTitleRepeat && value?.isUnit ? <Row className="border-none" label={t(value?.title)} text={value?.value ? `${getTranslatedValues(value?.value, value?.isNotTranslated)} ${t(value?.isUnit)}` : t("CS_NA")} /> : null}
                    {!detail?.isTitleRepeat && value?.isHeader ? <CardSubHeader className="obps-scrutiny-details-header">{t(value?.title)}</CardSubHeader> : null}
                  </div>
                )) : null}

                {/* SubOccupancy Values */}
                {(detail?.isSubOccupancyTable && detail?.additionalDetails?.subOccupancyTableDetails) ? (
                  <div className="full-width">
                    <SubOccupancyTable edcrDetails={detail?.additionalDetails} applicationData={data?.applicationData} />
                  </div>
                ) : null}

                {/* Scrutiny Values */}
                {(detail?.isScrutinyDetails && detail?.additionalDetails?.scruntinyDetails?.length > 0) ? (
                  <div className="full-width">
                    <div className="obps-documents-grid obps-index-top-spacing-2">
                      {detail?.additionalDetails?.scruntinyDetails.map((scrutiny, sIdx) => (
                        <div key={sIdx} className="obps-doc-group-card">
                          <div className="obps-doc-group-header">
                            <span className="obps-doc-category-label">{t(scrutiny?.title)}</span>
                          </div>
                          <div className="obps-doc-group-items">
                            <div
                              className="app-details-doc-card obps-pointer-cursor"
                              onClick={() => downloadDiagram(scrutiny?.value)}
                            >
                              <div className="app-details-doc-icon-wrap">
                                <PDFSvg />
                              </div>
                              <div className="app-details-doc-info">
                                <p className="app-details-doc-title">{scrutiny?.text ? t(scrutiny?.text) : t(scrutiny?.title)}</p>
                              </div>
                              <span className="app-details-doc-action">{t("CS_COMMON_DOWNLOAD") || "Download"} &darr;</span>
                            </div>
                          </div>
                        </div>
                      ))}
                    </div>
                  </div>
                ) : null}

                {/* Owner Values */}
                {(detail?.isOwnerDetails && detail?.additionalDetails?.owners?.length > 0) ? detail?.additionalDetails?.owners.map((owner, oIdx) => (
                  <div key={oIdx} className={detail?.additionalDetails?.owners?.length > 1 ? "obps-sub-owner-card" : "full-width"}>
                    {detail?.additionalDetails?.owners?.length > 1 && (
                      <div className="obps-sub-owner-title">
                        <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                          <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
                          <circle cx="12" cy="7" r="4" />
                        </svg>
                        <span>{`${t("Owner")} - ${oIdx + 1}`}</span>
                      </div>
                    )}
                    <div className="obps-details-grid">
                      {owner?.values.map((value, vIdx) => (
                        <Row key={vIdx} className="border-none" label={t(value?.title)} text={getTranslatedValues(value?.value, value?.isNotTranslated) || t("CS_NA")} />
                      ))}
                    </div>
                  </div>
                )) : null}

                {/* FieldInspection Values */}
                {(detail?.isFieldInspection && data?.applicationData?.additionalDetails?.fieldinspection_pending?.length > 0) && (
                  <div className="full-width">
                    <InspectionReport isCitizen={true} fiReport={data?.applicationData?.additionalDetails?.fieldinspection_pending} />
                  </div>
                )}

                {/* NOC Values */}
                {detail?.additionalDetails?.noc?.length > 0 ? detail?.additionalDetails?.noc.map((nocob, ind) => (
                  <div key={ind} className="obps-noc-card">
                    <div className="obps-noc-header">
                      <div className="obps-noc-title">
                        {t(`BPA_${detail?.additionalDetails?.data?.nocType}_HEADER`)}
                      </div>
                      {detail?.values?.[1]?.value && (
                        <div className={`obps-app-status-badge ${getStatusClass(detail?.values?.[1]?.value)}`}>
                          {getTranslatedValues(detail?.values?.[1]?.value, detail?.values?.[1]?.isNotTranslated)}
                        </div>
                      )}
                    </div>
                    <div className="obps-details-grid">
                      <Row className="border-none" label={t(`${detail?.values?.[0]?.title}`)} text={getTranslatedValues(detail?.values?.[0]?.value, detail?.values?.[0]?.isNotTranslated)} />
                      {detail?.values?.[2]?.value ? <Row className="border-none" label={t(`${detail?.values?.[2]?.title}`)} text={getTranslatedValues(detail?.values?.[2]?.value, detail?.values?.[2]?.isNotTranslated)} /> : null}
                      {detail?.values?.[3]?.value ? <Row className="border-none" label={t(`${detail?.values?.[3]?.title}`)} text={getTranslatedValues(detail?.values?.[3]?.value, detail?.values?.[3]?.isNotTranslated)} /> : null}
                      {detail?.values?.[4]?.value ? <Row className="border-none" label={t(`${detail?.values?.[4]?.title}`)} text={getTranslatedValues(detail?.values?.[4]?.value, detail?.values?.[4]?.isNotTranslated)} /> : null}
                    </div>
                    {nocob?.values ? (
                      <div className="obps-documents-grid obps-index-top-spacing-3">
                        {getOrderDocuments(nocob?.values, true)?.map((docGroup, docIdx) => (
                          <div key={docIdx} className="obps-doc-group-card">
                            {docGroup?.title && (
                              <div className="obps-doc-group-header">
                                <span className="obps-doc-category-label">{t(docGroup?.title)}</span>
                              </div>
                            )}
                            <div className="obps-doc-group-items">
                              {docGroup?.values?.map((value, vIdx) => (
                                <a
                                  key={vIdx}
                                  target="_blank"
                                  rel="noopener noreferrer"
                                  href={value?.url}
                                  className="app-details-doc-card"
                                >
                                  <div className="app-details-doc-icon-wrap">
                                    <PDFSvg />
                                  </div>
                                  <div className="app-details-doc-info">
                                    <p className="app-details-doc-title">{t(value?.title)}</p>
                                  </div>
                                  <span className="app-details-doc-action">{t("CS_COMMON_VIEW") || "View"} &rarr;</span>
                                </a>
                              ))}
                            </div>
                          </div>
                        ))}
                      </div>
                    ) : (
                      <div className="obps-index-top-spacing-2"><CardText>{t("BPA_NO_DOCUMENTS_UPLOADED_LABEL")}</CardText></div>
                    )}
                  </div>
                )) : null}

                {/* Permit Values */}
                {(!detail?.isTitleVisible && detail?.additionalDetails?.permit?.length > 0) ? detail?.additionalDetails?.permit?.map((value, pIdx) => (
                  <div key={pIdx} className="full-width">
                    <CardText>{value?.title}</CardText>
                  </div>
                )) : null}

                {/* Fee Values */}
                {detail?.additionalDetails?.inspectionReport && detail?.isFeeDetails && (
                  <div className="full-width">
                    <ScruntinyDetails scrutinyDetails={detail?.additionalDetails} paymentsList={[]} />
                  </div>
                )}
              </StatusTable>
            )}
          </div>
        );
      })}

      {/* Application Timeline Card */}
      <div className="obps-details-card" id="timeline">
        <div className="obps-section-header">
          <div className="obps-section-title">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#7c3aed" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <circle cx="12" cy="12" r="10" />
              <polyline points="12 6 12 12 16 14" />
            </svg>
            <span>{t("CS_APPLICATION_TIMELINE") || t("VIEW_TIMELINE") || "Application Timeline"}</span>
          </div>
        </div>

        <BPAApplicationTimeline application={data?.applicationData} id={id} />

        {!workflowDetails?.isLoading && workflowDetails?.data?.nextActions?.length > 0 && !isFromSendBack && checkBoxVisible && (
          <div className="obps-agreement-wrap">
            <CheckBox
              checked={isTocAccepted}
              label={getCheckBoxLable()}
              onChange={() => { setIsTocAccepted(!isTocAccepted); isTocAccepted ? setDisplayMenu(!isTocAccepted) : ""; }}
            />
          </div>
        )}

        {!workflowDetails?.isLoading && workflowDetails?.data?.nextActions?.length > 1 && isActionBarVisible && (
          <ActionBar>
            <div className="obps-inspection-report-fullwidth">
              {displayMenu && workflowDetails?.data?.nextActions ? (
                <Menu
                  localeKeyPrefix={"WF_BPA"}
                  options={workflowDetails?.data?.nextActions.map((action) => action.action)}
                  t={t}
                  onSelect={onActionSelect}
                />
              ) : null}
              <SubmitBar disabled={checkForSubmitDisable(isFromSendBack, isTocAccepted)} label={t("ES_COMMON_TAKE_ACTION")} onSubmit={() => setDisplayMenu(!displayMenu)} />
            </div>
          </ActionBar>
        )}

        {!workflowDetails?.isLoading && workflowDetails?.data?.nextActions?.length === 1 && (
          <ActionBar>
            <div className="obps-inspection-report-fullwidth">
              <button 
                className={`text-white ${isMobile ? "font-size-19" : ""} ${checkForSubmitDisable(isFromSendBack, isTocAccepted) ? "submit-bar-disabled" : "submit-bar"}`}
                disabled={checkForSubmitDisable(isFromSendBack, isTocAccepted)} 
                name={workflowDetails?.data?.nextActions?.[0]?.action} 
                value={workflowDetails?.data?.nextActions?.[0]?.action}
                onClick={(e) => { onActionSelect(e.target.value); }}>
                {t(`WF_BPA_${workflowDetails?.data?.nextActions?.[0]?.action}`)}
              </button>
            </div>
          </ActionBar>
        )}
      </div>

      {showTermsModal ? (
        <ActionModal
          t={t}
          action={"TERMS_AND_CONDITIONS"}
          tenantId={tenantId}
          id={id}
          closeModal={closeTermsModal}
          submitAction={submitAction}
          applicationData={data?.applicationData || {}}
        />
      ) : null}

      {showModal ? (
        <ActionModal
          t={t}
          action={selectedAction}
          tenantId={tenantId}
          id={id}
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
          className="obps-index-wrapper-7"
        />
      )}
    </div>
  );
};

export default BpaApplicationDetail;