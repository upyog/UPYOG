import { queryTemplate } from "../common/queryTemplate";

import { LocationService } from "../services/elements/Location";

const useLocation = (tenantId, locationType, config = {}) => {
    switch(locationType) {
        case 'Locality':
            return queryTemplate({
                queryKey: ["LOCALITY_DETAILS", tenantId],
                queryFn: () => LocationService.getLocalities(tenantId),
                config
            });
        case 'Ward':
            return queryTemplate({
                queryKey: ["WARD_DETAILS", tenantId],
                queryFn: () => LocationService.getWards(tenantId),
                config
            });
        default:
            break
    } 
}

export default useLocation;
