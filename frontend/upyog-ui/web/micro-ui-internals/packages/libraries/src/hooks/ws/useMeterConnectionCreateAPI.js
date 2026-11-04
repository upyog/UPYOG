import { WSService } from "../../services/elements/WS";
import { mutationTemplate } from "../../common/mutationTemplate";

const useMeterReadingCreateAPI = (businessService = "WS") => {
    return mutationTemplate({ mutationFn: (data) => WSService.meterConnectioncreate(data, businessService) });
};

export default useMeterReadingCreateAPI; 