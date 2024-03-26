// import { useQuery, useMutation } from "react-query";

// import WMSService from "../../../services/elements/WMS";

// export const useWmsCMGet = (tenantId, config = {}) => {
//   return useMutation((data) => WMSService.ContractorMaster.get(tenantId));
// };

// export default useWmsCMGet;


import { useQuery, useQueryClient } from "react-query";
import WMSService from "../../../services/elements/WMS";
export const useWmsCMCount = (tenantId) => {
  return useQuery(["WMS_CM_COUNT",tenantId], () => WMSService.ContractorMaster.getSingleData(tenantId));
};
export default useWmsCMCount;
