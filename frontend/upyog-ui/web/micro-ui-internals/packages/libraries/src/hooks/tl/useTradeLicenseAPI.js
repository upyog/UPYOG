import { TLService } from "../../services/elements/TL";
import { mutationTemplate } from "../../common/mutationTemplate";

const useTradeLicenseAPI = (tenantId, type = true) => {
  if (type) {
    return mutationTemplate({ mutationFn: (data) => TLService.create(data, tenantId) });
  } else {
    return mutationTemplate({ mutationFn: (data) => TLService.update(data, tenantId) });
  }
};

export default useTradeLicenseAPI;
