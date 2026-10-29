import { queryTemplate } from "../../common/queryTemplate";
import { WorkflowService } from "../../services/elements/WorkFlow";

const useWorkflowData = (tenantId, businessIds) => {
  const queryKey = [
    "FSM_WORKFLOW_BY_ID",
    tenantId,
    JSON.stringify(businessIds),
  ];

  return queryTemplate({
    queryKey,
    queryFn: () =>
      WorkflowService.getByBusinessId(tenantId, businessIds),
    config: { staleTime: Infinity },
  });
};

export default useWorkflowData;