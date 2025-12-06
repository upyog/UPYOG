import { Header, MultiLink } from "@upyog/digit-ui-react-components";
import _ from "lodash";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useParams } from "react-router-dom";
import ApplicationDetailsTemplate from "../../../../templates/ApplicationDetails";
import getEwAcknowledgementData from "../../utils/getEwAcknowledgementData";

/**
 * Employee interface for viewing and managing E-Waste application details.
 * Provides functionality for viewing application status, downloading documents,
 * and processing workflow actions.
 * 
 * @component
 * @returns {JSX.Element} Application details interface for employees
 */
const EWApplicationDetails = () => {
  const { t } = useTranslation();
  const { data: storeData } = Digit.Hooks.useStore.getInitData();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const { tenants } = storeData || {};
  const { id: requestId } = useParams();
  const [showToast, setShowToast] = useState(null);
  const [appDetailsToShow, setAppDetailsToShow] = useState({});
  const [showOptions, setShowOptions] = useState(false);

  const businessService = "ewst";
  const isAction = window.location.href.includes("applicationsearch");

  /**
   * Custom hooks for fetching and managing application data
   */
  const { isLoading, isError, data: applicationDetails, error } = Digit.Hooks.ew.useEwApplicationDetail(t, tenantId, requestId);

  const {
    isLoading: updatingApplication,
    isError: updateApplicationError,
    data: updateResponse,
    error: updateError,
    mutate,
  } = Digit.Hooks.ew.useEWApplicationAction(tenantId);

  const workflowDetails = Digit.Hooks.useWorkflowDetails({
    tenantId: applicationDetails?.applicationData?.tenantId || tenantId,
    id: applicationDetails?.applicationData?.applicationData?.requestId,
    moduleCode: businessService,
    role: "EW_VENDOR",
  });

  /**
   * Updates application details when new data is received
   */
  useEffect(() => {
    if (applicationDetails) {
      setAppDetailsToShow(_.cloneDeep(applicationDetails));
    }
  }, [applicationDetails]);

  /**
   * Generates and downloads application acknowledgment PDF
   * @async
   */
  const handleDownloadPdf = async () => {
    const EwasteApplication = appDetailsToShow?.applicationData;
    tenants.find((tenant) => tenant.code === EwasteApplication.tenantId);
    const tenantInfo = tenantId;
    const data = await getEwAcknowledgementData(EwasteApplication.applicationData, tenantInfo, t);
    Digit.Utils.pdf.generateTable(data);
  };

  /**
   * Generates and opens E-Waste service certificate in new window
   * @async
   */
  const printCertificate = async () => {
    let response = await Digit.PaymentService.generatePdf(
      tenantId, 
      { EwasteApplication: [applicationDetails?.applicationData?.applicationData] }, 
      "ewasteservicecertificate"
    );
    const fileStore = await Digit.PaymentService.printReciept(tenantId, { fileStoreIds: response.filestoreIds[0] });
    window.open(fileStore[response?.filestoreIds[0]], "_blank");
  };

  /**
   * Available document download options based on application status
   */
  let downloadOptions = [{
    label: t("EWASTE_DOWNLOAD_ACK_FORM"),
    onClick: () => handleDownloadPdf(),
  }];

  if (appDetailsToShow?.applicationData?.applicationData?.requestStatus === "REQUESTCOMPLETED") {
    downloadOptions.push({
      label: t("EW_CERTIFICATE"),
      onClick: () => printCertificate(),
    });
  }

  const closeToast = () => setShowToast(null);

  return (
    <div>
      <div className={"employee-application-details"} style={{ marginBottom: "15px" }}>
        <Header styles={{ marginLeft: "0px", paddingTop: "10px", fontSize: "32px" }}>
          {t("EW_APPLICATION_DETAILS")}
        </Header>
        <div style={{ zIndex: "10", display: "flex", flexDirection: "row-reverse", alignItems: "center", marginTop: "-25px" }}>
          <div style={{ zIndex: "10", position: "relative" }}>
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

export default React.memo(EWApplicationDetails);