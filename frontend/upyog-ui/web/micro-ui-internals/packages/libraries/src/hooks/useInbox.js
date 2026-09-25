import { queryTemplate } from "../common/queryTemplate";
import { InboxGeneral } from "../services/elements/InboxService"

const useInbox = ({tenantId, filters, config}) => queryTemplate({
        queryKey: ["INBOX_DATA",tenantId, ...Object.keys(filters)?.map( e => filters?.[e] )],
        queryFn: () => InboxGeneral.Search({inbox: {...filters}}),
        config,
    })

export default useInbox;