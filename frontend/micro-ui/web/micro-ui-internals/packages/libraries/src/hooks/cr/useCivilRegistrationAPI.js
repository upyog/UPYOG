import { CRService } from "../../services/elements/CR";
import { useMutation } from "react-query";

const useCivilRegistrationAPI = (tenantId, type = true) => {
  if(type){
  return useMutation((data) => CRService.create(data, tenantId));
} else {
  return useMutation((data) => CRService.update(data, tenantId));
}
};

export default useCivilRegistrationAPI;
