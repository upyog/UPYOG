import { Header, Loader, MultiLink } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { useParams } from "react-router-dom";
import ApplicationDetailsTemplate from "../../../../templates/ApplicationDetails";
import { downloadGCReceipt, downloadGCAcknowledgement } from "../../utils";

/**
 * ApplicationDetails Component
 * 
 * Renders the employee view of the GC application details page.
 * Fetches application data using `useGCSearch`, shapes it for the shared
 * `ApplicationDetailsTemplate`, and renders workflow actions for employee processing.
 * 
 * Extracts applicant details, address, garbage collection unit information from
 * the API response and formats them into section-based detail cards.
 */
const ApplicationDetails = () => {
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCurrentTenantId();

  const params = useParams();
  // Reconstruct application number from URL paramsing
  let reconstructedAppNo = params.applicationNo;
  if (params["*"]) {
    reconstructedAppNo = `${params.applicationNo}/${params["*"]}`;
  }
  const applicationNo = decodeURIComponent(reconstructedAppNo);



  const [showOptions, setShowOptions] = useState(false);
  const { data: storeData } = Digit.Hooks.useStore.getInitData();
  const { tenants } = storeData || {};

  const { data: reciept_data, isLoading: recieptDataLoading } = Digit.Hooks.useRecieptSearch(
    { tenantId, businessService: "garbage-service", consumerCodes: applicationNo, isEmployee: true },
    { enabled: !!applicationNo }
  );
  const { isLoading, data: applicationDetails } = Digit.Hooks.gc.useGCApplicationDetail(
    t, tenantId, applicationNo,
    { enabled: !!applicationNo, cacheTime: 0, staleTime: 0 }
  );

  const application = applicationDetails?.applicationData?.applicationData;
  const businessService = "garbage-service";

  let workflowDetails = Digit.Hooks.useWorkflowDetails({
    tenantId: application?.tenantId || tenantId,
    id: applicationNo, // Use the application number from the URL directly
    moduleCode: "garbage-service",
    config: { enabled: !!application, staleTime: 0 },
  });

  const {
    isLoading: updatingApplication,
    isError: updateApplicationError,
    data: updateResponse,
    error: updateError,
    mutate,
  } = Digit.Hooks.gc.useGCApplicationAction(tenantId);

  const [showToast, setShowToast] = useState(null);
  const closeToast = () => setShowToast(null);

  const getAcknowledgement = () => downloadGCAcknowledgement(application, tenants, t);

  const downloadOptions = [];
  downloadOptions.push({ label: t("GC_DOWNLOAD_ACKNOWLEDGEMENT"), onClick: getAcknowledgement });
  if (reciept_data?.Payments?.length > 0 && !recieptDataLoading) {
    downloadOptions.push({
      label: t("GC_FEE_RECEIPT"),
      onClick: () => downloadGCReceipt(reciept_data.Payments[0].tenantId, reciept_data.Payments),
    });
  }
  if (isLoading || workflowDetails?.isLoading) return <Loader />;;

  if (!application) {
    return (
      <div style={{ padding: "16px", textAlign: "center" }}>
        <h2>{t("CS_GC_APPLICATION_NOT_FOUND")}</h2>
      </div>
    );
  }

  const detailsArray = applicationDetails?.applicationData?.applicationDetails || [];
  const appNo = application?.grbgApplication?.applicationNo || application?.grbgApplicationNumber || t("CS_NA");

  return (
    <div>
      <div className={"employee-application-details"} style={{ marginBottom: "15px" }}>
        <Header styles={{ marginLeft: "0px", paddingTop: "10px", fontSize: "32px" }}>{t("GC_APPLICATION_DETAILS")}</Header>
        <div style={{ zIndex: "10", display: "flex", flexDirection: "row-reverse", alignItems: "center", marginTop: "-45px" }}>
          {downloadOptions && downloadOptions.length > 0 && (
            <MultiLink
              className="multilinkWrapper employee-mulitlink-main-div"
              onHeadClick={() => setShowOptions(!showOptions)}
              displayOptions={showOptions}
              options={downloadOptions}
              downloadBtnClassName={"employee-download-btn-className"}
              optionsClassName={"employee-options-btn-className"}
            />
          )}
        </div>
      </div>
      <ApplicationDetailsTemplate
        id={applicationNo}
        applicationDetails={{ applicationDetails: detailsArray, applicationData: application }}
        isLoading={isLoading}
        isDataLoading={isLoading}
        applicationData={application}
        mutate={mutate}
        workflowDetails={workflowDetails}
        businessService={businessService}
        moduleCode="garbage-service"
        showToast={showToast}
        setShowToast={setShowToast}
        closeToast={closeToast}
        timelineStatusPrefix={""}
        forcedActionPrefix={"WF_EMPLOYEE_GC"}
        statusAttribute={"status"}
        MenuStyle={{ color: "#FFFFFF", fontSize: "18px" }}
      />
    </div>
  );
};

export default ApplicationDetails;