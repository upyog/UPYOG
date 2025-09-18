    import { Header, MultiLink } from "@upyog/digit-ui-react-components";
    import _ from "lodash";
    import React, { useEffect, useState } from "react";
    import { useTranslation } from "react-i18next";
    import { useParams } from "react-router-dom";
    import ApplicationDetailsTemplate from "../../../../templates/ApplicationDetails";

/*
    The ApplicationDetails component fetches and displays details of an application 
    (either Water Tanker or Mobile Toilet) based on a booking number from the URL parameter. 
    It includes functionality for displaying download options like receipt and permission letter, 
    managing workflow details, and handling PDF generation for receipts/letters. 
    The component integrates with hooks for data fetching and mutation, 
    and provides a UI for interacting with the application details.
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
      const [BusinessService, setBusinessService] = useState("watertanker"); // Default to water tanker service   
  // Determine business service dynamically
  const isWaterTanker = bookingNo?.startsWith("WT"); // Water Tanker
  const isTreePruning = bookingNo?.startsWith("TP"); // Tree Pruning
  const businessService = isWaterTanker ? "watertanker" : (isTreePruning ? "treePruning" : "mobileToilet");
  const user = Digit.UserService.getUser().info;

  // Store the booking number in session storage with key based on service type
  const storageKey = isWaterTanker ? "wt" : (isTreePruning ? "tp" : "mt");
  sessionStorage.setItem(storageKey, bookingNo);

      // Fetch application details based on service type
       const { isLoading, isError, data: applicationDetails, error } = isWaterTanker
        ? Digit.Hooks.wt.useWTApplicationDetail(t, tenantId, bookingNo)
        : (isTreePruning 
           ? Digit.Hooks.wt.useTPApplicationDetail(t, tenantId, bookingNo)
           : Digit.Hooks.wt.useMTApplicationDetail(t, tenantId, bookingNo));

       // Fetch application action hooks based on service type
      const {
        isLoading: updatingApplication,
        isError: updateApplicationError,
        data: updateResponse,
        error: updateError,
        mutate,
      } = isWaterTanker
          ? Digit.Hooks.wt.useWTApplicationAction(tenantId)
          : (isTreePruning
             ? Digit.Hooks.wt.useTPApplicationAction(tenantId)
             : Digit.Hooks.wt.useMTApplicationAction(tenantId));

      // Fetch workflow details for the application
      let workflowDetails = Digit.Hooks.useWorkflowDetails({
        tenantId: applicationDetails?.applicationData?.tenantId || tenantId,
        id: applicationDetails?.applicationData?.applicationData?.bookingNo,
        moduleCode: businessService,
       role: isWaterTanker ? ["WT_CEMP"] : (isTreePruning ? ["TP_CEMP"] : ["MT_CEMP"]),
      });


      const closeToast = () => {
        setShowToast(null);
      };

      useEffect(() => {
        if (applicationDetails) {
          setAppDetailsToShow(_.cloneDeep(applicationDetails));
        
        }
      }, [applicationDetails]);



      useEffect(() => {
        if (
          workflowDetails?.data?.applicationBusinessService &&
          !(
            (workflowDetails?.data?.applicationBusinessService === "watertanker" && businessService === "watertanker") ||
            (workflowDetails?.data?.applicationBusinessService === "mobileToilet" && businessService === "mobileToilet") ||
            (workflowDetails?.data?.applicationBusinessService === "treePruning" && businessService === "treePruning")
          )
        ) {
          setBusinessService(workflowDetails?.data?.applicationBusinessService);
        }
      }, [workflowDetails.data]);
      

      let dowloadOptions = [];
      return (
        <div style={{ padding: user?.type === "CITIZEN" ? "0 15px" : ""}}>
        <div className={"employee-application-details"} style={{ marginBottom: "15px"  }}>
          <Header styles={{ marginLeft: "0px", paddingTop: "10px", fontSize: "32px" }}>{t("BOOKING_DETAILS")}</Header>
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
            forcedActionPrefix={"RS"}
            statusAttribute={"state"}
            MenuStyle={{ color: "#FFFFFF", fontSize: "18px" }}
          />
          

        </div>
      );
    };

    export default React.memo(ApplicationDetails);
