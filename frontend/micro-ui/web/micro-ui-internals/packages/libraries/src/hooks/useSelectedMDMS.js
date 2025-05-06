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
  'Advertisement',
  'CHB',
  'PetService',
  'Ewaste',
  'common-masters'
];

const mdmsV2Enabled = true;
let mdmsRef = null;

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