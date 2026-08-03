import { GCServices } from "../../elements/GC";
import { Request } from "../../atoms/Utils/Request";

const ApplicationUpdateActionGC = async (applicationData, tenantId) => {
  try {
    const account = applicationData?.garbageAccounts?.[0];
    const workflow = account?.workflow;

    if (account?.isOnlyWorkflowCall && workflow?.action) {
      return await Request({
        url: "/egov-workflow-v2/egov-wf/process/_transition",
        data: {
          ProcessInstances: [
            {
              tenantId,
              businessService: workflow.businessService || "garbage-service",
              businessId: account?.grbgApplicationNumber || account?.applicationNumber,
              action: workflow.action,
              comment: workflow.comment || workflow.comments || "",
              moduleName: workflow.moduleName || "garbage-service",
              ...(workflow.documents?.length ? { documents: workflow.documents } : {}),
            },
          ],
        },
        useCache: false,
        method: "POST",
        auth: true,
        userService: true,
      });
    }

    return await GCServices.update(applicationData, tenantId);
  } catch (error) {
    console.error("[GC] update error:", error);
    throw new Error(error?.response?.data?.Errors?.[0]?.message || error?.message);
  }
};

export default ApplicationUpdateActionGC;
