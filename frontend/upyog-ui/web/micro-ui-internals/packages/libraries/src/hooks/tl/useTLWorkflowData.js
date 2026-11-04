import { queryTemplate } from "../../common/queryTemplate";
import { WorkflowService } from "../../services/elements/WorkFlow";

const useTLWorkflowData = ({ tenantId, filters, config = {} }) => {
  return queryTemplate({
    queryKey: ["WORKFLOW_BY_GET_ALL_APPLICATION", tenantId, ...Object.keys(filters)?.map((e) => filters?.[e])],
    queryFn: () => WorkflowService.getAllApplication(tenantId, filters),
    config,
  });
};

export default useTLWorkflowData;
