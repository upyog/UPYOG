import { Header, Loader } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { useParams } from "react-router-dom";
import ApplicationDetailsTemplate from "../../../../templates/ApplicationDetails";

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



  const [showToast, setShowToast] = useState(null);
  const user = Digit.UserService.getUser().info;

  const searchCriteria = {
    searchCriteriaGarbageAccount: {
      applicationNumber: [applicationNo],
    },
  };
  const { isLoading, data: gcData } = Digit.Hooks.gc.useGCSearch(
    { tenantId, data: searchCriteria, filters: { applicationNumber: [applicationNo] } },
    { enabled: !!applicationNo, cacheTime: 0, staleTime: 0 }
  );

  const application = gcData?.garbageAccounts?.[0] || gcData?.GarbageApplications?.[0] || gcData?.data?.[0];
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

  const closeToast = () => setShowToast(null);
  const clearDataDetails = () => setTimeout(() => window.location.reload(), 5000);

  if (isLoading || workflowDetails?.isLoading) return <Loader />;;

  if (!application) {
    return (
      <div style={{ padding: "16px", textAlign: "center" }}>
        <h2>{t("CS_GC_APPLICATION_NOT_FOUND")}</h2>
      </div>
    );
  }

  // --- Data shaping ---
  const appData = application;

  const appNo = appData?.grbgApplication?.applicationNo || appData?.grbgApplicationNumber || t("CS_NA");
  const appStatus = appData?.grbgApplication?.status || appData?.status || t("CS_NA");

  const applicant = appData?.additionalDetail?.applicantDetails?.[0] || {};
  const ownerNames = applicant?.name || applicant?.applicantName || appData?.name || t("CS_NA");
  const mobileNumbers = applicant?.mobileNumber || appData?.mobileNumber || t("CS_NA");
  const altMobileNumbers = applicant?.alternateNumber || t("CS_NA");
  const emails = applicant?.emailId || t("CS_NA");

  const address = appData?.addresses?.[0] || {};
  const addressAdditional = address?.additionalDetail || {};
  const propertyId = appData?.propertyId || t("CS_NA");
  const pincode = address?.pincode || t("CS_NA");
  const city = address?.city || t("CS_NA");
  const locality = addressAdditional?.locality || t("CS_NA");
  const street = addressAdditional?.streetName || t("CS_NA");
  const houseNo = addressAdditional?.houseNo || t("CS_NA");
  const buildingName = addressAdditional?.houseName || t("CS_NA");
  const landmark = addressAdditional?.landmark || t("CS_NA");

  const collectionUnit = appData?.grbgCollectionUnits?.[0] || {};
  const typeOfCollection = collectionUnit?.unitType || t("CS_NA");
  const category = collectionUnit?.category || t("CS_NA");
  const subCategory = collectionUnit?.subCategory || t("CS_NA");
  const subCategoryType = collectionUnit?.subCategoryType || t("CS_NA");
  const noOfUnits = collectionUnit?.no_of_units ?? t("CS_NA");

  const detailsArray = [
    {
      title: "GC_APPLICATION_SUMMARY",
      asSectionHeader: true,
      values: [
        { title: "GC_APPLICATION_NUMBER_LABEL", value: appNo },
        { title: "GC_APPLICATION_STATUS_LABEL", value: appStatus ? t(`GC_STATUS_${appStatus}`) : t("CS_NA") },
      ],
    },
    {
      title: "ES_APPLICANT_DETAILS",
      asSectionHeader: true,
      values: [
        { title: "GC_APPLICANT_NAME", value: ownerNames },
        { title: "GC_MOBILE_NUMBER", value: mobileNumbers },
        { title: "GC_ALT_MOBILE_NUMBER", value: altMobileNumbers },
        { title: "GC_EMAIL_ID", value: emails },
      ],
    },
    {
      title: "GC_PROPERTY_LOCATION_DETAILS",
      asSectionHeader: true,
      values: [
        { title: "GC_PROPERTY_ID", value: propertyId },
        { title: "GC_PINCODE", value: pincode },
        { title: "GC_CITY", value: city },
        { title: "GC_LOCALITY", value: locality },
        { title: "GC_STREET_NAME", value: street },
        { title: "GC_HOUSE_NO", value: houseNo },
        { title: "GC_BUILDING_NAME", value: buildingName },
        { title: "GC_LANDMARK", value: landmark },
      ],
    },
    {
      title: "GC_GARBAGE_SPECIFICATIONS",
      asSectionHeader: true,
      values: [
        { title: "GC_TYPE_OF_COLLECTION", value: typeOfCollection },
        { title: "GC_CATEGORY", value: category },
        { title: "GC_SUB_CATEGORY", value: subCategory },
        { title: "GC_SUB_CATEGORY_TYPE", value: subCategoryType },
        { title: "GC_NO_OF_UNITS", value: String(noOfUnits) },
      ],
    },
  ];

  return (
    <div style={{ padding: user?.type === "CITIZEN" ? "0 15px" : "" }}>
      <div className={"employee-application-details"} style={{ marginBottom: "15px" }}>
        <Header styles={{ marginLeft: "0px", paddingTop: "10px", fontSize: "32px" }}>
          {t("GC_APPLICATION_DETAILS")}
        </Header>
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
        clearDataDetails={clearDataDetails}
        timelineStatusPrefix={""}
        forcedActionPrefix={"WF_EMPLOYEE_GC"}
        statusAttribute={"status"}
        MenuStyle={{ color: "#FFFFFF", fontSize: "18px" }}
      />
    </div>
  );
};

export default ApplicationDetails;