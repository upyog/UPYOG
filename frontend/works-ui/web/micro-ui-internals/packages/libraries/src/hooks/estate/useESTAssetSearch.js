import { queryTemplate } from "../../common/queryTemplate";

/**
 * useESTAssetSearch
 * -----------------
 * React Query wrapper for the estate asset search API:
 *   POST /estate-management/estate/asset/v1/_search
 *
 * Used by the asset listing / search screens:
 * - Employee: pages/employee/SearchApp.js (search applications)
 * - Citizen:  pages/citizen/MyApplications/index.js (my applications)
 *
 * Params:
 * @param {string} tenantId - ULB tenant the search is scoped to.
 * @param {object} filters  - Search body, e.g. { AssetSearchCriteria: { tenantId, estateNo, assetStatus, ... } }.
 * @param {object} config   - Extra React Query options (e.g. { enabled } to gate the call until a search runs).
 *
 * Returns the standard React Query result ({ data, isLoading, isSuccess, error, ... }).
 *
 * Notes:
 * - `queryKey` includes `filters`, so changing criteria (Building vs Land,
 *   status, asset number) triggers an automatic refetch.
 * - `structuralSharing: false` forces a fresh result object each time so the
 *   consuming table/cards re-render even when the shape looks similar.
 * - Callers typically pass `config.enabled` to avoid firing on first mount
 *   before the user has entered any search criteria.
 */
const useESTAssetSearch = ({ tenantId, filters = {}, config = {} } = {}) => {
  return queryTemplate({
    // filters in the key → criteria change = new cache entry + refetch.
    queryKey: ["EST_ASSET_SEARCH", tenantId, filters],
    queryFn: () => Digit.ESTService.assetSearch({ tenantId, filters }),
    config: {
      ...config,
      // Always return a new object so results re-render when criteria change
      // (e.g. Building vs Land), even if the payload looks structurally similar.
      structuralSharing: false,
    },
  });
};

export default useESTAssetSearch;
