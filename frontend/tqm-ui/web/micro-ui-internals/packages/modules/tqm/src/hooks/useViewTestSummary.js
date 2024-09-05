import { useQuery } from "react-query";
import { tqmService } from "./services/tqmService";

export const useViewTestSummary = ({ tenantId, t, id, config = {} }) => {
  return useQuery(["TQM_ADMIN_TEST_RESULTS_SUMMARY", id], () => tqmService.viewTestSummary({ tenantId, t, id }), config);
};
