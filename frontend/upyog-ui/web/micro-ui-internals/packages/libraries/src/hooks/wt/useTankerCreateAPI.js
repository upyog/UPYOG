import { useMutation } from "react-query";
import { WTService } from "../../services/elements/WT";


export const useTankerCreateAPI = (tenantId, type = true) => {
 
  if (type) {
    return useMutation((data) => WTService.create(data, tenantId));
  } 
  else {
    return useMutation((data) => WTService.update(data, tenantId));
  }
};

export default useTankerCreateAPI;
