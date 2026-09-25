import { WSService } from "../../services/elements/WS";
import { mutationTemplate } from "../../common/mutationTemplate";

const useWSUpdateAPI = (businessService = "WATER") => {
    return mutationTemplate({ mutationFn: (data) =>  WSService.update(data, businessService) });
};

export default useWSUpdateAPI;