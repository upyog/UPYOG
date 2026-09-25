import { queryTemplate } from "../../common/queryTemplate";
import { WSService } from "../../services/elements/WS";

  
const useConsumptionSearch = ({tenantId, filters = {}, BusinessService="WS", t}, config = {}) => {
  const { isLoading, error, data, isSuccess } =  queryTemplate({ queryKey: ['WS_SEARCH', tenantId, filters, BusinessService], queryFn: async () => await WSService.consumptionSearch({tenantId, filters: { ...filters }, businessService:BusinessService}), config })
  return { isLoading, error, data, isSuccess };
}

export default useConsumptionSearch;