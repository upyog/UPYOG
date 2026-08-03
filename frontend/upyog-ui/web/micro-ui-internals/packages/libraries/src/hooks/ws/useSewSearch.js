import { queryTemplate } from "../../common/queryTemplate";
import { WSService } from "../../services/elements/WS"

//while registering it's name is WSuseSearch
const useSewSearch = ({tenantId, filters, config={}}) => queryTemplate({
    queryKey: ["WS_SEW_SEARCH", tenantId, ...Object.keys(filters)?.map( e => filters?.[e] )],
    queryFn: () => WSService.WSSewsearch({tenantId, filters}),
    config,
 })


export default useSewSearch
