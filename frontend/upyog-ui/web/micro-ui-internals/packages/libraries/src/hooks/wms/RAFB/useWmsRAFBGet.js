import { useMutation, useQuery, useQueryClient } from "react-query";
import WMSService from "../../../services/elements/WMS";
const useWmsRAFBGet = (tenantId,type, config = {}) => {
 const getPreviousBill=()=>{    console.log("bankList sttepr single data lists")
  return useQuery(["WMS_RAFB_GET_PREVIOUS_BILL", tenantId], () => WMSService.RunningAccountFinalBill.getPreviousBill(), config);
};
// const CAList = ()=>{
//     return useQuery(["WMS_CA_GET_LIST", tenantId], () => WMSService.ContractorAgreement.getSingleFake(tenantId), config);// fake data
// };
// const CAUpdate = ()=>{
//   return useMutation((data) => WMSService.ContractorAgreement.updateFake(data,tenantId), config);

// }

switch(type){
  case "getPriviousBill":
    return getPreviousBill();
  // case "CASingleList":
  //   return CAList();
  // case "CAUpdate":
  //   return CAUpdate();
}
};
export default useWmsRAFBGet;
