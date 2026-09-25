import { queryTemplate } from "../../common/queryTemplate";
import { FSMService } from "../../services/elements/FSM";

const useWorkerSearch = ({ tenantId, params, details, config = {} }) => {
  const queryKey = [
    "FSM_WORKER_SEARCH",
    tenantId,
    JSON.stringify(details),
  ];

  return queryTemplate({
    queryKey,
    queryFn: () =>
      FSMService.workerSearch({ tenantId, params, details }),
    config,
  });
};

export default useWorkerSearch;