import { WSService } from "../../services/elements/WS";
import { mutationTemplate } from "../../common/mutationTemplate";

const useSewerageCreateAPI = (businessService = "WATER") => {
    return mutationTemplate({ mutationFn: (data) => WSService.create(data, businessService) });
};

export default useSewerageCreateAPI;