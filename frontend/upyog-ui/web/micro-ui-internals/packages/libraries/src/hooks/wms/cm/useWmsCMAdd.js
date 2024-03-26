import { useMutation } from "react-query";
import WMSService from "../../../services/elements/WMS";

const useWmsCMAdd = () => {
        return useMutation((data) =>{
            return WMSService.ContractorMaster.create(data)
        });
};

export default useWmsCMAdd;
