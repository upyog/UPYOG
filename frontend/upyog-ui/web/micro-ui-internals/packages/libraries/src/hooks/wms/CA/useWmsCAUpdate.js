import { useMutation } from "react-query";
import WMSService from "../../../services/elements/WMS";

const useWmsCAUpdate = (id) => {
  // return useMutation((data) => WMSService.ContractorAgreement.updateFake(data,tenantId), config);

        return useMutation((data) =>{
            console.log("bankList sttepr single data update UseWMSCAUpdate ",data)
            console.log("bankList sttepr single data update UseWMSCAUpdate id ",id)
            return WMSService.ContractorAgreement.updateFake(data,id)
            
        });
};

export default useWmsCAUpdate;

