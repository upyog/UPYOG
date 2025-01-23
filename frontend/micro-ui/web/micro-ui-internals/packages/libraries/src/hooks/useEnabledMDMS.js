import { useQuery } from "react-query";
import { MdmsService } from "../services/elements/MDMS";
import { MdmsServiceV2 } from "../services/elements/MDMSV2";

const mdmsV2Enabled = true;
let mdmsRef = null;

const getMDMSServiceRef = () => {
        if (mdmsV2Enabled) {
            mdmsRef = MdmsServiceV2;
        } else {
            mdmsRef = MdmsService;
        }
        return mdmsRef;
}

const useEnabledMDMS = (tenantId, moduleName, masterDetails = [], config = {}) => {
    return useQuery([tenantId, moduleName, masterDetails], () => getMDMSServiceRef().getMultipleTypesWithFilter(tenantId, moduleName, masterDetails), config);
};


export default useEnabledMDMS;