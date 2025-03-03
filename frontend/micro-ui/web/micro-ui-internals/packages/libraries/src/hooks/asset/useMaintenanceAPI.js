import { useQuery, useMutation } from "react-query";
import { ASSETService } from"../../services/elements/ASSET"



export const useMaintenanceAPI = (tenantId, type = true) => {
  if (type) {
    return useMutation((data)  => { 
      console.log('true -->' , data)
      return ASSETService.maintenance(data, tenantId)
    });
  } 
  else {
    return useMutation((data)  => {
      console.log('false -->' , data)
      return ASSETService.edit_maintenance(data, tenantId)
    });
  }
};

export default useMaintenanceAPI;
