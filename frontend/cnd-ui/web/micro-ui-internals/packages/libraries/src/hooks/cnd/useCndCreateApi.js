import { mutationTemplate } from "../../common/mutationTemplate";
import { CNDService } from "../../services/elements/CND";

export const useCndCreateApi = (tenantId, type = true) => {
  if (type) {
    return mutationTemplate({
      mutationFn: (data) => CNDService.create(data, tenantId)
    });
  } else {
    return mutationTemplate({
      mutationFn: (data) => CNDService.update(data, tenantId)
    });
  }
};

export default useCndCreateApi;
