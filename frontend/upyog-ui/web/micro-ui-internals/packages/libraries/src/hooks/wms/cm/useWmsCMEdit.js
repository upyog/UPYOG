import { useMutation } from "react-query";
import WMSService from "../../../services/elements/WMS";

const useWmsCMEdit = () => {
        return useMutation((data) =>{
            console.log("Edit ",data)
            return WMSService.ContractorMaster.update(data)
        });
};

export default useWmsCMEdit;
