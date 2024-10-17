import { useMutation } from "react-query";
import { FNOCService } from "../../services/elements/FNOC";

export const useCreateAPI = (tenantId, type = true) => {
  if (type) {
    return useMutation((data) => FNOCService.create(data, tenantId));
  } 
  else {
    return useMutation((data) => FNOCService.update(data, tenantId));
  }
};
export default useCreateAPI;
