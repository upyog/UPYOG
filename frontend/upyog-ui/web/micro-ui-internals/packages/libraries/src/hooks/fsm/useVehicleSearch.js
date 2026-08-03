import { queryTemplate } from "../../common/queryTemplate";
import { Search } from "../../services/molecules/FSM/Search";

const useVehicleSearch = ({ tenantId, filters, config = {}, options }) => {
  const queryKey = [
    "FSM_VEHICLE_SEARCH",
    tenantId,
    JSON.stringify(filters),
    options?.searchWithDSO,
  ];

  const queryFn = () =>
    options?.searchWithDSO
      ? Search.allVehiclesWithDSO(tenantId, filters)
      : Search.allVehicles(tenantId, filters);

  return queryTemplate({
    queryKey,
    queryFn,
    config,
  });
};

export default useVehicleSearch;