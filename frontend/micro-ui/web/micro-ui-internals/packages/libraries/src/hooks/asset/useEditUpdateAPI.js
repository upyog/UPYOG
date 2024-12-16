import { useQuery, useMutation } from "react-query";

import { ASSETService } from "../../services/elements/ASSET"



export const useEditUpdateAPI = (tenantId, type = true) => {
  if (type) {
    return useMutation((data) => ASSETService.update(data, tenantId));
  } 
  else {
    return useMutation((data) => ASSETService.create(data, tenantId));
  }
};

export default useEditUpdateAPI;
