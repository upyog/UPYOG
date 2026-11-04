import { queryTemplate } from "../../common/queryTemplate";
import { ESTService } from "../../services/elements/EST";

const useESTApplicationSearch = ({ filters, config = {} }) => {
  return queryTemplate({
    queryKey: ["EST_APPLICATION_SEARCH", filters],
    queryFn: () =>
      ESTService.allotmentSearch({
        tenantId: filters?.tenantId,
        filters,
      }),
    enabled: !!filters,
    config,
  });
};

export default useESTApplicationSearch;