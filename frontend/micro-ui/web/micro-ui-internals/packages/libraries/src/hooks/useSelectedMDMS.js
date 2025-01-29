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
}

getMDMSServiceRef();

const useSelectedMDMS = () => {
    return mdmsRef;
};
useSelectedMDMS();

export default useSelectedMDMS;