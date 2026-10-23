import { queryTemplate } from "../../common/queryTemplate";
import { useQueryClient } from "@tanstack/react-query";
import { FSMService } from "../../services/elements/FSM";

const useSearchForAuditData = (tenantId, filters, options = {}) => {
  const client = useQueryClient();

  const queryKey = [
    "FSM_APPLICATION_AUDIT",
    tenantId,
    JSON.stringify(filters),
  ];

  const query = queryTemplate({
    queryKey,
    queryFn: () => FSMService.search(tenantId, filters),
    config: options,
  });

  return {
    ...query,
    revalidate: () => client.invalidateQueries({ queryKey }),
  };
};

export default useSearchForAuditData;