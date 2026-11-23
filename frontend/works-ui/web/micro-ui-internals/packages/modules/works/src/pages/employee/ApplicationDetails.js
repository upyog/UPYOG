import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { useParams } from "react-router-dom";
import { Header, BackButton } from "@nudmcdgnpm/digit-ui-react-components";
import ApplicationDetailsTemplate from "../../../../templates/ApplicationDetails";
import { useActionPlanDetails } from "../../api/useActionPlanDetails";
import { useUpdateActionPlan } from "../../api/useUpdateActionPlan";

const ApplicationDetails = ({ parentRoute = "/works-ui/employee/works" }) => {
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const { id, applicationNumber: appNoParam } = useParams();
  const applicationNumber = id || appNoParam;

  const [showToast, setShowToast] = useState(null);

  const {
    isLoading,
    isError,
    data: applicationDetails,
    revalidate: revalidateDetails,
  } = useActionPlanDetails({
    tenantId,
    applicationNumber,
  });

  const businessService =
    applicationDetails?.applicationData?.workflow?.businessService ||
    applicationDetails?.applicationData?.businessService ||
    "WMS.ACTION_PLAN";

  const workflowDetails = Digit.Hooks.useWorkflowDetails({
    tenantId: applicationDetails?.tenantId || tenantId,
    id: applicationNumber,
    moduleCode: businessService,
    role: "WORKS_EMPLOYEE",
    config: {
      enabled: Boolean(applicationNumber),
    },
  });

  const { mutate, isLoading: isUpdating } = useUpdateActionPlan(
    applicationDetails?.tenantId || tenantId
  );

  const closeToast = () => {
    setShowToast(null);
  };

  useEffect(() => {
    if (showToast?.key === "success") {
      revalidateDetails();
    }
  }, [showToast]);

  const resolvedBusinessService =
    workflowDetails?.data?.processInstances?.[0]?.businessService ||
    businessService;

  return (
    <div style={{ padding: "16px 24px 80px" }}>
      <div style={{ marginBottom: "12px" }}>
        <BackButton>{t("CS_COMMON_BACK", "Back")}</BackButton>
      </div>

      <Header styles={{ fontSize: "28px", marginBottom: "16px" }}>
        {t("WORKS_AP_APPLICATION_DETAILS", "Action Plan Details")}
      </Header>

      {isError ? (
        <div
          style={{
            padding: "24px",
            background: "#fff",
            borderRadius: "8px",
            border: "1px solid #fecaca",
            color: "#b91c1c",
          }}
        >
          {t("WORKS_AP_DETAILS_LOAD_ERROR", "Failed to load Action Plan details.")}
        </div>
      ) : (
        <ApplicationDetailsTemplate
          applicationDetails={applicationDetails}
          isLoading={isLoading || isUpdating}
          isDataLoading={isLoading}
          applicationData={applicationDetails?.applicationData}
          mutate={mutate}
          workflowDetails={workflowDetails}
          businessService={resolvedBusinessService}
          moduleCode="WORKS"
          showToast={showToast}
          setShowToast={setShowToast}
          closeToast={closeToast}
          timelineStatusPrefix="WF_WORKS_"
          forcedActionPrefix="WF_WORKS"
          statusAttribute="state"
          id={applicationNumber}
          showTimeLine={true}
        />
      )}
    </div>
  );
};

export default ApplicationDetails;
