// import { useQuery, useMutation } from "react-query";

// import WMSService from "../../../services/elements/WMS";

// export const useWmsCMGet = (tenantId, config = {}) => {
//   return useMutation((data) => WMSService.ContractorMaster.get(tenantId));
// };

// export default useWmsCMGet;


import { useQuery, useQueryClient } from "react-query";
import WMSService from "../../../services/elements/WMS";
export const useWmsCMGet = (tenantId, config = {}) => {
  return useQuery(["WMS_CM_GET", tenantId], () => WMSService.ContractorMaster.getList(), config);
};
export default useWmsCMGet;
