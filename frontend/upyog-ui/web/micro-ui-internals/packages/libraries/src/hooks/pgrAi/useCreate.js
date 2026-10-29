import { mutationTemplate } from "../../common/mutationTemplate";
import { PGRAIService } from "../../services/elements/PGRAI";

export const useCreate = (tenantId, type = true) => {
  return mutationTemplate({
    mutationFn: (data) =>
      type
        ? PGRAIService.create(data, tenantId)
        : PGRAIService.update(data, tenantId),
  });
};

export default useCreate;