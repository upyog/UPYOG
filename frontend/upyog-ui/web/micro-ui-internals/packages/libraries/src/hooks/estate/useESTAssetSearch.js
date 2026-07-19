import { queryTemplate } from "../../common/queryTemplate";

const useESTAssetSearch = ({ tenantId, filters = {}, config = {} } = {}) => {
  return queryTemplate({
    queryKey: ["EST_ASSET_SEARCH", tenantId, filters],
    queryFn: () => Digit.ESTService.assetSearch({ tenantId, filters }),
    config: {
      ...config,
      // Always refetch when criteria change (e.g. Building vs Land).
      structuralSharing: false,
    },
  });
};

export default useESTAssetSearch;