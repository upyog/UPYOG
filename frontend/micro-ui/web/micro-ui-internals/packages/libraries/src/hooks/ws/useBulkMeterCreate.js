import { WSService } from "../../services/elements/WS";
import { useMutation } from "react-query";

const useBulkMeterReadingCreateAPI = (businessService = "WS") => {
    return useMutation((data) => WSService.bulkMeterConnectioncreate(data, businessService));
};

export default useBulkMeterReadingCreateAPI; 