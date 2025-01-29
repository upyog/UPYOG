    import { Header, MultiLink } from "@nudmcdgnpm/digit-ui-react-components";
    import _ from "lodash";
    import React, { useEffect, useState } from "react";
    import { useTranslation } from "react-i18next";
    import { useParams } from "react-router-dom";
    import ApplicationDetailsTemplate from "../../../../templates/ApplicationDetails";

/*
    The ApplicationDetails component fetches and displays details of a water Tanker application 
    based on a booking number from the URL parameter. It includes functionality for displaying 
    download options like receipt and permission letter, managing workflow details, and handling 
    PDF generation for receipts/letters. The component integrates with hooks for data fetching 
    and mutation, and provides a UI for interacting with the application details.
  */

    const ApplicationDetails = () => {
      const { t } = useTranslation();
      const { data: storeData } = Digit.Hooks.useStore.getInitData();
      const tenantId = Digit.ULBService.getCurrentTenantId();
      const { tenants } = storeData || {};
      const { id: bookingNo } = useParams();
      const [showToast, setShowToast] = useState(null);
      const [appDetailsToShow, setAppDetailsToShow] = useState({});
      const [showOptions, setShowOptions] = useState(false);
      const [businessService, setBusinessService] = useState("watertanker");
    
      sessionStorage.setItem("wt", bookingNo);
      const { isLoading, isError, data: applicationDetails, error } = Digit.Hooks.wt.useWTApplicationDetail(t, tenantId, bookingNo);
      
      const {
        isLoading: updatingApplication,
        isError: updateApplicationError,
        data: updateResponse,
        error: updateError,
        mutate,
      } = Digit.Hooks.wt.useWTApplicationAction(tenantId);
      let workflowDetails = Digit.Hooks.useWorkflowDetails({
        tenantId: applicationDetails?.applicationData?.tenantId || tenantId,
        id: applicationDetails?.applicationData?.applicationData?.bookingNo,
        moduleCode: businessService,
        role: ["WT_CEMP"],
      });

      const { isLoading: auditDataLoading, isError: isAuditError, data,refetch} = Digit.Hooks.wt.useTankerSearchAPI(
        {
          tenantId,
          filters: { bookingNo: bookingNo, audit: true },
        },
      );

      const closeToast = () => {
        setShowToast(null);
      };

      useEffect(() => {
        if (applicationDetails) {
          setAppDetailsToShow(_.cloneDeep(applicationDetails));
        
        }
      }, [applicationDetails]);



      useEffect(() => {

        if (workflowDetails?.data?.applicationBusinessService && !(workflowDetails?.data?.applicationBusinessService === "watertanker" && businessService === "watertanker")) {
          setBusinessService(workflowDetails?.data?.applicationBusinessService);
        }
      }, [workflowDetails.data]);
      

      let dowloadOptions = [];
      return (
        <div>
        <div className={"employee-application-details"} style={{ marginBottom: "15px" }}>
          <Header styles={{ marginLeft: "0px", paddingTop: "10px", fontSize: "32px" }}>{t("WT_BOOKING_DETAILS")}</Header>
          <div style={{zIndex: "10",display:"flex",flexDirection:"row-reverse",alignItems:"center",marginTop:"-25px"}}>
            {dowloadOptions && dowloadOptions.length > 0 && (
              <MultiLink
                className="multilinkWrapper employee-mulitlink-main-div"
                onHeadClick={() => setShowOptions(!showOptions)}
                displayOptions={showOptions}
                options={dowloadOptions}
                downloadBtnClassName={"employee-download-btn-className"}
                optionsClassName={"employee-options-btn-className"}
              // ref={menuRef}
              />
            )}
          </div>      
          </div>
          <ApplicationDetailsTemplate
            applicationDetails={appDetailsToShow?.applicationData}
            isLoading={isLoading}
            isDataLoading={isLoading}
            applicationData={appDetailsToShow?.applicationData?.applicationData}
            mutate={mutate}
            workflowDetails={workflowDetails}
            businessService={businessService}
            moduleCode="request-service"
            showToast={showToast}
            setShowToast={setShowToast}
            closeToast={closeToast}
            timelineStatusPrefix={""}
            forcedActionPrefix={"WT"}
            statusAttribute={"state"}
            MenuStyle={{ color: "#FFFFFF", fontSize: "18px" }}
          />
          

        </div>
      );
    };

    export default React.memo(ApplicationDetails);
