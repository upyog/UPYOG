import { queryTemplate } from "../../common/queryTemplate";
import { CNDService } from "../../services/elements/CND";

const useVehiclesSearch = (args) => {
  const { tenantId, filters, config } = args;
  return queryTemplate({
    queryKey: ["VEICLES_SEARCH", JSON.stringify(filters)],
    queryFn: () => CNDService.vehiclesSearch(tenantId, filters),
    config
  });
};

export default useVehiclesSearch;
