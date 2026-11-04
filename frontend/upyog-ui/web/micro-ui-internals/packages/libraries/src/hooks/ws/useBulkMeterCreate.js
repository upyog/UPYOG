import { WSService } from "../../services/elements/WS";
import { mutationTemplate } from "../../common/mutationTemplate";

const useBulkMeterReadingCreateAPI = (businessService = "WS") => {
    return mutationTemplate({ mutationFn: (data) => WSService.bulkMeterConnectioncreate(data, businessService) });
};

export default useBulkMeterReadingCreateAPI; 