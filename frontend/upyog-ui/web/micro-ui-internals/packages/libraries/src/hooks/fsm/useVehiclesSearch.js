import { queryTemplate } from "../../common/queryTemplate";
import { FSMService } from "../../services/elements/FSM";

const useVehiclesSearch = ({ tenantId, filters, config = {} }) => {
  const queryKey = [
    "FSM_VEHICLES_SEARCH",
    tenantId,
    JSON.stringify(filters),
  ];

  return queryTemplate({
    queryKey,
    queryFn: () => FSMService.vehiclesSearch(tenantId, filters),
    config,
  });
};

export default useVehiclesSearch;