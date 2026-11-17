import { queryTemplate } from "../../common/queryTemplate";
import { FSMService } from "../../services/elements/FSM";

const useDriverSearch = ({ tenantId, filters, config = {} }) => {
  const queryKey = [
    "FSM_DRIVER_SEARCH",
    tenantId,
    JSON.stringify(filters),
  ];

  const queryFn = () =>
    FSMService.driverSearch(tenantId, filters);

  return queryTemplate({
    queryKey,
    queryFn,
    config,
  });
};

export default useDriverSearch;