/**
 * Determines which MDMS service reference should be used.
 * 
 * This function checks the flag `mdmsV2Enabled` to decide whether to use the 
 * new version of the MDMS service (`MdmsServiceV2`) or the old one (`MdmsService`). 
 * It then assigns the appropriate service reference to `mdmsRef`.
 * 
 */

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