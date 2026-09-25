import React from "react";
import { useQuery } from "@tanstack/react-query";
import { InboxGeneral } from "../services/elements/InboxService"

const useInbox = ({tenantId, filters, config}) => useQuery({
        queryKey: ["INBOX_DATA",tenantId, ...Object.keys(filters)?.map( e => filters?.[e] )],
        queryFn: () => InboxGeneral.Search({inbox: {...filters}}),
        ...config
    })

export default useInbox;