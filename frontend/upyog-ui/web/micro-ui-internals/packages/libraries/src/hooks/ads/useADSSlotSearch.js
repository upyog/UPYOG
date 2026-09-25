import { mutationTemplate } from "../../common/mutationTemplate";
import { ADSServices } from "../../services/elements/ADS";

const useADSSlotSearch = (tenantId) => {
  const mutationFn = (data) => ADSServices.slot_search(data, tenantId);
  return mutationTemplate({ mutationFn });
};

export default useADSSlotSearch;