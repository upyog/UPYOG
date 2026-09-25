import { mutationTemplate } from "../../common/mutationTemplate";
import ReceiptsService from "../../services/elements/Receipts";

export const useReceiptsUpdate = (tenantId, businessService) => {
  return mutationTemplate({ mutationFn: (data) => ReceiptsService.update(data, tenantId, businessService) });
};

export default useReceiptsUpdate;
