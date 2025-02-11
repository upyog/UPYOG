import { useQuery } from "react-query";
import { tqmService } from "./services/tqmService";

export const useSearchTest = ({ id, tenantId, config = {} }) => {
  return useQuery(["TQM_TEST"], () => tqmService.searchTest({ id, tenantId }), config);
};
