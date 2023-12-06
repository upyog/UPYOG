// import { useQuery, useMutation } from "react-query";

// import WMSService from "../../../services/elements/WMS";

// export const useWmsCMGet = (tenantId, config = {}) => {
//   return useMutation((data) => WMSService.ContractorMaster.get(tenantId));
// };

// export default useWmsCMGet;


import { useQuery, useQueryClient } from "react-query";
import WMSService from "../../../services/elements/WMS";
export const useWmsCAGet = (tenantId, config = {}) => {
  return useQuery(["WMS_CA_GET", tenantId], () => WMSService.ContractorAgreement.getList(), config);
};
export default useWmsCAGet;
