import { useQuery, useMutation } from "react-query";
import { CNDService } from "../../services/elements/CND";


export const useCndCreateApi = (tenantId, type = true) => {
  if (type) {
    return useMutation((data) => CNDService.create(data, tenantId));
  } 
  else {
    return useMutation((data) => CNDService.update(data, tenantId));
  }
};

export default useCndCreateApi;
