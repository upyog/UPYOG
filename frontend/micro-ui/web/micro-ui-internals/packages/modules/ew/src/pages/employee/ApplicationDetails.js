// Importing necessary components, hooks, and utilities from external libraries and local files
import { Header, MultiLink } from "@nudmcdgnpm/digit-ui-react-components"; // UI components for header and multi-link
import _ from "lodash"; // Utility library for deep cloning
import React, { useEffect, useState } from "react"; // React hooks for state and lifecycle management
import { useTranslation } from "react-i18next"; // Hook for translations
import { useParams } from "react-router-dom"; // Hook to access route parameters
import ApplicationDetailsTemplate from "../../../../templates/ApplicationDetails"; // Template for displaying application details
import getEwAcknowledgementData from "../../utils/getEwAcknowledgementData"; // Utility function to fetch acknowledgment data

// Main component for displaying the details of an E-Waste application
const EWApplicationDetails = () => {
  const { t } = useTranslation(); // Translation hook
  const { data: storeData } = Digit.Hooks.useStore.getInitData(); // Fetching initial store data
  const tenantId = Digit.ULBService.getCurrentTenantId(); // Fetching the current tenant ID
  const { tenants } = storeData || {}; // Extracting tenants from store data
  const { id: requestId } = useParams(); // Extracting the request ID from route parameters
  const [showToast, setShowToast] = useState(null); // State to manage toast notifications
  const [appDetailsToShow, setAppDetailsToShow] = useState({}); // State to store application details
  const [showOptions, setShowOptions] = useState(false); // State to toggle download options

  let businessService = "ewst"; // Business service type for E-Waste

  // Flag to enable or disable the action bar based on the URL
  let isAction = false;
  if (window.location.href.includes("applicationsearch")) {
    isAction = true;
  }

  // Fetching application details using a custom hook
  const { isLoading, isError, data: applicationDetails, error } = Digit.Hooks.ew.useEwApplicationDetail(t, tenantId, requestId);

  // Hook to handle application actions (e.g., updates)
  const {
    isLoading: updatingApplication,
    isError: updateApplicationError,
    data: updateResponse,
    error: updateError,
    mutate,
  } = Digit.Hooks.ew.useEWApplicationAction(tenantId);

  // Fetching workflow details for the application
  let workflowDetails = Digit.Hooks.useWorkflowDetails({
    tenantId: applicationDetails?.applicationData?.tenantId || tenantId,
    id: applicationDetails?.applicationData?.applicationData?.requestId,
    moduleCode: businessService,
    role: "EW_VENDOR",
  });

  // Function to close the toast notification
  const closeToast = () => {
    setShowToast(null);
  };

  // Effect to update the application details state when new data is fetched
  useEffect(() => {
    if (applicationDetails) {
      setAppDetailsToShow(_.cloneDeep(applicationDetails));
    }
  }, [applicationDetails]);

  // Function to handle the download of the acknowledgment PDF
  const handleDownloadPdf = async () => {
    const EwasteApplication = appDetailsToShow?.applicationData; // Extracting application data
    tenants.find((tenant) => tenant.code === EwasteApplication.tenantId); // Finding tenant info
    const tenantInfo = tenantId; // Setting tenant info
    const data = await getEwAcknowledgementData(EwasteApplication.applicationData, tenantInfo, t); // Fetching acknowledgment data
    Digit.Utils.pdf.generateTable(data); // Generating the PDF
  };

  // Array to store download options
  let downloadOptions = [];

  // Function to generate and print the E-Waste service certificate
  const printCertificate = async () => {
    let response = await Digit.PaymentService.generatePdf(tenantId, { EwasteApplication: [applicationDetails?.applicationData?.applicationData] }, "ewasteservicecertificate");
    const fileStore = await Digit.PaymentService.printReciept(tenantId, { fileStoreIds: response.filestoreIds[0] });
    window.open(fileStore[response?.filestoreIds[0]], "_blank");
  };

  // Adding acknowledgment form download option
  downloadOptions.push({
    label: t("EWASTE_DOWNLOAD_ACK_FORM"),
    onClick: () => handleDownloadPdf(),
  });

  // Adding certificate download option if the request status is completed
  if (appDetailsToShow?.applicationData?.applicationData?.requestStatus === "REQUESTCOMPLETED") {
    downloadOptions.push({
      label: t("EW_CERTIFICATE"),
      onClick: () => printCertificate(),
    });
  }

  return (
    <div>
      <div className={"employee-application-details"} style={{ marginBottom: "15px" }}>
        {/* Header for the application details page */}
        <Header styles={{ marginLeft: "0px", paddingTop: "10px", fontSize: "32px" }}>{t("EW_APPLICATION_DETAILS")}</Header>
        <div style={{ zIndex: "10", display: "flex", flexDirection: "row-reverse", alignItems: "center", marginTop: "-25px" }}>
          <div style={{ zIndex: "10", position: "relative" }}>
            {/* Rendering download options */}
            {downloadOptions && downloadOptions.length > 0 && (
              <MultiLink
                className="multilinkWrapper"
                onHeadClick={() => setShowOptions(!showOptions)}
                displayOptions={showOptions}
                options={downloadOptions}
                downloadBtnClassName={"employee-download-btn-className"}
                optionsClassName={"employee-options-btn-className"}
              />
            )}
          </div>
        </div>
      </div>

      {/* Rendering the application details template */}
      <ApplicationDetailsTemplate
        isAction={isAction}
        applicationDetails={appDetailsToShow?.applicationData}
        isLoading={isLoading}
        isDataLoading={isLoading}
        applicationData={appDetailsToShow?.applicationData?.applicationData}
        mutate={mutate}
        workflowDetails={workflowDetails}
        businessService={businessService}
        moduleCode="ewaste-services"
        showToast={showToast}
        setShowToast={setShowToast}
        closeToast={closeToast}
        timelineStatusPrefix={"EW_COMMON_STATUS_"}
        forcedActionPrefix={"EMPLOYEE_EW"}
        statusAttribute={"state"}
        MenuStyle={{ color: "#FFFFFF", fontSize: "18px" }}
      />
    </div>
  );
};

export default React.memo(EWApplicationDetails); // Exporting the component with React.memo for performance optimization