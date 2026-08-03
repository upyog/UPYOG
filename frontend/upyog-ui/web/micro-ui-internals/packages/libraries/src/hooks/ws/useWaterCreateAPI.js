import { WSService } from "../../services/elements/WS";
import { mutationTemplate } from "../../common/mutationTemplate";

const useWaterCreateAPI = (businessService = "WATER") => {
    return mutationTemplate({ mutationFn: (data) => WSService.create(data, businessService) });
};

export default useWaterCreateAPI;