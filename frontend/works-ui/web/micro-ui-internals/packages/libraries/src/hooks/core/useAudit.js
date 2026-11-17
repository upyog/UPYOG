import { useQueryClient } from "@tanstack/react-query";
import { queryTemplate } from "../../common/queryTemplate";

/**
 * Fetch audit logs.
 */
const useAudit = ({ tenantId, filters }, config = {}) => {
  const client = useQueryClient();

  const args = tenantId
    ? { tenantId, filters }
    : { filters };

  const queryKey = [
    "AUDIT_LIST",
    tenantId,
    JSON.stringify(filters),
  ];

  const queryFn = () =>
    Digit.AuditService.audit_log(args);

  const query = queryTemplate({
    queryKey,
    queryFn,
    config,
  });

  return {
    ...query,
    revalidate: () =>
      client.invalidateQueries({ queryKey }),
  };
};

export default useAudit;