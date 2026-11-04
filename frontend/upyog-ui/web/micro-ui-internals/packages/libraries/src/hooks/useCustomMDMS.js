import { queryTemplate } from "../common/queryTemplate";
import { MdmsService } from "../services/elements/MDMS";

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
const useCustomMDMS = (tenantId, moduleName, masterDetails = [], config = {}) => {
  return queryTemplate({ queryKey: [tenantId, moduleName, masterDetails], queryFn: () => MdmsService.getMultipleTypesWithFilter(tenantId, moduleName, masterDetails), config });
};

export default useCustomMDMS;
