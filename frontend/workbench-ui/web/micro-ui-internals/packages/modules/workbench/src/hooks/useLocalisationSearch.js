import { useQueryClient} from "../../../../libraries/src/common/queryClientTemplate";
import { queryTemplate } from "../../../../libraries/src/common/queryTemplate";
import { LocalisationSearch } from "../utils/LocalisationSearch";

const useLocalisationSearch = ({url, params, body, config = {}, plainAccessRequest,changeQueryName="Random",state }) => {
  const client = useQueryClient();
  const CustomService = Digit.CustomService
  const hasAnySearchValue = state?.searchForm && Object.values(state.searchForm).some((v) => {
    if (v === null || v === undefined || v === "") return false;
    if (typeof v === "object" && !Array.isArray(v) && Object.keys(v).length === 0) return false;
    if (Array.isArray(v) && v.length === 0) return false;
    return true;
  });
  const finalEnabled = hasAnySearchValue ;
  const { isLoading, data, isFetching, refetch, error } = queryTemplate({
    queryKey: [url, changeQueryName, state?.searchForm].filter((e) => e),
    queryFn: () => {
      return LocalisationSearch.fetchResults({ url, params, body, plainAccessRequest, state });
    },
    config: {
      cacheTime: 0,
      ...config,
      enabled: finalEnabled,
    },
  });

  return {
    isLoading,
    isFetching,
    data,
    refetch,
    revalidate: () => {
      data && client.invalidateQueries({ queryKey: [url].filter((e) => e) });
    },
    error
  };
};



export default useLocalisationSearch;