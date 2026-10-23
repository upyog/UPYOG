import { queryTemplate } from "../common/queryTemplate";
import { useQueryClient } from "../common/queryClientTemplate";
import { AggregationService } from "../services/elements/Aggregation";

const useCitizenAggregate = ({ tenantId, page = "citizen-home", locale, requests, requestId = "550e8400-e29b-41d4-a716-446655440001", config = {} } = {}) => {
    const client = useQueryClient();
    const user = Digit.UserService.getUser();
    const currentTenantId = tenantId || user?.info?.tenantId || Digit.ULBService.getCitizenCurrentTenant(true) || "pg";
    const isCitizen = !!user?.access_token;
    const isEnabled = isCitizen && (config?.enabled !== undefined ? config.enabled : true);

    const queryKey = ["CITIZEN_AGGREGATE", currentTenantId, page, locale, requests, requestId, user?.info?.uuid || user?.info?.id];

    const queryResult = queryTemplate({
        queryKey,
        queryFn: () =>
            AggregationService.citizenAggregate({
                tenantId: currentTenantId,
                page,
                locale,
                requests,
                requestId,
            }),
        enabled: isEnabled,
        config: {
            staleTime: 60000,
            ...config,
        },
    });

    return {
        ...queryResult,
        revalidate: () => client.invalidateQueries(queryKey),
    };
};

export default useCitizenAggregate;
