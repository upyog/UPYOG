import { queryTemplate } from "../../common/queryTemplate";
import { WSService } from "../../services/elements/WS"

//while registering it's name is WSuseSearch
const useSearch = ({tenantId, filters, config={}}) => queryTemplate({
    queryKey: ["WS_WATER_SEARCH", tenantId, ...Object.keys(filters)?.map( e => filters?.[e] )],
    queryFn: () => WSService.WSWatersearch({tenantId, filters}),
    config,
 })


export default useSearch
