import { useQuery } from "react-query";
import { MdmsService } from "../services/elements/MDMS";
import useCustomAPIHook from "./useCustomAPIHook";
import Urls from "../services/atoms/urls";
import _ from "lodash";
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
const useCustomMDMS = (tenantId, moduleName, masterDetails = [], config = {},mdmsv2=false,) => {
  if(mdmsv2) {
    //here call the mdmsv2 api and return the options array 
    return useCustomAPIHook({
      url: Urls.mdms_v2.search,
      params:{},
      changeQueryName:`mdms-v2-dropdowns${mdmsv2?.schemaCode}`,
      body:{
        MdmsCriteria:{
          // tenantId, //changing here to send user's tenantId always whether stateId or city
          tenantId:Digit.ULBService.getCurrentTenantId(),
          schemaCode:mdmsv2?.schemaCode,
          isActive:true,
          limit: 100,
        }
      },
      config:{
        enabled:mdmsv2 ? true : false ,
        select: (response) => {
           //mdms will be an array of master data
          const {mdms} = response
          //first filter with isActive
          //then make a data array with actual data 
          //refer the "code" key in data(for now) and set options array , also set i18nKey in each object to show in UI
          const options = mdms?.filter(row => row?.isActive)?.map(row => {
            return {
              i18nKey:Digit.Utils.locale.getTransformedLocale(`${row?.schemaCode}_${row?.data?.code}`),
              ...row.data,
            }
          })
          return options;
        }
      }

    });
  }
  return useQuery([tenantId, moduleName, masterDetails], () => MdmsService.getMultipleTypesWithFilter(tenantId, moduleName, masterDetails), config);
};

export default useCustomMDMS;
