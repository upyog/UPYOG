import { useQuery, useMutation } from "react-query";

import { ASSETService } from "../../services/elements/ASSET";

export const useInventoryCreationAPI = (tenantId, type = true) => {
  if (type) {
    return useMutation((data) => ASSETService.inventoryCreation(data, tenantId));
  } else {
    return useMutation((data) => ASSETService.updateInventory(data, tenantId));
  }
};

export default useInventoryCreationAPI;
