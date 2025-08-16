import { useMutation } from "react-query";
import { MTService } from "../../services/elements/MT";
export const useMobileToiletCreateAPI = (tenantId, type = true) => {
 
  if (type) {
    return useMutation((data) => MTService.create(data, tenantId));
  } 
  else {
    return useMutation((data) => MTService.update(data, tenantId));
  }
};

export default useMobileToiletCreateAPI;
