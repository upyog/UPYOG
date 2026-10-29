import { queryTemplate } from "../../common/queryTemplate";
import { TLService } from "../../services/elements/TL";

const useSearch = ({ tenantId, filters, config = {} }) =>
  queryTemplate({
    queryKey: ["TL_SEARCH", tenantId, ...Object.keys(filters)?.map((e) => filters?.[e])],
    queryFn: () => TLService.TLsearch({ tenantId, filters }),
    config,
  });

export default useSearch;
