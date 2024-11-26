import { useQuery } from "react-query";
import { MdmsService } from "../services/elements/MDMS";
import { MdmsServiceV2 } from "../services/elements/MDMSV2";
/**
 * Custom hook which can be used to
 * make a single hook a module to get multiple masterdetails with/without filter
 * 
 * @author jagankumar-egov
 * 
 * @example
 * // returns useQuery object
 * Digit.Hooks.useCustomMDMS(
 *          "stateid",
 *          "modulename",
 *          [
 *              { name:"masterdetail1",filter:"[?(@.active == true)]"},
 *              { name:"masterdetail2" }
 *          ],
 *          { // all configs supported by the usequery 
 *              default:(data)=>{
 *                          format
 *                          return formattedData;
 *                          }
 *          })
 * 
 * @returns {Object} Returns the object of the useQuery from react-query.
 */
const useCustomMDMS = (tenantId, moduleName, masterDetails = [], config = {}, version = 1) => {
  console.log('Here version valueddd  :- ', version);
  if(version === 2){
    console.log(tenantId, moduleName, masterDetails,  config);
    return useQuery([tenantId, moduleName, masterDetails], () => MdmsServiceV2.getMultipleTypesWithFilter(tenantId, moduleName, masterDetails), config);
  }
  return useQuery([tenantId, moduleName, masterDetails], () => MdmsService.getMultipleTypesWithFilter(tenantId, moduleName, masterDetails), config);
};

export default useCustomMDMS;
