import Urls from "../atoms/urls";
import { Request } from "../atoms/Utils/Request";

export const AggregationService = {
    citizenAggregate: ({ tenantId, page = "citizen-home", locale, requests, requestId, auth = true } = {}) => {
        const user = Digit.UserService.getUser();
        const currentLocale = locale || Digit.StoreData.getCurrentLanguage() || user?.info?.locale || "en_IN";
        const userTenantId = tenantId || user?.info?.tenantId || Digit.ULBService.getCitizenCurrentTenant(true) || "pg";
        const generatedRequestId = requestId || "550e8400-e29b-41d4-a716-446655440001";
        const defaultRequests = [
            { provider: "quick-summary" },
            { provider: "recent-applications", pagination: { page: 1, size: 100 } },
            { provider: "notifications" },
            { provider: "draft-applications" },
            { provider: "due-renewals" },
            { provider: "upcoming-events" },
        ];

        return Request({
            url: Urls.citizenAggregate,
            useCache: false,
            method: "POST",
            auth: auth !== false,
            userService: auth !== false,
            data: {
                requestId: generatedRequestId,
                page: page,
                tenantId: userTenantId,
                locale: currentLocale,
                requests: requests || defaultRequests,
            },
        });
    },
};
