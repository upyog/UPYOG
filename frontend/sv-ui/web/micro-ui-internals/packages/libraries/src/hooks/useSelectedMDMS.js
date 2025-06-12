/**
 * Determines which MDMS service reference should be used.
 * 
 * This function checks if the requested module is in the mdmsV2Modules list
 * to decide whether to use the new version of the MDMS service (`MdmsServiceV2`)
 * or the old one (`MdmsService`).
 * 
 */

import { MdmsService } from "../services/elements/MDMS";
import { MdmsServiceV2 } from "../services/elements/MDMSV2";

/**
 * List of modules that should use MdmsServiceV2,
 * it should match with the modulename you enter in MDMS (masterData).
 */
const mdmsV2Modules = [
  'StreetVending',
];

const mdmsV2Enabled = true;
let mdmsRef = null;

/**
 * Wrapper hook/function to get the correct MDMS service.
 * if mdmsV2Enabled is true and moduleName matches to any of mention inside mdmsV2Modules array
 * then it return MdmsServiceV2 and if any of thsi is false then it return  MdmsService
 */
const getMDMSServiceRef = (moduleName) => {
  // Check if the module is in the list of modules that should use V2
  if (mdmsV2Enabled && mdmsV2Modules.includes(moduleName)) {
    return MdmsServiceV2;
  } else {
    return MdmsService;
  }
}

const useSelectedMDMS = (moduleName) => {
  // Get the appropriate service reference based on the module name
  mdmsRef = getMDMSServiceRef(moduleName);
  return mdmsRef;
};

export default useSelectedMDMS;