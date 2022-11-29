import React from "react";
import { useQuery } from "react-query";
import { CRService } from "../../services/elements/CR"

const useSearch = ({tenantId, filters, config={}}) => useQuery(
    ["CR_SEARCH", tenantId, ...Object.keys(filters)?.map( e => filters?.[e] )],
    () => CRService.CRsearch({tenantId, filters}),
    {
        // select: (data) => data.Licenses,
        ...config
    }
 )


export default useSearch
