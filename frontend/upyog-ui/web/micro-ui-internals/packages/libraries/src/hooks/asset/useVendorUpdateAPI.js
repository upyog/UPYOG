import { useQuery, useMutation } from "react-query";

import { ASSETService } from "../../services/elements/ASSET";

export const useVendorUpdateAPI = (tenantId, type = true) => {
  return useMutation((data) => ASSETService.updateAssetVendor(data, tenantId));
};

export default useVendorUpdateAPI;
