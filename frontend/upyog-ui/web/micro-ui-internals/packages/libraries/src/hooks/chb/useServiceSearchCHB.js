
import { useQuery, useQueryClient } from "react-query";

/**
 * useServiceSearchCHB Hook
 * 
 * This custom hook is responsible for searching services in the CHB (Community Hall Booking) module based on tenant and filter criteria.
 * 
 * Parameters:
 * - `tenantId`: The tenant ID for which the service search is being performed.
 * - `filters`: Filters to apply for the service search (e.g., reference IDs, attributes).
 * - `config`: Optional configuration object for the `useQuery` hook.
 * 
 * Logic:
 * - Constructs the search arguments (`serviceSearchArg`) using `tenantId` and `filters`.
 * - Uses the `useQuery` hook from `react-query` to fetch service data from `Digit.PTService.cfsearch`.
 * - Configures the query with caching disabled (`cacheTime: 0`) for real-time results.
 * - Provides a `revalidate` function to invalidate and refetch the query.
 * 
 * Returns:
 * - An object containing:
 *    - `isLoading`: Boolean indicating whether the query is in progress.
 *    - `error`: Error object if the query fails.
 *    - `data`: The fetched service data.
 *    - `revalidate`: Function to invalidate and refetch the query.
 */
const useServiceSearchCHB = ({ tenantId, filters }, config = {}) => {
  const client = useQueryClient();
  //removing servicedefids from search call as it's not required anymore
  // const searchargs = { filters : { ServiceDefinitionCriteria : {tenantId : filters?.serviceSearchArgs?.tenantId, module:filters?.serviceSearchArgs?.module, code:filters?.serviceSearchArgs?.code  }}};


  // const { isLoading, error, data } = useQuery(["ServiceDefinitionSearch", tenantId, filters], () => Digit.PTService.cfdefinitionsearch(searchargs), {
  //   ...config,
  //   });

let serviceSearchArg = {filters : {ServiceCriteria : {tenantId:filters?.serviceSearchArgs?.tenantId, /*serviceDefIds: [data?.ServiceDefinition?.[0]?.id]["ca134821-97f0-42b7-a53d-f6cd2796e4b9"],attributes:filters?.serviceSearchArgs?.attributes*/ referenceIds:filters?.serviceSearchArgs?.referenceIds}}}
let serviceconfig = {/*enabled : data?.ServiceDefinition?.[0]?.id ? true : false,*/...config, cacheTime: 0}

const { isLoading : serviceLoading, error : serviceerror, data :servicedata} = useQuery(["ServiceSearch", tenantId, filters], () => Digit.PTService.cfsearch(serviceSearchArg), {
    ...serviceconfig,
    });


return {isLoading: serviceLoading, error : serviceerror, data : servicedata, revalidate: () => client.invalidateQueries(["ServiceSearch", tenantId, filters]) };

};

export default useServiceSearchCHB;