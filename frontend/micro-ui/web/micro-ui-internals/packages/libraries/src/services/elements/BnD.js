// Birth and Death services for search requests

import { Request } from "../atoms/Utils/Request"
import Urls from "../atoms/urls";

export const BnDService = {
    // Birth search request
    Bsearch: ({ tenantId, filters }) =>
        Request({
            url: Urls.bnd.bsearch,
            useCache: false,
            method: "POST",
            auth: true,
            userService: false,
            params: { tenantId, ...filters },
        }),

    // Death search Request
    Dsearch: ({ tenantId, filters }) =>
        Request({
            url: Urls.bnd.dsearch,
            useCache: false,
            method: "POST",
            auth: true,
            userService: false,
            params: { tenantId, ...filters },
        }),
}