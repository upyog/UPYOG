import { queryTemplate } from "../../common/queryTemplate";
import { useQueryClient } from "@tanstack/react-query";
import { FSMService } from "../../services/elements/FSM";

const useApplicationAudit = (tenantId, filters) => {
  const client = useQueryClient();

  const queryKey = [
    "FSM_APPLICATION_AUDIT",
    tenantId,
    JSON.stringify(filters),
  ];

  const queryFn = () => FSMService.audit(tenantId, filters);

  const query = queryTemplate({
    queryKey,
    queryFn,
  });

  return {
    ...query,
    revalidate: () => client.invalidateQueries({ queryKey }),
  };
};

export default useApplicationAudit;