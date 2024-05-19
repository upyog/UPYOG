import { Header, MultiLink } from "@upyog/digit-ui-react-components";
import _ from "lodash";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useParams } from "react-router-dom";
import ApplicationDetailsTemplate from "../../../../templates/ApplicationDetails";
import getAssetAcknowledgementData from "../../getAssetAcknowledgementData";


const ApplicationDetails = () => {
  const { t } = useTranslation();
  const { data: storeData } = Digit.Hooks.useStore.getInitData();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const { tenants } = storeData || {};
  const { id: applicationNo } = useParams();
  const [showToast, setShowToast] = useState(null);
  const [appDetailsToShow, setAppDetailsToShow] = useState({});
  const [showOptions, setShowOptions] = useState(false);
  const [enableAudit, setEnableAudit] = useState(false);
  const [businessService, setBusinessService] = useState("asset-create");


  

  const { isLoading, isError, data: applicationDetails, error } = Digit.Hooks.asset.useAssetApplicationDetail(t, tenantId, applicationNo);
  




  const {
    isLoading: updatingApplication,
    isError: updateApplicationError,
    data: updateResponse,
    error: updateError,
    mutate,
  } = Digit.Hooks.ptr.usePTRApplicationAction(tenantId);





  let workflowDetails = Digit.Hooks.useWorkflowDetails({
    tenantId: applicationDetails?.applicationData?.tenantId || tenantId,
    id: applicationDetails?.applicationData?.applicationData?.applicationNo,
    moduleCode: businessService,
    role: "PT_CEMP",
  });







  const { isLoading: auditDataLoading, isError: isAuditError, data: auditData } = Digit.Hooks.asset.useASSETSearch(
    {
      tenantId,
      filters: { applicationNo: applicationNo, audit: true },
    },
    // { enabled: enableAudit, select: (data) => data.PetRegistrationApplications?.filter((e) => e.status === "ACTIVE") }
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

    if (workflowDetails?.data?.applicationBusinessService && !(workflowDetails?.data?.applicationBusinessService === "ptr" && businessService === "ptr")) {
      setBusinessService(workflowDetails?.data?.applicationBusinessService);



    }
  }, [workflowDetails.data]);





  const handleDownloadPdf = async () => {
    const PetRegistrationApplications = appDetailsToShow?.applicationData;
    const tenantInfo = tenants.find((tenant) => tenant.code === PetRegistrationApplications.tenantId);
    const data = await getAssetAcknowledgementData(PetRegistrationApplications.applicationData, tenantInfo, t);
    Digit.Utils.pdf.generate(data);
  };

  const AssetDetailsPDF = {
    order: 1,
    label: t("ASSET_APPLICATION"),
    onClick: () => handleDownloadPdf(),
  };
  let dowloadOptions = [AssetDetailsPDF];



  return (
    <div>
      <div className={"employee-application-details"} style={{ marginBottom: "15px" }}>
        <Header styles={{ marginLeft: "0px", paddingTop: "10px", fontSize: "32px" }}>{t("ASSET_APPLICATION_DETAILS")}</Header>
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




      <ApplicationDetailsTemplate
        applicationDetails={appDetailsToShow}
        isLoading={isLoading}
        isDataLoading={isLoading}
        applicationData={appDetailsToShow?.applicationData}
        mutate={mutate}
        // workflowDetails={workflowDetails}
        businessService={businessService}
        moduleCode="ASSET"
        showToast={showToast}
        setShowToast={setShowToast}
        closeToast={closeToast}
        timelineStatusPrefix={"ASSET_COMMON_STATUS_"}
        forcedActionPrefix={"EMPLOYEE_PTR"}
        statusAttribute={"state"}
        MenuStyle={{ color: "#FFFFFF", fontSize: "18px" }}
      />

    </div>
  );
};

export default React.memo(ApplicationDetails);
