import { useMutation } from "react-query";
import WMSService from "../../../services/elements/WMS";

const useWmsCAAdd = () => {
        return useMutation((data) =>{
            console.log("CA create data hook ",data)
            return WMSService.ContractorAgreement.createFake(data)
        });
};

export default useWmsCAAdd;
