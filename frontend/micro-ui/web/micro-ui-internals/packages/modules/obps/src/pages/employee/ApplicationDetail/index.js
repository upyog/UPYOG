import React, { Fragment, useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { Header, CardSectionHeader, PDFSvg, StatusTable, Row, MultiLink, LinkButton } from "@upyog/digit-ui-react-components";
import ApplicationDetailsTemplate from "../../../../../templates/ApplicationDetails";
import { downloadAndPrintReciept } from "../../../utils";

const ApplicationDetail = () => {
  const { id } = useParams();
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const state = tenantId?.split('.')[0]
  const [showToast, setShowToast] = useState(null);
  const [showOptions, setShowOptions] = useState(false);
  const { isLoading, data: applicationDetails } = Digit.Hooks.obps.useLicenseDetails(state, { applicationNumber: id, tenantId: state }, {});
  const isMobile = window.Digit.Utils.browser.isMobile();
  const [viewTimeline, setViewTimeline]=useState(false);
  const {
    isLoading: updatingApplication,
    isError: updateApplicationError,
    data: updateResponse,
    error: updateError,
    mutate,
  } = Digit.Hooks.obps.useBPAREGApplicationActions(tenantId);

  const workflowDetails = Digit.Hooks.useWorkflowDetails({
    tenantId: tenantId?.split('.')[0],
    id: id,
    moduleCode: "BPAREG",
  });

  const closeToast = () => {
    setShowToast(null);
  };
  
  const handleViewTimeline=()=>{
    setViewTimeline(true);
      const timelineSection=document.getElementById('timeline');
      if(timelineSection){
        timelineSection.scrollIntoView({behavior: 'smooth'});
      } 
  };
  let dowloadOptions = [];
  console.log("applicationDetails",applicationDetails)
  if (applicationDetails?.payments?.length > 0) {
    dowloadOptions.push({
      label: t("TL_RECEIPT"),
      onClick: () => downloadAndPrintReciept(applicationDetails?.payments?.[0]?.paymentDetails?.[0]?.businessService || "BPAREG", applicationDetails?.applicationData?.applicationNumber, applicationDetails?.applicationData?.tenantId,applicationDetails?.payments),
    })
  }

  return (
    <div className={"employee-main-application-details"}>
        <div  className={"employee-application-details"}>
        <Header>{t("CS_TITLE_APPLICATION_DETAILS")}</Header>
        {workflowDetails?.data?.timeline?.length>0 && (
        <div style={{color:"#A52A2A"}}>
        <LinkButton label={t("VIEW_TIMELINE")} onClick={handleViewTimeline}></LinkButton>
        </div>
        )}
        {applicationDetails?.payments?.length > 0 && 
        <MultiLink
          className="multilinkWrapper employee-mulitlink-main-div"
          onHeadClick={() => setShowOptions(!showOptions)}
          displayOptions={showOptions}
          options={dowloadOptions}
          downloadBtnClassName={"employee-download-btn-className"}
          optionsClassName={"employee-options-btn-className"}
        />}
        </div>
      <ApplicationDetailsTemplate
        applicationDetails={applicationDetails}
        isLoading={isLoading}
        id={"timeline"}
        isDataLoading={isLoading}
        applicationData={applicationDetails?.applicationData}
        mutate={mutate}
        workflowDetails={workflowDetails}
        businessService={workflowDetails?.data?.applicationBusinessService ? workflowDetails?.data?.applicationBusinessService : applicationDetails?.applicationData?.businessService}
        moduleCode="BPAREG"
        showToast={showToast}
        setShowToast={setShowToast}
        ActionBarStyle={isMobile?{}:{paddingRight:"50px"}}
        MenuStyle={isMobile?{}:{right:"50px"}}
        closeToast={closeToast}
        timelineStatusPrefix={"WF_NEWTL_"}
      />
    </div>
  )
}

export default ApplicationDetail;