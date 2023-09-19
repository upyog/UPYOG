import { useMutation } from "react-query";
import WMSService from "../../../services/elements/WMS";

const useWmsCMSearch = (filters, config) => {
        return useMutation((data) =>{
            return WMSService.ContractorMaster.getDataFilter(data)
        });
};

export default useWmsCMSearch;
