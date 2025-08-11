
import { useQuery, useQueryClient } from "react-query";

const useServiceSearchCHB = ({ tenantId, filters }, config = {}) => {
  const client = useQueryClient();

let serviceSearchArg = {filters : {ServiceCriteria : {tenantId:filters?.serviceSearchArgs?.tenantId, /*serviceDefIds: [data?.ServiceDefinition?.[0]?.id]["ca134821-97f0-42b7-a53d-f6cd2796e4b9"],attributes:filters?.serviceSearchArgs?.attributes*/ referenceIds:filters?.serviceSearchArgs?.referenceIds}}}
let serviceconfig = {/*enabled : data?.ServiceDefinition?.[0]?.id ? true : false,*/...config, cacheTime: 0}

const { isLoading : serviceLoading, error : serviceerror, data :servicedata} = useQuery(["ServiceSearch", tenantId, filters], () => Digit.PTService.cfsearch(serviceSearchArg), {
    ...serviceconfig,
    });


return {isLoading: serviceLoading, error : serviceerror, data : servicedata, revalidate: () => client.invalidateQueries(["ServiceSearch", tenantId, filters]) };

};

export default useServiceSearchCHB;