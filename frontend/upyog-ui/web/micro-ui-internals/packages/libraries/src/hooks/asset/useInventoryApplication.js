import { useQuery, useMutation } from "react-query";

import { ASSETService } from "../../services/elements/ASSET";

export const useInventoryApplication = (tenantId, type = true) => {
  if (type) {
    return useMutation((data) => ASSETService.inventoryVendor(data, tenantId));
  } else {
    return useMutation((data) => ASSETService.updateAssetVendor(data, tenantId));
  }
};

export default useInventoryApplication;
